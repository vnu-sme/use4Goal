# Bước 4 — Thiết kế Diagram View

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/view/
    <Lang>View.java          # JPanel custom rendering
    <Lang>BpmnView.java      # (nếu có transform sang BPMN)
```

---

## Kiến trúc View thực tế trong project

> **Quan trọng — không làm theo pattern cũ**: Plugin trong project này (MAXGoalView, MAXBpmnView)
> KHÔNG kế thừa từ `PlaceableNode`, `CompartmentNode`, hay `DiagramView` của USE GUI.
> Các class đó chỉ dùng cho USE core ClassDiagram (ClassNode, EnumNode, etc.).
>
> Plugin view của chúng ta là **JPanel custom** với:
> - Internal node model (class `Node` với x, y, w, h)
> - Toàn bộ rendering trong `paintComponent(Graphics2D)`
> - Drag bằng `MouseListener` trên chính JPanel
> - Không dùng `null layout + setBounds` cho component con

---

## Pattern chuẩn: JPanel + internal Node model

```java
package org.vnu.sme.<plugin>.view;

import org.tzi.use.gui.views.View;   // USE View interface — chỉ cần implement

public final class <Lang>View extends JPanel implements View {

    // ── Màu sắc (dark palette như MAXGoalView) ─────────────────────
    private static final Color C_BG       = new Color(22,  24,  34);
    private static final Color C_ACTOR_BG = new Color(33,  37,  52);
    // ... các màu khác theo concept

    // ── Internal node model (không phải Swing component) ───────────
    private enum NT { ACTOR, GOAL, TASK, RES }  // hoặc enum riêng của ngôn ngữ

    private static class Node {
        String     id, label, clause;
        NT         kind;
        String     actorId;          // nếu có container
        RefineSpec refine;           // từ MM
        int        x, y, w, h;      // absolute coords trong canvas
    }

    private final Map<String, Node> nodes      = new LinkedHashMap<>();
    private final List<Node>        actorNodes = new ArrayList<>();
    private <Lang>Model             model;

    public <Lang>View() {
        setBackground(C_BG);
        setPreferredSize(new Dimension(1400, 800));
    }

    // Gọi từ Form sau khi compile xong
    public void setModel(<Lang>Model m) {
        this.model = m;
        buildLayout();   // Bước 6: tính x,y,w,h cho mỗi Node
        revalidate();
        repaint();
    }

    // ── Paint pipeline (thứ tự quan trọng) ─────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        paintActors(g2);        // Layer 1: container boxes
        paintRefineEdges(g2);   // Layer 2: cạnh refinement
        paintDependEdges(g2);   // Layer 3: cạnh dependency
        paintNodes(g2);         // Layer 4: nodes (vẽ sau để đè lên cạnh)
    }

    // ── View interface ──────────────────────────────────────────────
    @Override public void detachModel() { model = null; repaint(); }
    @Override public void update()      { repaint(); }
}
```

---

## buildLayout(): tính vị trí node

Layout algorithm phản ánh đúng cấu trúc semantic của ngôn ngữ:

```java
private void buildLayout() {
    nodes.clear();
    actorNodes.clear();
    if (model == null) return;

    int ax = MARGIN;
    for (Actor a : model.getActors()) {
        Node actor   = new Node();
        actor.id     = a.name();
        actor.label  = a.name() + " «" + a.kind().name().toLowerCase() + "»";
        actor.kind   = NT.ACTOR;
        actor.x      = ax;
        actor.y      = MARGIN;

        int maxW = 0, cy = MARGIN + TITLEH + PAD;
        List<Node> children = new ArrayList<>();

        for (Intentional item : a.intentionals()) {
            Node n = new Node();
            n.id      = item.name();
            n.actorId = a.name();

            // Kích thước theo loại concept
            switch (item) {
                case GoalDef g -> {
                    n.label  = g.name();
                    n.clause = g.intentClause();
                    n.kind   = NT.GOAL;
                    n.refine = g.refine();
                    n.w = GW; n.h = GH;      // GW=138, GH=44
                }
                case TaskDef t -> {
                    n.label  = t.name();
                    n.kind   = NT.TASK;
                    n.refine = t.refine();
                    n.w = TW; n.h = TH;      // TW=122, TH=36
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

        // Căn giữa các node trong actor
        for (Node cn : children) cn.x = ax + (actor.w - cn.w) / 2;

        nodes.put(actor.id, actor);
        actorNodes.add(actor);
        ax += actor.w + HGAP;  // HGAP=50: khoảng cách giữa actors
    }

    int totalW = ax + MARGIN;
    int maxH   = actorNodes.stream()
                    .mapToInt(n -> n.y + n.h).max().orElse(400) + MARGIN;
    setPreferredSize(new Dimension(Math.max(totalW, 800), Math.max(maxH, 600)));
    installDrag();
}
```

---

## Drag and drop (internal Node model)

Drag dùng `MouseListener`/`MouseMotionListener` trên JPanel:

```java
private void installDrag() {
    // Xoá listener cũ trước (tránh accumulate)
    for (MouseMotionListener ml : getMouseMotionListeners()) removeMouseMotionListener(ml);
    for (MouseListener ml : getMouseListeners()) removeMouseListener(ml);

    int[] drag = {0, 0, 0, 0};  // [prevX, prevY, lastMouseX, lastMouseY]

    addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            Node h = hitTestNonActor(e.getX(), e.getY());
            if (h != null) {
                drag[2] = e.getX();
                drag[3] = e.getY();
            }
        }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
        @Override public void mouseDragged(MouseEvent e) {
            Node h = hitTestNonActor(e.getX(), e.getY());
            if (h == null) return;
            h.x += e.getX() - drag[2];
            h.y += e.getY() - drag[3];
            drag[2] = e.getX();
            drag[3] = e.getY();
            recomputeActorBounds(h.actorId);
            repaint();
        }
    });
}

private Node hitTestNonActor(int x, int y) {
    for (Node n : nodes.values())
        if (n.kind != NT.ACTOR &&
            x >= n.x && x <= n.x + n.w &&
            y >= n.y && y <= n.y + n.h)
            return n;
    return null;
}

private void recomputeActorBounds(String actorId) {
    Node actor = nodes.get(actorId);
    if (actor == null) return;
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    int maxX = 0, maxY = 0;
    for (Node n : nodes.values()) {
        if (actorId.equals(n.actorId)) {
            minX = Math.min(minX, n.x);   minY = Math.min(minY, n.y);
            maxX = Math.max(maxX, n.x + n.w); maxY = Math.max(maxY, n.y + n.h);
        }
    }
    if (minX == Integer.MAX_VALUE) return;
    actor.x = minX - PAD;
    actor.y = minY - TITLEH - PAD;
    actor.w = maxX - minX + PAD * 2;
    actor.h = maxY - minY + TITLEH + PAD * 2;
}
```

---

## Paint helpers chuẩn

```java
// Vẽ node goal (oval)
private void paintGoal(Graphics2D g2, Node n) {
    Color c = switch (n.clause == null ? "" : n.clause) {
        case "achieve"  -> C_GOAL_ACH;    // green
        case "maintain" -> C_GOAL_MNT;    // blue
        case "avoid"    -> C_GOAL_AVD;    // red
        default         -> C_GOAL_NONE;
    };
    g2.setColor(c.darker().darker());
    g2.fillOval(n.x, n.y, n.w, n.h);
    g2.setColor(c);
    g2.setStroke(new BasicStroke(1.5f));
    g2.drawOval(n.x, n.y, n.w, n.h);
    g2.setFont(FG); g2.setColor(C_LABEL);
    drawCentredString(g2, n.label, n.x + n.w/2, n.y + n.h/2 - 4);
    if (n.clause != null) {
        g2.setFont(FC); g2.setColor(C_COND);
        drawCentredString(g2, "«" + n.clause + "»", n.x + n.w/2, n.y + n.h/2 + 9);
    }
}

// Vẽ node task (rounded rect)
private void paintTask(Graphics2D g2, Node n) {
    g2.setColor(C_TASK_BG); g2.fillRoundRect(n.x, n.y, n.w, n.h, 8, 8);
    g2.setColor(C_TASK_BDR); g2.setStroke(new BasicStroke(1.5f));
    g2.drawRoundRect(n.x, n.y, n.w, n.h, 8, 8);
    g2.setFont(FT); g2.setColor(C_LABEL);
    drawCentredString(g2, n.label, n.x + n.w/2, n.y + n.h/2 + 4);
}

// Vẽ actor container (rounded box với dashed border)
private void paintActor(Graphics2D g2, Node a, Color borderColor) {
    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND, 0, new float[]{6, 4}, 0);
    g2.setColor(C_ACTOR_BG);
    g2.fillRoundRect(a.x, a.y, a.w, a.h, 14, 14);
    g2.setColor(borderColor); g2.setStroke(dashed);
    g2.drawRoundRect(a.x, a.y, a.w, a.h, 14, 14);
    // Title bar
    g2.setStroke(new BasicStroke(1));
    g2.setColor(borderColor.darker());
    g2.fillRoundRect(a.x + 1, a.y + 1, a.w - 2, TITLEH, 12, 12);
    g2.setFont(FA); g2.setColor(C_ACTOR_TITLE);
    g2.drawString(a.label, a.x + 8, a.y + 17);
}

// Arrow head
private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
    double angle = Math.atan2(y2 - y1, x2 - x1);
    int sz = 8;
    int[] xp = { x2,
                 x2 - (int)(sz * Math.cos(angle - 0.4)),
                 x2 - (int)(sz * Math.cos(angle + 0.4)) };
    int[] yp = { y2,
                 y2 - (int)(sz * Math.sin(angle - 0.4)),
                 y2 - (int)(sz * Math.sin(angle + 0.4)) };
    g2.fillPolygon(xp, yp, 3);
}

// Edge endpoints (tiếp xúc với border node, không phải center)
private int[] edgePair(Node a, Node b) {
    int ax = a.x + a.w/2, ay = a.y + a.h/2;
    int bx = b.x + b.w/2, by = b.y + b.h/2;
    int dx = bx - ax, dy = by - ay;
    if (Math.abs(dx) > Math.abs(dy)) {
        return new int[]{
            dx > 0 ? a.x + a.w : a.x, ay,
            dx > 0 ? b.x : b.x + b.w, by
        };
    } else {
        return new int[]{
            ax, dy > 0 ? a.y + a.h : a.y,
            bx, dy > 0 ? b.y : b.y + b.h
        };
    }
}

// Centered string
private void drawCentredString(Graphics2D g2, String s, int cx, int cy) {
    FontMetrics fm = g2.getFontMetrics();
    g2.drawString(s, cx - fm.stringWidth(s)/2, cy);
}
```

---

## USE View interface

```java
// org.tzi.use.gui.views.View — implement 2 method này
public interface View {
    void detachModel();  // Gọi khi USE unload model
    // update() — optional, một số version có, một số không
}
```

Thực tế trong MAXGoalView:
```java
@Override public void detachModel() { model = null; repaint(); }
public    void update()             { repaint(); }
```

---

## Checklist bước 4

- [ ] View là `JPanel` extends + implements `View` (không kế thừa `CompartmentNode`)
- [ ] Internal `Node` class với `x, y, w, h` (không phải JPanel per node)
- [ ] `paintComponent` vẽ theo layers: actors → edges → nodes
- [ ] Drag dùng `MouseListener`/`MouseMotionListener` trên JPanel
- [ ] `installDrag()` gọi `removeMouseXxxListener` trước khi add mới
- [ ] `setModel()` là public method để Form gọi
- [ ] `detachModel()` và `update()` implement đúng
- [ ] Không import gì từ package `ast` hoặc `parser`
- [ ] Màu sắc theo concept type (goal clause, task vs resource, actor kind)

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Node không drag được | Dùng internal Node model, cập nhật `x,y` của Node rồi `repaint()` |
| Drag accumulate listeners | Gọi `removeMouseXxxListener()` trong `installDrag()` |
| Cạnh không cập nhật sau drag | `repaint()` sau khi update tọa độ Node |
| Actor bounds không co dãn theo drag | Implement `recomputeActorBounds()` |
| Node vẽ đè lên cạnh | Vẽ cạnh (edges) TRƯỚC khi vẽ nodes trong `paintComponent` |
| Arrow head sai hướng | `atan2(y2-y1, x2-x1)` — đảm bảo tính từ source tới target |
