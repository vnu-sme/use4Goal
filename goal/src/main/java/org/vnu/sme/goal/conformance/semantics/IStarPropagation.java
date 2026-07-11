package org.vnu.sme.goal.conformance.semantics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.AndRefinement;
import org.vnu.sme.goal.istar.mm.Contribution;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.OrRefinement;
import org.vnu.sme.goal.istar.mm.PickRefinement;
import org.vnu.sme.goal.istar.mm.Refinement;

/**
 * Propagation rules of Fig. 7 (Caballero-Villalobos, Definition 3.5 LTS) — the operational
 * semantics that plays the role JUCS leaves to hand-written OCL pre/postconditions: instead
 * of a snapshot pattern authored per action, satisfaction is derived automatically from the
 * i* model's own Refinement/Contribution structure (see doc/paper/conformance-istar-bpmn2.md,
 * §3.3.1). Firing a leaf Goal/Task saturates AND/OR refinement and Make/Break contribution
 * until no rule applies (fixpoint).
 */
public final class IStarPropagation {

    // Opposing sufficient contributions (make vs. break) to the same quality, both kept
    // FULFILLED by leaf elements that are never un-fired, oscillate forever instead of
    // reaching a fixpoint. Bounded here since IStarScenario now lets a user pick any fire
    // sequence, not just the internally-generated ones the LTS builder used to feed in.
    private static final int MAX_SATURATION_STEPS = 10_000;

    private IStarPropagation() {}

    /** Fires the leaf element {@code firedLeafId} (rule P_leaf) then saturates all other rules. */
    public static IStarMarking fire(GoalModel gm, IStarMarking m, String firedLeafId) {
        IStarMarking next = m.withGoalTask(firedLeafId, GoalTaskStatus.FULFILLED);
        return saturate(gm, next);
    }

    /**
     * Directly sets a Goal/Task's status (no P_leaf restriction to FULFILLED-only) then
     * saturates — the "state as input" counterpart to {@link #fire}'s "state as derived
     * result": for a goal whose real satisfaction comes from a Dependency (e.g. a delegated
     * goal fulfilled once the dependee's dependum completes, not by the depender's own
     * action), the correct move is to assert that value directly rather than fire a
     * substitute leaf action that never really happened.
     */
    public static IStarMarking assignGoalTask(GoalModel gm, IStarMarking m, String id, GoalTaskStatus status) {
        return saturate(gm, m.withGoalTask(id, status));
    }

    /** Directly sets a Quality's status then saturates — see {@link #assignGoalTask}. */
    public static IStarMarking assignQuality(GoalModel gm, IStarMarking m, String id, QualityStatus status) {
        return saturate(gm, m.withQuality(id, status));
    }

    private static IStarMarking saturate(GoalModel gm, IStarMarking m) {
        Map<String, List<String>> andGroups = new LinkedHashMap<>();
        Map<String, Set<String>> orGroups = new LinkedHashMap<>();
        List<Contribution> contributions = new ArrayList<>();

        for (Actor actor : gm.getActors()) {
            for (Refinement r : actor.refinements()) {
                switch (r) {
                    case AndRefinement and -> andGroups
                            .computeIfAbsent(and.parent(), k -> new ArrayList<>())
                            .addAll(and.children());
                    case OrRefinement or -> orGroups
                            .computeIfAbsent(or.parent(), k -> new LinkedHashSet<>())
                            .add(or.child());
                    case ForRefinement forRef -> andGroups
                            .computeIfAbsent(forRef.parent(), k -> new ArrayList<>())
                            .add(forRef.child());
                    case PickRefinement pick -> orGroups
                            .computeIfAbsent(pick.parent(), k -> new LinkedHashSet<>())
                            .add(pick.child());
                }
            }
            contributions.addAll(actor.contributions());
        }

        IStarMarking current = m;
        boolean changed = true;
        int steps = 0;
        while (changed && steps++ < MAX_SATURATION_STEPS) {
            changed = false;

            // P_AND — parent satisfied once every child is fulfilled.
            for (Map.Entry<String, List<String>> e : andGroups.entrySet()) {
                String parent = e.getKey();
                if (current.goalTaskStatus(parent) == GoalTaskStatus.FULFILLED) continue;
                boolean allChildrenFulfilled = true;
                for (String child : e.getValue()) {
                    if (current.goalTaskStatus(child) != GoalTaskStatus.FULFILLED) {
                        allChildrenFulfilled = false;
                        break;
                    }
                }
                if (allChildrenFulfilled) {
                    current = current.withGoalTask(parent, GoalTaskStatus.FULFILLED);
                    changed = true;
                }
            }

            // P_OR — parent satisfied as soon as at least one child is fulfilled.
            for (Map.Entry<String, Set<String>> e : orGroups.entrySet()) {
                String parent = e.getKey();
                if (current.goalTaskStatus(parent) == GoalTaskStatus.FULFILLED) continue;
                boolean anyChildFulfilled = false;
                for (String child : e.getValue()) {
                    if (current.goalTaskStatus(child) == GoalTaskStatus.FULFILLED) {
                        anyChildFulfilled = true;
                        break;
                    }
                }
                if (anyChildFulfilled) {
                    current = current.withGoalTask(parent, GoalTaskStatus.FULFILLED);
                    changed = true;
                }
            }

            // P_Make / P_Break / BP_fulfill / BP_deny.
            for (Contribution c : contributions) {
                if (current.goalTaskStatus(c.element()) != GoalTaskStatus.FULFILLED) continue;

                boolean positive = ContributionPolarity.isSufficientPositive(c.type());
                boolean negative = ContributionPolarity.isSufficientNegative(c.type());
                if (!positive && !negative) continue; // UNKNOWN / non-sufficient: excluded (§3.3.1)

                QualityStatus wanted = positive ? QualityStatus.TRUE : QualityStatus.FALSE;
                if (current.qualityStatus(c.quality()) == wanted) continue;

                current = current.withQuality(c.quality(), wanted);
                changed = true;

                // BP_fulfill / BP_deny: any other FULFILLED contributor of the opposite
                // polarity to the same quality becomes PENDING — its earlier contribution
                // has just been overridden, preventing the quality from flipping back
                // immediately from a contributor that is still (stale-)fulfilled.
                for (Contribution other : contributions) {
                    if (!other.quality().equals(c.quality())) continue;
                    if (other.element().equals(c.element())) continue;
                    if (current.goalTaskStatus(other.element()) != GoalTaskStatus.FULFILLED) continue;

                    boolean otherPositive = ContributionPolarity.isSufficientPositive(other.type());
                    boolean otherNegative = ContributionPolarity.isSufficientNegative(other.type());
                    boolean opposes = (positive && otherNegative) || (negative && otherPositive);
                    if (opposes) {
                        current = current.withGoalTask(other.element(), GoalTaskStatus.PENDING);
                    }
                }
            }
        }
        return current;
    }
}
