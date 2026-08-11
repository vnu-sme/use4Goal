package org.vnu.sme.goal.verify.conformance.mapping;

/**
 * Correspondence between one i* actor and the BPMN2 pool/lane that realizes it
 * (Gröner §4: internal actor -> lane, external actor -> pool). {@code bpmnLaneId}
 * is null when the actor maps to a whole pool rather than one lane inside it.
 */
public record ActorMapping(String istarActorName, String bpmnPoolId, String bpmnLaneId) {

    public ActorMapping(String istarActorName, String bpmnPoolId) {
        this(istarActorName, bpmnPoolId, null);
    }
}
