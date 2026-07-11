package org.vnu.sme.goal.bpmn2.ast;

/** Message declaration in concrete syntax. */
public record MessageCS(
        String id,
        String name   // nullable
) {}
