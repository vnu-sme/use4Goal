package org.vnu.sme.goal.bpmn2.mm;

public final class IntermediateEvent implements Event {

    private final String         id;
    private final EventTrigger   trigger;
    private final EventDirection direction;

    public IntermediateEvent(String id, EventTrigger trigger, EventDirection direction) {
        this.id        = id;
        this.trigger   = trigger;
        this.direction = direction;
    }

    @Override public String         id()        { return id; }
    @Override public EventTrigger   trigger()   { return trigger; }
    public               EventDirection direction() { return direction; }
}
