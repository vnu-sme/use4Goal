package org.vnu.sme.goal.feature.aclstate;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/** Opens the ACL/AOL snapshot evaluator. */
public final class ActionOpenAclStateEvaluator implements IPluginActionDelegate {
    @Override
    public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> {
            AclStateEvaluatorForm form = new AclStateEvaluatorForm(mainWindow);
            form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            form.setLocationRelativeTo(mainWindow);
            form.setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }
}
