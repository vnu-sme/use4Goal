package org.vnu.sme.goal.acl.action;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.acl.gui.AclForm;

public final class ActionOpenAcl implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        Runnable openAcl = () -> openAclForm(session, mainWindow);
        if (SwingUtilities.isEventDispatchThread()) openAcl.run();
        else SwingUtilities.invokeLater(openAcl);
    }

    private static void openAclForm(Session session, MainWindow mainWindow) {
        AclForm form = new AclForm(session, mainWindow);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setResizable(true);
        form.setLocationRelativeTo(mainWindow);
        form.setVisible(true);
    }
}
