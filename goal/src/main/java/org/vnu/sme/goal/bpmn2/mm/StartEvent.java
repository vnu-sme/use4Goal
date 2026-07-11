package org.vnu.sme.goal.bpmn2.mm;

public final class StartEvent implements Event {

    private final String       id;
    private final EventTrigger trigger;

    public StartEvent(String id, EventTrigger trigger) {
        this.id      = id;
        this.trigger = trigger;
    }

    @Override public String       id()      { return id; }
    @Override public EventTrigger trigger() { return trigger; }
}
