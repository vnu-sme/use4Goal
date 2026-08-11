package org.vnu.sme.goal.verify.conformance.semantics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;

/**
 * Weak compliance / stability / strong compliance checking — Algorithms 1, 3, 4 of
 * Caballero-Villalobos §5, transcribed directly onto {@link ProductLts} (§3.8 of
 * doc/paper/conformance-istar-bpmn2.md). This is what "so sánh dựa trên chạy kịch bản"
 * (comparison based on running the scenario) means in code: run every possible execution of
 * the BPMN process forward, track how it drives the i* satisfaction marking, and check
 * whether "the goal is always still reachable".
 */
public final class ComplianceChecker {

    private ComplianceChecker() {}

    public static ComplianceResult check(ProductLts c, Set<String> qualityIds) {
        ProductState s0 = c.initial();

        Map<ProductState, ProductState> predecessor = new LinkedHashMap<>();
        Map<ProductState, FlowElement> predecessorFiring = new LinkedHashMap<>();
        Set<ProductState> reachable = forwardBfs(c, s0, predecessor, predecessorFiring);

        Set<ProductState> successStates = new LinkedHashSet<>();
        for (ProductState s : reachable) if (s.istar().isSuccess()) successStates.add(s);

        Set<ProductState> terminal = new LinkedHashSet<>();
        for (ProductState s : reachable) if (c.successors(s).isEmpty()) terminal.add(s);

        Set<ProductState> canReachSuccess = backwardBfs(c, reachable, successStates);

        Set<ProductState> disjunction = new LinkedHashSet<>(canReachSuccess);
        for (ProductState s : terminal) if (successStates.contains(s)) disjunction.add(s);

        boolean weak = disjunction.containsAll(reachable);

        boolean stable = true;
        for (String q : qualityIds) {
            Set<ProductState> notQ = new LinkedHashSet<>();
            for (ProductState s : reachable) {
                if (s.istar().qualityStatus(q) != QualityStatus.TRUE) notQ.add(s);
            }
            Set<ProductState> reachNotQ = backwardBfs(c, reachable, notQ);
            Set<ProductState> stableForQ = new LinkedHashSet<>(notQ);
            stableForQ.addAll(minus(reachable, reachNotQ));
            if (!stableForQ.containsAll(reachable)) {
                stable = false;
                break;
            }
        }

        ComplianceVerdict verdict = !weak ? ComplianceVerdict.NON_COMPLIANT
                : (stable ? ComplianceVerdict.STRONG_COMPLIANT : ComplianceVerdict.WEAK_COMPLIANT);

        List<FlowElement> trace = List.of();
        String message = "Every reachable state can reach a state where all qualities are satisfied.";
        if (!weak) {
            ProductState offender = firstOffending(reachable, disjunction);
            List<FlowElement> toOffender = reconstructTrace(s0, offender, predecessor, predecessorFiring);
            List<FlowElement> full = new ArrayList<>(toOffender);
            full.addAll(extendDoomedRun(c, offender));
            trace = full;
            message = "From state " + offender + " onward, no continuation reaches a state where "
                    + "all qualities are satisfied (this is the earliest point of no return, not "
                    + "necessarily a dead end — the trace below shows one full doomed run from here).";
        }

        return new ComplianceResult(verdict, weak, stable, trace, message);
    }

    private static ProductState firstOffending(Set<ProductState> reachable, Set<ProductState> disjunction) {
        for (ProductState s : reachable) if (!disjunction.contains(s)) return s;
        throw new IllegalStateException("weak == false but no offending state was found");
    }

    private static Set<ProductState> minus(Set<ProductState> a, Set<ProductState> b) {
        Set<ProductState> r = new LinkedHashSet<>(a);
        r.removeAll(b);
        return r;
    }

    /** Algorithm 2 (Forward_BFS), also recording predecessor pointers for trace reconstruction. */
    private static Set<ProductState> forwardBfs(ProductLts c, ProductState s0,
            Map<ProductState, ProductState> predecessor, Map<ProductState, FlowElement> predecessorFiring) {
        Set<ProductState> visited = new LinkedHashSet<>();
        Deque<ProductState> queue = new ArrayDeque<>();
        visited.add(s0);
        queue.add(s0);
        while (!queue.isEmpty()) {
            ProductState s = queue.poll();
            for (Transition t : c.successors(s)) {
                if (visited.add(t.next())) {
                    predecessor.put(t.next(), s);
                    predecessorFiring.put(t.next(), t.fired());
                    queue.add(t.next());
                }
            }
        }
        return visited;
    }

    /** Algorithm 3 (Backward_BFS), restricted to {@code reachable} (all we ever care about). */
    private static Set<ProductState> backwardBfs(ProductLts c, Set<ProductState> reachable, Set<ProductState> targets) {
        Map<ProductState, List<ProductState>> reversePreds = new LinkedHashMap<>();
        for (ProductState s : reachable) {
            for (Transition t : c.successors(s)) {
                if (reachable.contains(t.next())) {
                    reversePreds.computeIfAbsent(t.next(), k -> new ArrayList<>()).add(s);
                }
            }
        }
        Set<ProductState> visited = new LinkedHashSet<>(targets);
        Deque<ProductState> queue = new ArrayDeque<>(targets);
        while (!queue.isEmpty()) {
            ProductState s = queue.poll();
            for (ProductState pred : reversePreds.getOrDefault(s, List.of())) {
                if (visited.add(pred)) queue.add(pred);
            }
        }
        return visited;
    }

    /** Follows one arbitrary (deterministic) continuation from {@code from} until it terminates
     *  or a cycle is detected, purely to make the counterexample report a complete run. */
    private static List<FlowElement> extendDoomedRun(ProductLts c, ProductState from) {
        List<FlowElement> tail = new ArrayList<>();
        Set<ProductState> seen = new LinkedHashSet<>();
        ProductState cur = from;
        seen.add(cur);
        while (true) {
            List<Transition> succs = c.successors(cur);
            if (succs.isEmpty()) break;
            Transition next = succs.get(0);
            tail.add(next.fired());
            if (!seen.add(next.next())) break; // cycle guard
            cur = next.next();
        }
        return tail;
    }

    private static List<FlowElement> reconstructTrace(ProductState s0, ProductState target,
            Map<ProductState, ProductState> predecessor, Map<ProductState, FlowElement> predecessorFiring) {
        LinkedList<FlowElement> trace = new LinkedList<>();
        ProductState cur = target;
        while (!cur.equals(s0)) {
            trace.addFirst(predecessorFiring.get(cur));
            cur = predecessor.get(cur);
        }
        return trace;
    }
}
