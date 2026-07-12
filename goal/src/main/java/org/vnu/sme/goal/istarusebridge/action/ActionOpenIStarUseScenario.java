package org.vnu.sme.goal.istarusebridge.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.istarusebridge.gui.IStarUseScenarioForm;

/** Thin delegate, mirrors {@code ActionOpenIStarScenario}: opens the dialog, nothing else. */
public final class ActionOpenIStarUseScenario implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            IStarUseScenarioForm form = new IStarUseScenarioForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
