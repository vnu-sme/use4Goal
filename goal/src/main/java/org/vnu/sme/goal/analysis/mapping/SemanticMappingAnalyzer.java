package org.vnu.sme.goal.analysis.mapping;

import static org.vnu.sme.goal.analysis.mapping.SemanticMappingAnalysis.*;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclAttribute;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.bpmn.mm.Activity;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Lane;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;
import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.AndRefinement;
import org.vnu.sme.goal.dsl.istar.mm.Dependency;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.GoalActivationGraph;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.OrRefinement;
import org.vnu.sme.goal.dsl.istar.mm.Refinement;
import org.vnu.sme.goal.dsl.istar.mm.Task;

/**
 * Generates model-level Task--Activity candidates from the shared ACL vocabulary.
 * The result is deliberately evidence, not a proof: Rodin/ProB obligations are added later.
 */
public final class SemanticMappingAnalyzer {
    private SemanticMappingAnalyzer() {}

    private record TaskInfo(String id, String actor, String pre, Set<String> preProperties,
                            Map<String, Set<String>> preValues, String post, Set<String> properties,
                            Map<String, Set<String>> values) {}
    private record ActivityInfo(String id, String label, String process, String lane, String post,
                                Set<String> properties, Map<String, Set<String>> values) {}
    private record GoalInfo(String id, String actor, boolean root, Set<String> properties,
                            Map<String, Set<String>> values) {}

    public static SemanticMappingAnalysis analyze(AclModel acl, GoalModel goals, BpmnModel bpmn) {
        Objects.requireNonNull(acl); Objects.requireNonNull(goals); Objects.requireNonNull(bpmn);
        Set<String> vocabulary = propertyVocabulary(acl);
        List<TaskInfo> tasks = tasks(goals, vocabulary);
        List<ActivityInfo> activities = activities(bpmn, vocabulary);
        List<GoalInfo> goalInfos = goals(goals, vocabulary);
        Map<String, Set<String>> intentionParents = intentionParents(goals);

        List<MappingEntry> mappings = new ArrayList<>();
        for (TaskInfo task : tasks) {
            List<MappingEntry> direct = new ArrayList<>();
            for (ActivityInfo activity : activities) {
                Set<String> overlap = intersection(task.properties(), activity.properties());
                if (overlap.isEmpty() || !valuesCompatible(task, activity, overlap)) continue;
                int coverage = task.properties().isEmpty() ? 0
                        : (int)Math.round(70.0 * overlap.size() / task.properties().size());
                int role = Objects.equals(task.actor(), activity.lane()) ? 20 : 0;
                int name = nameSimilarity(task.id(), activity.id(), activity.label()) ? 10 : 0;
                boolean complete = overlap.containsAll(task.properties()) && !task.properties().isEmpty();
                int score = Math.min(100, coverage + role + name);
                if (!complete && score < 35) continue;
                List<String> supported = supportedGoals(task.id(), goals, intentionParents);
                direct.add(new MappingEntry(mappingId(List.of(activity.id()), task.id()),
                        List.of(activity.id()), activity.label(), activity.process(), activity.lane(),
                        task.id(), task.actor(), supported, MappingKind.REALIZES,
                        complete ? CandidateStatus.STATICALLY_SUPPORTED : CandidateStatus.CANDIDATE,
                        score, task.properties(), overlap,
                        evidence(task, List.of(activity), overlap, role > 0, complete)));
            }
            direct.sort(Comparator.comparingInt(MappingEntry::score).reversed()
                    .thenComparing(x -> x.activityIds().get(0)));
            mappings.addAll(direct);
            if (direct.stream().noneMatch(x -> x.candidateStatus() == CandidateStatus.STATICALLY_SUPPORTED))
                composite(task, activities, goals, intentionParents).ifPresent(mappings::add);
            for(ActivityInfo activity:activities) {
                Set<String> overlap=intersection(task.preProperties(),activity.properties());
                if(overlap.isEmpty()||!valuesCompatible(task.preValues(),activity,overlap)) continue;
                if(direct.stream().anyMatch(x->x.activityIds().contains(activity.id()))) continue;
                boolean complete=overlap.containsAll(task.preProperties())&&!task.preProperties().isEmpty();
                int role=Objects.equals(task.actor(),activity.lane())?15:0;
                int score=Math.min(100,(int)Math.round(55.0*overlap.size()/task.preProperties().size())+role);
                mappings.add(new MappingEntry(mappingId(List.of(activity.id()),task.id())+"__ENABLES",
                        List.of(activity.id()),activity.label(),activity.process(),activity.lane(),task.id(),task.actor(),
                        supportedGoals(task.id(),goals,intentionParents),MappingKind.ENABLES,
                        complete?CandidateStatus.STATICALLY_SUPPORTED:CandidateStatus.CANDIDATE,score,
                        task.preProperties(),overlap,"covers Task precondition; properties="+String.join(",",overlap)
                                +"; role="+(role>0?"compatible":"cross-role")+"; taskPre="+oneLine(task.pre())
                                +"; activityPost="+oneLine(activity.post())));
            }
        }

        mappings.sort(Comparator.comparing(MappingEntry::taskId)
                .thenComparing(Comparator.comparingInt(MappingEntry::score).reversed())
                .thenComparing(MappingEntry::id));
        List<TaskCoverage> taskCoverage = taskCoverage(tasks, mappings);
        List<GoalCoverage> goalCoverage = goalCoverage(goalInfos, activities, mappings);
        List<ActivityCoverage> activityCoverage = activityCoverage(activities, goalInfos, mappings);
        List<Diagnostic> diagnostics = diagnostics(taskCoverage, goalCoverage, activityCoverage);
        return new SemanticMappingAnalysis(mappings, taskCoverage, goalCoverage, activityCoverage, diagnostics);
    }

    private static java.util.Optional<MappingEntry> composite(TaskInfo task, List<ActivityInfo> activities,
            GoalModel goals, Map<String, Set<String>> parents) {
        if (task.properties().size() < 2) return java.util.Optional.empty();
        MappingEntry best = null;
        for (int i = 0; i < activities.size(); i++) for (int j = i + 1; j < activities.size(); j++) {
            ActivityInfo a = activities.get(i), b = activities.get(j);
            if (!a.process().equals(b.process())) continue;
            Set<String> covered = intersection(task.properties(), union(a.properties(), b.properties()));
            if (!covered.containsAll(task.properties())) continue;
            if (!valuesCompatible(task, a, intersection(task.properties(), a.properties()))
                    || !valuesCompatible(task, b, intersection(task.properties(), b.properties()))) continue;
            int role = Objects.equals(task.actor(), a.lane()) || Objects.equals(task.actor(), b.lane()) ? 10 : 0;
            int score = 75 + role;
            MappingEntry current = new MappingEntry(mappingId(List.of(a.id(), b.id()), task.id()),
                    List.of(a.id(), b.id()), a.label() + " + " + b.label(), a.process(),
                    same(a.lane(), b.lane()), task.id(), task.actor(),
                    supportedGoals(task.id(), goals, parents), MappingKind.COMPOSITE_REALIZES,
                    CandidateStatus.STATICALLY_SUPPORTED, score, task.properties(), covered,
                    evidence(task, List.of(a, b), covered, role > 0, true));
            if (best == null || current.score() > best.score()) best = current;
        }
        return java.util.Optional.ofNullable(best);
    }

    private static List<TaskCoverage> taskCoverage(List<TaskInfo> tasks, List<MappingEntry> mappings) {
        List<TaskCoverage> result = new ArrayList<>();
        for (TaskInfo task : tasks) {
            List<MappingEntry> found = mappings.stream().filter(x -> x.taskId().equals(task.id()))
                    .filter(x->x.kind()!=MappingKind.ENABLES).toList();
            Set<String> covered = new LinkedHashSet<>(); found.forEach(x -> covered.addAll(x.coveredProperties()));
            Set<String> missing = difference(task.properties(), covered);
            CoverageStatus status = found.isEmpty() ? CoverageStatus.UNCOVERED
                    : missing.isEmpty() ? CoverageStatus.COVERED : CoverageStatus.PARTIAL;
            result.add(new TaskCoverage(task.id(), task.actor(), status,
                    found.stream().map(MappingEntry::id).toList(), missing));
        }
        return List.copyOf(result);
    }

    private static List<GoalCoverage> goalCoverage(List<GoalInfo> goals, List<ActivityInfo> activities,
                                                    List<MappingEntry> mappings) {
        List<GoalCoverage> result = new ArrayList<>();
        for (GoalInfo goal : goals) {
            List<String> viaTasks = mappings.stream().filter(x -> x.kind()!=MappingKind.ENABLES)
                    .filter(x -> x.supportedGoals().contains(goal.id()))
                    .map(MappingEntry::id).distinct().toList();
            List<String> direct = activities.stream()
                    .filter(x -> !intersection(goal.properties(), x.properties()).isEmpty())
                    .map(ActivityInfo::id).distinct().toList();
            CoverageStatus status = !viaTasks.isEmpty() ? CoverageStatus.COVERED
                    : !direct.isEmpty() ? CoverageStatus.PARTIAL : CoverageStatus.UNCOVERED;
            result.add(new GoalCoverage(goal.id(), goal.actor(), goal.root(), status, viaTasks, direct));
        }
        return List.copyOf(result);
    }

    private static List<ActivityCoverage> activityCoverage(List<ActivityInfo> activities, List<GoalInfo> goals,
                                                            List<MappingEntry> mappings) {
        List<ActivityCoverage> result = new ArrayList<>();
        for (ActivityInfo activity : activities) {
            List<String> mapIds = mappings.stream().filter(x -> x.activityIds().contains(activity.id()))
                    .map(MappingEntry::id).toList();
            boolean realizes=mappings.stream().anyMatch(x->x.activityIds().contains(activity.id())
                    &&x.kind()!=MappingKind.ENABLES);
            boolean enables=mappings.stream().anyMatch(x->x.activityIds().contains(activity.id())
                    &&x.kind()==MappingKind.ENABLES);
            List<String> directGoals = goals.stream()
                    .filter(x -> !intersection(x.properties(), activity.properties()).isEmpty())
                    .map(GoalInfo::id).toList();
            ActivityStatus status = realizes ? ActivityStatus.MAPPED : enables ? ActivityStatus.ENABLES_TASK
                    : !directGoals.isEmpty() ? ActivityStatus.DIRECT_GOAL_EFFECT
                    : activity.post().isBlank() ? ActivityStatus.CONTROL_ONLY : ActivityStatus.ORPHAN;
            result.add(new ActivityCoverage(activity.id(), activity.process(), activity.lane(), status,
                    mapIds, directGoals));
        }
        return List.copyOf(result);
    }

    private static List<Diagnostic> diagnostics(List<TaskCoverage> tasks, List<GoalCoverage> goals,
                                                List<ActivityCoverage> activities) {
        List<Diagnostic> result = new ArrayList<>();
        tasks.stream().filter(x -> x.status() != CoverageStatus.COVERED).forEach(x -> result.add(new Diagnostic(
                Severity.WARNING, x.status() == CoverageStatus.UNCOVERED ? "UNMAPPED_TASK" : "PARTIAL_TASK_COVERAGE",
                "iStar Task", x.taskId(), x.status() == CoverageStatus.UNCOVERED
                        ? "No BPMN activity or fragment covers the Task postcondition."
                        : "Missing postcondition properties: " + String.join(", ", x.missingPostProperties()))));
        goals.stream().filter(x -> x.root() && x.status() == CoverageStatus.UNCOVERED).forEach(x -> result.add(
                new Diagnostic(Severity.ERROR, "UNCOVERED_ROOT_GOAL", "iStar Goal", x.goalId(),
                        "No mapped Task chain or direct Activity effect covers this root Goal.")));
        activities.stream().filter(x -> x.status() == ActivityStatus.ORPHAN).forEach(x -> result.add(new Diagnostic(
                Severity.WARNING, "ORPHAN_ACTIVITY", "BPMN Activity", x.activityId(),
                "The Activity has domain effects but no Task mapping or Goal effect was found.")));
        return List.copyOf(result);
    }

    private static List<TaskInfo> tasks(GoalModel model, Set<String> vocabulary) {
        List<TaskInfo> result = new ArrayList<>();
        for (Actor actor : model.getActors()) for (var element : actor.elements()) if (element instanceof Task task) {
            String pre = task.preconditions().stream().map(x -> x.oclBody()).reduce((a,b) -> a+" and "+b).orElse("");
            String post = task.postconditions().stream().map(x -> x.oclBody()).reduce((a,b) -> a+" and "+b).orElse("");
            result.add(new TaskInfo(task.id(), actor.name(), pre,footprint(pre,vocabulary),values(pre,vocabulary),
                    post, footprint(post, vocabulary), values(post, vocabulary)));
        }
        return List.copyOf(result);
    }

    private static List<GoalInfo> goals(GoalModel model, Set<String> vocabulary) {
        GoalActivationGraph graph = GoalActivationGraph.of(model);
        List<GoalInfo> result = new ArrayList<>();
        for (Actor actor : model.getActors()) for (var element : actor.elements()) if (element instanceof Goal goal) {
            String condition = goal.conditions().stream().map(x -> x.oclBody()).reduce((a,b)->a+" and "+b).orElse("");
            result.add(new GoalInfo(goal.id(), actor.name(), graph.isRoot(goal.id()), footprint(condition, vocabulary),
                    values(condition, vocabulary)));
        }
        return List.copyOf(result);
    }

    private static List<ActivityInfo> activities(BpmnModel model, Set<String> vocabulary) {
        List<ActivityInfo> result = new ArrayList<>();
        for (var process : model.processes()) collectActivities(process.flowElements()).forEach(activity -> {
            Lane lane = process.lanes().stream().filter(x -> contains(x.flowElements(), activity)).findFirst().orElse(null);
            String post = activity.postconditions().stream().map(x -> x.oclBody()).reduce((a,b)->a+" and "+b).orElse("");
            String label = activity.name() == null || activity.name().isBlank() ? activity.id() : activity.name();
            result.add(new ActivityInfo(activity.id(), label, process.id(), lane == null ? "" : lane.id(), post,
                    footprint(post, vocabulary), values(post, vocabulary)));
        });
        return List.copyOf(result);
    }

    private static boolean contains(List<FlowElement> elements, Activity target) {
        for (FlowElement element : elements) {
            if (element == target) return true;
            if (element instanceof SubProcess sub && contains(sub.flowElements(), target)) return true;
        }
        return false;
    }

    private static List<Activity> collectActivities(List<FlowElement> elements) {
        List<Activity> result = new ArrayList<>();
        for (FlowElement element : elements) if (element instanceof Activity activity) {
            result.add(activity);
            if (activity instanceof SubProcess sub) result.addAll(collectActivities(sub.flowElements()));
        }
        return result;
    }

    private static Map<String, Set<String>> intentionParents(GoalModel model) {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        for (Actor actor : model.getActors()) for (Refinement refinement : actor.refinements()) {
            if (refinement instanceof AndRefinement and) and.children().forEach(x -> edge(result, x, and.parent()));
            else if (refinement instanceof OrRefinement or) edge(result, or.child(), or.parent());
        }
        for (Dependency dependency : model.getDependencies())
            if (dependency.dependeeElmt() != null && dependency.dependerElmt() != null)
                edge(result, dependency.dependeeElmt(), dependency.dependerElmt());
        return result;
    }

    private static List<String> supportedGoals(String taskId, GoalModel model, Map<String, Set<String>> parents) {
        LinkedHashSet<String> seen = new LinkedHashSet<>(), goals = new LinkedHashSet<>();
        List<String> queue = new ArrayList<>(List.of(taskId));
        for (int i=0; i<queue.size(); i++) {
            String current = queue.get(i); if (!seen.add(current)) continue;
            if (model.findElement(current).orElse(null) instanceof Goal) goals.add(current);
            for (String parent : parents.getOrDefault(current, Set.of())) if (!seen.contains(parent)) queue.add(parent);
        }
        return List.copyOf(goals);
    }

    private static Set<String> propertyVocabulary(AclModel acl) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        acl.roles().forEach(x -> attributes(result, x.attributes()));
        acl.entities().forEach(x -> attributes(result, x.attributes()));
        acl.groups().forEach(x -> attributes(result, x.attributes()));
        acl.relations().forEach(x -> {
            result.add(x.name()); x.endpoints().forEach(e -> e.roleName().ifPresent(result::add));
        });
        return result;
    }

    private static void attributes(Set<String> result, Collection<AclAttribute> attributes) {
        attributes.forEach(x -> result.add(x.name()));
    }

    private static Set<String> footprint(String source, Set<String> vocabulary) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String property : vocabulary) if (Pattern.compile("(?i)(?<!#)\\b"+Pattern.quote(property)+"\\b")
                .matcher(source).find()) result.add(property);
        return result;
    }

    private static Map<String, Set<String>> values(String source, Set<String> vocabulary) {
        Map<String, Set<String>> result = new HashMap<>();
        String flat = source.replaceAll("\\s+", " ");
        for (String property : vocabulary) {
            Pattern p = Pattern.compile("(?i)(?<!#)(not\\s+)?(?:[A-Za-z_]\\w*\\.)*"+Pattern.quote(property)
                    +"\\b(?:\\s*=\\s*(#[A-Za-z_]\\w*|'[^']*'|true|false))?");
            Matcher matcher = p.matcher(flat); LinkedHashSet<String> found = new LinkedHashSet<>();
            while (matcher.find()) {
                String value = matcher.group(2);
                if (value == null) value = matcher.group(1) == null ? "TRUE" : "FALSE";
                found.add(value.toUpperCase(Locale.ROOT));
            }
            if (!found.isEmpty()) result.put(property, Set.copyOf(found));
        }
        return Map.copyOf(result);
    }

    private static boolean valuesCompatible(TaskInfo task, ActivityInfo activity, Set<String> overlap) {
        return valuesCompatible(task.values(),activity,overlap);
    }

    private static boolean valuesCompatible(Map<String,Set<String>> expectedValues, ActivityInfo activity,
                                            Set<String> overlap) {
        for (String property : overlap) {
            Set<String> expected = expectedValues.getOrDefault(property, Set.of());
            Set<String> produced = activity.values().getOrDefault(property, Set.of());
            if (!expected.isEmpty() && !produced.isEmpty() && intersection(expected, produced).isEmpty()) return false;
        }
        return true;
    }

    private static String evidence(TaskInfo task, List<ActivityInfo> activities, Set<String> overlap,
                                   boolean sameRole, boolean complete) {
        return (complete ? "covers Task postcondition" : "partial postcondition overlap")
                + "; properties=" + String.join(",", overlap)
                + "; role=" + (sameRole ? "compatible" : "cross-role/needs proof")
                + "; taskPost=" + oneLine(task.post())
                + "; activityPost=" + activities.stream().map(x -> oneLine(x.post())).reduce((a,b)->a+" + "+b).orElse("");
    }

    private static boolean nameSimilarity(String task, String activityId, String activityLabel) {
        String a = words(task), b = words(activityId), c = words(activityLabel);
        return a.equals(b) || a.equals(c) || b.contains(a) || c.contains(a) || a.contains(b);
    }

    private static String words(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .replaceAll("([a-z])([A-Z])", "$1 $2").replaceAll("[^A-Za-z0-9]", "")
                .toLowerCase(Locale.ROOT);
    }

    private static String mappingId(List<String> activities, String task) {
        return "MAP_" + id(String.join("_", activities)) + "__" + id(task);
    }
    private static String id(String value) { return value.replaceAll("[^A-Za-z0-9_]", "_").replaceAll("_+", "_"); }
    private static String oneLine(String value) { return value == null ? "" : value.replaceAll("\\s+", " ").trim(); }
    private static String same(String a, String b) { return Objects.equals(a,b) ? a : "multiple"; }
    private static void edge(Map<String, Set<String>> graph, String from, String to) {
        graph.computeIfAbsent(from, ignored -> new LinkedHashSet<>()).add(to);
    }
    private static <T> Set<T> intersection(Collection<T> a, Collection<T> b) {
        LinkedHashSet<T> result = new LinkedHashSet<>(a); result.retainAll(b); return result;
    }
    private static <T> Set<T> union(Collection<T> a, Collection<T> b) {
        LinkedHashSet<T> result = new LinkedHashSet<>(a); result.addAll(b); return result;
    }
    private static <T> Set<T> difference(Collection<T> a, Collection<T> b) {
        LinkedHashSet<T> result = new LinkedHashSet<>(a); result.removeAll(b); return result;
    }
}
