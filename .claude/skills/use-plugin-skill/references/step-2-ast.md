# Bước 2 — Thiết kế AST (Abstract Syntax Tree)

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/<lang>/ast/
```

## Vai trò của AST — bắt buộc cho MỌI ngôn ngữ, không ngoại lệ

AST (Concrete Syntax — hậu tố `CS`) là lớp trung gian giữa ParseTree (ANTLR) và MM runtime.
Nó ánh xạ gần 1-1 với cấu trúc grammar, lưu đủ thông tin để xây MM (kể cả source text thô của
condition/expression), không chứa logic semantic, và immutable sau khi Visitor build xong.

---

## Khuôn mẫu AST

### Root model

```java
public final class <Lang>ModelCS {
    private final String                  name;
    private final List<<Concept>DefCS>    entities;
    private final List<<Relation>CS>      relations;

    public <Lang>ModelCS(String name, List<<Concept>DefCS> entities, List<<Relation>CS> relations) {
        this.name      = name;
        this.entities  = List.copyOf(entities);
        this.relations = List.copyOf(relations);
    }

    public String                name()      { return name; }
    public List<<Concept>DefCS>  entities()  { return entities; }
    public List<<Relation>CS>    relations() { return relations; }
}
```

### Node record — khi khái niệm không có nhiều biến thể

```java
public record <Concept>DefCS(String id, String kind, List<ElementBodyCS> body) {}
public record <Relation>CS(String from, String label, String to) {}
```

### Sealed hierarchy — khi 1 khái niệm có nhiều loại khai báo trộn lẫn

Mỗi labeled alternative trong grammar (Bước 1) tương ứng 1-1 với 1 `record` implement chung
1 sealed interface — đây là cách chuyển "n nhánh `# label` trong 1 rule ANTLR" thành "n kiểu
CS mà compiler kiểm tra exhaustive switch được":

```java
public sealed interface ElementBodyCS
        permits ElementBodyCS.KindACS, ElementBodyCS.KindBCS, ElementBodyCS.RelationElemCS {

    record KindACS(String id) implements ElementBodyCS {}
    record KindBCS(String id) implements ElementBodyCS {}
    record RelationElemCS(String from, List<String> to) implements ElementBodyCS {}
}
```

---

## Visitor: ParseTree → AST

```java
public final class <Lang>BuildingVisitor extends <Lang>BaseVisitor<Object> {

    public static <Lang>ModelCS build(<Lang>Parser.ModelContext ctx) {
        List<<Concept>DefCS> entities = ctx.entityDef().stream()
                .map(e -> (<Concept>DefCS) new <Lang>BuildingVisitor().visitEntityDef(e))
                .collect(Collectors.toList());
        // ...
        return new <Lang>ModelCS(ctx.IDENT().getText(), entities, relations);
    }

    @Override
    public Object visitKindAElem(<Lang>Parser.KindAElemContext ctx) {
        return new ElementBodyCS.KindACS(ctx.IDENT().getText());
    }

    @Override
    public Object visitRelationElem(<Lang>Parser.RelationElemContext ctx) {
        return new ElementBodyCS.RelationElemCS(
                ctx.IDENT(0).getText(),
                ctx.IDENT().subList(1, ctx.IDENT().size()).stream()
                        .map(TerminalNode::getText).collect(Collectors.toList()));
    }
}
```

Mỗi `visit<Label>()` ánh xạ trực tiếp từ 1 labeled alternative của grammar (Bước 1) sang 1
record CS — không rẽ nhánh thủ công theo text token trong 1 method `visit<Rule>()` gộp chung.

---

## Khi ngôn ngữ có forward-reference: áp dụng ý tưởng `Context` của USE

Không phải ngôn ngữ nào cũng cần phần này — chỉ cần khi có tham chiếu tên khai báo *sau* nó
trong cùng scope theo cách gây vấn đề (ví dụ entity A tham chiếu tới entity B khai báo phía
sau trong cùng file). Khi đó, áp dụng đúng cách USE core làm với `.use`:

1. **`Context`-like object** đi xuyên suốt quá trình build: giữ symbol table (tên đã thấy),
   danh sách lỗi tích luỹ (không throw ngay khi gặp lỗi ngữ nghĩa), model đang xây.
2. **Build nhiều pha** thay vì 1 lượt Visitor duy nhất:
   - Pha 1: tạo khung rỗng cho mọi actor/element (chỉ có tên, chưa có nội dung/quan hệ).
   - Pha 2: điền nội dung, nối quan hệ (refine, dependency...) — lúc này mọi tên đã tồn tại
     trong symbol table nên forward-reference resolve được.
3. Lỗi ngữ nghĩa (ví dụ tham chiếu tới actor không tồn tại) được báo qua danh sách lỗi của
   `Context`, gộp chung với lỗi cú pháp khi trả về `Compiler.Result`.

Xem `doc/use-core-design-rules.md` mục 2.2 (`ASTModel.gen(Context)` 5 pha) cho pattern gốc.
Đừng áp dụng multi-pha khi ngôn ngữ chưa cần — thêm phức tạp không cần thiết.

---

## Checklist bước 2

- [ ] Tất cả CS class/record nằm trong package `<lang>/ast`
- [ ] Tất cả tên kết thúc bằng `CS`
- [ ] Không có logic xử lý semantic trong CS (chỉ data)
- [ ] Dùng `List.copyOf()` để đảm bảo immutable
- [ ] Dùng Java record cho CS node đơn giản (không có behavior)
- [ ] Dùng sealed interface + record cho hierarchy (nhiều loại khai báo trộn lẫn)
- [ ] CS class không import gì từ package `mm`
- [ ] CS class không import Swing/AWT
- [ ] Nếu ngôn ngữ có forward-reference: cân nhắc `Context` + build nhiều pha (xem trên)

## Lỗi thường gặp

| Lỗi | Nguyên nhân | Sửa |
|-----|-------------|-----|
| CS class có setter | CS phải immutable | Dùng record hoặc chỉ constructor + getter |
| CS class chứa logic | Sẽ làm khó test và bảo trì | Chuyển sang Factory (Bước 3) |
| Bỏ qua tầng AST, Visitor build thẳng MM | Coupling chặt, khó test/validate (bài học MAXGoal) | Luôn có `ast/` + Factory riêng |
| CS import MM class | Vòng dependency | CS chỉ được import primitive/util |
| Record không có `List.copyOf` | List có thể bị mutate sau đó | Luôn `List.copyOf()` trong constructor |
