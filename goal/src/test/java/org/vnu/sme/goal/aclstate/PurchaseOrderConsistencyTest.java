package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ConsistencyVerdict;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.RiskVerdict;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

class PurchaseOrderConsistencyTest {
    private static final Path EXAMPLE = Path.of(
            "src/main/resources/examples/purchase_order");

    @Test
    void evaluatesPurchaseOrderProcess() throws Exception {
        AclStateEvaluationSession session = AclStateEvaluationSession.load(
                EXAMPLE.resolve("purchase_order.acl"));
        session.loadBpmn(EXAMPLE.resolve("purchase_order.bpmn2"));
        session.loadIStar(EXAMPLE.resolve("purchase_order.istar"));
        session.loadBoundary(EXAMPLE.resolve("purchase_order.aclboundary"));

        long started = System.nanoTime();
        var result = session.validateWholeBpmnProcess();
        long elapsedMillis = (System.nanoTime() - started) / 1_000_000;
        System.out.printf("PurchaseOrder: executions=%d, conformant=%d, "
                        + "nonConformant=%d, risky=%d, time=%d ms%n",
                result.realizableExecutions(), result.goalAchievingExecutions(),
                result.nonGoalAchievingExecutions(), result.riskyExecutions(), elapsedMillis);
        System.out.println(result.summary());

        assertTrue(result.realizableExecutions() > 0, result::summary);
    }
}
