package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

public final class CallActivity implements Activity {
    private final String id;
    private final String name;
    private final List<ActivityConstraint> constraints;
    private final String effectSource;

    public CallActivity(String id) { this(id, null, List.of(), null); }
    public CallActivity(String id, String name, List<ActivityConstraint> constraints) {
        this(id, name, constraints, null);
    }
    public CallActivity(String id, String name, List<ActivityConstraint> constraints, String effectSource) {
        this.id = id;
        this.name = name;
        this.constraints = List.copyOf(constraints);
        this.effectSource = effectSource;
    }
    @Override public String id() { return id; }
    @Override public String name() { return name; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
    @Override public String effectSource() { return effectSource; }
}
