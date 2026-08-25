package org.vnu.sme.goal.verify.aclstate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.verify.aclstate.AclBpmnFlowRuntime.FlowStep;

/** Checks an explicitly supplied ACL state path against formal BPMN flow steps. */
public final class AclBpmnStateTraceEvaluator {
    private static final int MAX_CANDIDATES = 4096;

    public enum Verdict { CONFORMANT, AMBIGUOUS, NON_CONFORMANT }

    public record StepAlternative(String processId, String selfObject,
                                  String flowId, String sourceId, String targetId,
                                  Set<String> pass, Set<String> next,
                                  Set<String> resultingActivations,
                                  String contract) {
        public StepAlternative {
            selfObject = selfObject == null ? "-" : selfObject;
            pass = Set.copyOf(pass);
            next = Set.copyOf(next);
            resultingActivations = Set.copyOf(resultingActivations);
        }

        public String route() { return sourceId + " -> " + targetId; }

        String key() {
            return processId + "\u0000" + selfObject + "\u0000" + flowId
                    + "\u0000" + sorted(pass) + "\u0000" + sorted(next)
                    + "\u0000" + sorted(resultingActivations);
        }
    }

    public record StepResult(int index, Path beforeFile, Path afterFile,
                             Verdict verdict, List<StepAlternative> alternatives,
                             String detail) {
        public StepResult {
            alternatives = List.copyOf(alternatives);
            detail = detail == null ? "" : detail;
        }
    }

    public record TraceResult(Verdict verdict, List<StepResult> steps,
                              int survivingPaths, int completedPaths, String summary) {
        public TraceResult {
            steps = List.copyOf(steps);
            summary = summary == null ? "" : summary;
        }
    }

    private record Candidate(AclBpmnFlowRuntime runtime, String selfObject,
                             Set<String> marking, List<StepAlternative> history) {}

    private final Path bpmnFile;
    private final BpmnModel bpmnModel;
    private final String bpmnSource;
    private final List<AclBpmnFlowRuntime> runtimes;

    private AclBpmnStateTraceEvaluator(Path bpmnFile, BpmnModel bpmnModel, String bpmnSource) {
        this.bpmnFile = bpmnFile;
        this.bpmnModel = bpmnModel;
        this.bpmnSource = bpmnSource;
        this.runtimes = bpmnModel.processes().stream().map(AclBpmnFlowRuntime::new).toList();
    }

    public static AclBpmnStateTraceEvaluator load(Path bpmnFile, AclModel aclModel) throws IOException {
        Objects.requireNonNull(bpmnFile, "bpmnFile");
        Objects.requireNonNull(aclModel, "aclModel");
        Path source = bpmnFile.toAbsolutePath().normalize();
        BpmnCompiler.Result compiled = BpmnCompiler.compile(source);
        if (!compiled.ok()) {
            throw new IllegalArgumentException("BPMN compilation failed:\n"
                    + String.join("\n", compiled.errors()));
        }
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : compiled.model().processes()) {
            if (process.groupClass() != null && aclModel.findGroup(process.groupClass()).isEmpty()) {
                throw new IllegalArgumentException("BPMN pool '" + process.id()
                        + "' is scoped to unknown ACL Group '" + process.groupClass() + "'");
            }
        }
        return new AclBpmnStateTraceEvaluator(source, compiled.model(), Files.readString(source));
    }

    public Path bpmnFile() { return bpmnFile; }
    public BpmnModel bpmnModel() { return bpmnModel; }
    public String bpmnSource() { return bpmnSource; }
    List<AclBpmnFlowRuntime> runtimes() { return runtimes; }

    public TraceResult evaluate(List<AclStateEvaluationSession.StateResult> states) {
        Objects.requireNonNull(states, "states");
        if (states.size() < 2) {
            return failure(List.of(), "At least two ACL states are required for one formal flow step.");
        }
        for (AclStateEvaluationSession.StateResult state : states) {
            if (!invariantValid(state)) {
                return failure(List.of(), "State s" + state.index()
                        + " is not a valid ACL state: structure/invariant evaluation failed.");
            }
        }

        List<Candidate> candidates = initialCandidates(states.get(0).state());
        if (candidates.isEmpty()) {
            return failure(List.of(), "No BPMN process instance can be bound to the first ACL state.");
        }

        List<StepResult> results = new ArrayList<>();
        for (int index = 0; index + 1 < states.size(); index++) {
            var before = states.get(index);
            var after = states.get(index + 1);
            List<Candidate> nextCandidates = new ArrayList<>();
            List<String> rejected = new ArrayList<>();

            for (Candidate candidate : candidates) {
                for (FlowStep step : candidate.runtime().enabledSteps(candidate.marking())) {
                    try {
                        if (!candidate.runtime().preHolds(step, before.state(), candidate.selfObject())) {
                            rejected.add(step.label() + " Pre_B=false");
                            continue;
                        }
                        if (!candidate.runtime().postHolds(step, before.state(), after.state(),
                                candidate.selfObject())) {
                            rejected.add(step.label() + " Post_B=false");
                            continue;
                        }
                        Set<String> marking = candidate.runtime().execute(candidate.marking(), step);
                        StepAlternative alternative = alternative(candidate, step, marking);
                        List<StepAlternative> history = new ArrayList<>(candidate.history());
                        history.add(alternative);
                        nextCandidates.add(new Candidate(candidate.runtime(), candidate.selfObject(),
                                marking, List.copyOf(history)));
                        if (nextCandidates.size() >= MAX_CANDIDATES) break;
                    } catch (RuntimeException ex) {
                        rejected.add(step.label() + " OCL error: " + message(ex));
                    }
                }
                if (nextCandidates.size() >= MAX_CANDIDATES) break;
            }

            List<StepAlternative> alternatives = distinctAlternatives(nextCandidates);
            if (nextCandidates.isEmpty()) {
                String detail = rejected.stream().distinct().limit(12)
                        .reduce((a, b) -> a + "; " + b)
                        .orElse("No enabled formal BPMN flow matches this ACL state pair.");
                results.add(new StepResult(index, before.aolFile(), after.aolFile(),
                        Verdict.NON_CONFORMANT, List.of(), detail));
                return new TraceResult(Verdict.NON_CONFORMANT, results, 0, 0,
                        "The scenario deviates at formal flow step s" + index + " -> s" + (index + 1) + ".");
            }
            Verdict verdict = alternatives.size() == 1 ? Verdict.CONFORMANT : Verdict.AMBIGUOUS;
            results.add(new StepResult(index, before.aolFile(), after.aolFile(), verdict,
                    alternatives, alternatives.size() + " formal flow explanation(s)."));
            candidates = distinctCandidates(nextCandidates);
        }

        int completed = (int) candidates.stream()
                .filter(candidate -> candidate.runtime().fullyConsumed(candidate.marking())).count();
        // A choice may be ambiguous at one prefix and become unique after a
        // later flow contract. The overall verdict depends on surviving full paths.
        boolean ambiguous = candidates.size() != 1;
        Verdict verdict = ambiguous ? Verdict.AMBIGUOUS : Verdict.CONFORMANT;
        String summary = "The supplied ACL path is " + verdict
                + " under the formal flow semantics; " + candidates.size()
                + " execution path(s) survive and " + completed
                + " have consumed all End flows.";
        return new TraceResult(verdict, results, candidates.size(), completed, summary);
    }

    private List<Candidate> initialCandidates(AclSystemState state) {
        List<Candidate> result = new ArrayList<>();
        for (AclBpmnFlowRuntime runtime : runtimes) {
            List<String> bindings = runtime.process().groupClass() == null
                    ? java.util.Collections.singletonList(null)
                    : state.objectsOfType(runtime.process().groupClass()).stream()
                            .map(AclSystemState.ObjectValue::id).toList();
            for (String self : bindings) {
                result.add(new Candidate(runtime, self, runtime.initialMarking(), List.of()));
            }
        }
        return result;
    }

    private static StepAlternative alternative(Candidate candidate, FlowStep step, Set<String> marking) {
        return new StepAlternative(candidate.runtime().process().id(), candidate.selfObject(),
                step.flow().id(), step.flow().sourceId(), step.flow().targetId(),
                step.pass(), step.next(), marking, candidate.runtime().contractDescription(step));
    }

    private static List<StepAlternative> distinctAlternatives(List<Candidate> candidates) {
        Map<String, StepAlternative> result = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            StepAlternative last = candidate.history().get(candidate.history().size() - 1);
            result.putIfAbsent(last.key(), last);
        }
        return result.values().stream().sorted(Comparator.comparing(StepAlternative::key)).toList();
    }

    private static List<Candidate> distinctCandidates(List<Candidate> candidates) {
        Map<String, Candidate> result = new LinkedHashMap<>();
        for (Candidate candidate : candidates) {
            String history = candidate.history().stream().map(StepAlternative::key)
                    .reduce((a, b) -> a + "\u0001" + b).orElse("");
            String key = candidate.runtime().process().id() + "\u0000" + candidate.selfObject()
                    + "\u0000" + sorted(candidate.marking()) + "\u0000" + history;
            result.putIfAbsent(key, candidate);
            if (result.size() >= MAX_CANDIDATES) break;
        }
        return List.copyOf(result.values());
    }

    static boolean invariantValid(AclStateEvaluationSession.StateResult state) {
        return state.structureValid() && state.falseCount() == 0
                && state.undefinedCount() == 0 && state.errorCount() == 0;
    }

    private static TraceResult failure(List<StepResult> steps, String summary) {
        return new TraceResult(Verdict.NON_CONFORMANT, steps, 0, 0, summary);
    }

    private static String sorted(Set<String> values) {
        return values.stream().sorted().toList().toString();
    }

    private static String message(Throwable throwable) {
        String value = throwable.getMessage();
        return value == null || value.isBlank() ? throwable.getClass().getSimpleName() : value;
    }
}
