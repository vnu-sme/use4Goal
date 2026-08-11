package org.vnu.sme.goal.verify.conformance.semantics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.vnu.sme.goal.dsl.istar.mm.GoalType;

class GoalTaskMarkingTest {

    @Test
    void achieveRemembersThatItsPredicateWasReached() {
        GoalMarking m = GoalMarking.initial(GoalType.ACHIEVE).update(true, false);
        assertEquals(GoalTaskStatus.PENDING, m.status());
        m = m.update(true, true);
        assertEquals(GoalTaskStatus.FULFILLED, m.status());
        assertEquals(GoalTaskStatus.FULFILLED, m.update(true, false).status());
    }

    @Test
    void maintainCannotRecoverAfterItsPredicateFalls() {
        GoalMarking m = GoalMarking.initial(GoalType.MAINTAIN).update(true, true);
        assertEquals(GoalTaskStatus.FULFILLED, m.status());
        m = m.update(true, false);
        assertEquals(GoalTaskStatus.VIOLATED, m.status());
        assertEquals(GoalTaskStatus.VIOLATED, m.update(true, true).status());
    }

    @Test
    void sustainWaitsForFirstTruthThenRejectsAnyFall() {
        GoalMarking m = GoalMarking.initial(GoalType.SUSTAIN).update(true, false);
        assertEquals(GoalTaskStatus.PENDING, m.status());
        m = m.update(true, true);
        assertEquals(GoalTaskStatus.FULFILLED, m.status());
        m = m.update(true, false);
        assertEquals(GoalTaskStatus.VIOLATED, m.status());
        assertEquals(GoalTaskStatus.VIOLATED, m.update(true, true).status());
    }

    @Test
    void recurTracksTheCurrentPredicateRatherThanLatchingIt() {
        GoalMarking m = GoalMarking.initial(GoalType.RECUR).update(true, false);
        assertEquals(GoalTaskStatus.PENDING, m.status());
        m = m.update(true, true);
        assertEquals(GoalTaskStatus.FULFILLED, m.status());
        assertEquals(GoalTaskStatus.PENDING, m.update(true, false).status());
    }

    @Test
    void deactivationResetsTheGoalEpisode() {
        GoalMarking m = GoalMarking.initial(GoalType.SUSTAIN)
                .update(true, true).update(true, false);
        assertEquals(GoalTaskStatus.VIOLATED, m.status());
        m = m.update(false, false);
        assertEquals(GoalTaskStatus.UNKNOWN, m.status());
        assertEquals(GoalTaskStatus.PENDING, m.update(true, false).status());
    }

    @Test
    void taskAllowsPreAndPostToBeSeparatedByManySnapshots() {
        TaskMarking m = TaskMarking.initial().update(true, true, false);
        assertEquals(GoalTaskStatus.PENDING, m.status());
        m = m.update(true, false, false);
        assertEquals(GoalTaskStatus.PENDING, m.status());
        m = m.update(true, false, true);
        assertEquals(GoalTaskStatus.FULFILLED, m.status());
        assertEquals(GoalTaskStatus.FULFILLED, m.update(true, false, false).status());
    }
}
