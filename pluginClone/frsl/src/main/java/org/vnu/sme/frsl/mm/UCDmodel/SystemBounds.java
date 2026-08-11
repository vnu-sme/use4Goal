package org.vnu.sme.frsl.mm.UCDmodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;

import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;


public class SystemBounds extends PlaceableNode {
    protected static final int HORIZONTAL_INDENT = 30;
	protected static final int VERTICAL_INDENT = 10;

    protected final Rectangle2D.Double currentBounds = new Rectangle2D.Double(0,0,100,100);

    protected DiagramOptions fOpt;
    protected String label = "system";

    protected Map<String, UsecaseNode> usecase;


    public SystemBounds(DiagramOptions opt, Map<String, UsecaseNode> list) {
        this.fOpt = opt;
        usecase = new TreeMap<String, UsecaseNode>(list);
    }

    private void calculateBounds() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY; 
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (UsecaseNode us : usecase.values()) {
            minX = Math.min(us.getX(), minX);
            minY = Math.min(us.getY(), minY);
            maxX = Math.max(us.getX() + us.getWidth(), maxX);
            maxY = Math.max(us.getY() + us.getHeight(), maxY);
        }
       setCalculateCurrentBounds(minX, minY, maxX, maxY );
       
    }

    private void setCalculateCurrentBounds (double x, double y, double w, double h) {
        currentBounds.x = x - VERTICAL_INDENT;
        currentBounds.y = y - HORIZONTAL_INDENT;
        currentBounds.width = w - 2*x + 4 * VERTICAL_INDENT;
        currentBounds.height = h - 2*y + 4 * HORIZONTAL_INDENT;
    }

    @Override
    protected void doCalculateSize(Graphics2D g) {
        
        calculateBounds();
    }

    public void doSomething() {
        calculateBounds();
    }

    public Rectangle2D.Double getCurrentDouble () {
        return currentBounds;
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Rectangle2D.Double currentBounds = getCurrentDouble();

        int y;
        FontMetrics fm;
        Font oldFont = g.getFont();
        g.setFont(oldFont.deriveFont(Font.ITALIC));
        fm = g.getFontMetrics();

        int labelWidth = fm.stringWidth( label );

     
        // g.fill(currentBounds);
        double x = currentBounds.x + currentBounds.getMaxX() /2.0;
        x -=  labelWidth/2;
        y = (int)currentBounds.getY() + fm.getAscent() + VERTICAL_INDENT;
        
        g.drawString(label, Math.round(x), Math.round(y));
        g.setColor( Color.BLUE);

        g.drawRect( (int) currentBounds.getX(), (int) currentBounds.getY(), (int) currentBounds.getMaxX(),(int) currentBounds.getMaxY());

    }

    public void addListUsecase (UsecaseNode us) {
        usecase.put(us.fLabel, us);
    }


    @Override
    protected String getStoreType() {
    	return "Usecase";
    }


    @Override
	public String name() {
		return this.label;
	}

    @Override
    public String getId() {
        return name();
    }
}
