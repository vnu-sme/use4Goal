package org.vnu.sme.goal.dsl.bpmn.parser;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.bpmn.mm.*;
import org.vnu.sme.goal.dsl.bpmn.mm.Process;

/**
 * Semantic constraints on the BPMN 2 MM that go beyond what the grammar and the
 * model factory already enforce.
 *
 * <p>Rules enforced here (version 1 boundary):
 * <ol>
 *   <li>Event-based gateways are rejected. Inclusive gateways use the
 *       state-oriented activation semantics implemented by the execution and
 *       state-trace evaluators.</li>
 *   <li>MessageFlow elements are rejected — the execution engine does not process
 *       inter-pool communication in version 1.</li>
 *   <li>A pool without a {@code for} group-class binding whose activities reference
 *       {@code self} in pre/post conditions is rejected, because {@code self} has no
 *       meaning unless scoped to a concrete group instance.</li>
 * </ol>
 */
public final class BpmnSemanticValidator {

    private BpmnSemanticValidator() {}

    public static List<String> validate(BpmnModel model) {
        List<String> errors = new ArrayList<>();
        validateGateways(model, errors);
        validateMessageFlows(model, errors);
        validateSelfInUnboundPools(model, errors);
        return List.copyOf(errors);
    }

    // ── Gateway kind ─────────────────────────────────────────────────────────

    private static void validateGateways(BpmnModel model, List<String> errors) {
        for (Process process : model.processes()) {
            for (FlowElement fe : process.flowElements()) {
                if (fe instanceof Gateway gw) {
                    switch (gw.kind()) {
                        case EVENT_BASED ->
                            errors.add("BPMN unsupported: event-based gateway '" + gw.id()
                                    + "' in pool '" + process.id() + "' — event-based gateway"
                                    + " semantics are not implemented in version 1");
                        default -> { /* XOR, AND and OR are supported */ }
                    }
                }
            }
        }
    }

    // ── Message flows ─────────────────────────────────────────────────────────

    private static void validateMessageFlows(BpmnModel model, List<String> errors) {
        if (!model.messageFlows().isEmpty()) {
            errors.add("BPMN unsupported: the model declares " + model.messageFlows().size()
                    + " message-flow(s) — inter-pool message-flow semantics are not implemented"
                    + " in version 1 and will not be executed or verified");
        }
    }

    // ── self in unbound pools ─────────────────────────────────────────────────

    private static final String SELF_MARKER = "self";

    private static void validateSelfInUnboundPools(BpmnModel model, List<String> errors) {
        for (Process process : model.processes()) {
            // A pool declared `pool Foo for BarGroup` has a non-null groupClass.
            if (process.groupClass() != null) continue;
            for (FlowElement fe : process.flowElements()) {
                if (fe instanceof Activity activity) {
                    for (ActivityConstraint c : activity.constraints()) {
                        if (c.oclBody() != null && containsSelf(c.oclBody())) {
                            errors.add("BPMN semantic: activity '" + activity.id()
                                    + "' in pool '" + process.id()
                                    + "' references 'self' in a " + c.kind().name().toLowerCase()
                                    + " condition, but the pool has no 'for <GroupClass>' binding"
                                    + " — 'self' has no meaning without a concrete group scope");
                        }
                    }
                }
            }
        }
    }

    /** Quick heuristic: true when the OCL source text contains a bare {@code self} token. */
    private static boolean containsSelf(String ocl) {
        int idx = ocl.indexOf(SELF_MARKER);
        while (idx >= 0) {
            boolean beforeOk = idx == 0 || !Character.isJavaIdentifierPart(ocl.charAt(idx - 1));
            boolean afterOk  = idx + SELF_MARKER.length() >= ocl.length()
                    || !Character.isJavaIdentifierPart(ocl.charAt(idx + SELF_MARKER.length()));
            if (beforeOk && afterOk) return true;
            idx = ocl.indexOf(SELF_MARKER, idx + 1);
        }
        return false;
    }
}
