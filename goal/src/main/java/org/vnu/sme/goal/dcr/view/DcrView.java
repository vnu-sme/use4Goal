package org.vnu.sme.goal.dcr.view;

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
import org.vnu.sme.goal.dcr.mm.DcrModel;

@SuppressWarnings("serial")
public final class DcrView extends JPanel implements View, PrintableView {
    private final MainWindow mainWindow;
    private final DcrDiagram diagram;
    private final Placement placement;
    private String printTitle = "DCR graph";
    private Path sourceFile;
    private DcrModel model;

    private DcrView(MainWindow mainWindow, Placement placement) {
        super(new BorderLayout());
        this.mainWindow = mainWindow;
        this.placement = placement;
        diagram = new DcrDiagram(mainWindow.logWriter());
        diagram.setStatusBar(mainWindow.statusBar());
        configureSwitchAction();
        add(new JScrollPane(diagram), BorderLayout.CENTER);
    }

    public static void openUseDesktop(MainWindow mainWindow, DcrModel model, Path sourceFile) {
        DcrView view = new DcrView(mainWindow, Placement.USE_DESKTOP);
        view.setSourceFile(sourceFile);
        view.setModel(model);
        view.showInUseDesktop();
    }

    public static void openPopupWindow(MainWindow mainWindow, DcrModel model, Path sourceFile) {
        DcrView view = new DcrView(mainWindow, Placement.POPUP_WINDOW);
        view.setSourceFile(sourceFile);
        view.setModel(model);
        view.showInPopupWindow();
    }

    private void configureSwitchAction() {
        if (placement == Placement.USE_DESKTOP) {
            diagram.setSwitchAction("Open popup", this::switchToPopupWindow);
        } else {
            diagram.setSwitchAction("Open in USE", this::switchToUseDesktop);
        }
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
        diagram.setSourceFile(sourceFile);
        if (sourceFile != null) {
            printTitle = "DCR graph - " + sourceFile.getFileName();
        }
    }

    public void setModel(DcrModel model) {
        this.model = model;
        diagram.setModel(model);
    }

    public DcrDiagram getDiagram() {
        return diagram;
    }

    @Override
    public void detachModel() {
        diagram.setModel(null);
    }

    private void switchToPopupWindow() {
        openPopupWindow(mainWindow, model, sourceFile);
        ViewFrame owner = (ViewFrame) SwingUtilities.getAncestorOfClass(ViewFrame.class, this);
        if (owner != null) {
            owner.dispose();
        }
    }

    private void switchToUseDesktop() {
        openUseDesktop(mainWindow, model, sourceFile);
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner != null) {
            owner.dispose();
        }
    }

    private void showInUseDesktop() {
        ViewFrame frame = new ViewFrame(printTitle, this, "Diagram.gif");
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent ev) {
                mainWindow.statusBar().showTmpMessage("Move DCR events with the left mouse button; use right-click for options.");
            }

            @Override
            public void internalFrameDeactivated(InternalFrameEvent ev) {
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
        } catch (PropertyVetoException ignored) {
            // USE may veto selection while another internal-frame operation is in progress.
        }
    }

    private void showInPopupWindow() {
        JFrame frame = new JFrame(printTitle);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(this);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    @Override
    public void printView(PageFormat pf) {
        diagram.printDiagram(pf, printTitle);
    }

    @Override
    public void export(Graphics2D g) {
        boolean oldDb = diagram.isDoubleBuffered();
        diagram.setDoubleBuffered(false);
        diagram.paint(g);
        diagram.setDoubleBuffered(oldDb);
    }

    @Override
    public float getContentHeight() {
        return diagram.getPreferredSize().height;
    }

    @Override
    public float getContentWidth() {
        return diagram.getPreferredSize().width;
    }

    private enum Placement {
        USE_DESKTOP,
        POPUP_WINDOW
    }
}
