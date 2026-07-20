package org.vnu.sme.goal.bpmn2.mm;

public final class IntermediateEvent implements Event {

    private final String         id;
    private final EventTrigger   trigger;
    private final EventDirection direction;
    private final String         oclSource;

    public IntermediateEvent(String id, EventTrigger trigger, EventDirection direction) {
        this(id, trigger, direction, null);
    }

    public IntermediateEvent(String id, EventTrigger trigger, EventDirection direction, String oclSource) {
        this.id        = id;
        this.trigger   = trigger;
        this.direction = direction;
        this.oclSource = oclSource;
    }

    @Override public String         id()        { return id; }
    @Override public EventTrigger   trigger()   { return trigger; }
    public               EventDirection direction() { return direction; }
    @Override public String         oclSource() { return oclSource; }
}
