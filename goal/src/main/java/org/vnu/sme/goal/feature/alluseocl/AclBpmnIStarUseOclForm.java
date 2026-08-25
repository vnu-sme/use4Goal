package org.vnu.sme.goal.feature.alluseocl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.translate.aclistarbpmn2use.AclBpmnIStarUseOclService;

/** Small file-oriented UI for the combined USE/TOCL translation. */
public final class AclBpmnIStarUseOclForm extends JDialog {
    private static final Color OK = new Color(0, 125, 30);
    private static final Color ERROR = new Color(170, 20, 20);
    private static final Color RUNNING = new Color(140, 90, 0);

    private final JTextField acl = new JTextField(50);
    private final JTextField bpmn = new JTextField(50);
    private final JTextField istar = new JTextField(50);
    private final JTextField output = new JTextField(50);
    private final JTextArea summary = textArea();
    private final JTextArea usePreview = textArea();
    private final JTextArea toclPreview = textArea();
    private final JLabel status = new JLabel(" ");
    private final JButton generate = new JButton("Generate USE + TOCL");

    public AclBpmnIStarUseOclForm(MainWindow owner) {
        super(owner, "ACL + BPMN + iStar → USE/OCL", false);
        buildUi();
        setSize(1000, 700);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        setLayout(new BorderLayout(5, 5));
        JPanel inputs = new JPanel(new GridBagLayout());
        inputs.setBorder(new EmptyBorder(8, 8, 4, 8));
        addRow(inputs, 0, "1. ACL model (.acl):", acl, "acl", false);
        addRow(inputs, 1, "2. BPMN model (.bpmn2):", bpmn, "bpmn2", false);
        addRow(inputs, 2, "3. iStar model (.istar):", istar, "istar", false);
        addRow(inputs, 3, "Output folder:", output, null, true);
        GridBagConstraints buttonCell = cell(4, 1);
        buttonCell.anchor = GridBagConstraints.WEST;
        generate.addActionListener(event -> generate());
        inputs.add(generate, buttonCell);
        add(inputs, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Summary", new JScrollPane(summary));
        tabs.addTab("USE preview", new JScrollPane(usePreview));
        tabs.addTab("TOCL preview", new JScrollPane(toclPreview));
        add(tabs, BorderLayout.CENTER);
        status.setBorder(new EmptyBorder(3, 9, 5, 9));
        add(status, BorderLayout.SOUTH);
        summary.setText("Select matching ACL, BPMN and iStar files. The action emits one USE model "
                + "containing ACL structure, iStar predicates and BPMN operations/contracts, plus one "
                + "TOCL file containing goal and sequence-flow properties.");
    }

    private void addRow(JPanel panel, int row, String label, JTextField field,
                        String extension, boolean directory) {
        panel.add(new JLabel(label), cell(row, 0));
        GridBagConstraints inputCell = cell(row, 1);
        inputCell.fill = GridBagConstraints.HORIZONTAL;
        inputCell.weightx = 1;
        panel.add(field, inputCell);
        JButton browse = new JButton("Browse…");
        browse.addActionListener(event -> choose(field, extension, directory));
        panel.add(browse, cell(row, 2));
    }

    private void choose(JTextField field, String extension, boolean directory) {
        JFileChooser chooser = new JFileChooser();
        String current = field.getText().trim();
        if (!current.isEmpty()) {
            File file = new File(current);
            chooser.setCurrentDirectory(file.isDirectory() ? file : file.getParentFile());
        }
        if (directory) chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        else chooser.setFileFilter(new FileNameExtensionFilter(
                extension.toUpperCase() + " files (*." + extension + ")", extension));
        int answer = directory ? chooser.showSaveDialog(this) : chooser.showOpenDialog(this);
        if (answer == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void generate() {
        if (acl.getText().isBlank() || bpmn.getText().isBlank()
                || istar.getText().isBlank() || output.getText().isBlank()) {
            setStatus("Please select all three input files and an output folder.", ERROR);
            return;
        }
        generate.setEnabled(false);
        setStatus("Generating…", RUNNING);
        new SwingWorker<AclBpmnIStarUseOclService.Result, Void>() {
            @Override protected AclBpmnIStarUseOclService.Result doInBackground() {
                return AclBpmnIStarUseOclService.translate(
                        Path.of(acl.getText().trim()), Path.of(bpmn.getText().trim()),
                        Path.of(istar.getText().trim()), Path.of(output.getText().trim()));
            }

            @Override protected void done() {
                generate.setEnabled(true);
                try {
                    showResult(get());
                } catch (Exception exception) {
                    setStatus("Unexpected error: " + exception.getMessage(), ERROR);
                }
            }
        }.execute();
    }

    private void showResult(AclBpmnIStarUseOclService.Result result) {
        StringBuilder text = new StringBuilder();
        result.allDiagnostics().forEach(message -> text.append("• ").append(message).append('\n'));
        if (result.ok()) {
            text.append("\nUSE: ").append(result.written().useFile())
                    .append("\nTOCL: ").append(result.written().toclFile());
            setStatus("Generated into: " + result.outputFolder(), OK);
        } else {
            setStatus("Translation failed — see Summary.", ERROR);
        }
        summary.setText(text.toString());
        if (result.useResult() != null) {
            usePreview.setText(result.useResult().useText());
            toclPreview.setText(result.useResult().toclText());
        }
    }

    private void setStatus(String message, Color color) {
        status.setText(message);
        status.setForeground(color);
    }

    private static JTextArea textArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(false);
        return area;
    }

    private static GridBagConstraints cell(int row, int column) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(3, 4, 3, 4);
        return constraints;
    }
}
