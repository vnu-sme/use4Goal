package org.vnu.sme.goal.dsl.bpmn.mm;

/** Sequence flow — reference (not ownership) to its source/target FlowElement. */
public final class SequenceFlow {

    private final FlowElement source;
    private final FlowElement target;
    private final String      label;
    private final String      guardSource;
    private final boolean     isDefault;

    public SequenceFlow(FlowElement source, FlowElement target, String label) {
        this(source, target, label, null, false);
    }

    public SequenceFlow(FlowElement source, FlowElement target, String label, String guardSource) {
        this(source, target, label, guardSource, false);
    }

    public SequenceFlow(FlowElement source, FlowElement target, String label, String guardSource,
            boolean isDefault) {
        this.source = source;
        this.target = target;
        this.label  = label;
        this.guardSource = guardSource;
        this.isDefault = isDefault;
    }

    public FlowElement source() { return source; }
    public FlowElement target() { return target; }
    public String      label()  { return label; }
    public String      guardSource() { return guardSource; }
    /** True for a gateway's fallback flow: taken only if no other outgoing flow's guard is true. */
    public boolean      isDefault() { return isDefault; }
}
