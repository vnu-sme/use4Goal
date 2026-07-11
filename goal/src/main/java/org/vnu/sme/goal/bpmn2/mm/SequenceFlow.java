package org.vnu.sme.goal.bpmn2.mm;

/** Sequence flow — reference (not ownership) to its source/target FlowElement. */
public final class SequenceFlow {

    private final FlowElement source;
    private final FlowElement target;
    private final String      label;

    public SequenceFlow(FlowElement source, FlowElement target, String label) {
        this.source = source;
        this.target = target;
        this.label  = label;
    }

    public FlowElement source() { return source; }
    public FlowElement target() { return target; }
    public String      label()  { return label; }
}
