package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

public final class EndEvent implements Event {
    private final String id, name;
    private final EventTrigger trigger;
    private final List<ActivityConstraint> constraints;
    public EndEvent(String id, EventTrigger trigger) { this(id, null, trigger, List.of()); }
    public EndEvent(String id, String name, EventTrigger trigger, List<ActivityConstraint> constraints) {
        this.id = id; this.name = name; this.trigger = trigger; this.constraints = List.copyOf(constraints);
    }
    @Override public String id() { return id; }
    @Override public String name() { return name; }
    @Override public EventTrigger trigger() { return trigger; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
}
