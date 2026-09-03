package org.vnu.sme.goal.dsl.bpmn.mm;

/** Activity: Task | CallActivity | SubProcess. */
public sealed interface Activity extends FlowElement permits Task, CallActivity, SubProcess {
}
