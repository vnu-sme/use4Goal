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
import org.vnu.sme.goal.dsl.istar.mm.GoalType;
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
    private final ObjectAtom targetSelf;
    private final Map<String, Actor> owner = new LinkedHashMap<>();
    private final Map<String, List<String>> children = new LinkedHashMap<>();
    private final Map<String, Boolean> andRefinement = new LinkedHashMap<>();
    private final Set<String> childIds = new LinkedHashSet<>();

    AclIStarSymbolicSemantics(AclKodkodSymbolicModel symbolic, GoalModel model) {
        this(symbolic, model, null);
    }

    AclIStarSymbolicSemantics(AclKodkodSymbolicModel symbolic, GoalModel model, ObjectAtom targetSelf) {
        this.symbolic = symbolic;
        this.model = model;
        this.targetSelf = targetSelf;
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
            for (GoalTaskElement element : actor.elements().stream()
                    .filter(GoalTaskElement.class::isInstance)
                    .map(GoalTaskElement.class::cast).toList()) {
                LabelledFormula value = formula(actor, element, usedFrames);
                MarkingValue marking = evaluator.evaluate(value.satisfied())
                        ? MarkingValue.SATISFIED
                        : evaluator.evaluate(value.violated())
                                ? MarkingValue.VIOLATED : MarkingValue.UNKNOWN;
                String cond = element.oclSource();
                if (element instanceof Task task && cond == null) {
                    String preStr = task.preconditions().isEmpty() ? null : task.preconditions().get(0).oclBody();
                    String postStr = task.postconditions().isEmpty() ? null : task.postconditions().get(0).oclBody();
                    cond = (preStr == null ? "" : "pre: " + preStr + " ") + (postStr == null ? "" : "post: " + postStr);
                }
                result.add(new GoalEvaluation(value.label(), !childIds.contains(element.id()),
                        marking, cond));
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
            if (!refinementChildren.isEmpty()) {
                // Sustain compound: apply sustainSatisfied/sustainViolated on per-frame
                // instantaneous AND/OR of children. The Sustain type is only meaningful
                // when children are evaluated tức-thời (instantaneously) at each frame;
                // the parent detects when all children are SIMULTANEOUSLY true and
                // whether that truth is subsequently maintained.
                boolean isSustainCompound = element instanceof Goal g
                        && g.goalType() == GoalType.SUSTAIN;
                if (isSustainCompound) {
                    boolean isAnd = andRefinement.getOrDefault(element.id(), true);
                    List<Formula> perFrameValues = new ArrayList<>();
                    for (int i = 0; i < usedFrames; i++) {
                        Formula frameVal = isAnd ? Formula.TRUE : Formula.FALSE;
                        for (String childId : refinementChildren) {
                            GoalTaskElement child = model.findElement(childId)
                                    .filter(GoalTaskElement.class::isInstance)
                                    .map(GoalTaskElement.class::cast)
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Unknown iStar refinement child '" + childId + "'"));
                            Formula childInstant = instantAtFrame(child, self, i, usedFrames,
                                    new LinkedHashSet<>(visiting));
                            if (isAnd) frameVal = frameVal.and(childInstant);
                            else       frameVal = frameVal.or(childInstant);
                        }
                        if (hasDirectGoalCondition) {
                            var current  = symbolic.frame(i);
                            var previous = symbolic.frame(Math.max(0, i - 1));
                            Formula directCond = symbolic.expression(
                                    ((Goal) element).oclSource(), current, previous, self);
                            frameVal = frameVal.and(directCond);
                        }
                        perFrameValues.add(frameVal);
                    }
                    return new MarkingFormula(sustainSatisfied(perFrameValues),
                            sustainViolated(perFrameValues));
                }

                List<MarkingFormula> values = new ArrayList<>();
                for (String childId : refinementChildren) {
                    GoalTaskElement child = model.findElement(childId)
                            .filter(GoalTaskElement.class::isInstance)
                            .map(GoalTaskElement.class::cast)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Unknown iStar refinement child '" + childId + "'"));
                    values.add(marking(child, self, usedFrames, visiting));
                }
                MarkingFormula refined = andRefinement.getOrDefault(element.id(), true)
                        ? new MarkingFormula(andMarkings(values), anyViolated(values))
                        : new MarkingFormula(anySatisfied(values), allViolated(values));
                if (hasDirectGoalCondition) {
                    MarkingFormula direct = leafGoal((Goal) element, self, usedFrames);
                    return new MarkingFormula(
                            direct.satisfied().and(refined.satisfied()),
                            direct.violated().or(refined.violated()));
                }
                return refined;
            }
            if (element instanceof Goal goal) return leafGoal(goal, self, usedFrames);
            return leafTask((Task) element, self, usedFrames);
        } finally {
            visiting.remove(element.id());
        }
    }

    /**
     * Instantaneous truth of {@code element} at a specific {@code frameIndex}.
     *
     * <ul>
     *   <li><b>Achieve</b> goal / compound: latch – OR of truths from frame 0 to frameIndex.</li>
     *   <li><b>None / Maintain</b> goal: condition evaluated exactly at frameIndex (no latch).</li>
     *   <li><b>Sustain</b> goal (compound): per-frame AND/OR of children (recursive).</li>
     *   <li><b>Task</b>: latch – done at some point up to frameIndex (pre observed, then post).</li>
     * </ul>
     */
    private Formula instantAtFrame(GoalTaskElement element, ObjectAtom self,
                                   int frameIndex, int usedFrames, Set<String> visiting) {
        if (!visiting.add(element.id())) {
            throw new IllegalArgumentException("Cyclic iStar refinement at '" + element.id() + "'");
        }
        try {
            List<String> refinementChildren = children.getOrDefault(element.id(), List.of());
            boolean isAnd = andRefinement.getOrDefault(element.id(), true);
            GoalType type = element instanceof Goal g
                    ? (g.goalType() == null ? GoalType.NONE : g.goalType())
                    : GoalType.ACHIEVE; // tasks: latch semantics

            if (!refinementChildren.isEmpty()) {
                if (type == GoalType.ACHIEVE) {
                    // Latch compound: was the compound AND/OR ever true up to frameIndex?
                    Formula latched = Formula.FALSE;
                    for (int j = 0; j <= frameIndex; j++) {
                        Formula frameJ = isAnd ? Formula.TRUE : Formula.FALSE;
                        for (String childId : refinementChildren) {
                            GoalTaskElement child = model.findElement(childId)
                                    .filter(GoalTaskElement.class::isInstance)
                                    .map(GoalTaskElement.class::cast)
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Unknown iStar child '" + childId + "'"));
                            Formula childJ = instantAtFrame(child, self, j, usedFrames,
                                    new LinkedHashSet<>(visiting));
                            if (isAnd) frameJ = frameJ.and(childJ);
                            else       frameJ = frameJ.or(childJ);
                        }
                        latched = latched.or(frameJ);
                    }
                    return latched;
                } else {
                    // None / Sustain / Maintain compound: instantaneous at frameIndex
                    Formula frameVal = isAnd ? Formula.TRUE : Formula.FALSE;
                    for (String childId : refinementChildren) {
                        GoalTaskElement child = model.findElement(childId)
                                .filter(GoalTaskElement.class::isInstance)
                                .map(GoalTaskElement.class::cast)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Unknown iStar child '" + childId + "'"));
                        Formula childInstant = instantAtFrame(child, self, frameIndex, usedFrames, visiting);
                        if (isAnd) frameVal = frameVal.and(childInstant);
                        else       frameVal = frameVal.or(childInstant);
                    }
                    return frameVal;
                }
            }

            // Leaf goal
            if (element instanceof Goal goal) {
                if (goal.oclSource() == null || goal.oclSource().isBlank()) return Formula.FALSE;
                GoalType goalType = goal.goalType() == null ? GoalType.NONE : goal.goalType();
                if (goalType == GoalType.ACHIEVE) {
                    // Latch: OR of conditions from frame 0..frameIndex
                    Formula latched = Formula.FALSE;
                    for (int j = 0; j <= frameIndex; j++) {
                        var c = symbolic.frame(j);
                        var p = symbolic.frame(Math.max(0, j - 1));
                        latched = latched.or(symbolic.expression(goal.oclSource(), c, p, self));
                    }
                    return latched;
                } else {
                    // NONE / MAINTAIN / SUSTAIN: instantaneous condition at frameIndex
                    var current  = symbolic.frame(frameIndex);
                    var previous = symbolic.frame(Math.max(0, frameIndex - 1));
                    return symbolic.expression(goal.oclSource(), current, previous, self);
                }
            }

            // Task: latch — completed at some point up to frameIndex
            Task task = (Task) element;
            String pre  = task.preconditions().isEmpty()  ? null : task.preconditions().get(0).oclBody();
            String post = task.postconditions().isEmpty() ? null : task.postconditions().get(0).oclBody();
            if (pre == null && post == null) return Formula.FALSE;
            Formula done = Formula.FALSE;
            for (int start = 0; start <= frameIndex; start++) {
                var sf = symbolic.frame(start);
                var sp = symbolic.frame(Math.max(0, start - 1));
                Formula preTrue = pre == null ? Formula.TRUE
                        : symbolic.expression(pre, sf, sp, self);
                if (post == null) {
                    done = done.or(preTrue);
                } else {
                    Formula postTrue = Formula.FALSE;
                    for (int finish = start; finish <= frameIndex; finish++) {
                        var ff = symbolic.frame(finish);
                        var fp = symbolic.frame(Math.max(0, finish - 1));
                        postTrue = postTrue.or(symbolic.expression(post, ff, fp, self));
                    }
                    done = done.or(preTrue.and(postTrue));
                }
            }
            return done;
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
        GoalType type = goal.goalType() == null ? GoalType.NONE : goal.goalType();
        return switch (type) {
            case ACHIEVE, NONE -> new MarkingFormula(or(values), Formula.FALSE);
            case MAINTAIN -> new MarkingFormula(and(values), and(values).not());
            case SUSTAIN -> new MarkingFormula(sustainSatisfied(values), sustainViolated(values));
        };
    }

    private MarkingFormula leafTask(Task task, ObjectAtom self, int usedFrames) {
        if (task.postconditions().isEmpty() && task.preconditions().isEmpty()) {
            return new MarkingFormula(Formula.FALSE, Formula.FALSE);
        }
        String pre = task.preconditions().isEmpty() ? null : task.preconditions().get(0).oclBody();
        String post = task.postconditions().isEmpty() ? null : task.postconditions().get(0).oclBody();

        Formula activatedAny = pre == null ? Formula.TRUE : Formula.FALSE;
        Formula satisfied = Formula.FALSE;

        for (int start = 0; start < usedFrames; start++) {
            var startFrame = symbolic.frame(start);
            Formula preTrue = pre == null ? Formula.TRUE
                    : symbolic.expression(pre, startFrame,
                            symbolic.frame(Math.max(0, start - 1)), self);
            if (pre != null) {
                activatedAny = activatedAny.or(preTrue);
            }

            if (post == null) {
                satisfied = satisfied.or(preTrue);
            } else {
                Formula postTrueAfterPre = Formula.FALSE;
                for (int finish = start; finish < usedFrames; finish++) {
                    var finishFrame = symbolic.frame(finish);
                    Formula postTrue = symbolic.expression(post, finishFrame,
                            symbolic.frame(Math.max(0, finish - 1)), self);
                    postTrueAfterPre = postTrueAfterPre.or(postTrue);
                }
                satisfied = satisfied.or(preTrue.and(postTrueAfterPre));
            }
        }

        Formula violated = activatedAny.and(satisfied.not());
        return new MarkingFormula(satisfied, violated);
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

    private LabelledFormula formula(Actor actor, GoalTaskElement element, int usedFrames) {
        Formula population = Formula.FALSE;
        Formula satisfied = Formula.TRUE;
        Formula violated = Formula.FALSE;
        List<ObjectAtom> candidates = targetSelf != null && actor.name().equals(targetSelf.concreteType())
                ? List.of(targetSelf)
                : symbolic.actorCandidates(actor.name());
        for (ObjectAtom instance : candidates) {
            Formula present = symbolic.exists(symbolic.frame(usedFrames - 1), instance);
            if (targetSelf != null && !actor.name().equals(targetSelf.concreteType())) {
                var rel = symbolic.frame(usedFrames - 1).membership(targetSelf.concreteType(), actor.name());
                if (rel != null) {
                    present = present.and(targetSelf.singleton().product(instance.singleton()).in(rel));
                }
            }
            population = population.or(present);
            MarkingFormula value = marking(element, instance, usedFrames, new LinkedHashSet<>());
            satisfied = satisfied.and(present.implies(value.satisfied()));
            violated = violated.or(present.and(value.violated()));
        }
        return new LabelledFormula(actor.name() + "." + element.id(),
                population.and(satisfied), violated);
    }

    private void validateFrames(int usedFrames) {
        if (usedFrames < 1 || usedFrames > symbolic.frameCount()) {
            throw new IllegalArgumentException("Invalid iStar CSL path length " + usedFrames);
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
        // iStar 2.0 dependency semantics:
        // When the dependee element satisfies its obligation, the depender element is also satisfied.
        // Wire: dependerElmt --OR-> dependeeElmt (so that if dependee satisfies, depender is satisfied too)
        for (var dep : model.getDependencies()) {
            String dependerElmt = dep.dependerElmt();
            String dependeeElmt = dep.dependeeElmt();
            if (dependerElmt != null && dependeeElmt != null
                    && model.findElement(dependerElmt).isPresent()
                    && model.findElement(dependeeElmt).isPresent()) {
                // Only add if dependerElmt doesn't already have AND-refinement children
                // (i.e., it's currently a leaf — no explicit refinement defined by the author)
                if (!children.containsKey(dependerElmt)) {
                    children.computeIfAbsent(dependerElmt, ignored -> new ArrayList<>()).add(dependeeElmt);
                    // OR-refinement: if dependee succeeds, depender is satisfied
                    andRefinement.put(dependerElmt, false);
                    // dependeeElmt is a child in this dependency-derived tree,
                    // but it is NOT a structural iStar child (don't add to childIds)
                    // so that it can still appear as a root in its own actor if applicable
                }
            }
        }
        children.replaceAll((ignored, value) -> List.copyOf(value));
    }


    private void validateActors() {
        for (Actor actor : model.getActors()) {
            if (symbolic.actorCandidates(actor.name()).isEmpty()) {
                throw new IllegalArgumentException("iStar actor '" + actor.name()
                        + "' has no CSL classifier/object scope with the same name");
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
