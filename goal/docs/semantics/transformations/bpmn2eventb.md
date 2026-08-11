# Luật chuyển ACL + BPMN → Event-B

## 1. Cơ sở và thứ tự

Đường cơ sở là `latex/paperssssss/formalbpmnEventB.tex`, §3:

- Context chứa carrier sets/constants chung và process-instance identities;
- Machine chứa current process instances và lifecycle state;
- mỗi sequence flow có token marking theo từng process instance;
- flow object trở thành Event tiêu thụ/sinh token;
- merge thông thường tách Event theo incoming; AND-join dùng một Event nhận đủ mọi incoming;
- process attributes là functions theo process instance.

Phần mở rộng của DSL này là lane Role, ACL scope và OCL `pre/post/guard`.
BPMN cần ACL semantic model để resolve kiểu/OCL, nhưng không cần chờ ACL
project được ghi ra đĩa. `T_BPMN(ACL)` và `T_iStar(ACL)` có thể sinh hai target
fragments song song rồi compose. Ví dụ tăng tiến `B_n` giữ toàn bộ BPMN và
Event-B của các luật trước; `+=` chỉ phần mới.

Thứ tự bắt buộc:

```text
R1 process identity/scope
R2 sequence-flow token state
R3 start
R4 activity
R5 OCL pre/post
R6 XOR split
R7 XOR merge
R8 AND split
R9 AND join
R10 end/lifecycle
R11 loop
R12 subprocess
R13 message
R14 hook đồng bộ iStar
```

## R1 — Pool/Process → process declaration, identity và ACL scope

### Input tích lũy `B1`

```bpmn2
model MeetingScheduler {
  pool MeetingOrganization for MeetingUnit {
    lane Initiator; lane Organizer; lane Secretary; lane Participant;
  }
}
```

### Mapping

```event-b
// Context_B1 extends ACL Context
SETS
  PROCESS_DECL PI_MeetingOrganization_ID PROCESS_STATE
CONSTANTS
  p_MeetingOrganization created active completed
AXIOMS
  p_MeetingOrganization ∈ PROCESS_DECL
  partition(PROCESS_STATE, {created}, {active}, {completed})

// Machine_B1 extends ACL Machine
VARIABLES
  PI_MeetingOrganization
  processState_MeetingOrganization
  processScope_MeetingOrganization
INVARIANTS
  PI_MeetingOrganization ⊆ PI_MeetingOrganization_ID
  processState_MeetingOrganization ∈
    PI_MeetingOrganization → PROCESS_STATE
  processScope_MeetingOrganization ∈
    PI_MeetingOrganization → G_MeetingUnit
```

### Ngữ nghĩa Event-B

Một process instance không đồng nhất với MeetingUnit occurrence. Hàm
`processScope` nói process execution nào chạy trên ACL state của MeetingUnit
nào. Đây bám sát paper ở process identity và bổ sung ACL scope để định nghĩa
`self` của OCL.

Các lane `Initiator/Organizer/Secretary/Participant` không sinh carrier hoặc
constant mới. Chúng resolve trực tiếp tới ACL `R_*`, `owns_*` và `plays_*`.
Translator phải báo lỗi nếu lane không khớp một ACL Role.

## R2 — Sequence flow → token function theo process instance

### Input tích lũy `B2`

```bpmn2
model MeetingScheduler {
  pool MeetingOrganization for MeetingUnit {
    lane Initiator; lane Organizer; lane Secretary; lane Participant;
  }
  start start_meeting { lane Initiator trigger none flow decideMeetingDetails }
  activity decideMeetingDetails {
    type task lane Initiator flow end_meeting
  }
  end end_meeting { lane Organizer trigger none }
}
```

### Mapping tăng thêm

Hai source flows sinh hai token functions:

```event-b
VARIABLES
  tk_start_decide
  tk_decide_end
INVARIANTS
  tk_start_decide ∈ PI_MeetingOrganization → ℕ
  tk_decide_end ∈ PI_MeetingOrganization → ℕ
```

### Ngữ nghĩa Event-B

`tk_f(pid)` là số token của process instance `pid` trên flow `f`. Không dùng một marking chung cho mọi process vì sẽ trộn execution. Với fragment 1-safe có thể dùng `at_f ⊆ PI`; nó là specialization `at_f={pid·tk_f(pid)>0∣pid}`, còn token function bám sát paper và hỗ trợ nhiều token.

## R3 — Start Event → tạo process instance và token đầu tiên

### Input tích lũy `B3`

`B3=B2`, bổ sung start OCL:

```bpmn2
start start_meeting {
  lane Initiator trigger none
  pre {[ not self.detailsDecided and not self.timeChosen ]}
  flow decideMeetingDetails
}
```

### Mapping tăng thêm

```event-b
EVENT Start_start_meeting
ANY pid self performer
WHERE
  pid ∈ PI_MeetingOrganization_ID ∖ PI_MeetingOrganization
  self ∈ G_MeetingUnit
  self↦performer ∈ owns_Initiator
  MeetingUnit_detailsDecided(self)=FALSE
  MeetingUnit_timeChosen(self)=FALSE
THEN
  PI_MeetingOrganization ≔ PI_MeetingOrganization ∪ {pid}
  processState_MeetingOrganization ≔
    processState_MeetingOrganization ∪ {pid↦active}
  processScope_MeetingOrganization ≔
    processScope_MeetingOrganization ∪ {pid↦self}
  tk_start_decide ≔ tk_start_decide ∪ {pid↦1}
  tk_decide_end ≔ tk_decide_end ∪ {pid↦0}
END
```

### Ngữ nghĩa Event-B

Start là runtime Event, không phải `INITIALISATION`. Nó chọn một fresh process
identity, bind đúng Group occurrence, yêu cầu một Initiator occurrence trong
scope khởi phát process và tạo token trên outgoing flow. Start precondition
được đánh giá cho `self`, không áp đặt cho mọi MeetingUnit.

## R4 — Activity → Event tiêu thụ/sinh token

### Input tích lũy `B4`

`B4=B3`; activity giữ dạng:

```bpmn2
activity decideMeetingDetails {
  name "Decide Meeting Details"
  type task lane Initiator
  flow end_meeting
}
```

### Mapping tăng thêm

```event-b
EVENT Activity_decideMeetingDetails
ANY pid self performer
WHERE
  pid ∈ PI_MeetingOrganization
  processState_MeetingOrganization(pid)=active
  self=processScope_MeetingOrganization(pid)
  self↦performer ∈ owns_Initiator
  tk_start_decide(pid)>0
THEN
  tk_start_decide(pid) ≔ tk_start_decide(pid)-1
  tk_decide_end(pid) ≔ tk_decide_end(pid)+1
END
```

### Ngữ nghĩa Event-B

Event enabled iff process active, có token và có một ACL Role occurrence đúng
lane trong đúng process scope. `lane Initiator` bắt buộc sinh
`self↦performer∈owns_Initiator`; nó không tạo Role mới. `self` của BPMN OCL
vẫn là Group scope, còn `performer∈R_Initiator` là người thực hiện step.

Nếu cần Agent identity, đi tiếp qua Role inheritance/play chain:

```event-b
ANY agent meetingParty
WHERE
  agent↦meetingParty ∈ plays_MeetingParty
  meetingParty↦performer ∈ plays_Initiator
```

Event parameters xuất hiện trong ProB trace nên đã truy được performer. Nếu
cần audit lâu dài, thêm Machine relation
`executedBy_decideMeetingDetails ⊆ PI_MeetingOrganization × AGENT_ID` và
ghi maplet trong action. Consume/produce token và domain effect vẫn là một
atomic transition.

## R5 — OCL `pre/post` → guard và simultaneous ACL action

### Input tích lũy `B5`

`B5=B4`, activity trở thành:

```bpmn2
activity decideMeetingDetails {
  type task lane Initiator
  pre  {[ not self.detailsDecided ]}
  post {[ self.detailsDecided ]}
  flow end_meeting
}
```

### Mapping tăng thêm

```event-b
EVENT Activity_decideMeetingDetails
ANY pid self
WHERE
  // giữ guards R4
  MeetingUnit_detailsDecided(self)=FALSE
THEN
  // giữ token actions R4
  MeetingUnit_detailsDecided ≔
    MeetingUnit_detailsDecided <+ {self↦TRUE}
END
```

### Ngữ nghĩa Event-B

`pre` đọc before-state nên là guard. `post` chỉ sinh action khi translator suy ra được before/after substitution. Mọi action chạy song song. Post tổng quát như `score'>score` cần `ANY newScore` + guard; expression không hỗ trợ phải làm export thất bại, không được biến post thành before-state guard.

OCL navigation dùng ACL relations:

```text
self.Participant       → owns_Participant[{self}]
p.hasCalendar          → attribute trên parent Role qua plays chain
forAll/exists/select   → Event-B quantifier/comprehension
```

## R6 — XOR split → one Event per outgoing branch

### Input tích lũy `B6`

`B6` giữ `B5`, chèn:

```bpmn2
gateway phoneRequired {
  lane Organizer type xor
  flow requestCall when {[
    self.Participant->exists(p | not p.hasCalendar)
  ]}
  flow collectionComplete default
}
```

### Mapping tăng thêm

```event-b
EVENT XOR_phoneRequired_requestCall
ANY pid self
WHERE
  tk_in(pid)>0
  self=processScope_MeetingOrganization(pid)
  ∃p·p∈owns_Participant[{self}] ∧
    MeetingParty_hasCalendar[plays_Participant∼[{p}]]={FALSE}
THEN
  tk_in(pid) ≔ tk_in(pid)-1
  tk_request(pid) ≔ tk_request(pid)+1
END

EVENT XOR_phoneRequired_default
ANY pid self
WHERE
  tk_in(pid)>0
  self=processScope_MeetingOrganization(pid)
  ¬(∃p·p∈owns_Participant[{self}] ∧
      MeetingParty_hasCalendar[plays_Participant∼[{p}]]={FALSE})
THEN
  tk_in(pid) ≔ tk_in(pid)-1
  tk_complete(pid) ≔ tk_complete(pid)+1
END
```

### Ngữ nghĩa Event-B

Tách Event tránh disjunctive guard/case split, đúng lựa chọn của paper. Default enabled iff không ordinary guard nào đúng. Nếu guards có thể cùng đúng mà BPMN yêu cầu exclusive, validator phải báo ambiguity hoặc áp priority tường minh.

## R7 — XOR merge → one Event per incoming flow

### Input tích lũy `B7`

`B7` giữ `B6`, thêm XOR merge `collectionComplete` có hai incoming và một outgoing.

### Mapping tăng thêm

```event-b
EVENT XOR_collectionComplete_from_phone
WHERE tk_phone_done(pid)>0
THEN
  tk_phone_done(pid)≔tk_phone_done(pid)-1
  tk_after_merge(pid)≔tk_after_merge(pid)+1
END

EVENT XOR_collectionComplete_from_calendar
WHERE tk_calendar_done(pid)>0
THEN
  tk_calendar_done(pid)≔tk_calendar_done(pid)-1
  tk_after_merge(pid)≔tk_after_merge(pid)+1
END
```

### Ngữ nghĩa Event-B

Một incoming token là đủ; không đợi mọi incoming. Tách Events tránh guard tuyển và giữ trace chính xác incoming flow, đúng chiến lược của paper.

## R8 — AND split → produce every outgoing token

### Input tích lũy `B8`

`B8` giữ `B7`, thêm parallel split:

```bpmn2
gateway parallelStart { lane Organizer type parallel
  flow notifyBranch
  flow auditBranch
}
```

### Mapping tăng thêm

```event-b
EVENT AND_parallelStart
WHERE tk_parallel_in(pid)>0
THEN
  tk_parallel_in(pid) ≔ tk_parallel_in(pid)-1
  tk_notify(pid) ≔ tk_notify(pid)+1
  tk_audit(pid) ≔ tk_audit(pid)+1
END
```

### Ngữ nghĩa Event-B

AND split tạo mọi branch token trong một atomic Event.

## R9 — AND join → require every incoming token

### Input tích lũy `B9`

`B9` giữ `B8`, thêm:

```bpmn2
gateway parallelEnd { lane Organizer type parallel flow finish }
```

### Mapping tăng thêm

```event-b
EVENT AND_parallelEnd
WHERE
  tk_notify_done(pid)>0
  tk_audit_done(pid)>0
THEN
  tk_notify_done(pid)≔tk_notify_done(pid)-1
  tk_audit_done(pid)≔tk_audit_done(pid)-1
  tk_finish(pid)≔tk_finish(pid)+1
END
```

### Ngữ nghĩa Event-B

AND join enabled chỉ khi cùng process instance có đủ mọi incoming token; nó không được tách thành alternative Events như XOR merge.

## R10 — End Event → consume token và complete process

### Input tích lũy `B10`

`B10` giữ mọi phần trước và kết thúc bằng:

```bpmn2
end end_meeting { lane Organizer trigger none }
```

### Mapping tăng thêm

```event-b
EVENT End_end_meeting
ANY pid
WHERE
  pid∈PI_MeetingOrganization
  processState_MeetingOrganization(pid)=active
  tk_to_end(pid)>0
THEN
  tk_to_end(pid) ≔ tk_to_end(pid)-1
  processState_MeetingOrganization(pid) ≔ completed
END
```

### Ngữ nghĩa Event-B

End là token sink. Theo paper, tới một End không mặc nhiên có nghĩa toàn process complete nếu còn token/termination semantics khác; DSL đơn giản này chỉ gán `completed` khi well-formedness bảo đảm End là completion point.

## R11 — Loop → token-cycle Events

### Input tích lũy `B11`

`B11` giữ `B10`; nếu có flow quay lại `checkCalendar`, chính token graph tạo loop.

### Ngữ nghĩa Event-B

Loop không cần VARIANT trừ khi muốn chứng minh termination; chỉ Event khai `convergent` mới cần variant giảm.

## R12 — Subprocess → refined process with parent/outer links

### Input tích lũy `B12`

`B12` giữ `B11` và thêm subprocess. Context thêm subprocess identity carrier; Machine thêm current instances, lifecycle, `parent` và `outer` functions. Subprocess control/data được thêm ở refinement level sau container process.

### Ngữ nghĩa Event-B

`parent` giữ containing process instance; `outer` giữ activity instance tạo subprocess. Cách sắp refinement bảo toàn hierarchy như paper.

## R13 — Message flow → sent set + shared buffer

### Input tích lũy `B13`

`B13` giữ `B12`; với message `Request`, thêm:

```event-b
SETS MESSAGE_ID MESSAGE_TYPE
CONSTANTS m_Request
VARIABLES sent_Request buffer_Request
INVARIANTS
  sent_Request ⊆ MESSAGE_ID
  buffer_Request ⊆ sent_Request
```

Send thêm message vào cả hai tập; Receive yêu cầu message trong buffer rồi xóa khỏi buffer.

### Ngữ nghĩa Event-B

Message flow không phải sequence-flow token. Feature chưa hỗ trợ phải sinh diagnostic, không được hạ sai về ordinary flow.

## R14 — Composition hook → giao diện đồng bộ với iStar fragment

### Input tích lũy `B14`

`B14=B10` cho MTG hiện tại. Khi compose thêm iStar, Context thêm:

```event-b
SETS EVALUATION_PHASE
CONSTANTS READY DIRTY
AXIOMS partition(EVALUATION_PHASE,{READY},{DIRTY})
```

Machine thêm `evaluationPhase`. Mọi Activity/Start/Event có thể đổi ACL state giữ toàn bộ guards/actions trước và thêm:

```event-b
WHERE evaluationPhase=READY
THEN  evaluationPhase≔DIRTY
```

Control-only gateways không đổi ACL có thể giữ `READY`; chính sách đơn giản và an toàn nhất vẫn checkpoint sau mọi BPMN flow-object Event.

### Ngữ nghĩa Event-B

R14 không yêu cầu iStar được dịch sau BPMN. Nó định nghĩa interface của BPMN
fragment cho bước composition. iStar R12 độc lập cung cấp `EvaluateAllGoals`;
composer nối hai fragments. Khi `DIRTY`, không BPMN Activity nào được chạy
tiếp và Event đánh giá là đường duy nhất đưa phase về `READY`.

## 2. Áp dụng vào `mtg.bpmn2`

File MTG dùng R1–R7, R10 và R14; không dùng AND, subprocess hoặc message. Phép dịch mong muốn khác implementation cũ ở một điểm có chủ ý:

```text
cũ: process occurrence = G_MeetingUnit occurrence
mới: PI_MeetingOrganization occurrence --processScope→ G_MeetingUnit
```

Target inventory cuối của BPMN MTG, cộng trên ACL target:

```text
Context:
  PROCESS_DECL, PROCESS_STATE, PI_MeetingOrganization_ID,
  p_MeetingOrganization, created, active, completed
Machine:
  PI_MeetingOrganization,
  processState_MeetingOrganization,
  processScope_MeetingOrganization,
  one tk_<source>_<target> function per MTG sequence flow
Events:
  Start_start_meeting,
  Activity_decideMeetingDetails, Activity_checkCalendar,
  XOR_phoneCollectionRequired_requestSecretaryCall,
  XOR_phoneCollectionRequired_default,
  Activity_requestSecretaryCall,
  Activity_collectConstraintsByPhone,
  Merge_timetableCollectionComplete_<incoming>,
  Activity_chooseTimeAndDate, Activity_announceMeeting,
  Activity_participate, End_end_meeting
```

Pure BPMN fragment chưa chứa `EVALUATION_PHASE`. Composer của BPMN R14 với
iStar R12 thêm `EVALUATION_PHASE`, `READY`, `DIRTY`; mỗi Activity giữ token
actions/OCL effect và chuyển `READY→DIRTY`, còn `EvaluateAllGoals` chuyển
`DIRTY→READY`.

Cách mới bám sát paper BPMN, cho phép nhiều lần chạy process trên cùng một MeetingUnit và giữ process lifecycle độc lập với domain object lifecycle. Kết quả Rodin `98/98` của exporter cũ không phải bằng chứng cho luật mới; phải sửa code, sinh lại và chứng minh lại.
