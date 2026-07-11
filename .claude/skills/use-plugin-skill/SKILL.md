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

## Cấu trúc thư mục chuẩn cho 1 ngôn ngữ trong `goal/`

```
goal/
├── src/main/
│   ├── java/org/vnu/sme/goal/<lang>/
│   │   ├── ast/           # AST — CS suffix, bắt buộc cho MỌI ngôn ngữ
│   │   ├── mm/             # MetaModel runtime — record/sealed interface
│   │   ├── parser/         # <Lang>BuildingVisitor (Visitor) + <Lang>ModelFactory (Factory) + <LANG>Compiler
│   │   ├── action/         # ActionOpen<Lang> — IPluginActionDelegate
│   │   ├── gui/            # <Lang>Form — JDialog
│   │   └── view/           # <Lang>Node/<Lang>Edge (Adapter) + <Lang>Layout + <Lang>LayoutBuilder + <Lang>View (xem step-4-view.md)
│   └── resources/
│       ├── grammars/<LANG>.g4
│       ├── examples/*.<ext>
│       └── images/
├── pom.xml
└── useplugin.xml
```

Cấu trúc này là hợp đồng bắt buộc cho **mọi** ngôn ngữ mới thêm vào `goal/`, không phụ thuộc
vào việc project hiện đang có sẵn bao nhiêu ngôn ngữ ví dụ (0, 1, hay nhiều) — tự đứng vững
kể cả khi bắt đầu từ một `goal/` trống.

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

USE core (mã nguồn thật trong `use/`) dùng ANTLR3 + ghép fragment `.gpart` cho
`.use/.ocl/.soil` (vì 3 ngôn ngữ đó **nhúng lẫn nhau** trong cùng 1 file `.use`), và MM dùng
cặp interface `M*`/`M*Impl` (compensating pattern cho thời Java chưa có `record`). Plugin
trong `goal/` **không cần bắt chước 2 điều này**: một ngôn ngữ mới độc lập — không nhúng lẫn
ngôn ngữ khác, nên ANTLR4 1-file-1-grammar là đủ; và MM nên dùng `record`/`sealed interface`
hiện đại, ngắn gọn hơn `M*`/`M*Impl` mà không mất gì. Điều **nên** lấy từ USE core:

1. **Factory bắt buộc cho MỌI ngôn ngữ, không ngoại lệ** — dù ngôn ngữ có vẻ đơn giản đến
   đâu. Bỏ qua tầng AST/Factory và để Visitor build MM thẳng từ ParseTree làm coupling
   Visitor↔MM chặt, khó test, khó thêm bước validate (đây từng là lỗi thiết kế của một plugin
   cũ trong lịch sử project — đã bị xoá khỏi codebase chính vì lý do này).
2. **Nếu ngôn ngữ có forward-reference** (tham chiếu tên khai báo *sau* nó), áp dụng ý tưởng
   `Context` của USE (`org.tzi.use.parser.Context`: mang symbol table + tích luỹ lỗi) và build
   theo nhiều pha (khung rỗng trước, nối quan hệ sau) — xem `step-2-ast.md`.
3. **View = Adapter → Layout → Render, 3 tầng tách biệt** — đúng như `ClassDiagramView`/
   `PlaceableNode`/`DirectedGraph`/layout-pass thật của USE core (`use/use-gui/.../gui/views/diagrams/`)
   — xem thiết kế trong `step-4-view.md`.
4. **Nếu view là diagram graph thật, ưu tiên kế thừa hạ tầng diagram của USE thay vì tự viết
   Swing canvas từ đầu**: dùng `DiagramView`, `DiagramOptions`, `PlaceableNode`, `EdgeBase`,
   `DiagramGraph`, `DiagramInputHandling`, `ActionSaveLayout`, `ActionLoadLayout` khi cần UI
   đồng bộ với tool gốc. Custom `JPanel + paintComponent` chỉ là fallback cho preview đơn giản
   hoặc khi hạ tầng USE không phù hợp.
5. **Form mở file chỉ là loader cổ điển, không phải nơi chứa diagram**: giữ `<Lang>Form`
   tối giản (`File`, `Browse`, `Open`, tuỳ chọn dropdown nhỏ, `Close`). Sau khi compile OK,
   mở diagram mặc định trong USE `ViewFrame`; nếu có popup ngoài USE thì đó là placement thứ
   hai của cùng diagram, và context menu phải chuyển được hai chiều.

Toàn bộ lý giải "vì sao", với trích dẫn class/file cụ thể trong `use/`, nằm trong
[`doc/use-core-design-rules.md`](../../../doc/use-core-design-rules.md) — tài liệu này chỉ
giữ phần "phải làm gì". Mọi ví dụ minh hoạ trong skill này (nếu có) lấy từ mã nguồn thật của
`use/`, **không** lấy từ các ngôn ngữ ví dụ hiện có trong `goal/` (những ngôn ngữ đó có thể bị
xoá hoặc thay thế bất cứ lúc nào — skill không được phụ thuộc vào chúng còn tồn tại).

---

## Quy ước đặt tên

`<Lang>` = tên ngôn ngữ dạng chữ hoa đầu (vd. một ngôn ngữ tên "Foo" → `Foo`), `<LANG>` = toàn
chữ hoa (`FOO`), `<lang>` = toàn chữ thường dùng cho tên package (`foo`). `<Concept>` = tên
một khái niệm cụ thể trong ngôn ngữ đó (actor, task, pool, ...).

| Thành phần | Pattern |
|---|---|
| Grammar file | `<LANG>.g4` |
| AST root | `<Lang>ModelCS` |
| AST node | `<Concept>CS` |
| MM root | `<Lang>Model` |
| MM node | `<Concept>Def` hoặc `<Concept>` |
| MM hierarchy | `sealed interface <X>` (X = tên khái niệm đa nhánh) |
| Visitor | `<Lang>BuildingVisitor` |
| Factory | `<Lang>ModelFactory` |
| Compiler | `<LANG>Compiler` |
| Action | `ActionOpen<Lang>` |
| Form | `<Lang>Form` |
| View — Adapter | `<Lang>Node`, `<Lang>Edge` |
| View — Layout | `<Lang>Layout`, `<Lang>LayoutBuilder` |
| View — Renderer | `<Lang>View`; với diagram graph thật thêm `<Lang>Diagram`, `<Lang>DiagramOptions` |

---

## Checklist bắt buộc trước khi bắt đầu

- [ ] Tên plugin và tên ngôn ngữ, extension file (dạng `.<ext>`, ví dụ `.foo`)?
- [ ] Các khái niệm cốt lõi? Quan hệ giữa chúng?
- [ ] Ngôn ngữ có forward-reference không? (Nếu có → áp dụng `Context` + multi-pha ở `step-2-ast.md`)
- [ ] Diagram cần hiển thị gì? Thiết kế Node/Edge theo mẫu Adapter ở `step-4-view.md` trước khi vẽ.
- [ ] Loader form có đang tối giản và tách khỏi diagram window chưa? Diagram phải mở qua `ViewFrame` mặc định.
- [ ] Có transform sang ngôn ngữ khác không? (→ `step-6-transform.md`)
- [ ] Đã đọc [`doc/use-core-design-rules.md`](../../../doc/use-core-design-rules.md) để biết pattern nào lấy từ USE core, pattern nào không cần?

## Cách dùng skill này

Khi implement một bước đơn lẻ: đọc reference file tương ứng → kiểm tra bước trước đã đúng
pattern chưa → generate code theo template generic trong reference (không cần đối chiếu với
bất kỳ ngôn ngữ ví dụ cụ thể nào đang có sẵn trong `goal/`) → nhắc bước tiếp theo.

Khi implement toàn bộ ngôn ngữ mới từ đầu: xác nhận checklist ở trên → đi tuần tự bước 1 → 6,
confirm sau mỗi bước → ưu tiên record/sealed interface cho MM → thiết kế View theo 3 tầng
Adapter/Layout/Render ngay từ đầu theo `step-4-view.md`.

Nếu `goal/` đã có sẵn 1 vài ngôn ngữ khác (ví dụ hiện tại có thể là iStar, BPMN2, hoặc bất kỳ
tên nào) và bạn muốn đối chiếu thực tế: được phép đọc code của chúng để tham khảo phong cách,
nhưng **không được coi chúng là nguồn chân lý của quy tắc** — quy tắc nằm trong skill này và
trong `doc/use-core-design-rules.md` (dựa trên `use/`). Các ngôn ngữ ví dụ đó có thể bị xoá
bất cứ lúc nào mà không ảnh hưởng gì đến tính đúng đắn của skill.
