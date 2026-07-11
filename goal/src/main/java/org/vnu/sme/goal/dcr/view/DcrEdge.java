package org.vnu.sme.goal.dcr.view;

import org.vnu.sme.goal.dcr.mm.DcrRelationKind;

public record DcrEdge(String source, String target, DcrRelationKind kind, Integer time) {}
