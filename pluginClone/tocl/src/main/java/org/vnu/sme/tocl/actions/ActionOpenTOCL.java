package org.vnu.sme.tocl.actions;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.tocl.gui.TemporalOCLForm;

public class ActionOpenTOCL implements IPluginActionDelegate {
    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        TemporalOCLForm temporalOCLForm = new TemporalOCLForm(session, mainWindow);
        temporalOCLForm.setResizable(true);
    }
}
