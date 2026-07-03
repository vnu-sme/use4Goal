package org.vnu.sme.goal.maxbpmn.mm;

public record BpmnTask(String id, String label, String laneId) implements BpmnNode {}
