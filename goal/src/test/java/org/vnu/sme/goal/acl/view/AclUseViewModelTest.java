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
                "src/main/resources/examples/sales_forecast/tool/proposal_review.acl"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));

        var context = acl.model().findOrgContext("ProposalReviewCase").orElseThrow();
        assertEquals(2, context.attributes().size());
        assertEquals(2, context.roles().size());
        assertEquals(4, context.entities().size());
        assertEquals(1, acl.model().findRole("ProposalManager").orElseThrow().attributes().size());
        assertEquals(4, acl.model().findEntity("Proposal").orElseThrow().attributes().size());

        AclUseViewModel adapted = AclUseViewModel.build(acl.model());
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_ProposalManager"));
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_Customer"));
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_Proposal"));
        assertTrue(adapted.associations.containsKey("ProposalReviewCase_contains_ProposalRevision"));

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
        var acl = AclCompiler.compile(Path.of("../examples/ProposalReview/proposal-review-final.acl"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));

        AclUseViewModel adapted = AclUseViewModel.build(acl.model());
        var association = adapted.associations.get("ProposalManagerValidates");
        assertEquals("validator", association.associationEnds().get(0).name());
        assertEquals("validations", association.associationEnds().get(1).name());
        assertEquals("ProposalManager", association.associationEnds().get(0).cls().name());
        assertEquals("Validation", association.associationEnds().get(1).cls().name());
    }

    @Test
    void explicitEntityCompositionsDoNotCollideAcrossAnEntityInheritanceChain() {
        var acl = AclCompiler.compile("""
                acl v4.0 CollisionCheck {
                  entity Document { title : String; }
                  entity Budget extends Document { amount : Real; }
                  orgContext Department { }
                  orgContext AuditCommittee { }
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

    @Test
    void orgCtxWidthAndEnumColorAndHeader() {
        var acl = AclCompiler.compile("""
                acl v4.0 LayoutTest {
                  enum Status { DRAFT, ACTIVE }
                  orgContext SmallCtx {
                    id : String;
                  }
                }
                """);
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));

        AclLayout layout = AclLayoutBuilder.build(acl.model());
        var ctxNode = layout.nodes.get("group::SmallCtx");
        assertTrue(ctxNode.w < 165, "OrgCtx width should shrink based on attribute length, got " + ctxNode.w);

        var options = new AclDiagramOptions();
        java.awt.Color enumFill = options.getColor(AclDiagramOptions.ENUM_FILL);
        assertEquals(new java.awt.Color(255, 246, 210), enumFill);

        var font = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12);
        var enumNode = new AclDiagramNode(layout.nodes.get("enum::Status"), options, font);
        assertEquals(60.0, enumNode.getMinWidth());
    }
}
