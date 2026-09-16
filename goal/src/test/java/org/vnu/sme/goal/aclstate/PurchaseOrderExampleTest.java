package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ConsistencyVerdict;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.RiskVerdict;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

/**
 * Verifies the Purchase Order case study in examples/PurchaseOrder/.
 *
 * Expected result: WEAK_CONFORMANCE.
 * 5 execution paths: 4 normal paths (via full approval chain) are conformant;
 * 1 emergency bypass path violates PurchaseApprovalIntegrity (Maintain) because
 * departmentApproved = false when the order is placed.
 * Risk verdict: RISKY (safety MAINTAIN goal violated in bypass path).
 */
class PurchaseOrderExampleTest {

    private static final Path EXAMPLE =
            Path.of("../examples/PurchaseOrder");

    @Test
    void purchaseOrderAchievesWeakConformance() throws Exception {
        AclStateEvaluationSession session = AclStateEvaluationSession.load(
                EXAMPLE.resolve("purchase-order.csl"));
        session.loadBpmn(EXAMPLE.resolve("purchase-order.bpmn2"));
        session.loadIStar(EXAMPLE.resolve("purchase-order.istar"));
        session.loadBoundary(EXAMPLE.resolve("purchase-order.cslboundary"));

        long started = System.nanoTime();
        var result = session.validateWholeBpmnProcess();
        long elapsedMillis = (System.nanoTime() - started) / 1_000_000;

        System.out.printf(
                "PurchaseOrder: executions=%d, conformant=%d, nonConformant=%d, risky=%d, time=%d ms%n",
                result.realizableExecutions(),
                result.goalAchievingExecutions(),
                result.nonGoalAchievingExecutions(),
                result.riskyExecutions(),
                elapsedMillis);
        System.out.println(result.summary());

        assertTrue(result.realizableExecutions() >= 4,
                "Expected at least 4 realizable executions. " + result.summary());

        assertEquals(ConsistencyVerdict.WEAK_CONFORMANCE, result.consistency(),
                "Expected WEAK_CONFORMANCE (bypass path violates PurchaseApprovalIntegrity). "
                        + result.summary());

        assertEquals(RiskVerdict.RISKY, result.risk(),
                "Expected RISKY (PurchaseApprovalIntegrity MAINTAIN violated in bypass). "
                        + result.summary());

        assertTrue(result.goalAchievingExecutions() > 0,
                "Expected at least one conformant execution. " + result.summary());

        assertTrue(result.nonGoalAchievingExecutions() > 0,
                "Expected at least one non-conformant execution (bypass). " + result.summary());
    }
}
