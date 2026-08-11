package org.vnu.sme.goal.verify.conformance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.vnu.sme.goal.dsl.bpmn.view.BpmnView;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.BpmnScenarioSnapshot;
import org.vnu.sme.goal.dsl.istar.view.IStarView;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceEvaluator;

class MtgScenarioConformanceTest {
    private static final Path BASE = Path.of("src/main/resources/examples/mtg");

    @Test
    void allCalendarScenarioGeneratesShortTraceAndSatisfiesRoots() throws Exception {
        var session = run("mtg_i1o1p3s1.soil", "mtg.bpmn2");
        assertEquals(List.of("decideMeetingDetails", "checkCalendar", "chooseTimeAndDate",
                "announceMeeting", "participate"), activityIds(session));
        assertTrue(session.ended());
        assertTrue(session.rootGoalFailures().isEmpty(), () -> session.rootGoalFailures().toString());
    }

    @Test
    void mixedScenarioGeneratesPhoneThenCalendarTrace() throws Exception {
        var session = run("mtg_i1o1p4s1.soil", "mtg.bpmn2");
        assertEquals(List.of("decideMeetingDetails", "checkCalendar", "requestSecretaryCall",
                "collectConstraintsByPhone", "chooseTimeAndDate",
                "announceMeeting", "participate"), activityIds(session));
        assertTrue(session.ended());
        assertTrue(session.rootGoalFailures().isEmpty(), () -> session.rootGoalFailures().toString());
        assertTrue(session.frames().stream().anyMatch(frame -> !frame.stateDelta().isEmpty()));
        assertTrue(session.frames().stream().anyMatch(frame -> !frame.goalDelta().isEmpty()));
    }

    @Test
    void collectByPhoneUsesTheSecretaryParticipantContextOccurrence() throws Exception {
        var session = run("mtg_i1o1p4s1.soil", "mtg.bpmn2");
        var finalFrame = session.frames().get(session.frames().size() - 1);
        var instance = IStarUseTraceEvaluator.evaluate(session.goalModel(), finalFrame.checkpoint());
        var contextualTasks = instance.instanceModel().allElements().keySet().stream()
                .filter(id -> id.startsWith("CollectByPhone__"))
                .toList();
        assertFalse(contextualTasks.isEmpty());
        assertTrue(contextualTasks.stream().anyMatch(id ->
                instance.instanceMarking().goalTaskStatus(id)
                        == org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus.FULFILLED),
                () -> contextualTasks.stream().map(id -> id + "="
                        + instance.instanceMarking().goalTaskStatus(id)).toList().toString());
    }

    @Test
    void validInputCanExposeBpmnThatEndsWithoutSatisfyingRootGoal() throws Exception {
        var session = run("mtg_i1o1p3s1.soil", "mtg_goal_gap.bpmn2");
        assertTrue(session.ended(), "the faulty BPMN must still reach EndEvent");
        assertFalse(session.rootGoalFailures().isEmpty(),
                "conformance must fail because attendance root goal is pending");
    }

    @Test
    void oneShotCheckerUsesTheSameScenarioDrivenSemantics() throws Exception {
        var good = AclBpmnIStarConformanceChecker.check(BASE.resolve("mtg.acl"),
                BASE.resolve("mtg_i1o1p4s1.soil"), BASE.resolve("mtg.istar"), BASE.resolve("mtg.bpmn2"));
        var counterexample = AclBpmnIStarConformanceChecker.check(BASE.resolve("mtg.acl"),
                BASE.resolve("mtg_i1o1p3s1.soil"), BASE.resolve("mtg.istar"),
                BASE.resolve("mtg_goal_gap.bpmn2"));
        assertTrue(good.conformant(), () -> "Unexpected conformance errors: " + good.errors());
        assertFalse(counterexample.conformant());
        assertTrue(counterexample.bpmnFailures().isEmpty(), "the faulty BPMN still completes normally");
        assertFalse(counterexample.goalFailures().isEmpty());
    }

    @Test
    void diagramComponentsAcceptRuntimeModelsAndScenarioSnapshot() throws Exception {
        var session = run("mtg_i1o1p3s1.soil", "mtg.bpmn2");
        System.setProperty("use.gui.view.classdiagram.class.minheight", "20");
        System.setProperty("use.gui.view.classdiagram.class.minwidth", "20");
        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                var istar = new IStarView();
                istar.setModel(session.goalModel());
                var bpmn = new BpmnView();
                bpmn.setModel(session.bpmnModel());
                bpmn.setScenarioSnapshot(new BpmnScenarioSnapshot(
                        java.util.Map.of("run", session.processIds().get(0)), java.util.Map.of(),
                        java.util.Map.of(), List.of(), List.of(), List.of(), List.of(), List.of()));
            });
        } catch (java.awt.AWTError unavailableDisplay) {
            Assumptions.assumeTrue(false, "GUI display is unavailable: " + unavailableDisplay.getMessage());
        }
    }

    @Test
    void nondeterministicBpmnIsWeakButNotStrongWhenOnlyOneTraceSatisfiesRoots() {
        var result = ScenarioTraceExplorer.explore(BASE.resolve("mtg.istar"),
                BASE.resolve("mtg_nondeterministic.bpmn2"), BASE.resolve("mtg.acl"),
                BASE.resolve("mtg_i1o1p3s1.soil"), 16);
        assertEquals(2, result.traces().size());
        assertTrue(result.weak());
        assertFalse(result.strong());
        assertEquals(ScenarioTraceExplorer.Verdict.WEAKLY_CONFORMANT, result.verdict());
    }

    @Test
    void incidentResponseWasMigratedFromFinalSnapshotToExecutableInputScenario() throws Exception {
        Path base = Path.of("src/main/resources/examples/incident_response_acl");
        var result = AclBpmnIStarConformanceChecker.check(base.resolve("incident_response.acl"),
                base.resolve("incident_response.soil"), base.resolve("incident_response.istar"),
                base.resolve("incident_response.bpmn2"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertTrue(result.conformant(), () -> "BPMN=" + result.bpmnFailures()
                + " goals=" + result.goalFailures());
    }

    @Test
    void ojsNowHasAnExecutableInputScenarioInsteadOfOnlyAolFinalSnapshots() throws Exception {
        Path base = Path.of("src/main/resources/examples/ojs");
        var result = AclBpmnIStarConformanceChecker.check(base.resolve("ojs.acl"),
                base.resolve("ojs_input.soil"), base.resolve("ojs.istar"), base.resolve("ojs.bpmn2"));
        assertTrue(result.ok(), () -> String.join("\n", result.errors()));
        assertTrue(result.conformant(), () -> "BPMN=" + result.bpmnFailures()
                + " goals=" + result.goalFailures());
    }

    private static VisualConformanceSession run(String soil, String bpmn) throws Exception {
        var session = VisualConformanceSession.prepare(BASE.resolve("mtg.istar"), BASE.resolve(bpmn),
                BASE.resolve("mtg.acl"), BASE.resolve(soil));
        while (session.canAdvance()) session.next();
        return session;
    }

    private static List<String> activityIds(VisualConformanceSession session) {
        List<String> ids = new ArrayList<>();
        session.frames().stream().filter(frame -> !frame.initial())
                .forEach(frame -> ids.add(frame.activity().id()));
        return ids;
    }
}
