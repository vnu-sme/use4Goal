package org.vnu.sme.goal.bpmn2.view;

/** Adapter: one column in the Choreography view — a Process referenced by ≥1 MessageFlow. */
public final class Bpmn2ChoreoParticipant {

    public final String id;      // Process id
    public final String label;
    public int x;                // column center, mutable (drag)

    public Bpmn2ChoreoParticipant(String id, String label, int x) {
        this.id    = id;
        this.label = label;
        this.x     = x;
    }
}
