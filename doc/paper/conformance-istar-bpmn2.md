# Conformance giữa Goal Model i* (.istar) và Process Model BPMN (.bpmn2)

## Trạng thái hiện thực hóa: scenario-driven execution

Trong workflow ACL–SOIL–BPMN–iStar hiện tại, **scenario** là input fixture trước khi hệ thống
chạy, không phải một trace viết tay:

```
ACL   = hợp đồng cấu trúc dùng chung
SOIL  = object/agent/role/group và dữ liệu đầu vào cụ thể
BPMN  = bộ sinh trace bằng token, guard, pre/post và effect
USE   = trạng thái runtime dùng chung để đánh giá OCL
iStar = bộ quan sát mức độ thỏa mãn mục tiêu sau từng Activity
```

Sau khi đọc hết SOIL, initialization gate chỉ xác nhận fixture thuộc không gian trạng thái ACL.
Nó không phải verdict conformance. Verdict chỉ được đưa ra khi BPMN đã chạy:

- `CONFORMANT`: BPMN đến EndEvent và mọi occurrence của root goal iStar là `FULFILLED`.
- `NOT CONFORMANT`: BPMN đến EndEvent nhưng còn root goal `PENDING/UNKNOWN`, hoặc BPMN không
  thể hoàn tất theo contract của chính nó.

Một phản ví dụ đúng nghĩa vì thế phải có initial snapshot hợp lệ. Ví dụ kiểm thử MTG dùng cùng
`mtg_i1o1p3s1.soil`: `mtg.bpmn2` thực hiện cả attendance và conformant, trong khi
`mtg_goal_gap.bpmn2` vẫn đến EndEvent nhưng bỏ `participate`, làm root goal attendance chưa đạt.

`VisualConformanceSession` là semantics dùng chung của checker một lần và debugger. Mỗi bước:

1. lấy Activity đang enabled từ BPMN token engine;
2. kiểm tra precondition trên USE state hiện tại;
3. thực thi generic SOIL effect;
4. kiểm tra postcondition và ACL/USE constraints;
5. tính lại iStar instance marking;
6. lưu BPMN snapshot, state delta và goal delta làm proof trace.

Các input và expected trace MTG được catalog tại
`goal/src/main/resources/examples/mtg/README.md`.

### Nhiều trace và ba mức conformance đã hiện thực hóa

`ScenarioTraceExplorer` ghi lại mỗi choice point XOR/event-based có nhiều flow cùng hợp lệ,
tái chạy input scenario theo mọi choice vector quan sát được (có giới hạn `maxTraces`) và trả về:

- **weak**: tồn tại ít nhất một trace đến EndEvent và thỏa mọi root-goal occurrence;
- **strong**: mọi trace đã sinh đều đến EndEvent và thỏa mọi root goal;
- **stable**: trên từng trace, root goal đã fulfilled không quay lại pending/unknown;
- verdict `NON_CONFORMANT`, `WEAKLY_CONFORMANT`, hoặc `STRONGLY_CONFORMANT`.

Debugger có nút `Explore all traces` và combobox chọn trace để phát lại trực quan. Checker một
lần dùng cùng explorer, không còn sử dụng execution order tuyến tính riêng.

### Phạm vi token engine hiện tại

- nhiều process/pool độc lập được chạy trong cùng session;
- AND split/join giữ token cho đến khi đủ mọi incoming branch;
- structured inclusive-OR ghi số nhánh thực sự được chọn và chờ đúng số token tại OR join;
- XOR/event-based choice có thể enumerate;
- loop có thể enable lại cùng Activity;
- SubProcess được thực thi như một opaque contracted Activity (pre/effect/post ở biên);
- message-flow synchronization giữa nhiều pool chưa được suy diễn vì metamodel hiện chưa cung
  cấp correlation/runtime message instance trong bốn input conformance.

### Case study bổ sung

Incident Response đã được migrate khỏi ACL syntax/SOIL final-state cũ và có đủ
`incident_response.{acl,soil,istar,bpmn2}`. OJS có thêm `ojs_input.soil`, `ojs.istar` và
`ojs.bpmn2`; file AOL cũ vẫn giữ vai trò snapshot minh họa độc lập. Cả hai case được chạy trong
regression suite qua chính one-shot checker.

## Phương pháp nền JUCS + tổng hợp alig / vaDL — thiết kế đầy đủ (API cụ thể + case study chạy tay)

Ngày lập: 2026-07-04 (cập nhật: thiết kế đầy đủ toàn bộ 4 giai đoạn + case study thật)

> **Lưu ý đặt lại tên**: nội dung 2 file `alig.md` và `vaDL.md` **ngược** với suy đoán ban đầu
> từ tên file:
> - **`alig.md`** = Caballero-Villalobos, Baumeister, Paja, Kokoulina, López — *"**Alig**ning
>   processes with high-level requirements: Goal-model-based compliance checking"*, Information
>   and Software Technology 196 (2026). LTS (Workflow-net + DCR graph) cho process, **iStar goal
>   model với ngữ nghĩa vận hành (marking + propagation)**, tiêu chí weak/strong/monotonic
>   (stability) compliance, tool Kogi.
> - **`vaDL.md`** = Gröner, Asadi, Mohabbati, Gašević, Bošković, Silva Parreiras — *"**Va**lidation
>   of user intentions in process orchestration and choreography"* (dùng **D**escription **L**ogics
>   → "vaDL"), Information Systems 43 (2014). GRL (i*-compatible) + BPMN, ánh xạ actor↔pool/lane,
>   task↔activity, phát hiện **strong/potential inconsistency** bằng subsumption checking.
>
> Tài liệu gọi tên theo đúng nội dung thật (Caballero-Villalobos = "bài LTS/compliance",
> Gröner = "bài DL/inconsistency"), không theo tên file.

---

## 0. Mục tiêu

Thiết kế **chạy hoàn chỉnh** phương pháp conformance-checking giữa mô hình i* (`.istar`) và
BPMN (`.bpmn2`) trong project này, **dùng khung phương pháp của JUCS** (Dang–Truong–Gogolla,
J.UCS 16(17), 2010) làm backbone, lấp nội dung bằng Caballero-Villalobos (ngữ nghĩa động + tiêu
chí compliance) và Gröner (ánh xạ cụ thể + kiểm tra tĩnh nhanh). Tài liệu này đi tới **mức có thể
lập trình trực tiếp**: package/class/method signature cụ thể, thuật toán ở dạng pseudocode sẵn
sàng viết Java, định dạng file mapping cụ thể, và **1 case study thật đã tạo sẵn trong repo**
(`goal/src/main/resources/examples/construction_permit/construction_permit.{istar,bpmn2,map}`) kèm **trace tay đầy
đủ từ đầu đến cuối** để kiểm chứng thiết kế trước khi viết code thật.

---

## 1. Mỗi bài đóng góp gì

| Bài | Vai trò trong thiết kế này |
|---|---|
| **JUCS** (Dang et al. 2010) | **Khung phương pháp luận (backbone)**: 2 mô hình = tập *scenario* (dãy *action* với snapshot pre/post), đồng bộ bằng *triple rule*, thuật toán kiểm tra conformance. Mượn nguyên bộ khái niệm: snapshot pattern → action → scenario → model → execution state → triple transformation → thuật toán. |
| **Caballero-Villalobos et al.** (`alig.md`) | **Ngữ nghĩa vận hành cụ thể + tiêu chí conformance**: goal model marking (Definition 3.1), luật lan truyền AND/OR/Make/Break (Fig. 7 — *chính là* OCL invariant mà JUCS để trống), LTS process model, **synchronous product LTS** (Definition 4.1), 3 mức **weak compliance / stability / strong compliance** bằng BFS thuận-nghịch (Algorithm 1–4, đã chứng minh đúng đắn/đầy đủ/độ phức tạp). |
| **Gröner et al.** (`vaDL.md`) | **Ánh xạ cụ thể i*/GRL ↔ BPMN + tầng kiểm tra tĩnh nhanh**: actor→pool/lane, task→activity (§4), **strong inconsistency** (không tổ hợp thực thi nào thoả) và **potential inconsistency** (có tổ hợp thoả, có tổ hợp không) — dùng làm pre-check rẻ trước khi dựng LTS đầy đủ; bảng workflow-pattern ↔ intentional-relation (Table 1). |

---

## 2. Đối chiếu formal notation ↔ metamodel THẬT của project

### 2.1. Phía i* — `org.vnu.sme.goal.istar.mm`

| Formal | Class Java thật | Ghi chú |
|---|---|---|
| `IE = G ⊎ T ⊎ Q` | `IntentionalElement` (sealed: `Goal, Task, Resource, Quality`) | Có thêm `Resource` (không tham gia satisfaction propagation) |
| `RefLinks(GM)` | `Refinement.And(parent, children)` / `Refinement.Or(parent, child)` | `Or` chỉ có 1 child/record — nhiều `or-refine X : Y` cùng `X` phải gộp theo `parent()` khi dựng `Ch(e)` |
| `C: IE×Q → {Make,Help,Hurt,Break,...}` | `Contribution(element, ContribType, quality)` | 7 `ContribType` phải gộp về 2 lớp: `{MAKE,HELP,SOME_PLUS}→Make⁺`, `{HURT,BREAK,SOME_MINUS}→Break⁻`, `UNKNOWN` loại khỏi propagation (cả 2 bài đều loại trừ contribution non-sufficient khỏi suy luận tự động) |
| Dependency | `Dependency(depender, dependum, dependee)` | Khớp 1-1 |
| `ActorDef`, scope | `ActorDef(name, kind, elements, ...)`, `ActorKind={ACTOR,ROLE,AGENT}` | Dùng cho mapping actor↔pool/lane |
| `GoT(GM) = G ∪ T` | cần hàm tiện ích `IStarModel.goalsAndTasks()` (mới) | Không có sẵn, cần thêm (method thuần đọc, không đổi field) |

### 2.2. Phía BPMN2 — `org.vnu.sme.goal.bpmn2.mm`

| Formal | Class Java thật | Ghi chú |
|---|---|---|
| `V=A⊎G`, `E⊆V×V` | `FlowNode` (sealed), `SequenceFlow(source,target,condition)` | Không có "place" kiểu Petri net — suy ra token semantics trực tiếp trên `FlowNode`/`SequenceFlow` |
| `T(G)∈{AND,IOR,XOR,DISC}` | `GatewayType={XOR,AND,OR,EVENT_BASED}` | `OR`≈`IOR`; không có `DISC`, bỏ qua ở MVP |
| Pool/Lane | `Pool`, `Lane` | Khớp — dùng thẳng cho actor mapping |
| Message flow | `MessageFlow`, `Bpmn2Collaboration.getMessageFlows()` | Dùng cho choreography (Gröner Definition 9) — **ngoài phạm vi MVP**, xem §6.4 |

**Kết luận đối chiếu**: cấu trúc tĩnh đã đủ cho toàn bộ mapping + pre-check. Tầng *động*
(marking/LTS) **chưa tồn tại ở đâu cả** trong repo — là phần thật sự mới, thiết kế chi tiết ở §5.

---

## 3. Phương pháp — theo khung 6 khái niệm của JUCS

### 3.1. Metamodel (Bước 1) — đã có sẵn

`IStarModel` (nguồn) và `Bpmn2Collaboration` (đích/hiện thực hoá) đóng vai trò 2 metamodel ở 2
mức trừu tượng — đúng vai trò Use Case MM / Design MM trong JUCS §3.3.

### 3.2. Correspondence model (Bước 2) — thay cho Correspondence Graph của TGG

Không cài TGG engine đầy đủ — hiện thực hoá vai trò CG bằng **mapping model tường minh**
(Definition 3.8 của Caballero-Villalobos + nguyên tắc actor-mapping của Gröner §4):

```
ElementMapping(String istarElementId, String bpmnNodeId)
ActorMapping(String istarActorName, String bpmnPoolId, String bpmnLaneIdOrNull)
```

Quy tắc:
- `ActorKind.AGENT/ROLE` (internal) → map vào `Lane` trong 1 `Pool`.
- `ActorKind.ACTOR` (thường external) → map vào cả 1 `Pool`.
- Mỗi **leaf** `Goal`/`Task` (`Ch(e)=∅`) → map vào 1 `FlowNode.Task`/`SubProcess`, đúng
  `map: T → GoT(GM) ∪ {ε}` (Definition 3.8). **Ràng buộc quan trọng**: chỉ nên map các phần tử
  **leaf** — luật lan truyền (§3.3.1) chỉ có `P_leaf` gán trực tiếp từ action; map một phần tử
  không phải leaf không kích hoạt luật nào, nên vô nghĩa về mặt ngữ nghĩa (dù cú pháp không cấm) →
  `ConformanceMappingParser`/`ConformanceMapping` phải **validate và cảnh báo** nếu người dùng
  map nhầm 1 non-leaf goal.
- `map(node) = ε` cho gateway/event/task không có ý nghĩa nghiệp vụ tương ứng ("silent transition").

Định dạng file `.map` cụ thể — xem §5.1.

### 3.3. Snapshot pattern (Bước 3) — trạng thái từng bên

JUCS để người dùng viết tay OCL pre/post cho từng action. Ở đây **không cần** — luật lan truyền
của Caballero-Villalobos (Fig. 7) đóng đúng vai trò OCL invariant đó, nhưng **suy ra tự động** từ
cấu trúc `Refinement`/`Contribution` thay vì viết tay mỗi khi thêm action.

#### 3.3.1. Snapshot phía i* — Goal Model Marking (Definition 3.1, Caballero-Villalobos)

```
m̂ ⊆ IE × E,   E := Δ ⊎ (Δ×Δ),   Δ = {⊥,⊤,?}
∀ e:Goal|Task → giá trị ∈ {(?,?), (⊤,⊥), (⊤,⊤)}     // unknown / fulfilled / pending
∀ e:Quality   → giá trị ∈ {?, ⊤, ⊥}                  // unknown / fulfilled / denied
```

Khởi tạo: mọi Goal/Task = `(?,?)`, mọi Quality = `?`.

| Luật | Điều kiện | Hiệu ứng |
|---|---|---|
| `P_leaf` | `e` leaf, action mapped tới `e` vừa fired | `e := (⊤,⊥)` |
| `P_AND` | `Refinement.And(parent,children)`, **mọi** children `(⊤,⊥)` | `parent := (⊤,⊥)` |
| `P_OR` | `Refinement.Or(parent,·)` (gộp theo parent), **≥1** child `(⊤,⊥)` | `parent := (⊤,⊥)` |
| `P_Make` | `Contribution(e,Make⁺,q)`, `e=(⊤,⊥)`, `q=?` | `q := ⊤` |
| `P_Break` | `Contribution(e,Break⁻,q)`, `e=(⊤,⊥)`, `q=?` | `q := ⊥` |
| `BP_fulfill`/`BP_deny` | `q` đã quyết định, có contribution đối lập mới từ goal vừa `(⊤,⊥)` | `q` có thể lật; goal đối lập trước đó → `(⊤,⊤)` pending |

Áp fixpoint sau mỗi lần fire — *"repeatedly apply the rules ... until no rule is enabled"*.

Chuẩn hoá `ContribType` (bắt buộc):
```
Make⁺  := { MAKE, HELP, SOME_PLUS }
Break⁻ := { HURT, BREAK, SOME_MINUS }
UNKNOWN → không tham gia lan truyền tự động (chỉ hiển thị + cảnh báo)
```

#### 3.3.2. Snapshot phía BPMN2 — Marking kiểu WF-net

`Bpmn2Collaboration` không có "place" — suy ra trực tiếp trên `FlowNode`/`SequenceFlow`:

```
Places(pool) := { arc giữa 2 FlowNode kề qua SequenceFlow } ∪ { in_pool trước StartEvent, out_pool sau EndEvent }
Marking m ⊆ Places(pool)⊕   (giả định an toàn: multiset nhưng thực tế 0/1 token/place — "safe net")
```

- `Task`/`SubProcess`: 1 input arc → 1 output arc.
- `Gateway(AND)` split: 1→N đồng thời; join: cần đủ token cả N input.
- `Gateway(XOR)` split: 1→chọn đúng 1; join: fire ngay khi có token trên bất kỳ 1 input.
- `Gateway(OR)` (inclusive): split → chọn ≥1 nhánh (non-deterministic); join: MVP đơn giản hoá
  như XOR-join (giới hạn đã ghi nhận, không track "expected merge set").
- `MessageFlow`: đồng bộ 2 WF-net con qua token chờ — **ngoài phạm vi MVP** (xem §6.4).

> **Giới hạn cần ghi nhận**: `Bpmn2Collaboration` không ép well-formedness kiểu SESE (Gröner
> Definition 3: mỗi gateway đúng 1-vào-N-ra hoặc N-vào-1-ra). Trước khi build LTS, cần bước
> **validate cấu trúc** — `Bpmn2LtsBuilder.validateWellFormed()` (§5.2) — từ chối/cảnh báo nếu
> không thoả, đúng khuyến nghị "design recommendations" của Caballero-Villalobos §4.3.1.

### 3.4. Action (Bước 4)

1 action = 1 bước fire của `FlowNode n` với `map(n) ≠ ε`. Quy tắc chuyển đổi (nguyên văn
Definition 4.1, Caballero-Villalobos):

```
Rule (1) — map(n) = e ≠ ε:
   PM ⊢ s --n--> s'    và    GM ⊢ m̂ ==e==>* m̂'  (fixpoint §3.3.1)
   ⟹  ⟨GM,PM⟩ ⊢ (m̂,s) --n--> (m̂',s')

Rule (2) — map(n) = ε:
   PM ⊢ s --n--> s'
   ⟹  ⟨GM,PM⟩ ⊢ (m̂,s) --n--> (m̂,s')
```

### 3.5. Scenario & Model (Bước 5)

JUCS dựng **1** scenario cụ thể (use case tuyến tính). Với BPMN2 (gateway rẽ nhánh) + i*
(OR-refine rẽ nhánh), 1 scenario không đại diện toàn bộ hành vi → xét **toàn bộ reachable state
space** bằng BFS thay vì chọn 1 nhánh, đúng như phân tích Approve/Deny của
Caballero-Villalobos.

### 3.6. Execution state & Triple transformation (Bước 6) — Product LTS

Trùng khớp với `execution state (st,a^h,a^l)` + hàm `trT` của JUCS §4 — chính là **Product LTS**
`C` (Definition 4.1). Tập trạng thái đích (tương đương "execution finishes" của JUCS):

```
F_C = { (m̂,s) ∈ S_C | ∀q ∈ Qual(GM): m̂(q) = ⊤ }
```

### 3.7. Pre-check cấu trúc (bổ sung từ Gröner, chạy TRƯỚC Product LTS)

Với mỗi `Refinement.And(parent,children)` có mapping đầy đủ sang BPMN:
- `nodes = {map(c) | c ∈ children}`.
- Tồn tại `Gateway(XOR/EVENT_BASED)` mà 2 phần tử `nodes` nằm ở 2 nhánh loại-trừ → **strong
  inconsistency** (ví dụ mẫu chính là *"Send Receipt/Shipment"* của Gröner Definition 7, §7.2).
- Chỉ có `Gateway(OR)` giữa chúng (không loại trừ tuyệt đối) → **potential inconsistency**.
- Tuần tự hoặc cùng nhánh `AND`-split → không inconsistency ("realization equivalence").

Với `Refinement.Or(parent,child)`: hầu như không strong-inconsistent trừ khi `child` hoàn toàn
unreachable trong BPMN (kiểm bằng liên thông đơn giản → potential, "dead mapping").

### 3.8. Thuật toán tổng hợp (đầu ra)

```
Bước A — Pre-check tĩnh (§3.7)             → nếu có strong inconsistency: dừng sớm, báo lỗi cụ thể
Bước B — Build Product LTS (forward BFS)   → Algorithm 2, Caballero-Villalobos
Bước C — Weak Compliance (Algorithm 1)     → đúng/sai + counterexample nếu sai
Bước D — Stability (Algorithm 4)           → Weak ∧ Stable ⇒ Strong Compliance (Definition 4.4)
Bước E — Báo cáo 3 mức {Non-compliant | Weakly compliant | Strongly compliant} + trace
```

---

## 4. Thiết kế cấp cài đặt — package `org.vnu.sme.goal.conformance`

Theo đúng quy ước đã có của project (`step-6-transform.md`): logic liên-ngôn-ngữ đặt trong 1
package riêng, không đụng `view/`; nơi có switch lặp lại trên sealed interface → cân nhắc Visitor.

```
org.vnu.sme.goal.conformance/
├── mapping/
│   ├── ElementMapping.java              (record)
│   ├── ActorMapping.java                (record)
│   ├── ConformanceMapping.java
│   └── ConformanceMappingParser.java
├── structural/                          (Bước A — Gröner pre-check, P1)
│   ├── InconsistencyKind.java           (enum: STRONG, POTENTIAL)
│   ├── Inconsistency.java               (record)
│   ├── ExclusivityAnalyzer.java
│   └── StructuralConformanceChecker.java
├── semantics/                           (Bước B–D — Caballero-Villalobos, P2)
│   ├── GoalTaskStatus.java              (sealed: Unknown/Fulfilled/Pending)
│   ├── QualityStatus.java               (enum: UNKNOWN/TRUE/FALSE)
│   ├── ContributionPolarity.java
│   ├── IStarMarking.java
│   ├── IStarPropagation.java
│   ├── BpmnMarking.java
│   ├── IllFormedProcessException.java
│   ├── Bpmn2LtsBuilder.java
│   ├── ProductState.java                (record)
│   ├── Transition.java                  (record)
│   ├── ProductLts.java
│   ├── ComplianceVerdict.java           (enum)
│   ├── ComplianceResult.java            (record)
│   └── ComplianceChecker.java
└── report/                              (P3 — counterexample + UI hook)
    └── ConformanceOverlay.java          (record)
```

### 4.1. `mapping/` — API

```java
package org.vnu.sme.goal.conformance.mapping;

public record ElementMapping(String istarElementId, String bpmnNodeId) {}

public record ActorMapping(String istarActorName, String bpmnPoolId, String bpmnLaneId /* nullable */) {}

public final class ConformanceMapping {
    public ConformanceMapping(List<ElementMapping> elements, List<ActorMapping> actors) {}

    public Optional<String> bpmnNodeOf(String istarElementId);   // map⁻¹ hướng tác giả viết file
    public Optional<String> istarElementOf(String bpmnNodeId);   // map hướng Definition 3.8 (T → GoT ∪ {ε})
    public List<ElementMapping> elements();
    public List<ActorMapping>   actors();

    /** Cảnh báo nếu istarElementId không phải leaf Goal/Task trong gm. */
    public List<String> validate(IStarModel gm, Bpmn2Collaboration pm);
}

public final class ConformanceMappingParser {
    public static ConformanceMapping parse(java.nio.file.Path file) throws java.io.IOException;
}
```

Định dạng file `.map` (parser dòng-lệnh đơn giản, **cố ý không dùng ANTLR** — chi phí không đáng
so với cú pháp `key -> value` phẳng; đã minh hoạ bằng file thật, xem §6):

```
# comment
actor <istarActorName> -> pool <poolId> [lane <laneId>]
map   <istarElementId>  -> node <bpmnNodeId>
```

### 4.2. `structural/` — API (Bước A, §3.7)

```java
package org.vnu.sme.goal.conformance.structural;

public enum InconsistencyKind { STRONG, POTENTIAL }

public record Inconsistency(
    InconsistencyKind kind,
    String istarParentId, String istarChildAId, String istarChildBId,
    String bpmnNodeAId, String bpmnNodeBId,
    String message) {}

public final class ExclusivityAnalyzer {
    /** Với mỗi gateway XOR/EVENT_BASED/OR trong pool, trả về các tập node theo từng nhánh
     *  (BFS bị chặn tại gateway "join" kế tiếp — xem pseudocode bên dưới). */
    public static Map<String, List<Set<String>>> computeBranchSets(Pool pool, Set<GatewayType> types);
}

public final class StructuralConformanceChecker {
    public static List<Inconsistency> check(IStarModel gm, Bpmn2Collaboration pm, ConformanceMapping map);
}
```

Pseudocode `computeBranchSets` (BFS mỗi nhánh dừng khi gặp 1 node có in-degree > 1 = join):

```
computeBranchSets(pool, types):
  result = {}
  for gw in pool.allNodes() where gw is Gateway and gw.type() in types:
      branchSets = []
      for flow in sequenceFlowsStartingAt(gw):
          branch = {}
          frontier = [flow.target()]
          visited = {}
          while frontier not empty:
              n = frontier.pop(); if n in visited: continue
              visited.add(n); branch.add(n.id())
              if n is Gateway and inDegree(pool, n) > 1: continue   // dừng tại join
              frontier.push_all(successorsOf(pool, n))
          branchSets.add(branch)
      result[gw.id()] = branchSets
  return result
```

`StructuralConformanceChecker.check`: với mỗi `Refinement.And(parent, children)`, lấy
`nodes = children.map(map::bpmnNodeOf)`; với mỗi cặp `(a,b) ⊆ nodes`, nếu `a`,`b` nằm trong 2
tập khác nhau của cùng 1 gateway trong `computeBranchSets(pool, {XOR,EVENT_BASED})` → `STRONG`;
nếu trong `computeBranchSets(pool, {OR})` → `POTENTIAL`.

### 4.3. `semantics/` — API (Bước B–D, §3.3–3.8)

```java
package org.vnu.sme.goal.conformance.semantics;

public sealed interface GoalTaskStatus
        permits GoalTaskStatus.Unknown, GoalTaskStatus.Fulfilled, GoalTaskStatus.Pending {
    record Unknown()   implements GoalTaskStatus {}
    record Fulfilled() implements GoalTaskStatus {}
    record Pending()   implements GoalTaskStatus {}
}
public enum QualityStatus { UNKNOWN, TRUE, FALSE }

public final class ContributionPolarity {
    public static boolean isSufficientPositive(ContribType t); // MAKE, HELP, SOME_PLUS
    public static boolean isSufficientNegative(ContribType t); // HURT, BREAK, SOME_MINUS
}

public final class IStarMarking {
    public static IStarMarking initial(IStarModel gm);
    public IStarMarking with(String elementId, GoalTaskStatus s);
    public IStarMarking with(String qualityId, QualityStatus s);
    public boolean isSuccess(IStarModel gm);     // ∀q ∈ Qual(gm): qualityStatus(q) == TRUE  (∈ F_C)
    public Map<String, GoalTaskStatus> goalTaskStatuses();
    public Map<String, QualityStatus>  qualityStatuses();
    // equals/hashCode value-based — bắt buộc, dùng làm key trong visited-set của BFS
}

public final class IStarPropagation {
    /** Bắn 1 leaf element rồi bão hoà theo bảng luật §3.3.1 (fixpoint). */
    public static IStarMarking fire(IStarModel gm, IStarMarking m, String firedLeafId);
}

public final class BpmnMarking {
    public static BpmnMarking initial(Bpmn2Collaboration pm);
    public BpmnMarking moved(Set<String> consumedPlaces, Set<String> producedPlaces);
    // equals/hashCode value-based
}

public class IllFormedProcessException extends RuntimeException {}

public final class Bpmn2LtsBuilder {
    /** Kiểm gateway 1-vào-N-ra hoặc N-vào-1-ra (Gröner Definition 3 analogue). */
    public static void validateWellFormed(Bpmn2Collaboration pm) throws IllFormedProcessException;
    public static Set<FlowNode> enabled(Bpmn2Collaboration pm, BpmnMarking m);
    public static BpmnMarking fire(Bpmn2Collaboration pm, BpmnMarking m, FlowNode n);
}

public record ProductState(IStarMarking istar, BpmnMarking bpmn) {}
public record Transition(FlowNode fired, ProductState next) {}

public final class ProductLts {
    public ProductLts(IStarModel gm, Bpmn2Collaboration pm, ConformanceMapping map);
    public ProductState initial();
    public List<Transition> successors(ProductState s);   // Rule (1)/(2) của Definition 4.1
}

public enum ComplianceVerdict { NON_COMPLIANT, WEAK_COMPLIANT, STRONG_COMPLIANT }

public record ComplianceResult(
    ComplianceVerdict verdict, boolean weak, boolean stable,
    List<FlowNode> counterexampleTrace) {}   // rỗng nếu compliant

public final class ComplianceChecker {
    public static ComplianceResult check(ProductLts c, IStarModel gm, Set<String> qualityIds);
}
```

Pseudocode `ComplianceChecker.check` (transcribe trực tiếp Algorithm 1/3/4 của
Caballero-Villalobos, cộng thêm trích counterexample cho P3):

```
check(C, gm, Q):
  reachable      = forwardBFS(C, C.initial())                       // Algorithm 2
  successStates  = { s in reachable | s.istar().isSuccess(gm) }      // F_C
  terminal       = { s in reachable | C.successors(s).isEmpty() }
  canReachSucc   = backwardBFS(C, successStates)                    // Algorithm 3
  disj           = canReachSucc ∪ (terminal ∩ successStates)
  weak           = reachable ⊆ disj                                  // Algorithm 1

  stable = true
  for q in Q:
      notQ        = { s in reachable | qualityStatuses(s)[q] != TRUE }
      reachNotQ   = backwardBFS(C, notQ)
      stableForQ  = notQ ∪ (reachable \ reachNotQ)
      if reachable ⊄ stableForQ: stable = false; break                // Algorithm 4

  verdict = !weak ? NON_COMPLIANT : (stable ? STRONG_COMPLIANT : WEAK_COMPLIANT)
  trace   = weak ? [] : shortestPathFrom(C.initial(), pickCounterexampleState(reachable, disj))
  return ComplianceResult(verdict, weak, stable, trace)
```

Độ phức tạp thừa hưởng nguyên từ Caballero-Villalobos (đã chứng minh, không cần tự chứng minh
lại): `Θ(|S_C|+m)` cho weak compliance, `O(|Q|·(|S_C|+m))` cho stability.

### 4.4. `report/` — API (P3)

```java
package org.vnu.sme.goal.conformance.report;

public record ConformanceOverlay(
    Set<String> highlightedBpmnNodeIds,
    Set<String> highlightedIstarElementIds,
    String message) {}
```

Tích hợp vào `Bpmn2View`/`IStarView` bằng 1 method thêm **decoration**, không phải nguồn dữ liệu
mới: `view.setOverlay(ConformanceOverlay)` — trong `paintComponent`, sau khi vẽ layout như hiện
tại, nếu `overlay != null` thì vẽ thêm viền đỏ quanh node có id khớp. Đây **không vi phạm** nguyên
tắc "View chỉ đọc MM" của `step-6-transform.md`, vì overlay không phải dữ liệu mô hình mới mà là
gợi ý hiển thị (giống việc tô sáng kết quả tìm kiếm) — cần ghi chú rõ ranh giới này khi code để
không bị hiểu nhầm thành 1 nguồn MM thứ hai.

---

## 5. Case study thật — đã tạo trong repo

Đã tạo 3 file (không phải giả định — **file thật, đúng cú pháp ANTLR hiện có**, đã đối chiếu với
`IStar.g4`/`Bpmn2.g4`):

- `goal/src/main/resources/examples/construction_permit/construction_permit.istar`
- `goal/src/main/resources/examples/construction_permit/construction_permit.bpmn2`
- `goal/src/main/resources/examples/construction_permit/construction_permit.map`

Nội dung mô phỏng đúng business rule của running example (Register/Assess/Approve/Deny,
quality "tăng trưởng kinh doanh thành phố") trong Caballero-Villalobos, viết lại bằng cú pháp
`.istar`/`.bpmn2` thật của project:

**i\* (`construction_permit.istar`)**: `Applicant` nộp đơn (`SubmitApplication` → AND-refine
`ApplicationRegistered`); `Municipality` có `ApplicationProcessed = AND(ApplicationAssessed,
ApplicationClosed)`, `ApplicationAssessed = AND(AssessApplication)`, `ApplicationClosed =
OR(Approve, Deny)`, và `Approve --make--> CityBusinessGrowthSupported`,
`Deny --break--> CityBusinessGrowthSupported`.

**BPMN2 (`construction_permit.bpmn2`)**: 1 pool `CityHall`, 2 lane; luồng tuần tự
`submitApplication → registerApplication → assessApplication → gw_decision(xor) →
{approve → end_approved | deny → end_denied}`. **Cố ý** giữ `gw_decision` là XOR thuần, không có
đường lặp lại — đúng hình dạng WF-net trong bài báo gốc (không phải DCR graph).

**Mapping (`construction_permit.map`)**: `SubmitApplication↔submitApplication`,
`AssessApplication↔assessApplication`, `Approve↔approve`, `Deny↔deny`; các phần tử còn lại
(`registerApplication`, `gw_decision`, sự kiện start/end) là silent (`map(n)=ε`); các goal
AND/OR-parent (`ApplicationRegistered`, `ApplicationProcessed`, `ApplicationAssessed`,
`ApplicationClosed`) không map trực tiếp — chỉ thoả qua lan truyền.

### 5.1. Bước A — Pre-check tĩnh (chạy tay)

`Refinement.And(ApplicationProcessed, {ApplicationAssessed, ApplicationClosed})` — cả 2 con đều
là **goal trung gian, không map trực tiếp sang BPMN** (không có trong `ElementMapping`) → checker
bỏ qua cặp này (không đủ dữ kiện để xác định gateway liên quan — cần đi xuống tận leaf).

`Refinement.And(ApplicationAssessed, {AssessApplication})` — chỉ có 1 con → không có cặp để so
sánh loại trừ → không inconsistency.

Không có `Refinement.And` nào có ≥2 con cùng map trực tiếp sang 2 node BPMN khác nhau trong ví dụ
này (khác với ví dụ minh hoạ giả định ở bản thiết kế trước, nơi `ObtainAuthorization` và
`BuyFlightTickets` cùng map trực tiếp) → **Bước A: không phát hiện strong/potential
inconsistency** cho case study này. Điều này **đúng và có ý nghĩa**: cấu trúc AND ở đây nằm hoàn
toàn phía trên (`ApplicationProcessed`), còn phần map trực tiếp (`AssessApplication`) chỉ có 1
nhánh, không có XOR nào chia tách nó — nên không có gì để báo lỗi ở tầng tĩnh. Bước A xanh, tiếp
tục Bước B.

### 5.2. Bước B–E — Product LTS + compliance (chạy tay đầy đủ)

Ký hiệu tắt: `T` = trạng thái Task/Goal, viết `(⊤,⊥)`=Fulfilled, `(?,?)`=Unknown; Quality viết
`?`/`⊤`/`⊥`.

**Khởi tạo**: mọi Goal/Task = `(?,?)`; `CityBusinessGrowthSupported = ?`; BPMN marking = token
trước `start1`.

**Run 1 — nhánh Approve**: `start1 → submitApplication → registerApplication →
assessApplication → gw_decision → approve → end_approved`.

| Fire | map(n) | Lan truyền i* | Quality |
|---|---|---|---|
| `submitApplication` | `SubmitApplication` | `SubmitApplication:=(⊤,⊥)`; `P_AND`→`ApplicationRegistered:=(⊤,⊥)` (1 con, đủ) | `?` |
| `registerApplication` | ε | (không đổi) | `?` |
| `assessApplication` | `AssessApplication` | `AssessApplication:=(⊤,⊥)`; `P_AND`→`ApplicationAssessed:=(⊤,⊥)`; `ApplicationProcessed` cần cả `ApplicationClosed` nữa → vẫn `(?,?)` | `?` |
| `approve` | `Approve` | `Approve:=(⊤,⊥)`; `P_OR`→`ApplicationClosed:=(⊤,⊥)`; `P_AND`→ nay đủ 2 con → `ApplicationProcessed:=(⊤,⊥)`; `P_Make`(`Approve→CityBusinessGrowthSupported`) | `⊤` |

→ tại `end_approved`: `isSuccess = (CityBusinessGrowthSupported == ⊤)` = **true** → đây là 1
trạng thái thuộc `F_C`, và nó cũng là trạng thái **terminal** (BPMN end event, không có
successor) → thoả điều kiện thứ 2 của Definition 4.2 (terminal ⇒ đã ∈ F_C). Nhánh này OK.

**Run 2 — nhánh Deny**: `start1 → submitApplication → registerApplication →
assessApplication → gw_decision → deny → end_denied`.

Tương tự tới `assessApplication`. Sau đó:

| Fire | map(n) | Lan truyền i* | Quality |
|---|---|---|---|
| `deny` | `Deny` | `Deny:=(⊤,⊥)`; `P_OR`→`ApplicationClosed:=(⊤,⊥)`; `P_AND`→`ApplicationProcessed:=(⊤,⊥)`; `P_Break`(`Deny→CityBusinessGrowthSupported`) | `⊥` |

→ tại `end_denied`: **terminal** (không có successor) nhưng `isSuccess = false`
(`CityBusinessGrowthSupported = ⊥ ≠ ⊤`) → **vi phạm** điều kiện thứ 2 của Definition 4.2 (terminal
mà chưa ∈ F_C, không có cách quay lại vì đã terminal).

**Kết quả `ComplianceChecker.check`**:
- `weak = false` (trạng thái sau `deny → end_denied` là terminal, không ∈ `F_C`, không thể sửa).
- `verdict = NON_COMPLIANT`.
- `counterexampleTrace = [start1, submitApplication, registerApplication, assessApplication,
  gw_decision, deny, end_denied]`, kèm thông điệp: *"quality `CityBusinessGrowthSupported` bị
  denied tại trạng thái cuối `end_denied`; không tồn tại bước tiếp theo để đạt trạng thái mọi
  quality được thoả."*

**Đối chiếu để kiểm chứng thiết kế**: kết quả này **khớp chính xác** với phát hiện của
Caballero-Villalobos cho WF-net gốc của họ (§4.3: *"the WF-net is not weakly compliant ... either
executes t6:Approve ... or t8:Deny ... and then terminates"*) — case study dựng lại trên cú pháp
thật của project cho ra đúng kết luận đã biết trong tài liệu tham khảo, là 1 bằng chứng thiết kế
đúng (sanity check) trước khi viết code thật.

### 5.3. Ghi chú diễn giải quan trọng (không che giấu giới hạn của framework)

`NON_COMPLIANT` ở đây **không đồng nghĩa "BPMN sai"**. Trong đời thực, một số đơn xin phép **nên**
bị từ chối — buộc mọi nhánh Deny phải cuối cùng dẫn tới "tăng trưởng kinh doanh được hỗ trợ" là vô
lý về nghiệp vụ. Bản thân Caballero-Villalobos cũng gặp đúng vấn đề này: DCR graph của họ "vượt
qua" được weak compliance chỉ vì nó cho phép quay lại đăng ký/thẩm định vô hạn lần sau khi bị Deny
— một cách "lách" hình thức hơn là phản ánh đúng ý nghĩa nghiệp vụ. Điều này gợi ý: tiêu chí weak
compliance (dạng "buộc luôn có đường tới thoả mãn") phù hợp nhất khi quality đại diện cho *nghĩa
vụ phải hoàn thành được* (ví dụ "đơn phải được xử lý xong", không quan trọng Approve/Deny), còn
khi quality thiên về *kết quả mong muốn cụ thể* (như "tăng trưởng kinh doanh") thì cần mô hình goal
tinh tế hơn (ví dụ dùng `Some+`/`Some-` non-sufficient, hoặc tách quality riêng cho "xử lý xong"
và "được duyệt") — đây là giới hạn đã biết của framework nguồn, không phải lỗi của thiết kế này,
nhưng cần nêu rõ khi trình bày kết quả cho người dùng cuối.

---

## 6. Đánh giá khả năng triển khai thực nghiệm

### 6.1. Đã có sẵn, dùng ngay được

- Grammar ANTLR4 + AST + Factory + MM đầy đủ cho `.istar`/`.bpmn2`.
- 3 file case study thật (`construction_permit.*`), đã trace tay khớp kết quả tài liệu tham khảo.
- Quy ước package sẵn có (`step-6-transform.md`) cho biết chính xác nơi đặt code mới
  (`conformance/`, không đụng `view/`).

### 6.2. Cần viết mới (không có sẵn ở đâu trong repo)

Toàn bộ 17 class liệt kê ở §4 (`mapping/`, `structural/`, `semantics/`, `report/`) — vì tầng
semantics/execution chưa tồn tại cho cả 2 MM (đúng như `doc/project-analysis.md` đã ghi nhận:
dự án dừng ở MM+View).

### 6.3. Rủi ro/giới hạn kỹ thuật cụ thể (đã phát hiện qua case study)

1. `IStarModel` chưa có `goalsAndTasks()` — cần thêm (method đọc thuần, an toàn).
2. `Bpmn2Collaboration` không ép SESE well-formedness — `Bpmn2LtsBuilder.validateWellFormed()`
   phải tự kiểm tra bằng đếm bậc vào/ra, và quyết định chính sách khi model không thoả (từ chối
   hay chạy best-effort — khuyến nghị: từ chối, kèm thông báo rõ vị trí gateway sai cấu trúc).
3. 7 `ContribType` → 2 lớp Make⁺/Break⁻ là **quyết định mô hình hoá phải công khai** với người
   dùng cuối (ảnh hưởng trực tiếp việc 1 `.istar` có "well-formed" theo Definition 3.4(ii) hay
   không — quality cần ≥1 contribution Make⁺ để đủ điều kiện chạy compliance có ý nghĩa).
4. `Gateway(OR)` join xử lý đơn giản hoá như XOR-join ở MVP — có thể cho kết quả sai với BPMN
   dùng OR-join phức tạp (chờ đúng tập nhánh đã kích hoạt) — ghi nhận, không giải quyết ở MVP.
5. Choreography/`MessageFlow` **ngoài phạm vi MVP** — chỉ xét single-pool trước; multi-pool đồng
   bộ qua message flow (Gröner Definition 9) để P4.2 sau nếu cần.

### 6.4. Lộ trình triển khai (không đổi so với bản trước, nay có class list cụ thể để bám theo)

| Giai đoạn | Nội dung | Class liên quan (đã có API ở §4) |
|---|---|---|
| **P1** | Static pre-check | `mapping/*`, `structural/*` |
| **P2** | Dynamic LTS + compliance | `semantics/*` (trừ report) |
| **P3** | Counterexample & highlight UI | `report/ConformanceOverlay`, hook vào `Bpmn2View`/`IStarView` |
| **P4** | Case study mở rộng + benchmark | Đã có `construction_permit.*`; mở rộng thêm biến thể multi-pool/choreography nếu cần đo hiệu năng như Gröner (100/200/300 activity) |

### 6.5. Kết luận

**Triển khai được, và đã được kiểm chứng bằng trace tay trên dữ liệu thật** (không chỉ lý thuyết
suông) — case study `construction_permit.*` cho kết quả khớp với phát hiện đã công bố trong bài
báo tham khảo, xác nhận thiết kế đúng trước khi đầu tư viết code. Khuyến nghị thứ tự cài đặt: P1
→ P2 → P3, dùng `construction_permit.*` làm test case đầu tiên cho cả `StructuralConformanceChecker`
(kỳ vọng: danh sách rỗng, xem §5.1) lẫn `ComplianceChecker` (kỳ vọng: `NON_COMPLIANT` với trace cụ
thể ở §5.2) — tức đã có sẵn 2 unit test "vàng" (golden test) ngay từ đầu.

---

## 7. Bước tiếp theo

Thiết kế đã đầy đủ tới mức lập trình được trực tiếp. Sẵn sàng bắt đầu viết code theo thứ tự P1 →
P2 → P3 khi được yêu cầu; `construction_permit.istar/.bpmn2/.map` đã có sẵn trong
`goal/src/main/resources/examples/` để dùng làm test ngay từ class đầu tiên.
