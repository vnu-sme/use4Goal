package org.vnu.sme.goal.feature.openacl;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
public final class ActionOpenAcl implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        MainWindow mainWindow = pluginAction.getParent();
        Runnable openAcl = () -> openAclForm(mainWindow);
        if (SwingUtilities.isEventDispatchThread()) openAcl.run();
        else SwingUtilities.invokeLater(openAcl);
    }

    private static void openAclForm(MainWindow mainWindow) {
        AclForm form = new AclForm(mainWindow, new AclOpenService(mainWindow));
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setResizable(true);
        form.setLocationRelativeTo(mainWindow);
        form.setVisible(true);
    }
}
