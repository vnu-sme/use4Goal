# Bước 3 — Thiết kế MetaModel (MM) ngôn ngữ

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/mm/
```

## MM vs AST: khác nhau ở đâu?

| | AST (CS) | MetaModel (MM) |
|--|---------|----------------|
| Mục đích | Lưu cú pháp đã parse | Runtime semantic objects |
| Suffix | `CS` | Không có suffix |
| Mutability | Immutable | Thường immutable (record/final) |
| Logic | Không | Có (query, lookup, validate) |
| Quan hệ | String ID references | Có thể object references |
| Import | Không import MM | Không import AST |
| Dùng bởi | Factory/Visitor | View, Analyzer, Transformer |

---

## Pattern hiện đại: Java Record + Sealed Interface

Codebase này dùng Java 17+ records và sealed interfaces cho MM. Đây là pattern chuẩn cho plugin mới.

### Root model

```java
package org.vnu.sme.<plugin>.mm;

public final class MAXGoalModel {

    private final String           name;
    private final List<Actor>      actors       = new ArrayList<>();
    private final List<Dependency> dependencies = new ArrayList<>();

    // Lookup maps — build lúc addActor() để query O(1)
    private final Map<String, Intentional> intentionalMap = new HashMap<>();
    private final Map<String, String>      ownerMap       = new HashMap<>();

    public MAXGoalModel(String name) { this.name = name; }

    public String getName() { return name; }

    public void addActor(Actor a) {
        actors.add(a);
        for (Intentional i : a.intentionals()) {
            intentionalMap.put(i.name(), i);
            ownerMap.put(i.name(), a.name());
        }
    }

    public void addDependency(Dependency d) { dependencies.add(d); }

    public List<Actor>      getActors()       { return Collections.unmodifiableList(actors); }
    public List<Dependency> getDependencies() { return Collections.unmodifiableList(dependencies); }

    public Optional<Intentional> find(String id) {
        return Optional.ofNullable(intentionalMap.get(id));
    }

    public Optional<Actor> actorOf(String intentionalId) {
        String aname = ownerMap.get(intentionalId);
        if (aname == null) return Optional.empty();
        return actors.stream().filter(a -> a.name().equals(aname)).findFirst();
    }

    public Map<String, Intentional> allIntentionals() {
        return Collections.unmodifiableMap(intentionalMap);
    }
}
```

### Intentional element (sealed interface)

```java
public sealed interface Intentional
        permits GoalDef, TaskDef, ResourceDef {
    String name();
}
```

### MM nodes dùng record

```java
// GoalDef — immutable, dùng record
public record GoalDef(
        String     name,
        String     owner,          // actor name
        String     intentClause,   // "achieve"|"maintain"|"avoid"|null
        String     intentExpr,     // raw condition text (cho OCL eval sau)
        RefineSpec refine          // null nếu leaf
) implements Intentional {}

// TaskDef
public record TaskDef(
        String     name,
        String     owner,
        String     pre,
        String     post,
        String     needby,         // resource ID, nullable
        RefineSpec refine
) implements Intentional {}

// ResourceDef
public record ResourceDef(
        String  name,
        String  owner,
        ResKind kind
) implements Intentional {}
```

### Enums

```java
public enum ActorKind {
    AGENT, ROLE, POSITION;

    public static ActorKind from(String text) {
        return switch (text.toLowerCase()) {
            case "agent"    -> AGENT;
            case "role"     -> ROLE;
            case "position" -> POSITION;
            default -> throw new IllegalArgumentException("Unknown actor kind: " + text);
        };
    }
}

public enum ResKind {
    DATA, SERVICE, PHYSICAL;

    public static ResKind from(String text) {
        return switch (text.toLowerCase()) {
            case "data"     -> DATA;
            case "service"  -> SERVICE;
            case "physical" -> PHYSICAL;
            default -> DATA; // default safe
        };
    }
}
```

### Actor (không phải record — cần List mutable lúc build)

```java
public final class Actor {
    private final String          name;
    private final ActorKind       kind;
    private final List<Intentional> intentionals;

    public Actor(String name, ActorKind kind, List<Intentional> intentionals) {
        this.name         = name;
        this.kind         = kind;
        this.intentionals = List.copyOf(intentionals);
    }

    public String              name()         { return name; }
    public ActorKind           kind()         { return kind; }
    public List<Intentional>   intentionals() { return intentionals; }
}
```

### Dependency

```java
public record Dependency(String from, String to) {}
```

---

## Refinement hierarchy (sealed interface phức tạp)

Pattern thực tế từ `RefineSpec.java`:

```java
public sealed interface RefineSpec
        permits RefineSpec.SeqRefine, RefineSpec.IterRefine,
                RefineSpec.ParRefine,
                RefineSpec.IorRefine, RefineSpec.XorRefine {

    // ── Sequential (ordered, chạy 1 lần) ──────────────────────────
    non-sealed abstract class SeqRefine implements RefineSpec {
        public abstract List<String> children();

        public static SeqRefine of(List<String> children) {
            return new SeqRefine() {
                @Override public List<String> children() { return children; }
                @Override public String toString() { return "SEQ" + children; }
            };
        }
    }

    // ── ITER extends SEQ (loop body) ───────────────────────────────
    final class IterRefine extends SeqRefine implements RefineSpec {
        private final List<String> children;
        private final String       until;

        public IterRefine(List<String> body, String until) {
            this.children = List.copyOf(body);
            this.until    = until;
        }

        @Override public List<String> children() { return children; }
        public       String           until()     { return until; }
    }

    // ── Parallel ────────────────────────────────────────────────────
    record ParRefine(List<String> children) implements RefineSpec {}

    // ── Inclusive-OR (1 hoặc nhiều nhánh) ──────────────────────────
    non-sealed abstract class IorRefine implements RefineSpec {
        public abstract List<GuardedChild> branches();

        public static IorRefine of(List<GuardedChild> branches) {
            return new IorRefine() {
                @Override public List<GuardedChild> branches() { return branches; }
            };
        }
    }

    // ── XOR extends IOR (đúng 1 nhánh) ─────────────────────────────
    final class XorRefine extends IorRefine implements RefineSpec {
        private final List<GuardedChild> branches;
        public XorRefine(List<GuardedChild> branches) {
            this.branches = List.copyOf(branches);
        }
        @Override public List<GuardedChild> branches() { return branches; }
    }
}

// Guarded branch: (condition → childId)
public record GuardedChild(String condition, String childId) {}
```

### Pattern matching với sealed interface

```java
// Exhaustive switch — compiler báo lỗi nếu thiếu case
switch (refine) {
    case RefineSpec.IterRefine it  -> handleIter(it.children(), it.until());
    case RefineSpec.SeqRefine  s   -> handleSeq(s.children());
    case RefineSpec.ParRefine  p   -> handlePar(p.children());
    case RefineSpec.XorRefine  x   -> handleXor(x.branches());
    case RefineSpec.IorRefine  io  -> handleIor(io.branches());
}
```

> **Thứ tự quan trọng**: `IterRefine` phải đứng trước `SeqRefine` vì nó là subclass.

---

## Validation trong MM root

```java
// Trong MAXGoalModel (hoặc tương tự)
public List<String> validate() {
    List<String> errors = new ArrayList<>();

    // Check: mọi refine child reference phải tồn tại
    for (Intentional item : intentionalMap.values()) {
        RefineSpec refine = switch (item) {
            case GoalDef g     -> g.refine();
            case TaskDef t     -> t.refine();
            case ResourceDef r -> null;
        };
        if (refine == null) continue;

        List<String> childIds = switch (refine) {
            case RefineSpec.SeqRefine  s  -> s.children();
            case RefineSpec.ParRefine  p  -> p.children();
            case RefineSpec.IorRefine io  -> io.branches().stream()
                                               .map(GuardedChild::childId)
                                               .collect(Collectors.toList());
            case RefineSpec.IterRefine it -> it.children();
        };

        for (String childId : childIds) {
            if (!intentionalMap.containsKey(childId))
                errors.add("Unresolved reference '" + childId +
                           "' in refinement of '" + item.name() + "'");
        }
    }

    // Check: không có circular refinement (DFS)
    // ...

    return errors;
}
```

---

## Checklist bước 3

- [ ] Có sealed interface cho hierarchy (Intentional, RefineSpec)
- [ ] MM nodes dùng Java record (immutable, tự sinh equals/hashCode/toString)
- [ ] Enums có `from(String)` factory method để parse từ text
- [ ] MM root có `Map<String, X>` để lookup O(1) theo ID
- [ ] MM root có `validate()` kiểm tra cross-reference
- [ ] Không import gì từ package `ast` hoặc `parser`
- [ ] Không import Swing/AWT
- [ ] Sealed interface `permits` liệt kê đủ tất cả subtype
- [ ] Thứ tự case trong switch: subclass trước, superclass sau

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| `switch` không exhaustive | Thêm đủ case hoặc default |
| `IterRefine` bị match bởi `SeqRefine` trước | Đặt `IterRefine` trước `SeqRefine` trong switch |
| MM import CS class | Xoá — MM không được biết đến AST |
| MM chứa Swing/AWT | Chuyển toàn bộ UI sang View (Bước 4) |
| HashMap thay LinkedHashMap trong root | Mất thứ tự khai báo — dùng LinkedHashMap |
| Record thiếu `List.copyOf` | List có thể bị mutate từ ngoài — thêm vào constructor |
