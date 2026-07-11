# Bước 6 — Transform: layout algorithm chi tiết + transform sang ngôn ngữ khác (tuỳ chọn)

Bước này có 2 việc khác bản chất, đừng nhầm lẫn:

- **6.A — Layout algorithm**: phần logic bên trong `<Lang>LayoutBuilder` (xem `step-4-view.md`)
  tính toạ độ cụ thể cho từng loại node/edge của ngôn ngữ. Đây vẫn là nội-ngôn-ngữ, phục vụ hiển thị.
- **6.B — Transform liên ngôn ngữ**: biến MM ngôn ngữ A thành MM ngôn ngữ B (ví dụ Goal model
  → BPMN process) — đây là transform NGHIỆP VỤ, không liên quan gì tới hiển thị.

---

## 6.A — Layout algorithm chi tiết

Đặt toàn bộ logic này trong `<Lang>LayoutBuilder` (Bước 5) — **không** trong `<Lang>View`.
Khuôn mẫu cho ngôn ngữ có container lồng nhau (container chứa element, ví dụ actor chứa
intentional-element, hoặc pool chứa lane chứa node):

```java
public static <Lang>Layout build(<Lang>Model model) {
    Map<String, <Lang>Node> nodes = new LinkedHashMap<>();

    int ax = MARGIN;
    for (var container : model.getContainers()) {
        <Lang>Node containerNode = new <Lang>Node(container.id(), NodeKind.CONTAINER);
        containerNode.x = ax; containerNode.y = MARGIN;

        int cy = MARGIN + CONTAINER_HDR + CONTAINER_PAD;
        int maxW = 0;
        List<<Lang>Node> children = new ArrayList<>();
        for (var elem : container.elements()) {
            <Lang>Node n = toNode(elem);   // switch theo sealed interface -> NodeKind + w/h chuẩn
            n.x = ax + CONTAINER_PAD; n.y = cy;
            cy += n.h + ELEM_GAP; maxW = Math.max(maxW, n.w);
            children.add(n); nodes.put(n.id, n);
        }
        containerNode.w = maxW + CONTAINER_PAD * 2;
        containerNode.h = cy - MARGIN + CONTAINER_PAD;
        for (<Lang>Node cn : children) cn.x = ax + (containerNode.w - cn.w) / 2;  // căn giữa

        nodes.put(containerNode.id, containerNode);
        ax += containerNode.w + HGAP;
    }
    // ... build edges, rồi return new <Lang>Layout(nodes, edges, ax + MARGIN, ...)
}
```

Nguyên tắc rút ra:
1. Duyệt theo cấu trúc container-lồng-nhau của ngôn ngữ — container luôn tính bounds SAU khi
   đã đặt xong các con.
2. Kích thước mỗi loại node cố định theo `NodeKind` (không random) — dễ đoán, dễ căn chỉnh.
3. Container tự co giãn theo tổng kích thước con + padding, không hardcode.

### Edge clipping — điểm nối cạnh vào đúng biên hình dạng node

Node hình oval và node hình chữ nhật cần công thức khác nhau để điểm nối nằm đúng trên biên,
không phải center:

```java
private static double[] clipToShape(<Lang>Node n, double tx, double ty) {
    double cx = n.x + n.w/2.0, cy = n.y + n.h/2.0;
    double dx = tx - cx, dy = ty - cy;
    double hw = n.w/2.0, hh = n.h/2.0;
    double t = switch (n.kind) {
        case OVAL -> 1.0 / Math.sqrt(Math.pow(dx/hw,2) + Math.pow(dy/hh,2));   // ellipse
        default   -> Math.min(                                                  // rectangle
            Math.abs(dx) < 1e-9 ? Double.MAX_VALUE : hw/Math.abs(dx),
            Math.abs(dy) < 1e-9 ? Double.MAX_VALUE : hh/Math.abs(dy));
    };
    return new double[]{cx + t*dx, cy + t*dy};
}
```

### Vẽ theo sealed interface — mỗi nhánh 1 kiểu visual riêng

```java
private void paintRelation(Graphics2D g2, <MultiShapeRelation> rel) {
    switch (rel) {
        case <MultiShapeRelation>.KindA a -> { /* T-shaped arrowhead, mọi children */ }
        case <MultiShapeRelation>.KindB b -> { /* filled arrowhead, chỉ 1 child */ }
    }
}
```

`sealed interface` cho phép compiler báo lỗi nếu thiếu case khi thêm 1 nhánh quan hệ mới.

---

## 6.B — Transform sang ngôn ngữ khác (tuỳ chọn, tách biệt khỏi View)

Nếu plugin cần biến model ngôn ngữ A thành model ngôn ngữ B (ví dụ tương lai: Goal model →
BPMN process), viết class riêng trong package `transform/`, **không đụng gì tới `view/`**:

```java
package org.vnu.sme.goal.transform;

public final class <LangA>To<LangB>Transformer {

    public static <LangB>Model transform(<LangA>Model source) {
        <LangB>Model target = new <LangB>Model(source.getName());
        for (var element : source.allElements()) {
            target.add(mapElement(element));
        }
        return target;
    }

    private static Object mapElement(Object element) {
        // switch theo sealed interface của MM nguồn — mỗi nhánh map sang 1 kiểu bên đích
        throw new UnsupportedOperationException();
    }
}
```

**Khuyến nghị áp dụng Visitor pattern chuẩn hoá thay vì switch rời rạc** nếu logic transform
theo từng nhánh 1 sealed interface trong MM lặp lại ở nhiều nơi (transform + layout builder
đều cần switch qua từng nhánh) — định nghĩa 1 interface chung:

```java
public interface <X>Visitor<R> {
    R visitKindA(<X>.KindA a);
    R visitKindB(<X>.KindB b);

    static <R> R dispatch(<X> x, <X>Visitor<R> v) {
        return switch (x) {
            case <X>.KindA a -> v.visitKindA(a);
            case <X>.KindB b -> v.visitKindB(b);
        };
    }
}
```

Cả `<LangA>To<LangB>Transformer` và `<Lang>LayoutBuilder` implement `<X>Visitor<R>` với `R`
khác nhau (`R = TargetFragment` cho transform, `R = LayoutFragment` cho layout) — thêm 1
nhánh mới vào sealed interface `<X>` thì compiler bắt lỗi thiếu implement ở CẢ HAI nơi thay vì
âm thầm bỏ sót 1 switch nào đó.

---

## Checklist bước 6

- [ ] Layout algorithm nằm trong `<Lang>LayoutBuilder`, không nằm trong `paintComponent`
- [ ] Container (actor/pool/lane) tính bounds SAU khi đặt xong các phần tử con
- [ ] Edge clipping dùng công thức đúng theo shape của từng `NodeKind` (không phải luôn center-to-center)
- [ ] Switch theo sealed interface bao phủ đủ mọi nhánh (compiler exhaustive check)
- [ ] Nếu có transform liên ngôn ngữ: đặt trong package `transform/`, không import gì từ `view/`
- [ ] Nếu switch theo cùng 1 sealed interface lặp lại ≥ 2 nơi (transform + layout): cân nhắc rút thành `Visitor<R>` interface dùng chung

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Cạnh nối vào center thay vì biên hình | Dùng `clipToShape()` theo đúng `NodeKind` |
| Container không co giãn đúng theo con | Tính `w/h` của container SAU vòng lặp đặt con, không trước |
| Sửa thêm 1 nhánh sealed interface phải sửa nhiều nơi | Rút thành `Visitor<R>` interface dùng chung giữa transform và layout |
| Transform import từ `view/` | Vi phạm tách lớp — transform chỉ được đụng MM |
