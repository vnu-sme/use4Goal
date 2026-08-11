package org.vnu.sme.frsl;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.tzi.use.main.Session;

import org.tzi.use.uml.sys.MSystemException;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.parser.FRSLCompiler;
import org.vnu.sme.frsl.view.FRSL2UCD.UseCaseDiagramView;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;

public class FrslLoader {
	private Session session;
	private String frslFileName;
	private PrintWriter logWriter;
	private MainWindow mainWindow;
	private UseCaseDiagramView cdv;
	private FrslModel frslModel;

	public FrslLoader(Session session, String frslFileName, PrintWriter logWriter, MainWindow parent) {
		this.session = session;
		this.frslFileName = frslFileName;
		this.logWriter = logWriter;
		this.mainWindow = parent;
	}

	public boolean run() {
		logWriter.println("Compiling FrslModel ...");
		try {
			this.frslModel = FRSLCompiler.compileSpecification(frslFileName, logWriter, session.system().model());
		} catch (MSystemException e) {
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}

		if (this.frslModel == null) {
			logWriter.println("Error: FRSL compilation failed. Check the specification file.");
			logWriter.flush();
			return false;
		}

		try {
			cdv = new UseCaseDiagramView(mainWindow, this.frslModel);
			ViewFrame f = new ViewFrame("Use case diagram", cdv, "ClassDiagram.gif");
			JComponent c = (JComponent) f.getContentPane();
			c.setLayout(new BorderLayout());
			c.add(cdv, BorderLayout.CENTER);
			mainWindow.addNewViewFrame(f);
			mainWindow.getModelBrowser().setModel(session.system().model());
		} catch (Exception e) {
			logWriter.println("Error: Failed to open Use Case Diagram: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void again() {
		if (cdv == null) {
			logWriter.println("Error: No diagram loaded. Please Parse first.");
			logWriter.flush();
			return;
		}
		cdv.initDiagram();
		ViewFrame f = new ViewFrame("Use case diagram", cdv, "ClassDiagram.gif");
		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(cdv, BorderLayout.CENTER);
		mainWindow.addNewViewFrame(f);
		mainWindow.getModelBrowser().setModel(session.system().model());
	}
}
