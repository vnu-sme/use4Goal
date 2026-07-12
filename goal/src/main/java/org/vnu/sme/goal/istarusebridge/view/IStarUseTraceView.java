package org.vnu.sme.goal.istarusebridge.view;

import java.awt.Color;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.istar.mm.Goal;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.mm.Obstacle;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.Resource;
import org.vnu.sme.goal.istar.mm.Task;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceEvaluator;
import org.vnu.sme.goal.istarusebridge.IStarUseTraceEvaluator.Instantiation;
import org.vnu.sme.goal.istar.view.IStarSpecText;
import org.vnu.sme.goal.istar.view.IStarView;
import org.vnu.sme.goal.istar.view.NodeBadge;

/**
 * Full-mode diagram for one checkpoint of an
 * {@link org.vnu.sme.goal.istarusebridge.IStarUseTraceCompiler} trace: one actor box per
 * real {@code .use} object (5 separate Participant boxes, say), each badged with that
 * object's own marking -- the .istar+.use+.soil counterpart of
 * {@code org.vnu.sme.goal.iscn.view.IStarScenarioView.openScenarioDiagram} for {@code .iscn}. Built via
 * {@link IStarUseTraceEvaluator}, then rendered through the same {@link IStarView} engine.
 */
public final class IStarUseTraceView {

    private static final Color C_FULFILLED = new Color(30, 150, 60);
    private static final Color C_PENDING = new Color(180, 130, 0);
    private static final Color C_TRUE = new Color(30, 150, 60);
    private static final Color C_FALSE = new Color(170, 30, 30);
    private static final Color C_UNKNOWN = new Color(150, 150, 150);

    private IStarUseTraceView() {}

    public static void openCheckpoint(MainWindow mainWindow, GoalModel model, Path istarFile, Checkpoint checkpoint) {
        Instantiation inst = IStarUseTraceEvaluator.evaluate(model, checkpoint);
        Map<String, NodeBadge> badges = badgesOf(inst.instanceModel(), inst.instanceMarking());
        IStarView.openUseDesktop(mainWindow, inst.instanceModel(), istarFile, badges,
                "checkpoint " + checkpoint.index() + ": " + checkpoint.soilLine(),
                inst.actorLabels(), inst.nodeLabels());
    }

    public static String specificationText(GoalModel model, Checkpoint checkpoint) {
        Instantiation inst = IStarUseTraceEvaluator.evaluate(model, checkpoint);
        return IStarSpecText.generate(inst.instanceModel(), badgesOf(inst.instanceModel(), inst.instanceMarking()),
                inst.actorLabels(), inst.nodeLabels());
    }

    private static Map<String, NodeBadge> badgesOf(GoalModel instanceModel, IStarMarking marking) {
        Map<String, NodeBadge> badges = new LinkedHashMap<>();
        for (IntentionalElement elem : instanceModel.allElements().values()) {
            switch (elem) {
                case Goal g -> addGoalTaskBadge(badges, g.id(), marking.goalTaskStatus(g.id()));
                case Task t -> addGoalTaskBadge(badges, t.id(), marking.goalTaskStatus(t.id()));
                case Quality q -> addQualityBadge(badges, q.id(), marking.qualityStatus(q.id()));
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
            case UNKNOWN -> badges.put(id, new NodeBadge(C_UNKNOWN, "?", "Unknown"));
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
