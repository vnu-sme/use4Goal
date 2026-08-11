package org.vnu.sme.goal.translate.acl2eventb;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AclToEventBServiceTest {
    @TempDir Path output;

    @Test void exportsMtgAclWithoutIStarOrBpmn() throws Exception {
        Path acl=Path.of("src/main/resources/examples/mtg/mtg.acl");
        var result=new AclToEventBService().export(new AclToEventBRequest(acl,output,"MtgAclEventB"));
        assertTrue(result.success(),()->String.join("\n",result.diagnostics()));
        assertTrue(result.diagnostics().isEmpty());
        assertEquals(5,result.generatedFiles().size());
        assertFalse(Files.exists(result.projectDirectory().resolve("MtgAclEventB_properties.ltl")));

        String context=Files.readString(result.projectDirectory().resolve("MtgAclEventB_ctx.buc"));
        String machine=Files.readString(result.projectDirectory().resolve("MtgAclEventB_machine.bum"));
        assertTrue(context.contains("org.eventb.core.identifier=\"AGENT_ID\""));
        assertTrue(context.contains("org.eventb.core.identifier=\"R_MeetingParty_ID\""));
        assertTrue(context.contains("org.eventb.core.identifier=\"G_MeetingUnit_ID\""));
        assertFalse(context.contains("org.eventb.core.identifier=\"OBJECTS\""));
        assertFalse(context.contains("org.eventb.core.identifier=\"R_MeetingParty\""),
                "class extents must be Machine variables, not Context constants");

        assertTrue(machine.contains("org.eventb.core.identifier=\"R_MeetingParty\""));
        assertTrue(machine.contains("R_MeetingParty ⊆ R_MeetingParty_ID"));
        assertTrue(machine.contains("G_MeetingUnit ⊆ G_MeetingUnit_ID"));
        assertTrue(machine.contains("plays_Participant ∈ R_MeetingParty ↔ R_Participant"));
        assertTrue(machine.contains("owns_Participant ∈ G_MeetingUnit ↔ R_Participant"));
        assertTrue(machine.contains("knowsPhoneOf ∈ R_MeetingParty ↔ R_MeetingParty"));
        assertTrue(machine.contains("MeetingParty_name ∈ R_MeetingParty → STRING"));
        assertFalse(machine.contains("G_MeetingOrganized_A"));
        assertFalse(machine.contains("at_f_"));
        assertFalse(machine.contains("started"));
    }

    @Test void mapsNormalGeneralizationToMachineExtentInclusion() throws Exception {
        Path acl=output.resolve("library.acl");
        Files.writeString(acl,"""
                acl v2.0 Library {
                  entity Book {
                    title : String;
                    subtitle : String optional;
                  }
                  entity ReferenceBook extends Book;
                  group Organization {}
                }
                """);
        var result=new AclToEventBService().export(new AclToEventBRequest(acl,output,"LibraryAclEventB"));
        assertTrue(result.success(),()->String.join("\n",result.diagnostics()));
        String context=Files.readString(result.projectDirectory().resolve("LibraryAclEventB_ctx.buc"));
        String machine=Files.readString(result.projectDirectory().resolve("LibraryAclEventB_machine.bum"));
        assertTrue(context.contains("org.eventb.core.identifier=\"E_Book_ID\""));
        assertFalse(context.contains("E_ReferenceBook_ID"),"a normal subclass reuses its superclass identity pool");
        assertTrue(machine.contains("E_Book ⊆ E_Book_ID"));
        assertTrue(machine.contains("E_ReferenceBook ⊆ E_Book"));
        assertTrue(machine.contains("Book_title ∈ E_Book → STRING"));
        assertTrue(machine.contains("Book_subtitle ∈ E_Book ⇸ STRING"));
    }

    @Test void rejectsContradictoryAttributeMultiplicityModifiers() throws Exception {
        Path acl=output.resolve("invalid.acl");
        Files.writeString(acl,"""
                acl v2.0 Invalid {
                  entity Book { title : String optional required; }
                }
                """);
        var result=new AclToEventBService().export(new AclToEventBRequest(acl,output,"InvalidAclEventB"));
        assertFalse(result.success());
        assertTrue(result.diagnostics().stream().anyMatch(x->x.contains("cannot be both optional and required")));
    }
}
