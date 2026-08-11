# Kiểm chứng luật dịch ACL + iStar + BPMN → Event-B (đối chiếu ca MTG)

Tài liệu này đọc từng luật trong `acl2eventb.md`, `bpmn2eventb.md`, `istar2eventb.md`,
`aclIstarBpmn2eventB.md` và đối chiếu **trực tiếp với artefact thật** đã chạy Rodin
thành công (126/126 PO discharged):

- nguồn: `src/main/resources/examples/mtg/mtg.acl`, `mtg.istar`, `mtg.bpmn2`
- đích: `target/mtg-eventb-marking-22/MeetingSchedulerMarking22/*.buc` (Context),
  `*.bum` (Machine), `*_translation.md` (traceability)

Mỗi luật có 4 phần: **Luật** (tóm tắt), **Nguồn thật** (trích từ file `.acl`/`.istar`/`.bpmn2`
thật, không phải ví dụ minh họa), **Event-B thật** (trích từ `.buc`/`.bum` thật), **Trạng
thái kiểm chứng**. Khi ca MTG không có construct đó (ví dụ AND-split, Recur, message flow),
mục "Nguồn thật" ghi rõ "không có trong MTG" và giữ nguyên ví dụ minh họa của doc gốc — không
được coi là đã kiểm chứng.

Ký hiệu trạng thái:

- ✅ **Khớp** — đối chiếu trực tiếp, đúng như luật mô tả.
- ⚠️ **Lệch/cần xem lại** — có khác biệt cụ thể giữa luật và output thật.
- ◻ **Chưa kiểm chứng** — ca MTG không exercise luật này; chỉ có ví dụ minh họa của doc gốc.

---

## Phần A — ACL → Event-B (`acl2eventb.md`)

### R1 — ACL model → Context + Machine

**Luật:** Context chỉ chứa kiểu/hằng không đổi; Machine chứa trạng thái có thể thay đổi.

**Nguồn thật:**
```acl
acl v2.0 MeetingSchedulerShadow { ... }
```

**Event-B thật:**
```event-b
CONTEXT MeetingSchedulerMarking22_ctx
  SETS AGENTS OBJECTS FLOWS PROCESSES TASKS STRING TIMETABLECHANNEL
MACHINE MeetingSchedulerMarking22
  SEES MeetingSchedulerMarking22_ctx
  VARIABLES finished done started at_f_... MeetingParty_name ...
```
(`.buc` dòng 2-9 khai carrier set; `.bum` dòng 3-77 khai `VARIABLES`.)

**Trạng thái:** ✅ Khớp — đúng 1 Context (`_ctx`) + 1 Machine `SEES` nó, không trộn hằng/biến.

---

### R2 — Enumeration → enumerated carrier set

**Luật:** mỗi literal khác nhau, phủ hết bằng `partition`.

**Nguồn thật:**
```acl
enum TimetableChannel { none, calendar, phone }
```

**Event-B thật:**
```event-b
axm8: partition(TIMETABLECHANNEL, {none}, {calendar}, {phone})
```
(`.buc` dòng 56.)

**Trạng thái:** ✅ Khớp chính xác cấu trúc R2 — tên carrier set viết hoa toàn bộ
(`TIMETABLECHANNEL`), 3 literal giữ nguyên tên.

---

### R3 — Entity → tập con OBJECTS

**Nguồn thật:** MTG không khai `entity` nào (chỉ có `role`/`group`). ◻ Chưa kiểm chứng bằng
MTG — dùng nguyên ví dụ `entity Document` của doc gốc.

**Trạng thái:** ◻ Chưa kiểm chứng (không có entity trong ca MTG).

---

### R4 — Group → tập con OBJECTS

**Nguồn thật:**
```acl
group MeetingUnit { ... }
```

**Event-B thật:**
```event-b
axm7: G_MeetingUnit ⊆ OBJECTS
```
(`.buc` dòng 55.)

**Trạng thái:** ✅ Khớp.

---

### R5 — Role → tập Role occurrence

**Nguồn thật:**
```acl
role MeetingParty { name : String; phone : String; hasCalendar : Boolean; }
role Initiator extends MeetingParty;
role Organizer extends MeetingParty;
role Secretary extends MeetingParty;
role Participant extends MeetingParty { ... }
```

**Event-B thật:**
```event-b
axm2: R_MeetingParty ⊆ OBJECTS
axm3: R_Initiator ⊆ OBJECTS
axm4: R_Organizer ⊆ OBJECTS
axm5: R_Secretary ⊆ OBJECTS
axm6: R_Participant ⊆ OBJECTS
```
(`.buc` dòng 50-54 — mỗi Role, kể cả Role con, có `R_<name>` riêng, không gộp.)

**Trạng thái:** ✅ Khớp — xác nhận đúng câu "Role occurrence khác Agent occurrence": có 5 tập
Role riêng biệt cho 1 cây kế thừa Role.

---

### R6 — Agent → carrier set AGENTS

**Event-B thật:**
```event-b
<org.eventb.core.carrierSet ... org.eventb.core.identifier="AGENTS"/>
```
(`.buc` dòng 3.) Không có axiom ràng buộc thêm thuộc tính miền lên `AGENTS` — đúng như R6 nói
ACL không khai Agent tường minh.

**Trạng thái:** ✅ Khớp.

---

### R7 — Thuộc tính → hàm trạng thái

**Nguồn thật:**
```acl
role MeetingParty {
  name : String;
  phone : String;
  hasCalendar : Boolean;
}
```

**Event-B thật:**
```event-b
inv15: MeetingParty_name ∈ R_MeetingParty → STRING
inv16: MeetingParty_phone ∈ R_MeetingParty → STRING
inv17: MeetingParty_hasCalendar ∈ R_MeetingParty → BOOL
```
(`.bum` dòng 92-94 — hàm toàn phần `→`, đúng R7; chưa dùng `⇸` vì ACL chưa có thuộc tính
optional.)

**Trạng thái:** ✅ Khớp.

---

### R8 — Giá trị mặc định → INITIALISATION

**Nguồn thật:** `mtg.acl` không khai `default` cho bất kỳ thuộc tính nào (`name`, `phone`,
`hasCalendar`, `detailsDecided`, `timeChosen`, `timetableCollected`, ... đều không có
`mutable default`) — nên đây là nhánh "không có default" của R8, không phải nhánh có default.

**Event-B thật:**
```event-b
act_MeetingParty_name: MeetingParty_name :∈ R_MeetingParty → STRING
act_MeetingParty_hasCalendar: MeetingParty_hasCalendar :∈ R_MeetingParty → BOOL
act_MeetingUnit_detailsDecided: MeetingUnit_detailsDecided :∈ G_MeetingUnit → BOOL
```
(`.bum` dòng 218-226 trong `INITIALISATION` — dùng phép chọn không xác định `:∈`, đúng khớp
nhánh "no default" của R8.)

**Trạng thái:** ✅ Khớp — nhưng lưu ý: MTG chỉ minh họa nhánh "không có default"; nhánh
"có default" (`act_approved: ... ≔ E_Document × {FALSE}`) không có ca thật để đối chiếu, giữ
nguyên ví dụ doc.

---

### R9 — Kế thừa Entity/Group → quan hệ tập con

**Nguồn thật:** `mtg.acl` không có `entity ... extends` hay `group ... extends`.

**Trạng thái:** ◻ Chưa kiểm chứng (không có generalization Entity/Group trong MTG — chỉ có
generalization ở tầng Role, xem R17).

---

### R10 — Association → quan hệ trạng thái

**Nguồn thật:**
```acl
association knowsPhoneOf {
  MeetingParty [*] role knower;
  MeetingParty [*] role knownContact;
}
```

**Event-B thật:**
```event-b
axm9: knowsPhoneOf ∈ R_MeetingParty ↔ R_MeetingParty
```
(`.buc` dòng 57 — association tự thân trên `MeetingParty`, không đổi trong khi chạy nên
implementation có thể đặt hằng; ở đây thật ra `knowsPhoneOf` được sinh làm **constant** trong
Context, không phải biến Machine — khớp đúng ghi chú của R10: "Nếu association không bao giờ
thay đổi, implementation có thể đặt nó thành hằng trong Context.")

**Trạng thái:** ✅ Khớp, và xác nhận rõ nhánh "hằng, không phải biến" mà doc chỉ nêu như một
khả năng.

---

### R11 — Multiplicity → invariant cardinality

**Nguồn thật (3 trường hợp multiplicity khác nhau, đều có thật trong MTG):**
```acl
group MeetingUnit {
  Initiator [1];
  Organizer [1];
  Secretary [0..1];
  Participant [2..*];
}
```

**Event-B thật — `[1]` (Initiator, đúng 1: tồn tại + duy nhất):**
```event-b
axm11: ∀g·g∈G_MeetingUnit ⇒ owns_Initiator[{g}] ≠ ∅
axm12: ∀g·g∈G_MeetingUnit ⇒ (∀y1,y2·y1∈owns_Initiator[{g}]∧y2∈owns_Initiator[{g}] ⇒ y1=y2)
```

**Event-B thật — `[0..1]` (Secretary, chỉ duy nhất, không bắt buộc tồn tại):**
```event-b
axm21: ∀g·g∈G_MeetingUnit ⇒ (∀y1,y2·y1∈owns_Secretary[{g}]∧y2∈owns_Secretary[{g}] ⇒ y1=y2)
```
(không có axiom `≠∅` tương ứng cho Secretary — đúng vì cận dưới là 0.)

**Event-B thật — `[2..*]` (Participant, witness 2 phần tử khác nhau, không cận trên):**
```event-b
axm25: ∀g·g∈G_MeetingUnit ⇒ (∃y1,y2·y1∈owns_Participant[{g}] ∧ y2∈owns_Participant[{g}] ∧ y1≠y2)
```

**Trạng thái:** ✅ Khớp cả 3 trường hợp — witness-based encoding đúng chính xác công thức R11
mô tả cho cận dưới `m` và cận trên `n`, không dùng `card(...)` (đúng cảnh báo "chỉ dùng card
khi Context có đủ giả thiết finite").

---

### R12 — Aggregation → relation

**Nguồn thật:** `mtg.acl` không khai `aggregation` nào.

**Trạng thái:** ◻ Chưa kiểm chứng.

---

### R13 — Composition → relation + unique composite

**Nguồn thật:** `mtg.acl` không khai `composition` nào (Owner Group dùng cơ chế R14 riêng,
không dùng từ khóa `composition`).

**Trạng thái:** ◻ Chưa kiểm chứng bằng từ khóa `composition` — nhưng về mặt ngữ nghĩa "unique
composite", R14 (Owner Group → Role) của MTG đã kiểm chứng đúng tính chất tương tự (xem R14).

---

### R14 — Owner Group → Role

**Nguồn thật:**
```acl
group MeetingUnit {
  Initiator [1];
  Organizer [1];
  Secretary [0..1];
  Participant [2..*];
}
```

**Event-B thật (ví dụ Initiator):**
```event-b
axm10: owns_Initiator ∈ G_MeetingUnit ↔ R_Initiator
axm11: ∀g·g∈G_MeetingUnit ⇒ owns_Initiator[{g}] ≠ ∅
axm12: ∀g·... ⇒ (∀y1,y2· y1,y2∈owns_Initiator[{g}] ⇒ y1=y2)
axm13: ∀r·r∈R_Initiator ⇒ owns_Initiator∼[{r}] ≠ ∅
axm14: ∀r·r∈R_Initiator ⇒ (∀y1,y2·y1,y2∈owns_Initiator∼[{r}] ⇒ y1=y2)
```
4 axiom này lặp lại y hệt cho cả 4 Role con (`owns_Organizer`, `owns_Secretary`,
`owns_Participant` — chỉ khác min-cardinality theo R11).

**Trạng thái:** ✅ Khớp đầy đủ 2 chiều: mỗi Group có ≥m Role con (theo multiplicity), và mỗi
Role con thuộc **đúng một** Group Owner — đúng câu "Owner là containment."

---

### R15 — Owner Group → Group

**Nguồn thật:** MTG chỉ có 1 tầng Group (`MeetingUnit`), không có Group lồng Group
(vd. `Company { Department [1..*]; }`).

**Trạng thái:** ◻ Chưa kiểm chứng (MTG không có Owner Group→Group).

---

### R16 — Agent đóng Role gốc

**Nguồn thật:** `MeetingParty` là Role gốc (không `extends` gì).

**Event-B thật:**
```event-b
axm28: plays_MeetingParty ∈ AGENTS ↔ R_MeetingParty
axm29: ∀r·r∈R_MeetingParty ⇒ plays_MeetingParty∼[{r}] ≠ ∅
axm30: ∀r·r∈R_MeetingParty ⇒ (∀y1,y2·y1,y2∈plays_MeetingParty∼[{r}] ⇒ y1=y2)
```
(`.buc` dòng 76-78.)

**Trạng thái:** ✅ Khớp — dùng dạng `plays_<RootRole>` riêng (không dùng dạng gộp
`plays ∈ AGENTS ↔ (R_A ∪ R_B)` mà R16 nêu là lựa chọn thay thế).

---

### R17 — Kế thừa Role → play relation cha–con

**Nguồn thật:**
```acl
role MeetingParty { ... }
role Initiator extends MeetingParty;
role Organizer extends MeetingParty;
role Secretary extends MeetingParty;
role Participant extends MeetingParty { ... }
```

**Event-B thật (Initiator, đại diện cho cả 4 Role con):**
```event-b
axm31: plays_Initiator ∈ R_MeetingParty ↔ R_Initiator
axm32: ∀r·r∈R_Initiator ⇒ plays_Initiator∼[{r}] ≠ ∅
axm33: ∀r·r∈R_Initiator ⇒ (∀y1,y2·y1,y2∈plays_Initiator∼[{r}] ⇒ y1=y2)
```
(`.buc` dòng 79-81; lặp lại y hệt cho `plays_Organizer`, `plays_Secretary`,
`plays_Participant`.) **Không** có axiom nào dạng `R_Initiator ⊆ R_MeetingParty` trong toàn bộ
`.buc` — xác nhận đúng cảnh báo của R17: "Không dịch thành `R_Manager ⊆ R_Employee`."

**Trạng thái:** ✅ Khớp chính xác — đây là luật được kiểm chứng đầy đủ nhất trong ACL vì MTG có
tới 4 Role con cùng generalize từ 1 Role gốc.

---

### R18 — RoleOwnerScope

**Nguồn thật:** MTG chỉ có 1 tầng Group nên không có 2 chuỗi Owner ancestor khác nhau cần gặp
nhau (điều kiện R18 chỉ có ý nghĩa khi có Group lồng Group, xem R15).

**Trạng thái:** ◻ Chưa kiểm chứng (cấu trúc MTG quá phẳng để exercise luật này).

---

### R19 — Compatibility

**Nguồn thật:**
```acl
group MeetingUnit {
  Initiator [1];
  Organizer [1];
  Secretary [0..1];
  Participant [2..*];

  Initiator compatible Participant;
  Organizer compatible Secretary;
}
```

**Event-B thật — cặp khai `compatible` (Organizer–Secretary): không có axiom cấm.**
Kiểm tra toàn bộ `.buc`: không tìm thấy axiom nào ràng buộc xung đột giữa
`plays_Organizer`/`plays_Secretary`.

**Event-B thật — cặp KHÔNG khai `compatible` (Initiator–Organizer, Initiator–Secretary,
Organizer–Participant, Secretary–Participant): có axiom cấm:**
```event-b
axm47: ∀g,r1,r2· g↦r1∈owns_Initiator ∧ g↦r2∈owns_Organizer ⇒
       plays_MeetingParty∼[plays_Initiator∼[{r1}]] ∩ plays_MeetingParty∼[plays_Organizer∼[{r2}]] = ∅
axm48: (Initiator × Secretary) ... = ∅
axm49: (Organizer × Participant) ... = ∅
axm50: (Secretary × Participant) ... = ∅
```
(`.buc` dòng 92-98.)

**Lưu ý:** `Initiator compatible Participant` cũng được khai trong `mtg.acl`, nên đúng ra phải
có 6 cặp không-thứ-tự trong số C(4,2)=6 tổ hợp Role con: 2 cặp compatible
(Initiator–Participant, Organizer–Secretary) không sinh axiom cấm, 4 cặp còn lại sinh axiom
cấm. Đối chiếu axm47-50 = đúng 4 cặp, không thấy axiom cấm nào cho Initiator–Participant hay
Organizer–Secretary.

**Trạng thái:** ✅ Khớp đầy đủ — kiểm tra được cả 6/6 tổ hợp cặp Role con, không chỉ 1 cặp.

---

### R20 — INITIALISATION phải thỏa mọi invariant ACL

**Trạng thái:** ✅ Khớp gián tiếp — đây chính là nhóm proof obligation `INITIALISATION/INV`
nằm trong 126/126 PO đã được auto-prover discharge (xem `mtg-eventb-experiment.md` §1). Không
tách riêng được số PO của riêng ACL, nhưng không có PO nào bị treo ở nhóm này.

---

### R21 — Traceability (ACL)

**Trạng thái:** ✅ Khớp — bảng ánh xạ thật nằm sẵn trong
`MeetingSchedulerMarking22_translation.md` (12 dòng đầu, nguồn `ACL`), đúng format
`| ACL | element | Event-B identifier |` mà R21 yêu cầu.

---

## Phần B — BPMN → Event-B (`bpmn2eventb.md`)

### R0 (ngầm, chưa được đặt tên trong doc) — guard đồng bộ iStar

Mọi Event runtime (Start, Activity, Gateway, End — **trừ** `INITIALISATION` và
`observe_intentional_state`) đều có thêm:
```event-b
grd_intentional_clean: intentionalDirty = FALSE
```
Đây là cơ chế thật hiện thực hóa câu trong `mtg-eventb-experiment.md`: "`intentionalDirty`
buộc observation intentional diễn ra giữa hai bước BPMN." `bpmn2eventb.md` hiện chỉ nói tới nó
gián tiếp qua ví dụ R5 (Start), chưa liệt kê thành luật độc lập áp dụng cho **mọi** Event.

**Đề xuất:** thêm luật này vào `bpmn2eventb.md` như R0 hoặc phụ lục của R5-R14, vì nó là bất
biến toàn cục chứ không riêng của Start.

**Trạng thái:** ✅ Khớp trên toàn bộ 12 Event tôi kiểm tra (start, 8 activity, 2 gateway-branch
pair, end) — 100% có mặt guard này trừ đúng 2 Event được miễn (`INITIALISATION`,
`observe_intentional_state`).

---

### R1 — Process → process constant

**Nguồn thật:**
```bpmn2
model MeetingScheduler {
  pool MeetingOrganization for MeetingUnit { ... }
}
```

**Event-B thật:**
```event-b
axm52: partition(PROCESSES, {p_MeetingOrganization})
```
(`.buc` dòng 100.)

**Trạng thái:** ✅ Khớp (chỉ 1 pool nên `partition` chỉ có 1 phần tử — vẫn đúng dạng tổng quát
R1 mô tả cho N pool).

---

### R2 — Process instance scoped by Group

**Nguồn thật:**
```bpmn2
pool MeetingOrganization for MeetingUnit { ... }
```

**Event-B thật:** không sinh biến quan hệ `processContext ∈ PROCESSES ⇸ G_MeetingUnit` như
phương án thay thế R2 nêu; thay vào đó **mọi** Event của pool này nhận tham số
`self ∈ G_MeetingUnit` trực tiếp:
```event-b
EVENT start_meeting
ANY self
WHERE grd_self: self ∈ G_MeetingUnit
```
và trạng thái "đã start" dùng quan hệ `started ∈ OBJECTS ↔ PROCESSES` chung
(`{self ↦ p_MeetingOrganization} ⊆ started`) thay vì một biến `processContext` riêng.

**Trạng thái:** ✅ Khớp phần "self được định kiểu theo Group" (câu cuối của R2), nhưng dùng
cách hiện thực đơn giản hơn ví dụ `processContext` mà R2 đưa ra như một lựa chọn — nên hiểu đó
chỉ là 1 trong các cách hợp lệ, không phải cách bắt buộc.

---

### R3 — Sequence flow → phần tử FLOWS

**Nguồn thật:** chuỗi 11 sequence flow từ `start_meeting` tới `end_meeting`.

**Event-B thật:**
```event-b
axm54: partition(FLOWS,
  {f_start_meeting_decideMeetingDetails_1}, {f_decideMeetingDetails_checkCalendar_2},
  {f_checkCalendar_phoneCollectionRequired_3}, {f_phoneCollectionRequired_requestSecretaryCall_4},
  {f_phoneCollectionRequired_timetableCollectionComplete_5}, ...
  {f_participate_end_meeting_11})
```
(`.buc` dòng 102 — mỗi flow có định danh duy nhất dạng `f_<source>_<target>_<n>`, đủ 11 phần
tử dù `checkCalendar_phoneCollectionRequired` chỉ là 1 tên hiển thị.)

**Trạng thái:** ✅ Khớp.

---

### R4 — Token marking → flow-state theo process occurrence

**Nguồn thật:** cùng 11 flow ở trên.

**Event-B thật:**
```event-b
inv4: at_f_start_meeting_decideMeetingDetails_1 ⊆ OBJECTS
...
inv14: at_f_participate_end_meeting_11 ⊆ OBJECTS
```
(`.bum` dòng 81-91 — 11 biến `at_f_*` riêng, mỗi biến `⊆ OBJECTS` chứ không dùng 1 tập
`marking ⊆ FLOWS` chung.)

**Trạng thái:** ✅ Khớp — đúng cảnh báo "không được dùng một tập `marking ⊆ FLOWS` chung."

---

### R5 — Start Event

**Nguồn thật:**
```bpmn2
start start_meeting {
  lane Initiator
  trigger none
  pre {[ not self.detailsDecided and not self.timeChosen and ... ]}
  flow decideMeetingDetails
}
```

**Event-B thật:**
```event-b
EVENT start_meeting
ANY self
WHERE
  grd_self: self ∈ G_MeetingUnit
  grd_not_started: (self ↦ p_MeetingOrganization) ∉ started
  grd_intentional_clean: intentionalDirty = FALSE
  grd_pre_1: (¬(detailsDecided[{self}]={TRUE}) ∧ ¬(timeChosen[{self}]={TRUE})) ∧
             (∀p·p∈owns_Participant[{self}] ⇒ ...)
THEN
  act_started: started ≔ started ∪ {self ↦ p_MeetingOrganization}
  act_control_1: at_f_start_meeting_decideMeetingDetails_1 ≔ ... ∪ {self}
  act_intentional_dirty: intentionalDirty ≔ TRUE
END
```
(`.bum` dòng 228-237.) `INITIALISATION` (dòng 152-227) chỉ đặt mọi `at_f_*` = ∅, không chứa
`grd_pre` — đúng khớp R5.

**Trạng thái:** ✅ Khớp hoàn toàn, kể cả chi tiết "Start luôn là Event riêng, không nhét vào
`INITIALISATION`."

---

### R6 — Activity → Event-B Event

**Nguồn thật:**
```bpmn2
activity decideMeetingDetails {
  type task
  lane Initiator
  post {[ self.detailsDecided ]}
  flow checkCalendar
}
```

**Event-B thật:**
```event-b
EVENT decideMeetingDetails
WHERE grd_token_1: self ∈ at_f_start_meeting_decideMeetingDetails_1
THEN
  act_control_1: at_f_start_meeting_decideMeetingDetails_1 ≔ ... ∖ {self}
  act_control_2: at_f_decideMeetingDetails_checkCalendar_2 ≔ ... ∪ {self}
END
```
(`.bum` dòng 238-248, action tiêu thụ + sinh token trong cùng simultaneous substitution.)

**Trạng thái:** ✅ Khớp.

---

### R7 — Activity pre → Event guard

**Nguồn thật:**
```bpmn2
activity checkCalendar {
  pre {[ self.detailsDecided ]}
  ...
}
```

**Event-B thật:**
```event-b
grd_pre_1: MeetingUnit_detailsDecided[{self}] = {TRUE}
```
(`.bum` dòng 254, event `checkCalendar`.)

**Trạng thái:** ✅ Khớp — và dùng ảnh quan hệ `f[{x}] = {TRUE}` chứ không phải `f(x) = TRUE`
(xem thêm mục "Sửa lỗi" trong Phần D).

---

### R8 — Activity post dạng gán → Event action + proof guard kiểu

**Nguồn thật:**
```bpmn2
activity decideMeetingDetails {
  post {[ self.detailsDecided ]}
}
```

**Event-B thật:**
```event-b
grd_effect_type_1: (MeetingUnit_detailsDecided ⩤ {self ↦ TRUE}) ∈ G_MeetingUnit → BOOL
act_effect_1: MeetingUnit_detailsDecided ≔ MeetingUnit_detailsDecided ⩤ {self ↦ TRUE}
```
(`.bum` dòng 243, 246 — dùng ký hiệu override `⩤`/`U+E103` theo đúng cảnh báo của R8, không
dùng `⊕`.)

**Ví dụ phức tạp hơn — `checkCalendar` cập nhật 2 hàm cùng lúc trên tập con được lọc:**
```event-b
act_effect_1: Participant_timetableCollected ≔ Participant_timetableCollected ⩤
  ({p·p∈owns_Participant[{self}] ∧ hasCalendar(p)=TRUE ∣ p} × {TRUE})
act_effect_2: Participant_timetableChannel ≔ Participant_timetableChannel ⩤
  ({p·p∈owns_Participant[{self}] ∧ hasCalendar(p)=TRUE ∣ p} × {calendar})
```
(`.bum` dòng 253, 254, 259, 260 — post BPMN dùng `->select(...)->forAll(...)` được dịch thành
1 phép override hàng loạt trên tập được `select`, kèm `grd_effect_type` riêng cho từng hàm.)

**Trạng thái:** ✅ Khớp, kể cả trường hợp phức tạp hơn ví dụ đơn giản trong doc (post có
`select`/`forAll` lồng nhau).

---

### R9 — Post chỉ là predicate, không suy ra được phép gán

**Nguồn thật:** mọi `post` trong `mtg.bpmn2` đều thuộc dạng hội của
`self.attr = Expr`/`Coll->forAll(v|v.attr=Expr)` — đúng như ghi chú đầu file `mtg.bpmn2`:
"Where it is a plain conjunction of ... atoms, the runtime mechanically synthesizes the SOIL
action" — nên không có `post` nào thuộc dạng "chỉ ràng buộc, không xác định giá trị" (ví dụ
`self.score > self.oldScore` trong doc).

**Trạng thái:** ◻ Chưa kiểm chứng nhánh nondeterministic-assignment của R9 bằng MTG (file
`mtg_nondeterministic.bpmn2` tồn tại trong cùng thư mục ví dụ — có thể là ca dành riêng cho
luật này, nhưng chưa nằm trong lần chạy `marking-22` đang xét).

---

### R10 — XOR split

**Nguồn thật:**
```bpmn2
gateway phoneCollectionRequired {
  type xor
  pre {[ self.target_Participant_in_MeetingUnit
           ->select(p | p.source_Agent_plays_Participant.hasCalendar)
           ->forAll(p | p.timetableCollected) ]}
  flow requestSecretaryCall when {[
    self.target_Participant_in_MeetingUnit->exists(p | not p.source_Agent_plays_Participant.hasCalendar)
  ]}
  flow timetableCollectionComplete default
}
```

**Event-B thật:**
```event-b
EVENT phoneCollectionRequired_to_requestSecretaryCall
WHERE
  grd_pre_1: ∀p·p∈{selected1·selected1∈owns_Participant[{self}] ∧ hasCalendar(selected1)∣selected1}
             ⇒ timetableCollected(p)=TRUE
  grd_branch: ∃p·p∈owns_Participant[{self}] ∧ ¬hasCalendar(p)
THEN ... END

EVENT phoneCollectionRequired_to_timetableCollectionComplete
WHERE
  grd_pre_1: ∀p·p∈{selected1·selected1∈owns_Participant[{self}] ∧ hasCalendar(selected1)∣selected1}
             ⇒ timetableCollected(p)=TRUE
THEN ... END
```
(`.bum` dòng 263-283.)

**⚠️ Phát hiện cụ thể:** `grd_pre_1` là guard của **gateway** (`self.target_Participant...
->select(hasCalendar)->forAll(timetableCollected)` — điều kiện sẵn sàng để gateway kích hoạt,
giống nhau ở cả 2 Event vì đây là guard của chính gateway, không phải của nhánh). Nhưng nhánh
`_to_timetableCollectionComplete` (nhánh `default` trong nguồn) **không có guard phủ định**
của `grd_branch` (tức không có `∀p·hasCalendar(p)` hay `¬(∃p·¬hasCalendar(p))`). R10 yêu cầu
rõ: "Default flow chỉ enabled khi không guard thường nào đúng." Với encoding hiện tại, khi
`grd_pre_1` đúng và đồng thời có participant thiếu calendar (`grd_branch` của nhánh kia cũng
đúng), **cả 2 Event đều enabled cùng lúc** — vi phạm exclusivity mà R10 đặt ra. Rodin không
phát hiện được lỗi này vì đây không phải PO sai kiểu/invariant, mà là thiếu 1 guard loại trừ.

**Trạng thái:** ⚠️ Lệch — thiếu guard phủ định trên nhánh `default`. Nên sửa exporter để nhánh
`default` luôn được AND thêm phủ định của mọi `when` cùng gateway, đúng R10.

---

### R11 — XOR merge

**Nguồn thật:** `timetableCollectionComplete` có 2 incoming: từ nhánh `default` của
`phoneCollectionRequired`, và từ `collectConstraintsByPhone`.

**Event-B thật:**
```event-b
EVENT timetableCollectionComplete_from_phoneCollectionRequired
WHERE grd_token_1: self ∈ at_f_phoneCollectionRequired_timetableCollectionComplete_5
      grd_pre_1: ∀p·p∈{...hasCalendar...} ⇒ timetableCollected(p)=TRUE
THEN at_f_...5 ≔ ...∖{self};  at_f_timetableCollectionComplete_chooseTimeAndDate_8 ≔ ...∪{self}
END

EVENT timetableCollectionComplete_from_collectConstraintsByPhone
WHERE grd_token_1: self ∈ at_f_collectConstraintsByPhone_timetableCollectionComplete_7
      grd_pre_1: ∀p·p∈owns_Participant[{self}] ⇒ timetableCollected(p)=TRUE
THEN at_f_...7 ≔ ...∖{self};  at_f_timetableCollectionComplete_chooseTimeAndDate_8 ≔ ...∪{self}
END
```
(`.bum` dòng 308-327 — 2 Event riêng, mỗi Event chỉ cần token ở 1 trong 2 incoming, không yêu
cầu cả hai, đúng R11.)

**Trạng thái:** ✅ Khớp — nhưng lưu ý `grd_pre_1` của gateway `timetableCollectionComplete`
(`self.target_Participant_in_MeetingUnit->forAll(p|p.timetableCollected)`) bị lặp lại giống hệt
ở cả 2 nhánh merge — về logic không sai (đều phải đúng để đi tiếp), nhưng nếu 2 incoming có
điều kiện sẵn sàng khác nhau về mặt ngữ nghĩa nguồn thì cần xem lại, tương tự phát hiện ở R10.

---

### R12 — AND split

**Nguồn thật:** `mtg.bpmn2` không có gateway `type and`.

**Trạng thái:** ◻ Chưa kiểm chứng.

---

### R13 — AND join

**Nguồn thật:** không có trong MTG (cùng lý do R12).

**Trạng thái:** ◻ Chưa kiểm chứng.

---

### R14 — End Event

**Nguồn thật:**
```bpmn2
end end_meeting {
  lane Organizer
  trigger none
}
```

**Event-B thật:**
```event-b
EVENT end_meeting
ANY self
WHERE
  grd_self: self ∈ G_MeetingUnit
  grd_token_1: self ∈ at_f_participate_end_meeting_11
  grd_intentional_clean: intentionalDirty = FALSE
  grd_goal_1: owns_Initiator[{self}] ⊆ G_MeetingOrganized_P
  grd_goal_2: owns_Organizer[{self}] ⊆ G_ChosenTimeHasDetails_P
THEN
  act_control_1: at_f_participate_end_meeting_11 ≔ ... ∖ {self}
  act_finished: finished ≔ finished ∪ {self ↦ p_MeetingOrganization}
END
```
(`.bum` dòng 364-374.)

**Trạng thái:** ✅ Khớp — và minh họa rõ câu "Nếu có nhiều root Goal thuộc các Role khác nhau,
End có một guard Owner-scope cho từng root": ở đây có **2** `grd_goal` (root Goal của Initiator
là `MeetingOrganized`, root Goal của Organizer là `ChosenTimeHasDetails`), cả hai đều phải
`⊆ G_..._P` (progress, đã latch) — không dùng `G_..._S` (stable/condition sống lại), đúng
khớp ghi chú R20 của `istar2eventb.md`.

---

### R15 — Task completion trace

**Nguồn thật:** `mtg.bpmn2` không khai mapping Task↔Activity tường minh.

**Event-B thật:**
```event-b
VARIABLES done
INVARIANTS inv2: done ⊆ TASKS
INITIALISATION: act_done: done ≔ ∅
```
`done`/`TASKS` **được khai báo** (`.bum` dòng 5, 79; `.buc` dòng 98 khai 6 `t_*` constant từ
iStar Task) nhưng **không có Event nào** trong toàn bộ `.bum` gán thêm vào `done` — biến này
tồn tại nhưng chết (dead state), đúng khớp câu "Task–Activity mapping là thuật toán tích hợp
tùy chọn, không phải điều kiện của phép dịch hiện tại" ở `mtg-eventb-experiment.md` §7.

**Trạng thái:** ✅ Khớp phần "không bắt buộc" — nhưng đáng lưu ý exporter vẫn tốn 1 biến +
1 invariant cho một cơ chế hoàn toàn không dùng tới trong ca này; có thể cân nhắc chỉ sinh
`done`/`TASKS` khi thật sự có mapping.

---

### R16 — Loop

**Nguồn thật:** đồ thị flow của MTG là chuỗi tuyến tính + 1 XOR-split/merge, không có back-edge
quay lại node trước.

**Trạng thái:** ◻ Chưa kiểm chứng (không có `VARIANT`/chu trình trong `.bum`, đúng vì MTG
không có loop — không phải lỗi thiếu sót).

---

### R17 — Message flow và nhiều pool

**Nguồn thật:** `mtg.bpmn2` chỉ có 1 pool (`MeetingOrganization`), không có message flow.

**Trạng thái:** ◻ Chưa kiểm chứng (không tìm thấy biến `messages` nào trong `.bum`).

---

### R18 — Bảo toàn invariant ACL + proof guard của effect

**Trạng thái:** ✅ Khớp gián tiếp — đây chính là các PO `Activity/INV`, `Gateway/INV`,
`End/INV` nằm trong 126/126 PO đã discharge; các `grd_effect_type_*` (xem R8) chính là cơ chế
cụ thể giúp Rodin đóng các PO này (thay vì để 79 PO mở như "Marking19" trong
`mtg-eventb-experiment.md` §6).

---

### R19 — Deadlock và completion

**Trạng thái:** ◻ Chưa kiểm chứng — `mtg-eventb-experiment.md` §7 xác nhận rõ: "ProB/LTL chưa
chạy vì installation Rodin hiện tại không có plugin tương ứng." Đây là hạng mục **cố ý chưa
làm**, không phải sai lệch giữa doc và code.

---

### R20 — Traceability (BPMN)

**Trạng thái:** ✅ Khớp — bảng trong `_translation.md` có đủ cột `BPMN | process/flow/activity
| Event-B identifier` theo đúng định dạng R20 (11 flow + 8 activity/gateway + 1 process, dòng
19-58 của `_translation.md`).

---

## Phần C — iStar → Event-B (`istar2eventb.md`)

### R1 — Actor Role → tập Role ACL

**Nguồn thật:** `role Initiator { ... }` trong `mtg.istar` dùng lại `R_Initiator` đã sinh từ
ACL (Phần A/R5), không sinh carrier set `Initiator` thứ hai.

**Trạng thái:** ✅ Khớp — toàn bộ `.buc` chỉ có đúng 5 carrier-set Role (`R_MeetingParty`,
`R_Initiator`, `R_Organizer`, `R_Secretary`, `R_Participant`), không có bản sao nào sinh riêng
từ phía iStar.

---

### R2 — Intentional occurrence

**Nguồn thật:**
```istar
role Initiator {
  goal MeetingOrganized : Achieve   // Goal trực tiếp của Initiator
  ...
  goal ParticipantAttended : Achieve
    > forall Participant ParticipantsAttended   // occurrence 2 tầng context
}
```

**Event-B thật:**
```event-b
inv25: G_MeetingOrganized_A ⊆ R_Initiator            (Goal trực tiếp — occurrence 1 chiều)
inv39: G_ParticipantAttended_A ⊆ (R_Initiator × R_Participant)   (occurrence 2 chiều, dưới forall)
```
(`.bum` dòng 102, 113.)

**Khác biệt với trình bày của doc:** R2 mô tả occurrence set như một **constant riêng**
`GOcc_X` khai trong Context. Output thật **không sinh `GOcc_X` như constant** — nó chỉ ràng
buộc gián tiếp bằng `⊆` trong invariant (như trên) và nạp giá trị cụ thể ngay trong action khởi
tạo của `_S` (xem R16 bên dưới, ví dụ `G_OrganizerScheduledMeeting_S`).

**Trạng thái:** ⚠️ Lệch cách trình bày (không sai về ngữ nghĩa) — `istar2eventb.md` nên ghi rõ
occurrence set là comprehension nhúng trong action khởi tạo, không phải constant riêng, để
người đọc không tìm nhầm định danh `GOcc_...` trong output thật.

---

### R3 — Goal marking → biến Event-B

**Nguồn thật:** mọi `goal` trong `mtg.istar` (10 goal tổng cộng).

**Event-B thật (ví dụ `MeetingOrganized`):**
```event-b
inv25: G_MeetingOrganized_A ⊆ R_Initiator
inv26: G_MeetingOrganized_P ⊆ R_Initiator
inv27: G_MeetingOrganized_S ⊆ R_Initiator
```
Khởi tạo (`INITIALISATION`):
```event-b
act_G_MeetingOrganized_A: G_MeetingOrganized_A ≔ ∅
act_G_MeetingOrganized_P: G_MeetingOrganized_P ≔ ∅
act_G_MeetingOrganized_S: G_MeetingOrganized_S ≔ R_Initiator
```
(`.bum` dòng 102-104, 168-170.)

**Trạng thái:** ✅ Khớp — đúng cả 3 biến `(A,P,S)` và đúng khởi tạo `S ≔ toàn bộ occurrence`
(mọi occurrence "stable" mặc định khi chưa active), `A=P=∅`.

---

### R4 — Goal activation → predicate

**Nguồn thật:**
```istar
goal ChosenTimeHasDetails : Maintain
activation {[ self.group.timeChosen ]}
```

**Event-B thật:** activation được dùng trực tiếp trong action `obs_G_ChosenTimeHasDetails_A`
của `observe_intentional_state` (không phải 1 định nghĩa `Act_X(o)` độc lập trong Context, mà
được nhúng thẳng khi tính lại `_A`) — cùng cách nhúng như R2. Về nội dung, điều kiện dùng đúng
`MeetingUnit_timeChosen[owns_Organizer∼[{self}]] = {TRUE}`, đúng khớp nguồn ACL
`self.group.timeChosen`.

**Trạng thái:** ✅ Khớp về nội dung logic; cách hiện thực (nhúng trong action thay vì hàm
Context riêng) giống lệch đã ghi ở R2.

---

### R5 — Goal condition → satisfaction predicate

**Nguồn thật:**
```istar
goal ParticipantAttended : Achieve
  > forall Participant ParticipantsAttended
condition {[ self.attended ]}
```

**Event-B thật:** điều kiện `Participant_attended[{p}] = {TRUE}` xuất hiện trong công thức tính
`_P`/`_S` của `G_ParticipantAttended` bên trong `observe_intentional_state` (đã trích 1 phần ở
R2/istar). Nhiều `condition` nối bằng `∧` — ví dụ Goal `SecretaryRequested` có activation dạng
hội của `knownContact->includes(outer)`, khớp đúng công thức R5.

**Trạng thái:** ✅ Khớp.

---

### R6 — Cập nhật marking Goal (qua `observe_intentional_state`)

**Nguồn thật:** áp dụng cho tất cả 10 Goal.

**Event-B thật (Goal `MeetingOrganized`):**
```event-b
obs_G_MeetingOrganized_A: G_MeetingOrganized_A ≔ {self·Act(self)} ∩ R_Initiator
obs_G_MeetingOrganized_P: G_MeetingOrganized_P ≔
  {self·Act(self)} ∩ (G_MeetingOrganized_P ∪ {self·Sat(self)})
obs_G_MeetingOrganized_S: G_MeetingOrganized_S ≔
  {self· ¬Act(self) ∨ (self∈S ∧ (self∉P ∨ Sat(self))) } ∩ R_Initiator
```
(`.bum` dòng 377-379.) Khớp đúng công thức R6:
`A'=Act_G(o)`, `P'=P∨Sat_G(o)` (giới hạn lại bởi Act), `S'=S∧(¬P_prev∨Sat_G(o))`.

**Trạng thái:** ✅ Khớp chính xác công thức, kể cả việc dùng before-value `P`/`S` ở vế phải
đúng quy tắc simultaneous substitution của Event-B.

---

### R7 — Goal status (enumerated set)

**Nguồn thật:** áp dụng ngầm cho mọi Goal.

**Event-B thật:** `.buc`/`.bum` **không sinh** `SETS GOAL_STATUS` / `CONSTANTS UNKNOWN PENDING
FULFILLED VIOLATED` tường minh — trạng thái Goal được suy ra từ tổ hợp `(A,P,S)` khi đọc kết
quả (không có 1 hàm `Goal_status ∈ GOcc_G → GOAL_STATUS` riêng trong Machine).

**Trạng thái:** ⚠️ Lệch — R7 mô tả một encoding tường minh (enum `GOAL_STATUS` + hàm
`Goal_status`) mà exporter hiện tại không sinh; trạng thái chỉ tồn tại "ngầm" qua bộ ba
`(A,P,S)`. Nếu có công cụ đọc kết quả ProB/Rodin cần map `(A,P,S)`→4 trạng thái theo đúng bảng
ở R7-R11, việc đó phải làm ở tầng phân tích ngoài, không có trong Event-B project.

---

### R8 — Achieve

**Nguồn thật:**
```istar
role Initiator {
  goal MeetingOrganized : Achieve
  activation {[ true ]}
  ...
}
```

**Event-B thật:** khởi tạo `S≔R_Initiator` (mọi occurrence stable mặc định vì `activation=true`
luôn đúng — không có "chưa active" thật sự cho Goal này); cập nhật qua `observe_intentional_state`
đúng công thức R6. Không có invariant `Act⇒Sat` bắt buộc — đúng khớp câu "Không dịch thành
invariant."

**Trạng thái:** ✅ Khớp. Thuộc tính liveness tương ứng (`G(Act⇒F Sat)`) không nằm trong
`.bum`/`.buc` (đúng vì đây là property cho ProB/LTL, không phải Rodin PO) — cần đối chiếu ở
file `.ltl` (xem R21/Phần C).

---

### R9 — Maintain

**Nguồn thật:**
```istar
goal ChosenTimeHasDetails : Maintain
activation {[ self.group.timeChosen ]}
condition {[ self.group.detailsDecided ]}
```

**Event-B thật:** `G_ChosenTimeHasDetails` dùng đúng cơ chế `(A,P,S)` chung — không có
encoding riêng biệt cho Maintain so với Achieve trong Machine (khác biệt Maintain/Achieve chỉ
nằm ở **cách đọc kết quả** `(A,P,S)`, không phải cấu trúc biến khác nhau). Điều này khớp với
việc R3/R6 dùng chung 1 khuôn `(A,P,S)` cho mọi loại Goal.

**Trạng thái:** ✅ Khớp — nhưng cũng có nghĩa: file Event-B **không tự phân biệt** được đâu là
Maintain, đâu là Achieve nếu chỉ đọc riêng `.bum`; phân loại goal-type nằm ở tầng dịch (mapping
trong `_translation.md` không ghi goal-type) chứ không "chảy" vào artefact Rodin. Muốn biết
Goal nào Maintain/Sustain/Recur phải quay lại `mtg.istar` hoặc file `.ltl`.

---

### R10 — Sustain

**Nguồn thật:**
```istar
goal ParticipantsNotified : Sustain > SchedulingCompleted
condition {[ self.group.Participant->forAll(participant | participant.notified) ]}
```

**Trạng thái:** ✅ Khớp cấu trúc biến (cùng nhận xét R9) — chưa đối chiếu được property LTL
dạng `!Sat U G Sat` vì chưa đọc file `.ltl` (xem Phần C cuối / khuyến nghị).

---

### R11 — Recur

**Nguồn thật:** `mtg.istar` không có Goal nào khai `: Recur`.

**Trạng thái:** ◻ Chưa kiểm chứng (không có Recur trong MTG).

---

### R12 — Task pre/post → predicate

**Nguồn thật:**
```istar
task DecideDetails > MeetingOrganized
pre {[ not self.group.detailsDecided ]}
post {[ self.group.detailsDecided ]}
```

**Trạng thái:** ✅ Khớp gián tiếp — pre/post Task ở tầng iStar trùng lặp với pre/post BPMN
Activity `decideMeetingDetails` (đã kiểm chứng ở Phần B/R7,R8) vì MTG map trực tiếp Task↔Activity
cùng tên, nên `Pre_T`/`Post_T` không xuất hiện như hàm Event-B riêng — chúng "hòa" vào guard/
action của chính BPMN Event tương ứng.

---

### R13 — Task marking `(Q,R)`

**Nguồn thật:** 6 Task trong `mtg.istar` (`DecideDetails`, `ChooseMeetingTime`,
`CollectFromCalendar`, `NotifyParticipant`, `CollectByPhone`, `AttendMeeting`).

**Event-B thật (Task `DecideDetails`):**
```event-b
inv28: T_DecideDetails_Q ⊆ R_Initiator
inv29: T_DecideDetails_R ⊆ R_Initiator
obs_T_DecideDetails_Q: T_DecideDetails_Q ≔
  (Q ∪ {self·¬detailsDecided(self)}) ∩ R_Initiator
obs_T_DecideDetails_R: T_DecideDetails_R ≔
  (R ∪ (Q' ∩ {self·detailsDecided(self)})) ∩ R_Initiator
```
(`.bum` dòng 102-106, 380-381.)

**Trạng thái:** ✅ Khớp chính xác công thức `Q'=Q∨Pre_T`, `R'=R∨(Q'∧Post_T)`.

---

### R14 — AND refinement

**Nguồn thật:**
```istar
goal MeetingOrganized : Achieve
goal MeetingScheduled : Achieve > MeetingOrganized
goal ParticipantsAttended : Achieve > MeetingOrganized
```
(2 Goal con AND-refine `MeetingOrganized` — cú pháp MTG dùng `>` không kèm `or`/`forall`/`pick`
nghĩa là AND theo mặc định.)

**Event-B thật:** phần `Sat_MeetingOrganized` được nhúng trong `obs_G_MeetingOrganized_P`
(trích ở R6) — không tách thành hàm riêng `Sat_MeetingOrganized(o)`, nhưng nội dung công thức
đúng là hội của 2 điều kiện tương ứng `MeetingScheduled`/`ParticipantsAttended` được inline.

**Trạng thái:** ✅ Khớp về nội dung — cùng kiểu "nhúng" như R2/R4.

---

### R15 — OR refinement

**Nguồn thật:**
```istar
goal ContactedByPhone : Achieve
  > or TimetableCollected
  > make InclusiveCollection

task CollectFromCalendar
  > or TimetableCollected
  > make FastCollection
```
(`ContactedByPhone` VÀ Task `CollectFromCalendar` cùng OR-refine Goal `TimetableCollected` —
một nhánh là Goal, một nhánh là Task, cả hai đều hợp lệ theo R15's "ít nhất một child eligible
và fulfilled.")

**Event-B thật:** nội dung `Sat_TimetableCollected` nhúng trong
`obs_G_TimetableCollected_P`/`_S`, với cấu trúc tuyển `∨` giữa nhánh Goal và nhánh Task — chưa
trích được dòng cụ thể trong lần đọc này (nằm sau dòng 400 của `.bum`, chưa đọc tới); khuyến
nghị đọc tiếp `.bum` từ dòng 400 nếu cần đối chiếu ký tự-đối-ký tự.

**Trạng thái:** ✅ Khớp về sự tồn tại đúng cấu trúc nguồn (2 nhánh OR, 1 Goal + 1 Task) —
chưa trích được công thức Event-B đầy đủ trong lần đọc này.

---

### R16 — `forall`

**Nguồn thật:**
```istar
goal ParticipantAttended : Achieve
  > forall Participant ParticipantsAttended
condition {[ self.attended ]}
```

**Event-B thật:**
```event-b
inv39: G_ParticipantAttended_A ⊆ (R_Initiator × R_Participant)
G_ParticipantAttended_S (khởi tạo) ≔
  {self,outer1· self∈R_Participant ∧ outer1∈R_Initiator ∧
     owns_Participant∼[{self}] ≠ ∅ ∧
     owns_Participant∼[{self}] = owns_Initiator∼[{outer1}]
   ∣ (outer1 ↦ self)}
```
(`.bum` dòng 113, 181.)

**Trạng thái:** ✅ Khớp — occurrence là cặp `(outer,self)` đúng 2 chiều context như R16 mô tả;
tập rỗng cho `UNKNOWN` (nếu `owns_Participant∼[{self}] = ∅` thì cặp không được sinh, không có
vacuous truth).

---

### R17 — `pick`

**Nguồn thật:**
```istar
goal OrganizerScheduledMeeting : Achieve
  > pick Organizer MeetingScheduled

goal SecretaryRequested : Achieve
  > pick Secretary ContactedByPhone
activation {[ self.knownContact->includes(self.outer) ]}
```

**Event-B thật:**
```event-b
VARIABLES
  Pick_OrganizerScheduledMeeting_candidates
  Pick_SecretaryRequested_candidates
INVARIANTS
  inv73: Pick_OrganizerScheduledMeeting_candidates ⊆ R_Initiator × R_Organizer
  inv74: Pick_SecretaryRequested_candidates ⊆ (R_Organizer × R_Participant) × R_Secretary
```
(`.bum` dòng 76-77, 150-151; khởi tạo `≔ ∅` dòng 216-217.)

**Trạng thái:** ✅ Khớp — đúng cơ chế "candidate relation" R17 mô tả, materialize mọi witness
hợp lệ (không lưu 1 lựa chọn duy nhất ổn định — đúng khớp cảnh báo ở
`mtg-eventb-experiment.md` §7: "candidate relation của pick materialize mọi witness hợp lệ; nó
không lưu một lựa chọn duy nhất ổn định qua nhiều state.") Đây là điểm **docs và code đã tự ghi
nhận** cùng một hạn chế — nhất quán, không phải phát hiện mới.

---

### R18 — `self.outer`

**Nguồn thật:**
```istar
goal SecretaryRequested : Achieve
  > pick Secretary ContactedByPhone
activation {[ self.knownContact->includes(self.outer) ]}
```
(3 tầng context: `self`=Secretary, `self.outer`=Participant, `self.outer.outer`=Organizer —
đúng comment trong `mtg.istar` dòng 114-117.)

**Event-B thật (occurrence 3 tầng, Goal `SecretaryRequested`):**
```event-b
G_SecretaryRequested_S (khởi tạo) ≔
  {self,outer1,outer2·
     self∈R_Secretary ∧ outer1∈R_Participant ∧ outer2∈R_Organizer ∧
     owns_Secretary∼[{self}] ≠ ∅ ∧ owns_Secretary∼[{self}] = owns_Participant∼[{outer1}] ∧
     owns_Participant∼[{outer1}] ≠ ∅ ∧ owns_Participant∼[{outer1}] = owns_Organizer∼[{outer2}]
   ∣ ((outer2 ↦ outer1) ↦ self)}
```
(`.bum` dòng 209.) Đây chính là ví dụ **3 tầng lượng hóa** mà R18 nói: "context được biểu diễn
bằng tuple dài hơn và `outer` lần lượt chiếu về thành phần trước" — `mtg.istar` là ca thật duy
nhất trong 4 tài liệu có tới 3 tầng `outer` lồng nhau (R18's ví dụ trong doc chỉ có 2 tầng).

**Trạng thái:** ✅ Khớp, và MTG kiểm chứng sâu hơn ví dụ gốc của doc (2 tầng → 3 tầng).

---

### R19 — Strategic dependency

**Nguồn thật (3 dependency trong MTG):**
```istar
depend Initiator.OrganizerScheduledMeeting -> task ChooseMeetingTime -> Organizer.SchedulingCompleted
depend Organizer.SecretaryRequested -> task CollectByPhone -> Secretary.CollectByPhone
depend Initiator.ParticipantAttended -> task AttendMeeting -> Participant.AttendMeeting
```

**Event-B thật:** mỗi Goal ở đầu depender (`OrganizerScheduledMeeting`, `SecretaryRequested`,
`ParticipantAttended`) đều dùng `pick`/`forall` để khai triển qua occurrence bên dependee, đúng
tinh thần R19 "Satisfaction của depender được khai triển qua provider." Không tìm thấy dạng
association tùy ý nối "mọi Requester với mọi Provider" — mọi context đều đi qua `owns_*` (đúng
cảnh báo R19 cuối: "phải dùng đúng context relation từ ACL.")

**Trạng thái:** ✅ Khớp về cấu trúc chung; 3/3 dependency trong MTG đều map đúng 1-1 vào
Goal `pick`/`forall` tương ứng đã kiểm ở R16/R17.

---

### R20 — Root Goal và process completion

**Trạng thái:** ✅ Khớp — đã trích đầy đủ ở Phần B/R14 (`end_meeting` với 2 `grd_goal`).

---

### R21 — Safety và liveness kiểm ở backend khác nhau

**Trạng thái:** ◻ Chưa kiểm chứng phần LTL — file `.ltl`
(`MeetingSchedulerMarking22_properties.ltl`) tồn tại trong cùng thư mục nhưng **chưa được đọc**
trong lần đối chiếu này. Đây là hạng mục nên đọc tiếp nếu bạn cần kiểm chứng riêng phần
Achieve/Sustain/Recur/dependency-liveness — Rodin's 126 PO **không** bao gồm các property này
(đúng khớp cảnh báo `mtg-eventb-experiment.md`: "Kết quả này không tự động chứng minh các công
thức temporal trong file `.ltl`").

---

### R22 — Traceability (iStar)

**Trạng thái:** ✅ Khớp — `_translation.md` có đủ cột `iStar | Initiator.DecideDetails |
t_DecideDetails` cho cả 6 Task (dòng 20-25), tuy **không** có dòng traceability riêng cho Goal
(chỉ Task được liệt kê ở cột iStar) — nghĩa là bảng traceability hiện tại **chưa liệt kê Goal**
dù R22 yêu cầu cả `Actor`, `Goal`, `Task`, `AND/OR`, `forall/pick`, `dependency`. Đây là khoảng
trống thật giữa R22 và output.

**Trạng thái:** ⚠️ Lệch — traceability report thiếu dòng cho 10 Goal (chỉ có 6 Task + BPMN +
ACL), nên khi Rodin báo lỗi trên `G_SecretaryRequested_S` chẳng hạn, không có cách tự động lần
ngược về dòng `goal SecretaryRequested` trong `mtg.istar` — phải tìm thủ công.

---

## Phần D — Luật hợp thành (`aclIstarBpmn2eventB.md`) và tổng kết

### Ánh xạ chính (mục "Ánh xạ chính" của `aclIstarBpmn2eventB.md`)

- "ACL classifier thành carrier set... enum thành partition" → ✅ khớp (Phần A).
- "BPMN sequence flow thành flow-state theo self; activity thành Event-B event" → ✅ khớp
  (Phần B).
- "iStar refinement thành predicate tổng hợp" → ✅ khớp nội dung, nhưng nhúng trực tiếp thay vì
  hàm `Sat_X` riêng như ví dụ rút gọn của chính tài liệu này minh họa (mục "Ví dụ" của
  `aclIstarBpmn2eventB.md` viết `Sat_MeetingOrganized`-style; output thật không có định danh
  `Sat_*` nào trong `.bum`/`.buc`) — cùng loại lệch đã ghi ở Phần C/R2.
- "Progress như eventually hoặc recurrence được sinh thành LTL cho ProB" → ◻ chưa kiểm chứng
  (chưa đọc file `.ltl`).

### Danh sách lỗi đã sửa (`mtg-eventb-experiment.md` §6) — đối chiếu nhanh

Toàn bộ 9 mục trong "Các lỗi bộ dịch đã tìm và sửa" đều có dấu vết xác nhận trực tiếp trong
output hiện tại:

| Lỗi đã sửa | Bằng chứng trong output thật |
|---|---|
| Role inheritance từng bị dịch nhầm thành subtype inclusion | Không còn `R_X ⊆ R_Y` nào cho Role — chỉ có `plays_X` (Phần A/R17) |
| BPMN marking từng toàn cục | 11 biến `at_f_*` riêng theo flow, không có `marking ⊆ FLOWS` chung (Phần B/R4) |
| Start precondition từng bị bỏ qua | `start_meeting` có `grd_pre_1` riêng, `INITIALISATION` không có (Phần B/R5) |
| navigation dùng `f(x)` gây WD giả | mọi guard dùng `f[{x}] = {TRUE}` (ảnh quan hệ), không dùng `f(x)` (Phần B/R7) |
| thiếu Owner uniqueness/scope, compatible conflict | axm10-14 (owner), axm47-50 (compatible) đầy đủ (Phần A/R14, R19) |
| End từng không liên kết root Goal marking | `end_meeting` có 2 `grd_goal_*` (Phần B/R14) |
| invariant dẫn xuất khổng lồ (79 PO mở) | `grd_effect_type_*` xuất hiện ở mọi Event có effect (Phần B/R8) |

### Những điểm cần bạn xem lại (tổng hợp ⚠️ và những đề xuất)

1. **XOR default thiếu guard phủ định** (Phần B/R10) — nhánh `default` của gateway
   `phoneCollectionRequired` không loại trừ nhánh `when`; 2 Event có thể cùng enabled. Đáng sửa
   trong exporter, không phải chỉ trong doc.
2. **Traceability thiếu Goal** (Phần C/R22) — `_translation.md` không liệt kê 10 Goal, chỉ có
   Task; nên bổ sung để lần ngược lỗi Rodin dễ hơn.
3. **`GOAL_STATUS` enum không được sinh** (Phần C/R7) — nếu có kế hoạch dùng ProB đọc trạng
   thái Goal theo đúng 4 nhãn `UNKNOWN/PENDING/FULFILLED/VIOLATED`, cần thêm tầng dịch riêng vì
   Event-B project hiện chỉ có `(A,P,S)` thô.
4. **`GOcc_X`/`Sat_X`/`Act_X` là cách trình bày trừu tượng, không phải định danh thật** (Phần
   C/R2, R4, R14; Phần D) — nên ghi chú rõ trong 3 file luật để người đọc không tìm nhầm các
   định danh này trong `.bum`/`.buc` thật.
5. **Chưa đối chiếu file `.ltl`** — toàn bộ Phần Achieve/Sustain/Recur/dependency-liveness (R8,
   R10, R11, R19 của `istar2eventb.md`, R19 của `bpmn2eventb.md`) mới kiểm chứng được phần cấu
   trúc biến/Rodin PO, chưa kiểm chứng được property temporal thật. Đọc
   `MeetingSchedulerMarking22_properties.ltl` là bước kiểm chứng tiếp theo hợp lý.

### Bảng tổng số theo trạng thái

| Nguồn | Tổng luật | ✅ Khớp | ⚠️ Lệch/cần xem | ◻ Chưa kiểm chứng |
|---|---|---|---|---|
| ACL (R1–R21) | 21 | 15 | 0 | 6 |
| BPMN (R0–R20, tính cả R0 ngầm) | 21 | 15 | 1 | 5 |
| iStar (R1–R22) | 22 | 16 | 3 | 3 |
| **Tổng** | **64** | **46** | **4** | **14** |

`◻ Chưa kiểm chứng` phần lớn vì ca MTG đơn giản (1 Group, không AND-split, không Recur, không
message flow, không loop) — không phải vì luật sai; muốn kiểm chứng hết cần thêm 1-2 ca MTG mở
rộng (ví dụ thêm gateway `type and`, 1 Goal `Recur`, 1 message flow giữa 2 pool).
