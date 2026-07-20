package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

public final class CallActivity implements Activity {

    private final String id;
    private final List<ActivityConstraint> constraints;
    private final List<ActivityEffect> effects;

    public CallActivity(String id) {
        this(id, List.of(), List.of());
    }

    public CallActivity(String id, List<ActivityConstraint> constraints) {
        this(id, constraints, List.of());
    }

    public CallActivity(String id, List<ActivityConstraint> constraints, List<ActivityEffect> effects) {
        this.id          = id;
        this.constraints = List.copyOf(constraints);
        this.effects     = List.copyOf(effects);
    }

    @Override public String id() { return id; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
    @Override public List<ActivityEffect> effects() { return effects; }
}
