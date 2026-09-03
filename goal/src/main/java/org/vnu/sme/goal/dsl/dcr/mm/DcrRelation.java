package org.vnu.sme.goal.dsl.dcr.mm;

public record DcrRelation(DcrRelationKind kind, String source, String target, Integer time) {}
