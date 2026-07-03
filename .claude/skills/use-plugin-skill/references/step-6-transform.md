# Bước 6 — Transformation Rules: MetaModel → Diagram View

## Nguyên tắc

Bước 6 là cầu nối từ MM sang pixel. Có hai phần:

1. **`buildLayout()`** trong View — tính toán `x, y, w, h` cho từng `Node` trong internal node model
2. **`paintComponent(Graphics2D)`** — render từng layer: actors → edges → nodes

Tất cả code này nằm trong View class (Bước 4). Bước 6 là phần **logic** của View.

---

## 6.1 Constants cho layout

```java
// Trong <Lang>View — điều chỉnh theo kích thước concept
private static final int PAD    = 18;   // padding bên trong actor container
private static final int HGAP   = 50;   // khoảng ngang giữa hai actor
private static final int VG     = 10;   // khoảng dọc giữa các node trong actor
private static final int TITLEH = 26;   // chiều cao title bar của actor
private static final int MARGIN = 24;   // margin ngoài cùng

// Kích thước chuẩn cho từng loại node (tuỳ chỉnh theo ngôn ngữ)
private static final int GW = 138, GH = 44;  // Goal: oval
private static final int TW = 122, TH = 36;  // Task: rounded rect
private static final int RW = 110, RH = 28;  // Resource: rect

// Fonts
private static final Font FA = new Font(Font.SANS_SERIF, Font.BOLD,   12); // Actor title
private static final Font FG = new Font(Font.SANS_SERIF, Font.PLAIN,  11); // Goal label
private static final Font FT = new Font(Font.MONOSPACED, Font.PLAIN,  10); // Task label
private static final Font FC = new Font(Font.SANS_SERIF, Font.ITALIC,  9); // Condition/meta
```

---

## 6.2 buildLayout() — tính tọa độ tất cả Node

**Pattern thực tế từ `MAXGoalView.buildLayout()`**:

```java
private void buildLayout() {
    nodes.clear();
    actorNodes.clear();
    if (model == null) return;

    int ax = MARGIN;
    for (Actor a : model.getActors()) {
        // ── Tạo actor node ────────────────────────────────────────
        Node actor = new Node();
        actor.id    = a.name();
        actor.kind  = NT.ACTOR;
        actor.x     = ax;
        actor.y     = MARGIN;
        actor.label = a.name() + " «" + a.kind().name().toLowerCase() + "»";

        int maxW = 0;
        int cy   = MARGIN + TITLEH + PAD;
        List<Node> children = new ArrayList<>();

        // ── Tạo node cho từng intentional element ─────────────────
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
            cy   += n.h + VG;
            maxW  = Math.max(maxW, n.w);
            children.add(n);
            nodes.put(n.id, n);
        }

        // ── Sizing actor container ─────────────────────────────────
        actor.w = maxW + PAD * 2;
        actor.h = cy - MARGIN + PAD;

        // Căn giữa các node con trong actor
        for (Node cn : children) cn.x = ax + (actor.w - cn.w) / 2;

        nodes.put(actor.id, actor);
        actorNodes.add(actor);
        ax += actor.w + HGAP;
    }

    // ── Canvas preferred size ──────────────────────────────────────
    int totalW = ax + MARGIN;
    int maxH   = actorNodes.stream()
                    .mapToInt(n -> n.y + n.h).max().orElse(400) + MARGIN;
    setPreferredSize(new Dimension(Math.max(totalW, 800), Math.max(maxH, 600)));
    installDrag();
}
```

---

## 6.3 paintRefineEdges() — cạnh refinement

Mỗi loại `RefineSpec` có visual riêng biệt:

```java
private void paintRefineEdges(Graphics2D g2) {
    if (model == null) return;
    for (Intentional item : model.allIntentionals().values()) {
        RefineSpec refine = switch (item) {
            case GoalDef g     -> g.refine();
            case TaskDef t     -> t.refine();
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
            // ITER: đường nét đứt từ child ngược lên parent (loop)
            for (String childId : it.children()) {
                Node child = nodes.get(childId);
                if (child != null) drawArrowReverse(g2, child, parent, C_ITER);
            }
        }
        case RefineSpec.SeqRefine s -> {
            // SEQ: đánh số thứ tự, bullet tại parent
            paintBullet(g2, parent, C_SEQ, true);
            int seq = 1;
            for (String childId : s.children()) {
                Node child = nodes.get(childId);
                if (child != null)
                    drawArrow(g2, parent, child, String.valueOf(seq++), C_SEQ);
            }
        }
        case RefineSpec.ParRefine p -> {
            // PAR: bullet filled, mũi tên thường
            paintBullet(g2, parent, C_PAR, true);
            for (String childId : p.children()) {
                Node child = nodes.get(childId);
                if (child != null) drawArrow(g2, parent, child, null, C_PAR);
            }
        }
        case RefineSpec.XorRefine x -> {
            // XOR: hollow squares tại parent, guard label trên cạnh
            paintHollowSquares(g2, parent, x.branches().size(), C_XOR);
            for (GuardedChild b : x.branches()) {
                Node child = nodes.get(b.childId());
                if (child != null)
                    drawArrow(g2, parent, child, truncCond(b.condition()), C_XOR);
            }
        }
        case RefineSpec.IorRefine io -> {
            // IOR: hollow circles tại parent
            paintHollowCircles(g2, parent, io.branches().size(), C_IOR);
            for (GuardedChild b : io.branches()) {
                Node child = nodes.get(b.childId());
                if (child != null)
                    drawArrow(g2, parent, child, truncCond(b.condition()), C_IOR);
            }
        }
    }
}
```

---

## 6.4 paintDependEdges() — cạnh dependency

```java
private void paintDependEdges(Graphics2D g2) {
    if (model == null) return;
    Stroke dashed = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10, new float[]{8, 4}, 0);
    g2.setStroke(dashed);
    g2.setColor(C_DEP);   // gold

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
```

---

## 6.5 Marker helpers tại parent node

```java
// SEQ/PAR: filled dot ở bottom-center của parent
private void paintBullet(Graphics2D g2, Node n, Color c, boolean filled) {
    int bx = n.x + n.w/2 - 5;
    int by = n.y + n.h - 5;
    if (filled) { g2.setColor(c); g2.fillOval(bx, by, 10, 10); }
    else        { g2.setColor(c); g2.drawOval(bx, by, 10, 10); }
}

// XOR: hollow squares (count = số branches)
private void paintHollowSquares(Graphics2D g2, Node n, int count, Color c) {
    g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
    int bx = n.x + n.w/2 - count * 7;
    int by = n.y + n.h - 6;
    for (int i = 0; i < count; i++)
        g2.drawRect(bx + i * 14, by, 10, 10);
}

// IOR: hollow circles
private void paintHollowCircles(Graphics2D g2, Node n, int count, Color c) {
    g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
    int bx = n.x + n.w/2 - count * 7;
    int by = n.y + n.h - 6;
    for (int i = 0; i < count; i++)
        g2.drawOval(bx + i * 14, by, 10, 10);
}
```

---

## 6.6 Arrow drawing

```java
// Arrow thường: parent → child (direction = parent to child)
private void drawArrow(Graphics2D g2, Node from, Node to,
                       String label, Color c) {
    g2.setColor(c); g2.setStroke(new BasicStroke(1.5f));
    int[] p = edgePair(from, to);
    g2.drawLine(p[0], p[1], p[2], p[3]);
    drawArrowHead(g2, p[0], p[1], p[2], p[3]);
    if (label != null) {
        g2.setFont(FC); g2.setColor(c.brighter());
        g2.drawString(label, (p[0]+p[2])/2 + 3, (p[1]+p[3])/2 - 4);
    }
}

// Arrow ngược + dotted: child → parent (ITER loop)
private void drawArrowReverse(Graphics2D g2, Node from, Node to, Color c) {
    g2.setColor(c);
    Stroke dotted = new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10, new float[]{4, 3}, 0);
    g2.setStroke(dotted);
    int[] p = edgePair(from, to);
    g2.drawLine(p[0], p[1], p[2], p[3]);
    drawArrowHead(g2, p[0], p[1], p[2], p[3]);
}

// Arrow head: filled triangle
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

// Edge endpoint: tiếp xúc cạnh node, không phải center
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

// Truncate condition text cho label trên cạnh
private String truncCond(String cond) {
    if (cond == null || cond.isBlank()) return "";
    return cond.length() > 20 ? cond.substring(0, 18) + "…" : cond;
}
```

---

## 6.7 Palette màu chuẩn (dark theme)

```java
// Colors cho refinement types
private static final Color C_SEQ  = new Color( 80, 160, 255);  // xanh dương
private static final Color C_PAR  = new Color(255, 165,  50);  // cam
private static final Color C_XOR  = new Color(210,  80, 230);  // tím
private static final Color C_IOR  = new Color( 50, 210, 180);  // cyan
private static final Color C_ITER = new Color(235,  80,  80);  // đỏ
private static final Color C_DEP  = new Color(220, 210,  50);  // vàng

// Colors cho nodes
private static final Color C_GOAL_ACH  = new Color( 50, 168,  82); // achieve: xanh lá
private static final Color C_GOAL_MNT  = new Color( 50, 120, 200); // maintain: xanh dương
private static final Color C_GOAL_AVD  = new Color(200,  55,  55); // avoid: đỏ
private static final Color C_GOAL_NONE = new Color( 80,  90, 115); // không có clause
private static final Color C_TASK_BG   = new Color( 45,  55,  80);
private static final Color C_TASK_BDR  = new Color( 90, 130, 210);
private static final Color C_RES_BG    = new Color( 40,  55,  45);
private static final Color C_RES_BDR   = new Color( 80, 175, 100);
private static final Color C_LABEL     = new Color(225, 230, 240);

// Actor border colors theo kind
private Color actorBorderColor(ActorKind kind) {
    return switch (kind) {
        case AGENT    -> new Color(255, 190,  40); // vàng
        case ROLE     -> new Color( 70, 100, 170); // xanh nhạt
        case POSITION -> new Color(100, 200, 130); // xanh lá
    };
}
```

---

## 6.8 Transformer sang ngôn ngữ khác (tuỳ chọn)

Nếu plugin có transform (ví dụ GOAL → BPMN), viết class riêng trong package `transform/`:

```java
package org.vnu.sme.<plugin>.transform;

public final class <Lang>ToBpmnTransformer {

    public static BpmnProcess transform(<Lang>Model model) {
        BpmnProcess process = new BpmnProcess(model.getName());

        for (Actor actor : model.getActors()) {
            Lane lane = new Lane(actor.name());

            // Map từng intentional element thành BPMN node
            for (Intentional item : actor.intentionals()) {
                switch (item) {
                    case GoalDef g -> {
                        // Goal với refine → gateway + sub-tasks
                        if (g.refine() != null) {
                            addGatewayForRefine(process, lane, g);
                        }
                    }
                    case TaskDef t -> {
                        // Task → BpmnTask
                        lane.addNode(new BpmnTask(t.name()));
                    }
                    default -> {}
                }
            }
            process.addLane(lane);
        }

        // Dependencies → MessageFlow
        for (Dependency d : model.getDependencies()) {
            process.addMessage(new MessageFlow(d.from(), d.to()));
        }

        return process;
    }
}
```

---

## Checklist bước 6

- [ ] `buildLayout()` tính `x,y,w,h` cho tất cả Node (actors + children)
- [ ] Actor bounds re-computed sau drag (`recomputeActorBounds`)
- [ ] `paintComponent` vẽ theo đúng thứ tự: actors → edges → nodes
- [ ] Mỗi loại `RefineSpec` có visual riêng (color + marker)
- [ ] `IterRefine` case đặt TRƯỚC `SeqRefine` trong switch (subclass)
- [ ] `edgePair()` trả về điểm tiếp xúc cạnh node (không phải center)
- [ ] Guard label được truncate để không chiếm quá nhiều không gian
- [ ] `setPreferredSize()` cập nhật sau mỗi `buildLayout()` để scroll đúng
- [ ] `installDrag()` xoá listener cũ trước khi add mới

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Cạnh không cập nhật sau drag | `repaint()` trong `mouseDragged` của JPanel |
| Node chồng lên cạnh | Vẽ edges trước, nodes sau trong `paintComponent` |
| IterRefine match SeqRefine | Đặt `case RefineSpec.IterRefine it` trước `case RefineSpec.SeqRefine s` |
| Arrow head sai hướng | `drawArrowHead(g2, x1, y1, x2, y2)` — x1,y1 là SOURCE, x2,y2 là TARGET |
| Canvas không scroll | `setPreferredSize()` theo bounding box thực tế, không hardcode |
| Actor bounds không co dãn | Implement `recomputeActorBounds(actorId)` sau mỗi drag |
| Label condition quá dài | Gọi `truncCond()` để cắt ở 20 chars |
