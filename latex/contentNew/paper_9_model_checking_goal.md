# Making Model Checking Feasible for GOAL

**Tác giả:** Yi Yang, Tom Holvoet  |  **Năm:** 2014  |  **Venue:** PRIMA 2014 (International Conference on Principles and Practice of Multi-Agent Systems)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực giao thoa giữa **lập trình tác nhân (Agent Programming)** và **kiểm chứng hình thức (Formal Verification)**. Cụ thể, bài giải quyết vấn đề: làm thế nào để verify tính đúng đắn của các chương trình viết bằng **GOAL** — một ngôn ngữ lập trình tác nhân đặc biệt.

GOAL (Goal-Oriented Agent Language) là ngôn ngữ lập trình cho các **tác nhân tự trị** (autonomous agents) — các entities có thể tự ra quyết định mà không cần lập trình viên chỉ định từng bước cụ thể. Tác nhân GOAL hoạt động dựa trên:
- **Beliefs** (BB — Belief Base): niềm tin về trạng thái hiện tại của thế giới
- **Goals** (GB — Goal Base): những gì tác nhân muốn đạt được
- **Knowledge** (KB — Knowledge Base): các quy tắc suy luận
- **Actions**: những gì tác nhân có thể làm để thay đổi thế giới

Thay vì lập trình "làm bước A, rồi bước B, rồi bước C", lập trình viên GOAL chỉ cần mô tả beliefs, goals, và actions có sẵn — agent tự tìm ra chuỗi hành động để đạt goals.

**Tầm quan trọng**: Các tác nhân tự trị ngày càng được triển khai trong các hệ thống quan trọng như robot, hệ thống điều khiển tự động, và AI agents. Nếu agent có bugs (ví dụ: stuck states, hoặc thực hiện actions sai), hậu quả có thể nghiêm trọng. Formal verification là cách duy nhất để đảm bảo correctness một cách có systematic.

### Bài toán cụ thể

Bài toán: **biến đổi chương trình GOAL thành một cấu trúc mà các công cụ model checking hiện có có thể xử lý, đồng thời đảm bảo tính tương đương ngữ nghĩa (faithfulness) giữa original program và representation mới.**

- **Đầu vào**: Chương trình GOAL đơn tác nhân, đơn mục tiêu, có cấu trúc phân tầng (stratified) — gồm: Belief Base (BB), Goal Base (GB), Knowledge Base (KB), action constraints, enabledness conditions, Action Specification (ActSpec), domain D
- **Đầu ra**: Transition System TS = (S, Act, →, I, F, AP, L) — tương đương ngữ nghĩa với GOAL program, có thể đưa vào symbolic model checker

### Tại sao khó

1. **GOAL dùng first-order logic (FOL)**: FOL có expressivity mạnh hơn propositional logic — có thể express quantifiers (∀, ∃) và relations. Hầu hết model checkers làm việc với finite state machines hay propositional formulas. Biến đổi FOL sang finite representation không trivial và có thể mất information.

2. **State explosion problem**: Nếu khởi tạo toàn bộ không gian biến trong FOL, số lượng states tăng theo cấp số mũ — model checker không thể xử lý. Cần chiến lược "minimal model" để chỉ khởi tạo đúng những gì cần thiết.

3. **Faithfulness requirement**: Phải chứng minh toán học rằng Transition System sinh ra là tương đương ngữ nghĩa với GOAL program gốc — không phải approximation hay over-approximation. Đây là yêu cầu strict hơn nhiều so với chỉ "thực thi giống nhau trong test cases".

4. **Không có tool nào còn hoạt động**: Tại thời điểm nghiên cứu (2014), dedicated model checker cho GOAL đã không còn accessible, và các frameworks chung (như MCAPL/AIL) có vấn đề về faithfulness.

### Đóng góp của bài

Tác giả đề xuất:
1. **Thuật toán biến đổi GOAL → Transition System** với 5 bước, dựa trên minimal model strategy
2. **Chứng minh toán học bijection** giữa states của TS và states của GOAL program — đảm bảo faithfulness
3. **Proof of concept** trên benchmark Blocks World (2–20 blocks) cho thấy approach là feasible với reasonable state space size

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: MCAPL/AIL Framework

MCAPL (Model Checking Agent Programming Languages) cung cấp framework chung để verify các ngôn ngữ tác nhân, bao gồm GOAL. Cách tiếp cận: cài đặt lại GOAL như một subclass của AIL (Abstract Interpreter Language). Vấn đề: không có proof rằng implementation AIL của GOAL là faithful — có thể có semantic gaps giữa GOAL gốc và AIL representation. Hơn nữa, performance kém hơn so với dedicated approaches.

### Hướng 2: Theorem Proving (Isabelle)

Một số công trình sử dụng theorem prover Isabelle để verify properties của GOAL programs. Ưu điểm: very rigorous, có thể prove properties phức tạp. Nhược điểm: không tự động — đòi hỏi human expertise cao để write proofs; không scalable cho programs lớn; không trực tiếp biểu diễn được first-order properties của GOAL.

### Hướng 3: Dedicated GOAL Model Checker (Cũ)

Từng tồn tại một model checker chuyên dụng cho GOAL, nhưng tại thời điểm viết bài không còn truy cập được và không được maintain. Không có documentation đầy đủ về approach được dùng.

### Hướng 4: General Agent Verification (APL Model Checking)

Nhiều languages khác (Jason, AgentSpeak, Golog) có model checking support. Nhưng các tools này không support GOAL semantics — không thể reuse trực tiếp.

### Khoảng trống (Research Gap)

Chưa có phương pháp nào vừa: (1) faithful (tương đương ngữ nghĩa có chứng minh), (2) tận dụng được symbolic model checkers hiệu suất cao hiện có, (3) tự động hóa hoàn toàn, và (4) feasible về scalability. Bài báo này nhắm vào đây.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Thay vì xây dựng model checker mới từ đầu (expensive và hard to maintain), hãy **biến đổi GOAL program thành Transition System** mà các model checkers hiện có (NuSMV, Uppaal, SPIN) có thể xử lý trực tiếp.

Thách thức chính: GOAL dùng first-order logic, nhưng model checkers cần finite state representation. Giải pháp: dùng **minimal model** của FOL theory — chỉ khởi tạo đúng những constants và facts cần thiết để evaluate goals và constraints, không sinh toàn bộ không gian.

Minimal model là mô hình nhỏ nhất thỏa mãn một tập axioms — trong context GOAL, đây là assignment nhỏ nhất của truth values cho atoms sao cho KB và BB được thỏa mãn. Điều này kiểm soát state space size.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**Bước 1 — Derivation of First-Order Theories:**
Từ GOAL program, extract các first-order theories (FOL formulas) từ BB, GB, KB. Phân loại biến trong mỗi formula thành:
- uni_exp1: biến chỉ xuất hiện ở vế trái của implication → chỉ những biến này cần khởi tạo
- uni_exp2: biến xuất hiện ở cả hai vế → có thể được derived

Chỉ khởi tạo uni_exp1 variables → giới hạn state space đáng kể.

**Bước 2 — State Property Generation:**
- Initial state I: mã hóa BB ban đầu (beliefs tác nhân có lúc khởi động)
- Final states F: mã hóa GB (beliefs tác nhân muốn đạt được)
- Label function L(s) = (current_beliefs, desired_beliefs) cho mỗi state

**Bước 3 — Satisfiable Constraints & Enabled Actions:**
Tại mỗi state s, đánh giá các atoms dựa trên KB + domain D. Dùng constraints và enabledness conditions để tìm Next Actions (NA) — tập hành động khả thi tại state s.

**Bước 4 — State Transformer:**
Với mỗi action a trong NA, áp dụng Action Specification (ActSpec) để tính Next States (NS). Ghi mỗi transition (s, a, s') vào TS.

**Bước 5 — Termination & Output:**
Lặp Bước 2-4 cho tất cả states cho đến khi không có states hay transitions mới. Kiểm tra: mọi end state có phải là F không? Nếu có dead state không phải F → có vấn đề → return None. Nếu OK → return TS hoàn chỉnh.

### 3.3 Các Thành Phần Chính

**First-order Theory Derivation:**
Phân tích cú pháp của GOAL formulas để xác định minimal model. Sử dụng Herbrand interpretation — gán truth values cho ground atoms (atoms không có biến) dựa trên known facts trong BB và KB.

**State Representation:**
Mỗi state s trong TS là một tuple (BB_s, GB_s) — beliefs hiện tại và goals hiện tại của agent. Label function L ánh xạ state sang propositional formulas mà model checker có thể process.

**Bijection Proof:**
Core technical contribution: tác giả chứng minh có bijection (ánh xạ 1-1 và onto) giữa tập states của TS và tập states của GOAL program execution. Điều này đảm bảo: (a) không có state nào trong TS không tồn tại trong GOAL, và (b) không có state nào trong GOAL bị bỏ sót trong TS. Faithfulness được đảm bảo mathematical.

**Action Constraints:**
GOAL cho phép định nghĩa constraints để hạn chế không gian hành động. Đây là công cụ quan trọng để kiểm soát state explosion: nếu constraint chỉ cho phép hành động "đặt block vào đúng vị trí đích hoặc xuống bàn", state space giảm dramatically.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

**Blocks World**: Có 3 blocks (A, B, C) trên bàn, cần xếp thành stack A on B, B on C theo thứ tự.

**GOAL Program**:
- BB ban đầu: on(A, table), on(B, table), on(C, table), clear(A), clear(B), clear(C)
- GB (goal): on(A, B), on(B, C)
- Actions: move(X, Y) — di chuyển block X lên block Y (nếu X và Y đều clear)
- Action constraint: only move block onto its target position or table

**Execution trace (Transition System)**:

*State 0 (Initial)*: BB = {on(A,table), on(B,table), on(C,table), clear(A,B,C)}, GB = {on(A,B), on(B,C)}

Evaluate enabled actions: move(B,C) enabled (B clear, C clear), move(A,B) NOT enabled (B sẽ không clear sau khi move)

*State 1 (after move(B,C))*: BB = {on(A,table), on(B,C), on(C,table), clear(A), clear(B)}, GB unchanged

Evaluate: move(A,B) NOW enabled (B is now clear)

*State 2 (after move(A,B))*: BB = {on(A,B), on(B,C), on(C,table)}, GB = {on(A,B), on(B,C)} — GB subset of BB!

→ Final state reached. No dead states. TS valid.

**Transition System (TS)**:
- S = {State0, State1, State2}
- Act = {move(B,C), move(A,B)}
- → = {(S0, move(B,C), S1), (S1, move(A,B), S2)}
- I = S0, F = {S2}

Model checker có thể verify: "Mọi execution path từ S0 đều reach F" → TRUE. Agent đạt goal trong mọi trường hợp.

Với 20 blocks, TS có 6,330 states và 27,825 transitions — lớn hơn nhiều nhưng vẫn khả thi cho symbolic model checkers.

### 3.5 Điểm Mới So Với Trước

Hai điểm đột phá:

1. **Faithfulness có chứng minh**: Tác giả không chỉ claim mà prove toán học bijection giữa states của TS và GOAL execution — đây là guarantee mạnh nhất có thể về semantic equivalence.

2. **Minimal model strategy**: Thay vì phải khởi tạo toàn bộ FOL interpretation (exponential), chỉ khởi tạo minimal subset đủ để evaluate goals và constraints — giữ state space ở mức manageable.

---

## PHẦN 4 — Abstract (Tiếng Việt)

GOAL là ngôn ngữ lập trình tác nhân cho phép tác nhân tự ra quyết định dựa trên beliefs và goals. Kiểm chứng tính đúng đắn của các chương trình GOAL là quan trọng nhưng thách thức, vì GOAL dùng first-order logic và không có model checker chuyên dụng nào còn hoạt động. Bài báo này đề xuất thuật toán biến đổi chương trình GOAL thành một Transition System (TS) tương đương ngữ nghĩa, có thể đưa vào symbolic model checkers hiện có. Thuật toán gồm 5 bước: derivation of first-order theories với minimal model strategy, state property generation, satisfiable constraints và enabled actions evaluation, state transformation, và termination check. Điểm mấu chốt là chứng minh toán học bijection giữa states của TS và states của GOAL program — đảm bảo faithfulness hoàn toàn. Minimal model strategy kiểm soát state space bằng cách chỉ khởi tạo variables cần thiết, không sinh toàn bộ không gian. Proof-of-concept trên Blocks World (2–20 blocks) cho thấy approach feasible: với 20 blocks, TS có 6,330 states và 27,825 transitions — trong tầm xử lý của symbolic model checkers hiện đại.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
**Blocks World** — benchmark AI kinh điển. N blocks phải được xếp từ trạng thái ban đầu (tất cả không đúng vị trí) về trạng thái đích (stack theo thứ tự cụ thể).

**Baselines:**
Không có quantitative baseline — không có model checker GOAL nào còn hoạt động để so sánh. Thực nghiệm focus vào demonstrating feasibility.

**Kết quả chính:**

| Số blocks | States | Transitions |
|---|---|---|
| 2 | 3 | 2 |
| 5 | 13 | 21 |
| 10 | 75 | 154 |
| 15 | 366 | 1,010 |
| 20 | 6,330 | 27,825 |

Tăng trưởng có thể kiểm soát được nhờ action constraints phù hợp (chỉ di chuyển block về đúng vị trí đích hoặc xuống bàn). Symbolic model checkers hiện đại xử lý được quy mô này.

**Trường hợp tốt:** Khi action constraints được định nghĩa tốt → state space nhỏ, feasible.
**Trường hợp kém:** Không có constraints hoặc domain quá lớn → state explosion vẫn là mối đe dọa.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Single-agent, single-goal, stratified chỉ**: Phương pháp hiện tại chỉ áp dụng cho một agent với một goal và stratified program structure. Multi-agent và multi-goal scenarios phức tạp hơn nhiều — chưa được xử lý.

2. **Scalability phụ thuộc constraints**: Nếu không có action constraints phù hợp, state explosion vẫn là vấn đề nghiêm trọng. Không có automated guidance để define constraints tốt.

3. **Không có quantitative baseline**: Không có tool nào để so sánh performance — chỉ có thể demonstrate feasibility, không thể so sánh relative improvement.

4. **Benchmark đơn giản**: Blocks World là benchmark AI kinh điển nhưng đơn giản. Cần validate với real-world agent programs phức tạp hơn.

**Hướng nghiên cứu tiếp theo:**
- Mở rộng sang multi-agent và multi-goal GOAL programs
- Tích hợp hoàn chỉnh với NuSMV hay Uppaal như pipeline tự động
- Xử lý probabilistic actions (thay Prolog bằng Problog cho probabilistic reasoning)
- Kết nối với Robot Operating System (ROS) cho real-time verification
- Automated constraint suggestion để giảm state space

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất thuật toán biến đổi chương trình GOAL thành Transition System với faithfulness được chứng minh toán học — lần đầu tiên formal verification có thể được áp dụng cho GOAL programs một cách rigorous và automated. Minimal model strategy kiểm soát state space bằng cách chỉ khởi tạo necessary variables. Proof-of-concept trên Blocks World (20 blocks → 6,330 states) cho thấy approach feasible với symbolic model checkers hiện đại. Giới hạn chính là chỉ áp dụng cho single-agent, single-goal programs — mở rộng sang multi-agent là hướng nghiên cứu tiếp theo.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** quyết định **không** xây dựng model checker mới mà thay vào đó reuse existing tools bằng cách biến đổi representation — một quyết định pragmatic và bền vững. Kết hợp với bijection proof cho faithfulness, đây là approach vừa theoretically rigorous vừa practically viable. Đây là ví dụ đẹp về "standing on the shoulders of giants" trong formal verification research.
