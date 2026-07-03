package org.vnu.sme.goal.bpmn2.mm;

/** Message flow (dashed arrow with hollow arrowhead) between pools. */
public record MessageFlow(String source, String target, String label) {}
