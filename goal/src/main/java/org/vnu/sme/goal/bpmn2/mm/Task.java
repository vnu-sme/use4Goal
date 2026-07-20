package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

public final class Task implements Activity {

    private final String id;
    private final String name;
    private final List<ActivityConstraint> constraints;
    private final List<ActivityEffect> effects;

    public Task(String id, String name) {
        this(id, name, List.of(), List.of());
    }

    public Task(String id, String name, List<ActivityConstraint> constraints) {
        this(id, name, constraints, List.of());
    }

    public Task(String id, String name, List<ActivityConstraint> constraints, List<ActivityEffect> effects) {
        this.id          = id;
        this.name        = name;
        this.constraints = List.copyOf(constraints);
        this.effects     = List.copyOf(effects);
    }

    @Override public String id()   { return id; }
    public               String name() { return name; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
    @Override public List<ActivityEffect> effects() { return effects; }
}
