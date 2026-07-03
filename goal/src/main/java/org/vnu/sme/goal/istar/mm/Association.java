package org.vnu.sme.goal.istar.mm;

/** actor is-a / participates-in another actor */
public record Association(String actor, AssocKind kind, String target) {}
