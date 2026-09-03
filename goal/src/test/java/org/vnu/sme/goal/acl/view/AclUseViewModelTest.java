package org.vnu.sme.goal.dsl.acl.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;

class AclUseViewModelTest {
    @Test
    void opensNestedOrgContextSyntaxAndBuildsContainmentView() throws Exception {
        var acl = AclCompiler.compile(Path.of(
                "src/main/resources/examples/sales_forecast/proposal_review_whole/proposal_review.acl"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));

        var context = acl.model().findOrgContext("ProposalReviewCase").orElseThrow();
        assertTrue(context.attributes().isEmpty(), "orgContext must not own state attributes");
        assertEquals(2, context.roles().size());
        assertEquals(1, context.entities().size());
        assertEquals(2, acl.model().findRole("Customer").orElseThrow().attributes().size());
        assertEquals(8, acl.model().findEntity("Proposal").orElseThrow().attributes().size());

        AclUseViewModel adapted = AclUseViewModel.build(acl.model());
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_ProposalManager"));
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_Customer"));
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_Proposal"));

        AclLayout layout = AclLayoutBuilder.build(acl.model());
        assertEquals(AclNodeKind.ORG_CONTEXT,
                layout.nodes.get("group::ProposalReviewCase").kind);
        assertEquals("orgContext", layout.nodes.get("group::ProposalReviewCase").subtitle);
        assertFalse(layout.edges.isEmpty());

        String rendered = AclSpecText.render(acl.model());
        assertTrue(rendered.contains("orgContext ProposalReviewCase"));
        assertTrue(rendered.contains("role Customer"));
        assertTrue(rendered.contains("entity Proposal"));
        assertFalse(rendered.contains("group ProposalReviewCase"));
        var roundTrip = AclCompiler.compile(rendered, "<rendered-proposal-review>");
        assertTrue(roundTrip.ok(), () -> String.join("\n", roundTrip.errors()));
    }

    @Test
    void associationUsesRealUseMetamodelEndsAndRoleNames() throws Exception {
        var acl = AclCompiler.compile(Path.of("src/main/resources/examples/mtg/mtg.acl"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));

        AclUseViewModel adapted = AclUseViewModel.build(acl.model());
        var association = adapted.associations.get("knowsPhoneOf");
        assertEquals("knower", association.associationEnds().get(0).name());
        assertEquals("knownContact", association.associationEnds().get(1).name());
        assertEquals("MeetingParty", association.associationEnds().get(0).cls().name());
        assertEquals("MeetingParty", association.associationEnds().get(1).cls().name());
    }

    @Test
    void explicitEntityCompositionsDoNotCollideAcrossAnEntityInheritanceChain() {
        var acl = AclCompiler.compile("""
                acl v2.0 CollisionCheck {
                  entity Document { title : String; }
                  entity Budget extends Document { amount : Real; }
                  group Department { }
                  group AuditCommittee { }
                  composition departmentBudget {
                    Department [1] role department;
                    Budget [1] role budget;
                  }
                  composition auditDocuments {
                    AuditCommittee [1] role committee;
                    Document [*] role documents;
                  }
                }
                """);
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));

        AclUseViewModel adapted = AclUseViewModel.build(acl.model());
        assertTrue(adapted.associations.containsKey("departmentBudget"));
        assertTrue(adapted.associations.containsKey("auditDocuments"));
    }
}
