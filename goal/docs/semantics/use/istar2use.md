# Các luật chuyển iStar → USE

## 1. Giả định duy nhất

File USE đã được sinh từ ACL. Mỗi Role xuất hiện trong iStar phải có một class
Role cùng tên trong ACL và trong file USE.

Ví dụ, iStar có:

```istar
role Organizer {
  ...
}
```

thì file USE đã có:

```use
class Organizer
...
end
```

Phép chuyển iStar chỉ chèn thêm operation dẫn xuất vào các class Role đã có.
Khi một luật thật sự cần invariant, invariant đó được nối vào khối
`constraints` đã có. Nó không sinh lại class diagram ACL.

Trong các luật dưới đây, ký hiệu:

```text
holds(G) = G_condition()    nếu G là Goal
holds(T) = T_postHolds()    nếu T là Task
```

## R1 — Role iStar → class Role USE đã có

### iStar

```istar
role Organizer {
  goal MeetingOrganized : Achieve
    activation {[ self.ready ]}
    condition  {[ self.organized ]}
}
```

### USE được bổ sung

Các operation của intentional element trong `Organizer` được chèn vào class
`Organizer` đã có:

```use
class Organizer
attributes
  -- các thuộc tính đã sinh từ ACL
operations
  MeetingOrganized_activation() : Boolean = self.ready
  MeetingOrganized_condition() : Boolean = self.organized
end
```

Không sinh class `MeetingOrganized` và không sinh thêm class `Organizer`.

## R2 — Goal activation → operation Boolean

### iStar

```istar
goal MeetingOrganized : Achieve
  activation {[ self.ready and self.enabled ]}
  condition  {[ self.organized ]}
```

### USE được bổ sung

```use
operations
  MeetingOrganized_activation() : Boolean =
    self.ready and self.enabled
```

`MeetingOrganized_activation()` trả về `true` tại những state mà Goal được
kích hoạt.

Nếu Goal có nhiều `activation`, các biểu thức được nối bằng `and`.

### iStar

```istar
goal MeetingOrganized : Achieve
  activation {[ self.ready ]}
  activation {[ self.enabled ]}
  condition  {[ self.organized ]}
```

### USE được bổ sung

```use
MeetingOrganized_activation() : Boolean =
  (self.ready) and (self.enabled)
```

## R3 — Goal condition → operation Boolean

### iStar

```istar
goal MeetingOrganized : Achieve
  activation {[ self.ready ]}
  condition  {[ self.organized and self.notificationsSent ]}
```

### USE được bổ sung

```use
operations
  MeetingOrganized_condition() : Boolean =
    self.organized and self.notificationsSent
```

`MeetingOrganized_condition()` cho biết Goal đang được thỏa trong state USE
hiện tại.

Nếu Goal có nhiều `condition`, chúng cũng được nối bằng `and`:

```use
MeetingOrganized_condition() : Boolean =
  (condition1) and (condition2) and (condition3)
```

## R4 — Goal type

Các loại `Achieve`, `Maintain`, `Sustain` và `Recur` không làm thay đổi class
diagram USE. Trong file USE, tất cả đều tạo các operation `_activation()` và
`_condition()`.

### iStar

```istar
goal PaymentReceived : Achieve
  activation {[ self.invoiceSent ]}
  condition  {[ self.paid ]}

goal ServiceAvailable : Maintain
  activation {[ self.serviceStarted ]}
  condition  {[ self.available ]}
```

### USE được bổ sung

```use
operations
  PaymentReceived_activation() : Boolean = self.invoiceSent
  PaymentReceived_condition() : Boolean = self.paid

  ServiceAvailable_activation() : Boolean = self.serviceStarted
  ServiceAvailable_condition() : Boolean = self.available
```

Sự khác nhau giữa các Goal type là ngữ nghĩa qua nhiều state, nên không được
biến thành invariant OCL buộc condition đúng ngay trong cùng state activation.

## R5 — Task pre → operation Boolean

### iStar

```istar
task ScheduleMeeting
  pre  {[ self.ready and self.slotAvailable ]}
  post {[ self.scheduled ]}
```

### USE được bổ sung

```use
operations
  ScheduleMeeting_preHolds() : Boolean =
    self.ready and self.slotAvailable
```

Operation cho biết precondition của Task có đúng trong state hiện tại hay
không. Nó không tự thực thi Task.

## R6 — Task post → operation Boolean

### iStar

```istar
task ScheduleMeeting
  pre  {[ self.ready ]}
  post {[ self.scheduled and self.notificationsSent ]}
```

### USE được bổ sung

```use
operations
  ScheduleMeeting_postHolds() : Boolean =
    self.scheduled and self.notificationsSent
```

Nếu Task có nhiều `pre` hoặc nhiều `post`, các biểu thức cùng loại được nối
bằng `and`.

## R7 — Goal lá

Goal không được refine bởi Goal/Task khác là Goal lá. Satisfaction của nó lấy
trực tiếp từ `condition`.

### iStar

```istar
goal InvitationSent : Achieve
  activation {[ self.ready ]}
  condition  {[ self.invitationSent ]}
```

### USE được bổ sung

```use
operations
  InvitationSent_activation() : Boolean = self.ready
  InvitationSent_condition() : Boolean = self.invitationSent
```

## R8 — AND refinement

Mọi child AND phải thỏa thì parent mới thỏa:

```text
holds(P) = holds(C1) and holds(C2) and ... and holds(Cn)
```

### iStar

```istar
role Organizer {
  goal MeetingOrganized : Achieve
    activation {[ self.ready ]}

  task ChooseSlot
    > MeetingOrganized
    pre  {[ self.ready ]}
    post {[ self.slotChosen ]}

  task SendInvitations
    > MeetingOrganized
    pre  {[ self.slotChosen ]}
    post {[ self.invitationsSent ]}
}
```

### USE được bổ sung

```use
operations
  MeetingOrganized_activation() : Boolean = self.ready
  MeetingOrganized_condition() : Boolean =
    self.ChooseSlot_postHolds() and
    self.SendInvitations_postHolds()
  ChooseSlot_preHolds() : Boolean = self.ready
  ChooseSlot_postHolds() : Boolean = self.slotChosen
  SendInvitations_preHolds() : Boolean = self.slotChosen
  SendInvitations_postHolds() : Boolean = self.invitationsSent
```

Trong trường hợp này, `MeetingOrganized_condition()` là operation dẫn xuất từ
AND refinement:

```use
MeetingOrganized_condition() : Boolean =
  self.ChooseSlot_postHolds() and self.SendInvitations_postHolds()
```

Không được gán mặc định `false` cho operation của parent.

Nếu parent còn khai báo một `condition P`, kết quả là:

```text
parent condition = P and holds(C1) and ... and holds(Cn)
```

## R9 — OR refinement

Ít nhất một child OR thỏa thì parent thỏa:

```text
holds(P) = holds(C1) or holds(C2) or ... or holds(Cn)
```

### iStar

```istar
role Organizer {
  goal InvitationSent : Achieve
    activation {[ self.ready ]}

  task SendEmail
    > or InvitationSent
    pre  {[ self.ready ]}
    post {[ self.emailSent ]}

  task SendSms
    > or InvitationSent
    pre  {[ self.ready ]}
    post {[ self.smsSent ]}
}
```

### USE được bổ sung

```use
operations
  InvitationSent_condition() : Boolean =
    self.SendEmail_postHolds() or self.SendSms_postHolds()
```

Không dịch thành từng luật riêng:

```ocl
child implies parent
```

vì cách đó vẫn cho phép parent đúng khi không child nào đúng.

## R10 — `forall`

`forall R` tạo một occurrence child cho mọi Role occurrence `R` trong context.
Parent thỏa khi mọi occurrence child đều thỏa.

```text
holds(P) = for every r : R in current context, holds(C(r))
```

### ACL/USE có sẵn

Giả sử `Meeting` sở hữu nhiều `Participant` và navigation đã có là
`self.meeting.participants`.

### iStar

```istar
role Organizer {
  goal AllParticipantsConfirmed : Achieve
    activation {[ self.registrationClosed ]}

  goal ParticipantConfirmed : Achieve
    > forall Participant AllParticipantsConfirmed
    condition {[ self.confirmed ]}
}
```

### USE được bổ sung

Operation của Goal con được chèn vào class `Participant`:

```use
class Participant
operations
  ParticipantConfirmed_condition() : Boolean = self.confirmed
end
```

Parent được bổ sung trên `Organizer`:

```use
class Organizer
operations
  AllParticipantsConfirmed_activation() : Boolean =
    self.registrationClosed

  AllParticipantsConfirmed_condition() : Boolean =
    not self.meeting.participants->isEmpty() and
    self.meeting.participants->forAll(p |
      p.ParticipantConfirmed_condition())
end
```

Điều kiện `not ...->isEmpty()` là cần thiết: tập Role occurrence rỗng không
được làm Goal tự động thỏa bởi vacuous truth của OCL.

## R11 — `pick`

`pick R` chọn một candidate Role occurrence `R`. Parent thỏa nếu có ít nhất
một candidate mà child thỏa:

```text
holds(P) = exists r : R in current context, holds(C(r))
```

### iStar

```istar
role Organizer {
  goal AParticipantConfirmed : Achieve
    activation {[ self.registrationClosed ]}

  goal ParticipantConfirmed : Achieve
    > pick Participant AParticipantConfirmed
    condition {[ self.confirmed ]}
}
```

### USE được bổ sung

```use
class Organizer
operations
  AParticipantConfirmed_activation() : Boolean =
    self.registrationClosed

  AParticipantConfirmed_condition() : Boolean =
    self.meeting.participants->exists(p |
      p.ParticipantConfirmed_condition())
end
```

Vì `exists` trên collection rỗng trả về `false`, `pick` không cần guard
`notEmpty` để tính satisfaction.

Nếu runtime cần nhớ chính xác candidate đã được chọn qua nhiều state, binding
đó phải được lưu trong state/trace; không được dùng `->any(x | true)` để chọn
một occurrence tùy ý ở mỗi lần đánh giá.

## R12 — `self` trong Goal/Task

`self` luôn là occurrence ở đỉnh context stack.

### iStar

```istar
role Participant {
  goal Confirmed : Achieve
    activation {[ self.invited ]}
    condition  {[ self.confirmed ]}
}
```

### USE được bổ sung vào class Participant

```use
Confirmed_activation() : Boolean = self.invited
Confirmed_condition() : Boolean = self.confirmed
```

## R13 — `self.outer`

Trong nhánh `forall` hoặc `pick`, `self` là Role được bind và `self.outer` là
occurrence bao ngoài.

### iStar

```istar
role Organizer {
  goal AllNotified : Achieve

  goal ParticipantNotified : Achieve
    > forall Participant AllNotified
    condition {[
      self.notified and self.meeting = self.outer.meeting
    ]}
}
```

### USE được bổ sung

Nếu navigation từ Participant tới Organizer cùng Meeting là
`self.meeting.organizer`, biểu thức được viết lại thành:

```use
class Participant
operations
  ParticipantNotified_condition() : Boolean =
    self.notified and
    self.meeting = self.meeting.organizer.meeting
end
```

Navigation thay cho `self.outer` phải trỏ đúng occurrence bao ngoài trong cùng
Group context. Không được thay `self.outer` bằng một object cùng class nhưng
thuộc Group occurrence khác.

## R14 — Dependency có boundary element

Một dependency cho biết depender yêu cầu dependee cung cấp dependum. Trong
file USE, dependency được biểu diễn bằng các operation quan sát demand và
satisfaction; nó không tạo association mới.

### iStar

```istar
role Requester {
  goal RequestCompleted : Achieve
    activation {[ self.requested ]}
    condition  {[ self.completed ]}
}

role Provider {
  goal ServiceProvided : Achieve
    condition {[ self.provided ]}
}

depend Requester.RequestCompleted
  -> goal ServiceProvided
  -> Provider.ServiceProvided
```

### USE được bổ sung

Giả sử navigation ACL từ Requester tới Provider trong cùng context là
`self.group.provider`, chèn vào `Requester`:

```use
operations
  Dep_RequestCompleted_ServiceProvided_demand() : Boolean =
    self.RequestCompleted_activation()

  Dep_RequestCompleted_ServiceProvided_satisfied() : Boolean =
    self.group.provider->exists(p |
      p.ServiceProvided_condition())
```

Ý nghĩa:

- `_demand()` cho biết dependency đang được yêu cầu;
- `_satisfied()` cho biết có đúng dependee occurrence cung cấp dependum;
- satisfaction của dependency không đồng nghĩa với condition riêng của
  `RequestCompleted`; nó là một thành phần để đánh giá Goal depender.

Không sinh invariant:

```ocl
demand implies satisfied
```

vì invariant đó buộc dependency được đáp ứng ngay trong cùng state, làm mất
ngữ nghĩa chờ đợi qua thời gian.

## R15 — Dependency không có boundary element

### iStar

```istar
depend Requester
  -> goal ServiceProvided
  -> Provider
```

Không có element ở boundary nên demand là demand ở cấp actor/context. Phần
satisfaction vẫn quan sát dependum tại dependee:

```use
class Requester
operations
  Dep_Requester_ServiceProvided_satisfied() : Boolean =
    self.group.provider->exists(p |
      p.ServiceProvided_condition())
end
```

Việc dependency được demand khi nào không thể suy ra chỉ từ cú pháp này trong
một state USE. Nó được kích hoạt bởi cơ chế thực thi/trace. Translator không
được tự đặt `_demand() = true`.

## R16 — Dependency tới Task

Nếu dependum là Task, satisfaction dùng postcondition của Task.

### iStar

```istar
depend Requester.RequestPrepared
  -> task ProcessRequest
  -> Provider.ProcessRequest
```

### USE được bổ sung

```use
Dep_RequestPrepared_ProcessRequest_satisfied() : Boolean =
  self.group.provider->exists(p |
    p.ProcessRequest_postHolds())
```

Nếu cần biết Task đã sẵn sàng thay vì đã hoàn thành, dùng operation riêng:

```use
Dep_RequestPrepared_ProcessRequest_enabled() : Boolean =
  self.group.provider->exists(p |
    p.ProcessRequest_preHolds())
```

## R17 — Nhiều dependee occurrence

Nếu navigation tới dependee có multiplicity nhiều, dependency được thỏa khi có
ít nhất một occurrence cung cấp dependum:

```ocl
dependees->exists(d | holds_d(dependum))
```

### Ví dụ USE

```use
Dep_OrderProcessed_satisfied() : Boolean =
  self.department.processors->exists(processor |
    processor.ProcessOrder_postHolds())
```

Nếu dependency chỉ định một binding cụ thể trong context `pick`, translator
phải dùng binding đó thay vì lượng hóa lại toàn bộ collection.

## R18 — Thứ tự chèn vào file USE

Các luật được áp dụng theo thứ tự:

1. tìm class Role đã có cho từng actor iStar;
2. xác định context của từng Goal và Task;
3. chèn operation cho `activation`, `condition`, `pre`, `post`;
4. chèn biểu thức dẫn xuất cho AND và OR;
5. chèn biểu thức lượng hóa cho `forall` và `pick`;
6. chèn operation quan sát dependency;
7. nối invariant iStar vào khối `constraints` đã có nếu một luật cần invariant;
8. biên dịch lại toàn bộ file USE sau khi chèn.

Không tạo khối `constraints` thứ hai.

## R19 — Các khái niệm chưa dịch trong giai đoạn này

Các khái niệm sau tạm thời không tham gia phép chuyển:

- Resource;
- Quality;
- Obstacle;
- contribution `make`, `help`, `hurt`, `break`;
- `qualifies`;
- `needed-by`;
- `obstructs` và `resolves`.

Chúng không tạo class, operation hoặc invariant trong các luật hiện tại.
