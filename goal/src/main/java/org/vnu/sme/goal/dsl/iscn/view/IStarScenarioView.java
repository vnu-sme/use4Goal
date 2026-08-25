package org.vnu.sme.goal.dsl.iscn.view;

import java.util.LinkedHashMap;
import java.util.Map;

import java.awt.Color;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.dsl.istar.view.IStarView;
import org.vnu.sme.goal.dsl.istar.view.IStarSpecText;
import org.vnu.sme.goal.dsl.istar.view.NodeBadge;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioInstance;
import org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.Result;

/**
 * Scenario view entry point. By default it opens one instance-level diagram that contains the
 * declared actor instances and qualified element occurrences from the .iscn file. The older
 * per-instance type-level overlays remain available for debugging via {@link #openPerInstanceOverlays}.
 */
public final class IStarScenarioView {

    private static final Color C_FULFILLED = new Color(30, 150, 60);
    private static final Color C_PENDING   = new Color(180, 130, 0);
    private static final Color C_TRUE      = new Color(30, 150, 60);
    private static final Color C_FALSE     = new Color(170, 30, 30);

    private IStarScenarioView() {}

    /** Opens the scenario-level diagram expected by the i* scenario action. */
    public static void openAllInstances(MainWindow mainWindow, Result result) {
        openScenarioDiagram(mainWindow, result);
    }

    /** Opens one instance-level scenario diagram that contains all declared actor instances. */
    public static void openScenarioDiagram(MainWindow mainWindow, Result result) {
        IStarScenarioViewModel.Built built = IStarScenarioViewModel.build(result);
        IStarView.openUseDesktop(mainWindow, built.model(), result.modelFile(), built.badges(),
                "scenario " + result.scenarioModel().name(), built.actorLabels(), built.nodeLabels());
    }

    public static String specificationText(Result result) {
        IStarScenarioViewModel.Built built = IStarScenarioViewModel.build(result);
        return IStarSpecText.generate(built.model(), built.badges(), built.actorLabels(), built.nodeLabels());
    }

    public static void openPerInstanceOverlays(MainWindow mainWindow, Result result) {
        boolean labelWindows = result.markings().size() > 1;
        Map<String, String> instanceActorType = new LinkedHashMap<>();
        for (ScenarioInstance instance : result.scenarioModel().instances()) {
            instanceActorType.put(instance.name(), instance.actorType());
        }
        result.markings().forEach((instanceId, marking) -> {
            String actorType = instanceActorType.get(instanceId);
            Map<String, String> labelOverrides = actorType == null ? Map.of() : Map.of(actorType, instanceId);
            IStarView.openUseDesktop(mainWindow, result.model(), result.modelFile(), badgesOf(marking),
                    labelWindows ? instanceId : null, labelOverrides);
        });
    }

    /** Opens the diagram annotated with the first instance's marking (the only one for a single-instance scenario). */
    public static void openUseDesktop(MainWindow mainWindow, Result result) {
        openUseDesktop(mainWindow, result, firstInstance(result));
    }

    public static void openUseDesktop(MainWindow mainWindow, Result result, String instanceId) {
        IStarView.openUseDesktop(mainWindow, result.model(), result.modelFile(), badgesOf(result.markings().get(instanceId)));
    }

    public static void openPopupWindow(MainWindow mainWindow, Result result) {
        openPopupWindow(mainWindow, result, firstInstance(result));
    }

    public static void openPopupWindow(MainWindow mainWindow, Result result, String instanceId) {
        IStarView.openPopupWindow(mainWindow, result.model(), result.modelFile(), badgesOf(result.markings().get(instanceId)));
    }

    private static String firstInstance(Result result) {
        return result.markings().keySet().iterator().next();
    }

    private static Map<String, NodeBadge> badgesOf(IStarMarking marking) {
        Map<String, NodeBadge> badges = new LinkedHashMap<>();
        marking.goalTaskStatuses().forEach((id, status) -> {
            switch (status) {
                case FULFILLED -> badges.put(id, new NodeBadge(C_FULFILLED, "F", "Fulfilled"));
                case PENDING   -> badges.put(id, new NodeBadge(C_PENDING, "P", "Pending"));
                case VIOLATED  -> badges.put(id, new NodeBadge(C_FALSE, "V", "Violated"));
                case UNKNOWN   -> { /* no badge: not part of the scenario */ }
            }
        });
        marking.qualityStatuses().forEach((id, status) -> {
            switch (status) {
                case TRUE    -> badges.put(id, new NodeBadge(C_TRUE, "T", "True"));
                case FALSE   -> badges.put(id, new NodeBadge(C_FALSE, "F", "False"));
                case UNKNOWN -> { /* no badge: not part of the scenario */ }
            }
        });
        return badges;
    }
}
