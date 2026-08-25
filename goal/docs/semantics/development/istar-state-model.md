# State-aware iStar — bản phát triển 3.0

Tài liệu này mô tả
[`istar-state.ecore`](../../../model/development/istar-state.ecore). Mục tiêu của
bản phát triển không phải biến iStar thành một bản sao của ACL. ACL giữ system
state; iStar định nghĩa cách đọc và đánh giá lại toàn bộ intentional model trên
mỗi state đó.

## 1. Vấn đề của iStar chỉ có specification

Một declaration:

```text
Goal AvoidOvertime
condition self.workingHours <= 8
```

chưa nói:

- `self` là occurrence nào;
- condition được đọc ở checkpoint nào;
- goal đang inactive, pending, satisfied hay violated;
- trạng thái mới có buộc đánh giá lại goal không.

Vì vậy bản 3.0 tách hai phần:

```text
GoalModel  -- template M1
GoalTrace  -- evaluation M0 theo SystemHistory
```

## 2. `ActorView`

iStar không khai báo lại Agent, Role hoặc Group. Một `ActorView` tham chiếu trực
tiếp một `ACL::Actor`:

```text
ActorView.actor : ACL::Actor
```

Ngữ nghĩa của intentional boundary được suy ra từ `actor.kind`:

| Actor kind | Intentional elements trong view |
|---|---|
| `INDIVIDUAL` | mục tiêu nội tại của từng cá nhân/software occurrence |
| `ROLE` | trách nhiệm của mỗi role occurrence |
| `COLLECTIVE` | mục tiêu chung của mỗi organizational occurrence |

Khái niệm này tồn tại để cả ACL structure, iStar intention và BPMN performer
tham chiếu cùng một Actor M1. Tên giống nhau không còn được coi là mapping.

## 3. Intentional element và state predicate

### `Goal`

Goal là desired state, không phải biến lưu trạng thái. Nó có:

- `activation`: predicate xác định goal có áp dụng tại checkpoint hay không;
- `condition`: predicate xác định desired state hiện đang đúng hay sai;
- `goalType`: `ACHIEVE`, `MAINTAIN`, `SUSTAIN`, `RECUR`.

### `Task`

Task là intended action contract:

- `precondition` đọc source state;
- `postcondition` đọc successor state.

Task không thay thế BPMN Activity. iStar nói actor muốn action nào được thực
hiện; BPMN quy định transition cụ thể và thứ tự điều khiển.

### `Quality`

Quality có thể có state predicate để việc đánh giá không chỉ dựa trên
contribution label. Contribution vẫn cho biết một intention `MAKE/HELP/HURT/BREAK`
quality đó.

### `Resource`

Resource vẫn là intentional element: một thứ actor cần. Domain object thực tế
được biểu diễn bởi ACL EntityObject; Resource không được dùng để sao chép object
state.

### `StatePredicate`

Mọi OCL body được đánh giá với ba tên context:

```text
self   runtime subject của marking hiện tại
outer  subject bao ngoài của quantified refinement, nếu có
state  toàn bộ ACL SystemState đang được đánh giá
```

`contextActor` trên IntentionalElement xác định type của `self`. Nếu bỏ trống,
type mặc định là `owner.actor`.

`contextActor` tồn tại vì một collective goal có thể chứa template:

```text
forall Participant p : p.attended
```

Template vẫn thuộc goal tree của `MeetingUnit`, nhưng mỗi marking dùng một
Participant occurrence làm `self`.

## 4. Refinement và dependency

- `AndRefinement`: mọi child marking cần đạt;
- `OrRefinement`: ít nhất một child marking cần đạt;
- `QuantifiedRefinement(FORALL)`: conjunction trên mọi occurrence phù hợp;
- `QuantifiedRefinement(PICK)`: existential choice trên các occurrence phù hợp.

Dependency nối hai ActorView và composition-own một dependum cụ thể. Không cần
`DependumKind`: subtype thực tế của dependum đã cho biết nó là Goal, Task,
Quality hay Resource.

ACL Composition giữa collective cha và collective con không làm phát sinh
dependency. Delegation phải được khai báo tường minh trong iStar.

## 5. `GoalTrace`

`GoalTrace` tham chiếu đúng một GoalModel và một ACL SystemHistory có chung ACL
schema. Nó chứa một `GoalSnapshot` cho mỗi checkpoint được kiểm tra.

### `GoalSnapshot`

GoalSnapshot không chứa domain state. Nó tham chiếu `SystemState` và chứa kết
quả đánh giá của state đó.

`complete = true` có nghĩa checker đã sinh marking cho mọi cặp intention/subject
đang applicable, không chỉ những goal mà activity vừa thay đổi trực tiếp.

Đây là nguyên tắc đồng bộ bắt buộc:

```text
BPMN transition tạo Σ(i+1)
→ iStar đánh giá lại toàn bộ model trên Σ(i+1)
→ chỉ sau khi GoalSnapshot(i+1) complete mới chạy transition kế tiếp
```

### `IntentionalMarking`

Một marking có khóa:

\[
(element, subject, outer?)
\]

và một `status`:

| Status | Ngữ nghĩa |
|---|---|
| `INACTIVE` | activation sai hoặc intention chưa applicable |
| `PENDING` | đang active nhưng desired state của ACHIEVE chưa đạt |
| `SATISFIED` | condition hoặc propagation result đúng |
| `VIOLATED` | một MAINTAIN/SUSTAIN obligation đang active đã bị phá vỡ |

`evidence` giải thích dữ kiện hoặc propagation rule tạo status; nó phục vụ
debug, không tham gia semantics.

## 6. Goal của cá nhân, role và collective

Không cần ba metaclass goal:

```text
Person ActorView       owns AvoidOvertime
Organizer ActorView    owns HaveMeetingScheduled
MeetingUnit ActorView  owns HaveMeetingOrganized
```

Ở M0, chúng trở thành:

```text
AvoidOvertime(alice)
HaveMeetingScheduled(organizer1)
HaveMeetingOrganized(unit1)
```

Alice có thể enact `organizer1`, nhưng hai marking vẫn khác nhau. Khi Alice rời
role, goal cá nhân của Alice còn tồn tại; marking theo organizer role được gắn
cho bearer mới.

## 7. Nguyên tắc đánh giá khách quan

iStar không được cập nhật goal theo kiểu “Activity X biết nó ảnh hưởng Goal Y”.
Sau mỗi state change, checker phải:

1. tìm mọi ActorOccurrence active;
2. tìm ActorView tương ứng với type của occurrence;
3. instantiate quantified templates;
4. đánh giá activation/condition/pre/post trên state;
5. propagate refinement, contribution và dependency;
6. ghi một GoalSnapshot đầy đủ.

Nhờ đó một BPMN process có thể làm thay đổi goal của process khác hoặc làm hỏng
goal cá nhân mà không cần khai báo mapping Activity–Goal thủ công.
