package org.vnu.sme.goal.dsl.bpmn.view;

import java.util.ArrayList;
import java.util.List;

/** Adapter: one Pool (participant) — a container of lanes and/or direct elements. */
public final class BpmnPool {

    public final String id;
    public final String label;
    public int x, y, w, h;
    public final List<BpmnLane> lanes    = new ArrayList<>();
    public final List<BpmnNode> elements = new ArrayList<>(); // nodes without a lane

    public BpmnPool(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public List<BpmnNode> allNodes() {
        List<BpmnNode> all = new ArrayList<>(elements);
        for (BpmnLane l : lanes) all.addAll(l.elements);
        return all;
    }
}
