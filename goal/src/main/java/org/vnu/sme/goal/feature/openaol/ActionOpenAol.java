package org.vnu.sme.goal.feature.openaol;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.openaol.AolForm;

public final class ActionOpenAol implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        Runnable openAol = () -> openAolForm(session, mainWindow);
        if (SwingUtilities.isEventDispatchThread()) openAol.run();
        else SwingUtilities.invokeLater(openAol);
    }

    private static void openAolForm(Session session, MainWindow mainWindow) {
        AolForm form = new AolForm(session, mainWindow);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setResizable(true);
        form.setLocationRelativeTo(mainWindow);
        form.setVisible(true);
    }
}
