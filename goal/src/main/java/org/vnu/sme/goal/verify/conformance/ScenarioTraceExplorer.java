package org.vnu.sme.goal.verify.conformance;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Enumerates all finite XOR/event-based choices observed for one valid input scenario. */
public final class ScenarioTraceExplorer {
    public enum Verdict { NON_CONFORMANT, WEAKLY_CONFORMANT, STRONGLY_CONFORMANT }

    public record Trace(List<Integer> choices, List<String> activities, boolean ended,
                        boolean rootsSatisfied, boolean stable, List<String> failures) {}
    public record Assessment(Verdict verdict, boolean weak, boolean strong, boolean stable,
                             List<Trace> traces) {}

    private ScenarioTraceExplorer() {}

    public static Assessment explore(Path istar, Path bpmn, Path acl, Path soil, int maxTraces) {
        Queue<List<Integer>> pending = new ArrayDeque<>();
        Set<List<Integer>> seen = new HashSet<>();
        List<Trace> traces = new ArrayList<>();
        pending.add(List.of());
        while (!pending.isEmpty() && traces.size() < maxTraces) {
            List<Integer> plan = canonical(pending.remove());
            if (!seen.add(plan)) continue;
            try {
                VisualConformanceSession session = VisualConformanceSession.prepare(
                        istar, bpmn, acl, soil, plan);
                while (session.canAdvance()) session.next();
                boolean ended = session.ended();
                List<String> failures = ended ? session.rootGoalFailures()
                        : List.of("BPMN deadlock before EndEvent");
                boolean stable = stable(session);
                List<String> activities = session.frames().stream().filter(frame -> !frame.initial())
                        .map(frame -> frame.processId() + "." + frame.activity().id()).toList();
                traces.add(new Trace(plan, activities, ended, ended && failures.isEmpty(), stable, failures));
                expandPlans(plan, session.choiceWidths(), pending, seen);
            } catch (Exception ex) {
                traces.add(new Trace(plan, List.of(), false, false, false, List.of(ex.getMessage())));
            }
        }
        boolean weak = traces.stream().anyMatch(Trace::rootsSatisfied);
        boolean strong = !traces.isEmpty() && traces.stream().allMatch(Trace::rootsSatisfied);
        boolean stable = !traces.isEmpty() && traces.stream().allMatch(Trace::stable);
        Verdict verdict = strong && stable ? Verdict.STRONGLY_CONFORMANT
                : weak ? Verdict.WEAKLY_CONFORMANT : Verdict.NON_CONFORMANT;
        return new Assessment(verdict, weak, strong, stable, List.copyOf(traces));
    }

    private static void expandPlans(List<Integer> plan, List<Integer> widths,
            Queue<List<Integer>> pending, Set<List<Integer>> seen) {
        for (int position = 0; position < widths.size(); position++) {
            int selected = position < plan.size() ? plan.get(position) : 0;
            for (int branch = 0; branch < widths.get(position); branch++) {
                if (branch == selected) continue;
                List<Integer> alternative = new ArrayList<>();
                for (int i = 0; i < position; i++) alternative.add(i < plan.size() ? plan.get(i) : 0);
                alternative.add(branch);
                List<Integer> immutable = canonical(alternative);
                if (!seen.contains(immutable)) pending.add(immutable);
            }
        }
    }

    private static List<Integer> canonical(List<Integer> plan) {
        int size = plan.size();
        while (size > 0 && plan.get(size - 1) == 0) size--;
        return List.copyOf(plan.subList(0, size));
    }

    private static boolean stable(VisualConformanceSession session) {
        Set<String> onceFulfilled = new HashSet<>();
        for (var frame : session.frames()) {
            Set<String> nowFailed = new HashSet<>();
            session.rootGoalFailures(frame).forEach(text -> nowFailed.add(text.substring(0, text.indexOf(" = "))));
            for (String id : onceFulfilled) if (nowFailed.contains(id)) return false;
            var inst = org.vnu.sme.goal.trace.usetrace.IStarUseTraceEvaluator.evaluate(
                    session.goalModel(), frame.checkpoint());
            inst.instanceMarking().goalTaskStatuses().forEach((id, status) -> {
                if (status == org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus.FULFILLED) {
                    onceFulfilled.add(id);
                }
            });
        }
        return true;
    }
}
