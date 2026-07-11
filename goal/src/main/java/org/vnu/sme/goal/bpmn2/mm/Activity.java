package org.vnu.sme.goal.bpmn2.mm;

/** Activity: Task | CallActivity | SubProcess. */
public sealed interface Activity extends FlowElement permits Task, CallActivity, SubProcess {}
