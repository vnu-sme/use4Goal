package org.vnu.sme.goal.trace.istartrace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.trace.istartrace.nativeacl.BpmnTraceIStarMonitor;

class BpmnTraceIStarMonitorTest {
    private static final Path MTG = Path.of("src/main/resources/examples/mtg");

    @Test
    void evaluatesMtgBpmnTraceWithoutUseOrSoil() {
        var result = BpmnTraceIStarMonitor.run(MTG.resolve("mtg.acl"), MTG.resolve("mtg.istar"),
                MTG.resolve("mtg1.bpmntrace"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertEquals(1, result.processes().size());
        assertEquals(8, result.processes().get(0).frames().size());
        var finalFrame = result.processes().get(0).frames().get(7);
        assertTrue(!finalFrame.instanceModel().allElements().isEmpty());
        assertTrue(finalFrame.marking().goalTaskStatuses().values().stream()
                .anyMatch(status -> status == GoalTaskStatus.FULFILLED));
        var process=result.processes().get(0);
        assertRow(process,"MeetingOrganized [", P,P,P,P,P,P,P,F);
        assertRow(process,"MeetingScheduled [", U,U,U,U,P,F,F,F);
        assertRow(process,"ParticipantsAttended [", P,P,P,P,P,P,P,F);
        assertRow(process,"ChosenTimeHasDetails [", U,U,U,U,U,F,F,F);
        assertRow(process,"TimetablesCollected [", P,P,P,P,F,F,F,F);
        assertRow(process,"TimetableCollected [initiatorAlice,organizerBob,participantAlice]",
                P,P,F,F,F,F,F,F);
        assertRow(process,"TimetableCollected [initiatorAlice,organizerBob,participantCarol]",
                P,P,P,P,F,F,F,F);
        assertRow(process,"ParticipantsNotified [", P,P,P,P,P,P,F,F);
        assertRow(process,"ParticipantAttended [initiatorAlice,participantAlice]",
                P,P,P,P,P,P,P,F);
        assertRow(process,"ParticipantAttended [initiatorAlice,participantCarol]",
                P,P,P,P,P,P,P,F);
        assertRow(process,"SecretaryRequested [initiatorAlice,organizerBob,participantAlice,", U,U,U,U,U,U,U,U);
        assertRow(process,"SecretaryRequested [initiatorAlice,organizerBob,participantCarol,", P,P,P,P,F,F,F,F);
        assertRow(process,"CollectByPhone [initiatorAlice,organizerBob,participantAlice,", U,U,U,U,U,U,U,U);
        assertRow(process,"CollectByPhone [initiatorAlice,organizerBob,participantCarol,", P,P,P,P,F,F,F,F);
    }

    private static final GoalTaskStatus U=GoalTaskStatus.UNKNOWN;
    private static final GoalTaskStatus P=GoalTaskStatus.PENDING;
    private static final GoalTaskStatus F=GoalTaskStatus.FULFILLED;

    private static void assertRow(BpmnTraceIStarMonitor.ProcessResult process,String labelPrefix,
                                  GoalTaskStatus... expected){
        String id=process.frames().get(0).nodeLabels().entrySet().stream()
                .filter(e->e.getValue().startsWith(labelPrefix)).map(java.util.Map.Entry::getKey)
                .findFirst().orElseThrow(()->new AssertionError("Missing occurrence: "+labelPrefix));
        var actual=process.frames().stream().map(frame->frame.marking().goalTaskStatus(id)).toList();
        assertEquals(java.util.List.of(expected),actual,labelPrefix);
    }
}
