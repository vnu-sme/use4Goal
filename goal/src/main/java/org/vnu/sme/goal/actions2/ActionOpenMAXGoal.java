package org.vnu.sme.goal.actions2;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.gui2.MAXGoalForm;

public final class ActionOpenMAXGoal implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session    session    = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            MAXGoalForm form = new MAXGoalForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
