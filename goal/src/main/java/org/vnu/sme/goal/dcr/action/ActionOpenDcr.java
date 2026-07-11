package org.vnu.sme.goal.dcr.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.dcr.gui.DcrForm;

public final class ActionOpenDcr implements IPluginActionDelegate {
    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            DcrForm form = new DcrForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
