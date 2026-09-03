package org.vnu.sme.goal.feature.bpmnscenario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile;
import org.vnu.sme.goal.verify.conformance.AolBpmnTraceRunner;

/**
 * Runs a BPMN process seeded by a concrete AOL population and saves the resulting execution
 * trace to a {@code .bpmntrace} file. Generation and viewing are deliberately two separate
 * tools: this one only produces the trace file; {@link BpmnAolTraceViewerForm} (a separate
 * action) is what opens and steps through it.
 */
@SuppressWarnings("serial")
public final class BpmnAolScenarioForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(BpmnAolScenarioForm.class);
    private static final String PREF_BPMN = "bpmn2aolscenario.bpmn";
    private static final String PREF_ACL = "bpmn2aolscenario.acl";
    private static final String PREF_AOL = "bpmn2aolscenario.aol";

    private final Session session;
    private final MainWindow mainWindow;
    private JTextField bpmnField;
    private JTextField aclField;
    private JTextField aolField;
    private JTextPane resultPane;
    private JLabel statusLabel;
    private JButton runButton;
    private JButton saveTraceButton;
    private JProgressBar progressBar;
    private JPanel summaryPanel;
    private AolBpmnTraceRunner.Result lastResult;
    private String lastAclLabel;

    // Styled text colours
    private static final Color C_OK      = new Color(0, 130, 0);
    private static final Color C_ERR     = new Color(180, 0, 0);
    private static final Color C_HEADER  = new Color(30, 80, 160);
    private static final Color C_DELTA   = new Color(150, 60, 0);
    private static final Color C_MONO    = new Color(50, 50, 50);
    private static final Color C_ACCENT  = new Color(30, 80, 160);
    private static final Color C_MUTED   = new Color(110, 110, 110);
    private static final Color C_TILE_BG = new Color(246, 248, 251);
    private static final Font  F_MONO    = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font  F_LABEL   = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    public BpmnAolScenarioForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "Generate BPMN Trace from AOL", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        bpmnField.setText(PREFS.get(PREF_BPMN, ""));
        aclField.setText(PREFS.get(PREF_ACL, ""));
        aolField.setText(PREFS.get(PREF_AOL, ""));
        setSize(880, 620);
        setMinimumSize(new Dimension(680, 440));
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel north = new JPanel();
        north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
        north.add(buildInputsPanel());
        north.add(Box.createVerticalStrut(8));
        north.add(buildSummaryContainer());
        root.add(north, BorderLayout.NORTH);

        root.add(buildResultArea(), BorderLayout.CENTER);
        root.add(buildStatusRow(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ── Inputs ───────────────────────────────────────────────────────────────

    private JPanel buildInputsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(titled("Inputs"));

        bpmnField = new JTextField(36);
        aclField  = new JTextField(36);
        aolField  = new JTextField(36);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(3, 4, 3, 4);
        gc.gridy = 0;
        addInputRow(panel, gc, "BPMN process", bpmnField, "BPMN2 files (*.bpmn2)", "bpmn2");
        gc.gridy = 1;
        addInputRow(panel, gc, "ACL structure", aclField, "ACL files (*.acl)", "acl");
        gc.gridy = 2;
        addInputRow(panel, gc, "AOL population", aolField, "AOL files (*.aol)", "aol");

        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 0));
        runButton = primaryButton("▶  Run");
        runButton.addActionListener(e -> run());
        saveTraceButton = new JButton("Save Trace…");
        saveTraceButton.setEnabled(false);
        saveTraceButton.addActionListener(e -> saveTrace());
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        buttons.add(saveTraceButton);
        buttons.add(runButton);
        buttons.add(close);

        gc.gridy = 3;
        gc.gridx = 0;
        gc.gridwidth = 3;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0;
        gc.insets = new Insets(8, 4, 2, 4);
        panel.add(buttons, gc);

        return panel;
    }

    private void addInputRow(JPanel panel, GridBagConstraints gc, String label, JTextField field,
                             String filterLabel, String extension) {
        JLabel l = new JLabel(label);
        l.setFont(F_LABEL);
        GridBagConstraints lc = (GridBagConstraints) gc.clone();
        lc.gridx = 0;
        lc.anchor = GridBagConstraints.WEST;
        lc.fill = GridBagConstraints.NONE;
        lc.weightx = 0;
        panel.add(l, lc);

        field.setToolTipText(filterLabel);
        GridBagConstraints fc = (GridBagConstraints) gc.clone();
        fc.gridx = 1;
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1;
        panel.add(field, fc);

        JButton browse = new JButton("Browse…");
        browse.addActionListener(e -> chooseFile(field, filterLabel, extension));
        GridBagConstraints bc = (GridBagConstraints) gc.clone();
        bc.gridx = 2;
        bc.fill = GridBagConstraints.NONE;
        bc.weightx = 0;
        panel.add(browse, bc);
    }

    private void chooseFile(JTextField field, String filterLabel, String extension) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(filterLabel, extension));
        String cur = field.getText().trim();
        if (!cur.isEmpty()) fc.setSelectedFile(new File(cur));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    // ── Summary dashboard ────────────────────────────────────────────────────

    private JPanel buildSummaryContainer() {
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        return summaryPanel;
    }

    private void showSummary(AolBpmnTraceRunner.Result result, long ms) {
        summaryPanel.removeAll();
        summaryPanel.setBorder(titled("Summary"));

        int traceCount = result.traces().size();
        long completed = result.traces().stream().filter(AolBpmnTraceRunner.InstanceTrace::ended).count();
        int totalSteps = result.traces().stream().mapToInt(t -> t.frames().size()).sum();
        boolean allOk = completed == traceCount;

        JPanel tiles = new JPanel(new GridLayout(1, 4, 8, 0));
        tiles.add(statTile(String.valueOf(traceCount), "Process instance" + (traceCount == 1 ? "" : "s"), C_ACCENT));
        tiles.add(statTile(String.valueOf(totalSteps), "Step" + (totalSteps == 1 ? "" : "s") + " total", C_ACCENT));
        tiles.add(statTile(completed + " / " + traceCount, "Reached EndEvent", allOk ? C_OK : C_ERR));
        tiles.add(statTile(ms + " ms", "Generation time", C_MUTED));
        summaryPanel.add(tiles);
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private JPanel statTile(String value, String caption, Color accent) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(C_TILE_BG);
        tile.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(8, 12, 8, 12)));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        captionLabel.setForeground(C_MUTED);
        captionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tile.add(valueLabel);
        tile.add(captionLabel);
        return tile;
    }

    // ── Report ───────────────────────────────────────────────────────────────

    private JScrollPane buildResultArea() {
        resultPane = new JTextPane();
        resultPane.setEditable(false);
        resultPane.setFont(F_MONO);
        resultPane.setBackground(UIManager.getColor("TextArea.background"));
        resultPane.setBorder(new EmptyBorder(6, 8, 6, 8));
        StyledDocument doc = resultPane.getStyledDocument();
        Style base = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style header = doc.addStyle("header", base);
        StyleConstants.setForeground(header, C_HEADER);
        StyleConstants.setBold(header, true);
        Style delta = doc.addStyle("delta", base);
        StyleConstants.setForeground(delta, C_DELTA);
        StyleConstants.setBold(delta, true);
        Style mono = doc.addStyle("mono", base);
        StyleConstants.setForeground(mono, C_MONO);
        Style err = doc.addStyle("err", base);
        StyleConstants.setForeground(err, C_ERR);
        StyleConstants.setBold(err, true);
        JScrollPane sp = new JScrollPane(resultPane);
        sp.setBorder(titled("Execution Report"));
        return sp;
    }

    // ── Status bar ───────────────────────────────────────────────────────────

    private JPanel buildStatusRow() {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)),
                new EmptyBorder(6, 2, 0, 2)));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(140, progressBar.getPreferredSize().height));
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        p.add(progressBar, BorderLayout.WEST);
        p.add(statusLabel, BorderLayout.CENTER);
        return p;
    }

    // ── Run / save ───────────────────────────────────────────────────────────

    private void run() {
        clearResult();
        saveTraceButton.setEnabled(false);
        summaryPanel.removeAll();
        summaryPanel.setBorder(null);
        summaryPanel.revalidate();
        summaryPanel.repaint();
        lastResult = null;
        String bpmnPath = bpmnField.getText().trim();
        String aclPath  = aclField.getText().trim();
        String aolPath  = aolField.getText().trim();
        if (bpmnPath.isEmpty() || aclPath.isEmpty() || aolPath.isEmpty()) {
            status("BPMN, ACL, and AOL paths are all required.", C_ERR);
            return;
        }
        PREFS.put(PREF_BPMN, bpmnPath);
        PREFS.put(PREF_ACL, aclPath);
        PREFS.put(PREF_AOL, aolPath);

        // Run trace generation in a background thread so the Swing EDT stays responsive.
        setFormEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        status("Running trace generation...", C_MONO);
        long t0 = System.currentTimeMillis();

        new SwingWorker<AolBpmnTraceRunner.Result, Void>() {
            @Override protected AolBpmnTraceRunner.Result doInBackground() {
                return AolBpmnTraceRunner.run(
                        Path.of(aclPath), Path.of(aolPath), Path.of(bpmnPath));
            }
            @Override protected void done() {
                try {
                    AolBpmnTraceRunner.Result result = get();
                    long ms = System.currentTimeMillis() - t0;
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    setFormEnabled(true);
                    if (!result.ok()) {
                        appendStyled(String.join("\n", result.errors()) + "\n", "err");
                        status("Run failed.", C_ERR);
                        return;
                    }
                    printReport(result, ms);
                    showSummary(result, ms);
                    lastResult = result;
                    lastAclLabel = Path.of(aclPath).getFileName().toString();
                    saveTraceButton.setEnabled(!result.traces().isEmpty());
                    boolean everyTraceEnded = result.traces().stream().allMatch(AolBpmnTraceRunner.InstanceTrace::ended);
                    status(result.traces().size() + " trace(s) generated in " + ms + " ms"
                            + (everyTraceEnded ? "." : " — some did not reach an EndEvent."),
                            everyTraceEnded ? C_OK : C_ERR);
                } catch (Exception ex) {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    setFormEnabled(true);
                    status("Unexpected error: " + ex.getMessage(), C_ERR);
                }
            }
        }.execute();
    }

    private void setFormEnabled(boolean enabled) {
        runButton.setEnabled(enabled);
        bpmnField.setEnabled(enabled);
        aclField.setEnabled(enabled);
        aolField.setEnabled(enabled);
    }

    private void saveTrace() {
        if (lastResult == null) return;
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("BPMN trace files (*.bpmntrace)", "bpmntrace"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File selected = fc.getSelectedFile();
        Path target = selected.getName().endsWith(".bpmntrace")
                ? selected.toPath() : Path.of(selected.getPath() + ".bpmntrace");
        try {
            BpmnAolTraceFile.write(target, lastAclLabel, lastResult);
            status("Trace saved to " + target, C_OK);
        } catch (java.io.IOException ex) {
            status("Could not save trace: " + ex.getMessage(), C_ERR);
        }
    }

    private void printReport(AolBpmnTraceRunner.Result result, long ms) {
        appendStyled("=== Trace Generation Report (%d trace(s), %d ms) ===\n"
                .formatted(result.traces().size(), ms), "header");
        for (AolBpmnTraceRunner.InstanceTrace trace : result.traces()) {
            String label = trace.processId()
                    + (trace.selfObjectName() == null ? "" : "#" + trace.selfObjectName());
            appendStyled("\n── " + label + " ("
                    + (trace.ended() ? "COMPLETE" : "DID NOT REACH END") + ") ──\n", "header");
            for (AolBpmnTraceRunner.Frame frame : trace.frames()) {
                if (frame.activityId() == null) {
                    appendStyled("  [INITIAL]\n", "mono");
                } else {
                    appendStyled("  " + frame.activityId(), "mono");
                    if (!frame.stateDelta().isEmpty())
                        appendStyled(" (" + frame.stateDelta().size() + " change"
                                + (frame.stateDelta().size() > 1 ? "s" : "") + ")", "mono");
                    appendStyled("\n", "mono");
                }
                for (String d : frame.stateDelta())
                    appendStyled("      Δ " + d + "\n", "delta");
            }
        }
    }

    private void clearResult() {
        resultPane.setText("");
    }

    private void appendStyled(String text, String styleName) {
        StyledDocument doc = resultPane.getStyledDocument();
        Style style = doc.getStyle(styleName);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ignored) {}
        resultPane.setCaretPosition(doc.getLength());
    }

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private static TitledBorder titled(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 220)), title);
        border.setTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        border.setTitleColor(new Color(90, 90, 90));
        return border;
    }

    private static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        return b;
    }
}
