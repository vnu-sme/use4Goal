package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

/** Ensures every canonical case-study bundle loads through the public CSL pipeline. */
class CslOfficialExamplesTest {

    @Test
    void loadsAllOfficialCaseStudies() throws Exception {
        Path examples = Files.exists(Path.of("examples/ProposalReview"))
                ? Path.of("examples")
                : Path.of("../examples");

        for (CaseStudy study : List.of(
                new CaseStudy(examples.resolve("ProposalReview"), "proposal-review-final",
                        "proposal-review-final", "proposal-review-final", "proposal-review"),
                new CaseStudy(examples.resolve("Forecast/paper"), "sales-forecast",
                        "sales-forecast", "sales-forecast", "sales-forecast"),
                new CaseStudy(examples.resolve("CustomerComplaintManagement/paper"), "customer-complaint",
                        "customer-complaint", "customer-complaint", "customer-complaint"),
                new CaseStudy(examples.resolve("StudentRegistration/paper"), "student-registration",
                        "student-registration", "student-registration", "student-registration"),
                new CaseStudy(examples.resolve("PurchaseOrder/paper"), "purchase-order",
                        "purchase-order", "purchase-order", "purchase-order"))) {
            AclStateEvaluationSession session = AclStateEvaluationSession.load(
                    study.directory().resolve(study.cslStem() + ".csl"));
            var process = session.loadBpmn(study.directory().resolve(study.bpmnStem() + ".bpmn2"));
            session.loadIStar(study.directory().resolve(study.istarStem() + ".istar"));
            session.loadBoundary(study.directory().resolve(study.boundaryStem() + ".cslboundary"));
            assertFalse(process.bpmnModel().processes().isEmpty(), study.directory().toString());
        }
    }

    private record CaseStudy(Path directory, String cslStem, String bpmnStem,
                             String istarStem, String boundaryStem) {}
}
