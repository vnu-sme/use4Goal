package org.vnu.sme.frsl.view.FRSL2SSD;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;


import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.PrintableView;
import org.tzi.use.gui.views.View;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.view.Browser.Browser;
import org.vnu.sme.frsl.view.Browser.TreeSequence;


public class SequenceUCView extends JPanel implements View, PrintableView {
    
	private MainWindow fmainwindow;

	private Usecase fUsecase;

	private SequenceUC fSequenceUC;

	private UCProperties properties;

	private Browser fMBrowser;

	private FrslModel fModel;

	private JSplitPane sp;

	private TreeSequence treeSequence;


	public SequenceUCView(MainWindow mainWindow, Usecase usecase, FrslModel model) {
		this.fmainwindow = mainWindow;
		this.fUsecase = usecase;
		this.fModel = model;
		properties = new UCProperties();
		treeSequence = new TreeSequence(usecase);

		initDiagram();
	}


	private void initDiagram() {
		fSequenceUC = new SequenceUC(this, fUsecase, properties, fmainwindow);
		

		this.setFocusable(true);
		setLayout(new BorderLayout());

		fMBrowser = new Browser(treeSequence);
		this.removeAll();
		Component view = new JScrollPane(fSequenceUC);
		sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp.add(view);

		add(sp, BorderLayout.CENTER);
	}

	/*
	 * ----------------------------------------
	 * 
	 * change layout JPanel when select show browser.
	 * 
	 * ----------------------------------------
	 */
	public void changeLayout() {
		if (properties.getShowBrowser()) {
			sp.add(fMBrowser);
			sp.setDividerLocation(800);
		} else {
			sp.remove(fMBrowser);
		}
	}


	public FrslModel getfModel() {
		return fModel;
	}
	
    @Override
	public float getContentHeight() {
		return this.getPreferredSize().height;
	}

	@Override
	public float getContentWidth() {
		return this.getPreferredSize().width;
	}

    public void printView(PageFormat pf) {

	}


    public void detachModel() {
		
	}
	
    @Override
	public void export(Graphics2D g) {
		
	}
}
