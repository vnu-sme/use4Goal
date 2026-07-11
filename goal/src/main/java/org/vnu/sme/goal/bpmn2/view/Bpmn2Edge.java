package org.vnu.sme.goal.bpmn2.view;

/** Adapter: sequence flow (within a pool) or message flow (across pools). */
public record Bpmn2Edge(String fromId, String toId, Bpmn2EdgeKind kind, String label) {}
