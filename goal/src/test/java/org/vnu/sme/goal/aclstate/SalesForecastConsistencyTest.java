package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

/**
 * Whole-process consistency evaluation for the SalesForecast case study.
 * Uses the ACL v2.0 model from goal/src/main/resources/examples/sales_forecast/.
 */
class SalesForecastConsistencyTest {
    private static final Path EXAMPLE = Path.of(
            "src/main/resources/examples/sales_forecast");

    @Test
    void evaluatesSalesForecastWholeProcess() throws Exception {
        AclStateEvaluationSession session = AclStateEvaluationSession.load(
                EXAMPLE.resolve("sales_forecast.acl"));
        session.loadBpmn(EXAMPLE.resolve("sales_forecast.bpmn2"));
        session.loadIStar(EXAMPLE.resolve("sales_forecast.istar"));
        // Use boundary from examples/Forecast which has correct scope (Deal 1)
        session.loadBoundary(Path.of("src/main/resources/examples/sales_forecast/sales_forecast.aclboundary"));

        long started = System.nanoTime();
        var result = session.validateWholeBpmnProcess();
        long elapsedMillis = (System.nanoTime() - started) / 1_000_000;

        System.out.printf("%n========== SalesForecast Metrics ==========%n");
        System.out.printf("  Realizable executions (branches): %d%n", result.realizableExecutions());
        System.out.printf("  Goal achieving (conformant):       %d%n", result.goalAchievingExecutions());
        System.out.printf("  Non-goal achieving (risky):        %d%n", result.nonGoalAchievingExecutions());
        System.out.printf("  Risky executions:                  %d%n", result.riskyExecutions());
        System.out.printf("  Consistency verdict:               %s%n", result.consistency());
        System.out.printf("  Risk verdict:                      %s%n", result.risk());
        System.out.printf("  Execution Time:                    %d ms (%.3f s)%n",
                elapsedMillis, elapsedMillis / 1000.0);
        System.out.printf("  Summary: %s%n", result.summary());
        System.out.printf("===========================================%n%n");

        assertTrue(result.realizableExecutions() > 0,
                () -> "SalesForecast must have at least 1 realizable execution.\nSummary: " + result.summary());
    }
}
