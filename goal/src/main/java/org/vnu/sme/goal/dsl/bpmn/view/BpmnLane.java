package org.vnu.sme.goal.dsl.bpmn.view;

import java.util.ArrayList;
import java.util.List;

/** Adapter: horizontal band within a {@link BpmnPool}. Bounds are mutable (drag/resize). */
public final class BpmnLane {

    public final String id;
    public final String label;
    public int x, y, w, h;
    public final List<BpmnNode> elements = new ArrayList<>();

    public BpmnLane(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
