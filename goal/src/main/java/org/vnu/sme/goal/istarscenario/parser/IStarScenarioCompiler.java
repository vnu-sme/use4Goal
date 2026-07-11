package org.vnu.sme.goal.istarscenario.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.antlr.v4.runtime.*;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.GoalTaskElement;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Obstacle;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Resource;
import org.vnu.sme.goal.istar.mm.Task;
import org.vnu.sme.goal.istar.parser.IStarCompiler;
import org.vnu.sme.goal.istarscenario.mm.AggregateResult;
import org.vnu.sme.goal.istarscenario.mm.IStarScenarioModel;
import org.vnu.sme.goal.istarscenario.mm.ScenarioInstance;
import org.vnu.sme.goal.istarscenario.mm.ScenarioStmt;

/**
 * Pure Java: .iscn file -> AST -> MM -> resolved GoalModel -> one {@link IStarMarking} per
 * declared instance (or one implicit default instance if none is declared), plus any
 * {@code aggregate} checks across them.
 *
 * <p>Why a marking per instance, not one shared marking: a singleton actor (e.g. an HR
 * department) processes each instance of a "many" actor (e.g. each Candidate) separately —
 * one shared {@code ScreenApplication} value could not tell "screened c1" from "screened c2"
 * apart. So each instance gets its own private trace through the *whole* referenced model,
 * reusing {@link IStarPropagation} (Caballero-Villalobos rules, Fig. 7) rather than
 * re-implementing satisfaction propagation.
 */
public final class IStarScenarioCompiler {

    /** Key used for {@link #markings()} when the scenario declares no 'instance' at all. */
    public static final String DEFAULT_INSTANCE = "";

    public record Result(GoalModel model, Path modelFile, IStarScenarioModel scenario,
                          Map<String, IStarMarking> markings, List<AggregateResult> aggregates,
                          IStarScenarioEvaluation evaluation,
                          List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    public static Result compile(Path file) throws IOException {
        List<String> errors = new ArrayList<>();

        IStarScenarioLexer  lexer  = new IStarScenarioLexer(CharStreams.fromPath(file));
        IStarScenarioParser parser = new IStarScenarioParser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        ANTLRErrorListener errListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> rec, Object sym, int line, int col,
                                    String msg, RecognitionException e) {
                errors.add("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.addErrorListener(errListener);
        parser.addErrorListener(errListener);

        IStarScenarioParser.ScenarioContext tree = parser.scenario();
        if (!errors.isEmpty()) return new Result(null, null, null, Map.of(), List.of(), null, errors);

        // AST pipeline: parse tree -> CS (AST) -> MM
        var ast      = IStarScenarioBuildingVisitor.build(tree);
        var scenario = IStarScenarioModelFactory.build(ast);

        Path modelFile = file.toAbsolutePath().getParent().resolve(scenario.modelFile()).normalize();
        IStarCompiler.Result modelResult;
        try {
            modelResult = IStarCompiler.compile(modelFile);
        } catch (IOException ex) {
            errors.add("cannot read model file '" + modelFile + "': " + ex.getMessage());
            return new Result(null, modelFile, scenario, Map.of(), List.of(), null, errors);
        }
        if (!modelResult.ok()) {
            errors.add("errors in referenced model '" + modelFile + "':");
            errors.addAll(modelResult.errors());
            return new Result(null, modelFile, scenario, Map.of(), List.of(), null, errors);
        }
        GoalModel model = modelResult.model();

        Set<String> instanceIds = new LinkedHashSet<>();
        Map<String, String> instanceActorType = new LinkedHashMap<>();
        for (ScenarioInstance instance : scenario.instances()) {
            if (model.findActor(instance.actorType()).isEmpty()) {
                errors.add("semantic: instance '" + instance.name() + "' : '" + instance.actorType()
                        + "' — no such actor in '" + modelFile + "'");
            }
            if (!instanceIds.add(instance.name())) {
                errors.add("semantic: instance '" + instance.name() + "' declared more than once");
            }
            instanceActorType.put(instance.name(), instance.actorType());
        }
        boolean multiInstance = !instanceIds.isEmpty();
        List<String> allInstances = multiInstance ? List.copyOf(instanceIds) : List.of(DEFAULT_INSTANCE);

        for (ScenarioStmt stmt : scenario.statements()) {
            switch (stmt) {
                case ScenarioStmt.Fire f -> {
                    validateQualifiedTarget(f.instanceId(), f.elementId(), "fire", model, modelFile,
                            multiInstance, instanceIds, errors);
                    validateFireObject(f, multiInstance, instanceIds, errors);
                }
                case ScenarioStmt.Assign a -> {
                    validateQualifiedTarget(a.instanceId(), a.elementId(), "assign", model, modelFile,
                            multiInstance, instanceIds, errors);
                    validateAssignValue(a, model, errors);
                }
                case ScenarioStmt.Aggregate a -> validateAggregate(a, model, modelFile, instanceActorType, errors);
            }
        }
        if (!errors.isEmpty()) return new Result(model, modelFile, scenario, Map.of(), List.of(), null, errors);

        Map<String, IStarMarking> markings = new LinkedHashMap<>();
        for (String instanceId : allInstances) {
            IStarMarking marking = IStarMarking.initial(model);
            for (ScenarioStmt stmt : scenario.statements()) {
                switch (stmt) {
                    case ScenarioStmt.Fire f -> {
                        if (appliesTo(f.instanceId(), instanceId, multiInstance)) {
                            marking = IStarPropagation.fire(model, marking, f.elementId());
                        }
                    }
                    case ScenarioStmt.Assign a -> {
                        if (appliesTo(a.instanceId(), instanceId, multiInstance)) {
                            marking = applyAssign(model, marking, a);
                        }
                    }
                    case ScenarioStmt.Aggregate ignored -> { /* evaluated after all markings settle */ }
                }
            }
            markings.put(instanceId, marking);
        }

        List<AggregateResult> aggregates = new ArrayList<>();
        for (ScenarioStmt stmt : scenario.statements()) {
            if (stmt instanceof ScenarioStmt.Aggregate a) {
                List<String> universe = a.actorType() == null ? allInstances
                        : allInstances.stream().filter(id -> a.actorType().equals(instanceActorType.get(id))).toList();
                aggregates.add(evaluateAggregate(a, model, markings, universe));
            }
        }

        IStarScenarioEvaluation evaluation = IStarScenarioEvaluator.evaluate(model, scenario);
        return new Result(model, modelFile, scenario, markings, evaluation.aggregates(), evaluation, Collections.emptyList());
    }

    /**
     * Unqualified fires/assigns broadcast to every instance's private trace once 'instance' is
     * declared — they represent facts shared by the whole scenario (e.g. a singleton
     * Organizer's own actions), not something specific to one instance.
     */
    private static boolean appliesTo(String targetInstanceId, String instanceId, boolean multiInstance) {
        if (targetInstanceId == null) return true;
        return multiInstance && instanceId.equals(targetInstanceId);
    }

    private static void validateQualifiedTarget(String targetInstanceId, String elementId, String verb,
                                                GoalModel model, Path modelFile, boolean multiInstance,
                                                Set<String> instanceIds, List<String> errors) {
        if (multiInstance && targetInstanceId != null && !instanceIds.contains(targetInstanceId)) {
            errors.add("semantic: " + verb + " '" + targetInstanceId + "." + elementId + "' — no such instance declared");
        } else if (!multiInstance && targetInstanceId != null) {
            errors.add("semantic: " + verb + " '" + targetInstanceId + "." + elementId
                    + "' qualifies an instance, but none is declared");
        }

        Optional<IntentionalElement> elem = model.findElement(elementId);
        if (elem.isEmpty()) {
            errors.add("semantic: " + verb + " '" + elementId + "' — no such element in '" + modelFile + "'");
        } else if (verb.equals("fire") && !(elem.get() instanceof GoalTaskElement)) {
            errors.add("semantic: fire '" + elementId + "' — only Goal/Task can be fired (rule P_leaf), found "
                    + elem.get().getClass().getSimpleName());
        }
    }

    private static void validateAssignValue(ScenarioStmt.Assign a, GoalModel model, List<String> errors) {
        Optional<IntentionalElement> elem = model.findElement(a.elementId());
        if (elem.isEmpty()) return; // already reported by validateQualifiedTarget
        boolean isQuality = elem.get() instanceof Quality;
        boolean isGoalTask = elem.get() instanceof GoalTaskElement;
        boolean valueIsBoolean = a.statusValue().equals("True") || a.statusValue().equals("False");
        boolean valueIsGoalTask = a.statusValue().equals("Fulfilled") || a.statusValue().equals("Pending");

        if (isQuality && !valueIsBoolean) {
            errors.add("semantic: assign '" + a.elementId() + "' = " + a.statusValue()
                    + " — a Quality can only be assigned True/False");
        } else if (isGoalTask && !valueIsGoalTask) {
            errors.add("semantic: assign '" + a.elementId() + "' = " + a.statusValue()
                    + " — a Goal/Task can only be assigned Fulfilled/Pending");
        } else if (!isQuality && !isGoalTask) {
            errors.add("semantic: assign '" + a.elementId()
                    + "' — target must be a Goal/Task or Quality, found " + elem.get().getClass().getSimpleName());
        }
    }

    private static void validateFireObject(ScenarioStmt.Fire f, boolean multiInstance,
                                           Set<String> instanceIds, List<String> errors) {
        if (f.objectInstanceId() == null) return;
        if (f.instanceId() == null) {
            errors.add("semantic: fire '" + f.elementId() + " for " + f.objectInstanceId()
                    + "' — paired fire must qualify the actor instance that performs the task");
        }
        if (multiInstance && !instanceIds.contains(f.objectInstanceId())) {
            errors.add("semantic: fire '" + f.elementId() + " for " + f.objectInstanceId()
                    + "' — no such object instance declared");
        } else if (!multiInstance) {
            errors.add("semantic: fire '" + f.elementId() + " for " + f.objectInstanceId()
                    + "' qualifies an object instance, but none is declared");
        }
    }

    private static IStarMarking applyAssign(GoalModel model, IStarMarking marking, ScenarioStmt.Assign a) {
        return switch (a.statusValue()) {
            case "Fulfilled" -> IStarPropagation.assignGoalTask(model, marking, a.elementId(), GoalTaskStatus.FULFILLED);
            case "Pending"   -> IStarPropagation.assignGoalTask(model, marking, a.elementId(), GoalTaskStatus.PENDING);
            case "True"      -> IStarPropagation.assignQuality(model, marking, a.elementId(), QualityStatus.TRUE);
            case "False"     -> IStarPropagation.assignQuality(model, marking, a.elementId(), QualityStatus.FALSE);
            default -> throw new IllegalStateException("Unknown status value: " + a.statusValue());
        };
    }

    private static void validateAggregate(ScenarioStmt.Aggregate a, GoalModel model, Path modelFile,
                                          Map<String, String> instanceActorType, List<String> errors) {
        Optional<IntentionalElement> elem = model.findElement(a.elementId());
        if (elem.isEmpty()) {
            errors.add("semantic: aggregate '" + a.label() + "' over '" + a.elementId()
                    + "' — no such element in '" + modelFile + "'");
        } else if (!(elem.get() instanceof GoalTaskElement) && !(elem.get() instanceof Quality)) {
            errors.add("semantic: aggregate '" + a.label() + "' over '" + a.elementId()
                    + "' — target must be a Goal/Task or Quality, found " + elem.get().getClass().getSimpleName());
        }

        if (a.actorType() != null && !instanceActorType.containsValue(a.actorType())) {
            errors.add("semantic: aggregate '" + a.label() + "' of '" + a.actorType()
                    + "' — no instance of that actor type is declared");
        }
    }

    private static AggregateResult evaluateAggregate(ScenarioStmt.Aggregate a, GoalModel model,
                                                      Map<String, IStarMarking> markings, List<String> allInstances) {
        IntentionalElement elem = model.findElement(a.elementId()).orElseThrow();
        List<String> satisfied = new ArrayList<>();
        for (String instanceId : allInstances) {
            if (isPositive(elem, markings.get(instanceId))) satisfied.add(instanceId);
        }
        boolean holds = switch (a.mode()) {
            case ALL -> satisfied.size() == allInstances.size();
            case ANY -> !satisfied.isEmpty();
        };
        return new AggregateResult(a.label(), a.mode(), a.elementId(), holds, satisfied, allInstances);
    }

    private static boolean isPositive(IntentionalElement elem, IStarMarking marking) {
        return switch (elem) {
            case Goal g -> marking.goalTaskStatus(g.id()) == GoalTaskStatus.FULFILLED;
            case Task t -> marking.goalTaskStatus(t.id()) == GoalTaskStatus.FULFILLED;
            case Quality q -> marking.qualityStatus(q.id()) == QualityStatus.TRUE;
            case Resource r -> false;
            case Obstacle o -> false;
        };
    }
}
