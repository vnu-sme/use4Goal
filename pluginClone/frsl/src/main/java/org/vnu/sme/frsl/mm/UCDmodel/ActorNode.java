package org.vnu.sme.frsl.mm.UCDmodel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.behavior.DrawingUtil;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.vnu.sme.frsl.mm.FRSLmodel.Actor;


public class ActorNode extends PlaceableNode{
    private static final int MARGIN = 5;
	private DiagramOptions fOpt;
    private Actor fActor;
    private String fLabel;

    public ActorNode(Actor ac, DiagramOptions opt) {
        this.fOpt = opt;
        this.fActor = ac;
        this.fLabel = ac.getName().toString();
        
    }


    // placenode
    @Override
	protected void onDraw(Graphics2D g) {
		Color oldColor = g.getColor();

		Rectangle2D currentBounds = getBounds();
		FontMetrics fm = g.getFontMetrics();

		g.setColor(new Color(255, 255, 255, 1));
		g.fill(currentBounds);
		g.draw(currentBounds);

		int x = (int) currentBounds.getCenterX();
		int y = (int) currentBounds.getY();

		if (isSelected()) {
			g.setColor(fOpt.getNODE_SELECTED_COLOR());
		} else {
			g.setColor(fOpt.getEDGE_COLOR());
		}

		DrawingUtil.drawBigActor(x, y + MARGIN, g);
		g.drawString(fLabel, (int) (currentBounds.getCenterX() - fm.stringWidth(fLabel) / 2), (int) currentBounds.getMinY()
				+ DrawingUtil.TOTAL_HEIGHT_BIG + fm.getAscent() + 2 * MARGIN);
		g.setColor(oldColor);
	}

    @Override
	public void doCalculateSize(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();

		int labelWidth = fm.stringWidth(fLabel);
		int nameHeight = fm.getAscent();
		int maxWidth;

		if (labelWidth < DrawingUtil.ARMS_LENGTH_BIG) {
			maxWidth = DrawingUtil.ARMS_LENGTH_BIG;
		} else {
			maxWidth = labelWidth;
		}

		setCalculatedBounds(maxWidth + 2 * MARGIN, nameHeight + DrawingUtil.TOTAL_HEIGHT_BIG + 3 * MARGIN);
	}
    @Override
	public String name() {
		return fLabel;
	}
    @Override
	public String getId() {
        // no override
		return fLabel;
	}
    @Override
	protected String getStoreType() {
		return "User Node";
	}
}
