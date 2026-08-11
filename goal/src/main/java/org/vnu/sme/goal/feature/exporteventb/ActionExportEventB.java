package org.vnu.sme.goal.feature.exporteventb;

import java.nio.file.Path;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportRequest;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportResult;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportService;

/** USE menu action for the three-model Event-B exporter. */
public final class ActionExportEventB implements IPluginActionDelegate {
    @Override public void performAction(IPluginAction action) {
        Objects.requireNonNull(action, "action");
        Runnable work = () -> run(action.getParent());
        if (SwingUtilities.isEventDispatchThread()) work.run(); else SwingUtilities.invokeLater(work);
    }

    private static void run(MainWindow parent) {
        Path acl = chooseFile(parent, "Select ACL model", "ACL (*.acl)", "acl"); if (acl == null) return;
        Path istar = chooseFile(parent, "Select iStar model", "iStar (*.istar)", "istar"); if (istar == null) return;
        Path bpmn = chooseFile(parent, "Select BPMN model", "BPMN (*.bpmn2)", "bpmn2"); if (bpmn == null) return;
        JFileChooser target = new JFileChooser(acl.toAbsolutePath().getParent().toFile());
        target.setDialogTitle("Select Rodin workspace/output directory"); target.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (target.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;
        String defaultName = base(acl.getFileName().toString()) + "EventB";
        String name = JOptionPane.showInputDialog(parent, "Rodin project name", defaultName);
        if (name == null || name.isBlank()) return;
        EventBExportResult result = new EventBExportService().export(new EventBExportRequest(
                acl, istar, bpmn, target.getSelectedFile().toPath(), name.trim()));
        if (result.success()) JOptionPane.showMessageDialog(parent,
                "Generated Rodin project:\n" + result.projectDirectory()
                        + (result.diagnostics().isEmpty() ? "" : "\n\nTranslation warnings:\n" + String.join("\n", result.diagnostics())),
                "Event-B export", result.diagnostics().isEmpty() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        else JOptionPane.showMessageDialog(parent, String.join("\n", result.diagnostics()),
                "Event-B export failed", JOptionPane.ERROR_MESSAGE);
    }
    private static Path chooseFile(MainWindow parent, String title, String description, String extension) {
        JFileChooser chooser = new JFileChooser(); chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        return chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile().toPath() : null;
    }
    private static String base(String name) { int dot=name.lastIndexOf('.'); return dot>0?name.substring(0,dot):name; }
}
