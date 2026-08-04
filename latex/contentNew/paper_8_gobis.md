# GoBIS: An Integrated Framework to Analyse the Goal and Business Process Perspectives in Information Systems

**Tác giả:** Renata Guizzardi, Monalessa Perini Barcellos, et al.  |  **Năm:** 2014–2015  |  **Venue:** Information Systems Journal (Elsevier) / CAiSE 2014

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Information Systems Engineering** — cụ thể là tích hợp hai góc nhìn (perspectives) quan trọng nhất trong phân tích và thiết kế hệ thống thông tin: **goal perspective** (tại sao hệ thống tồn tại — mục tiêu của tổ chức và các tác nhân) và **business process perspective** (như thế nào hệ thống hoạt động — các quy trình thực hiện để đạt goals).

Hai góc nhìn này bổ trợ lẫn nhau: goals cho ta biết *tại sao* cần thực hiện một quy trình; processes cho ta biết *như thế nào* goals được hiện thực hóa. Tuy nhiên, trong thực tiễn phát triển IS, chúng thường được xử lý bởi các nhóm khác nhau với các công cụ khác nhau, dẫn đến sự thiếu nhất quán.

Lĩnh vực này đặc biệt quan trọng vì: khi một IS phức tạp được thiết kế mà không có sự liên kết rõ ràng giữa goals và processes, kết quả là:
- Quy trình không đạt được mục tiêu chiến lược (misalignment)
- Không thể trace tại sao một quy trình tồn tại (no justification)
- Khi goals thay đổi, không biết processes nào cần thay đổi theo

Bài báo này xây dựng framework GoBIS (Goal and Business Process Integration System) để giải quyết vấn đề này một cách toàn diện — không chỉ là mapping lý thuyết mà là framework đầy đủ với ontological foundation, guidelines, tool support, và empirical validation.

### Bài toán cụ thể

Bài toán: **làm thế nào để tích hợp goal modeling (i*) và business process modeling (Communication Analysis — CA) vào một framework thống nhất với nền tảng bản thể học (ontological foundation) rõ ràng, hướng dẫn kịch bản áp dụng, công cụ hỗ trợ, và được validate thực nghiệm?**

- **Đầu vào**: Mục tiêu tổ chức (i* model với actors, intentions, dependencies) + bối cảnh phân tích (goal-driven, process-driven, hoặc bidirectional)
- **Đầu ra**: i* model + CA model nhất quán, với ánh xạ rõ ràng và traceability goals ↔ processes, và CA model đầy đủ hơn so với xây dựng riêng lẻ

### Tại sao khó

1. **Khác biệt ngữ nghĩa và ontological**: i* và CA được thiết kế với mục đích khác nhau, dùng khái niệm khác nhau. Ánh xạ trực tiếp mà không có ontological grounding dễ gây ra semantic inconsistencies — hai construct tưởng như "tương tự" nhưng thực ra khác nhau về bản chất.

2. **Thiếu guidelines cho từng kịch bản**: Trong thực tế, đôi khi ta có goals trước và cần sinh processes (goal-first), đôi khi ta có processes và cần extract goals (process-first), đôi khi cả hai được phát triển đồng thời (bidirectional). Không có solution nào fit-all.

3. **Thiếu tool support**: Lý thuyết tích hợp không có giá trị thực tiễn nếu không có công cụ hỗ trợ. Practitioners không thể áp dụng mental mapping phức tạp mà không có tool.

4. **Thiếu empirical evidence**: Phần lớn các đề xuất tích hợp goal-process chỉ là lý thuyết — không có bằng chứng rằng chúng thực sự cải thiện quality của models trong thực tiễn.

### Đóng góp của bài

Bài báo đề xuất GoBIS framework với bốn contribution chính:
1. **Ontological foundation**: sử dụng UFO (Unified Foundational Ontology) để đảm bảo nhất quán ngữ nghĩa trong ánh xạ i* ↔ CA
2. **iStar2ca v2.0**: bộ guidelines hoàn chỉnh cho 3 kịch bản (goal-first, process-first, bidirectional), được tinh chỉnh qua 3 vòng Design Science iteration
3. **Empirical validation**: controlled experiment với sinh viên IS cho thấy completeness CA model tăng 21% khi dùng guidelines
4. **Focus group feedback**: practitioners xác nhận usefulness trong thực tế

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: i\* Framework (Yu, 1995)

i* là one of the most influential goal modeling languages — với actors, intentions (goals, tasks, softgoals, resources), và social dependencies. Mạnh cho strategic requirements. Nhược điểm: không có companion process modeling language và không có mechanism chuyển đổi sang process model một cách có hệ thống.

### Hướng 2: KAOS + BPMN Mapping

Rolland et al. và các nghiên cứu liên quan đề xuất ánh xạ từ KAOS goal model sang BPMN process model. Hướng đúng nhưng thiếu ontological foundation — ánh xạ dựa trên structural similarity không được justify bởi semantic equivalences. Kết quả có thể không nhất quán về mặt conceptual.

### Hướng 3: GRL + UCM (URN Framework)

Đã được thảo luận trong paper_1 và paper_6. URN tích hợp GRL và UCM trong cùng chuẩn ITU-T. GoBIS khác ở chỗ dùng CA (Communication Analysis) thay UCM — CA có đặc tính communication-centric khác với UCM's causal path semantics, và phù hợp hơn với IS context vì IS thường là về communication giữa actors.

### Hướng 4: Tropos + BPMN (Giorgini et al.)

Một số nghiên cứu trong cộng đồng Tropos đề xuất ánh xạ từ Tropos model sang BPMN. Thiếu hai thứ: (a) hướng dẫn kịch bản thực tế — biết khi nào dùng goal-first vs. process-first, và (b) công cụ hỗ trợ. Chỉ là lý thuyết.

### Hướng 5: Alignment-only Approaches

Guizzardi (paper_4), Koliadis & Ghose (paper_2), và các bài liên quan tập trung vào alignment sau khi đã xây dựng riêng lẻ. GoBIS khác ở chỗ tích hợp ngay từ đầu quy trình — không phải retrofit alignment vào models đã có.

### Khoảng trống (Research Gap)

Cần framework đầy đủ với: (1) nền tảng ontological, (2) guidelines theo kịch bản, (3) tool support, và (4) empirical evidence. Không có solution nào trước GoBIS đáp ứng cả bốn yêu cầu này.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

GoBIS dựa trên một insight: **i* và CA là compatible vì cả hai đều có focus vào social actors và commitments.** i* models goals và intentions của actors; CA models communication acts và business transactions giữa actors. Sự tương đồng về "actor-centric" design cho phép ánh xạ tự nhiên hơn so với ánh xạ i* sang BPMN (vốn ít actor-centric hơn).

**UFO (Unified Foundational Ontology)** của Guizzardi cung cấp neutral ontological framework để đặt cả hai languages vào cùng conceptual space, tránh ánh xạ "bề mặt" (structural matching) mà không có semantic justification.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

GoBIS được phát triển theo **Design Science Research Methodology** — bốn vòng iteration:

**Vòng 1 — Điều tra**: Khảo sát literature, xác định khoảng trống trong goal-process integration.

**Vòng 2 — GoBIS v1 + iStar2ca v0.5**: Thiết kế framework đầu tiên với UFO-based ontological mapping. Test trong laboratory với small groups.

**Vòng 3 — GoBIS v2 + iStar2ca v1.0**: Refinement sau laboratory feedback. Tiến hành comparative experiment với sinh viên.

**Vòng 4 — GoBIS v3 + iStar2ca v2.0**: Refinement sau experiment feedback + focus group với practitioners. Đây là phiên bản được báo cáo trong bài.

### 3.3 Các Thành Phần Chính

**i* (iStar 1.0):**
Goal modeling language với: Actor (agent/role/position), Intentional Element (Goal — functional, Softgoal — quality, Task — operational, Resource — physical/informational), Relationships (Dependency giữa actors, Contribution từ task sang softgoal, Decomposition).

**Communication Analysis (CA):**
Process modeling language developed theo Language-Action Perspective (Winograd & Flores). Tập trung vào communication acts — khi actor A makes a commitment to actor B về một work process. CA phản ánh tự nhiên IS context: phần lớn IS là về mediating communications và transactions giữa organizational actors.

**UFO (Unified Foundational Ontology):**
Framework bản thể học do Guizzardi et al. phát triển — phân loại entities, events, và relationships theo các formal ontological categories. Được dùng để analyze semantic correspondences giữa i* constructs và CA constructs: ví dụ, i* "Dependency" và CA "Business Transaction" đều represent "commitments between agents" trong UFO — justified semantic equivalence.

**iStar2ca v2.0 — Bộ guidelines với 3 kịch bản:**

*Kịch bản 1 — Goal-first (top-down)*: Bắt đầu từ i* model đã có → sinh CA process model. Guidelines: i* Actor → CA Actor; i* Goal → CA Business Process (tên); i* Task → CA Work Process; i* Dependency → CA Business Transaction; i* Softgoal → supplementary CA constructs.

*Kịch bản 2 — Process-first (bottom-up)*: Bắt đầu từ CA process model → extract i* goals. Guidelines: CA Actor → i* Actor; CA Business Process → i* Goal; CA Work Process → i* Task; CA Business Transaction → i* Dependency; thiếu CA constructs → candidate Softgoals.

*Kịch bản 3 — Bidirectional*: Cả hai models được phát triển đồng thời với cross-checking định kỳ bằng iStar2ca correspondence table.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

**Case study: Procurement System (Hệ thống mua hàng)**

**i* model**:
- Actor: Purchasing Department (Buyer), Supplier
- Goal của Buyer: "Acquire Materials on Time" → phân rã thành tasks: "Create Purchase Order", "Approve Purchase Order", "Receive Materials"
- Dependency: Buyer depends on Supplier for "Material Delivery"
- Softgoal của Buyer: "Minimize Cost"

**Áp dụng iStar2ca v2.0 (goal-first kịch bản)**:

*Bước 1 — Map actors*: Purchasing Department → CA Actor "Buyer"; Supplier → CA Actor "Supplier"

*Bước 2 — Map Goal → Business Process*: "Acquire Materials on Time" → CA Business Process "Material Acquisition Process"

*Bước 3 — Map Tasks → Work Processes*:
- "Create Purchase Order" → CA Work Process "PO Creation"
- "Approve Purchase Order" → CA Work Process "PO Approval"
- "Receive Materials" → CA Work Process "Materials Receipt"

*Bước 4 — Map Dependency → Business Transaction*: "Material Delivery" dependency → CA Business Transaction "Delivery Agreement" giữa Buyer và Supplier (với commitment, fulfillment, acceptance phases)

*Bước 5 — Map Softgoal → Supplementary constructs*: "Minimize Cost" → CA supplementary constraint "Cost Monitoring" được thêm vào process như monitoring trigger

**Kết quả**: CA model đầy đủ với Business Transaction "Delivery Agreement" có đủ phases (promise, perform, report, accept/decline) — một yếu tố mà designers thường bỏ sót khi xây dựng CA riêng lẻ. i* Dependency đã "nhắc nhở" về sự cần thiết của Business Transaction này.

Trong comparative experiment, nhóm không có iStar2ca thường bỏ sót Business Transactions và Commitment phases — đây là lý do completeness thấp hơn.

### 3.5 Điểm Mới So Với Trước

Hai điểm khác biệt căn bản:

1. **Ontological grounding thực sự**: UFO không chỉ là "nice to have" — nó giải quyết vấn đề semantic inconsistency của tất cả approaches trước. Mỗi mapping rule trong iStar2ca được justify bởi UFO correspondences, không phải structural similarity.

2. **Empirical validation là lần đầu tiên trong domain**: Trước GoBIS, không có controlled experiment nào chứng minh rằng goal-process integration guidelines thực sự cải thiện model quality. GoBIS là bài đầu tiên cung cấp empirical evidence.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Phát triển hệ thống thông tin đòi hỏi hiểu biết đồng thời về goals của tổ chức (tại sao) và business processes (như thế nào). Tuy nhiên, hai perspectives này thường được phát triển riêng lẻ, dẫn đến thiếu nhất quán. Bài báo này đề xuất GoBIS — framework tích hợp goal perspective (i*) và business process perspective (Communication Analysis) dựa trên nền tảng bản thể học UFO (Unified Foundational Ontology). Framework cung cấp bộ hướng dẫn iStar2ca v2.0 cho ba kịch bản thực tiễn: goal-first, process-first, và bidirectional. iStar2ca được phát triển theo phương pháp Design Science qua bốn vòng iteration với laboratory studies, controlled experiments, và focus groups. Comparative experiment cho thấy completeness của CA model tăng từ 68% lên 89% khi sử dụng iStar2ca, trong khi thời gian hoàn thành không tăng đáng kể. Focus group với IS practitioners xác nhận tính hữu ích trong thực tiễn. GoBIS là framework đầu tiên trong domain goal-process integration cung cấp đồng thời ontological foundation, scenario-based guidelines, và empirical evidence về hiệu quả.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
- **Procurement System**: hệ thống mua hàng điển hình — dùng xuyên suốt bài làm running example
- **Comparative experiment**: sinh viên master/PhD IS tại đại học Brazil

**Comparative Experiment:**
- Nhóm A (treatment): n = 12–15, dùng iStar2ca v1.0
- Nhóm B (control): n tương đương, xây dựng riêng lẻ không có guidelines
- Cùng case study, cùng thời gian cho phép

| Tiêu chí | Nhóm B (không có iStar2ca) | Nhóm A (có iStar2ca) | Ý nghĩa thống kê |
|---|---|---|---|
| Completeness của CA model | 68% | **89%** | p < 0.05 |
| Correctness của CA constructs | 71% | **85%** | Có ý nghĩa |
| Thời gian hoàn thành | Baseline | Tương đương | Không tăng overhead |
| Traceability goals ↔ processes | Thiếu | Rõ ràng, đầy đủ | Improvement hoàn toàn |
| Usability rating | N/A | **4.1/5.0** | Rất tích cực |

**Focus Group:**
5–7 IS practitioners từ công ty phần mềm Brazil. Practitioners đánh giá iStar2ca hữu ích, đặc biệt cho goal-first scenario. Feedback dẫn đến refinements trong v1.0 → v2.0.

**Observations quan trọng:**
- Nhóm không có guidelines thường bỏ sót Business Transactions và Commitment phases trong CA — yếu tố được "nhắc" bởi i* Dependency trong iStar2ca
- Nhóm có guidelines ít bỏ sót softgoals — vì iStar2ca có step explicit cho softgoal → CA supplementary construct mapping

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Thực nghiệm với sinh viên**: Không hoàn toàn đại diện cho practitioners thực tế có kinh nghiệm nhiều năm. Sinh viên có thể benefit nhiều hơn từ structured guidelines so với practitioners đã có mental models riêng.

2. **Cỡ mẫu nhỏ**: n = 12–15 mỗi nhóm — statistical power hạn chế. Cần replication với larger samples.

3. **Domain hẹp**: Case study chủ yếu là IS domain tại Brazil. Cần validate trong contexts khác (manufacturing, healthcare, government).

4. **CA ít phổ biến hơn BPMN**: Communication Analysis không phải mainstream — hạn chế adoption của GoBIS trong practitioners thường làm việc với BPMN.

5. **iStar2ca chỉ hỗ trợ iStar 1.0**: iStar 2.0 (phiên bản mới hơn với simplified syntax) chưa được hỗ trợ, hạn chế forward compatibility.

**Hướng nghiên cứu tiếp theo:**
- Mở rộng GoBIS cho iStar 2.0 và BPMN (thay CA) — tăng adoption thực tế đáng kể
- Tool support tự động hóa: plugin sinh CA model từ i* model theo iStar2ca rules
- Replication study với practitioners thực tế ở diverse domains
- Bidirectional evolution study: khi process thay đổi, impact propagation sang goals như thế nào
- Tích hợp GoBIS vào Agile IS development — dùng goal-process alignment trong sprint planning

---

## PHẦN 7 — Kết Luận

GoBIS là framework tích hợp goal-process đầu tiên đáp ứng đầy đủ bốn tiêu chí: ontological foundation (UFO), scenario-based guidelines (iStar2ca v2.0 với 3 kịch bản), tool support, và empirical evidence (completeness +21%, correctness +14% so với approach không có guidelines). Framework được phát triển qua 4 vòng Design Science iteration với feedback từ laboratory studies, controlled experiments, và focus groups. Hạn chế chính là CA ít phổ biến hơn BPMN và thực nghiệm với sinh viên — mở rộng sang BPMN và replication với practitioners là hướng tự nhiên tiếp theo.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc kết hợp rigorousness lý thuyết (UFO ontological foundation) với pragmatism thực tiễn (3 kịch bản linh hoạt, tool support) và empirical validation — một combination hiếm thấy trong GORE research. Đây là mô hình đáng học hỏi cho các bài báo về goal-process integration: không chỉ đề xuất lý thuyết mà còn validate bằng controlled experiments với metrics cụ thể.
