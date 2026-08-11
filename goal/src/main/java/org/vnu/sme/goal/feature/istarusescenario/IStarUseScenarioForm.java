package org.vnu.sme.goal.feature.istarusescenario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
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
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.verify.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.verify.conformance.semantics.QualityStatus;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.Checkpoint;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.InstanceKey;
import org.vnu.sme.goal.trace.usetrace.IStarUseTraceCompiler.Result;
import org.vnu.sme.goal.trace.usetrace.view.IStarUseTraceView;

/**
 * Loader for the .istar + .use + .soil trio (see
 * doc/istarDesign/istar-scenario-action-architecture.md): compiles the OCL-guarded
 * goal model against a real .use structure, replays the .soil script one statement at
 * a time via {@link IStarUseTraceCompiler}, and lets the user step through the
 * resulting checkpoints. This is the .iscn-free counterpart of
 * {@code org.vnu.sme.goal.feature.openistarscenario.IStarScenarioForm} — same role (pick files, run, show a marking report), but
 * the marking here comes from evaluating real OCL against a real USE system state
 * instead of hand-written fire/assign/aggregate statements.
 */
@SuppressWarnings("serial")
public final class IStarUseScenarioForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(IStarUseScenarioForm.class);
    private static final String PREF_ISTAR = "istarusescenario.istarFile";
    private static final String PREF_USE = "istarusescenario.useFile";
    private static final String PREF_SOIL = "istarusescenario.soilFile";
    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    @SuppressWarnings("unused")
    private final Session session;
    private final MainWindow mainWindow;

    private JTextField istarField;
    private JTextField useField;
    private JTextField soilField;
    private JTextArea resultArea;
    private JLabel statusLabel;
    private JLabel checkpointLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JButton diagramButton;

    private Result result;
    private Path istarPathUsed;
    private int currentCheckpoint = -1;

    public IStarUseScenarioForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "Open i* + USE Scenario", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        istarField.setText(PREFS.get(PREF_ISTAR, ""));
        useField.setText(PREFS.get(PREF_USE, ""));
        soilField.setText(PREFS.get(PREF_SOIL, ""));
        setSize(820, 560);
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        root.add(buildFileRows(), BorderLayout.NORTH);
        root.add(buildResultArea(), BorderLayout.CENTER);
        root.add(buildBottomRow(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildFileRows() {
        JPanel p = new JPanel(new GridLayout(4, 1, 2, 2));
        istarField = new JTextField();
        useField = new JTextField();
        soilField = new JTextField();

        p.add(fileRow("Goal model (.istar):", istarField, "i* model files (*.istar)", "istar"));
        p.add(fileRow("Structure (.use):", useField, "USE model files (*.use)", "use"));
        p.add(fileRow("Execution (.soil):", soilField, "Soil script files (*.soil)", "soil"));

        JPanel runRow = new JPanel(new BorderLayout());
        JButton run = new JButton("Run");
        run.addActionListener(e -> run());
        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.add(run);
        buttons.add(close);
        runRow.add(buttons, BorderLayout.EAST);
        p.add(runRow);
        return p;
    }

    private JPanel fileRow(String label, JTextField field, String filterLabel, String ext) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> chooseFile(field, filterLabel, ext));
        row.add(browse, BorderLayout.EAST);
        return row;
    }

    private void chooseFile(JTextField field, String filterLabel, String ext) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(filterLabel, ext));
        String cur = field.getText().trim();
        if (!cur.isEmpty()) fc.setSelectedFile(new File(cur));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            field.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private JScrollPane buildResultArea() {
        resultArea = new JTextArea(20, 70);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setEditable(false);
        resultArea.setBackground(UIManager.getColor("TextArea.background"));
        JScrollPane sp = new JScrollPane(resultArea);
        sp.setBorder(new EtchedBorder());
        return sp;
    }

    private JPanel buildBottomRow() {
        JPanel p = new JPanel(new BorderLayout());

        JPanel nav = new JPanel();
        prevButton = new JButton("<< Prev checkpoint");
        nextButton = new JButton("Next checkpoint >>");
        diagramButton = new JButton("Show Diagram");
        prevButton.addActionListener(e -> showCheckpoint(currentCheckpoint - 1));
        nextButton.addActionListener(e -> showCheckpoint(currentCheckpoint + 1));
        diagramButton.addActionListener(e -> showDiagram());
        checkpointLabel = new JLabel(" ");
        nav.add(prevButton);
        nav.add(checkpointLabel);
        nav.add(nextButton);
        nav.add(diagramButton);
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        diagramButton.setEnabled(false);

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(new EmptyBorder(2, 2, 0, 2));

        p.add(nav, BorderLayout.NORTH);
        p.add(statusLabel, BorderLayout.SOUTH);
        return p;
    }

    private void run() {
        String istarPath = istarField.getText().trim();
        String usePath = useField.getText().trim();
        String soilPath = soilField.getText().trim();
        resultArea.setText("");
        result = null;
        currentCheckpoint = -1;
        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
        diagramButton.setEnabled(false);
        checkpointLabel.setText(" ");

        if (istarPath.isEmpty() || usePath.isEmpty() || soilPath.isEmpty()) {
            status("Select all three files (.istar, .use, .soil).", C_ERR);
            return;
        }
        try {
            istarPathUsed = Path.of(istarPath);
            result = IStarUseTraceCompiler.compile(istarPathUsed, Path.of(usePath), Path.of(soilPath));
            if (!result.ok()) {
                resultArea.setText(String.join("\n", result.errors()));
                status("Compile/run failed.", C_ERR);
                return;
            }
            PREFS.put(PREF_ISTAR, istarPath);
            PREFS.put(PREF_USE, usePath);
            PREFS.put(PREF_SOIL, soilPath);

            int n = result.checkpoints().size();
            status("Compiled OK — " + n + " checkpoint(s). Use Prev/Next to step through the trace.", C_OK);
            if (n > 0) {
                prevButton.setEnabled(true);
                nextButton.setEnabled(true);
                diagramButton.setEnabled(true);
                showCheckpoint(n - 1);
            }
        } catch (IOException ex) {
            status("IO error: " + ex.getMessage(), C_ERR);
        }
    }

    private void showDiagram() {
        if (result == null || currentCheckpoint < 0) return;
        IStarUseTraceView.openCheckpoint(mainWindow, result.model(), istarPathUsed,
                result.checkpoints().get(currentCheckpoint));
    }

    private void showCheckpoint(int index) {
        if (result == null || result.checkpoints().isEmpty()) return;
        int n = result.checkpoints().size();
        if (index < 0 || index >= n) return;
        currentCheckpoint = index;

        Checkpoint cp = result.checkpoints().get(index);
        checkpointLabel.setText("Checkpoint " + (index + 1) + " / " + n);

        StringBuilder sb = new StringBuilder();
        sb.append("soil line: ").append(cp.soilLine()).append("\n\n");
        for (var e : cp.markings().entrySet()) {
            InstanceKey key = e.getKey();
            IStarMarking marking = e.getValue();
            boolean any = marking.goalTaskStatuses().values().stream().anyMatch(s -> s != GoalTaskStatus.UNKNOWN)
                    || marking.qualityStatuses().values().stream().anyMatch(s -> s != QualityStatus.UNKNOWN);
            if (!any) continue;
            sb.append("-- ").append(key).append(" --\n");
            marking.goalTaskStatuses().forEach((id, s) -> {
                if (s != GoalTaskStatus.UNKNOWN) sb.append("  ").append(id).append(" = ").append(s).append("\n");
            });
            marking.qualityStatuses().forEach((id, s) -> {
                if (s != QualityStatus.UNKNOWN) sb.append("  ").append(id).append(" = ").append(s).append("\n");
            });
            sb.append("\n");
        }
        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0);
    }

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }
}
