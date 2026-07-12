package org.vnu.sme.goal.iscn.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import org.antlr.v4.runtime.*;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.conformance.semantics.IStarPropagation;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.GoalTaskElement;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.ParameterRefinement;
import org.vnu.sme.goal.istar.mm.PickRefinement;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Refinement;
import org.vnu.sme.goal.istar.parser.IStarCompiler;
import org.vnu.sme.goal.iscn.goalmodelinstance.parser.GoalModelInstanceEvaluation;
import org.vnu.sme.goal.iscn.goalmodelinstance.parser.GoalModelInstanceTranslator;
import org.vnu.sme.goal.iscn.mm.AggregateResult;
import org.vnu.sme.goal.iscn.goalmodelinstance.mm.GoalModelInstance;
import org.vnu.sme.goal.iscn.mm.IStarScenarioModel;
import org.vnu.sme.goal.iscn.mm.ScenarioInstance;
import org.vnu.sme.goal.iscn.mm.ScenarioStmt;

/**
 * Compiles an {@code .iscn} file through two deliberately separate metamodel layers:
 *
 * <ol>
 *   <li>{@link IStarScenarioModel}: the scenario/script MM parsed from {@code .iscn}
 *       itself -- declared actor instances plus ordered {@code fire}/{@code assign}/{@code aggregate}
 *       statements.</li>
 *   <li>{@link GoalModelInstance}: the instance-level goal-model MM built only after the
 *       referenced {@code .istar} has been compiled into a source {@link GoalModel}.</li>
 * </ol>
 *
 * <p>The per-instance private {@link IStarMarking} map is kept as a debugging/reporting view.
 * The scenario action's main diagram uses {@link Result#goalModelInstance()}.
 *
 * <p>Why keep a marking per instance as report data: a singleton actor (e.g. an HR
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
                          GoalModelInstanceEvaluation evaluation,
                          List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }

        /** MM parsed from the {@code .iscn} file itself. */
        public IStarScenarioModel scenarioModel() { return scenario; }

        /** Instance-level goal MM produced from {@code scenarioModel + model}. */
        public GoalModelInstance goalModelInstance() {
            return evaluation == null ? null : evaluation.goalModelInstance();
        }
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

        GoalModelInstanceEvaluation evaluation = GoalModelInstanceTranslator.evaluate(model, scenario);

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

        applyParameterRefinementAggregation(model, markings, instanceActorType, allInstances);
        broadcastSingletonActorFacts(model, markings, instanceActorType, allInstances);

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

    /**
     * Rolls a forall/pick-quantified child up into its parent, across the declared instances
     * of the quantifying ActorType, and writes the result into the parent's owning actor's own
     * instance(s) -- the piece {@link IStarPropagation#saturate} cannot do on its own, since it
     * only ever sees one flat, per-instance marking and has no notion of "the child's value in
     * every OTHER instance's own trace". Without this, a forall/pick-quantified goal's owning
     * actor (e.g. Initiator, for MeetingAttendedByParticipant -> forall Participant ->
     * MeetingAttended) would never see its own top-level goal (HaveMeetingOrganized) become
     * Fulfilled in its OWN private trace, even once every participant's own trace individually
     * satisfies the per-participant condition -- exactly the gap {@code GoalModelInstanceTranslator}
     * already closes for the SR diagram, just not, until now, for this per-instance report.
     */
    private static void applyParameterRefinementAggregation(GoalModel model, Map<String, IStarMarking> markings,
                                                             Map<String, String> instanceActorType,
                                                             List<String> allInstances) {
        Map<String, List<String>> instancesByType = new LinkedHashMap<>();
        for (String id : allInstances) {
            instancesByType.computeIfAbsent(instanceActorType.get(id), k -> new ArrayList<>()).add(id);
        }

        for (Actor actor : model.getActors()) {
            for (Refinement ref : actor.refinements()) {
                if (!(ref instanceof ParameterRefinement pr)) continue;

                List<String> universe = instancesByType.getOrDefault(pr.actorType(), List.of());
                boolean isForall = pr instanceof ForRefinement;
                boolean aggregate = !universe.isEmpty() && (isForall
                        ? universe.stream().allMatch(id -> markings.get(id).goalTaskStatus(pr.child()) == GoalTaskStatus.FULFILLED)
                        : universe.stream().anyMatch(id -> markings.get(id).goalTaskStatus(pr.child()) == GoalTaskStatus.FULFILLED));

                String ownerType = model.ownerOf(pr.child()).orElse(null);
                for (String ownerInstance : instancesByType.getOrDefault(ownerType, List.of())) {
                    markings.put(ownerInstance, IStarPropagation.assignGoalTask(model, markings.get(ownerInstance),
                            pr.child(), aggregate ? GoalTaskStatus.FULFILLED : GoalTaskStatus.PENDING));
                }
            }
        }
    }

    /**
     * A singleton actor's own facts (including ones only just derived by
     * {@link #applyParameterRefinementAggregation}, e.g. HaveMeetingScheduled) are, in effect,
     * exactly the kind of "fact shared by the whole scenario" {@link #appliesTo} already
     * broadcasts for a literal unqualified fire/assign -- there is only one instance of that
     * actor type, so nothing was ever really "qualified" about them in the first place. Without
     * this, a singleton actor's fully-propagated state (not just its literally fired/assigned
     * leaves) would only ever show up in that one instance's own trace, never in any other
     * declared instance's private trace.
     */
    private static void broadcastSingletonActorFacts(GoalModel model, Map<String, IStarMarking> markings,
                                                      Map<String, String> instanceActorType, List<String> allInstances) {
        Map<String, List<String>> instancesByType = new LinkedHashMap<>();
        for (String id : allInstances) {
            instancesByType.computeIfAbsent(instanceActorType.get(id), k -> new ArrayList<>()).add(id);
        }

        for (var entry : instancesByType.entrySet()) {
            if (entry.getValue().size() != 1) continue;
            String singletonId = entry.getValue().get(0);
            IStarMarking singletonMarking = markings.get(singletonId);

            for (String recipientId : allInstances) {
                if (recipientId.equals(singletonId)) continue;
                IStarMarking recipientMarking = markings.get(recipientId);
                for (var e : singletonMarking.goalTaskStatuses().entrySet()) {
                    if (e.getValue() == GoalTaskStatus.UNKNOWN) continue;
                    recipientMarking = IStarPropagation.assignGoalTask(model, recipientMarking, e.getKey(), e.getValue());
                }
                for (var e : singletonMarking.qualityStatuses().entrySet()) {
                    if (e.getValue() == QualityStatus.UNKNOWN) continue;
                    recipientMarking = IStarPropagation.assignQuality(model, recipientMarking, e.getKey(), e.getValue());
                }
                markings.put(recipientId, recipientMarking);
            }
        }
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

}
