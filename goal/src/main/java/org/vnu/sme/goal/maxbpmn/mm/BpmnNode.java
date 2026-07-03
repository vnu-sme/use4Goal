package org.vnu.sme.goal.maxbpmn.mm;

public sealed interface BpmnNode permits StartEvent, EndEvent, BpmnTask, Gateway {
    String id();
    String laneId();
}
