# Bước 5 — Thiết kế Diagram View: Adapter → Layout → Renderer

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/<lang>/view/
    <Lang>View.java          # Renderer — JPanel + Graphics2D, KHÔNG tự tính layout
```

(Tách đúng 3 tầng thì có thêm 2 file nữa — xem mục "Thiết kế bắt buộc cho View".)

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

## Bắt buộc: mọi ngôn ngữ làm đúng 3 tầng ngay từ đầu

`<Lang>Node`/`<Lang>Edge` (Adapter) + `<Lang>Layout` (POJO thuần, không Swing) +
`<Lang>LayoutBuilder` (MM → Layout, không import Swing/AWT) + `<Lang>View` (chỉ render + xử
lý drag/resize, không tính toạ độ trong `paintComponent`). `<Lang>Form` gọi
`view.setModel(model)` — View tự delegate sang `<Lang>LayoutBuilder.build(model)` một lần rồi
gọi `setLayout(...)`, không lặp lại thuật toán tính toạ độ trong chính nó. Đây không phải gợi
ý tuỳ chọn — mọi View mới đều phải theo thiết kế này ngay từ đầu, theo đúng khuôn mẫu
`ClassDiagramView`/`PlaceableNode`/`DirectedGraph` của USE core đã trích ở trên.

---

## Ưu tiên UI đồng bộ với USE: kế thừa hạ tầng diagram sẵn có

Khi view là một diagram graph tương tác thật (node/edge, kéo thả, zoom, context menu, save/load
layout), **không tự viết một Swing canvas hoàn toàn riêng**. Hãy reuse các component trong
`use/use-gui/src/main/java/org/tzi/use/gui/views/diagrams/` từ plugin trong `goal/`:

- `<Lang>View extends JPanel implements View`: wrapper mỏng để nhúng diagram vào form/dialog.
- `<Lang>Diagram extends DiagramView`: canvas chính, dùng `DiagramGraph`, `DiagramInputHandling`,
  menu layout, grid, anti-aliasing, grayscale và repaint lifecycle của USE.
- `<Lang>DiagramOptions extends DiagramOptions`: gom màu, stroke, font, persist option theo
  cùng kiểu USE.
- `<Lang>Node extends PlaceableNode`: adapter node của ngôn ngữ, override `onDraw`,
  `doCalculateSize`, tooltip và store/restore placement nếu cần.
- `<Lang>Edge extends EdgeBase`: adapter edge của ngôn ngữ, dùng `DirectedEdgeFactory`/
  waypoint support và chỉ custom glyph/màu/label theo semantics riêng.

Vẫn giữ `<Lang>Layout` + `<Lang>LayoutBuilder` thuần Java để tạo node/edge semantic và toạ độ
mặc định trước khi đổ vào `DiagramGraph`. Phần Swing nằm ở lớp adapter diagram, không nằm trong
MM hay compiler.

Custom `JPanel + Graphics2D` chỉ dùng cho preview rất nhỏ, prototype, hoặc trường hợp đã kiểm
tra mà `DiagramView` không phù hợp. Nếu dùng fallback này, ghi rõ lý do trong code hoặc tài liệu
ngắn của view để lần sau không vô tình làm UI lệch khỏi USE tool gốc.

Không kế thừa trực tiếp `ClassDiagramView`/`ObjectDiagramView` cho ngôn ngữ không phải UML class
hoặc object diagram; chúng gắn với `MClass`, `MObject`, `MSystem`. Với DSL riêng, kế thừa lớp
nền tổng quát `DiagramView` và tự cung cấp node/edge adapter.

Khi hỗ trợ lưu/đọc layout, dùng action/persistence sẵn có của USE:

- `ActionSaveLayout` / `ActionLoadLayout` cho menu.
- `storePlacementInfo(...)` trên node/edge và `restorePlacementInfo(...)` khi load.
- `getDefaultLayoutFileSuffix()` để đặt suffix riêng cho ngôn ngữ.
- Không sửa code trong `use/`; chỉ import và reuse public/protected API có sẵn từ plugin.

---

## Form loader, ViewFrame và popup window

Với diagram view, `<Lang>Form` chỉ là form mở file cổ điển. Không nhúng canvas/diagram vào form
loader. Giữ UI cô đọng, tên tiếng Anh ngắn:

```
File: [........................] [Browse] [Open] [v] [Close]
```

Quy tắc:

- `Open` mặc định compile file và mở diagram trong USE desktop bằng `ViewFrame`.
- Nút `v` nếu có chỉ mở `JPopupMenu` nhỏ với lựa chọn phụ như `Open in USE` và `Open popup`.
- Không để vùng preview trắng, header dài, log `Ready`, hoặc text hướng dẫn thừa trong loader.
- Parse/IO lỗi hiển thị bằng `JOptionPane` hoặc status label rất ngắn; không biến loader thành
  console/debug panel.

Đặt logic mở window ở `<Lang>View`, không dàn trải trong form:

- `<Lang>View.openUseDesktop(MainWindow, Model, Path)` tạo `<Lang>View`, bọc bằng `ViewFrame`,
  add vào `mainWindow.addNewViewFrame(frame)`.
- `<Lang>View.openPopupWindow(MainWindow, Model, Path)` tạo cùng view nhưng đặt trong `JFrame`.
- `<Lang>View implements View, PrintableView` nếu muốn Print/Export của USE bật khi frame được
  chọn.

Nếu hỗ trợ cả hai placement (`ViewFrame` trong USE và popup ngoài USE), context menu chuột phải
trên chính diagram phải chuyển được hai chiều:

- Đang trong USE `ViewFrame` → menu item `Open popup`.
- Đang trong popup `JFrame` → menu item `Open in USE`.

Nối menu này qua callback từ `<Lang>View` xuống `<Lang>Diagram` (ví dụ `setSwitchAction(label,
Runnable)`), để `<Lang>Diagram` chỉ dựng `JPopupMenu`, còn `<Lang>View` quản lý lifecycle
`ViewFrame`/`JFrame`. Không sửa `MainWindow` hoặc `ViewFrame` của USE core để làm việc này.

---

## Thiết kế bắt buộc cho View

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
// ... 1 cặp fill/border color cho mỗi NodeKind của ngôn ngữ
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

- [ ] Có tách `<Lang>Node`/`<Lang>Edge` (Adapter) khỏi `<Lang>View` (Renderer) — bắt buộc cho
      mọi ngôn ngữ mới, kể cả ngôn ngữ trông đơn giản
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
