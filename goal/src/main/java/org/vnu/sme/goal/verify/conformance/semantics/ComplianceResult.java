package org.vnu.sme.goal.verify.conformance.semantics;

import java.util.List;

import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;

/** Result of {@link ComplianceChecker#check}. {@code counterexampleTrace} is empty iff compliant. */
public record ComplianceResult(
        ComplianceVerdict verdict,
        boolean weak,
        boolean stable,
        List<FlowElement> counterexampleTrace,
        String message) {}
