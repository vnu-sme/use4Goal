package org.vnu.sme.goal.bpmn2.mm;

public final class EndEvent implements Event {

    private final String       id;
    private final EventTrigger trigger;
    private final String       oclSource;

    public EndEvent(String id, EventTrigger trigger) {
        this(id, trigger, null);
    }

    public EndEvent(String id, EventTrigger trigger, String oclSource) {
        this.id      = id;
        this.trigger = trigger;
        this.oclSource = oclSource;
    }

    @Override public String       id()      { return id; }
    @Override public EventTrigger trigger() { return trigger; }
    @Override public String       oclSource() { return oclSource; }
}
