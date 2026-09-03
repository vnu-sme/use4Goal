# Ngữ nghĩa DSL iStar

iStar mô tả **điều hệ thống muốn đạt và trách nhiệm thuộc về ai** trên trạng
thái do ACL định nghĩa. iStar không tự sửa object system. Trong một kịch bản,
BPMN thực thi activity để tạo trạng thái ACL mới; sau mỗi lần như vậy, iStar
đánh giá lại toàn bộ goal model trên snapshot mới.

## Thành phần của mô hình

- Actor: `agent` và `role`. Một iStar Role dùng lại Role đã khai báo trong ACL;
  nó không tạo một loại role runtime độc lập.
- Intentional element: `goal`, `task`, `quality`, `resource`.
- Refinement: AND, OR, `forall` và `pick`.
- Quan hệ: contribution, qualification, needed-by, actor association và
  strategic dependency.
- Goal type: `Achieve`, `Maintain`, `Sustain`.

`Obstacle`, `ObstacleType`, `Obstruction` và `Resolution` không thuộc metamodel
iStar của dự án. Các từ khóa `obstacle`, `obstructs`, `resolves` trong file
`.istar` phải bị báo lỗi thay vì được nhận rồi bỏ qua.

Dependency composition-own một dependum. Dependum được khai báo trực tiếp là
một `goal`, `task`, `quality` hoặc `resource`, ví dụ:

```istar
depend Initiator.OrganizerScheduledMeeting
  -> task ChooseMeetingTime
  -> Organizer.SchedulingCompleted
```

`task ChooseMeetingTime` là object dependum thuộc dependency. Nó không phải
một chuỗi `kind=TASK`, và cũng không phải tham chiếu đến
`Organizer.ChooseMeetingTime` đã có sẵn. Hai phần tử ở hai đầu mới là các tham
chiếu biên của depender và dependee.

## Goal và Task contract

Goal có:

```istar
goal ChosenTimeHasDetails : Maintain
activation {[
  self.group.timeChosen
]}
condition {[
  self.group.detailsDecided
]}
```

- `activation`: khi nào obligation bắt đầu tồn tại;
- `condition`: predicate ACL cần được đánh giá theo GoalType.

Task có:

```istar
task ChooseMeetingTime
pre {[
  self.group.detailsDecided
]}
post {[
  self.group.timeChosen
]}
```

- `pre`: trạng thái cho phép/đòi hỏi task bắt đầu;
- `post`: trạng thái chứng tỏ task đã hoàn tất.

Root Goal phải có nguồn activation. Child của AND/`forall` nhận demand từ
parent và không khai báo lại activation. Child của OR/`pick` có thể dùng
activation làm eligibility guard. Goal nhận demand qua dependency cũng không
phải là activation root mới.

## Occurrence và context

Khai báo Goal/Task là kiểu thiết kế. Khi chạy, nó được nhân thành các
occurrence theo ACL object thật. Nếu có hai `Participant` instance thì
`ParticipantAttended` có hai occurrence và hai lịch sử marking độc lập.

Context path đi từ binding trong ra ngoài:

- `self` là object của occurrence hiện tại;
- `self.outer` là object của context lượng hóa/dependency gần nhất bên ngoài;
- AND/OR giữ nguyên context;
- `forall`/`pick` thêm một binding;
- dependency truyền context từ depender sang actor chịu trách nhiệm.

Miền `forall`/`pick` không phải toàn bộ object cùng class trên hệ thống. Nó chỉ
gồm các Role occurrence thuộc cùng ACL Group-instance scope. Tập rỗng cho
`UNKNOWN`, không mặc định `forall` rỗng là đúng.

## Một bước chạy của goal model

Giả sử activity thứ \(i\) biến đổi trạng thái ACL từ \(\Sigma_{i-1}\) thành
\(\Sigma_i\). Trước khi activity tiếp theo được chạy, bộ đánh giá thực hiện
bốn pha:

1. **Instantiate**: dựng mọi Goal/Task occurrence và context path có mặt trong
   \(\Sigma_i\).
2. **Evaluate all**: tính lại tất cả `activation`, `condition`, `pre`, `post`
   trên snapshot \(\Sigma_i\). Không dùng danh sách “goal bị activity tác
   động”.
3. **Advance history**: dùng marking ở checkpoint trước để cập nhật tuple
   thời gian `(A,P,S)` của Goal và `(Q,R)` của Task.
4. **Propagate to fixpoint**: tính lại AND/OR, `forall`/`pick`, dependency và
   contribution cho đến khi toàn cây ổn định.

Do đó chuỗi chạy thực tế là:

```text
(ACL state 0, goal marking 0)
  -- BPMN activity 1 --> ACL state 1
  -- evaluate all iStar --> goal marking 1
  -- BPMN activity 2 --> ACL state 2
  -- evaluate all iStar --> goal marking 2
  ...
```

Đây là **global re-evaluation**. Activity của một process có thể làm thay đổi
Goal của process khác nếu OCL của Goal đó đọc cùng trạng thái ACL.

## Bốn ngữ nghĩa thời gian của Goal

Mỗi Goal occurrence giữ `(A,P,S)`:

- `A`: goal đang active;
- `P`: giá trị condition đã được xử lý theo GoalType;
- `S`: chưa từng mất condition sau khi condition đã cần được giữ trong
  activation episode hiện tại.

Khi `A=false`, occurrence có status `UNKNOWN` và lịch sử Goal reset. Một lần
`A=false -> true` mở episode mới. Dùng các ký hiệu `U`, `P`, `F`, `V` lần lượt
cho `UNKNOWN`, `PENDING`, `FULFILLED`, `VIOLATED`.

### 1. Achieve

Ý nghĩa: trong episode hiện tại, condition cuối cùng phải được đạt ít nhất một
lần. Sau khi đạt, kết quả được nhớ đến hết episode.

```text
condition:   -   false false true  false -
status:      U -> P  -> P  -> F -> F  -> U
```

Ví dụ `MeetingOrganized : Achieve`: một khi trạng thái tổ chức họp đã đạt,
việc một predicate quan sát tạm thời đổi sau đó không xóa thành tựu trong cùng
episode.

### 2. Maintain

Ý nghĩa: condition phải đúng ngay tại checkpoint đầu khi activation bật và
phải đúng liên tục. Sai một lần là `VIOLATED` đến hết episode; đúng trở lại
không phục hồi lịch sử đó.

```text
condition:   -   true  true  false true  -
status:      U -> F  -> F  -> V  -> V -> U
```

Ví dụ `ChosenTimeHasDetails : Maintain`: ngay khi `timeChosen=true`,
`detailsDecided` phải đang true và không được mất trong thời gian goal còn
active.

### 3. Sustain

Ý nghĩa: ban đầu có thể chờ condition được đạt. Sau lần đạt đầu tiên,
condition phải được duy trì. Nếu mất thì `VIOLATED` đến hết episode.

```text
condition:   -   false true  true  false true  -
status:      U -> P  -> F  -> F  -> V  -> V -> U
```

Khác Maintain, Sustain không vi phạm chỉ vì condition chưa đúng ở đầu episode.
Khác Achieve, Sustain không cho phép condition mất sau khi đã đạt.

## Task, Quality và propagation

Task dùng `(Q,R)`:

- `(false,false)` = `UNKNOWN`;
- `(true,false)` = `PENDING`: pre đã xuất hiện nhưng post chưa đạt;
- `(true,true)` = `FULFILLED`.

Task không có `VIOLATED` trong marking hiện tại và giữ lịch sử qua nhiều
checkpoint, nên pre và post không cần đúng trong cùng một snapshot.

Quality dùng ba giá trị `UNKNOWN`, `TRUE`, `FALSE`. `make` và `break` là đóng
góp đủ để quyết định Quality; `help` và `hurt` chỉ là đóng góp không đủ và
không tự quyết định giá trị.

Goal cấu trúc không có `condition` được suy ra từ child:

- AND/`forall`: mọi nhánh phải `FULFILLED`;
- OR/`pick`: một nhánh `FULFILLED` là đủ;
- dependency truyền demand/context sang dependee và truyền kết quả thực hiện
  về depender;
- propagation lặp đến fixpoint sau khi **mọi OCL đã được đánh giá lại**.

Nếu Goal vừa có refinement vừa khai báo `condition`, condition là định nghĩa
trực tiếp có thẩm quyền; kết quả refinement không ghi đè nó.

## Ví dụ MTG theo nhiều occurrence

```istar
role Initiator {
  goal ParticipantsAttended : Achieve

  goal ParticipantAttended : Achieve
    > forall Participant ParticipantsAttended
  condition {[
    self.attended
  ]}
}
```

Với hai participant `p1`, `p2` trong cùng `MeetingUnit`, marking là:

| ACL checkpoint | `p1.attended` | `p2.attended` | Goal `p1` | Goal `p2` | `ParticipantsAttended` |
|---|---:|---:|---|---|---|
| ban đầu | false | false | PENDING | PENDING | PENDING |
| `p1` attend | true | false | FULFILLED | PENDING | PENDING |
| `p2` attend | true | true | FULFILLED | FULFILLED | FULFILLED |

Sau event `p1` attend, hệ thống vẫn đánh giá lại cả occurrence của `p2` và mọi
Goal khác. Parent chỉ hoàn tất khi `forall` aggregate hai kết quả mới.

Đặc tả công thức đầy đủ nằm tại [formal/istar.md](../../formal/istar.md).
