# Khảo Sát: Bài Toán Kiểm Tra Mức Độ Tương Thích Giữa Mô Hình Mục Tiêu (Goal Model) và Mô Hình Quy Trình Nghiệp Vụ (Business Process Model)

> Tổng hợp từ `public/paperSurvey/aligementGOALBPMN/` (16 bài báo) + kết quả tìm kiếm bổ sung trên Google Scholar/ResearchGate/ScienceDirect/Springer/IEEE Xplore/MDPI/arXiv, đối chiếu với định hướng nghiên cứu riêng trong `public/Goal_BPMN/research/gap.md` + `gap_assessment.md`.
> Ngày tổng hợp: 2026-07-22 (cập nhật lần 2 — bổ sung ràng buộc chất lượng nguồn + research online)

## 0. Ràng buộc lựa chọn tài liệu (cập nhật)

1. **Chỉ chọn bài đã đăng tạp chí (journal, ưu tiên có Scopus/SCImago) hoặc hội nghị/workshop uy tín trong lĩnh vực RE/BPM/CAiSE** (BPM, CAiSE, RE, ICSE, PRIMA, IEEE EDOC, TSE, REJ, JSS, IST, SoSyM...). Khóa luận/luận văn sinh viên chưa qua bình duyệt **bị loại khỏi danh sách chính**.
2. **Phương pháp kiểm tra một chiều (unidirectional)** — ví dụ chỉ kiểm tra "process có thỏa mãn goal hay không" mà không cần suy luận ngược lại — **vẫn được tính là liên quan trực tiếp**, không bị coi là kém giá trị hơn phương pháp hai chiều (bidirectional). Đây là một trong hai họ phương pháp chính của bài toán, và phần lớn tài liệu Tier 1 hiện có (paper_3, paper_13, Nagel et al. 2013, Eshuis & Ghose 2021) đều là kiểm tra một chiều.

---

## 1. Định nghĩa bài toán

**Bài toán "kiểm tra mức độ tương thích"** (compatibility / consistency / alignment validation) giữa hai loại mô hình:

- **Mô hình mục tiêu** (Goal Model): trả lời câu hỏi *Why/Who* — GRL, i*, Tropos, KAOS...
- **Mô hình quy trình nghiệp vụ** (Business Process Model): trả lời câu hỏi *What/How/When* — BPMN, UCM, Communication Analysis, case management schema...

Câu hỏi cốt lõi: **Cho một goal model và một process model đã tồn tại (hoặc được ánh xạ với nhau), làm sao xác định/kiểm chứng một cách hình thức rằng process model có thực sự thỏa mãn (satisfy/cover) các goal đã đặc tả hay không — và nếu không, chỉ ra chính xác loại inconsistency?**

Đây là bài toán **validation** (kiểm tra sau khi hai mô hình đã có), khác với bài toán **transformation** (sinh process model từ goal model) hay **mining** (khai phá lại goal từ process/log). Hai họ phương pháp validation chính:

- **Một chiều (unidirectional)**: chỉ kiểm tra process có thỏa mãn goal (goal → process direction check), không yêu cầu suy luận ngược.
- **Hai chiều (bidirectional)**: thiết lập traceability + kiểm tra co-evolution cả hai chiều (goal đổi → process nào bị ảnh hưởng, và ngược lại).

> **Cập nhật khung nhìn (theo trao đổi lần 3):** Goal↔Process chỉ là **một trường hợp cụ thể (instance)** của một bài toán tổng quát hơn: **kiểm chứng ngữ nghĩa tương thích (semantic conformance/consistency checking) giữa 2 mô hình**, hai mô hình này có thể khác nhau về **cấp độ trừu tượng** (abstraction level), **góc nhìn/viewpoint**, hoặc cả hai. Mục 6 phía dưới mở rộng khảo sát sang khung nhìn tổng quát này.

---

## 2. Danh sách bài báo — phân theo mức độ liên quan trực tiếp

### Tier 1 — Trực tiếp về kiểm tra tương thích / nhất quán (CORE, đã lọc theo ràng buộc journal/hội nghị uy tín)

#### 2.1 Đã có trong survey gốc

| # | Bài báo | Tác giả, Năm | Goal Lang. | Process Lang. | Kỹ thuật kiểm tra | Chiều | File |
|---|---------|--------------|-----------|----------------|--------------------|-------|------|
| 1 | **Validation of User Intentions in Process Models** | Ghose, Koliadis — BPM 2007 | GRL | BPMN | Description Logics (DL) → strong/potential inconsistency | 1 chiều | `paper_3_validation_user_intentions.md` |
| 2 | **Validation of User Intentions in Process Orchestration and Choreography** | mở rộng paper_3 | GRL | BPMN (orch. + choreo.) | DL + actor-dependency checking | 1 chiều | `paper_13_validation_orchestration_choreography.md` |
| 3 | **Relating Business Process Models to Goal-Oriented Requirements Models in KAOS** | Koliadis, Ghose — APCCM 2006 | KAOS | BPMN | Effect annotations + RT-LTL model checking | 2 chiều | `paper_11_relating_bpm_kaos.md` |
| 4 | **A Method to Align Goals and Business Processes** | Guizzardi, Reis — CAiSE 2012 | Tropos | BPMN | 5-bước alignment + value propagation (brownfield) | 2 chiều (kiểm tra + gợi ý sửa) | `paper_4_align_goals_business_processes.md` |
| 5 | **Verifying Goal-Oriented Specifications Used in MDD Processes** | Guizzardi et al. — CAiSE 2014 / Requirements Engineering Journal | i* | Integranova/MDD class model | OCL Verification Measures | 1 chiều | `paper_14_verifying_goal_specs_mdd.md` |

> **Đã loại khỏi Tier 1**: bài "Nghiên cứu phát triển DSL và công cụ hỗ trợ mô hình hóa yêu cầu hướng mục tiêu" (khóa luận tốt nghiệp, UET/VNU, 2026 — file `paper_16_kaos_dsl_bpmn_consistency.md`) — **loại do là khóa luận sinh viên, chưa qua bình duyệt/chưa đăng tạp chí hay hội nghị**, không đạt ràng buộc chất lượng nguồn. File vẫn giữ lại trong thư mục để tham khảo nội bộ nhưng không tính vào danh sách "bài báo".

#### 2.2 Bổ sung mới (research online — Scholar/ResearchGate/ScienceDirect/Springer/IEEE/MDPI)

| # | Bài báo | Tác giả, Năm | Venue | Goal↔Process | Kỹ thuật | Chiều | Nguồn |
|---|---------|--------------|-------|---------------|----------|-------|-------|
| N1 | **Ensuring Consistency among Business Goals and Business Process Models** | Nagel, B.; Gerth, C.; Engels, G.; Post, J. — 2013 | 17th IEEE EDOC 2013 (IEEE Xplore) | Business goal model ↔ BPMN | Kiểm tra logical/temporal dependency giữa goals khi ánh xạ sang process (thứ tự thực hiện) | 1 chiều | [IEEE Xplore](https://ieeexplore.ieee.org/document/6658260/) |
| N2 | **Consistency Checking of Goal Models and Case Management Schemas** | Eshuis, R.; Ghose, A. — 2021 | BPM 2021 Forum, LNBIP, Springer | Goal model ↔ declarative case management schema (data-centric process) | Structural consistency checking, có tool + case study | 1 chiều | [Springer](https://link.springer.com/chapter/10.1007/978-3-030-85440-9_4) |
| N3 | **Consistency Verification between Collaborative Business Processes and Requirements** | (tác giả chưa xác định đầy đủ qua tìm kiếm — cần tra IEEE Xplore) — 2019 | IEEE conference (IEEE Xplore doc 8875503) | Requirement Dependency Graph (mở rộng từ goal model) ↔ collaborative BPMN | Model checking hình thức | 1 chiều | [IEEE Xplore](https://ieeexplore.ieee.org/document/8875503/) |
| N4 | **High-Level Requirements-Driven Business Process Compliance** (bản hội nghị) / **Aligning processes with high-level requirements: Goal-model-based compliance checking** (bản tạp chí mở rộng) | Caballero-Villalobos, J.; Burattin, A.; López, H.A. — 2025/2026 | BPM 2025 Forum (LNBIP vol. 564, Springer) + **Information and Software Technology** (Elsevier, 2026) | Goal model (high-level/non-functional requirements) ↔ BPMN | Nâng compliance checking từ mức task lên mức business goal/softgoal | 1 chiều | [Springer](https://link.springer.com/chapter/10.1007/978-3-032-02929-4_2) · [ScienceDirect](https://www.sciencedirect.com/science/article/abs/pii/S0950584926001357) |
| N12 | **A complete approach for CIM modelling and model formalising** | Li, Zonghua; Zhou, Xiaofeng; Gu, Aihua; Li, Qinfeng — 2015 | **Information and Software Technology**, Vol. 65, pp. 39–55 (Elsevier) | **Goal model → Scenario model → Process model** (GSP, stepwise refinement trong cùng tầng CIM) | Goal model hình thức hóa bằng **category theory**, Scenario/Process model hình thức hóa bằng **Petri nets**, ánh xạ giữa các mô hình định nghĩa bằng **QVTo** | 1 chiều (refinement goal→process) | [ScienceDirect](https://www.sciencedirect.com/science/article/abs/pii/S0950584915000786) |

**Nhận xét về N12:** đây là phát hiện đáng chú ý nhất của vòng research này cho chính bài toán gốc (goal↔process) — đề xuất một chuỗi hình thức hóa **Goal → Scenario → Process** hoàn toàn khác kỹ thuật với Tier 1 gốc (category theory + Petri net thay vì DL/RT-LTL/OCL), cho thấy còn cả một họ kỹ thuật hình thức khác (algebraic/category-theoretic) chưa được nhóm 16 bài gốc đề cập.

**Nhận xét về N1–N4:** đây đều là các bài **rất mới so với bộ 16 bài gốc** (2013, 2019, 2021, 2025/2026) và bổ sung đúng vào khoảng trống "unidirectional consistency checking giữa goal và BPMN" mà bộ gốc còn thiếu case đa dạng. Đặc biệt **N4 là bài mới nhất (2026)**, cùng nhóm tác giả với "Three Decades of Formal Methods in Business Process Compliance" (mục 2.3), cho thấy đây là một hướng nghiên cứu đang hoạt động (active) chứ không phải đã đóng băng.

### Tier 2 — Liên quan gần (transformation/construction có bước kiểm tra alignment, hoặc traceability/change-impact)

| # | Bài báo | Tác giả, Năm | Ghi chú liên quan |
|---|---------|--------------|--------------------|
| 7 | Combining Goal Modelling with Business Process Modelling: 20 năm URN | Amyot et al. — 2022 | Framework tích hợp GRL+UCM, có propagation/alignment rules nhưng thiên về xây dựng hơn kiểm tra hậu-kỳ |
| 8 | Stra2Bis: Aligning Business Strategy and Business Processes | Koliadis, Ghose — APCCM 2022 | Transformation LiteStrat → CA process, hướng dẫn thủ công, không có kiểm tra hình thức |
| 9 | An Improved URN Models' Construction Approach | Mussbacher, Amyot et al. — 2012 | Bridging mechanism GRL↔UCM để tránh mất nhất quán *khi xây dựng*, gần với validation nhưng mục tiêu chính là construction |
| 10 | GoBIS: Integrated Framework to Analyse Goal and Business Process Perspectives | Guizzardi, Barcellos et al. — 2014-15 | Framework tích hợp i* + Communication Analysis, có ontological foundation nhưng validation chỉ là một phần |
| 11 | Towards Goal-oriented Process Mining | Ghasemi — 2019/2022 | Chiều ngược: từ event log/process suy ra goal satisfaction — bổ trợ cho bài toán checking nhưng không phải static validation |
| N5 | **Consistency Analysis for User Requirements Notation Models** | Akhigbe, O.; Amyot, D.; Anda, A.A.; Lessard, L.; Xiao, D. — 2016 | International i* Workshop (CEUR-WS, workshop — không phải journal, xếp Tier 2 vì mức bình duyệt nhẹ hơn). Bộ OCL constraint 1 chiều kiểm tra nhất quán **GRL↔UCM** (goal↔process trong cùng chuẩn URN), cài trong tool jUCMNav. |
| N6 | **An Automated Change Impact Analysis Approach for User Requirements Notation Models** | Alkaf, H.S.; Hassine, J.; Binalialhaq, T.; Amyot, D. — 2019 | **Journal of Systems and Software, Vol. 157** (Elsevier). Không kiểm tra tĩnh một lần mà lan truyền tác động thay đổi qua GRL↔UCM links — **khớp trực tiếp với hướng "bidirectional change impact" mà bạn liệt kê là gap ưu tiên 3** trong `gap.md`. |
| N7 | **Practical Challenges for Methods Transforming i\* Goal Models into Business Process Models** | Decreus, K.; Snoeck, M.; Poels, G. — 2009 | **RE 2009** (IEEE). Bài đánh giá phê phán (critical survey) các phương pháp transform i*→BPMN đương thời; kết luận nêu rõ **"lack of inter-model consistency checks"** là một trong 5 hạn chế lớn nhất — dùng để justify tầm quan trọng của bài toán bạn đang làm. |
| N8 | **A systematic literature mapping of goal and non-goal modelling methods for legal and regulatory compliance** | Akhigbe, O.; Amyot, D.; Richards, G. — 2019 | **Requirements Engineering Journal** (Springer). SLR 103/286 bài, phạm vi rộng hơn (compliance pháp lý nói chung) nhưng phần goal-oriented methods trùng với Tier 1; hữu ích để trích dẫn bối cảnh "goal modelling for compliance checking" nói chung. |
| N9 | **Synthesizing goal models from declarative data-centric process models** | (tác giả chưa xác nhận đầy đủ) — 2025 | ScienceDirect/Elsevier. Chiều ngược của N2 (Eshuis & Ghose): tự động **suy ra goal model từ milestone của declarative data-centric process model** — cùng cặp tác giả/hướng nghiên cứu Eindhoven (TU/e), bổ trợ paper_11 (Goal-oriented Process Mining) với process model kiểu declarative thay vì event log. |

### Tier 3 — Liên quan xa hoặc mới nổi (bối cảnh, hướng công nghệ mới)

| # | Bài báo | Ghi chú |
|---|---------|---------|
| 12 | ActivFORMS (self-adaptive systems) — Weyns, Iftikhar 2016 | Goal ở dạng quality goals cho MAPE-K runtime, không phải BPMN |
| 13 | Formal Reasoning for Evolving Goal Models — Litoiu, Horkoff et al. 2019 | Chỉ phân tích goal model theo thời gian, không ghép với process model |
| 14 | Making Model Checking Feasible for GOAL — Yang, Holvoet 2014 | Verify agent-programming language GOAL, không phải goal-model-vs-process |
| 15 | Multi-Objective Reasoning with Constrained Goal Models — Nguyen et al. 2021 | Tối ưu hóa realization của goal model, không liên quan process model |
| 16 | Including Business Strategy in Model-Driven Methods: An Experiment — Noel, Panach, Pastor 2023 | Thực nghiệm về đưa business strategy vào CIM/MDA, không phải compatibility checking |
| N10 | **Three Decades of Formal Methods in Business Process Compliance: A Systematic Literature Review** — López, H.A.; Hildebrandt, T.T. — arXiv 2024 (46 bài chính từ 5018 ứng viên) | SLR rất rộng về formal methods cho BP compliance nói chung (không riêng goal model); hiện là **preprint arXiv, chưa xác nhận đã đăng journal** — cần kiểm tra lại trước khi trích dẫn chính thức. Cùng đồng tác giả (López) với N4. |
| N11 | **Iterative Requirements-Driven Business Process Modeling and Verification with Large Language Models** — 2026, MDPI *Applied Sciences* (16(1):518) | Hướng **mới nổi**: dùng LLM sinh structured requirements model rồi ánh xạ + verify sang BPMN (gọi là "CBPMN"), có bước kiểm tra static flow error để giữ traceability/consistency. Không dùng GRL/i*/KAOS truyền thống nhưng cùng bài toán "requirements ↔ process consistency" — đáng theo dõi vì gần với hướng AI-agent của repo này. |

---

## 3. Cặp bài báo liên hệ chặt (kế thừa/mở rộng lẫn nhau)

- `paper_3 → paper_13`: paper_13 mở rộng DL validation từ orchestration sang cả choreography.
- `paper_11 ↔ paper_4`: cùng mục tiêu alignment KAOS/Tropos ↔ BPMN, khác kỹ thuật (RT-LTL vs 5-bước alignment).
- `paper_3 ↔ paper_14`: cùng dùng formal methods (DL vs OCL) để validate mapping goal↔target-model.
- `paper_1 ↔ paper_6`: paper_6 là cải tiến quy trình xây dựng URN được đề cập trong paper_1.
- `N1 (Nagel et al. 2013) ↔ paper_3/N2`: cùng họ "1-chiều DL/dependency-based checking", khác notation goal (business goal model tự do vs GRL) và khác process đích (BPMN vs case management schema).
- `N2 (Eshuis & Ghose 2021) ↔ N9 (2025)`: hai chiều ngược nhau của cùng cặp goal-model ↔ declarative process — N2 kiểm tra consistency, N9 tổng hợp ngược goal model từ process.
- `N5 ↔ N6`: cùng nhóm tác giả Amyot/Akhigbe, cùng nền URN (GRL↔UCM) — N5 là kiểm tra tĩnh 1 lần, N6 là lan truyền thay đổi (bidirectional change impact) — N6 chính là bước tiếp theo tự nhiên của N5.
- `N4 ↔ N10`: cùng đồng tác giả López (DTU), N4 là phương pháp cụ thể (goal-model-based), N10 là SLR đặt N4 vào bối cảnh 3 thập kỷ nghiên cứu.
- `N7`: không đề xuất phương pháp mới nhưng nên trích dẫn ở phần "motivation/gap" vì nêu đúng câu hỏi nghiên cứu của bạn từ 2009.

---

## 4. Đối chiếu với định hướng nghiên cứu hiện tại của bạn

Theo `Goal_BPMN/research/gap.md` và `gap_assessment.md`, gap bạn đã chốt là:

> *"Chưa có một cách đơn giản, rõ ràng, và có thể validation để biết goal model đã được BPMN bao phủ đầy đủ (goal coverage) hay chưa."*

Đề xuất cụ thể: xây dựng **trace model** (GoalToTaskLink, ActorToLaneLink...) + **bộ OCL constraints** (3-5 rule) để tự động báo lỗi coverage.

→ Đây chính là bài toán mà **Tier 1** ở trên đã và đang giải quyết, với các mức độ hình thức hóa khác nhau:
- DL-based (paper_3, paper_13) — mạnh về suy luận logic nhưng scalability hạn chế.
- Dependency-graph/model-checking (N1, N3) — kiểm tra thứ tự/logic dependency giữa goals khi ánh xạ sang process.
- RT-LTL (paper_11) — verify theo temporal logic.
- OCL-based (paper_14, N2) — gần nhất với cách tiếp cận MDE/DSL/OCL bạn muốn dùng; N2 (Eshuis & Ghose 2021) là case gần nhất về mặt kỹ thuật (structural consistency checking + tool + case study).
- 5-bước thủ công + value propagation (paper_4) — ít hình thức hơn, dễ áp dụng brownfield.
- Compliance ở mức softgoal/non-functional (N4, 2025-2026) — hướng rất mới, nâng abstraction level lên business goal/quality thay vì chỉ structural mapping.

**Khoảng trống còn mở** (theo gap.md mục 5) mà chưa bài nào ở trên giải quyết trọn vẹn:
- **Softgoal/non-functional goal coverage**: N4 đã bắt đầu hướng này (2025-2026) nhưng chưa dùng GRL/OCL theo hướng MDE của bạn — vẫn còn khoảng trống kỹ thuật cụ thể.
- **Bidirectional change impact**: N6 (Alkaf et al. 2019, JSS) đã làm điều này **nhưng trong nội bộ URN (GRL↔UCM)**, chưa làm giữa GRL và BPMN — đây là khoảng trống rất cụ thể và trực tiếp bạn có thể nhắm tới (áp dụng ý tưởng của N6 sang cặp GRL↔BPMN thay vì GRL↔UCM).

---

## 5. Nguồn tài liệu gốc

- Thư mục bài báo đầy đủ: `public/paperSurvey/aligementGOALBPMN/` (17 file, gồm bảng so sánh có sẵn tại `comparison_all_papers.md`). Lưu ý: `paper_16_kaos_dsl_bpmn_consistency.md` (khóa luận UET/VNU) vẫn nằm trong thư mục nhưng đã bị loại khỏi danh sách "bài báo" chính thức (xem mục 2.1).
- Ghi chú gap/định hướng nghiên cứu riêng: `public/Goal_BPMN/research/gap.md`, `gap_assessment.md`, `gap_landscape.md`, `research.md`, `research1.md`, `research2.md`, `research_direction.md`
- Đề xuất nghiên cứu (proposal drafts): `public/Goal_BPMN/proposal/` (P1–P14, xem `INDEX.md`)
- Các bài mới (N1–N11) được tìm qua Google/Google Scholar snippet, ResearchGate, ScienceDirect, SpringerLink, IEEE Xplore, MDPI, arXiv trong phiên làm việc này — **chưa fetch được full-text đầy đủ cho tất cả** (một số bị paywall/403: SoSyM Springer, ScienceDirect trực tiếp). Khuyến nghị: tự truy cập các link ở mục 2.2/2.3 qua tài khoản thư viện trường để xác nhận lại tên tác giả đầy đủ trước khi trích dẫn chính thức trong luận văn, đặc biệt N3 và N9 (tác giả chưa xác nhận qua tìm kiếm).

---

## 6. Mở rộng bài toán: Kiểm chứng ngữ nghĩa tương thích giữa 2 mô hình (Goal↔Process chỉ là một trường hợp cụ thể)

### 6.1 Khung nhìn tổng quát

Bài toán tổng quát mà bạn muốn chứng minh: **cho 2 mô hình M1, M2 mô tả cùng một hệ thống nhưng khác nhau ở cấp độ trừu tượng và/hoặc góc nhìn, làm sao kiểm chứng ngữ nghĩa (semantic conformance) giữa chúng — tức là mọi hành vi/thực thi hợp lệ ở M1 phải tương ứng với một hành vi/cấu trúc hợp lệ ở M2, và ngược lại (hoặc một chiều)?**

Goal model ↔ Business Process model (mục 1–5 ở trên) là **một điểm dữ liệu (data point) cụ thể** trên một trục lớn hơn gồm ít nhất 3 biến thiên độc lập:

| Trục biến thiên | Ý nghĩa | Ví dụ |
|---|---|---|
| **(A) Cấp độ trừu tượng khác nhau** | Cùng một concern/viewpoint nhưng một mô hình cụ thể hơn, chi tiết hơn mô hình kia | Use case (đặc tả) ↔ Design model (hiện thực hóa) — bài JUCS bạn đưa ra |
| **(B) Góc nhìn/viewpoint khác nhau** | Hai mô hình mô tả 2 mối quan tâm (concern) khác nhau của cùng hệ thống, không nhất thiết khác abstraction | Goal (why) ↔ Process (how); Security policy ↔ Process; Structural view ↔ Behavioral view |
| **(C) Ngôn ngữ/formalism hoàn toàn khác nhau (heterogeneous)** | Cần một formalism trung gian chung để so sánh ngữ nghĩa | Bất kỳ cặp (A) hoặc (B) nào biểu diễn bằng 2 DSL/metamodel độc lập |

Trong thực tế 3 trục này thường chồng lấp — ví dụ Goal↔Process vừa khác viewpoint (B) vừa thường khác abstraction (A, vì goal thường trừu tượng hơn task cụ thể).

### 6.2 Bài báo minh họa Trục (A) — khác cấp độ trừu tượng

| Bài báo | Tác giả, Năm, Venue | Cặp mô hình (trừu tượng → cụ thể) | Kỹ thuật |
|---|---|---|---|
| **Checking the Conformance between Models Based on Scenario Synchronization** | Dang, D.-H.; Truong, A.-H.; Gogolla, M. — 2010, **Journal of Universal Computer Science (JUCS)** 16(17):2293–2312 | Use Case model ↔ Design model | Triple Graph Grammars (TGG) + OCL để đồng bộ hóa (synchronize) execution scenarios ở 2 tầng, phát hiện scenario không thể tiếp tục ⇒ vi phạm conformance. **File đã có sẵn:** `public/paperSurvey/16/06/paper/JUCS.md` |
| On Scenario Synchronization | Dang, D.-H. et al. — ATVA 2010 | (tiền thân/hội nghị của bài JUCS ở trên) | Cùng hướng TGG+OCL |
| On integrating triple graph grammars and OCL for model-driven development | Dang, D.-H. — Luận án Tiến sĩ, University of Bremen, 2009 | Khung lý thuyết chung cho TGG+OCL | Nền tảng lý thuyết của bài JUCS |
| **Validating Consistency between a Feature Model and Its Implementation** | Le, D.M.; Lee, H.; Kang, K.C.; Keun, L. — 2013, **ICSR 2013** (Springer LNCS) | Feature model (SPL, commonality & variability ở mức thiết kế) ↔ Implementation/asset code | Kiểm tra C&V (commonality & variability) đặc tả trong feature model có khớp với C&V nhúng trong code (preprocessor directives) hay không |
| An Approach of Conformance Verification between Design Models and Code Based on Abstract Syntax Tree | — 2016, MATEC Web of Conferences | Design model ↔ Source code | So khớp cấu trúc dựa trên AST của code sinh ra so với design model |
| Requirements traceability in model-driven development: Applying model and transformation conformance | (ResearchGate) | Requirements model ↔ các mô hình dẫn xuất theo hierarchy | Khái niệm "conformance" dùng để đơn giản hóa truy vết yêu cầu: nếu target model conform với source model, mọi requirement đã thỏa ở source tự động thỏa ở target |
| **Test generation with inputs, outputs and repetitive quiescence** | Tretmans, J. — 1996, **Software — Concepts and Tools** 17(3):103–120 | Specification model (LTS/IOLTS) ↔ Implementation (black-box) | Bài **kinh điển nền tảng** của lý thuyết **ioco (input-output conformance)** — định nghĩa hình thức quan hệ "implementation conforms to specification" dùng cho model-based testing; hàng nghìn trích dẫn, là gốc rễ lý thuyết cho rất nhiều kỹ thuật conformance-checking sau này (kể cả một số áp dụng cho BPMN/quy trình). |
| **Software Reflexion Models: Bridging the Gap between Design and Implementation** | Murphy, G.C.; Notkin, D.; Sullivan, K.J. — 2001, **IEEE Transactions on Software Engineering (TSE)** 27(4):364–380 | High-level architecture/design model ↔ Source code | Kỹ thuật kinh điển (hàng nghìn trích dẫn) "reflexion model": trích xuất source model từ code, ánh xạ sang high-level model do kỹ sư định nghĩa, rồi hiển thị **convergence/divergence/absence** giữa 2 mô hình — chủ động khai thác thay vì loại bỏ độ lệch (drift). |
| A Comparison of Static Architecture Compliance Checking Approaches | Knodel, J.; Popescu, D. — 2007, WICSA 2007 (IEEE) | (khảo sát 3 kỹ thuật: reflexion models, relation conformance rules, component access rules) | So sánh có hệ thống 3 họ kỹ thuật kiểm tra compliance kiến trúc↔code trên 13 tiêu chí — hữu ích để dẫn nhập phần "các họ kỹ thuật kiểm tra conformance nói chung" trong luận văn. |
| Architecture conformance analysis using model-based testing: A case study approach | Uzun, B.; Tekinerdogan, B. — 2019, **Software: Practice and Experience** (Wiley) 49(3):423–448 | Architectural view/model ↔ Code | Kết hợp reflexion-model-style conformance với model-based testing: sinh test case tự động từ architectural view để kiểm tra ràng buộc kiến trúc có được tuân thủ trong code hay không. |

**Nhận xét:** bài JUCS (Dang, Truong, Gogolla 2010) đặc biệt đáng chú ý vì **Duc-Hanh Dang chính là CBHD (cán bộ hướng dẫn) của khóa luận paper_16** đã bị loại khỏi Tier 1 ở mục 2.1 — tức đây là công trình nền tảng phương pháp luận (TGG+OCL, kiểm chứng conformance bằng đồng bộ hóa scenario) của chính nhóm nghiên cứu tại UET/VNU mà bạn có khả năng đang tiếp cận. Rất nên đọc kỹ bài này và các công trình liên quan của Dang (SEFM 2009, ATVA 2010, luận án 2009) như tài liệu phương pháp luận nền, độc lập với việc dùng khóa luận paper_16 làm case study.

### 6.3 Bài báo minh họa Trục (B) — khác góc nhìn/viewpoint (cùng hoặc gần cấp độ trừu tượng)

| Bài báo | Tác giả, Năm, Venue | Cặp mô hình (viewpoint 1 ↔ viewpoint 2) | Ghi chú |
|---|---|---|---|
| *(toàn bộ Tier 1/2 ở mục 2)* | — | **Goal (why) ↔ Process (how)** | Đã khảo sát chi tiết ở mục 2 |
| **A Method to Ensure Compliance with Attribute and Role Based Access Control Policy for Executing BPMN Models** | Nguyen, D.H.; Le, V.V.; Nguyen, T.H.; **Dang, D.H.** — 2021, **ICSSE 2021** (IEEE) | Security/access-control policy (ABAC/RBAC) ↔ BPMN process model | **Cùng nhóm nghiên cứu Duc-Hanh Dang (UET/VNU)** — một cặp viewpoint khác (an ninh vs vận hành) áp dụng cùng triết lý kiểm tra compliance vào BPMN. Rất đáng tham khảo vì cùng tác giả/nhóm với bối cảnh đề tài của bạn. |
| Consistency Checking of Goal Models and Case Management Schemas (N2, mục 2.2) | Eshuis, Ghose — 2021 | Goal (why) ↔ Case management schema (declarative "how") | Case đặc biệt: process không phải BPMN mà là declarative schema |
| A complete approach for CIM modelling and model formalising (N12, mục 2.2) | Li, Zhou, Gu, Li — 2015, IST | Goal model ↔ Scenario model ↔ Process model | Vừa là instance của Trục (A) (stepwise refinement, càng về sau càng cụ thể hơn) vừa của Trục (B) (goal formalized bằng category theory — một góc nhìn toán học khác hẳn DL/OCL/LTL) |
| Multi-view Consistency in UML | (arXiv survey) | Structural view (class) ↔ Behavioral view (sequence/state machine) trong UML | Survey các kỹ thuật kiểm tra nhất quán đa góc nhìn trong UML |

### 6.4 Khung lý thuyết / khảo sát tổng quát cho bài toán "kiểm chứng tương thích giữa nhiều mô hình" (không giới hạn cặp cụ thể nào)

| Bài báo | Tác giả, Năm, Venue | Đóng góp |
|---|---|---|
| **Comprehensive Systems: A formal foundation for Multi-Model Consistency Management** | Stünkel, P.; König, H.; Lamo, Y.; Rutle, A. — 2021, **Formal Aspects of Computing** (Springer, Vol. 33, 1067–1114) | Đề xuất "comprehensive system" — cấu trúc hình thức biểu diễn quan hệ nhiều-ngôi (không chỉ nhị phân) giữa nhiều mô hình; định nghĩa 3 bước Alignment → Verification → Restoration cho model consistency management nói chung. Framework lý thuyết tổng quát nhất tìm được, có thể dùng để định vị bài toán goal-process của bạn như một trường hợp alignment nhị phân cụ thể. |
| Towards Multiple Model Synchronization with Comprehensive Systems | (cùng nhóm) — 2020 | Bản mở rộng ứng dụng của Comprehensive Systems cho đồng bộ hóa nhiều mô hình |
| **Towards behavioral consistency in heterogeneous modeling scenarios** | Kräuter, T. et al. — 2024, arXiv 2404.12941 / IEEE conference | Căn chỉnh (align) các behavioral metamodel khác nhau bằng cách định nghĩa inter-model relation mang ý nghĩa hành vi, rồi chuyển tất cả về một formalism hành vi chung để model-check tính nhất quán toàn cục — đúng tinh thần "kiểm chứng ngữ nghĩa" bạn muốn tổng quát hóa. |
| Incremental Consistency Checking of Heterogeneous Multimodels | (ResearchGate) | Kiểm tra tăng dần (incremental) khi một trong nhiều mô hình dị chủng thay đổi — liên quan hướng "bidirectional change impact" đã nêu ở mục 4 |
| Multifaceted Consistency Checking of Collaborative Engineering Artifacts | (ResearchGate) | Mở rộng ra ngoài software models — artefact kỹ thuật nói chung |
| How consistency is handled in Model Driven Software Engineering and UML: an expert opinion survey | — 2022, **Software Quality Journal** (Springer) | Khảo sát thực nghiệm 124 chuyên gia — cho số liệu thực tế về việc consistency checking được dùng ở đâu trong MDSE (transformation, verification, comprehension...), hữu ích để trích dẫn phần "motivation" cho thấy đây là nhu cầu thực tế rộng, không riêng gì goal-process. |

### 6.5 Ý nghĩa đối với việc định vị đề tài

Với khung nhìn mở rộng này, bạn có thể định vị luận văn/bài báo của mình theo hướng:

> *"Chúng tôi đề xuất một phương pháp [X] để kiểm chứng ngữ nghĩa tương thích giữa hai mô hình khác cấp độ trừu tượng/góc nhìn nói chung, và minh chứng bằng bài toán cụ thể goal model ↔ business process model — một trong những cặp mô hình phổ biến và có nhu cầu thực tế cao nhất thuộc lớp bài toán này."*

Điều này giúp:
1. Kết nối được với tài liệu phương pháp luận rất mạnh và tổng quát ở mục 6.1/6.4 (TGG+OCL, Comprehensive Systems, heterogeneous behavioral consistency) — không chỉ giới hạn trong tài liệu Tier 1/2 chuyên biệt về goal-process.
2. Tăng tính khái quát hóa (generalizability) của phương pháp đề xuất — một điểm các reviewer thường đánh giá cao.
3. Có sẵn ít nhất 2 case study khác để đối chiếu/mở rộng sau này nếu cần (feature model↔implementation ở mục 6.2, security policy↔BPMN ở mục 6.3) — đúng tinh thần bạn nêu: "goal-process chỉ là một bài toán mà tôi chứng minh".

**Lưu ý về chất lượng nguồn ở mục 6:** phần lớn bài trong mục 6.2–6.4 đạt chuẩn journal/hội nghị uy tín (JUCS, ICSR/Springer, Formal Aspects of Computing, Software Quality Journal, IEEE TSE, Wiley Software: Practice and Experience, Information and Software Technology). Riêng "Towards behavioral consistency..." (Kräuter et al.) hiện có bản arXiv preprint song song bản IEEE conference — nên trích bản IEEE khi có thể. Các mục không có tên tác giả đầy đủ (Multi-view Consistency in UML, Incremental Consistency Checking of Heterogeneous Multimodels, Multifaceted Consistency Checking...) cần tự tra cứu lại DBLP/Scholar trước khi trích dẫn chính thức. **Không có bài nào ở mục 6 là khóa luận/luận văn sinh viên** — hai bài có nguồn gốc "luận án" là luận án Tiến sĩ (Dang, D.-H., Bremen 2009), đáp ứng ràng buộc "PhD trở lên thì dùng được".

### 6.6 Bổ sung vòng research thứ 3 (theo yêu cầu "chỉ PhD trở lên")

| Bài báo | Tác giả, Năm, Venue | Vai trò trong khảo sát |
|---|---|---|
| Test generation with inputs, outputs and repetitive quiescence (lý thuyết **ioco**) | Tretmans, J. — 1996, Software — Concepts and Tools | Nền tảng lý thuyết kinh điển cho mọi quan hệ "conformance" giữa spec và implementation — nên trích khi định nghĩa hình thức khái niệm "conformance" nói chung trong chương Background |
| Software Reflexion Models | Murphy, Notkin, Sullivan — 2001, **IEEE TSE** | Kỹ thuật kinh điển nhất cho conformance kiến trúc↔code — ví dụ mẫu mực cho cách trình bày "convergence/divergence/absence", có thể mượn thuật ngữ này khi trình bày kết quả kiểm tra goal↔process của bạn |
| A Comparison of Static Architecture Compliance Checking Approaches | Knodel, Popescu — 2007, WICSA | Mẫu hình bài "so sánh các họ kỹ thuật" — có thể tham khảo cấu trúc để so sánh các họ kỹ thuật goal↔process (DL vs OCL vs LTL vs Petri net/category theory) |
| Architecture conformance analysis using model-based testing | Uzun, Tekinerdogan — 2019, Software: Practice and Experience | Ví dụ hiện đại kết hợp reflexion-model với model-based testing |
| **A complete approach for CIM modelling and model formalising** (N12) | Li, Zhou, Gu, Li — 2015, Information and Software Technology | **Quan trọng nhất trong vòng này** — xem mục 2.2 và ghi chú N12: một họ kỹ thuật hình thức khác (category theory + Petri net) cho chính bài toán goal→process mà 16 bài gốc chưa đề cập |
