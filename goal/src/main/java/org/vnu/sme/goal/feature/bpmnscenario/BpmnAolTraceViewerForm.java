package org.vnu.sme.goal.feature.bpmnscenario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;
import org.vnu.sme.goal.dsl.aol.view.AolSpecText;
import org.vnu.sme.goal.dsl.aol.view.AolView;
import java.awt.Font;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile.InstanceTrace;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile.Step;
import org.vnu.sme.goal.trace.bpmn.BpmnAolTraceFile.TraceFile;

/**
 * Opens an already-generated {@code .bpmntrace} file and lets you inspect it two ways:
 * <ul>
 *   <li>Step view — Prev/Next move one frame at a time through the same {@link AolView} the
 *       static {@code .aol} viewer uses.</li>
 *   <li>Table view — every step at once, columns = steps, rows = objects (agents, the group
 *       instance, and each role occurrence), so an attribute's whole history is one row.</li>
 * </ul>
 * Deliberately does not run BPMN/ACL/AOL itself — that is {@link BpmnAolScenarioForm}'s job.
 */
@SuppressWarnings("serial")
public final class BpmnAolTraceViewerForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(BpmnAolTraceViewerForm.class);
    private static final String PREF_TRACE = "bpmn2aoltraceviewer.trace";
    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    private final MainWindow mainWindow;
    private final AolView aolView;
    private JTextField pathField;
    private JLabel statusLabel;
    private JComboBox<String> instanceChooser;
    private JLabel stepLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel tableHolder;
    private JPanel deltaLogHolder;
    private JTabbedPane tabs;
    private JTextPane specPane;
    private TraceFile traceFile;
    private int frameIndex;

    private static final Color C_STEP  = new Color(30, 80, 160);
    private static final Color C_DELTA = new Color(150, 60, 0);
    private static final Color C_PLAIN = new Color(50, 50, 50);

    public BpmnAolTraceViewerForm(MainWindow mainWindow) {
        super(mainWindow, "BPMN Trace Viewer", false);
        this.mainWindow = mainWindow;
        this.aolView = AolView.embedded(mainWindow);
        buildUI();
        pathField.setText(PREFS.get(PREF_TRACE, ""));
        setSize(1050, 720);
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        root.add(buildFileRow(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.addTab("Step View", buildStepView());
        tableHolder = new JPanel(new BorderLayout());
        tabs.addTab("Table View", tableHolder);
        deltaLogHolder = new JPanel(new BorderLayout());
        tabs.addTab("Delta Log", deltaLogHolder);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) rebuildTable();
            else if (tabs.getSelectedIndex() == 2) rebuildDeltaLog();
        });
        root.add(tabs, BorderLayout.CENTER);

        JPanel status = new JPanel(new BorderLayout());
        statusLabel = new JLabel(" ");
        statusLabel.setBorder(new EmptyBorder(2, 2, 0, 2));
        status.add(statusLabel, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildFileRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pathField = new JTextField(46);
        pathField.setToolTipText("Path to .bpmntrace file");
        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> chooseFile());
        JButton load = new JButton("Load");
        load.addActionListener(e -> load());
        p.add(new JLabel("Trace:"));
        p.add(pathField);
        p.add(browse);
        p.add(load);
        return p;
    }

    private JPanel buildStepView() {
        JPanel content = new JPanel(new BorderLayout(0, 4));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        top.add(new JLabel("Instance:"));
        instanceChooser = new JComboBox<>();
        instanceChooser.addActionListener(e -> { frameIndex = 0; showFrame(); rebuildDeltaLog(); });
        top.add(instanceChooser);
        content.add(top, BorderLayout.NORTH);

        // Each step's marking is shown two ways -- Graphical (the same AolView the static
        // .aol viewer uses) and Specification (AolSpecText's readable AOL-source rendering
        // of the exact same model) -- kept in sync by showFrame() regardless of which of
        // the two is currently selected, so Prev/Next always advances both.
        JTabbedPane markingTabs = new JTabbedPane();
        markingTabs.addTab("Graphical", aolView);
        markingTabs.addTab("Specification", buildSpecPane());
        content.add(markingTabs, BorderLayout.CENTER);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        prevButton = new JButton("\u2039 Prev");
        prevButton.addActionListener(e -> { frameIndex--; showFrame(); });
        nextButton = new JButton("Next \u203a");
        nextButton.addActionListener(e -> { frameIndex++; showFrame(); });
        stepLabel = new JLabel(" ", JLabel.CENTER);
        nav.add(prevButton);
        nav.add(stepLabel);
        nav.add(nextButton);
        content.add(nav, BorderLayout.SOUTH);
        return content;
    }

    private JScrollPane buildSpecPane() {
        specPane = new JTextPane();
        specPane.setEditable(false);
        specPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        specPane.setBorder(new EmptyBorder(6, 8, 6, 8));
        return new JScrollPane(specPane);
    }

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("BPMN trace files (*.bpmntrace)", "bpmntrace"));
        String cur = pathField.getText().trim();
        if (!cur.isEmpty()) fc.setSelectedFile(new File(cur));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
            load();
        }
    }

    private void load() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            status("No file selected.", C_ERR);
            return;
        }
        try {
            traceFile = BpmnAolTraceFile.read(Path.of(path));
        } catch (IOException ex) {
            status("Could not read trace: " + ex.getMessage(), C_ERR);
            return;
        }
        if (traceFile.traces().isEmpty()) {
            status("Trace file has no process instances.", C_ERR);
            return;
        }
        PREFS.put(PREF_TRACE, path);
        instanceChooser.removeAllItems();
        for (InstanceTrace trace : traceFile.traces()) {
            instanceChooser.addItem(trace.processId()
                    + (trace.selfObjectName() == null ? "" : "#" + trace.selfObjectName()));
        }
        frameIndex = 0;
        showFrame();
        rebuildTable();
        rebuildDeltaLog();
        status("Loaded " + traceFile.traces().size() + " instance trace(s) from " + path, C_OK);
    }

    private void showFrame() {
        if (traceFile == null || instanceChooser.getItemCount() == 0) return;
        InstanceTrace trace = traceFile.traces().get(instanceChooser.getSelectedIndex());
        List<Step> steps = trace.steps();
        frameIndex = Math.max(0, Math.min(frameIndex, steps.size() - 1));
        Step step = steps.get(frameIndex);

        String description = step.activityId() == null ? "initial state (from AOL)" : "after " + step.activityId()
                + (step.delta().isEmpty() ? "" : " (" + step.delta().size() + " change(s))");
        stepLabel.setText("Step " + frameIndex + " / " + (steps.size() - 1) + " — " + description);
        prevButton.setEnabled(frameIndex > 0);
        nextButton.setEnabled(frameIndex < steps.size() - 1);
        aolView.setModel(step.model());
        specPane.setText(AolSpecText.render(step.model()));
        specPane.setCaretPosition(0);
    }

    private void rebuildTable() {
        tableHolder.removeAll();
        if (traceFile == null || instanceChooser.getItemCount() == 0) {
            tableHolder.revalidate();
            tableHolder.repaint();
            return;
        }
        InstanceTrace trace = traceFile.traces().get(instanceChooser.getSelectedIndex());
        List<Step> steps = trace.steps();

        Set<String> rowKeys = new LinkedHashSet<>();
        Map<String, String> rowLabels = new LinkedHashMap<>();
        for (Step step : steps) collectRows(step.model(), rowKeys, rowLabels);

        String[] columns = new String[steps.size() + 1];
        columns[0] = "Object";
        for (int c = 0; c < steps.size(); c++) {
            Step step = steps.get(c);
            columns[c + 1] = c + ": " + (step.activityId() == null ? "INITIAL" : step.activityId());
        }

        Object[][] rows = new Object[rowKeys.size()][columns.length];
        int r = 0;
        for (String key : rowKeys) {
            rows[r][0] = rowLabels.get(key);
            for (int c = 0; c < steps.size(); c++) {
                rows[r][c + 1] = formatAttributes(attributesOf(steps.get(c).model(), key));
            }
            r++;
        }

        JTable table = new JTable(new DefaultTableModel(rows, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        for (int c = 1; c < columns.length; c++) table.getColumnModel().getColumn(c).setPreferredWidth(220);
        tableHolder.add(new JScrollPane(table), BorderLayout.CENTER);
        tableHolder.revalidate();
        tableHolder.repaint();
    }

    /**
     * Rebuilds the Delta Log tab: a chronological text log of all state changes.
     * Each step is a header line; each delta entry is indented and coloured.
     */
    private void rebuildDeltaLog() {
        deltaLogHolder.removeAll();
        if (traceFile == null || instanceChooser.getItemCount() == 0) {
            deltaLogHolder.revalidate();
            deltaLogHolder.repaint();
            return;
        }
        InstanceTrace trace = traceFile.traces().get(instanceChooser.getSelectedIndex());

        JTextPane pane = new JTextPane();
        pane.setEditable(false);
        StyledDocument doc = pane.getStyledDocument();
        Style base = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style stepStyle = doc.addStyle("step", base);
        StyleConstants.setForeground(stepStyle, C_STEP);
        StyleConstants.setBold(stepStyle, true);
        Style deltaStyle = doc.addStyle("delta", base);
        StyleConstants.setForeground(deltaStyle, C_DELTA);
        StyleConstants.setBold(deltaStyle, true);
        Style plainStyle = doc.addStyle("plain", base);
        StyleConstants.setForeground(plainStyle, C_PLAIN);

        int stepNum = 0;
        for (Step step : trace.steps()) {
            String header = "Step " + stepNum + " — "
                    + (step.activityId() == null ? "INITIAL" : step.activityId()) + "\n";
            try { doc.insertString(doc.getLength(), header, stepStyle); } catch (BadLocationException ignored) {}
            if (step.delta().isEmpty()) {
                try { doc.insertString(doc.getLength(), "    (no changes)\n", plainStyle); } catch (BadLocationException ignored) {}
            } else {
                for (String d : step.delta()) {
                    try { doc.insertString(doc.getLength(), "    Δ " + d + "\n", deltaStyle); } catch (BadLocationException ignored) {}
                }
            }
            stepNum++;
        }
        deltaLogHolder.add(new JScrollPane(pane), BorderLayout.CENTER);
        deltaLogHolder.revalidate();
        deltaLogHolder.repaint();
    }

    /** Row identity: agent name, the group's own instanceId, and each play's instanceId. */
    private static void collectRows(AolModel model, Set<String> keys, Map<String, String> labels) {
        for (String agent : model.agents()) {
            keys.add("agent:" + agent);
            labels.put("agent:" + agent, "agent " + agent);
        }
        if (!model.groupInstances().isEmpty()) {
            AolGroupInstance group = model.groupInstances().get(0);
            String groupKey = "group:" + group.instanceId();
            keys.add(groupKey);
            labels.put(groupKey, "group " + group.typeName() + " " + group.instanceId());
            for (AolPlay play : group.plays()) {
                String playKey = "play:" + play.instanceId();
                keys.add(playKey);
                labels.put(playKey, play.roleType() + " " + play.instanceId() + " (by " + play.agentId() + ")");
            }
        }
    }

    private static Map<String, String> attributesOf(AolModel model, String key) {
        if (key.startsWith("agent:")) {
            return model.agentAttributeValues().get(key.substring("agent:".length()));
        }
        if (!model.groupInstances().isEmpty()) {
            AolGroupInstance group = model.groupInstances().get(0);
            if (key.equals("group:" + group.instanceId())) return group.attributeValues();
            for (AolPlay play : group.plays()) {
                if (key.equals("play:" + play.instanceId())) return play.attributeValues();
            }
        }
        return null;
    }

    private static String formatAttributes(Map<String, String> attrs) {
        if (attrs == null) return "";
        if (attrs.isEmpty()) return "—";
        StringBuilder out = new StringBuilder();
        attrs.forEach((k, v) -> {
            if (out.length() > 0) out.append("; ");
            out.append(k).append('=').append(v);
        });
        return out.toString();
    }

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
}
