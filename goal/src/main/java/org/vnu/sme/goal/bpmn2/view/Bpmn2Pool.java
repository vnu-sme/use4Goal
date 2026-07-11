package org.vnu.sme.goal.bpmn2.view;

import java.util.ArrayList;
import java.util.List;

/** Adapter: one Pool (participant) — a container of lanes and/or direct elements. */
public final class Bpmn2Pool {

    public final String id;
    public final String label;
    public int x, y, w, h;
    public final List<Bpmn2Lane> lanes    = new ArrayList<>();
    public final List<Bpmn2Node> elements = new ArrayList<>(); // nodes without a lane

    public Bpmn2Pool(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public List<Bpmn2Node> allNodes() {
        List<Bpmn2Node> all = new ArrayList<>(elements);
        for (Bpmn2Lane l : lanes) all.addAll(l.elements);
        return all;
    }
}
