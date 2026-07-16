package org.vnu.sme.goal.acl.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.beans.PropertyVetoException;
import java.nio.file.Path;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.views.PrintableView;
import org.tzi.use.gui.views.View;
import org.vnu.sme.goal.acl.mm.AclModel;

@SuppressWarnings("serial")
public final class AclView extends JPanel implements View, PrintableView {
    private final MainWindow mainWindow;
    private final Placement placement;
    private final AclDiagram diagram;
    private final JTextArea specArea = new JTextArea();
    private Path sourceFile;
    private AclModel model;

    private AclView(MainWindow mainWindow, Placement placement) {
        super(new BorderLayout());
        this.mainWindow = mainWindow;
        this.placement = placement;
        this.diagram = new AclDiagram(mainWindow == null ? new java.io.PrintWriter(System.out, true) : mainWindow.logWriter());
        if (mainWindow != null) {
            diagram.setStatusBar(mainWindow.statusBar());
            diagram.setSwitchAction(placement == Placement.USE_DESKTOP ? "Open popup" : "Open in USE",
                    placement == Placement.USE_DESKTOP ? this::switchToPopupWindow : this::switchToUseDesktop);
        }
        specArea.setEditable(false);
        specArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Diagram", new JScrollPane(diagram));
        tabs.addTab("Specification", new JScrollPane(specArea));
        add(tabs, BorderLayout.CENTER);
    }

    public static void openUseDesktop(MainWindow mainWindow, AclModel model, Path sourceFile) {
        AclView view = new AclView(mainWindow, Placement.USE_DESKTOP);
        view.setSourceFile(sourceFile);
        view.setModel(model);
        view.showInUseDesktop();
    }

    public static void openPopupWindow(MainWindow mainWindow, AclModel model, Path sourceFile) {
        AclView view = new AclView(mainWindow, Placement.POPUP_WINDOW);
        view.setSourceFile(sourceFile);
        view.setModel(model);
        view.showInPopupWindow();
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
        diagram.setSourceFile(sourceFile);
    }

    public void setModel(AclModel model) {
        this.model = model;
        diagram.setModel(model);
        specArea.setText(model == null ? "" : AclSpecText.render(model));
        specArea.setCaretPosition(0);
    }

    private String title() {
        return sourceFile == null ? "ACL v2.0" : "ACL v2.0 - " + sourceFile.getFileName();
    }

    private void showInUseDesktop() {
        ViewFrame frame = new ViewFrame(title(), this, "Diagram.gif");
        JComponent content = (JComponent) frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(this, BorderLayout.CENTER);
        mainWindow.addNewViewFrame(frame);
        frame.setSize(1000, 700);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException ignored) {}
    }

    private void showInPopupWindow() {
        JFrame frame = new JFrame(title());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(this);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    private void switchToPopupWindow() {
        openPopupWindow(mainWindow, model, sourceFile);
        disposeOwner();
    }

    private void switchToUseDesktop() {
        openUseDesktop(mainWindow, model, sourceFile);
        disposeOwner();
    }

    private void disposeOwner() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner != null) owner.dispose();
    }

    @Override public void detachModel() { setModel(null); }

    public void update() { repaint(); }

    @Override public void printView(PageFormat pf) { diagram.printDiagram(pf, title()); }

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
