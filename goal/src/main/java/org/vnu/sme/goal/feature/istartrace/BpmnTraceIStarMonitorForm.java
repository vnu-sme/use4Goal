package org.vnu.sme.goal.feature.istartrace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
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
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.istar.mm.Obstacle;
import org.vnu.sme.goal.dsl.istar.mm.Quality;
import org.vnu.sme.goal.dsl.istar.mm.Resource;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.dsl.istar.view.IStarSpecText;
import org.vnu.sme.goal.dsl.istar.view.IStarView;
import org.vnu.sme.goal.dsl.istar.view.NodeBadge;
import org.vnu.sme.goal.trace.istartrace.nativeacl.BpmnTraceIStarMonitor;

/**
 * Verifies an already-generated {@code .bpmntrace} against an iStar goal model, evaluating
 * activation/condition/pre/post directly over each ACL/AOL snapshot in the trace (no USE/SOIL
 * round-trip). Each step's goal/task marking is shown two ways -- Graphical (an {@link IStarView}
 * with status badges) and Specification ({@link IStarSpecText}, badge-annotated) -- kept in sync
 * by {@link #showFrame()} regardless of which is selected, with a separate Table View tab giving
 * the whole trace's marking history (columns = steps, rows = goal/task occurrences) at a glance.
 */
@SuppressWarnings("serial")
public final class BpmnTraceIStarMonitorForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(BpmnTraceIStarMonitorForm.class);
    private static final String PREF_ACL = "native.acl";
    private static final String PREF_ISTAR = "native.istar";
    private static final String PREF_TRACE = "native.trace";

    private static final Color GREEN = new Color(30, 150, 60);
    private static final Color AMBER = new Color(180, 130, 0);
    private static final Color RED = new Color(170, 30, 30);
    private static final Color GRAY = new Color(150, 150, 150);
    private static final Color C_ERR = new Color(180, 0, 0);

    @SuppressWarnings("unused")
    private final Session session;

    private JTextField aclField;
    private JTextField istarField;
    private JTextField traceField;
    private JComboBox<String> processChooser;
    private IStarView diagram;
    private JTextPane specPane;
    private JTable table;
    private JLabel statusLabel;
    private JLabel stepLabel;
    private JButton prevButton;
    private JButton nextButton;

    private BpmnTraceIStarMonitor.Result result;
    private int processIndex;
    private int frameIndex;

    public BpmnTraceIStarMonitorForm(Session session, MainWindow owner) {
        super(owner, "iStar Goal Marking Viewer", false);
        this.session = session;
        buildUI();
        aclField.setText(PREFS.get(PREF_ACL, ""));
        istarField.setText(PREFS.get(PREF_ISTAR, ""));
        traceField.setText(PREFS.get(PREF_TRACE, ""));
        setSize(1200, 820);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.add(buildInputsPanel(), BorderLayout.NORTH);

        JTabbedPane outerTabs = new JTabbedPane();
        outerTabs.addTab("Step View", buildStepView());
        outerTabs.addTab("Table View", buildTableView());
        root.add(outerTabs, BorderLayout.CENTER);

        root.add(buildStatusRow(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ── Inputs ───────────────────────────────────────────────────────────────

    private JPanel buildInputsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(titled("Inputs"));

        aclField = new JTextField(36);
        istarField = new JTextField(36);
        traceField = new JTextField(36);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(3, 4, 3, 4);
        gc.gridy = 0;
        addInputRow(panel, gc, "ACL structure", aclField, "ACL files (*.acl)", "acl");
        gc.gridy = 1;
        addInputRow(panel, gc, "iStar goal model", istarField, "iStar files (*.istar)", "istar");
        gc.gridy = 2;
        addInputRow(panel, gc, "BPMN trace", traceField, "BPMN trace files (*.bpmntrace)", "bpmntrace");

        JPanel bottomRow = new JPanel(new BorderLayout(8, 0));
        JPanel instanceRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        instanceRow.add(new JLabel("Process instance:"));
        processChooser = new JComboBox<>();
        processChooser.addActionListener(e -> {
            if (result == null) return;
            processIndex = Math.max(0, processChooser.getSelectedIndex());
            frameIndex = 0;
            installTable();
            showFrame();
        });
        instanceRow.add(processChooser);

        JButton verify = primaryButton("▶  Verify Trace");
        verify.addActionListener(e -> load());
        JPanel verifyRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        verifyRow.add(verify);

        bottomRow.add(instanceRow, BorderLayout.WEST);
        bottomRow.add(verifyRow, BorderLayout.EAST);

        gc.gridy = 3;
        gc.gridx = 0;
        gc.gridwidth = 3;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.insets = new Insets(8, 4, 2, 4);
        panel.add(bottomRow, gc);

        return panel;
    }

    private void addInputRow(JPanel panel, GridBagConstraints gc, String label, JTextField field,
                             String filterLabel, String extension) {
        JLabel l = new JLabel(label);
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

    // ── Step View: Graphical / Specification tabs + Prev/Next ──────────────────

    private JPanel buildStepView() {
        JPanel content = new JPanel(new BorderLayout(0, 4));

        JTabbedPane markingTabs = new JTabbedPane();
        diagram = new IStarView();
        markingTabs.addTab("Graphical", diagram);
        markingTabs.addTab("Specification", buildSpecPane());
        content.add(markingTabs, BorderLayout.CENTER);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        prevButton = new JButton("‹ Prev");
        prevButton.addActionListener(e -> { frameIndex--; showFrame(); });
        nextButton = new JButton("Next ›");
        nextButton.addActionListener(e -> { frameIndex++; showFrame(); });
        stepLabel = new JLabel("No trace loaded", SwingConstants.CENTER);
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

    // ── Table View ───────────────────────────────────────────────────────────

    private JPanel buildTableView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(titled("Marking History (columns = steps, rows = goal/task occurrences)"));
        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(23);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int c = table.columnAtPoint(e.getPoint());
                if (c > 0) { frameIndex = table.convertColumnIndexToModel(c) - 1; showFrame(); }
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void installTable() {
        if (result == null || result.processes().isEmpty()) return;
        BpmnTraceMarkingTableModel model = new BpmnTraceMarkingTableModel(result.processes().get(processIndex));
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(450);
        StatusCellRenderer renderer = new StatusCellRenderer();
        for (int c = 1; c < table.getColumnCount(); c++) {
            table.getColumnModel().getColumn(c).setPreferredWidth(75);
            table.getColumnModel().getColumn(c).setCellRenderer(renderer);
        }
    }

    // ── Status bar ───────────────────────────────────────────────────────────

    private JPanel buildStatusRow() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 225, 225)),
                new EmptyBorder(6, 2, 0, 2)));
        statusLabel = new JLabel(" ");
        p.add(statusLabel, BorderLayout.CENTER);
        return p;
    }

    // ── Load / navigate ──────────────────────────────────────────────────────

    private void load() {
        String aclPath = aclField.getText().trim();
        String istarPath = istarField.getText().trim();
        String tracePath = traceField.getText().trim();
        if (aclPath.isEmpty() || istarPath.isEmpty() || tracePath.isEmpty()) {
            status("ACL, iStar, and BPMN trace paths are all required.", C_ERR);
            return;
        }
        result = BpmnTraceIStarMonitor.run(Path.of(aclPath), Path.of(istarPath), Path.of(tracePath));
        if (!result.ok()) {
            specPane.setText(String.join("\n", result.errors()));
            status("Verification failed.", C_ERR);
            return;
        }
        PREFS.put(PREF_ACL, aclPath);
        PREFS.put(PREF_ISTAR, istarPath);
        PREFS.put(PREF_TRACE, tracePath);

        processChooser.removeAllItems();
        for (var process : result.processes()) {
            processChooser.addItem(process.processId()
                    + (process.selfObjectName() == null ? "" : "#" + process.selfObjectName()));
        }
        processIndex = 0;
        frameIndex = 0;
        installTable();
        showFrame();
        status("Trace evaluated natively over ACL/AOL snapshots (no USE/SOIL round-trip).", GREEN);
    }

    private void showFrame() {
        if (result == null || result.processes().isEmpty()) return;
        var frames = result.processes().get(processIndex).frames();
        frameIndex = Math.max(0, Math.min(frameIndex, frames.size() - 1));
        var frame = frames.get(frameIndex);

        Map<String, NodeBadge> badges = badgesOf(frame.instanceModel().allElements(), frame.marking());
        diagram.setModel(frame.instanceModel());
        diagram.setActorLabelOverrides(frame.actorLabels());
        diagram.setNodeLabelOverrides(frame.nodeLabels());
        diagram.setNodeBadges(badges);

        specPane.setText(specText(frame, badges));
        specPane.setCaretPosition(0);

        stepLabel.setText("Step " + frameIndex + " / " + (frames.size() - 1) + " — "
                + (frame.activityId() == null ? "INITIAL" : frame.activityId()));
        prevButton.setEnabled(frameIndex > 0);
        nextButton.setEnabled(frameIndex + 1 < frames.size());
        table.repaint();
    }

    private String specText(BpmnTraceIStarMonitor.Frame frame, Map<String, NodeBadge> badges) {
        StringBuilder out = new StringBuilder();
        out.append(IStarSpecText.generate(frame.instanceModel(), badges, frame.actorLabels(), frame.nodeLabels()));
        out.append("ACL delta this step\n");
        out.append("===================\n");
        if (frame.delta().isEmpty()) {
            out.append("  (none)\n");
        } else {
            for (String d : frame.delta()) out.append("  Δ ").append(d).append('\n');
        }
        return out.toString();
    }

    private static Map<String, NodeBadge> badgesOf(Map<String, IntentionalElement> elements,
                                                    org.vnu.sme.goal.verify.conformance.semantics.IStarMarking marking) {
        Map<String, NodeBadge> out = new LinkedHashMap<>();
        for (var e : elements.values()) {
            switch (e) {
                case Goal g -> out.put(g.id(), badge(marking.goalTaskStatus(g.id())));
                case Task t -> out.put(t.id(), badge(marking.goalTaskStatus(t.id())));
                case Quality q -> { }
                case Resource r -> { }
                case Obstacle o -> { }
            }
        }
        return out;
    }

    private static NodeBadge badge(GoalTaskStatus status) {
        return switch (status) {
            case UNKNOWN -> new NodeBadge(GRAY, "?", "Unknown");
            case PENDING -> new NodeBadge(AMBER, "P", "Pending");
            case FULFILLED -> new NodeBadge(GREEN, "F", "Fulfilled");
            case VIOLATED -> new NodeBadge(RED, "V", "Violated");
        };
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

    /** Colours each Table View cell by its {@link GoalTaskStatus}, with a black outline on the
     *  column that matches the step currently shown in Step View. */
    private final class StatusCellRenderer extends DefaultTableCellRenderer {
        StatusCellRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                        boolean focus, int row, int column) {
            super.getTableCellRendererComponent(t, value, selected, focus, row, column);
            GoalTaskStatus status = (GoalTaskStatus) value;
            setText(switch (status) {
                case UNKNOWN -> "U";
                case PENDING -> "P";
                case FULFILLED -> "F";
                case VIOLATED -> "V";
            });
            setForeground(Color.WHITE);
            setBackground(switch (status) {
                case UNKNOWN -> GRAY;
                case PENDING -> AMBER;
                case FULFILLED -> GREEN;
                case VIOLATED -> RED;
            });
            setBorder(table.convertColumnIndexToModel(column) == frameIndex + 1
                    ? BorderFactory.createLineBorder(Color.BLACK, 2)
                    : BorderFactory.createEmptyBorder(1, 1, 1, 1));
            return this;
        }
    }
}
