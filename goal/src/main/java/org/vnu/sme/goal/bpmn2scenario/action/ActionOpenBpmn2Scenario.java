package org.vnu.sme.goal.bpmn2scenario.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.bpmn2scenario.gui.Bpmn2ScenarioForm;

public final class ActionOpenBpmn2Scenario implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            Bpmn2ScenarioForm form = new Bpmn2ScenarioForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
