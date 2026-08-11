package org.vnu.sme.goal.feature.istarusescenario;

import java.nio.file.Path;
import java.util.List;

import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.trace.usetrace.view.IStarUseTraceView;

/**
 * Runnable end-to-end demonstration of the .istar + .use + .soil bridge.
 *
 * <p>Run (after {@code mvn -pl goal compile}), from the repository root:
 * <pre>
 *   java -cp goal/target/classes:$(mvn -q -pl goal dependency:build-classpath -Dmdep.outputFile=/dev/stdout) \
 *        org.vnu.sme.goal.feature.istarusescenario.IStarUseTraceDemoMain
 * </pre>
 */
public final class IStarUseTraceDemoMain {

    public static void main(String[] args) throws Exception {
        boolean printSpec = List.of(args).contains("--spec");
        List<String> positional = List.of(args).stream()
                .filter(a -> !a.equals("--spec"))
                .toList();

        Path istarFile;
        Path useFile;
        Path soilFile;
        if (positional.size() == 3) {
            istarFile = Path.of(positional.get(0));
            useFile = Path.of(positional.get(1));
            soilFile = Path.of(positional.get(2));
        } else {
            Path dir = Path.of(positional.size() > 0 ? positional.get(0) : "goal/src/main/resources/examples/mtg");
            istarFile = dir.resolve("mtg.istar");
            useFile = dir.resolve("mtg_shadow.use");
            soilFile = dir.resolve("mtg.soil");
        }

        IStarUseTraceCompiler.Result result = IStarUseTraceCompiler.compile(istarFile, useFile, soilFile);

        if (!result.ok()) {
            result.errors().forEach(System.err::println);
            System.exit(1);
        }

        System.out.println("Checkpoints: " + result.checkpoints().size());
        System.out.println();

        GoalTaskStatus[] lastTop = new GoalTaskStatus[4];
        String[] topGoals = {
                "MeetingOrganized",
                "SchedulingCompleted",
                "ParticipantsAttended",
                "TimetablesCollected"
        };

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
