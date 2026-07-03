package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/** BPMN 2.0 Lane — horizontal band within a Pool. */
public final class Lane {

    private final String         id;
    private final String         label;
    private final List<FlowNode> elements;

    public Lane(String id, String label, List<FlowNode> elements) {
        this.id       = id;
        this.label    = label;
        this.elements = List.copyOf(elements);
    }

    public String         id()       { return id; }
    public String         label()    { return label; }
    public List<FlowNode> elements() { return elements; }
}
