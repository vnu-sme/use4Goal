package org.vnu.sme.goal.verify.conformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;
import org.vnu.sme.goal.dsl.istar.parser.IStarCompiler;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceEvaluator;
import org.vnu.sme.goal.translate.aclbpmn2use.AclBpmn2UseTranslator;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;

/** Executable acceptance criteria for the industrial Sales Forecast example. */
class SalesForecastConformanceTest {
    private static final Path BASE = Path.of("src/main/resources/examples/sales_forecast");

    @Test
    void completePublishedSrReconstructionIsWellFormed() throws Exception {
        var result = IStarCompiler.compile(BASE.resolve("sales_forecast_full.istar"));

        assertTrue(result.ok(), result.errors()::toString);
        assertEquals(17, result.model().getActors().size());
        assertEquals(45, result.model().allElements().size());
        assertEquals(21, result.model().getDependencies().size());
    }

    @Test
    void completePublishedBpmnReconstructionIsWellFormed() throws Exception {
        var result = BpmnCompiler.compile(BASE.resolve("sales_forecast_full.bpmn2"));

        assertTrue(result.ok(), result.errors()::toString);
        assertEquals(1, result.model().processes().size());
        assertEquals(8, result.model().processes().getFirst().lanes().size());
        assertEquals(51, result.model().flowElementCount());

        var acl = AclCompiler.compile(BASE.resolve("sales_forecast.acl"));
        assertTrue(acl.ok(), acl.errors()::toString);
        var translated = AclBpmn2UseTranslator.translate(acl.model(), result.model());
        assertTrue(translated.ok(), translated.diagnostics()::toString);
    }

    @Test
    void prescribedApprovalWithinWindowSatisfiesEveryRootGoal() throws Exception {
        var session = run("sales_forecast.bpmn2", "official_on_time.soil");

        assertEquals(List.of("prepareProposal", "submitForApproval", "reviewProposalOnTime",
                "approveProposal", "sendApprovedProposal"), activityIds(session));
        assertTrue(session.rootGoalFailures().isEmpty(), session.rootGoalFailures()::toString);
        assertGoal(session, "OnlyApprovedProposalSent", GoalTaskStatus.FULFILLED);
        assertGoal(session, "RespondQuicklyAndFlexibly", GoalTaskStatus.FULFILLED);
        assertGoal(session, "CustomerSatisfied", GoalTaskStatus.FULFILLED);
    }

    @Test
    void prescribedButDelayedApprovalReproducesTheReportedMisalignment() throws Exception {
        var session = run("sales_forecast.bpmn2", "official_delayed.soil");

        assertEquals(List.of("prepareProposal", "submitForApproval", "reviewProposalLate",
                "approveProposal", "sendApprovedProposal"), activityIds(session));
        assertFalse(session.rootGoalFailures().isEmpty());
        assertGoal(session, "OnlyApprovedProposalSent", GoalTaskStatus.FULFILLED);
        assertGoal(session, "ProposalCompliant", GoalTaskStatus.FULFILLED);
        assertGoal(session, "RespondQuicklyAndFlexibly", GoalTaskStatus.PENDING);
        assertGoal(session, "CustomerSatisfied", GoalTaskStatus.PENDING);
        assertTrue(session.rootGoalFailures().stream()
                .anyMatch(failure -> failure.startsWith("CustomerRelationshipMaintained")),
                session.rootGoalFailures()::toString);
        assertFalse(session.rootGoalFailures().stream()
                .anyMatch(failure -> failure.startsWith("SalesRiskControlled")),
                session.rootGoalFailures()::toString);
    }

    @Test
    void directSendWorkaroundRestoresSpeedButViolatesApprovalGoals() throws Exception {
        var session = run("sales_forecast_workaround.bpmn2", "workaround.soil");

        assertEquals(List.of("prepareProposalOutsideIS", "sendUnapprovedProposal"), activityIds(session));
        assertFalse(session.rootGoalFailures().isEmpty());
        assertGoal(session, "RespondQuicklyAndFlexibly", GoalTaskStatus.FULFILLED);
        assertGoal(session, "CustomerSatisfied", GoalTaskStatus.FULFILLED);
        assertGoal(session, "ProposalCompliant", GoalTaskStatus.PENDING);
        assertGoal(session, "OnlyApprovedProposalSent", GoalTaskStatus.VIOLATED);
        assertTrue(session.rootGoalFailures().stream()
                .anyMatch(failure -> failure.startsWith("SalesRiskControlled")),
                session.rootGoalFailures()::toString);
    }

    private static VisualConformanceSession run(String bpmn, String soil) throws Exception {
        var session = VisualConformanceSession.prepare(BASE.resolve("sales_forecast.istar"),
                BASE.resolve(bpmn), BASE.resolve("sales_forecast.acl"), BASE.resolve(soil));
        while (session.canAdvance()) session.next();
        assertTrue(session.ended(), "BPMN process must reach its EndEvent");
        return session;
    }

    private static List<String> activityIds(VisualConformanceSession session) {
        return session.frames().stream().filter(frame -> !frame.initial())
                .map(frame -> frame.activity().id()).toList();
    }

    private static void assertGoal(VisualConformanceSession session, String sourceId,
                                   GoalTaskStatus expected) {
        var frame = session.frames().get(session.frames().size() - 1);
        var result = IStarUseTraceEvaluator.evaluate(session.goalModel(), frame.checkpoint());
        var matches = result.nodeLabels().entrySet().stream()
                .filter(entry -> entry.getValue().startsWith(sourceId + " ["))
                .map(entry -> result.instanceMarking().goalTaskStatus(entry.getKey()))
                .toList();
        assertFalse(matches.isEmpty(), "No instance occurrence found for " + sourceId);
        assertTrue(matches.stream().allMatch(expected::equals),
                () -> sourceId + " expected " + expected + " but was " + matches);
    }
}
