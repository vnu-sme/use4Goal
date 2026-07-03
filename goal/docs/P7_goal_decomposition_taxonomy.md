# P7 — Mở rộng Phân rã Goal KAOS/i* với Taxonomy 5 Kiểu cho Đa tác tử

> Phương pháp nền: **KAOS AND/OR Refinement** (van Lamsweerde 2001) và **iStar 2.0 AND/OR/XOR** (Dalpiaz 2016)  
> Khả năng mở rộng: Phân loại tinh chỉnh thành 5 kiểu thực thi (SEQ, PAR, XOR, IOR, ITER) với ngữ nghĩa hình thức cho đa tác tử

---

## 1. Phương pháp nền và điểm có thể phát triển

### KAOS AND/OR Refinement

KAOS (van Lamsweerde 2001) định nghĩa hai kiểu phân rã goal:
- **AND-refinement**: Root goal đạt được khi *tất cả* sub-goals đạt được
- **OR-refinement**: Root goal đạt được khi *ít nhất một* sub-goal đạt được

KAOS có nền tảng formal mạnh (FLTL — First-order Linear Temporal Logic) cho invariants và obstacle analysis.

**iStar 2.0** (Dalpiaz et al. 2016) có thêm **XOR** (exactly one sub-goal) nhưng không thêm gì khác.

### Điểm có thể phát triển hơn

| Kiểu phân rã | KAOS | iStar 2.0 | MOISE+ | Tropos/GRL |
|-------------|------|-----------|--------|-----------|
| AND (tất cả phải đạt) | AND | AND | partial | AND |
| OR (ít nhất 1) | OR | OR | partial | OR |
| **SEQ** (AND có thứ tự) | **Không** — AND là unordered | **Không** | **Không** | **Không** |
| **PAR** (AND song song) | AND nhưng không explicit | **Không** | **Không** | **Không** |
| **XOR** (chính xác 1) | **Không explicit** | Có | **Không** | **Không** |
| **IOR** (≥1, có thể nhiều) | OR nhưng không phân biệt | **Không** | **Không** | **Không** |
| **ITER** (lặp đến điều kiện dừng) | **Không** | **Không** | **Không** | **Không** |

**Kết luận survey**: Không có phương pháp GORE nào phân biệt SEQ vs PAR ở tầng goal, không có IOR tường minh, và **không có ITER** như một kiểu phân rã goal đầu tiên. Đây là khoảng trống thực sự.

**Tại sao phân biệt này quan trọng cho MAS:**
- SEQ: agent B chỉ được nhận goal sau khi agent A đã xong — ảnh hưởng đến scheduling
- PAR: nhiều agent có thể nhận goal đồng thời — ảnh hưởng đến resource allocation
- XOR: chỉ một nhánh agent nào xử lý — ảnh hưởng đến conflict resolution
- IOR: nhiều agent cùng xử lý nhiều nhánh đồng thời — ảnh hưởng đến coordination
- ITER: agent phải lặp lại hành động cho đến điều kiện dừng — ảnh hưởng đến convergence

---

## 2. Định nghĩa 5 Kiểu Phân rã (Hình thức)

### Ký hiệu chung

```
G  : Goal (cha)
G₁..Gₙ : Sub-goals (con)
σᵢ : Guard condition cho Gᵢ (điều kiện chọn nhánh)
Φ  : Termination condition (chỉ dùng cho ITER)
S  : World state
achieved(G, S) : Goal G đạt được trong state S
```

---

### Kiểu 1: SEQ — Sequential AND

**Định nghĩa**: G được đạt khi G₁, G₂, …, Gₙ được đạt **tuần tự** theo thứ tự đó.

```
SEQ(G, [G₁, G₂, …, Gₙ]) ≡
  achieved(G, S) ↔
  ∃ s₀ ≺ s₁ ≺ … ≺ sₙ :
    achieved(G₁, s₁)  ∧
    (∀ i > 1 : achieved(Gᵢ, sᵢ) ∧ sᵢ > sᵢ₋₁)  ∧  -- thứ tự thời gian
    (∀ i > 1 : achieved(Gᵢ₋₁, sᵢ₋₁) → enable(Gᵢ))  -- G trước kích hoạt G sau
```

**Semantic constraint**: `¬ (started(Gᵢ) ∧ ¬achieved(Gᵢ₋₁))` — Gᵢ không được bắt đầu khi Gᵢ₋₁ chưa xong.

**MAS execution**: Agent được gán cho Gᵢ chỉ nhận goal sau khi agent của Gᵢ₋₁ báo `ACHIEVED`.

---

### Kiểu 2: PAR — Parallel AND

**Định nghĩa**: G được đạt khi tất cả G₁..Gₙ đều đạt được, không ràng buộc thứ tự.

```
PAR(G, {G₁, …, Gₙ}) ≡
  achieved(G, S) ↔
  ∀ i ∈ {1..n} : achieved(Gᵢ, Sᵢ) ∧ Sᵢ ⊆ S
  -- không có ràng buộc thời gian giữa các Sᵢ
```

**Semantic constraint**: Không có. Các Gᵢ có thể được kích hoạt đồng thời.

**MAS execution**: Giao Gᵢ cho n agent khác nhau ngay lập tức. G đạt khi tất cả agent báo `ACHIEVED`.

---

### Kiểu 3: XOR — Exclusive OR

**Định nghĩa**: G được đạt khi **đúng một** Gᵢ được chọn và đạt được. Các guards σᵢ loại trừ nhau.

```
XOR(G, [(σ₁,G₁), …, (σₙ,Gₙ)]) ≡
  achieved(G, S) ↔
  ∃! i : eval(σᵢ, S) = true  ∧  achieved(Gᵢ, S')  -- ∃! = exists exactly one

Guard completeness: ∀ S : ∃ i : eval(σᵢ, S) = true
Guard exclusivity:  ∀ i ≠ j : ¬ (eval(σᵢ, S) ∧ eval(σⱼ, S))
```

**MAS execution**: Evaluate guards → chọn i duy nhất → giao G_i cho agent phù hợp.

---

### Kiểu 4: IOR — Inclusive OR

**Định nghĩa**: G được đạt khi **ít nhất một** Gᵢ được chọn và đạt được. Các guards σᵢ có thể đồng thời true.

```
IOR(G, [(σ₁,G₁), …, (σₙ,Gₙ)]) ≡
  achieved(G, S) ↔
  let Active = {i | eval(σᵢ, S) = true}
  in Active ≠ ∅  ∧  ∀ i ∈ Active : achieved(Gᵢ, Sᵢ)
```

**Phân biệt với XOR**: Trong IOR, nhiều σᵢ có thể đúng đồng thời → nhiều Gᵢ được kích hoạt song song.

**MAS execution**: Evaluate guards → chọn tập Active → giao tất cả Gᵢ trong Active cho các agent.

**Ví dụ phân biệt XOR vs IOR**:
```
Tình huống: Hệ thống cảnh báo tai nạn

XOR: IF (tai nạn loại A) → gửi cứu thương
     ELSE IF (tai nạn loại B) → gửi xe chữa cháy
     (không thể là cả A và B)

IOR: IF (có người bị thương) → gửi cứu thương
     IF (có nguy cơ cháy nổ) → gửi xe chữa cháy
     IF (cần chặn đường) → gửi cảnh sát
     (có thể cả 3 cùng đúng → dispatch 3 loại đồng thời)
```

---

### Kiểu 5: ITER — Iterative Refinement

**Định nghĩa**: G được đạt khi một tập sub-goals được thực hiện lặp lại cho đến khi Φ (termination condition) thỏa.

```
ITER(G, Body, Φ) ≡
  achieved(G, Sₙ) ↔
  ∃ n ≥ 1, ∃ S₀ ≺ S₁ ≺ … ≺ Sₙ :
    eval(Φ, S₀) = false  ∧        -- ban đầu chưa dừng
    ∀ k ∈ {0..n-1} :
      achievedBody(Body, Sk, Sk+1)  ∧  eval(Φ, Sk) = false  ∧
    eval(Φ, Sₙ) = true             -- điều kiện dừng thỏa ở bước cuối

achievedBody: Body là cấu trúc SEQ/PAR/XOR/IOR của sub-goals (lồng nhau được)
```

**Termination guarantee requirement**: Phải chứng minh hoặc giả định `∃ n finite : eval(Φ, Sₙ) = true`. Không thỏa → infinite loop → goal không bao giờ đạt.

**MAS execution**: Vòng lặp điều khiển bởi Orchestrator agent:
```
loop:
  if eval(Φ, currentState): break
  execute Body with assigned agents
  collect results, update currentState
```

---

## 3. Case Study — Hệ thống Cứu trợ Thảm họa Đa tác tử

### Bối cảnh

Hệ thống MAS điều phối ứng phó thảm họa (lũ lụt). Gồm 5 loại agent:
- **DroneAgent**: khảo sát địa hình, định vị nạn nhân
- **RescueAgent**: tiếp cận và cứu người
- **MedAgent**: sơ cứu tại chỗ
- **LogAgent**: vận chuyển hàng cứu trợ
- **CoordAgent**: điều phối tổng thể

### Goal model sử dụng 5 kiểu phân rã

```
Root: Ứng phó thảm họa lũ lụt thành công
  [SEQ]
  ├─ G1: Đánh giá tình hình khu vực thảm họa
  ├─ G2: Tìm kiếm và giải cứu nạn nhân          ← sau khi G1 xong
  └─ G3: Tái thiết và bàn giao                   ← sau khi G2 xong
```

**Phân rã G1 — PAR** (khảo sát đồng thời nhiều khu vực):
```
G1: Đánh giá tình hình
  [PAR]
  ├─ G1.1: Khảo sát khu A (DroneAgent_1)
  ├─ G1.2: Khảo sát khu B (DroneAgent_2)
  └─ G1.3: Khảo sát khu C (DroneAgent_3)
```
*G1 đạt khi cả 3 drone báo hoàn thành, không cần thứ tự.*

**Phân rã G2 — IOR** (loại ứng phó phụ thuộc vào tình hình thực tế):
```
G2: Tìm kiếm và giải cứu
  [IOR]
  σ₁: có_nạn_nhân_cần_cứu(khu A,B,C) → G2.1: Cứu người bị mắc kẹt
  σ₂: thiếu_lương_thực(khu A,B,C)    → G2.2: Phân phối thực phẩm khẩn cấp
  σ₃: có_người_cần_y_tế(khu A,B,C)   → G2.3: Sơ cứu và chuyển viện
```
*Sau khảo sát G1, nếu cả 3 điều kiện đúng → 3 loại ứng phó song song.*
*Nếu chỉ σ₁ và σ₃ đúng → 2 loại ứng phó. IOR đúng ngữ nghĩa hơn XOR ở đây.*

**Phân rã G2.1 — XOR** (phương thức cứu hộ phụ thuộc địa hình):
```
G2.1: Cứu người bị mắc kẹt
  [XOR]
  σ_boat: mực_nước > 1m  → G2.1a: Tiếp cận bằng thuyền
  σ_heli: mực_nước > 3m  → G2.1b: Tiếp cận bằng trực thăng
  σ_foot: mực_nước ≤ 1m  → G2.1c: Tiếp cận bằng đường bộ
```
*Chính xác 1 phương thức được chọn dựa trên guard. Guards loại trừ nhau.*

**Phân rã G2.2 — ITER** (phân phối cho đến khi phủ hết khu vực):
```
G2.2: Phân phối thực phẩm khẩn cấp
  [ITER, Φ: tất_cả_hộ_gia_đình_trong_danh_sách_đã_nhận]
  Body [SEQ]:
    ├─ B1: Chọn lô hàng và tính toán tuyến đường (LogAgent)
    ├─ B2: Vận chuyển và giao hàng (LogAgent)
    └─ B3: Cập nhật danh sách đã nhận (CoordAgent)
```
*Mỗi iteration xử lý một đợt giao hàng. Lặp cho đến khi tất cả hộ đã nhận.*

**Phân rã G3 — SEQ** (bàn giao phải đúng thứ tự):
```
G3: Tái thiết và bàn giao
  [SEQ]
  ├─ G3.1: Kiểm tra an toàn toàn bộ khu vực
  ├─ G3.2: Lập báo cáo thiệt hại           ← chỉ sau G3.1 xong
  └─ G3.3: Bàn giao cho chính quyền địa phương  ← chỉ sau G3.2 xong
```

### Minh họa: Vì sao KAOS AND/OR không đủ

```
Với KAOS AND/OR:
  G2 = OR-refinement → "ít nhất 1 trong 3 nhánh"
  Nhưng không phân biệt được:
    - AND: "phải làm cả 3" (sai — chỉ làm nhánh nào cần)
    - OR: "ít nhất 1" (gần đúng nhưng không biết có thể làm song song)
    - IOR cần: "tất cả nhánh có guard đúng đều được làm đồng thời"

  G1 = AND-refinement → "cả 3 drone phải xong"
  Nhưng KAOS AND không nói "song song" hay "tuần tự" → ambiguous
  Người dùng không biết DroneAgent_2 có thể bắt đầu trước DroneAgent_1 xong không

  G2.2 = không có cách biểu diễn ITER trong KAOS
  Workaround: vẽ loop bên ngoài goal model (trong UCM/BPMN) → mất semantic gắn kết
```

---

## 4. Phản biện và Đánh giá Khả thi

### 4.1 Phản biện có cơ sở

**Phản biện 1: "SEQ/PAR là thuộc về process layer, không phải goal layer"**

Đây là phản biện mạnh nhất. KAOS truyền thống cố tình giữ goal layer **declarative** (không quan tâm thứ tự). Đưa SEQ/PAR vào goal model có thể xem là pha trộn hai lớp.

*Phản hồi*: Trong MAS, thứ tự không chỉ là process concern mà là **resource constraint** ở tầng goal. Ví dụ: không thể dispatch RescueAgent khi chưa có kết quả khảo sát từ DroneAgent — đây là dependency về thông tin, không chỉ là sequence về luồng. Một số công trình (Letier & van Lamsweerde 2004) đã thêm temporal constraints vào KAOS ở tầng goal nhưng không có ký hiệu explicit.

**Phản biện 2: "IOR khó có ví dụ thực tế trong requirements"**

Trong requirements engineering thuần túy, IOR ít xuất hiện hơn XOR. Người đặt yêu cầu thường biết rõ là OR hay XOR.

*Phản hồi*: Trong MAS ứng phó thảm họa, emergency response system, IoT alert system — IOR xuất hiện tự nhiên khi nhiều sensor/agent phát hiện nhiều điều kiện độc lập cùng lúc và tất cả cần được xử lý. Ví dụ case study trên là minh chứng.

**Phản biện 3: "ITER không có termination guarantee trong goal layer"**

Đây là phản biện kỹ thuật quan trọng. Nếu Φ không bao giờ thỏa → goal không bao giờ đạt → hệ thống treo.

*Phản hồi*: Đây là constraint cần phát biểu tường minh trong metamodel. Đề xuất yêu cầu người mô hình hóa khai báo: (1) termination condition Φ, (2) monotonicity argument (lý do tại sao mỗi iteration tiến gần đến Φ). Nếu không khai báo được → tool cảnh báo. Tương tự cách KAOS xử lý obstacle.

**Phản biện 4: "Đã có HTN — tại sao không dùng?"**

HTN (Hierarchical Task Networks) cũng có ordering, AND/OR, iteration.

*Phản hồi*: HTN hoạt động ở tầng **task/action** (know-how), không phải tầng **goal** (know-why). Khi HTN phân rã, thông tin về *tại sao* task này tồn tại bị mất. 5-type taxonomy đề xuất hoạt động ở tầng goal — gần với requirements, có thể truy vết đến stakeholder intent.

### 4.2 Đánh giá Khả thi

| Kiểu | Định nghĩa hình thức | Biến đổi sang BPMN | Kiểm tra tự động | Độ phức tạp impl |
|------|---------------------|-------------------|-----------------|-----------------|
| SEQ | ★★★★ | ★★★★★ | ★★★★ | Thấp |
| PAR | ★★★★★ | ★★★★★ | ★★★★ | Thấp |
| XOR | ★★★★ | ★★★★★ | ★★★ | Trung bình |
| IOR | ★★★ | ★★★★ | ★★★ | Trung bình |
| ITER | ★★★ | ★★★ | ★★ | Cao |

**Nhận xét**:
- SEQ, PAR, XOR: định nghĩa rõ, biến đổi sang BPMN trivial (→ Sequence, Parallel Gateway, XOR Gateway)
- IOR: cần định nghĩa guard evaluation rõ hơn, nhưng khả thi
- ITER: phần khó nhất — cần formal termination argument, runtime semantics phức tạp

**Khuyến nghị scope thạc sĩ**: Implement đầy đủ SEQ, PAR, XOR, IOR. Với ITER — chỉ cần định nghĩa hình thức và case study, không cần full tool implementation (scope quá lớn cho thạc sĩ một mình).

---

## 5. Đóng góp đề xuất — MAGoalTax

### Tên
**MAGoalTax**: Multi-Agent Goal Decomposition Taxonomy — Phân loại 5 kiểu phân rã goal cho đa tác tử

### Mở rộng metamodel KAOS/i* (nhỏ)

```
-- Thêm vào GoalRefinement của KAOS/i*:
extension GoalRefinement {
    decompositionType : DecompositionType
    guards            : Map(SubGoal → Condition)  -- cho XOR, IOR
    terminationCond   : Condition                 -- cho ITER
    monotonicityArg   : String                    -- cho ITER (lý do terminate)
}

DecompositionType = { SEQ, PAR, XOR, IOR, ITER }
```

### Biến đổi sang BPMN (transformation rules)

```
SEQ(G, [G₁..Gₙ]) → BPMN: G₁ →seq→ G₂ →seq→ … →seq→ Gₙ
PAR(G, {G₁..Gₙ}) → BPMN: ParallelGateway(split) → {G₁..Gₙ} → ParallelGateway(join)
XOR(G, [(σ,G)..]) → BPMN: XORGateway(split, guards=σᵢ) → {Gᵢ} → XORGateway(join)
IOR(G, [(σ,G)..]) → BPMN: IORGateway(split, guards=σᵢ) → {Gᵢ} → IORGateway(join)
ITER(G, Body, Φ)  → BPMN: Body →loop→ XORGateway(Φ? exit : repeat)
```

### OCL Constraints

```ocl
-- C1: XOR guards phải loại trừ nhau
context GoalRefinement inv XORGuardsExclusive:
  self.decompositionType = XOR implies
  self.guards->forAll(gᵢ, gⱼ | gᵢ ≠ gⱼ implies
    ¬ (eval(gᵢ) and eval(gⱼ)))  -- không thể cả hai cùng true

-- C2: XOR guards phải đầy đủ (ít nhất 1 guard luôn đúng)
context GoalRefinement inv XORGuardsComplete:
  self.decompositionType = XOR implies
  self.guards->exists(g | eval(g) = true)

-- C3: ITER phải có termination condition
context GoalRefinement inv ITERHasTermination:
  self.decompositionType = ITER implies
  self.terminationCond <> null and
  self.monotonicityArg <> null

-- C4: SEQ: sub-goals phải có thứ tự tuyến tính
context GoalRefinement inv SEQLinearOrder:
  self.decompositionType = SEQ implies
  self.subGoals->isOrderedSet()
```

---

## 6. Scope thạc sĩ

### Phần implement (khuyến nghị)

- **Metamodel extension**: 1 enum (5 values) + 2 optional fields → nhỏ, rõ
- **OCL constraints**: 4 quy tắc
- **Transformation rules**: 5 rules sang BPMN (SEQ, PAR, XOR, IOR đầy đủ; ITER partial)
- **Tool**: Python ~500 LOC — goal model parser (JSON) + type checker + BPMN generator
- **Case study**: disaster response system (minh họa cả 5 kiểu)
- **Evaluation**: 10 goal model mẫu từ literature → reclassify bằng taxonomy → so sánh với original AND/OR

### Phần nghiên cứu (không implement)

- Formal termination semantics của ITER (chứng minh trong luận văn, không cần code)
- Mapping sang BDI agent languages (Jason/2APL) — hướng mở rộng tương lai

### Thời gian: 18–20 tháng

---

## 7. Nguồn chính

- van Lamsweerde, A. (2001). Goal-Oriented Requirements Engineering: A Guided Tour. *RE*.
- Dalpiaz, Franch, Horkoff (2016). iStar 2.0. arXiv:1605.07767
- Horkoff et al. (2019). GORE extended systematic mapping. https://doi.org/10.1007/s00766-017-0280-z
- Hübner, J.F. et al. (2002). MOISE+: Towards a Structural, Functional and Deontic Model. *AAMAS*.
- Letier, E. & van Lamsweerde, A. (2004). Reasoning about partial goal satisfaction. *SIGSOFT FSE*.
- Rao, A.S. & Georgeff, M.P. (1991). Modeling rational agents within a BDI architecture. *KR*.
- Ghasemi, M., & Amyot, D. (2020). From event logs to goals: SLR. https://doi.org/10.1007/s00766-018-00308-3
- Dumas et al. (2025). Agentic BPM Research Manifesto. arXiv:2603.18916
