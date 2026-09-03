package org.vnu.sme.goal.translate.aclbpmn2use;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.uml.mm.ModelFactory;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.bpmn.parser.BpmnCompiler;

class AclBpmn2UseTranslatorTest {

    private static final Path MTG = Path.of("src/main/resources/examples/mtg");

    @Test
    void hostsOperationsOnLaneRolesAndEmitsTemporalPrecedence() throws Exception {
        var acl = AclCompiler.compile(MTG.resolve("mtg_old.acl"));
        var bpmn = BpmnCompiler.compile(MTG.resolve("mtg_old.bpmn2"));
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(bpmn.ok(), () -> String.join("\n", bpmn.errors()));

        var translated = AclBpmn2UseTranslator.translate(acl.model(), bpmn.model());
        String use = translated.useText();
        String tocl = translated.toclText();

        assertFalse(use.contains("ProcessState"));
        assertFalse(use.contains("token_decideMeetingDetails_to_checkCalendar"));
        assertTrue(use.contains("class Agent\nattributes\n  id : Integer"));
        assertTrue(use.contains("class MeetingUnit\nattributes\n  id : Integer"));
        assertTrue(use.contains("class Initiator\nattributes\n  id : Integer\noperations\n"
                + "  start_meeting()\n  decideMeetingDetails()"));
        assertTrue(use.contains("class Organizer\nattributes\n  id : Integer\noperations\n  checkCalendar()"));
        assertTrue(use.contains("class Participant\nattributes"));
        assertTrue(use.contains("  participate()"));
        assertTrue(use.contains("context Organizer::checkCalendar()"));
        assertTrue(use.contains("context Organizer::end_meeting()\n"
                + "  pre BPMN_MeetingOrganization_end_meeting_Enabled:\n    true"));
        assertTrue(use.contains("self.meetingUnit.detailsDecided"));
        assertTrue(use.contains("post BPMN_MeetingOrganization_checkCalendar_DomainPost:"));
        assertFalse(use.contains("source_Agent_plays_Participant"));
        assertFalse(use.contains("source_MeetingParty_plays_Participant"));
        assertTrue(use.contains("p.meetingParty.hasCalendar"));
        assertTrue(tocl.contains("inv BPMN_MeetingOrganization_before_decideMeetingDetails:"));
        assertTrue(tocl.contains("context Initiator"));
        assertTrue(tocl.contains("inv BPMN_MeetingOrganization_start_start_meeting_occurs:"));
        assertTrue(tocl.contains("sometime isCalled(start_meeting())"));
        assertTrue(tocl.contains("isCalled(decideMeetingDetails())"));
        assertTrue(tocl.contains("sometimePast ("));
        assertTrue(tocl.contains("isCalled(start_meeting())"));
        assertTrue(tocl.contains("context Organizer"));
        assertTrue(tocl.contains("Initiator.allInstances()->select(initiatorCandidate : Initiator | "));
        assertTrue(tocl.contains("->any(true).decideMeetingDetails()"));
        assertFalse(tocl.contains("exists(target"));
        assertFalse(tocl.contains("before_end_meeting"));
        assertTrue(translated.diagnostics().stream().anyMatch(message ->
                message.contains("precedence for target 'end_meeting' was omitted")));
        assertUseCompiles(use);
    }

    @Test
    void encodesAndJoinAsConjunctiveTemporalPrecedence() {
        var acl = AclCompiler.compile("""
                acl v2.0 ParallelFlow {
                  role Worker;
                  group Case {
                    done : Boolean;
                    Worker [1];
                  }
                }
                """);
        var bpmn = BpmnCompiler.compile("""
                model ParallelFlow {
                  pool Work for Case { lane Worker; }
                  start begin { lane Worker trigger none flow fork }
                  gateway fork {
                    lane Worker type and
                    flow left
                    flow right
                  }
                  activity left { type task lane Worker flow join }
                  activity right { type task lane Worker flow join }
                  gateway join { lane Worker type and flow finish }
                  end finish { lane Worker trigger none }
                }
                """);
        assertTrue(acl.ok(), () -> String.join("\n", acl.errors()));
        assertTrue(bpmn.ok(), () -> String.join("\n", bpmn.errors()));

        var translated = AclBpmn2UseTranslator.translate(acl.model(), bpmn.model());
        String use = translated.useText();
        String tocl = translated.toclText();

        assertFalse(use.contains("ProcessState"));
        assertTrue(use.contains("class Worker\nattributes\n  id : Integer\noperations"));
        assertTrue(tocl.contains("inv BPMN_Work_before_join:"));
        assertTrue(tocl.contains("inv BPMN_Work_start_begin_occurs:"));
        // `begin` is a USE keyword, therefore the generated operation is `_begin`.
        assertTrue(tocl.contains("sometime isCalled(_begin())"));
        assertTrue(tocl.contains("isCalled(left())"));
        assertTrue(tocl.contains("and sometimePast ("));
        assertTrue(tocl.contains("isCalled(right())"));
        assertUseCompiles(use);
    }

    private static void assertUseCompiles(String use) {
        StringWriter errors = new StringWriter();
        var model = USECompiler.compileSpecification(use, "generated-bpmn.use",
                new PrintWriter(errors), new ModelFactory());
        assertNotNull(model, () -> errors + "\n" + use);
    }
}
