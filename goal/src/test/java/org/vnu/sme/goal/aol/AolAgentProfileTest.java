package org.vnu.sme.goal.dsl.aol;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.dsl.aol.parser.AolCompiler;
import org.vnu.sme.goal.translate.aclaol2soil.AclAol2SoilTranslator;

class AolAgentProfileTest {
    private static final Path BASE = Path.of("src/main/resources/examples/MultiLevelOrganization");

    @Test
    void abstractProfileStateIsEmittedOnAgentAndOccurrenceStateStaysOnRole() throws Exception {
        var acl = AclCompiler.compile(BASE.resolve("MultiLevelOrganization.acl"));
        var aol = AolCompiler.compile(BASE.resolve("MultiLevelOrganization.aol"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(aol.ok(), () -> String.join("\n", aol.errors()));

        String use = Acl2UseTranslator.translate(acl.model());
        String soil = AclAol2SoilTranslator.transform(acl.model(), aol.model());

        assertTrue(use.contains("class Agent\nattributes\n  name : String\n  phone : String"));
        assertTrue(use.contains("abstract class Person\nend"));
        assertTrue(soil.contains("!set alice.name := 'Alice'"));
        assertTrue(soil.contains("!set alice.phone := '0999999'"));
        assertTrue(soil.contains("!set aliceMember.email := 'alice@example.org'"));
        assertTrue(soil.contains("!set aliceDepartmentParty.hasCalendar := true"));
    }

    @Test
    void profileAssociationBecomesAgentToAgentLink() throws Exception {
        Path mtg = Path.of("src/main/resources/examples/mtg");
        var acl = AclCompiler.compile(mtg.resolve("mtg.acl"));
        var aol = AolCompiler.compile(mtg.resolve("mtg.aol"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(aol.ok(), () -> String.join("\n", aol.errors()));

        String use = Acl2UseTranslator.translate(acl.model());
        String soil = AclAol2SoilTranslator.transform(acl.model(), aol.model());
        assertTrue(use.contains("association knowsPhoneOf between\n  Agent[0..*]"));
        assertTrue(soil.contains("!insert (bob, alice) into knowsPhoneOf"));
        assertTrue(soil.contains("!insert (bob, carol) into knowsPhoneOf"));
        assertTrue(soil.contains("!set architectureReview.detailsDecided := false"));
    }
}
