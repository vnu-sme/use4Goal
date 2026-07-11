package org.vnu.sme.goal.bpmn2.view;

import java.util.ArrayList;
import java.util.List;

/** Adapter: horizontal band within a {@link Bpmn2Pool}. Bounds are mutable (drag/resize). */
public final class Bpmn2Lane {

    public final String id;
    public final String label;
    public int x, y, w, h;
    public final List<Bpmn2Node> elements = new ArrayList<>();

    public Bpmn2Lane(String id, String label) {
        this.id = id;
        this.label = label;
    }
}
