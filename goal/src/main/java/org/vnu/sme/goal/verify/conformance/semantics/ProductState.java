package org.vnu.sme.goal.verify.conformance.semantics;

/** One state of the synchronous product LTS — Definition 4.1 (Caballero-Villalobos). */
public record ProductState(IStarMarking istar, BpmnMarking bpmn) {}
