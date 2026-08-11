package org.vnu.sme.goal.dsl.acl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;

class AclOclPropertyResolverTest {

    private static final Path MEETING_ACL =
            Path.of("src/main/resources/examples/mtg/mtg.acl");

    @Test
    void rewritesReadableAclNavigationWithoutExposingUseAssociationEnds() throws Exception {
        AclCompiler.Result compiled = AclCompiler.compile(MEETING_ACL);
        assertTrue(compiled.ok(), () -> String.join("\n", compiled.errors()));

        String source = """
                self.group.detailsDecided and
                self.group.Participant->forAll(participant |
                  participant.phone <> '' and
                  self.knownContact->includes(participant))
                """;
        String rewritten = AclOclPropertyResolver.rewrite(
                compiled.model(), "Secretary", Map.of("participant", "Participant"), source);

        assertTrue(rewritten.contains("self.source_Secretary_in_MeetingUnit.detailsDecided"));
        assertTrue(rewritten.contains(
                "self.source_Secretary_in_MeetingUnit.target_Participant_in_MeetingUnit"));
        assertTrue(rewritten.contains("participant.source_MeetingParty_plays_Participant.phone"), rewritten);
        assertTrue(rewritten.contains("self.source_MeetingParty_plays_Secretary.knownContact"), rewritten);
        assertFalse(rewritten.contains(".group"));
        assertFalse(rewritten.contains(".agent"));
    }

    @Test
    void contextualRoleFacadeMakesAgentProfileNavigationTransparent() throws Exception {
        AclCompiler.Result compiled = AclCompiler.compile(MEETING_ACL);
        assertTrue(compiled.ok(), () -> String.join("\n", compiled.errors()));

        String rewritten = AclOclPropertyResolver.rewrite(compiled.model(), "Secretary",
                Map.of("participant", "Participant"), """
                self.knownContact->includes(participant) and
                participant.phone <> '' and
                not participant.hasCalendar and
                not participant.timetableCollected
                """);

        assertTrue(rewritten.contains("self.source_MeetingParty_plays_Secretary.knownContact"));
        assertTrue(rewritten.contains(
                "includes(participant.source_MeetingParty_plays_Participant)"));
        assertTrue(rewritten.contains("participant.source_MeetingParty_plays_Participant.phone"));
        assertTrue(rewritten.contains("participant.source_MeetingParty_plays_Participant.hasCalendar"));
        assertTrue(rewritten.contains("participant.timetableCollected"),
                "a concrete role-occurrence attribute must not be projected to Agent");
    }
}
