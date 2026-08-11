package org.vnu.sme.goal.feature.istaruseocl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.translate.aclistar2use.IStarUseOclFolderService;

/**
 * GUI form for the "iStar + ACL → USE/OCL (Folder)" translation action.
 *
 * <p>Same pipeline as {@link IStarUseOclForm} ({@link
 * org.vnu.sme.goal.translate.aclistar2use.AclIStar2UseTranslator}, unchanged), but the
 * output picker chooses a FOLDER instead of an exact {@code .use} file
 * path -- filenames ({@code <model>.use}, {@code <model>.tocl} if
 * applicable) are derived automatically by {@link IStarUseOclFolderService}.
 */
public final class IStarUseOclFolderForm extends JDialog {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(IStarUseOclFolderForm.class);

    private static final Color COLOR_OK  = new Color(0,   125, 30);
    private static final Color COLOR_ERR = new Color(170,  20, 20);
    private static final Color COLOR_RUN = new Color(140,  90,  0);

    // ── Input fields ──────────────────────────────────────────────────────────
    private final JTextField aclField    = new JTextField(50);
    private final JTextField istarField  = new JTextField(50);
    private final JTextField outputField = new JTextField(50);

    // ── Output / status ───────────────────────────────────────────────────────
    private final JLabel    statusBar  = new JLabel(" ");
    private final JTextArea summaryArea = textArea();
    private final JTextArea useArea     = textArea();
    private final JTextArea toclArea    = textArea();

    private JButton generateBtn;

    // ── Constructor ───────────────────────────────────────────────────────────

    public IStarUseOclFolderForm(MainWindow owner) {
        super(owner, "iStar + ACL → USE/OCL (Folder)", false);
        buildUi();
        restorePrefs();
        setSize(1000, 700);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(owner);
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUi() {
        setLayout(new BorderLayout(5, 5));

        // ── North: file/folder pickers ──
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(new EmptyBorder(8, 8, 4, 8));

        addFileRow(inputPanel, 0, "1. ACL model (.acl):",     aclField,    "acl",   false, false);
        addFileRow(inputPanel, 1, "2. iStar model (.istar):", istarField,  "istar", false, false);
        addFileRow(inputPanel, 2, "Output folder:",           outputField, null,    true,  false);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        generateBtn = new JButton("Generate USE + TOCL");
        generateBtn.setFont(generateBtn.getFont().deriveFont(Font.BOLD));
        generateBtn.addActionListener(e -> runGenerate());
        buttonRow.add(generateBtn);
        buttonRow.add(new JLabel("Writes <model>.use into the folder, plus <model>.tocl if any temporal properties exist."));

        JPanel north = new JPanel(new BorderLayout());
        north.add(inputPanel, BorderLayout.CENTER);
        north.add(buttonRow,  BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // ── Center: tabbed preview ──
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Summary", new JScrollPane(summaryArea));
        tabs.addTab("USE preview", new JScrollPane(useArea));
        tabs.addTab("TOCL preview", new JScrollPane(toclArea));
        add(tabs, BorderLayout.CENTER);

        // ── South: status bar ──
        statusBar.setBorder(new EmptyBorder(3, 9, 5, 9));
        statusBar.setFont(statusBar.getFont().deriveFont(Font.PLAIN, 12f));
        add(statusBar, BorderLayout.SOUTH);

        // Seed summary
        summaryArea.setText(
                "WORKFLOW\n\n" +
                "1. Select an .acl file that defines roles, groups and entities.\n" +
                "2. Select an .istar file whose actors match ACL role names.\n" +
                "3. Choose an output FOLDER (filenames are derived from the model name).\n" +
                "4. Click 'Generate USE + TOCL'.\n\n" +
                "OUTPUT (into the chosen folder)\n\n" +
                "  <model>.use — plain OCL, compiles standalone in USE:\n" +
                "    • USE class diagram (from ACL)\n" +
                "    • derived operations (activation/condition/pre/post per goal/task)\n" +
                "    • refinement structural invariants (AND/OR)\n\n" +
                "  <model>.tocl — only written when the model has temporal properties\n" +
                "  (always/sometime/alwaysPast); load it via the TOCL plugin.");
    }

    // ── File-picker row ───────────────────────────────────────────────────────

    private void addFileRow(JPanel panel, int row, String label,
                            JTextField field, String ext, boolean isDir, boolean isSave) {
        GridBagConstraints c = gbc(row, 0);
        panel.add(new JLabel(label), c);

        c = gbc(row, 1);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        panel.add(field, c);

        JButton browse = new JButton("Browse…");
        browse.addActionListener(e -> pickPath(field, ext, isDir, isSave));
        c = gbc(row, 2);
        panel.add(browse, c);
    }

    private static GridBagConstraints gbc(int row, int col) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = col;
        c.gridy  = row;
        c.insets = new Insets(2, 4, 2, 4);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private void pickPath(JTextField field, String ext, boolean isDir, boolean isSave) {
        JFileChooser fc = new JFileChooser();
        String current = field.getText().trim();
        if (!current.isEmpty()) {
            java.io.File f = new java.io.File(current);
            fc.setCurrentDirectory(f.isDirectory() ? f : f.getParentFile());
            if (isSave && f.isFile()) fc.setSelectedFile(f);
        }
        if (isDir) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else if (ext != null) {
            fc.setFileFilter(new FileNameExtensionFilter(
                    ext.toUpperCase() + " files (*." + ext + ")", ext));
        }
        int ret = isSave || isDir
                ? fc.showSaveDialog(this)
                : fc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            String chosen = fc.getSelectedFile().getAbsolutePath();
            if (isSave && ext != null && !chosen.endsWith("." + ext)) {
                chosen = chosen + "." + ext;
            }
            field.setText(chosen);
        }
    }

    // ── Generate ──────────────────────────────────────────────────────────────

    private void runGenerate() {
        String aclText   = aclField.getText().trim();
        String istarText = istarField.getText().trim();
        String outText   = outputField.getText().trim();

        if (aclText.isEmpty() || istarText.isEmpty() || outText.isEmpty()) {
            setStatus("Please fill in all three paths.", COLOR_ERR);
            return;
        }

        Path aclPath      = Path.of(aclText);
        Path istarPath    = Path.of(istarText);
        Path outputFolder = Path.of(outText);

        savePrefs();
        setStatus("Generating…", COLOR_RUN);
        generateBtn.setEnabled(false);
        useArea.setText("");

        new SwingWorker<IStarUseOclFolderService.Result, Void>() {
            @Override
            protected IStarUseOclFolderService.Result doInBackground() {
                return IStarUseOclFolderService.translate(aclPath, istarPath, outputFolder);
            }

            @Override
            protected void done() {
                generateBtn.setEnabled(true);
                try {
                    IStarUseOclFolderService.Result result = get();
                    applyResult(result);
                } catch (Exception ex) {
                    setStatus("Unexpected error: " + ex.getMessage(), COLOR_ERR);
                }
            }
        }.execute();
    }

    private void applyResult(IStarUseOclFolderService.Result result) {
        List<String> diag = result.allDiagnostics();

        if (!result.errors().isEmpty()) {
            setStatus("Translation failed — see Summary tab.", COLOR_ERR);
        } else {
            setStatus("Generated into: " + result.outputFolder(), COLOR_OK);
        }

        // Summary tab
        StringBuilder sb = new StringBuilder();
        if (!result.errors().isEmpty()) {
            sb.append("=== ERRORS ===\n");
            result.errors().forEach(e -> sb.append("  ✗ ").append(e).append('\n'));
            sb.append('\n');
        }
        if (!diag.isEmpty()) {
            sb.append("=== DIAGNOSTICS ===\n");
            diag.forEach(d -> sb.append("  · ").append(d).append('\n'));
            sb.append('\n');
        }
        if (result.ok() && result.written() != null) {
            sb.append("=== OUTPUT FILES ===\n");
            sb.append("  USE  : ").append(result.written().useFile()).append('\n');
            sb.append("  TOCL : ").append(result.written().toclFile() != null
                    ? result.written().toclFile() : "(none — no temporal properties)").append('\n');
        }
        summaryArea.setText(sb.toString());
        summaryArea.setCaretPosition(0);

        if (result.useResult() != null) {
            useArea.setText(result.useResult().useText());
            useArea.setCaretPosition(0);
            toclArea.setText(result.useResult().toclText());
            toclArea.setCaretPosition(0);
        }
    }

    // ── Preferences ───────────────────────────────────────────────────────────

    private void restorePrefs() {
        aclField.setText(PREFS.get("aclPath",    ""));
        istarField.setText(PREFS.get("istarPath", ""));
        outputField.setText(PREFS.get("outFolder", ""));
    }

    private void savePrefs() {
        PREFS.put("aclPath",   aclField.getText().trim());
        PREFS.put("istarPath", istarField.getText().trim());
        PREFS.put("outFolder", outputField.getText().trim());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setStatus(String msg, Color color) {
        statusBar.setText(msg);
        statusBar.setForeground(color);
    }

    private static JTextArea textArea() {
        JTextArea ta = new JTextArea();
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setLineWrap(false);
        return ta;
    }
}
