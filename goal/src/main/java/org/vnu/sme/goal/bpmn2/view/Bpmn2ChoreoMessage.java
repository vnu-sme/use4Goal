package org.vnu.sme.goal.bpmn2.view;

/**
 * One MessageFlow drawn as a horizontal arrow in the Choreography view.
 * {@code y} is fixed by declaration order (no real timing info in the DSL).
 */
public record Bpmn2ChoreoMessage(String fromParticipantId, String toParticipantId, String label, int y) {}
