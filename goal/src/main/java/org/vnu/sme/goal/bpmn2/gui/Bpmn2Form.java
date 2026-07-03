package org.vnu.sme.goal.bpmn2.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Collaboration;
import org.vnu.sme.goal.bpmn2.parser.Bpmn2Compiler;
import org.vnu.sme.goal.bpmn2.view.Bpmn2View;

public final class Bpmn2Form extends JDialog {

    private static final Preferences PREFS   = Preferences.userNodeForPackage(Bpmn2Form.class);
    private static final String      PREF_KEY = "bpmn2.lastFile";

    private static final Color C_OK  = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    private final Session    session;
    private final MainWindow mainWindow;

    private JTextField pathField;
    private JTextArea  logArea;
    private JLabel     statusLabel;
    private Bpmn2View  view;

    public Bpmn2Form(Session session, MainWindow mainWindow) {
        super(mainWindow, "BPMN 2.0 — Collaboration Diagram Viewer", false);
        this.session    = session;
        this.mainWindow = mainWindow;
        buildUI();
        // restore last path
        String last = PREFS.get(PREF_KEY, "");
        if (!last.isEmpty()) pathField.setText(last);
        setSize(1300, 820);
        setLocationRelativeTo(mainWindow);
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(4, 4));
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildCanvas(),  BorderLayout.CENTER);
        add(buildLog(),     BorderLayout.SOUTH);
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")),
                new EmptyBorder(0, 4, 0, 4)));

        pathField = new JTextField(36);
        pathField.setToolTipText("Path to .bpmn2 file");
        pathField.addActionListener(e -> doLoad());

        JButton browse = new JButton("Browse…");
        browse.addActionListener(e -> chooseFile());

        JButton load = new JButton("Load");
        load.setFont(load.getFont().deriveFont(Font.BOLD));
        load.addActionListener(e -> doLoad());

        p.add(new JLabel("File:"));
        p.add(pathField);
        p.add(browse);
        p.add(load);
        return p;
    }

    private JScrollPane buildCanvas() {
        view = new Bpmn2View();
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(new TitledBorder(
                new EtchedBorder(), "BPMN 2.0 Collaboration Diagram"));
        return sp;
    }

    private JPanel buildLog() {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBorder(new EmptyBorder(0, 6, 6, 6));

        logArea = new JTextArea(4, 80);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        logArea.setEditable(false);
        logArea.setBackground(UIManager.getColor("TextArea.background"));
        JScrollPane ls = new JScrollPane(logArea);
        ls.setBorder(new EtchedBorder());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 11f));

        p.add(ls,          BorderLayout.CENTER);
        p.add(statusLabel, BorderLayout.SOUTH);
        return p;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("BPMN 2.0 (*.bpmn2)", "bpmn2"));
        String cur = pathField.getText().trim();
        if (!cur.isEmpty()) fc.setSelectedFile(new File(cur));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
            doLoad();
        }
    }

    private void doLoad() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) { status("No file selected.", C_ERR); return; }
        try {
            Bpmn2Compiler.Result r = Bpmn2Compiler.compile(Path.of(path));
            if (!r.ok()) {
                log("Parse errors:\n" + String.join("\n", r.errors()));
                status("Load FAILED  (" + r.errors().size() + " error(s)).", C_ERR);
                return;
            }
            Bpmn2Collaboration m = r.model();
            view.setModel(m);
            PREFS.put(PREF_KEY, path);   // remember for next session
            log("Loaded: " + m.getPools().size() + " pools, "
                    + m.allNodes().size() + " flow nodes, "
                    + m.getMessageFlows().size() + " message flows.");
            status("Loaded: " + Path.of(path).getFileName(), C_OK);
        } catch (IOException ex) {
            log("IO error: " + ex.getMessage());
            status("IO error reading file.", C_ERR);
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void status(String msg, Color c) {
        statusLabel.setText(msg);
        statusLabel.setForeground(c);
    }
}
