package org.vnu.sme.goal.feature.bpmnuseocl;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.bpmnuseocl.BpmnUseOclForm;

/**
 * USE plugin action that opens the BPMN + ACL → USE/OCL (Folder) generator
 * form.
 *
 * <p>Registered in {@code useplugin.xml} under the "GoalModel Plugin" menu.
 * The action is intentionally stateless; each invocation creates a new,
 * independent {@link BpmnUseOclForm} dialog.
 */
public final class ActionOpenBpmnUseOcl implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> openForm(mainWindow);
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }

    private static void openForm(MainWindow mainWindow) {
        BpmnUseOclForm form = new BpmnUseOclForm(mainWindow);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setResizable(true);
        form.setLocationRelativeTo(mainWindow);
        form.setVisible(true);
    }
}
