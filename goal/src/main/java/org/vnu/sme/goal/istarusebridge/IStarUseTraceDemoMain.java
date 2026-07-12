package org.vnu.sme.goal.istarusebridge;

import java.nio.file.Path;
import java.util.List;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.istarusebridge.view.IStarUseTraceView;

/**
 * Runnable end-to-end demonstration of the .istar + .use + .soil bridge, using
 * goal/src/main/resources/examples/mtg/{mtg.istar,mtg_shadow.use,mtg.soil}.
 *
 * <p>Run (after {@code mvn -pl goal compile}), from the repository root:
 * <pre>
 *   java -cp goal/target/classes:$(mvn -q -pl goal dependency:build-classpath -Dmdep.outputFile=/dev/stdout) \
 *        org.vnu.sme.goal.istarusebridge.IStarUseTraceDemoMain
 * </pre>
 */
public final class IStarUseTraceDemoMain {

    public static void main(String[] args) throws Exception {
        Path dir = Path.of(args.length > 0 ? args[0] : "goal/src/main/resources/examples/mtg");
        boolean printSpec = List.of(args).contains("--spec");

        IStarUseTraceCompiler.Result result = IStarUseTraceCompiler.compile(
                dir.resolve("mtg.istar"), dir.resolve("mtg_shadow.use"), dir.resolve("mtg.soil"));

        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }

        System.out.println("Checkpoints: " + result.checkpoints().size());
        System.out.println();

        GoalTaskStatus[] lastTop = new GoalTaskStatus[4];
        String[] topGoals = {"HaveMeetingOrganized", "HaveMeetingScheduled", "MeetingAttendedByParticipant", "TimetableCollected"};

        for (var cp : result.checkpoints()) {
            StringBuilder changed = new StringBuilder();
            for (int i = 0; i < topGoals.length; i++) {
                GoalTaskStatus best = GoalTaskStatus.UNKNOWN;
                for (var e : cp.markings().entrySet()) {
                    GoalTaskStatus s = e.getValue().goalTaskStatus(topGoals[i]);
                    if (s == GoalTaskStatus.FULFILLED) best = GoalTaskStatus.FULFILLED;
                    else if (s == GoalTaskStatus.PENDING && best != GoalTaskStatus.FULFILLED) best = GoalTaskStatus.PENDING;
                }
                if (best != lastTop[i]) {
                    changed.append(topGoals[i]).append("=").append(best).append("  ");
                    lastTop[i] = best;
                }
            }
            if (!changed.isEmpty()) {
                System.out.printf("[%3d] %-55s -> %s%n", cp.index(), cp.soilLine(), changed);
            }
        }

        System.out.println();
        System.out.println("-- final per-instance markings --");
        var last = result.checkpoints().get(result.checkpoints().size() - 1);
        for (var e : last.markings().entrySet()) {
            System.out.println(e.getKey());
            e.getValue().goalTaskStatuses().forEach((id, s) -> {
                if (s != GoalTaskStatus.UNKNOWN) System.out.println("    " + id + " = " + s);
            });
        }

        if (printSpec) {
            System.out.println();
            System.out.println("-- specification text --");
            System.out.println(IStarUseTraceView.specificationText(result.model(), last));
        }
    }
}
