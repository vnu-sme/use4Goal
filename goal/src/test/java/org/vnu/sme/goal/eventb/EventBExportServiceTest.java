package org.vnu.sme.goal.translate.aclistarbpmn2eventb;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EventBExportServiceTest {
    @TempDir Path output;

    @Test void exportsMeetingSchedulerAsRodinSources() throws Exception {
        Path examples = Path.of("src/main/resources/examples/mtg");
        EventBExportResult result = new EventBExportService().export(new EventBExportRequest(
                examples.resolve("mtg.acl"), examples.resolve("mtg.istar"), examples.resolve("mtg.bpmn2"),
                output, "MeetingSchedulerEventB"));
        assertTrue(result.success(), () -> String.join("\n", result.diagnostics()));
        assertTrue(result.diagnostics().stream().noneMatch(x -> x.startsWith("Unsupported")),
                () -> "every mtg.bpmn2 pre/post clause must translate: " + String.join("\n", result.diagnostics()));
        assertTrue(result.diagnostics().isEmpty(), () -> String.join("\n", result.diagnostics()));
        assertEquals(8, result.generatedFiles().size(), "Rodin sources plus Markdown/CSV semantic mapping reports");
        assertTrue(Files.readString(result.projectDirectory()
                .resolve(".settings/org.eclipse.core.resources.prefs")).contains("encoding/<project>=UTF-8"));
        Path context = result.projectDirectory().resolve("MeetingSchedulerEventB_ctx.buc");
        Path machine = result.projectDirectory().resolve("MeetingSchedulerEventB_machine.bum");
        Path ltl = result.projectDirectory().resolve("MeetingSchedulerEventB_properties.ltl");
        String projectText = Files.readString(result.projectDirectory().resolve(".project"));
        assertTrue(projectText.contains("org.rodinp.core.rodinbuilder"));
        assertTrue(projectText.contains("org.rodinp.core.rodinnature"));
        assertTrue(Files.readString(context).contains("R_Participant"));
        String contextText = Files.readString(context);
        String machineText = Files.readString(machine);
        assertTrue(machineText.contains("decideMeetingDetails"));
        assertTrue(contextText.contains("PI_MeetingOrganization_ID"));
        assertTrue(contextText.contains("QUALITY_DECL"));
        assertTrue(machineText.contains("processScope_MeetingOrganization ∈ PI_MeetingOrganization → G_MeetingUnit"));
        assertTrue(machineText.contains("tk_MeetingOrganization_start_meeting_decideMeetingDetails_1 ∈ PI_MeetingOrganization → ℕ"));
        assertTrue(machineText.contains("org.eventb.core.label=\"start_meeting\""));
        assertTrue(machineText.contains("¬(MeetingUnit_detailsDecided[{self}] = {TRUE})"),
                "the BPMN Start precondition must be a guard of Start(self)");
        assertTrue(machineText.contains("tk_MeetingOrganization_start_meeting_decideMeetingDetails_1 ≔ ∅"));
        assertTrue(machineText.contains("∪ {pid ↦ 1}"));
        assertTrue(machineText.contains("org.eventb.core.label=\"grd_root_goal_1\""),
                "BPMN End(self) must be disabled until its iStar root marking has completed");
        assertTrue(machineText.contains("self ↦ performer ∈ owns_Initiator"));
        assertFalse(contextText.contains("R_Participant ⊆ R_MeetingParty"),
                "ACL Role inheritance is a play relation, not Event-B set inclusion");
        assertTrue(machineText.contains("plays_Participant ∈ R_MeetingParty ↔ R_Participant"));
        assertTrue(machineText.contains("∀member·member∈R_Organizer ⇒ owns_Organizer∼[{member}] ≠ ∅"));
        assertTrue(machineText.contains("owns_Initiator ∧ g↦r2∈owns_Organizer"),
                "non-compatible roles in one Group must conflict");
        assertFalse(machineText.contains("owns_Initiator ∧ g↦r2∈owns_Participant"),
                "an ACL compatible pair must not receive the default conflict axiom");
        assertFalse(machineText.contains("owns_Organizer ∧ g↦r2∈owns_Secretary"));
        assertTrue(machineText.contains("G_MeetingOrganized_A ⊆ R_Initiator"));
        assertTrue(machineText.contains("G_MeetingOrganized_P ⊆ R_Initiator"));
        assertTrue(machineText.contains("G_MeetingOrganized_S ⊆ R_Initiator"));
        assertTrue(machineText.contains("T_CollectByPhone_Q"));
        assertTrue(machineText.contains("T_CollectByPhone_R"));
        assertTrue(machineText.contains("EvaluateAllGoals"));
        assertTrue(machineText.contains("intentionalPhase=READY"));
        assertTrue(machineText.contains("intentionalPhase ≔ DIRTY"));
        assertTrue(machineText.contains("intentionalPhase=DIRTY"));
        assertTrue(machineText.contains("intentionalPhase ≔ READY"));
        assertTrue(machineText.contains("tk_MeetingOrganization_participate_end_meeting_11"));
        assertTrue(machineText.contains("Pick_OrganizerScheduledMeeting_candidates"));
        assertTrue(machineText.contains("Pick_SecretaryRequested_candidates"));
        assertTrue(machineText.contains("Q_InclusiveCollection_I"));
        assertTrue(machineText.contains("Q_InclusiveCollection_TRUE"));
        assertTrue(machineText.contains("Q_InclusiveCollection_FALSE"));
        assertTrue(machineText.contains("∃qualitySelected1"),
                "a Quality at Organizer context must aggregate contributors below Participant context");
        assertFalse(machineText.contains(" ◁ (Participant_"),
                "batch effects must preserve attribute values outside the selected Role set");
        assertFalse(machineText.contains("⊕"), "Rodin uses the Event-B override token U+E103");
        assertTrue(machineText.contains(""));
        assertTrue(machineText.contains("org.eventb.core.label=\"grd_effect_type_1\""),
                "each functional post-state update must carry an explicit Rodin type proof guard");
        String temporalProperties = Files.readString(ltl);
        assertTrue(temporalProperties.contains("LTL_ACHIEVE_MeetingOrganized"));
        assertTrue(temporalProperties.contains("LTL_ACHIEVE_ParticipantAttended"),
                "typed child goals must not disappear from the Event-B/LTL translation");
        assertTrue(temporalProperties.contains("LTL_MAINTAIN_ChosenTimeHasDetails"));
        assertTrue(temporalProperties.contains("LTL_SUSTAIN_ParticipantsNotified"));
        assertTrue(temporalProperties.contains("LTL_DEP_ChooseMeetingTime"));
        assertTrue(temporalProperties.contains("G_OrganizerScheduledMeeting_A ∖ G_OrganizerScheduledMeeting_P"));
        assertTrue(Files.readString(ltl).contains("MeetingUnit_detailsDecided"),
                "the generated liveness property must contain the declared goal trigger");
        assertTrue(Files.readString(machine).contains("org.eventb.core.theorem=\"true\""),
                "statically supported Task–Activity mappings must become Rodin theorem guards");
        assertTrue(temporalProperties.contains("MAPPING_SOUNDNESS"));
        assertTrue(Files.isRegularFile(result.projectDirectory().resolve("MeetingSchedulerEventB_mapping.md")));
        assertTrue(Files.isRegularFile(result.projectDirectory().resolve("MeetingSchedulerEventB_mapping.csv")));
        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); factory.newDocumentBuilder().parse(context.toFile());
        factory.newDocumentBuilder().parse(machine.toFile());
    }
}
