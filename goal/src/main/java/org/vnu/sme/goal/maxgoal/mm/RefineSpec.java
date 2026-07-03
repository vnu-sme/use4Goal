package org.vnu.sme.goal.maxgoal.mm;

import java.util.List;

/**
 * Sealed hierarchy for goal/task refinement.
 *
 *   IterRefine extends SeqRefine  — loop is ordered sequential execution that repeats
 *   XorRefine  extends IorRefine  — exclusive-OR is inclusive-OR with exactly-one constraint
 *
 * Sealed permits all 5 concrete types so pattern-match switches stay exhaustive.
 */
public sealed interface RefineSpec
        permits RefineSpec.SeqRefine, RefineSpec.IterRefine,
                RefineSpec.ParRefine,
                RefineSpec.IorRefine, RefineSpec.XorRefine {

    // ── Sequential base ───────────────────────────────────────────────────────

    /** Ordered children, executed once in sequence. */
    non-sealed abstract class SeqRefine implements RefineSpec {
        public abstract List<String> children();

        public static SeqRefine of(List<String> children) {
            return new SeqRefine() {
                @Override public List<String> children() { return children; }
                @Override public String toString() { return "SeqRefine" + children; }
            };
        }
    }

    /** Ordered body, repeated until {@code until} guard expression is true (ITER extends SEQ). */
    final class IterRefine extends SeqRefine implements RefineSpec {
        private final List<String> children;
        private final String       until;

        public IterRefine(List<String> body, String until) {
            this.children = List.copyOf(body);
            this.until    = until;
        }

        @Override public List<String> children() { return children; }
        public       String           until()     { return until; }

        @Override public String toString() {
            return "IterRefine{body=" + children + ", until='" + until + "'}";
        }
    }

    // ── Parallel ──────────────────────────────────────────────────────────────

    record ParRefine(List<String> children) implements RefineSpec {}

    // ── Inclusive-OR base ─────────────────────────────────────────────────────

    /** One or more guarded branches fire. */
    non-sealed abstract class IorRefine implements RefineSpec {
        public abstract List<GuardedChild> branches();

        public static IorRefine of(List<GuardedChild> branches) {
            return new IorRefine() {
                @Override public List<GuardedChild> branches() { return branches; }
                @Override public String toString() { return "IorRefine" + branches; }
            };
        }
    }

    /** Exactly one guarded branch fires (XOR extends IOR). */
    final class XorRefine extends IorRefine implements RefineSpec {
        private final List<GuardedChild> branches;

        public XorRefine(List<GuardedChild> branches) {
            this.branches = List.copyOf(branches);
        }

        @Override public List<GuardedChild> branches() { return branches; }

        @Override public String toString() {
            return "XorRefine" + branches;
        }
    }
}
