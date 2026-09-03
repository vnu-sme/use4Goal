package org.vnu.sme.goal.dsl.acl.mm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;

class AclSemanticValidatorTest {

    @Test
    void allBundledAclExamplesAreCanonicalAndSemanticallyValid() throws Exception {
        Path examples = Path.of("src/main/resources/examples");
        try (var paths = Files.walk(examples)) {
            for (Path path : paths.filter(value -> value.toString().endsWith(".acl")).toList()) {
                var acl = AclCompiler.compile(path);
                assertTrue(acl.ok(), () -> path + "\n" + String.join("\n", acl.errors()));
            }
        }
    }

    @Test
    void rejectsRoleParentAcrossUnrelatedGroupTrees() {
        var acl = AclCompiler.compile("""
                acl v2.0 WrongRoleParent {
                  role Employee;
                  role Manager extends Employee;
                  group CompanyA { Employee [0..*]; }
                  group CompanyB { Manager [0..*]; }
                }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("invalid Role parent")
                && msg.contains("CompanyA") && msg.contains("CompanyB")),
                () -> String.join("\n", acl.errors()));
    }

    @Test
    void acceptsRoleParentWhenParentGroupIsIndirectOwnerAncestor() {
        var acl = AclCompiler.compile("""
                acl v2.0 NestedRoleScope {
                  role Person;
                  role Employee extends Person;
                  role Manager extends Employee;
                  group Company { Person [0..*]; Division [0..*]; }
                  group Division { Department [0..*]; }
                  group Department { Employee [0..*]; Team [0..*]; }
                  group Team { Manager [0..*]; }
                }
                """);
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
    }

    @Test
    void rejectsUnownedChildBelowOwnedRoleAncestorEvenThroughUnownedIntermediate() {
        var acl = AclCompiler.compile("""
                acl v2.0 WidenedRoleScope {
                  role Person;
                  role Employee extends Person;
                  role Manager extends Employee;
                  group Company { Person [0..*]; }
                }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("Manager")
                && msg.contains("has no Owner")), () -> String.join("\n", acl.errors()));
    }

    @Test
    void rejectsEntityMemberWithActionableDiagnostic() {
        var acl = AclCompiler.compile("""
                acl v2.0 EntityMember {
                  entity Document;
                  group Folder { Document [0..*]; }
                }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("Document")
                && msg.contains("cannot be a member") && msg.contains("explicit")),
                () -> String.join("\n", acl.errors()));
    }

    @Test
    void rejectsSelfCompatibility() {
        var acl = AclCompiler.compile("""
                acl v2.0 SelfCompatibility {
                  role Member;
                  group Team { Member [0..*]; Member compatible Member; }
                }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("self-compatibility")),
                () -> String.join("\n", acl.errors()));
    }

    @Test
    void rejectsLegacyAbstractRoleAtSyntaxBoundary() {
        var acl = AclCompiler.compile("""
                acl v2.0 Legacy { abstract role Person; }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("syntax")));
    }

    @Test
    void rejectsCompatibilityDeclaredOutsideEitherRolesOwnershipScope() {
        // Unrelated has no nesting relationship to Owner1 or Owner2, so a
        // compatibility declared there can never apply to any real occurrence
        // pair -- ACL_OCL_SEMANTICS.md §4.2 requires the declaring Group to be
        // the nearest common scope of both Roles.
        var acl = AclCompiler.compile("""
                acl v2.0 WrongScope {
                  role RoleA;
                  role RoleB;
                  group Unrelated {
                    RoleA compatible RoleB;
                  }
                  group Owner1 { RoleA [1]; }
                  group Owner2 { RoleB [1]; }
                }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("RoleA") && msg.contains("never owned")));
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("RoleB") && msg.contains("never owned")));
    }

    @Test
    void acceptsCompatibilityDeclaredInNearestCommonAncestorGroup() {
        var acl = AclCompiler.compile("""
                acl v2.0 RightScope {
                  role RoleA;
                  role RoleB;
                  group Parent {
                    Child [1..*];
                    RoleA [1];
                    RoleA compatible RoleB;
                  }
                  group Child {
                    RoleB [1];
                  }
                }
                """);
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
    }

    @Test
    void rejectsCompositionWithEntityAsWholeOverNonEntity() {
        // A composition's part (target/second endpoint) must be the Entity when
        // the pair is Entity/Group or Entity/Role -- a Group or Role cannot be
        // owned by an Entity's lifecycle.
        var acl = AclCompiler.compile("""
                acl v2.0 InvertedComposition {
                  entity Doc;
                  group Holder { }
                  composition backwards {
                    Doc [*] role docs;
                    Holder [1] role holder;
                  }
                }
                """);
        assertFalse(acl.ok());
        assertTrue(acl.errors().stream().anyMatch(msg -> msg.contains("backwards") && msg.contains("cannot be owned by an Entity")));
    }

    @Test
    void acceptsCompositionWithEntityAsThePart() {
        var acl = AclCompiler.compile("""
                acl v2.0 CorrectComposition {
                  entity Doc;
                  group Holder { }
                  composition correct {
                    Holder [1] role holder;
                    Doc [*] role docs;
                  }
                }
                """);
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
    }
}
