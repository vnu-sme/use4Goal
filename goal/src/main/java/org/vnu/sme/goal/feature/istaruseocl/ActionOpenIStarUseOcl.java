package org.vnu.sme.goal.feature.istaruseocl;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.istaruseocl.IStarUseOclForm;

/**
 * USE plugin action that opens the iStar + ACL → USE / TOCL generator form.
 *
 * <p>Registered in {@code useplugin.xml} under the "GoalModel Plugin" menu.
 * The action is intentionally stateless; each invocation creates a new,
 * independent {@link IStarUseOclForm} dialog.
 */
public final class ActionOpenIStarUseOcl implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> openForm(mainWindow);
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }

    private static void openForm(MainWindow mainWindow) {
        IStarUseOclForm form = new IStarUseOclForm(mainWindow);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setResizable(true);
        form.setLocationRelativeTo(mainWindow);
        form.setVisible(true);
    }
}
