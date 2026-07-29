package org.vnu.sme.goal.conformance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.istar.mm.GoalModel;

/** One-shot facade over the same token execution semantics used by the visual debugger. */
public final class AclBpmnIStarConformanceChecker {
    public record Result(Path generatedUse, Path executionSoil, GoalModel goalModel,
                         Bpmn2Model bpmnModel, int checkpoints, List<String> aclFailures,
                         List<String> bpmnFailures, List<String> goalFailures, List<String> errors,
                         String conformanceLevel) {
        public boolean ok() { return errors.isEmpty(); }
        public boolean conformant() { return ok() && "STRONGLY_CONFORMANT".equals(conformanceLevel); }
    }

    private AclBpmnIStarConformanceChecker() {}

    public static Result check(Path aclFile, Path soilFile, Path istarFile, Path bpmnFile) {
        VisualConformanceSession session = null;
        List<String> bpmnFailures = new ArrayList<>();
        try {
            session = VisualConformanceSession.prepare(istarFile, bpmnFile, aclFile, soilFile);
            while (session.canAdvance()) session.next();
            if (!session.ended()) bpmnFailures.add("BPMN execution is deadlocked before an EndEvent");
            var assessment = ScenarioTraceExplorer.explore(istarFile, bpmnFile, aclFile, soilFile, 128);
            for (var trace : assessment.traces()) if (!trace.ended()) {
                bpmnFailures.add("trace " + trace.choices() + ": " + String.join("; ", trace.failures()));
            }
            List<String> goalFailures = assessment.traces().stream().filter(trace -> trace.ended()
                    && !trace.rootsSatisfied()).flatMap(trace -> trace.failures().stream()
                            .map(failure -> "trace " + trace.choices() + ": " + failure)).toList();
            return new Result(session.generatedUse(), session.executionSoil(), session.goalModel(),
                    session.bpmnModel(), session.frames().size(), List.of(),
                    List.copyOf(bpmnFailures), goalFailures, List.of(), assessment.verdict().name());
        } catch (Exception ex) {
            if (session == null) {
                return new Result(null, null, null, null, 0, List.of(), List.of(), List.of(),
                        List.of("initialization/compilation failed: " + ex.getMessage()), "INVALID_INPUT");
            }
            bpmnFailures.add(ex.getMessage());
            return new Result(session.generatedUse(), session.executionSoil(), session.goalModel(),
                    session.bpmnModel(), session.frames().size(), List.of(),
                    List.copyOf(bpmnFailures), List.of(), List.of(), "NON_CONFORMANT");
        }
    }
}
