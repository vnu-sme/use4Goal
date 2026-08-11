package org.vnu.sme.frsl.view.selection;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.tzi.use.gui.views.diagrams.util.MenuScroller;
import org.vnu.sme.frsl.view.FRSL2UCD.UseCaseDiagram;
import org.vnu.sme.frsl.view.FRSL2UCD.UseCaseDiagramOptions;

public class UsecaseSelection {
    private final UseCaseDiagram diagram;
    private final UseCaseDiagramOptions opt;

    // Control for scroll menu
    private final int numOfElement = 20;
    private final int interval = 125;
    private final int topFixedCount = 0;
    private final int bottomFixedCount = 0;

    public UsecaseSelection(UseCaseDiagram diagram, UseCaseDiagramOptions opt) {
        this.diagram = diagram;
        this.opt = opt;
    }


    public JMenu getSubMenuHideUsecase() {

        JMenu subMenuSelectionUsecaseHide = new JMenu("hide use case");
        MenuScroller.setScrollerFor(subMenuSelectionUsecaseHide, numOfElement, interval, topFixedCount, bottomFixedCount);
        
        int nodesize = 0;

        // for (UsecaseNode node : )
        subMenuSelectionUsecaseHide.add(new JMenuItem("asdas"));
        return subMenuSelectionUsecaseHide;
    }

    public JCheckBoxMenuItem getSubMenuHideModelBrowser() {

        JCheckBoxMenuItem showModelBrowser = new JCheckBoxMenuItem("Show model browser");
        showModelBrowser.setState(opt.isShowBrowser());
        showModelBrowser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                opt.setShowBrowser(e.getStateChange() == ItemEvent.SELECTED);
                diagram.hideOrShow(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        return showModelBrowser;
    }
}
