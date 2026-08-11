package org.vnu.sme.goal.dsl.aol.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.util.Direction;
import org.tzi.use.gui.views.diagrams.waypoints.WayPoint;

public final class AolDiagramEdge extends EdgeBase {
    private static final Font LABEL_FONT = new Font(Font.SERIF, Font.ITALIC, 11);
    private static final double MARKER_LENGTH = 13.0;
    private static final double MARKER_HALF_WIDTH = 5.0;

    private final AolEdge edge;
    private final AolDiagramOptions opt;

    public AolDiagramEdge(AolEdge edge, PlaceableNode source, PlaceableNode target, AolDiagramOptions opt) {
        super(source, target, edge.kind().name().toLowerCase(), opt, false);
        this.edge = edge;
        this.opt = opt;
    }

    @Override
    protected void onDraw(Graphics2D graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : edgeColor());
        g.setStroke(edgeStroke());

        List<Point2D> points = new ArrayList<>();
        for (WayPoint wayPoint : getWayPoints()) {
            wayPoint.draw(g);
            points.add(wayPoint.getCenter());
        }
        if (points.size() < 2) {
            g.dispose();
            return;
        }

        Point2D start = points.get(0);
        Point2D end = points.get(points.size() - 1);
        for (int i = 1; i < points.size(); i++) {
            Point2D from = points.get(i - 1);
            Point2D to = points.get(i);
            g.drawLine((int) Math.round(from.getX()), (int) Math.round(from.getY()),
                    (int) Math.round(to.getX()), (int) Math.round(to.getY()));
        }
        Point2D endInside = points.get(points.size() - 2);

        paintEndMarker(g, endInside, end);
        paintLabel(g, start, end);
        g.dispose();
    }

    private void paintEndMarker(Graphics2D g, Point2D inside, Point2D endpoint) {
        Marker marker = switch (edge.kind()) {
            case SUBGROUP_COMPOSITION, PLAY_COMPOSITION -> Marker.FILLED_DIAMOND;
            case ENTITY_AGGREGATION -> Marker.OPEN_DIAMOND;
            case PLAYED_BY, LINK -> Marker.OPEN_ARROW;
        };
        drawMarker(g, inside, endpoint, marker);
    }

    private void paintLabel(Graphics2D g, Point2D start, Point2D end) {
        if (edge.label() == null) return;
        g.setFont(LABEL_FONT);
        g.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : edgeColor());
        double x = (start.getX() + end.getX()) / 2.0;
        double y = (start.getY() + end.getY()) / 2.0 - 5.0;
        FontMetrics metrics = g.getFontMetrics();
        int width = metrics.stringWidth(edge.label());
        g.setColor(new Color(255, 255, 255, 235));
        g.fillRoundRect((int) x - 3, (int) y - metrics.getAscent() - 2, width + 6, metrics.getHeight() + 3, 5, 5);
        g.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : edgeColor());
        g.drawString(edge.label(), (float) x, (float) y);
    }

    private static void drawMarker(Graphics2D g, Point2D inside, Point2D endpoint, Marker marker) {
        double dx = endpoint.getX() - inside.getX();
        double dy = endpoint.getY() - inside.getY();
        double length = Math.hypot(dx, dy);
        if (length < 0.01) return;
        double ux = dx / length;
        double uy = dy / length;
        double px = -uy;
        double py = ux;

        double baseX = endpoint.getX() - ux * MARKER_LENGTH;
        double baseY = endpoint.getY() - uy * MARKER_LENGTH;

        if (marker == Marker.OPEN_ARROW) {
            Path2D path = new Path2D.Double();
            path.moveTo(baseX + px * MARKER_HALF_WIDTH, baseY + py * MARKER_HALF_WIDTH);
            path.lineTo(endpoint.getX(), endpoint.getY());
            path.lineTo(baseX - px * MARKER_HALF_WIDTH, baseY - py * MARKER_HALF_WIDTH);
            g.draw(path);
            return;
        }

        double centerX = endpoint.getX() - ux * MARKER_LENGTH / 2.0;
        double centerY = endpoint.getY() - uy * MARKER_LENGTH / 2.0;
        Path2D path = new Path2D.Double();
        path.moveTo(endpoint.getX(), endpoint.getY());
        path.lineTo(centerX + px * MARKER_HALF_WIDTH, centerY + py * MARKER_HALF_WIDTH);
        path.lineTo(baseX, baseY);
        path.lineTo(centerX - px * MARKER_HALF_WIDTH, centerY - py * MARKER_HALF_WIDTH);
        path.closePath();

        if (marker == Marker.OPEN_DIAMOND) {
            Color lineColor = g.getColor();
            g.setColor(Color.WHITE);
            g.fill(path);
            g.setColor(lineColor);
            g.draw(path);
        } else {
            g.fill(path);
        }
    }

    @Override
    public boolean isLink() {
        return false;
    }

    @Override
    protected String getIdInternal() {
        return "aol::" + edge.kind().name() + "::" + edge.fromId() + "::" + edge.toId();
    }

    @Override
    protected String getStoreType() {
        return "AolEdge";
    }

    @Override
    public Direction getReflexivePosition() {
        return reflexivePosition == null ? Direction.NORTH_EAST : reflexivePosition;
    }

    private Color edgeColor() {
        return opt.getColor(AolDiagramOptions.EDGE_COLOR);
    }

    private Stroke edgeStroke() {
        if (edge.kind() == AolEdgeKind.PLAYED_BY) {
            return new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    10f, new float[] {2f, 5f}, 0f);
        }
        return new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    private enum Marker {
        OPEN_ARROW,
        OPEN_DIAMOND,
        FILLED_DIAMOND
    }
}
