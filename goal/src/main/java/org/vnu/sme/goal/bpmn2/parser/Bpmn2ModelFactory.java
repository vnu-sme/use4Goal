package org.vnu.sme.goal.bpmn2.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.bpmn2.ast.*;
import org.vnu.sme.goal.bpmn2.mm.*;

/**
 * Converts the BPMN 2.0 AST (CS layer) to the runtime MetaModel (MM layer).
 */
public final class Bpmn2ModelFactory {

    private Bpmn2ModelFactory() {}

    public static Bpmn2Collaboration build(Bpmn2CollaborationCS cs) {
        Bpmn2Collaboration collab = new Bpmn2Collaboration(cs.id());

        for (PoolCS pCS : cs.pools()) {
            collab.addPool(buildPool(pCS));
        }
        for (MessageFlowCS mCS : cs.messageFlows()) {
            collab.addMessageFlow(new MessageFlow(mCS.source(), mCS.target(), mCS.label()));
        }
        return collab;
    }

    private static Pool buildPool(PoolCS cs) {
        List<Lane>         lanes    = cs.lanes().stream()
                .map(Bpmn2ModelFactory::buildLane)
                .collect(Collectors.toList());
        List<FlowNode>     elements = cs.elements().stream()
                .map(Bpmn2ModelFactory::buildFlowNode)
                .collect(Collectors.toList());
        List<SequenceFlow> flows    = cs.sequenceFlows().stream()
                .map(sf -> new SequenceFlow(sf.source(), sf.target(), sf.condition()))
                .collect(Collectors.toList());
        return new Pool(cs.id(), cs.label(), lanes, elements, flows);
    }

    private static Lane buildLane(LaneCS cs) {
        List<FlowNode> elements = cs.elements().stream()
                .map(Bpmn2ModelFactory::buildFlowNode)
                .collect(Collectors.toList());
        return new Lane(cs.id(), cs.label(), elements);
    }

    private static FlowNode buildFlowNode(FlowNodeCS cs) {
        return switch (cs) {
            case FlowNodeCS.StartEventCS      e -> new FlowNode.StartEvent(e.id(), EventType.from(e.eventType()));
            case FlowNodeCS.EndEventCS        e -> new FlowNode.EndEvent(e.id(), EventType.from(e.eventType()));
            case FlowNodeCS.IntermediateEventCS e ->
                    new FlowNode.IntermediateEvent(e.id(), EventType.from(e.eventType()), e.catching());
            case FlowNodeCS.TaskCS            e -> new FlowNode.Task(e.id(), e.label());
            case FlowNodeCS.GatewayCS         e -> new FlowNode.Gateway(e.id(), GatewayType.from(e.gwType()));
            case FlowNodeCS.SubProcessCS      e -> new FlowNode.SubProcess(
                    e.id(), e.label(),
                    e.children().stream().map(Bpmn2ModelFactory::buildFlowNode).collect(Collectors.toList()),
                    e.flows().stream().map(sf -> new SequenceFlow(sf.source(), sf.target(), sf.condition())).collect(Collectors.toList()));
        };
    }
}
