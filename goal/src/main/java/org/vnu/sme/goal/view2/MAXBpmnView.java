package org.vnu.sme.goal.view2;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import org.tzi.use.gui.views.View;
import org.vnu.sme.goal.maxbpmn.mm.*;

/**
 * Renders a BpmnProcess as a swimlane BPMN diagram.
 * One horizontal lane per actor; nodes placed left-to-right by process order.
 * Sequence flows connect within/across lanes; message flows shown dashed.
 */
public final class MAXBpmnView extends JPanel implements View {

    // ── colours ───────────────────────────────────────────────────────────────
    private static final Color C_BG          = new Color(20,  22,  30);
    private static final Color C_POOL_HDR    = new Color(30,  40,  65);
    private static final Color C_LANE_EVEN   = new Color(26,  30,  45);
    private static final Color C_LANE_ODD    = new Color(22,  26,  40);
    private static final Color C_LANE_BDR    = new Color(55,  75, 130);
    private static final Color C_LANE_LABEL  = new Color(160, 185, 230);
    private static final Color C_START       = new Color( 50, 200,  80);
    private static final Color C_END         = new Color(220,  55,  55);
    private static final Color C_TASK_BG     = new Color( 40,  55,  90);
    private static final Color C_TASK_BDR    = new Color( 80, 130, 220);
    private static final Color C_TASK_FG     = new Color(210, 220, 240);
    private static final Color C_GW_XOR      = new Color(210,  80, 230);
    private static final Color C_GW_PAR      = new Color(255, 165,  50);
    private static final Color C_GW_IOR      = new Color( 50, 210, 180);
    private static final Color C_FLOW        = new Color(120, 150, 210);
    private static final Color C_MSG_FLOW    = new Color(220, 210,  50);
    private static final Color C_LABEL       = new Color(200, 220, 240);
    private static final Color C_COND_LABEL  = new Color(160, 175, 200);

    // ── layout ────────────────────────────────────────────────────────────────
    private static final int POOL_HDR_W = 28;
    private static final int LANE_LBL_W = 130;
    private static final int LANE_H     = 110;
    private static final int NODE_W     = 110;
    private static final int NODE_H     = 40;
    private static final int START_R    = 14;
    private static final int END_R      = 14;
    private static final int GW_S       = 28;
    private static final int H_GAP      = 38;
    private static final int MARGIN     = 24;

    private static final Font FN  = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private static final Font FL  = new Font(Font.SANS_SERIF, Font.BOLD,  11);
    private static final Font FC  = new Font(Font.SANS_SERIF, Font.ITALIC, 9);

    private BpmnProcess           process;
    private final Map<String,Rect> layout  = new LinkedHashMap<>();
    private final List<LaneBand>  bands   = new ArrayList<>();

    record LaneBand(String laneId, String label, int y, int h) {}
    record Rect(int x, int y, int w, int h) {
        int cx() { return x + w/2; }
        int cy() { return y + h/2; }
    }

    public MAXBpmnView() {
        setBackground(C_BG);
        setPreferredSize(new Dimension(1400, 600));
    }

    public void setProcess(BpmnProcess p) {
        this.process = p;
        buildLayout();
        revalidate();
        repaint();
    }

    // ── layout ───────────────────────────────────────────────────────────────

    private void buildLayout() {
        layout.clear();
        bands.clear();
        if (process == null) return;

        int laneY = MARGIN;
        for (Lane lane : process.getLanes()) {
            bands.add(new LaneBand(lane.id(), lane.actorName() + " «" + lane.actorKind() + "»", laneY, LANE_H));
            laneY += LANE_H;
        }

        // assign X positions per lane, nodes ordered by appearance
        Map<String, Integer> laneX = new HashMap<>();
        for (Lane l : process.getLanes()) laneX.put(l.id(), POOL_HDR_W + LANE_LBL_W + MARGIN);

        for (BpmnNode node : process.getNodes()) {
            LaneBand band = bandFor(node.laneId());
            if (band == null) continue;
            int x = laneX.getOrDefault(node.laneId(), 50);
            int cy = band.y() + band.h() / 2;

            switch (node) {
                case StartEvent s -> {
                    layout.put(s.id(), new Rect(x, cy - START_R, START_R*2, START_R*2));
                    laneX.put(node.laneId(), x + START_R*2 + H_GAP);
                }
                case EndEvent e -> {
                    layout.put(e.id(), new Rect(x, cy - END_R, END_R*2, END_R*2));
                    laneX.put(node.laneId(), x + END_R*2 + H_GAP);
                }
                case BpmnTask t -> {
                    layout.put(t.id(), new Rect(x, cy - NODE_H/2, NODE_W, NODE_H));
                    laneX.put(node.laneId(), x + NODE_W + H_GAP);
                }
                case Gateway g -> {
                    layout.put(g.id(), new Rect(x, cy - GW_S/2, GW_S, GW_S));
                    laneX.put(node.laneId(), x + GW_S + H_GAP);
                }
            }
        }

        int totalH = laneY + MARGIN;
        int totalW = laneX.values().stream().mapToInt(v -> v).max().orElse(800) + MARGIN;
        setPreferredSize(new Dimension(Math.max(totalW, 900), Math.max(totalH, 400)));
    }

    private LaneBand bandFor(String laneId) {
        return bands.stream().filter(b -> b.laneId().equals(laneId)).findFirst().orElse(null);
    }

    // ── paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        if (process == null) {
            g2.setColor(C_LANE_LABEL); g2.setFont(FL);
            g2.drawString("No BPMN process loaded.", 40, 60);
            return;
        }

        paintLanes(g2);
        paintSequenceFlows(g2);
        paintMessageFlows(g2);
        paintNodes(g2);
    }

    private void paintLanes(Graphics2D g2) {
        int totalW = getWidth();
        // pool header
        g2.setColor(C_POOL_HDR);
        int totalH = bands.isEmpty() ? 100 : bands.get(bands.size()-1).y() + bands.get(bands.size()-1).h() + MARGIN;
        g2.fillRect(MARGIN, MARGIN, POOL_HDR_W, totalH - 2*MARGIN);
        g2.setColor(C_LANE_BDR); g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(MARGIN, MARGIN, POOL_HDR_W, totalH - 2*MARGIN);

        // rotate process name in header
        Graphics2D gr = (Graphics2D) g2.create();
        gr.setColor(C_LANE_LABEL); gr.setFont(FL);
        gr.rotate(-Math.PI/2, MARGIN + POOL_HDR_W/2, (MARGIN + totalH)/2);
        String pName = process.getName();
        FontMetrics fm = gr.getFontMetrics();
        gr.drawString(pName, (MARGIN + totalH)/2 - fm.stringWidth(pName)/2, MARGIN + POOL_HDR_W/2 + 4);
        gr.dispose();

        for (int i = 0; i < bands.size(); i++) {
            LaneBand b = bands.get(i);
            Color bg = (i % 2 == 0) ? C_LANE_EVEN : C_LANE_ODD;
            int lx = MARGIN + POOL_HDR_W;
            g2.setColor(bg);
            g2.fillRect(lx, b.y(), totalW - lx - MARGIN, b.h());
            g2.setColor(C_LANE_BDR); g2.setStroke(new BasicStroke(1f));
            g2.drawRect(lx, b.y(), totalW - lx - MARGIN, b.h());

            // lane label box
            g2.setColor(C_POOL_HDR);
            g2.fillRect(lx, b.y(), LANE_LBL_W, b.h());
            g2.setColor(C_LANE_BDR);
            g2.drawRect(lx, b.y(), LANE_LBL_W, b.h());
            g2.setColor(C_LANE_LABEL); g2.setFont(FL);
            drawVertString(g2, b.label(), lx + LANE_LBL_W/2, b.y() + b.h()/2);
        }
    }

    private void drawVertString(Graphics2D g2, String s, int cx, int cy) {
        FontMetrics fm = g2.getFontMetrics();
        int sw = fm.stringWidth(s);
        Graphics2D gr = (Graphics2D) g2.create();
        gr.rotate(-Math.PI/2, cx, cy);
        gr.drawString(s, cx - sw/2, cy + 4);
        gr.dispose();
    }

    private void paintSequenceFlows(Graphics2D g2) {
        g2.setColor(C_FLOW); g2.setStroke(new BasicStroke(1.5f));
        for (SequenceFlow f : process.getFlows()) {
            Rect s = layout.get(f.sourceId());
            Rect t = layout.get(f.targetId());
            if (s == null || t == null) continue;

            boolean sameY = Math.abs(s.cy() - t.cy()) < 8;
            int x1, y1, x2, y2;
            if (sameY || s.cx() < t.cx()) {
                x1 = s.cx() < t.cx() ? s.x + s.w : s.x;
                y1 = s.cy();
                x2 = s.cx() < t.cx() ? t.x : t.x + t.w;
                y2 = t.cy();
            } else {
                x1 = s.cx(); y1 = s.y + s.h;
                x2 = t.cx(); y2 = t.y;
            }

            if (sameY || Math.abs(y1 - y2) < 8) {
                g2.drawLine(x1, y1, x2, y2);
            } else {
                int mx = (x1 + x2) / 2;
                g2.drawLine(x1, y1, mx, y1);
                g2.drawLine(mx, y1, mx, y2);
                g2.drawLine(mx, y2, x2, y2);
            }
            drawArrowHead(g2, x1, y1, x2, y2, C_FLOW);
            if (f.label() != null && !f.label().isBlank()) {
                g2.setFont(FC); g2.setColor(C_COND_LABEL);
                g2.drawString(truncLabel(f.label()), (x1+x2)/2 + 3, (y1+y2)/2 - 4);
                g2.setColor(C_FLOW);
            }
        }
    }

    private void paintMessageFlows(Graphics2D g2) {
        Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{8, 4}, 0);
        g2.setStroke(dashed); g2.setColor(C_MSG_FLOW);
        for (MessageFlow m : process.getMessages()) {
            Rect s = layout.get(m.sourceId());
            Rect t = layout.get(m.targetId());
            if (s == null || t == null) continue;
            g2.drawLine(s.cx(), s.cy(), t.cx(), t.cy());
            drawArrowHead(g2, s.cx(), s.cy(), t.cx(), t.cy(), C_MSG_FLOW);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void paintNodes(Graphics2D g2) {
        for (BpmnNode node : process.getNodes()) {
            Rect r = layout.get(node.id());
            if (r == null) continue;
            switch (node) {
                case StartEvent s -> paintStartEvent(g2, r);
                case EndEvent   e -> paintEndEvent(g2, r);
                case BpmnTask   t -> paintTask(g2, r, t.label());
                case Gateway    gw -> paintGateway(g2, r, gw);
            }
        }
    }

    private void paintStartEvent(Graphics2D g2, Rect r) {
        g2.setColor(C_START.darker().darker());
        g2.fillOval(r.x, r.y, r.w, r.h);
        g2.setColor(C_START); g2.setStroke(new BasicStroke(2f));
        g2.drawOval(r.x, r.y, r.w, r.h);
    }

    private void paintEndEvent(Graphics2D g2, Rect r) {
        g2.setColor(C_END.darker().darker());
        g2.fillOval(r.x, r.y, r.w, r.h);
        g2.setColor(C_END); g2.setStroke(new BasicStroke(3f));
        g2.drawOval(r.x, r.y, r.w, r.h);
        g2.setColor(C_END);
        g2.fillOval(r.x + 4, r.y + 4, r.w - 8, r.h - 8);
    }

    private void paintTask(Graphics2D g2, Rect r, String label) {
        g2.setColor(C_TASK_BG); g2.fillRoundRect(r.x, r.y, r.w, r.h, 8, 8);
        g2.setColor(C_TASK_BDR); g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(r.x, r.y, r.w, r.h, 8, 8);
        g2.setFont(FN); g2.setColor(C_TASK_FG);
        FontMetrics fm = g2.getFontMetrics();
        String disp = label != null ? label : "";
        if (fm.stringWidth(disp) > r.w - 8) disp = disp.substring(0, 14) + "…";
        g2.drawString(disp, r.x + (r.w - fm.stringWidth(disp))/2, r.cy() + 4);
    }

    private void paintGateway(Graphics2D g2, Rect r, Gateway gw) {
        Color c = switch (gw.type()) {
            case XOR -> C_GW_XOR;
            case PAR -> C_GW_PAR;
            case IOR -> C_GW_IOR;
        };
        // diamond shape
        int cx = r.cx(), cy = r.cy(), hs = r.w/2;
        int[] xp = {cx, cx+hs, cx, cx-hs};
        int[] yp = {cy-hs, cy, cy+hs, cy};
        g2.setColor(c.darker().darker()); g2.fillPolygon(xp, yp, 4);
        g2.setColor(c); g2.setStroke(new BasicStroke(1.5f)); g2.drawPolygon(xp, yp, 4);

        g2.setColor(c); g2.setFont(FN);
        String sym = switch (gw.type()) {
            case XOR -> "×";
            case PAR -> "+";
            case IOR -> "○";
        };
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(sym, cx - fm.stringWidth(sym)/2, cy + fm.getAscent()/2 - 1);
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2, Color c) {
        g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int sz = 8;
        int[] xp = { x2, x2-(int)(sz*Math.cos(angle-0.4)), x2-(int)(sz*Math.cos(angle+0.4)) };
        int[] yp = { y2, y2-(int)(sz*Math.sin(angle-0.4)), y2-(int)(sz*Math.sin(angle+0.4)) };
        g2.fillPolygon(xp, yp, 3);
    }

    private String truncLabel(String s) {
        return s != null && s.length() > 18 ? s.substring(0, 16) + "…" : s == null ? "" : s;
    }

    // ── View interface ────────────────────────────────────────────────────────

    @Override public void detachModel() { process = null; repaint(); }
    public    void update()             { repaint(); }
}
