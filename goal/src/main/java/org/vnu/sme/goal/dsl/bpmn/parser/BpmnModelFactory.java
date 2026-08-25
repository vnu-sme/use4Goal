package org.vnu.sme.goal.dsl.bpmn.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.dsl.bpmn.ast.*;
import org.vnu.sme.goal.dsl.bpmn.mm.*;
import org.vnu.sme.goal.dsl.bpmn.mm.Process; // disambiguate from java.lang.Process

/**
 * Converts the BPMN 2.0 AST (CS layer) to the runtime MetaModel (MM layer).
 *
 * <p>Flow elements are top-level in the AST (each names its own lane and
 * its own outgoing flow), but the MM keeps the original containment shape
 * doc/05-bpmn2-metamodel.drawio describes: a Process owns every FlowElement,
 * a Lane only references the subset assigned to it. Grouping by lane, and
 * resolving each element's own flow reference(s) into {@link SequenceFlow}
 * edges, both happen here rather than in the grammar or the visitor.
 */
public final class BpmnModelFactory {

    private BpmnModelFactory() {}

    public static BpmnModel build(BpmnModelCS cs) {
        BpmnModel model = new BpmnModel(cs.name());

        // lane id -> owning pool id, so a top-level flow element (which only
        // names its lane) can be grouped under the right Process.
        Map<String, String> laneOwner = new LinkedHashMap<>();
        for (ProcessCS p : cs.processes()) {
            for (LaneCS l : p.lanes()) laneOwner.put(l.id(), p.id());
        }

        Map<String, List<FlowElementCS>> byPool = new LinkedHashMap<>();
        for (FlowElementCS fe : cs.flowElements()) {
            String poolId = laneOwner.get(fe.laneId());
            if (poolId == null) {
                throw new IllegalStateException("Unknown lane '" + fe.laneId() + "' for element " + fe.id());
            }
            byPool.computeIfAbsent(poolId, ignored -> new ArrayList<>()).add(fe);
        }

        for (ProcessCS pCS : cs.processes()) {
            model.addProcess(buildProcess(pCS, byPool.getOrDefault(pCS.id(), List.of())));
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

    private static Process buildProcess(ProcessCS cs, List<FlowElementCS> elementsCS) {
        List<FlowElement> owned = elementsCS.stream()
                .map(BpmnModelFactory::buildFlowElement)
                .collect(Collectors.toList());

        Map<String, FlowElement> scope = new LinkedHashMap<>();
        for (FlowElement fe : owned) scope.put(fe.id(), fe);

        Map<String, List<FlowElement>> byLane = new LinkedHashMap<>();
        for (FlowElementCS feCS : elementsCS) {
            byLane.computeIfAbsent(feCS.laneId(), ignored -> new ArrayList<>()).add(scope.get(feCS.id()));
        }
        // A bare `lane Initiator;` skeleton has no declared display name of its
        // own in the new syntax; its identifier doubles as its label.
        List<Lane> lanes = cs.lanes().stream()
                .map(lCS -> new Lane(lCS.id(), lCS.id(), byLane.getOrDefault(lCS.id(), List.of())))
                .collect(Collectors.toList());

        List<SequenceFlow> flows = new ArrayList<>();
        for (FlowElementCS feCS : elementsCS) {
            FlowElement source = scope.get(feCS.id());
            switch (feCS) {
                case FlowElementCS.StartEventCS e ->
                        flows.add(resolveFlow(source, e.flow(), null, null, false, scope));
                case FlowElementCS.EndEventCS e -> { /* an end never has an outgoing flow */ }
                case FlowElementCS.IntermediateEventCS e -> {
                    if (e.flow() != null) flows.add(resolveFlow(source, e.flow(), null, null, false, scope));
                }
                case FlowElementCS.ActivityCS e -> {
                    if (e.flow() != null) flows.add(resolveFlow(source, e.flow(), null, null, false, scope));
                }
                case FlowElementCS.GatewayCS e -> {
                    for (GatewayFlowCS gf : e.flows()) {
                        flows.add(resolveFlow(source, gf.target(), gf.postSource(),
                                gf.guardSource(), gf.isDefault(), scope));
                    }
                }
            }
        }

        return new Process(cs.id(), cs.name(), cs.groupClass(), lanes, owned, flows);
    }

    private static SequenceFlow resolveFlow(FlowElement source, String targetId, String postSource,
            String guardSource, boolean isDefault, Map<String, FlowElement> scope) {
        FlowElement target = scope.get(targetId);
        if (target == null) {
            throw new IllegalStateException("Unknown flow target '" + targetId + "' from " + source.id());
        }
        return new SequenceFlow(source, target, null, postSource, guardSource, isDefault);
    }

    private static FlowElement buildFlowElement(FlowElementCS cs) {
        return switch (cs) {
            case FlowElementCS.StartEventCS e ->
                    new StartEvent(e.id(), null, EventTrigger.from(e.trigger()), constraints(e.pre(), null));
            case FlowElementCS.EndEventCS e ->
                    new EndEvent(e.id(), null, EventTrigger.from(e.trigger()), List.of());
            case FlowElementCS.IntermediateEventCS e -> new IntermediateEvent(
                    e.id(), null, EventTrigger.from(e.trigger()), EventDirection.from(e.direction()), List.of());
            case FlowElementCS.ActivityCS e -> buildActivity(e);
            case FlowElementCS.GatewayCS e ->
                    new Gateway(e.id(), null, GatewayKind.from(e.kind()), constraints(e.pre(), null));
        };
    }

    private static Activity buildActivity(FlowElementCS.ActivityCS e) {
        List<ActivityConstraint> constraints = constraints(e.pre(), e.post());
        return switch (e.kind()) {
            case "task" -> new Task(e.id(), e.name(), constraints);
            case "call-activity" -> new CallActivity(e.id(), e.name(), constraints);
            // Nested content is not supported by the current grammar; a
            // subprocess is a flat activity here, like Task/CallActivity.
            case "subprocess" -> new SubProcess(e.id(), e.name(), constraints, List.of(), List.of());
            default -> throw new IllegalStateException("Unknown activity type: " + e.kind());
        };
    }

    private static List<ActivityConstraint> constraints(String pre, String post) {
        List<ActivityConstraint> result = new ArrayList<>(2);
        if (pre != null) result.add(new ActivityConstraint(ActivityConstraint.Kind.PRE, pre));
        if (post != null) result.add(new ActivityConstraint(ActivityConstraint.Kind.POST, post));
        return result;
    }
}
