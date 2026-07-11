package org.vnu.sme.goal.bpmn2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Iterator;

import org.tzi.use.gui.views.diagrams.edges.DirectedEdgeFactory;
import org.tzi.use.gui.views.diagrams.edges.DirectedEdgeFactory.ArrowStyle;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.util.Direction;
import org.tzi.use.gui.views.diagrams.waypoints.WayPoint;

public final class Bpmn2DiagramEdge extends EdgeBase {
    private static final Font EDGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private final Bpmn2Edge edge;

    public Bpmn2DiagramEdge(Bpmn2Edge edge, PlaceableNode source, PlaceableNode target, Bpmn2DiagramOptions opt) {
        super(source, target, edge.kind().name().toLowerCase(), opt, false);
        this.edge = edge;
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR()
                : edge.kind() == Bpmn2EdgeKind.MESSAGE ? fOpt.getColor(Bpmn2DiagramOptions.MESSAGE_COLOR) : fOpt.getEDGE_COLOR());
        g2.setStroke(stroke());
        Point2D first = null;
        Point2D last = null;
        if (!getWayPoints().isEmpty()) {
            Iterator<WayPoint> it = getWayPoints().iterator();
            WayPoint previous = it.next();
            first = previous.getCenter();
            previous.draw(g2);
            while (it.hasNext()) {
                WayPoint current = it.next();
                current.draw(g2);
                Point2D p1 = previous.getCenter();
                Point2D p2 = current.getCenter();
                if (!it.hasNext()) {
                    DirectedEdgeFactory.drawArrow(g2, (int) p1.getX(), (int) p1.getY(),
                            (int) p2.getX(), (int) p2.getY(), ArrowStyle.FILLED);
                    last = p2;
                } else {
                    g2.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
                }
                previous = current;
            }
        }
        paintLabel(g2, first, last);
        g2.dispose();
    }

    @Override public boolean isLink() { return false; }

    @Override protected String getIdInternal() {
        return "bpmn::" + edge.kind().name() + "::" + edge.fromId() + "::" + edge.toId();
    }

    @Override protected String getStoreType() { return "Bpmn2Edge"; }

    @Override public Direction getReflexivePosition() {
        return reflexivePosition == null ? Direction.NORTH_EAST : reflexivePosition;
    }

    private Stroke stroke() {
        if (edge.kind() == Bpmn2EdgeKind.MESSAGE) {
            return new BasicStroke(1.3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{7f, 5f}, 0f);
        }
        return new BasicStroke(1.4f);
    }

    private void paintLabel(Graphics2D g, Point2D first, Point2D last) {
        if (edge.label() == null || first == null || last == null) return;
        int x = (int) ((first.getX() + last.getX()) / 2.0);
        int y = (int) ((first.getY() + last.getY()) / 2.0);
        g.setFont(EDGE_FONT);
        g.setColor(Color.BLACK);
        g.drawString(edge.label(), x + 4, y - 4);
    }
}
