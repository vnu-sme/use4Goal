package org.vnu.sme.goal.feature.aclstate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession.ConstraintResult;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession.StateResult;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession.TruthValue;
import org.vnu.sme.goal.verify.aclstate.AclBpmnStateTraceEvaluator;
import org.vnu.sme.goal.verify.aclstate.AclBpmnStateTraceEvaluator.StepAlternative;
import org.vnu.sme.goal.verify.aclstate.AclBpmnStateTraceEvaluator.StepResult;
import org.vnu.sme.goal.verify.aclstate.AclBpmnStateTraceEvaluator.TraceResult;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.MappingEntry;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ProcessResult;
import org.vnu.sme.goal.verify.aclstate.AclBpmnWholeProcessValidator.ValidationResult;
import org.vnu.sme.goal.verify.aclstate.AclStateScenario;

/**
 * Validation-mode UI for ACL states. The ACL file defines the state space and
 * embeds its OCL invariants; each added AOL file contributes one complete state.
 */
@SuppressWarnings("serial")
public final class AclStateEvaluatorForm extends JDialog {
    private static final Preferences PREFS = Preferences.userNodeForPackage(AclStateEvaluatorForm.class);
    private static final String PREF_ACL = "aclStateEvaluator.aclFile";
    private static final String PREF_AOL_DIR = "aclStateEvaluator.aolDirectory";
    private static final String PREF_BPMN = "aclStateEvaluator.bpmnFile";
    private static final String PREF_ISTAR = "aclStateEvaluator.istarFile";
    private static final String PREF_BOUNDARY = "aclStateEvaluator.boundaryFile";
    private static final String PREF_SCENARIO_DIR = "aclStateEvaluator.scenarioDirectory";
    private static final Color GREEN = new Color(0, 125, 45);
    private static final Color RED = new Color(175, 35, 35);
    private static final Color AMBER = new Color(175, 115, 0);
    private static final Color GRAY = new Color(115, 115, 115);

    private final JTextField aclField = new JTextField(56);
    private final JTextField bpmnField = new JTextField(56);
    private final JTextField istarField = new JTextField(56);
    private final JTextField boundaryField = new JTextField(56);
    private final JButton addStateButton = new JButton("Add AOL v2 state(s)...");
    private final JButton loadBpmnButton = new JButton("Load BPMN");
    private final JButton loadIStarButton = new JButton("Load iStar");
    private final JButton loadBoundaryButton = new JButton("Load boundary");
    private final JButton loadScenarioButton = new JButton("Load state-only scenario...");
    private final JButton validateWholeButton = new JButton("Validate whole consistency");
    private final JButton previousButton = new JButton("Previous state");
    private final JButton nextButton = new JButton("Next state");
    private final JLabel summaryLabel = new JLabel("Load an ACL specification to begin.");
    private final JLabel statusLabel = new JLabel(" ");
    private final DefaultTableModel statesModel = readOnlyModel(
            "State", "AOL snapshot", "Objects", "Links", "Structure",
            "True", "False", "Undefined", "Error");
    private final DefaultTableModel constraintsModel = readOnlyModel(
            "Invariant", "Context", "Result", "OCL expression", "Detail");
    private final DefaultTableModel bpmnStepsModel = readOnlyModel(
            "Step", "State pair", "Process", "Self", "Flow", "Source", "Target",
            "Pass", "Next", "Result", "Alternatives");
    private final DefaultTableModel wholeProcessModel = readOnlyModel(
            "Process", "Self", "Consistency", "Backend", "Loop bound", "Snapshots", "BPMN configs",
            "Transitions", "Executions", "Completed", "Loop cutoffs", "State cutoffs",
            "Solver calls", "Detail");
    private final DefaultTableModel mappingModel = readOnlyModel(
            "Process", "BPMN activity", "Activity name", "Lane", "iStar actor",
            "Leaf kind", "Leaf element", "Score", "Mapping basis");
    private final JTable statesTable = new JTable(statesModel);
    private final JTable constraintsTable = new JTable(constraintsModel);
    private final JTable bpmnStepsTable = new JTable(bpmnStepsModel);
    private final JTable wholeProcessTable = new JTable(wholeProcessModel);
    private final JTable mappingTable = new JTable(mappingModel);
    private final JTextArea aclOclSource = new JTextArea();
    private final JTextArea bpmnSource = new JTextArea();
    private final JTextArea istarSource = new JTextArea();
    private final JTextArea bpmnDetail = new JTextArea();
    private final JTextArea wholeProcessDetail = new JTextArea();

    private AclStateEvaluationSession evaluation;
    private TraceResult bpmnTrace;
    private ValidationResult wholeProcessValidation;

    public AclStateEvaluatorForm(MainWindow owner) {
        super(owner, "ACL State Evaluator", false);
        buildUi();
        aclField.setText(PREFS.get(PREF_ACL, ""));
        bpmnField.setText(PREFS.get(PREF_BPMN, ""));
        istarField.setText(PREFS.get(PREF_ISTAR, ""));
        boundaryField.setText(PREFS.get(PREF_BOUNDARY, ""));
        setSize(1280, 760);
        setMinimumSize(new Dimension(920, 580));
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(7, 7));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));
        root.add(buildInputs(), BorderLayout.NORTH);
        root.add(buildContent(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildInputs() {
        JPanel inputs = new JPanel();
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));
        JPanel aclRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JLabel label = new JLabel("ACL specification:");
        label.setPreferredSize(new Dimension(125, label.getPreferredSize().height));
        JButton browse = new JButton("Browse...");
        browse.addActionListener(e -> chooseAcl());
        JButton load = new JButton("Load ACL");
        load.setFont(load.getFont().deriveFont(Font.BOLD));
        load.addActionListener(e -> loadAcl());
        aclField.addActionListener(e -> loadAcl());
        aclRow.add(label);
        aclRow.add(aclField);
        aclRow.add(browse);
        aclRow.add(load);

        JPanel bpmnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JLabel bpmnLabel = new JLabel("BPMN process:");
        bpmnLabel.setPreferredSize(new Dimension(125, bpmnLabel.getPreferredSize().height));
        JButton browseBpmn = new JButton("Browse...");
        browseBpmn.addActionListener(e -> chooseBpmn());
        loadBpmnButton.setEnabled(false);
        loadBpmnButton.addActionListener(e -> loadBpmn());
        bpmnField.addActionListener(e -> loadBpmn());
        bpmnRow.add(bpmnLabel);
        bpmnRow.add(bpmnField);
        bpmnRow.add(browseBpmn);
        bpmnRow.add(loadBpmnButton);

        JPanel istarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JLabel istarLabel = new JLabel("iStar requirements:");
        istarLabel.setPreferredSize(new Dimension(125, istarLabel.getPreferredSize().height));
        JButton browseIStar = new JButton("Browse...");
        browseIStar.addActionListener(e -> chooseIStar());
        loadIStarButton.setEnabled(false);
        loadIStarButton.addActionListener(e -> loadIStar());
        istarField.addActionListener(e -> loadIStar());
        istarRow.add(istarLabel);
        istarRow.add(istarField);
        istarRow.add(browseIStar);
        istarRow.add(loadIStarButton);

        JPanel boundaryRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JLabel boundaryLabel = new JLabel("Validation boundary:");
        boundaryLabel.setPreferredSize(new Dimension(125, boundaryLabel.getPreferredSize().height));
        JButton browseBoundary = new JButton("Browse...");
        browseBoundary.addActionListener(e -> chooseBoundary());
        loadBoundaryButton.setEnabled(false);
        loadBoundaryButton.addActionListener(e -> loadBoundary());
        boundaryField.addActionListener(e -> loadBoundary());
        boundaryRow.add(boundaryLabel);
        boundaryRow.add(boundaryField);
        boundaryRow.add(browseBoundary);
        boundaryRow.add(loadBoundaryButton);

        JPanel stateRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        addStateButton.setEnabled(false);
        addStateButton.addActionListener(e -> addStates());
        loadScenarioButton.addActionListener(e -> loadScenario());
        validateWholeButton.setEnabled(false);
        validateWholeButton.setToolTipText(
                "Kodkod/SAT4J generates bounded ACL paths and checks BPMN execution against iStar root goals");
        validateWholeButton.addActionListener(e -> refreshWholeProcessValidation());
        stateRow.add(loadScenarioButton);
        stateRow.add(addStateButton);
        stateRow.add(validateWholeButton);
        stateRow.add(new JLabel("Scenario uses AOL. Whole validation uses ACL + BPMN + iStar + boundary."));

        inputs.add(aclRow);
        inputs.add(bpmnRow);
        inputs.add(istarRow);
        inputs.add(boundaryRow);
        inputs.add(stateRow);
        return inputs;
    }

    private Component buildContent() {
        configureTables();
        JPanel stateHistory = new JPanel(new BorderLayout(0, 4));
        stateHistory.setBorder(BorderFactory.createTitledBorder("System states"));
        stateHistory.add(new JScrollPane(statesTable), BorderLayout.CENTER);

        JPanel constraintDetail = new JPanel(new BorderLayout(0, 4));
        constraintDetail.setBorder(BorderFactory.createTitledBorder("OCL results at selected state"));
        constraintDetail.add(new JScrollPane(constraintsTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stateHistory, constraintDetail);
        split.setResizeWeight(0.35);
        split.setDividerLocation(220);

        aclOclSource.setEditable(false);
        aclOclSource.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        aclOclSource.setTabSize(2);

        bpmnSource.setEditable(false);
        bpmnSource.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        bpmnSource.setTabSize(2);
        istarSource.setEditable(false);
        istarSource.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        istarSource.setTabSize(2);
        bpmnDetail.setEditable(false);
        bpmnDetail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        bpmnDetail.setLineWrap(true);
        bpmnDetail.setWrapStyleWord(true);

        wholeProcessDetail.setEditable(false);
        wholeProcessDetail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        wholeProcessDetail.setLineWrap(true);
        wholeProcessDetail.setWrapStyleWord(true);

        JPanel bpmnEvaluation = new JPanel(new BorderLayout(0, 4));
        bpmnEvaluation.setBorder(BorderFactory.createTitledBorder("State-only BPMN conformance"));
        JSplitPane bpmnSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(bpmnStepsTable), new JScrollPane(bpmnDetail));
        bpmnSplit.setResizeWeight(0.68);
        bpmnSplit.setDividerLocation(330);
        bpmnEvaluation.add(bpmnSplit, BorderLayout.CENTER);

        JPanel wholeEvaluation = new JPanel(new BorderLayout(0, 4));
        wholeEvaluation.setBorder(BorderFactory.createTitledBorder(
                "Kodkod whole validation: ACL state paths + BPMN process + iStar goals"));
        JSplitPane wholeSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(wholeProcessTable), new JScrollPane(wholeProcessDetail));
        wholeSplit.setResizeWeight(0.55);
        wholeSplit.setDividerLocation(280);
        wholeEvaluation.add(wholeSplit, BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("State evaluation", split);
        tabs.addTab("BPMN inferred trace", bpmnEvaluation);
        tabs.addTab("Whole consistency", wholeEvaluation);
        tabs.addTab("BPMN–iStar mapping", new JScrollPane(mappingTable));
        tabs.addTab("ACL/OCL specification", new JScrollPane(aclOclSource));
        tabs.addTab("BPMN specification", new JScrollPane(bpmnSource));
        tabs.addTab("iStar specification", new JScrollPane(istarSource));
        return tabs;
    }

    private void configureTables() {
        statesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statesTable.setAutoCreateRowSorter(true);
        statesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedState();
        });
        statesTable.getColumnModel().getColumn(1).setPreferredWidth(360);
        statesTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        constraintsTable.setAutoCreateRowSorter(true);
        constraintsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        constraintsTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        constraintsTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        constraintsTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        constraintsTable.getColumnModel().getColumn(3).setPreferredWidth(590);
        constraintsTable.getColumnModel().getColumn(4).setPreferredWidth(260);
        constraintsTable.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());

        bpmnStepsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bpmnStepsTable.setAutoCreateRowSorter(true);
        bpmnStepsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {55, 190, 120, 90, 180, 140, 140, 220, 220, 120, 90};
        for (int i = 0; i < widths.length; i++) bpmnStepsTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        bpmnStepsTable.getColumnModel().getColumn(9).setCellRenderer(new StatusRenderer());
        bpmnStepsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedBpmnStep();
        });

        wholeProcessTable.setAutoCreateRowSorter(true);
        wholeProcessTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wholeWidths = {140, 100, 100, 130, 90, 90, 110, 100, 100, 100, 100, 100, 100, 520};
        for (int i = 0; i < wholeWidths.length; i++) {
            wholeProcessTable.getColumnModel().getColumn(i).setPreferredWidth(wholeWidths[i]);
        }
        wholeProcessTable.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());
        wholeProcessTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showSelectedWholeProcess();
        });

        mappingTable.setAutoCreateRowSorter(true);
        mappingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] mappingWidths = {120, 190, 220, 100, 110, 90, 210, 70, 330};
        for (int i = 0; i < mappingWidths.length; i++) {
            mappingTable.getColumnModel().getColumn(i).setPreferredWidth(mappingWidths[i]);
        }
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(4, 3));
        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 1));
        previousButton.addActionListener(e -> moveSelection(-1));
        nextButton.addActionListener(e -> moveSelection(1));
        navigation.add(previousButton);
        navigation.add(summaryLabel);
        navigation.add(nextButton);
        footer.add(navigation, BorderLayout.NORTH);
        footer.add(statusLabel, BorderLayout.SOUTH);
        updateNavigation();
        return footer;
    }

    private void chooseAcl() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("ACL specifications (*.acl)", "acl"));
        if (!aclField.getText().isBlank()) chooser.setSelectedFile(new File(aclField.getText().trim()));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            aclField.setText(chooser.getSelectedFile().getAbsolutePath());
            loadAcl();
        }
    }

    private void chooseBpmn() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("BPMN process specifications (*.bpmn2, *.bpmn)",
                "bpmn2", "bpmn"));
        if (!bpmnField.getText().isBlank()) chooser.setSelectedFile(new File(bpmnField.getText().trim()));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            bpmnField.setText(chooser.getSelectedFile().getAbsolutePath());
            loadBpmn();
        }
    }

    private void chooseIStar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("iStar requirement models (*.istar)", "istar"));
        if (!istarField.getText().isBlank()) {
            chooser.setSelectedFile(new File(istarField.getText().trim()));
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            istarField.setText(chooser.getSelectedFile().getAbsolutePath());
            loadIStar();
        }
    }

    private void chooseBoundary() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "ACL/BPMN boundaries (*.aclboundary)", "aclboundary"));
        if (!boundaryField.getText().isBlank()) {
            chooser.setSelectedFile(new File(boundaryField.getText().trim()));
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            boundaryField.setText(chooser.getSelectedFile().getAbsolutePath());
            loadBoundary();
        }
    }

    private void loadAcl() {
        String input = aclField.getText().trim();
        if (input.isEmpty()) {
            status("Select an ACL specification first.", RED);
            return;
        }
        try {
            AclStateEvaluationSession loaded = AclStateEvaluationSession.load(Path.of(input));
            evaluation = loaded;
            statesModel.setRowCount(0);
            constraintsModel.setRowCount(0);
            aclOclSource.setText(loaded.aclSource());
            aclOclSource.setCaretPosition(0);
            addStateButton.setEnabled(true);
            loadBpmnButton.setEnabled(true);
            loadIStarButton.setEnabled(true);
            loadBoundaryButton.setEnabled(true);
            validateWholeButton.setEnabled(false);
            bpmnStepsModel.setRowCount(0);
            wholeProcessModel.setRowCount(0);
            bpmnSource.setText("");
            istarSource.setText("");
            bpmnDetail.setText("Load a BPMN process, then add at least two ordered AOL states.");
            wholeProcessDetail.setText("Load BPMN and an .aclboundary file for whole validation.");
            bpmnTrace = null;
            wholeProcessValidation = null;
            boundaryField.setText("");
            PREFS.put(PREF_ACL, loaded.aclFile().toString());
            summaryLabel.setText("No AOL states added yet.");
            updateNavigation();
            status("Loaded " + loaded.aclFile().getFileName() + ": "
                    + loaded.aclModel().invariants().size() + " native ACL/OCL invariant(s).", GREEN);
        } catch (Exception ex) {
            evaluation = null;
            addStateButton.setEnabled(false);
            loadBpmnButton.setEnabled(false);
            loadIStarButton.setEnabled(false);
            loadBoundaryButton.setEnabled(false);
            validateWholeButton.setEnabled(false);
            aclOclSource.setText("");
            showError("Cannot load ACL", ex);
            status("ACL load failed.", RED);
        }
    }

    private void loadIStar() {
        if (evaluation == null) {
            status("Load an ACL specification before loading iStar.", RED);
            return;
        }
        String input = istarField.getText().trim();
        if (input.isEmpty()) {
            status("Select an iStar requirement model first.", RED);
            return;
        }
        try {
            var loaded = evaluation.loadIStar(Path.of(input));
            istarSource.setText(evaluation.istarSource());
            istarSource.setCaretPosition(0);
            istarField.setText(evaluation.istarFile().toString());
            PREFS.put(PREF_ISTAR, evaluation.istarFile().toString());
            validateWholeButton.setEnabled(evaluation.bpmnEvaluator() != null
                    && evaluation.boundary() != null);
            resetWholeProcessPrompt();
            long goals = loaded.allElements().values().stream()
                    .filter(org.vnu.sme.goal.dsl.istar.mm.Goal.class::isInstance).count();
            status("Loaded " + evaluation.istarFile().getFileName() + ": " + goals
                    + " goal(s). Non-leaf goal conditions are rejected by the compiler.", GREEN);
        } catch (Exception ex) {
            istarSource.setText("");
            validateWholeButton.setEnabled(false);
            showError("Cannot load iStar", ex);
            status("iStar load failed.", RED);
        }
    }

    private void loadBpmn() {
        if (evaluation == null) {
            status("Load an ACL specification before loading BPMN.", RED);
            return;
        }
        String input = bpmnField.getText().trim();
        if (input.isEmpty()) {
            status("Select a BPMN process specification first.", RED);
            return;
        }
        try {
            AclBpmnStateTraceEvaluator loaded = evaluation.loadBpmn(Path.of(input));
            bpmnSource.setText(loaded.bpmnSource());
            bpmnSource.setCaretPosition(0);
            PREFS.put(PREF_BPMN, loaded.bpmnFile().toString());
            validateWholeButton.setEnabled(evaluation.boundary() != null
                    && evaluation.goalModel() != null);
            refreshBpmnTrace();
            resetWholeProcessPrompt();
            status("Loaded " + loaded.bpmnFile().getFileName() + ": "
                    + loaded.bpmnModel().processes().size() + " process(es), "
                    + loaded.bpmnModel().flowElementCount() + " flow element(s).", GREEN);
        } catch (Exception ex) {
            bpmnSource.setText("");
            bpmnStepsModel.setRowCount(0);
            wholeProcessModel.setRowCount(0);
            bpmnTrace = null;
            wholeProcessValidation = null;
            validateWholeButton.setEnabled(false);
            showError("Cannot load BPMN", ex);
            status("BPMN load failed.", RED);
        }
    }

    private void loadBoundary() {
        if (evaluation == null) {
            status("Load an ACL specification before loading its boundary.", RED);
            return;
        }
        String input = boundaryField.getText().trim();
        if (input.isEmpty()) {
            status("Select an .aclboundary file first.", RED);
            return;
        }
        try {
            var loaded = evaluation.loadBoundary(Path.of(input));
            boundaryField.setText(loaded.file().toString());
            PREFS.put(PREF_BOUNDARY, loaded.file().toString());
            validateWholeButton.setEnabled(evaluation.bpmnEvaluator() != null
                    && evaluation.goalModel() != null);
            resetWholeProcessPrompt();
            status("Loaded boundary " + loaded.file().getFileName() + ": snapshots="
                    + loaded.snapshots() + ", loop-bound=" + loaded.loopBound()
                    + ", classifier scopes=" + loaded.objectScopes().size() + ".", GREEN);
        } catch (Exception ex) {
            validateWholeButton.setEnabled(false);
            showError("Cannot load validation boundary", ex);
            status("Boundary load failed.", RED);
        }
    }

    private void loadScenario() {
        String fallbackDirectory = ".";
        if (!aclField.getText().isBlank()) {
            File parent = new File(aclField.getText().trim()).getAbsoluteFile().getParentFile();
            if (parent != null) fallbackDirectory = parent.getAbsolutePath();
        }
        JFileChooser chooser = new JFileChooser(PREFS.get(PREF_SCENARIO_DIR, fallbackDirectory));
        chooser.setFileFilter(new FileNameExtensionFilter(
                "ACL state-only scenarios (*.aclscenario)", "aclscenario"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File selected = chooser.getSelectedFile();
        if (selected.getParentFile() != null) {
            PREFS.put(PREF_SCENARIO_DIR, selected.getParentFile().getAbsolutePath());
        }
        try {
            AclStateScenario scenario = AclStateScenario.load(selected.toPath());
            AclStateEvaluationSession loaded = AclStateEvaluationSession.load(scenario.aclFile());
            loaded.loadBpmn(scenario.bpmnFile());
            for (Path state : scenario.stateFiles()) loaded.addState(state);

            evaluation = loaded;
            aclField.setText(loaded.aclFile().toString());
            bpmnField.setText(loaded.bpmnEvaluator().bpmnFile().toString());
            aclOclSource.setText(loaded.aclSource());
            aclOclSource.setCaretPosition(0);
            bpmnSource.setText(loaded.bpmnEvaluator().bpmnSource());
            bpmnSource.setCaretPosition(0);
            addStateButton.setEnabled(true);
            loadBpmnButton.setEnabled(true);
            loadIStarButton.setEnabled(true);
            loadBoundaryButton.setEnabled(true);
            validateWholeButton.setEnabled(false);
            statesModel.setRowCount(0);
            constraintsModel.setRowCount(0);
            for (StateResult state : loaded.states()) appendState(state);
            if (!loaded.states().isEmpty()) statesTable.setRowSelectionInterval(0, 0);
            PREFS.put(PREF_ACL, loaded.aclFile().toString());
            PREFS.put(PREF_BPMN, loaded.bpmnEvaluator().bpmnFile().toString());
            refreshBpmnTrace();
            resetWholeProcessPrompt();
            status("Loaded state-only scenario " + scenario.name() + " with "
                    + scenario.stateFiles().size() + " ACL snapshots. " + bpmnTrace.summary(),
                    colorFor(bpmnTrace.verdict()));
        } catch (Exception ex) {
            showError("Cannot load state-only scenario", ex);
            status("Scenario load failed.", RED);
        }
    }

    private void addStates() {
        if (evaluation == null) return;
        JFileChooser chooser = new JFileChooser(PREFS.get(PREF_AOL_DIR,
                evaluation.aclFile().getParent().toString()));
        chooser.setFileFilter(new FileNameExtensionFilter("Formal AOL v2 state snapshots (*.aol)", "aol"));
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File[] selected = chooser.getSelectedFiles();
        if (selected.length == 0 && chooser.getSelectedFile() != null) {
            selected = new File[] { chooser.getSelectedFile() };
        }
        if (selected.length == 0) return;
        Arrays.sort(selected, Comparator.comparing(File::getName));
        File parent = selected[0].getParentFile();
        if (parent != null) PREFS.put(PREF_AOL_DIR, parent.getAbsolutePath());

        int added = 0;
        for (File file : selected) {
            try {
                StateResult result = evaluation.addState(file.toPath());
                appendState(result);
                added++;
            } catch (Exception ex) {
                showError("Cannot add AOL state " + file.getName(), ex);
                break;
            }
        }
        if (added > 0) {
            int row = statesModel.getRowCount() - 1;
            statesTable.setRowSelectionInterval(row, row);
            status("Added and evaluated " + added + " AOL state snapshot(s).", GREEN);
            refreshBpmnTrace();
            resetWholeProcessPrompt();
        }
        updateNavigation();
    }

    private void appendState(StateResult state) {
        statesModel.addRow(new Object[] {
                "s" + state.index(), state.aolFile().getFileName().toString(),
                state.objectCount(), state.linkCount(), state.structureValid() ? "VALID" : "INVALID",
                state.trueCount(), state.falseCount(), state.undefinedCount(), state.errorCount()
        });
    }

    private void showSelectedState() {
        constraintsModel.setRowCount(0);
        if (evaluation == null) {
            updateNavigation();
            return;
        }
        int viewRow = statesTable.getSelectedRow();
        if (viewRow < 0) {
            updateNavigation();
            return;
        }
        int modelRow = statesTable.convertRowIndexToModel(viewRow);
        List<StateResult> states = evaluation.states();
        if (modelRow >= states.size()) return;
        StateResult state = states.get(modelRow);
        for (ConstraintResult constraint : state.constraints()) {
            constraintsModel.addRow(new Object[] {
                    constraint.name(), constraint.context(), constraint.result(),
                    constraint.expression(), constraint.detail()
            });
        }
        summaryLabel.setText("s" + state.index() + " — OCL: " + state.trueCount() + " true, "
                + state.falseCount() + " false, " + state.undefinedCount() + " undefined, "
                + state.errorCount() + " error; structure: "
                + (state.structureValid() ? "valid" : "invalid")
                + (state.inputDiagnostics().isEmpty() ? "" : "; AOL diagnostics: " + state.inputDiagnostics().size()));
        status(state.structureValid() && state.falseCount() == 0
                        && state.undefinedCount() == 0 && state.errorCount() == 0
                        ? "Selected state satisfies its structure and every embedded OCL invariant."
                        : !state.inputDiagnostics().isEmpty()
                                ? String.join("\n", state.inputDiagnostics())
                                : state.structureReport().isBlank()
                                ? "Selected state contains failed or undefined OCL results."
                                : state.structureReport(),
                state.structureValid() && state.falseCount() == 0
                        && state.undefinedCount() == 0 && state.errorCount() == 0 ? GREEN : RED);
        updateNavigation();
    }

    private void refreshBpmnTrace() {
        bpmnStepsModel.setRowCount(0);
        bpmnDetail.setText("");
        bpmnTrace = null;
        if (evaluation == null || evaluation.bpmnEvaluator() == null) {
            bpmnDetail.setText("Load a BPMN process to infer the execution route.");
            return;
        }
        if (evaluation.states().size() < 2) {
            bpmnDetail.setText("Add at least two ordered AOL snapshots. Each adjacent pair denotes one formal flow execution.");
            return;
        }
        bpmnTrace = evaluation.evaluateBpmnTrace();
        for (StepResult step : bpmnTrace.steps()) {
            StepAlternative first = step.alternatives().isEmpty() ? null : step.alternatives().get(0);
            bpmnStepsModel.addRow(new Object[] {
                    step.index() + 1,
                    step.beforeFile().getFileName() + " -> " + step.afterFile().getFileName(),
                    first == null ? "-" : first.processId(),
                    first == null ? "-" : first.selfObject(),
                    first == null ? "-" : first.flowId(),
                    first == null ? "-" : first.sourceId(),
                    first == null ? "-" : first.targetId(),
                    first == null ? "-" : first.pass(),
                    first == null ? "-" : first.next(),
                    step.verdict(),
                    step.alternatives().size()
            });
        }
        if (bpmnStepsModel.getRowCount() > 0) {
            bpmnStepsTable.setRowSelectionInterval(0, 0);
        } else {
            bpmnDetail.setText(bpmnTrace.summary());
        }
    }

    private void showSelectedBpmnStep() {
        if (bpmnTrace == null) return;
        int viewRow = bpmnStepsTable.getSelectedRow();
        if (viewRow < 0) return;
        int row = bpmnStepsTable.convertRowIndexToModel(viewRow);
        if (row >= bpmnTrace.steps().size()) return;
        StepResult step = bpmnTrace.steps().get(row);
        StringBuilder text = new StringBuilder();
        text.append("s").append(step.index()).append(" -> s").append(step.index() + 1)
                .append(": ").append(step.verdict()).append('\n')
                .append(step.detail()).append("\n\n");
        for (int i = 0; i < step.alternatives().size(); i++) {
            StepAlternative alternative = step.alternatives().get(i);
            text.append(i + 1).append(". process=").append(alternative.processId())
                    .append(", self=").append(alternative.selfObject())
                    .append(", flow=").append(alternative.flowId()).append('\n')
                    .append("   route: ").append(alternative.route()).append('\n')
                    .append("   Pass: ").append(alternative.pass()).append('\n')
                    .append("   Next: ").append(alternative.next()).append('\n')
                    .append("   contract: ").append(alternative.contract()).append('\n')
                    .append("   next activations: ").append(alternative.resultingActivations()).append("\n");
        }
        text.append("\nTrace: ").append(bpmnTrace.summary());
        bpmnDetail.setText(text.toString());
        bpmnDetail.setCaretPosition(0);
    }

    private void refreshWholeProcessValidation() {
        wholeProcessModel.setRowCount(0);
        mappingModel.setRowCount(0);
        wholeProcessDetail.setText("");
        wholeProcessValidation = null;
        if (evaluation == null || evaluation.bpmnEvaluator() == null) {
            wholeProcessDetail.setText("Load an ACL model and BPMN process first.");
            return;
        }
        if (evaluation.boundary() == null) {
            wholeProcessDetail.setText("Load an .aclboundary file. Whole validation does not use AOL states.");
            return;
        }
        if (evaluation.goalModel() == null) {
            wholeProcessDetail.setText("Load an iStar model. Its markings will be derived from generated ACL states.");
            return;
        }
        wholeProcessValidation = evaluation.validateWholeBpmnProcess();
        for (ProcessResult process : wholeProcessValidation.processes()) {
            wholeProcessModel.addRow(new Object[] {
                    process.processId(), process.selfObject(), wholeProcessValidation.consistency(),
                    wholeProcessValidation.backend(), wholeProcessValidation.loopBound(),
                    wholeProcessValidation.snapshots(),
                    process.productStates(), process.transitions(), process.boundedExecutions(),
                    process.completedStates(), process.loopCutoffs(), process.snapshotCutoffs(),
                    process.solverCalls(), process.detail()
            });
        }
        for (MappingEntry mapping : wholeProcessValidation.mappings()) {
            mappingModel.addRow(new Object[] {
                    mapping.processId(), mapping.activityId(), mapping.activityName(), mapping.lane(),
                    mapping.actor(), mapping.leafKind(), mapping.leafId(), mapping.score(), mapping.basis()
            });
        }
        if (wholeProcessModel.getRowCount() > 0) {
            wholeProcessTable.setRowSelectionInterval(0, 0);
        } else {
            wholeProcessDetail.setText(wholeProcessValidation.summary());
        }
        status(wholeProcessValidation.summary(), colorFor(wholeProcessValidation.verdict()));
    }

    private void resetWholeProcessPrompt() {
        wholeProcessModel.setRowCount(0);
        mappingModel.setRowCount(0);
        wholeProcessValidation = null;
        wholeProcessDetail.setText("Load ACL + BPMN + iStar + .aclboundary, then press 'Validate whole consistency'. "
                + "Kodkod generates ACL state paths; iStar has no separate state space and AOL is ignored.");
    }

    private void showSelectedWholeProcess() {
        if (wholeProcessValidation == null) return;
        int viewRow = wholeProcessTable.getSelectedRow();
        if (viewRow < 0) return;
        int row = wholeProcessTable.convertRowIndexToModel(viewRow);
        if (row >= wholeProcessValidation.processes().size()) return;
        ProcessResult process = wholeProcessValidation.processes().get(row);
        StringBuilder text = new StringBuilder();
        text.append("process=").append(process.processId())
                .append(", self=").append(process.selfObject())
                .append(", result=").append(process.verdict())
                .append(", consistency=").append(wholeProcessValidation.consistency())
                .append(", backend=").append(wholeProcessValidation.backend())
                .append(", loop bound=").append(wholeProcessValidation.loopBound())
                .append(", snapshots=").append(wholeProcessValidation.snapshots())
                .append("\nboundary=").append(wholeProcessValidation.boundaryFile()).append("\n\n")
                .append(process.detail()).append("\n\n")
                .append("Explored BPMN configurations: ").append(process.productStates()).append('\n')
                .append("Explored formal transitions: ").append(process.transitions()).append('\n')
                .append("Maximal bounded executions: ").append(process.boundedExecutions()).append('\n')
                .append("Fully consumed End executions: ").append(process.completedStates()).append('\n')
                .append("Loop-bound cutoffs: ").append(process.loopCutoffs()).append('\n')
                .append("Snapshot-bound cutoffs: ").append(process.snapshotCutoffs()).append('\n')
                .append("Kodkod solver calls: ").append(process.solverCalls()).append('\n');
        text.append("Realizable BPMN executions: ").append(process.realizableExecutions()).append('\n')
                .append("Goal-achieving executions: ").append(process.goalAchievingExecutions()).append('\n')
                .append("Root iStar goals: ").append(wholeProcessValidation.rootGoals()).append('\n');
        if (!process.counterexample().isEmpty()) {
            text.append("\nCounterexample execution:\n  ")
                    .append(String.join("\n  -> ", process.counterexample())).append('\n');
        }
        if (!process.witnessStates().isEmpty()) {
            text.append("\nExample SAT witness (state_at_i):\n  ")
                    .append(String.join("\n  -> ", process.witnessStates())).append('\n');
        }
        text.append("\n").append(wholeProcessValidation.summary())
                .append("\n\nThe displayed ACL states were generated by Kodkod from the boundary;")
                .append(" iStar markings were evaluated over that path, and no AOL scenario was loaded.")
                .append("\nThe mapping tab is explanatory inference; the verdict is obtained from OCL formulas.");
        wholeProcessDetail.setText(text.toString());
        wholeProcessDetail.setCaretPosition(0);
    }

    private void moveSelection(int delta) {
        int count = statesTable.getRowCount();
        if (count == 0) return;
        int row = statesTable.getSelectedRow();
        int next = Math.max(0, Math.min(count - 1, row < 0 ? 0 : row + delta));
        statesTable.setRowSelectionInterval(next, next);
        statesTable.scrollRectToVisible(statesTable.getCellRect(next, 0, true));
    }

    private void updateNavigation() {
        int row = statesTable.getSelectedRow();
        previousButton.setEnabled(row > 0);
        nextButton.setEnabled(row >= 0 && row + 1 < statesTable.getRowCount());
    }

    private void showError(String title, Exception ex) {
        JTextArea text = new JTextArea(message(ex));
        text.setEditable(false);
        text.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(850, 360));
        JOptionPane.showMessageDialog(this, scroll, title, JOptionPane.ERROR_MESSAGE);
    }

    private void status(String text, Color color) {
        statusLabel.setText("<html>" + escape(text).replace("\n", "<br>") + "</html>");
        statusLabel.setForeground(color);
    }

    private static Color colorFor(AclBpmnStateTraceEvaluator.Verdict verdict) {
        return switch (verdict) {
            case CONFORMANT -> GREEN;
            case AMBIGUOUS -> AMBER;
            case NON_CONFORMANT -> RED;
        };
    }

    private static Color colorFor(AclBpmnWholeProcessValidator.Verdict verdict) {
        return switch (verdict) {
            case VALID -> GREEN;
            case INCONCLUSIVE -> AMBER;
            case INVALID -> RED;
        };
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private static String message(Throwable throwable) {
        String value = throwable.getMessage();
        return value == null || value.isBlank() ? throwable.toString() : value;
    }

    private static String escape(String text) {
        return String.valueOf(text).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final class StatusRenderer extends DefaultTableCellRenderer {
        StatusRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                        boolean focus, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            if (!selected) {
                Color color = switch (String.valueOf(value)) {
                    case "TRUE", "VALID", "CONFORMANT", "CONSISTENT" -> GREEN;
                    case "FALSE", "INVALID", "ERROR", "NON_CONFORMANT", "INCONSISTENT" -> RED;
                    case "UNDEFINED", "AMBIGUOUS", "INCONCLUSIVE", "WEAKLY_CONSISTENT" -> AMBER;
                    default -> GRAY;
                };
                setForeground(color);
                setFont(getFont().deriveFont(Font.BOLD));
            }
            return this;
        }
    }
}
