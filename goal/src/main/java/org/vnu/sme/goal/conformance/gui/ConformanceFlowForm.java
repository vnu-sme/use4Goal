package org.vnu.sme.goal.conformance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.bpmn2.view.Bpmn2View;
import org.vnu.sme.goal.conformance.flow.ConformanceFlowRunner;
import org.vnu.sme.goal.conformance.flow.ConformanceFlowRunner.Result;
import org.vnu.sme.goal.istar.view.IStarView;

/**
 * Five-input coordinator UI. The individual language actions remain useful as
 * viewers, while this form owns the ordered end-to-end execution.
 */
public final class ConformanceFlowForm extends JDialog {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(ConformanceFlowForm.class);
    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);
    private static final Color C_RUN = new Color(120, 80, 0);

    @SuppressWarnings("unused")
    private final Session session;
    private final MainWindow mainWindow;

    private final JTextField aclField = new JTextField(48);
    private final JTextField aolField = new JTextField(48);
    private final JTextField istarField = new JTextField(48);
    private final JTextField iscnField = new JTextField(48);
    private final JTextField bpmnField = new JTextField(48);
    private final JTextArea resultArea = new JTextArea(30, 110);
    private final JLabel statusLabel = new JLabel(" ");
    private JButton runButton;

    public ConformanceFlowForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "Complete iStar / BPMN2 Conformance Flow", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        restorePaths();
        setSize(1050, 720);
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        setLayout(new BorderLayout(4, 4));
        add(buildInputs(), BorderLayout.NORTH);
        add(buildResults(), BorderLayout.CENTER);
    }

    private JPanel buildInputs() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")),
                new EmptyBorder(4, 6, 4, 6)));

        JPanel grid = new JPanel(new GridBagLayout());
        addFileRow(grid, 0, "1. ACL schema (.acl):", aclField, "ACL (*.acl)", "acl");
        addFileRow(grid, 1, "2. AOL snapshot (.aol):", aolField, "AOL (*.aol)", "aol");
        addFileRow(grid, 2, "3. iStar model (.istar):", istarField, "iStar 2.0 (*.istar)", "istar");
        addFileRow(grid, 3, "4. ISCN oracle (.iscn):", iscnField, "iStar scenario (*.iscn)", "iscn");
        addFileRow(grid, 4, "5. BPMN2 + OCL (.bpmn2):", bpmnField, "BPMN 2.0 (*.bpmn2)", "bpmn2");
        outer.add(grid, BorderLayout.CENTER);

        JPanel controls = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        runButton = new JButton("Run Complete Flow");
        runButton.setFont(runButton.getFont().deriveFont(Font.BOLD));
        runButton.addActionListener(event -> runFlow());
        controls.add(runButton);
        controls.add(new JLabel(
                "Concrete trace: ACL→USE, AOL→SOIL, BPMN effects/OCL, iStar OCL, ISCN oracle."));
        outer.add(controls, BorderLayout.SOUTH);
        return outer;
    }

    private void addFileRow(
            JPanel panel,
            int row,
            String label,
            JTextField field,
            String filterDescription,
            String extension) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(2, 3, 2, 3);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        panel.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);

        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        JButton browse = new JButton("Browse...");
        browse.addActionListener(event ->
                chooseFile(field, filterDescription, extension));
        panel.add(browse, c);
    }

    private void chooseFile(
            JTextField field,
            String filterDescription,
            String extension) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(filterDescription, extension));
        String current = field.getText().trim();
        if (!current.isEmpty()) chooser.setSelectedFile(new File(current));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private JPanel buildResults() {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setBorder(new EmptyBorder(0, 6, 6, 6));
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setBackground(UIManager.getColor("TextArea.background"));
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void runFlow() {
        String acl = aclField.getText().trim();
        String aol = aolField.getText().trim();
        String istar = istarField.getText().trim();
        String iscn = iscnField.getText().trim();
        String bpmn = bpmnField.getText().trim();
        if (acl.isEmpty() || aol.isEmpty() || istar.isEmpty()
                || iscn.isEmpty() || bpmn.isEmpty()) {
            status("Select all five flow inputs.", C_ERR);
            return;
        }
        final Path aclPath;
        final Path aolPath;
        final Path istarPath;
        final Path iscnPath;
        final Path bpmnPath;
        try {
            aclPath = Path.of(acl);
            aolPath = Path.of(aol);
            istarPath = Path.of(istar);
            iscnPath = Path.of(iscn);
            bpmnPath = Path.of(bpmn);
        } catch (RuntimeException ex) {
            status("One of the selected paths is invalid: " + rootMessage(ex), C_ERR);
            return;
        }

        resultArea.setText("");
        runButton.setEnabled(false);
        status("Running ordered flow...", C_RUN);
        new SwingWorker<Result, Void>() {
            @Override
            protected Result doInBackground() {
                return ConformanceFlowRunner.run(
                        aclPath, aolPath, istarPath, iscnPath, bpmnPath);
            }

            @Override
            protected void done() {
                try {
                    showResult(get(), istarPath, bpmnPath);
                } catch (Exception ex) {
                    append("Unexpected UI failure: " + rootMessage(ex));
                    status("Flow crashed.", C_ERR);
                } finally {
                    runButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void showResult(Result result, Path istarPath, Path bpmnPath) {
        append("STAGES");
        for (var stage : result.stages()) {
            append(String.format("  %-30s %-6s  %s",
                    stage.stage().label(), stage.state(), stage.detail()));
        }
        append("");

        result.errors().forEach(this::append);
        if (!result.ok()) {
            append("VERDICT: " + result.verdict());
            status(result.verdict().name(), C_ERR);
            return;
        }

        IStarView.openUseDesktop(
                mainWindow, result.goalModel(), istarPath);
        Bpmn2View.openUseDesktop(
                mainWindow, result.bpmnModel(), bpmnPath);

        append("ARTIFACTS");
        append("  Initial SOIL : " + result.generatedInitialSoil());
        append("");

        append("EXECUTION SPACE");
        append("  Traces       : " + result.traces().size());
        append("  Conformant   : " + result.conformantTraceCount());
        append("  Complete     : " + result.completeExecutionSpace());
        append("");

        for (var trace : result.traces()) {
            var conformance = trace.conformance();
            append("TRACE #" + trace.index() + ": " + trace.verdict());
            append("  Activities   : " + String.join(" -> ", trace.activityIds()));
            append("  Generated USE: " + conformance.generatedUse());
            append("  Execution SOIL: " + conformance.executionSoil());
            append("  Checkpoints  : " + conformance.checkpoints());
            appendGroup("  ACL invariants", conformance.aclFailures());
            appendGroup("  BPMN pre/post OCL", conformance.bpmnFailures());
            appendGroup("  iStar root goals", conformance.goalFailures());
            appendGroup("  ISCN oracle", trace.oracleFailures());
            append("");
        }

        append("");
        append("VERDICT: " + result.verdict());
        append("Scope  : " + (result.verdict().isProcessLevel()
                ? "complete supported BPMN execution space"
                : "one concrete trace"));

        rememberPaths();
        status(result.verdict().name(),
                result.conformant() ? C_OK : C_ERR);
    }

    private void appendGroup(String label, java.util.List<String> failures) {
        append(label + ": " + (failures.isEmpty() ? "PASS" : "FAIL"));
        failures.forEach(failure -> append("  - " + failure));
    }

    private void append(String message) {
        resultArea.append(message + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    private void status(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private void restorePaths() {
        aclField.setText(PREFS.get("completeFlow.acl", ""));
        aolField.setText(PREFS.get("completeFlow.aol", ""));
        istarField.setText(PREFS.get("completeFlow.istar", ""));
        iscnField.setText(PREFS.get("completeFlow.iscn", ""));
        bpmnField.setText(PREFS.get("completeFlow.bpmn2", ""));
    }

    private void rememberPaths() {
        PREFS.put("completeFlow.acl", aclField.getText().trim());
        PREFS.put("completeFlow.aol", aolField.getText().trim());
        PREFS.put("completeFlow.istar", istarField.getText().trim());
        PREFS.put("completeFlow.iscn", iscnField.getText().trim());
        PREFS.put("completeFlow.bpmn2", bpmnField.getText().trim());
    }

    private static String rootMessage(Exception ex) {
        Throwable current = ex;
        while (current.getCause() != null) current = current.getCause();
        return current.getMessage() == null ? current.toString() : current.getMessage();
    }
}
