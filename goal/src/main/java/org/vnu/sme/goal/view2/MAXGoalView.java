package org.vnu.sme.goal.view2;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import org.tzi.use.gui.views.View;
import org.vnu.sme.goal.maxgoal.mm.*;

/**
 * Canvas rendering a MAXGoalModel using i* / MAGoalTax notation:
 *   Actor: rounded box container with dashed border
 *   Goal: oval (achieve=green, maintain=blue, avoid=red)
 *   Task: rounded rectangle
 *   SEQ: numbered arrows from parent
 *   PAR: filled bullet at parent, plain arrows
 *   XOR: hollow square markers, guard labels
 *   IOR: hollow circle markers, guard labels
 *   ITER: reverse arrows (child to parent)
 *   depend: dashed gold arrow between leaf nodes
 */
public final class MAXGoalView extends JPanel implements View {

    // ── colours ──────────────────────────────────────────────────────────────
    private static final Color C_BG           = new Color(22,  24,  34);
    private static final Color C_ACTOR_BG     = new Color(33,  37,  52);
    private static final Color C_ACTOR_BDR    = new Color(70, 100, 170);
    private static final Color C_ACTOR_TITLE  = new Color(130, 170, 255);
    private static final Color C_AGENT_BDR    = new Color(255, 190,  40);
    private static final Color C_ROLE_BDR     = new Color( 70, 100, 170);
    private static final Color C_POS_BDR      = new Color(100, 200, 130);
    private static final Color C_GOAL_ACH     = new Color( 50, 168,  82);
    private static final Color C_GOAL_MNT     = new Color( 50, 120, 200);
    private static final Color C_GOAL_AVD     = new Color(200,  55,  55);
    private static final Color C_GOAL_NONE    = new Color( 80,  90, 115);
    private static final Color C_TASK_BG      = new Color( 45,  55,  80);
    private static final Color C_TASK_BDR     = new Color( 90, 130, 210);
    private static final Color C_RES_BG       = new Color( 40,  55,  45);
    private static final Color C_RES_BDR      = new Color( 80, 175, 100);
    private static final Color C_SEQ          = new Color( 80, 160, 255);
    private static final Color C_PAR          = new Color(255, 165,  50);
    private static final Color C_XOR          = new Color(210,  80, 230);
    private static final Color C_IOR          = new Color( 50, 210, 180);
    private static final Color C_ITER         = new Color(235,  80,  80);
    private static final Color C_DEP          = new Color(220, 210,  50);
    private static final Color C_LABEL        = new Color(225, 230, 240);
    private static final Color C_COND         = new Color(160, 175, 200);

    // ── layout ───────────────────────────────────────────────────────────────
    private static final int PAD    = 18;
    private static final int HGAP   = 50;
    private static final int VGAP   = 26;
    private static final int GW     = 138;
    private static final int GH     = 44;
    private static final int TW     = 122;
    private static final int TH     = 36;
    private static final int RW     = 110;
    private static final int RH     = 28;
    private static final int CGAP   = 22;
    private static final int VG     = 10;
    private static final int TITLEH = 26;
    private static final int MARGIN = 24;

    private static final Font FA = new Font(Font.SANS_SERIF, Font.BOLD,  12);
    private static final Font FG = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    private static final Font FT = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    private static final Font FC = new Font(Font.SANS_SERIF, Font.ITALIC, 9);

    // ── node model ───────────────────────────────────────────────────────────
    private enum NT { ACTOR, GOAL, TASK, RES }

    private static class Node {
        String  id, label, clause;
        NT      kind;
        String  actorId;
        RefineSpec refine;
        int     x, y, w, h;
    }

    private final Map<String, Node>  nodes    = new LinkedHashMap<>();
    private final List<Node>         actorNodes = new ArrayList<>();
    private MAXGoalModel             model;

    public MAXGoalView() {
        setBackground(C_BG);
        setPreferredSize(new Dimension(1400, 800));
    }

    public void setModel(MAXGoalModel m) {
        this.model = m;
        buildLayout();
        revalidate();
        repaint();
    }

    // ── layout ───────────────────────────────────────────────────────────────

    private void buildLayout() {
        nodes.clear();
        actorNodes.clear();
        if (model == null) return;

        int ax = MARGIN;
        for (Actor a : model.getActors()) {
            Node actor = new Node();
            actor.id     = a.name();
            actor.label  = a.name();
            actor.kind   = NT.ACTOR;
            actor.x      = ax;
            actor.y      = MARGIN;

            int maxW = 0, cy = MARGIN + TITLEH + PAD;
            List<Node> children = new ArrayList<>();

            for (Intentional item : a.intentionals()) {
                Node n = new Node();
                n.id      = item.name();
                n.actorId = a.name();
                switch (item) {
                    case GoalDef g -> {
                        n.label  = g.name();
                        n.clause = g.intentClause();
                        n.kind   = NT.GOAL;
                        n.refine = g.refine();
                        n.w = GW; n.h = GH;
                    }
                    case TaskDef t -> {
                        n.label  = t.name();
                        n.kind   = NT.TASK;
                        n.refine = t.refine();
                        n.w = TW; n.h = TH;
                    }
                    case ResourceDef r -> {
                        n.label = r.name();
                        n.kind  = NT.RES;
                        n.w = RW; n.h = RH;
                    }
                }
                n.x = ax + PAD;
                n.y = cy;
                cy += n.h + VG;
                maxW = Math.max(maxW, n.w);
                children.add(n);
                nodes.put(n.id, n);
            }

            actor.w = maxW + PAD * 2;
            actor.h = cy - MARGIN + PAD;
            actor.label = a.name() + " «" + a.kind().name().toLowerCase() + "»";

            for (Node cn : children) cn.x = ax + (actor.w - cn.w) / 2;

            nodes.put(actor.id, actor);
            actorNodes.add(actor);
            ax += actor.w + HGAP;
        }

        int totalW = ax + MARGIN;
        int maxH   = actorNodes.stream().mapToInt(n -> n.y + n.h).max().orElse(400) + MARGIN;
        setPreferredSize(new Dimension(Math.max(totalW, 800), Math.max(maxH, 600)));
        installDrag();
    }

    // ── drag ─────────────────────────────────────────────────────────────────

    private void installDrag() {
        for (MouseMotionListener ml : getMouseMotionListeners()) removeMouseMotionListener(ml);
        for (MouseListener ml : getMouseListeners()) removeMouseListener(ml);

        int[] drag = {0, 0, 0, 0};

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                Node hit = hitTest(e.getX(), e.getY());
                if (hit != null) { drag[0] = hit.x; drag[1] = hit.y; drag[2] = e.getX(); drag[3] = e.getY(); drag[0] = -1; }
                Node h2 = hitTestNonActor(e.getX(), e.getY());
                if (h2 != null) { drag[0] = h2.x; drag[1] = h2.y; drag[2] = e.getX(); drag[3] = e.getY(); }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                Node h = hitTestNonActor(e.getX(), e.getY());
                if (h == null) return;
                h.x += e.getX() - drag[2]; h.y += e.getY() - drag[3];
                drag[2] = e.getX(); drag[3] = e.getY();
                recomputeActorBounds(h.actorId);
                repaint();
            }
        });
    }

    private Node hitTest(int x, int y) {
        for (Node n : actorNodes)
            if (x >= n.x && x <= n.x+n.w && y >= n.y && y <= n.y+n.h) return n;
        return null;
    }

    private Node hitTestNonActor(int x, int y) {
        for (Node n : nodes.values())
            if (n.kind != NT.ACTOR && x >= n.x && x <= n.x+n.w && y >= n.y && y <= n.y+n.h) return n;
        return null;
    }

    private void recomputeActorBounds(String actorId) {
        Node actor = nodes.get(actorId);
        if (actor == null) return;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = 0, maxY = 0;
        for (Node n : nodes.values()) {
            if (actorId.equals(n.actorId)) {
                minX = Math.min(minX, n.x); minY = Math.min(minY, n.y);
                maxX = Math.max(maxX, n.x + n.w); maxY = Math.max(maxY, n.y + n.h);
            }
        }
        if (minX == Integer.MAX_VALUE) return;
        actor.x = minX - PAD; actor.y = minY - TITLEH - PAD;
        actor.w = maxX - minX + PAD * 2;
        actor.h = maxY - minY + TITLEH + PAD * 2;
    }

    // ── paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        paintActors(g2);
        paintRefineEdges(g2);
        paintDependEdges(g2);
        paintNodes(g2);
    }

    private void paintActors(Graphics2D g2) {
        for (Node a : actorNodes) {
            Color bdr = switch (model.getActors().stream()
                    .filter(ac -> ac.name().equals(a.id)).findFirst()
                    .map(ac -> ac.kind()).orElse(ActorKind.ROLE)) {
                case AGENT    -> C_AGENT_BDR;
                case ROLE     -> C_ROLE_BDR;
                case POSITION -> C_POS_BDR;
            };
            Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{6, 4}, 0);
            g2.setColor(C_ACTOR_BG); g2.fillRoundRect(a.x, a.y, a.w, a.h, 14, 14);
            g2.setColor(bdr); g2.setStroke(dashed);
            g2.drawRoundRect(a.x, a.y, a.w, a.h, 14, 14);
            g2.setStroke(new BasicStroke(1));
            g2.setColor(bdr.darker()); g2.fillRoundRect(a.x+1, a.y+1, a.w-2, TITLEH, 12, 12);
            g2.setFont(FA); g2.setColor(C_ACTOR_TITLE);
            g2.drawString(a.label, a.x + 8, a.y + 17);
        }
    }

    private void paintNodes(Graphics2D g2) {
        for (Node n : nodes.values()) {
            if (n.kind == NT.ACTOR) continue;
            switch (n.kind) {
                case GOAL -> paintGoal(g2, n);
                case TASK -> paintTask(g2, n);
                case RES  -> paintResource(g2, n);
            }
        }
    }

    private void paintGoal(Graphics2D g2, Node n) {
        Color c = switch (n.clause == null ? "" : n.clause) {
            case "achieve"  -> C_GOAL_ACH;
            case "maintain" -> C_GOAL_MNT;
            case "avoid"    -> C_GOAL_AVD;
            default         -> C_GOAL_NONE;
        };
        g2.setColor(c.darker().darker()); g2.fillOval(n.x, n.y, n.w, n.h);
        g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(n.x, n.y, n.w, n.h);
        g2.setFont(FG); g2.setColor(C_LABEL);
        drawCentredString(g2, n.label, n.x + n.w/2, n.y + n.h/2 - 4);
        if (n.clause != null) {
            g2.setFont(FC); g2.setColor(C_COND);
            drawCentredString(g2, "«" + n.clause + "»", n.x + n.w/2, n.y + n.h/2 + 9);
        }
    }

    private void paintTask(Graphics2D g2, Node n) {
        g2.setColor(C_TASK_BG); g2.fillRoundRect(n.x, n.y, n.w, n.h, 8, 8);
        g2.setColor(C_TASK_BDR); g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(n.x, n.y, n.w, n.h, 8, 8);
        g2.setFont(FT); g2.setColor(C_LABEL);
        drawCentredString(g2, n.label, n.x + n.w/2, n.y + n.h/2 + 4);
    }

    private void paintResource(Graphics2D g2, Node n) {
        g2.setColor(C_RES_BG); g2.fillRect(n.x, n.y, n.w, n.h);
        g2.setColor(C_RES_BDR); g2.setStroke(new BasicStroke(1f));
        g2.drawRect(n.x, n.y, n.w, n.h);
        g2.setFont(FC); g2.setColor(new Color(140, 200, 150));
        drawCentredString(g2, "«resource» " + n.label, n.x + n.w/2, n.y + n.h/2 + 4);
    }

    private void paintRefineEdges(Graphics2D g2) {
        if (model == null) return;
        for (Intentional item : model.allIntentionals().values()) {
            RefineSpec refine = switch (item) {
                case GoalDef g -> g.refine();
                case TaskDef t -> t.refine();
                case ResourceDef r -> null;
            };
            if (refine == null) continue;
            Node parent = nodes.get(item.name());
            if (parent == null) continue;
            paintRefine(g2, parent, refine);
        }
    }

    private void paintRefine(Graphics2D g2, Node parent, RefineSpec refine) {
        switch (refine) {
            case RefineSpec.IterRefine it -> {
                g2.setColor(C_ITER);
                for (String childId : it.children()) {
                    Node child = nodes.get(childId);
                    if (child != null) drawArrowReverse(g2, child, parent, C_ITER);
                }
            }
            case RefineSpec.SeqRefine s -> {
                g2.setColor(C_SEQ); g2.setStroke(new BasicStroke(1.5f));
                paintBullet(g2, parent, C_SEQ, true);
                int seq = 1;
                for (String childId : s.children()) {
                    Node child = nodes.get(childId);
                    if (child != null) drawArrow(g2, parent, child, String.valueOf(seq++), C_SEQ);
                }
            }
            case RefineSpec.ParRefine p -> {
                g2.setColor(C_PAR); paintBullet(g2, parent, C_PAR, true);
                for (String childId : p.children()) {
                    Node child = nodes.get(childId);
                    if (child != null) drawArrow(g2, parent, child, null, C_PAR);
                }
            }
            case RefineSpec.XorRefine x -> {
                g2.setColor(C_XOR); paintHollowSquares(g2, parent, x.branches().size(), C_XOR);
                for (GuardedChild b : x.branches()) {
                    Node child = nodes.get(b.childId());
                    if (child != null) drawArrow(g2, parent, child, truncCond(b.condition()), C_XOR);
                }
            }
            case RefineSpec.IorRefine io -> {
                paintHollowCircles(g2, parent, io.branches().size(), C_IOR);
                for (GuardedChild b : io.branches()) {
                    Node child = nodes.get(b.childId());
                    if (child != null) drawArrow(g2, parent, child, truncCond(b.condition()), C_IOR);
                }
            }
        }
    }

    private void paintDependEdges(Graphics2D g2) {
        if (model == null) return;
        Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{8, 4}, 0);
        g2.setStroke(dashed); g2.setColor(C_DEP);
        for (Dependency d : model.getDependencies()) {
            Node src = nodes.get(d.from());
            Node tgt = nodes.get(d.to());
            if (src == null || tgt == null) continue;
            int x1 = src.x + src.w/2, y1 = src.y + src.h/2;
            int x2 = tgt.x + tgt.w/2, y2 = tgt.y + tgt.h/2;
            g2.drawLine(x1, y1, x2, y2);
            drawArrowHead(g2, x1, y1, x2, y2);
            g2.setFont(FC); g2.setColor(C_DEP);
            g2.drawString("≫", (x1+x2)/2 - 6, (y1+y2)/2 - 4);
            g2.setColor(C_DEP); g2.setStroke(dashed);
        }
    }

    // ── drawing helpers ───────────────────────────────────────────────────────

    private void paintBullet(Graphics2D g2, Node n, Color c, boolean filled) {
        int bx = n.x + n.w/2 - 5, by = n.y + n.h - 5;
        if (filled) { g2.setColor(c); g2.fillOval(bx, by, 10, 10); }
        else { g2.setColor(c); g2.drawOval(bx, by, 10, 10); }
    }

    private void paintHollowSquares(Graphics2D g2, Node n, int count, Color c) {
        g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
        int bx = n.x + n.w/2 - count * 7, by = n.y + n.h - 6;
        for (int i = 0; i < count; i++) {
            g2.drawRect(bx + i*14, by, 10, 10);
        }
    }

    private void paintHollowCircles(Graphics2D g2, Node n, int count, Color c) {
        g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
        int bx = n.x + n.w/2 - count * 7, by = n.y + n.h - 6;
        for (int i = 0; i < count; i++) {
            g2.drawOval(bx + i*14, by, 10, 10);
        }
    }

    private void drawArrow(Graphics2D g2, Node from, Node to, String label, Color c) {
        g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
        int[] p = edgePair(from, to);
        g2.drawLine(p[0], p[1], p[2], p[3]);
        drawArrowHead(g2, p[0], p[1], p[2], p[3]);
        if (label != null) {
            g2.setFont(FC); g2.setColor(c.brighter());
            g2.drawString(label, (p[0]+p[2])/2 + 3, (p[1]+p[3])/2 - 4);
        }
    }

    private void drawArrowReverse(Graphics2D g2, Node from, Node to, Color c) {
        g2.setColor(c);
        Stroke dotted = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{4, 3}, 0);
        g2.setStroke(dotted);
        int[] p = edgePair(from, to);
        g2.drawLine(p[0], p[1], p[2], p[3]);
        drawArrowHead(g2, p[0], p[1], p[2], p[3]);
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int sz = 8;
        int[] xp = { x2, x2 - (int)(sz * Math.cos(angle - 0.4)),
                         x2 - (int)(sz * Math.cos(angle + 0.4)) };
        int[] yp = { y2, y2 - (int)(sz * Math.sin(angle - 0.4)),
                         y2 - (int)(sz * Math.sin(angle + 0.4)) };
        g2.fillPolygon(xp, yp, 3);
    }

    private int[] edgePair(Node a, Node b) {
        int ax = a.x + a.w/2, ay = a.y + a.h/2;
        int bx = b.x + b.w/2, by = b.y + b.h/2;
        int dx = bx - ax, dy = by - ay;
        int x1, y1, x2, y2;
        if (Math.abs(dx) > Math.abs(dy)) {
            x1 = dx > 0 ? a.x + a.w : a.x; y1 = ay;
            x2 = dx > 0 ? b.x : b.x + b.w; y2 = by;
        } else {
            x1 = ax; y1 = dy > 0 ? a.y + a.h : a.y;
            x2 = bx; y2 = dy > 0 ? b.y : b.y + b.h;
        }
        return new int[]{x1, y1, x2, y2};
    }

    private void drawCentredString(Graphics2D g2, String s, int cx, int cy) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(s, cx - fm.stringWidth(s)/2, cy);
    }

    private String truncCond(String cond) {
        if (cond == null) return "";
        return cond.length() > 20 ? cond.substring(0, 18) + "…" : cond;
    }

    // ── View interface ────────────────────────────────────────────────────────

    @Override public void detachModel() { model = null; repaint(); }
    public    void update()             { repaint(); }
}
