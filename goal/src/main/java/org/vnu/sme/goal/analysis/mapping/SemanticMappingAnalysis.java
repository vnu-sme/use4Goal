package org.vnu.sme.goal.analysis.mapping;

import java.util.List;
import java.util.Set;

/** Immutable result of the model-level iStar/BPMN semantic mapping analysis. */
public record SemanticMappingAnalysis(
        List<MappingEntry> mappings,
        List<TaskCoverage> taskCoverage,
        List<GoalCoverage> goalCoverage,
        List<ActivityCoverage> activityCoverage,
        List<Diagnostic> diagnostics) {

    public SemanticMappingAnalysis {
        mappings = List.copyOf(mappings);
        taskCoverage = List.copyOf(taskCoverage);
        goalCoverage = List.copyOf(goalCoverage);
        activityCoverage = List.copyOf(activityCoverage);
        diagnostics = List.copyOf(diagnostics);
    }

    public enum MappingKind { REALIZES, COMPOSITE_REALIZES, ENABLES }
    public enum CandidateStatus { STATICALLY_SUPPORTED, CANDIDATE }
    public enum CoverageStatus { COVERED, PARTIAL, UNCOVERED }
    public enum ActivityStatus { MAPPED, ENABLES_TASK, DIRECT_GOAL_EFFECT, CONTROL_ONLY, ORPHAN }
    public enum Severity { INFO, WARNING, ERROR }

    public record MappingEntry(
            String id,
            List<String> activityIds,
            String activityLabel,
            String processId,
            String laneRole,
            String taskId,
            String taskActor,
            List<String> supportedGoals,
            MappingKind kind,
            CandidateStatus candidateStatus,
            int score,
            Set<String> taskPostProperties,
            Set<String> coveredProperties,
            String evidence) {
        public MappingEntry {
            activityIds = List.copyOf(activityIds);
            supportedGoals = List.copyOf(supportedGoals);
            taskPostProperties = Set.copyOf(taskPostProperties);
            coveredProperties = Set.copyOf(coveredProperties);
        }

        public String stableKey() {
            return String.join("+", activityIds) + "|" + taskId + "|" + kind;
        }
    }

    public record TaskCoverage(String taskId, String actor, CoverageStatus status,
                               List<String> mappingIds, Set<String> missingPostProperties) {
        public TaskCoverage {
            mappingIds = List.copyOf(mappingIds);
            missingPostProperties = Set.copyOf(missingPostProperties);
        }
    }

    public record GoalCoverage(String goalId, String actor, boolean root, CoverageStatus status,
                               List<String> mappingIds, List<String> directActivityIds) {
        public GoalCoverage {
            mappingIds = List.copyOf(mappingIds);
            directActivityIds = List.copyOf(directActivityIds);
        }
    }

    public record ActivityCoverage(String activityId, String processId, String laneRole,
                                   ActivityStatus status, List<String> mappingIds,
                                   List<String> directlyAffectedGoals) {
        public ActivityCoverage {
            mappingIds = List.copyOf(mappingIds);
            directlyAffectedGoals = List.copyOf(directlyAffectedGoals);
        }
    }

    public record Diagnostic(Severity severity, String code, String elementKind,
                             String elementId, String message) {}
}
