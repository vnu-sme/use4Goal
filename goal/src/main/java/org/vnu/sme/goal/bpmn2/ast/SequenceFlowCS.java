package org.vnu.sme.goal.bpmn2.ast;

/** Sequence flow between two flow elements (same Process/SubProcess) in concrete syntax. */
public record SequenceFlowCS(
        String source,
        String target,
        String label,      // nullable
        String guardSource // nullable boolean OCL
) {}
