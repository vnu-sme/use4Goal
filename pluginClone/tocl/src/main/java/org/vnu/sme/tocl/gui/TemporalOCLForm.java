package org.vnu.sme.tocl.gui;

import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.util.CloseOnEscapeKeyListener;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.main.Session;
import org.tzi.use.plugin.filmstrip.gui.FilePathLabel;
import org.tzi.use.plugin.filmstrip.logic.FilmstripMMVisitor;
import org.tzi.use.plugin.filmstrip.logic.FilmstripOptions;
import org.tzi.use.plugin.filmstrip.logic.FilmstripTransformer;
import org.tzi.use.plugin.filmstrip.logic.TransformationException;
import org.tzi.use.plugin.filmstrip.logic.TransformationInputException;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.vnu.sme.tocl.parser.TOCLTranslator;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class TemporalOCLForm extends JDialog {
    private Session session;
    private MainWindow mainWindow;
    private PrintWriter logWriter;
    private JTextField modelNameField;
    private JButton okButton;
    private JButton cancelButton;
    private JFileChooser filmstripFileChooser;
    private JFileChooser toclFileChooser;
    
    public TemporalOCLForm(Session session, MainWindow parent) {
        super(parent, "Validate TemporalOCL Constraints");
        this.session = session;
        this.mainWindow = parent;
        this.logWriter = parent.logWriter();
        this.session.addChangeListener(e -> this.close());
        this.addKeyListener(new CloseOnEscapeKeyListener(this)); // add close action when pressing ESC key
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        getRootPane().setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, getRootPane().getBackground()));
        
        // handle Enter and Escape keys
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if((e.getSource() instanceof JComponent)
                        && ((JComponent) e.getSource()).getRootPane() == getRootPane()
                        && e.getID() == KeyEvent.KEY_PRESSED){
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        okButton.doClick();
                        return true;
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        dispose();
                        return true;
                    }
                }
                return false;
            }
        });
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        int row = 0;
        
        mainPanel.add(new JLabel("Model name:"), getGBC(row, 0));
        
        modelNameField = new JTextField(session.system().model().name());
        modelNameField.setEditable(false);
        modelNameField.setFocusable(false);
        mainPanel.add(modelNameField, getGBC(row++, 1, 2, 1));
        
        // choosing the destination filmstrip file
        JLabel fileLabel = new FilePathLabel();
        fileLabel.setPreferredSize(new Dimension(300,  20));
        JButton fileChooserButton = new JButton("Select");
        filmstripFileChooser = new JFileChooser(Options.getLastDirectory().toFile());
        filmstripFileChooser.setFileFilter(new ExtFileFilter("use", "USE Model"));
        filmstripFileChooser.setDialogTitle("Choose save file");
        filmstripFileChooser.setMultiSelectionEnabled(false);
        // set the label text to the selected file path
        filmstripFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
                    fileLabel.setText(filmstripFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filmstripFileChooser.showSaveDialog(TemporalOCLForm.this);
            }
        });
        mainPanel.add(new JLabel("Destination file:"), getGBC(row, 0));
        mainPanel.add(fileLabel, getGBC(row, 1));
        mainPanel.add(fileChooserButton, getGBC(row++, 2));
        
        // choosing the temporal OCL file
        JLabel toclFileLabel = new FilePathLabel();
        toclFileLabel.setPreferredSize(new Dimension(300, 20));
        JButton toclFileChooserButton = new JButton("Select");
        toclFileChooser = new JFileChooser(Options.getLastDirectory().toFile());
        toclFileChooser.setFileFilter(new ExtFileFilter("tocl", "Temporal OCL"));
        toclFileChooser.setDialogTitle("Choose Temporal OCL file");
        toclFileChooser.setMultiSelectionEnabled(false);
        // set the label text to the selected file path
        toclFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                if (cmd.equals(JFileChooser.APPROVE_SELECTION)) {
                    toclFileLabel.setText(toclFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        toclFileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toclFileChooser.showSaveDialog(TemporalOCLForm.this);
            }
        });
        mainPanel.add(new JLabel("Temporal OCL file:"), getGBC(row, 0));
        mainPanel.add(toclFileLabel, getGBC(row, 1));
        mainPanel.add(toclFileChooserButton, getGBC(row++, 2));
        
        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File filmstripFile = filmstripFileChooser.getSelectedFile();
                File toclFile = toclFileChooser.getSelectedFile();
                if (filmstripFile == null) {
                    JOptionPane.showMessageDialog(TemporalOCLForm.this,
                                                "Please select a destination file!",
                                                "No file",
                                                JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (toclFile == null) {
                    JOptionPane.showMessageDialog(TemporalOCLForm.this,
                                                "Please select a Temporal OCL file!",
                                                "No file",
                                                JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (filmstripFile.exists()) {
                    int overwriteResult = JOptionPane.showConfirmDialog(TemporalOCLForm.this,
                                                                        "File already exists. Overwrite?",
                                                                        "File exists",
                                                                        JOptionPane.YES_NO_OPTION);
                    if (overwriteResult != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                // check if tocl file is empty
                if (toclFile.length() == 0) {
                    JOptionPane.showMessageDialog(TemporalOCLForm.this,
                                                "Temporal OCL file is empty!",
                                                "Empty file",
                                                JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String modelName = modelNameField.getText().trim();
                if (modelName.isEmpty()) {
                    JOptionPane.showMessageDialog(TemporalOCLForm.this,
                            "Enter a model name!",
                            "Empty model name",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                dispose();
                // transform and save
                transformAndSave(filmstripFile, toclFile);
            }
        });
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        
        buttonPanel.add(okButton, gbc);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(cancelButton, gbc);
        mainPanel.add(buttonPanel, getGBC(row, 0, 3, 1));
        
        setContentPane(mainPanel);
        this.pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private void transformAndSave(File filmstripFile, File toclFile) {
        MModel currentModel = this.session.system().model();
        Collection<MClass> classes = currentModel.classes();
        // transform into filmstrip model and write result to file
        // "Maximum compatible transformation" is used
        FilmstripOptions options = new FilmstripOptions(currentModel, modelNameField.getText(), filmstripFile, false, false, FilmstripMMVisitor.NAME);
        FilmstripTransformer transformer = new FilmstripTransformer(options);
        try {
            transformer.transformAndSave();
        } catch (TransformationInputException | TransformationException | IOException e) {
            JOptionPane.showMessageDialog(this.mainWindow, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String result = TOCLTranslator.translate(toclFile, classes);
        System.out.println("TemporalOCL: \n" + result);
        // Append the result to the filmstrip file
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filmstripFile, true)))) {
            writer.println(getSameIdConstraint(classes));
            writer.println(getDiffIdInASnapshotConstraints(classes));
            writer.println(result);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this.mainWindow, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private String getSameIdConstraint(Collection<MClass> classes) {
        StringBuilder builder = new StringBuilder();
        for (MClass c : classes) {
            builder.append("context ").append(c.name()).append("\n");
            builder.append("inv ").append(c.name()).append("SameId: ").append("self.succ").append(c.name()).append(".isDefined() implies self.id = self.succ").append(c.name()).append(".id\n");
        }
        return builder.toString();
    }
    
    private String getDiffIdInASnapshotConstraints(Collection<MClass> classes) {
        StringBuilder builder = new StringBuilder();
        for (MClass c : classes) {
            builder.append("context ").append(c.name()).append("\n");
            builder.append("inv ").append(c.name()).append("DiffId: ").append("self.snapshot").append(c.name()).append(".").append(toLowerFirstChar(c.name())).append("->forAll(o | o <> self implies o.id <> self.id)\n");
        }
        return builder.toString();
    }
    
    private void close() {
        this.setVisible(false);
        this.dispose();
    }
    
    private GridBagConstraints getGBC(int row, int col){
        return getGBC(row, col, 1, 1);
    }
    
    private GridBagConstraints getGBC(int row, int col, int gridWidth, int gridHeight) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = gridWidth;
        gbc.gridheight = gridHeight;
        gbc.insets.top = (row==0)?0:5;
        gbc.insets.left = (col>0)?5:0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }
    
    private String toLowerFirstChar(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}