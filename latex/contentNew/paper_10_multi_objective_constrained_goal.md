# Multi-Objective Reasoning with Constrained Goal Models

**Tác giả:** Chi Mai Nguyen, Dalal Alrajeh, Alessio Ferrari, et al.  |  **Năm:** 2021  |  **Venue:** IEEE Transactions on Software Engineering (TSE)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Goal-oriented Requirements Engineering (GORE)** — cụ thể là phân tích và tối ưu hóa tự động trên goal models. GORE là hướng tiếp cận requirements engineering bắt đầu từ việc nắm bắt goals (mục tiêu) của stakeholders thay vì đặc tả chức năng ngay từ đầu. Từ goals, ta suy ra requirements, và từ requirements, ta thiết kế hệ thống.

Một trong những hoạt động quan trọng nhất trong GORE là **lựa chọn realization** — khi một goal có thể được hiện thực hóa bằng nhiều cách khác nhau (AND/OR refinements), cần chọn cách tốt nhất. Ví dụ: goal "Authenticate User" có thể được hiện thực hóa bằng password, biometrics, hay multi-factor — mỗi cách có chi phí, thời gian, và mức độ security khác nhau. Bài toán: chọn realization nào tốt nhất khi có nhiều tiêu chí (cost thấp, security cao, usability tốt) và nhiều constraints (ngân sách tối đa, tuân thủ regulations)?

Bài toán này đặc biệt quan trọng trong thực tiễn vì: trong các hệ thống phức tạp (healthcare IT, smart city, air traffic management), goal models có thể có hàng nghìn nodes và hàng chục tiêu chí tối ưu hóa — không thể giải quyết thủ công.

### Bài toán cụ thể

Hai bài toán cụ thể được giải quyết:
1. **Realizability checking**: Có tồn tại realization hợp lệ nào thỏa mãn tất cả constraints không? Nếu không, nguyên nhân (unsatisfiable core) là gì?
2. **Multi-objective optimization**: Trong số các realizability hợp lệ, tìm realization tối ưu theo nhiều tiêu chí đồng thời với thứ tự ưu tiên lexicographic.

- **Đầu vào**: CGM model = ⟨B, N, D, Ψ⟩: tập mệnh đề nguyên tử B (goals G, refinements R, assumptions A), biến số N (cost, time, weight...), đồ thị DAG D, công thức SMT(LRA) Ψ; kèm theo user assertions (ép buộc goal on/off), thứ tự ưu tiên (lexicographic ordering)
- **Đầu ra**: Realization tối ưu của CGM (đồ thị con thỏa mãn mọi constraints + tối ưu theo priority); hoặc unsatisfiable core nếu không có realization hợp lệ

### Tại sao khó

1. **Thiếu expressivity trong ngôn ngữ hiện có**: KAOS, i*, GRL không hỗ trợ constraints Boolean tường minh, numerical variables (cost, time), hay hàm tối ưu hóa như first-class citizens. Chúng được thiết kế cho qualitative reasoning, không quantitative optimization.

2. **Refinements không first-class**: Trong KAOS/i*, refinements (cách hiện thực hóa goal) không được treat như independent entities có thể constrain hay loại trừ. Điều này giới hạn khả năng reasoning về lựa chọn realization.

3. **Scalability**: Với mô hình lớn (hàng nghìn nodes), exhaustive search hoặc propagation heuristics không khả thi. Cần formal solving technique có scalability guarantees.

4. **Multi-objective với conflicting criteria**: Khi cost thấp mâu thuẫn với security cao, cần định nghĩa thứ tự ưu tiên rõ ràng — không phải tất cả criteria đều có trọng số bằng nhau.

### Đóng góp của bài

Tác giả đề xuất **Constrained Goal Model (CGM)** — ngôn ngữ goal modeling mở rộng với:
1. **Numerical variables**: cost, time, quality là first-class citizens trong model
2. **Refinements là first-class**: có thể constrain và optimize trực tiếp trên refinements
3. **SMT(LRA)/OMT encoding**: mã hóa tự động CGM sang formula cho OMT solver
4. **Lexicographic optimization**: tối ưu hóa nhiều tiêu chí theo thứ tự ưu tiên
5. **Scalability evidence**: >10,000 nodes với realizability check gần như lập tức

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: KAOS

KAOS (van Lamsweerde) là goal modeling language thành thục với AND/OR decomposition, responsibility assignment, và causal analysis. Mạnh cho qualitative goal analysis. Nhược điểm: thiếu numerical variables, refinements không first-class, optimization là manual và không scalable với models lớn.

### Hướng 2: i\* (iStar)

i* (Yu) mô hình hóa actors, intentions, và strategic dependencies. Reasoning qua contribution propagation heuristics — không có formal guarantee về optimality. Không hỗ trợ numerical constraints hay lexicographic optimization.

### Hướng 3: Techne

Techne (Jureta et al.) là goal model language với formal constraints dùng SAT solving. Bước tiến so với KAOS/i* vì có formal solving. Nhưng Techne chỉ hỗ trợ Boolean variables — không có real-valued/numerical variables. Không thể express "minimize cost" hay "maximize quality score".

### Hướng 4: NFR Framework (Chung et al.)

NFR Framework xử lý non-functional requirements và softgoals với contribution links. Reasoning là qualitative (positive/negative/neutral contributions) — không định lượng, không tối ưu hóa. Propagation là manual.

### Hướng 5: Requirements Optimization (GORE + Search)

Một số công trình kết hợp GORE với search-based software engineering (SBSE) để tối ưu hóa. Tuy nhiên, search methods (genetic algorithms, simulated annealing) không có formal optimality guarantee — có thể bỏ lỡ optimal solution.

### Khoảng trống (Research Gap)

Không có goal modeling language nào vừa: (1) expressible với numerical variables và Boolean constraints, (2) có automated reasoning với formal optimality guarantees, (3) scalable với models lớn, (4) hỗ trợ multi-objective optimization với lexicographic ordering. CGM + OMT lấp đầy tất cả bốn khoảng trống này.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

**Insight căn bản**: Bài toán lựa chọn realization trong goal models là một bài toán **optimization modulo theories (OMT)** — tìm assignment của biến thỏa mãn logic constraints và tối ưu hóa một hàm mục tiêu. OMT solvers hiện đại (như OptiMathSAT) cực kỳ hiệu quả cho loại bài toán này, có thể xử lý hàng chục nghìn biến trong vài giây.

Vì vậy, thay vì phát triển thuật toán tối ưu hóa riêng, bài báo **mã hóa CGM thành OMT formula** và để solver tự giải. Đây là pattern "reduce to a well-studied problem" — tận dụng state-of-the-art solver thay vì re-invent the wheel.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**Bước 1 — CGM Modeling**: Analyst mô hình hóa requirements dưới dạng CGM:
- Goals: điều kiện cần đạt (Boolean)
- Refinements: cách hiện thực hóa goal (Boolean — first-class element)
- Assumptions: điều kiện domain (Boolean hay numerical)
- Numerical variables: cost, time, quality, v.v. (Real-valued)
- Constraints Ψ: quan hệ giữa variables (linear arithmetic inequalities)
- Objectives: hàm tối ưu hóa (minimize cost, maximize quality)
- Lexicographic priority: thứ tự ưu tiên giữa các objectives

**Bước 2 — CGM Encoding**: CGM-Tool tự động dịch sang SMT(LRA) formula:
- Mỗi goal g → Boolean variable bool_g
- Mỗi refinement r → Boolean variable bool_r (first-class!)
- AND decomposition: bool_parent → (bool_child1 ∧ bool_child2 ∧ ...)
- OR decomposition: bool_parent → (bool_child1 ∨ bool_child2 ∨ ...)
- Numerical constraints → LRA (Linear Rational Arithmetic) formulas
- User assertions → fixed values
- Objectives → objective function list

**Bước 3 — OMT Solving**: OptiMathSAT giải formula với lexicographic optimization:
- Tối ưu objective_1 trước
- Trong số solutions tối ưu objective_1, tối ưu objective_2
- Tiếp tục...
- Output: optimal assignment

**Bước 4 — Visualization**: CGM-Tool map assignment ngược về CGM graph — highlight realization tối ưu. Nếu UNSAT, hiển thị unsatisfiable core.

### 3.3 Các Thành Phần Chính

**CGM Language (Constrained Goal Model):**
Mở rộng của KAOS/i* với: (1) numerical variables là first-class entities trong model, không chỉ là annotations; (2) refinements có independent existence — có thể constrain, loại trừ, hay force bằng user assertions; (3) Ψ là tập SMT formulas biểu diễn domain constraints (budget ≤ 100, time < 24h, etc.).

**SMT(LRA) Encoding:**
LRA = Linear Rational Arithmetic — cho phép express linear inequalities trên real-valued variables (ax + by ≤ c). Kết hợp với Boolean variables, ta có SMT = Satisfiability Modulo Theories — framework cho phép mix Boolean logic và arithmetic. Ví dụ: "Nếu refinement R1 được chọn thì cost += 50" được mã hóa là: bool_R1 → (cost = cost_prev + 50).

**OptiMathSAT:**
State-of-the-art OMT solver hỗ trợ LRA. Sử dụng DPLL(T) framework — kết hợp SAT solving (cho Boolean part) và theory reasoning (cho arithmetic part). Lexicographic optimization: giải từng objective theo priority, thêm constraint "solution phải tốt ít nhất bằng optimal của objectives trước" trước khi giải objective tiếp theo.

**Lexicographic Ordering:**
Phản ánh thực tế ra quyết định: một stakeholder có thể nói "tôi muốn minimize cost trước tất cả, sau đó với cùng cost, prefer quality cao hơn". Lexicographic ordering capture điều này một cách chính xác.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

**Meeting Scheduling System** — bài toán lên lịch họp trực tuyến:

**CGM structure (simplified)**:
- Root goal: "Schedule Meeting" với OR-refinement:
  - R1: "Manual Scheduling" (cost: 0, time: 60 min, quality: 0.5)
  - R2: "AI-assisted Scheduling" (cost: 100, time: 5 min, quality: 0.9)
  - R3: "Automated Scheduling with Calendar Sync" (cost: 200, time: 1 min, quality: 0.95)
- Domain constraints: budget ≤ 150
- User assertion: bool_R1 = False (không muốn manual)
- Objectives (lexicographic): minimize time → maximize quality

**SMT Encoding** (sketch):
```
bool_R1 = False  (user assertion)
bool_R2 ∨ bool_R3  (root must be realized)
cost = 100 * bool_R2 + 200 * bool_R3
cost ≤ 150  (budget constraint)
time = 5 * bool_R2 + 1 * bool_R3
quality = 0.9 * bool_R2 + 0.95 * bool_R3
```

**OptiMathSAT solving**:
1. Check feasibility: bool_R2 = True → cost = 100 ≤ 150 ✓; bool_R3 = True → cost = 200 > 150 ✗ → R3 infeasible
2. Unique feasible option: R2
3. Optimal: time = 5 min, quality = 0.9

Output: highlight R2 trong CGM graph → "AI-assisted Scheduling" là realization tối ưu.

**Với 10,000 nodes**: CGM-Tool encode trong vài giây, OptiMathSAT giải realizability check gần như lập tức vì Boolean structure kiểm soát search space.

### 3.5 Điểm Mới So Với Trước

Ba điểm đột phá:

1. **Numerical variables + refinements first-class**: Lần đầu tiên goal model có thể express "minimize cost" và "force/exclude specific refinement" như native operations, không phải workarounds.

2. **Formal optimality guarantee**: OMT solver tìm được optimal solution (không phải near-optimal như search-based methods). Nếu không có solution, trả về unsatisfiable core để diagnose vấn đề.

3. **Scalability qua OMT**: Lexicographic multi-objective optimization trên >10,000 nodes — không có framework GORE nào trước đây đạt được quy mô này.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Phân tích goal models trong requirements engineering đòi hỏi khả năng kiểm tra tính khả thi (realizability checking) và tối ưu hóa theo nhiều tiêu chí đồng thời. Các ngôn ngữ goal modeling hiện tại (KAOS, i*) thiếu hỗ trợ cho numerical variables, refinements như first-class elements, và automated optimization. Bài báo này đề xuất Constrained Goal Model (CGM) — mở rộng goal modeling với numerical variables, Boolean constraints trên refinements, và domain assumptions tích hợp. CGM được mã hóa tự động thành SMT(LRA) formula và giải bởi OMT solver OptiMathSAT với lexicographic multi-objective optimization. Lexicographic ordering cho phép ưu tiên tiêu chí quan trọng nhất (minimize cost) trước, tie-break bằng tiêu chí thứ hai (maximize quality), phản ánh thực tế ra quyết định. Thực nghiệm trên benchmark lên đến 10,000 nodes và 6,000 numerical variables cho thấy realizability checking gần như lập tức, và multi-objective optimization khả thi trong vài giây đến vài phút tùy kích thước objective. Đây là framework đầu tiên trong GORE cung cấp formal optimality guarantee với scalability ở mức industrial.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
Benchmark tổng hợp từ "Meeting Scheduling System" (54 nodes, 30 numerical variables) được nhân bản N lần và kết nối lại với tham số k (connectivity) và p (probability). Quy mô tối đa: >10,000 nodes và >6,000 rational variables.

**Kết quả chính:**

| Loại query | Quy mô | Thời gian | Ghi chú |
|---|---|---|---|
| Realizability check (SAT) | >10,000 nodes, >6,000 vars | **Gần như lập tức** | Không phụ thuộc quy mô |
| Realizability check (UNSAT) | >10,000 nodes | **Gần như lập tức** | Fast unsatisfiable core |
| Single-objective (cost, 2N vars) | >8,000 nodes | **<1 giây** | Ít vars trong objective |
| Single-objective (Weight, 16N vars) | ~400 nodes | Vài giây | Nhiều vars = chậm hơn |
| Lexicographic 3-objectives | Hàng nghìn nodes | Vài giây đến vài phút | Phụ thuộc vars trong objectives |

**Key findings:**
- Số biến trong objective function là yếu tố quan trọng nhất, không phải kích thước đồ thị
- User assertions (force/exclude specific refinements) thu hẹp search space → kết quả nhanh hơn đáng kể trong thực tế (benchmark không dùng user assertions)

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Benchmark artificial**: CGM được sinh tự động có thể không phản ánh cấu trúc models thực tế. Structure của benchmark rất rộng và nông — khác với industry models thường sâu và có nhiều cross-dependencies.

2. **Weight function quá lớn**: Benchmark tạo ra Weight objective với 16N variables — không realistic vì trong thực tế, không phải mọi node đều contribute vào một objective.

3. **Chưa có user evaluation**: Validation chỉ là scalability benchmark, không có user study với domain experts để evaluate usefulness và usability của CGM language.

4. **Chỉ Linear Rational Arithmetic**: Một số real-world scenarios đòi hỏi non-linear relationships (quadratic, exponential) — LRA không đủ. Cần NRA (Non-linear Rational Arithmetic) extension.

**Hướng nghiên cứu tiếp theo:**
- User evaluation với domain experts trong healthcare, smart cities, air traffic management
- Industry case studies để validate với real-world complexity
- Integration với next release problem và self-adaptive systems
- Mở rộng sang non-linear arithmetic (NRA)
- Adaptive optimization: cập nhật optimal solution incrementally khi requirements thay đổi

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất Constrained Goal Model (CGM) và framework encoding sang OMT để giải quyết realizability checking và multi-objective optimization trong GORE một cách automatic và scalable. Ba đóng góp chính: (1) CGM language với numerical variables và first-class refinements, (2) lexicographic OMT encoding đảm bảo formal optimality, (3) scalability evidence trên >10,000 nodes. Điểm mạnh là formal guarantee về optimality — điều không có search-based approaches nào đạt được. Giới hạn là thiếu user evaluation và chỉ có benchmark artificial — đây là hướng nghiên cứu tiếp theo quan trọng.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc biến GORE optimization từ "art" (manual judgment, heuristics, trial-and-error) thành "science" (formal problem, automated solver, optimality guarantee). Khi requirements engineer có thể nói "minimize cost, then maximize quality, subject to budget ≤ 150" và tool tự động tìm optimal configuration — đây là bước nhảy vọt về mức độ automation trong GORE.
