package org.vnu.sme.goal.feature.openistar;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.openistar.IStarForm;

public final class ActionOpenIStar implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session    session    = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            IStarForm form = new IStarForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
