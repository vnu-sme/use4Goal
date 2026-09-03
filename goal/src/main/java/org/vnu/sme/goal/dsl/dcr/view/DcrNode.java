package org.vnu.sme.goal.dsl.dcr.view;

import org.vnu.sme.goal.dsl.dcr.mm.DcrEvent;

public final class DcrNode {
    public final DcrEvent event;
    public final String id;
    public final String label;
    public int x;
    public int y;
    public int w = 155;
    public int h = 105;

    public DcrNode(DcrEvent event) {
        this.event = event;
        this.id = event.id();
        this.label = event.label();
    }

    public boolean contains(int px, int py) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }
}
