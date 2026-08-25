package org.vnu.sme.goal.dsl.acl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;

class AclOclPropertyResolverTest {

    private static final String MEETING_ACL = """
            acl v2.0 MeetingSchedulerShadow {
              role MeetingParty { phone : String; hasCalendar : Boolean; }
              role Secretary extends MeetingParty;
              role Participant extends MeetingParty { timetableCollected : Boolean; }
              association knowsPhoneOf {
                MeetingParty [*] role knower;
                MeetingParty [*] role knownContact;
              }
              group MeetingUnit {
                detailsDecided : Boolean;
                Secretary [0..1];
                Participant [2..*];
              }
            }
            """;

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

        assertTrue(rewritten.contains("self.meetingUnit.detailsDecided"));
        assertTrue(rewritten.contains("self.meetingUnit.participant"));
        assertTrue(rewritten.contains("participant.meetingParty.phone"), rewritten);
        assertTrue(rewritten.contains("self.meetingParty.knownContact"), rewritten);
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

        assertTrue(rewritten.contains("self.meetingParty.knownContact"));
        assertTrue(rewritten.contains("includes(participant.meetingParty)"));
        assertTrue(rewritten.contains("participant.meetingParty.phone"));
        assertTrue(rewritten.contains("participant.meetingParty.hasCalendar"));
        assertTrue(rewritten.contains("participant.timetableCollected"),
                "a concrete role-occurrence attribute must not be projected to Agent");
    }
}
