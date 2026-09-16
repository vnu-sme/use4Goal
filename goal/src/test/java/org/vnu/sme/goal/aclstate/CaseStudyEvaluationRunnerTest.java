package org.vnu.sme.goal.aclstate;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

public class CaseStudyEvaluationRunnerTest {

    @Test
    void runAllCaseStudiesAndPrintMetrics() throws Exception {
        System.out.println("=========================================================================");
        System.out.println("CASE STUDY EVALUATION METRICS REPORT (FULL SESSION & SOLVER TIMING)");
        System.out.println("=========================================================================");

        Path examples = Files.exists(Path.of("examples/ProposalReview"))
                ? Path.of("examples")
                : Path.of("../examples");
        Path propDir = examples.resolve("ProposalReview");
        runCaseStudy("1. ProposalReview",
                propDir.resolve("proposal-review-final.csl"),
                propDir.resolve("proposal-review-final.bpmn2"),
                propDir.resolve("proposal-review-final.istar"),
                propDir.resolve("proposal-review.cslboundary"));

        Path sales = examples.resolve("Forecast/paper");
        runCaseStudy("2. SalesForecast",
                sales.resolve("sales-forecast.csl"),
                sales.resolve("sales-forecast.bpmn2"),
                sales.resolve("sales-forecast.istar"),
                sales.resolve("sales-forecast.cslboundary"));

        Path complaint = examples.resolve("CustomerComplaintManagement/paper");
        runCaseStudy("3. CustomerComplaintManagement",
                complaint.resolve("customer-complaint.csl"),
                complaint.resolve("customer-complaint.bpmn2"),
                complaint.resolve("customer-complaint.istar"),
                complaint.resolve("customer-complaint.cslboundary"));

        Path registration = examples.resolve("StudentRegistration/paper");
        runCaseStudy("4. StudentRegistration",
                registration.resolve("student-registration.csl"),
                registration.resolve("student-registration.bpmn2"),
                registration.resolve("student-registration.istar"),
                registration.resolve("student-registration.cslboundary"));

        Path purchase = examples.resolve("PurchaseOrder/paper");
        runCaseStudy("5. PurchaseOrder",
                purchase.resolve("purchase-order.csl"),
                purchase.resolve("purchase-order.bpmn2"),
                purchase.resolve("purchase-order.istar"),
                purchase.resolve("purchase-order.cslboundary"));

        System.out.println("=========================================================================");
    }

    private void runCaseStudy(String name, Path acl, Path bpmn, Path istar, Path boundary) {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Running: " + name);
        try {
            long sessionStart = System.currentTimeMillis();
            AclStateEvaluationSession session = AclStateEvaluationSession.load(acl);
            var bpmnEvaluator = session.loadBpmn(bpmn);
            if (istar != null && Files.exists(istar)) {
                session.loadIStar(istar);
            }
            var bnd = session.loadBoundary(boundary);

            int nodeCount = 0;
            for (var proc : bpmnEvaluator.bpmnModel().processes()) {
                nodeCount += proc.flowElements().size();
            }

            long solverStart = System.currentTimeMillis();
            var result = session.validateWholeBpmnProcess();
            long solverElapsed = System.currentTimeMillis() - solverStart;
            long sessionElapsed = System.currentTimeMillis() - sessionStart;

            System.out.printf("  [METRIC] BPMN Node count: %d%n", nodeCount);
            System.out.printf("  [METRIC] Configured snapshots bound: %d%n", bnd.snapshots());
            System.out.printf("  [METRIC] Realizable execution paths (branches): %d%n", result.realizableExecutions());
            System.out.printf("  [METRIC] Goal achieving (conformant): %d%n", result.goalAchievingExecutions());
            System.out.printf("  [METRIC] Non-goal achieving (risky): %d%n", result.nonGoalAchievingExecutions());
            System.out.printf("  [METRIC] Verdict: Consistency=%s, Risk=%s%n", result.consistency(), result.risk());
            System.out.printf("  [TIME] Solver validation time: %.2f s (%d ms)%n", solverElapsed / 1000.0, solverElapsed);
            System.out.printf("  [TIME] Total session time (Load+Parse+SAT): %.2f s (%d ms)%n", sessionElapsed / 1000.0, sessionElapsed);
        } catch (Exception e) {
            System.err.printf("  [ERROR] Failed running %s: %s%n", name, e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
