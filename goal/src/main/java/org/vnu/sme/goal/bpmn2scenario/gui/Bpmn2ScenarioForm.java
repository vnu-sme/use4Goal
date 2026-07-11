package org.vnu.sme.goal.bpmn2scenario.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.bpmn2.view.Bpmn2View;
import org.vnu.sme.goal.bpmn2scenario.mm.AssertionResult;
import org.vnu.sme.goal.bpmn2scenario.parser.Bpmn2ScenarioCompiler;

/** Loader for .bscn files; compiles and opens the referenced BPMN model with scenario badges. */
@SuppressWarnings("serial")
public final class Bpmn2ScenarioForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(Bpmn2ScenarioForm.class);
    private static final String PREF_KEY = "bpmn2scenario.lastFile";
    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    @SuppressWarnings("unused")
    private final Session session;
    private final MainWindow mainWindow;
    private JTextField pathField;
    private JTextArea resultArea;
    private JLabel statusLabel;

    public Bpmn2ScenarioForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "Open BPMN Scenario", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        String last = PREFS.get(PREF_KEY, "");
        if (!last.isEmpty()) pathField.setText(last);
        setSize(780, 500);
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
        pathField = new JTextField(42);
        pathField.setToolTipText("Path to .bscn BPMN scenario file");
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
        resultArea = new JTextArea(18, 66);
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
        fc.setFileFilter(new FileNameExtensionFilter("BPMN Scenario files (*.bscn)", "bscn"));
        String cur = pathField.getText().trim();
        if (!cur.isEmpty()) fc.setSelectedFile(new File(cur));
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
            Bpmn2ScenarioCompiler.Result result = Bpmn2ScenarioCompiler.compile(Path.of(path));
            if (!result.ok()) {
                appendResult(String.join("\n", result.errors()));
                status("Load failed.", C_ERR);
                return;
            }
            printReport(result);
            Path scenarioFile = Path.of(path);
            String scenarioSource = scenarioSourceText(scenarioFile, resultArea.getText());
            Bpmn2View.openUseDesktop(mainWindow, result.model(), result.modelFile(), result.snapshot(),
                    scenarioFile, scenarioSource);
            PREFS.put(PREF_KEY, path);
            boolean assertionsHold = result.assertions().stream().allMatch(AssertionResult::holds);
            status("Scenario '" + result.scenario().name() + "' loaded"
                    + (assertionsHold ? " — assertions hold." : " — some assertions FAIL."),
                    assertionsHold ? C_OK : C_ERR);
            dispose();
        } catch (IOException ex) {
            appendResult("IO error: " + ex.getMessage());
            status("IO error.", C_ERR);
        }
    }

    private String scenarioSourceText(Path scenarioFile, String report) throws IOException {
        return "Scenario source: " + scenarioFile + "\n\n"
                + Files.readString(scenarioFile)
                + "\n\n--- Compile report ---\n"
                + report;
    }

    private void printReport(Bpmn2ScenarioCompiler.Result result) {
        appendResult("Scenario : " + result.scenario().name());
        appendResult("Model    : " + result.modelFile());
        appendResult("Processes: " + result.snapshot().processInstances().size());
        appendResult("Actors   : " + result.snapshot().actors().size());
        appendResult("");

        appendResult("Completed:");
        result.snapshot().completed().forEach(o -> appendResult("  " + o.display()));
        appendResult("Active:");
        result.snapshot().active().forEach(o -> appendResult("  " + o.display()));
        appendResult("Tokens:");
        result.snapshot().tokens().forEach(t -> appendResult("  " + t.display()));
        appendResult("Assertions:");
        result.assertions().forEach(a -> appendResult("  " + a.expression()
                + " -> " + (a.holds() ? "HOLDS" : "FAILS") + " (" + a.detail() + ")"));
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
