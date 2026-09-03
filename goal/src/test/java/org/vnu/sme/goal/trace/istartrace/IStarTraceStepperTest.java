package org.vnu.sme.goal.trace.istartrace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.feature.istartrace.IStarTraceTableModel;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.InstanceKey;

class IStarTraceStepperTest {
    @TempDir Path directory;

    @Test
    void replaysEveryStatementAndExposesRawTemporalMarking() throws Exception {
        Path acl = directory.resolve("monitor.acl");
        Path istar = directory.resolve("monitor.istar");
        Path trace = directory.resolve("monitor.soil");
        Files.writeString(acl, """
                acl v2.0 TraceMonitor {
                  role Controller {
                    enabled : Boolean;
                    signal : Boolean;
                  }
                  group MonitorUnit {
                    Controller [1];
                  }
                }
                """);
        Files.writeString(istar, """
                istar TraceMonitor {
                  role Controller {
                    goal SignalObserved : Sustain
                    condition {[ self.enabled and self.signal ]}
                  }
                }
                """);
        Files.writeString(trace, """
                !create monitor : MonitorUnit
                !create controller : Controller
                !set controller.enabled := false
                !set controller.signal := false
                !insert (monitor, controller) into Controller_in_MonitorUnit
                !set controller.enabled := true
                !set controller.signal := true
                !set controller.signal := false
                """);

        IStarTraceStepper.Result result = new IStarTraceStepper().load(acl, istar, trace);
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertEquals(8, result.steps().size());
        InstanceKey key = new InstanceKey("Controller", "controller");
        var fulfilled = result.steps().get(6).checkpoint().markings().get(key)
                .goalMarking("SignalObserved");
        assertTrue(fulfilled.active());
        assertTrue(fulfilled.condition());
        assertEquals(GoalTaskStatus.FULFILLED, fulfilled.status());

        var violated = result.steps().get(7).checkpoint().markings().get(key)
                .goalMarking("SignalObserved");
        assertTrue(violated.active());
        assertFalse(violated.condition());
        assertEquals(GoalTaskStatus.VIOLATED, violated.status());
        assertTrue(result.steps().get(7).goalDelta().stream().anyMatch(delta ->
                delta.contains("FULFILLED -> VIOLATED")));
    }

    @Test
    void mtgTraceContainsOneAtomicInitialCheckpointBeforeBusinessSteps() {
        Path mtg = Path.of("src/main/resources/examples/mtg");
        IStarTraceStepper.Result result = new IStarTraceStepper().load(
                mtg.resolve("mtg.acl"), mtg.resolve("mtg.istar"), mtg.resolve("mtg_execution_trace.soil"));

        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertEquals(11, result.steps().size());
        assertTrue(result.steps().get(0).checkpoint().soilLine().contains("mtg_execution_trace.soil"));
        assertTrue(result.steps().get(0).stateDelta().stream()
                .anyMatch(delta -> delta.contains("Loaded") && delta.contains("trace initialization")));
        assertEquals("architectureReview.detailsDecided := true",
                result.steps().get(1).checkpoint().soilLine());
        assertTrue(result.steps().get(10).goalDelta().stream()
                .anyMatch(delta -> delta.contains("PENDING -> FULFILLED")));

        IStarTraceTableModel table = new IStarTraceTableModel(result);
        assertEquals(12, table.getColumnCount()); // occurrence label + s0..s10
        assertTrue(table.getRowCount() > 10);
        int meetingOrganized = java.util.stream.IntStream.range(0, table.getRowCount())
                .filter(row -> table.row(row).label().contains("MeetingOrganized"))
                .findFirst().orElseThrow();
        assertEquals(GoalTaskStatus.FULFILLED, table.getValueAt(meetingOrganized, 11));
        assertEquals("participantCarol.attended := true", table.statementAt(11));

        int chosenTimeHasDetails = java.util.stream.IntStream.range(0, table.getRowCount())
                .filter(row -> table.row(row).label().contains("ChosenTimeHasDetails"))
                .findFirst().orElseThrow();
        // activation = timeChosen: false in s0..s5, true only after transition to s6.
        for (int stateColumn = 1; stateColumn <= 6; stateColumn++) {
            assertEquals(GoalTaskStatus.UNKNOWN,
                    table.getValueAt(chosenTimeHasDetails, stateColumn));
        }
        assertEquals(GoalTaskStatus.FULFILLED, table.getValueAt(chosenTimeHasDetails, 7));
    }
}
