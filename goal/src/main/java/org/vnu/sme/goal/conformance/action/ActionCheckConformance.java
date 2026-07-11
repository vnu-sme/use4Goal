package org.vnu.sme.goal.conformance.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.conformance.gui.ConformanceForm;

public final class ActionCheckConformance implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            ConformanceForm form = new ConformanceForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
