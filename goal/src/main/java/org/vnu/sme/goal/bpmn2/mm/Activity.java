package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/** Activity: Task | CallActivity | SubProcess. */
public sealed interface Activity extends FlowElement permits Task, CallActivity, SubProcess {
    List<ActivityConstraint> constraints();
    List<ActivityEffect> effects();

    default List<ActivityConstraint> preconditions() {
        return constraints().stream()
                .filter(c -> c.kind() == ActivityConstraint.Kind.PRE)
                .toList();
    }

    default List<ActivityConstraint> postconditions() {
        return constraints().stream()
                .filter(c -> c.kind() == ActivityConstraint.Kind.POST)
                .toList();
    }
}
