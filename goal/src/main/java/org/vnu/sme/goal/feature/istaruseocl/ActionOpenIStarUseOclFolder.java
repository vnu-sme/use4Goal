package org.vnu.sme.goal.feature.istaruseocl;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.istaruseocl.IStarUseOclFolderForm;

/**
 * USE plugin action that opens the iStar + ACL → USE/OCL (Folder) generator
 * form -- same translation as {@link ActionOpenIStarUseOcl}, but writes into
 * a chosen output folder with auto-derived filenames instead of an exact
 * {@code .use} file path.
 *
 * <p>Registered in {@code useplugin.xml} under the "GoalModel Plugin" menu.
 */
public final class ActionOpenIStarUseOclFolder implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> openForm(mainWindow);
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }

    private static void openForm(MainWindow mainWindow) {
        IStarUseOclFolderForm form = new IStarUseOclFolderForm(mainWindow);
        form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        form.setResizable(true);
        form.setLocationRelativeTo(mainWindow);
        form.setVisible(true);
    }
}
