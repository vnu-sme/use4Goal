package org.vnu.sme.frsl.view.selection.event;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUCView;

public class ActionShowSequenceView extends ActionShowDiagram{
    
    private Usecase usecase;
    private MainWindow mainWindow;
    private FrslModel model;
    public ActionShowSequenceView(String label, Usecase usecase, MainWindow mainWindow, FrslModel model) {
        super(label);
        this.usecase = usecase;
        this.mainWindow = mainWindow;
        this.model = model;

    }

    @Override
    protected void showDiagram() {
        SequenceUCView suv = new SequenceUCView(mainWindow, usecase, model);
        ViewFrame f = new ViewFrame(usecase.getName() + " sequence", suv, "SequenceUCDiagram.gif");
		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(suv, BorderLayout.CENTER);
		mainWindow.addNewViewFrame(f);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showDiagram();
    }

    public void showFromAnother() {
        showDiagram();
    }
}
