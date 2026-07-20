package org.vnu.sme.goal.bpmn2.mm;

/**
 * Root of the BPMN 2.0 flow element hierarchy: Event | Activity | Gateway.
 * Mirrors the abstract FlowElement class in doc/05-bpmn2-metamodel.drawio.
 */
public sealed interface FlowElement permits Event, Activity, Gateway {
    String id();

    default String oclSource() { return null; }
}
