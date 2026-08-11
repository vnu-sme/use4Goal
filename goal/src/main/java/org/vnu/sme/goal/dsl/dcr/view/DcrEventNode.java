package org.vnu.sme.goal.dsl.dcr.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.tzi.use.gui.views.diagrams.ToolTipProvider;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.vnu.sme.goal.dsl.dcr.mm.DcrEvent;
import org.vnu.sme.goal.dsl.dcr.mm.DcrMarking;

public final class DcrEventNode extends PlaceableNode implements ToolTipProvider {
    private static final int ARC = 12;
    private static final int HEADER_H = 28;
    private static final int MIN_W = 155;
    private static final int MIN_H = 96;

    private final DcrEvent event;

    public DcrEventNode(DcrEvent event, DcrDiagramOptions opt, Font baseFont) {
        this.event = event;
        setBackColor(Color.WHITE);
        setBackColorSelected(opt.getNODE_SELECTED_COLOR());
        setFrameColor(opt.getNODE_FRAME_COLOR());
        setTextColor(opt.getNODE_LABEL_COLOR());
        setFont(baseFont);
        setMinWidth(MIN_W);
        setMinHeight(MIN_H);
        setRequiredWidth("DCR_EVENT", MIN_W);
        setRequiredHeight("DCR_EVENT", MIN_H);
    }

    public DcrEvent event() {
        return event;
    }

    @Override
    public String getId() {
        return event.id();
    }

    @Override
    public String name() {
        return event.id();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public Area getArea() {
        return new Area(new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), ARC, ARC));
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Area area = getArea();

        g2.setColor(isSelected() ? getBackColorSelected() : getBackColor());
        g2.fill(area);

        Stroke oldStroke = g2.getStroke();
        if (!event.initialMarking().included()) {
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                    1f, new float[]{10f, 7f}, 0f));
        } else {
            g2.setStroke(new BasicStroke(2f));
        }
        g2.setColor(getFrameColor());
        g2.draw(area);
        g2.setStroke(oldStroke);

        g2.draw(new Rectangle2D.Double(getX(), getY() + HEADER_H, getWidth(), 0));
        g2.setColor(new Color(0, 120, 220));
        g2.setFont(getFont().deriveFont(Font.PLAIN, 10f));
        g2.drawString(markingText(), (float) getX() + 10, (float) getY() + 18);

        g2.setColor(getTextColor());
        g2.setFont(getFont().deriveFont(Font.PLAIN, 16f));
        drawCentered(g2, event.id() + ": " + event.label(),
                new Rectangle2D.Double(getX(), getY() + HEADER_H, getWidth(), getHeight() - HEADER_H));
        g2.dispose();
    }

    @Override
    protected void doCalculateSize(Graphics2D g) {
        FontMetrics fm = g.getFontMetrics(getFont().deriveFont(Font.PLAIN, 16f));
        double width = Math.max(MIN_W, fm.stringWidth(event.id() + ": " + event.label()) + 28);
        setCalculatedBounds(width, MIN_H);
    }

    @Override
    protected String getStoreType() {
        return "DcrEvent";
    }

    @Override
    public String getToolTip(MouseEvent event) {
        return "<html><b>" + this.event.id() + "</b>: " + this.event.label()
                + "<br/>" + markingText() + "</html>";
    }

    private String markingText() {
        DcrMarking marking = event.initialMarking();
        StringBuilder sb = new StringBuilder();
        if (marking.executed()) {
            sb.append("executed");
            if (marking.happenedAge() != null) sb.append(" ").append(marking.happenedAge());
            sb.append(" ");
        }
        sb.append(marking.included() ? "included " : "excluded ");
        if (marking.pending()) {
            sb.append("pending");
            if (marking.pendingDeadline() != null) sb.append(" ").append(formatTime(marking.pendingDeadline()));
        }
        return sb.toString().trim();
    }

    private static String formatTime(Integer value) {
        return value != null && value == DcrMarking.OMEGA ? "omega" : String.valueOf(value);
    }

    private static void drawCentered(Graphics2D g, String text, Rectangle2D bounds) {
        FontMetrics fm = g.getFontMetrics();
        float x = (float) (bounds.getX() + (bounds.getWidth() - fm.stringWidth(text)) / 2.0);
        float y = (float) (bounds.getY() + (bounds.getHeight() - fm.getHeight()) / 2.0 + fm.getAscent());
        g.drawString(text, x, y);
    }
}
