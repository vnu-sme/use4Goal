package org.vnu.sme.goal.dsl.dcr.view;

import org.vnu.sme.goal.dsl.dcr.mm.DcrRelationKind;

public record DcrEdge(String source, String target, DcrRelationKind kind, Integer time) {}
