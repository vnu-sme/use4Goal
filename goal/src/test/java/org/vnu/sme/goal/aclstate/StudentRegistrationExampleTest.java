package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ConsistencyVerdict;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.RiskVerdict;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

/**
 * Verifies the Student Registration case study in examples/StudentRegistration/.
 *
 * Expected result: CONSISTENT (Strong Conformance).
 * All 16 execution paths (2^4 XOR gateways) satisfy all iStar goals because
 * RegistrationIntegrity accepts both formal and workaround alternatives at each gateway.
 * Risk verdict: NON_RISKY (no MAINTAIN goal violated in any path).
 */
class StudentRegistrationExampleTest {

    private static final Path EXAMPLE =
            Path.of("../examples/StudentRegistration");

    @Test
    void studentRegistrationAchievesStrongConformance() throws Exception {
        AclStateEvaluationSession session = AclStateEvaluationSession.load(
                EXAMPLE.resolve("student-registration.csl"));
        session.loadBpmn(EXAMPLE.resolve("student-registration.bpmn2"));
        session.loadIStar(EXAMPLE.resolve("student-registration.istar"));
        session.loadBoundary(EXAMPLE.resolve("student-registration.cslboundary"));

        long started = System.nanoTime();
        var result = session.validateWholeBpmnProcess();
        long elapsedMillis = (System.nanoTime() - started) / 1_000_000;

        System.out.printf(
                "StudentRegistration: executions=%d, conformant=%d, nonConformant=%d, risky=%d, time=%d ms%n",
                result.realizableExecutions(),
                result.goalAchievingExecutions(),
                result.nonGoalAchievingExecutions(),
                result.riskyExecutions(),
                elapsedMillis);
        System.out.println(result.summary());

        assertTrue(result.realizableExecutions() >= 8,
                "Expected at least 8 realizable executions, got " + result.realizableExecutions()
                        + ". " + result.summary());

        assertEquals(ConsistencyVerdict.CONSISTENT, result.consistency(),
                "Expected CONSISTENT (Strong Conformance). " + result.summary());

        assertEquals(RiskVerdict.NON_RISKY, result.risk(),
                "Expected NON_RISKY (no safety goal violated). " + result.summary());

        assertEquals(0, result.nonGoalAchievingExecutions(),
                "Expected zero non-conformant executions. " + result.summary());
    }
}
