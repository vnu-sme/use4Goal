package org.vnu.sme.goal.translate.aclistarbpmn2eventb;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vnu.sme.goal.translate.aclbpmn2eventb.*;
import org.vnu.sme.goal.translate.aclistar2eventb.*;

class SeparatedEventBExportServiceTest {
    @TempDir Path output;
    private static final Path MTG=Path.of("src/main/resources/examples/mtg");

    @Test void aclIStarExtendsCanonicalAclWithoutBpmnState() throws Exception {
        var result=new AclIStar2EventBService().export(new AclIStar2EventBRequest(
                MTG.resolve("mtg.acl"),MTG.resolve("mtg.istar"),output,"AclIStarSeparated"));
        assertTrue(result.success(),()->String.join("\n",result.diagnostics()));
        assertTrue(result.diagnostics().isEmpty());
        String context=Files.readString(result.projectDirectory().resolve("AclIStarSeparated_ctx.buc"));
        String machine=Files.readString(result.projectDirectory().resolve("AclIStarSeparated_machine.bum"));
        assertFalse(context.contains("OBJECTS"));
        assertTrue(machine.contains("MeetingParty_name ∈ R_MeetingParty → STRING"));
        assertTrue(machine.contains("G_MeetingOrganized_A ⊆ R_Initiator"));
        assertTrue(machine.contains("T_CollectByPhone_Q"));
        assertTrue(machine.contains("Pick_SecretaryRequested_candidates"));
        assertTrue(context.contains("QUALITY_DECL"));
        assertTrue(context.contains("q_InclusiveCollection"));
        assertTrue(machine.contains("Q_InclusiveCollection_I"));
        assertTrue(machine.contains("Q_InclusiveCollection_TRUE"));
        assertTrue(machine.contains("Q_InclusiveCollection_FALSE"));
        assertTrue(machine.contains("EvaluateAllGoals"));
        assertFalse(machine.contains("tk_"));
        assertFalse(machine.contains("processState_"));
        assertTrue(Files.exists(result.projectDirectory().resolve("AclIStarSeparated_properties.ltl")));
    }

    @Test void aclBpmnSeparatesProcessIdentityFromAclGroupAndReusesLaneRoles() throws Exception {
        var result=new AclBpmn2EventBService().export(new AclBpmn2EventBRequest(
                MTG.resolve("mtg.acl"),MTG.resolve("mtg.bpmn2"),output,"AclBpmnSeparated"));
        assertTrue(result.success(),()->String.join("\n",result.diagnostics()));
        assertTrue(result.diagnostics().isEmpty());
        String context=Files.readString(result.projectDirectory().resolve("AclBpmnSeparated_ctx.buc"));
        String machine=Files.readString(result.projectDirectory().resolve("AclBpmnSeparated_machine.bum"));
        assertFalse(context.contains("OBJECTS"));
        assertTrue(machine.contains("MeetingUnit_detailsDecided ∈ G_MeetingUnit → BOOL"));
        assertTrue(context.contains("PI_MeetingOrganization_ID"));
        assertTrue(machine.contains("PI_MeetingOrganization ⊆ PI_MeetingOrganization_ID"));
        assertTrue(machine.contains("processScope_MeetingOrganization ∈ PI_MeetingOrganization → G_MeetingUnit"));
        assertTrue(machine.contains("tk_MeetingOrganization_start_meeting_decideMeetingDetails_1 ∈ PI_MeetingOrganization → ℕ"));
        assertTrue(machine.contains("org.eventb.core.identifier=\"performer\""));
        assertTrue(machine.contains("self ↦ performer ∈ owns_Initiator"));
        assertTrue(machine.contains("org.eventb.core.label=\"grd_branch\""));
        assertTrue(machine.contains("¬((∃p·"),"default XOR flow must negate the ordinary branch guard");
        assertFalse(machine.contains("G_MeetingOrganized_A"));
        assertFalse(machine.contains("EvaluateAllGoals"));
        assertFalse(Files.exists(result.projectDirectory().resolve("AclBpmnSeparated_properties.ltl")));
    }
}
