package org.vnu.sme.goal.verify.conformance;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import org.tzi.use.parser.Symtable;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.SimpleEvalContext;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.uml.sys.MObject;

import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.dsl.bpmn.mm.*;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.trace.bpmn.BpmnPostEffect;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.dsl.istar.mm.*;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceEvaluator;

/** One-shot facade over the same token execution semantics used by the visual debugger. */
public final class AclBpmnIStarConformanceChecker {

    private record ExecutionPlan(String soil, List<ActivityStep> steps, int initialCheckpoint) {}
    private record ActivityStep(Activity activity, int preCheckpoint, int postCheckpoint, String selfObject) {}

    public record Result(Path generatedUse,
                         Path executionSoil,
                         GoalModel goalModel,
                         BpmnModel bpmnModel,
                         int checkpoints,
                         List<String> aclFailures,
                         List<String> bpmnFailures,
                         List<String> goalFailures,
                         List<String> errors,
                         ConformanceVerdict verdict) {
        public Result {
            aclFailures = List.copyOf(aclFailures);
            bpmnFailures = List.copyOf(bpmnFailures);
            goalFailures = List.copyOf(goalFailures);
            errors = List.copyOf(errors);
        }

        public boolean ok() { return verdict != ConformanceVerdict.EXECUTION_ERROR; }
        public boolean conformant() { return verdict == ConformanceVerdict.TRACE_CONFORMANT; }
    }

    private AclBpmnIStarConformanceChecker() {}

    public static Result check(Path aclFile, Path soilFile, Path istarFile, Path bpmnFile) throws Exception {
        return checkInternal(aclFile, soilFile, istarFile, bpmnFile, null);
    }

    public static Result checkTrace(
            Path aclFile,
            Path soilFile,
            Path istarFile,
            Path bpmnFile,
            List<String> activityIds) throws Exception {
        return checkInternal(aclFile, soilFile, istarFile, bpmnFile, List.copyOf(activityIds));
    }

    private static Result checkInternal(
            Path aclFile,
            Path soilFile,
            Path istarFile,
            Path bpmnFile,
            List<String> activityIds) throws Exception {
        List<String> errors = new ArrayList<>();

        AclCompiler.Result acl = AclCompiler.compile(aclFile);
        if (!acl.ok()) return failure(errors, "ACL", acl.errors());

        BpmnCompiler.Result bpmn = BpmnCompiler.compile(bpmnFile);
        if (!bpmn.ok()) return failure(errors, "BPMN2", bpmn.errors());

        Path generatedUse = Files.createTempFile("acl-", ".use");
        Files.writeString(generatedUse, Acl2UseTranslator.translate(acl.model()));

        List<String> activityErrors = new ArrayList<>();
        List<Activity> activities = activityIds == null
                ? executionOrder(bpmn.model())
                : resolveActivities(bpmn.model(), activityIds, activityErrors);
        if (!activityErrors.isEmpty()) return failure(errors, "BPMN2 trace", activityErrors);

        ExecutionPlan plan = executionPlan(Files.readString(soilFile), activities, bpmn.model());
        Path executionSoil = Files.createTempFile("bpmn-execution-", ".soil");
        Files.writeString(executionSoil, plan.soil());

        IStarUseTraceCompiler.Result trace = IStarUseTraceCompiler.compile(
                istarFile, generatedUse, executionSoil, acl.model(), plan.initialCheckpoint());
        if (!trace.ok()) return failure(errors, "i*+USE/SOIL", trace.errors());
        if (trace.checkpoints().isEmpty()) return failure(errors, "SOIL", List.of("no checkpoints were produced"));

        Checkpoint finalCheckpoint = trace.checkpoints().get(trace.checkpoints().size() - 1);
        List<String> aclFailures = evaluateAclInvariants(plan, trace.checkpoints(), errors);
        List<String> bpmnFailures = evaluateBpmnOcl(
                plan, trace.useModel(), trace.checkpoints(), errors);
        List<String> goalFailures = evaluateRootGoals(trace.model(), finalCheckpoint);

        ConformanceVerdict verdict = !errors.isEmpty()
                ? ConformanceVerdict.EXECUTION_ERROR
                : aclFailures.isEmpty() && bpmnFailures.isEmpty() && goalFailures.isEmpty()
                        ? ConformanceVerdict.TRACE_CONFORMANT
                        : ConformanceVerdict.TRACE_NON_CONFORMANT;
        return new Result(generatedUse, executionSoil, trace.model(), bpmn.model(), trace.checkpoints().size(),
                aclFailures, bpmnFailures, goalFailures, errors, verdict);
    }

    private static Result failure(List<String> errors, String stage, List<String> stageErrors) {
        errors.add(stage + " failed:");
        stageErrors.forEach(e -> errors.add("  - " + e));
        return new Result(null, null, null, null, 0, List.of(), List.of(), List.of(),
                errors, ConformanceVerdict.EXECUTION_ERROR);
    }

    private static ExecutionPlan executionPlan(
            String initialSoil, List<Activity> activities, BpmnModel model) {
        List<ActivityStep> steps = new ArrayList<>();
        StringBuilder out = new StringBuilder();
        out.append(initialSoil.stripTrailing()).append("\n\n");
        out.append("-- Generic BPMN effects follow; snapshot object names are not embedded in them.\n");
        int checkpoint = countSoilStatements(initialSoil);
        int initialCheckpoint = checkpoint;
        for (Activity activity : activities) {
            int preCheckpoint = checkpoint;
            String selfObject = scopedObject(initialSoil, model, activity);
            String effect = activity.postconditions().isEmpty() ? null
                    : BpmnPostEffect.toSoil(activity.postconditions().get(0).oclBody());
            if (effect != null) {
                effect = bindSelf(effect, selfObject);
                // IStarUseTraceCompiler consumes one SOIL statement per physical line.
                String oneStatement = effect.replaceAll("\\s+", " ").strip();
                out.append("-- effect of ").append(activity.id()).append("\n")
                        .append(oneStatement).append("\n");
                checkpoint++;
            }
            steps.add(new ActivityStep(activity, preCheckpoint, checkpoint, selfObject));
        }
        return new ExecutionPlan(out.toString(), List.copyOf(steps), initialCheckpoint);
    }

    /** Resolves the sole object that instantiates an activity's scoped BPMN pool. */
    private static String scopedObject(String soil, BpmnModel model, Activity activity) {
        String processId = model.ownerProcessId(activity.id()).orElse(null);
        org.vnu.sme.goal.dsl.bpmn.mm.Process process = processId == null
                ? null : model.findProcess(processId).orElse(null);
        if (process == null || process.groupClass() == null) return null;
        var pattern = java.util.regex.Pattern.compile(
                "(?m)^\\s*!create\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*"
                        + java.util.regex.Pattern.quote(process.groupClass()) + "\\s*$");
        var matcher = pattern.matcher(soil);
        String result = null;
        while (matcher.find()) {
            if (result != null) {
                throw new IllegalArgumentException("one-shot conformance is ambiguous: pool '"
                        + process.id() + "' has more than one " + process.groupClass()
                        + " instance; use an explicit scoped trace");
            }
            result = matcher.group(1);
        }
        if (result == null) {
            throw new IllegalArgumentException("scoped pool '" + process.id()
                    + "' requires one " + process.groupClass() + " object in the input SOIL");
        }
        return result;
    }

    private static String bindSelf(String source, String selfObject) {
        return selfObject == null ? source
                : source.replaceAll("\\bself\\b", java.util.regex.Matcher.quoteReplacement(selfObject));
    }

    private static List<Activity> resolveActivities(
            BpmnModel model,
            List<String> activityIds,
            List<String> errors) {
        Map<String, Activity> byId = new LinkedHashMap<>();
        activities(model).forEach(activity -> byId.put(activity.id(), activity));
        List<Activity> resolved = new ArrayList<>();
        for (String id : activityIds) {
            Activity activity = byId.get(id);
            if (activity == null) {
                errors.add("execution trace references unknown BPMN activity '" + id + "'");
            } else {
                resolved.add(activity);
            }
        }
        return resolved;
    }

    private static List<String> evaluateAclInvariants(
            ExecutionPlan plan,
            List<Checkpoint> checkpoints,
            List<String> errors) {
        Set<Integer> selected = new LinkedHashSet<>();
        selected.add(plan.initialCheckpoint());
        plan.steps().forEach(step -> selected.add(step.postCheckpoint()));
        List<String> failures = new ArrayList<>();
        for (int number : selected) {
            if (number <= 0 || number > checkpoints.size()) {
                errors.add("ACL state check cannot use missing checkpoint " + number);
                continue;
            }
            StringWriter output = new StringWriter();
            boolean valid = checkpoints.get(number - 1).state().check(
                    new PrintWriter(output), false, true, true, List.of());
            if (!valid) {
                String detail = "earliest invalid checkpoint " + number
                        + " violates ACL/USE constraints:\n" + output.toString().strip();
                if (number == plan.initialCheckpoint()) {
                    errors.add("invalid initial ACL state: " + detail);
                } else {
                    failures.add(detail);
                }
                break;
            }
        }
        return failures;
    }

    private static int countSoilStatements(String soil) {
        int count = 0;
        for (String raw : soil.lines().toList()) {
            String line = raw.strip();
            if (!line.isEmpty() && !line.startsWith("--")) count++;
        }
        return count;
    }

    private static List<Activity> executionOrder(BpmnModel model) {
        List<Activity> order = new ArrayList<>();
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : model.processes()) {
            Map<String, FlowElement> byId = new LinkedHashMap<>();
            process.flowElements().forEach(e -> byId.put(e.id(), e));

            Map<String, List<String>> outgoing = new LinkedHashMap<>();
            Map<String, Integer> indegree = new LinkedHashMap<>();
            byId.keySet().forEach(id -> indegree.put(id, 0));
            for (SequenceFlow flow : process.sequenceFlows()) {
                outgoing.computeIfAbsent(flow.source().id(), ignored -> new ArrayList<>()).add(flow.target().id());
                indegree.computeIfPresent(flow.target().id(), (id, n) -> n + 1);
            }

            Queue<String> queue = new ArrayDeque<>();
            byId.values().stream()
                    .filter(e -> e instanceof StartEvent)
                    .map(FlowElement::id)
                    .forEach(queue::add);
            if (queue.isEmpty()) {
                indegree.entrySet().stream()
                        .filter(e -> e.getValue() == 0)
                        .map(Map.Entry::getKey)
                        .forEach(queue::add);
            }

            Set<String> seen = new LinkedHashSet<>();
            while (!queue.isEmpty()) {
                String id = queue.remove();
                if (!seen.add(id)) continue;
                FlowElement element = byId.get(id);
                if (element instanceof Activity activity) order.add(activity);
                for (String target : outgoing.getOrDefault(id, List.of())) {
                    Integer next = indegree.computeIfPresent(target, (ignored, n) -> Math.max(0, n - 1));
                    if (next != null && (next == 0 || element instanceof StartEvent || element instanceof EndEvent)) {
                        queue.add(target);
                    }
                }
            }

            for (FlowElement element : process.flowElements()) {
                if (!seen.contains(element.id()) && element instanceof Activity activity) order.add(activity);
            }
        }
        return order;
    }

    private static List<String> evaluateBpmnOcl(
            ExecutionPlan plan,
            MModel useModel,
            List<Checkpoint> checkpoints,
            List<String> errors) {
        List<String> failures = new ArrayList<>();
        for (ActivityStep step : plan.steps()) {
            Activity activity = step.activity();
            for (ActivityConstraint constraint : activity.preconditions()) {
                String label = activity.id() + " " + constraint.kind().name().toLowerCase();
                evaluateOclAtCheckpoint(useModel, checkpoints, step.preCheckpoint(), label,
                        constraint.oclBody(), step.selfObject(), failures, errors);
            }
            for (ActivityConstraint constraint : activity.postconditions()) {
                String label = activity.id() + " " + constraint.kind().name().toLowerCase();
                evaluateOclAtCheckpoint(useModel, checkpoints, step.postCheckpoint(), label,
                        constraint.oclBody(), step.selfObject(), failures, errors);
            }
        }
        return failures;
    }

    private static void evaluateOclAtCheckpoint(MModel useModel, List<Checkpoint> checkpoints, int checkpointNumber,
                                                String label, String oclBody,
                                                String selfObject,
                                                List<String> failures, List<String> errors) {
        if (checkpointNumber <= 0 || checkpointNumber > checkpoints.size()) {
            errors.add(label + " cannot be evaluated: missing checkpoint " + checkpointNumber);
            return;
        }
        MSystemState state = checkpoints.get(checkpointNumber - 1).state();
        MObject self = selfObject == null ? null : state.objectByName(selfObject);
        Expression expr = compileOcl(useModel, label, oclBody, self, errors);
        if (expr == null) return;
        try {
            EvalContext context = new SimpleEvalContext(state, state, new VarBindings());
            if (self != null) context.pushVarBinding("self", self.value());
            var value = expr.eval(context);
            if (!(value instanceof BooleanValue bv) || !bv.isTrue()) {
                failures.add(label + " is false at checkpoint " + checkpointNumber);
            }
        } catch (RuntimeException ex) {
            errors.add(label + " evaluation failed at checkpoint " + checkpointNumber + ": "
                    + ex.getMessage());
        }
    }

    private static Expression compileOcl(
            MModel useModel, String label, String source, MObject self, List<String> errors) {
        StringWriter sw = new StringWriter();
        PrintWriter err = new PrintWriter(sw);
        try {
            Symtable vars = new Symtable();
            if (self != null) vars.add("self", self.cls(), null);
            Expression expr = OCLCompiler.compileExpression(useModel, source, label, err, vars);
            err.flush();
            if (expr == null) errors.add(label + " does not compile: " + sw);
            return expr;
        } catch (Exception ex) {
            errors.add(label + " does not compile: " + ex.getMessage());
            return null;
        }
    }

    private static List<String> evaluateRootGoals(GoalModel model, Checkpoint checkpoint) {
        // Quantified refinement and dependency satisfaction only have a precise meaning
        // after type-level nodes have been expanded to their concrete role occurrences.
        // Keep the one-shot checker aligned with VisualConformanceSession by evaluating
        // exactly that instance model rather than the checkpoint's flat type marking.
        var instance = IStarUseTraceEvaluator.evaluate(model, checkpoint);
        List<String> failures = new ArrayList<>();
        for (Actor actor : instance.instanceModel().getActors()) {
            for (String goalId : rootGoalIds(actor)) {
                GoalTaskStatus status = instance.instanceMarking().goalTaskStatus(goalId);
                if (!isStrictRootGoalSatisfied(status)) {
                    failures.add(goalId + " = " + status);
                }
            }
        }
        return failures;
    }

    static boolean isStrictRootGoalSatisfied(GoalTaskStatus status) {
        return status == GoalTaskStatus.FULFILLED;
    }

    private static Set<String> rootGoalIds(Actor actor) {
        Set<String> children = new LinkedHashSet<>();
        for (Refinement r : actor.refinements()) {
            switch (r) {
                case AndRefinement and -> children.addAll(and.children());
                case OrRefinement or -> children.add(or.child());
                case ForRefinement f -> children.add(f.child());
                case PickRefinement p -> children.add(p.child());
            }
        }
        Set<String> roots = new LinkedHashSet<>();
        for (IntentionalElement element : actor.elements()) {
            if (element instanceof Goal && !children.contains(element.id())) roots.add(element.id());
        }
        return roots;
    }

    private static List<Activity> activities(BpmnModel model) {
        List<Activity> activities = new ArrayList<>();
        model.processes().forEach(p -> collectActivities(p.flowElements(), activities));
        return activities;
    }

    private static void collectActivities(List<FlowElement> elements, List<Activity> out) {
        for (FlowElement element : elements) {
            if (element instanceof Activity activity) {
                out.add(activity);
                if (activity instanceof SubProcess subProcess) {
                    collectActivities(subProcess.flowElements(), out);
                }
            }
        }
    }
}
