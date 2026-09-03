package org.vnu.sme.goal.feature.aclstate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ConsistencyVerdict;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.GoalEvidence;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.GoalStatus;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.MappingEntry;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ProcessResult;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ValidationResult;

/** Builds the single, conditional report shown after integrated verification. */
public final class ConformanceReportBuilder {
    public record Row(String type, String process, String bpmnElement, String iStarElement,
                      String status, String evidenceOrAction) {}

    private ConformanceReportBuilder() {}

    public static List<Row> build(ValidationResult result) {
        List<Row> rows = new ArrayList<>();
        rows.add(new Row("RESULT", "-", "-", String.join(", ", result.rootGoals()),
                result.consistency() + " / " + result.risk(), result.summary()));

        if (result.consistency() == ConsistencyVerdict.CONSISTENT) {
            result.mappings().forEach(mapping -> rows.add(mappingRow(mapping, "INFERRED_MAPPING",
                    "score=" + mapping.score() + "; " + mapping.basis())));
            return List.copyOf(rows);
        }

        if (result.consistency() == ConsistencyVerdict.INCONCLUSIVE
                || result.consistency() == ConsistencyVerdict.BPMN_ONLY) {
            rows.add(new Row("DIAGNOSTIC", "-", "-", "-", "NO_COUNTERMEASURE",
                    "The integrated verdict is inconclusive; no repair is proposed without a counterexample."));
            return List.copyOf(rows);
        }

        for (ProcessResult process : result.processes()) {
            addPartialMappings(rows, result.mappings(), process);
            String affectedGoals = process.counterexampleGoals().stream()
                    .filter(goal -> goal.status() != GoalStatus.SATISFIED)
                    .map(GoalEvidence::goal).collect(Collectors.joining(", "));
            for (String hint : process.repairHints()) {
                rows.add(new Row("COUNTERMEASURE", process.processId(),
                        process.invalidatingStep(), affectedGoals.isBlank() ? "-" : affectedGoals,
                        "CANDIDATE", hint));
            }
            if (process.repairHints().isEmpty() && !process.counterexample().isEmpty()) {
                rows.add(new Row("COUNTERMEASURE", process.processId(),
                        process.invalidatingStep(), affectedGoals.isBlank() ? "-" : affectedGoals,
                        "REVIEW_REQUIRED",
                        "Restore the unsatisfied goal conditions before process completion."));
            }
        }
        return List.copyOf(rows);
    }

    private static void addPartialMappings(List<Row> rows, List<MappingEntry> mappings,
                                           ProcessResult process) {
        Map<String, Integer> routeIndex = new LinkedHashMap<>();
        for (int index = 0; index < process.counterexample().size(); index++) {
            routeIndex.putIfAbsent(source(process.counterexample().get(index)), index);
        }
        String invalidatingActivity = source(process.invalidatingStep());
        Integer invalidatingIndex = routeIndex.get(invalidatingActivity);
        for (MappingEntry mapping : mappings) {
            if (!mapping.processId().equals(process.processId())) continue;
            Integer index = routeIndex.get(mapping.activityId());
            if (index == null) continue;
            String status;
            if (invalidatingIndex != null && index.equals(invalidatingIndex)) {
                status = "INVALIDATING_STEP";
            } else if (invalidatingIndex == null || index < invalidatingIndex) {
                status = "MAPPED_BEFORE_FAILURE";
            } else {
                continue;
            }
            rows.add(mappingRow(mapping, status,
                    "counterexample step=" + (index + 1) + "; score=" + mapping.score()
                            + "; " + mapping.basis()));
        }
    }

    private static Row mappingRow(MappingEntry mapping, String status, String evidence) {
        String target = mapping.leafId().equals("-") ? "unmapped"
                : mapping.actor() + "." + mapping.leafId();
        String effectiveStatus = mapping.leafId().equals("-") ? "UNMAPPED" : status;
        return new Row("MAPPING", mapping.processId(),
                mapping.activityName() + " [" + mapping.activityId() + "]",
                target, effectiveStatus, evidence);
    }

    private static String source(String routeStep) {
        if (routeStep == null) return "";
        int separator = routeStep.indexOf(" -> ");
        return separator < 0 ? "" : routeStep.substring(0, separator).trim();
    }
}
