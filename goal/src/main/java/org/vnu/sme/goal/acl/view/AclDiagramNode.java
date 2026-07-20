package org.vnu.sme.goal.acl.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.tzi.use.gui.views.diagrams.ToolTipProvider;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;

public final class AclDiagramNode extends PlaceableNode implements ToolTipProvider {
    private static final int H_PAD = 10;
    private static final int V_PAD = 5;
    private static final int HEADER_EXTRA = 18;

    private final AclNode node;
    private final AclDiagramOptions opt;

    public AclDiagramNode(AclNode node, AclDiagramOptions opt, Font font) {
        this.node = node;
        this.opt = opt;
        setBackColor(fillColor());
        setBackColorSelected(opt.getNODE_SELECTED_COLOR());
        setFrameColor(opt.getNODE_FRAME_COLOR());
        setTextColor(opt.getNODE_LABEL_COLOR());
        setFont(font);
        setMinWidth(Math.max(90, node.w));
        setMinHeight(Math.max(50, node.h));
        setRequiredWidth(node.id, Math.max(90, node.w));
        setRequiredHeight(node.id, Math.max(50, node.h));
    }

    public AclNode node() {
        return node;
    }

    @Override
    public String getId() {
        return node.id;
    }

    @Override
    public String name() {
        return node.id;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public Area getArea() {
        return new Area(shape());
    }

    @Override
    protected void onDraw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Shape shape = shape();
        g2.setColor(isSelected() ? getBackColorSelected() : fillColor());
        g2.fill(shape);

        g2.setColor(getTextColor());
        g2.setFont(getFont().deriveFont(Font.PLAIN, 10f));
        FontMetrics kindFm = g2.getFontMetrics();
        g2.drawString(node.subtitle, (float) getX() + H_PAD, (float) getY() + V_PAD + kindFm.getAscent());

        Font nameFont = getFont().deriveFont(node.subtitle.startsWith("abstract") ? Font.ITALIC : Font.BOLD, 13f);
        g2.setFont(nameFont);
        FontMetrics nameFm = g2.getFontMetrics();
        drawCentered(g2, node.label, new Rectangle2D.Double(getX() + H_PAD, getY() + 18, getWidth() - H_PAD * 2,
                nameFm.getHeight() + V_PAD));

        g2.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        FontMetrics detailFm = g2.getFontMetrics();
        int dividerY = (int) getY() + headerHeight(g2);
        g2.setColor(borderColor());
        g2.setStroke(stroke());
        g2.draw(new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight()));
        g2.draw(new Line2D.Double(getX(), dividerY, getX() + getWidth(), dividerY));

        g2.setColor(getTextColor());
        int y = dividerY + V_PAD + detailFm.getAscent();
        for (String detail : node.details) {
            if (y > getY() + getHeight() - V_PAD) break;
            g2.drawString(clip(detail, detailFm, (int) getWidth() - H_PAD * 2), (float) getX() + H_PAD, y);
            y += detailFm.getHeight();
        }
        g2.dispose();
    }

    @Override
    protected void doCalculateSize(Graphics2D g) {
        FontMetrics nameFm = g.getFontMetrics(getFont().deriveFont(Font.BOLD, 13f));
        FontMetrics detailFm = g.getFontMetrics(getFont().deriveFont(Font.PLAIN, 11f));
        int width = nameFm.stringWidth(node.label) + H_PAD * 2;
        width = Math.max(width, g.getFontMetrics(getFont().deriveFont(Font.PLAIN, 10f)).stringWidth(node.subtitle) + H_PAD * 2);
        for (String detail : node.details) {
            width = Math.max(width, detailFm.stringWidth(detail) + H_PAD * 2);
        }
        int height = headerHeight(g) + Math.max(detailFm.getHeight(), node.details.size() * detailFm.getHeight()) + V_PAD * 2;
        setCalculatedBounds(Math.max(node.w, width), Math.max(node.h, height));
    }

    @Override
    protected String getStoreType() {
        return "AclNode";
    }

    @Override
    public String getToolTip(MouseEvent event) {
        return "<html><b>" + node.label + "</b><br/>" + node.subtitle + "</html>";
    }

    private Shape shape() {
        return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }

    private Color fillColor() {
        return switch (node.kind) {
            case ENTITY -> opt.getColor(AclDiagramOptions.ENTITY_FILL);
            case ROLE -> opt.getColor(AclDiagramOptions.ROLE_FILL);
            case AGENT -> opt.getColor(AclDiagramOptions.AGENT_FILL);
            case GROUP -> opt.getColor(AclDiagramOptions.GROUP_FILL);
        };
    }

    private Color borderColor() {
        return switch (node.kind) {
            case GROUP -> new Color(154, 119, 34);
            default -> getFrameColor();
        };
    }

    private BasicStroke stroke() {
        if (node.subtitle.startsWith("abstract") || node.kind == AclNodeKind.GROUP) {
            return new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[] {7f, 4f}, 0f);
        }
        return new BasicStroke(1.5f);
    }

    private int headerHeight(Graphics2D g) {
        FontMetrics kindFm = g.getFontMetrics(getFont().deriveFont(Font.PLAIN, 10f));
        FontMetrics nameFm = g.getFontMetrics(getFont().deriveFont(Font.BOLD, 13f));
        return V_PAD * 2 + kindFm.getHeight() + nameFm.getHeight() + HEADER_EXTRA;
    }

    private static void drawCentered(Graphics2D g, String text, Rectangle2D bounds) {
        FontMetrics fm = g.getFontMetrics();
        String clipped = clip(text, fm, (int) bounds.getWidth());
        float x = (float) (bounds.getX() + (bounds.getWidth() - fm.stringWidth(clipped)) / 2.0);
        float y = (float) (bounds.getY() + (bounds.getHeight() - fm.getHeight()) / 2.0 + fm.getAscent());
        g.drawString(clipped, x, y);
    }

    private static String clip(String text, FontMetrics fm, int width) {
        if (fm.stringWidth(text) <= width) return text;
        String ellipsis = "...";
        int n = text.length();
        while (n > 0 && fm.stringWidth(text.substring(0, n) + ellipsis) > width) n--;
        return n <= 0 ? ellipsis : text.substring(0, n) + ellipsis;
    }
}
