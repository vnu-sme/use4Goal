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

## Ví dụ thật: MM của iStar 2.0 (`istar/mm/`)

### Root model — class thường (cần Map lookup, không phải record)

```java
public final class IStarModel {
    private final String                 name;
    private final List<ActorDef>         actors = new ArrayList<>();
    private final List<Dependency>       dependencies = new ArrayList<>();
    private final Map<String, ActorDef>  actorMap = new LinkedHashMap<>();

    public IStarModel(String name) { this.name = name; }

    public void addActor(ActorDef a) { actors.add(a); actorMap.put(a.id(), a); }
    public void addDependency(Dependency d) { dependencies.add(d); }

    public List<ActorDef>   getActors()       { return Collections.unmodifiableList(actors); }
    public List<Dependency> getDependencies() { return Collections.unmodifiableList(dependencies); }
    public Optional<ActorDef> findActor(String id) { return Optional.ofNullable(actorMap.get(id)); }
}
```

### Sealed interface cho hierarchy đa nhánh

```java
public sealed interface IntentionalElement
        permits IntentionalElement.Goal, IntentionalElement.Task,
                IntentionalElement.Resource, IntentionalElement.Quality {
    String id();

    record Goal(String id)     implements IntentionalElement {}
    record Task(String id)     implements IntentionalElement {}
    record Resource(String id) implements IntentionalElement {}
    record Quality(String id)  implements IntentionalElement {}
}

// Hierarchy cho quan hệ refine (And/Or khác cấu trúc: nhiều con vs 1 con)
public sealed interface Refinement permits Refinement.And, Refinement.Or {
    String parent();
    record And(String parent, List<String> children) implements Refinement {}
    record Or (String parent, String child)          implements Refinement {}
}
```

### Enum có `from(String)` factory method

```java
public enum ActorKind {
    ACTOR, AGENT, ROLE;

    public static ActorKind from(String text) {
        return switch (text.toLowerCase()) {
            case "agent" -> AGENT;
            case "role"  -> ROLE;
            default      -> ACTOR;
        };
    }
}
```

### Ví dụ MM khác dạng: BPMN2 (`bpmn2/mm/FlowNode.java`) — hierarchy 6 nhánh, 1 nhánh phức tạp hơn record thường

```java
public sealed interface FlowNode
        permits FlowNode.StartEvent, FlowNode.EndEvent, FlowNode.IntermediateEvent,
                FlowNode.Task, FlowNode.SubProcess, FlowNode.Gateway {
    String id();

    record StartEvent(String id, EventType type) implements FlowNode {}
    record Task(String id, String label)         implements FlowNode {}

    // Nhánh có List con → dùng final class thay vì record (cần validate/List.copyOf trong constructor)
    final class SubProcess implements FlowNode {
        private final String id, label;
        private final List<FlowNode> elements;
        private final List<SequenceFlow> flows;

        public SubProcess(String id, String label, List<FlowNode> elements, List<SequenceFlow> flows) {
            this.id = id; this.label = label;
            this.elements = List.copyOf(elements);
            this.flows    = List.copyOf(flows);
        }
        @Override public String id() { return id; }
        public String label() { return label; }
        public List<FlowNode> elements() { return elements; }
        public List<SequenceFlow> flows() { return flows; }
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
    case FlowNode.StartEvent e -> paintEvent(e, THIN);
    case FlowNode.EndEvent   e -> paintEvent(e, THICK);
    case FlowNode.Task       t -> paintTask(t);
    case FlowNode.Gateway    g -> paintGateway(g);
    case FlowNode.SubProcess s -> paintSubProcess(s);
    case FlowNode.IntermediateEvent e -> paintEvent(e, DOUBLE);
}
```

`sealed interface` bắt compiler báo lỗi nếu switch thiếu case — không cần `default`.

---

## Factory: AST → MM (bắt buộc, đây là cầu nối duy nhất)

```java
public final class IStarModelFactory {
    private IStarModelFactory() {}

    public static IStarModel build(IStarModelCS cs) {
        IStarModel model = new IStarModel(cs.name());
        for (ActorDefCS aCS : cs.actors()) model.addActor(buildActor(aCS));
        for (DependencyCS dCS : cs.dependencies())
            model.addDependency(new Dependency(dCS.depender(), dCS.dependum(), dCS.dependee()));
        return model;
    }

    private static ActorDef buildActor(ActorDefCS cs) {
        ActorKind kind = ActorKind.from(cs.kind());
        List<IntentionalElement> elements = new ArrayList<>();
        for (ElementBodyCS item : cs.body()) {
            switch (item) {
                case ElementBodyCS.GoalCS e -> elements.add(new IntentionalElement.Goal(e.id()));
                case ElementBodyCS.TaskCS e -> elements.add(new IntentionalElement.Task(e.id()));
                // ... các case khác
                default -> {}
            }
        }
        return new ActorDef(cs.id(), kind, elements /* , ... */);
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
