package org.vnu.sme.goal.bpmn2.mm;

/** Activity: Task | CallActivity | SubProcess. */
public sealed interface Activity extends FlowElement permits Task, CallActivity, SubProcess {
    /** Generic SOIL program; it must select objects by type/navigation, never by snapshot object name. */
    default String effectSource() { return null; }
}
