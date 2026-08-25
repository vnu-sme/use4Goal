package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ConsistencyVerdict;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

class ProposalReviewWholeConsistencyTest {
    private static final Path EXAMPLE = Path.of(
            "src/main/resources/examples/sales_forecast/proposal_review_whole");

    @Test
    void detectsThatUpdateWithoutRevalidationBreaksOnlyOneBpmnBranch() throws Exception {
        AclStateEvaluationSession session = AclStateEvaluationSession.load(
                EXAMPLE.resolve("proposal_review.acl"));
        session.loadBpmn(EXAMPLE.resolve("proposal_review.bpmn2"));
        session.loadIStar(EXAMPLE.resolve("proposal_review.istar"));
        session.loadBoundary(EXAMPLE.resolve("proposal_review.aclboundary"));

        var result = session.validateWholeBpmnProcess();
        assertEquals(ConsistencyVerdict.WEAKLY_CONSISTENT, result.consistency(),
                () -> result.summary() + " " + result.processes());
        assertEquals(2, result.realizableExecutions(), result::summary);
        assertEquals(1, result.goalAchievingExecutions(), result::summary);
        assertTrue(result.rootGoals().contains("ProposalManager.ProposalSuccessfullyCompleted"));
        assertTrue(result.rootGoals().contains("Customer.ProposalReceived"));
        assertTrue(result.processes().get(0).counterexample().stream()
                .anyMatch(flow -> flow.contains("updateProposal")));
        assertTrue(result.mappings().stream().anyMatch(mapping ->
                mapping.activityId().equals("validateProposal")
                        && mapping.leafId().equals("ValidateProposal")));
        assertTrue(result.mappings().stream().anyMatch(mapping ->
                mapping.activityId().equals("updateProposal")
                        && mapping.leafId().equals("UpdateProposal")));
        assertTrue(session.states().isEmpty(), "whole validation must generate states, not load AOL");
    }
}
