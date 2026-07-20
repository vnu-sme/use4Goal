package org.vnu.sme.goal.acl.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.util.Direction;
import org.tzi.use.gui.views.diagrams.waypoints.WayPoint;
import org.vnu.sme.goal.acl.mm.AclScope;

public final class AclDiagramEdge extends EdgeBase {
    private static final Font CARDINALITY_FONT = new Font(Font.SERIF, Font.ITALIC, 11);
    private static final double MARKER_LENGTH = 13.0;
    private static final double MARKER_HALF_WIDTH = 5.0;

    private final AclEdge edge;
    private final AclDiagramOptions opt;

    public AclDiagramEdge(AclEdge edge, PlaceableNode source,
                          PlaceableNode target, AclDiagramOptions opt) {
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
        Point2D startInside;
        Point2D endInside;
        if (edge.routeCount() > 1 && points.size() == 2 && start.distance(end) > 1.0) {
            Point2D control = controlPoint(start, end);
            g.draw(new QuadCurve2D.Double(start.getX(), start.getY(),
                    control.getX(), control.getY(), end.getX(), end.getY()));
            startInside = control;
            endInside = control;
        } else {
            for (int i = 1; i < points.size(); i++) {
                Point2D from = points.get(i - 1);
                Point2D to = points.get(i);
                g.drawLine((int) Math.round(from.getX()), (int) Math.round(from.getY()),
                        (int) Math.round(to.getX()), (int) Math.round(to.getY()));
            }
            startInside = points.get(1);
            endInside = points.get(points.size() - 2);
        }

        paintEndMarkers(g, start, startInside, endInside, end);
        paintExtendsSubgroupsScope(g, start, startInside);
        paintCardinality(g, start, startInside);
        paintRelationLabel(g, start, end);
        g.dispose();
    }

    private void paintEndMarkers(Graphics2D g, Point2D start, Point2D startInside,
                                 Point2D endInside, Point2D end) {
        if (edge.kind() == AclEdgeKind.INHERITANCE) {
            drawMarker(g, endInside, end, Marker.OPEN_TRIANGLE);
            return;
        }
        if (edge.kind().isComposition()) {
            drawMarker(g, endInside, end, Marker.OPEN_DIAMOND);
            return;
        }
        if (edge.kind().isRoleEntityRelation()) {
            drawMarker(g, endInside, end, Marker.OPEN_ARROW);
            return;
        }
        if (!edge.kind().isRoleLink()) return;

        Marker marker = switch (edge.kind()) {
            case ACQUAINTANCE -> Marker.OPEN_ARROW;
            case COMMUNICATION -> Marker.FILLED_CIRCLE;
            case AUTHORITY -> Marker.FILLED_TRIANGLE;
            case COMPATIBILITY -> Marker.FILLED_DIAMOND;
            default -> throw new IllegalStateException("Unexpected role-link kind: " + edge.kind());
        };
        drawMarker(g, endInside, end, marker);
        if (edge.bidirectional()) drawMarker(g, startInside, start, marker);
    }

    private void paintExtendsSubgroupsScope(Graphics2D g, Point2D start, Point2D inside) {
        if (!edge.extendsSubgroups()) return;
        Point2D center = pointToward(start, inside, edge.bidirectional() ? 20.0 : 12.0);
        g.fill(new Ellipse2D.Double(center.getX() - 3.5, center.getY() - 3.5, 7.0, 7.0));
    }

    private void paintCardinality(Graphics2D g, Point2D start, Point2D inside) {
        if (!edge.kind().isComposition() || edge.cardinality() == null || edge.cardinality().isBlank()) return;
        double dx = inside.getX() - start.getX();
        double dy = inside.getY() - start.getY();
        double length = Math.hypot(dx, dy);
        if (length < 0.01) return;
        double ux = dx / length;
        double uy = dy / length;
        Point2D anchor = new Point2D.Double(
                start.getX() + ux * 23.0 - uy * 12.0,
                start.getY() + uy * 23.0 + ux * 12.0);

        g.setFont(CARDINALITY_FONT);
        FontMetrics metrics = g.getFontMetrics();
        String text = edge.cardinality();
        int width = metrics.stringWidth(text);
        g.setColor(new Color(255, 255, 255, 235));
        g.fillRoundRect((int) Math.round(anchor.getX()) - 3,
                (int) Math.round(anchor.getY()) - metrics.getAscent() - 2,
                width + 6, metrics.getHeight() + 3, 5, 5);
        g.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : edgeColor());
        g.drawString(text, (float) anchor.getX(), (float) anchor.getY());
    }

    private void paintRelationLabel(Graphics2D g, Point2D start, Point2D end) {
        if (!edge.kind().isRoleEntityRelation() || edge.label() == null) return;
        g.setFont(CARDINALITY_FONT);
        g.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : edgeColor());
        double x = (start.getX() + end.getX()) / 2.0;
        double y = (start.getY() + end.getY()) / 2.0 - 5.0;
        g.drawString(edge.label(), (float) x, (float) y);
    }

    private Point2D controlPoint(Point2D start, Point2D end) {
        double dx = end.getX() - start.getX();
        double dy = end.getY() - start.getY();
        double length = Math.max(1.0, Math.hypot(dx, dy));
        double slot = edge.routeIndex() - (edge.routeCount() - 1) / 2.0;
        if (edge.fromId().compareTo(edge.toId()) > 0) slot = -slot;
        double offset = slot * 38.0;
        return new Point2D.Double(
                (start.getX() + end.getX()) / 2.0 - dy / length * offset,
                (start.getY() + end.getY()) / 2.0 + dx / length * offset);
    }

    private static Point2D pointToward(Point2D from, Point2D toward, double distance) {
        double dx = toward.getX() - from.getX();
        double dy = toward.getY() - from.getY();
        double length = Math.hypot(dx, dy);
        if (length < 0.01) return from;
        return new Point2D.Double(from.getX() + dx / length * distance,
                from.getY() + dy / length * distance);
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

        if (marker == Marker.FILLED_CIRCLE) {
            double cx = endpoint.getX() - ux * 4.0;
            double cy = endpoint.getY() - uy * 4.0;
            g.fill(new Ellipse2D.Double(cx - 4.0, cy - 4.0, 8.0, 8.0));
            return;
        }

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

        Path2D path = new Path2D.Double();
        if (marker == Marker.OPEN_DIAMOND || marker == Marker.FILLED_DIAMOND) {
            double centerX = endpoint.getX() - ux * MARKER_LENGTH / 2.0;
            double centerY = endpoint.getY() - uy * MARKER_LENGTH / 2.0;
            path.moveTo(endpoint.getX(), endpoint.getY());
            path.lineTo(centerX + px * MARKER_HALF_WIDTH, centerY + py * MARKER_HALF_WIDTH);
            path.lineTo(baseX, baseY);
            path.lineTo(centerX - px * MARKER_HALF_WIDTH, centerY - py * MARKER_HALF_WIDTH);
        } else {
            path.moveTo(endpoint.getX(), endpoint.getY());
            path.lineTo(baseX + px * MARKER_HALF_WIDTH, baseY + py * MARKER_HALF_WIDTH);
            path.lineTo(baseX - px * MARKER_HALF_WIDTH, baseY - py * MARKER_HALF_WIDTH);
        }
        path.closePath();

        if (marker == Marker.OPEN_TRIANGLE || marker == Marker.OPEN_DIAMOND) {
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
        return "acl::" + edge.kind().name() + "::" + edge.fromId() + "::" + edge.toId()
                + "::" + edge.routeIndex();
    }

    @Override
    protected String getStoreType() {
        return "AclEdge";
    }

    @Override
    public Direction getReflexivePosition() {
        return reflexivePosition == null ? Direction.NORTH_EAST : reflexivePosition;
    }

    private Color edgeColor() {
        return opt.getColor(AclDiagramOptions.MOISE_EDGE_COLOR);
    }

    private Stroke edgeStroke() {
        if ((edge.kind().isRoleLink() || edge.kind().isRoleEntityRelation()) && edge.scope() == AclScope.INTRA_GROUP) {
            return new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    10f, new float[] {2f, 5f}, 0f);
        }
        return new BasicStroke(edge.kind() == AclEdgeKind.GROUP_CARDINALITY ? 1.2f : 1.4f,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    private enum Marker {
        OPEN_ARROW,
        OPEN_TRIANGLE,
        FILLED_TRIANGLE,
        FILLED_CIRCLE,
        OPEN_DIAMOND,
        FILLED_DIAMOND
    }
}
