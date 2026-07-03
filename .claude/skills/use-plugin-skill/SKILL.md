---
name: use-plugin-dev
description: >
  Hướng dẫn phát triển plugin cho USE (UML-based Specification Environment) theo đúng
  quy tắc thiết kế THẬT của chính USE core (không phải quy ước tự đặt ra) — rút ra từ
  cách USE tự nạp plugin, tự biên dịch .use/.ocl/.soil, và tự dựng diagram từ model/system
  đang chạy. Bắt buộc dùng skill này khi người dùng đang phát triển plugin USE, thêm ngôn
  ngữ mới vào USE, viết ANTLR grammar cho USE plugin, tạo diagram view trong USE, hoặc bất
  kỳ task nào liên quan đến kiến trúc plugin (action, form, compiler, ast, mm, view).
  Đừng bỏ qua skill này kể cả khi chỉ cần implement một bước — các bước có ràng buộc lẫn nhau.
---

# USE Plugin Development Skill

## Nguyên tắc bất biến — đọc trước tiên

**KHÔNG được sửa bất kỳ file nào trong `use/`** (submodule `use-core`/`use-gui`/`use-assembly`
— mã nguồn của chính USE). Mọi ngôn ngữ, action, form, compiler, view mới **chỉ được thêm
trong `goal/`**.

Đây không phải quy ước tự đặt — đó là cách USE core thực sự hoạt động: `Main.java` nạp
`MainPluginRuntime`, quét thư mục plugin tìm `*.jar`, `PluginRegistry` (Singleton) đọc
`useplugin.xml` bên trong jar bằng 1 SAX parser viết tay (`PluginParser`), rồi
`ActionExtensionPoint` đăng ký action khai báo trong đó. Class Java thật chỉ được nạp
**lazy**, mỗi plugin qua **classloader riêng cô lập** (`PluginClassLoader`). Không có API,
hook, hay lối tắt nào yêu cầu — hay thậm chí cho phép — sửa code trong `use/`. Chi tiết đầy
đủ (class/file cụ thể): [`doc/use-core-design-rules.md`](../../../doc/use-core-design-rules.md) mục 1.

**Hợp đồng bắt buộc** khi thêm 1 plugin/action:
- `goal/src/main/resources/useplugin.xml`: khai báo `<action label=".." icon=".." class="fully.Qualified.Delegate" tooltip=".." menu=".." menuitem=".." toolbaritem=".." id="..">`.
- Class đó implement `org.tzi.use.runtime.gui.IPluginActionDelegate` — bắt buộc có
  `performAction(IPluginAction)`, lấy cả `pluginAction.getSession()` (→ `Session`/`MSystem`)
  và `pluginAction.getParent()` (→ `MainWindow`).

---

## Cấu trúc thư mục chuẩn (đã áp dụng đúng ở `istar/` và `bpmn2/`)

```
goal/
├── src/main/
│   ├── java/org/vnu/sme/goal/<lang>/
│   │   ├── ast/           # AST — CS suffix, bắt buộc cho MỌI ngôn ngữ
│   │   ├── mm/             # MetaModel runtime — record/sealed interface
│   │   ├── parser/         # <Lang>BuildingVisitor (Visitor) + <Lang>ModelFactory (Factory) + <LANG>Compiler
│   │   ├── action/         # ActionOpen<Lang> — IPluginActionDelegate
│   │   ├── gui/            # <Lang>Form — JDialog
│   │   └── view/           # <Lang>View — JPanel render (xem step-4 cho thiết kế đề xuất)
│   └── resources/
│       ├── grammars/<LANG>.g4
│       ├── examples/*.<ext>
│       └── images/
├── pom.xml
└── useplugin.xml
```

`istar/` (22 file) và `bpmn2/` (20 file) trong codebase này hiện đã đúng khuôn hoàn chỉnh —
dùng làm mẫu tham chiếu trực tiếp khi thêm ngôn ngữ thứ 3.

---

## Vòng đời 1 ngôn ngữ mới

| Bước | Việc cần làm | Output | Pattern áp dụng |
|---|---|---|---|
| 1 | Grammar ANTLR4 | `<LANG>.g4` → Lexer/Parser/Visitor sinh ra | — |
| 2 | AST | package `ast/`, record/sealed hậu tố `CS` | **Visitor** (ANTLR `BaseVisitor`) |
| 3 | MetaModel | package `mm/`, record/sealed không suffix | **Factory** (AST → MM) |
| 4 | Action + Form + Compiler | `action/`, `gui/`, `parser/<LANG>Compiler` | Delegate + Result record |
| 5 | View — Adapter/Layout/Render | package `view/` | **Adapter + Layout pass + Renderer** (3 tầng tách biệt) |
| 6 | (tuỳ chọn) Transform sang ngôn ngữ khác | package `transform/` | **Visitor/Strategy** trên sealed MM |

Đọc chi tiết từng bước: [`references/step-1-grammar.md`](references/step-1-grammar.md) ·
[`step-2-ast.md`](references/step-2-ast.md) · [`step-3-mm.md`](references/step-3-mm.md) ·
[`step-5-compiler.md`](references/step-5-compiler.md) · [`step-4-view.md`](references/step-4-view.md) ·
[`step-6-transform.md`](references/step-6-transform.md)

### Khác với USE core ở đâu, và tại sao (đọc để không copy nhầm)

USE core dùng ANTLR3 + ghép fragment `.gpart` cho `.use/.ocl/.soil` (vì 3 ngôn ngữ đó
**nhúng lẫn nhau** trong cùng 1 file `.use`), và MM dùng cặp interface `M*`/`M*Impl` (compensating
pattern cho thời Java chưa có `record`). Plugin trong `goal/` **không cần bắt chước 2 điều
này**: mỗi ngôn ngữ (iStar, BPMN2, ...) độc lập — không nhúng lẫn nhau, nên ANTLR4 1-file-1-grammar
là đủ; và MM đã dùng `record`/`sealed interface` hiện đại, ngắn gọn hơn `M*`/`M*Impl` mà không
mất gì. Điều **nên** lấy từ USE core:

1. **Factory bắt buộc cho MỌI ngôn ngữ, không ngoại lệ.** (Bài học từ MAXGoal — plugin cũ đã
   xoá khỏi codebase này — từng bỏ qua bước AST/Factory, build MM thẳng từ ParseTree trong
   Visitor. Điều này làm coupling Visitor↔MM chặt, khó test, khó thêm bước validate. `istar/`
   và `bpmn2/` không mắc lỗi này.)
2. **Nếu ngôn ngữ có forward-reference** (tham chiếu tên khai báo *sau* nó), áp dụng ý tưởng
   `Context` của USE (`org.tzi.use.parser.Context`: mang symbol table + tích luỹ lỗi) và build
   theo nhiều pha (khung rỗng trước, nối quan hệ sau) — xem `step-2-ast.md`. iStar/BPMN2 hiện
   tại chưa cần vì ngôn ngữ chưa có forward-reference phức tạp.
3. **View = Adapter → Layout → Render, 3 tầng tách biệt** — đúng như `ClassDiagramView`/
   `PlaceableNode`/`DirectedGraph`/layout-pass của USE core. Đây là điểm `istar/`/`bpmn2/`
   **hiện chưa làm đúng** (View tự đọc MM và tự tính toạ độ ngay trong `paintComponent`) — xem
   thiết kế đề xuất trong `step-4-view.md`.

Toàn bộ lý giải "vì sao" nằm trong [`doc/use-core-design-rules.md`](../../../doc/use-core-design-rules.md)
— tài liệu này chỉ giữ phần "phải làm gì".

---

## Quy ước đặt tên

| Thành phần | Pattern | Ví dụ (iStar 2.0) | Ví dụ (BPMN2) |
|---|---|---|---|
| Grammar file | `<LANG>.g4` | `IStar.g4` | `Bpmn2.g4` |
| AST root | `<Lang>ModelCS` | `IStarModelCS` | `Bpmn2CollaborationCS` |
| AST node | `<Concept>CS` | `ActorDefCS`, `DependencyCS` | `PoolCS`, `FlowNodeCS` |
| MM root | `<Lang>Model` | `IStarModel` | `Bpmn2Collaboration` |
| MM node | `<Concept>Def` hoặc `<Concept>` | `ActorDef` | `Pool`, `Lane` |
| MM hierarchy | `sealed interface <X>` | `Refinement`, `IntentionalElement` | `FlowNode` |
| Visitor | `<Lang>BuildingVisitor` | `IStarBuildingVisitor` | `Bpmn2BuildingVisitor` |
| Factory | `<Lang>ModelFactory` | `IStarModelFactory` | `Bpmn2ModelFactory` |
| Compiler | `<LANG>Compiler` | `IStarCompiler` | `Bpmn2Compiler` |
| Action | `ActionOpen<Lang>` | `ActionOpenIStar` | `ActionOpenBpmn2` |
| Form | `<Lang>Form` | `IStarForm` | `Bpmn2Form` |
| View | `<Lang>View` | `IStarView` | `Bpmn2View` |

---

## Checklist bắt buộc trước khi bắt đầu

- [ ] Tên plugin và tên ngôn ngữ, extension file (`.istar`, `.bpmn2`, ...)?
- [ ] Các khái niệm cốt lõi? Quan hệ giữa chúng?
- [ ] Ngôn ngữ có forward-reference không? (Nếu có → áp dụng `Context` + multi-pha ở `step-2-ast.md`)
- [ ] Diagram cần hiển thị gì? Thiết kế Node/Edge theo mẫu Adapter ở `step-4-view.md` trước khi vẽ.
- [ ] Có transform sang ngôn ngữ khác không? (→ `step-6-transform.md`)
- [ ] Đã đọc [`doc/use-core-design-rules.md`](../../../doc/use-core-design-rules.md) để biết pattern nào lấy từ USE core, pattern nào không cần?

## Cách dùng skill này

Khi implement một bước đơn lẻ: đọc reference file tương ứng → kiểm tra bước trước đã đúng
pattern chưa → generate code theo pattern trong reference + pattern thực tế đang có trong
`istar/`/`bpmn2/` → nhắc bước tiếp theo.

Khi implement toàn bộ ngôn ngữ mới từ đầu: xác nhận checklist ở trên → đi tuần tự bước 1 → 6,
confirm sau mỗi bước → ưu tiên record/sealed interface cho MM → thiết kế View theo 3 tầng
Adapter/Layout/Render ngay từ đầu (đừng để nợ kỹ thuật như `IStarView`/`Bpmn2View` hiện tại).
