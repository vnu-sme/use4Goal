package org.vnu.sme.goal.bpmn2.ast;

/** Sequence flow between two flow nodes in concrete syntax. */
public record SequenceFlowCS(
        String source,
        String target,
        String condition   // nullable
) {}
