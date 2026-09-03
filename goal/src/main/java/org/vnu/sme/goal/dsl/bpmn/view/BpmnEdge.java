package org.vnu.sme.goal.dsl.bpmn.view;

/** Adapter: sequence flow (within a pool) or message flow (across pools). */
public record BpmnEdge(String fromId, String toId, BpmnEdgeKind kind, String label) {}
