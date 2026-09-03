package org.vnu.sme.goal.feature.exportacltouse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;

/** GUI action that validates an ACL specification and exports a USE class model. */
public final class ActionExportAclToUse implements IPluginActionDelegate {
    @Override public void performAction(IPluginAction pluginAction) {
        Objects.requireNonNull(pluginAction, "pluginAction");
        MainWindow parent = pluginAction.getParent();
        Runnable export = () -> export(parent);
        if (SwingUtilities.isEventDispatchThread()) export.run();
        else SwingUtilities.invokeLater(export);
    }

    private static void export(MainWindow parent) {
        JFileChooser sourceChooser = new JFileChooser();
        sourceChooser.setDialogTitle("Select ACL specification");
        sourceChooser.setFileFilter(new FileNameExtensionFilter("ACL files (*.acl)", "acl"));
        if (sourceChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return;
        Path source = sourceChooser.getSelectedFile().toPath();

        AclCompiler.Result result;
        try {
            result = AclCompiler.compile(source);
        } catch (IOException exception) {
            showError(parent, "Cannot read ACL file", exception.getMessage());
            return;
        }
        if (!result.ok()) {
            showError(parent, "ACL validation failed", String.join(System.lineSeparator(), result.errors()));
            return;
        }

        JFileChooser targetChooser = new JFileChooser(source.toAbsolutePath().getParent().toFile());
        targetChooser.setDialogTitle("Export USE class model");
        targetChooser.setFileFilter(new FileNameExtensionFilter("USE model files (*.use)", "use"));
        targetChooser.setSelectedFile(new File(baseName(source.getFileName().toString()) + ".use"));
        if (targetChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;
        Path target = ensureUseExtension(targetChooser.getSelectedFile().toPath());
        if (Files.exists(target)) {
            int answer = JOptionPane.showConfirmDialog(parent, "Overwrite " + target.getFileName() + "?",
                    "Export ACL to USE", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer != JOptionPane.YES_OPTION) return;
        }
        try {
            Files.writeString(target, Acl2UseTranslator.translate(result.model()), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(parent, "Generated " + target.toAbsolutePath(),
                    "ACL to USE", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            showError(parent, "Cannot write USE file", exception.getMessage());
        }
    }

    private static Path ensureUseExtension(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".use")
                ? path : path.resolveSibling(path.getFileName() + ".use");
    }

    private static String baseName(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static void showError(MainWindow parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
