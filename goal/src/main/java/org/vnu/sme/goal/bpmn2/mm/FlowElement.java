package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/** State-oriented BPMN flow element. */
public sealed interface FlowElement permits Event, Activity, Gateway {
    String id();
    default String name() { return null; }
    default List<ActivityConstraint> constraints() { return List.of(); }
    default List<ActivityConstraint> preconditions() {
        return constraints().stream().filter(c -> c.kind() == ActivityConstraint.Kind.PRE).toList();
    }
    default List<ActivityConstraint> postconditions() {
        return constraints().stream().filter(c -> c.kind() == ActivityConstraint.Kind.POST).toList();
    }
}
