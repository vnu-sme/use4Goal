package org.vnu.sme.goal.scenarioverification;

import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclConstraintCompiler;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult.Status;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluator;

/** Orchestrates BPMN OCL evaluation over an externally supplied scenario state. */
public final class ScenarioTraceVerifier {

    private ScenarioTraceVerifier() {}

    public static ScenarioTraceVerificationReport verifyBpmn(
            String scenarioName,
            String processName,
            ScenarioRuntimeState runtimeState,
            Map<String, Bpmn2OclConstraintCompiler.ConstraintInfo> constraints) {
        if (runtimeState == null) {
            return new ScenarioTraceVerificationReport(scenarioName, processName, null,
                    List.of(reportError("runtimeState is null")));
        }
        if (constraints == null) {
            return new ScenarioTraceVerificationReport(scenarioName, processName, runtimeState,
                    List.of(reportError("constraints map is null")));
        }

        List<Bpmn2OclEvaluationResult> results = Bpmn2OclEvaluator.evaluateAll(
                constraints,
                runtimeState.state(),
                runtimeState.selfBindings());
        return new ScenarioTraceVerificationReport(scenarioName, processName, runtimeState, results);
    }

    private static Bpmn2OclEvaluationResult reportError(String reason) {
        return new Bpmn2OclEvaluationResult(
                null, null, null, null, null, Status.ERROR, null, reason);
    }
}
