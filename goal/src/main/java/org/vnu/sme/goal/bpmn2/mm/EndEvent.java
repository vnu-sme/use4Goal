package org.vnu.sme.goal.bpmn2.mm;

public final class EndEvent implements Event {

    private final String       id;
    private final EventTrigger trigger;

    public EndEvent(String id, EventTrigger trigger) {
        this.id      = id;
        this.trigger = trigger;
    }

    @Override public String       id()      { return id; }
    @Override public EventTrigger trigger() { return trigger; }
}
