# 2. Công Trình Liên Quan

File LaTeX tương lai: `\section{Công trình liên quan}\label{sec:related-work}`.

**Lịch sử sửa đổi**: bản 1 chia quá mịn (gần 1 bài/nhóm). Bản 2 lập danh sách 16 bài trước rồi
gộp 4 nhóm nhưng vẫn dựa một phần vào tóm tắt tìm kiếm. Bản này (3) đọc trực tiếp
`latex/context/SURVEY_kiem_tra_tuong_thich_goal_process.md` (khảo sát 16 bài gốc + bổ sung N1–N12
+ mở rộng khung nhìn mục 6) và đọc toàn văn 4 bài Tier 1 cốt lõi
(`public/paperSurvey/aligementGOALBPMN/paper_3`, `paper_11`, `paper_4`, `paper_14`), rồi chia lại
thành **5 nhóm theo cơ chế kỹ thuật**, mỗi nhóm 3–5 bài — không còn nhóm 1 bài.

**Phát hiện khi đối chiếu**: `paper_13` (SURVEY ghi tác giả không chắc chắn: "mở rộng từ Ghose/
Koliadis") và `groner2014vadl` dùng ở bản 2 có **tiêu đề trùng tuyệt đối** ("Validation of User
Intentions in Process Orchestration and Choreography") — nhiều khả năng là **cùng 1 bài** (Gröner
et al., *Information Systems* 43, 2014), và SURVEY đã đoán nhầm tác giả theo quán tính nối tiếp
`paper_3`. Đã gộp thành 1 mục duy nhất trong danh sách dưới, **cần tự xác nhận lại tác giả chính
xác qua DBLP trước khi trích dẫn chính thức**.

## Mục tiêu của phần

Cho người đọc bức tranh đầy đủ trước khi thu hẹp về khoảng trống. Chỉ đưa vào nhóm chính các công
trình thuộc đúng loại bài toán **Validation** (kiểm tra 2 mô hình đã tồn tại) theo đúng cách SURVEY
tự phân biệt ở §1 của nó (Transformation / Validation / Mining) — loại transformation-thuần và
mining-ngược ra khỏi nhóm chính, chỉ dùng làm citation bối cảnh ở Introduction/Discussion nếu cần.

## Cấu trúc heading đề xuất

```
\subsection{Danh sách công trình liên quan}
\subsection{Suy luận tĩnh trên hình thức luận chung}
\subsection{Ngữ nghĩa vận hành theo vết thực thi}
\subsection{Căn chỉnh định lượng và liên tục}
\subsection{Đối chiếu với ràng buộc phẳng bên ngoài}
\subsection{Đặc tả và kiểm chứng cấu trúc vai trò tổ chức}
\subsection{Khoảng trống còn lại}
```

7 subsection: 1 danh sách tổng + 5 nhóm cơ chế (A–E) + 1 tổng hợp khoảng trống.

## 2.1. Danh sách công trình liên quan

**Nguyên tắc lọc**: chỉ giữ bài thuộc bài toán Validation (goal↔process, đã tồn tại cả 2 phía) hoặc
bài thuộc nhóm đặc tả cấu trúc tổ chức (RQ1). Loại khỏi bảng — không tính vào 20 bài — các bài
Transformation-thuần (`paper_1` URN survey, `paper_2` Stra2Bis, `paper_6` URN improved, `paper_8`
GoBIS), Reasoning-nội-bộ-goal-model (`paper_5`, `paper_7`, `paper_9`, `paper_10`), và Mining/
Synthesis-chiều-ngược (`paper_12`, N9 — N9 **vẫn giữ lại** vì mang tính định lượng gần nhóm C, xem
ghi chú ở 2.4). `yu1997towards`/`omg2013bpmn`/`gogolla2007use` vẫn để riêng làm Background, không
tính vào Related Work.

| # | Khoá đề xuất | Tác giả, tiêu đề (rút gọn), venue | Nguồn | Nhóm |
|---|---|---|---|---|
| 1 | `ghose2007validation` | Ghose, Koliadis — Validation of User Intentions in Process Models — BPM 2007 | `paper_3` (đọc toàn văn) | A |
| 2 | `groner2014vadl` | Gröner và cộng sự (SURVEY ghi chưa chắc) — Validation of User Intentions in Process Orchestration and Choreography — Information Systems 43, 2014 | `paper_13` (đọc toàn văn) + đối chiếu `doc/paper/conformance-istar-bpmn2.md` | A |
| 3 | `guizzardi2014vemi` | Guizzardi và cộng sự — Verifying Goal-Oriented Specifications Used in MDD Processes (VeMI) — CAiSE 2014 / Requirements Engineering Journal | `paper_14` (đọc toàn văn) | A |
| 4 | `eshuis2021consistency` | Eshuis, Ghose — Consistency Checking of Goal Models and Case Management Schemas — BPM 2021 Forum, LNBIP | SURVEY §2.2 (N2) | A |
| 5 | `li2015cim` | Li, Zhou, Gu, Li — A complete approach for CIM modelling and model formalising (Goal→Scenario→Process, category theory + Petri net + QVTo) — Information and Software Technology 65, 2015 | SURVEY §2.2 (N12) | A |
| 6 | `koliadis2006goalbpm` | Koliadis, Ghose — Relating Business Process Models to Goal-Oriented Requirements Models in KAOS (GoalBPM) — APCCM 2006 | `paper_11` (đọc toàn văn) | B |
| 7 | `dang2010jucs` | Dang, Truong, Gogolla — đồng bộ kịch bản use case/design model qua snapshot pattern — J.UCS 16(17), 2010 | `doc/paper/conformance-istar-bpmn2.md` + SURVEY §6.2 | B |
| 8 | `caballero2026alig` | Caballero-Villalobos và cộng sự — Aligning processes with high-level requirements: goal-model-based compliance checking — Information and Software Technology 196, 2026 | `doc/paper/conformance-istar-bpmn2.md` | B |
| 9 | `nagel2013consistency` | Nagel, Gerth, Engels, Post — Ensuring Consistency among Business Goals and Business Process Models — IEEE EDOC 2013 | SURVEY §2.2 (N1) | B |
| 10 | `n3_2019_reqdepgraph` | Tác giả chưa xác nhận — Consistency Verification between Collaborative Business Processes and Requirements — IEEE 2019 | SURVEY §2.2 (N3, tự ghi "cần tra IEEE Xplore") | B |
| 11 | `guizzardi2012align` | Guizzardi, Reis — A Method to Align Goals and Business Processes — CAiSE 2012 | `paper_4` (đọc toàn văn) | C |
| 12 | `shamsaei2011kpi` | Shamsaei, Pourshahid, Amyot — Business process compliance tracking using key performance indicators (GRL/URN) — BPM Workshops 2011 | Tra cứu `WebSearch` phiên trước | C |
| 13 | `n9_2025_goalsynthesis` | Tác giả chưa xác nhận — Synthesizing goal models from declarative data-centric process models — 2025 | SURVEY §2.2 (N9, tự ghi "tác giả chưa xác nhận đầy đủ") | C |
| 14 | `governatori2006compliance` | Governatori, Milosevic, Sadiq — Compliance checking between business processes and business contracts — EDOC 2006 | Tra cứu `WebSearch` phiên trước | D |
| 15 | `ghose2007auditing` | Ghose, Koliadis — Auditing Business Process Compliance — ICSOC 2007 | Tra cứu `WebSearch` phiên trước | D |
| 16 | `awad2009visualization` | Awad, Weske — Visualization of Compliance Violation in Business Process Models — BPM Workshops 2009 | Tra cứu `WebSearch` phiên trước | D |
| 17 | `aalst2005conformance` | van der Aalst, de Medeiros — Process mining and security: detecting anomalous process executions and checking process conformance — ENTCS, 2005 | Tra cứu `WebSearch` phiên trước | D |
| 18 | `hubner2002moise` | Hübner, Sichman, Boissier — A model for the structural, functional, and deontic specification of organizations in MAS (MOISE) — SBIA 2002 | Tra cứu `WebSearch` phiên trước + `doc/reference/acl.yaml` | E |
| 19 | `hubner2007moiseplus` | Hübner, Sichman, Boissier — Developing organised multi-agent systems using the MOISE+ model — IJAOSE 1(3/4), 2007 | Tra cứu `WebSearch` phiên trước | E |
| 20 | `ray2004rbacuml` | Ray và cộng sự — Using UML to visualize role-based access control constraints — SACMAT 2004 | Tra cứu `WebSearch` phiên trước | E |

Ghi chú độ tin cậy: mục 2, 4, 5, 9, 10, 12, 13 lấy từ tóm tắt (SURVEY hoặc kết quả tìm kiếm), chưa
fetch PDF gốc — mục 10 và 13 SURVEY tự thừa nhận **chưa xác nhận đầy đủ tác giả**, cần tự tra DBLP/
IEEE Xplore trước khi đưa vào `references.bib`. Mục 1, 6, 11, 3 đã đọc toàn văn (qua bản tóm tắt
chi tiết trong `paperSurvey`, không phải PDF gốc trực tiếp) — độ tin cậy cao hơn các mục còn lại.

## 2.2. Suy luận tĩnh trên hình thức luận chung

- **Đại diện** (5 bài): `ghose2007validation`, `groner2014vadl`, `guizzardi2014vemi`,
  `eshuis2021consistency`, `li2015cim`.
- **Cơ chế chung**: dịch cả 2 mô hình (hoặc cả chuỗi mô hình) sang **1 hình thức luận chung** —
  Description Logics/OWL (`ghose2007validation`, `groner2014vadl`), metamodel tích hợp có ràng buộc
  OCL (`guizzardi2014vemi`, tương tự `eshuis2021consistency`), hay cấu trúc đại số/phạm trù
  (`li2015cim` dùng category theory cho goal model, Petri net cho process, QVTo cho ánh xạ) — rồi
  dùng 1 bộ máy suy luận/truy vấn tự động (reasoner DL như Pellet, engine OCL, hoặc chứng minh đại
  số) để phân loại tính nhất quán. **Không mô phỏng thực thi** — thuần suy luận tĩnh trên cấu trúc
  đã dịch.
- **Điểm mạnh**: tự động hoá hoàn toàn, có bảo đảm lý thuyết (DL: decidable reasoning, đầy đủ toàn
  bộ state space không cần liệt kê; OCL: tái dùng công cụ EMF/USE trưởng thành); `ghose2007validation`
  cho ra **bảng tra cứu sẵn** (Realization Equivalence Table 5×3) dùng được mà không cần chạy tool
  lại mỗi lần; `guizzardi2014vemi` phát hiện lỗi **trước khi biến đổi mô hình** (pre-transformation),
  chi phí sửa thấp hơn nhiều so với phát hiện sau khi đã sinh code/thực thi.
- **Hạn chế**: `ghose2007validation`/`groner2014vadl` chỉ xử lý AND/IOR/XOR decomposition, chưa mã
  hoá contribution link (Make/Help/Hurt) và gặp vấn đề undecidability tiềm năng với loop;
  `guizzardi2014vemi` gắn chặt với 1 MDD platform cụ thể (Integranova), chỉ kiểm **cấu trúc**, không
  phát hiện xung đột ở mức business logic (goal conflict, dependency cycle theo nghĩa nghiệp vụ);
  `li2015cim` dùng bộ máy toán học nặng (category theory), khó tái dùng trực tiếp cho practitioner.
  **Không nhóm nào xét ràng buộc tổ chức/vai trò.**
- **Vị trí đặc biệt**: đây là nhóm **gần nhất về mặt kỹ thuật** với phương pháp của bài báo — cùng
  triết lý "dịch về 1 miền hình thức chung rồi truy vấn" (ACL→USE/OCL ở Phần 3.6). Khác biệt cốt
  lõi cần nhấn ở Thảo luận (Phần 4): `guizzardi2014vemi` xác minh **trước** biến đổi, trên 1 model
  tĩnh; phương pháp của bài báo xác minh dọc theo **vết thực thi cụ thể** (Phần 3.7) — bổ sung
  chiều "động" mà cả nhóm A đều không có.

## 2.3. Ngữ nghĩa vận hành theo vết thực thi

- **Đại diện** (5 bài): `koliadis2006goalbpm`, `dang2010jucs`, `caballero2026alig`,
  `nagel2013consistency`, `n3_2019_reqdepgraph`.
- **Cơ chế chung**: định nghĩa 1 khái niệm **vết/trajectory/marking** của process theo thời gian —
  effect annotation + trích trajectory + đối chiếu RT-LTL (`koliadis2006goalbpm`), snapshot pattern
  đồng bộ kịch bản (`dang2010jucs` — nền tảng đề xuất, xem Phần 1), goal marking + LTS + duyệt BFS
  (`caballero2026alig`), kiểm thứ tự/phụ thuộc logic giữa goal khi ánh xạ sang process
  (`nagel2013consistency`), model checking trên Requirement Dependency Graph mở rộng
  (`n3_2019_reqdepgraph`) — rồi tích luỹ hiệu ứng dọc theo vết đó và đối chiếu với đặc lời văn thời
  gian/logic của goal.
- **Điểm mạnh**: khác nhóm A ở chỗ có khái niệm **thứ tự và tích luỹ nhân quả** qua nhiều bước, cho
  counterexample cụ thể (đường đi nào vi phạm), `caballero2026alig` có chứng minh đúng đắn/đầy đủ
  hình thức cho thuật toán.
- **Hạn chế**: `koliadis2006goalbpm` **hoàn toàn thủ công** (effect annotation, trích trajectory,
  kiểm RT-LTL đều làm tay, tác giả tự nhận không scalable); các phương pháp dựa LTS
  (`caballero2026alig`) đối mặt bùng nổ trạng thái khi có loop; tự phê phán nội tại của
  `caballero2026alig` (buộc mọi nhánh dẫn tới thoả mãn có thể "vô lý nghiệp vụ" — xem
  `doc/paper/conformance-istar-bpmn2.md` §5.3); không nhóm nào xét ràng buộc tổ chức.
- Đây cũng là nhóm chứa `dang2010jucs` — bài đã dùng làm "nền tảng đề xuất" ở Introduction (Phần
  1, Đoạn 3). Ở đây thảo luận **kỹ thuật** (so với các bài cùng cơ chế), khác vai trò "định vị nền
  tảng phương pháp luận" ở Introduction — cố ý không lặp lại nội dung, chỉ bổ sung góc nhìn kỹ
  thuật khi đặt cạnh `koliadis2006goalbpm`/`caballero2026alig`.

## 2.4. Căn chỉnh định lượng và liên tục

- **Đại diện** (3 bài): `guizzardi2012align`, `shamsaei2011kpi`, `n9_2025_goalsynthesis`.
- **Cơ chế chung**: thay vì trả lời nhị phân đạt/không đạt, tính **1 điểm số/mức độ liên tục** —
  propagation có trọng số theo path (`guizzardi2012align`: AND=min, OR=max, satisfaction ∈[0,1]
  theo từng execution path), chỉ số KPI theo dõi qua GRL/URN (`shamsaei2011kpi`), hoặc tổng hợp
  ngược goal model từ milestone của process khai báo (`n9_2025_goalsynthesis` — chiều ngược nhưng
  cùng bản chất "định lượng/liên tục", không nhị phân).
- **Điểm mạnh**: `guizzardi2012align` áp dụng được cho **brownfield** (cả 2 mô hình đã tồn tại độc
  lập, không cần quan hệ sinh 1 chiều — đúng đối lập với nhóm Transformation đã loại ở 2.1), có gap
  analysis tự động (phát hiện goal bị bỏ sót/activity thừa) đi kèm điểm số; kiểm chứng bằng thực
  nghiệm có kiểm soát (14 sinh viên, cho kết quả tốt hơn ad-hoc).
- **Hạn chế**: gán trọng số hoàn toàn chủ quan, chưa có tool tự động hoá (`guizzardi2012align` tự
  nhận "manual, không scalable"); ngưỡng KPI cũng mang tính chủ quan tương tự; không nhóm nào xét
  ràng buộc tổ chức.

## 2.5. Đối chiếu với ràng buộc phẳng bên ngoài

- **Đại diện** (4 bài): `governatori2006compliance`, `ghose2007auditing`, `awad2009visualization`,
  `aalst2005conformance`.
- **Cơ chế chung**: "yêu cầu" phía đối chiếu là 1 đặc tả **phẳng, phi cấu trúc ý định** — nghĩa vụ
  hợp đồng (`governatori2006compliance`), quy định/luật (`ghose2007auditing` — lưu ý: cùng cặp tác
  giả Ghose/Koliadis với `ghose2007validation` ở nhóm A nhưng là 2 bài khác nhau, khác venue, khác
  câu hỏi), mẫu LTL/BPMN-Q (`awad2009visualization`), hoặc log thực thi thật
  (`aalst2005conformance`) — không có actor, AND/OR refinement, hay contribution link như i*/GRL.
- **Điểm mạnh**: kỹ thuật trưởng thành (LTL model checking, replay log, KPI), đã ứng dụng công
  nghiệp rộng cho bài toán compliance nói chung.
- **Hạn chế** — mấu chốt để phân biệt với A/B/C: không công trình nào ở đây đối chiếu process với
  **1 goal model có cấu trúc ý định đầy đủ** (actor, refinement, contribution) — "yêu cầu" luôn bị
  phẳng hoá thành rule/KPI/log trước khi kiểm, mất đi ngữ nghĩa AND/OR/Make/Break đặc thù của goal
  model.

## 2.6. Đặc tả và kiểm chứng cấu trúc vai trò tổ chức

- **Đại diện** (3 bài): `hubner2002moise`, `hubner2007moiseplus`, `ray2004rbacuml` — không đổi so
  với bản trước.
- **Cơ chế/điểm mạnh/hạn chế**: giữ nguyên nội dung đã có — xem lý do đầy đủ ở bản 2 (không lặp lại
  ở đây để tránh trùng lặp); tóm tắt: đặc tả Role/Group/cardinality/compatibility hình thức tốt,
  nhưng tách rời hoàn toàn khỏi goal/process của cùng hệ thống.

## 2.7. Khoảng trống còn lại

- Đối chiếu lại đúng 2 RQ (Phần 1): nhóm 2.2–2.5 (A–D) đều thuộc RQ2 nhưng theo 4 cơ chế khác nhau
  (tĩnh/động/định lượng/phẳng) — không nhóm nào dùng **đồng thời** (a) goal model có cấu trúc ý
  định AND/OR/contribution làm "yêu cầu", (b) 1 miền ngữ nghĩa thực thi được để kiểm dọc theo vết
  thực thi thật, và (c) ràng buộc cấu trúc tổ chức. Nhóm 2.6 (E) trả lời RQ1 nhưng tách biệt hoàn
  toàn khỏi A–D.
- Câu chốt: khoảng trống nằm ở **giao điểm** của (A ∪ B ∪ C ∪ D) và E — chưa có công trình nào trả
  lời đồng thời RQ1 và RQ2 trên **cùng một mô hình hệ thống cụ thể**. Bài báo lấp khoảng trống này
  bằng cách kế thừa đúng "khung kịch bản/snapshot" của `dang2010jucs` (nhóm B) cho phần đồng bộ,
  đúng triết lý "dịch về 1 hình thức luận chung rồi truy vấn" của `guizzardi2014vemi` (nhóm A) cho
  phần cấu trúc — nhưng khác cả hai ở việc bổ sung **chiều tổ chức** (như E) và kiểm tra dọc theo
  **vết thực thi cụ thể** (như B) trên cùng một hệ thống đối tượng USE duy nhất.

## Nguồn tham chiếu / cơ sở

- Danh sách 2.1 + nội dung chi tiết nhóm A/B/C: đọc trực tiếp
  `latex/context/SURVEY_kiem_tra_tuong_thich_goal_process.md` (toàn văn, 211 dòng) và toàn văn 4
  file `paper_3`, `paper_11`, `paper_4`, `paper_14` tại
  `academic-research-skills-main/public/paperSurvey/aligementGOALBPMN/` (working directory ngoài
  repo chính, xem đường dẫn đầy đủ trong lịch sử hội thoại).
- Nhóm D/E: giữ nguyên nguồn `WebSearch` từ phiên làm việc trước (xem bản 2).
- Phát hiện trùng `paper_13`/`groner2014vadl`: đối chiếu tiêu đề chuỗi ký tự giống hệt nhau giữa
  `paper_13_validation_orchestration_choreography.md` dòng 1 và ghi chú tác giả trong
  `doc/paper/conformance-istar-bpmn2.md` dòng 9–13 (mục lưu ý đặt lại tên đầu file đó).

## Ghi chú khi viết prose thật

- Bắt buộc tự fetch DBLP/IEEE Xplore cho `n3_2019_reqdepgraph` và `n9_2025_goalsynthesis` (2 bài
  SURVEY tự nhận chưa xác nhận tác giả đầy đủ) trước khi đưa vào `references.bib` — nếu không tìm
  lại được nguồn xác thực, cân nhắc bỏ khỏi bản thảo chính thức thay vì trích dẫn mập mờ.
- Bắt buộc tự xác nhận qua DBLP xem `paper_13` và `groner2014vadl` có thật sự là cùng 1 bài hay là
  2 bài khác nhau trùng tiêu đề tình cờ — nếu là 2 bài khác nhau, tách lại thành 2 mục riêng trong
  nhóm A (không ảnh hưởng cấu trúc nhóm, chỉ đổi số lượng đại diện từ 5 lên 6).
- Văn phong 2.2–2.6: nêu cơ chế chung trước, `\cite{}` từng đại diện ngay khi nhắc tên, rồi mới
  điểm mạnh/hạn chế — giữ nguyên nguyên tắc đã đặt ra ở bản 2.
- 2.7 là đoạn duy nhất dùng giọng khẳng định mạnh.
