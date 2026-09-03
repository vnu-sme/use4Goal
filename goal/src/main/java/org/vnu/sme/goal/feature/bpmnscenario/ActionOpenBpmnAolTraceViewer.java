package org.vnu.sme.goal.feature.bpmnscenario;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.bpmnscenario.BpmnAolTraceViewerForm;

public final class ActionOpenBpmnAolTraceViewer implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        MainWindow mainWindow = pluginAction.getParent();
        SwingUtilities.invokeLater(() -> {
            BpmnAolTraceViewerForm form = new BpmnAolTraceViewerForm(mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
