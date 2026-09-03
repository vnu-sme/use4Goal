package org.vnu.sme.goal.feature.conformance;

import javax.swing.SwingUtilities;

import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.feature.conformance.FormalVerificationForm;

/** Opens the single end-to-end formal verification workspace. */
public final class ActionOpenFormalVerification implements IPluginActionDelegate {
    @Override public void performAction(IPluginAction action) {
        SwingUtilities.invokeLater(() -> {
            FormalVerificationForm form = new FormalVerificationForm(action.getParent());
            form.setVisible(true);
        });
    }

    @Override public boolean shouldBeEnabled(IPluginAction action) { return true; }
}
