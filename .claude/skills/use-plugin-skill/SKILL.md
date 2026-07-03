---
name: use-plugin-dev
description: >
  Hướng dẫn phát triển plugin cho USE (UML-based Specification Environment) theo đúng quy trình 6 bước bắt buộc:
  (1) Grammar ANTLR4, (2) AST, (3) MetaModel ngôn ngữ, (4) MetaModel giao diện/Diagram,
  (5) Action+Form+Compiler, (6) TransformationRules từ MM sang View.
  Bắt buộc dùng skill này khi người dùng đang phát triển plugin USE, thêm ngôn ngữ mới vào USE,
  viết ANTLR grammar cho USE plugin, tạo diagram view trong USE, hoặc bất kỳ task nào liên quan đến
  USE plugin architecture (action, form, loader, diagram, ast, mm). Đừng bỏ qua skill này kể cả khi
  chỉ cần implement một bước — các bước có ràng buộc lẫn nhau.
---

# USE Plugin Development Skill

Plugin trong USE theo kiến trúc phân lớp nghiêm ngặt. Mỗi ngôn ngữ đặc tả mới cần đi qua **đúng 6 bước** theo thứ tự — bỏ bước hoặc đảo thứ tự sẽ gây lỗi compile hoặc runtime.

## Cấu trúc thư mục chuẩn

```
<plugin-name>/
├── src/main/
│   ├── java/org/vnu/sme/<plugin>/
│   │   ├── ast/           # Bước 2: AST classes (hậu tố CS)
│   │   ├── mm/            # Bước 3: MetaModel runtime (record/sealed)
│   │   ├── view/          # Bước 4: Custom JPanel diagram view
│   │   ├── parser/        # Bước 5: Compiler, Visitor, Factory
│   │   │   └── semantic/  # (nếu cần) semantic checks riêng
│   │   ├── action/        # Bước 5: Action + Form
│   │   └── transform/     # (tuỳ chọn) transformer sang ngôn ngữ khác
│   └── resources/
│       ├── grammars/
│       │   └── <LANG>.g4  # Bước 1: Grammar ANTLR4
│       ├── images/        # Icon cho action menu
│       └── META-INF/
│           └── MANIFEST.MF
├── pom.xml
└── useplugin.xml          # Khai báo action/button trong USE GUI
```

---

## Quy trình 6 bước — tổng quan nhanh

| Bước | Việc cần làm | Output chính |
|------|-------------|--------------|
| 1 | Thiết kế grammar ANTLR4 | `<LANG>.g4` → generated Lexer/Parser/Visitor |
| 2 | Thiết kế AST | Package `ast/` — record/class với hậu tố `CS` |
| 3 | Thiết kế MetaModel ngôn ngữ | Package `mm/` — record/sealed interface runtime |
| 4 | Thiết kế Diagram View | Package `view/` — `JPanel` custom + `Graphics2D` renderer |
| 5 | Viết Action + Form + Compiler | Package `action/` + `parser/` |
| 6 | Viết Transformation Rules | `buildLayout()` trong View + `paintComponent()` + edge renderer |

Đọc reference chi tiết từng bước tại `references/step-<N>.md`.

---

## Checklist bắt buộc trước khi bắt đầu

Trước khi viết bất kỳ dòng code nào, xác định rõ:

- [ ] Tên plugin và tên ngôn ngữ? (ví dụ: plugin `goal`, ngôn ngữ `MAXGOAL`, extension `.maxgoal`)
- [ ] Các khái niệm cốt lõi của ngôn ngữ? (actor, goal, task, resource, dependency...)
- [ ] Quan hệ giữa các khái niệm? (refinement: SEQ/PAR/XOR/IOR/ITER? dependency? contribution?)
- [ ] Diagram cần hiển thị gì? (actor containers, node shapes, edge styles)
- [ ] Plugin có transform sang ngôn ngữ khác không? (ví dụ: GOAL → BPMN)
- [ ] Plugin có cần semantic validation không? (cross-reference check, OCL evaluation?)

---

## Luồng dữ liệu đầy đủ

```
.lang file
    │
    ▼  (Bước 1: grammar ANTLR4)
ANTLR Lexer/Parser  ──►  ParseTree
    │
    ▼  (Bước 5: <Lang>ModelBuildingVisitor)
AST (CS classes)    ◄── Bước 2: GoalModelCS, GoalDef CS, ...
    │
    ▼  (Bước 5: <Lang>ModelFactory hoặc trực tiếp từ Visitor)
MetaModel (MM)      ◄── Bước 3: sealed interface / record
    │
    ▼  (Bước 6: buildLayout() + paintComponent())
Diagram View        ◄── Bước 4: JPanel + Graphics2D
    │
    ▼
USE JDialog / JFrame (hiển thị độc lập, không qua ViewFrame)
```

> **Lưu ý kiến trúc thực tế**: Plugin trong project này (MAXGoal, goal) dùng
> `JDialog` standalone thay vì `ViewFrame` của USE. View là `JPanel` custom
> với `Graphics2D`, không kế thừa `CompartmentNode` hay `PlaceableNode`.
> `CompartmentNode` chỉ dùng cho USE core ClassDiagram.

---

## Nguyên tắc kiến trúc bắt buộc

1. **CS vs MM tách biệt**: Class AST kết thúc bằng `CS`. MM không có suffix. Không trộn lẫn.

2. **MM không import AST**: Dependency 1 chiều: `view → mm ← factory ← ast`. MM không biết đến `ast` hay `view`.

3. **Factory/Visitor là cầu nối**: AST → MM chỉ đi qua `*Factory` hoặc thẳng trong `*BuildingVisitor`. View chỉ đọc MM, không đọc AST.

4. **View là custom JPanel + Graphics2D**: Vẽ toàn bộ diagram trong `paintComponent(Graphics2D)`. Không dùng `null-layout + setBounds` cho node khi cần cạnh tự cập nhật — thay vào đó dùng internal node model (class `Node` trong view).

5. **Action nhận Session + MainWindow**: `IPluginActionDelegate.performAction(IPluginAction)` cung cấp cả `getSession()` và `getParent()` (MainWindow). Luôn lấy cả hai.

6. **Compiler thuần Java**: `<LANG>Compiler` không có Swing. Trả về `Result` record chứa `model` + `errors`.

7. **Form là JDialog**: `<Lang>ModelForm` extends `JDialog`, không phải `JFrame`. Non-modal (`false`).

8. **SwingUtilities.invokeLater**: Mọi thao tác UI sau compile phải chạy trong `invokeLater`.

---

## Quy ước đặt tên

| Thành phần | Pattern | Ví dụ (MAXGoal) |
|------------|---------|-----------------|
| Grammar file | `<LANG>.g4` | `MAXGoal.g4` |
| AST root | `<Lang>ModelCS` | `MAXGoalModelCS` |
| AST node | `<Concept>CS` hoặc `<Concept>DefCS` | `GoalDefCS`, `ActorCS` |
| MM root | `<Lang>Model` | `MAXGoalModel` |
| MM node (record) | `<Concept>Def` hoặc `<Concept>` | `GoalDef`, `Actor` |
| MM hierarchy | `sealed interface <X>` | `RefineSpec` |
| Visitor | `<Lang>BuildingVisitor` | `MAXGoalBuildingVisitor` |
| Factory | `<Lang>ModelFactory` | `MAXGoalModelFactory` |
| Compiler | `<LANG>Compiler` | `MAXGoalCompiler` |
| View | `<Lang>View` | `MAXGoalView` |
| Action | `ActionOpen<LANG>` | `ActionOpenMAXGoal` |
| Form | `<Lang>ModelForm` | `MAXGoalForm` |

---

## Pattern hiện đại trong codebase này

### MM dùng Java Record + Sealed Interface

```java
// MM node đơn giản → dùng record
public record GoalDef(
        String     name,
        String     owner,
        String     intentClause,
        String     intentExpr,
        RefineSpec refine        // nullable
) implements Intentional {}

// Hierarchy phức tạp → dùng sealed interface
public sealed interface RefineSpec
        permits RefineSpec.SeqRefine, RefineSpec.IterRefine,
                RefineSpec.ParRefine,
                RefineSpec.IorRefine, RefineSpec.XorRefine {

    non-sealed abstract class SeqRefine implements RefineSpec {
        public abstract List<String> children();
    }

    record ParRefine(List<String> children) implements RefineSpec {}

    final class XorRefine extends IorRefine implements RefineSpec { ... }
}
```

Sealed interface cho phép `switch` exhaustive:
```java
switch (refine) {
    case RefineSpec.SeqRefine s  -> paintSeqEdges(s.children());
    case RefineSpec.ParRefine p  -> paintParEdges(p.children());
    case RefineSpec.XorRefine x  -> paintXorEdges(x.branches());
    case RefineSpec.IorRefine io -> paintIorEdges(io.branches());
    case RefineSpec.IterRefine it -> paintIterEdges(it.children(), it.until());
}
```

### Compiler trả về Result record

```java
public final class MAXGoalCompiler {

    public record Result(MAXGoalModel model, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    public static Result compile(Path file) throws IOException { ... }
    public static Result compile(String source) { ... }
}
```

### View dùng internal Node model

```java
// Trong MAXGoalView — không dùng JPanel per node
private static class Node {
    String id, label, clause;
    NT     kind;        // enum: ACTOR, GOAL, TASK, RES
    String actorId;
    RefineSpec refine;
    int x, y, w, h;    // absolute coords trong canvas
}

private final Map<String, Node> nodes = new LinkedHashMap<>();

@Override
protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    paintActors(g2);
    paintRefineEdges(g2);
    paintDependEdges(g2);
    paintNodes(g2);
}
```

---

## useplugin.xml chuẩn

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plugin>
  <name>MAXGoal Plugin</name>
  <version>1.0</version>
  <vendor>VNU-SME</vendor>

  <actions>
    <action id="openMAXGoal"
            label="Open MAXGoal..."
            class="org.vnu.sme.goal.actions2.ActionOpenMAXGoal"
            icon="images/goal.png"
            tooltip="Load and visualise a MAXGoal model"
            menuPath="Plugins/MAXGoal"/>
  </actions>
</plugin>
```

---

## Reference files

- [step-1-grammar.md](references/step-1-grammar.md) — ANTLR4 grammar design, structure, error listener, codegen
- [step-2-ast.md](references/step-2-ast.md) — AST CS classes, record vs class, immutability
- [step-3-mm.md](references/step-3-mm.md) — MM record/sealed pattern, validation, lookup
- [step-4-view.md](references/step-4-view.md) — JPanel + Graphics2D view, internal Node model, drag, paint layers
- [step-5-compiler.md](references/step-5-compiler.md) — Action (Session+MainWindow), Form (JDialog), Compiler (Result record), Visitor, Factory
- [step-6-transform.md](references/step-6-transform.md) — buildLayout(), paintComponent layers, edge rendering per RefineSpec type

---

## Cách dùng skill này

Khi implement một bước đơn lẻ:
1. Đọc reference file tương ứng
2. Kiểm tra bước trước đã đúng pattern chưa
3. Generate code theo pattern trong reference + pattern thực tế trong codebase
4. Nhắc bước tiếp theo

Khi implement toàn plugin từ đầu:
1. Xác nhận checklist ở trên
2. Đi tuần tự bước 1 → 6, confirm sau mỗi bước
3. Ưu tiên dùng Java records + sealed interfaces cho MM mới
4. View dùng Graphics2D custom painting (xem MAXGoalView làm mẫu)
