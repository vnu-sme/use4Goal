package org.vnu.sme.goal.dsl.bpmnscenario.mm;

/** One process-qualified occurrence of a BPMN flow node in a scenario. */
public record NodeOccurrence(String processInstanceId, String elementId, String objectId, String actorId) {
    public String display() {
        StringBuilder b = new StringBuilder(processInstanceId).append('.').append(elementId);
        if (objectId != null) b.append(" for ").append(objectId);
        if (actorId != null) b.append(" by ").append(actorId);
        return b.toString();
    }
}
