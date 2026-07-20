package org.vnu.sme.goal.acl.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
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

public final class AclDiagramEdge extends EdgeBase {
    private static final Font EDGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private final AclEdge edge;
    private final AclDiagramOptions opt;

    public AclDiagramEdge(AclEdge edge, PlaceableNode source, PlaceableNode target, AclDiagramOptions opt) {
        super(source, target, edge.kind().name().toLowerCase(), opt, false);
        this.edge = edge;
        this.opt = opt;
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : color());
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
                    drawEnd(g2, p1, p2);
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

    @Override
    public boolean isLink() {
        return false;
    }

    @Override
    protected String getIdInternal() {
        return "acl::" + edge.kind().name() + "::" + edge.fromId() + "::" + edge.toId() + "::" + edge.label();
    }

    @Override
    protected String getStoreType() {
        return "AclEdge";
    }

    @Override
    public Direction getReflexivePosition() {
        return reflexivePosition == null ? Direction.NORTH_EAST : reflexivePosition;
    }

    private void drawEnd(Graphics2D g, Point2D p1, Point2D p2) {
        int x1 = (int) Math.round(p1.getX()), y1 = (int) Math.round(p1.getY());
        int x2 = (int) Math.round(p2.getX()), y2 = (int) Math.round(p2.getY());
        g.drawLine(x1, y1, x2, y2);
        if (edge.kind() == AclEdgeKind.SPECIALIZES) {
            DirectedEdgeFactory.drawArrow(g, x1, y1, x2, y2, ArrowStyle.OPEN);
        } else if (edge.kind() == AclEdgeKind.LINK) {
            DirectedEdgeFactory.drawArrow(g, x1, y1, x2, y2, ArrowStyle.FILLED);
        }
    }

    private Color color() {
        return switch (edge.kind()) {
            case PART_OF -> opt.getColor(AclDiagramOptions.PART_OF_COLOR);
            case LINK -> opt.getColor(AclDiagramOptions.LINK_COLOR);
            case GROUP_MEMBER -> opt.getColor(AclDiagramOptions.MEMBER_COLOR);
            default -> opt.getEDGE_COLOR();
        };
    }

    private Stroke stroke() {
        return switch (edge.kind()) {
            case GROUP_MEMBER, LINK -> new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[] {6f, 4f}, 0f);
            case SPECIALIZES -> new BasicStroke(1.4f);
            default -> new BasicStroke(1.4f);
        };
    }

    private void paintLabel(Graphics2D g, Point2D first, Point2D last) {
        if (edge.label() == null || first == null || last == null) return;
        int x = (int) Math.round((first.getX() + last.getX()) / 2.0);
        int y = (int) Math.round((first.getY() + last.getY()) / 2.0);
        g.setFont(EDGE_FONT);
        FontMetrics fm = g.getFontMetrics();
        String text = edge.label();
        g.setColor(new Color(255, 255, 255, 225));
        g.fillRoundRect(x - fm.stringWidth(text) / 2 - 4, y - 14, fm.stringWidth(text) + 8, 16, 6, 6);
        g.setColor(color());
        g.drawString(text, x - fm.stringWidth(text) / 2, y - 3);
    }
}
