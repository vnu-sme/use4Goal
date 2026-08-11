package org.vnu.sme.goal.dsl.bpmn.view;

/**
 * One MessageFlow drawn as a horizontal arrow in the Choreography view.
 * {@code y} is fixed by declaration order (no real timing info in the DSL).
 */
public record BpmnChoreoMessage(String fromParticipantId, String toParticipantId, String label, int y) {}
