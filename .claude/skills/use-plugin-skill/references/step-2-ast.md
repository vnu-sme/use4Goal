# Bước 2 — Thiết kế AST (Abstract Syntax Tree)

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/<lang>/ast/
```

## Vai trò của AST — bắt buộc cho MỌI ngôn ngữ, không ngoại lệ

AST (Concrete Syntax — hậu tố `CS`) là lớp trung gian giữa ParseTree (ANTLR) và MM runtime.
Nó ánh xạ gần 1-1 với cấu trúc grammar, lưu đủ thông tin để xây MM (kể cả source text thô của
condition/expression), không chứa logic semantic, và immutable sau khi Visitor build xong.

> **Bài học từ MAXGoal (đã xoá khỏi codebase)**: plugin đó từng bỏ qua tầng AST — Visitor
> build thẳng MM từ ParseTree. Hậu quả: Visitor và MM coupling chặt, không tách được validate
> khỏi parse, khó viết test riêng cho tầng build. **Không lặp lại — mọi ngôn ngữ mới đều phải
> có tầng AST riêng**, đúng như `istar/ast/` và `bpmn2/ast/` hiện có.

---

## Ví dụ thật: AST của iStar 2.0 (`istar/ast/`)

### Root model

```java
public final class IStarModelCS {
    private final String              name;
    private final List<ActorDefCS>    actors;
    private final List<DependencyCS>  dependencies;

    public IStarModelCS(String name, List<ActorDefCS> actors, List<DependencyCS> dependencies) {
        this.name         = name;
        this.actors       = List.copyOf(actors);
        this.dependencies = List.copyOf(dependencies);
    }

    public String             name()         { return name; }
    public List<ActorDefCS>   actors()       { return actors; }
    public List<DependencyCS> dependencies() { return dependencies; }
}
```

### Node record

```java
public record ActorDefCS(String id, String kind, List<ElementBodyCS> body) {}
public record DependencyCS(String depender, String dependum, String dependee) {}
```

### Sealed hierarchy cho thân actor (nhiều loại khai báo trộn lẫn)

```java
public sealed interface ElementBodyCS
        permits ElementBodyCS.GoalCS, ElementBodyCS.TaskCS, ElementBodyCS.ResourceCS,
                ElementBodyCS.QualityCS, ElementBodyCS.AndRefineCS, ElementBodyCS.OrRefineCS,
                ElementBodyCS.NeededByCS, ElementBodyCS.ContributionCS,
                ElementBodyCS.QualificationCS, ElementBodyCS.IsACS, ElementBodyCS.ParticipatesCS {

    record GoalCS(String id) implements ElementBodyCS {}
    record TaskCS(String id) implements ElementBodyCS {}
    record ResourceCS(String id) implements ElementBodyCS {}
    record QualityCS(String id) implements ElementBodyCS {}
    record AndRefineCS(String parent, List<String> children) implements ElementBodyCS {}
    record OrRefineCS(String parent, String child) implements ElementBodyCS {}
    record NeededByCS(String resource, String task) implements ElementBodyCS {}
    record ContributionCS(String element, String type, String quality) implements ElementBodyCS {}
    record QualificationCS(String quality, String element) implements ElementBodyCS {}
    record IsACS(String actor, String target) implements ElementBodyCS {}
    record ParticipatesCS(String actor, String target) implements ElementBodyCS {}
}
```

Đây là ví dụ hierarchy AST phẳng — mỗi kiểu khai báo trong thân actor là 1 `record`
implement chung 1 sealed interface, tương ứng 1-1 với các labeled alternative trong grammar.

---

## Visitor: ParseTree → AST

```java
public final class IStarBuildingVisitor extends IStarBaseVisitor<Object> {

    public static IStarModelCS build(IStarParser.ModelContext ctx) {
        List<ActorDefCS> actors = ctx.actorDef().stream()
                .map(a -> (ActorDefCS) new IStarBuildingVisitor().visitActorDef(a))
                .collect(Collectors.toList());
        // ...
        return new IStarModelCS(ctx.IDENT().getText(), actors, dependencies);
    }

    @Override
    public Object visitGoalElem(IStarParser.GoalElemContext ctx) {
        return new ElementBodyCS.GoalCS(ctx.IDENT().getText());
    }

    @Override
    public Object visitAndRefine(IStarParser.AndRefineContext ctx) {
        return new ElementBodyCS.AndRefineCS(
                ctx.IDENT(0).getText(),
                ctx.IDENT().subList(1, ctx.IDENT().size()).stream()
                        .map(TerminalNode::getText).collect(Collectors.toList()));
    }
}
```

Xem `goal/src/main/java/org/vnu/sme/goal/istar/parser/IStarBuildingVisitor.java` và
`bpmn2/parser/Bpmn2BuildingVisitor.java` cho code đầy đủ.

---

## Khi ngôn ngữ có forward-reference: áp dụng ý tưởng `Context` của USE

iStar/BPMN2 hiện tại **không cần** phần này (không có tham chiếu tên khai báo *sau* nó trong
cùng scope theo cách gây vấn đề). Nhưng nếu 1 ngôn ngữ mới cần — ví dụ actor A tham chiếu tới
actor B khai báo phía sau trong cùng file — áp dụng đúng cách USE core làm với `.use`:

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
