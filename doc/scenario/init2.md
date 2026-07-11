Bản song song của [init.md](init.md), nhưng phía **i\*** thay vì BPMN. Cùng một
miền nghiệp vụ, cùng một trạng thái khởi tạo — nhưng nhìn qua goal model thay
vì qua activity/gateway. File thật tương ứng:

- Domain model: `goal/src/main/resources/examples/mtg/mtg.istar`
- Kịch bản 3 participant (khớp `example/extenral/mtg.pl`): `mtg.iscn`
- Kịch bản 5 participant (khớp "Kịch bản C" của `init.md`): `meeting_five_participants.iscn`

Quy trình này có các loại vai trò, mỗi vai trò sở hữu các goal/task (thay vì
activity type như bên BPMN):

- **Initiator**: `HaveMeetingOrganized` (goal gốc), `DecideMeetingDetails` (task),
  `HaveSchedulingPerformed`, `MeetingAttended`, và 2 goal *uỷ quyền* (delegated) —
  `HaveSchedulingPerformedByOrganizer` (thoả mãn bởi Organizer),
  `MeetingAttendedByParticipant` (thoả mãn bởi từng Participant).
- **Organizer**: `HaveMeetingScheduled`, `TimetablesCollected`,
  `ChooseTimeAndDate` (task), `MeetingAnnounced`, `TimetableCollected`,
  `HavePPCalled`, `CollectFromCalendar` (task), `AnnounceMeeting` (task), goal
  uỷ quyền `HaveSecretaryCallPP` (thoả mãn bởi Secretary), 2 quality
  `Inclusivity`/`QuickScheduling`.
- **Secretary**: `CollectConstraintsByPhone` (task).
- **Participant**: `Participate` (task).

Quan hệ refinement: `HaveMeetingOrganized` = AND(`DecideMeetingDetails`,
`HaveSchedulingPerformed`, `MeetingAttended`). `HaveMeetingScheduled` =
AND(`TimetablesCollected`, `ChooseTimeAndDate`, `MeetingAnnounced`).
`TimetableCollected` = OR(`HavePPCalled`, `CollectFromCalendar`) — đây chính
là nhánh rẽ calendar/phone. Chi tiết đầy đủ xem `mtg.istar`.

### Trạng thái khởi tạo

Giống hệt `init.md` — cùng một trạng thái, chỉ đổi ống kính quan sát:

có `abdul` là Initiator. Có `matilda` là Organizer, `alex` là Secretary. Và
các người tham gia là `xing`, `amr`, `naya`, `bao`, `chloe`.

- `xing.hasCalendar = true`, `amr.hasCalendar = true`
- `naya.hasCalendar = false`, `bao.hasCalendar = false`, `chloe.hasCalendar = false`
- `alex.canCall(*) = true` cho cả 5 người

## Khác biệt cốt lõi so với BPMN: "occurrence" ở đây là gì?

Bên BPMN, một activity type sinh ra nhiều **occurrence** khi có nhiều
participant. Bên i\*, không có khái niệm occurrence tách rời — thay vào đó,
**mỗi Participant instance có một `IStarMarking` (trạng thái thoả mãn) độc
lập của riêng nó**, tính trên toàn bộ domain model. "Một occurrence của
`Check Participant Calendar` cho `naya`" bên BPMN ⇔ "trong trace riêng của
`naya`, `TimetableCollected = Fulfilled`" bên i\*.

Thiết lập trạng thái cho một instance có 2 cách tương đương (xem
`doc/concept-domain-model-vs-scenario.md`), và kịch bản `.iscn` dùng cả hai:

- **`fire`** — hành động thật của actor, trạng thái *suy ra* qua lan truyền
  AND/OR/contribution. Dùng cho `DecideMeetingDetails`, `ChooseTimeAndDate`,
  `CollectFromCalendar`, `CollectConstraintsByPhone`, `AnnounceMeeting`,
  `Participate`.
- **`assign`** — trạng thái *gán thẳng*, dùng cho goal uỷ quyền (thoả mãn qua
  `depend`, không phải hành động của chính actor đó):
  `HaveSchedulingPerformedByOrganizer`, `HaveSecretaryCallPP`,
  `MeetingAttendedByParticipant`.

## Một lần chạy cụ thể (5 participant, `meeting_five_participants.iscn`)

### Bước 1 — Initiator quyết định thông tin cuộc họp

```
fire DecideMeetingDetails;                             // broadcast: 1 lần, dùng chung
assign HaveSchedulingPerformedByOrganizer = Fulfilled;  // goal uỷ quyền cho Organizer
```

`DecideMeetingDetails` không gắn tên instance → áp dụng cho **cả 5** trace
của Participant cùng lúc (facts dùng chung của cuộc họp), đúng như
`Decide Meeting Details` bên BPMN chỉ sinh 1 occurrence.

### Bước 2 — Thu thập lịch, rẽ nhánh theo từng participant

Với `xing`, `amr` (`hasCalendar = true`):

```
fire xing.CollectFromCalendar;
fire amr.CollectFromCalendar;
```

→ `TimetableCollected` FULFILLED qua nhánh OR `CollectFromCalendar`, không
đụng tới Secretary — khớp "lấy được lịch trực tiếp từ calendar" trong
`init.md`.

Với `naya`, `bao`, `chloe` (`hasCalendar = false`):

```
fire naya.CollectConstraintsByPhone;
assign naya.HaveSecretaryCallPP = Fulfilled;
// ... lặp lại cho bao, chloe
```

→ `CollectConstraintsByPhone` (hành động thật của `alex`) fire trước;
`HaveSecretaryCallPP` (goal uỷ quyền của Organizer, thoả mãn qua `depend
Organizer.HaveSecretaryCallPP -> task CollectConstraintsByPhone ->
Secretary`) được gán trực tiếp — vì bản thân Organizer không "làm" gì cả, chỉ
nhờ Secretary làm hộ. Từ đó `HavePPCalled` FULFILLED (AND 1 con) →
`TimetableCollected` FULFILLED qua nhánh OR còn lại.

Sau bước này, cả 5 trace đều có `TimetableCollected = Fulfilled` — khớp "tất
cả 5 participant đều đã có lịch" trong `init.md`.

### Bước 3 — Organizer chọn ngày giờ

```
fire ChooseTimeAndDate;   // broadcast
```

### Bước 4 — Gửi thông báo cho từng participant

```
fire xing.AnnounceMeeting;
fire amr.AnnounceMeeting;
fire naya.AnnounceMeeting;
fire bao.AnnounceMeeting;
fire chloe.AnnounceMeeting;
```

### Bước 5 — Từng participant tham gia

```
fire xing.Participate;
assign xing.MeetingAttendedByParticipant = Fulfilled;
// ... lặp lại cho amr, naya, bao, chloe
```

`Participate` là hành động thật của participant (fire); `MeetingAttendedByParticipant`
là goal uỷ quyền của Initiator, thoả mãn qua `depend
Initiator.MeetingAttendedByParticipant -> task Participate -> Participant` —
gán trực tiếp, cùng lý do như bước 2.

### Bước 6 — Khẳng định trạng thái cuối (thay cho "meeting1 kết thúc")

Đây là phần duy nhất *thật sự khẳng định* điều gì (xem
`doc/concept-domain-model-vs-scenario.md` §1.3) — 4 bước trên chỉ là thiết
lập:

```
aggregate MeetingFullyOrganized : all of Participant over HaveMeetingOrganized;
```

Chạy thật: **HOLDS (5/5)** — mọi participant, trong trace riêng của mình, đều
đạt `HaveMeetingOrganized = Fulfilled`. Đây là điều kiện "quy trình kết thúc"
bên i\*, tương đương `meeting1.status = completed` bên BPMN.

## Tóm tắt — đối chiếu với bảng occurrence của `init.md`

Đếm số trace (trên 5 Participant) mà phần tử i\* tương ứng đạt
`Fulfilled`/`True`:

| Activity (`init.md`) | Số occurrence | Phần tử i\* tương ứng | Kết quả (đã chạy thật) |
|---|---:|---|---:|
| Decide Meeting Details | 1 | `DecideMeetingDetails` (broadcast) | 1 |
| Check Participant Calendar | 5 | `TimetableCollected` | **5** |
| — lấy trực tiếp từ calendar | *(2 trong 5)* | `CollectFromCalendar` | **2** |
| Request Secretary Call | 3 | `HaveSecretaryCallPP` | **3** |
| Collect Constraints By Phone | 3 | `CollectConstraintsByPhone` | **3** |
| Choose Time And Date | 1 | `ChooseTimeAndDate` (broadcast) | 1 |
| Announce Meeting | 5 | `AnnounceMeeting` | **5** |
| Participate | 5 | `Participate` | **5** |

Khớp 1-1 với bảng trong `init.md` — vì đây là cùng một trạng thái khởi tạo,
chỉ khác ống kính mô tả (BPMN activity occurrence ⇔ i\* per-instance goal
satisfaction).

## Ba kịch bản trên cùng một domain model

Giống hệt tinh thần của `init.md`: BPMN/`.istar` chỉ là **khung** (type-level);
tập instance + trạng thái khởi tạo mới quyết định điều gì thật sự xảy ra.

- **Kịch bản A** — cả 5 đều có calendar: chỉ cần `fire pX.CollectFromCalendar`
  cho cả 5, không có `CollectConstraintsByPhone`/`HaveSecretaryCallPP` nào.
  `UniversalQuickScheduling : all of Participant over QuickScheduling` sẽ
  **HOLDS** (5/5) thay vì FAILS.
- **Kịch bản B** — không ai có calendar: chỉ dùng
  `CollectConstraintsByPhone`/`assign HaveSecretaryCallPP` cho cả 5.
  `SomeInclusivityViaSecretary : any of Participant over Inclusivity` vẫn
  HOLDS, nhưng giờ là 5/5 thay vì 3/5.
- **Kịch bản C** — `meeting_five_participants.iscn` hiện tại: hỗn hợp,
  2 qua calendar + 3 qua secretary, đúng như bảng trên.

Cả ba đều dùng **chung một `mtg.istar`** — chỉ khác nội dung file `.iscn`.
Đây chính là điểm mấu chốt: domain model mô tả *cái gì có thể xảy ra*, còn
kịch bản mô tả *một trạng thái cụ thể trong không gian đó*.
