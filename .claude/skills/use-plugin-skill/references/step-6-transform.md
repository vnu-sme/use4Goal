# Bước 6 — Transform: layout algorithm chi tiết + transform sang ngôn ngữ khác (tuỳ chọn)

Bước này có 2 việc khác bản chất, đừng nhầm lẫn:

- **6.A — Layout algorithm**: phần logic bên trong `<Lang>LayoutBuilder` (xem `step-4-view.md`)
  tính toạ độ cụ thể cho từng loại node/edge của ngôn ngữ. Đây vẫn là nội-ngôn-ngữ, phục vụ hiển thị.
- **6.B — Transform liên ngôn ngữ**: biến MM ngôn ngữ A thành MM ngôn ngữ B (ví dụ Goal model
  → BPMN process) — đây là transform NGHIỆP VỤ, không liên quan gì tới hiển thị.

---

## 6.A — Layout algorithm chi tiết

Ví dụ thật từ `IStarView.buildLayout()` (hiện đang nằm trong View — theo `step-4-view.md`,
nên chuyển phần này sang `<Lang>LayoutBuilder` cho ngôn ngữ mới):

```java
private void buildLayout() {
    nodes.clear(); actorNodes.clear();
    if (model == null) return;

    int ax = MARGIN;
    for (ActorDef actor : model.getActors()) {
        Node aNode = new Node();
        aNode.id = actor.name(); aNode.kind = NT.ACTOR;
        aNode.x = ax; aNode.y = MARGIN;

        int cy = MARGIN + ACTOR_HDR + ACTOR_PAD;
        int maxW = 0;
        List<Node> children = new ArrayList<>();
        for (IntentionalElement elem : actor.elements()) {
            Node n = new Node();
            n.id = elem.id(); n.actorId = actor.name();
            switch (elem) {
                case IntentionalElement.Goal     g -> { n.kind = NT.GOAL;     n.w = GW; n.h = GH; }
                case IntentionalElement.Task     t -> { n.kind = NT.TASK;     n.w = TW; n.h = TH; }
                case IntentionalElement.Resource r -> { n.kind = NT.RESOURCE; n.w = RW; n.h = RH; }
                case IntentionalElement.Quality  q -> { n.kind = NT.QUALITY;  n.w = QW; n.h = QH; }
            }
            n.x = ax + ACTOR_PAD; n.y = cy;
            cy += n.h + ELEM_GAP; maxW = Math.max(maxW, n.w);
            children.add(n); nodes.put(n.id, n);
        }
        aNode.w = maxW + ACTOR_PAD * 2;
        aNode.h = cy - MARGIN + ACTOR_PAD;
        for (Node cn : children) cn.x = ax + (aNode.w - cn.w) / 2;   // căn giữa trong actor

        nodes.put(aNode.id, aNode); actorNodes.add(aNode);
        ax += aNode.w + HGAP;
    }
}
```

Nguyên tắc rút ra (áp dụng cho ngôn ngữ mới, đặt trong `<Lang>LayoutBuilder`):
1. Duyệt theo cấu trúc container-lồng-nhau của ngôn ngữ (actor chứa element) — container luôn
   tính bounds SAU khi đã đặt xong các con.
2. Kích thước mỗi loại node cố định theo `NodeKind` (không random) — dễ đoán, dễ căn chỉnh.
3. Container tự co giãn theo tổng kích thước con + padding, không hardcode.

### Edge clipping — điểm nối cạnh vào đúng biên hình dạng node

`IStarView.clipToShape()` là ví dụ tốt: node hình oval (Goal) và node hình chữ nhật (Resource)
cần công thức khác nhau để điểm nối nằm đúng trên biên, không phải center:

```java
private static double[] clipToShape(Node n, double tx, double ty) {
    double cx = n.x + n.w/2.0, cy = n.y + n.h/2.0;
    double dx = tx - cx, dy = ty - cy;
    double hw = n.w/2.0, hh = n.h/2.0;
    double t = switch (n.kind) {
        case GOAL -> 1.0 / Math.sqrt(Math.pow(dx/hw,2) + Math.pow(dy/hh,2));   // ellipse
        default   -> Math.min(                                                  // rectangle
            Math.abs(dx) < 1e-9 ? Double.MAX_VALUE : hw/Math.abs(dx),
            Math.abs(dy) < 1e-9 ? Double.MAX_VALUE : hh/Math.abs(dy));
    };
    return new double[]{cx + t*dx, cy + t*dy};
}
```

### Vẽ theo sealed interface — mỗi nhánh 1 kiểu visual riêng

```java
private void paintRefinement(Graphics2D g2, Refinement ref) {
    switch (ref) {
        case Refinement.And and -> { /* T-shaped arrowhead, mọi children */ }
        case Refinement.Or  or  -> { /* filled arrowhead, chỉ 1 child */ }
    }
}
```

`sealed interface` cho phép compiler báo lỗi nếu thiếu case khi thêm 1 loại `Refinement` mới.

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
theo từng nhánh sealed interface (ví dụ `Refinement`/`FlowNode`) lặp lại ở nhiều nơi (transform
+ layout builder đều cần switch qua từng nhánh) — định nghĩa 1 interface chung:

```java
public interface RefinementVisitor<R> {
    R visitAnd(Refinement.And and);
    R visitOr(Refinement.Or or);

    static <R> R dispatch(Refinement ref, RefinementVisitor<R> v) {
        return switch (ref) {
            case Refinement.And and -> v.visitAnd(and);
            case Refinement.Or  or  -> v.visitOr(or);
        };
    }
}
```

Cả `<LangA>To<LangB>Transformer` và `<Lang>LayoutBuilder` implement `RefinementVisitor<R>` với
`R` khác nhau (`R = TargetFragment` cho transform, `R = LayoutFragment` cho layout) — thêm 1
nhánh `Refinement` mới thì compiler bắt lỗi thiếu implement ở CẢ HAI nơi thay vì âm thầm bỏ sót
1 switch nào đó.

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
