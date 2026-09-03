package org.vnu.sme.goal.dsl.bpmn.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.tzi.use.gui.views.diagrams.ToolTipProvider;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.vnu.sme.goal.gui.DiagramVisualStyle;

public final class BpmnDiagramNode extends PlaceableNode implements ToolTipProvider {
    public enum ContainerKind { NONE, POOL, LANE, PARTICIPANT }

    private final String id;
    private final String label;
    private final BpmnNode node;
    private final ContainerKind containerKind;
    private ScenarioState scenarioState = ScenarioState.NONE;
    private final List<String> scenarioDetails = new ArrayList<>();

    public BpmnDiagramNode(BpmnNode node, BpmnDiagramOptions opt, Font font) {
        this.id = node.id;
        this.label = node.label;
        this.node = node;
        this.containerKind = ContainerKind.NONE;
        this.scenarioState = node.scenarioState;
        this.scenarioDetails.addAll(node.scenarioDetails);
        init(opt, font, node.w, node.h);
    }

    public BpmnDiagramNode(String id, String label, ContainerKind kind, int w, int h,
                            BpmnDiagramOptions opt, Font font) {
        this.id = id;
        this.label = label;
        this.node = null;
        this.containerKind = kind;
        init(opt, font, w, h);
    }

    private void init(BpmnDiagramOptions opt, Font font, int w, int h) {
        setBackColor(Color.WHITE);
        setBackColorSelected(opt.getNODE_SELECTED_COLOR());
        setFrameColor(opt.getNODE_FRAME_COLOR());
        setTextColor(opt.getNODE_LABEL_COLOR());
        setFont(font);
        setMinWidth(Math.max(30, w));
        setMinHeight(Math.max(24, h));
        setRequiredWidth(id, Math.max(30, w));
        setRequiredHeight(id, Math.max(24, h));
    }

    @Override public String getId() { return id; }

    @Override public String name() { return id; }

    public ContainerKind containerKind() { return containerKind; }

    public boolean isContainer() { return containerKind != ContainerKind.NONE; }

    @Override public boolean isResizable() { return containerKind != ContainerKind.NONE || node == null || node.kind != BpmnNodeKind.START_EVT; }

    @Override public Area getArea() {
        return containerKind == ContainerKind.NONE ? new Area(shape()) : new Area(containerHitShape());
    }

    @Override public boolean occupies(double x, double y) {
        return containerKind == ContainerKind.NONE ? shape().contains(x, y) : containerHitShape().contains(x, y);
    }

    public void setScenarioState(ScenarioState state, List<String> details) {
        this.scenarioState = state == null ? ScenarioState.NONE : state;
        this.scenarioDetails.clear();
        if (details != null) this.scenarioDetails.addAll(details);
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (containerKind != ContainerKind.NONE) {
            drawContainer(g2);
            g2.dispose();
            return;
        }
        Shape s = shape();
        g2.setColor(fillColor());
        g2.fill(s);
        g2.setColor(getFrameColor());
        g2.setStroke(containerKind == ContainerKind.NONE ? DiagramVisualStyle.solid()
                : new BasicStroke(1.1f));
        g2.draw(s);
        paintMarker(g2);
        paintScenarioBadge(g2);
        g2.setColor(getTextColor());
        g2.setFont(getFont().deriveFont(Font.PLAIN, containerKind == ContainerKind.NONE ? 10f : 11f));
        drawCentered(g2, label, new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight()));
        g2.dispose();
    }

    @Override protected void doCalculateSize(Graphics2D g) { setCalculatedBounds(getWidth(), getHeight()); }

    @Override protected String getStoreType() { return "BpmnNode"; }

    @Override public String getToolTip(MouseEvent event) {
        StringBuilder tip = new StringBuilder("<html><b>").append(id).append("</b><br/>")
                .append(node == null ? containerKind : node.kind).append(": ").append(label);
        if (!scenarioDetails.isEmpty()) {
            tip.append("<br/><br/><b>Scenario</b>");
            for (String detail : scenarioDetails) tip.append("<br/>").append(detail);
        }
        return tip.append("</html>").toString();
    }

    private Color fillColor() {
        if (scenarioState == ScenarioState.ACTIVE) return new Color(224, 248, 231);
        if (scenarioState == ScenarioState.COMPLETED) return new Color(226, 236, 250);
        if (containerKind == ContainerKind.POOL || containerKind == ContainerKind.PARTICIPANT) return new Color(242, 245, 252);
        if (containerKind == ContainerKind.LANE) return new Color(252, 253, 255);
        return switch (node.kind) {
            case TASK, CALL_ACTIVITY -> new Color(255, 255, 235);
            case SUBPROCESS -> new Color(240, 248, 255);
            case GATEWAY -> new Color(255, 252, 235);
            case CHOREOGRAPHY -> new Color(245, 249, 255);
            default -> Color.WHITE;
        };
    }

    private Shape shape() {
        double x = getX(), y = getY(), w = getWidth(), h = getHeight();
        if (containerKind != ContainerKind.NONE) return new Rectangle2D.Double(x, y, w, h);
        return switch (node.kind) {
            case START_EVT, END_EVT, INT_EVT -> new Ellipse2D.Double(x, y, w, h);
            case GATEWAY -> diamond(x, y, w, h);
            default -> new RoundRectangle2D.Double(x, y, w, h, 10, 10);
        };
    }

    private Shape containerHitShape() {
        double x = getX(), y = getY(), w = getWidth(), h = getHeight();
        double header = containerKind == ContainerKind.LANE ? Math.min(52, w) : Math.min(28, w);
        return new Rectangle2D.Double(x, y, header, h);
    }

    private void drawContainer(Graphics2D g) {
        double x = getX(), y = getY(), w = getWidth(), h = getHeight();
        boolean lane = containerKind == ContainerKind.LANE;
        double header = lane ? Math.min(52, w) : Math.min(28, w);
        Color border = lane ? new Color(165, 178, 205) : new Color(128, 145, 185);
        Color headerFill = lane ? new Color(241, 246, 255) : new Color(228, 235, 250);

        g.setColor(new Color(255, 255, 255, 0));
        g.fill(new Rectangle2D.Double(x, y, w, h));
        g.setColor(headerFill);
        g.fill(new Rectangle2D.Double(x, y, header, h));
        g.setColor(border);
        g.setStroke(new BasicStroke(lane ? 1.0f : 1.4f));
        g.draw(new Rectangle2D.Double(x, y, w, h));
        g.draw(new Rectangle2D.Double(x, y, header, h));

        g.setColor(new Color(35, 46, 75));
        g.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        drawVerticalCentered(g, label, new Rectangle2D.Double(x, y, header, h));
    }

    private void paintScenarioBadge(Graphics2D g) {
        if (scenarioState == ScenarioState.NONE) return;
        String text = scenarioState == ScenarioState.ACTIVE ? "active" : "done";
        Font old = g.getFont();
        g.setFont(old.deriveFont(Font.BOLD, 9f));
        FontMetrics fm = g.getFontMetrics();
        int pad = 4;
        int bw = fm.stringWidth(text) + pad * 2;
        int bh = fm.getHeight() + 1;
        int bx = (int) Math.round(getX() + getWidth() - bw - 4);
        int by = (int) Math.round(getY() + 4);
        g.setColor(scenarioState == ScenarioState.ACTIVE ? new Color(58, 145, 82) : new Color(68, 105, 170));
        g.fillRoundRect(bx, by, bw, bh, 7, 7);
        g.setColor(Color.WHITE);
        g.drawString(text, bx + pad, by + fm.getAscent());
        g.setFont(old);
    }

    private void paintMarker(Graphics2D g) {
        if (node == null) return;
        double x = getX(), y = getY(), w = getWidth(), h = getHeight();
        if (node.kind == BpmnNodeKind.END_EVT) {
            g.setStroke(new BasicStroke(DiagramVisualStyle.STROKE_EMPHASIS));
            g.draw(new Ellipse2D.Double(x + 3, y + 3, w - 6, h - 6));
            paintEventIcon(g, true);
        } else if (node.kind == BpmnNodeKind.INT_EVT) {
            g.draw(new Ellipse2D.Double(x + 4, y + 4, w - 8, h - 8));
            paintEventIcon(g, !node.catching);
        } else if (node.kind == BpmnNodeKind.START_EVT) {
            paintEventIcon(g, false);
        } else if (node.kind == BpmnNodeKind.GATEWAY) {
            paintGatewayMarker(g);
        } else if (node.kind == BpmnNodeKind.SUBPROCESS) {
            g.drawRect((int) (x + w / 2 - 6), (int) (y + h - 13), 12, 8);
            g.drawLine((int) (x + w / 2), (int) (y + h - 13), (int) (x + w / 2), (int) (y + h - 5));
            g.drawLine((int) (x + w / 2 - 4), (int) (y + h - 9), (int) (x + w / 2 + 4), (int) (y + h - 9));
        } else if (node.kind == BpmnNodeKind.CALL_ACTIVITY) {
            g.setStroke(new BasicStroke(DiagramVisualStyle.STROKE_EMPHASIS));
            g.draw(new RoundRectangle2D.Double(x + 3, y + 3, w - 6, h - 6, 8, 8));
        } else if (node.kind == BpmnNodeKind.CHOREOGRAPHY) {
            g.drawLine((int) x, (int) (y + 14), (int) (x + w), (int) (y + 14));
            g.drawLine((int) x, (int) (y + h - 14), (int) (x + w), (int) (y + h - 14));
        }
    }

    private void paintEventIcon(Graphics2D g, boolean filled) {
        if (node.trigger == null || node.trigger == org.vnu.sme.goal.dsl.bpmn.mm.EventTrigger.NONE) return;
        int cx = (int) Math.round(getX() + getWidth() / 2.0);
        int cy = (int) Math.round(getY() + getHeight() / 2.0);
        int r = Math.max(3, (int) Math.round(getWidth() / 2.0) - 9);
        switch (node.trigger) {
            case MESSAGE -> {
                g.drawRect(cx - r, cy - r / 2, r * 2, r);
                g.drawLine(cx - r, cy - r / 2, cx, cy);
                g.drawLine(cx, cy, cx + r, cy - r / 2);
            }
            case TIMER -> {
                g.drawOval(cx - r, cy - r, r * 2, r * 2);
                g.drawLine(cx, cy, cx, cy - r + 2);
                g.drawLine(cx, cy, cx + r / 2, cy);
            }
            case ERROR -> {
                int[] xp = {cx - r / 2, cx - r / 5, cx + r / 5, cx + r / 2};
                int[] yp = {cy - r, cy, cy - r / 2, cy + r};
                if (filled) g.fillPolygon(xp, yp, 4); else g.drawPolyline(xp, yp, 4);
            }
            case SIGNAL -> {
                int[] xp = {cx, cx - r, cx + r};
                int[] yp = {cy - r, cy + r / 2, cy + r / 2};
                if (filled) g.fillPolygon(xp, yp, 3); else g.drawPolygon(xp, yp, 3);
            }
            case TERMINATE -> g.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
            case COMPENSATION -> {
                g.fillPolygon(new int[]{cx - 2, cx - 2 - r / 2, cx - 2 - r / 2},
                        new int[]{cy, cy - r / 2, cy + r / 2}, 3);
                g.fillPolygon(new int[]{cx + 2, cx + 2, cx + 2 + r / 2},
                        new int[]{cy - r / 2, cy + r / 2, cy}, 3);
            }
            case CONDITIONAL -> {
                g.drawRect(cx - r, cy - r, r * 2, r * 2);
                for (int i = 1; i <= 3; i++) {
                    int lineY = cy - r + i * r * 2 / 4;
                    g.drawLine(cx - r + 2, lineY, cx + r - 2, lineY);
                }
            }
            default -> {}
        }
    }

    private void paintGatewayMarker(Graphics2D g) {
        int cx = (int) Math.round(getX() + getWidth() / 2.0);
        int cy = (int) Math.round(getY() + getHeight() / 2.0);
        int sr = Math.max(4, (int) Math.round(getHeight() / 2.0) - 9);
        g.setStroke(new BasicStroke(2f));
        switch (node.gwKind == null ? org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind.XOR : node.gwKind) {
            case XOR -> {
                g.drawLine(cx - sr, cy - sr, cx + sr, cy + sr);
                g.drawLine(cx + sr, cy - sr, cx - sr, cy + sr);
            }
            case AND -> {
                g.drawLine(cx, cy - sr, cx, cy + sr);
                g.drawLine(cx - sr, cy, cx + sr, cy);
            }
            case OR -> g.drawOval(cx - sr, cy - sr, sr * 2, sr * 2);
            case EVENT_BASED -> {
                g.drawOval(cx - sr - 3, cy - sr - 3, (sr + 3) * 2, (sr + 3) * 2);
                for (int i = 0; i < 5; i++) {
                    double a1 = 2 * Math.PI * i / 5 - Math.PI / 2;
                    double a2 = 2 * Math.PI * (i + 1) / 5 - Math.PI / 2;
                    g.drawLine((int) (cx + sr * Math.cos(a1)), (int) (cy + sr * Math.sin(a1)),
                            (int) (cx + sr * Math.cos(a2)), (int) (cy + sr * Math.sin(a2)));
                }
            }
        }
    }

    private static Shape diamond(double x, double y, double w, double h) {
        Polygon p = new Polygon();
        p.addPoint((int) (x + w / 2), (int) y);
        p.addPoint((int) (x + w), (int) (y + h / 2));
        p.addPoint((int) (x + w / 2), (int) (y + h));
        p.addPoint((int) x, (int) (y + h / 2));
        Path2D.Double path = new Path2D.Double();
        path.moveTo(p.xpoints[0], p.ypoints[0]);
        for (int i = 1; i < p.npoints; i++) path.lineTo(p.xpoints[i], p.ypoints[i]);
        path.closePath();
        return path;
    }

    private static void drawCentered(Graphics2D g, String text, Rectangle2D bounds) {
        FontMetrics fm = g.getFontMetrics();
        String s = text == null ? "" : text;
        while (fm.stringWidth(s) > bounds.getWidth() - 10 && s.length() > 3) {
            s = s.substring(0, s.length() - 2) + ".";
        }
        float x = (float) (bounds.getX() + (bounds.getWidth() - fm.stringWidth(s)) / 2.0);
        float y = (float) (bounds.getY() + (bounds.getHeight() - fm.getHeight()) / 2.0 + fm.getAscent());
        g.drawString(s, x, y);
    }

    private static void drawVerticalCentered(Graphics2D g, String text, Rectangle2D bounds) {
        FontMetrics fm = g.getFontMetrics();
        String s = text == null ? "" : text;
        while (fm.stringWidth(s) > bounds.getHeight() - 12 && s.length() > 3) {
            s = s.substring(0, s.length() - 2) + ".";
        }
        Graphics2D g2 = (Graphics2D) g.create();
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();
        g2.rotate(-Math.PI / 2.0, cx, cy);
        float x = (float) (cx - fm.stringWidth(s) / 2.0);
        float y = (float) (cy + fm.getAscent() / 2.0 - 2);
        g2.drawString(s, x, y);
        g2.dispose();
    }

    public enum ScenarioState {
        NONE,
        COMPLETED,
        ACTIVE
    }
}
