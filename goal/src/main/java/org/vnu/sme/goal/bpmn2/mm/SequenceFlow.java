package org.vnu.sme.goal.bpmn2.mm;

/** Sequence flow — reference (not ownership) to its source/target FlowElement. */
public final class SequenceFlow {

    private final FlowElement source;
    private final FlowElement target;
    private final String      label;
    private final String      guardSource;

    public SequenceFlow(FlowElement source, FlowElement target, String label) {
        this(source, target, label, null);
    }

    public SequenceFlow(FlowElement source, FlowElement target, String label, String guardSource) {
        this.source = source;
        this.target = target;
        this.label  = label;
        this.guardSource = guardSource;
    }

    public FlowElement source() { return source; }
    public FlowElement target() { return target; }
    public String      label()  { return label; }
    public String      guardSource() { return guardSource; }
}
