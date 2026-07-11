package org.vnu.sme.goal.istarscenario.view;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Obstacle;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Resource;
import org.vnu.sme.goal.istar.mm.Task;
import org.vnu.sme.goal.istar.view.NodeBadge;
import org.vnu.sme.goal.istarscenario.parser.IStarScenarioCompiler.Result;
import org.vnu.sme.goal.istarscenario.parser.IStarScenarioEvaluation;

final class IStarScenarioViewModel {

    private static final Color C_FULFILLED = new Color(30, 150, 60);
    private static final Color C_PENDING   = new Color(180, 130, 0);
    private static final Color C_TRUE      = new Color(30, 150, 60);
    private static final Color C_FALSE     = new Color(170, 30, 30);
    private static final Color C_UNKNOWN   = new Color(150, 150, 150);

    record Built(GoalModel model, Map<String, NodeBadge> badges,
                 Map<String, String> actorLabels, Map<String, String> nodeLabels) {}

    static Built build(Result result) {
        IStarScenarioEvaluation evaluation = result.evaluation();
        return new Built(evaluation.instanceModel(), badges(evaluation), evaluation.actorLabels(), evaluation.nodeLabels());
    }

    private static Map<String, NodeBadge> badges(IStarScenarioEvaluation evaluation) {
        Map<String, NodeBadge> badges = new LinkedHashMap<>();
        for (IntentionalElement elem : evaluation.instanceModel().allElements().values()) {
            switch (elem) {
                case Goal g -> addGoalTaskBadge(badges, g.id(), evaluation.instanceMarking().goalTaskStatus(g.id()));
                case Task t -> addGoalTaskBadge(badges, t.id(), evaluation.instanceMarking().goalTaskStatus(t.id()));
                case Quality q -> addQualityBadge(badges, q.id(), evaluation.instanceMarking().qualityStatus(q.id()));
                case Resource r -> { }
                case Obstacle o -> { }
            }
        }
        return badges;
    }

    private static void addGoalTaskBadge(Map<String, NodeBadge> badges, String id, GoalTaskStatus status) {
        switch (status) {
            case FULFILLED -> badges.put(id, new NodeBadge(C_FULFILLED, "F", "Fulfilled"));
            case PENDING -> badges.put(id, new NodeBadge(C_PENDING, "P", "Pending"));
            case UNKNOWN -> badges.put(id, new NodeBadge(C_UNKNOWN, "?", "Unknown / branch not used"));
        }
    }

    private static void addQualityBadge(Map<String, NodeBadge> badges, String id, QualityStatus status) {
        switch (status) {
            case TRUE -> badges.put(id, new NodeBadge(C_TRUE, "T", "True"));
            case FALSE -> badges.put(id, new NodeBadge(C_FALSE, "F", "False"));
            case UNKNOWN -> badges.put(id, new NodeBadge(C_UNKNOWN, "?", "Unknown"));
        }
    }
}
