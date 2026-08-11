package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import org.tzi.use.gui.views.diagrams.Selectable;
import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public abstract class LifeLineUC implements Selectable{

    protected SequenceUC sequenceUC;

    protected UCProperties properties;

	protected String lifeName;

    protected int xPos;

	protected ArrayList<ActivationUC> listActivationUCs ;
	
    public LifeLineUC(SequenceUC sequenceUC, UCProperties properties) {
        this.sequenceUC = sequenceUC;
        this.properties = properties;
		listActivationUCs = new ArrayList<ActivationUC>();
    }


    public void draw (Graphics2D g) {
        FontMetrics fm = sequenceUC.getFontMetrics(properties.getFont());
        g.setColor(Color.black);

        drawLifeline(g, fm);
    }

	protected abstract void drawLifeline(Graphics2D g, FontMetrics fm);

	public void drawDashLine(Graphics2D g) {
        drawDashedLine(xPos,properties.yStart(), properties.getFyLifeLineHeight(), g);
		for (ActivationUC ac: listActivationUCs) {
			ac.draw(g);
		}
    }

    void drawDashedLine(int x, int y1, int y2, Graphics2D g) {
		Stroke oldStroke = g.getStroke();
		g.setStroke(properties.getDASHEDSTROKE());
		g.drawLine(x, y1, x, y2);
		g.setStroke(oldStroke);
	}

	public String getLifeName() {
		return lifeName;
	}

	public void setLifeName(String lifeName) {
		this.lifeName = lifeName;
	}

	public void insertActivation(MessageUC message) {
		if(listActivationUCs.size() == 0) {
			createActivation(message);
		} else {
			listActivationUCs.getLast().setMessEnd(message);
		}
	}

	public void createActivation(MessageUC message) {
		listActivationUCs.add(new ActivationUC(xPos, properties, message));
	}


    // selectable
    @Override
	public void setSelected(boolean on) {
		// fIsSelected = on;
	}

	@Override
	public boolean isSelected() {
		return false;
	}
    @Override
	public void setResizeAllowed(boolean allowed) {
		
	}
    @Override
	public boolean getResizeAllowed() {
		return false;
	}


	public abstract int getMaxXLifeLine() ;
}
