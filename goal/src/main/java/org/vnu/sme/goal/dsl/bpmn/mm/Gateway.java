package org.vnu.sme.goal.dsl.bpmn.mm;

import java.util.List;

public final class Gateway implements FlowElement {
    private final String id, name;
    private final GatewayKind kind;
    private final List<ActivityConstraint> constraints;
    public Gateway(String id, GatewayKind kind) { this(id, null, kind, List.of()); }
    public Gateway(String id, String name, GatewayKind kind, List<ActivityConstraint> constraints) {
        this.id = id; this.name = name; this.kind = kind; this.constraints = List.copyOf(constraints);
    }
    @Override public String id() { return id; }
    @Override public String name() { return name; }
    public GatewayKind kind() { return kind; }
    @Override public List<ActivityConstraint> constraints() { return constraints; }
}
