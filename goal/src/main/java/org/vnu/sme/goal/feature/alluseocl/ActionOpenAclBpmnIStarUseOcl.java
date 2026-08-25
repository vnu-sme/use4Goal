package org.vnu.sme.goal.feature.alluseocl;

import java.util.Objects;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

/** Opens the combined ACL + BPMN + iStar to USE/TOCL translator. */
public final class ActionOpenAclBpmnIStarUseOcl implements IPluginActionDelegate {
    @Override
    public void performAction(IPluginAction action) {
        Objects.requireNonNull(action, "action");
        Runnable open = () -> {
            AclBpmnIStarUseOclForm form = new AclBpmnIStarUseOclForm(action.getParent());
            form.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            form.setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }
}
