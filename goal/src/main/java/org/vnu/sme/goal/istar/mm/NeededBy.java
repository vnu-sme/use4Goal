package org.vnu.sme.goal.istar.mm;

/** resource --o--> task  (circle arrowhead: resource needed by task) */
public record NeededBy(String resource, String task) {}
