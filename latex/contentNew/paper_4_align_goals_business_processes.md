# A Method to Align Goals and Business Processes

**Tác giả:** Renata S.S. Guizzardi, Ariane Nunes Reis (Universidade Federal do Espírito Santo, Brazil)  |  **Năm:** 2012  |  **Venue:** International Conference on Advanced Information Systems Engineering (CAiSE 2012)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực giao thoa giữa **Business Process Management (BPM)** và **Goal-oriented Requirements Engineering**. Đây là một trong những vấn đề thực tiễn cấp bách nhất trong quản lý tổ chức hiện đại: làm thế nào để đảm bảo rằng các quy trình nghiệp vụ đang được thực thi thực sự đóng góp vào mục tiêu chiến lược của tổ chức?

BPM đã được áp dụng rộng rãi trong doanh nghiệp, nhưng có một nghịch lý phổ biến: các quy trình được thiết kế và triển khai tốn kém lại **không explicitly liên kết** với mục tiêu chiến lược. Kết quả là:
- Quy trình có thừa activities không đóng góp vào mục tiêu nào (lãng phí nguồn lực)
- Và ngược lại, có mục tiêu chiến lược quan trọng nhưng không có hoạt động nào trong process hỗ trợ (goals bị bỏ sót)

Khi ban lãnh đạo hỏi "quy trình này phục vụ mục tiêu chiến lược nào?", câu trả lời thường là sự im lặng hoặc những liên kết mơ hồ. Đây không chỉ là vấn đề tài liệu mà là vấn đề quản trị thực sự.

Goal-oriented Requirements Engineering — với các ngôn ngữ như Tropos, i*, GRL — cung cấp công cụ mạnh để mô hình hóa mục tiêu và reasoning về mức độ thỏa mãn chúng. Tuy nhiên, phần lớn nghiên cứu trong lĩnh vực này tập trung vào việc *sinh ra* process model từ goal model, không phải *alignment* với process model đã tồn tại. Trong thực tiễn doanh nghiệp, "brownfield scenarios" — nơi cả goal model lẫn process model đều đã tồn tại và không thể bắt đầu lại từ đầu — là phổ biến hơn nhiều.

### Bài toán cụ thể

Bài toán được định nghĩa như sau: **Với một goal model (Tropos) và một process model (BPMN) đã tồn tại, làm thế nào để xác định một cách có hệ thống mối quan hệ giữa chúng?**

Cụ thể, cần trả lời bốn câu hỏi:
1. Activities nào trong process đóng góp vào goals nào, và đóng góp bao nhiêu?
2. Goals nào không có activity nào hỗ trợ (goals bị bỏ sót)?
3. Activities nào không đóng góp vào bất kỳ goal nào (activities thừa)?
4. Mức độ thỏa mãn từng goal là bao nhiêu theo từng execution path của process?

- **Đầu vào**: Goal model (Tropos với actors, goals, softgoals, plans, contribution links, AND/OR decomposition) + Process model (BPMN với activities, sub-processes, gateways, sequence flows, swim lanes)
- **Đầu ra**: Aligned model có (1) ánh xạ activities → Tropos plans, (2) danh sách unaligned goals và unaligned activities, (3) quantitative satisfaction scores cho từng goal theo từng execution path, (4) overall goal satisfaction assessment

### Tại sao khó

Thách thức xuất phát từ nhiều nguồn:

1. **Khác biệt ngôn ngữ và semantics**: Goal model (Tropos) và process model (BPMN) được thiết kế với mục đích khác nhau và dùng khái niệm khác nhau. Không có cầu nối tự nhiên giữa "goal decomposition" của Tropos và "gateway patterns" của BPMN.

2. **Multiple execution paths**: Process model với gateways tạo ra nhiều execution paths khác nhau. Một goal có thể được hỗ trợ trên main path nhưng bị bỏ sót trên alternative path — và ngược lại. Điều này làm cho alignment không phải là một quan hệ đơn giản mà là quan hệ **path-dependent**.

3. **Quantitative contribution là chủ quan**: Khi nói "activity A đóng góp vào goal G", cần biết *bao nhiêu*. Không có phương pháp chuẩn nào để xác định trọng số contribution — nó phụ thuộc vào domain knowledge và stakeholder judgment.

4. **BPM platforms không có goal support**: Các process engines phổ biến như BPEL, YAWL, jBPM không có khái niệm "goal" — alignment chỉ là documentation thuần túy, không có executable semantics.

### Đóng góp của bài

Tác giả đề xuất một **phương pháp 5 bước systematic** để align existing goal model với existing process model, kết hợp:
- Structural mapping (activities → plans)
- Gap analysis tự động (phát hiện unaligned goals và activities)
- Quantitative contribution weighting
- Forward/backward propagation để tính satisfaction scores
- Path-aware analysis (contribution khác nhau theo execution path)

Validation qua controlled experiment với 14 sinh viên thạc sĩ cho thấy phương pháp cho alignment quality tốt hơn ad-hoc approach.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: Goal-to-Process Transformation

Penserini et al. và Soffer & Wand đề xuất sinh process model tự động từ goal model. Ưu điểm: alignment được đảm bảo by construction. Nhược điểm căn bản: không áp dụng được cho **brownfield scenarios** — khi process đã tồn tại với legacy system constraints, regulatory requirements, và organizational history không thể bỏ qua. Trong thực tiễn doanh nghiệp, "hãy thiết kế lại quy trình từ đầu" là không khả thi về chính trị lẫn kinh tế.

### Hướng 2: Strategy-to-Process (Stra2Bis)

Koliadis & Ghose (2006) với Stra2Bis tập trung vào strategy-to-process transformation trong khung MDA. Đây là hướng bổ sung cho bài này nhưng có hai khoảng trống: (1) không cung cấp cơ chế quantitative assessment mức độ goal satisfaction, (2) không xử lý existing process models — Stra2Bis tạo process từ strategy, không align với process đã có.

### Hướng 3: Annotation-based Linking

Một số nghiên cứu đề xuất thêm annotations để kết nối process elements với goals. Đây là hướng thực dụng nhưng thiếu tính systematic: không có quy trình hướng dẫn để thực hiện, không có propagation để tính aggregate satisfaction, và không có mechanism tự động phát hiện unaligned goals/activities.

### Hướng 4: BPM-only Approaches

BPEL, YAWL, jBPM và các process engines lớn — tất cả đều thiếu goal layer. Alignment chỉ là documentation, không có executable semantics. Không có cách nào để hỏi "execution path này có đạt goal X không?" trong context của process engine.

### Hướng 5: Tropos Quantitative Reasoning

Framework Tropos có cơ chế reasoning về goal satisfaction thông qua contribution links và propagation algorithms. Tuy nhiên, Tropos chủ yếu được dùng cho requirements elicitation và system design — không được kết nối với BPM process models trong các công trình trước đây.

### Khoảng trống (Research Gap)

Không có phương pháp nào kết hợp đủ bốn yếu tố: (1) áp dụng được cho existing models, (2) systematic gap analysis, (3) quantitative contribution weighting, (4) path-aware propagation. Bài báo này lấp đầy khoảng trống này với phương pháp 5 bước.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Insight căn bản của bài báo là: **BPMN activities về bản chất tương ứng với "plans" trong Tropos — cả hai đều là concrete actions được thực hiện để đạt goal.** Nếu ta thiết lập ánh xạ activities → plans, ta có thể tận dụng toàn bộ bộ máy reasoning của Tropos (contribution links, propagation algorithms) để tính mức độ thỏa mãn từng goal.

Thêm vào đó, vì BPMN có nhiều execution paths (do gateways), contribution của một activity không phải là hằng số — nó phụ thuộc vào context path. Cùng một "Process Payment" activity có thể đóng góp nhiều hơn cho goal "Minimize Transaction Time" trên fast-path so với slow-path. Vì vậy, alignment phải là **path-aware**.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

Phương pháp gồm 5 bước tuần tự:

**Bước 1 — Path Classification trong BPMN**: Phân tích BPMN diagram và phân loại execution paths thành: main path (happy path thông thường), secondary path (alternate normal flows), alternative path (exception handling). Việc phân loại này là nền tảng vì contribution của activities sẽ được đánh giá per-path.

**Bước 2 — Ánh xạ BPMN activities → Tropos plans**: Với mỗi activity/sub-process trong BPMN, xác định plan tương ứng trong Tropos goal model. Gán contribution type cho từng ánh xạ: positive (+), negative (−), break (phá vỡ hoàn toàn), hurt (gây hại nhẹ), help (hỗ trợ nhẹ), hoặc unknown (chưa xác định được). Áp dụng per-path: cùng activity nhưng contribution type có thể khác nhau trên main path và alternative path.

**Bước 3 — Gap Analysis (phát hiện misalignments)**: Sau khi có ánh xạ, tự động xác định: (a) Unaligned goals = goals không có activity nào map vào → process đang bỏ sót mục tiêu này; (b) Unaligned activities = activities không contribute vào bất kỳ goal nào → activities này có thể là thừa hoặc chưa được liên kết với goals tương ứng; (c) Path gaps = goals chỉ được hỗ trợ trên một số paths nhưng không phải tất cả.

**Bước 4 — Quantitative weight assignment**: Gán trọng số w ∈ [0, 1] cho mỗi contribution link, dựa trên domain knowledge và stakeholder input. Trọng số cao = đóng góp mạnh. Đây là bước duy nhất đòi hỏi human judgment — tất cả các bước khác có thể được cơ giới hóa.

**Bước 5 — Propagation (forward + backward)**: Tính satisfaction scores cho tất cả goals theo từng execution path:
- Forward: từ leaf plans lan truyền lên root goals theo contribution links
- AND-decomposition: satisfaction = min(children satisfaction)
- OR-decomposition: satisfaction = max(children satisfaction)
- Contribution link: parent satisfaction += w × child satisfaction
- Output: satisfaction score [0, 1] cho mỗi goal trên mỗi execution path

### 3.3 Các Thành Phần Chính

**Path Classifier**: Phân tích BPMN diagram theo cấu trúc gateway để xác định tất cả execution paths và phân loại chúng. Main paths thường đi qua normal sequence flows; alternative paths thường kích hoạt khi có exceptions hoặc điều kiện đặc biệt.

**Activity-Plan Mapper**: Interface cho domain expert xác định ánh xạ từ BPMN activities sang Tropos plans. Cần human judgment vì không thể tự động hóa hoàn toàn — tên activities và plans không nhất thiết phải giống nhau, cần semantic understanding.

**Gap Detector**: Sau khi có ánh xạ hoàn chỉnh, tự động compute: (a) set of goals với no mapped activity, (b) set of activities với no mapped goal, (c) per-path gap analysis. Đây là component có thể tự động hóa hoàn toàn — không cần human judgment.

**Weight Assigner**: Interface để domain expert và stakeholders gán trọng số. Có thể dùng qualitative labels (strong contribution, moderate, weak) được chuyển sang numerical values.

**Tropos Propagator**: Implement Tropos quantitative propagation algorithm — traverse goal hierarchy từ leaf plans đến root goals, tính satisfaction scores theo AND/OR decomposition rules và contribution weights.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Giả sử một cơ quan hành chính công có process xử lý đơn xin giấy phép xây dựng:

**Goal model (Tropos)**:
- Root goal: "Provide Quality Public Service"
  - Sub-goal G1: "Ensure Document Completeness" (AND-decomposition)
  - Sub-goal G2: "Minimize Processing Time" (OR-decomposition)
    - G2a: "Fast-track eligible applications"
    - G2b: "Efficient standard processing"

**Process model (BPMN)** có hai paths:
- Main path: Receive Application → Check Documents → Fast-track Decision → Issue Permit
- Alternative path: Receive Application → Check Documents → Standard Review → Request Additional Info → Final Decision → Issue Permit

**Áp dụng phương pháp 5 bước:**

*Bước 1*: Main path và alternative path được xác định từ gateway sau "Check Documents".

*Bước 2*: Ánh xạ:
- "Check Documents" → plan "Document Verification", contribution: positive (+) to G1 với weight 0.9
- "Fast-track Decision" (main path only) → plan "Fast Processing", contribution: positive (+) to G2a với weight 0.8
- "Standard Review" (alt path) → plan "Standard Processing", contribution: positive (+) to G2b với weight 0.6
- "Request Additional Info" (alt path) → không có plan tương ứng trong Tropos → **Gap detected!**

*Bước 3*: Gap analysis phát hiện:
- "Request Additional Info" là **unaligned activity** (không có goal nào capture việc này — là overhead hay là cần thiết?)
- G1 "Ensure Document Completeness" chỉ được support bởi "Check Documents" (cả hai paths OK), nhưng không có activity nào thực sự *enforce* completeness ở cuối → **potential gap**

*Bước 4*: Gán weights dựa trên stakeholder input (như đã thể hiện ở trên).

*Bước 5*: Propagation:
- Main path: G2a satisfaction = 0.8, G2 (OR) = max(0.8, 0) = 0.8, Root = min(G1, G2) = min(0.9, 0.8) = **0.8**
- Alternative path: G2b satisfaction = 0.6, G2 (OR) = max(0, 0.6) = 0.6, Root = min(0.9, 0.6) = **0.6**

Kết quả: root goal đạt 0.8 trên main path và 0.6 trên alternative path — gap rõ ràng giữa hai paths, cần cải thiện alternative path.

### 3.5 Điểm Mới So Với Trước

Ba điểm khác biệt căn bản:

1. **Brownfield applicability**: Phương pháp áp dụng cho existing models — không yêu cầu generate từ đầu. Đây là điều tất cả phương pháp transformation trước không làm được.

2. **Gap analysis chính thức**: Lần đầu tiên có mechanism tự động phát hiện unaligned goals và unaligned activities — thay vì chỉ dựa vào manual inspection.

3. **Path-aware quantitative assessment**: Kết hợp path classification của BPMN với Tropos propagation — cho phép biết không chỉ "process có align với goals không" mà còn "trên execution path nào, ở mức độ nào".

---

## PHẦN 4 — Abstract (Tiếng Việt)

Business process management được áp dụng rộng rãi trong các tổ chức nhưng hiếm khi có cơ chế explicit để đảm bảo rằng quy trình thực sự hỗ trợ mục tiêu chiến lược. Bài báo này đề xuất một phương pháp 5 bước để align goal model (Tropos) với process model (BPMN) khi cả hai đã tồn tại trong tổ chức. Phương pháp bao gồm: phân loại execution paths, ánh xạ BPMN activities sang Tropos plans với contribution types, gap analysis tự động để phát hiện unaligned goals và activities, gán trọng số đóng góp, và propagation để tính quantitative satisfaction scores cho từng goal trên từng execution path. Không như các phương pháp trước chỉ sinh process từ goals, phương pháp này áp dụng được cho brownfield scenarios với existing models. Controlled experiment với 14 sinh viên thạc sĩ so sánh phương pháp 5 bước với ad-hoc alignment và cho thấy phương pháp đề xuất đạt tỷ lệ correct alignments cao hơn và phát hiện được nhiều gaps hơn.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
Process models thực tế từ cơ quan quản lý công (public administration) tại Brazil. Process về quản lý hồ sơ hành chính với ~20-30 activities và 8-12 goals. Dataset không công bố tên cụ thể vì lý do bảo mật.

**Controlled Experiment:**
14 sinh viên thạc sĩ Computer Science (có background BPM và Requirements Engineering) được chia thành 2 nhóm:
- Nhóm thực nghiệm: sử dụng phương pháp 5 bước
- Nhóm control: ad-hoc alignment (tự chọn cách approach)

| Tiêu chí | Ad-hoc Group | Phương pháp 5 bước |
|---|---|---|
| Tỷ lệ correct alignments | Thấp hơn | **Cao hơn đáng kể** |
| Tỷ lệ incorrect alignments | Cao hơn | **Thấp hơn** |
| Tốc độ thực hiện | ~30% nhanh hơn | Chậm hơn (nhiều bước hơn) |
| Post-experiment satisfaction | — | >80% participants tích cực |

**Qualitative findings:**
- Bước 3 (Gap Analysis) được đánh giá là hữu ích nhất — cung cấp objective checklist để nhận diện alignment.
- Bước 1 (Path Classification) giúp participants nhận ra rằng contribution của cùng activity có thể khác nhau tùy execution context.
- Nhóm ad-hoc thường bỏ sót goals ở alternative paths — nhóm phương pháp 5 bước ít bỏ sót hơn.

**Ví dụ cụ thể từ bài:**
- Goal "Ensure Document Completeness" chỉ được support trên main path nhưng thiếu activity tương ứng trên alternative path → Gap detected ở Bước 3.
- Goal "Minimize Processing Time" có contribution từ 3 activities với weights {0.8, 0.5, 0.3} → propagated satisfaction score = 0.72 trên main path vs. 0.45 trên alternative path.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Experiment nhỏ và academic**: 14 participants từ academic context — khó generalize sang industrial practitioners có nhiều kinh nghiệm hơn. Controlled experiment cũng loại bỏ nhiều complexity của môi trường doanh nghiệp thực tế.

2. **Weight assignment là chủ quan**: Không có hướng dẫn cụ thể cho việc gán trọng số contribution — phụ thuộc hoàn toàn vào domain expert judgment. Inter-rater reliability chưa được đo.

3. **Tropos subset**: Chỉ xử lý AND/OR decomposition và contribution links trong Tropos. Softgoal conflict resolution và các Tropos constructs phức tạp hơn chưa được tích hợp đầy đủ.

4. **Tool support chưa có**: Phương pháp 5 bước hiện tại là manual, không scalable cho large models với hàng trăm activities và goals. Bước 3 và 5 có thể tự động hóa hoàn toàn nhưng chưa có tool implementation.

5. **Single case study**: Dataset từ một cơ quan công quyền tại Brazil — chưa validate với other domains và other countries.

**Hướng nghiên cứu tiếp theo:**
- Phát triển tool automation cho 5 bước, đặc biệt Gap Detection và Propagation
- Mining contribution weights từ process execution logs (thay vì chỉ dựa vào stakeholder judgment)
- Mở rộng sang compliance checking với regulatory requirements
- Replication study với industry practitioners trong diverse domains
- Tích hợp với BPM suites (Camunda, Signavio) như native plugin

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất phương pháp 5 bước để align existing goal model (Tropos) với existing process model (BPMN), khắc phục hạn chế của các phương pháp transformation trước đây vốn yêu cầu generate process từ goals. Phương pháp kết hợp path classification, structural mapping, gap analysis tự động, quantitative weighting, và Tropos propagation để cung cấp cả assessment định tính (gaps) lẫn định lượng (satisfaction scores per path). Controlled experiment với 14 participants cho thấy phương pháp 5 bước đạt alignment quality tốt hơn ad-hoc approach, dù chậm hơn. Hạn chế chính là weight assignment vẫn chủ quan và thiếu tool support — đây là hướng mở rộng tự nhiên tiếp theo.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc đặt ra câu hỏi thực tiễn đúng đắn hơn: thay vì "làm thế nào để sinh process từ goals?", hỏi "làm thế nào để align goals với process đã tồn tại?". Sự thay đổi framing này mở ra hướng nghiên cứu áp dụng được trong thực tiễn doanh nghiệp, nơi rewriting quy trình từ đầu gần như không bao giờ là option.
