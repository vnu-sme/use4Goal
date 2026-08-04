# Towards Goal-oriented Process Mining

**Tác giả:** Mahdi Ghasemi  |  **Năm:** 2019 (đề xuất ban đầu); 2022 (thuật toán mở rộng)  |  **Venue:** CAiSE 2019 / Information Systems Journal

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực giao thoa giữa **Process Mining** và **Goal-oriented Requirements Engineering (GORE)**. Đây là sự kết hợp novel của hai lĩnh vực thường không được nghiên cứu cùng nhau.

**Process Mining** là kỹ thuật khai thác mô hình quy trình từ event logs — nhật ký sự kiện ghi lại mọi hoạt động trong hệ thống thông tin (timestamp, actor, activity type). Các tools như ProM hay Disco có thể tự động discover process model từ logs. Tuy nhiên, Process Mining truyền thống chỉ trả lời các câu hỏi: *what* (hoạt động gì xảy ra), *when* (khi nào), *where* (ở đâu), *who* (ai làm). Câu hỏi *why* (tại sao) — mục đích chiến lược, goals của tổ chức — hoàn toàn vắng mặt.

**Goal Modeling** (i*, GRL, KAOS) trả lời câu hỏi *why* xuất sắc. Nhưng nó làm việc với mô hình tĩnh, không connected với data thực tế về hành vi của hệ thống.

**Khoảng trống**: Không có framework nào kết nối hai lĩnh vực này — dẫn đến:
- Process Mining discoveries không phân biệt "trace đạt goal" vs "trace không đạt goal" → mô hình lẫn lộn good behaviors và bad behaviors
- Goal models được evaluate trên mô hình tĩnh, không phản ánh behavior thực tế từ logs
- Practitioners không biết process thực tế có phục vụ strategic goals hay không

### Bài toán cụ thể

Bài báo giải quyết hai sub-problems:

**GoPED (Goal-oriented Process Enhancement and Discovery)**: Làm thế nào để discover process model từ event logs mà chỉ giữ lại traces thỏa mãn goals? Nếu ta chỉ mine "good behaviors" (traces của cases đạt goals), model sẽ đơn giản hơn và bám sát strategy hơn.

**GoCC (Goal-oriented Conformance Checking)**: Làm thế nào để kiểm tra liệu process thực tế (từ logs) có tuân thủ goals đã đặt ra không, và học hỏi từ deviations để cải thiện goal model và process design?

- **Input (GoPED)**: Raw event log + goal model (GRL) + goal criteria/KPIs cho mỗi trace
- **Output (GoPED)**: Process model đơn giản, bám sát goals, discovered từ tập traces "tốt"
- **Input (GoCC)**: Prescribed process model + discovered process model + goal model
- **Output (GoCC)**: Báo cáo deviations, diagnostics, đề xuất cập nhật goal model hoặc process design

### Tại sao khó

1. **Event logs thiếu goal attributes**: Logs thường chỉ có timestamps, activity names, và case IDs. Goal satisfaction level của một case không có sẵn — phải được computed từ KPIs, và KPIs không phải lúc nào cũng được log.

2. **Over-fitting risk**: Nếu lọc traces quá khắt khe (chỉ giữ traces đạt tất cả goals), có thể còn quá ít traces → discovered model không representative.

3. **Multi-goal tension**: Optimizing trace selection theo goal A có thể contradict goal B. Cần tradeoff mechanism.

4. **Spaghetti models problem**: Process Mining từ full logs tạo ra "spaghetti models" — quá phức tạp, không interpretable, và không aligned với strategy. GoPED phải solve both problems: simpler AND goal-aligned.

### Đóng góp của bài

1. **Framework lý thuyết** kết nối Process Mining và Goal Modeling — lần đầu tiên trong literature
2. **GoPED**: 5 selection strategies để lọc traces theo goal criteria trước khi discovery
3. **GoCC**: vòng lặp conformance checking + crowdsourced requirements từ deviations
4. **Design Science Research methodology**: framework được phát triển qua iterations với stakeholders thực tế

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: Traditional Process Mining (ProM, Disco)

ProM và Disco là state-of-the-art tools cho process discovery, conformance checking, và enhancement. Mạnh về algorithms và scalability. Nhược điểm căn bản: treat tất cả traces như nhau — không có concept về "trace đạt goal" hay "trace không đạt goal". Mọi behavior, tốt hay xấu, đều được include trong discovered model.

### Hướng 2: Goal Modeling (i\*, GRL, KAOS)

Đã thảo luận trong nhiều bài trước. Mạnh cho requirements modeling và satisfaction analysis. Nhược điểm trong context này: không connected với real data. Goal satisfaction được evaluated trên static models, không reflect what actually happens in the system.

### Hướng 3: Conformance Checking (Alignment-based)

Alignment-based conformance checking (van der Aalst et al.) so sánh event log traces với prescribed process model. Tính fitness và precision. Không xét goals — một process có thể perfectly conform với prescribed model nhưng vẫn không đạt strategic goals.

### Hướng 4: Goal-Process Alignment (paper_3, paper_11)

Ghose & Koliadis (paper_3), Koliadis & Ghose (paper_11), Guizzardi et al. (paper_4) kiểm tra nhất quán goal↔process ở model level. Không dùng event logs — không biết behavior thực tế có đạt goals không, chỉ biết mô hình thiết kế có aligned không.

### Hướng 5: KPI-driven Process Mining

Một số công trình filter traces dựa trên KPIs (key performance indicators). Gần với GoPED nhưng thiếu goal model layer — KPIs không có semantic linking với strategic goals, không có reasoning về contribution/correlation.

### Khoảng trống (Research Gap)

Chưa có framework nào kết nối Process Mining và Goal Modeling để: lọc traces theo goal satisfaction trước discovery, và kiểm tra conformance với goal requirements. GoPED + GoCC lấp đầy khoảng trống này.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

**GoPED intuition**: "Garbage in, garbage out." Nếu ta feed toàn bộ log (kể cả traces không đạt goals) vào discovery algorithm, ta sẽ get một model phản ánh tất cả behaviors kể cả bad behaviors. Thay vào đó, hãy lọc log trước: chỉ giữ traces của cases thực sự đạt goals → discovered model sẽ là "best practice model" aligned với goals.

**GoCC intuition**: Deviations từ prescribed process không nhất thiết là lỗi. Một số deviations có thể là behavior tốt mà designers chưa nghĩ đến khi viết process. GoCC phân biệt "harmful deviations" (cần fix) vs "beneficial deviations" (cần incorporate vào goal model và process design) — đây là "crowdsourcing requirements from practice".

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**GoPED Pipeline:**

1. **Data Preprocessing**: Extract traces từ event log. Tính goal satisfaction level cho mỗi trace/case dựa trên KPIs. Annotate traces với goal attributes.

2. **Selection Algorithms (5 strategies)**:
   - Strategy 1: Lọc traces đạt threshold của một goal cụ thể
   - Strategy 2: Lọc traces đạt threshold của nhiều goals (conjunction)
   - Strategy 3: Lọc để đảm bảo aggregate satisfaction của một goal qua tất cả cases đạt threshold
   - Strategy 4: Lọc để đảm bảo aggregate satisfaction của nhiều goals (multi-goal optimization)
   - Strategy 5: Lọc toàn diện — tất cả goals × tất cả cases (NP-hard, cần approximation)

3. **Process Discovery**: Áp dụng discovery algorithm (α-algorithm, Inductive Miner, etc.) trên filtered traces. Output: process model đơn giản, aligned với goals.

4. **Stakeholder satisfaction analysis**: Propagate KPIs từ filtered traces lên goal model để visualize achievement levels.

**GoCC Pipeline:**

1. **LTL Checking**: Dùng LTL (Linear Temporal Logic) checkers để identify deviations của discovered model (từ logs) so với prescribed process model và goal requirements.

2. **Deviation Classification**:
   - Harmful: vi phạm goal requirements → cần sửa process
   - Neutral: variation không affect goals
   - Beneficial: deviation đạt goals tốt hơn prescribed process → cơ hội cải thiện

3. **What-if Analysis**: Forward propagation KPIs từ logs lên goal model. "Nếu chúng ta incorporate deviation D, impact lên high-level goals là gì?"

4. **Crowdsourced Requirements**: Deviations có tần suất cao và beneficial → likely là intentional behaviors của practitioners. Extract thành requirements mới, cập nhật goal model, thiết kế lại process.

### 3.3 Các Thành Phần Chính

**Event Log với Goal Annotations:**
Bước preprocessing quan trọng nhất: link log traces với goal satisfaction data. Thường cần external data sources (outcome databases, CRM systems) để compute KPI values per case.

**5 Selection Strategies:**
Spectrum từ individual-case/single-goal (Strategy 1) đến aggregate/multi-goal (Strategy 4 và 5). Cho phép linh hoạt: Strategy 1 cho use cases đơn giản, Strategy 4-5 cho scenarios phức tạp cần multi-objective tradeoffs.

**LTL Checkers:**
Sử dụng LTL formulas để express goal requirements (ví dụ: "nếu order được placed thì payment phải occur before delivery"). Check từng trace trong log xem có vi phạm LTL formula không.

**Crowdsourcing Mechanism:**
Novel concept: deviations xảy ra với tần suất cao không phải là random noise — chúng reflect actual practice của practitioners. GoCC provides mechanism để extract, analyze, và potentially incorporate chúng vào official process design.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

**Bệnh viện scenario** (hypothetical example từ planned case studies):

**Situation**: Hospital đang mine event log từ Electronic Health Record (EHR) system. Goal model có goals: G1 "Patient Safety" (KPI: adverse events = 0), G2 "Timely Treatment" (KPI: door-to-needle time < 90 min), G3 "Cost Efficiency" (KPI: average cost per patient < $5,000).

**GoPED application**:
- Raw log: 10,000 patient cases
- Selection Strategy 3: Lọc để aggregate G2 satisfaction ≥ 90% (90% of cases finish in < 90 min)
- Kết quả: 8,500 cases pass filter
- Discovery trên 8,500 cases → model chỉ chứa "fast treatment paths", loại bỏ outlier/delay paths
- Model này phản ánh "best practice for timely treatment" — đơn giản hơn và aligned với G2

**GoCC application**:
- Prescribed model (official process): A → B → C → D → E
- Discovered model (from logs): A → B → [skip C] → D → E (C bị bỏ qua ~30% cases)
- LTL check: G1 goal rule "C must occur before D" → violation detected in 30% traces
- Deviation classification: C là "Blood Culture" test. Khi bỏ qua C, adverse events tăng lên → harmful deviation
- Action: Enforce C in process, thêm gate "C must complete before D"

**Alternate scenario**: Activity X không có trong prescribed model nhưng xuất hiện trong 40% high-quality traces (G1=0, G2≤70min). → Beneficial deviation: X có thể là informal best practice. → Crowdsource requirements: investigate X, possibly add to official process.

### 3.5 Điểm Mới So Với Trước

Ba điểm đột phá:

1. **First integration of goal-oriented reasoning into process mining pipeline**: Không phải post-hoc analysis (mine first, then check goals) mà là goal-informed discovery (filter by goals first, then mine).

2. **Crowdsourcing requirements from deviations**: Biến deviations từ "problem" thành "potential requirements" — điều này phản ánh thực tế rằng practitioners often know better than designers what works.

3. **5-strategy selection spectrum**: Từ simple single-case/single-goal đến complex multi-case/multi-goal — covering full range of use cases.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Process Mining khai thác mô hình quy trình từ event logs nhưng không có khái niệm về mục tiêu chiến lược (goals). Goal modeling captures intentions nhưng không kết nối với behavior thực tế. Bài báo này đề xuất Goal-oriented Process Mining — framework tích hợp Process Mining và Goal-oriented Requirements Engineering. Framework gồm hai thành phần: GoPED (Goal-oriented Process Enhancement and Discovery) lọc event log trước discovery để chỉ giữ traces thỏa mãn goals, tạo ra process model đơn giản và aligned với strategy; và GoCC (Goal-oriented Conformance Checking) kiểm tra deviations của process thực tế so với goal requirements, phân biệt harmful deviations (cần sửa) và beneficial deviations (crowdsourced requirements), và propagate KPIs lên goal model cho what-if analysis. GoPED cung cấp 5 selection strategies từ đơn giản (single-case/single-goal threshold) đến phức tạp (NP-hard multi-case/multi-goal optimization). Framework được phát triển theo Design Science methodology với planned case studies tại Bệnh viện Montfort (Canada) và Western Norway University.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Trạng thái validation** (2019 bài gốc):
Đây là framework proposal paper — thực nghiệm đầy đủ chưa được reported tại thời điểm publication. Tác giả present các planned case studies:
- Nhật ký sự kiện từ tổ chức tài chính và bệnh viện ở Hà Lan
- Controlled experiment với sinh viên
- Bệnh viện Montfort (Canada)
- Western Norway University of Applied Sciences

**Kết quả từ 2022 extension paper:**
- Constraint-based optimization cho selection algorithms (đặc biệt Strategy 4-5): feasible và scalable với reasonable time
- Multi-goal optimization: solved successfully về mặt kỹ thuật

**Kết quả kỳ vọng từ full implementation:**
- Process models discovered từ goal-filtered traces sẽ đơn giản hơn traditional discovery
- Models sẽ phản ánh best practices aligned với KPIs
- GoCC sẽ phát hiện deviations có actionable insights

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Thiếu dữ liệu thực tế**: Event logs hiếm khi có sẵn goal-related attributes hay KPI per trace. Cần preprocessing phức tạp để compute goal satisfaction từ external data sources.

2. **Over-fitting risk**: Lọc quá khắt khe → quá ít traces → discovered model không representative. Cần balance giữa goal satisfaction threshold và data sufficiency.

3. **Computational complexity**: Strategy 5 (toàn diện) là NP-hard. Với logs lớn, cần approximation algorithms hoặc heuristics.

4. **Concept drift**: GoCC giả định behavior quá khứ (trong log) là informative cho tương lai — nhưng nếu environment thay đổi, past "good behaviors" có thể không còn optimal.

5. **Validation gaps**: Bài 2019 thiếu empirical validation đầy đủ — chỉ có framework proposal. Full validation cần large-scale industry studies.

**Hướng nghiên cứu tiếp theo:**
- Thuật toán hiệu quả hơn cho multi-goal trace selection
- Prototype tool tự động hóa GoPED + GoCC pipeline
- Integration với reinforcement learning cho dynamic goal-oriented selection
- Full validation tại Bệnh viện Montfort và Western Norway University
- Extension cho streaming process mining (real-time goal-oriented discovery)

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất Goal-oriented Process Mining — framework tích hợp mới kết nối Process Mining với Goal Modeling. GoPED giải quyết "spaghetti model" problem bằng cách filter traces theo goal satisfaction trước discovery. GoCC converts deviations từ "problems" thành "potential requirements" thông qua crowdsourcing mechanism. Framework được phát triển theo Design Science methodology. Hạn chế chính là thiếu empirical validation đầy đủ — đây là framework proposal với planned case studies. Computational complexity của multi-goal optimization (NP-hard) là thách thức kỹ thuật cần giải quyết.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** concept về **crowdsourcing requirements from deviations** — ý tưởng rằng practitioners thường biết better practices hơn designers, và những deviations có tần suất cao thường là intentional improvements, không phải violations. Nếu đúng, GoCC có thể là mechanism để "harvest" tacit knowledge từ data và incorporate vào formal requirements — một loop feedback từ practice back to design mà hiện tại không có framework nào capture được.
