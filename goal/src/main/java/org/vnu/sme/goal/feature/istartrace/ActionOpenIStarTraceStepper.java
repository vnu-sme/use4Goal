package org.vnu.sme.goal.feature.istartrace;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.istartrace.BpmnTraceIStarMonitorForm;

/** Opens the checkpoint-by-checkpoint iStar execution-trace debugger. */
public final class ActionOpenIStarTraceStepper implements IPluginActionDelegate {
    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> {
            BpmnTraceIStarMonitorForm form = new BpmnTraceIStarMonitorForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }
}
