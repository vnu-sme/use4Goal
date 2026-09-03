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
import kodkod.engine.Evaluator;
import kodkod.engine.Solution;

/**
 * Symbolic interpretation of an iStar model over a generated ACL state path.
 * iStar owns no independent state space: every marking formula is compiled over
 * the time-indexed ACL frames supplied by {@link AclKodkodSymbolicModel}.
 */
final class AclIStarSymbolicSemantics {
    enum MarkingValue { SATISFIED, UNKNOWN, VIOLATED }

    record GoalEvaluation(String label, boolean root, MarkingValue value, String condition) {}

    private record MarkingFormula(Formula satisfied, Formula violated) {
        Formula unknown() { return satisfied.not().and(violated.not()); }
    }

    private record LabelledFormula(String label, Formula satisfied, Formula violated) {}

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
        Formula result = Formula.TRUE;
        for (LabelledFormula root : rootFormulas(usedFrames)) result = result.and(root.satisfied());
        return result;
    }

    /** At least one applicable root Goal is explicitly violated in the final marking. */
    Formula rootGoalsViolated(int usedFrames) {
        Formula result = Formula.FALSE;
        for (LabelledFormula root : rootFormulas(usedFrames)) result = result.or(root.violated());
        return result;
    }

    List<GoalEvaluation> evaluateGoals(Solution solution, int usedFrames) {
        validateFrames(usedFrames);
        Evaluator evaluator = new Evaluator(solution.instance());
        List<GoalEvaluation> result = new ArrayList<>();
        for (Actor actor : model.getActors()) {
            for (Goal goal : actor.elements().stream().filter(Goal.class::isInstance)
                    .map(Goal.class::cast).toList()) {
                LabelledFormula value = formula(actor, goal, usedFrames);
                MarkingValue marking = evaluator.evaluate(value.satisfied())
                        ? MarkingValue.SATISFIED
                        : evaluator.evaluate(value.violated())
                                ? MarkingValue.VIOLATED : MarkingValue.UNKNOWN;
                result.add(new GoalEvaluation(value.label(), !childIds.contains(goal.id()),
                        marking, goal.oclSource()));
            }
        }
        return List.copyOf(result);
    }

    List<String> rootGoalLabels() {
        return model.allElements().values().stream()
                .filter(Goal.class::isInstance).map(Goal.class::cast)
                .filter(goal -> !childIds.contains(goal.id()))
                .map(goal -> owner.get(goal.id()).name() + "." + goal.id()).toList();
    }

    private MarkingFormula marking(GoalTaskElement element, ObjectAtom self, int usedFrames,
                                   Set<String> visiting) {
        if (!visiting.add(element.id())) {
            throw new IllegalArgumentException("Cyclic iStar refinement at '" + element.id() + "'");
        }
        try {
            List<String> refinementChildren = children.getOrDefault(element.id(), List.of());
            boolean hasDirectGoalCondition = element instanceof Goal goal
                    && goal.oclSource() != null && !goal.oclSource().isBlank();
            if (!refinementChildren.isEmpty() && !hasDirectGoalCondition) {
                List<MarkingFormula> values = new ArrayList<>();
                for (String childId : refinementChildren) {
                    GoalTaskElement child = model.findElement(childId)
                            .filter(GoalTaskElement.class::isInstance)
                            .map(GoalTaskElement.class::cast)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown iStar refinement child '" + childId + "'"));
                    values.add(marking(child, self, usedFrames, visiting));
                }
                if (andRefinement.getOrDefault(element.id(), true)) {
                    return new MarkingFormula(andMarkings(values), anyViolated(values));
                }
                return new MarkingFormula(anySatisfied(values), allViolated(values));
            }
            if (element instanceof Goal goal) return leafGoal(goal, self, usedFrames);
            return leafTask((Task) element, self, usedFrames);
        } finally {
            visiting.remove(element.id());
        }
    }

    private MarkingFormula leafGoal(Goal goal, ObjectAtom self, int usedFrames) {
        if (goal.oclSource() == null || goal.oclSource().isBlank()) {
            return new MarkingFormula(Formula.FALSE, Formula.FALSE);
        }
        List<Formula> values = new ArrayList<>();
        for (int index = 0; index < usedFrames; index++) {
            var current = symbolic.frame(index);
            var previous = symbolic.frame(Math.max(0, index - 1));
            values.add(symbolic.expression(goal.oclSource(), current, previous, self));
        }
        return switch (goal.goalType()) {
            case ACHIEVE -> new MarkingFormula(or(values), Formula.FALSE);
            case MAINTAIN -> new MarkingFormula(and(values), and(values).not());
            case SUSTAIN -> new MarkingFormula(sustainSatisfied(values), sustainViolated(values));
        };
    }

    private MarkingFormula leafTask(Task task, ObjectAtom self, int usedFrames) {
        if (task.postconditions().isEmpty()) {
            return new MarkingFormula(Formula.FALSE, Formula.FALSE);
        }
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
        return new MarkingFormula(result, Formula.FALSE);
    }

    /** False* True+: the first achievement must be preserved through the final frame. */
    private static Formula sustainSatisfied(List<Formula> values) {
        Formula result = Formula.FALSE;
        for (int start = 0; start < values.size(); start++) {
            Formula candidate = Formula.TRUE;
            for (int index = 0; index < start; index++) candidate = candidate.and(values.get(index).not());
            for (int index = start; index < values.size(); index++) candidate = candidate.and(values.get(index));
            result = result.or(candidate);
        }
        return result;
    }

    /** A Sustain Goal is violated once a true condition is followed by a false condition. */
    private static Formula sustainViolated(List<Formula> values) {
        Formula result = Formula.FALSE;
        Formula previouslyTrue = Formula.FALSE;
        for (Formula value : values) {
            result = result.or(previouslyTrue.and(value.not()));
            previouslyTrue = previouslyTrue.or(value);
        }
        return result;
    }

    private List<LabelledFormula> rootFormulas(int usedFrames) {
        validateFrames(usedFrames);
        List<Goal> roots = model.allElements().values().stream()
                .filter(Goal.class::isInstance).map(Goal.class::cast)
                .filter(goal -> !childIds.contains(goal.id())).toList();
        if (roots.isEmpty()) {
            throw new IllegalArgumentException("iStar model has no root Goal; root Tasks are not verdict targets");
        }
        List<LabelledFormula> result = new ArrayList<>();
        Map<Actor, List<Goal>> byActor = new IdentityHashMap<>();
        for (Goal root : roots) byActor.computeIfAbsent(owner.get(root.id()), ignored -> new ArrayList<>()).add(root);
        for (var entry : byActor.entrySet()) {
            for (Goal root : entry.getValue()) result.add(formula(entry.getKey(), root, usedFrames));
        }
        return List.copyOf(result);
    }

    private LabelledFormula formula(Actor actor, Goal goal, int usedFrames) {
        Formula population = Formula.FALSE;
        Formula satisfied = Formula.TRUE;
        Formula violated = Formula.FALSE;
        for (ObjectAtom instance : symbolic.actorCandidates(actor.name())) {
            Formula present = symbolic.exists(symbolic.frame(usedFrames - 1), instance);
            population = population.or(present);
            MarkingFormula value = marking(goal, instance, usedFrames, new LinkedHashSet<>());
            satisfied = satisfied.and(present.implies(value.satisfied()));
            violated = violated.or(present.and(value.violated()));
        }
        return new LabelledFormula(actor.name() + "." + goal.id(),
                population.and(satisfied), violated);
    }

    private void validateFrames(int usedFrames) {
        if (usedFrames < 1 || usedFrames > symbolic.frameCount()) {
            throw new IllegalArgumentException("Invalid iStar ACL path length " + usedFrames);
        }
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

    private static Formula andMarkings(List<MarkingFormula> markings) {
        Formula result = Formula.TRUE;
        for (MarkingFormula marking : markings) result = result.and(marking.satisfied());
        return result;
    }

    private static Formula anySatisfied(List<MarkingFormula> markings) {
        Formula result = Formula.FALSE;
        for (MarkingFormula marking : markings) result = result.or(marking.satisfied());
        return result;
    }

    private static Formula anyViolated(List<MarkingFormula> markings) {
        Formula result = Formula.FALSE;
        for (MarkingFormula marking : markings) result = result.or(marking.violated());
        return result;
    }

    private static Formula allViolated(List<MarkingFormula> markings) {
        Formula result = Formula.TRUE;
        for (MarkingFormula marking : markings) result = result.and(marking.violated());
        return result;
    }
}
