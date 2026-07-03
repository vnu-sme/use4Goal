# Bước 5 — Thiết kế Diagram View: Adapter → Layout → Renderer

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/<lang>/view/
    <Lang>View.java          # Renderer — JPanel + Graphics2D, KHÔNG tự tính layout
```

(Nếu tách đúng theo khuyến nghị dưới đây, thêm 2 file nữa — xem mục "Thiết kế đề xuất".)

---

## USE core làm gì (căn cứ, không phải suy đoán)

`ClassDiagramView`/`ObjectDiagramView` của USE core (`use/use-gui/.../gui/views/diagrams/`)
tách rõ 3 việc:

1. **Adapter**: mỗi phần tử MM (`MClass`, `MObject`) được bọc thành 1 node đặt-được-vị-trí
   (`PlaceableNode`, `ClassNode` kế thừa nó), thêm vào `DirectedGraph<PlaceableNode,EdgeBase>`
   — đồ thị này **chỉ phục vụ layout/vẽ**, tách biệt khỏi đồ thị ngữ nghĩa của model.
2. **Layout**: sau khi mọi node/edge đã có trong graph, **1 bước riêng** (`loadDefaultLayout()`,
   hoặc thuật toán `SpringLayout`) tính toạ độ (x,y).
3. **Renderer**: `paintComponent` chỉ vẽ node/edge đã có toạ độ sẵn — không tính toán gì thêm.

Chi tiết đầy đủ + trích dẫn class/file: `doc/use-core-design-rules.md` mục 3.

---

## Hiện trạng `IStarView`/`Bpmn2View` — CHƯA tách đúng 3 tầng

Cả 2 View hiện có trong codebase (`istar/view/IStarView.java`, `bpmn2/view/Bpmn2View.java`)
đều là `JPanel implements View`, **tự đọc MM và tự tính toạ độ ngay trong chính nó**:
`setModel(model)` gọi `buildLayout()` (tính x,y,w,h cho từng node) rồi `paintComponent` vẽ
luôn — cả 3 việc Adapter/Layout/Render gộp trong 1 class. Đây là nợ kỹ thuật hiện tại (hoạt
động đúng, nhưng không tách được để test layout độc lập hay tái dùng cho renderer khác, ví
dụ export SVG).

**Không bắt buộc sửa lại `IStarView`/`Bpmn2View` ngay** — nhưng **ngôn ngữ mới nên làm đúng
3 tầng ngay từ đầu**, theo thiết kế dưới đây.

---

## Thiết kế đề xuất cho ngôn ngữ mới

```
view/
├── <Lang>Node.java / <Lang>Edge.java   # Adapter — bọc 1 phần tử MM, có id/kind/x/y/w/h
├── <Lang>Layout.java                    # Kết quả layout: List<Node> + List<Edge>, POJO thuần
├── <Lang>LayoutBuilder.java             # MM → <Lang>Layout (Adapter + thuật toán layout)
└── <Lang>View.java                      # JPanel — CHỈ nhận <Lang>Layout và vẽ + xử lý drag/zoom
```

### 1. Adapter — Node/Edge bọc phần tử MM

```java
package org.vnu.sme.goal.<lang>.view;

public final class <Lang>Node {
    public final String id, label;
    public final NodeKind kind;     // enum: theo từng loại concept của ngôn ngữ
    public int x, y, w, h;          // do LayoutBuilder gán, View chỉ đọc

    public <Lang>Node(String id, String label, NodeKind kind) {
        this.id = id; this.label = label; this.kind = kind;
    }
}

public record <Lang>Edge(String fromId, String toId, EdgeKind kind, String label) {}
```

### 2. Layout — kết quả thuần, không phụ thuộc Swing

```java
public final class <Lang>Layout {
    public final Map<String, <Lang>Node> nodes;
    public final List<<Lang>Edge>        edges;
    public final int width, height;      // kích thước canvas cần thiết

    public <Lang>Layout(Map<String, <Lang>Node> nodes, List<<Lang>Edge> edges, int w, int h) {
        this.nodes = Map.copyOf(nodes); this.edges = List.copyOf(edges);
        this.width = w; this.height = h;
    }
}
```

### 3. LayoutBuilder — MM → Layout (Adapter step + thuật toán tính toạ độ)

```java
public final class <Lang>LayoutBuilder {
    private <Lang>LayoutBuilder() {}

    public static <Lang>Layout build(<Lang>Model model) {
        Map<String, <Lang>Node> nodes = new LinkedHashMap<>();
        List<<Lang>Edge> edges = new ArrayList<>();

        // bước Adapter: 1 phần tử MM → 1 Node
        for (var element : model.allElements()) {
            nodes.put(element.id(), toNode(element));
        }
        // bước Layout: tính x,y (thuật toán tuỳ ngôn ngữ — grid theo actor, tree, spring...)
        int x = MARGIN;
        for (<Lang>Node n : nodes.values()) {
            n.x = x; n.y = MARGIN;
            x += n.w + HGAP;
        }
        return new <Lang>Layout(nodes, edges, x + MARGIN, 600);
    }

    private static <Lang>Node toNode(/* MM element */ Object element) {
        // switch theo sealed interface của MM, map sang NodeKind + kích thước chuẩn
        throw new UnsupportedOperationException();
    }
}
```

### 4. View — chỉ render, xử lý input, KHÔNG tính layout

```java
public final class <Lang>View extends JPanel implements View {
    private <Lang>Layout layout;

    public void setLayout(<Lang>Layout l) {
        this.layout = l;
        setPreferredSize(new Dimension(l.width, l.height));
        revalidate(); repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (layout == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintEdges(g2);   // vẽ cạnh TRƯỚC
        paintNodes(g2);   // vẽ node SAU (đè lên cạnh)
    }

    // drag chỉ cập nhật x/y trên chính layout.nodes.get(id), không tính lại toàn bộ layout
    private void installDrag() { /* MouseListener cập nhật node.x/node.y rồi repaint() */ }

    @Override public void detachModel() { layout = null; repaint(); }
}
```

Form gọi: `view.setLayout(<Lang>LayoutBuilder.build(model))` thay vì `view.setModel(model)` —
tách rõ "tính layout" khỏi "render". Muốn export SVG hay renderer khác chỉ cần viết 1
class mới đọc `<Lang>Layout`, không đụng vào `LayoutBuilder`.

---

## Palette màu & shape — vẫn giữ trong View (đây là phần render, đúng chỗ)

```java
private static final Color C_GOAL_FILL = new Color(230, 248, 232);
private static final Color C_GOAL_BDR  = new Color(30, 140, 60);
// ... theo từng NodeKind của ngôn ngữ, xem IStarView/Bpmn2View hiện có để tham khảo màu sắc
```

---

## USE `View` interface (bắt buộc implement)

```java
// org.tzi.use.gui.views.View
public interface View {
    void detachModel();  // Gọi khi USE unload model
}
```

Thực tế: `@Override public void detachModel() { layout = null; repaint(); }`.

---

## Checklist bước 5

- [ ] Có tách `<Lang>Node`/`<Lang>Edge` (Adapter) khỏi `<Lang>View` (Renderer) — không bắt buộc
      với ngôn ngữ đơn giản, nhưng khuyến nghị mạnh cho ngôn ngữ mới
- [ ] `<Lang>LayoutBuilder` không import Swing/AWT — chỉ tính toán số học
- [ ] `<Lang>View.paintComponent` KHÔNG gọi lại logic tính toạ độ — chỉ đọc field có sẵn
- [ ] Vẽ theo layer: actors/container → edges → nodes (nodes vẽ sau để đè lên cạnh)
- [ ] Drag cập nhật trực tiếp `x,y` trên Node đang có, không rebuild toàn bộ Layout
- [ ] `detachModel()` implement đúng theo `View` interface của USE

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Node không drag được | Cập nhật `x,y` của Node trong Layout hiện có, rồi `repaint()` |
| Cạnh không cập nhật sau drag | `repaint()` ngay sau khi đổi toạ độ |
| Node vẽ đè lên cạnh | Vẽ edges TRƯỚC nodes trong `paintComponent` |
| Layout tính lại mỗi lần vẽ | Tách `LayoutBuilder.build()` khỏi `paintComponent` — chỉ gọi 1 lần khi `setModel`/reload |
| Muốn thêm renderer khác (SVG, PDF) mà phải sửa cả logic tính vị trí | Dấu hiệu Adapter/Layout đang bị trộn với Renderer — tách theo mẫu ở trên |
