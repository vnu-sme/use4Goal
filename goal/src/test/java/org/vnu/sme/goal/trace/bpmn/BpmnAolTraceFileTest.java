package org.vnu.sme.goal.trace.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile.InstanceTrace;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile.Step;
import org.vnu.sme.goal.verify.conformance.AolBpmnTraceRunner;

class BpmnAolTraceFileTest {
    private static final Path BASE = Path.of("src/main/resources/examples/mtg");

    @Test
    void writtenTraceReadsBackWithTheSameActivitySequenceAndAttributeHistory(@TempDir Path dir) throws Exception {
        var runResult = AolBpmnTraceRunner.run(BASE.resolve("mtg.acl"), BASE.resolve("mtg.aol"), BASE.resolve("mtg.bpmn2"));
        assertTrue(runResult.ok(), () -> String.join("\n", runResult.errors()));

        Path file = dir.resolve("mtg.bpmntrace");
        BpmnAolTraceFile.write(file, "mtg.acl", runResult);
        BpmnAolTraceFile.TraceFile loaded = BpmnAolTraceFile.read(file);

        assertEquals(1, loaded.traces().size());
        InstanceTrace trace = loaded.traces().get(0);
        assertEquals("MeetingOrganization", trace.processId());
        assertEquals("MeetingUnit", trace.groupClass());
        assertTrue(trace.ended());

        assertEquals(runResult.traces().get(0).frames().size(), trace.steps().size());
        var activityIds = trace.steps().stream().map(Step::activityId)
                .filter(java.util.Objects::nonNull).toList();
        assertEquals(java.util.List.of("decideMeetingDetails", "checkCalendar", "requestSecretaryCall",
                "collectConstraintsByPhone", "chooseTimeAndDate", "announceMeeting", "participate"), activityIds);

        // Reconstructed AolModel must reflect the same attribute history checkCalendar produced.
        var afterCheckCalendar = trace.steps().stream()
                .filter(s -> "checkCalendar".equals(s.activityId())).findFirst().orElseThrow();
        var alice = afterCheckCalendar.model().groupInstances().get(0).plays().stream()
                .filter(p -> p.roleType().equals("Participant") && p.agentId().equals("alice"))
                .findFirst().orElseThrow();
        assertEquals("true", alice.attributeValues().get("timetableCollected"));
        assertEquals("calendar", alice.attributeValues().get("timetableChannel"));
        assertEquals("true", afterCheckCalendar.model().agentAttributeValues().get("alice").get("hasCalendar"));

        // The delta lines captured at generation time must survive the round trip too.
        assertTrue(afterCheckCalendar.delta().stream().anyMatch(d -> d.contains("timetableCollected")));

        // mtg.acl's knowsPhoneOf association (bob -> alice, carol) must survive the round trip.
        var bobKnows = afterCheckCalendar.model().links().stream()
                .filter(l -> l.sourceInstanceId().equals("bob")).findFirst().orElseThrow();
        assertEquals("knowsPhoneOf", bobKnows.relationName());
        assertEquals(java.util.Set.of("alice", "carol"), java.util.Set.copyOf(bobKnows.targetInstanceIds()));
    }
}
