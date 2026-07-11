package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/**
 * Lane — partitions (references) a subset of its owning Process's
 * FlowElements for visual grouping. It does NOT own them (Process does);
 * see doc/05-bpmn2-metamodel.drawio.
 */
public final class Lane {

    private final String            id;
    private final String            name;
    private final List<FlowElement> flowElements;

    public Lane(String id, String name, List<FlowElement> flowElements) {
        this.id           = id;
        this.name         = name;
        this.flowElements = List.copyOf(flowElements);
    }

    public String            id()           { return id; }
    public String            name()         { return name; }
    public List<FlowElement> flowElements() { return flowElements; }
}
