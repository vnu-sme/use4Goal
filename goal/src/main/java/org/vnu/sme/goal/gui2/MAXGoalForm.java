package org.vnu.sme.goal.gui2;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.maxbpmn.mm.BpmnProcess;
import org.vnu.sme.goal.maxbpmn.writer.MAXBpmnWriter;
import org.vnu.sme.goal.maxgoal.mm.MAXGoalModel;
import org.vnu.sme.goal.maxgoal.parser.MAXGoalCompiler;
import org.vnu.sme.goal.transform.GoalToBpmnTransformer;
import org.vnu.sme.goal.view2.MAXBpmnView;
import org.vnu.sme.goal.view2.MAXGoalView;

public final class MAXGoalForm extends JDialog {

    // ── dark palette ─────────────────────────────────────────────────────────
    private static final Color C_BG       = new Color(30, 30, 30);
    private static final Color C_PANEL    = new Color(37, 37, 38);
    private static final Color C_BORDER   = new Color(60, 60, 60);
    private static final Color C_FG       = new Color(212, 212, 212);
    private static final Color C_ACCENT   = new Color(0, 122, 204);
    private static final Color C_SUCCESS  = new Color(78, 201, 176);
    private static final Color C_ERROR    = new Color(244, 71, 71);
    private static final Color C_FIELD_BG = new Color(50, 50, 50);
    private static final Color C_BTN_BG   = new Color(0, 122, 204);
    private static final Color C_BTN2_BG  = new Color(55, 55, 55);

    private static final Font FONT_MONO  = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    private static final Font FONT_LABEL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    // ── state ────────────────────────────────────────────────────────────────
    private final Session    session;
    private final MainWindow mainWindow;
    private MAXGoalModel     currentModel;
    private BpmnProcess      currentProcess;

    // ── widgets ───────────────────────────────────────────────────────────────
    private JTextField  pathField;
    private JTextArea   logArea;
    private JLabel      statusLabel;
    private MAXGoalView goalView;
    private MAXBpmnView bpmnView;

    public MAXGoalForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "MAXGoal Compiler", false);
        this.session    = session;
        this.mainWindow = mainWindow;
        buildUI();
        setSize(1300, 820);
        setLocationRelativeTo(mainWindow);
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildUI() {
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCentrePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel p = dark(new JPanel(new BorderLayout(8, 0)));
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        JLabel title = new JLabel("MAXGoal — Multi-Actor Goal Compiler");
        title.setFont(FONT_TITLE); title.setForeground(C_FG);
        p.add(title, BorderLayout.WEST);

        JPanel row = dark(new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0)));
        pathField = new JTextField(30);
        styleField(pathField);
        JButton browse = btn("Browse…", C_BTN2_BG, e -> chooseFile());
        JButton compile = btn("Compile", C_BTN_BG, e -> doCompile());
        JButton toHtml  = btn("→ BPMN", C_BTN_BG, e -> doTransform());
        JButton save    = btn("Save .maxbpmn", C_BTN2_BG, e -> doSave());

        row.add(new JLabel("File:") {{ setForeground(C_FG); setFont(FONT_LABEL); }});
        row.add(pathField); row.add(browse); row.add(compile);
        row.add(Box.createHorizontalStrut(12));
        row.add(toHtml); row.add(save);
        p.add(row, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCentrePanel() {
        JPanel p = dark(new JPanel(new GridLayout(1, 2, 6, 0)));
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        goalView = new MAXGoalView();
        bpmnView = new MAXBpmnView();

        JScrollPane goalScroll = scroll(goalView, "Goal Model (MAXGoal)");
        JScrollPane bpmnScroll = scroll(bpmnView, "BPMN Diagram (MAXBpmn)");

        p.add(goalScroll);
        p.add(bpmnScroll);
        return p;
    }

    private JScrollPane scroll(JPanel inner, String title) {
        JScrollPane sp = new JScrollPane(inner);
        sp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(C_BORDER),
                title, 0, 0, FONT_LABEL, C_FG));
        sp.getViewport().setBackground(inner.getBackground());
        return sp;
    }

    private JPanel buildBottomPanel() {
        JPanel p = dark(new JPanel(new BorderLayout(0, 4)));
        p.setBorder(BorderFactory.createEmptyBorder(4, 12, 10, 12));

        logArea = new JTextArea(5, 80);
        logArea.setFont(FONT_MONO); logArea.setBackground(C_FIELD_BG);
        logArea.setForeground(C_FG); logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JScrollPane ls = new JScrollPane(logArea);
        ls.setBorder(BorderFactory.createLineBorder(C_BORDER));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT_LABEL); statusLabel.setForeground(C_FG);

        p.add(ls,          BorderLayout.CENTER);
        p.add(statusLabel, BorderLayout.SOUTH);
        return p;
    }

    // ── actions ───────────────────────────────────────────────────────────────

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("MAXGoal files (*.maxgoal)", "maxgoal"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void doCompile() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) { status("No file selected.", C_ERROR); return; }
        try {
            MAXGoalCompiler.Result r = MAXGoalCompiler.compile(Path.of(path));
            if (!r.ok()) {
                log("Compile errors:\n" + String.join("\n", r.errors()));
                status("Compile FAILED — " + r.errors().size() + " error(s).", C_ERROR);
                return;
            }
            currentModel = r.model();
            goalView.setModel(currentModel);
            currentProcess = null; bpmnView.setProcess(null);
            log("Compiled OK: " + currentModel.getActors().size() + " actors, "
                + currentModel.allIntentionals().size() + " intentionals.");
            status("Compiled successfully.", C_SUCCESS);
        } catch (IOException ex) {
            log("IO error: " + ex.getMessage());
            status("IO error.", C_ERROR);
        }
    }

    private void doTransform() {
        if (currentModel == null) { status("Compile a .maxgoal file first.", C_ERROR); return; }
        currentProcess = GoalToBpmnTransformer.transform(currentModel);
        bpmnView.setProcess(currentProcess);
        log("Transformed to BPMN: " + currentProcess.getLanes().size() + " lanes, "
            + currentProcess.getNodes().size() + " nodes, "
            + currentProcess.getFlows().size() + " flows, "
            + currentProcess.getMessages().size() + " messages.");
        status("Transformation complete.", C_SUCCESS);
    }

    private void doSave() {
        if (currentProcess == null) { status("Run → BPMN first.", C_ERROR); return; }
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("MAXBpmn files (*.maxbpmn)", "maxbpmn"));
        fc.setSelectedFile(new File(currentProcess.getName() + ".maxbpmn"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File target = fc.getSelectedFile();
        if (!target.getName().endsWith(".maxbpmn"))
            target = new File(target.getAbsolutePath() + ".maxbpmn");
        try {
            MAXBpmnWriter.saveTo(currentProcess, target);
            log("Saved: " + target.getAbsolutePath());
            status("Saved to " + target.getName(), C_SUCCESS);
        } catch (IOException ex) {
            log("Save error: " + ex.getMessage());
            status("Save failed.", C_ERROR);
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void status(String msg, Color c) {
        statusLabel.setText(msg);
        statusLabel.setForeground(c);
    }

    private static JPanel dark(JPanel p) {
        p.setBackground(C_BG); return p;
    }

    private static void styleField(JTextField f) {
        f.setBackground(C_FIELD_BG); f.setForeground(C_FG);
        f.setCaretColor(C_FG);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70,70,70)),
                BorderFactory.createEmptyBorder(2,6,2,6)));
        f.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    private static JButton btn(String label, Color bg, ActionListener al) {
        JButton b = new JButton(label);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        return b;
    }
}
