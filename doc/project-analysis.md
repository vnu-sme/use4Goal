# Phân tích dự án use-goal-BPMN

Ngày lập: 2026-07-03

Tài liệu này tổng hợp 3 phần được yêu cầu:
1. Tình trạng công cụ quản lý ngữ cảnh dự án (`code-review-graph`) và các skill hiện có.
2. Dự án này là gì, các tool chính, thiết kế (xem thêm 3 file `.drawio` kèm theo).
3. Phân tích kiến trúc quy trình phát triển plugin (6 bước) so với kiến trúc mong muốn, các phần chưa bám sát.

---

## 1. Công cụ quản lý ngữ cảnh dự án

### 1.1. `code-review-graph` — KHÔNG hoạt động đúng chức năng ở môi trường hiện tại

Kiểm tra `.mcp.json`:

```json
{
  "mcpServers": {
    "code-review-graph": {
      "command": "C:\\Users\\chaum\\.pyenv\\pyenv-win\\versions\\3.11.9\\python.exe",
      "args": ["-m", "code_review_graph", "serve"],
      "cwd": "E:\\code\\Lab\\USE_spaces\\use-goal-BPMN"
    }
  }
}
```

Vấn đề:

- **Đường dẫn Windows cứng (hard-coded)**: `command` trỏ tới một Python cài qua `pyenv-win` trên máy Windows (`C:\Users\chaum\...`), `cwd` cũng là ổ đĩa Windows (`E:\...`). Phiên làm việc hiện tại chạy trên Linux (mount tại `/run/media/qbert/.../use-goal-BPMN`) — server MCP này **không thể khởi động được** trên máy Linux với cấu hình như vậy.
- **Bằng chứng cụ thể**: hook khởi động phiên báo `Nodes: 0, Edges: 0, Files: 0, Last updated: never`, và không có tool nào (`query_graph`, `semantic_search_nodes`, `get_architecture_overview`, `detect_changes`, `get_impact_radius`, `refactor_tool`, `get_affected_flows`) xuất hiện được trong danh sách tool khả dụng của phiên này.
- **Thư mục làm việc trên Linux không phải git repo** (không có `.git`). `.gitmodules` cho thấy nó *từng* là git repo (submodule `use` trỏ `github.com/vnu-sme/use.git`), nhưng bản sao ở đường dẫn Linux hiện tại thiếu `.git`. Điều này còn chặn thêm `detect_changes` (vốn dựa trên diff git) dù server có kết nối được.
- `graph.db` nặng 150MB tồn tại trong `.code-review-graph/`, nhiều khả năng là dữ liệu index cũ từ máy Windows, chưa từng được build lại cho bản sao Linux này.

**Kết luận**: công cụ này **hiện không lưu lại/khôi phục được ngữ cảnh dự án** như kỳ vọng trong `CLAUDE.md`. Đây là lỗi cấu hình môi trường (cross-machine), không phải do project sai thiết kế.

**Đề xuất khắc phục** (nếu muốn dùng lại):
- Sửa `.mcp.json`: `command` → `python3` (hệ thống có sẵn tại `/usr/bin/python3`), `cwd` → đường dẫn Linux thực tế.
- Cần cài package `code_review_graph` cho Python này (`pip show code_review_graph` để kiểm tra).
- `git init` (hoặc khôi phục `.git` gốc) để `detect_changes` hoạt động.
- Sau đó chạy lệnh index/serve lại để build graph mới cho bản sao Linux.

### 1.2. Skills trong `.claude/skills/`

| Skill | File | Trạng thái | Ghi chú |
|---|---|---|---|
| `use-plugin-skill` | `SKILL.md` (đúng chuẩn) | **Hoạt động** — có trong danh sách skill khả dụng | Mô tả đúng và khớp với codebase thực tế (xem mục 3) |
| `debug-issue` | `skill.md` (chữ thường) | **Hỏng — không được nhận diện** | Claude Code yêu cầu tên file chính xác `SKILL.md`; do đặt `skill.md` nên skill này **không xuất hiện** trong danh sách skill khả dụng đầu phiên |
| `explore-codebase` | `skill.md` (chữ thường) | **Hỏng — không được nhận diện** | Tương tự |
| `refactor-safely` | `skill.md` (chữ thường) | **Hỏng — không được nhận diện** | Tương tự |
| `review-changes` | `skill.md` (chữ thường) | **Hỏng — không được nhận diện** | Tương tự |

Ngoài lỗi tên file, cả 4 skill "hỏng" này đều được viết để **chạy hoàn toàn dựa vào** các tool của `code-review-graph` (`semantic_search_nodes`, `query_graph`, `get_flow`, `detect_changes`, `refactor_tool`, `get_minimal_context`, `list_communities`...). Vì mục 1.1 cho thấy server đó không kết nối được ở môi trường này, **kể cả sau khi đổi tên file thành `SKILL.md`, 4 skill này vẫn không dùng được** cho tới khi `code-review-graph` được sửa xong.

→ Tóm lại: bộ máy "lưu ngữ cảnh dự án bằng graph" quảng cáo trong `CLAUDE.md` hiện **không vận hành** trên máy này ở cả 2 lớp (MCP server lẫn skill phụ thuộc nó). Chỉ `use-plugin-skill` (không phụ thuộc graph) là dùng được ngay.

---

## 2. Dự án này là gì

### 2.1. Bản chất

Đây là **bộ plugin mở rộng cho USE** (UML-based Specification Environment — công cụ đặc tả/kiểm chứng mô hình UML+OCL, submodule `use/` trỏ `vnu-sme/use`, Java 21, Maven multi-module theo `pom.xml` gốc: `use-core`, `use-gui`, `use-assembly`, `goal`).

Plugin tên **MAGoalTax — Goal Decomposition Plugin** (`goal/.../Main.java`, implements `IPlugin`), bổ sung cho USE **3 ngôn ngữ đặc tả văn bản mới** cộng 1 định dạng đích trung gian:

| Ngôn ngữ | File mẫu | Ý tưởng |
|---|---|---|
| **MAXGoal** (`.maxgoal`) | `loan_approval.maxgoal` | Ngôn ngữ goal-oriented đa tác nhân tự thiết kế: `Actor { Goal/Task/Resource }` + khối `depend`; mỗi Goal/Task có `RefineSpec` kiểu SEQ / PAR / XOR / IOR / ITER |
| **iStar 2.0** (`.istar`) | `DigitalLibrarySystem.istar`, `travel_reimbursement.istar` | Ngôn ngữ i* chuẩn học thuật: actor/goal/task/resource/quality, refinement AND/OR, contribution, qualification, is-a/participates-in |
| **BPMN 2.0 textual** (`.bpmn2`) | `order_process.bpmn2` | Cú pháp text cho collaboration/pool/lane/flow-node/sequence-flow/message-flow |
| **MAXBpmn** (`.maxbpmn`) | — (sinh ra, không có mẫu tay) | Không phải ngôn ngữ nhập của người dùng — là **định dạng đích** sinh ra từ việc transform MAXGoal → BPMN, chỉ có *writer* (ghi ra text), chưa có compiler đọc lại |

### 2.2. Tool chính & luồng dùng thực tế

Mỗi ngôn ngữ (trừ MAXBpmn) có: 1 `Action*` (đăng ký trong `useplugin.xml`, xuất hiện trên menu Plugins của USE) → mở 1 `*Form` (JDialog không modal, độc lập với `ViewFrame` gốc của USE) → bên trong Form có nút "Compile" gọi `*Compiler.compile(...)` → kết quả đổ vào 1 `*View` (JPanel tự vẽ bằng `Graphics2D`).

Riêng `MAXGoalForm` đặc biệt hơn: là **giao diện kép** — vừa hiển thị `MAXGoalView` (mô hình goal), vừa có nút "→ BPMN" gọi `GoalToBpmnTransformer.transform(model)` để sinh `BpmnProcess` và hiển thị luôn ở `MAXBpmnView` bên cạnh, cùng nút "Save .maxbpmn" gọi `MAXBpmnWriter`.

Xem chi tiết cấu trúc trực quan trong **[`01-project-overview.drawio`](01-project-overview.drawio)**.

---

## 3. Kiến trúc plugin: hiện tại so với mong muốn

### 3.1. Quy trình 6 bước chuẩn (theo `.claude/skills/use-plugin-skill/SKILL.md`, khớp với ý định của bạn)

```
.lang file → [1 Grammar ANTLR4] → ParseTree
           → [2 AST/CS]          → AST (record hậu tố CS)
           → [3+Factory]         → MetaModel (MM, record/sealed interface)
           → [6 transform]       → Model khác để hiển thị (View/Layout model)
           → [4 View]            → JPanel + Graphics2D
```
Nguyên tắc bắt buộc: MM không import AST; cầu nối AST→MM chỉ qua Factory/Visitor; View chỉ đọc MM, không đọc AST.

### 3.2. Đối chiếu thực tế từng ngôn ngữ

| Bước | MAXGoal | iStar 2.0 | BPMN2 (textual) | MAXBpmn |
|---|---|---|---|---|
| 1. Grammar ANTLR4 | ✅ `MAXGoal.g4` | ✅ `IStar.g4` | ✅ `Bpmn2.g4` | ✅ `MAXBpmn.g4` (chỉ có grammar, chưa nối vào compiler) |
| 2. AST (package `ast/`, hậu tố `CS`) | ❌ **Không có** | ✅ `ActorDefCS, DependencyCS, ElementBodyCS, IStarModelCS` | ✅ `PoolCS, LaneCS, FlowNodeCS, MessageFlowCS, SequenceFlowCS, Bpmn2CollaborationCS` | — (không có input pipeline) |
| 3. MM (record/sealed) | ✅ `MAXGoalModel`, `RefineSpec` (sealed) | ✅ `IStarModel` | ✅ `Bpmn2Collaboration` | ✅ `BpmnProcess` (là *đích* của transform, không phải parse) |
| Factory AST→MM riêng | ❌ **Không có** — `MAXGoalBuildingVisitor` build **thẳng MM** từ `ParseTree`, bỏ qua bước AST hoàn toàn | ✅ `IStarModelFactory.build(cs)` tách biệt khỏi `IStarBuildingVisitor` | ✅ `Bpmn2ModelFactory.build(cs)` tách biệt khỏi `Bpmn2BuildingVisitor` | N/A |
| 4. View | ✅ `MAXGoalView` (465 dòng) | ✅ `IStarView` | ✅ `Bpmn2View` | ✅ `MAXBpmnView` |
| 5. Action+Form+Compiler | ✅ | ✅ | ✅ | ❌ không có Action/Form/Compiler riêng — dùng ké `MAXGoalForm` |
| 6. Transform MM → model hiển thị | ⚠️ Không tách riêng — xem 3.3 | ⚠️ như trên | ⚠️ như trên | — |

### 3.3. Những điểm **chưa bám sát** kiến trúc mong muốn của bạn

**(a) MAXGoal bỏ hẳn bước AST — vi phạm chính quy tắc mà project tự đặt ra**

`MAXGoalBuildingVisitor.build()` (`goal/.../maxgoal/parser/MAXGoalBuildingVisitor.java:13-31`) đi thẳng `MAXGoalParser.ModelContext → MAXGoalModel`, không qua lớp AST trung gian, không có `MAXGoalModelFactory`. Trong khi đó `IStarModelFactory` (dòng 12 file tương ứng) và `Bpmn2ModelFactory` đều ghi rõ comment: *"Dependency direction: factory → ast, factory → mm. Neither ast nor mm knows the other."* — đúng nguyên tắc bạn muốn (áp Factory pattern làm cầu nối AST→MM), nhưng **chỉ 2/3 ngôn ngữ tuân theo**, MAXGoal thì không. Đây là ngôn ngữ *chính* của plugin (goal decomposition) nên sự thiếu nhất quán này đáng chú ý nhất.

→ Nếu theo đúng kiến trúc bạn mô tả (DSL → ANTLR → AST → áp design pattern → MM), cần bổ sung cho MAXGoal: package `maxgoal/ast/` (các record `*CS` tương ứng `GoalDefCS`, `TaskDefCS`, `ActorCS`, `RefineSpecCS`...) + `MAXGoalModelFactory` tách khỏi Visitor, theo đúng khuôn của `iStar`/`Bpmn2`.

**(b) Bước 6 "transform MM sang model khác để hiển thị" — hiện KHÔNG tồn tại như một lớp riêng biệt**

Đây là điểm bạn nhấn mạnh nhất ("về mặt giao diện hiển thị, phải dịch từ meta model tương ứng sang mô hình khác để biểu diễn"). Thực tế hiện tại:

- Mỗi `*View` (`MAXGoalView`, `IStarView`, `Bpmn2View`, `MAXBpmnView`) là 1 `JPanel` **tự đọc MM và tự tính toán layout** trong chính nó (ví dụ `MAXGoalView` có `private static class Node { ... int x,y,w,h }` và hàm build layout nội bộ), rồi vẽ luôn trong `paintComponent(Graphics2D)`.
- Nghĩa là bước "MM → Diagram/Layout model" và bước "Layout model → vẽ pixel" đang **gộp chung vào 1 class UI**, không tách thành 1 tầng transform độc lập, không test được riêng, không tái sử dụng logic layout cho renderer khác (SVG export, in ấn...) mà không kéo theo Swing.
- Có 2 khái niệm "transform" đang bị gọi chung tên nhưng khác bản chất trong code: `GoalToBpmnTransformer` là **transform liên-ngôn-ngữ** (MM ngôn ngữ A → MM ngôn ngữ B, ở tầng nghiệp vụ, đúng như bạn nói "về nghiệp vụ thì đến đây là xong"), còn cái bạn hỏi thêm — MM → model hiển thị — là **transform nội-ngôn-ngữ để phục vụ UI**, và cái này **chưa được tách ra** thành step riêng như skill mô tả.

→ Đề xuất: thêm 1 lớp `<Lang>LayoutBuilder` (ví dụ `MAXGoalLayoutBuilder.build(MAXGoalModel): DiagramLayout`) đứng giữa MM và View. `DiagramLayout` là POJO thuần (danh sách Node{id,x,y,w,h,kind}, Edge{from,to,style}), không phụ thuộc Swing. `*View` (JPanel) lúc đó chỉ còn nhận `DiagramLayout` và vẽ — đúng vai "View" thuần theo nghĩa MVVM, tách khỏi việc suy luận vị trí/kích thước.

**(c) Các design pattern biên dịch bạn muốn (Factory, Visitor, Strategy...) mới dùng rải rác, chưa hình thức hoá dùng chung**

- **Visitor**: đã dùng đúng nghĩa ANTLR (`*BaseVisitor`) để build AST/MM — tốt.
- **Factory**: đã có ở iStar/BPMN2 nhưng dạng static method rời rạc (`Bpmn2ModelFactory.build`, `IStarModelFactory.build`), không có interface chung (`ModelFactory<CS,MM>`) — nếu muốn thêm ngôn ngữ thứ 4 tương lai, không có "khuôn" bắt buộc tuân theo ngoài quy ước đặt tên trong SKILL.md.
- **Strategy/Visitor cho `RefineSpec`**: logic `switch (refine) { case SeqRefine ... case ParRefine ... }` đang bị **lặp lại ở ít nhất 2 nơi độc lập** — trong `GoalToBpmnTransformer.transformRefine()` (biên dịch sang BPMN) và trong `MAXGoalView` (vẽ cạnh refine). Mỗi khi thêm 1 kiểu `RefineSpec` mới, phải sửa ở cả 2 chỗ (dễ quên 1 chỗ). Đây là cơ hội rõ ràng để dùng **Visitor pattern chuẩn hoá** (`interface RefineSpecVisitor<R> { R seq(...); R par(...); R xor(...); R ior(...); R iter(...); }`), cả transformer lẫn layout-builder đều implement interface này thay vì switch rời rạc.
- **"Table syntax"** bạn nhắc tới: các hàm `ActorKind.from(String)`, `ResKind.from(String)`, `GatewayType.from(String)`, `EventType.from(String)`, `ContribType.from(String)`, `AssocKind` — mỗi enum tự viết lại kiểu bảng tra cứu chuỗi→enum giống hệt nhau. Có thể rút thành 1 helper chung (map/bảng) để tránh lặp, đúng tinh thần "table-driven" bạn đề cập.

### 3.4. Tóm tắt: mức độ bám sát kiến trúc mong muốn

- **AST tách biệt khỏi MM qua Factory**: đạt ở iStar & BPMN2, **chưa đạt ở MAXGoal** (ngôn ngữ chính).
- **Transform liên ngôn ngữ (nghiệp vụ, MM→MM)**: đã có và đúng ý (`GoalToBpmnTransformer`), nhưng dùng switch thủ công thay vì Visitor pattern chuẩn hoá — chấp nhận được, có thể nâng cấp.
- **Transform MM → model hiển thị (bước 6 riêng biệt)**: **chưa tồn tại như 1 tầng độc lập** — đây là khoảng cách lớn nhất so với điều bạn mong muốn, hiện logic layout bị nhúng cứng trong lớp View (JPanel).
- **Design pattern dùng nhất quán, có khuôn chung** (Factory interface, Visitor interface cho RefineSpec, table-driven enum lookup): **chưa hình thức hoá**, mỗi chỗ tự viết lại theo cảm tính.

Sơ đồ so sánh trực quan: **[`02-current-pipeline-architecture.drawio`](02-current-pipeline-architecture.drawio)** (hiện trạng, có đánh dấu chỗ thiếu) và **[`03-target-pipeline-architecture.drawio`](03-target-pipeline-architecture.drawio)** (kiến trúc đề xuất, áp design pattern đầy đủ).

---

## 4. Việc cần làm nếu muốn đồng bộ hoá kiến trúc (gợi ý thứ tự ưu tiên)

1. Bổ sung tầng AST (`ast/` + `*CS` records) + `MAXGoalModelFactory` cho MAXGoal, đưa về cùng khuôn với iStar/BPMN2.
2. Tách 1 interface `RefineSpecVisitor<R>` dùng chung cho mọi nơi cần xử lý theo loại refine (transformer, layout builder) — xoá switch lặp.
3. Thêm tầng `<Lang>LayoutBuilder` (MM → DiagramLayout POJO) cho từng ngôn ngữ, tách khỏi `*View`; `*View` chỉ còn render.
4. (Tuỳ chọn) Chuẩn hoá `ModelFactory<CS,MM>` interface chung + table-driven enum lookup helper, để dễ mở rộng ngôn ngữ thứ 4/5 sau này.
5. Sửa `.mcp.json` cho `code-review-graph` (path Linux, `git init`) nếu muốn dùng lại graph + 4 skill phụ thuộc nó; đồng thời đổi tên `skill.md` → `SKILL.md` cho 4 skill đó.

---

## 5. Danh sách file đính kèm trong `doc/`

- `01-project-overview.drawio` — tổng quan plugin, các ngôn ngữ, luồng Action→Form→Compiler→View.
- `02-current-pipeline-architecture.drawio` — kiến trúc pipeline hiện tại theo từng ngôn ngữ, đánh dấu bước bị thiếu (MAXGoal thiếu AST/Factory, thiếu tầng layout riêng).
- `03-target-pipeline-architecture.drawio` — kiến trúc đề xuất, áp đầy đủ Factory/Visitor/Strategy pattern + tầng LayoutBuilder độc lập.
