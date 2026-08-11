package org.vnu.sme.frsl.mm.UCDmodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeEvent;
import org.tzi.use.gui.main.ModelBrowserSorting.SortChangeListener;
import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.util.Util;
import org.vnu.sme.frsl.mm.FRSLmodel.ExtensionPoint;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;

public class UsecaseNode extends UseNode implements SortChangeListener {
    
    private String[] fExtendPoint;
    private Color color = null; 
    private Color[] fExtendColor;

    public UsecaseNode(Usecase us, DiagramOptions opt ) {
        super(us, opt);
        fExtendPoint = new String[us.getExtPoint().size()];
        fExtendColor = new Color[us.getExtPoint().size()];
        int i = 0;
        for (ExtensionPoint extPoint : us.getExtPoint().values()) {
            fExtendPoint[i] = extPoint.getName();
            i++;
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor (Color color) {
        this.color = color;
    }

    @Override
    protected void onDraw( Graphics2D g ) {
    	Rectangle2D.Double currentBounds = getRoundedBounds();
    	
    	if (g.getClipBounds() != null && 
    	    !Util.enlargeRectangle(currentBounds, 20).intersects(g.getClipBounds())) {
    		return;
    	}
                
        FontMetrics fm; 
        
        Font oldFont = g.getFont();
        g.setFont( oldFont.deriveFont( Font.ITALIC ) );
        
        fm = g.getFontMetrics();
        
        int labelWidth = fm.stringWidth( fLabel );
        
        if ( isSelected() ) {
            g.setColor( fOpt.getNODE_SELECTED_COLOR() );
        } else {
        	if (getColor() != null)
        		g.setColor( getColor() );
        	else
        		g.setColor( fOpt.getNODE_COLOR() );
        }
        
        g.fillOval( (int) currentBounds.x ,(int) currentBounds.y
        ,(int) currentBounds.width , (int)currentBounds.height );
        double x = getCenter().getX();
        double y = getCenter().getY();

        

        
        g.setColor( fOpt.getNODE_LABEL_COLOR() );
        // We know that the name fits, because we require this size
        if (hasExtend()) {
            Line2D.Double lineAttrDivider = new Line2D.Double(currentBounds.getX(), y, currentBounds.getMaxX(), y);

            Rectangle2D.Double extensionBound = new Rectangle2D.Double( x - widthExtendList(fm) , y, widthExtendList(fm) * 2 , currentBounds.height);
            extensionBound.height = currentBounds.getMaxY() - y - VERTICAL_INDENT - Util.getLineHeight(fm);
            g.draw(lineAttrDivider);

            g.drawString( fLabel, Math.round(x - labelWidth / 2), Math.round((y + currentBounds.y) /2 ));

            y = drawCompartment(g, (int) y, fExtendPoint, fExtendColor, extensionBound);
        } else {
            g.drawString( fLabel, Math.round(x - labelWidth / 2), Math.round(y + HORIZONTAL_INDENT));
        }
        // resetting font and fontMetrics if the class was abstract
        g.setFont( oldFont );
        fm = g.getFontMetrics();
        
    }

    private int widthExtendList(FontMetrics fm) {
        int num = 0;
        for (String extend : fExtendPoint) {
            if (fm.stringWidth(extend) > num) {
                num = fm.stringWidth(extend);
            }
        }
        return num / 2 + 10;
    }


    public Usecase Usecase() {
        return fUsecase;
    }
    // placenode
    @Override
	public boolean isResizable() {
		return true;
	}

    // usenode
    @Override
    protected void calculateNameRectSize(Graphics2D g, Rectangle2D.Double rect) {
        Font classNameFont;
        
        classNameFont = g.getFont().deriveFont( Font.ITALIC );
      
        FontMetrics classNameFontMetrics = g.getFontMetrics( classNameFont );
        rect.width = classNameFontMetrics.stringWidth( fLabel );
		rect.height = classNameFontMetrics.getDescent()
				+ classNameFontMetrics.getAscent() + (2 * VERTICAL_INDENT);
		// At least the class name should be visible
		this.setRequiredHeight("CLASSNODE", rect.height);
		this.setRequiredWidth("CLASSNODE", rect.width + (2 * HORIZONTAL_INDENT));
    }

    @Override
    protected void calculateExtendPoint(Graphics2D g, Double rect) {
        calculateCompartmentRectSize(g, rect, fExtendPoint);
    }
    

    @Override
	public void stateChanged( SortChangeEvent e ) {
    }

    @Override
    protected boolean hasExtend() {
        // TODO Auto-generated method stub
        return fExtendPoint.length != 0;
    }

}
