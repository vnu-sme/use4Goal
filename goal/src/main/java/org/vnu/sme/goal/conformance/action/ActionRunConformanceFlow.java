package org.vnu.sme.goal.conformance.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.conformance.gui.ConformanceFlowForm;

/** USE entry point for the ordered ACL/AOL/iStar/ISCN/BPMN conformance flow. */
public final class ActionRunConformanceFlow implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            ConformanceFlowForm form = new ConformanceFlowForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }

    @Override
    public boolean shouldBeEnabled(IPluginAction pluginAction) {
        return true;
    }
}
