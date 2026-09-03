package org.vnu.sme.goal.feature.conformance;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.verify.conformance.AclBpmnIStarConformanceChecker;
import org.vnu.sme.goal.feature.conformance.ConformanceForm;

public final class ActionCheckConformance implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session session = pluginAction.getSession();
        MainWindow mainWindow = pluginAction.getParent();
        Runnable open = () -> {
            ConformanceForm form = new ConformanceForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        };
        if (SwingUtilities.isEventDispatchThread()) open.run();
        else SwingUtilities.invokeLater(open);
    }

    /** Four-input entry point used by the action UI and by repeatable scenario checks. */
    public static AclBpmnIStarConformanceChecker.Result check(
            Path istar, Path bpmn, Path acl, Path soil) throws Exception {
        requireInput(istar, ".istar");
        requireInput(bpmn, ".bpmn2");
        requireInput(acl, ".acl");
        requireInput(soil, ".soil");
        return AclBpmnIStarConformanceChecker.check(acl, soil, istar, bpmn);
    }

    private static void requireInput(Path file, String extension) {
        if (file == null || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("input file does not exist: " + file);
        }
        if (!file.getFileName().toString().toLowerCase().endsWith(extension)) {
            throw new IllegalArgumentException("expected " + extension + " file: " + file);
        }
    }
}
