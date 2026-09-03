# Meeting Scheduler: ví dụ M1 và M0 cho ACL/iStar state-aware

Ví dụ được chia thành bốn artefact liên kết bằng XMI identity:

| Artefact | Tầng | Nội dung |
|---|---|---|
| [`mtg-development.acl.xmi`](../../../model/development/mtg-development.acl.xmi) | M1 | Actor/Entity types và cấu trúc |
| [`mtg-development.istar.xmi`](../../../model/development/mtg-development.istar.xmi) | M1 | intentional views dùng lại ACL Actor |
| [`mtg-development.state.xmi`](../../../model/development/mtg-development.state.xmi) | M0 | runtime identities và hai system states |
| [`mtg-development.goaltrace.xmi`](../../../model/development/mtg-development.goaltrace.xmi) | M0 | hai lần đánh giá đầy đủ goal model |

## 1. Model ACL M1

```text
Actor Person               kind INDIVIDUAL
Actor MeetingOrganization  kind COLLECTIVE
Actor MeetingUnit          kind COLLECTIVE
Actor MeetingParty         kind ROLE, abstract
Actor Organizer            kind ROLE
Actor Participant          kind ROLE

Entity Meeting
Entity Calendar
Entity MeetingRequest
```

Structure chính:

```text
MeetingOrganization ◆── MeetingUnit
MeetingUnit         ◆── Organizer
MeetingUnit         ◆── Participant
MeetingUnit         ◆── Meeting
Person              ─── enacts MeetingParty
Participant         ◇── Calendar
```

`MeetingParty` cung cấp một endpoint chung cho association enactment;
`Organizer`, `Participant`, `Initiator`, `Secretary` specialize đúng Actor có
kind `ROLE`.

## 2. Model iStar M1

Các ActorView không tạo Actor mới:

```text
MeetingOrganization view
└── ImproveCoordination

MeetingUnit view
└── HaveMeetingOrganized
    AND
    ├── DetailsDecided
    ├── MeetingScheduled
    └── ParticipantsAttended
        FORALL Participant
        └── ParticipantAttendanceExpectation

Organizer view
└── HaveMeetingScheduled
    AND
    ├── ChooseMeetingTime
    └── AnnounceMeeting

Participant view
└── AttendMeeting

Person view
├── AvoidOvertime
└── SustainableWorkload
```

Ba dependency tường minh diễn tả:

1. organization phụ thuộc meeting unit cho đóng góp tổ chức meeting;
2. meeting unit phụ thuộc organizer cho scheduling;
3. meeting unit phụ thuộc participant cho attendance.

Không dependency nào được suy ra tự động từ ACL Composition.

## 3. Runtime identities M0

```text
org1         : MeetingOrganization
unit1        : MeetingUnit
alice        : Person
bob          : Person
organizer1   : Organizer
participant1 : Participant
meeting1     : Meeting
calendar1    : Calendar
```

Các identity tồn tại qua cả hai state. Link enactment cho biết:

```text
alice enacts organizer1
bob   enacts participant1
```

`organizer1` không đồng nhất với `alice`: một state tương lai có thể thay link
bằng `carol enacts organizer1` mà không thay identity của role slot.

## 4. Hai system states

| State fact | `s0 BeforeScheduling` | `s1 AfterMeetingExecution` |
|---|---:|---:|
| `meeting1.detailsDecided` | true | true |
| `meeting1.timeChosen` | false | true |
| `meeting1.announced` | false | true |
| `participant1.notified` | false | true |
| `participant1.attended` | false | true |
| `org1.coordinationScore` | 0 | 1 |
| `alice.workingHours` | 7 | 9 |

State `s1` mô phỏng kết quả một BPMN execution: meeting thành công nhưng Alice
làm quá tám giờ.

## 5. Hai goal snapshots

| Marking | `gs0` | `gs1` |
|---|---|---|
| `ImproveCoordination(org1)` | PENDING | SATISFIED |
| `HaveMeetingOrganized(unit1)` | PENDING | SATISFIED |
| `HaveMeetingScheduled(organizer1)` | PENDING | SATISFIED |
| `AttendMeeting(participant1)` | INACTIVE | SATISFIED |
| `AvoidOvertime(alice)` | SATISFIED | VIOLATED |
| `AvoidOvertime(bob)` | SATISFIED | SATISFIED |

Ví dụ này chủ ý tạo một kết quả không hoàn toàn “xanh”: process và collective
goal thành công nhưng goal nội tại của Alice bị vi phạm. Nếu chỉ cập nhật các
goal được map trực tiếp từ activity, lỗi này sẽ bị bỏ sót. Việc đánh giá lại toàn
bộ iStar model trên `s1` phát hiện nó tự động.

## 6. Những khái niệm được ví dụ biện minh

| Concept | Vì sao phải tồn tại |
|---|---|
| `Actor` | cá nhân, collective và role đều có thể làm intentional subject |
| `ActorKind` | ba loại subject có quy tắc instance/lifecycle khác nhau |
| `Entity` | cần state-bearing object không có intentionality |
| `ActorOccurrence` | goal cần một runtime subject cụ thể |
| `EntityObject` | OCL cần đọc domain object cụ thể |
| `SystemState` | cùng identity có giá trị khác nhau theo thời gian |
| `ActorView` | iStar dùng lại Actor ACL, không map bằng tên |
| `contextActor` | quantified goal template có `self` khác boundary owner |
| `GoalSnapshot` | lưu một lần đánh giá hoàn chỉnh tại một checkpoint |
| `IntentionalMarking` | phân biệt goal template với status theo subject/context |

## 7. Cách xem trong Eclipse

1. Import thư mục `goal/model/development` vào workspace hoặc đặt nó trong
   project đã import.
2. Mở `acl-state.ecore` và `istar-state.ecore` bằng **Sample Ecore Model
   Editor** hoặc **Ecore Tools**.
3. Mở lần lượt các file XMI bằng **Sample Ecore Model Editor**.
4. Validate root của từng file, không chỉ validate `.ecore`.

Đây là model phát triển. Compiler `.acl/.istar` 2.0 chưa đọc namespace 3.0 và
không được dùng làm validator cho bốn artefact này.
