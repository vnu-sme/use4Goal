package org.vnu.sme.goal.conformance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.bpmn2.view.Bpmn2View;
import org.vnu.sme.goal.conformance.AclBpmnIStarConformanceChecker;
import org.vnu.sme.goal.istar.view.IStarView;

/**
 * Four-input conformance UI:
 * ACL + initial SOIL + i* goals + BPMN2/OCL proposed solution.
 */
public final class ConformanceForm extends JDialog {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ConformanceForm.class);
    private static final String PREF_ACL = "conformance4.aclFile";
    private static final String PREF_SOIL = "conformance4.soilFile";
    private static final String PREF_ISTAR = "conformance4.istarFile";
    private static final String PREF_BPMN2 = "conformance4.bpmn2File";

    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    @SuppressWarnings("unused")
    private final Session session;
    @SuppressWarnings("unused")
    private final MainWindow mainWindow;

    private JTextField aclField;
    private JTextField soilField;
    private JTextField istarField;
    private JTextField bpmn2Field;
    private JTextArea resultArea;
    private JLabel statusLabel;
    private IStarView istarView;
    private Bpmn2View bpmn2View;

    public ConformanceForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "ACL / SOIL / i* / BPMN2 Conformance Checker", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        aclField.setText(PREFS.get(PREF_ACL, ""));
        soilField.setText(PREFS.get(PREF_SOIL, ""));
        istarField.setText(PREFS.get(PREF_ISTAR, ""));
        bpmn2Field.setText(PREFS.get(PREF_BPMN2, ""));
        setSize(1400, 900);
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        setLayout(new BorderLayout(4, 4));
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildCanvas(), BorderLayout.CENTER);
        add(buildResult(), BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")),
                new EmptyBorder(2, 4, 2, 4)));

        aclField = new JTextField(42);
        soilField = new JTextField(42);
        istarField = new JTextField(42);
        bpmn2Field = new JTextField(42);

        p.add(fileRow("ACL structure (.acl):", aclField, "ACL (*.acl)", "acl"));
        p.add(fileRow("Initial state (.soil):", soilField, "SOIL (*.soil)", "soil"));
        p.add(fileRow("i* goals (.istar):", istarField, "iStar 2.0 (*.istar)", "istar"));
        p.add(fileRow("BPMN2 solution (.bpmn2):", bpmn2Field, "BPMN 2.0 (*.bpmn2)", "bpmn2"));

        JPanel runRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 6));
        JButton run = new JButton("Check 4-Input Conformance");
        run.setFont(run.getFont().deriveFont(Font.BOLD));
        run.addActionListener(e -> doCheck());
        runRow.add(run);
        p.add(runRow);
        return p;
    }

    private JPanel fileRow(String label, JTextField field, String filterDesc, String ext) {
        JPanel row = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 2));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new java.awt.Dimension(150, l.getPreferredSize().height));
        row.add(l);
        row.add(field);
        JButton browse = new JButton("Browse...");
        browse.addActionListener(e -> chooseFile(field, filterDesc, ext));
        row.add(browse);
        return row;
    }

    private void chooseFile(JTextField field, String filterDesc, String ext) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(filterDesc, ext));
        String cur = field.getText().trim();
        if (!cur.isEmpty()) fc.setSelectedFile(new File(cur));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private JSplitPane buildCanvas() {
        istarView = new IStarView();
        bpmn2View = new Bpmn2View();

        JScrollPane left = new JScrollPane(istarView);
        left.setBorder(new TitledBorder(new EtchedBorder(), "i* Goal Model"));
        JScrollPane right = new JScrollPane(bpmn2View);
        right.setBorder(new TitledBorder(new EtchedBorder(), "BPMN2 Solution Model"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.5);
        return split;
    }

    private JPanel buildResult() {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBorder(new EmptyBorder(0, 6, 6, 6));

        resultArea = new JTextArea(12, 100);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(UIManager.getColor("TextArea.background"));
        JScrollPane sp = new JScrollPane(resultArea);
        sp.setBorder(new EtchedBorder());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 12f));

        p.add(sp, BorderLayout.CENTER);
        p.add(statusLabel, BorderLayout.SOUTH);
        return p;
    }

    private void doCheck() {
        String aclPath = aclField.getText().trim();
        String soilPath = soilField.getText().trim();
        String istarPath = istarField.getText().trim();
        String bpmnPath = bpmn2Field.getText().trim();
        resultArea.setText("");

        if (aclPath.isEmpty() || soilPath.isEmpty() || istarPath.isEmpty() || bpmnPath.isEmpty()) {
            status("Please select all 4 files (.acl, .soil, .istar, .bpmn2).", C_ERR);
            return;
        }

        try {
            AclBpmnIStarConformanceChecker.Result result =
                    AclBpmnIStarConformanceChecker.check(
                            Path.of(aclPath), Path.of(soilPath), Path.of(istarPath), Path.of(bpmnPath));

            if (!result.ok()) {
                result.errors().forEach(this::appendResult);
                status("Conformance check failed before verdict.", C_ERR);
                return;
            }

            istarView.setModel(result.goalModel());
            bpmn2View.setModel(result.bpmnModel());

            appendResult("Generated USE : " + result.generatedUse());
            appendResult("Execution SOIL: " + result.executionSoil());
            appendResult("Checkpoints   : " + result.checkpoints());
            appendResult("BPMN OCL      : " + (result.bpmnFailures().isEmpty() ? "PASS" : "FAIL"));
            result.bpmnFailures().forEach(f -> appendResult("  - " + f));
            appendResult("i* root goals : " + (result.goalFailures().isEmpty() ? "PASS" : "FAIL"));
            result.goalFailures().forEach(f -> appendResult("  - " + f));
            appendResult("Verdict       : " + (result.conformant() ? "CONFORMANT" : "NOT CONFORMANT"));

            PREFS.put(PREF_ACL, aclPath);
            PREFS.put(PREF_SOIL, soilPath);
            PREFS.put(PREF_ISTAR, istarPath);
            PREFS.put(PREF_BPMN2, bpmnPath);

            status(result.conformant() ? "CONFORMANT" : "NOT CONFORMANT",
                    result.conformant() ? C_OK : C_ERR);
        } catch (Exception ex) {
            appendResult("Error: " + ex.getMessage());
            status("Conformance check crashed.", C_ERR);
        }
    }

    private void appendResult(String msg) {
        resultArea.append(msg + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    private void status(String msg, Color c) {
        statusLabel.setText(msg);
        statusLabel.setForeground(c);
    }
}
