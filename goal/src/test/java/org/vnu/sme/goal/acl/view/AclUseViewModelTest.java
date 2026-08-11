package org.vnu.sme.goal.dsl.acl.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;

class AclUseViewModelTest {
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
