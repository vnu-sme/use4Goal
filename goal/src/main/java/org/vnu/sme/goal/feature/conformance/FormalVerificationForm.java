package org.vnu.sme.goal.feature.conformance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

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
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportRequest;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportResult;
import org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportService;

/** One workspace for source selection, translation, inspection and proof hand-off. */
public final class FormalVerificationForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(FormalVerificationForm.class);
    private static final Color OK = new Color(0, 125, 30), ERR = new Color(170, 20, 20), RUN = new Color(145, 90, 0);
    private final JTextField acl = new JTextField(48), istar = new JTextField(48), bpmn = new JTextField(48);
    private final JTextField output = new JTextField(48), project = new JTextField("MeetingSchedulerEventB", 28);
    private final JLabel status = new JLabel("Select the three source models.");
    private final JTextArea overview = area();
    private final JTabbedPane artifacts = new JTabbedPane();
    private final Map<String, JTextArea> viewers = new LinkedHashMap<>();
    private JButton generate;

    public FormalVerificationForm(MainWindow owner) {
        super(owner, "Formal Verification Workspace", false);
        build(); restore(); setSize(1180, 790); setLocationRelativeTo(owner);
    }

    private void build() {
        setLayout(new BorderLayout(5, 5));
        JPanel inputs = new JPanel(new GridBagLayout()); inputs.setBorder(new EmptyBorder(7, 8, 3, 8));
        row(inputs, 0, "1. ACL structure", acl, "ACL model (*.acl)", "acl", false);
        row(inputs, 1, "2. iStar goals", istar, "iStar model (*.istar)", "istar", false);
        row(inputs, 2, "3. BPMN behavior", bpmn, "BPMN model (*.bpmn2)", "bpmn2", false);
        row(inputs, 3, "Output workspace", output, "Directory", null, true);
        GridBagConstraints c = constraints(4, 0); inputs.add(new JLabel("Event-B project"), c);
        c = constraints(4, 1); c.fill=GridBagConstraints.HORIZONTAL; c.weightx=1; inputs.add(project, c);
        JPanel north = new JPanel(new BorderLayout()); north.add(inputs, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        generate = new JButton("Generate and inspect Event-B"); generate.setFont(generate.getFont().deriveFont(Font.BOLD));
        generate.addActionListener(e -> generate()); buttons.add(generate);
        buttons.add(new JLabel("Rodin proof status is reported separately from translation status."));
        north.add(buttons, BorderLayout.SOUTH); add(north, BorderLayout.NORTH);

        overview.setText("WORKFLOW\n\n1. Parse and type-check ACL, iStar and BPMN.\n"
                + "2. Check cross-model references and translate them to Event-B.\n"
                + "3. Inspect generated artifacts here in USE.\n"
                + "4. Import the generated project into Rodin to generate and discharge proof obligations.\n\n"
                + "TEMPORAL PROPERTIES\n\nTOCL is the in-USE, trace/filmstrip-level checker. "
                + "The generated ProB LTL file is the state-space property input. These are complementary checks.");
        artifacts.addTab("Overview", new JScrollPane(overview));
        add(artifacts, BorderLayout.CENTER);
        status.setBorder(new EmptyBorder(3, 9, 7, 9)); add(status, BorderLayout.SOUTH);
    }

    private void row(JPanel p, int y, String label, JTextField field, String description, String extension, boolean directory) {
        GridBagConstraints c=constraints(y,0); p.add(new JLabel(label),c);
        c=constraints(y,1); c.weightx=1; c.fill=GridBagConstraints.HORIZONTAL; p.add(field,c);
        c=constraints(y,2); JButton browse=new JButton("Browse..."); browse.addActionListener(e -> choose(field,description,extension,directory)); p.add(browse,c);
    }
    private static GridBagConstraints constraints(int y,int x) { GridBagConstraints c=new GridBagConstraints(); c.gridy=y;c.gridx=x;c.anchor=GridBagConstraints.WEST;c.insets=new Insets(2,4,2,4);return c; }
    private void choose(JTextField field,String description,String extension,boolean directory) {
        JFileChooser fc=new JFileChooser(); fc.setDialogTitle("Select "+description);
        if(directory) fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); else fc.setFileFilter(new FileNameExtensionFilter(description,extension));
        if(!field.getText().isBlank()) fc.setSelectedFile(new File(field.getText().trim()));
        if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) field.setText(fc.getSelectedFile().getAbsolutePath());
    }

    private void generate() {
        EventBExportRequest request;
        try { request=new EventBExportRequest(Path.of(acl.getText().trim()),Path.of(istar.getText().trim()),Path.of(bpmn.getText().trim()),Path.of(output.getText().trim()),project.getText().trim()); }
        catch(RuntimeException ex){ setStatus("Invalid input: "+message(ex),ERR); return; }
        if(project.getText().isBlank()||output.getText().isBlank()){setStatus("Output directory and project name are required.",ERR);return;}
        generate.setEnabled(false); setStatus("Parsing, validating and translating...",RUN);
        new SwingWorker<EventBExportResult,Void>() {
            protected EventBExportResult doInBackground(){return new EventBExportService().export(request);}
            protected void done(){try{showResult(get());}catch(Exception ex){setStatus("Translation failed: "+message(ex),ERR);}finally{generate.setEnabled(true);}}
        }.execute();
    }

    private void showResult(EventBExportResult r) {
        if(!r.success()) { overview.setText("TRANSLATION FAILED\n\n"+String.join("\n",r.diagnostics())); artifacts.setSelectedIndex(0); setStatus("Event-B was not generated.",ERR); return; }
        clearArtifactTabs();
        StringBuilder summary=new StringBuilder("TRANSLATION: SUCCESS\nEVENT-B PROJECT: ").append(r.projectDirectory())
                .append("\nRODIN PROOF: NOT RUN\nTOCL TRACE CHECK: NOT RUN\n\nGenerated files:\n");
        r.generatedFiles().forEach(f -> summary.append("  ").append(f.getFileName()).append('\n'));
        if(!r.diagnostics().isEmpty()) summary.append("\nDiagnostics:\n  ").append(String.join("\n  ",r.diagnostics()));
        overview.setText(summary.toString());
        for(Path f:r.generatedFiles()) addArtifact(f);
        remember(); artifacts.setSelectedIndex(0);
        setStatus("Generated and loaded in USE. Import into Rodin for proof obligations.", r.diagnostics().isEmpty()?OK:RUN);
    }
    private void clearArtifactTabs(){while(artifacts.getTabCount()>1)artifacts.removeTabAt(1);viewers.clear();}
    private void addArtifact(Path file){try{JTextArea a=area();a.setText(Files.readString(file));a.setCaretPosition(0);viewers.put(file.getFileName().toString(),a);artifacts.addTab(file.getFileName().toString(),new JScrollPane(a));}catch(IOException ex){overview.append("\nCannot display "+file+": "+message(ex));}}
    private static JTextArea area(){JTextArea a=new JTextArea();a.setEditable(false);a.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));return a;}
    private void setStatus(String text,Color color){status.setText(text);status.setForeground(color);}
    private static String message(Throwable t){while(t.getCause()!=null)t=t.getCause();return t.getMessage()==null?t.getClass().getSimpleName():t.getMessage();}
    private void restore(){acl.setText(PREFS.get("acl",""));istar.setText(PREFS.get("istar",""));bpmn.setText(PREFS.get("bpmn",""));output.setText(PREFS.get("output",System.getProperty("user.dir")));project.setText(PREFS.get("project","MeetingSchedulerEventB"));}
    private void remember(){PREFS.put("acl",acl.getText());PREFS.put("istar",istar.getText());PREFS.put("bpmn",bpmn.getText());PREFS.put("output",output.getText());PREFS.put("project",project.getText());}
}
