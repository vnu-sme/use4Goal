package org.vnu.sme.goal.feature.openistarscenario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.verify.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.dsl.iscn.mm.AggregateResult;
import org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler;
import org.vnu.sme.goal.dsl.iscn.view.IStarScenarioView;

/**
 * Loader for an .iscn file — compiles it (which resolves and compiles the .istar model it
 * targets, then executes the fire-script), prints the resulting marking, and opens the
 * target model's SR diagram annotated with satisfaction badges via {@link org.vnu.sme.goal.dsl.iscn.view.IStarScenarioView}.
 */
@SuppressWarnings("serial")
public final class IStarScenarioForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(IStarScenarioForm.class);
    private static final String PREF_KEY = "iscn.lastFile";
    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    @SuppressWarnings("unused")
    private final Session session;
    private final MainWindow mainWindow;
    private JTextField pathField;
    private JTextArea resultArea;
    private JLabel statusLabel;

    public IStarScenarioForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "Open i* Scenario", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        String last = PREFS.get(PREF_KEY, "");
        if (!last.isEmpty()) {
            pathField.setText(last);
        }
        setSize(720, 480);
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        root.add(buildFileRow(), BorderLayout.NORTH);
        root.add(buildResultArea(), BorderLayout.CENTER);
        root.add(buildStatusRow(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildFileRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pathField = new JTextField(38);
        pathField.setToolTipText("Path to .iscn scenario file");
        pathField.addActionListener(e -> open());

        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> chooseFile());

        JButton open = new JButton("Open");
        open.addActionListener(e -> open());

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());

        p.add(new JLabel("File:"));
        p.add(pathField);
        p.add(browse);
        p.add(open);
        p.add(close);
        return p;
    }

    private JScrollPane buildResultArea() {
        resultArea = new JTextArea(18, 60);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(UIManager.getColor("TextArea.background"));
        JScrollPane sp = new JScrollPane(resultArea);
        sp.setBorder(new EtchedBorder());
        return sp;
    }

    private JPanel buildStatusRow() {
        JPanel p = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(new EmptyBorder(2, 2, 0, 2));
        p.add(statusLabel, BorderLayout.CENTER);
        return p;
    }

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("i* Scenario files (*.iscn)", "iscn"));
        String cur = pathField.getText().trim();
        if (!cur.isEmpty()) {
            fc.setSelectedFile(new File(cur));
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
            open();
        }
    }

    private void open() {
        String path = pathField.getText().trim();
        resultArea.setText("");
        if (path.isEmpty()) {
            status("No file selected.", C_ERR);
            return;
        }
        try {
            org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.Result result = org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.compile(Path.of(path));
            if (!result.ok()) {
                appendResult(String.join("\n", result.errors()));
                status("Load failed.", C_ERR);
                return;
            }
            printReport(result);
            org.vnu.sme.goal.dsl.iscn.view.IStarScenarioView.openAllInstances(mainWindow, result);
            PREFS.put(PREF_KEY, path);
            boolean allAggregatesHold = result.aggregates().stream().allMatch(AggregateResult::holds);
            status("Scenario '" + result.scenarioModel().name() + "' evaluated ("
                    + result.markings().size() + " instance(s))"
                    + (result.aggregates().isEmpty() ? "." : allAggregatesHold ? " — all aggregates hold." : " — some aggregates FAIL."),
                    allAggregatesHold ? C_OK : C_ERR);
        } catch (IOException ex) {
            appendResult("IO error: " + ex.getMessage());
            status("IO error.", C_ERR);
        }
    }

    private void printReport(org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.Result result) {
        appendResult("Scenario : " + result.scenarioModel().name());
        appendResult("Model    : " + result.modelFile());
        appendResult("Instances: " + result.markings().size()
                + (result.markings().size() == 1 && result.markings().containsKey(org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.DEFAULT_INSTANCE)
                        ? " (default, no 'instance' declared)" : ""));
        appendResult("");

        if (result.evaluation() != null) {
            appendResult("── instance-level scenario graph ──");
            printMarking(result.evaluation().instanceMarking());
            appendResult("");
        }

        appendResult("Per-instance private traces:");
        for (var entry : result.markings().entrySet()) {
            String label = entry.getKey().equals(org.vnu.sme.goal.dsl.iscn.parser.IStarScenarioCompiler.DEFAULT_INSTANCE) ? "(default)" : entry.getKey();
            appendResult("── " + label + " ──");
            printMarking(entry.getValue());
            appendResult("");
        }

        if (!result.aggregates().isEmpty()) {
            appendResult("Aggregates:");
            for (AggregateResult a : result.aggregates()) {
                appendResult("  " + a.label() + " : " + a.mode() + " over " + a.elementId()
                        + " -> " + (a.holds() ? "HOLDS" : "FAILS")
                        + " (" + a.satisfiedInstances().size() + "/" + a.allInstances().size() + ")");
                if (!a.holds()) {
                    List<String> unsatisfied = new ArrayList<>(a.allInstances());
                    unsatisfied.removeAll(a.satisfiedInstances());
                    appendResult("    not satisfied by: " + String.join(", ", unsatisfied));
                }
            }
        }
    }

    private void printMarking(IStarMarking marking) {
        for (var e : marking.goalTaskStatuses().entrySet()) {
            if (e.getValue() == GoalTaskStatus.UNKNOWN) continue;
            appendResult("  " + e.getKey() + " = " + e.getValue());
        }
        for (var e : marking.qualityStatuses().entrySet()) {
            if (e.getValue() == QualityStatus.UNKNOWN) continue;
            appendResult("  " + e.getKey() + " = " + e.getValue());
        }
    }

    private void appendResult(String msg) {
        resultArea.append(msg + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
}
