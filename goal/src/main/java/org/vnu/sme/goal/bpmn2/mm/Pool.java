package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/** BPMN 2.0 Pool — represents one participant in a Collaboration. */
public final class Pool {

    private final String             id;
    private final String             label;
    private final List<Lane>         lanes;
    private final List<FlowNode>     elements;  // elements not inside any lane
    private final List<SequenceFlow> flows;

    public Pool(String id, String label,
                List<Lane> lanes,
                List<FlowNode> elements,
                List<SequenceFlow> flows) {
        this.id       = id;
        this.label    = label != null ? label : id;
        this.lanes    = List.copyOf(lanes);
        this.elements = List.copyOf(elements);
        this.flows    = List.copyOf(flows);
    }

    public String             id()       { return id; }
    public String             label()    { return label; }
    public List<Lane>         lanes()    { return lanes; }
    public List<FlowNode>     elements() { return elements; }
    public List<SequenceFlow> flows()    { return flows; }

    /** All flow nodes reachable in this pool (lanes + direct elements). */
    public java.util.List<FlowNode> allNodes() {
        var list = new java.util.ArrayList<FlowNode>(elements);
        for (Lane l : lanes) list.addAll(l.elements());
        return list;
    }
}
