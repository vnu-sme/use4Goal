package org.vnu.sme.goal.bpmn2.mm;

/** Sequence flow (solid arrow) within a pool. */
public record SequenceFlow(String source, String target, String condition) {}
