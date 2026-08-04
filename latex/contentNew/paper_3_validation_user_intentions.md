# Validation of User Intentions in Process Models

**Tác giả:** Aditya Ghose, George Koliadis (University of Wollongong, Australia)  |  **Năm:** 2007  |  **Venue:** International Conference on Business Process Management (BPM 2007)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực giao thoa giữa **Business Process Management (BPM)** và **Requirements Engineering**, cụ thể là vấn đề **validation alignment** giữa goal models và process models. Đây là một trong những thách thức cơ bản nhất trong phát triển hệ thống thông tin hướng quy trình.

Vấn đề xuất phát từ thực tiễn: trong các dự án phần mềm hiện đại, hai loại mô hình thường được phát triển bởi các nhóm khác nhau và theo các phương pháp luận khác nhau. **Goal model** (như GRL — Goal-oriented Requirements Language) được tạo ra bởi requirements analysts để nắm bắt ý định người dùng và mục tiêu tổ chức. **Process model** (như BPMN — Business Process Model and Notation) được tạo ra bởi business architects để mô tả cách công việc được thực hiện. Hai loại mô hình này thường được phát triển **semi-independently** và cực kỳ dễ mất đồng bộ với nhau theo thời gian.

Khi process model không còn phản ánh đúng ý định trong goal model, hệ quả có thể nghiêm trọng: quy trình tốn kém được triển khai nhưng không đạt được mục tiêu kinh doanh, hoặc tệ hơn, tạo ra kết quả ngược với mong muốn. Phát hiện vấn đề này chỉ sau khi deploy là cực kỳ tốn kém.

Vì vậy, cần một **cơ chế validation hình thức** để kiểm tra xem process model có thực sự thực thi ý định trong goal model hay không — và nếu không, xác định chính xác loại inconsistency là gì để có thể sửa chữa.

### Bài toán cụ thể

Bài toán được định nghĩa cụ thể như sau:

Khi tasks trong goal model (GRL) được ánh xạ sang activities trong process model (BPMN), có rất nhiều cấu hình ánh xạ khả thi. Với một ánh xạ bất kỳ, cần kiểm tra: **mọi đường thực thi có thể trong process có thỏa mãn đúng ý định người dùng trong goal model không?** — không chỉ là happy path mà toàn bộ state space của process.

- **Đầu vào**: Goal model (GRL với tasks, intentional relations: AND/IOR/XOR decomposition, contribution links) + Process model (BPMN/workflow với activities, AND/XOR/IOR gateways) + Ánh xạ giữa tasks và activities
- **Đầu ra**: Phân loại mỗi cặp (workflow pattern, intentional relation) thành một trong ba trạng thái:
  - **Realization Equivalent**: mọi execution path thỏa mãn goal
  - **Strong Inconsistency**: không có execution path nào thỏa mãn
  - **Potential Inconsistency**: một số paths thỏa mãn, một số không

### Tại sao khó

Thách thức đến từ nhiều hướng:

1. **Exponential state space**: Process models với gateways và loops có thể có số lượng đường thực thi khổng lồ — kiểm tra thủ công hay test-based không thể đảm bảo coverage đầy đủ.

2. **Cần formal semantics**: "Thỏa mãn ý định người dùng" là khái niệm mơ hồ nếu không được định nghĩa hình thức. Cần một ngôn ngữ lý thuyết chính xác để nói rõ thế nào là inconsistency.

3. **Sự tương tác phức tạp giữa gateway types**: AND-split, XOR-split, IOR-split trong BPMN có semantics khác nhau và tương tác theo những cách không trực quan với AND/IOR/XOR decomposition trong GRL. Ví dụ: XOR-gateway (chọn MỘT branch) có thể không tương thích với AND-decomposition (phải thực hiện TẤT CẢ tasks).

4. **Potential Inconsistency đặc biệt nguy hiểm**: Nếu chỉ test happy path, Potential Inconsistency sẽ bị bỏ qua vì một số executions vẫn đúng. Chỉ khi kiểm tra toàn bộ state space mới phát hiện được.

### Đóng góp của bài

Tác giả tuyên bố các đóng góp chính:
1. **Định nghĩa hình thức** lần đầu tiên cho Strong Inconsistency và Potential Inconsistency trong context goal-process mapping.
2. **Phương pháp validation tự động** dựa trên Description Logics (DL) reasoning — dịch cả goal model và process model sang DL knowledge base, sau đó dùng automated reasoner để kiểm tra toàn bộ state space.
3. **Realization Equivalence Table**: Bảng đầy đủ phân loại tất cả 15 cặp (5 workflow patterns × 3 intentional relation types) — reference table cho practitioners.
4. **Proof-of-concept tool** sử dụng OWL-API và Pellet reasoner, chạy trong khoảng 6 giây.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: Transformation Approaches (Goal → Process)

Các phương pháp như Liu et al. (2002) và Soffer & Wand (2005) tự động sinh process model từ goal model. Ưu điểm: đảm bảo alignment ngay từ đầu vì process được derived từ goals. Nhược điểm nghiêm trọng: (1) trong thực tế, process model thường đã tồn tại và không được sinh từ goals; (2) sau khi sinh ra không có cơ chế kiểm tra lại nếu ai đó modify process manually; (3) transformation thường mất đi một số flexibility của process design.

### Hướng 2: Alignment/Linking Approaches

Koliadis et al. (2006) — bài Stra2Bis trước đó — và các nghiên cứu tương tự đề xuất liên kết elements giữa hai models bằng annotations hay hyperlinks. Đây là hướng thực dụng hơn vì không đòi hỏi process phải được sinh từ goals. Tuy nhiên, liên kết cấu trúc không đảm bảo logical consistency: một link "đúng cú pháp" (task A linked to activity X) có thể vẫn sai về ngữ nghĩa (ví dụ: AND-task linked to XOR-gateway). Thiếu formal verification để phát hiện loại lỗi này.

### Hướng 3: Testing-Based Approaches

Duyệt một số test scenarios để kiểm tra alignment. Vấn đề căn bản: không thể đảm bảo coverage. Potential Inconsistency đặc biệt nguy hiểm vì happy path vẫn pass tests nhưng alternative execution paths vi phạm goal. Không có test suite nào đảm bảo rằng mọi possible execution đã được kiểm tra.

### Hướng 4: Formal Methods trong BPM

Các nghiên cứu về model checking và formal verification trong BPM (như dùng Petri nets hay temporal logic) tập trung vào các properties nội tại của process (soundness, deadlock-freedom, etc.) chứ không phải alignment với external goal models. Chúng không có mechanism để express "thỏa mãn goal X" như một verifiable property.

### Khoảng trống (Research Gap)

Cộng đồng thiếu kỹ thuật validation hình thức có thể: (1) định nghĩa chính xác thế nào là inconsistency giữa goal model và process model, (2) kiểm tra toàn bộ state space chứ không phải từng test case, (3) phân biệt được strong inconsistency (luôn luôn sai) và potential inconsistency (đôi khi sai). Đây là ba điều bài báo này nhắm đến.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Ý tưởng căn bản là: **dịch cả goal model và process model sang cùng một ngôn ngữ hình thức — Description Logics (DL) — để có thể so sánh chúng bằng automated reasoning.**

Description Logics là nền tảng lý thuyết của OWL (Web Ontology Language) — một ngôn ngữ biểu diễn tri thức có reasoner tự động (như Pellet) và tính chất **decidable reasoning** — tức là mọi câu hỏi về subsumption và satisfiability đều có thể được trả lời trong thời gian hữu hạn.

Khi cả hai models được mã hóa thành DL knowledge base, câu hỏi "process có thỏa mãn goal không?" trở thành câu hỏi DL có thể trả lời tự động: "concept WF (workflow pattern) có subsume concept IR (intentional relation) không?"

### 3.2 Kiến Trúc / Pipeline Tổng Thể

Pipeline gồm ba giai đoạn chính:

**Giai đoạn 1 — DL Translation (Song song):**
- Goal model (GRL) được dịch sang DL: mỗi task G trở thành một DL concept G; mỗi intentional relation (AND/IOR/XOR decomposition) trở thành DL axioms biểu diễn conjunction, disjunction, hay exclusive union của tasks.
- Process model (BPMN) được dịch sang DL: mỗi workflow pattern (AND-split/join, XOR-split/join, IOR-split/join, sequence) trở thành DL concept RelA; mỗi ánh xạ activity-to-task trở thành DL concept equivalence.

**Giai đoạn 2 — Tích hợp thành OWL Knowledge Base:**
Hai bản DL translation và mapping giữa chúng được tích hợp thành một OWL ontology thống nhất.

**Giai đoạn 3 — Automated Reasoning và Classification:**
Pellet reasoner (ALC expressiveness) thực hiện ba checks:
- Subsumption check (RelA ⊑ RelG): nếu đúng → Realization Equivalent
- Satisfiability check (RelA ⊓ RelG ≠ ⊥): nếu satisfiable nhưng không subsume → Potential Inconsistency
- Unsatisfiability (RelA ⊓ RelG ⊑ ⊥): nếu unsatisfiable → Strong Inconsistency

### 3.3 Các Thành Phần Chính

**DL Translation của Goal Model:**
Mỗi intentional relation trong GRL được mã hóa thành DL axioms tương ứng. Ví dụ: AND-decomposition của task G thành G1 và G2 trở thành axiom "bất kỳ execution nào thực hiện G phải thực hiện cả G1 AND G2". IOR-decomposition (inclusive OR) thành "ít nhất một trong {G1, G2, ...}". XOR-decomposition thành "đúng một trong {G1, G2, ...}".

**DL Translation của Process Model:**
Mỗi workflow pattern được mã hóa thành DL concept biểu diễn tập hợp executions mà pattern đó cho phép. AND-split: tất cả branches đều execute. XOR-split: đúng một branch executes. IOR-split: một hoặc nhiều branches execute (subset không rỗng). Sequence: thực hiện tuần tự không có lựa chọn.

**Pellet Reasoner:**
Pellet là một DL reasoner hỗ trợ ALC (Attributive Language with Complement) expressiveness — đủ để mã hóa các constructs cần thiết từ GRL và BPMN. Reasoner thực hiện classification tự động trên toàn bộ knowledge base, không cần enumerate từng execution path.

**Realization Equivalence Table:**
Output cuối cùng quan trọng nhất là bảng 5×3 phân loại tất cả 15 cặp (workflow pattern, intentional relation). Đây là reference table mà practitioners có thể dùng trực tiếp mà không cần chạy tool — nhìn vào bảng để biết ngay cặp mapping nào là safe và cặp nào có vấn đề.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Xét một e-commerce scenario:

**Goal model (GRL)**: Có task "Ship & Bill Order" với AND-decomposition thành hai subtasks: "Bill Customer (BC)" và "Ship Order (SO)". AND-decomposition có nghĩa là PHẢI thực hiện CẢ HAI: cả billing lẫn shipping.

**Scenario 1 — Strong Inconsistency**:
Process model dùng XOR-split gateway sau task "Process Order": hoặc đi qua "Send Bill" (BC), hoặc đi qua "Shipment" (SO), nhưng không bao giờ cả hai. XOR-split chỉ cho phép ĐÚNG MỘT branch.

Khi tool dịch sang DL:
- Goal: RelG = AND-decomposition → "phải có BC AND SO"
- Process: RelA = XOR-split → "đúng một trong {BC, SO}"
- DL check: RelA ⊓ RelG ⊑ ⊥ → **Strong Inconsistency**: process này không bao giờ có thể thỏa mãn goal.

**Scenario 2 — Potential Inconsistency**:
Process model dùng IOR-split gateway (một hoặc nhiều branches). IOR-split cho phép: {BC only}, {SO only}, hoặc {BC, SO}.

Khi tool check:
- Goal: AND-decomposition → cần cả BC và SO
- Process: IOR-split → cho phép chỉ một hoặc cả hai
- DL check: RelA ⊓ RelG satisfiable (khi cả hai branches chạy, goal thỏa mãn), nhưng RelA ⊄ RelG (có execution chỉ chạy một branch, không thỏa mãn) → **Potential Inconsistency**: đôi khi đúng, đôi khi sai.

**Scenario 3 — Realization Equivalent**:
Process model dùng AND-split gateway: cả BC và SO đều bắt buộc chạy.
- DL check: RelA ⊑ RelG → **Realization Equivalent**: mọi execution đều thỏa mãn goal.

### 3.5 Điểm Mới So Với Trước

Ba điểm khác biệt căn bản:

1. **First formal definitions**: Lần đầu tiên Strong Inconsistency và Potential Inconsistency được định nghĩa hình thức bằng DL — không còn là khái niệm trực quan mơ hồ mà là mathematical assertions có thể verify.

2. **Full state space coverage**: DL reasoning kiểm tra toàn bộ state space của process, không phụ thuộc vào test cases hay sampling. Đây là bảo đảm completeness mà testing không có được.

3. **Comprehensive reference table**: Bảng 5×3 phân loại tất cả 15 combinations — practitioners không cần chạy tool mỗi lần, chỉ cần tra bảng để biết cặp mapping nào safe.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Các goal models và process models thường được phát triển song song và dễ mất đồng bộ với nhau. Bài báo này đề xuất một kỹ thuật validation hình thức để kiểm tra xem một process model có thực sự thực thi các ý định người dùng được biểu diễn trong goal model hay không. Chúng tôi định nghĩa hai loại inconsistency: Strong Inconsistency (không có đường thực thi nào trong process thỏa mãn goal) và Potential Inconsistency (chỉ một số đường thực thi thỏa mãn). Phương pháp dịch cả goal model (GRL) và process model (BPMN/workflow) sang Description Logics (DL) knowledge base, sau đó sử dụng Pellet reasoner để tự động phân loại từng cặp (workflow pattern, intentional relation) thành Realization Equivalent, Potential Inconsistent, hoặc Strongly Inconsistent thông qua subsumption và satisfiability checks. Chúng tôi cung cấp Realization Equivalence Table phân loại đầy đủ 15 cặp (5 workflow patterns × 3 intentional relation types), và một proof-of-concept tool xử lý 20 model pairs trong khoảng 6 giây. Đây là lần đầu tiên các loại inconsistency này được định nghĩa và kiểm tra hình thức trong context goal-process alignment.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
20 goal models và 20 BPMN process models từ một e-store case study (online commerce scenario). Tasks tiêu biểu gồm: Ship Order, Bill Customer, Process Payment, Update Inventory. Dataset nhỏ nhưng đủ để demonstrate proof-of-concept và verify correctness của tool.

**Baseline:**
Không có quantitative baseline comparison — đây là formal methods paper và các phương pháp trước không có cùng output format để so sánh trực tiếp.

**Kết quả chính (với số liệu cụ thể):**

| Bước xử lý | Thời gian trung bình |
|---|---|
| Tạo DL knowledge base (Σ) từ models | 2480 ms |
| Reasoning (Pellet classification) | 3480 ms |
| **Tổng** | **~6 giây** |

Tool phân loại chính xác 100% các test cases theo Realization Equivalence Table lý thuyết: correctly identifies mọi valid equivalences, strong inconsistencies, và potential inconsistencies trong 20 model pairs.

**Realization Equivalence Table (trích):**
- AND-split + AND-decomposition → **Realization Equivalent** (safe)
- XOR-split + AND-decomposition → **Strong Inconsistency** (luôn sai)
- IOR-split + AND-decomposition → **Potential Inconsistency** (đôi khi sai)
- AND-split + XOR-decomposition → **Strong Inconsistency** (AND chạy hết nhưng XOR chỉ cho phép 1)
- XOR-split + XOR-decomposition → **Realization Equivalent** (safe khi mapping đúng)

**Ablation study:** Không có — formal methods paper; validation là proof of correctness của DL translation.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **GRL subset hạn chế**: Chỉ xử lý AND/IOR/XOR decomposition relations trong GRL. Contribution links (positive/negative contributions từ softgoals) và complex goal dependency structures chưa được mã hóa đầy đủ trong DL translation.

2. **Vấn đề loops**: Loops và recursive structures trong process model tạo ra vấn đề undecidability tiềm năng cho DL reasoning — giới hạn applicability với processes có vòng lặp phức tạp.

3. **Dataset nhỏ**: 20 models từ một case study duy nhất. Chưa validate scalability với industrial-size models có hàng trăm elements và phức tạp hơn nhiều.

4. **Partial mappings chưa được xử lý**: Khi một task ánh xạ sang nhiều activities, hoặc nhiều tasks ánh xạ sang một activity, phương pháp cần được mở rộng thêm.

**Hướng nghiên cứu tiếp theo:**
- Mở rộng DL encoding để xử lý contribution links và softgoals trong GRL
- Giải quyết vấn đề loops bằng bounded model checking hoặc abstraction techniques
- Tích hợp với process modeling tools (Activiti, Camunda) như validation plugin
- Scale lên industrial-size models với hàng trăm elements
- Mở rộng sang compliance checking với regulatory requirements (bước tiến tự nhiên từ goal-process alignment)
- Xử lý partial mappings và N-to-M task-activity relationships

---

## PHẦN 7 — Kết Luận

Bài báo trình bày kỹ thuật validation hình thức đầu tiên để kiểm tra alignment giữa goal models (GRL) và process models (BPMN), dựa trên DL reasoning. Phương pháp định nghĩa chính xác hai loại inconsistency (Strong và Potential), dịch cả hai models sang OWL knowledge base, và sử dụng Pellet reasoner để phân loại tự động với full state space coverage. Proof-of-concept tool xử lý 20 model pairs trong ~6 giây và phân loại chính xác 100%. Realization Equivalence Table cung cấp reference cho practitioners ngay cả khi không chạy tool. Hạn chế chính là chưa xử lý contribution links, loops, và partial mappings — đây là hướng mở rộng tự nhiên cho công việc tiếp theo.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc biến câu hỏi mơ hồ "process có thỏa mãn goal không?" thành một câu hỏi DL hình thức có thể trả lời tự động, đồng thời phân biệt rõ hai mức độ inconsistency (Strong vs. Potential) — một phân biệt cực kỳ quan trọng vì Potential Inconsistency hoàn toàn có thể qua mặt mọi test suite nếu happy path vẫn hoạt động bình thường.
