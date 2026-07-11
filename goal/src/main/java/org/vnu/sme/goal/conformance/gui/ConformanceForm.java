package org.vnu.sme.goal.conformance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.mm.FlowElement;
import org.vnu.sme.goal.bpmn2.parser.Bpmn2Compiler;
import org.vnu.sme.goal.bpmn2.view.Bpmn2View;
import org.vnu.sme.goal.conformance.mapping.ConformanceMapping;
import org.vnu.sme.goal.conformance.mapping.ConformanceMappingParser;
import org.vnu.sme.goal.conformance.semantics.Bpmn2LtsBuilder;
import org.vnu.sme.goal.conformance.semantics.ComplianceChecker;
import org.vnu.sme.goal.conformance.semantics.ComplianceResult;
import org.vnu.sme.goal.conformance.semantics.IllFormedProcessException;
import org.vnu.sme.goal.conformance.semantics.ProductLts;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.Quality;
import org.vnu.sme.goal.istar.mm.IntentionalElement;
import org.vnu.sme.goal.istar.parser.IStarCompiler;
import org.vnu.sme.goal.istar.view.IStarView;

/**
 * Plugin form for the i* / BPMN2 conformance checker designed in
 * doc/paper/conformance-istar-bpmn2.md — takes an .istar file, a .bpmn2 file and a .map
 * mapping file, shows both diagrams side by side (reusing the existing {@link IStarView} /
 * {@link Bpmn2View}), and runs {@link ComplianceChecker} to report weak/strong compliance
 * with a counterexample trace on failure.
 */
public final class ConformanceForm extends JDialog {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ConformanceForm.class);
    private static final String PREF_ISTAR = "conformance.istarFile";
    private static final String PREF_BPMN2 = "conformance.bpmn2File";
    private static final String PREF_MAP = "conformance.mapFile";

    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_WARN = new Color(160, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    private final Session session;
    private final MainWindow mainWindow;

    private JTextField istarField;
    private JTextField bpmn2Field;
    private JTextField mapField;
    private JTextArea resultArea;
    private JLabel statusLabel;
    private IStarView istarView;
    private Bpmn2View bpmn2View;

    public ConformanceForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "i* / BPMN2 Conformance Checker", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        istarField.setText(PREFS.get(PREF_ISTAR, ""));
        bpmn2Field.setText(PREFS.get(PREF_BPMN2, ""));
        mapField.setText(PREFS.get(PREF_MAP, ""));
        setSize(1400, 900);
        setLocationRelativeTo(mainWindow);
    }

    // ── UI ────────────────────────────────────────────────────────────────────

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

        istarField = new JTextField(34);
        bpmn2Field = new JTextField(34);
        mapField = new JTextField(34);

        p.add(fileRow("i*  (.istar):", istarField, "iStar 2.0 (*.istar)", "istar"));
        p.add(fileRow("BPMN2 (.bpmn2):", bpmn2Field, "BPMN 2.0 (*.bpmn2)", "bpmn2"));
        p.add(fileRow("Mapping (.map):", mapField, "Conformance mapping (*.map)", "map"));

        JPanel runRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 6));
        JButton run = new JButton("Check Conformance");
        run.setFont(run.getFont().deriveFont(Font.BOLD));
        run.addActionListener(e -> doCheck());
        runRow.add(run);
        p.add(runRow);

        return p;
    }

    private JPanel fileRow(String label, JTextField field, String filterDesc, String ext) {
        JPanel row = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 2));
        row.add(new JLabel(label));
        row.add(field);
        JButton browse = new JButton("Browse…");
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
        left.setBorder(new TitledBorder(new EtchedBorder(), "i* Strategic Rationale Diagram"));
        JScrollPane right = new JScrollPane(bpmn2View);
        right.setBorder(new TitledBorder(new EtchedBorder(), "BPMN 2.0 Collaboration Diagram"));

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

    // ── Actions ───────────────────────────────────────────────────────────────

    private void doCheck() {
        String istarPath = istarField.getText().trim();
        String bpmnPath = bpmn2Field.getText().trim();
        String mapPath = mapField.getText().trim();
        resultArea.setText("");

        if (istarPath.isEmpty() || bpmnPath.isEmpty() || mapPath.isEmpty()) {
            status("Please select all 3 files (.istar, .bpmn2, .map).", C_ERR);
            return;
        }

        try {
            IStarCompiler.Result ir = IStarCompiler.compile(Path.of(istarPath));
            if (!ir.ok()) {
                appendResult("i* parse errors:\n" + String.join("\n", ir.errors()));
                status("i* parse FAILED.", C_ERR);
                return;
            }
            GoalModel gm = ir.model();
            istarView.setModel(gm);

            Bpmn2Compiler.Result br = Bpmn2Compiler.compile(Path.of(bpmnPath));
            if (!br.ok()) {
                appendResult("BPMN2 parse errors:\n" + String.join("\n", br.errors()));
                status("BPMN2 parse FAILED.", C_ERR);
                return;
            }
            Bpmn2Model pm = br.model();
            bpmn2View.setModel(pm);

            ConformanceMapping map = ConformanceMappingParser.parse(Path.of(mapPath));
            List<String> warnings = map.validate(gm, pm);
            for (String w : warnings) appendResult("[warn] " + w);

            Bpmn2LtsBuilder.validateWellFormed(pm);

            ProductLts lts = new ProductLts(gm, pm, map);
            Set<String> qualityIds = new LinkedHashSet<>();
            for (IntentionalElement e : gm.allElements().values()) {
                if (e instanceof Quality q) qualityIds.add(q.id());
            }

            ComplianceResult result = ComplianceChecker.check(lts, qualityIds);

            appendResult("");
            appendResult("Verdict : " + result.verdict());
            appendResult("Weak    : " + result.weak());
            appendResult("Stable  : " + result.stable());
            appendResult("Message : " + result.message());
            if (!result.counterexampleTrace().isEmpty()) {
                appendResult("Counterexample trace:");
                for (FlowElement n : result.counterexampleTrace()) {
                    appendResult("  -> " + n.id());
                }
            }

            PREFS.put(PREF_ISTAR, istarPath);
            PREFS.put(PREF_BPMN2, bpmnPath);
            PREFS.put(PREF_MAP, mapPath);

            status(result.verdict().toString(), switch (result.verdict()) {
                case NON_COMPLIANT -> C_ERR;
                case WEAK_COMPLIANT -> C_WARN;
                case STRONG_COMPLIANT -> C_OK;
            });
        } catch (IllFormedProcessException ex) {
            appendResult("Process model is not well-formed: " + ex.getMessage());
            status("BPMN2 not well-formed.", C_ERR);
        } catch (IllegalArgumentException ex) {
            appendResult("Mapping file error: " + ex.getMessage());
            status("Mapping parse FAILED.", C_ERR);
        } catch (IOException ex) {
            appendResult("IO error: " + ex.getMessage());
            status("IO error reading a file.", C_ERR);
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
