package org.vnu.sme.goal.dsl.dcr.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Iterator;

import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.edges.DirectedEdgeFactory;
import org.tzi.use.gui.views.diagrams.edges.DirectedEdgeFactory.ArrowStyle;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.util.Direction;
import org.tzi.use.gui.views.diagrams.waypoints.WayPoint;
import org.vnu.sme.goal.dsl.dcr.mm.DcrMarking;
import org.vnu.sme.goal.dsl.dcr.mm.DcrRelation;
import org.vnu.sme.goal.dsl.dcr.mm.DcrRelationKind;

public final class DcrRelationEdge extends EdgeBase {
    private final DcrRelation relation;
    private static final Font EDGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    public DcrRelationEdge(DcrRelation relation, PlaceableNode source, PlaceableNode target,
                           DcrDiagramOptions opt) {
        super(source, target, relation.kind().name().toLowerCase(), opt, false);
        this.relation = relation;
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(isSelected() ? fOpt.getEDGE_SELECTED_COLOR() : colorFor(relation.kind()));
        g2.setStroke(strokeFor(relation.kind()));

        Point2D last = null;
        if (!getWayPoints().isEmpty()) {
            Iterator<WayPoint> it = getWayPoints().iterator();
            WayPoint previous = it.next();
            previous.draw(g2);
            while (it.hasNext()) {
                WayPoint current = it.next();
                current.draw(g2);
                Point2D p1 = previous.getCenter();
                Point2D p2 = current.getCenter();
                if (!it.hasNext()) {
                    DirectedEdgeFactory.drawArrow(g2, (int) Math.round(p1.getX()), (int) Math.round(p1.getY()),
                            (int) Math.round(p2.getX()), (int) Math.round(p2.getY()), ArrowStyle.FILLED);
                    last = p2;
                } else {
                    g2.drawLine((int) Math.round(p1.getX()), (int) Math.round(p1.getY()),
                            (int) Math.round(p2.getX()), (int) Math.round(p2.getY()));
                }
                previous = current;
            }
        }

        if (last != null) {
            g2.setStroke(new BasicStroke(1.5f));
            g2.setFont(EDGE_FONT.deriveFont(Font.BOLD, 15f));
            g2.drawString(symbolFor(relation.kind()), (float) last.getX() - 22, (float) last.getY() - 6);
            paintTimeLabel(g2);
        }
        g2.dispose();
    }

    @Override
    public boolean isLink() {
        return false;
    }

    @Override
    protected String getIdInternal() {
        return "dcr::" + relation.kind().name().toLowerCase() + "::" + relation.source() + "::" + relation.target();
    }

    @Override
    protected String getStoreType() {
        return "DcrRelation";
    }

    @Override
    public Direction getReflexivePosition() {
        return reflexivePosition == null ? Direction.NORTH_EAST : reflexivePosition;
    }

    @Override
    public PlaceableNode findNode(double x, double y) {
        for (EdgeProperty property : getProperties()) {
            if (property.occupies(x, y)) return property;
        }
        return super.findNode(x, y);
    }

    private Color colorFor(DcrRelationKind kind) {
        return switch (kind) {
            case CONDITION -> fOpt.getColor(DcrDiagramOptions.CONDITION_COLOR);
            case RESPONSE -> fOpt.getColor(DcrDiagramOptions.RESPONSE_COLOR);
            case INCLUDE -> fOpt.getColor(DcrDiagramOptions.INCLUDE_COLOR);
            case EXCLUDE -> fOpt.getColor(DcrDiagramOptions.EXCLUDE_COLOR);
            case MILESTONE -> fOpt.getColor(DcrDiagramOptions.MILESTONE_COLOR);
        };
    }

    private Stroke strokeFor(DcrRelationKind kind) {
        if (kind == DcrRelationKind.MILESTONE) {
            return new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[]{7f, 5f}, 0f);
        }
        return new BasicStroke(1.8f);
    }

    private static String symbolFor(DcrRelationKind kind) {
        return switch (kind) {
            case CONDITION -> "|-";
            case RESPONSE -> "o";
            case INCLUDE -> "+";
            case EXCLUDE -> "%";
            case MILESTONE -> "<>";
        };
    }

    private void paintTimeLabel(Graphics2D g) {
        if (relation.time() == null || getWayPoints().isEmpty()) return;
        Point2D source = getWayPoints().get(0).getCenter();
        Point2D target = getWayPoints().get(getWayPoints().size() - 1).getCenter();
        String text = relation.kind() == DcrRelationKind.CONDITION
                ? "after " + relation.time()
                : "within " + formatTime(relation.time());
        int x = (int) Math.round((source.getX() + target.getX()) / 2.0);
        int y = (int) Math.round((source.getY() + target.getY()) / 2.0);
        g.setFont(EDGE_FONT.deriveFont(Font.PLAIN, 10f));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.fillRoundRect(x - fm.stringWidth(text) / 2 - 4, y - 15, fm.stringWidth(text) + 8, 17, 6, 6);
        g.setColor(fOpt.getColor(DiagramOptions.EDGE_LABEL_COLOR));
        g.drawString(text, x - fm.stringWidth(text) / 2, y - 3);
    }

    private static String formatTime(Integer value) {
        return value != null && value == DcrMarking.OMEGA ? "omega" : String.valueOf(value);
    }
}
