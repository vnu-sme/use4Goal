package org.vnu.sme.goal.bpmn2.mm;

/**
 * Event: StartEvent | EndEvent | IntermediateEvent. Kept as one class per
 * kind only because IntermediateEvent carries an extra relation
 * (direction) that Start/EndEvent do not — otherwise a single class with a
 * "kind" attribute would have been enough.
 */
public sealed interface Event extends FlowElement permits StartEvent, EndEvent, IntermediateEvent {
    EventTrigger trigger();
}
