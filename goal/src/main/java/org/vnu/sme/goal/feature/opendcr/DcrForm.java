package org.vnu.sme.goal.feature.opendcr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.dsl.dcr.mm.DcrModel;
import org.vnu.sme.goal.dsl.dcr.parser.DCRCompiler;
import org.vnu.sme.goal.dsl.dcr.view.DcrView;

@SuppressWarnings("serial")
public final class DcrForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(DcrForm.class);
    private static final String PREF_KEY = "dcr.lastFile";
    private static final Color C_OK = new Color(0, 120, 0);
    private static final Color C_ERR = new Color(160, 0, 0);

    @SuppressWarnings("unused")
    private final Session session;
    private final MainWindow mainWindow;
    private JTextField pathField;
    private JLabel statusLabel;
    private JButton openMenuButton;

    public DcrForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "Open DCR", false);
        this.session = session;
        this.mainWindow = mainWindow;
        buildUI();
        String last = PREFS.get(PREF_KEY, "");
        if (!last.isEmpty()) {
            pathField.setText(last);
        }
        pack();
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(6, 6));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        root.add(buildFileRow(), BorderLayout.CENTER);
        root.add(buildStatusRow(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildFileRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pathField = new JTextField(42);
        pathField.setToolTipText("Path to .dcr file");
        pathField.addActionListener(e -> open(OpenTarget.USE_DESKTOP));

        JButton browse = new JButton("Browse");
        browse.addActionListener(e -> chooseFile());

        JButton open = new JButton("Open");
        open.addActionListener(e -> open(OpenTarget.USE_DESKTOP));

        openMenuButton = new JButton("v");
        openMenuButton.setToolTipText("Open options");
        openMenuButton.addActionListener(e -> showOpenMenu());

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());

        p.add(new JLabel("File:"));
        p.add(pathField);
        p.add(browse);
        p.add(open);
        p.add(openMenuButton);
        p.add(close);
        return p;
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
        fc.setFileFilter(new FileNameExtensionFilter("DCR files (*.dcr)", "dcr"));
        String cur = pathField.getText().trim();
        if (!cur.isEmpty()) {
            fc.setSelectedFile(new File(cur));
        }
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
            open(OpenTarget.USE_DESKTOP);
        }
    }

    private void open(OpenTarget target) {
        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            status("No file selected.", C_ERR);
            return;
        }

        try {
            DCRCompiler.Result result = DCRCompiler.compile(Path.of(path));
            if (!result.ok()) {
                showErrors(result.errors());
                status("Load failed.", C_ERR);
                return;
            }

            DcrModel model = result.model();
            Path source = Path.of(path);
            if (target == OpenTarget.POPUP_WINDOW) {
                DcrView.openPopupWindow(mainWindow, model, source);
            } else {
                DcrView.openUseDesktop(mainWindow, model, source);
            }
            PREFS.put(PREF_KEY, path);
            status("Opened " + source.getFileName(), C_OK);
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
            status("I/O error.", C_ERR);
        }
    }

    private void showOpenMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem desktop = new JMenuItem("Open in USE");
        desktop.addActionListener(e -> open(OpenTarget.USE_DESKTOP));
        JMenuItem popup = new JMenuItem("Open popup");
        popup.addActionListener(e -> open(OpenTarget.POPUP_WINDOW));
        menu.add(desktop);
        menu.add(popup);
        menu.show(openMenuButton, 0, openMenuButton.getHeight());
    }

    private void showErrors(java.util.List<String> errors) {
        String message = String.join("\n", errors);
        JOptionPane.showMessageDialog(this, message, "DCR Parse Errors", JOptionPane.ERROR_MESSAGE);
    }

    private void status(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private enum OpenTarget {
        USE_DESKTOP,
        POPUP_WINDOW
    }
}
