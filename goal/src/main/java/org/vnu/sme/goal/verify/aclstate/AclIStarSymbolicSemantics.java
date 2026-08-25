package org.vnu.sme.goal.verify.aclstate;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.AndRefinement;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.GoalTaskElement;
import org.vnu.sme.goal.dsl.istar.mm.OrRefinement;
import org.vnu.sme.goal.dsl.istar.mm.Refinement;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.verify.aclstate.AclKodkodSymbolicModel.ObjectAtom;

import kodkod.ast.Formula;

/**
 * Symbolic interpretation of an iStar model over a generated ACL state path.
 * iStar owns no independent state space: every marking formula is compiled over
 * the time-indexed ACL frames supplied by {@link AclKodkodSymbolicModel}.
 */
final class AclIStarSymbolicSemantics {
    private final AclKodkodSymbolicModel symbolic;
    private final GoalModel model;
    private final Map<String, Actor> owner = new LinkedHashMap<>();
    private final Map<String, List<String>> children = new LinkedHashMap<>();
    private final Map<String, Boolean> andRefinement = new LinkedHashMap<>();
    private final Set<String> childIds = new LinkedHashSet<>();

    AclIStarSymbolicSemantics(AclKodkodSymbolicModel symbolic, GoalModel model) {
        this.symbolic = symbolic;
        this.model = model;
        index();
        validateActors();
    }

    /** All root Goal instances must be fulfilled; root Tasks are deliberately excluded. */
    Formula rootGoalsSatisfied(int usedFrames) {
        if (usedFrames < 1 || usedFrames > symbolic.frameCount()) {
            throw new IllegalArgumentException("Invalid iStar ACL path length " + usedFrames);
        }
        List<Goal> roots = model.allElements().values().stream()
                .filter(Goal.class::isInstance).map(Goal.class::cast)
                .filter(goal -> !childIds.contains(goal.id())).toList();
        if (roots.isEmpty()) {
            throw new IllegalArgumentException("iStar model has no root Goal; root Tasks are not verdict targets");
        }

        Formula result = Formula.TRUE;
        Map<Actor, List<Goal>> byActor = new IdentityHashMap<>();
        for (Goal root : roots) byActor.computeIfAbsent(owner.get(root.id()), ignored -> new ArrayList<>()).add(root);
        for (var entry : byActor.entrySet()) {
            Actor actor = entry.getKey();
            List<ObjectAtom> instances = symbolic.actorCandidates(actor.name());
            Formula population = Formula.FALSE;
            for (ObjectAtom instance : instances) {
                Formula present = symbolic.exists(symbolic.frame(usedFrames - 1), instance);
                population = population.or(present);
                Formula goals = Formula.TRUE;
                for (Goal root : entry.getValue()) {
                    goals = goals.and(satisfied(root, instance, usedFrames, new LinkedHashSet<>()));
                }
                result = result.and(present.implies(goals));
            }
            result = result.and(population);
        }
        return result;
    }

    List<String> rootGoalLabels() {
        return model.allElements().values().stream()
                .filter(Goal.class::isInstance).map(Goal.class::cast)
                .filter(goal -> !childIds.contains(goal.id()))
                .map(goal -> owner.get(goal.id()).name() + "." + goal.id()).toList();
    }

    private Formula satisfied(GoalTaskElement element, ObjectAtom self, int usedFrames,
                              Set<String> visiting) {
        if (!visiting.add(element.id())) {
            throw new IllegalArgumentException("Cyclic iStar refinement at '" + element.id() + "'");
        }
        try {
            List<String> refinementChildren = children.getOrDefault(element.id(), List.of());
            if (!refinementChildren.isEmpty()) {
                List<Formula> values = new ArrayList<>();
                for (String childId : refinementChildren) {
                    GoalTaskElement child = model.findElement(childId)
                            .filter(GoalTaskElement.class::isInstance)
                            .map(GoalTaskElement.class::cast)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown iStar refinement child '" + childId + "'"));
                    values.add(satisfied(child, self, usedFrames, visiting));
                }
                return andRefinement.getOrDefault(element.id(), true)
                        ? and(values) : or(values);
            }
            if (element instanceof Goal goal) return leafGoal(goal, self, usedFrames);
            return leafTask((Task) element, self, usedFrames);
        } finally {
            visiting.remove(element.id());
        }
    }

    private Formula leafGoal(Goal goal, ObjectAtom self, int usedFrames) {
        if (goal.oclSource() == null || goal.oclSource().isBlank()) return Formula.FALSE;
        List<Formula> values = new ArrayList<>();
        for (int index = 0; index < usedFrames; index++) {
            var current = symbolic.frame(index);
            var previous = symbolic.frame(Math.max(0, index - 1));
            values.add(symbolic.expression(goal.oclSource(), current, previous, self));
        }
        return switch (goal.goalType()) {
            case ACHIEVE -> or(values);
            case MAINTAIN -> and(values);
            case SUSTAIN -> sustain(values);
            case RECUR -> throw new IllegalArgumentException(
                    "RECUR is outside the GoalType semantics formalized for whole validation");
        };
    }

    private Formula leafTask(Task task, ObjectAtom self, int usedFrames) {
        if (task.postconditions().isEmpty()) return Formula.FALSE;
        String pre = task.preconditions().isEmpty() ? null : task.preconditions().get(0).oclBody();
        String post = task.postconditions().get(0).oclBody();
        Formula result = Formula.FALSE;
        for (int start = 0; start < usedFrames; start++) {
            var startFrame = symbolic.frame(start);
            Formula activated = pre == null ? Formula.TRUE
                    : symbolic.expression(pre, startFrame,
                            symbolic.frame(Math.max(0, start - 1)), self);
            for (int finish = start; finish < usedFrames; finish++) {
                var finishFrame = symbolic.frame(finish);
                Formula fulfilled = symbolic.expression(post, finishFrame,
                        symbolic.frame(Math.max(0, finish - 1)), self);
                result = result.or(activated.and(fulfilled));
            }
        }
        return result;
    }

    private static Formula sustain(List<Formula> values) {
        Formula result = Formula.FALSE;
        for (int start = 0; start < values.size(); start++) {
            Formula suffix = Formula.TRUE;
            for (int index = start; index < values.size(); index++) suffix = suffix.and(values.get(index));
            result = result.or(suffix);
        }
        return result;
    }

    private void index() {
        for (Actor actor : model.getActors()) {
            actor.elements().forEach(element -> owner.put(element.id(), actor));
            for (Refinement refinement : actor.refinements()) {
                List<String> refined = switch (refinement) {
                    case AndRefinement and -> and.children();
                    case OrRefinement or -> List.of(or.child());
                };
                children.computeIfAbsent(refinement.parent(), ignored -> new ArrayList<>()).addAll(refined);
                childIds.addAll(refined);
                andRefinement.put(refinement.parent(), refinement instanceof AndRefinement);
            }
        }
        children.replaceAll((ignored, value) -> List.copyOf(value));
    }

    private void validateActors() {
        for (Actor actor : model.getActors()) {
            if (symbolic.actorCandidates(actor.name()).isEmpty()) {
                throw new IllegalArgumentException("iStar actor '" + actor.name()
                        + "' has no ACL classifier/object scope with the same name");
            }
        }
    }

    private static Formula and(List<Formula> formulas) {
        Formula result = Formula.TRUE;
        for (Formula formula : formulas) result = result.and(formula);
        return result;
    }

    private static Formula or(List<Formula> formulas) {
        Formula result = Formula.FALSE;
        for (Formula formula : formulas) result = result.or(formula);
        return result;
    }
}
