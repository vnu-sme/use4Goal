package org.vnu.sme.goal.conformance;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MSystemState;
import org.vnu.sme.goal.acl.parser.AclCompiler;
import org.vnu.sme.goal.acl.use.AclUseTranslator;
import org.vnu.sme.goal.bpmn2.execution.Bpmn2ExecutionEngine;
import org.vnu.sme.goal.bpmn2.mm.Activity;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.mm.FlowElement;
import org.vnu.sme.goal.bpmn2.parser.Bpmn2Compiler;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.AndRefinement;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.OrRefinement;
import org.vnu.sme.goal.istar.mm.PickRefinement;
import org.vnu.sme.goal.istar.mm.Refinement;
import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceEvaluator;

/**
 * Executes one BPMN process over a SOIL input scenario. SOIL is initialization
 * only; the trace is derived by token routing and generic BPMN effects.
 */
public final class VisualConformanceSession {
    public record Frame(int number, String processId, Activity activity, Checkpoint checkpoint,
                        Map<String, Bpmn2ExecutionEngine.Snapshot> bpmnSnapshots,
                        List<String> stateDelta, List<String> goalDelta) {
        public boolean initial() { return activity == null; }
    }

    private final Path istarFile;
    private final Path useFile;
    private final Path executionFile;
    private final String initialSoil;
    private final GoalModel goalModel;
    private MModel useModel;
    private final Bpmn2Model bpmnModel;
    private final Map<String, org.vnu.sme.goal.bpmn2.mm.Process> processes = new LinkedHashMap<>();
    private final Map<String, Bpmn2ExecutionEngine> engines = new LinkedHashMap<>();
    private final List<Frame> frames = new ArrayList<>();
    private String effects = "";
    private Checkpoint checkpoint;
    private final List<Integer> choicePlan;
    private final List<Integer> choiceWidths = new ArrayList<>();
    private int choiceCursor;

    private VisualConformanceSession(Path istarFile, Path useFile, Path executionFile,
            String initialSoil, IStarUseTraceCompiler.Result initialTrace, Bpmn2Model bpmnModel,
            List<org.vnu.sme.goal.bpmn2.mm.Process> processModels, List<Integer> choicePlan) throws Exception {
        this.istarFile = istarFile;
        this.useFile = useFile;
        this.executionFile = executionFile;
        this.initialSoil = initialSoil;
        this.goalModel = initialTrace.model();
        this.useModel = initialTrace.useModel();
        this.bpmnModel = bpmnModel;
        this.choicePlan = List.copyOf(choicePlan);
        this.checkpoint = initialTrace.checkpoints().get(initialTrace.checkpoints().size() - 1);
        requireValidState(checkpoint.state(), "initialization gate");
        for (var process : processModels) {
            processes.put(process.id(), process);
            Bpmn2ExecutionEngine engine = new Bpmn2ExecutionEngine(process, this::chooseBranch);
            engines.put(process.id(), engine);
            engine.start(this::evaluate);
        }
        frames.add(new Frame(0, null, null, checkpoint, snapshots(), List.of(), List.of()));
    }

    public static VisualConformanceSession prepare(Path istar, Path bpmn, Path acl, Path soil) throws Exception {
        return prepare(istar, bpmn, acl, soil, List.of());
    }

    public static VisualConformanceSession prepare(Path istar, Path bpmn, Path acl, Path soil,
            List<Integer> choicePlan) throws Exception {
        require(istar, ".istar");
        require(bpmn, ".bpmn2");
        require(acl, ".acl");
        require(soil, ".soil");

        AclCompiler.Result aclResult = AclCompiler.compile(acl);
        if (!aclResult.ok()) throw new IllegalArgumentException(String.join("\n", aclResult.errors()));
        Bpmn2Compiler.Result bpmnResult = Bpmn2Compiler.compile(bpmn);
        if (!bpmnResult.ok()) throw new IllegalArgumentException(String.join("\n", bpmnResult.errors()));
        if (bpmnResult.model().processes().isEmpty()) throw new IllegalArgumentException("BPMN has no process");

        Path useFile = Files.createTempFile("acl-visual-", ".use");
        Files.writeString(useFile, AclUseTranslator.translate(aclResult.model()));
        String initialSoil = Files.readString(soil).stripTrailing() + "\n";
        Path executionFile = Files.createTempFile("scenario-trace-", ".soil");
        Files.writeString(executionFile, initialSoil);
        IStarUseTraceCompiler.Result trace = IStarUseTraceCompiler.compile(istar, useFile, executionFile);
        if (!trace.ok()) throw new IllegalArgumentException(String.join("\n", trace.errors()));
        if (trace.checkpoints().isEmpty()) throw new IllegalArgumentException("input scenario creates no state");
        return new VisualConformanceSession(istar, useFile, executionFile, initialSoil, trace,
                bpmnResult.model(), bpmnResult.model().processes(), choicePlan);
    }

    public Frame initialFrame() { return frames.get(0); }
    public List<Frame> frames() { return List.copyOf(frames); }
    public GoalModel goalModel() { return goalModel; }
    public Bpmn2Model bpmnModel() { return bpmnModel; }
    public List<String> processIds() { return List.copyOf(processes.keySet()); }
    public List<Integer> choiceWidths() { return List.copyOf(choiceWidths); }
    public Path generatedUse() { return useFile; }
    public Path executionSoil() { return executionFile; }
    public boolean ended() { return engines.values().stream().allMatch(engine -> engine.snapshot().ended()); }

    public boolean canAdvance() {
        return !ended() && engines.entrySet().stream().anyMatch(entry -> entry.getValue().snapshot().enabled()
                .stream().anyMatch(id -> isActivity(entry.getKey(), id)));
    }

    public Frame next() throws Exception {
        String processId = engines.entrySet().stream().filter(entry -> entry.getValue().snapshot().enabled()
                .stream().anyMatch(id -> isActivity(entry.getKey(), id))).map(Map.Entry::getKey).findFirst()
                .orElseThrow(() -> new IllegalStateException("no process has an enabled Activity"));
        Bpmn2ExecutionEngine engine = engines.get(processId);
        String activityId = engine.snapshot().enabled().stream().filter(id -> isActivity(processId, id)).findFirst()
                .orElseThrow();
        Activity activity = (Activity) find(processId, activityId);
        Checkpoint before = checkpoint;
        Bpmn2ExecutionEngine.RunningStep running = engine.begin(activityId, this::evaluate);
        if (activity.effectSource() != null && !activity.effectSource().isBlank()) {
            String statement = "begin " + activity.effectSource().replaceAll("\\s+", " ").strip() + " end";
            effects += "\n-- effect of " + activity.id() + "\n" + statement + "\n";
            Files.writeString(executionFile, initialSoil + effects);
            IStarUseTraceCompiler.Result trace = IStarUseTraceCompiler.compile(istarFile, useFile, executionFile);
            if (!trace.ok()) throw new IllegalStateException(String.join("\n", trace.errors()));
            useModel = trace.useModel();
            checkpoint = trace.checkpoints().get(trace.checkpoints().size() - 1);
        }
        requireValidState(checkpoint.state(), "after " + activity.id());
        Bpmn2ExecutionEngine.Snapshot snapshot = engine.complete(running, this::evaluate);
        Frame frame = new Frame(frames.size(), processId, activity, checkpoint, snapshots(),
                stateDelta(before, checkpoint), goalDelta(before, checkpoint));
        frames.add(frame);
        return frame;
    }

    /** A completed trace conforms iff every root-goal occurrence is fulfilled. */
    public List<String> rootGoalFailures() {
        if (!ended()) return List.of("BPMN process did not reach an EndEvent");
        return rootGoalFailures(frames.get(frames.size() - 1));
    }

    public List<String> rootGoalFailures(Frame frame) {
        var inst = IStarUseTraceEvaluator.evaluate(goalModel, frame.checkpoint());
        Set<String> roots = rootGoalIds(inst.instanceModel());
        List<String> failures = new ArrayList<>();
        for (String id : roots) {
            GoalTaskStatus status = inst.instanceMarking().goalTaskStatus(id);
            if (status != GoalTaskStatus.FULFILLED) failures.add(id + " = " + status);
        }
        return List.copyOf(failures);
    }

    private List<String> stateDelta(Checkpoint before, Checkpoint after) {
        Map<String, String> left = attributeValues(before.state());
        Map<String, String> right = attributeValues(after.state());
        Set<String> keys = new LinkedHashSet<>(left.keySet());
        keys.addAll(right.keySet());
        List<String> delta = new ArrayList<>();
        for (String key : keys) if (!java.util.Objects.equals(left.get(key), right.get(key))) {
            delta.add(key + ": " + left.getOrDefault(key, "<absent>") + " -> "
                    + right.getOrDefault(key, "<absent>"));
        }
        return List.copyOf(delta);
    }

    private static Map<String, String> attributeValues(MSystemState state) {
        Map<String, String> values = new LinkedHashMap<>();
        state.allObjects().stream().sorted(java.util.Comparator.comparing(o -> o.name())).forEach(object ->
                object.state(state).attributeValueMap().forEach((attribute, value) ->
                        values.put(object.name() + "." + attribute.name(), String.valueOf(value))));
        return values;
    }

    private List<String> goalDelta(Checkpoint before, Checkpoint after) {
        var left = IStarUseTraceEvaluator.evaluate(goalModel, before);
        var right = IStarUseTraceEvaluator.evaluate(goalModel, after);
        Set<String> ids = new LinkedHashSet<>(left.instanceMarking().goalTaskStatuses().keySet());
        ids.addAll(right.instanceMarking().goalTaskStatuses().keySet());
        List<String> delta = new ArrayList<>();
        for (String id : ids) {
            var a = left.instanceMarking().goalTaskStatus(id);
            var b = right.instanceMarking().goalTaskStatus(id);
            if (a != b) delta.add(right.nodeLabels().getOrDefault(id, id) + ": " + a + " -> " + b);
        }
        return List.copyOf(delta);
    }

    private static Set<String> rootGoalIds(GoalModel model) {
        Set<String> children = new LinkedHashSet<>();
        for (Actor actor : model.getActors()) for (Refinement refinement : actor.refinements()) {
            switch (refinement) {
                case AndRefinement and -> children.addAll(and.children());
                case OrRefinement or -> children.add(or.child());
                case ForRefinement forall -> children.add(forall.child());
                case PickRefinement pick -> children.add(pick.child());
            }
        }
        Set<String> roots = new LinkedHashSet<>();
        for (IntentionalElement element : model.allElements().values()) {
            if (element instanceof Goal && !children.contains(element.id())) roots.add(element.id());
        }
        return roots;
    }

    private Map<String, Bpmn2ExecutionEngine.Snapshot> snapshots() {
        Map<String, Bpmn2ExecutionEngine.Snapshot> result = new LinkedHashMap<>();
        engines.forEach((id, engine) -> result.put(id, engine.snapshot()));
        return Map.copyOf(result);
    }

    private int chooseBranch(String gatewayId,
            List<org.vnu.sme.goal.bpmn2.mm.SequenceFlow> acceptedFlows) {
        int ordinal = choiceCursor++;
        choiceWidths.add(acceptedFlows.size());
        return ordinal < choicePlan.size() ? choicePlan.get(ordinal) : 0;
    }

    private boolean isActivity(String processId, String id) { return find(processId, id) instanceof Activity; }

    private FlowElement find(String processId, String id) {
        return processes.get(processId).flowElements().stream().filter(element -> element.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("unknown BPMN element " + processId + "." + id));
    }

    private boolean evaluate(String ocl) throws Exception {
        StringWriter errors = new StringWriter();
        Expression expression = OCLCompiler.compileExpression(useModel, ocl, "BPMN runtime OCL",
                new PrintWriter(errors), new Symtable());
        if (expression == null) throw new IllegalArgumentException(errors.toString().strip());
        var value = expression.eval(new SimpleEvalContext(
                checkpoint.state(), checkpoint.state(), new VarBindings()));
        if (!(value instanceof BooleanValue booleanValue)) {
            throw new IllegalArgumentException("BPMN runtime OCL is not Boolean: " + ocl);
        }
        return booleanValue.isTrue();
    }

    private static void requireValidState(MSystemState state, String phase) {
        StringWriter report = new StringWriter();
        if (!state.check(new PrintWriter(report), false, true, true, List.of())) {
            throw new IllegalStateException(phase + " failed ACL/USE constraints:\n" + report.toString().strip());
        }
    }

    private static void require(Path file, String extension) {
        if (file == null || !Files.isRegularFile(file)
                || !file.getFileName().toString().toLowerCase().endsWith(extension)) {
            throw new IllegalArgumentException("expected existing " + extension + " file: " + file);
        }
    }
}
