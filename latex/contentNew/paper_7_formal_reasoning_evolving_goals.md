# Formal Reasoning for Analyzing Goal Models that Evolve over Time

**Tác giả:** Marin Litoiu, Jennifer Horkoff, et al. (nhóm York University / University of Toronto)  |  **Năm:** 2019  |  **Venue:** Requirements Engineering Conference (RE 2019)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Goal-oriented Requirements Engineering (GORE)** — cụ thể là phân tích và suy luận về goal models trong bối cảnh **thay đổi theo thời gian**. Đây là một hướng nghiên cứu quan trọng nhưng còn thiếu sự quan tâm tương xứng: trong khi các hệ thống phần mềm và tổ chức liên tục thay đổi mục tiêu, các công cụ GORE hiện tại chủ yếu chỉ hỗ trợ phân tích tại một thời điểm tĩnh.

Tại sao điều này quan trọng? Hãy xem xét một dự án đô thị như xây dựng làn đường xe đạp trong thành phố. Ở năm 2001, goal "Promote Cycling" có thể được ủng hộ mạnh. Sau một cuộc bầu cử vào năm 2003, goal "Minimize Traffic Disruption" có thể trở nên quan trọng hơn và conflict với goal trước. Đến năm 2006, một nghiên cứu khoa học mới có thể làm thay đổi cân bằng một lần nữa. Nếu requirements analyst chỉ có thể mô hình hóa goals ở một thời điểm, họ không thể nắm bắt sự phức tạp này — và không thể trả lời câu hỏi quan trọng: "Nếu goal X bị loại bỏ ở năm 2003, hệ quả đối với goal Y trong giai đoạn 2004–2006 là gì?"

Lĩnh vực này áp dụng cho: quy hoạch đô thị, chính sách công, phát triển phần mềm dài hạn, quản lý thay đổi yêu cầu trong large-scale projects.

### Bài toán cụ thể

Bài toán được định nghĩa: **làm thế nào để mô hình hóa và suy luận hình thức về các goal models thay đổi theo thời gian?**

Cụ thể, một goal có thể được thỏa mãn ở time point T1 nhưng không được thỏa mãn ở T2. Relationships giữa goals (contribution links) cũng có thể xuất hiện và biến mất theo thời gian. Cần phân tích: với mỗi cấu hình của goals và relationships tại mỗi time point, satisfaction level của từng goal là bao nhiêu?

- **Đầu vào**: Goal model tĩnh cơ sở (Tropos framework) + thông tin tiến hóa: time intervals cho mỗi goal/relationship, evolution functions mô tả cách satisfaction values thay đổi
- **Đầu ra**: Tập hợp "evaluation paths" — chuỗi các trạng thái satisfaction cho mỗi time point, cho phép what-if reasoning theo chiều thời gian

### Tại sao khó

Bốn thách thức kỹ thuật:

1. **"Static snapshot" problem**: Hầu hết kỹ thuật goal modeling (KAOS, i*, Tropos) coi goal model như "bản chụp tĩnh" của một thời điểm duy nhất. Chúng không có cú pháp hay semantics để biểu diễn "goal này tồn tại từ T1 đến T3" hay "contribution từ A đến B chỉ có hiệu lực trong giai đoạn T2–T4".

2. **Thiếu formal semantics**: Một số phương pháp trước cố gắng mô phỏng thời gian bằng cách thêm labels hay annotations, nhưng thiếu formal semantics rõ ràng — kết quả phụ thuộc vào cách diễn giải của từng người, không reproducible.

3. **Combinatorial explosion**: Khi có N goals và M time points, số lượng possible configurations tăng theo cấp số mũ. Phân tích thủ công hoàn toàn không khả thi với models có kích thước thực tế.

4. **Thiếu tool support**: Phần lớn đề xuất trong literature chỉ là lý thuyết — không có tool thực tiễn để practitioners áp dụng.

### Đóng góp của bài

Bài báo đề xuất framework **"Evolving Intentions"** với:
1. **CSP-based formal foundation**: Mã hóa bài toán thành Constraint Satisfaction Problem (CSP) — đảm bảo formal semantics rõ ràng và tự động hóa hoàn toàn
2. **5 evolution functions**: Constant, Stochastic, Increase, Decrease, NotBoth — được định nghĩa toán học chặt chẽ
3. **JaCoP solver integration**: Tự động tìm tất cả evaluation paths hợp lệ
4. **BloomingLeaf**: Web-based tool đầu tiên cho temporal goal model analysis, với giao diện visualization interactive

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: Goal Modeling Tĩnh (Tropos, i\*, KAOS)

Tropos (Bresciani et al., 2004), i* (Yu, 1995), và KAOS (van Lamsweerde, 2001) là các frameworks thành thục trong GORE. Chúng cung cấp cú pháp và semantics phong phú cho goal modeling tại một thời điểm — nhưng không có support cho temporal reasoning. Không thể biểu diễn "goal này chỉ valid trong giai đoạn 2001–2005".

### Hướng 2: Temporal Extensions cho Goal Models

Liu & Yu và Bresciani et al. đã đề xuất thêm temporal labels vào goal models. Ý tưởng đúng hướng nhưng thiếu formal semantics — simulation dựa vào human interpretation, kết quả không tái hiện được. Hai người phân tích cùng một model có thể đưa ra kết quả khác nhau.

### Hướng 3: RELAX Framework

RELAX (Whittle et al.) mô hình hóa softgoals linh hoạt và có khả năng handle uncertainty trong requirements. Tuy nhiên, RELAX tập trung vào relaxation của constraints trong không gian, không phải temporal evolution. Không hỗ trợ reasoning về mức độ satisfaction thay đổi theo thời gian.

### Hướng 4: KAOS Temporal Extensions

Một số nghiên cứu mở rộng KAOS với temporal operators từ temporal logic (LTL, CTL). Về lý thuyết mạnh, nhưng quá phức tạp cho practitioners thông thường và thiếu tool support.

### Hướng 5: Requirements Evolution Management

Nghiên cứu về change management trong requirements (như Zowghi et al., Nuseibeh) tập trung vào process quản lý thay đổi, không phải formal reasoning về tác động của thay đổi goals theo thời gian.

### Khoảng trống (Research Gap)

Cộng đồng thiếu: (1) framework hình thức với semantics rõ ràng cho temporal evolution của goal models, (2) tự động hóa phân tích evaluation paths, (3) tool thực tiễn cho practitioners. "Evolving Intentions" + BloomingLeaf lấp đầy cả ba khoảng trống này.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Ý tưởng căn bản: **mã hóa toàn bộ bài toán phân tích goal model tiến hóa thành một Constraint Satisfaction Problem (CSP).**

CSP là một framework toán học trong đó ta có: một tập biến (variables), miền giá trị cho từng biến (domains), và ràng buộc (constraints). Bài toán: tìm tất cả assignments của biến thỏa mãn mọi ràng buộc. CSP solver (như JaCoP) tự động tìm các solutions này.

Khi ánh xạ sang temporal goal analysis:
- **Variables**: satisfaction value của mỗi intention tại mỗi time point
- **Domains**: {Satisfied, Partially Satisfied, Unknown, Partially Denied, Denied} (5 values của Tropos)
- **Constraints**: contribution links + evolution functions + time interval constraints

CSP solver sẽ tự động tìm tất cả combinations của satisfaction values thỏa mãn mọi constraints — đây là tập "evaluation paths".

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**Bước 1 — Xây dựng Tropos base model**: Mô hình hóa actors, intentions, và contribution links sử dụng Tropos framework. Đây là snapshot tĩnh ban đầu — chưa có thông tin thời gian.

**Bước 2 — Thêm thông tin tiến hóa**: Với mỗi intention và relationship, thêm: (a) time interval [t_start, t_end] — khoảng thời gian nó tồn tại, và (b) evolution function — hàm xác định cách satisfaction value thay đổi.

**Bước 3 — CSP Encoding**: Toàn bộ mô hình được tự động dịch sang CSP:
- Variables: intentionᵢ_timeⱼ cho mỗi intention i tại time point j
- Constraints từ contribution links, evolution functions, và time intervals
- JaCoP solver tìm tất cả valid assignments

**Bước 4 — Visualization và Analysis**: BloomingLeaf hiển thị evaluation paths như timeline visualization. Stakeholders tương tác để thực hiện what-if reasoning: "Nếu tôi thay đổi evolution function của goal X, điều gì xảy ra?"

### 3.3 Các Thành Phần Chính

**Tropos Base Model:**
Framework goal modeling với actors (active agents), intentions (goals, softgoals, tasks, resources), và contribution links (positive/negative/help/hurt contributions). Trong "Evolving Intentions", Tropos được mở rộng với temporal metadata nhưng cú pháp cơ bản được giữ nguyên.

**Time Intervals:**
Mỗi intention và relationship được gán một interval [t_start, t_end] xác định khi nào nó "active". Intention không active ở time point t sẽ không contribute vào evaluation tại t. Điều này cho phép model hóa goals xuất hiện và biến mất theo thời gian.

**5 Evolution Functions:**
- **Constant**: satisfaction value không đổi trong suốt interval — phù hợp cho goals ổn định
- **Stochastic**: satisfaction value ngẫu nhiên tại mỗi time point — phù hợp cho goals với uncertainty cao
- **Increase**: satisfaction value tăng dần theo thời gian (monotone) — phù hợp cho goals được build up dần
- **Decrease**: satisfaction value giảm dần — phù hợp cho goals dần mất priority
- **NotBoth**: ràng buộc hai intentions không thể cùng satisfied tại cùng time point — phù hợp cho conflicting goals

**JaCoP Solver:**
Java Constraint Programming library — solver hiệu quả cho CSP. Tìm tất cả evaluation paths (all solutions) chứ không chỉ một solution — quan trọng để có bức tranh đầy đủ về possible evolutions.

**BloomingLeaf:**
Web application (JavaScript frontend) cho phép: (1) nhập goal model và evolution annotations qua giao diện đồ họa, (2) kích hoạt CSP solving, (3) visualize evaluation paths như timeline charts, (4) filter và explore what-if scenarios.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

**Case study Toronto Bike Lanes** (được đơn giản hóa):

**Actors và intentions:**
- Actor City Council: Goal G1 "Promote Cycling", Goal G2 "Minimize Traffic Disruption"
- Actor Cycling Advocates: Goal G3 "Expand Bike Lane Network"
- Contribution: G3 positively contributes to G1; G3 negatively contributes to G2

**Time intervals và evolution functions:**
- G1 active [T1, T7] (toàn bộ timeline), function: Increase (city becomes more cycling-friendly over time)
- G2 active [T1, T7], function: Constant
- G3 active [T3, T7] (Cycling Advocates group formed at T3), function: Increase

**CSP encoding:**
- Variables: G1_T1, G1_T2, ..., G1_T7, G2_T1, ..., G3_T3, ..., G3_T7
- Constraints: G3 positively contributes to G1 (khi G3 active), G3 negatively contributes to G2, Increase constraint cho G1 (G1_Tᵢ ≤ G1_Tᵢ₊₁), etc.

**JaCoP output**: Tìm được evaluation paths cho thấy:
- Tại T1-T2: G1 = Partially Satisfied (chưa có Cycling Advocates support)
- Tại T3-T7: G1 tăng dần → Satisfied (nhờ contribution từ G3)
- Tại T3-T7: G2 giảm dần (negative contribution từ G3) → từ Satisfied xuống Partially Denied

**What-if**: Analyst có thể hỏi "Điều gì xảy ra nếu Cycling Advocates group chỉ active từ T5?" — BloomingLeaf recalculate và hiển thị evaluation paths mới: G1 tăng chậm hơn, G2 ít bị ảnh hưởng hơn trong giai đoạn T3-T4.

Kết quả match với historical record: quyết định mở rộng bike lanes ở Toronto thực sự được đưa ra vào giai đoạn T5-T6.

### 3.5 Điểm Mới So Với Trước

Ba điểm đột phá:

1. **Formal semantics thực sự**: CSP encoding là lần đầu tiên temporal goal analysis có formal semantics rõ ràng — không còn phụ thuộc vào human interpretation. Kết quả reproducible bởi bất kỳ ai với cùng model input.

2. **Tự động hóa hoàn toàn**: JaCoP solver tìm ALL evaluation paths tự động — practitioners không cần enumerate manually. Điều này scalable với models có kích thước thực tế.

3. **Evolution functions là contribution kỹ thuật chính**: 5 functions được định nghĩa toán học chặt chẽ bao phủ majority of real-world temporal patterns, từ stable goals đến conflicting goals.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Các goal models trong requirements engineering thường được coi là "bản chụp tĩnh" của một thời điểm, trong khi trên thực tế goals và relationships giữa chúng tiến hóa theo thời gian. Bài báo này đề xuất framework "Evolving Intentions" để mô hình hóa và suy luận hình thức về goal models thay đổi theo thời gian. Framework mở rộng Tropos với hai loại annotation: time intervals (khoảng thời gian một goal/relationship active) và evolution functions (cách satisfaction values thay đổi). Toàn bộ bài toán được mã hóa thành Constraint Satisfaction Problem (CSP) và giải bởi JaCoP solver để tự động tìm tất cả "evaluation paths" — chuỗi satisfaction states qua các time points. Năm evolution functions (Constant, Stochastic, Increase, Decrease, NotBoth) được định nghĩa toán học để bao phủ các patterns tiến hóa phổ biến. BloomingLeaf, một web tool thực tiễn, hỗ trợ input mô hình và visualization các evaluation paths. Phương pháp được validate trên hai case study lịch sử thực tế tại Toronto (Toronto Bike Lanes và Spadina Expressway), cho thấy framework có thể tái tạo chính xác các quyết định chính sách lịch sử và hỗ trợ what-if reasoning có giá trị cho stakeholders.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
Hai case study lịch sử thực tế tại Toronto với ground truth từ public records:
1. **Toronto Bike Lanes (2001–2007)**: quy hoạch đô thị, nhiều stakeholders xung đột (cyclists, drivers, city council), timeline thực tế với các quyết định chính trị
2. **Spadina Expressway**: dự án đường cao tốc lịch sử những năm 1960–1970s, kết thúc với quyết định hủy bỏ

Mỗi case study: 10–20 intentions, 3–7 time points, nhiều evolution functions.

**Kết quả chính:**

| Tiêu chí | Kết quả |
|---|---|
| Tái tạo Bike Lanes timeline | Evaluation paths match với quyết định lịch sử thực tế |
| Tái tạo Spadina Expressway | Solver tái hiện đúng thứ tự quyết định hủy bỏ dự án |
| Ngữ nghĩa rõ ràng | 5 evolution functions loại bỏ mơ hồ trong diễn giải |
| Scalability | 20 intentions × 7 time points: solver chạy trong vài giây |
| Reproducibility | Cùng input → cùng output (không phụ thuộc human interpretation) |

**Ablation study:** Không có ablation study chính thức. Tác giả minh họa từng evolution function riêng lẻ với small examples để chứng minh tính đúng đắn.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **5 evolution functions có thể chưa đủ**: Trong thực tế có thể cần step functions (thay đổi đột ngột), periodic patterns (goals theo mùa), hay user-defined functions — chưa được hỗ trợ.

2. **Scalability với large models**: CSP với nhiều intentions × nhiều time points có thể gặp combinatorial explosion. Với models lớn (>50 intentions, >10 time points), solver time có thể trở nên không chấp nhận được.

3. **Validation hẹp**: Chỉ hai case study thuộc domain quy hoạch đô thị. Cần validation trong nhiều domains khác: business process evolution, software requirements change management, policy analysis.

4. **BloomingLeaf chỉ hỗ trợ Tropos**: Chưa tổng quát cho i*, GRL (URN), hay KAOS — giới hạn adoption trong cộng đồng sử dụng các frameworks khác.

**Hướng nghiên cứu tiếp theo:**
- Mở rộng bộ evolution functions (step functions, user-defined custom functions)
- Tối ưu hóa CSP encoding để xử lý large-scale models
- Tích hợp với process modeling (BPMN) — phân tích alignment goal-process theo chiều thời gian
- User study đánh giá tính hữu ích của BloomingLeaf với practitioners thực tế
- Ứng dụng trong requirements evolution management tự động

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất framework "Evolving Intentions" giải quyết một hạn chế cơ bản của GORE: inability to reason về goal models thay đổi theo thời gian. Bằng cách mã hóa bài toán thành CSP và định nghĩa 5 evolution functions toán học, framework cung cấp formal semantics rõ ràng và tự động hóa hoàn toàn việc tìm evaluation paths. BloomingLeaf hiện thực hóa framework thành tool thực tiễn. Validation trên hai case study Toronto cho thấy khả năng tái tạo chính xác historical decisions và giá trị của what-if reasoning. Giới hạn chính là về scalability và breadth of validation — đây là hướng mở rộng cần thiết.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc đặt ra và giải quyết câu hỏi mà hầu hết GORE research bỏ qua: "Goals thay đổi theo thời gian — làm thế nào để reason về điều này một cách hình thức?" CSP encoding là cách giải quyết elegant: biến một bài toán có vẻ khó (temporal goal reasoning) thành một bài toán đã được giải quyết tốt (CSP solving), và tận dụng toàn bộ machinery của CSP community.
