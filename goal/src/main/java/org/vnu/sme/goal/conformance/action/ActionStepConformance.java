package org.vnu.sme.goal.conformance.action;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.conformance.gui.StepConformanceForm;

/** Opens the non-destructive, checkpoint-by-checkpoint conformance debugger. */
public final class ActionStepConformance implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> {
            StepConformanceForm form = new StepConformanceForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }
}
