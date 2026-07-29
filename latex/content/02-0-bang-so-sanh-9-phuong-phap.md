# 2.0. Bảng So Sánh Chi Tiết 9 Phương Pháp Cốt Lõi (Tier 1)

Tài liệu tham chiếu kỹ thuật, hỗ trợ viết `02-cong-trinh-lien-quan.tex` — không phải bản thân
section paper. Hoàn thiện lại bản nháp 9 "Bài" do người dùng viết tay, dựa trên
`latex/context/SURVEY_kiem_tra_tuong_thich_goal_process.md` §2.1–2.2 (đúng 9 mục Tier 1: 4 mục
§2.1 + 5 mục §2.2, khớp số thứ tự Bài 1–9 người dùng đã đặt) cộng đối chiếu lại với văn bản gốc đã
đọc toàn văn (paper_3, paper_11, paper_4, paper_14, và bản đầy đủ `doc/paper/alig.md`), cộng 4 lượt
`WebSearch` bổ sung cho các bài chỉ có tóm tắt cấp cao trong SURVEY (Nagel et al., Eshuis & Ghose,
Li et al., và bài chưa xác định tác giả).

**Quy ước 4 cột theo đúng yêu cầu**, cột 3 "Chất lượng" được hiểu là: *phương pháp có mô hình hoá
khái niệm Quality/softgoal và contribution link (Make/Help/Hurt/Break...) hay không, và có dùng nó
trong thuật toán kiểm tra hay chỉ dừng ở goal/task cứng* — không phải quartile tạp chí (Q1/Q2). Nếu
ý người dùng là quartile, cần nói lại để đổi cách trình bày.

**Nguyên tắc gộp bài**: chỉ gộp khi cùng tác giả **và** cùng kỹ thuật cốt lõi (không gộp chỉ vì cùng
tác giả — ví dụ Ghose & Koliadis xuất hiện ở cả Bài 1 lẫn Bài 2/khảo sát khác với kỹ thuật khác hẳn,
nên giữ tách). Theo nguyên tắc này, trong 9 bài chỉ có đúng 1 cặp đủ điều kiện gộp: paper_3 và
paper_13 (Bài 1) — cùng tác giả gốc, cùng kỹ thuật Description Logics, paper_13 là bản mở rộng trực
tiếp của paper_3 sang choreography.

## Bảng tổng hợp

| # | Tên bài (gộp) | Tác giả — Năm — Venue | Goal lang. | Process lang. | Chất lượng / softgoal | Loại đầu ra | Độ tin cậy |
|---|---|---|---|---|---|---|---|
| 1 | Validation of User Intentions in Process Models + ...in Process Orchestration and Choreography | Ghose, Koliadis — 2007 — BPM · (+ mở rộng, tác giả bản mở rộng cần xác nhận lại, xem ghi chú) — 2014 — Information Systems | GRL | BPMN (orchestration + choreography) | Không — chỉ AND/IOR/XOR decomposition, contribution link bị loại khỏi phạm vi tường minh | Phân loại 3 lớp: Realization Equivalent / Strong Inconsistency / Potential Inconsistency | Cao (đã đọc toàn văn paper_3; paper_13 đọc toàn văn bản tóm tắt, tác giả bản mở rộng chưa chốt — xem ghi chú §1) |
| 2 | Relating Business Process Models to Goal-Oriented Requirements Models in KAOS (GoalBPM) | Koliadis, Ghose — 2006 — APCCM | KAOS | BPMN | Không có lớp Quality tách biệt; một số ràng buộc phi chức năng có thể biểu diễn qua goal dạng Maintain, không có contribution link định lượng | Traceability link + Satisfaction link (đạt/không đạt theo trajectory); hướng dẫn co-evolution khi thêm goal | Cao (đã đọc toàn văn) |
| 3 | A Method to Align Goals and Business Processes | Guizzardi, Reis — 2012 — CAiSE | Tropos | BPMN | Có, trung tâm — softgoal + contribution link (Make/Help/Hurt/Break/Unknown) + trọng số $w \in [0,1]$ | Ánh xạ activity→plan + danh sách gap (goal/activity chưa gắn) + điểm thoả mãn liên tục theo từng path | Cao (đã đọc toàn văn) |
| 4 | Verifying Goal-Oriented Specifications Used in MDD Processes (VeMI) | Guizzardi và cộng sự — 2014/2015 — CAiSE / Requirements Engineering Journal | i* | **Không phải BPMN** — đích là class model MDD (Integranova); xem ghi chú §2 | i* có Quality nhưng VeMI không kiểm dựa trên đó — trọng tâm là ánh xạ cấu trúc, không phải goal/quality satisfaction | Danh sách lỗi NAE / NIC / Warning + Fixing Guidelines | Cao (đã đọc toàn văn) |
| 5 | Ensuring Consistency among Business Goals and Business Process Models | Nagel, Gerth, Engels, Post — 2013 — IEEE EDOC | Kaos4SOA (mở rộng KAOS riêng của nhóm tác giả cho SOA, không phải KAOS gốc) | BPMN | Chưa xác nhận — cần đọc bài gốc | Xác nhận/vi phạm ràng buộc phụ thuộc logic-thời gian giữa các goal khi ánh xạ sang process | Trung bình (chỉ có abstract qua `WebSearch`, chưa đọc toàn văn — xem ghi chú §5) |
| 6 | Consistency Checking of Goal Models and Case Management Schemas | Eshuis, Ghose — 2021 — BPM Forum (LNBIP) | Goal model (dạng chung, không nêu rõ GRL/i*/KAOS trong abstract) | **Không phải BPMN** — declarative case management schema (data-centric) | Chưa xác nhận — abstract chỉ nói "structural approach", không nhắc contribution/quality | PASS/FAIL nhất quán cấu trúc, có tool + case study | Trung bình (chỉ có abstract — xem ghi chú §6) |
| 7 | Consistency Verification between Collaborative Business Processes and Requirements | Tác giả chưa xác định | Requirement Dependency Graph (mở rộng từ goal model, theo SURVEY — **chưa xác nhận được qua tra cứu**) | Collaborative BPMN | Chưa xác nhận | Chưa xác nhận — SURVEY ghi "model checking hình thức" | Thấp (không tìm được abstract khớp qua `WebSearch` — bài trả về cùng mã tài liệu lại nói về "concurrency operator", nghi ngờ SURVEY mô tả sai hoặc nhầm bài; xem ghi chú §7 — **không nên trích dẫn cho tới khi xác nhận lại**) |
| 8 | Aligning processes with high-level requirements: Goal-model-based compliance checking | Caballero-Villalobos, Baumeister, Paja, Kokoulina, López — 2026 — Information and Software Technology | iStar (goal + quality, contribution link Make/Help/Hurt/Break) | Workflow net **hoặc** DCR graph (2 formalism thay thế nhau, không phải dùng đồng thời cả 3 như bản nháp ghi) | Có, trung tâm — đây là bài quan hệ chất lượng sâu nhất trong 9 bài, xem §8 | Weak compliance / Strong compliance (= weak + quality satisfaction monotonicity); Non-compliant = phủ định weak | Cao nhất (đã đọc toàn văn đầy đủ `doc/paper/alig.md`, gồm cả phần Preliminaries hình thức) |
| 9 | A complete approach for CIM modelling and model formalising (GSP) | Li, Zhou, Gu, Li — 2015 — Information and Software Technology | Goal model hình thức hoá bằng lý thuyết phạm trù | Scenario model + Process model, cả hai hình thức hoá bằng Petri net | Chưa xác nhận rõ — abstract không nhắc Quality/softgoal tách biệt, có thể goal model chỉ có refinement thuần | 1 chiều: sinh tự động Scenario model từ Goal model, rồi Process model từ Scenario model (transformation, không phải kiểm tra hậu kỳ) | Trung bình (đã có abstract chi tiết hơn qua `WebSearch` — xem ghi chú §9) |

## Chi tiết từng bài

### Bài 1 — Validation of User Intentions (DL family)

- **Định nghĩa hình thức chính**: mỗi *intentional relation* (AND/IOR/XOR decomposition của GRL) và
  mỗi *workflow pattern* (AND/XOR/IOR-split/join của BPMN) được mã hoá thành 1 DL concept; quan hệ
  giữa 2 concept được kiểm bằng subsumption ($\mathrm{RelA} \sqsubseteq \mathrm{RelG}$) và
  satisfiability ($\mathrm{RelA} \sqcap \mathrm{RelG} \neq \bot$).
- **Luật chuyển**: task↔activity **không tự động suy ra** — là 1 phần đầu vào do người dùng cung
  cấp trước (mapping model); bài chỉ tự động hoá bước dịch DL + suy luận, không tự động hoá bước
  ánh xạ.
- **Input**: GRL goal model (task + AND/IOR/XOR) + BPMN/workflow process model (activity + gateway)
  + mapping model (task↔activity).
- **Output**: với mỗi cặp (workflow pattern, intentional relation) đã ánh xạ, 1 trong 3 nhãn —
  Realization Equivalent / Strong Inconsistency / Potential Inconsistency; kèm bảng tra cứu đầy đủ
  15 tổ hợp (5 pattern × 3 relation) không cần chạy lại tool.
- **Thuật toán**: dịch sang OWL knowledge base → chạy reasoner Pellet (ALC expressiveness) →
  classification. Đo được ~6 giây cho 20 cặp model.
- **Ghi chú độ tin cậy**: paper_3 (BPM 2007, Ghose & Koliadis) đã đọc toàn văn, thông tin chắc chắn.
  Bản mở rộng "...Orchestration and Choreography" — SURVEY nội bộ ghi tác giả "mở rộng từ paper_3
  (Ghose/Koliadis)" nhưng tiêu đề này **trùng khớp tuyệt đối** với bài Gröner, Asadi, Mohabbati,
  Gašević, Bošković, Silva Parreiras, *Information Systems* 43 (2014) đã dẫn ở
  `doc/paper/conformance-istar-bpmn2.md`. Đây là mâu thuẫn tác giả chưa giải quyết — **bắt buộc tự
  tra DBLP trước khi đưa vào bibliography chính thức**, xem thêm ghi chú tương tự đã nêu ở
  `latex/paper/references.bib` (khoá `groner2014vadl`).

### Bài 2 — Relating BPM to KAOS (GoalBPM)

- **Định nghĩa hình thức chính**: *Effect Annotation* (nhãn thể tường thuật gán cho activity BPMN,
  mô tả postcondition, có thể hình thức hoá bằng FOL); *Trajectory* (1 đường thực thi cụ thể từ
  start event tới end event, phân loại Normal/Exceptional); mẫu RT-LTL của KAOS goal — Achieve
  ($C \Rightarrow \Diamond T$), Maintain ($C \Rightarrow \Box T$), Avoid ($C \Rightarrow \Box \neg T$),
  Cease ($C \Rightarrow \Diamond \neg T$).
- **Luật chuyển**: 2 giai đoạn — (1) traceability link giữa KAOS goal và BPMN activity, thiết lập
  **hoàn toàn thủ công** dựa trên đối chiếu pre/postcondition bằng mắt; (2) satisfaction link qua 3
  bước: gán effect annotation, trích trajectory, đối chiếu hiệu ứng tích luỹ với mẫu RT-LTL — cả 3
  bước đều thủ công, không có công cụ tự động.
- **Input**: BPMN process (activity/gateway/flow) + KAOS goal model (goal có đặc tả RT-LTL) +
  traceability table đã thiết lập tay.
- **Output**: mỗi trajectory được đánh dấu thoả/không thoả từng goal; khi thêm 1 goal mới (kịch bản
  tiến hoá), chỉ ra trajectory nào không còn thoả và gợi ý loại activity cần bổ sung.
- **Thuật toán**: không có — toàn bộ pipeline chạy tay trên case study Package Sorting Process (~5
  activity, ~14 goal, 2 swim lane).
- **Ghi chú độ tin cậy**: cao, đã đọc toàn văn.

### Bài 3 — A Method to Align Goals and Business Processes (5-bước)

- **Định nghĩa hình thức chính**: *Path classification* (main/secondary/alternative theo cấu trúc
  gateway); *Gap* — unaligned goal (không activity nào map vào), unaligned activity (không đóng góp
  goal nào), path gap (chỉ được hỗ trợ trên 1 số path); *Contribution type* theo từng path (positive/
  negative/break/hurt/help/unknown) kèm trọng số $w \in [0,1]$.
- **Luật chuyển**: 5 bước tuần tự — (1) phân loại execution path; (2) ánh xạ activity→plan kèm
  contribution type theo path, **thủ công**, cần domain expert; (3) gap detection, **tự động hoá
  được hoàn toàn**; (4) gán trọng số, thủ công; (5) lan truyền forward/backward, **tự động hoá
  được**: AND-decomposition = $\min(\text{children})$, OR-decomposition = $\max(\text{children})$,
  contribution: $\text{parent} \mathrel{+}= w \times \text{child}$.
- **Input**: Tropos goal model đã tồn tại độc lập (actor, goal, softgoal, plan, AND/OR, contribution)
  + BPMN process model đã tồn tại độc lập (activity, sub-process, gateway, flow, swim lane) —
  brownfield, không yêu cầu quan hệ sinh 1 chiều.
- **Output**: bảng ánh xạ activity→plan; danh sách gap (goal/activity chưa gắn); điểm thoả mãn liên
  tục $[0,1]$ cho từng goal theo từng execution path.
- **Thuật toán**: propagation tuyến tính min/max/weighted-sum nêu trên; đánh giá qua thực nghiệm có
  kiểm soát (14 học viên cao học, so 5-bước với ad-hoc alignment, quy trình 5-bước cho tỉ lệ ánh xạ
  đúng cao hơn).
- **Ghi chú độ tin cậy**: cao, đã đọc toàn văn.

### Bài 4 — VeMI (Verification for Model Integration)

- **Định nghĩa hình thức chính**: *NAE* (Non-Accessible Element — phần tử i* không ánh xạ được sang
  bất kỳ phần tử Integranova nào theo transformation rule hiện có); *NIC* (Non-Instantiable Class —
  lớp Integranova sinh ra có ràng buộc mâu thuẫn, không thể có instance ở runtime); *Integration
  Metamodel* (hợp nhất i* metamodel + Integranova metamodel + cross-metamodel link mã hoá luật biến
  đổi thành constraint truy vấn được).
- **Luật chuyển**: transformation rule i*→Integranova được **phân tích trước** (ví dụ Actor→Class,
  Resource dependency→Association mandatory $[1..1]$, Task→Method), rồi mã hoá thành ràng buộc trong
  Integration Metamodel — đây là bước làm 1 lần khi xây VeMI, không phải chạy lại cho mỗi model.
- **Input**: i* model của người dùng + bộ transformation rule i*→Integranova đã biết trước (cố định,
  không phải đầu vào biến thiên).
- **Output**: danh sách lỗi NAE/NIC/Warning, mỗi lỗi kèm 1 hoặc nhiều Fixing Guideline cụ thể hướng
  dẫn sửa i* model.
- **Thuật toán**: OCL Verification Measures — các query OCL thực thi được trên Integration Metamodel
  (nền EMF), chạy **trước khi** phép biến đổi mô hình thật sự diễn ra (pre-transformation). Đánh giá
  qua thực nghiệm có kiểm soát 24 người tham gia + case study công nghiệp (photography agency,
  ~20–30 actor).
- **Lưu ý phân loại quan trọng**: khác 8 bài còn lại, đích đối chiếu của VeMI **không phải business
  process model (BPMN)** mà là 1 class model MDD (Integranova). Xếp vào Tier 1 vì cùng họ "kiểm tra
  nhất quán goal model với 1 mô hình đích trừu tượng thấp hơn", nhưng khi trích trong Related Work
  cần nói rõ đây là biến thể goal↔design-model, không phải goal↔process thuần tuý.
- **Ghi chú độ tin cậy**: cao, đã đọc toàn văn.

### Bài 5 — Ensuring Consistency among Business Goals and Business Process Models

- **Định nghĩa hình thức chính**: chưa xác nhận đầy đủ. Xác nhận được qua `WebSearch`: nhóm tác giả
  dùng 1 ngôn ngữ goal model tự mở rộng gọi là **Kaos4SOA** (không phải KAOS gốc), nhấn mạnh khả
  năng biểu diễn *temporal and logical dependencies* giữa các goal — đây chính là điểm khác biệt cốt
  lõi so với Bài 1/2/3/4 (tập trung vào dependency **giữa các goal với nhau**, không phải quan hệ
  goal↔activity đơn lẻ).
- **Luật chuyển / Input / Output / Thuật toán**: **chưa đủ thông tin** — chỉ có abstract, không có
  toàn văn. Không suy diễn thêm để tránh bịa chi tiết kỹ thuật.
- **Việc cần làm**: tự truy cập IEEE Xplore (document 6658260, EDOC 2013, tr. 17–26) qua tài khoản
  thư viện trước khi viết bản thảo chính thức có trích dẫn kỹ thuật cho bài này.

### Bài 6 — Consistency Checking of Goal Models and Case Management Schemas

- **Định nghĩa hình thức chính**: chưa xác nhận đầy đủ. Abstract xác nhận: đối tượng đối chiếu là
  **case management schema dạng khai báo (declarative, data-centric)**, khác hẳn BPMN (activity-
  centric) — đây là điểm đặc biệt của bài này trong 9 bài, chỉ có Bài 6 và (gián tiếp, chiều ngược)
  N9 xét process dạng khai báo thay vì BPMN.
- **Luật chuyển / Input / Output**: gọi là "structural approach" theo abstract, có tool và case
  study đi kèm, nhưng cơ chế cụ thể (loại ràng buộc nào được kiểm, thuật toán nào) **chưa xác nhận**.
- **Việc cần làm**: tự truy cập chương sách Springer (BPM 2021 Forum, LNBIP, DOI
  10.1007/978-3-030-85440-9_4, tr. 54–70) qua tài khoản thư viện trước khi viết bản thảo chính thức.

### Bài 7 — Consistency Verification between Collaborative Business Processes and Requirements

- **Cảnh báo độ tin cậy quan trọng nhất trong bảng**: `WebSearch` cho tài liệu IEEE Xplore document
  8875503 không trả về mô tả khớp với "Requirement Dependency Graph" như SURVEY nội bộ ghi — kết quả
  tìm kiếm chỉ cho biết tài liệu này (hoặc 1 tài liệu liên quan cùng chủ đề) giới thiệu "concurrency
  operator" để **compose process của các tổ chức tham gia** (collaborative process), không nhắc gì
  tới goal model hay requirement dependency graph.
- **Kết luận**: không đủ cơ sở để hoàn thiện bất kỳ ô nào khác trong bảng cho bài này. **Khuyến nghị
  không trích dẫn bài này trong bản thảo chính thức cho tới khi tự truy cập trực tiếp IEEE Xplore và
  xác nhận lại đúng tên bài, đúng tác giả, và đúng nội dung khớp với mô tả "goal↔collaborative BPMN
  consistency"** — có khả năng SURVEY nội bộ đã gán nhầm mã tài liệu.

### Bài 8 — Aligning Processes with High-Level Requirements (Caballero-Villalobos et al.)

Bài có độ tin cậy cao nhất trong bảng vì đã đọc toàn văn đầy đủ, gồm cả phần Preliminaries hình thức
hoá (`doc/paper/alig.md`, Definition 2.1–2.8 trở đi).

- **Định nghĩa hình thức chính**:
  - *Labeled Transition System* (Definition 2.1): $\Gamma_A = \langle S, A, s_0, \to \rangle$ — nền
    tảng ngữ nghĩa chung cho **cả 2 phía** goal và process.
  - *Workflow net* (Definition 2.3, dựa trên Petri net Definition 2.2): $N = (P, T, F, in, out)$,
    có khái niệm *sound* (proper completing + weakly terminating + quasi-live) và *safe*.
  - *DCR graph* (Definition 2.4–2.7): marking $\mathsf{M}$ gồm bộ ba $(h, i, p)$ — happened/included/
    pending cho mỗi event; 4 loại quan hệ: condition ($e \to\!\bullet\, e'$), response
    ($e \,\bullet\!\to e'$), inclusion ($e \to\!+\, e'$), exclusion ($e \to\!\%\, e'$); *accepting
    run* — mọi event pending+included cuối cùng phải được thực thi hoặc bị loại trừ.
  - *Goal model* (Definition 2.8, kế thừa từ Giorgini et al., dựa trên iStar): $GM := (IE, L)$ với
    $IE := G \uplus T \uplus Q$ (Goal/Task/Quality) — có contribution link Make/Help/Hurt/Break, và
    luật lan truyền marking AND/OR/Make/Break (khớp đúng những gì đã ghi nhận trước đây trong
    `doc/paper/conformance-istar-bpmn2.md`).
- **Luật chuyển**: cả Workflow net lẫn DCR graph đều được dịch sang LTS (Definition 2.1) theo đúng
  ngữ nghĩa vận hành riêng của từng formalism; goal model được gán ngữ nghĩa vận hành riêng (marking
  propagation) rồi cũng biểu diễn dưới dạng LTS; 1 *mapping function* (correspondence relation) xác
  định process action nào cập nhật intentional element nào — đây là điểm khớp nối duy nhất giữa 2
  bên, tương tự vai trò `ConformanceMapping` trong thiết kế nội bộ đã ghi ở
  `doc/paper/conformance-istar-bpmn2.md`.
- **Input**: 1 goal model iStar (goal, task, quality, contribution) + 1 process model, là **Workflow
  net hoặc DCR graph** (2 lựa chọn thay thế nhau, không phải cả 3 formalism dùng đồng thời như bản
  nháp ban đầu ghi) + 1 mapping function process action↔intentional element.
- **Output**: 3 mức verdict —
  - *Weak compliance*: từ mọi trạng thái sản phẩm (product state) đạt được, tồn tại đường tiếp tục
    tới 1 trạng thái mà mọi quality đều thoả, và mọi trạng thái kết thúc (terminal) đều đã thoả mọi
    quality.
  - *Strong compliance*: weak compliance **cộng thêm** tính đơn điệu thoả mãn quality (quality
    satisfaction monotonicity, còn gọi là stability) — 1 khi quality đã thoả trên 1 run thì vẫn thoả
    ở mọi trạng thái sau đó của run đó.
  - *Non-compliant*: phủ định của weak compliance.
  Đây là điểm cần sửa so với bản nháp ban đầu: không phải 4 nhãn rời rạc ngang hàng, mà là **2 mức
  lồng nhau** (strong ⊂ weak) cộng phần bù (non-compliant).
- **Thuật toán**: dựng *synchronous product* của 2 LTS (goal + process), rồi kiểm reachability theo
  2 hướng (forward/backward BFS) trên product đó — đúng bản chất đã ghi nhận trước đây khi phân tích
  case study `construction_permit` trong `doc/paper/conformance-istar-bpmn2.md` §5. Có chứng minh
  hình thức về tính đúng đắn, đầy đủ, và độ phức tạp cho cả thuật toán kiểm compliance lẫn thuật
  toán kiểm stability. Công cụ hỗ trợ: Kogi tool.
- **Chất lượng/softgoal**: đây là bài xử lý sâu nhất trong 9 bài — toàn bộ đóng góp chính của bài là
  nâng compliance checking từ mức task/event rời rạc lên mức *high-level business requirement* biểu
  diễn qua Quality với contribution link, đúng như tiêu đề bài đã nêu.

### Bài 9 — A Complete Approach for CIM Modelling and Model Formalising (GSP)

- **Định nghĩa hình thức chính**: goal model hình thức hoá bằng **lý thuyết phạm trù** (category
  theory); scenario model và process model hình thức hoá bằng **mạng Petri** (Petri net) — khác hẳn
  nền tảng toán học của 8 bài còn lại (DL/RT-LTL/OCL/marking-LTS), là "họ kỹ thuật" thứ 5 xuất hiện
  trong 9 bài Tier 1.
- **Luật chuyển**: định nghĩa 1 bộ metamodel + transformation rule cho phép **sinh tự động** Scenario
  model từ Goal model, rồi Process model từ Scenario model — dùng **QVTo** làm ngôn ngữ biến đổi.
  Đây là điểm khác biệt quan trọng nhất so với 8 bài còn lại: Bài 1–8 đều là bài toán **validation**
  (2 model đã tồn tại, kiểm tra sau), còn Bài 9 là bài toán **transformation** (sinh model đích từ
  model nguồn theo 1 chiều, refinement từng bước trong cùng tầng CIM) — về bản chất **không cùng loại
  bài toán** với 8 bài kia, dù cùng nằm trong Tier 1 của SURVEY vì cùng liên quan goal↔process.
- **Input**: 1 goal model cấp cao (category theory).
- **Output**: Scenario model (Petri net) sinh tự động, rồi Process model (Petri net) sinh tự động từ
  Scenario model — không phải danh sách lỗi/verdict nhất quán như 8 bài kia.
- **Thuật toán**: các transformation rule QVTo, cài đặt và đánh giá bằng 1 công cụ MDA, kiểm chứng
  qua case study công ty du lịch (travel agency).
- **Ghi chú quan trọng cho việc trích dẫn**: vì Bài 9 thuộc loại bài toán transformation chứ không
  phải validation, khi đưa vào Related Work (nhóm nào trong 5 nhóm ở `02-cong-trinh-lien-quan.tex`)
  cần cân nhắc lại — hiện đang xếp vào nhóm A (suy luận tĩnh trên hình thức luận chung) vì cùng dùng
  hình thức hoá đại số/logic, nhưng về bản chất bài toán gần nhóm "Sinh và ánh xạ quy trình từ mô
  hình mục tiêu" đã bị loại khỏi phạm vi khảo sát chính (xem nguyên tắc lọc ở
  `02-cong-trinh-lien-quan.md` §2.1). Cần quyết định lại: giữ trong nhóm A với vai trò "1 kỹ thuật
  hình thức hoá khác lạ đáng chú ý" (như đã làm), hay bỏ khỏi Related Work chính vì sai loại bài
  toán theo đúng nguyên tắc lọc đã đặt ra.

## Việc cần làm tiếp theo

1. **Xác nhận lại tác giả bản mở rộng của Bài 1** ("...Orchestration and Choreography") qua DBLP —
   nghi vấn đây là bài Gröner et al. 2014 (Information Systems), không phải bản mở rộng của chính
   Ghose/Koliadis như SURVEY nội bộ ghi.
2. **Đọc toàn văn Bài 5 (Nagel et al.) và Bài 6 (Eshuis & Ghose)** qua tài khoản thư viện trước khi
   đưa chi tiết kỹ thuật (luật chuyển, thuật toán) vào bản thảo chính thức — hiện chỉ có abstract.
3. **Xác minh lại toàn bộ Bài 7** — có khả năng SURVEY nội bộ gán nhầm mã tài liệu IEEE Xplore; nên
   loại khỏi bản thảo chính thức cho tới khi xác nhận được đúng bài.
4. **Quyết định vị trí của Bài 9** trong 5 nhóm ở `02-cong-trinh-lien-quan.tex` — bài toán
   transformation, không phải validation, có thể cần tách khỏi Related Work chính hoặc nêu rõ ràng
   là ngoại lệ khi trích.
5. Sau khi 4 việc trên hoàn tất, cập nhật lại `02-cong-trinh-lien-quan.tex` với thông tin đã chính
   xác hoá ở bảng này, đặc biệt sửa lại phần mô tả Bài 8 (`caballero2026alig`) cho đúng 2 mức weak/
   strong lồng nhau thay vì liệt kê phẳng.
