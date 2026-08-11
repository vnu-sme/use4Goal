package org.vnu.sme.goal.dsl.bpmn.mm;

import java.util.List;

public final class CallActivity implements Activity {
    private final String id;
    private final String name;
    private final List<ActivityConstraint> constraints;

    public CallActivity(String id) { this(id, null, List.of()); }
    public CallActivity(String id, String name, List<ActivityConstraint> constraints) {
        this.id = id;
        this.name = name;
        this.constraints = List.copyOf(constraints);
    }
    @Override public String id() { return id; }
    @Override public String name() { return name; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
}
