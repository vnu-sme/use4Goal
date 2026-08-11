package org.vnu.sme.frsl.view.FRSL2SSD;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.frsl.mm.FRSLmodel.Action;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;
import org.vnu.sme.frsl.mm.FRSLmodel.ActorStep;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.RejoinStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Step;
import org.vnu.sme.frsl.mm.FRSLmodel.SystemStep;
import org.vnu.sme.frsl.mm.FRSLmodel.UCStep;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;
import org.vnu.sme.frsl.mm.SSDmodel.ActorLifeLine;
import org.vnu.sme.frsl.mm.SSDmodel.AltFrame;
import org.vnu.sme.frsl.mm.SSDmodel.FrameUC;
import org.vnu.sme.frsl.mm.SSDmodel.LifeLineUC;
import org.vnu.sme.frsl.mm.SSDmodel.LoopFrame;
import org.vnu.sme.frsl.mm.SSDmodel.MessageReturn;
import org.vnu.sme.frsl.mm.SSDmodel.MessageSync;
import org.vnu.sme.frsl.mm.SSDmodel.MessageSyncRef;
import org.vnu.sme.frsl.mm.SSDmodel.MessageSyncSelf;
import org.vnu.sme.frsl.mm.SSDmodel.MessageUC;
import org.vnu.sme.frsl.mm.SSDmodel.RefFrame;
import org.vnu.sme.frsl.mm.SSDmodel.SsdModel;
import org.vnu.sme.frsl.mm.SSDmodel.UsecaseLifeLine;
import org.vnu.sme.frsl.view.selection.SequenceSelection;

public class SequenceUC extends JPanel implements MouseListener {

	private int lastYValue = 0;
	public static final int OFFSET_LEFT_MARGIN_ACTOR_CENTER = 10;

	private SequenceUCView fParent;

	private Usecase fUsecase;

	private UCProperties properties;

	private MainWindow fMainWindow;

	private FrslModel model;

	private int numActor = 0;

	private SsdModel sssModel;

	private SSDProcess currentModel;

	private JPopupMenu fPopupMenu;

	private SequenceSelection fSelection;

	public SequenceUC(SequenceUCView fparent, Usecase usecase, UCProperties properties, MainWindow fMainWindow) {
		this.fParent = fparent;
		this.fUsecase = usecase;
		this.properties = properties;
		this.fMainWindow = fMainWindow;
		this.model = fparent.getfModel();
		sssModel = new SsdModel();
		fPopupMenu = new JPopupMenu();
		fSelection = new SequenceSelection(this, properties);

		addMouseListener(this);
		initState();

		setProperties();
		unionOfPopUpMenu();

	}

	private void initState() {
		currentModel = new SSDProcess(model, fUsecase, this);
		currentModel.initmodel();
		for (int i = 0; i < sssModel.getNumMess(); i++) {
			sssModel.getAllMessage().get(i).updateYPos();
			;
		}
	}

//TODO: DOCS
	public void actorAndUC2LifeLine() {

		sssModel.addLifeLine(new ActorLifeLine(this, properties, fUsecase.getPrimaryActor(), numActor));
		numActor++;
		Collection<Actor> seconds = fUsecase.getSecondaryActors();
		for (Actor secon : seconds) {

			sssModel.addLifeLine(new ActorLifeLine(this, properties, secon, numActor));
			numActor++;
		}
		sssModel.addLifeLine(new UsecaseLifeLine(this, properties, fUsecase, numActor));
		numActor++;
	}

	public AltFrame initAltFlow(Step step) {
		AltFrame newFrame = new AltFrame(this, properties, step.getName());
		sssModel.addFrame(newFrame);
		newFrame.createBaseFlow(this.getMessageUCLast());
		return newFrame;
	}

	public MessageUC getMessageUCLast() {
		return sssModel.getMessageUCLast();
	}

	public void step2MessageUC(Step step) {
		if (step instanceof ActorStep) {
			actorStep2Message((ActorStep) step);
		} else if (step instanceof SystemStep) {
			systemStep2Message((SystemStep) step);
		} else if (step instanceof UCStep) {
			ucStep2FrameUC((UCStep) step);
		} else {
			rejionStep2FrameUC((RejoinStep) step);
		}
	}

	private void actorStep2Message(ActorStep step) {
		LifeLineUC start = sssModel.getAllLifeLine().get(fUsecase.getPrimaryActor().getName());
		sssModel.addMessage(new MessageSyncSelf(step, this, properties, start));

	}

	private void systemStep2Message(SystemStep step) {
		Collection<Action> listAction = ((SystemStep) step).getActions();
		Map<String, MessageReturn> localMess = new TreeMap<>();

		if (listAction.size() == 0) {
			LifeLineUC start = sssModel.getAllLifeLine().get(fUsecase.getPrimaryActor().getName());
			LifeLineUC goal = sssModel.getAllLifeLine().get(fUsecase.getName());

			sssModel.addMessage(new MessageSync(step, this, properties, start, goal));
			sssModel.addMessage(new MessageReturn(step, this, properties, goal, start, null));
			return;
		}

		LifeLineUC goal = sssModel.getAllLifeLine().get(fUsecase.getName());
		for (Action ac : listAction) {
			LifeLineUC start = sssModel.getAllLifeLine().get(ac.getActor().getName());
			if (localMess.containsKey(start.getLifeName())) {
				localMess.get(start.getLifeName()).addActionMess(ac);
				continue;
			}
			sssModel.addMessage(new MessageSync(step, this, properties, start, goal));
			MessageReturn mess = new MessageReturn(step, this, properties, goal, start, ac);
			sssModel.addMessage(mess);
			localMess.put(start.getLifeName(), mess);

		}
		return;
	}

	private void rejionStep2FrameUC(RejoinStep step) {

		String rejionName = step.getRejoinTo().getName();
		LoopFrame frame = new LoopFrame(this, properties, step.getName());

		for (int i = sssModel.getAllMessage().size() - 1; i >= 0; i--) {
			MessageUC mes = sssModel.getAllMessage().get(i);
			frame.addFlow(mes);
			if (mes.getmName() == rejionName) {

				sssModel.addFrame(frame);
				frame.updatePosMess();

				return;
			}
		}
		return;
	}

	private void ucStep2FrameUC(UCStep step) {

		LifeLineUC start = sssModel.getAllLifeLine().get(fUsecase.getName());

		RefFrame frame = new RefFrame(this, properties, numActor, step.getIncludedUC());
		MessageSyncRef mess = new MessageSyncRef(step, this, properties, start, frame);

		frame.addOnlyOne(mess);
		sssModel.addMessage(mess);
		sssModel.addFrame(frame);
		frame.updatePosMess();

		return;

	}

	private void setProperties() {
		setBorder(BorderFactory.createEmptyBorder());
		setBackground(Color.white);
		setLayout(null);
		// setMinimumSize(new Dimension(50, 50));
		setPreferredSize(new Dimension(600, 600));

	}

	public synchronized void paint(Graphics g) {
		Font oldFont = g.getFont();
		// g.setFont(fProperties.getFont());
		super.paint(g);

		drawDiagram((Graphics2D) g);

		g.setFont(oldFont);
	}

	private synchronized void drawDiagram(Graphics2D g) {

		g.setColor(Color.BLACK);
		int maxX = 0;
		int maxY = properties.getFyLifeLineHeight();

		int y_height = lastYValue;

		if (y_height == 0) {
			y_height = 20;
		}
		for (LifeLineUC lifeline : sssModel.getAllLifeLine().values()) {
			lifeline.draw(g);
			maxX = Math.max(maxX, lifeline.getMaxXLifeLine());
		}

		for (int i = 0; i < sssModel.getNumMess(); i++) {
			sssModel.getAllMessage().get(i).draw(g);

		}

		for (LifeLineUC lifeline : sssModel.getAllLifeLine().values()) {
			lifeline.drawDashLine(g);
		}

		for (FrameUC frame : sssModel.getAllFrame()) {
			frame.draw(g);
			maxX = Math.max(maxX, frame.getXPosEnd());
		}

		Dimension newDimension = new Dimension((int) maxX + 30, (int) maxY + 20);
		if (!newDimension.equals(this.getPreferredSize())) {
			this.setPreferredSize(newDimension);
			this.revalidate();
		}

	}

	public void hideOrShow(Boolean isShow) {
		fParent.changeLayout();
	}

	private void unionOfPopUpMenu() {
		int pos = 0;
		JMenu show = new JMenu("hide show");
		fPopupMenu.insert(show, pos++);
		fPopupMenu.insert(fSelection.getSubMenuHideModelBrower(), pos);
	}

	public void mouseClicked(MouseEvent e) {
		RefFrame frame = findFrameUC(e.getX(), e.getY());
		if (frame != null && e.getClickCount() == 2 && !e.isConsumed()) {
			e.isConsumed();
			frame.open(fMainWindow, model);
		}
	}

	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	public void mouseEntered(MouseEvent e) {
		// System.out.println("sequence entered");
	}

	public void mouseExited(MouseEvent e) {
		// System.out.println("sequence exited ");
	}

	public void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			fPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public RefFrame findFrameUC(double x, double y) {
		for (FrameUC frameUC : sssModel.getAllFrame()) {
			if (frameUC.isNation(x, y) && frameUC instanceof RefFrame) {
				return (RefFrame) frameUC;
			}
		}
		return null;
	}

}
