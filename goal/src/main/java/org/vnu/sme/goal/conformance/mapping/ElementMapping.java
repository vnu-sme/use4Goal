package org.vnu.sme.goal.conformance.mapping;

/**
 * Correspondence between one i* leaf Goal/Task and the BPMN2 FlowNode that realizes it.
 * The runtime counterpart of {@code map} in Definition 3.8 (Caballero-Villalobos).
 */
public record ElementMapping(String istarElementId, String bpmnNodeId) {}
