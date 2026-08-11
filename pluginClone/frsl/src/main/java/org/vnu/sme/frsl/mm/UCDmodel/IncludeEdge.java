package org.vnu.sme.frsl.mm.UCDmodel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.awt.geom.Point2D;

import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.edges.DirectedEdgeFactory;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.waypoints.WayPoint;

public class IncludeEdge extends EdgeBase{
        
    protected boolean isDashed;

    public IncludeEdge(PlaceableNode source, PlaceableNode target, String name, DiagramView diagram , boolean isDashed) {
        super(source, target, name, diagram.getOptions(), true);
        this.isDashed = isDashed;
    }


    @Override
    protected void onDraw( Graphics2D g ) {
        if ( isSelected() ) {
            g.setColor( fOpt.getEDGE_SELECTED_COLOR() );
        } else {
            g.setColor( fOpt.getEDGE_COLOR() );
        }
        
        drawEdge( g );
    }

    private void drawEdge( Graphics2D g ) {
        EdgeProperty n1 = null;
        Point2D p1 = null;        
        WayPoint n2 = null;
        Point2D p2 = null;

        // draw all line segments
        
        if ( !fWayPoints.isEmpty() ) {
        	Iterator<WayPoint> it = fWayPoints.iterator();
        	
            if ( it.hasNext() ) {
                n1 = it.next();
                n1.draw( g );
            }
            
            while( it.hasNext() ) {
                n2 = it.next();
            }
            p1 = n1.getCenter();
            p2 = n2.getCenter();
                            
            // draw way points
            n2.draw( g );
            
            try {
                drawAssociationKind(g, p1, p2);
                n1 = n2;
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }

            int width = 10;
            int xCenter = (int) (p1.getX() + p2.getX() ) /2 ;
            int yCenter = (int) (p1.getY() + p2.getY()) / 2 ;
            g.drawString("<< extend >>", xCenter - width , yCenter - 5);
            
        }
    }

    

    protected void drawAssociationKind( Graphics g, Point2D p2d1, Point2D p2d2 ) {
        
    	Point p1 = new Point((int)Math.round(p2d1.getX()), (int)Math.round(p2d1.getY()));
    	Point p2 = new Point((int)Math.round(p2d2.getX()), (int)Math.round(p2d2.getY()));
    	
        try {
            DirectedEdgeFactory.drawDirectedEdge( g,p2.x, p2.y, p1.x, p1.y,  isDashed );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

    @Override
    protected String getStoreType() {
        return "Artor use case";
    }

    @Override
	public boolean isLink() { return false; }

	@Override
	protected String getIdInternal() {
        // no override
        return "";
		// return (isLink() ? link.toString() : fAssoc.name());
	}
}
