package org.vnu.sme.frsl.mm.SSDmodel;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.vnu.sme.frsl.view.FRSL2SSD.SequenceUC;
import org.vnu.sme.frsl.view.FRSL2SSD.UCProperties;

public abstract class FrameUC {

    protected FillBox fillTag;
    protected FillBox fillBody;

    private boolean canTag = false, canBody = false;

    private Color tagColor, bodyColor;

    private String nameType;

    protected String frameName;

    protected final Rectangle2D.Double frameBounds = new Rectangle2D.Double(0, 0, 100, 100);

    protected ArrayList<Flow> listFlow;

    protected SequenceUC sequenceUC;
    protected UCProperties properties;

    protected FrameUC(SequenceUC sequenceUC, UCProperties properties, String name, String type) {
        this.sequenceUC = sequenceUC;
        this.properties = properties;
        this.frameName = name;
        this.nameType = type;
        listFlow = new ArrayList<Flow>();
    }

    public void draw(Graphics2D g) {
        FontMetrics fm = sequenceUC.getFontMetrics(properties.getFont());

        g.setColor(Color.black);
        drawFrame(g, fm);
    }

    protected void drawFrame(Graphics2D g, FontMetrics fm) {
        caculateFrameBounds();

        drawTag(g, fm);
        colorFill(g);
        g.drawRect((int) frameBounds.getX(), (int) frameBounds.getY(), (int) frameBounds.width,
                (int) frameBounds.height);

        if (listFlow.size() == 1)
            return;
        Stroke oleStroke = g.getStroke();
        g.setStroke(properties.getDASHEDSTROKE());

        for (int i = 0; i < listFlow.size() - 1; i++) {
            double y = (listFlow.get(i).getMaxY() + listFlow.get(i + 1).getY()) / 2;
            g.drawLine((int) frameBounds.getX(), (int) y, (int) (frameBounds.width + frameBounds.getX()), (int) y);
        }
        g.setStroke(oleStroke);
    }

    private void colorFill(Graphics2D g) {
        if (canTag) {
            colorTag(g);
        }
        if (canBody) {
            colorBody(g);
        }
    }

    protected void colorTag(Graphics2D g) {
        Color oldColor = g.getColor();
        g.setColor(tagColor);
        fillTag.fillBox(g);
        g.setColor(oldColor);
    }

    protected void colorBody(Graphics2D g) {
        Color oldColor = g.getColor();
        g.setColor(bodyColor);
        fillBody.fillBox(g);
        g.setColor(oldColor);
    }

    protected void setColorTag(Color color) {
        canTag = true;
        tagColor = color;
    }

    protected void setColorBody(Color color) {
        canBody = true;
        bodyColor = color;
    }

    public void drawTag(Graphics2D g, FontMetrics fm) {
        int width = fm.stringWidth(nameType) + 4;
        int height = fm.getFont().getSize() + 2;

        fillBody = new FillBox();
        fillTag = new FillBox();
        // update size frame bounds
        frameBounds.y = frameBounds.y - height;
        frameBounds.height = frameBounds.height + height;

        int horizonXPos = (int) frameBounds.x + width + properties.getFrameTagLineIncline();
        int horizonYPos = (int) frameBounds.y + height;

        int verticaXPos = (int) frameBounds.x + width;
        int verticaYPos = (int) frameBounds.y + height + properties.getFrameTagLineIncline();

        g.drawLine(horizonXPos, (int) frameBounds.y, horizonXPos, horizonYPos);
        g.drawLine((int) frameBounds.x, verticaYPos, verticaXPos, verticaYPos);
        g.drawLine(horizonXPos, horizonYPos, verticaXPos, verticaYPos);

        fillTag.addList(horizonXPos, (int) frameBounds.y);
        fillTag.addList(horizonXPos, horizonYPos);
        fillTag.addList(verticaXPos, verticaYPos);
        fillTag.addList((int) frameBounds.x, verticaYPos);
        fillTag.addList((int) frameBounds.x, (int) frameBounds.y);

        fillBody.addList(horizonXPos, (int) frameBounds.y);
        fillBody.addList(horizonXPos, horizonYPos);
        fillBody.addList(verticaXPos, verticaYPos);
        fillBody.addList((int) frameBounds.x, verticaYPos);

        fillBody.addList((int) frameBounds.x, (int) (frameBounds.y + frameBounds.getHeight()));
        fillBody.addList((int) (frameBounds.x + frameBounds.getWidth()),
                (int) (frameBounds.y + frameBounds.getHeight()));
        fillBody.addList((int) (frameBounds.x + frameBounds.getWidth()), (int) frameBounds.y);

        g.drawString(nameType, (int) frameBounds.x + 2, verticaYPos - 3);
    }

    protected void caculateFrameBounds() {
        double maxX = Double.NEGATIVE_INFINITY;
        double minX = Double.POSITIVE_INFINITY;
        int w = properties.getFrameMarginVertical();

        for (Flow flow : listFlow) {
            flow.caculateFlowBounds();
            minX = Math.min(minX, flow.getX());
            maxX = Math.max(maxX, flow.getMaxX());
        }
        frameBounds.x = minX - w;
        frameBounds.y = listFlow.getFirst().getY();
        frameBounds.width = maxX - minX + 2 * w;
        frameBounds.height = listFlow.getLast().getMaxY() - listFlow.getFirst().getY();

    }

    public String getFrameName() {
        return frameName;
    }

    public int getXPosEnd() {
        return (int) frameBounds.getMaxX();
    }

    public void updatePosMess() {

        for (Flow flow : listFlow) {
            flow.calculatePaddingMess();
        }
        listFlow.getFirst().calculateTag();
    }

    public boolean isNation(double x, double y) {
        return getShape().contains(x, y);
    }

    public Shape getShape() {
        return (Shape) getBounds();
    }

    public Rectangle2D getBounds() {
        return frameBounds;
    }

    public double getYBound() {
        return frameBounds.getY();
    }

    public double getHeightBound() {
        return frameBounds.getY() + frameBounds.getHeight();
    }

    class Flow {
        protected final Rectangle2D.Double flowBounds = new Rectangle2D.Double(0, 0, 100, 100);

        private ArrayList<MessageUC> listMessage = new ArrayList<MessageUC>();
        private double minX = Double.POSITIVE_INFINITY;
        private double minY = Double.POSITIVE_INFINITY;
        private double maxX = Double.NEGATIVE_INFINITY;
        private double maxY = Double.NEGATIVE_INFINITY;

        public double getCenterX() {
            return flowBounds.getCenterX();
        }

        public double getCenterY() {
            return flowBounds.getCenterY();
        }

        public void clear() {
            listMessage.clear();
        }

        public void addListMessage(MessageUC mes) {
            listMessage.add(mes);
        }

        public void addLastMessage(MessageUC mes) {
            listMessage.addFirst(mes);
        }

        public Double getX() {
            return minX;
        }

        public Double getMaxX() {
            return maxX;
        }

        public Double getY() {
            return minY;
        }

        public Double getMaxY() {
            return maxY;
        }

        protected void caculateFlowBounds() {
            for (MessageUC mes : listMessage) {
                minX = Math.min(minX, mes.getLeftPos(FrameUC.this));
                maxX = Math.max(maxX, mes.getRightPos(FrameUC.this));
                minY = Math.min(minY, mes.getTopYPos(FrameUC.this));
                maxY = Math.max(maxY, mes.getBotYPos(FrameUC.this));
            }
            caculateCurrentBound(minX, minY, maxX, maxY);
        }

        protected void calculateTag() {
            MessageUC first = listMessage.getFirst();
            first.setMarginTop(properties.getFrameTagHeight());
        }

        protected void calculatePaddingMess() {
            MessageUC first = listMessage.getFirst();
            MessageUC end = listMessage.getLast();

            for (MessageUC mess : listMessage) {
                mess.setLevel(FrameUC.this);
            }
            // System.out.print( first.getmName() + " " + end.getmName() + " flow ");
            first.setMarginTop(properties.getFramePaddingTop());
            end.setMarginBottom(properties.getFramePaddingBottom());
        }

        protected void caculateCurrentBound(double x, double y, double maxX, double maxY) {
            // int h = properties.getFrameMarginHorizon();
            // int w = properties.getFrameMarginVertical();
            flowBounds.x = x;
            flowBounds.y = y;
            flowBounds.width = maxX - x;
            flowBounds.height = maxY - y;
        }
    }

    class FillBox {
        private ArrayList<Integer> listX = new ArrayList<>();
        private ArrayList<Integer> listY = new ArrayList<>();
        private int lenght = 0;

        public void addList(int x, int y) {
            listX.add(x);
            listY.add(y);
            lenght += 1;
        }

        public void fillBox(Graphics2D g) {
            int[] x = new int[lenght];
            x = change(listX);
            int[] y = new int[lenght];
            y = change(listY);

            g.fillPolygon(x, y, lenght);
        }

        private int[] change(ArrayList<Integer> list) {
            int[] listlist = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                listlist[i] = list.get(i);
            }
            return listlist;
        }
    }
}
