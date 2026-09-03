package org.vnu.sme.goal.dsl.bpmn.ast;

/** Message declaration in concrete syntax. */
public record MessageCS(
        String id,
        String name   // nullable
) {}
