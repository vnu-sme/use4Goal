# Luật chuyển ACL + iStar sang USE/OCL

## 1. Đầu vào và đầu ra

Bộ dịch nhận một tệp `.acl`, một tệp `.istar` và sinh hai tệp:

```text
<iStar-model>_Verification.use
<iStar-model>_Verification.tocl
```

ACL sinh class diagram và navigation miền. Mỗi actor iStar phải trùng tên với
một Role ACL. Goal và Task không trở thành class hoặc attribute mới; chúng trở
thành các Boolean query operation trên class Role của actor khai báo chúng.

## 2. Goal chỉ sinh một `condition()`

Với Goal `G`, bộ dịch chỉ sinh:

```use
G_condition() : Boolean = expression
```

Không sinh `activation()`, `localCondition()` hoặc `holds()`. `activation`
không tham gia công thức thời gian hiện tại nên được bỏ khỏi đầu ra. Nếu Goal
có nhiều khối `condition`, chúng được nối bằng `and`.

Với Goal lá:

```istar
goal LessonDelivered : Achieve
condition {[ self.group.lessonDelivered ]}
```

kết quả là:

```use
LessonDelivered_condition() : Boolean =
  self.classroom.lessonDelivered
```

Goal lá không có `condition` được dịch thành `false` và sinh diagnostic.

## 3. Task

Với Task `T`, bộ dịch giữ hai query:

```use
T_preHolds()  : Boolean = pre-expression
T_postHolds() : Boolean = post-expression
```

Không sinh `T_holds()`. `preHolds()` chỉ quan sát khả năng thực thi; trạng thái
hoàn thành của Task dùng trực tiếp `postHolds()`. Không có `pre` mặc định là
`true`; không có `post` mặc định là `false`.

## 4. Goal cha và phép lan truyền

Condition của Goal cha chứa trực tiếp condition/post của các con:

```text
AND: P_condition = C1_condition and ... and Cn_condition
OR : P_condition = C1_condition or  ... or Cn_condition
```

Nếu con là Task thì dùng `T_postHolds()` thay cho `T_condition()`.

Ví dụ AND:

```istar
goal ClassCompleted : Achieve

goal LessonDelivered : Achieve
  > ClassCompleted

goal AttendanceSummaryRecorded : Achieve
  > ClassCompleted
```

sinh:

```use
ClassCompleted_condition() : Boolean =
  self.LessonDelivered_condition() and
  self.AttendanceSummaryRecorded_condition()
```

Ví dụ OR với hai Task:

```istar
task RecordAttendanceManually
  > or AttendanceSummaryRecorded

task RecordAttendanceElectronically
  > or AttendanceSummaryRecorded
```

sinh:

```use
AttendanceSummaryRecorded_condition() : Boolean =
  self.RecordAttendanceManually_postHolds() or
  self.RecordAttendanceElectronically_postHolds()
```

Nếu Goal cha vừa có OCL riêng vừa có refinement, OCL riêng được nối bằng đúng
toán tử của refinement:

```text
AND-refinement:
  P_condition = local(P) and (C1 and ... and Cn)

OR-refinement:
  P_condition = local(P) or (C1 or ... or Cn)
```

Như vậy không cần operation trung gian hay invariant lan truyền riêng.

## 5. Ngữ nghĩa thời gian

Goal type được dịch vào tệp TOCL:

```text
Achieve  G -> sometime G_condition()
Maintain G -> always G_condition()
Sustain  G -> sometime (always G_condition())
Recur    G -> tạm thời không sinh công thức
```

Ví dụ:

```tocl
context Teacher
inv ACHIEVE_ClassCompleted:
  sometime self.ClassCompleted_condition()
```

TOCL nằm trong tệp riêng vì `sometime` và `always` không phải toán tử OCL lõi.

## 6. Phạm vi

- Hỗ trợ Goal, Task, OCL `condition`/`pre`/`post`, AND và OR.
- `activation` vẫn có thể tồn tại trong cú pháp iStar nhưng không được dịch.
- Không hỗ trợ refinement iStar `forall` và `pick`; tool dừng với diagnostic.
- Không xấp xỉ cross-class refinement.
- Dependency, Resource, Quality và contribution chưa tham gia phép lan truyền.

Ví dụ hoàn chỉnh nằm trong
`src/main/resources/examples/classroom/{classroom.acl,classroom.istar,classroom.bpmn2}`.
