package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

public final class SubProcess implements Activity {
    private final String id;
    private final String name;
    private final List<ActivityConstraint> constraints;
    private final String effectSource;
    private final List<FlowElement> flowElements;
    private final List<SequenceFlow> sequenceFlows;

    public SubProcess(String id, String name, List<FlowElement> elements, List<SequenceFlow> flows) {
        this(id, name, List.of(), null, elements, flows);
    }
    public SubProcess(String id, String name, List<ActivityConstraint> constraints,
            List<FlowElement> elements, List<SequenceFlow> flows) {
        this(id, name, constraints, null, elements, flows);
    }
    public SubProcess(String id, String name, List<ActivityConstraint> constraints, String effectSource,
            List<FlowElement> elements, List<SequenceFlow> flows) {
        this.id = id;
        this.name = name;
        this.constraints = List.copyOf(constraints);
        this.effectSource = effectSource;
        this.flowElements = List.copyOf(elements);
        this.sequenceFlows = List.copyOf(flows);
    }
    @Override public String id() { return id; }
    @Override public String name() { return name; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
    @Override public String effectSource() { return effectSource; }
    public List<FlowElement> flowElements() { return flowElements; }
    public List<SequenceFlow> sequenceFlows() { return sequenceFlows; }
}
