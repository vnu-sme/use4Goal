package org.vnu.sme.goal.bpmn2.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.bpmn2.gui.Bpmn2Form;

public final class ActionOpenBpmn2 implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session    session    = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            Bpmn2Form form = new Bpmn2Form(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
