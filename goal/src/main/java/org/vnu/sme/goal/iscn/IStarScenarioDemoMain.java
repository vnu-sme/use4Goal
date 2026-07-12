package org.vnu.sme.goal.iscn;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Task;
import org.vnu.sme.goal.iscn.mm.AggregateResult;
import org.vnu.sme.goal.iscn.parser.IStarScenarioCompiler;
import org.vnu.sme.goal.iscn.view.IStarScenarioView;

/**
 * Runnable end-to-end demonstration of the i* Scenario mechanism, using
 * goal/src/main/resources/examples/job_application_review/job_application_review.{istar,iscn}.
 *
 * <p>Run (after {@code mvn -pl goal compile}), from the repository root:
 * <pre>
 *   java -cp goal/target/classes:$(find ~/.m2 -iname 'antlr4-runtime*.jar' | head -1) \
 *        org.vnu.sme.goal.iscn.IStarScenarioDemoMain
 * </pre>
 */
public final class IStarScenarioDemoMain {

    public static void main(String[] args) throws Exception {
        Path file = Path.of(args.length > 0 ? args[0]
                : "goal/src/main/resources/examples/job_application_review/job_application_review.iscn");
        boolean printSpec = List.of(args).contains("--spec");

        IStarScenarioCompiler.Result result = IStarScenarioCompiler.compile(file);
        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }

        System.out.println("Scenario  : " + result.scenarioModel().name());
        System.out.println("Model     : " + result.modelFile());
        System.out.println("Instances : " + result.markings().size());
        System.out.println();

        if (result.evaluation() != null) {
            System.out.println("-- instance-level scenario graph --");
            boolean printed = false;
            result.evaluation().instanceMarking().goalTaskStatuses().forEach((id, status) -> {
                if (status != GoalTaskStatus.UNKNOWN) System.out.println("  " + id + " = " + status);
            });
            printed |= result.evaluation().instanceMarking().goalTaskStatuses().values().stream()
                    .anyMatch(status -> status != GoalTaskStatus.UNKNOWN);
            result.evaluation().instanceMarking().qualityStatuses().forEach((id, status) -> {
                if (status != QualityStatus.UNKNOWN) System.out.println("  " + id + " = " + status);
            });
            printed |= result.evaluation().instanceMarking().qualityStatuses().values().stream()
                    .anyMatch(status -> status != QualityStatus.UNKNOWN);
            if (!printed) {
                result.evaluation().instanceModel().allElements().values().forEach(element ->
                        printInitialObject(element, result.evaluation().instanceMarking()));
            }
            System.out.println();
        }

        for (var entry : result.markings().entrySet()) {
            String label = entry.getKey().equals(IStarScenarioCompiler.DEFAULT_INSTANCE) ? "(default)" : entry.getKey();
            System.out.println("-- " + label + " --");
            entry.getValue().goalTaskStatuses().forEach((id, status) -> {
                if (status != GoalTaskStatus.UNKNOWN) System.out.println("  " + id + " = " + status);
            });
            entry.getValue().qualityStatuses().forEach((id, status) -> {
                if (status != QualityStatus.UNKNOWN) System.out.println("  " + id + " = " + status);
            });
            System.out.println();
        }

        if (!result.aggregates().isEmpty()) {
            System.out.println("Aggregates:");
            for (AggregateResult a : result.aggregates()) {
                System.out.println("  " + a.label() + " : " + a.mode() + " over " + a.elementId()
                        + " -> " + (a.holds() ? "HOLDS" : "FAILS")
                        + " (" + a.satisfiedInstances().size() + "/" + a.allInstances().size() + ")");
                if (!a.holds()) {
                    List<String> unsatisfied = new ArrayList<>(a.allInstances());
                    unsatisfied.removeAll(a.satisfiedInstances());
                    System.out.println("    not satisfied by: " + String.join(", ", unsatisfied));
                }
            }
        }

        if (printSpec && result.evaluation() != null) {
            System.out.println();
            System.out.println("-- specification text --");
            System.out.println(IStarScenarioView.specificationText(result));
        }
    }

    private static void printInitialObject(IntentionalElement element,
                                           org.vnu.sme.goal.conformance.semantics.IStarMarking marking) {
        switch (element) {
            case Goal g -> System.out.println("  " + g.id() + " = " + marking.goalTaskStatus(g.id()));
            case Task t -> System.out.println("  " + t.id() + " = " + marking.goalTaskStatus(t.id()));
            case Quality q -> System.out.println("  " + q.id() + " = " + marking.qualityStatus(q.id()));
            default -> { }
        }
    }
}
