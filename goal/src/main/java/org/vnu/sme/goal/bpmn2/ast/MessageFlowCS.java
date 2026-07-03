package org.vnu.sme.goal.bpmn2.ast;

/** Cross-pool message flow in concrete syntax. */
public record MessageFlowCS(
        String source,
        String target,
        String label    // nullable
) {}
