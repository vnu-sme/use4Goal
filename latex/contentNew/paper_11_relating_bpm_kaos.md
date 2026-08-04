# Relating Business Process Models to Goal-Oriented Requirements Models in KAOS

**Tác giả:** George Koliadis, Aditya K. Ghose  |  **Năm:** 2006  |  **Venue:** APCCM 2006 (Asia-Pacific Conference on Conceptual Modelling)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực giao thoa giữa **Business Process Management (BPM)** và **Goal-oriented Requirements Engineering (GORE)** — cụ thể là phương pháp thiết lập liên kết có thể kiểm chứng giữa BPMN process models và KAOS goal models. Lưu ý: đây là bài của cùng tác giả Koliadis với Stra2Bis (paper_2) và Validation paper (paper_3) — và được viết cùng năm (2006). Ba bài này tạo thành một cụm nghiên cứu về goal-process alignment từ University of Wollongong.

Trong quản lý tổ chức, một thực tế phổ biến là: **goals và processes thường xuyên thay đổi, nhưng thay đổi một cách riêng lẻ.** Khi ban quản lý thêm một goal mới (ví dụ: yêu cầu bảo mật từ regulatory body), họ không có cách systematic để xác định quy trình nào cần được cập nhật và cập nhật như thế nào. Tương tự, khi một quy trình được thiết kế lại, không có cách để verify rằng quy trình mới vẫn thỏa mãn tất cả goals hiện có.

Đây là vấn đề **co-evolution** — goals và processes cần "tiến hóa cùng nhau" một cách có kiểm soát, không phải drift xa nhau theo thời gian.

### Bài toán cụ thể

Bài toán được đặt ra: **làm thế nào để (1) thiết lập liên kết truy xuất nguồn gốc (traceability links) giữa BPMN processes và KAOS goals, và (2) kiểm chứng hình thức liệu process có thực sự thỏa mãn goals — đặc biệt khi goals thay đổi?**

Đây là bài toán "bidirectional alignment with evolution support" — khác với các bài trước chỉ giải quyết một chiều (goal → process hay process → goal).

- **Đầu vào**: BPMN process model (activities, gateways, sequence flows) + KAOS goal model (goals với RT-LTL specifications: Achieve, Maintain, Avoid, Cease)
- **Đầu ra**: Traceability links (goal ↔ activity), satisfaction links (goal ↔ process path), chỉ dẫn tiến hóa process khi goal không được thỏa mãn

### Tại sao khó

1. **BPMN dùng "optative mood"**: Activities trong BPMN được đặt tên theo hành động ("Register Customer", "Process Payment") — không khai báo state nào được đạt được sau khi thực hiện. KAOS goals, ngược lại, dùng "indicative mood" — khai báo rõ trạng thái phải đạt được (postcondition). Không thể so sánh trực tiếp hai ngôn ngữ này.

2. **Temporal semantics của KAOS**: KAOS sử dụng RT-LTL (Real-Time Linear Temporal Logic) để specify goals: Achieve (◇T — đạt T vào lúc nào đó), Maintain (□T — luôn duy trì T), Avoid (□¬T — không bao giờ T), Cease (◇¬T — T chấm dứt vào lúc nào đó). BPMN không có temporal logic built-in.

3. **Multiple execution paths**: Process với gateways có nhiều paths — một goal có thể được thỏa mãn trên happy path nhưng không trên error path, hay ngược lại.

4. **Co-evolution complexity**: Khi goal mới được thêm, không trivial để xác định chính xác BPMN activity nào cần thêm/sửa/xóa để thỏa mãn goal mới mà không vi phạm goals cũ.

### Đóng góp của bài

Tác giả đề xuất **GoalBPM framework** — phương pháp 2 giai đoạn:
1. **Traceability links**: liên kết informal nhưng có hướng dẫn giữa goals và activities
2. **Satisfaction links**: liên kết hình thức qua effect annotations và trajectory analysis

Cơ chế mới: **Effect Annotations** — gán "indicative mood" labels cho BPMN activities để có thể so sánh với KAOS goal postconditions.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: BPM Evolution Approaches

Nghiên cứu về process change management (như ADEPTFLEX, Provop) hỗ trợ thay đổi process theo nhiều cách khác nhau. Tuy nhiên, chúng thiếu tiêu chí đánh giá: không có goals để verify rằng thay đổi là "đúng hướng". Process có thể thay đổi mà không biết đang improve hay worsen goal satisfaction.

### Hướng 2: Goal-to-Process Transformation

Rolland et al., Penserini et al., và Soffer & Wand đề xuất sinh process từ goals (đã thảo luận trong paper_3 và paper_4). Không áp dụng cho brownfield scenarios — không hỗ trợ co-evolution của existing models.

### Hướng 3: Process Annotation với Informal Goals

Một số approaches gắn goal labels vào BPMN activities như annotations. Ví dụ: thêm comment "this activity supports goal G3" vào activity A. Limitations: informal, không verifiable, không có mechanism để prove satisfaction, không có temporal semantics.

### Hướng 4: KAOS Methodology

KAOS (van Lamsweerde et al.) là comprehensive methodology với RT-LTL goal specification, obstacle analysis, và responsibility assignment. Mạnh cho requirements side nhưng không có connection với BPM side. Không có guidelines để link KAOS goals với BPMN activities.

### Khoảng trống (Research Gap)

Cần phương pháp kết nối BPMN và KAOS để: (1) thiết lập traceability có thể trace ngược, (2) kiểm chứng satisfaction qua effect annotations + RT-LTL, (3) hướng dẫn co-evolution khi goals thay đổi. Không có solution nào trước GoalBPM đáp ứng cả ba.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Ý tưởng căn bản: **thêm "indicative mood" annotations vào BPMN activities để bridge semantic gap với KAOS goals.**

BPMN activity "Register Customer" → Effect Annotation "CustomerRegistered" (indicative mood).
KAOS goal "Achieve(CustomerRegistered)" → postcondition "CustomerRegistered".

Bây giờ có thể so sánh: trajectory của BPMN accumulates effects, và nếu cumulative effects thỏa mãn RT-LTL temporal ordering của goal, thì trajectory satisfies goal.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**GoalBPM framework gồm 2 giai đoạn:**

**Giai đoạn 1 — Thiết lập Traceability Links (thủ công):**
Nhà phân tích đối chiếu KAOS goal nodes với BPMN activities dựa trên preconditions và postconditions của cả hai. Đây là informal mapping — không có formal algorithm. Output: traceability table (goal ↔ activity).

**Giai đoạn 2 — Thiết lập Satisfaction Links (3 bước):**

*Bước 2a — Gán Effect Annotations:* Mỗi BPMN activity nhận một label ở "indicative mood" mô tả trạng thái được đạt sau khi thực hiện. Có thể optional formalize bằng first-order logic (ví dụ: ∀c: Customer, ∃cr: CustomerRecord. Stored(c.Details, cr)).

*Bước 2b — Trích xuất Critical Trajectories:* Trajectory = một execution path duy nhất qua BPMN từ start đến end event. Phân loại:
- Normal trajectory: hoàn thành thành công (đạt end event chính)
- Exceptional trajectory: kết thúc sớm do lỗi không khắc phục được

*Bước 2c — Trajectory Analysis:* Với mỗi trajectory, tích lũy effects của tất cả activities trên path. Đối chiếu cumulative effects với RT-LTL temporal ordering của goal:
- Achieve (C ⇒ ◇T): T phải xuất hiện trong cumulative effects tại ít nhất một điểm
- Maintain (C ⇒ □T): T phải xuất hiện và không bị negated sau đó
- Avoid (C ⇒ □¬T): Negation of T phải maintain suốt
- Cease (C ⇒ ◇¬T): Sau khi T đạt, phải có điểm negation

### 3.3 Các Thành Phần Chính

**KAOS Goal Model:**
Goals được specify bằng RT-LTL với antecedent (điều kiện kích hoạt) và consequent (trạng thái cần đạt). Bốn loại temporal pattern: Achieve, Maintain, Avoid, Cease. KAOS cũng có goal hierarchy (refinement/operationalization) và responsibility links.

**BPMN Process Model:**
Standard BPMN notation với activities, sequence flows, gateways (AND, XOR, OR), start/end events, swim lanes. Bài báo tập trung vào structural analysis — không đề cập đến BPMN execution semantics (BPEL).

**Effect Annotations:**
Cơ chế bridge quan trọng nhất. Mỗi annotation là một propositional fact ở indicative mood, mô tả postcondition của activity. Optional: có thể formalize bằng FOL. Effect annotations không phải là extension của BPMN chuẩn — chúng là metadata thêm vào cho GoalBPM analysis.

**Trajectory Extractor:**
Trong bài, trajectory extraction là manual — nhà phân tích trace qua BPMN diagram bằng tay. Đây là hạn chế lớn nhất được tác giả thừa nhận: không scalable với complex processes.

**RT-LTL Checker:**
Kiểm tra cumulative effects của trajectory có thỏa mãn temporal pattern của goal hay không. Trong bài, checking là manual — không có automated tool. Tương tự: hạn chế scalability.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

**Package Sorting Process** — quy trình phân loại gói hàng tại một công ty vận tải:

**BPMN (ban đầu)**:
- Swim lane Sort Operations: [1] Sort Package → [2] Register Package → [3] Assign Barcode
- Swim lane Bond Operations: [4] Check Package → gateway → [5] Clear Package (nếu OK) / [6] Quarantine (nếu nghi ngờ)

**Effect Annotations** được thêm vào:
- Activity 1 "Sort Package" → "PackageSorted"
- Activity 2 "Register Package" → "PackageRegistered"
- Activity 3 "Assign Barcode" → "BarcodeAssigned"
- Activity 4 "Check Package" → "PackageChecked"
- Activity 5 "Clear Package" → "PackageCleared"
- Activity 6 "Quarantine" → "PackageQuarantined"

**KAOS Goals (ban đầu)**:
- G1: Achieve(PackageSorted) — gói phải được phân loại
- G2: Achieve(PackageRegistered) — gói phải được đăng ký
- G3: Achieve(BarcodeAssigned) — gói phải có barcode

**Trajectory Analysis**:
Normal trajectory A: 1→2→3→4→5. Cumulative effects: {PackageSorted, PackageRegistered, BarcodeAssigned, PackageChecked, PackageCleared}. → Thỏa mãn G1, G2, G3 ✓

Normal trajectory B: 1→2→3→4→6. Cumulative effects: {PackageSorted, PackageRegistered, BarcodeAssigned, PackageChecked, PackageQuarantined}. → Thỏa mãn G1, G2, G3 ✓

**Goal evolution scenario**: Regulatory body thêm goal mới G4: Achieve(ScreenedByAuthorities) — gói phải được kiểm tra bởi cơ quan thẩm quyền.

Trajectory Analysis với G4: Không trajectory nào có "ScreenedByAuthorities" trong cumulative effects → G4 KHÔNG thỏa mãn.

**Process evolution**: Thêm activity [6'] "Provide to Authority" → new gateway → [7] "ScreenedByAuthorities" (nếu selected) / continue. Cập nhật BPMN và re-run trajectory analysis → Tất cả trajectories (với path qua 6') đều thỏa mãn G4 ✓.

### 3.5 Điểm Mới So Với Trước

Hai điểm khác biệt:

1. **Effect Annotations bridge optative-indicative gap**: Lần đầu tiên có cơ chế đơn giản nhưng hiệu quả để connect BPMN activities (optative) với KAOS goals (indicative) — không cần formal transformation hay semantic embedding.

2. **Co-evolution support**: GoalBPM không chỉ check alignment tại một thời điểm mà còn hướng dẫn cụ thể cách update process khi goal mới được thêm — đây là điểm mà tất cả approaches trước thiếu.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Business process management đòi hỏi sự liên kết rõ ràng giữa quy trình nghiệp vụ và mục tiêu tổ chức, đặc biệt khi cả hai đồng thời thay đổi. Bài báo này đề xuất GoalBPM — phương pháp 2 giai đoạn để kết nối BPMN process models với KAOS goal models. Giai đoạn 1 thiết lập traceability links giữa KAOS goals và BPMN activities dựa trên preconditions/postconditions. Giai đoạn 2 thiết lập satisfaction links qua ba bước: gán effect annotations (indicative mood labels) vào BPMN activities, trích xuất critical trajectories (execution paths), và phân tích cumulative effects của từng trajectory so với RT-LTL temporal pattern của goals (Achieve, Maintain, Avoid, Cease). Framework hỗ trợ co-evolution: khi goal mới được thêm, GoalBPM phát hiện unsatisfied trajectory và hướng dẫn cách cập nhật process. Proof-of-concept trên Package Sorting case study chứng minh tính khả thi của phương pháp, mặc dù còn hoàn toàn thủ công và cần được formalized và automated trong công việc tiếp theo.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
**Package Sorting Process** — quy trình phân loại gói hàng của một tổ chức vận tải. Quy mô nhỏ: ~5 activities ban đầu, 2 swim lanes, ~14 KAOS goals ở các cấp độ khác nhau. Đây là illustrative case study, không phải industrial benchmark.

**Kết quả định tính:**

| Kịch bản | Kết quả GoalBPM |
|---|---|
| As-is analysis | ✓ Xác nhận 2 normal + 2 exceptional trajectories đều thỏa mãn goals hiện tại |
| Thêm G4 "ScreenedByAuthorities" | ✓ Phát hiện process KHÔNG thỏa mãn goal mới |
| Process evolution (thêm activity 6' + gateway) | ✓ Process mới có trajectories đều thỏa mãn tất cả goals kể cả G4 |

**Co-evolution demonstration:**
GoalBPM chứng minh có thể: (a) detect unsatisfied goal sau khi goal mới được thêm, (b) suggest loại activity cần thêm (activity produce effect "ScreenedByAuthorities"), (c) verify sau khi update. Đây là workflow co-evolution đầu tiên được demonstrate cho BPMN-KAOS pair.

**Methodology:** Hoàn toàn thủ công (manual) — không có tool automation.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Hoàn toàn thủ công và phi hình thức**: Effect annotations, trajectory extraction, và RT-LTL checking đều được thực hiện bằng tay. Không scalable cho processes lớn với nhiều activities và goals.

2. **Trajectory extraction khó**: Process với loops, nhiều gateways, hay phức tạp → xác định tất cả trajectories thủ công là không thực tiễn và error-prone.

3. **Case study quá nhỏ**: 5 activities, 14 goals — chưa được validate trên non-trivial industrial-scale cases.

4. **Thiếu formal foundation**: Effect annotation semantics chưa được formalized — hai analysts có thể gán annotations khác nhau cho cùng activity.

**Hướng nghiên cứu tiếp theo:**
- Formalize nền tảng toán học của GoalBPM (formal semantics cho effect annotations)
- Phát triển tool tự động hóa trajectory extraction từ BPMN
- Automated RT-LTL checking bằng model checking tools
- Validate trên industrial-scale processes
- Kết hợp với paper_3 (Validation of User Intentions) — dùng DL-based verification thay vì RT-LTL manual checking

---

## PHẦN 7 — Kết Luận

GoalBPM đề xuất phương pháp 2 giai đoạn kết nối BPMN và KAOS thông qua effect annotations và trajectory analysis, với hỗ trợ co-evolution khi goals thay đổi. Proof-of-concept trên Package Sorting chứng minh tính khả thi: phát hiện unsatisfied goal sau khi goal mới được thêm và hướng dẫn process update. Giới hạn lớn nhất là thiếu formal foundation và tool automation — đây là hướng mở rộng cần thiết. Bài này cùng với paper_2 (Stra2Bis) và paper_3 tạo thành một trilogy về goal-process alignment từ cùng nhóm tác giả — mỗi bài giải quyết một khía cạnh: transformation (paper_2), validation (paper_3), và co-evolution (bài này).

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** ý tưởng Effect Annotations — đơn giản đến mức dường như "obvious" nhưng lại là missing piece quan trọng: bằng cách thêm indicative mood labels vào BPMN activities, ta tạo ra một bridge semantic giữa hai ngôn ngữ vốn dùng "mood" khác nhau. Ý tưởng này, dù simple, mở ra khả năng formal verification mà không cần restructure hoặc rewrite bất kỳ model nào.
