package org.vnu.sme.goal.conformance.semantics;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Obstacle;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Resource;
import org.vnu.sme.goal.istar.mm.Task;

/**
 * Goal Model Marking — Definition 3.1 (Caballero-Villalobos), instantiated over the actual
 * {@link GoalModel} of this project. Immutable: every update returns a new instance, so a
 * marking can safely be used as a state / map key while building the product LTS.
 */
public final class IStarMarking {

    private final Map<String, GoalTaskStatus> goalTask;
    private final Map<String, QualityStatus> quality;

    private IStarMarking(Map<String, GoalTaskStatus> goalTask, Map<String, QualityStatus> quality) {
        this.goalTask = goalTask;
        this.quality = quality;
    }

    /** Initial marking m0: every Goal/Task unknown, every Quality unknown. */
    public static IStarMarking initial(GoalModel gm) {
        Map<String, GoalTaskStatus> gt = new LinkedHashMap<>();
        Map<String, QualityStatus> q = new LinkedHashMap<>();
        for (IntentionalElement e : gm.allElements().values()) {
            switch (e) {
                case Goal g -> gt.put(g.id(), GoalTaskStatus.UNKNOWN);
                case Task t -> gt.put(t.id(), GoalTaskStatus.UNKNOWN);
                case Quality qu -> q.put(qu.id(), QualityStatus.UNKNOWN);
                case Resource r -> { /* resources are not part of the marking */ }
                case Obstacle o -> { /* obstacles are not part of the marking */ }
            }
        }
        return new IStarMarking(gt, q);
    }

    public GoalTaskStatus goalTaskStatus(String id) {
        return goalTask.getOrDefault(id, GoalTaskStatus.UNKNOWN);
    }

    public QualityStatus qualityStatus(String id) {
        return quality.getOrDefault(id, QualityStatus.UNKNOWN);
    }

    public IStarMarking withGoalTask(String id, GoalTaskStatus s) {
        Map<String, GoalTaskStatus> gt = new LinkedHashMap<>(goalTask);
        gt.put(id, s);
        return new IStarMarking(gt, quality);
    }

    public IStarMarking withQuality(String id, QualityStatus s) {
        Map<String, QualityStatus> q = new LinkedHashMap<>(quality);
        q.put(id, s);
        return new IStarMarking(goalTask, q);
    }

    /** Membership test for F_C (Definition 4.1): every declared quality must be TRUE. */
    public boolean isSuccess() {
        return quality.values().stream().allMatch(v -> v == QualityStatus.TRUE);
    }

    public Map<String, GoalTaskStatus> goalTaskStatuses() {
        return Collections.unmodifiableMap(goalTask);
    }

    public Map<String, QualityStatus> qualityStatuses() {
        return Collections.unmodifiableMap(quality);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IStarMarking m && goalTask.equals(m.goalTask) && quality.equals(m.quality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goalTask, quality);
    }

    @Override
    public String toString() {
        return "IStarMarking" + goalTask + quality;
    }
}
