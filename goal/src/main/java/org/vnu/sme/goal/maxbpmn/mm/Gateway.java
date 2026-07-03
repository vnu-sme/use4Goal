package org.vnu.sme.goal.maxbpmn.mm;

public record Gateway(String id, GatewayType type, GatewayRole role, String laneId)
        implements BpmnNode {}
