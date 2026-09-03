package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ConsistencyVerdict;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.RiskVerdict;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

class CustomerComplaintManagementConsistencyTest {
    private static final Path EXAMPLE = Path.of(
            "src/main/resources/examples/customer_complaint_management");

    @Test
    void detectsPrematureClosureAmongOtherwiseConformantComplaintRoutes() throws Exception {
        AclStateEvaluationSession session = AclStateEvaluationSession.load(
                EXAMPLE.resolve("customer_complaint_management.acl"));
        session.loadBpmn(EXAMPLE.resolve("customer_complaint_management.bpmn2"));
        session.loadIStar(EXAMPLE.resolve("customer_complaint_management.istar"));
        session.loadBoundary(EXAMPLE.resolve("customer_complaint_management.aclboundary"));

        long started = System.nanoTime();
        var result = session.validateWholeBpmnProcess();
        long elapsedMillis = (System.nanoTime() - started) / 1_000_000;
        System.out.printf("CustomerComplaintManagement: executions=%d, conformant=%d, "
                        + "nonConformant=%d, risky=%d, time=%d ms%n",
                result.realizableExecutions(), result.goalAchievingExecutions(),
                result.nonGoalAchievingExecutions(), result.riskyExecutions(), elapsedMillis);
        System.out.println(result.summary());

        assertEquals(ConsistencyVerdict.WEAKLY_CONSISTENT, result.consistency(), result::summary);
        assertEquals(RiskVerdict.RISK_PRONE, result.risk(), result::summary);
        assertEquals(10, result.realizableExecutions(), result::summary);
        assertEquals(9, result.goalAchievingExecutions(), result::summary);
        assertEquals(1, result.nonGoalAchievingExecutions(), result::summary);
        assertEquals(1, result.riskyExecutions(), result::summary);
        assertTrue(result.processes().get(0).counterexample().stream()
                .anyMatch(step -> step.contains("closePendingComplaint")));
        assertTrue(result.processes().get(0).counterexampleGoals().stream()
                .anyMatch(goal -> goal.goal().endsWith("ResolutionIntegrity")));
    }
}
