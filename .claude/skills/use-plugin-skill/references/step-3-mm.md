# Bước 3 — Thiết kế MetaModel (MM) + Factory (AST → MM)

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/<lang>/mm/
src/main/java/org/vnu/sme/<plugin>/<lang>/parser/<Lang>ModelFactory.java
```

## MM vs AST: khác nhau ở đâu?

| | AST (CS) | MetaModel (MM) |
|--|---------|----------------|
| Mục đích | Lưu cú pháp đã parse | Runtime semantic objects |
| Suffix | `CS` | Không có suffix |
| Quan hệ | String ID references | Có thể object references |
| Import | Không import MM | Không import AST |
| Dùng bởi | Factory | View, Transformer |

**Dependency 1 chiều bắt buộc**: `view → mm ← factory ← ast`. MM không được biết đến `ast`
hay `view`. Factory là cầu nối **duy nhất** — không có Factory nghĩa là Visitor phải tự lo
mọi việc, lặp lại đúng lỗi thiết kế mà MAXGoal (đã xoá) từng mắc phải.

---

## Vì sao không dùng `M*`/`M*Impl` như USE core

USE core (`org.tzi.use.uml.mm.MClass`/`MClassImpl`, tạo qua `ModelFactory.createClass()`)
tách interface/implementation vì **thời điểm viết code đó Java chưa có `record`** — đây là
compensating pattern, không phải mục tiêu tự thân. MM trong `goal/` dùng **Java record +
sealed interface hiện đại**: ngắn gọn hơn, immutable sẵn, tự sinh `equals`/`hashCode`/`toString`,
không mất khả năng test hay mở rộng. Điều **giữ lại** từ USE core là ý tưởng cốt lõi: **Factory
chịu trách nhiệm tạo MM, không rải logic tạo object khắp nơi**.

---

## Khuôn mẫu MM

### Root model — class thường (cần Map lookup, không phải record)

```java
public final class <Lang>Model {
    private final String                       name;
    private final List<<Concept>Def>           entities     = new ArrayList<>();
    private final List<<Relation>>             relations    = new ArrayList<>();
    private final Map<String, <Concept>Def>    entityMap    = new LinkedHashMap<>();

    public <Lang>Model(String name) { this.name = name; }

    public void addEntity(<Concept>Def e) { entities.add(e); entityMap.put(e.id(), e); }
    public void addRelation(<Relation> r) { relations.add(r); }

    public List<<Concept>Def> getEntities()  { return Collections.unmodifiableList(entities); }
    public List<<Relation>>   getRelations() { return Collections.unmodifiableList(relations); }
    public Optional<<Concept>Def> findEntity(String id) { return Optional.ofNullable(entityMap.get(id)); }
}
```

### Sealed interface cho hierarchy đa nhánh

```java
public sealed interface <ConceptKind>
        permits <ConceptKind>.KindA, <ConceptKind>.KindB {
    String id();

    record KindA(String id) implements <ConceptKind> {}
    record KindB(String id) implements <ConceptKind> {}
}

// Hierarchy cho 1 quan hệ có nhiều hình dạng khác nhau (vd. nhiều-con vs 1-con)
public sealed interface <MultiShapeRelation> permits <MultiShapeRelation>.KindA, <MultiShapeRelation>.KindB {
    String parent();
    record KindA(String parent, List<String> children) implements <MultiShapeRelation> {}
    record KindB(String parent, String child)          implements <MultiShapeRelation> {}
}
```

### Enum có `from(String)` factory method

```java
public enum <Concept>Kind {
    DEFAULT, VARIANT_A, VARIANT_B;

    public static <Concept>Kind from(String text) {
        return switch (text.toLowerCase()) {
            case "variant-a" -> VARIANT_A;
            case "variant-b" -> VARIANT_B;
            default          -> DEFAULT;
        };
    }
}
```

### Nhánh sealed interface có List con — final class thay vì record

```java
public sealed interface FlowNode
        permits FlowNode.SimpleNode, FlowNode.CompositeNode {
    String id();

    record SimpleNode(String id, String label) implements FlowNode {}

    // Nhánh có List con → dùng final class thay vì record (cần validate/List.copyOf trong constructor)
    final class CompositeNode implements FlowNode {
        private final String id, label;
        private final List<FlowNode> elements;

        public CompositeNode(String id, String label, List<FlowNode> elements) {
            this.id = id; this.label = label;
            this.elements = List.copyOf(elements);
        }
        @Override public String id() { return id; }
        public String label() { return label; }
        public List<FlowNode> elements() { return elements; }
    }
}
```

> **Quy tắc chọn record vs final class cho 1 nhánh sealed interface**: dùng `record` khi mọi
> field truyền thẳng vào constructor không cần biến đổi; dùng `final class` viết tay khi cần
> `List.copyOf()` hay validate trong constructor (record cũng hỗ trợ compact constructor cho
> việc này — final class chỉ cần khi có thêm behavior ngoài accessor).

### Pattern matching exhaustive

```java
switch (node) {
    case FlowNode.SimpleNode    n -> paintSimple(n);
    case FlowNode.CompositeNode c -> paintComposite(c);
}
```

`sealed interface` bắt compiler báo lỗi nếu switch thiếu case — không cần `default`. Đây
chính là lợi ích chính của việc chọn `sealed interface`/`record` thay cho `M*`/`M*Impl` của
USE core (xem phần "Vì sao không dùng `M*`/`M*Impl`" ở trên).

---

## Factory: AST → MM (bắt buộc, đây là cầu nối duy nhất)

```java
public final class <Lang>ModelFactory {
    private <Lang>ModelFactory() {}

    public static <Lang>Model build(<Lang>ModelCS cs) {
        <Lang>Model model = new <Lang>Model(cs.name());
        for (<Concept>DefCS eCS : cs.entities()) model.addEntity(buildEntity(eCS));
        for (<Relation>CS rCS : cs.relations())
            model.addRelation(new <Relation>(rCS.from(), rCS.label(), rCS.to()));
        return model;
    }

    private static <Concept>Def buildEntity(<Concept>DefCS cs) {
        <Concept>Kind kind = <Concept>Kind.from(cs.kind());
        List<ElementBody> elements = new ArrayList<>();
        for (ElementBodyCS item : cs.body()) {
            switch (item) {
                case ElementBodyCS.KindACS e -> elements.add(new ElementBody.KindA(e.id()));
                case ElementBodyCS.KindBCS e -> elements.add(new ElementBody.KindB(e.id()));
                // ... các case khác
                default -> {}
            }
        }
        return new <Concept>Def(cs.id(), kind, elements /* , ... */);
    }
}
```

`static` factory method là đủ cho quy mô hiện tại (1 method `build(CS): MM`, stateless). Chỉ
cân nhắc factory dạng instance/interface (`interface ModelFactory<CS,MM> { MM build(CS cs); }`)
nếu sau này cần nhiều biến thể Factory cho cùng 1 CS (ví dụ build MM "strict" vs "lenient").

---

## Validation trong MM root (tuỳ chọn, khi cần cross-reference check)

```java
public List<String> validate() {
    List<String> errors = new ArrayList<>();
    for (Dependency d : dependencies) {
        if (findActor(d.depender()).isEmpty())
            errors.add("Unknown actor in dependency: " + d.depender());
    }
    return errors;
}
```

Nếu ngôn ngữ cần nhiều rule validate phức tạp, cân nhắc gộp vào 1 `Context`-like object được
truyền qua Factory thay vì để rải rác — xem `step-2-ast.md` phần forward-reference.

---

## Checklist bước 3

- [ ] Có sealed interface cho hierarchy đa nhánh (`IntentionalElement`, `FlowNode`, `Refinement`)
- [ ] MM node dùng Java record khi có thể; final class viết tay khi cần `List.copyOf`/validate
- [ ] Enum có `from(String)` factory method để parse từ text
- [ ] MM root có `Map<String, X>` để lookup O(1) theo ID
- [ ] Factory (`<Lang>ModelFactory`) là cầu nối DUY NHẤT từ AST sang MM — không bỏ qua bước này
- [ ] Không import gì từ package `ast` hoặc `view` trong MM
- [ ] Không import Swing/AWT trong MM
- [ ] Sealed interface `permits` liệt kê đủ tất cả subtype

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| `switch` không exhaustive | Thêm đủ case, không cần `default` nếu sealed liệt kê đủ |
| Bỏ qua Factory, Visitor tự build MM | Luôn tách Factory riêng — xem lý do ở `step-2-ast.md` |
| MM import CS class | Xoá — MM không được biết đến AST |
| MM chứa Swing/AWT | Chuyển toàn bộ UI sang View (Bước 4-5) |
| `HashMap` thay `LinkedHashMap` trong root | Mất thứ tự khai báo — dùng `LinkedHashMap` |
| Record thiếu `List.copyOf` | List có thể bị mutate từ ngoài — thêm compact constructor |
