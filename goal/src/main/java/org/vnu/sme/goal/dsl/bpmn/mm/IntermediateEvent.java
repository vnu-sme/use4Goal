package org.vnu.sme.goal.dsl.bpmn.mm;

import java.util.List;

public final class IntermediateEvent implements Event {
    private final String id, name;
    private final EventTrigger trigger;
    private final EventDirection direction;
    private final List<ActivityConstraint> constraints;
    public IntermediateEvent(String id, EventTrigger trigger, EventDirection direction) {
        this(id, null, trigger, direction, List.of());
    }
    public IntermediateEvent(String id, String name, EventTrigger trigger, EventDirection direction,
            List<ActivityConstraint> constraints) {
        this.id = id; this.name = name; this.trigger = trigger; this.direction = direction;
        this.constraints = List.copyOf(constraints);
    }
    @Override public String id() { return id; }
    @Override public String name() { return name; }
    @Override public EventTrigger trigger() { return trigger; }
    public EventDirection direction() { return direction; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
}
