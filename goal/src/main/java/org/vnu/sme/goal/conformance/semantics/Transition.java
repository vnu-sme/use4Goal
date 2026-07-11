package org.vnu.sme.goal.conformance.semantics;

import org.vnu.sme.goal.bpmn2.mm.FlowElement;

/** One step of the product LTS: firing {@code fired} moves the product to {@code next}. */
public record Transition(FlowElement fired, ProductState next) {}
