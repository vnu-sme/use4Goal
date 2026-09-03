# Luật chuyển ACL → Event-B

## 1. Cơ sở và quy ước

Đường cơ sở là UML-B trong `latex/paperssssss/umlB.tex`, §3–3.5:

- class có carrier set chứa mọi identity có thể có và Machine variable chứa
  current instances;
- attribute là function từ current class extent tới value type;
- association được định kiểu theo hai đầu và multiplicity;
- specialization là inclusion giữa current subclass và current superclass.

ACL giữ các luật này cho Entity và Group. ACL bổ sung Agent, Role occurrence,
Role inheritance bằng `plays_*`, Owner và Compatible. Association được biểu
diễn thống nhất bằng relation cộng invariant multiplicity; cách này tương
đương các partial/total/injective/surjective function trong bảng UML-B, nhưng
dễ sinh tự động cho `[m..n]`.

Mỗi luật dưới đây dùng ví dụ tăng tiến `A_n`: input/output của `R(n)` chứa toàn
bộ phần đã sinh bởi `R1..R(n-1)`. Để tránh lặp boilerplate, `Context_n +=` và
`Machine_n +=` nghĩa là giữ nguyên mọi dòng trước rồi thêm đúng khối hiển thị.

Quy ước tên:

```text
Entity C      → E_C_ID / E_C
Group G       → G_G_ID / G_G
Role R        → R_R_ID / R_R
Agent         → AGENT_ID / AGENTS
Property C.p  → C_p
```

## R1 — Enumeration → carrier set, literals, partition

### Input tích lũy `A1`

```acl
acl v2.0 MeetingSchedulerEB {
  enum TimetableChannel { none, calendar, phone }
}
```

### Mapping

```event-b
CONTEXT MeetingSchedulerEB_ctx
SETS
  TIMETABLECHANNEL
CONSTANTS
  none calendar phone
AXIOMS
  axm_enum:
    partition(TIMETABLECHANNEL, {none}, {calendar}, {phone})
END
```

### Ngữ nghĩa Event-B

`TIMETABLECHANNEL` là value domain bất biến. `partition` bảo đảm ba literal
khác nhau và phủ toàn bộ enum. Enum không có current extent trong Machine.

## R2 — Entity → identity carrier và current extent

### Input tích lũy `A2`

```acl
acl v2.0 MeetingSchedulerEB {
  enum TimetableChannel { none, calendar, phone }
  entity MeetingRecord;
}
```

### Mapping tăng thêm

```event-b
// Context_2 +=
SETS E_MeetingRecord_ID

// Machine_2 sees Context_2 and +=
VARIABLES E_MeetingRecord
INVARIANTS
  inv_entity:
    E_MeetingRecord ⊆ E_MeetingRecord_ID
```

### Ngữ nghĩa Event-B

`E_MeetingRecord_ID` là mọi identity có thể dùng; `E_MeetingRecord` là các
MeetingRecord đang tồn tại. Đây là đúng luật variable class của UML-B, không
dùng một constant population kiểu `E_MeetingRecord ⊆ OBJECTS`.

## R3 — Group → identity carrier và current extent

### Input tích lũy `A3`

```acl
acl v2.0 MeetingSchedulerEB {
  enum TimetableChannel { none, calendar, phone }
  entity MeetingRecord;
  group MeetingUnit { }
}
```

### Mapping tăng thêm

```event-b
// Context_3 +=
SETS G_MeetingUnit_ID

// Machine_3 +=
VARIABLES G_MeetingUnit
INVARIANTS
  inv_group:
    G_MeetingUnit ⊆ G_MeetingUnit_ID
```

### Ngữ nghĩa Event-B

Group dùng đúng class rule của UML-B. Group declaration là cấu trúc tĩnh;
Group occurrences hiện có là Machine state.

## R4 — Attribute → total/partial function

### Input tích lũy `A4`

```acl
acl v2.0 MeetingSchedulerEB {
  enum TimetableChannel { none, calendar, phone }
  entity MeetingRecord { title : String optional; }
  group MeetingUnit {
    detailsDecided : Boolean;
    timeChosen : Boolean;
  }
}
```

### Mapping tăng thêm

```event-b
// Context_4 +=
SETS STRING

// Machine_4 +=
VARIABLES
  MeetingRecord_title
  MeetingUnit_detailsDecided
  MeetingUnit_timeChosen
INVARIANTS
  inv_title:
    MeetingRecord_title ∈ E_MeetingRecord ⇸ STRING
  inv_details:
    MeetingUnit_detailsDecided ∈ G_MeetingUnit → BOOL
  inv_time:
    MeetingUnit_timeChosen ∈ G_MeetingUnit → BOOL
```

### Ngữ nghĩa Event-B

Mặc định ACL `[1]` sinh total function `→`: mỗi current object luôn có đúng
một value. `optional` sinh partial function `⇸`: value có thể undefined.
Đây là luật UML-B attribute; function không hàm ý hai object phải có giá trị
khác nhau.

## R5 — Role và Agent tổng hợp → hai extent riêng

### Input tích lũy `A5`

```acl
acl v2.0 MeetingSchedulerEB {
  enum TimetableChannel { none, calendar, phone }
  entity MeetingRecord { title : String optional; }
  role MeetingParty { name : String; phone : String; hasCalendar : Boolean; }
  role Participant {
    timetableCollected : Boolean;
    timetableChannel : TimetableChannel;
    notified : Boolean;
    attended : Boolean;
  }
  group MeetingUnit { detailsDecided : Boolean; timeChosen : Boolean; }
}
```

### Mapping tăng thêm

```event-b
// Context_5 +=
SETS AGENT_ID R_MeetingParty_ID R_Participant_ID

// Machine_5 +=
VARIABLES AGENTS R_MeetingParty R_Participant
INVARIANTS
  AGENTS ⊆ AGENT_ID
  R_MeetingParty ⊆ R_MeetingParty_ID
  R_Participant ⊆ R_Participant_ID
  MeetingParty_name ∈ R_MeetingParty → STRING
  MeetingParty_phone ∈ R_MeetingParty → STRING
  MeetingParty_hasCalendar ∈ R_MeetingParty → BOOL
  Participant_timetableCollected ∈ R_Participant → BOOL
  Participant_timetableChannel ∈ R_Participant → TIMETABLECHANNEL
  Participant_notified ∈ R_Participant → BOOL
  Participant_attended ∈ R_Participant → BOOL
```

### Ngữ nghĩa Event-B

Agent là player identity; Role occurrence là một participation occurrence.
Hai tập không đồng nhất. Mỗi Role có identity pool riêng vì Role inheritance
của ACL không phải UML subtype identity.

## R6 — Role inheritance → `plays` relation

### Input tích lũy `A6`

`A6` giữ `A5`, thay Role Participant bằng:

```acl
role Participant extends MeetingParty {
  timetableCollected : Boolean;
  timetableChannel : TimetableChannel;
  notified : Boolean;
  attended : Boolean;
}
```

### Mapping tăng thêm

```event-b
VARIABLES plays_MeetingParty plays_Participant
INVARIANTS
  plays_MeetingParty ∈ AGENTS ↔ R_MeetingParty
  plays_Participant ∈ R_MeetingParty ↔ R_Participant

  ∀mp·mp∈R_MeetingParty ⇒
    plays_MeetingParty∼[{mp}] ≠ ∅
  ∀mp,a1,a2·mp∈R_MeetingParty ∧
    a1∈plays_MeetingParty∼[{mp}] ∧
    a2∈plays_MeetingParty∼[{mp}] ⇒ a1=a2

  ∀p·p∈R_Participant ⇒
    plays_Participant∼[{p}] ≠ ∅
  ∀p,mp1,mp2·p∈R_Participant ∧
    mp1∈plays_Participant∼[{p}] ∧
    mp2∈plays_Participant∼[{p}] ⇒ mp1=mp2
```

### Ngữ nghĩa Event-B

Một Participant occurrence phải được đúng một MeetingParty occurrence cha
đóng; MeetingParty đó lại được đúng một Agent đóng. Không sinh
`R_Participant ⊆ R_MeetingParty`. Entity/Group inheritance vẫn dùng subset
theo UML-B; chỉ Role inheritance dùng play chain.

## R7 — Owner và multiplicity → scoped relation

### Input tích lũy `A7`

`A7` giữ `A6`, bổ sung các Role con và owner declarations:

```acl
role Initiator extends MeetingParty;
role Organizer extends MeetingParty;
role Secretary extends MeetingParty;

group MeetingUnit {
  detailsDecided : Boolean;
  timeChosen : Boolean;
  Initiator [1]; Organizer [1]; Secretary [0..1]; Participant [2..*];
}
```

### Mapping tăng thêm

```event-b
VARIABLES
  owns_Initiator owns_Organizer owns_Secretary owns_Participant
INVARIANTS
  owns_Initiator ∈ G_MeetingUnit ↔ R_Initiator
  owns_Organizer ∈ G_MeetingUnit ↔ R_Organizer
  owns_Secretary ∈ G_MeetingUnit ↔ R_Secretary
  owns_Participant ∈ G_MeetingUnit ↔ R_Participant

  // exactly one Initiator per MeetingUnit
  ∀g·g∈G_MeetingUnit ⇒ owns_Initiator[{g}] ≠ ∅
  ∀g,i1,i2·g∈G_MeetingUnit ∧
    i1∈owns_Initiator[{g}] ∧ i2∈owns_Initiator[{g}] ⇒ i1=i2

  // each owned occurrence has exactly one owner
  ∀p·p∈R_Participant ⇒ owns_Participant∼[{p}] ≠ ∅
  ∀p,g1,g2·p∈R_Participant ∧
    g1∈owns_Participant∼[{p}] ∧
    g2∈owns_Participant∼[{p}] ⇒ g1=g2

  // lower bound 2
  ∀g·g∈G_MeetingUnit ⇒
    ∃p1,p2·p1∈owns_Participant[{g}] ∧
      p2∈owns_Participant[{g}] ∧ p1≠p2
```

### Ngữ nghĩa Event-B

Multiplicity ở target ràng buộc direct image; multiplicity ở source ràng
buộc inverse image. `[0..1]` là at-most-one; `[1]` là existence + uniqueness;
`[m..n]` dùng witnesses và uniqueness, không dùng `card` khi chưa biết hữu hạn.
Owner ACL chỉ từ Group tới Role hoặc Group con. Group–Entity whole/part phải là
Composition, không phải Owner.

## R8 — Compatible → loại cặp khỏi conflict mặc định

### Input tích lũy `A8`

`A8` giữ `A7`, trong `MeetingUnit` thêm:

```acl
Initiator compatible Participant;
Organizer compatible Secretary;
```

### Mapping tăng thêm

Không sinh runtime variable `compatible`. Với mỗi cặp Role độc lập cùng owner
Group mà **không** khai compatible, sinh:

```event-b
∀g,r1,r2·
  g↦r1∈owns_R1 ∧ g↦r2∈owns_R2 ⇒
  agentOf_R1(r1) ∩ agentOf_R2(r2) = ∅
```

Không sinh invariant trên cho hai cặp đã khai trong `A8`. Với cùng Role type,
sinh no-duplicate occurrence cho cùng Agent trong cùng Group.

### Ngữ nghĩa Event-B

Compatible là permission làm yếu conflict mặc định, không phải link thay đổi
theo runtime. Role ancestor/descendant không bị coi là hai Role độc lập.

## R9 — Association → relation + endpoint multiplicity

### Input tích lũy `A9`

`A9` giữ `A8`, thêm:

```acl
association knowsPhoneOf {
  MeetingParty [*] role knower;
  MeetingParty [*] role knownContact;
}
```

### Mapping tăng thêm

```event-b
VARIABLES knowsPhoneOf
INVARIANTS
  knowsPhoneOf ∈ R_MeetingParty ↔ R_MeetingParty
```

### Ngữ nghĩa Event-B

Association chỉ là link, không mang ownership. `[*]` ở cả hai đầu không thêm
bound. Với target `[1]`, direct image phải tồn tại và unique; với source `[1]`,
inverse image phải tồn tại và unique. Đây là relation-form tương đương bảng
function/multiplicity của UML-B.

## R10 — Aggregation → weak whole–part relation

### Input tích lũy `A10`

`A10` giữ `A9`, thêm:

```acl
aggregation references {
  MeetingUnit [*] role referringUnit;
  MeetingRecord [*] role sharedRecord;
}
```

### Mapping tăng thêm

```event-b
VARIABLES references
INVARIANTS
  references ∈ G_MeetingUnit ↔ E_MeetingRecord
```

### Ngữ nghĩa Event-B

Aggregation có cùng relation core với Association vì ACL chưa định nghĩa
cascade lifecycle. Nó vẫn là concept nguồn riêng và được giữ trong trace. Nếu
sau này có delete Events, weak part không bị bắt buộc xóa theo aggregate.

## R11 — Composition → relation + unique composite

### Input tích lũy `A11`

`A11` giữ toàn bộ `A10`, thêm:

```acl
composition containsRecord {
  MeetingUnit [1] role container;
  MeetingRecord [0..*] role record;
}
```

### Mapping tăng thêm

```event-b
VARIABLES containsRecord
INVARIANTS
  containsRecord ∈ G_MeetingUnit ↔ E_MeetingRecord

  // unique composite for every part
  ∀r,g1,g2·r∈E_MeetingRecord ∧
    g1∈containsRecord∼[{r}] ∧
    g2∈containsRecord∼[{r}] ⇒ g1=g2
```

### Ngữ nghĩa Event-B

Composition thêm unique composite. Multiplicity `[1]` ở composite end còn
buộc mỗi current part có một composite. Khi có delete Events, Composition phải
bổ sung cascade/guard; ACL-only không tự phát minh lifecycle.

## R12 — Entity/Group generalization → subclass extent inclusion

### Input tích lũy `A12`

`A12` giữ `A11`, thêm:

```acl
entity AuditedRecord extends MeetingRecord;
group OnlineMeetingUnit extends MeetingUnit { }
```

### Mapping tăng thêm

```event-b
VARIABLES E_AuditedRecord G_OnlineMeetingUnit
INVARIANTS
  E_AuditedRecord ⊆ E_MeetingRecord
  G_OnlineMeetingUnit ⊆ G_MeetingUnit
```

### Ngữ nghĩa Event-B

Subclass occurrence chính là superclass occurrence và dùng cùng root identity
pool. Đây là UML-B specialization. ACL hiện không có `abstract`/disjoint
generalization set nên không tự sinh coverage hoặc sibling-disjointness.

## R13 — INITIALISATION → empty current population

### Input tích lũy `A13`

`A13=A12`; không thêm source concept. Đây là bước đóng Machine.

### Mapping tăng thêm

```event-b
EVENT INITIALISATION
THEN
  AGENTS ≔ ∅
  E_MeetingRecord ≔ ∅
  E_AuditedRecord ≔ ∅
  G_MeetingUnit ≔ ∅
  G_OnlineMeetingUnit ≔ ∅
  R_MeetingParty ≔ ∅
  R_Initiator ≔ ∅
  R_Organizer ≔ ∅
  R_Secretary ≔ ∅
  R_Participant ≔ ∅
  MeetingUnit_detailsDecided ≔ ∅
  MeetingUnit_timeChosen ≔ ∅
  plays_MeetingParty ≔ ∅
  plays_Participant ≔ ∅
  owns_Participant ≔ ∅
  knowsPhoneOf ≔ ∅
  references ≔ ∅
  containsRecord ≔ ∅
  // mọi variable còn lại cũng ≔ ∅
END
```

### Ngữ nghĩa Event-B

ACL mô tả state space/invariants, không mô tả create/delete operations. Empty
current extents thỏa total attributes vì empty function có domain empty. Một
population cụ thể phải đến từ constructor Events hoặc refinement tiếp theo.

## 2. Áp dụng vào `mtg.acl`

File `goal/src/main/resources/examples/mtg/mtg.acl` dùng R1, R3–R9 và R13:

```text
TimetableChannel          → TIMETABLECHANNEL + partition
MeetingUnit               → G_MeetingUnit_ID / G_MeetingUnit
MeetingParty              → R_MeetingParty_ID / R_MeetingParty
Initiator..Participant    → Role extents + plays chains
attributes                → total functions
MeetingUnit members       → owns_* + multiplicity
compatible pairs          → conflict exclusions
knowsPhoneOf              → Role↔Role relation
```

Target inventory cuối cho đúng file MTG:

```text
Context sets:
  AGENT_ID, STRING, TIMETABLECHANNEL, G_MeetingUnit_ID,
  R_MeetingParty_ID, R_Initiator_ID, R_Organizer_ID,
  R_Secretary_ID, R_Participant_ID
Context constants:
  none, calendar, phone + enum partition
Machine extents:
  AGENTS, G_MeetingUnit, R_MeetingParty,
  R_Initiator, R_Organizer, R_Secretary, R_Participant
Machine attributes:
  MeetingParty_name, MeetingParty_phone, MeetingParty_hasCalendar,
  Participant_timetableCollected, Participant_timetableChannel,
  Participant_notified, Participant_attended,
  MeetingUnit_detailsDecided, MeetingUnit_timeChosen
Machine relations:
  plays_MeetingParty, plays_Initiator, plays_Organizer,
  plays_Secretary, plays_Participant,
  owns_Initiator, owns_Organizer, owns_Secretary, owns_Participant,
  knowsPhoneOf
Machine constraints:
  extent/attribute typing, play total/unique,
  owner multiplicity/total/unique/scope,
  default conflict minus two compatible pairs
```

Nó không khai Entity, Aggregation, Composition hoặc Entity/Group inheritance;
vì vậy các artefact minh họa ở R2, R10, R11, R12 không xuất hiện trong output
MTG.
Project ACL-only đã kiểm chứng trước đây có Rodin markers `0` và proofs
`51/51`; khi luật/code thay đổi phải sinh và chứng minh lại, không tái dùng con
số này như bằng chứng cho phiên bản mới.
