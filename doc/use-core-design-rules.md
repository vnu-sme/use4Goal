# Quy tắc thiết kế thật của USE core

Ngày lập: 2026-07-03

Tài liệu này ghi lại (không suy đoán) cách chính USE tự thiết kế 3 việc: nạp plugin, biên dịch ngôn ngữ đặc tả của nó (`.use`/`.ocl`/`.soil`), và dựng diagram từ model/system đang chạy. Mục đích: dùng làm căn cứ cho `use-plugin-skill` khi viết plugin mới trong `goal/`, thay vì suy luận theo cảm tính.

**Nguồn**: đọc trực tiếp mã nguồn trong submodule `use/` (không sửa gì ở đây, chỉ đọc).

---

## 1. Cơ chế nạp plugin (không được và không cần sửa `use/`)

File liên quan:
- `use/use-gui/src/main/java/org/tzi/use/main/Main.java` (dòng ~85-113)
- `use/use-gui/src/main/java/org/tzi/use/runtime/MainPluginRuntime.java`
- `use/use-gui/src/main/java/org/tzi/use/runtime/util/{PluginRegistry,PluginParser,PluginClassLoader}.java`
- `use/use-gui/src/main/java/org/tzi/use/runtime/impl/{PluginRuntime,PluginDescriptor,Plugin}.java`
- `use/use-gui/src/main/java/org/tzi/use/runtime/model/{PluginModel,PluginActionModel,...}.java`

Luồng cụ thể:

1. `Main.main()` load `MainPluginRuntime.run(pluginDirURL)` nếu bật `-plugin`.
2. `MainPluginRuntime` quét thư mục plugin tìm mọi `*.jar`, gọi `PluginRuntime.getInstance().registerPlugin(...)` — `PluginRuntime` và `PluginRegistry` đều là **Singleton**.
3. `PluginRegistry` mở jar, đọc `useplugin.xml` bên trong, đưa cho `PluginParser` — 1 **SAX `DefaultHandler`** viết tay (không dùng thư viện binding), sinh ra `PluginModel` (data holder thuần, có `PluginActionModel`/`PluginServiceModel`/`PluginShellCmdModel` con).
4. `PluginModel` + jar URL được bọc thành `PluginDescriptor` (implements `IPluginDescriptor`), lưu vào `Map<String,IPluginDescriptor>` trong `PluginRuntime`, chặn trùng tên+version.
5. Với mỗi plugin đã đăng ký, `ActionExtensionPoint`/`ShellExtensionPoint` (Extension Point pattern, interface `IExtensionPoint`) đăng ký các action/command khai báo trong XML.
6. Class Java thật **chỉ được nạp khi cần** (lazy): `PluginClassLoader` — **mỗi plugin jar có 1 classloader riêng, cô lập với nhau** — nạp class đặt trong `class="..."` của `<plugin>` (nếu có, implement `IPlugin`) hoặc từng `<action class="...">` (bắt buộc implement `IPluginActionDelegate`).

**Hợp đồng bắt buộc cho 1 plugin:**
- `useplugin.xml`: gốc `<plugin name=".." version=".." class="tuỳ chọn">`, có 0..n `<action label=".." icon=".." class="bắt buộc" tooltip=".." menu=".." menuitem=".." toolbaritem=".." id="..">`.
- Nếu `<plugin class="...">` có mặt: class đó implement `org.tzi.use.runtime.IPlugin` — `String getName()` + `void run(IPluginRuntime rt)` (gọi lần đầu action nào đó được kích hoạt, không gọi lúc khởi động USE). Thường kế thừa `org.tzi.use.runtime.impl.Plugin` để có sẵn `getResource(name)`/`getResourceAsStream(name)` đọc file trong jar.
- Mỗi `<action class="...">` **bắt buộc** implement `org.tzi.use.runtime.gui.IPluginActionDelegate`:
  - `void performAction(IPluginAction pluginAction)` — trong đó lấy `pluginAction.getSession()` (→ `Session`, từ đó ra `MSystem`/`MModel`) và `pluginAction.getParent()` (→ `MainWindow`, GUI đang chạy).
  - có thể override `boolean shouldBeEnabled(IPluginAction)` (mặc định: bật khi `session.hasSystem()`).

**Kết luận, áp dụng cho `goal/`:** không có bất kỳ hook/API nào yêu cầu sửa file trong `use/`. Toàn bộ ngôn ngữ, action, form, view mới chỉ cần khai báo trong `goal/src/main/resources/useplugin.xml` + code Java trong `goal/src/main/java/...`, đóng gói thành 1 jar riêng — đúng như cách `goal` plugin đang làm.

---

## 2. Pipeline ngôn ngữ của chính USE: `.use` / `.ocl` / `.soil`

File liên quan:
- Grammar fragment: `use/use-core/src/main/resources/grammars/{base,use,ocl,soil,generator,shell,testsuite}/*.gpart`
- Build config: `use/use-core/pom.xml` (dòng ~31-34 antlr-runtime 3.4, ~140-206 merge-file + antlr3-maven-plugin)
- Compiler: `use/use-core/src/main/java/org/tzi/use/parser/use/USECompiler.java`
- AST: `use/use-core/src/main/java/org/tzi/use/parser/use/ASTModel.java` (dòng ~97-340) và các lớp `AST*` khác trong `org.tzi.use.parser.{use,ocl,soil.ast}`
- Context: `use/use-core/src/main/java/org/tzi/use/parser/Context.java`
- Factory: `org.tzi.use.uml.mm.ModelFactory`
- Metamodel: `org.tzi.use.uml.mm.{MClass,MAssociation,MModel,...}` (interface) + `M*Impl` (implementation)
- SOIL: `use/use-core/src/main/java/org/tzi/use/parser/soil/SoilCompiler.java`, thực thi qua `org.tzi.use.uml.sys.MSystem.execute(MStatement,...)` (dòng ~1242-1330)

### 2.1 Công nghệ grammar: ANTLR3 + ghép fragment, KHÔNG phải ANTLR4 đơn file

Khác với `goal` plugin (mỗi ngôn ngữ 1 file `.g4` độc lập, ANTLR4), USE core dùng **ANTLR3** và **không** có 1 file `.g` trọn vẹn nào commit sẵn — thay vào đó nhiều mảnh `.gpart` được Maven merge tại build-time thành grammar hoàn chỉnh (`target/grammars/{USE,OCL,Soil,Generator,ShellCommand,TestSuite}.g`). Ví dụ `USE.g` = `use/USE.gpart` + `base/OCLBase.gpart` + `base/SoilBase.gpart` + `base/OCLLexerRules.gpart`. Lý do: `.use` file cần nhúng cả cú pháp OCL (invariant, pre/post) và SOIL (thân operation) ngay bên trong, nên 3 ngôn ngữ này **chia sẻ ngữ pháp** — ghép fragment tránh lặp lại.

→ **Không áp dụng trực tiếp cho `goal`**: iStar/BPMN2 là ngôn ngữ độc lập, không nhúng lẫn nhau, nên giữ nguyên cách hiện tại (mỗi ngôn ngữ 1 file `.g4` ANTLR4 riêng) là hợp lý — không cần bắt chước kỹ thuật ghép fragment.

### 2.2 Pipeline dịch: AST tự sinh MM qua `gen(Context)` + `ModelFactory`, theo nhiều pha

1. `USELexer`/`USEParser` (ANTLR3, API `org.antlr.runtime.*`) đọc token stream.
2. `parser.model()` trả về `ASTModel` — **KHÔNG phải cây do ANTLR tự sinh**, mà là lớp AST viết tay (`AST*`, ví dụ `ASTClass`, `ASTAssociation`, `ASTAttribute`, `ASTPrePost`, `ASTExpression`...), do action nhúng trong grammar tạo ra khi parse.
3. Mỗi `AST*` có method **`gen(Context ctx)`** — vừa validate (báo lỗi qua `ctx.reportError(...)`, tích luỹ không throw ngay) vừa **tự sinh đối tượng metamodel** bằng cách gọi `ctx.modelFactory().createXxx(...)`. Đây là **Builder điều khiển từ chính AST**, không phải Visitor tách rời đi thăm AST từ bên ngoài.
4. `ModelFactory` (`org.tzi.use.uml.mm.ModelFactory`) có các hàm `createModel`, `createClass`, `createAssociationClass`, `createClassInvariant`, ... mỗi hàm trả về `M*Impl` tương ứng implement interface `M*`.
5. **`ASTModel.gen(Context)` build theo đúng thứ tự nhiều pha** (không làm 1 lượt duy nhất như visitor thông thường):
   - Pha 1: tạo model + tạo **khung rỗng** cho enum/datatype/class/association-class/signal (mọi tên đã tồn tại, chưa có nội dung).
   - Pha 2: thêm attribute/operation signature/generalization (giờ mọi tên kiểu đã resolve được).
   - Pha 3: sinh association, state machine/state, association-class-as-association, end-constraint.
   - Pha 4: sinh operation body, derived attribute, invariant/constraint (đồ thị interface class đã đầy đủ, OCL type-check được).
   - Pha 5: constraint toàn cục, pre/post-condition, transition của state machine.
   - **Lý do multi-pha**: hỗ trợ **forward reference** — 1 class có thể tham chiếu 1 class khai báo *sau* nó trong cùng file `.use`.
6. `Context` (`org.tzi.use.parser.Context`) là đối tượng xuyên suốt mọi lời gọi `gen()`: giữ `ModelFactory`, `MModel` hiện tại, symbol table (`Symtable`), tên file, bộ đếm lỗi.
7. Kết quả: `org.tzi.use.uml.mm.MModel` chứa đầy đủ `MClass`, `MAssociation`, `MAssociationClass`, `MDataType`, `MClassInvariant`, `MOperation`, state machine...

### 2.3 SOIL — cùng hình dạng, áp lên system đang chạy

`SoilCompiler`: ANTLR3 `SoilLexer/SoilParser` → `ASTStatement` (`org.tzi.use.parser.soil.ast.*`, ví dụ `ASTNewObjectStatement`, `ASTLinkInsertionStatement`, `ASTAttributeAssignmentStatement`) → mỗi lớp có method kiểu `gen(...)` sinh ra `org.tzi.use.uml.sys.soil.MStatement` (đối tượng có thể **thực thi được**), sau đó `MSystem.execute(MStatement, ...)` áp statement đó lên `MSystemState`/`MSystem` đang chạy (tạo/xoá `MObject`, `MLink`, `MLinkObject`...). OCL expression cũng theo hình dạng tương tự, dịch ra cây `org.tzi.use.uml.ocl.expr.Expression`, evaluate trên `MSystemState`.

### 2.4 Quy ước đặt tên của USE core

`AST*` (parser package) → `gen(Context)` → `ModelFactory.createXxx()` → `M*` (interface) / `M*Impl` (implementation).

**Đối chiếu áp dụng cho `goal`:** `M*`/`M*Impl` là compensating pattern cho thời Java chưa có `record`/`sealed interface` — **không cần copy máy móc**, vì goal đã dùng record/sealed hiện đại (ngắn gọn hơn, immutable sẵn). Điều **nên** lấy từ USE core là:
- **Factory bắt buộc cho MỌI ngôn ngữ** (đã đúng ở `IStarModelFactory`/`Bpmn2ModelFactory`, còn thiếu ở MAXGoal cũ — nay đã xoá).
- **`Context` mang theo trạng thái build + tích luỹ lỗi**, thay vì mỗi Factory tự xử lý lỗi rời rạc.
- **Build nhiều pha khi ngôn ngữ có forward-reference** (ví dụ nếu iStar cho phép actor tham chiếu actor khai báo sau, factory nên build 2 pha: tạo khung actor rỗng trước, rồi mới nối refinement/dependency — hiện tại `IStarModelFactory`/`Bpmn2ModelFactory` build 1 pha vì ngôn ngữ của chúng chưa cần forward-reference, nhưng nếu mở rộng thêm cú pháp có tham chiếu xuôi thì nên áp dụng multi-pha này thay vì lookup thủ công).

---

## 3. `MModel`/`MSystem` → Diagram: Adapter + Layout tách biệt, không auto-Observer toàn phần

File liên quan:
- `use/use-gui/src/main/java/org/tzi/use/gui/main/MainWindow.java`
- `use/use-core/src/main/java/org/tzi/use/main/Session.java`
- `use/use-gui/src/main/java/org/tzi/use/gui/views/diagrams/DiagramView.java`
- `use/use-gui/src/main/java/org/tzi/use/gui/views/diagrams/classdiagram/{ClassDiagramView,ClassDiagram,ClassNode,...}.java`
- `use/use-gui/src/main/java/org/tzi/use/gui/views/diagrams/objectdiagram/{NewObjectDiagramView,NewObjectDiagram,ObjectNode}.java`
- `use/use-gui/src/main/java/org/tzi/use/gui/views/diagrams/elements/PlaceableNode.java`, `edges/EdgeBase.java`
- `use/use-gui/src/main/java/org/tzi/use/graph/DirectedGraph.java`
- `use/use-core/src/main/java/org/tzi/use/uml/sys/events/tags/SystemStateChangedEvent.java`

### 3.1 `MainWindow` không giữ model/system trực tiếp

`MainWindow` giữ 1 `Session` (`org.tzi.use.main.Session`), và `Session` giữ `private MSystem fSystem` + danh sách `ChangeListener` (`fireStateChanged()` khi system bị thay/compile lại). `MainWindow` luôn truy cập qua `fSession.system()`; chỉ tự tạo `MSystem` mới khi (re)compile spec (`new MSystem(model)`).

### 3.2 Dựng diagram = Adapter (snapshot) → Layout riêng → Render riêng

1. Action GUI (ví dụ "Create Class Diagram") tạo `ClassDiagramView(MainWindow, MSystem, loadLayout)` — nhận `MSystem` sống nhưng build diagram như **1 snapshot tại thời điểm đó**, không bind kiểu data-binding.
2. `ClassDiagramView.initState()` duyệt `fSystem.model().classes()` (và associations/enums/signals/datatypes tương tự), gọi `fClassDiagram.addClass(cls)` cho từng `MClass` — đây là **bước build model→view**.
3. `ClassDiagram.addClass(MClass cls)` bọc đối tượng metamodel vào **node adapter**: `new ClassNode(cls, opt)` — 1 subclass của `PlaceableNode`, giữ tham chiếu ngược lại `MClass`, gán vị trí khởi tạo ngẫu nhiên, thêm vào `fGraph` (`org.tzi.use.graph.DirectedGraph<PlaceableNode,EdgeBase>` — cấu trúc đồ thị **chỉ dùng để layout/vẽ**, tách biệt hoàn toàn khỏi đồ thị ngữ nghĩa của model). Association → `EdgeBase` subclass (`BinaryAssociationOrLinkEdge`, `GeneralizationEdge`...).
4. Sau khi đồ thị Node/Edge dựng xong, **1 bước layout riêng**: `loadDefaultLayout()` hoặc thuật toán layout (`SpringLayout`...) tính toạ độ (x,y) — **tách khỏi bước 3** (bước 3 chỉ tạo node/edge, chưa quan tâm vị trí cuối cùng).
5. Render: `ClassDiagram`/`NewObjectDiagram` kế thừa `DiagramView` (`JPanel`), vẽ trực tiếp `PlaceableNode`/`EdgeBase` bằng Java2D — **chỉ vẽ, không tính toán lại vị trí**.
6. **Cập nhật khi model đổi**: KHÔNG phải auto-Observer toàn phần theo kiểu MVC chuẩn. `Session` có `ChangeListener` (báo "system đã đổi", ví dụ compile lại spec), `MSystem` có `SystemStateChangedEvent` (báo state thay đổi khi chạy SOIL). Diagram nghe các event này và **tự quyết định gọi lại `addClass`/`addObject`/`addLink`** để cập nhật đồ thị/graph, không có cơ chế tự-đồng-bộ toàn bộ mỗi khi có thay đổi nhỏ.

### 3.3 Bảng tổng hợp pattern USE core dùng cho diagram

| Bước | Vai trò | Class ví dụ trong USE core |
|---|---|---|
| Adapter | Bọc 1 phần tử metamodel thành 1 node/edge có thể đặt vị trí | `PlaceableNode`, `ClassNode`, `EdgeBase` |
| Graph chứa | Đồ thị node/edge riêng cho mục đích layout/vẽ | `DirectedGraph<PlaceableNode,EdgeBase>` |
| Layout | Tính toạ độ, tách khỏi bước tạo node/edge | `loadDefaultLayout()`, `SpringLayout` |
| Render | Vẽ node/edge đã có toạ độ | `DiagramView` (JPanel) + Java2D |
| Cập nhật | Nghe event, tự gọi lại API build khi cần | `Session.ChangeListener`, `MSystem.SystemStateChangedEvent` |

---

## 4. Áp dụng cụ thể vào `goal` plugin — hiện trạng & khuyến nghị

| Quy tắc USE core | Hiện trạng `goal` (sau khi xoá MAXGoal) | Khuyến nghị |
|---|---|---|
| Factory bắt buộc AST→MM | `IStarModelFactory`, `Bpmn2ModelFactory` — **đã đúng** | Giữ nguyên, dùng làm khuôn mẫu cho ngôn ngữ mới |
| `Context` mang trạng thái + lỗi | Chưa có — lỗi hiện chỉ báo qua `Compiler.Result.errors()` ở tầng cú pháp (ANTLR), chưa có tầng lỗi *ngữ nghĩa* tích luỹ khi build MM | Cân nhắc thêm khi cần validate ngữ nghĩa (ví dụ: dependency trỏ tới actor không tồn tại) |
| Build nhiều pha khi có forward-reference | Chưa cần (ngôn ngữ hiện tại không có tham chiếu xuôi phức tạp) | Áp dụng nếu mở rộng cú pháp có forward-reference |
| View = Adapter + Layout + Render tách biệt | **Chưa có** — `IStarView`/`Bpmn2View` tự đọc MM và tự tính toạ độ ngay trong `paintComponent` | Nên tách theo đúng 3 tầng ở mục 3.3 — xem thiết kế cụ thể trong `use-plugin-skill` |
| Plugin không sửa core | Đã đúng — mọi thứ nằm trong `goal/`, chỉ khai báo qua `useplugin.xml` | Giữ nguyên, đây là ràng buộc cứng |

Tài liệu này được `use-plugin-skill` (`.claude/skills/use-plugin-skill/SKILL.md`) trích dẫn trực tiếp — skill giữ phần "phải làm gì", tài liệu này giữ phần "tại sao/lấy từ đâu".
