package org.vnu.sme.goal.bpmn2.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import org.tzi.use.gui.views.View;
import org.vnu.sme.goal.bpmn2.mm.*;

/**
 * BPMN 2.0 Collaboration diagram — interactive canvas.
 *
 * Interactions:
 *   Drag pool header (blue column)    → move entire pool (lanes + nodes follow)
 *   Drag pool border (E/W/N/S edge)   → resize pool; lanes auto-adjust
 *   Drag lane header (grey band)      → move lane vertically; nodes follow;
 *                                        adjacent lane shrinks/grows
 *   Drag flow node                    → move node; pool/lane auto-expands
 *   Right-click                       → Save Layout / Load Layout / Reset Layout
 *
 * Edges are drawn center-to-center, clipped to each node's shape border.
 */
public final class Bpmn2View extends JPanel implements View {

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color C_BG        = Color.WHITE;
    private static final Color C_POOL_HDR  = new Color(70, 100, 170);
    private static final Color C_POOL_HDR_T= Color.WHITE;
    private static final Color C_POOL_FILL = new Color(242, 245, 252);
    private static final Color C_POOL_BDR  = new Color(80, 110, 185);
    private static final Color C_LANE_FILL = new Color(252, 253, 255);
    private static final Color C_LANE_HDR  = new Color(210, 220, 240);
    private static final Color C_LANE_BDR  = new Color(170, 185, 215);
    private static final Color C_LANE_TXT  = new Color(50,  70, 130);
    private static final Color C_TASK_FILL = new Color(255, 255, 235);
    private static final Color C_TASK_BDR  = new Color(80,  90, 175);
    private static final Color C_TASK_TXT  = new Color(30,  30,  60);
    private static final Color C_EVT_FILL  = Color.WHITE;
    private static final Color C_EVT_TXT   = new Color(30,  30,  60);
    private static final Color C_START_BDR = new Color(25, 135,  55);
    private static final Color C_END_BDR   = new Color(195,  35,  35);
    private static final Color C_INT_BDR   = new Color(65,  95, 185);
    private static final Color C_GW_FILL   = new Color(255, 252, 235);
    private static final Color C_GW_BDR    = new Color(185, 140,  20);
    private static final Color C_GW_SYM    = new Color(140, 105,  15);
    private static final Color C_SEQ       = new Color(40,  40,  80);
    private static final Color C_MSG       = new Color(65, 100, 190);
    private static final Color C_COND_TXT  = new Color(80, 100, 130);
    private static final Color C_SUB_FILL  = new Color(240, 248, 255);
    private static final Color C_SUB_BDR   = new Color(75, 120, 200);
    private static final Color C_RESIZE_HDL= new Color(100, 140, 220);

    // ── Sizes ─────────────────────────────────────────────────────────────────
    private static final int MARGIN    = 20;
    private static final int POOL_HDR  = 26;
    private static final int LANE_HDR  = 52;
    private static final int LANE_H    = 110;
    private static final int POOL_PAD  = 14;
    private static final int ELEM_PAD  = 24;
    private static final int EVT_D     = 34;
    private static final int TASK_W    = 110; private static final int TASK_H = 55;
    private static final int GW_D      = 44;
    private static final int SUB_W     = 120; private static final int SUB_H = 58;
    private static final int RESIZE_TOL= 7;   // px tolerance for resize-handle detection
    private static final int LANE_MIN_H= 50;

    private static final Font FP  = new Font(Font.SANS_SERIF, Font.BOLD,  10);
    private static final Font FL  = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private static final Font FT  = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private static final Font FEV = new Font(Font.SANS_SERIF, Font.PLAIN,  9);
    private static final Font FC  = new Font(Font.SANS_SERIF, Font.ITALIC, 8);

    // ── Internal node model ───────────────────────────────────────────────────
    private enum NT { START_EVT, END_EVT, INT_EVT, TASK, SUBPROCESS, GATEWAY }

    private static class VNode {
        String    id, label;
        NT        kind;
        EventType evtType  = EventType.NONE;
        boolean   catching = true;
        GatewayType gwType = GatewayType.XOR;
        int x, y, w, h;
    }

    private static class VLane {
        String id, label;
        int x, y, w, h;                // absolute
        List<VNode> elements = new ArrayList<>();
    }

    private static class VPool {
        String id, label;
        int x, y, w, h;
        List<VLane> lanes    = new ArrayList<>();
        List<VNode> elements = new ArrayList<>(); // nodes without a lane

        List<VNode> allNodes() {
            List<VNode> all = new ArrayList<>(elements);
            for (VLane vl : lanes) all.addAll(vl.elements);
            return all;
        }
    }

    private final Map<String, VNode> nodeMap = new LinkedHashMap<>();
    private final List<VPool>        vPools  = new ArrayList<>();
    private Bpmn2Collaboration       model;
    private Path                     sourceFile;

    // ── Drag state ────────────────────────────────────────────────────────────
    private enum DragMode {
        NONE, POOL, LANE, NODE,
        RESIZE_N, RESIZE_S, RESIZE_E, RESIZE_W
    }

    private DragMode dragMode  = DragMode.NONE;
    private VPool    dragPool  = null;
    private VLane    dragLane  = null;
    private VNode    dragVNode = null;
    private int      dragX0, dragY0;

    // ── Constructor ───────────────────────────────────────────────────────────

    public Bpmn2View() {
        setBackground(C_BG);
        setPreferredSize(new Dimension(1200, 700));
        installInteraction();
    }

    public void setSourceFile(Path p) { this.sourceFile = p; }

    public void setModel(Bpmn2Collaboration m) {
        this.model = m;
        buildLayout();
        if (sourceFile != null) loadLayout();
        revalidate();
        repaint();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {
        nodeMap.clear();
        vPools.clear();
        if (model == null) return;

        int py = MARGIN;
        int maxW = 0;

        for (Pool pool : model.getPools()) {
            VPool vp = new VPool();
            vp.id    = pool.id();
            vp.label = pool.label();
            vp.x     = MARGIN;
            vp.y     = py;

            int contentX = MARGIN + POOL_HDR;
            int laneY    = py + POOL_PAD;

            for (Lane lane : pool.lanes()) {
                VLane vl = new VLane();
                vl.id    = lane.id();
                vl.label = lane.label();
                vl.x     = contentX;
                vl.y     = laneY;

                int ex = contentX + LANE_HDR + ELEM_PAD;
                int maxNodeH = 0;
                for (FlowNode fn : lane.elements()) {
                    VNode vn = toVNode(fn);
                    vn.x = ex;
                    vl.elements.add(vn);
                    nodeMap.put(vn.id, vn);
                    ex += vn.w + ELEM_PAD;
                    if (vn.h > maxNodeH) maxNodeH = vn.h;
                }
                vl.h = maxNodeH > 0 ? maxNodeH + ELEM_PAD * 2 : LANE_MIN_H;
                // Centre nodes vertically within the computed lane height
                for (VNode vn : vl.elements) vn.y = laneY + (vl.h - vn.h) / 2;
                vl.w = Math.max(ex - contentX + ELEM_PAD, 400);
                maxW = Math.max(maxW, MARGIN + POOL_HDR + vl.w);
                vp.lanes.add(vl);
                laneY += vl.h;
            }

            // pool-level elements (no lane)
            if (!pool.elements().isEmpty()) {
                int ex = contentX + ELEM_PAD;
                int rowY = laneY + POOL_PAD;
                for (FlowNode fn : pool.elements()) {
                    VNode vn = toVNode(fn);
                    vn.x = ex; vn.y = rowY;
                    vp.elements.add(vn);
                    nodeMap.put(vn.id, vn);
                    ex += vn.w + ELEM_PAD;
                    maxW = Math.max(maxW, ex + MARGIN);
                }
                laneY += LANE_H + POOL_PAD;
            }

            vp.h = laneY - py + POOL_PAD;
            vPools.add(vp);
            py += vp.h + MARGIN;
        }

        // Normalise all pools and lanes to the same width
        for (VPool vp : vPools) {
            vp.w = maxW - MARGIN;
            for (VLane vl : vp.lanes) vl.w = vp.w - POOL_HDR;
        }

        setPreferredSize(new Dimension(Math.max(maxW + MARGIN, 900), Math.max(py + MARGIN, 600)));
    }

    private VNode toVNode(FlowNode fn) {
        VNode vn = new VNode();
        vn.id = fn.id();
        switch (fn) {
            case FlowNode.StartEvent se -> {
                vn.kind = NT.START_EVT; vn.evtType = se.type();
                vn.label = se.id(); vn.w = EVT_D; vn.h = EVT_D;
            }
            case FlowNode.EndEvent ee -> {
                vn.kind = NT.END_EVT; vn.evtType = ee.type();
                vn.label = ee.id(); vn.w = EVT_D; vn.h = EVT_D;
            }
            case FlowNode.IntermediateEvent ie -> {
                vn.kind = NT.INT_EVT; vn.evtType = ie.type(); vn.catching = ie.catching();
                vn.label = ie.id(); vn.w = EVT_D; vn.h = EVT_D;
            }
            case FlowNode.Task t -> {
                vn.kind = NT.TASK; vn.label = t.label(); vn.w = TASK_W; vn.h = TASK_H;
            }
            case FlowNode.SubProcess sp -> {
                vn.kind = NT.SUBPROCESS; vn.label = sp.label(); vn.w = SUB_W; vn.h = SUB_H;
            }
            case FlowNode.Gateway gw -> {
                vn.kind = NT.GATEWAY; vn.gwType = gw.type();
                vn.label = gw.id(); vn.w = GW_D; vn.h = GW_D;
            }
        }
        return vn;
    }

    // ── Interaction ───────────────────────────────────────────────────────────

    private void installInteraction() {
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragX0 = e.getX(); dragY0 = e.getY();
                if (e.isPopupTrigger()) { showContextMenu(e); dragMode = DragMode.NONE; return; }
                // Priority: resize handle > node > lane header > pool header
                for (VPool vp : vPools) {
                    DragMode rm = hitResizeHandle(e.getX(), e.getY(), vp);
                    if (rm != DragMode.NONE) { dragMode = rm; dragPool = vp; return; }
                }
                VNode vn = hitVNode(e.getX(), e.getY());
                if (vn != null) { dragMode = DragMode.NODE; dragVNode = vn; return; }
                for (VPool vp : vPools) {
                    for (VLane vl : vp.lanes) {
                        if (hitLaneHeader(e.getX(), e.getY(), vl)) {
                            dragMode = DragMode.LANE; dragLane = vl; dragPool = vp; return;
                        }
                    }
                    if (hitPoolHeader(e.getX(), e.getY(), vp)) {
                        dragMode = DragMode.POOL; dragPool = vp; return;
                    }
                }
                dragMode = DragMode.NONE;
            }

            @Override public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
                dragMode = DragMode.NONE;
                dragPool = null; dragLane = null; dragVNode = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - dragX0, dy = e.getY() - dragY0;
                dragX0 = e.getX(); dragY0 = e.getY();
                switch (dragMode) {
                    case POOL    -> movePool(dragPool, dx, dy);
                    case LANE    -> moveLane(dragPool, dragLane, dy);
                    case NODE    -> moveNode(dragVNode, dx, dy);
                    case RESIZE_E -> resizePoolE(dragPool, dx);
                    case RESIZE_W -> resizePoolW(dragPool, dx);
                    case RESIZE_S -> resizePoolS(dragPool, dy);
                    case RESIZE_N -> resizePoolN(dragPool, dy);
                    default -> {}
                }
                updatePreferredSize();
                repaint();
            }

            @Override public void mouseMoved(MouseEvent e) {
                // Change cursor near resize handles
                Cursor c = Cursor.getDefaultCursor();
                outer: for (VPool vp : vPools) {
                    DragMode rm = hitResizeHandle(e.getX(), e.getY(), vp);
                    switch (rm) {
                        case RESIZE_E, RESIZE_W -> { c = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); break outer; }
                        case RESIZE_N, RESIZE_S -> { c = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); break outer; }
                        default -> {}
                    }
                }
                setCursor(c);
            }
        });
    }

    // ── Hit-test helpers ──────────────────────────────────────────────────────

    private DragMode hitResizeHandle(int x, int y, VPool vp) {
        boolean inY  = y >= vp.y - RESIZE_TOL && y <= vp.y + vp.h + RESIZE_TOL;
        boolean inX  = x >= vp.x - RESIZE_TOL && x <= vp.x + vp.w + RESIZE_TOL;
        boolean onN  = Math.abs(y - vp.y)         <= RESIZE_TOL && inX;
        boolean onS  = Math.abs(y - (vp.y + vp.h)) <= RESIZE_TOL && inX;
        boolean onE  = Math.abs(x - (vp.x + vp.w)) <= RESIZE_TOL && inY;
        boolean onW  = Math.abs(x - vp.x)           <= RESIZE_TOL && inY;
        // Avoid triggering on pool header interior
        if (onN) return DragMode.RESIZE_N;
        if (onS) return DragMode.RESIZE_S;
        if (onE) return DragMode.RESIZE_E;
        if (onW) return DragMode.RESIZE_W;
        return DragMode.NONE;
    }

    private boolean hitPoolHeader(int x, int y, VPool vp) {
        return x >= vp.x && x <= vp.x + POOL_HDR
                && y >= vp.y && y <= vp.y + vp.h;
    }

    private boolean hitLaneHeader(int x, int y, VLane vl) {
        return x >= vl.x && x <= vl.x + LANE_HDR
                && y >= vl.y && y <= vl.y + vl.h;
    }

    private VNode hitVNode(int x, int y) {
        for (VNode n : nodeMap.values())
            if (x >= n.x && x <= n.x + n.w && y >= n.y && y <= n.y + n.h) return n;
        return null;
    }

    // ── Drag operations ───────────────────────────────────────────────────────

    private void movePool(VPool vp, int dx, int dy) {
        vp.x += dx; vp.y += dy;
        for (VLane vl : vp.lanes) {
            vl.x += dx; vl.y += dy;
            for (VNode vn : vl.elements) { vn.x += dx; vn.y += dy; }
        }
        for (VNode vn : vp.elements) { vn.x += dx; vn.y += dy; }
    }

    private void moveLane(VPool vp, VLane vl, int dy) {
        int idx = vp.lanes.indexOf(vl);
        if (dy < 0) {
            // Moving up: shrink previous lane
            if (idx > 0) {
                VLane prev = vp.lanes.get(idx - 1);
                int newH = Math.max(LANE_MIN_H, prev.h + dy);
                int actualDy = newH - prev.h;
                prev.h = newH;
                vl.y += actualDy;
                for (VNode vn : vl.elements) vn.y += actualDy;
                // Shift subsequent lanes
                for (int i = idx + 1; i < vp.lanes.size(); i++) {
                    vp.lanes.get(i).y += actualDy;
                    for (VNode vn : vp.lanes.get(i).elements) vn.y += actualDy;
                }
                recalcPoolHeight(vp);
            }
        } else {
            // Moving down: grow this lane (push next lane down)
            int newH = vl.h + dy;
            vl.h = newH;
            for (int i = idx + 1; i < vp.lanes.size(); i++) {
                vp.lanes.get(i).y += dy;
                for (VNode vn : vp.lanes.get(i).elements) vn.y += dy;
            }
            recalcPoolHeight(vp);
        }
    }

    private void moveNode(VNode vn, int dx, int dy) {
        vn.x += dx; vn.y += dy;
        // Auto-expand pool width if node goes beyond right border
        for (VPool vp : vPools) {
            if (!containsNode(vp, vn)) continue;
            if (vn.x + vn.w + ELEM_PAD > vp.x + vp.w) {
                int newW = vn.x + vn.w + ELEM_PAD - vp.x;
                vp.w = newW;
                for (VLane vl : vp.lanes) vl.w = vp.w - POOL_HDR;
            }
            // Auto-expand lane height if node goes below lane bottom
            for (VLane vl : vp.lanes) {
                if (!vl.elements.contains(vn)) continue;
                if (vn.y + vn.h + POOL_PAD > vl.y + vl.h) {
                    int excess = (vn.y + vn.h + POOL_PAD) - (vl.y + vl.h);
                    vl.h += excess;
                    int idx = vp.lanes.indexOf(vl);
                    for (int i = idx + 1; i < vp.lanes.size(); i++) {
                        vp.lanes.get(i).y += excess;
                        for (VNode nvn : vp.lanes.get(i).elements) nvn.y += excess;
                    }
                    recalcPoolHeight(vp);
                }
                break;
            }
            break;
        }
    }

    private void resizePoolE(VPool vp, int dx) {
        vp.w = Math.max(200, vp.w + dx);
        for (VLane vl : vp.lanes) vl.w = vp.w - POOL_HDR;
    }

    private void resizePoolW(VPool vp, int dx) {
        int newW = Math.max(200, vp.w - dx);
        int actualDx = vp.w - newW;
        vp.x += actualDx; vp.w = newW;
        for (VLane vl : vp.lanes) {
            vl.x += actualDx; vl.w = vp.w - POOL_HDR;
            for (VNode vn : vl.elements) vn.x += actualDx;
        }
        for (VNode vn : vp.elements) vn.x += actualDx;
    }

    private void resizePoolS(VPool vp, int dy) {
        vp.h = Math.max(LANE_MIN_H + POOL_PAD * 2, vp.h + dy);
        // Grow last lane to fill
        if (!vp.lanes.isEmpty()) {
            VLane last = vp.lanes.get(vp.lanes.size() - 1);
            int lanesBottom = last.y + last.h;
            int poolBottom  = vp.y + vp.h - POOL_PAD;
            if (poolBottom > lanesBottom) last.h += poolBottom - lanesBottom;
            else if (poolBottom < lanesBottom) last.h = Math.max(LANE_MIN_H, last.h + (poolBottom - lanesBottom));
        }
    }

    private void resizePoolN(VPool vp, int dy) {
        int newH = Math.max(LANE_MIN_H + POOL_PAD * 2, vp.h - dy);
        int delta = vp.h - newH;
        vp.y += delta; vp.h = newH;
        for (VLane vl : vp.lanes) {
            vl.y += delta;
            for (VNode vn : vl.elements) vn.y += delta;
        }
        for (VNode vn : vp.elements) vn.y += delta;
        if (!vp.lanes.isEmpty()) {
            VLane first = vp.lanes.get(0);
            first.y += 0; // already shifted above
            first.h = Math.max(LANE_MIN_H, first.h - delta);
        }
    }

    private void recalcPoolHeight(VPool vp) {
        if (vp.lanes.isEmpty()) return;
        VLane last = vp.lanes.get(vp.lanes.size() - 1);
        vp.h = last.y + last.h - vp.y + POOL_PAD;
    }

    private boolean containsNode(VPool vp, VNode vn) {
        if (vp.elements.contains(vn)) return true;
        for (VLane vl : vp.lanes) if (vl.elements.contains(vn)) return true;
        return false;
    }

    private void updatePreferredSize() {
        int maxX = vPools.stream().mapToInt(vp -> vp.x + vp.w).max().orElse(900) + MARGIN;
        int maxY = vPools.stream().mapToInt(vp -> vp.y + vp.h).max().orElse(600) + MARGIN;
        setPreferredSize(new Dimension(Math.max(maxX, 900), Math.max(maxY, 600)));
        revalidate();
    }

    // ── Right-click menu ──────────────────────────────────────────────────────

    private void showContextMenu(MouseEvent e) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem saveItem = new JMenuItem("Save Layout");
        saveItem.addActionListener(ev -> {
            saveLayout();
            JOptionPane.showMessageDialog(Bpmn2View.this, "Layout saved.", "BPMN 2.0",
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
        for (VPool vp : vPools) {
            p.setProperty("pool." + vp.id + ".x", String.valueOf(vp.x));
            p.setProperty("pool." + vp.id + ".y", String.valueOf(vp.y));
            p.setProperty("pool." + vp.id + ".w", String.valueOf(vp.w));
            p.setProperty("pool." + vp.id + ".h", String.valueOf(vp.h));
            for (VLane vl : vp.lanes) {
                p.setProperty("lane." + vl.id + ".x", String.valueOf(vl.x));
                p.setProperty("lane." + vl.id + ".y", String.valueOf(vl.y));
                p.setProperty("lane." + vl.id + ".w", String.valueOf(vl.w));
                p.setProperty("lane." + vl.id + ".h", String.valueOf(vl.h));
            }
        }
        for (VNode vn : nodeMap.values()) {
            p.setProperty("node." + vn.id + ".x", String.valueOf(vn.x));
            p.setProperty("node." + vn.id + ".y", String.valueOf(vn.y));
        }
        try (OutputStream os = Files.newOutputStream(lp)) {
            p.store(os, "BPMN 2.0 Layout");
        } catch (IOException ex) { /* ignore */ }
    }

    private void loadLayout() {
        Path lp = layoutPath();
        if (lp == null || !Files.exists(lp)) return;
        java.util.Properties p = new java.util.Properties();
        try (InputStream is = Files.newInputStream(lp)) {
            p.load(is);
            for (VPool vp : vPools) {
                vp.x = parseInt(p, "pool." + vp.id + ".x", vp.x);
                vp.y = parseInt(p, "pool." + vp.id + ".y", vp.y);
                vp.w = parseInt(p, "pool." + vp.id + ".w", vp.w);
                vp.h = parseInt(p, "pool." + vp.id + ".h", vp.h);
                for (VLane vl : vp.lanes) {
                    vl.x = parseInt(p, "lane." + vl.id + ".x", vl.x);
                    vl.y = parseInt(p, "lane." + vl.id + ".y", vl.y);
                    vl.w = parseInt(p, "lane." + vl.id + ".w", vl.w);
                    vl.h = parseInt(p, "lane." + vl.id + ".h", vl.h);
                }
            }
            for (VNode vn : nodeMap.values()) {
                vn.x = parseInt(p, "node." + vn.id + ".x", vn.x);
                vn.y = parseInt(p, "node." + vn.id + ".y", vn.y);
            }
        } catch (IOException ex) { /* ignore */ }
        updatePreferredSize();
    }

    private static int parseInt(java.util.Properties p, String key, int dflt) {
        String v = p.getProperty(key);
        if (v == null) return dflt;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return dflt; }
    }

    // ── Paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        paintPools(g2);
        paintSequenceFlows(g2);
        paintMessageFlows(g2);
        paintNodes(g2);
        if (dragPool != null && dragMode != DragMode.NONE) paintResizeHandles(g2, dragPool);
        // Always show handles for hovered pool (handled via cursor change; skip for brevity)
    }

    private void paintPools(Graphics2D g2) {
        for (VPool vp : vPools) {
            g2.setColor(C_POOL_FILL);
            g2.fillRect(vp.x, vp.y, vp.w, vp.h);

            g2.setColor(C_POOL_HDR);
            g2.fillRect(vp.x, vp.y, POOL_HDR, vp.h);

            g2.setColor(C_POOL_BDR); g2.setStroke(new BasicStroke(2f));
            g2.drawRect(vp.x, vp.y, vp.w, vp.h);
            g2.setStroke(new BasicStroke(1));

            drawRotated(g2, vp.label, vp.x + POOL_HDR / 2, vp.y + vp.h / 2, FP, C_POOL_HDR_T);

            for (VLane vl : vp.lanes) {
                g2.setColor(C_LANE_FILL);
                g2.fillRect(vl.x, vl.y, vl.w, vl.h);

                g2.setColor(C_LANE_HDR);
                g2.fillRect(vl.x, vl.y, LANE_HDR, vl.h);

                g2.setColor(C_LANE_BDR); g2.setStroke(new BasicStroke(1f));
                g2.drawLine(vl.x + LANE_HDR, vl.y, vl.x + LANE_HDR, vl.y + vl.h);
                g2.drawRect(vl.x, vl.y, vl.w, vl.h);
                g2.setStroke(new BasicStroke(1));

                drawRotated(g2, vl.label, vl.x + LANE_HDR / 2, vl.y + vl.h / 2, FL, C_LANE_TXT);
            }
        }
    }

    private void paintResizeHandles(Graphics2D g2, VPool vp) {
        g2.setColor(C_RESIZE_HDL);
        int[] xs = {vp.x,              vp.x + vp.w / 2, vp.x + vp.w};
        int[] ys = {vp.y,              vp.y + vp.h / 2, vp.y + vp.h};
        for (int xi : new int[]{0, 2}) for (int yi : new int[]{0, 1, 2})
            g2.fillRect(xs[xi] - 4, ys[yi] - 4, 8, 8);
        for (int yi : new int[]{0, 2})
            g2.fillRect(xs[1] - 4, ys[yi] - 4, 8, 8);
    }

    private void drawRotated(Graphics2D g2, String text, int cx, int cy, Font f, Color c) {
        g2.setFont(f); g2.setColor(c);
        FontMetrics fm = g2.getFontMetrics();
        AffineTransform orig = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(-Math.PI / 2);
        g2.drawString(text, -fm.stringWidth(text) / 2, fm.getAscent() / 2);
        g2.setTransform(orig);
    }

    // ── Nodes ─────────────────────────────────────────────────────────────────

    private void paintNodes(Graphics2D g2) {
        for (VPool vp : vPools) {
            for (VLane vl : vp.lanes) for (VNode vn : vl.elements) paintNode(g2, vn);
            for (VNode vn : vp.elements) paintNode(g2, vn);
        }
    }

    private void paintNode(Graphics2D g2, VNode vn) {
        switch (vn.kind) {
            case START_EVT  -> paintStartEvt(g2, vn);
            case END_EVT    -> paintEndEvt(g2, vn);
            case INT_EVT    -> paintIntEvt(g2, vn);
            case TASK       -> paintTask(g2, vn);
            case SUBPROCESS -> paintSubProcess(g2, vn);
            case GATEWAY    -> paintGateway(g2, vn);
        }
        paintLabel(g2, vn);
    }

    private void paintStartEvt(Graphics2D g2, VNode vn) {
        g2.setColor(C_EVT_FILL);  g2.fillOval(vn.x, vn.y, vn.w, vn.h);
        g2.setColor(C_START_BDR); g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(vn.x, vn.y, vn.w, vn.h);
        paintEvtIcon(g2, vn, C_START_BDR, false);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintEndEvt(Graphics2D g2, VNode vn) {
        g2.setColor(C_EVT_FILL); g2.fillOval(vn.x, vn.y, vn.w, vn.h);
        g2.setColor(C_END_BDR);  g2.setStroke(new BasicStroke(3.5f));
        g2.drawOval(vn.x, vn.y, vn.w, vn.h);
        paintEvtIcon(g2, vn, C_END_BDR, true);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintIntEvt(Graphics2D g2, VNode vn) {
        g2.setColor(C_EVT_FILL); g2.fillOval(vn.x, vn.y, vn.w, vn.h);
        g2.setColor(C_INT_BDR);  g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(vn.x, vn.y, vn.w, vn.h);
        int gap = 4;
        g2.drawOval(vn.x + gap, vn.y + gap, vn.w - gap * 2, vn.h - gap * 2);
        paintEvtIcon(g2, vn, C_INT_BDR, !vn.catching);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintEvtIcon(Graphics2D g2, VNode vn, Color c, boolean fill) {
        int cx = vn.x + vn.w / 2, cy = vn.y + vn.h / 2, r = vn.w / 2 - 9;
        if (r < 3) return;
        g2.setColor(c); g2.setStroke(new BasicStroke(1.2f));
        switch (vn.evtType) {
            case MESSAGE -> {
                g2.drawRect(cx - r, cy - r / 2, r * 2, r);
                g2.drawLine(cx - r, cy - r / 2, cx, cy);
                g2.drawLine(cx, cy, cx + r, cy - r / 2);
            }
            case TIMER -> {
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.drawLine(cx, cy, cx, cy - r + 2);
                g2.drawLine(cx, cy, cx + r / 2, cy);
            }
            case ERROR -> {
                int[] xp = {cx - r/2, cx - r/5, cx + r/5, cx + r/2};
                int[] yp = {cy - r,   cy,        cy - r/2, cy + r};
                if (fill) g2.fillPolygon(xp, yp, 4); else g2.drawPolyline(xp, yp, 4);
            }
            case SIGNAL -> {
                int[] xp = {cx, cx - r, cx + r};
                int[] yp = {cy - r, cy + r/2, cy + r/2};
                if (fill) g2.fillPolygon(xp, yp, 3); else g2.drawPolygon(xp, yp, 3);
            }
            case TERMINATE -> g2.fillOval(cx - r + 2, cy - r + 2, (r - 2) * 2, (r - 2) * 2);
            case COMPENSATION -> {
                g2.fillPolygon(new int[]{cx-2,cx-2-r/2,cx-2-r/2}, new int[]{cy,cy-r/2,cy+r/2}, 3);
                g2.fillPolygon(new int[]{cx+2,cx+2,cx+2+r/2},     new int[]{cy-r/2,cy+r/2,cy}, 3);
            }
            case CONDITIONAL -> {
                g2.drawRect(cx - r, cy - r, r * 2, r * 2);
                for (int i = 1; i <= 3; i++) {
                    int lineY = cy - r + i * r * 2 / 4;
                    g2.drawLine(cx - r + 2, lineY, cx + r - 2, lineY);
                }
            }
            default -> {}
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void paintTask(Graphics2D g2, VNode vn) {
        g2.setColor(C_TASK_FILL); g2.fillRoundRect(vn.x, vn.y, vn.w, vn.h, 8, 8);
        g2.setColor(C_TASK_BDR);  g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(vn.x, vn.y, vn.w, vn.h, 8, 8);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintSubProcess(Graphics2D g2, VNode vn) {
        g2.setColor(C_SUB_FILL); g2.fillRoundRect(vn.x, vn.y, vn.w, vn.h, 8, 8);
        g2.setColor(C_SUB_BDR);  g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(vn.x, vn.y, vn.w, vn.h, 8, 8);
        int mx = vn.x + vn.w / 2, my = vn.y + vn.h - 10;
        g2.drawRect(mx - 6, my - 6, 12, 12);
        g2.drawLine(mx, my - 4, mx, my + 4);
        g2.drawLine(mx - 4, my, mx + 4, my);
        g2.setStroke(new BasicStroke(1));
    }

    private void paintGateway(Graphics2D g2, VNode vn) {
        int cx = vn.x + vn.w / 2, cy = vn.y + vn.h / 2;
        int hw = vn.w / 2, hh = vn.h / 2;
        int[] xp = {cx, cx + hw, cx, cx - hw};
        int[] yp = {cy - hh, cy, cy + hh, cy};
        g2.setColor(C_GW_FILL); g2.fillPolygon(xp, yp, 4);
        g2.setColor(C_GW_BDR);  g2.setStroke(new BasicStroke(1.8f));
        g2.drawPolygon(xp, yp, 4); g2.setStroke(new BasicStroke(2f));

        int sr = hh - 9;
        g2.setColor(C_GW_SYM);
        if (sr > 2) switch (vn.gwType) {
            case XOR -> {
                g2.drawLine(cx - sr, cy - sr, cx + sr, cy + sr);
                g2.drawLine(cx + sr, cy - sr, cx - sr, cy + sr);
            }
            case AND -> {
                g2.drawLine(cx, cy - sr, cx, cy + sr);
                g2.drawLine(cx - sr, cy, cx + sr, cy);
            }
            case OR -> g2.drawOval(cx - sr, cy - sr, sr * 2, sr * 2);
            case EVENT_BASED -> {
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - sr - 3, cy - sr - 3, (sr + 3) * 2, (sr + 3) * 2);
                for (int i = 0; i < 5; i++) {
                    double a1 = 2 * Math.PI * i / 5 - Math.PI / 2;
                    double a2 = 2 * Math.PI * (i + 1) / 5 - Math.PI / 2;
                    g2.drawLine((int)(cx + sr * Math.cos(a1)), (int)(cy + sr * Math.sin(a1)),
                                (int)(cx + sr * Math.cos(a2)), (int)(cy + sr * Math.sin(a2)));
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void paintLabel(Graphics2D g2, VNode vn) {
        if (vn.label == null || vn.label.isBlank()) return;
        if (vn.kind == NT.TASK || vn.kind == NT.SUBPROCESS) {
            g2.setFont(FT); g2.setColor(C_TASK_TXT);
            FontMetrics fm = g2.getFontMetrics();
            String txt = vn.label;
            while (fm.stringWidth(txt) > vn.w - 10 && txt.length() > 3)
                txt = txt.substring(0, txt.length() - 1);
            g2.drawString(txt, vn.x + (vn.w - fm.stringWidth(txt)) / 2,
                          vn.y + vn.h / 2 + fm.getAscent() / 2 - 2);
        } else {
            g2.setFont(FEV); g2.setColor(C_EVT_TXT);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(vn.label, vn.x + (vn.w - fm.stringWidth(vn.label)) / 2,
                          vn.y + vn.h + 11);
        }
    }

    // ── Sequence flows ────────────────────────────────────────────────────────

    private void paintSequenceFlows(Graphics2D g2) {
        if (model == null) return;
        for (Pool pool : model.getPools()) {
            for (SequenceFlow sf : pool.flows()) {
                VNode src = nodeMap.get(sf.source()), tgt = nodeMap.get(sf.target());
                if (src == null || tgt == null) continue;
                g2.setColor(C_SEQ); g2.setStroke(new BasicStroke(1.5f));
                int[] p = edge(src, tgt);
                g2.drawLine(p[0], p[1], p[2], p[3]);
                drawFilledArrow(g2, p[0], p[1], p[2], p[3], C_SEQ);
                if (sf.condition() != null) {
                    g2.setFont(FC); g2.setColor(C_COND_TXT);
                    g2.drawString(sf.condition(), (p[0] + p[2]) / 2 + 3, (p[1] + p[3]) / 2 - 4);
                }
                g2.setStroke(new BasicStroke(1));
            }
        }
    }

    // ── Message flows ─────────────────────────────────────────────────────────

    private void paintMessageFlows(Graphics2D g2) {
        if (model == null) return;
        Stroke dashed = new BasicStroke(1.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{6, 4}, 0);
        for (MessageFlow mf : model.getMessageFlows()) {
            VNode src = nodeMap.get(mf.source()), tgt = nodeMap.get(mf.target());
            if (src == null || tgt == null) continue;
            int[] p = edge(src, tgt);

            g2.setColor(C_MSG); g2.setStroke(dashed);
            g2.drawLine(p[0], p[1], p[2], p[3]);
            g2.setStroke(new BasicStroke(1.4f));

            // small circle at source, hollow arrowhead at target
            g2.setColor(Color.WHITE); g2.fillOval(p[0] - 5, p[1] - 5, 10, 10);
            g2.setColor(C_MSG);       g2.drawOval(p[0] - 5, p[1] - 5, 10, 10);

            double angle = Math.atan2(p[3] - p[1], p[2] - p[0]);
            int sz = 9;
            int[] xp = {p[2], p[2]-(int)(sz*Math.cos(angle-.45)), p[2]-(int)(sz*Math.cos(angle+.45))};
            int[] yp = {p[3], p[3]-(int)(sz*Math.sin(angle-.45)), p[3]-(int)(sz*Math.sin(angle+.45))};
            g2.setColor(Color.WHITE); g2.fillPolygon(xp, yp, 3);
            g2.setColor(C_MSG);       g2.drawPolygon(xp, yp, 3);

            if (mf.label() != null) {
                g2.setFont(FC); g2.setColor(C_COND_TXT);
                g2.drawString(mf.label(), (p[0] + p[2]) / 2 + 3, (p[1] + p[3]) / 2 - 4);
            }
            g2.setStroke(new BasicStroke(1));
        }
    }

    // ── Edge clipping ─────────────────────────────────────────────────────────

    private static double[] clipToShape(VNode n, double tx, double ty) {
        double cx = n.x + n.w / 2.0, cy = n.y + n.h / 2.0;
        double dx = tx - cx, dy = ty - cy;
        if (Math.abs(dx) < 0.5 && Math.abs(dy) < 0.5) return new double[]{cx, cy};
        double hw = n.w / 2.0, hh = n.h / 2.0;
        double t;
        switch (n.kind) {
            case START_EVT, END_EVT, INT_EVT -> {
                // Circle (ellipse with equal axes)
                double dxr = dx / hw, dyr = dy / hh;
                t = 1.0 / Math.sqrt(dxr * dxr + dyr * dyr);
            }
            case GATEWAY -> {
                // Diamond: |dx|/hw + |dy|/hh = 1  →  t = 1 / (|dx/hw| + |dy/hh|)
                double dxr = Math.abs(dx) / hw, dyr = Math.abs(dy) / hh;
                t = 1.0 / (dxr + dyr + 1e-9);
            }
            default -> {
                // Rounded rect ≈ rectangle for clipping
                double txr = Math.abs(dx) < 1e-9 ? Double.MAX_VALUE : hw / Math.abs(dx);
                double tyr = Math.abs(dy) < 1e-9 ? Double.MAX_VALUE : hh / Math.abs(dy);
                t = Math.min(txr, tyr);
            }
        }
        return new double[]{cx + t * dx, cy + t * dy};
    }

    private int[] edge(VNode a, VNode b) {
        double acx = a.x + a.w / 2.0, acy = a.y + a.h / 2.0;
        double bcx = b.x + b.w / 2.0, bcy = b.y + b.h / 2.0;
        double[] p1 = clipToShape(a, bcx, bcy);
        double[] p2 = clipToShape(b, acx, acy);
        return new int[]{(int) p1[0], (int) p1[1], (int) p2[0], (int) p2[1]};
    }

    // ── Arrow helpers ─────────────────────────────────────────────────────────

    private void drawFilledArrow(Graphics2D g2, int x1, int y1, int x2, int y2, Color c) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int sz = 9;
        int[] xp = {x2, x2-(int)(sz*Math.cos(angle-.42)), x2-(int)(sz*Math.cos(angle+.42))};
        int[] yp = {y2, y2-(int)(sz*Math.sin(angle-.42)), y2-(int)(sz*Math.sin(angle+.42))};
        g2.setColor(c); g2.fillPolygon(xp, yp, 3);
    }

    // ── View interface ────────────────────────────────────────────────────────

    @Override public void detachModel() { model = null; repaint(); }
    public    void update()             { repaint(); }
}
