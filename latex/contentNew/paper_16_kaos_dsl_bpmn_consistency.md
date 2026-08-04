# Nghiên Cứu Phát Triển Ngôn Ngữ DSL và Công Cụ Hỗ Trợ cho Mô Hình Hoá Yêu Cầu Hướng Mục Tiêu

**Tác giả:** Nguyễn Thu Trang  |  **Năm:** 2026  |  **Loại:** Khóa luận tốt nghiệp đại học hệ chính quy  |  **Cơ sở:** Trường Đại học Công nghệ, Đại học Quốc gia Hà Nội  |  **CBHD:** PGS.TS. Đặng Đức Hạnh  |  **Ngành:** Công nghệ thông tin định hướng thị trường Nhật Bản

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Khóa luận thuộc lĩnh vực **Kỹ nghệ yêu cầu hướng mục tiêu** (Goal-Oriented Requirements Engineering — GORE) kết hợp với **Kỹ nghệ phần mềm hướng mô hình** (Model-Driven Software Engineering — MDSE). Đây là hai xu hướng đang ngày càng quan trọng trong phát triển phần mềm hiện đại, đặc biệt trong các dự án lớn đòi hỏi tính truy vết giữa ý định chiến lược và thiết kế hệ thống.

Phân tích và đặc tả yêu cầu là khâu quyết định thành bại của dự án phần mềm. Sai sót ở giai đoạn yêu cầu tốn chi phí sửa chữa gấp 100 lần so với phát hiện trong giai đoạn coding. KAOS (Keep All Objectives Satisfied) là một trong những phương pháp GORE mạnh nhất — cho phép mô hình hóa yêu cầu từ nhiều góc nhìn khác nhau: tại sao hệ thống tồn tại (goals), ai tham gia (agents), dữ liệu gì được thao tác (objects), và hệ thống làm gì cụ thể (operations).

Sau khi xây dựng xong mô hình yêu cầu KAOS, bước tiếp theo trong thực tế là thiết kế quy trình nghiệp vụ bằng BPMN (Business Process Model and Notation) — chuẩn đồ họa được sử dụng rộng rãi nhất để mô tả luồng công việc. Tuy nhiên, **không có cơ chế tự động nào kiểm tra xem mô hình BPMN có phản ánh đúng và đầy đủ các mục tiêu đã đặc tả trong KAOS hay không**. Khoảng cách này dẫn đến một vấn đề thực tiễn nghiêm trọng: các nhà thiết kế dễ vô tình tạo ra quy trình BPMN mâu thuẫn với ý định ban đầu.

### Bài toán cụ thể

Khóa luận giải quyết hai bài toán song song:

**Bài toán 1 — Thiếu công cụ mô hình hóa KAOS tích hợp**: Các công cụ hỗ trợ KAOS hiện tại xử lý từng góc nhìn (goal view, object view, agent view, operation view) riêng lẻ, không có cấu trúc thống nhất. Người dùng không thể vẽ đồng thời và truy vết xuyên suốt bốn loại biểu đồ trong một môi trường duy nhất.

**Bài toán 2 — Thiếu cơ chế kiểm tra nhất quán KAOS-BPMN**: Sau khi có mô hình KAOS và mô hình BPMN, không có cách nào tự động xác nhận rằng: (a) mỗi thao tác trong KAOS có Task tương ứng trong BPMN; (b) agent chịu trách nhiệm trong KAOS được ánh xạ đúng sang Pool/Lane trong BPMN; (c) cấu trúc phân rã AND/OR của goals được phản ánh đúng bằng Parallel/Exclusive Gateway.

**Đầu vào**: Mô hình KAOS (file `*.base`) + Mô hình BPMN (file `*.bpmn2`).  
**Đầu ra**: Báo cáo kiểm tra chi tiết — danh sách "Satisfied" và "Violation" theo từng quy tắc, với chỉ định chính xác phần tử nào trong mô hình đang bị sai.

### Tại sao khó

**Thứ nhất**, KAOS và BPMN có metamodel hoàn toàn khác nhau. KAOS là ngôn ngữ goal-oriented với semantics intentional (actors có intentions, goals có satisfaction conditions). BPMN là ngôn ngữ process-oriented với semantics operational (tasks được execute, gateways điều khiển flow). Ánh xạ giữa hai thế giới này không trivial và không có chuẩn hóa sẵn.

**Thứ hai**, KAOS có bốn góc nhìn liên kết với nhau — xây dựng DSL phải hỗ trợ cả bốn góc nhìn này trong một metamodel nhất quán, với OCL constraints đảm bảo không có inconsistency giữa các views.

**Thứ ba**, kiểm tra nhất quán hành vi (behavioral consistency) — đặc biệt là R2.1 (AND-refinement) và R2.2 (OR-refinement) — đòi hỏi phải hiểu semantics của cả goal decomposition trong KAOS lẫn control flow patterns trong BPMN, rồi kiểm tra xem chúng có tương thích không.

### Đóng góp chính

1. **DSL cho KAOS**: Ngôn ngữ chuyên biệt miền với cú pháp trừu tượng (Ecore metamodel) và cú pháp đồ họa (Eclipse Sirius), tích hợp đủ bốn góc nhìn KAOS trong một môi trường.
2. **7 quy tắc kiểm tra nhất quán KAOS-BPMN**: Được phân thành 2 nhóm — structural mapping rules (R1.1–R1.4) và behavioral consistency rules (R2.1–R2.3).
3. **Plugin Eclipse tự động hóa**: Công cụ thực thi 7 quy tắc với báo cáo trực quan dạng cây, phát hiện 100% vi phạm trong case study thực nghiệm.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

Bài báo không có phần Related Work truyền thống riêng biệt — nội dung liên quan được tích hợp vào phần cơ sở lý thuyết và phần thiết kế phương pháp. Dưới đây là các hướng nghiên cứu liên quan chính.

### Hướng 1: KAOS và Công Cụ Hỗ Trợ KAOS

KAOS (Lamsweerde, 2001) là một trong những phương pháp GORE có ảnh hưởng lớn nhất, với các khái niệm phong phú về AND/OR goal refinement, agent assignment, obstacle analysis. Công cụ KAOS Tools (LAAS/CNRS) là công cụ gốc nhưng đã ngừng phát triển. Objectiver là commercial tool hiện tại nhưng không open-source, không hỗ trợ extensibility. Các công cụ này xử lý từng view riêng lẻ và không có export format tích hợp để kết nối với BPMN tools.

### Hướng 2: Tích Hợp Goal Models và BPMN

Một số nghiên cứu về alignment giữa goal models và BPMN đã tồn tại — đáng chú ý là Koliadis & Ghose (GoalBPM, 2006), Ghose & Koliadis (DL-based validation, 2007), và Guizzardi & Reis (5-step alignment, 2012). Tuy nhiên, hầu hết các nghiên cứu này tập trung vào i*/GRL chứ không phải KAOS. Các công cụ tự động kiểm tra nhất quán KAOS-BPMN chưa tồn tại.

### Hướng 3: Xây Dựng DSL với Eclipse/EMF/Sirius

Eclipse Modeling Framework (EMF/Ecore) là nền tảng standard cho xây dựng DSL trong học thuật và công nghiệp. Eclipse Sirius là framework để tạo graphical DSL từ Ecore metamodel. Cặp công nghệ này được dùng rộng rãi nhưng chưa được áp dụng để tạo DSL KAOS đa góc nhìn tích hợp.

### Khoảng Trống

Chưa có công cụ nào: (1) hỗ trợ đủ bốn góc nhìn KAOS trong một môi trường đồ họa tích hợp, (2) tự động kiểm tra nhất quán giữa KAOS model và BPMN model dựa trên tập quy tắc có ngữ nghĩa rõ ràng. Khóa luận này lấp đầy khoảng trống đó.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Ý tưởng tổng thể của khóa luận là **xây dựng cầu nối hình thức giữa thế giới goal-oriented (KAOS) và thế giới process-oriented (BPMN)** thông qua hai đóng góp bổ sung nhau:

- **Phía KAOS**: Tạo DSL chuẩn hóa để KAOS model có metamodel formal, có thể đọc/phân tích bằng chương trình — tiền đề cần thiết cho bất kỳ automated reasoning nào.
- **Phía KAOS↔BPMN**: Định nghĩa tập quy tắc ánh xạ có ngữ nghĩa rõ ràng, cài đặt thành plugin tự động đối chiếu hai model và báo cáo inconsistencies.

Điểm then chốt: các quy tắc không chỉ kiểm tra cấu trúc (element X có tồn tại không?) mà còn kiểm tra **hành vi** (khi goal được phân rã AND, BPMN phải đảm bảo cả hai nhánh đều xảy ra — không được dùng Exclusive Gateway loại trừ một nhánh).

### 3.2 Kiến Trúc / Pipeline Tổng Thể

```
Người dùng vẽ KAOS model
  (trong Eclipse + DSL tool)
         ↓
  Lưu tệp *.base (EMF/Ecore)
         ↓
Người dùng vẽ / import BPMN model
         ↓
  Lưu tệp *.bpmn2
         ↓
Chọn 2 file → Chuột phải
→ "Check KAOS-BPMN Consistency"
         ↓
  [handler] Nhận sự kiện
         ↓
  [loader] Đọc *.base → EObject (KAOS)
           Đọc *.bpmn2 → EObject (BPMN)
         ↓
  [core / ConsistencyEngine]
    → Khởi tạo ngữ cảnh (build context)
    → Chạy tuần tự 7 quy tắc R1.1 → R2.3
    → Mỗi quy tắc: so sánh elements
      → Satisfied / Violation
         ↓
  [views] Hiển thị Consistency View
    → Nền xanh "THE MODELS ARE CONSISTENT"
      hoặc nền đỏ "INCONSISTENT"
    → Cây phân cấp theo từng quy tắc
    → Dấu 'i' xanh (Satisfied) / 'x' đỏ (Violation)
    → Mô tả chính xác element nào bị sai
```

### 3.3 Các Thành Phần Chính

#### 3.3.1 DSL cho KAOS — Cú Pháp Trừu Tượng (Abstract Syntax)

Cú pháp trừu tượng được định nghĩa dưới dạng siêu mô hình Ecore, tổ chức thành kiến trúc phân tầng:

**Lớp Base** (gốc): Hai khái niệm cốt lõi `Node` (mọi phần tử mô hình có tên và mô tả) và `Link` (mọi quan hệ kết nối hai Node). Mọi khái niệm KAOS đều kế thừa từ đây — đảm bảo uniformity.

**Khung nhìn Đối Tượng (Object View)** — trả lời "Trên cái gì?":
- `Object` (trừu tượng) → chuyên biệt hóa thành `Entity` (thực thể domain), `Event` (sự kiện kích hoạt goal), `Agent` (tác nhân — xem thêm ở Agent View)
- `Attribute` (thuộc tính của Object, với `type`, `Multiplicity`, danh sách `domainValues`)
- Quan hệ: `Association` (tổng quát) → `Specialization` (kế thừa), `Aggregation` (tập hợp), `Tracking` (theo dõi trạng thái)

**Khung nhìn Thao Tác (Operation View)** — trả lời "Cái gì?":
- `Operation` (hành vi/dịch vụ hiện thực hóa goal), với `domPre` (precondition) và `domPost` (postcondition) trên state variables
- `Operationalization` (quan hệ nối Goal lá với Operation thực hiện nó)
- `InputLink`, `OutputLink` (luồng dữ liệu vào/ra của Operation)

**Khung nhìn Mục Tiêu (Goal View)** — trả lời "Tại sao?":
- Goals với phân rã AND/OR, contributions, obstacles

**Khung nhìn Tác Tử (Agent View)** — trả lời "Ai?":
- Agents (Human, Software-to-be, Environment) với khả năng và trách nhiệm

#### 3.3.2 DSL cho KAOS — OCL Constraints (Ràng Buộc Hợp Lệ)

Các ràng buộc OCL được nhúng trực tiếp vào Ecore để phát hiện lỗi logic ngay khi người dùng vẽ model:

| Constraint | Context | Biểu thức | Ý nghĩa |
|---|---|---|---|
| `UniqueAttributes` | `Object` | `self.attributes->isUnique(name)` | Không cho phép hai thuộc tính trùng tên trong cùng một entity |
| `ValidInterval` | `Multiplicity` | `(self.upper <> -1) implies (self.lower <= self.upper)` | Cận trên phải ≥ cận dưới |
| `NoInheritanceCycle` | `Specialization` | `self.target->closure(...)->excludes(self.source)` | Không có vòng lặp trong quan hệ kế thừa |
| *(các constraints khác)* | `Operation` | Pre/post variables phải thuộc object tương ứng | Đảm bảo tính đúng đắn của state machine |

#### 3.3.3 DSL cho KAOS — Cú Pháp Đồ Họa (Graphical Concrete Syntax)

Được xây dựng bằng Eclipse Sirius — ánh xạ các lớp Ecore thành visual elements:

| Khái niệm KAOS | Hình dạng đồ họa |
|---|---|
| `Entity` | Hình chữ nhật |
| `Operation` | Hình oval |
| `Event` | Hình thoi hoặc ký hiệu sự kiện |
| `Agent` | Hình người hoặc ký hiệu actor |
| Quan hệ kế thừa (`Specialization`) | Mũi tên rỗng |
| `Association` | Đường nối có nhãn bản số (multiplicity) |
| `InputLink`/`OutputLink` | Mũi tên có nhãn |

Môi trường cung cấp: Palette kéo thả, Project Explorer quản lý file `*.base` + `*.aird`, và bốn loại editor biểu đồ tương ứng bốn góc nhìn KAOS.

#### 3.3.4 Plugin Kiểm Tra Nhất Quán — 5 Packages

Plugin được tổ chức thành 5 gói (packages) tách biệt rõ ràng giữa logic và UI:

| Package | Vai trò |
|---|---|
| `consistency` | Quản lý vòng đời plugin (activation, lifecycle) |
| `loader` | Đọc `*.base` và `*.bpmn2` bằng EMF → trả về EObject |
| `core` | `ConsistencyEngine` + tập luật — toàn bộ logic đối chiếu |
| `handler` | Lắng nghe event "Check KAOS-BPMN Consistency" từ context menu |
| `views` | Render Consistency View (SWT/JFace tree widget) |

#### 3.3.5 Bảy Quy Tắc Kiểm Tra Nhất Quán

Đây là phần trung tâm của khóa luận. Bảy quy tắc chia thành hai nhóm với triết lý khác nhau:

**Nhóm R1 — Structural Mapping Rules** (Luật ánh xạ cấu trúc): Kiểm tra xem các phần tử trong KAOS có tồn tại counterpart đúng trong BPMN không.

**R1.1 — Agent Mapping**:
> *Human Agent* và *Environment Agent* trong KAOS → phải ánh xạ thành **Pool độc lập** trong BPMN (mỗi agent là một participant riêng biệt).  
> *Software-to-be Agent* → phải được gom vào **Lane** thuộc cùng một Pool (vì software là internal component, không phải external party).

Lý do: BPMN dùng Pool để biểu diễn các bên độc lập (có thể là tổ chức khác nhau), còn Lane biểu diễn các vai trò nội bộ. Ánh xạ sai loại Pool/Lane phá vỡ ngữ nghĩa collaboration trong BPMN.

**R1.2 — Operation-Task-Lane**:
> Mỗi **Operation** trong KAOS phải có đúng một **Task** tương ứng trong BPMN, và Task đó phải nằm trong **Lane/Pool của Agent chịu trách nhiệm** thực thi Operation.

Lý do: Nếu Task nằm sai Lane, BPMN ngụ ý agent sai đang thực hiện công việc — vi phạm phân công trách nhiệm đã đặc tả trong KAOS.

**R1.3 — Operation-DataObject**:
> **Dữ liệu đầu vào/đầu ra** (InputLink/OutputLink) của Operation phải được truyền đầy đủ thành **Data Input/Output** cho Task tương ứng trong BPMN.

Lý do: Nếu BPMN bỏ qua một data flow, quy trình không có đủ thông tin để thực thi đúng — dù logic flow có thể trông hợp lệ.

**R1.4 — Entity-DataState**:
> **Entity** trong KAOS ánh xạ thành **Data Object** trong BPMN. **Trạng thái dữ liệu** (data state) gắn với Data Object phải nằm trong **domain values hợp lệ** đã định nghĩa trong Attribute của Entity.

Lý do: Nếu BPMN đặt data state ngoài domain, quy trình đặt hệ thống vào trạng thái không hợp lệ theo specification.

---

**Nhóm R2 — Behavioral Consistency Rules** (Luật nhất quán hành vi): Kiểm tra xem control flow trong BPMN có tôn trọng semantics của goal decomposition trong KAOS không. Đây là nhóm tinh tế và quan trọng hơn.

**R2.1 — AND-Refinement**:
> Các mục tiêu lá thuộc cùng một nhánh **phân rã AND** trong KAOS → **BẮT BUỘC** phải được hoàn tất trong BPMN. Các Task tương ứng phải nằm trên **luồng tuần tự** hoặc sau một **Parallel Gateway** — **TUYỆT ĐỐI KHÔNG** dùng Exclusive Gateway.

Lý do: AND-refinement nghĩa là *tất cả* sub-goals phải đạt được để parent goal được thỏa mãn. Nếu BPMN dùng Exclusive Gateway (chỉ chọn một nhánh), parent goal sẽ không bao giờ hoàn toàn đạt được — vi phạm semantic của AND.

**R2.2 — OR-Refinement**:
> Các mục tiêu trong phân rã **OR** mang tính thay thế và loại trừ nhau → BPMN **BẮT BUỘC** dùng **Exclusive Gateway** để đảm bảo chúng không đồng thời xảy ra trong một kịch bản.

Lý do: OR-refinement nghĩa là *chỉ một* trong các sub-goals cần đạt được. Nếu BPMN cho phép cả hai cùng thực hiện (Parallel Gateway), chi phí tài nguyên tăng không cần thiết và có thể tạo conflict.

**R2.3 — Leaf Goal**:
> Các Task cùng **hiện thực hóa một mục tiêu lá** (leaf goal) phải có khả năng **đồng tồn tại** — nằm trên luồng tuần tự hoặc Parallel Gateway, **không bị tách** bởi Exclusive Gateway.

Lý do: Một mục tiêu lá là một đơn vị nguyên tử — nếu nhiều Operations cùng hiện thực hóa nó, tất cả phải được thực hiện để mục tiêu đó đạt được. Exclusive Gateway phá vỡ tính nguyên tử này.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Lấy ví dụ từ case study thực nghiệm — **Hệ thống OJS (Open Journal Systems)** — phân hệ luồng công việc biên tập. Xét mục tiêu: *"Manuscript Quality Improved"* (Chất lượng bản thảo được cải thiện).

**Trong KAOS model**:
```
Goal: "Manuscript Quality Improved"   [AND-refinement]
  ├── Goal: "Manuscript Copyedited"
  │     └── Operation: "Copy edit"   (Agent: Copy Editor)
  └── Goal: "Manuscript Format Ready"
        └── Operation: "Create galley"   (Agent: Layout Editor)
```

**Trong BPMN chuẩn (đúng)**:
```
[Pool: Publisher]
  [Lane: Copy Editor]      → Task: "Copy edit"    ──┐
                                                      ├── [Parallel Gateway ⊕] → ...
  [Lane: Layout Editor]    → Task: "Create galley" ──┘
```

**Kiểm tra Rule R2.1 (AND-Refinement)**:
1. `loader` đọc KAOS: Goal "Manuscript Quality Improved" có AND-refinement với hai lá: "Manuscript Copyedited" và "Manuscript Format Ready"
2. `loader` đọc BPMN: Task "Copy edit" và Task "Create galley" nằm sau Parallel Gateway
3. `ConsistencyEngine` chạy R2.1: cả hai Task có thể đồng tồn tại trên cùng một trace? **YES** (Parallel Gateway) → **Satisfied** ✓

**Kiểm tra Rule R1.2 (Operation-Task-Lane)**:
1. KAOS: Operation "Copy edit" do Agent "Copy Editor" thực hiện
2. BPMN: Task "Copy edit" nằm trong Lane "Copy Editor" ✓ → **Satisfied** ✓

**Kịch bản lỗi — vi phạm R2.1**:
Thay Parallel Gateway bằng Exclusive Gateway:
```
[Lane: Copy Editor]      → Task: "Copy edit"    ──┐
                                                    ├── [Exclusive Gateway ✕] → ...
[Lane: Layout Editor]    → Task: "Create galley" ──┘
```
3. `ConsistencyEngine` chạy R2.1: hai Task KHÔNG thể đồng tồn tại (Exclusive Gateway chỉ chọn một) → **Violation** ✗
4. Báo cáo: *"Violation: Tasks 'Copy edit' and 'Create galley' cannot coexist on any trace. AND-refinement requires both to be executed."*

**Kịch bản lỗi — vi phạm R1.2**:
Di chuyển Task "Copy edit" từ Lane "Copy Editor" sang Lane "Layout Editor":
3. `ConsistencyEngine` chạy R1.2: Operation "Copy edit" → Agent "Copy Editor" → Task "Copy edit" → Lane "Layout Editor" ≠ "Copy Editor" → **Violation** ✗
4. Báo cáo: *"Violation: Operation 'Copy edit' mapped incorrectly. Expected Lane: 'Copy Editor', found: 'Layout Editor'."*

### 3.5 Điểm Mới So Với Trước

**Điểm 1 — DSL KAOS đa góc nhìn trong một môi trường**: Không có công cụ open-source nào trước đây hỗ trợ đủ bốn góc nhìn KAOS (goal, object, agent, operation) trong một Eclipse plugin với Ecore metamodel formal. Đây là nền tảng cần thiết cho automated reasoning.

**Điểm 2 — Behavioral consistency rules**: Quy tắc R2.1 và R2.2 không chỉ kiểm tra sự tồn tại của phần tử mà kiểm tra semantics của control flow — liệu BPMN có tôn trọng AND/OR semantics của goal decomposition không. Đây là mức kiểm tra tinh vi hơn đáng kể so với structural mapping đơn thuần.

**Điểm 3 — End-to-end automation**: Toàn bộ pipeline từ "chọn hai file" đến "báo cáo chi tiết từng rule" được tự động hóa hoàn toàn — không cần analyst phải hiểu internals của quy trình đối chiếu.

---

## PHẦN 4 — Tóm Tắt (Tiếng Việt — từ tóm tắt khóa luận)

Phân tích và đặc tả yêu cầu là một khâu quan trọng trong quy trình phát triển phần mềm. KAOS là một phương pháp kỹ nghệ yêu cầu hướng mục tiêu, cho phép mô hình hóa yêu cầu theo nhiều góc nhìn khác nhau. Tuy nhiên, các công cụ hỗ trợ KAOS hiện nay còn hạn chế trong việc tích hợp các góc nhìn này trong một cấu trúc thống nhất. Bên cạnh đó, cũng chưa có cơ chế kiểm tra nhất quán giữa mô hình yêu cầu hướng mục tiêu KAOS và mô hình quy trình nghiệp vụ BPMN, nhằm đảm bảo mô hình BPMN phản ánh đầy đủ và chính xác các mục tiêu đã được đặc tả.

Khóa luận tập trung nghiên cứu phát triển một ngôn ngữ chuyên biệt miền (DSL) cho KAOS và công cụ hỗ trợ mô hình hóa trên nền tảng Eclipse/EMF tích hợp Sirius, đồng thời đề xuất một phương pháp kiểm tra tính nhất quán giữa các mô hình KAOS và BPMN dựa trên tập bảy quy tắc chia thành hai nhóm: luật ánh xạ cấu trúc (R1.1–R1.4) và luật nhất quán hành vi (R2.1–R2.3). Phương pháp được kiểm chứng trên case study hệ thống OJS với kết quả phát hiện 100% vi phạm trong tám kịch bản lỗi được thiết kế.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

### Case Study: Hệ Thống OJS (Open Journal Systems)

**Đối tượng**: Phân hệ luồng công việc biên tập của OJS — quy trình từ nộp bài, phân công phản biện, biên tập bản thảo, đến xuất bản.

**RQ1 — Khả năng mô hình hóa**: Công cụ đã vẽ thành công và chính xác cả bốn loại biểu đồ KAOS cho OJS:
- Biểu đồ mục tiêu với phân rã AND/OR (ví dụ: "Gán Editor tự động/thủ công" là OR-refinement)
- Biểu đồ đối tượng với thuộc tính `status` của bản thảo và domain values hợp lệ
- Biểu đồ tác tử phân loại đúng Editor (Human), Copy Editor (Human), Layout Editor (Human), OJS System (Software-to-be)
- Biểu đồ thao tác với pre/post conditions trên state variables

**RQ2 — Kiểm tra nhất quán**:

*Thử nghiệm 1 — Mô hình chuẩn*:
- Input: KAOS model OJS + BPMN quy trình chuẩn
- Kết quả: **"THE MODELS ARE CONSISTENT | 28 Passed, 0 Violations"**
- Tất cả 7 quy tắc đều thỏa mãn ✓

*Thử nghiệm 2 — 8 kịch bản lỗi cố tình*:

| Kịch bản | Loại vi phạm | Quy tắc | Phát hiện |
|---|---|---|---|
| 1 | Agent Human ánh xạ thành Lane thay vì Pool | R1.1 | ✓ |
| 2 | Software agent ánh xạ thành Pool riêng | R1.1 | ✓ |
| 3 | Task "Copy edit" đặt sai Lane | R1.2 | ✓ |
| 4 | Data Input bị xóa khỏi Task | R1.3 | ✓ |
| 5 | Data State ngoài domain values | R1.4 | ✓ |
| 6 | AND-refinement dùng Exclusive Gateway | R2.1 | ✓ |
| 7 | OR-refinement dùng Parallel Gateway | R2.2 | ✓ |
| 8 | Leaf goal tasks bị tách bởi Exclusive Gateway | R2.3 | ✓ |

**Kết quả**: Phát hiện **100% vi phạm** (8/8 kịch bản), **0 false positive** — plugin không báo lỗi sai trên mô hình chuẩn.

---

## PHẦN 6 — Hạn Chế & Hướng Phát Triển

### Hạn Chế Tác Giả Thừa Nhận

**Một case study duy nhất**: Toàn bộ thực nghiệm dựa trên hệ thống OJS — chỉ một domain. Kết quả chưa được kiểm chứng trên các lĩnh vực đặc thù khác (y tế, tài chính, embedded systems) nơi KAOS và BPMN có thể được sử dụng với patterns khác.

**Phụ thuộc vào chất lượng KAOS input**: Tính chính xác của kết quả kiểm tra hoàn toàn phụ thuộc vào chất lượng mô hình KAOS đầu vào. Nếu KAOS model bản thân đã có lỗi hoặc không đầy đủ, plugin không thể phát hiện — nó chỉ kiểm tra BPMN theo KAOS, không validate KAOS theo thực tế.

**Tập luật thủ công — chưa đầy đủ**: 7 quy tắc được thiết kế thủ công dựa trên phân tích của tác giả. Có thể tồn tại các trường hợp inconsistency phức tạp hơn chưa được bao quát — ví dụ: goal refinement kết hợp AND và OR ở nhiều cấp, hay các BPMN patterns phức tạp như Event-based Gateway, Compensation, Loop.

**Yêu cầu người dùng hiểu KAOS**: Để sử dụng hiệu quả công cụ, người dùng vẫn cần có kiến thức về phương pháp KAOS — công cụ không hướng dẫn *cách* xây dựng KAOS model đúng, chỉ kiểm tra *sau khi* model đã có.

### Hướng Phát Triển

**Tự động sinh BPMN từ KAOS**: Bước tiến tự nhiên nhất — thay vì chỉ kiểm tra nhất quán, tự động *sinh* mô hình BPMN từ KAOS model. Đây là bài toán model-to-model transformation trong MDSE, mở ra hướng nghiên cứu hoàn toàn tự động hóa pipeline KAOS→BPMN.

**Mở rộng tập quy tắc**: Bổ sung quy tắc cho: obstacle handling, exception flows, temporal constraints trong KAOS (nếu sử dụng RT-LTL), complex BPMN patterns (Event-based Gateway, Sub-process).

**Tích hợp với i*/GRL**: Mở rộng công cụ để hỗ trợ cả GRL (URN) thay vì chỉ KAOS — tạo ra một framework kiểm tra nhất quán tổng quát hơn cho goal-process alignment.

---

## PHẦN 7 — Kết Luận

Khóa luận đề xuất và cài đặt thành công hai đóng góp bổ sung nhau: (1) DSL KAOS đa góc nhìn trên Eclipse/EMF/Sirius — lần đầu tiên tích hợp đủ bốn góc nhìn KAOS (goal, object, agent, operation) trong một môi trường đồ họa open-source; (2) Plugin kiểm tra nhất quán KAOS-BPMN với 7 quy tắc chia thành hai nhóm — structural mapping (R1.1–R1.4) và behavioral consistency (R2.1–R2.3). Thực nghiệm trên case study OJS cho kết quả 100% phát hiện vi phạm, 0 false positive.

Hạn chế chính là giới hạn ở một case study, phụ thuộc chất lượng KAOS input, và tập 7 quy tắc có thể chưa bao quát mọi tình huống phức tạp. Hướng phát triển quan trọng nhất là tự động sinh BPMN từ KAOS — chuyển từ "kiểm tra" sang "sinh tự động."

**Tóm lại, điểm đáng chú ý nhất của khóa luận này là** cặp quy tắc hành vi R2.1 và R2.2 — đây là lần đầu tiên ngữ nghĩa của AND/OR goal refinement trong KAOS được form thành các điều kiện kiểm tra cụ thể trên BPMN gateway types, tạo ra một cầu nối semantic thực sự giữa thế giới goal-oriented và process-oriented thay vì chỉ kiểm tra structural mapping đơn thuần.
