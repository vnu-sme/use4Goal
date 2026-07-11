package org.vnu.sme.goal.bpmn2scenario.ast;

/** Process-qualified BPMN flow node id, e.g. {@code m1.checkCalendar}. */
public record QualifiedFlowNodeCS(String processInstanceId, String elementId) {}
