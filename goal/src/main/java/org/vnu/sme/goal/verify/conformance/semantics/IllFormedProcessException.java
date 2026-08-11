package org.vnu.sme.goal.verify.conformance.semantics;

/**
 * Thrown by {@link BpmnLtsBuilder#validateWellFormed} when a BPMN2 process is not
 * structured enough (SESE-style, Gröner Definition 3) for the token semantics of
 * §3.3.2 (doc/paper/conformance-istar-bpmn2.md) to be meaningful.
 */
public final class IllFormedProcessException extends RuntimeException {
    public IllFormedProcessException(String message) {
        super(message);
    }
}
