package org.vnu.sme.goal.verify.conformance.semantics;

import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;

/** One step of the product LTS: firing {@code fired} moves the product to {@code next}. */
public record Transition(FlowElement fired, ProductState next) {}
