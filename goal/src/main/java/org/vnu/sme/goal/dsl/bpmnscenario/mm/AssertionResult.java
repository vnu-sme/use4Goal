package org.vnu.sme.goal.dsl.bpmnscenario.mm;

/** Result of evaluating a lightweight scenario assertion. */
public record AssertionResult(String expression, boolean holds, String detail) {}
