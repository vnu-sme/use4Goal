package org.vnu.sme.goal.dsl.bpmnscenario.mm;

/** Explicit token-on-arc mark in a process instance. */
public record TokenMark(String processInstanceId, String sourceId, String targetId, String objectId) {
    public String arcId() {
        return sourceId + "::" + targetId;
    }

    public String display() {
        String text = processInstanceId + "." + arcId();
        return objectId == null ? text : text + " for " + objectId;
    }
}
