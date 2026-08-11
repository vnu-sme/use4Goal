package org.vnu.sme.goal.verify.conformance;

/** Verdicts retained by the concrete-trace flow. */
public enum ConformanceVerdict {
    TRACE_CONFORMANT,
    TRACE_NON_CONFORMANT,
    PROCESS_CONFORMANT,
    PROCESS_NON_CONFORMANT,
    PROCESS_PARTIALLY_CONFORMANT,
    INCONCLUSIVE,
    EXECUTION_ERROR;

    public boolean isConformant() {
        return this == TRACE_CONFORMANT || this == PROCESS_CONFORMANT;
    }

    public boolean isProcessLevel() {
        return this == PROCESS_CONFORMANT || this == PROCESS_NON_CONFORMANT
                || this == PROCESS_PARTIALLY_CONFORMANT || this == INCONCLUSIVE;
    }
}
