package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/**
 * BPMN 2.0 flow node hierarchy.
 * Events: circle (Start=thin, End=thick, Intermediate=double)
 * Task:   rounded rectangle
 * SubProcess: rounded rectangle with + marker
 * Gateway: diamond
 */
public sealed interface FlowNode
        permits FlowNode.StartEvent, FlowNode.EndEvent, FlowNode.IntermediateEvent,
                FlowNode.Task, FlowNode.SubProcess, FlowNode.Gateway {

    String id();

    record StartEvent       (String id, EventType type) implements FlowNode {}
    record EndEvent         (String id, EventType type) implements FlowNode {}
    record IntermediateEvent(String id, EventType type, boolean catching) implements FlowNode {}
    record Task             (String id, String label)   implements FlowNode {}
    record Gateway          (String id, GatewayType type) implements FlowNode {}

    final class SubProcess implements FlowNode {
        private final String         id;
        private final String         label;
        private final List<FlowNode> elements;
        private final List<SequenceFlow> flows;

        public SubProcess(String id, String label,
                          List<FlowNode> elements, List<SequenceFlow> flows) {
            this.id       = id;
            this.label    = label;
            this.elements = List.copyOf(elements);
            this.flows    = List.copyOf(flows);
        }

        @Override public String id()              { return id; }
        public        String   label()            { return label; }
        public        List<FlowNode>    elements() { return elements; }
        public        List<SequenceFlow> flows()  { return flows; }
    }
}
