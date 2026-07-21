package org.vnu.sme.goal.conformance;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
import org.vnu.sme.goal.bpmn2.mm.Activity;
import org.vnu.sme.goal.bpmn2.mm.ActivityConstraint;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.mm.EndEvent;
import org.vnu.sme.goal.bpmn2.mm.FlowElement;
import org.vnu.sme.goal.bpmn2.mm.SequenceFlow;
import org.vnu.sme.goal.bpmn2.mm.StartEvent;
import org.vnu.sme.goal.bpmn2.mm.SubProcess;
import org.vnu.sme.goal.bpmn2.parser.Bpmn2Compiler;
import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.istar.mm.Actor;
import org.vnu.sme.goal.istar.mm.AndRefinement;
import org.vnu.sme.goal.istar.mm.ForRefinement;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.OrRefinement;
import org.vnu.sme.goal.istar.mm.PickRefinement;
import org.vnu.sme.goal.istar.mm.Refinement;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler.Checkpoint;

public final class AclBpmnIStarConformanceChecker {

    private record ExecutionPlan(String soil, List<ActivityStep> steps, int initialCheckpoint) {}
    private record ActivityStep(Activity activity, int preCheckpoint, int postCheckpoint) {}

    public record Result(Path generatedUse,
                         Path executionSoil,
                         GoalModel goalModel,
                         Bpmn2Model bpmnModel,
                         int checkpoints,
                         List<String> aclFailures,
                         List<String> bpmnFailures,
                         List<String> goalFailures,
                         List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
        public boolean conformant() { return ok() && aclFailures.isEmpty()
                && bpmnFailures.isEmpty() && goalFailures.isEmpty(); }
    }

    private AclBpmnIStarConformanceChecker() {}

    public static Result check(Path aclFile, Path soilFile, Path istarFile, Path bpmnFile) throws Exception {
        List<String> errors = new ArrayList<>();

        AclCompiler.Result acl = AclCompiler.compile(aclFile);
        if (!acl.ok()) return failure(errors, "ACL", acl.errors());

        Bpmn2Compiler.Result bpmn = Bpmn2Compiler.compile(bpmnFile);
        if (!bpmn.ok()) return failure(errors, "BPMN2", bpmn.errors());

        Path generatedUse = Files.createTempFile("acl-", ".use");
        Files.writeString(generatedUse, AclUseTranslator.translate(acl.model()));

        ExecutionPlan plan = executionPlan(Files.readString(soilFile), bpmn.model());
        Path executionSoil = Files.createTempFile("bpmn-execution-", ".soil");
        Files.writeString(executionSoil, plan.soil());

        IStarUseTraceCompiler.Result trace = IStarUseTraceCompiler.compile(istarFile, generatedUse, executionSoil);
        if (!trace.ok()) return failure(errors, "i*+USE/SOIL", trace.errors());
        if (trace.checkpoints().isEmpty()) return failure(errors, "SOIL", List.of("no checkpoints were produced"));

        Checkpoint finalCheckpoint = trace.checkpoints().get(trace.checkpoints().size() - 1);
        List<String> aclFailures = evaluateAclInvariants(plan, trace.checkpoints());
        List<String> bpmnFailures = evaluateBpmnOcl(plan, trace.useModel(), trace.checkpoints());
        List<String> goalFailures = evaluateRootGoals(trace.model(), finalCheckpoint);

        return new Result(generatedUse, executionSoil, trace.model(), bpmn.model(), trace.checkpoints().size(),
                aclFailures, bpmnFailures, goalFailures, List.of());
    }

    private static Result failure(List<String> errors, String stage, List<String> stageErrors) {
        errors.add(stage + " failed:");
        stageErrors.forEach(e -> errors.add("  - " + e));
        return new Result(null, null, null, null, 0, List.of(), List.of(), List.of(), List.copyOf(errors));
    }

    private static ExecutionPlan executionPlan(String initialSoil, Bpmn2Model bpmnModel) {
        List<ActivityStep> steps = new ArrayList<>();
        StringBuilder out = new StringBuilder();
        out.append(initialSoil.stripTrailing()).append("\n\n");
        out.append("-- Generic BPMN effects follow; snapshot object names are not embedded in them.\n");
        int checkpoint = countSoilStatements(initialSoil);
        int initialCheckpoint = checkpoint;
        for (Activity activity : executionOrder(bpmnModel)) {
            int preCheckpoint = checkpoint;
            String effect = activity.effectSource();
            if (effect != null && !effect.isBlank()) {
                // IStarUseTraceCompiler consumes one SOIL statement per physical line.
                String oneStatement = effect.replaceAll("\\s+", " ").strip();
                out.append("-- effect of ").append(activity.id()).append("\n")
                        .append(oneStatement).append("\n");
                checkpoint++;
            }
            steps.add(new ActivityStep(activity, preCheckpoint, checkpoint));
        }
        return new ExecutionPlan(out.toString(), List.copyOf(steps), initialCheckpoint);
    }

    private static List<String> evaluateAclInvariants(ExecutionPlan plan, List<Checkpoint> checkpoints) {
        Set<Integer> selected = new LinkedHashSet<>();
        selected.add(plan.initialCheckpoint());
        plan.steps().forEach(step -> selected.add(step.postCheckpoint()));
        List<String> failures = new ArrayList<>();
        for (int number : selected) {
            if (number <= 0 || number > checkpoints.size()) {
                failures.add("ACL state check cannot use missing checkpoint " + number);
                continue;
            }
            StringWriter output = new StringWriter();
            boolean valid = checkpoints.get(number - 1).state().check(
                    new PrintWriter(output), false, true, true, List.of());
            if (!valid) {
                failures.add("earliest invalid checkpoint " + number + " violates ACL/USE constraints:\n"
                        + output.toString().strip());
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

    private static List<Activity> executionOrder(Bpmn2Model model) {
        List<Activity> order = new ArrayList<>();
        for (org.vnu.sme.goal.bpmn2.mm.Process process : model.processes()) {
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

    private static List<String> evaluateBpmnOcl(ExecutionPlan plan, MModel useModel, List<Checkpoint> checkpoints) {
        List<String> failures = new ArrayList<>();
        for (ActivityStep step : plan.steps()) {
            Activity activity = step.activity();
            for (ActivityConstraint constraint : activity.preconditions()) {
                String label = activity.id() + " " + constraint.kind().name().toLowerCase();
                evaluateOclAtCheckpoint(useModel, checkpoints, step.preCheckpoint(), label, constraint, failures);
            }
            for (ActivityConstraint constraint : activity.postconditions()) {
                String label = activity.id() + " " + constraint.kind().name().toLowerCase();
                evaluateOclAtCheckpoint(useModel, checkpoints, step.postCheckpoint(), label, constraint, failures);
            }
        }
        return failures;
    }

    private static void evaluateOclAtCheckpoint(MModel useModel, List<Checkpoint> checkpoints, int checkpointNumber,
                                                String label, ActivityConstraint constraint, List<String> failures) {
        if (checkpointNumber <= 0 || checkpointNumber > checkpoints.size()) {
            failures.add(label + " cannot be evaluated: missing checkpoint " + checkpointNumber);
            return;
        }
        MSystemState state = checkpoints.get(checkpointNumber - 1).state();
        Expression expr = compileOcl(useModel, label, constraint.oclBody(), failures);
        if (expr == null) return;
        var value = expr.eval(new SimpleEvalContext(state, state, new VarBindings()));
        if (!(value instanceof BooleanValue bv) || !bv.isTrue()) {
            failures.add(label + " is false at checkpoint " + checkpointNumber);
        }
    }

    private static Expression compileOcl(MModel useModel, String label, String source, List<String> failures) {
        StringWriter sw = new StringWriter();
        PrintWriter err = new PrintWriter(sw);
        try {
            Expression expr = OCLCompiler.compileExpression(useModel, source, label, err, new Symtable());
            err.flush();
            if (expr == null) failures.add(label + " does not compile: " + sw);
            return expr;
        } catch (Exception ex) {
            failures.add(label + " does not compile: " + ex.getMessage());
            return null;
        }
    }

    private static List<String> evaluateRootGoals(GoalModel model, Checkpoint checkpoint) {
        Set<String> rootGoalIds = rootGoalIds(model);
        List<String> failures = new ArrayList<>();
        for (var entry : checkpoint.markings().entrySet()) {
            for (String goalId : rootGoalIds) {
                GoalTaskStatus status = entry.getValue().goalTaskStatus(goalId);
                if (status == GoalTaskStatus.UNKNOWN) continue;
                if (status != GoalTaskStatus.FULFILLED) {
                    failures.add(entry.getKey() + "." + goalId + " = " + status);
                }
            }
        }
        return failures;
    }

    private static Set<String> rootGoalIds(GoalModel model) {
        Set<String> children = new LinkedHashSet<>();
        for (Actor actor : model.getActors()) {
            for (Refinement r : actor.refinements()) {
                switch (r) {
                    case AndRefinement and -> children.addAll(and.children());
                    case OrRefinement or -> children.add(or.child());
                    case ForRefinement f -> children.add(f.child());
                    case PickRefinement p -> children.add(p.child());
                }
            }
        }
        Set<String> roots = new LinkedHashSet<>();
        for (IntentionalElement element : model.allElements().values()) {
            if (element instanceof Goal && !children.contains(element.id())) roots.add(element.id());
        }
        return roots;
    }

    private static List<Activity> activities(Bpmn2Model model) {
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
