# Luật chuyển ACL + BPMN + iStar → Event-B

## 1. Cơ sở, khác biệt có chủ ý và kiến trúc hợp thành

Nguồn KAOS được chỉ định là
`latex/paperssssss/kaosEventB.tex`, nhưng file hiện chỉ có tiêu đề, không có
nội dung luật. Cơ sở kiểm được trong workspace là `gstar_eventb.pdf`; nguồn
KAOS công khai gần nhất mô tả cách xây Event-B tăng tiến theo goal-refinement
patterns: parent Achieve goal là abstract Event, subgoals là concrete Events
refine nó. Các nguyên lý dùng ở đây là:

- Context chứa domain/declaration/assumption tĩnh;
- Machine chứa state, invariant và Events;
- Maintain là safety invariant;
- Achieve là progress/milestone, không phải invariant luôn đúng;
- AND/OR giữ conjunctive/alternative satisfaction;
- dependency tạo assumption–guarantee/progress obligation;
- refinement phải tăng tiến và giữ traceability.

Khác biệt có chủ ý: trong hệ này BPMN sinh operational Events. Nếu mỗi iStar
Goal lại thành một Event độc lập như KAOS, hai nguồn transition sẽ cạnh tranh
và Goal có thể tự làm state đúng mà không qua process. Vì vậy iStar sinh cấu
trúc satisfaction và một Event duy nhất `EvaluateAllGoals`; BPMN Events làm
ACL state `DIRTY`, rồi Event này đánh giá **toàn bộ** Goal tree trước checkpoint
tiếp theo.

Đây không tạo phụ thuộc dịch tuần tự BPMN→iStar. Sau khi parse/type-check ACL,
hai phép dịch có thể chạy độc lập hoặc song song:

```text
                         ┌→ T_BPMN(ACL): process, tokens, Events ─┐
ACL semantic model ────┘                                      ├→ Compose
                         └→ T_iStar(ACL): declarations, Eval tree ─┘

Compose → shared Context/Machine + READY/DIRTY protocol
```

`T_iStar` tự sinh được `EvaluateAllGoals` chỉ từ ACL+iStar. `T_BPMN` tự sinh
được process transition system chỉ từ ACL+BPMN. Bước `Compose` mới thêm guard
`READY`, action `DIRTY` vào domain-changing Events và nối chúng với
`EvaluateAllGoals`. Vì vậy không cần ghi BPMN Event-B project trước rồi mới
bắt đầu dịch iStar.

Ví dụ `I_n` tăng tiến: mỗi luật giữ toàn bộ source/target trước và thêm concept
mới. Resource và Obstacle nằm ngoài phạm vi hiện tại. Quality và Contribution
được dịch ở R14 vì Quality có occurrence/marking theo context giống Goal.

Các `partition(GOAL_DECL/TASK_DECL,...)` là closed-world axioms.
Khi một luật thêm declaration, generator thay axiom partition cũ bằng axiom
mới chứa toàn bộ constants đã biết; các invariant/Events trước được giữ nguyên.

## R1 — Actor Role → reuse ACL Role, không sinh actor mới

### Input tích lũy `I1`

```istar
istar MeetingScheduler {
  role Initiator { }
}
```

### Mapping

iStar actor không sinh carrier/constant/extent mới. Nó dùng trực tiếp ACL:

```text
iStar role Initiator ↦ ACL Role Initiator
                      ↦ R_Initiator_ID / R_Initiator / owns_Initiator
```

### Ngữ nghĩa Event-B

`R_Initiator_ID` là identity pool và `R_Initiator` là current occurrences.
Lexical ownership của Goal/Task được giữ trong source-to-target trace và được
enforce bằng kiểu của GoalContext/TaskContext. Actor không tồn tại trong ACL là
lỗi trước khi export.

## R2 — Goal declaration và kind → Context structure + Role trace

### Input tích lũy `I2`

```istar
istar MeetingScheduler {
  role Initiator {
    goal MeetingOrganized : Achieve
    activation {[ true ]}
  }
}
```

### Mapping tăng thêm

```event-b
SETS GOAL_DECL GOAL_KIND
CONSTANTS
  g_MeetingOrganized ACHIEVE MAINTAIN SUSTAIN RECUR goalKind
AXIOMS
  partition(GOAL_DECL,{g_MeetingOrganized})
  partition(GOAL_KIND,{ACHIEVE},{MAINTAIN},{SUSTAIN},{RECUR})
  goalKind ∈ GOAL_DECL → GOAL_KIND
  goalKind(g_MeetingOrganized)=ACHIEVE
```

### Ngữ nghĩa Event-B

Goal name và kind là bất biến nên ở Context. Trace ghi
`g_MeetingOrganized ↦ iStar Initiator ↦ ACL Initiator`. R3 enforce owner
bằng `goalContext_MeetingOrganized ∈ GI_MeetingOrganized → R_Initiator`;
không cần tạo `ACTOR_DECL` hoặc `declaredInGoal` trùng ACL Role.

## R3 — Goal occurrence và GoalContext → Machine state có kiểu

### Input tích lũy `I3`

`I3=I2`; không thêm cú pháp, chỉ hiện thực runtime layer cho Goal đã khai.

### Mapping tăng thêm

```event-b
SETS GOAL_INSTANCE_ID

VARIABLES
  goalInstances goalDecl
  GI_MeetingOrganized goalContext_MeetingOrganized
INVARIANTS
  goalInstances ⊆ GOAL_INSTANCE_ID
  goalDecl ∈ goalInstances → GOAL_DECL
  GI_MeetingOrganized = goalDecl∼[{g_MeetingOrganized}]
  goalContext_MeetingOrganized ∈
    GI_MeetingOrganized → R_Initiator
```

Process Start tạo Goal instances cho tree áp dụng trong process scope; Event
thay đổi Role population phải đồng bộ applicable instances trước evaluation.

### Ngữ nghĩa Event-B

Goal declaration, Goal instance và actor Role occurrence là ba identity khác
nhau. Nhiều Goal episodes có thể có cùng context nếu domain cho phép; muốn mỗi
`(declaration,context)` duy nhất thì thêm injectivity/uniqueness tường minh.

## R4 — OCL activation/condition → named predicates trên ACL state

### Input tích lũy `I4`

```istar
istar MeetingScheduler {
  role Initiator {
    goal MeetingOrganized : Achieve
    activation {[ true ]}
    condition {[ self.group.detailsDecided ]}
  }
}
```

### Mapping tăng thêm

Với `i=goalContext_MeetingOrganized(gi)`:

```event-b
Act_MeetingOrganized(gi) ≙ gi∈GI_MeetingOrganized

Sat_MeetingOrganized(gi) ≙
  MeetingUnit_detailsDecided[
    owns_Initiator∼[{i}]
  ]={TRUE}
```

### Ngữ nghĩa Event-B

OCL predicate là công thức đọc Machine state nên không thể là Context value.
Generator gắn named definition với Goal declaration, rồi dùng nó trong Event,
invariant hoặc LTL. Navigation `self.group` là inverse Owner image; total/unique
Owner làm nó có đúng một Group occurrence.

Mapping OCL tối thiểu:

| OCL | Event-B |
|---|---|
| `self.a` | attribute image/application |
| `self.group` | inverse `owns_*` image |
| `self.R` | direct `owns_R` image |
| association role | direct/inverse relation image |
| inherited Role property | inverse `plays_*` chain rồi attribute |
| `forAll/exists/select` | quantifier/comprehension |
| enum `#x` | literal constant `x` |
| `oclIsUndefined` | value/image `=∅` |

Unsupported OCL là translation error. Implementation cũ lọc kết quả `null`
là không sound và phải bỏ trước khi tuyên bố phép dịch đầy đủ.

## R5 — Task declaration, Role context, pre/post → Context + predicates

### Input tích lũy `I5`

```istar
istar MeetingScheduler {
  role Initiator {
    goal MeetingOrganized : Achieve
    activation {[ true ]}
    condition {[ self.group.detailsDecided ]}

    task DecideDetails > MeetingOrganized
    pre  {[ not self.group.detailsDecided ]}
    post {[ self.group.detailsDecided ]}
  }
}
```

### Mapping tăng thêm

```event-b
SETS TASK_DECL TASK_INSTANCE_ID
CONSTANTS t_DecideDetails
AXIOMS
  partition(TASK_DECL,{t_DecideDetails})

VARIABLES taskInstances taskDecl taskContext_DecideDetails
INVARIANTS
  taskInstances ⊆ TASK_INSTANCE_ID
  taskDecl ∈ taskInstances → TASK_DECL
  taskContext_DecideDetails ∈
    taskDecl∼[{t_DecideDetails}] → R_Initiator
```

```event-b
Pre_DecideDetails(ti) ≙
  MeetingUnit_detailsDecided[
    owns_Initiator∼[{taskContext_DecideDetails(ti)}]
  ]={FALSE}

Post_DecideDetails(ti) ≙
  MeetingUnit_detailsDecided[
    owns_Initiator∼[{taskContext_DecideDetails(ti)}]
  ]={TRUE}
```

### Ngữ nghĩa Event-B

Task không có `TaskKind`; `TASK_DECL` chỉ định danh declarations. Pre/post là
contract quan sát. Task không tự sinh domain-changing Event vì BPMN đã cung
cấp transition. Nếu có explicit Task–Activity mapping, nó chỉ thêm proof/trace
rằng BPMN after-state thỏa `Post_T`.

## R6 — AND refinement → static edges + conjunctive evaluation

### Input tích lũy `I6`

`I6` giữ `I5`, thêm:

```istar
goal MeetingScheduled : Achieve > MeetingOrganized
goal ParticipantsAttended : Achieve > MeetingOrganized
```

### Mapping tăng thêm

```event-b
CONSTANTS g_MeetingScheduled g_ParticipantsAttended AND_REF refinement
AXIOMS
  refinement ⊆ (GOAL_DECL∪TASK_DECL) × GOAL_DECL
  t_DecideDetails↦g_MeetingOrganized ∈ refinement
  g_MeetingScheduled↦g_MeetingOrganized ∈ refinement
  g_ParticipantsAttended↦g_MeetingOrganized ∈ refinement
```

Evaluation được khai triển trực tiếp:

```text
Eval(MeetingOrganized,ctx,Σ) =
  Eval(DecideDetails,ctx,Σ)
  ∧ Eval(MeetingScheduled,ctx,Σ)
  ∧ Eval(ParticipantsAttended,ctx,Σ)
```

### Ngữ nghĩa Event-B

Parent satisfaction dùng children của cùng parent/context, không lượng hóa
trên mọi child instance toàn hệ thống. Đây giữ conjunctive goal refinement như
KAOS, nhưng không sinh child Events cạnh tranh với BPMN Events.

## R7 — OR refinement → alternative satisfaction

### Input tích lũy `I7`

`I7` giữ `I6`, thêm trong Organizer:

```istar
role Organizer {
  goal TimetableCollected : Achieve
  goal ContactedByPhone : Achieve > or TimetableCollected
    condition {[
      self.timetableCollected and self.timetableChannel = #phone
    ]}
  task CollectFromCalendar > or TimetableCollected
    post {[
      self.timetableCollected and self.timetableChannel = #calendar
    ]}
}
```

### Mapping tăng thêm

Context thêm và tái partition Goal/Task declarations; Role vẫn reuse ACL:

```event-b
CONSTANTS
  g_TimetableCollected g_ContactedByPhone t_CollectFromCalendar
AXIOMS
  goalKind(g_TimetableCollected)=ACHIEVE
  goalKind(g_ContactedByPhone)=ACHIEVE
```

`GOAL_DECL`/`TASK_DECL` partition được tái sinh với mọi declaration của I7;
`refinement` thêm hai OR edges. Predicate:

```text
Eval(TimetableCollected,ctx,Σ) =
  Eval(ContactedByPhone,ctx,Σ)
  ∨ Eval(CollectFromCalendar,ctx,Σ)
```

### Ngữ nghĩa Event-B

Ít nhất một alternative cùng context thỏa là đủ. Nếu DSL yêu cầu exclusive
OR, phải thêm mutual-exclusion predicate; ordinary OR không tự cấm hai nhánh
cùng đúng.

## R8 — `forall` → scoped child contexts + universal evaluation

### Input tích lũy `I8`

`I8` giữ `I7`, thêm:

```istar
goal TimetablesCollected : Achieve
goal TimetableCollected : Achieve
  > forall Participant TimetablesCollected
condition {[ self.timetableCollected ]}
```

### Mapping tăng thêm

Context schema được giữ bằng generated typing và trace; depth `0=self`,
`1=self.outer`:

```event-b
goalContext_TimetableCollected ∈
  GI_TimetableCollected → (R_Organizer × R_Participant)
```

Với Organizer `o` trong Group `g`:

```text
Candidates(o)=owns_Participant[{g}]

Eval(TimetablesCollected,o,Σ)=
  Candidates(o)≠∅ ∧
  ∀p·p∈Candidates(o) ⇒
    Eval(TimetableCollected,o↦p,Σ)
```

### Ngữ nghĩa Event-B

Candidate lấy trong ACL owner scope, không phải toàn bộ `R_Participant`. Tập
rỗng không được vacuously fulfilled; nó cho UNKNOWN/not-applicable theo marking
policy. Mỗi branch có context riêng nên không trộn hai MeetingUnit.

## R9 — `pick` và `self.outer` → stable selected context

### Input tích lũy `I9`

`I9` giữ `I8`, thêm:

```istar
goal SecretaryRequested : Achieve
  > pick Secretary ContactedByPhone
activation {[
  self.knownContact->includes(self.outer)
]}
```

### Mapping tăng thêm

```event-b
VARIABLES pickedSecretary
INVARIANTS
  pickedSecretary ∈ GI_ContactedByPhone ⇸ R_Secretary
```

Với context outer-to-inner `organizer↦participant↦secretary`:

```text
self             = secretary
self.outer       = participant
self.outer.outer = organizer
```

Activation dịch thành relation navigation trên đúng bindings:

```event-b
plays_Participant∼[{participant}] ⊆
  knowsPhoneOf[
    plays_Secretary∼[{secretary}]
  ]
```

### Ngữ nghĩa Event-B

`pick` dùng tồn tại nhưng lựa chọn đã ghi phải ổn định qua checkpoints; không
được chọn một Secretary mới mỗi lần đánh giá. Truy cập `outer` quá context
depth là translation error. Equality/navigation so sánh occurrence, không chỉ
Role type.

## R10 — Strategic dependency → static declaration, context transfer, LTL

### Input tích lũy `I10`

`I10` giữ `I9`, thêm:

```istar
role Secretary {
  task CollectByPhone
  pre  {[ self.outer.phone <> '' ]}
  post {[ self.outer.timetableCollected ]}
}

depend Organizer.SecretaryRequested
  -> task CollectByPhone
  -> Secretary.CollectByPhone
```

### Mapping tăng thêm

```event-b
SETS DEP_DECL
CONSTANTS d_SecretaryRequest
AXIOMS
  d_SecretaryRequest ∈ DEP_DECL
```

Dependee context giữ stack nguồn và thêm lexical actor Secretary:

```text
Secretary self
→ Participant self.outer
→ Organizer self.outer.outer
```

Instance link phải conform declaration và ACL scope. Progress property:

```text
G({Requested(d)} => F {Provided(d)})
```

### Ngữ nghĩa Event-B

Dependency không phải arbitrary Role↔Role association. Nó truyền demand và
context giữa đúng depender/dependee instances. Rodin kiểm typing/safety của
links; ProB/LTL kiểm eventual provision.

## R11 — Marking schema → Context fields + Machine valuation

### Input tích lũy `I11`

`I11=I10`; đây là semantic layer áp dụng cho toàn bộ Goal/Task đã có.

### Mapping tăng thêm

```event-b
SETS GOAL_MARK_FIELD TASK_MARK_FIELD INTENTIONAL_STATUS
CONSTANTS A P S Q R UNKNOWN PENDING FULFILLED VIOLATED
AXIOMS
  partition(GOAL_MARK_FIELD,{A},{P},{S})
  partition(TASK_MARK_FIELD,{Q},{R})
  partition(INTENTIONAL_STATUS,
    {UNKNOWN},{PENDING},{FULFILLED},{VIOLATED})

VARIABLES goalA goalP goalS taskQ taskR
INVARIANTS
  goalA⊆goalInstances
  goalP⊆goalInstances
  goalS⊆goalInstances
  taskQ⊆taskInstances
  taskR⊆taskInstances
```

### Ngữ nghĩa Event-B

`A/P/S` và `Q/R` là static field names trong Context; instance nào mang field
nào là runtime Machine state. `P` lưu đã từng satisfy trong activation episode;
`S` lưu stability history; Task `Q/R` là requested/result observed.

## R12 — `EvaluateAllGoals` → full-tree atomic checkpoint evaluation

### Input tích lũy `I12`

`I12=I11`; khi đứng độc lập, Event này có thể đánh giá sau mọi ACL-changing
Event. Khi compose với BPMN R14, `INITIALISATION` đặt marking empty và
`evaluationPhase=DIRTY`; lần đánh giá đầu tạo checkpoint ban đầu.

### Mapping tăng thêm

```event-b
EVENT EvaluateAllGoals
WHERE
  evaluationPhase=DIRTY
THEN
  goalA ≔ {gi·gi∈goalInstances ∧ Act(goalDecl(gi),ctx(gi),Σ)∣gi}

  goalP ≔ {gi·gi∈goalInstances ∧
    Act(goalDecl(gi),ctx(gi),Σ) ∧
    (gi∈goalP ∨ Eval(goalDecl(gi),ctx(gi),Σ))∣gi}

  goalS ≔ {gi·gi∈goalInstances ∧
    (¬Act(goalDecl(gi),ctx(gi),Σ) ∨
     (gi∈goalS ∧
       (gi∉goalP ∨ Eval(goalDecl(gi),ctx(gi),Σ))))∣gi}

  taskQ ≔ EvaluateAllTaskPreconditions(Σ)
  taskR ≔ EvaluateAllTaskPostconditions(Σ,taskQ,taskR)
  evaluationPhase ≔ READY
END
```

`Eval` được generator khai triển từ leaves lên root theo R4–R10 trong cùng
ACL state `Σ`; nó không đọc child marking cũ để tính parent mới.

### Ngữ nghĩa Event-B

Mọi BPMN/domain Event chỉ chạy khi `READY` và kết thúc bằng `DIRTY`. Khi
`DIRTY`, mọi Event khác bị disable; `EvaluateAllGoals` đánh giá **mọi Goal của
mọi iStar model dùng chung ACL**, không tối ưu theo read/write sets. Chỉ state
`READY` là observable checkpoint:

```event-b
evaluationPhase=READY ⇒ GoalConsistency
```

Protocol bắt buộc:

```text
READY --BPMN/ACL Event--> DIRTY --EvaluateAllGoals--> READY
```

Event tách riêng đọc after-state BPMN một cách tự nhiên và tránh phải thế mọi
after-expression vào Goal predicates.

## R13 — Goal kind → safety/history/LTL obligation

### Input tích lũy `I13`

`I13=I12`; áp Goal kinds đã khai ở R2 cho toàn bộ tree.

### Mapping tăng thêm và ngữ nghĩa

```text
Achieve:  G({READY ∧ Act(gi)} => F {READY ∧ Eval(gi)})
Maintain: G({READY ∧ Act(gi)} => {Eval(gi)})
Sustain:  G({READY ∧ Act(gi)} => (!{Eval(gi)} U G {Eval(gi)}))
Recur:    G({READY ∧ Act(gi)} => G F {READY ∧ Eval(gi)})
```

Maintain có thể trở thành Machine invariant được guard bởi `READY`. Achieve,
Sustain, Recur là temporal properties; Rodin invariant proofs không chứng minh
eventuality. ProB/temporal backend phải kiểm chúng cùng dependency LTL.

## R14 — Quality declaration, occurrence và Contribution → instance marking

### Input tích lũy `I14`

`I14=I13`, thêm hai Quality và Contribution đã có trong MTG:

```istar
quality InclusiveCollection
quality FastCollection
goal ContactedByPhone > make InclusiveCollection
task CollectFromCalendar > make FastCollection
```

### Mapping tăng thêm

```event-b
SETS QUALITY_DECL
CONSTANTS q_InclusiveCollection q_FastCollection
AXIOMS
  partition(QUALITY_DECL,{q_InclusiveCollection},{q_FastCollection})

VARIABLES
  Q_InclusiveCollection_I
  Q_InclusiveCollection_TRUE
  Q_InclusiveCollection_FALSE
INVARIANTS
  Q_InclusiveCollection_I ⊆ R_Organizer
  Q_InclusiveCollection_TRUE ⊆ Q_InclusiveCollection_I
  Q_InclusiveCollection_FALSE ⊆ Q_InclusiveCollection_I
  Q_InclusiveCollection_TRUE ∩ Q_InclusiveCollection_FALSE = ∅
```

Trong `EvaluateAllGoals`, đặt `Pos(q,x)` là có contributor Make/Help của `q`
thỏa tại occurrence `x`, và `Neg(q,x)` tương tự cho Hurt/Break:

```event-b
Q_InclusiveCollection_I ≔ R_Organizer
Q_InclusiveCollection_TRUE ≔
  {x·x∈R_Organizer ∧ Pos(q_InclusiveCollection,x)
                    ∧ ¬Neg(q_InclusiveCollection,x)∣x}
Q_InclusiveCollection_FALSE ≔
  {x·x∈R_Organizer ∧ Neg(q_InclusiveCollection,x)
                    ∧ ¬Pos(q_InclusiveCollection,x)∣x}
```

### Ngữ nghĩa Event-B

Quality declaration là cấu trúc Context; Quality occurrence và giá trị của nó
là Machine state. Mỗi occurrence dùng lại đầy đủ Role/outer-context của actor
chứa Quality, không phải một boolean toàn cục. Khi hai polarity cùng đúng,
occurrence không nằm trong TRUE hay FALSE: đó là conflict cần oracle/policy xử
lý, không được tùy tiện chọn contributor cuối cùng.

## 2. Ví dụ MTG cuối và traceability

Áp R1–R14 cho `goal/src/main/resources/examples/mtg/mtg.istar` sau khi ACL và
BPMN đã dịch:

```text
Actor Roles              → reuse ACL R_Initiator/... and owns_*
Goal declarations        → GOAL_DECL + goalKind + Role-context trace
Task declarations        → TASK_DECL + Role-context trace
Quality declarations     → QUALITY_DECL + instance TRUE/FALSE marking
direct OCL               → predicates over ACL Machine state
AND/OR                    → recursive Eval conjunction/disjunction
forall/pick               → scoped context stacks and quantifiers
dependency                → context transfer + instance link + LTL
all markings              → goalA/P/S, taskQ/R
every process checkpoint  → EvaluateAllGoals
```

Target inventory cuối của iStar MTG, cộng trên ACL+BPMN target:

```text
Context:
  GOAL_DECL, TASK_DECL, QUALITY_DECL, GOAL_KIND,
  GOAL_MARK_FIELD, TASK_MARK_FIELD, INTENTIONAL_STATUS,
  DEP_DECL, GOAL_INSTANCE_ID, TASK_INSTANCE_ID,
  all actor/goal/task/dependency constants,
  goalKind, refinement,
  all partition/typing/structure axioms
Machine:
  goalInstances, goalDecl, per-goal goalContext_<G>,
  taskInstances, taskDecl, per-task taskContext_<T>,
  goalA, goalP, goalS, taskQ, taskR, qualityI/TRUE/FALSE,
  picked-candidate variables and instance dependency links
Definitions:
  Act_<G>, Sat_<G>, Pre_<T>, Post_<T>,
  recursively expanded Eval_<element>
Event:
  EvaluateAllGoals (DIRTY→READY, full-tree evaluation)
Temporal properties:
  Achieve/Maintain/Sustain/Recur and dependency LTL,
  observed only at READY checkpoints
```

Implementation dùng contextual occurrence làm identity runtime cho Goal, Task
và Quality; action hợp nhất ép giao thức `READY/DIRTY`, và OCL không hỗ trợ làm
export thất bại. Kết quả Rodin/ProB phải được sinh lại sau mỗi thay đổi luật.
