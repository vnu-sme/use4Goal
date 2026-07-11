package org.vnu.sme.goal.bpmn2.ast;

/** Cross-process message flow in concrete syntax. */
public record MessageFlowCS(
        String source,
        String target,
        String message   // nullable — id of a declared MessageCS
) {}
