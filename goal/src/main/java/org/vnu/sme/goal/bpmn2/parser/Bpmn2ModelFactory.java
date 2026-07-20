package org.vnu.sme.goal.bpmn2.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.bpmn2.ast.*;
import org.vnu.sme.goal.bpmn2.mm.*;
import org.vnu.sme.goal.bpmn2.mm.Process; // disambiguate from java.lang.Process

/**
 * Converts the BPMN 2.0 AST (CS layer) to the runtime MetaModel (MM layer).
 *
 * <p>Two containment/reference subtleties handled here (see
 * doc/05-bpmn2-metamodel.drawio):
 * <ul>
 *   <li>A FlowElement written inside a {@code lane} block is, in the MM,
 *       OWNED by the enclosing Process (added to {@code Process.flowElements});
 *       the Lane only keeps a reference list for partitioning.</li>
 *   <li>SequenceFlow endpoints only resolve within the same Process/SubProcess
 *       scope (they never cross a Process boundary); MessageFlow endpoints and
 *       its optional Message resolve against the whole Model.</li>
 * </ul>
 */
public final class Bpmn2ModelFactory {

    private Bpmn2ModelFactory() {}

    public static Bpmn2Model build(Bpmn2ModelCS cs) {
        Bpmn2Model model = new Bpmn2Model(cs.name());

        for (ProcessCS pCS : cs.processes()) {
            model.addProcess(buildProcess(pCS));
        }
        for (MessageCS mCS : cs.messages()) {
            model.addMessage(new Message(mCS.id(), mCS.name()));
        }
        for (MessageFlowCS mfCS : cs.messageFlows()) {
            FlowElement source = model.findFlowElement(mfCS.source())
                    .orElseThrow(() -> new IllegalStateException("Unknown message-flow source: " + mfCS.source()));
            FlowElement target = model.findFlowElement(mfCS.target())
                    .orElseThrow(() -> new IllegalStateException("Unknown message-flow target: " + mfCS.target()));
            Message message = mfCS.message() == null ? null
                    : model.findMessage(mfCS.message())
                            .orElseThrow(() -> new IllegalStateException("Unknown message: " + mfCS.message()));
            model.addMessageFlow(new MessageFlow(source, target, message));
        }
        return model;
    }

    private static Process buildProcess(ProcessCS cs) {
        List<FlowElement> topLevel = cs.flowElements().stream()
                .map(Bpmn2ModelFactory::buildFlowElement)
                .collect(Collectors.toList());

        List<Lane> lanes = new ArrayList<>();
        List<FlowElement> owned = new ArrayList<>(topLevel);
        for (LaneCS lCS : cs.lanes()) {
            List<FlowElement> laneElements = lCS.flowElements().stream()
                    .map(Bpmn2ModelFactory::buildFlowElement)
                    .collect(Collectors.toList());
            owned.addAll(laneElements);
            lanes.add(new Lane(lCS.id(), lCS.name(), laneElements));
        }

        Map<String, FlowElement> scope = new LinkedHashMap<>();
        for (FlowElement fe : owned) scope.put(fe.id(), fe);

        List<SequenceFlow> flows = cs.sequenceFlows().stream()
                .map(sf -> resolveSequenceFlow(sf, scope))
                .collect(Collectors.toList());

        return new Process(cs.id(), cs.name(), lanes, owned, flows);
    }

    private static FlowElement buildFlowElement(FlowElementCS cs) {
        return switch (cs) {
            case FlowElementCS.StartEventCS e -> new StartEvent(e.id(), EventTrigger.from(e.trigger()), e.oclSource());
            case FlowElementCS.EndEventCS e -> new EndEvent(e.id(), EventTrigger.from(e.trigger()), e.oclSource());
            case FlowElementCS.IntermediateEventCS e -> new IntermediateEvent(
                    e.id(), EventTrigger.from(e.trigger()), EventDirection.from(e.direction()), e.oclSource());
            case FlowElementCS.TaskCS e -> new Task(e.id(), e.name(), e.oclSource());
            case FlowElementCS.CallActivityCS e -> new CallActivity(e.id(), e.oclSource());
            case FlowElementCS.GatewayCS e -> new Gateway(e.id(), GatewayKind.from(e.kind()), e.oclSource());
            case FlowElementCS.SubProcessCS e -> buildSubProcess(e);
        };
    }

    private static SubProcess buildSubProcess(FlowElementCS.SubProcessCS cs) {
        List<FlowElement> children = cs.flowElements().stream()
                .map(Bpmn2ModelFactory::buildFlowElement)
                .collect(Collectors.toList());

        Map<String, FlowElement> scope = new LinkedHashMap<>();
        for (FlowElement fe : children) scope.put(fe.id(), fe);

        List<SequenceFlow> flows = cs.sequenceFlows().stream()
                .map(sf -> resolveSequenceFlow(sf, scope))
                .collect(Collectors.toList());

        return new SubProcess(cs.id(), cs.name(), children, flows, cs.oclSource());
    }

    private static SequenceFlow resolveSequenceFlow(SequenceFlowCS cs, Map<String, FlowElement> scope) {
        FlowElement source = scope.get(cs.source());
        FlowElement target = scope.get(cs.target());
        if (source == null) throw new IllegalStateException("Unknown flow source: " + cs.source());
        if (target == null) throw new IllegalStateException("Unknown flow target: " + cs.target());
        return new SequenceFlow(source, target, cs.label(), cs.oclSource());
    }
}
