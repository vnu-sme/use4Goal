package org.vnu.sme.goal.dsl.acl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.ModelFactory;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;

class Acl2UseCanonicalRulesTest {
    @Test
    void translatesEveryBundledAclExampleToCompilableUse() throws Exception {
        try (var paths = Files.walk(Path.of("src/main/resources/examples"))) {
            for (Path path : paths.filter(value -> value.toString().endsWith(".acl")).toList()) {
                var compiled = AclCompiler.compile(path);
                assertTrue(compiled.ok(), () -> path + "\n" + String.join("\n", compiled.errors()));
                assertUseCompiles(Acl2UseTranslator.translate(compiled.model()));
            }
        }
    }

    @Test
    void translatesCanonicalClassifierRelationshipAndOwnerRules() {
        var compiled = AclCompiler.compile("""
                acl v2.0 Canonical {
                  enum Priority { LOW, HIGH }
                  entity Document { title : String; }
                  entity Report extends Document;
                  role Person { name : String; }
                  role Employee extends Person;
                  group Company { Employee [0..*]; Department [0..*]; }
                  group Department extends Company { }
                  association mentors {
                    Person [0..*] role mentors;
                    Employee [0..*] role mentees;
                  }
                  composition reports {
                    Department [1] role department;
                    Report [0..*] role reports;
                  }
                }
                """);
        assertTrue(compiled.ok(), () -> String.join("\n", compiled.errors()));
        String use = Acl2UseTranslator.translate(compiled.model());
        assertTrue(use.contains("enum Priority {LOW, HIGH}"));
        assertTrue(use.contains("class Agent\nend"));
        assertTrue(use.contains("class Report < Document"));
        assertTrue(use.contains("class Department < Company"));
        assertFalse(use.contains("class Employee < Person"));
        assertTrue(use.contains("association Person_plays_Employee"));
        assertTrue(use.contains("association Agent_plays_Person"));
        assertTrue(use.contains("association mentors between\n  Person[0..*] role mentors"));
        assertTrue(use.contains("composition reports between\n  Department[1] role department"));
        assertTrue(use.contains("composition Employee_in_Company"));
        assertTrue(use.contains("composition Owner_Company_Department"));
        assertUseCompiles(use);
    }

    @Test
    void emitsOccurrenceLevelRoleOwnerScopeInvariantAcrossIndirectGroupAncestor() {
        var compiled = AclCompiler.compile("""
                acl v2.0 ScopeInvariant {
                  role Employee;
                  role Manager extends Employee;
                  group Company { Employee [0..*]; Division [0..*]; }
                  group Division { Department [0..*]; }
                  group Department { Manager [0..*]; }
                }
                """);
        assertTrue(compiled.ok(), () -> String.join("\n", compiled.errors()));
        String use = Acl2UseTranslator.translate(compiled.model());
        assertTrue(use.contains("context Manager inv RoleOwnerScope_Employee_Manager:"));
        assertTrue(use.contains("self.source_Employee_plays_Manager.source_Employee_in_Company"));
        assertTrue(use.contains("self.source_Manager_in_Department"
                + ".source_Owner_Division_Department.source_Owner_Company_Division"));
        assertUseCompiles(use);
    }

    private static void assertUseCompiles(String use) {
        StringWriter errors = new StringWriter();
        var model = USECompiler.compileSpecification(use, "generated.use",
                new PrintWriter(errors), new ModelFactory());
        assertTrue(model != null, () -> errors.toString() + "\n" + use);
    }
}
