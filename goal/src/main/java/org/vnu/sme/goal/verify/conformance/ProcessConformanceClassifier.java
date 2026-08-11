package org.vnu.sme.goal.verify.conformance;

/** Lifts trace results to a process result without claiming more than explored. */
public final class ProcessConformanceClassifier {
    private ProcessConformanceClassifier() {}

    public static ConformanceVerdict classify(int conformant, int total,
                                               boolean completeSpace, boolean uniqueTraces) {
        if (total <= 0) return ConformanceVerdict.INCONCLUSIVE;
        if (!completeSpace || !uniqueTraces) return ConformanceVerdict.INCONCLUSIVE;
        if (conformant == total) return ConformanceVerdict.PROCESS_CONFORMANT;
        if (conformant == 0) return ConformanceVerdict.PROCESS_NON_CONFORMANT;
        return ConformanceVerdict.PROCESS_PARTIALLY_CONFORMANT;
    }
}
