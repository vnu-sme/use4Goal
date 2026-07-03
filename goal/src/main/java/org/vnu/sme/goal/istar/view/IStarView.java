package org.vnu.sme.goal.istar.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import org.tzi.use.gui.views.View;
import org.vnu.sme.goal.istar.mm.*;

/**
 * iStar 2.0 Strategic Rationale (SR) diagram — interactive canvas.
 *
 * Interactions:
 *   Left-drag actor boundary  → moves actor + all contained elements
 *   Left-drag element         → moves element; actor boundary auto-resizes
 *   Right-click               → Save Layout / Load Layout / Reset Layout
 *
 * Edges are drawn center-to-center, clipped to each node's shape border.
 * Layout (x,y) persists to a .layout file next to the source file.
 */
public final class IStarView extends JPanel implements View {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color C_BG          = Color.WHITE;
    private static final Color C_ACTOR_FILL  = new Color(245, 246, 250);
    private static final Color C_ACTOR_BDR   = new Color(100, 110, 140);
    private static final Color C_ROLE_BDR    = new Color(50,  100, 190);
    private static final Color C_AGENT_BDR   = new Color(160, 110,  20);
    private static final Color C_ACTOR_LBL   = new Color(50,  60,  90);
    private static final Color C_GOAL_FILL   = new Color(230, 248, 232);
    private static final Color C_GOAL_BDR    = new Color(30,  140,  60);
    private static final Color C_TASK_FILL   = new Color(228, 238, 255);
    private static final Color C_TASK_BDR    = new Color(50,  100, 200);
    private static final Color C_RES_FILL    = new Color(225, 245, 245);
    private static final Color C_RES_BDR     = new Color(30,  150, 150);
    private static final Color C_QUAL_FILL   = new Color(255, 250, 220);
    private static final Color C_QUAL_BDR    = new Color(175, 130,  10);
    private static final Color C_ELEM_TXT    = new Color(20,  20,  40);
    private static final Color C_AND_REF     = new Color(60,  60,  80);
    private static final Color C_OR_REF      = new Color(50, 100, 200);
    private static final Color C_NEEDED      = new Color(30, 145, 145);
    private static final Color C_QUALIFY_LNK = new Color(160, 120,  10);
    private static final Color C_DEP         = new Color(100,  70, 160);

    // ── Sizes ─────────────────────────────────────────────────────────────────
    private static final int MARGIN    = 30;
    private static final int ACTOR_PAD = 22;
    private static final int ACTOR_HDR = 28;
    private static final int HGAP      = 56;
    private static final int ELEM_GAP  = 12;
    private static final int GW = 126; private static final int GH = 36;
    private static final int TW = 108; private static final int TH = 44;
    private static final int RW = 108; private static final int RH = 26;
    private static final int QW = 118; private static final int QH = 46;
    private static final int SYM_R = 16;

    private static final Font FA  = new Font(Font.SANS_SERIF, Font.BOLD,  11);
    private static final Font FE  = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private static final Font FLK = new Font(Font.SANS_SERIF, Font.ITALIC, 9);

    // ── Internal node model ───────────────────────────────────────────────────
    private enum NT { ACTOR, GOAL, TASK, RESOURCE, QUALITY }

    private static class Node {
        String id, label;
        NT     kind;
        String actorId;   // null for actor nodes
        int    x, y, w, h;
    }

    private final Map<String, Node> nodes      = new LinkedHashMap<>();
    private final List<Node>        actorNodes = new ArrayList<>();
    private IStarModel              model;
    private Path                    sourceFile;

    // ── Drag state ────────────────────────────────────────────────────────────
    private Node dragNode = null;
    private int  dragX0, dragY0;

    // ── Constructor ───────────────────────────────────────────────────────────

    public IStarView() {
        setBackground(C_BG);
        setPreferredSize(new Dimension(1200, 700));
        installInteraction();
    }

    public void setSourceFile(Path p) { this.sourceFile = p; }

    public void setModel(IStarModel m) {
        this.model = m;
        buildLayout();
        if (sourceFile != null) loadLayout();
        revalidate();
        repaint();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {
        nodes.clear();
        actorNodes.clear();
        if (model == null) return;

        int ax = MARGIN;
        for (ActorDef actor : model.getActors()) {
            Node aNode = new Node();
            aNode.id    = actor.name();
            aNode.label = actor.name() + " «" + actor.kind().name().toLowerCase() + "»";
            aNode.kind  = NT.ACTOR;
            aNode.x     = ax;
            aNode.y     = MARGIN;

            int cy   = MARGIN + ACTOR_HDR + ACTOR_PAD;
            int maxW = 0;
            List<Node> children = new ArrayList<>();

            for (IntentionalElement elem : actor.elements()) {
                Node n = new Node();
                n.id      = elem.id();
                n.label   = elem.id();
                n.actorId = actor.name();
                switch (elem) {
                    case IntentionalElement.Goal     g -> { n.kind = NT.GOAL;     n.w = GW; n.h = GH; }
                    case IntentionalElement.Task     t -> { n.kind = NT.TASK;     n.w = TW; n.h = TH; }
                    case IntentionalElement.Resource r -> { n.kind = NT.RESOURCE; n.w = RW; n.h = RH; }
                    case IntentionalElement.Quality  q -> { n.kind = NT.QUALITY;  n.w = QW; n.h = QH; }
                }
                n.x = ax + ACTOR_PAD;
                n.y = cy;
                cy += n.h + ELEM_GAP;
                maxW = Math.max(maxW, n.w);
                children.add(n);
                nodes.put(n.id, n);
            }

            aNode.w = maxW + ACTOR_PAD * 2;
            aNode.h = cy - MARGIN + ACTOR_PAD;
            for (Node cn : children) cn.x = ax + (aNode.w - cn.w) / 2;

            nodes.put(aNode.id, aNode);
            actorNodes.add(aNode);
            ax += aNode.w + HGAP;
        }

        int totalW = ax + MARGIN;
        int maxH   = actorNodes.stream().mapToInt(n -> n.y + n.h).max().orElse(500) + MARGIN;
        setPreferredSize(new Dimension(Math.max(totalW, 900), Math.max(maxH, 600)));
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    private void installInteraction() {
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) { showContextMenu(e); dragNode = null; return; }
                dragNode = hitNode(e.getX(), e.getY());
                dragX0 = e.getX(); dragY0 = e.getY();
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
                dragNode = null;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragNode == null) return;
                int dx = e.getX() - dragX0, dy = e.getY() - dragY0;
                dragX0 = e.getX(); dragY0 = e.getY();
                if (dragNode.kind == NT.ACTOR) {
                    // Move actor + all its contained elements together
                    dragNode.x += dx; dragNode.y += dy;
                    for (Node n : nodes.values())
                        if (dragNode.id.equals(n.actorId)) { n.x += dx; n.y += dy; }
                } else {
                    dragNode.x += dx; dragNode.y += dy;
                    if (dragNode.actorId != null) recomputeActorBounds(dragNode.actorId);
                }
                updatePreferredSize();
                repaint();
            }
        });
    }

    /** Hit-test: child elements have priority over actor boundaries. */
    private Node hitNode(int x, int y) {
        for (Node n : nodes.values())
            if (n.kind != NT.ACTOR && x >= n.x && x <= n.x + n.w && y >= n.y && y <= n.y + n.h)
                return n;
        for (Node a : actorNodes)
            if (x >= a.x && x <= a.x + a.w && y >= a.y && y <= a.y + a.h)
                return a;
        return null;
    }

    /** Expand/shrink actor boundary to tightly wrap its children. */
    private void recomputeActorBounds(String actorId) {
        Node actor = nodes.get(actorId);
        if (actor == null) return;
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = 0, maxY = 0;
        for (Node n : nodes.values()) {
            if (actorId.equals(n.actorId)) {
                minX = Math.min(minX, n.x); minY = Math.min(minY, n.y);
                maxX = Math.max(maxX, n.x + n.w); maxY = Math.max(maxY, n.y + n.h);
            }
        }
        if (minX == Integer.MAX_VALUE) return;
        actor.x = minX - ACTOR_PAD;
        actor.y = minY - ACTOR_HDR - ACTOR_PAD;
        actor.w = maxX - minX + ACTOR_PAD * 2;
        actor.h = maxY - minY + ACTOR_HDR + ACTOR_PAD * 2;
    }

    private void updatePreferredSize() {
        int maxX = nodes.values().stream().mapToInt(n -> n.x + n.w).max().orElse(900) + MARGIN;
        int maxY = nodes.values().stream().mapToInt(n -> n.y + n.h).max().orElse(600) + MARGIN;
        setPreferredSize(new Dimension(Math.max(maxX, 900), Math.max(maxY, 600)));
        revalidate();
    }

    // ── Right-click menu ──────────────────────────────────────────────────────

    private void showContextMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem saveItem = new JMenuItem("Save Layout");
        saveItem.addActionListener(ev -> {
            saveLayout();
            JOptionPane.showMessageDialog(IStarView.this, "Layout saved.", "iStar 2.0",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem loadItem = new JMenuItem("Load Layout");
        loadItem.setEnabled(sourceFile != null &&
                Files.exists(sourceFile.resolveSibling(sourceFile.getFileName() + ".layout")));
        loadItem.addActionListener(ev -> { loadLayout(); repaint(); });

        JMenuItem resetItem = new JMenuItem("Reset Layout");
        resetItem.addActionListener(ev -> { buildLayout(); repaint(); });

        menu.add(saveItem);
        menu.add(loadItem);
        menu.addSeparator();
        menu.add(resetItem);
        menu.show(this, e.getX(), e.getY());
    }

    // ── Layout persistence ────────────────────────────────────────────────────

    private Path layoutPath() {
        return sourceFile == null ? null
                : sourceFile.resolveSibling(sourceFile.getFileName() + ".layout");
    }

    private void saveLayout() {
        Path lp = layoutPath();
        if (lp == null) return;
        java.util.Properties p = new java.util.Properties();
        for (Node n : nodes.values()) {
            p.setProperty("node." + n.id + ".x", String.valueOf(n.x));
            p.setProperty("node." + n.id + ".y", String.valueOf(n.y));
            p.setProperty("node." + n.id + ".w", String.valueOf(n.w));
            p.setProperty("node." + n.id + ".h", String.valueOf(n.h));
        }
        try (OutputStream os = Files.newOutputStream(lp)) {
            p.store(os, "iStar 2.0 Layout");
        } catch (IOException ex) { /* ignore */ }
    }

    private void loadLayout() {
        Path lp = layoutPath();
        if (lp == null || !Files.exists(lp)) return;
        java.util.Properties p = new java.util.Properties();
        try (InputStream is = Files.newInputStream(lp)) {
            p.load(is);
            for (Node n : nodes.values()) {
                String xs = p.getProperty("node." + n.id + ".x");
                String ys = p.getProperty("node." + n.id + ".y");
                String ws = p.getProperty("node." + n.id + ".w");
                String hs = p.getProperty("node." + n.id + ".h");
                if (xs != null) n.x = Integer.parseInt(xs);
                if (ys != null) n.y = Integer.parseInt(ys);
                if (ws != null) n.w = Integer.parseInt(ws);
                if (hs != null) n.h = Integer.parseInt(hs);
            }
        } catch (IOException | NumberFormatException ex) { /* ignore */ }
        updatePreferredSize();
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        paintActorBoundaries(g2);
        paintLinks(g2);
        paintDependencies(g2);
        paintElements(g2);
        paintActorSymbols(g2);
    }

    private void paintActorBoundaries(Graphics2D g2) {
        if (model == null) return;
        for (Node a : actorNodes) {
            g2.setColor(C_ACTOR_FILL);
            g2.fillRoundRect(a.x, a.y, a.w, a.h, 18, 18);

            Color bdr = actorBdrColor(a.id);
            g2.setColor(bdr);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    0, new float[]{7, 4}, 0));
            g2.drawRoundRect(a.x, a.y, a.w, a.h, 18, 18);
            g2.setStroke(new BasicStroke(1));

            g2.setFont(FA); g2.setColor(C_ACTOR_LBL);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(a.label, a.x + (a.w - fm.stringWidth(a.label)) / 2, a.y + 20);
        }
    }

    private void paintActorSymbols(Graphics2D g2) {
        if (model == null) return;
        for (ActorDef actor : model.getActors()) {
            Node a = nodes.get(actor.name());
            if (a == null) continue;
            int cx = a.x + a.w / 2, cy = a.y;
            Color bdr = actorBdrColor(actor.name());

            g2.setColor(C_BG);
            g2.fillOval(cx - SYM_R, cy - SYM_R, SYM_R * 2, SYM_R * 2);
            g2.setColor(bdr); g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(cx - SYM_R, cy - SYM_R, SYM_R * 2, SYM_R * 2);

            g2.setStroke(new BasicStroke(1.5f));
            switch (actor.kind()) {
                case ROLE  -> g2.drawArc(cx - SYM_R + 5, cy - 4, (SYM_R - 5) * 2, SYM_R - 2, 0, -180);
                case AGENT -> g2.drawLine(cx - SYM_R + 5, cy - SYM_R / 2,
                                          cx + SYM_R - 5, cy - SYM_R / 2);
                case ACTOR -> { /* plain circle */ }
            }
            g2.setStroke(new BasicStroke(1));
        }
    }

    private Color actorBdrColor(String id) {
        return model.findActor(id).map(a -> switch (a.kind()) {
            case ROLE  -> C_ROLE_BDR;
            case AGENT -> C_AGENT_BDR;
            default    -> C_ACTOR_BDR;
        }).orElse(C_ACTOR_BDR);
    }

    private void paintElements(Graphics2D g2) {
        for (Node n : nodes.values()) {
            if (n.kind == NT.ACTOR) continue;
            switch (n.kind) {
                case GOAL     -> paintGoal(g2, n);
                case TASK     -> paintTask(g2, n);
                case RESOURCE -> paintResource(g2, n);
                case QUALITY  -> paintQuality(g2, n);
                default -> {}
            }
        }
    }

    private void paintGoal(Graphics2D g2, Node n) {
        g2.setColor(C_GOAL_FILL);  g2.fillOval(n.x, n.y, n.w, n.h);
        g2.setColor(C_GOAL_BDR);   g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(n.x, n.y, n.w, n.h); g2.setStroke(new BasicStroke(1));
        drawCentred(g2, n.label, n.x + n.w / 2, n.y + n.h / 2 + 4, FE, C_ELEM_TXT);
    }

    private void paintTask(Graphics2D g2, Node n) {
        Shape hex = buildHex(n.x, n.y, n.w, n.h);
        g2.setColor(C_TASK_FILL); g2.fill(hex);
        g2.setColor(C_TASK_BDR);  g2.setStroke(new BasicStroke(1.5f)); g2.draw(hex);
        g2.setStroke(new BasicStroke(1));
        drawCentred(g2, n.label, n.x + n.w / 2, n.y + n.h / 2 + 4, FE, C_ELEM_TXT);
    }

    private void paintResource(Graphics2D g2, Node n) {
        g2.setColor(C_RES_FILL); g2.fillRect(n.x, n.y, n.w, n.h);
        g2.setColor(C_RES_BDR);  g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(n.x, n.y, n.w, n.h); g2.setStroke(new BasicStroke(1));
        drawCentred(g2, n.label, n.x + n.w / 2, n.y + n.h / 2 + 4, FE, C_ELEM_TXT);
    }

    private void paintQuality(Graphics2D g2, Node n) {
        Shape cloud = buildCloud(n.x, n.y, n.w, n.h);
        g2.setColor(C_QUAL_FILL); g2.fill(cloud);
        g2.setColor(C_QUAL_BDR);  g2.setStroke(new BasicStroke(1.5f)); g2.draw(cloud);
        g2.setStroke(new BasicStroke(1));
        drawCentred(g2, n.label, n.x + n.w / 2, n.y + n.h / 2 + 4, FE, C_ELEM_TXT);
    }

    // ── Links ─────────────────────────────────────────────────────────────────

    private void paintLinks(Graphics2D g2) {
        if (model == null) return;
        for (ActorDef actor : model.getActors()) {
            for (Refinement   ref : actor.refinements())    paintRefinement(g2, ref);
            for (Contribution c   : actor.contributions())  paintContribution(g2, c);
            for (Qualification q  : actor.qualifications()) paintQualification(g2, q);
            for (NeededBy nb      : actor.neededBys())      paintNeededBy(g2, nb);
        }
    }

    private void paintRefinement(Graphics2D g2, Refinement ref) {
        Node parent = nodes.get(ref.parent());
        if (parent == null) return;
        switch (ref) {
            case Refinement.And and -> {
                g2.setColor(C_AND_REF); g2.setStroke(new BasicStroke(1.5f));
                for (String childId : and.children()) {
                    Node child = nodes.get(childId);
                    if (child == null) continue;
                    int[] p = edge(child, parent);
                    g2.drawLine(p[0], p[1], p[2], p[3]);
                    drawTArrow(g2, p[0], p[1], p[2], p[3], C_AND_REF);
                }
            }
            case Refinement.Or or -> {
                g2.setColor(C_OR_REF); g2.setStroke(new BasicStroke(1.5f));
                Node child = nodes.get(or.child());
                if (child != null) {
                    int[] p = edge(child, parent);
                    g2.drawLine(p[0], p[1], p[2], p[3]);
                    drawFilledArrow(g2, p[0], p[1], p[2], p[3], C_OR_REF);
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void paintContribution(Graphics2D g2, Contribution c) {
        Node elem = nodes.get(c.element()), qual = nodes.get(c.quality());
        if (elem == null || qual == null) return;
        Color col = c.type().color().darker();
        g2.setColor(col); g2.setStroke(new BasicStroke(1.5f));
        int[] p = edge(elem, qual);
        g2.drawLine(p[0], p[1], p[2], p[3]);
        drawFilledArrow(g2, p[0], p[1], p[2], p[3], col);
        g2.setFont(FLK); g2.setColor(col);
        g2.drawString(c.type().label(), (p[0] + p[2]) / 2 + 3, (p[1] + p[3]) / 2 - 4);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintQualification(Graphics2D g2, Qualification q) {
        Node qual = nodes.get(q.quality()), elem = nodes.get(q.element());
        if (qual == null || elem == null) return;
        g2.setColor(C_QUALIFY_LNK);
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{4, 3}, 0));
        int[] p = edge(qual, elem);
        g2.drawLine(p[0], p[1], p[2], p[3]);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintNeededBy(Graphics2D g2, NeededBy nb) {
        Node res = nodes.get(nb.resource()), task = nodes.get(nb.task());
        if (res == null || task == null) return;
        g2.setColor(C_NEEDED); g2.setStroke(new BasicStroke(1.5f));
        int[] p = edge(res, task);
        g2.drawLine(p[0], p[1], p[2], p[3]);
        g2.fillOval(p[2] - 5, p[3] - 5, 10, 10);
        g2.setStroke(new BasicStroke(1));
    }

    // ── Dependencies ─────────────────────────────────────────────────────────

    private void paintDependencies(Graphics2D g2) {
        if (model == null) return;
        Stroke dashed = new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{8, 4}, 0);
        for (Dependency dep : model.getDependencies()) {
            Node from = firstElemOf(dep.depender());
            Node to   = firstElemOf(dep.dependee());
            if (from == null) from = nodes.get(dep.depender());
            if (to   == null) to   = nodes.get(dep.dependee());
            if (from == null || to == null) continue;

            double x1 = from.x + from.w / 2.0, y1 = from.y + from.h / 2.0;
            double x2 = to.x   + to.w   / 2.0, y2 = to.y   + to.h   / 2.0;
            int mx = (int)((x1 + x2) / 2), my = (int)((y1 + y2) / 2);

            // clip line segments to shape borders
            double[] c1 = clipToShape(from, x2, y2);
            double[] c2 = clipToShape(to,   x1, y1);

            g2.setColor(C_DEP); g2.setStroke(dashed);
            g2.drawLine((int)c1[0], (int)c1[1], mx - 13, my);
            g2.drawLine(mx + 13, my, (int)c2[0], (int)c2[1]);
            g2.setStroke(new BasicStroke(1));

            // D-oval
            g2.setColor(C_BG);  g2.fillOval(mx - 13, my - 9, 26, 18);
            g2.setColor(C_DEP); g2.setStroke(new BasicStroke(1.4f));
            g2.drawOval(mx - 13, my - 9, 26, 18);
            g2.setStroke(new BasicStroke(1));
            drawCentred(g2, "D", mx, my + 4, FA, C_DEP);

            g2.setFont(FLK); g2.setColor(C_DEP);
            FontMetrics fm = g2.getFontMetrics();
            String lbl = dep.dependum();
            g2.drawString(lbl, mx - fm.stringWidth(lbl) / 2, my - 13);

            drawFilledArrow(g2, (int)c1[0], (int)c1[1], mx - 13, my, C_DEP);   // depender → dependum
            drawFilledArrow(g2, mx + 13, my, (int)c2[0], (int)c2[1], C_DEP);   // dependum → dependee
        }
    }

    private Node firstElemOf(String actorName) {
        return nodes.values().stream()
                .filter(n -> actorName.equals(n.actorId))
                .findFirst().orElse(null);
    }

    // ── Edge clipping ─────────────────────────────────────────────────────────

    /**
     * Returns the point on the border of node {@code n} along the line
     * from n's center toward (tx, ty).  Uses the exact shape formula
     * for each node type.
     */
    private static double[] clipToShape(Node n, double tx, double ty) {
        double cx = n.x + n.w / 2.0, cy = n.y + n.h / 2.0;
        double dx = tx - cx, dy = ty - cy;
        if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5) return new double[]{cx, cy};

        double hw = n.w / 2.0, hh = n.h / 2.0;
        double t;
        switch (n.kind) {
            case GOAL -> {
                // Ellipse: t = 1 / sqrt((dx/rx)^2 + (dy/ry)^2)
                double dxr = dx / hw, dyr = dy / hh;
                t = 1.0 / Math.sqrt(dxr * dxr + dyr * dyr);
            }
            case QUALITY -> {
                // Cloud ≈ smaller ellipse (cloud bumps pull inward ~15%)
                double rx = hw * 0.82, ry = hh * 0.82;
                double dxr = dx / rx, dyr = dy / ry;
                t = 1.0 / Math.sqrt(dxr * dxr + dyr * dyr);
            }
            case TASK -> {
                // Hexagon ≈ ellipse with inscribed radius adjustment
                double rx = hw - 3, ry = hh - 3;
                double dxr = dx / rx, dyr = dy / ry;
                t = 1.0 / Math.sqrt(dxr * dxr + dyr * dyr);
            }
            default -> {
                // Rectangle and Actor boundary
                double txr = Math.abs(dx) < 1e-9 ? Double.MAX_VALUE : hw / Math.abs(dx);
                double tyr = Math.abs(dy) < 1e-9 ? Double.MAX_VALUE : hh / Math.abs(dy);
                t = Math.min(txr, tyr);
            }
        }
        return new double[]{cx + t * dx, cy + t * dy};
    }

    /** Returns [x1,y1, x2,y2] — both ends clipped to their respective shape borders. */
    private int[] edge(Node a, Node b) {
        double acx = a.x + a.w / 2.0, acy = a.y + a.h / 2.0;
        double bcx = b.x + b.w / 2.0, bcy = b.y + b.h / 2.0;
        double[] p1 = clipToShape(a, bcx, bcy);
        double[] p2 = clipToShape(b, acx, acy);
        return new int[]{(int) p1[0], (int) p1[1], (int) p2[0], (int) p2[1]};
    }

    // ── Shapes ────────────────────────────────────────────────────────────────

    private static Shape buildHex(int x, int y, int w, int h) {
        GeneralPath hex = new GeneralPath();
        double cx = x + w / 2.0, cy = y + h / 2.0;
        double rx = w / 2.0 - 2, ry = h / 2.0 - 2;
        for (int i = 0; i < 6; i++) {
            double a  = Math.PI / 3.0 * i;
            double px = cx + rx * Math.cos(a), py = cy + ry * Math.sin(a);
            if (i == 0) hex.moveTo(px, py); else hex.lineTo(px, py);
        }
        hex.closePath();
        return hex;
    }

    private static Shape buildCloud(int x, int y, int w, int h) {
        Area cloud = new Area();
        cloud.add(new Area(new Ellipse2D.Float(x + w * .15f, y + h * .30f, w * .70f, h * .55f)));
        cloud.add(new Area(new Ellipse2D.Float(x + w * .05f, y + h * .15f, w * .35f, h * .50f)));
        cloud.add(new Area(new Ellipse2D.Float(x + w * .30f, y,            w * .40f, h * .55f)));
        cloud.add(new Area(new Ellipse2D.Float(x + w * .58f, y + h * .10f, w * .35f, h * .48f)));
        cloud.add(new Area(new Ellipse2D.Float(x,            y + h * .35f, w * .25f, h * .50f)));
        cloud.add(new Area(new Ellipse2D.Float(x + w * .75f, y + h * .32f, w * .25f, h * .50f)));
        return cloud;
    }

    // ── Arrow helpers ─────────────────────────────────────────────────────────

    private void drawTArrow(Graphics2D g2, int x1, int y1, int x2, int y2, Color c) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double perp  = angle + Math.PI / 2;
        int sz = 9;
        g2.setColor(c); g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x2 - (int)(sz * Math.cos(perp)), y2 - (int)(sz * Math.sin(perp)),
                    x2 + (int)(sz * Math.cos(perp)), y2 + (int)(sz * Math.sin(perp)));
        g2.setStroke(new BasicStroke(1.5f));
    }

    private void drawFilledArrow(Graphics2D g2, int x1, int y1, int x2, int y2, Color c) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int sz = 8;
        int[] xp = {x2, x2-(int)(sz*Math.cos(angle-.42)), x2-(int)(sz*Math.cos(angle+.42))};
        int[] yp = {y2, y2-(int)(sz*Math.sin(angle-.42)), y2-(int)(sz*Math.sin(angle+.42))};
        g2.setColor(c); g2.fillPolygon(xp, yp, 3);
    }

    private void drawCentred(Graphics2D g2, String s, int cx, int cy, Font f, Color c) {
        g2.setFont(f); g2.setColor(c);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(s, cx - fm.stringWidth(s) / 2, cy);
    }

    // ── View interface ────────────────────────────────────────────────────────

    @Override public void detachModel() { model = null; repaint(); }
    public    void update()             { repaint(); }
}
