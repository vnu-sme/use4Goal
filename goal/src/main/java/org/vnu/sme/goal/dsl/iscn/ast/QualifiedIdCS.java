package org.vnu.sme.goal.dsl.iscn.ast;

/** {@code instanceId.elementId}, or just {@code elementId} when {@code instanceId} is null (legacy/default). */
public record QualifiedIdCS(String instanceId, String elementId) {}
