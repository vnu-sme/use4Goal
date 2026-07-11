package org.vnu.sme.goal.bpmn2.view;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.beans.PropertyVetoException;
import java.nio.file.Path;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.views.PrintableView;
import org.tzi.use.gui.views.View;
import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;

@SuppressWarnings("serial")
public final class Bpmn2View extends JPanel implements View, PrintableView {
    private final MainWindow mainWindow;
    private final Bpmn2Diagram diagram;
    private final Placement placement;
    private Path sourceFile;
    private Path scenarioFile;
    private Bpmn2Model model;
    private Bpmn2ScenarioSnapshot scenarioSnapshot;
    private String scenarioSource;
    private String title = "BPMN";

    public Bpmn2View() {
        this(null, Placement.USE_DESKTOP);
    }

    private Bpmn2View(MainWindow mainWindow, Placement placement) {
        super(new BorderLayout());
        this.mainWindow = mainWindow;
        this.placement = placement;
        this.diagram = new Bpmn2Diagram(mainWindow == null ? new java.io.PrintWriter(System.out, true) : mainWindow.logWriter());
        if (mainWindow != null) {
            diagram.setStatusBar(mainWindow.statusBar());
            diagram.setSwitchAction(placement == Placement.USE_DESKTOP ? "Open popup" : "Open in USE",
                    placement == Placement.USE_DESKTOP ? this::switchToPopupWindow : this::switchToUseDesktop);
        }
        add(new JScrollPane(diagram), BorderLayout.CENTER);
    }

    public static void openUseDesktop(MainWindow mainWindow, Bpmn2Model model, Path sourceFile) {
        openUseDesktop(mainWindow, model, sourceFile, null);
    }

    public static void openUseDesktop(MainWindow mainWindow, Bpmn2Model model, Path sourceFile,
                                      Bpmn2ScenarioSnapshot snapshot) {
        openUseDesktop(mainWindow, model, sourceFile, snapshot, null, null);
    }

    public static void openUseDesktop(MainWindow mainWindow, Bpmn2Model model, Path sourceFile,
                                      Bpmn2ScenarioSnapshot snapshot, Path scenarioFile, String scenarioSource) {
        Bpmn2View view = new Bpmn2View(mainWindow, Placement.USE_DESKTOP);
        view.setSourceFile(sourceFile);
        view.setModel(model);
        view.setScenarioSnapshot(snapshot);
        view.setScenarioSource(scenarioFile, scenarioSource);
        view.showInUseDesktop();
    }

    public static void openPopupWindow(MainWindow mainWindow, Bpmn2Model model, Path sourceFile) {
        openPopupWindow(mainWindow, model, sourceFile, null);
    }

    public static void openPopupWindow(MainWindow mainWindow, Bpmn2Model model, Path sourceFile,
                                       Bpmn2ScenarioSnapshot snapshot) {
        openPopupWindow(mainWindow, model, sourceFile, snapshot, null, null);
    }

    public static void openPopupWindow(MainWindow mainWindow, Bpmn2Model model, Path sourceFile,
                                       Bpmn2ScenarioSnapshot snapshot, Path scenarioFile, String scenarioSource) {
        Bpmn2View view = new Bpmn2View(mainWindow, Placement.POPUP_WINDOW);
        view.setSourceFile(sourceFile);
        view.setModel(model);
        view.setScenarioSnapshot(snapshot);
        view.setScenarioSource(scenarioFile, scenarioSource);
        view.showInPopupWindow();
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
        this.title = sourceFile == null ? "BPMN" : "BPMN - " + sourceFile.getFileName();
        diagram.setSourceFile(sourceFile);
    }

    public void setModel(Bpmn2Model model) {
        this.model = model;
        diagram.setModel(model);
    }

    public void setScenarioSnapshot(Bpmn2ScenarioSnapshot snapshot) {
        this.scenarioSnapshot = snapshot;
        diagram.setScenarioSnapshot(snapshot);
    }

    public void setScenarioSource(Path scenarioFile, String scenarioSource) {
        this.scenarioFile = scenarioFile;
        this.scenarioSource = scenarioSource;
        diagram.setScenarioSource(scenarioFile, scenarioSource);
    }

    private void switchToPopupWindow() {
        openPopupWindow(mainWindow, model, sourceFile, scenarioSnapshot, scenarioFile, scenarioSource);
        ViewFrame owner = (ViewFrame) SwingUtilities.getAncestorOfClass(ViewFrame.class, this);
        if (owner != null) owner.dispose();
    }

    private void switchToUseDesktop() {
        openUseDesktop(mainWindow, model, sourceFile, scenarioSnapshot, scenarioFile, scenarioSource);
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner != null) owner.dispose();
    }

    private void showInUseDesktop() {
        ViewFrame frame = new ViewFrame(title, this, "Diagram.gif");
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override public void internalFrameActivated(InternalFrameEvent ev) {
                mainWindow.statusBar().showTmpMessage("Move BPMN elements with the left mouse button; use right-click for options.");
            }
            @Override public void internalFrameDeactivated(InternalFrameEvent ev) {
                mainWindow.statusBar().clearMessage();
            }
        });
        JComponent content = (JComponent) frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(this, BorderLayout.CENTER);
        mainWindow.addNewViewFrame(frame);
        frame.setSize(950, 650);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException ignored) {}
    }

    private void showInPopupWindow() {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(this);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    @Override public void detachModel() { diagram.setModel(null); }

    public void update() { repaint(); }

    @Override public void printView(PageFormat pf) { diagram.printDiagram(pf, title); }

    @Override public void export(Graphics2D g) {
        boolean oldDb = diagram.isDoubleBuffered();
        diagram.setDoubleBuffered(false);
        diagram.paint(g);
        diagram.setDoubleBuffered(oldDb);
    }

    @Override public float getContentHeight() { return diagram.getPreferredSize().height; }

    @Override public float getContentWidth() { return diagram.getPreferredSize().width; }

    private enum Placement {
        USE_DESKTOP,
        POPUP_WINDOW
    }
}
