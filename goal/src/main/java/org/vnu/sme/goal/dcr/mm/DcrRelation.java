package org.vnu.sme.goal.dcr.mm;

public record DcrRelation(DcrRelationKind kind, String source, String target, Integer time) {}
