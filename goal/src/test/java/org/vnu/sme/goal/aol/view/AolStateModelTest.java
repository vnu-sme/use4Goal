package org.vnu.sme.goal.dsl.aol.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;
import org.vnu.sme.goal.verify.conformance.AolBpmnTraceRunner;

class AolStateModelTest {
    private static final Path BASE = Path.of("src/main/resources/examples/mtg");

    @Test
    void initialFrameReconstructsTheAolPopulationThatSeededIt() {
        var result = AolBpmnTraceRunner.run(BASE.resolve("mtg.acl"), BASE.resolve("mtg.aol"), BASE.resolve("mtg.bpmn2"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        var trace = result.traces().get(0);
        var initial = trace.frames().get(0);

        AolModel model = AolStateModel.build(result.aclModel(), initial.state(), trace.groupClass(),
                initial.state().objectByName(trace.selfObjectName()));

        assertEquals(1, model.groupInstances().size());
        var group = model.groupInstances().get(0);
        assertEquals("MeetingUnit", group.typeName());
        assertEquals("false", group.attributeValues().get("detailsDecided"));

        // mtg.aol: Alice has a calendar, Carol doesn't.
        var byAgent = group.plays().stream()
                .filter(p -> p.roleType().equals("Participant"))
                .collect(java.util.stream.Collectors.toMap(AolPlay::agentId, p -> p));
        assertEquals("true", model.agentAttributeValues().get("alice").get("hasCalendar"));
        assertEquals("false", model.agentAttributeValues().get("carol").get("hasCalendar"));
        assertEquals("false", byAgent.get("alice").attributeValues().get("timetableCollected"));

        // mtg.acl's knowsPhoneOf association (bob -> alice, carol) must show up as a link.
        var bobKnows = model.links().stream().filter(l -> l.sourceInstanceId().equals("bob")).findFirst().orElseThrow();
        assertEquals("knowsPhoneOf", bobKnows.relationName());
        assertEquals(java.util.Set.of("alice", "carol"), java.util.Set.copyOf(bobKnows.targetInstanceIds()));
    }

    @Test
    void laterFrameReflectsActivityEffectsAlreadyApplied() {
        var result = AolBpmnTraceRunner.run(BASE.resolve("mtg.acl"), BASE.resolve("mtg.aol"), BASE.resolve("mtg.bpmn2"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        var trace = result.traces().get(0);
        var afterCheckCalendar = trace.frames().stream()
                .filter(f -> "checkCalendar".equals(f.activityId())).findFirst().orElseThrow();

        AolModel model = AolStateModel.build(result.aclModel(), afterCheckCalendar.state(), trace.groupClass(),
                afterCheckCalendar.state().objectByName(trace.selfObjectName()));

        var alice = model.groupInstances().get(0).plays().stream()
                .filter(p -> p.roleType().equals("Participant") && p.agentId().equals("alice"))
                .findFirst().orElseThrow();
        assertEquals("true", alice.attributeValues().get("timetableCollected"));
        assertEquals("calendar", alice.attributeValues().get("timetableChannel"));
    }
}
