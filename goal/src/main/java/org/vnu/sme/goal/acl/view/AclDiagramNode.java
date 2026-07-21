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
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.tzi.use.gui.views.diagrams.ToolTipProvider;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;

public final class AclDiagramNode extends PlaceableNode implements ToolTipProvider {
    private static final int H_PAD = 10;
    private static final int V_PAD = 5;
    private static final int ROLE_HEADER = 40;
    private static final int ENTITY_HEADER = 48;
    private static final int GROUP_HEADER = 38;
    private static final int ENUM_HEADER = 48;
    private static final int GROUP_TAB_WIDTH = 30;
    private static final int GROUP_TAB_HEIGHT = 11;

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
        setMinHeight(Math.max(42, node.h));
        setRequiredWidth(node.id, Math.max(90, node.w));
        setRequiredHeight(node.id, Math.max(42, node.h));
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
    protected void onDraw(Graphics2D graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        Shape shape = shape();
        g.setColor(isSelected() ? getBackColorSelected() : fillColor());
        g.fill(shape);
        g.setColor(getFrameColor());
        g.setStroke(new BasicStroke(1.4f));
        g.draw(shape);

        switch (node.kind) {
            case ROLE -> drawRole(g);
            case GROUP -> drawGroup(g);
            case ENTITY -> drawEntity(g);
            case ENUM -> drawEnum(g);
        }
        g.dispose();
    }

    private void drawRole(Graphics2D g) {
        drawRoleIcon(g);
        Font nameFont = getFont().deriveFont(isAbstractRole() ? Font.BOLD | Font.ITALIC : Font.BOLD, 13f);
        g.setColor(getTextColor());
        g.setFont(nameFont);
        drawCentered(g, node.label, new Rectangle2D.Double(
                getX() + 22, getY() + V_PAD, getWidth() - 44, ROLE_HEADER - V_PAD * 2));

        if (node.details.isEmpty()) return;
        g.setColor(getFrameColor());
        g.draw(new Line2D.Double(getX() + 18, getY() + ROLE_HEADER,
                getX() + getWidth() - 18, getY() + ROLE_HEADER));
        drawDetails(g, ROLE_HEADER, 22);
    }

    /** Small person icon in the header; the rest of the Role component stays compact. */
    private void drawRoleIcon(Graphics2D g) {
        double x = getX() + 14, y = getY() + 10;
        g.setColor(getFrameColor());
        g.setStroke(new BasicStroke(1.3f));
        g.draw(new java.awt.geom.Ellipse2D.Double(x - 3, y, 6, 6));
        g.draw(new Line2D.Double(x, y + 6, x, y + 17));
        g.draw(new Line2D.Double(x - 6, y + 10, x + 6, y + 10));
        g.draw(new Line2D.Double(x, y + 17, x - 5, y + 23));
        g.draw(new Line2D.Double(x, y + 17, x + 5, y + 23));
    }

    private void drawGroup(Graphics2D g) {
        g.setColor(getTextColor());
        g.setFont(getFont().deriveFont(Font.BOLD, 13f));
        drawCentered(g, node.label, new Rectangle2D.Double(
                getX() + H_PAD, getY() + GROUP_TAB_HEIGHT,
                getWidth() - H_PAD * 2, GROUP_HEADER - GROUP_TAB_HEIGHT));

        g.setColor(getFrameColor());
        g.draw(new Line2D.Double(getX(), getY() + GROUP_HEADER,
                getX() + getWidth(), getY() + GROUP_HEADER));
        drawDetails(g, GROUP_HEADER, H_PAD);
    }

    private void drawEntity(Graphics2D g) {
        g.setColor(getTextColor());
        g.setFont(getFont().deriveFont(Font.PLAIN, 10f));
        g.drawString("«entity»", (float) getX() + H_PAD,
                (float) getY() + V_PAD + g.getFontMetrics().getAscent());

        g.setFont(getFont().deriveFont(Font.BOLD, 13f));
        drawCentered(g, node.label, new Rectangle2D.Double(
                getX() + H_PAD, getY() + 16, getWidth() - H_PAD * 2, 26));

        g.setColor(getFrameColor());
        g.draw(new Line2D.Double(getX(), getY() + ENTITY_HEADER,
                getX() + getWidth(), getY() + ENTITY_HEADER));
        drawDetails(g, ENTITY_HEADER, H_PAD);
    }

    private void drawEnum(Graphics2D g) {
        g.setColor(getTextColor());
        g.setFont(getFont().deriveFont(Font.PLAIN, 10f));
        g.drawString("«enumeration»", (float) getX() + H_PAD,
                (float) getY() + V_PAD + g.getFontMetrics().getAscent());
        g.setFont(getFont().deriveFont(Font.BOLD, 13f));
        drawCentered(g, node.label, new Rectangle2D.Double(
                getX() + H_PAD, getY() + 16, getWidth() - H_PAD * 2, 26));
        g.setColor(getFrameColor());
        g.draw(new Line2D.Double(getX(), getY() + ENUM_HEADER,
                getX() + getWidth(), getY() + ENUM_HEADER));
        drawDetails(g, ENUM_HEADER, H_PAD);
    }

    private void drawDetails(Graphics2D g, int headerHeight, int horizontalPadding) {
        g.setColor(getTextColor());
        g.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        FontMetrics metrics = g.getFontMetrics();
        int y = (int) getY() + headerHeight + V_PAD + metrics.getAscent();
        for (String detail : node.details) {
            if (y > getY() + getHeight() - V_PAD) break;
            g.drawString(clip(detail, metrics, (int) getWidth() - horizontalPadding * 2),
                    (float) getX() + horizontalPadding, y);
            y += metrics.getHeight();
        }
    }

    @Override
    protected void doCalculateSize(Graphics2D g) {
        FontMetrics nameMetrics = g.getFontMetrics(getFont().deriveFont(Font.BOLD, 13f));
        FontMetrics detailMetrics = g.getFontMetrics(getFont().deriveFont(Font.PLAIN, 11f));
        int width = nameMetrics.stringWidth(node.label) + H_PAD * 4;
        for (String detail : node.details) {
            width = Math.max(width, detailMetrics.stringWidth(detail) + H_PAD * 4);
        }
        int height = switch (node.kind) {
            case ROLE -> ROLE_HEADER + (node.details.isEmpty() ? 0 : node.details.size() * detailMetrics.getHeight() + 10);
            case GROUP -> GROUP_HEADER
                    + Math.max(1, node.details.size()) * detailMetrics.getHeight() + 10;
            case ENTITY -> ENTITY_HEADER + Math.max(1, node.details.size()) * detailMetrics.getHeight() + 10;
            case ENUM -> ENUM_HEADER + Math.max(1, node.details.size()) * detailMetrics.getHeight() + 10;
        };
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
        return switch (node.kind) {
            case ROLE -> {
                double arc = Math.min(56.0, getHeight());
                yield new RoundRectangle2D.Double(getX(), getY(), getWidth(), getHeight(), arc, arc);
            }
            case GROUP -> groupShape();
            case ENTITY -> new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
            case ENUM -> new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
        };
    }

    private Shape groupShape() {
        Path2D path = new Path2D.Double();
        path.moveTo(getX(), getY());
        path.lineTo(getX() + GROUP_TAB_WIDTH, getY());
        path.lineTo(getX() + GROUP_TAB_WIDTH, getY() + GROUP_TAB_HEIGHT);
        path.lineTo(getX() + getWidth(), getY() + GROUP_TAB_HEIGHT);
        path.lineTo(getX() + getWidth(), getY() + getHeight());
        path.lineTo(getX(), getY() + getHeight());
        path.closePath();
        return path;
    }

    private Color fillColor() {
        return switch (node.kind) {
            case ENTITY -> opt.getColor(AclDiagramOptions.ENTITY_FILL);
            case ROLE -> opt.getColor(AclDiagramOptions.ROLE_FILL);
            case GROUP -> opt.getColor(AclDiagramOptions.GROUP_FILL);
            case ENUM -> opt.getColor(AclDiagramOptions.ENUM_FILL);
        };
    }

    private boolean isAbstractRole() {
        return node.subtitle.startsWith("abstract");
    }

    private static void drawCentered(Graphics2D g, String text, Rectangle2D bounds) {
        FontMetrics metrics = g.getFontMetrics();
        String clipped = clip(text, metrics, (int) bounds.getWidth());
        float x = (float) (bounds.getX() + (bounds.getWidth() - metrics.stringWidth(clipped)) / 2.0);
        float y = (float) (bounds.getY() + (bounds.getHeight() - metrics.getHeight()) / 2.0 + metrics.getAscent());
        g.drawString(clipped, x, y);
    }

    private static String clip(String text, FontMetrics metrics, int width) {
        if (metrics.stringWidth(text) <= width) return text;
        String ellipsis = "...";
        int length = text.length();
        while (length > 0 && metrics.stringWidth(text.substring(0, length) + ellipsis) > width) length--;
        return length <= 0 ? ellipsis : text.substring(0, length) + ellipsis;
    }
}
