package org.vnu.sme.goal.iscn.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.iscn.gui.IStarScenarioForm;

public final class ActionOpenIStarScenario implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session    session    = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            IStarScenarioForm form = new IStarScenarioForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
