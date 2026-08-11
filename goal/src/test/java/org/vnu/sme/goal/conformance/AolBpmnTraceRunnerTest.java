package org.vnu.sme.goal.verify.conformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class AolBpmnTraceRunnerTest {
    private static final Path BASE = Path.of("src/main/resources/examples/mtg");

    @Test
    void singleMeetingAolProducesOneEndedTraceThroughEveryActivity() {
        var result = AolBpmnTraceRunner.run(BASE.resolve("mtg.acl"), BASE.resolve("mtg.aol"), BASE.resolve("mtg.bpmn2"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertEquals(1, result.traces().size());
        var trace = result.traces().get(0);
        assertTrue(trace.ended(), "trace must reach an EndEvent");
        // mtg.aol has a mixed population (Alice has a calendar, Carol doesn't), so the
        // phone-collection branch must fire alongside the calendar path.
        assertEquals(List.of("decideMeetingDetails", "checkCalendar", "requestSecretaryCall",
                "collectConstraintsByPhone", "chooseTimeAndDate", "announceMeeting", "participate"),
                activityIds(trace));
        assertTrue(trace.frames().stream().anyMatch(frame -> !frame.stateDelta().isEmpty()),
                "at least one frame must mutate state (post-synthesized SOIL ran)");
    }

    @Test
    void twoMeetingsAolProducesOneIndependentTracePerGroupInstance() {
        var result = AolBpmnTraceRunner.run(
                BASE.resolve("mtg.acl"), BASE.resolve("mtg_two_meetings.aol"), BASE.resolve("mtg.bpmn2"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertEquals(2, result.traces().size());
        assertTrue(result.traces().stream().allMatch(AolBpmnTraceRunner.InstanceTrace::ended));
    }

    private static List<String> activityIds(AolBpmnTraceRunner.InstanceTrace trace) {
        return trace.frames().stream().map(AolBpmnTraceRunner.Frame::activityId)
                .filter(java.util.Objects::nonNull).toList();
    }
}
