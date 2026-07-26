package org.vnu.sme.goal.scenarioverification;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.sys.MObject;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult;
import org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclEvaluationResult.Status;

/** Immutable report for BPMN OCL evaluation over one externally supplied scenario state. */
public record ScenarioTraceVerificationReport(
        String scenarioName,
        String processName,
        ScenarioRuntimeState runtimeState,
        List<Bpmn2OclEvaluationResult> bpmnResults) {

    public ScenarioTraceVerificationReport {
        scenarioName = scenarioName == null || scenarioName.isBlank() ? "<unknown>" : scenarioName;
        processName = processName == null || processName.isBlank() ? "<unknown>" : processName;
        bpmnResults = List.copyOf(bpmnResults == null ? List.of() : bpmnResults);
    }

    public long passCount() {
        return count(Status.PASS);
    }

    public long failCount() {
        return count(Status.FAIL);
    }

    public long errorCount() {
        return count(Status.ERROR);
    }

    public boolean passed() {
        return failCount() == 0 && errorCount() == 0;
    }

    public String overallStatus() {
        if (errorCount() > 0) {
            return "ERROR";
        }
        if (failCount() > 0) {
            return "FAIL";
        }
        return "PASS";
    }

    public String toText() {
        StringBuilder text = new StringBuilder();
        text.append("Scenario: ").append(scenarioName).append(System.lineSeparator());
        text.append("Process: ").append(processName).append(System.lineSeparator());
        text.append(System.lineSeparator());

        text.append("Objects").append(System.lineSeparator());
        if (runtimeState == null || runtimeState.state() == null) {
            text.append("  <unavailable>").append(System.lineSeparator());
        } else {
            runtimeState.state().allObjects().stream()
                    .sorted(Comparator.comparing(MObject::name))
                    .forEach(object -> text.append("  ")
                            .append(object.name())
                            .append(" : ")
                            .append(object.cls().name())
                            .append(System.lineSeparator()));
        }
        text.append(System.lineSeparator());

        text.append("Self bindings").append(System.lineSeparator());
        if (runtimeState == null || runtimeState.selfBindings().isEmpty()) {
            text.append("  <none>").append(System.lineSeparator());
        } else {
            runtimeState.selfBindings().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> text.append("  ")
                            .append(entry.getKey())
                            .append(" -> ")
                            .append(entry.getValue().name())
                            .append(System.lineSeparator()));
        }
        text.append(System.lineSeparator());

        text.append("Evaluated BPMN constraints").append(System.lineSeparator());
        if (bpmnResults.isEmpty()) {
            text.append("  <none>").append(System.lineSeparator());
        } else {
            for (Bpmn2OclEvaluationResult result : bpmnResults) {
                text.append("  ")
                        .append(result.status())
                        .append(" ")
                        .append(result.constraintId() == null ? "<unknown>" : result.constraintId())
                        .append(System.lineSeparator());
                text.append("    owner: ")
                        .append(result.ownerKind() == null ? "<unknown>" : result.ownerKind())
                        .append(" ")
                        .append(result.ownerId() == null ? "<unknown>" : result.ownerId())
                        .append(", self: ")
                        .append(result.selfObjectName() == null ? "<missing>" : result.selfObjectName())
                        .append(", context: ")
                        .append(result.contextType() == null ? "<unknown>" : result.contextType())
                        .append(System.lineSeparator());
                text.append("    reason: ")
                        .append(result.reason() == null ? "<none>" : result.reason())
                        .append(System.lineSeparator());
            }
        }
        text.append(System.lineSeparator());

        text.append("Summary").append(System.lineSeparator());
        text.append("  Total: ").append(bpmnResults.size()).append(System.lineSeparator());
        text.append("  PASS: ").append(passCount()).append(System.lineSeparator());
        text.append("  FAIL: ").append(failCount()).append(System.lineSeparator());
        text.append("  ERROR: ").append(errorCount()).append(System.lineSeparator());
        text.append(System.lineSeparator());
        text.append("Overall: ").append(overallStatus()).append(System.lineSeparator());
        return text.toString();
    }

    private long count(Status status) {
        return bpmnResults.stream().filter(result -> result.status() == status).count();
    }
}
