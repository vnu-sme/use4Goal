# BPMN2 OCL Goal Metamodel

File metamodel sinh ra:

```text
doc/bpmnSource/bpmn2-ocl-goal-metamodel.use
```

## Mục Tiêu

Metamodel này mở rộng BPMN2 reduced metamodel trong `doc/drawio/05-bpmn2-metamodel.drawio` để phục vụ bài toán:

```text
Kiểm chứng một BPMN Task/Activity có làm cho một goal được thỏa mãn hay không.
```

Điểm tinh chỉnh quan trọng là không xem BPMN `Task`/`Activity` chính là domain operation. BPMN activity mô tả công việc trong quy trình; domain operation mới là hành vi làm thay đổi trạng thái hệ thống.

Vì vậy, metamodel dùng chuỗi nối sau:

```text
Activity
  invokes DomainOperation
    has precondition/postcondition OCL
      transforms SystemState before -> after
        checked by Goal.satisfactionCondition OCL
```

## Nguồn Thiết Kế

Phần BPMN giữ theo Draw.io:

```text
Model
  processes: Process[*]
  messages: Message[*]
  messageFlows: MessageFlow[*]

Process
  lanes: Lane[*]
  flowElements: FlowElement[*]
  sequenceFlows: SequenceFlow[*]

FlowElement
  Event
    StartEvent
    EndEvent
    IntermediateEvent
  Activity
    BpmnTask
    CallActivity
    SubProcess
  Gateway
```

Các phần mở rộng được thêm:

```text
OclExpression
DomainModel / DomainClass / DomainAttribute / DomainOperation
GoalModel / Goal / GoalRefinement
SystemState / ObjectInstance / Slot
OperationGoalProof
AchievementEvaluation
GoalContribution
```

## Vì Sao Không Gộp Task Và Operation?

Ví dụ:

```text
BPMN Task:        Approve Order
Domain Operation: Order::approve()
```

`Approve Order` là một bước trong process. `Order::approve()` là hành vi tác động lên domain state, có thể được đặc tả bằng OCL:

```text
context Order::approve()
pre PendingOrder:
  self.status = OrderStatus::PENDING

context Order::approve()
post ApprovedOrder:
  self.status = OrderStatus::APPROVED
```

Do đó quan hệ cốt lõi là:

```text
Activity 0..* --invokes-- 0..* DomainOperation
```

Một activity có thể gọi nhiều operation, và một operation cũng có thể được dùng lại bởi nhiều activity.

## OCL Trong Metamodel

`OclExpression` là lớp chung cho các biểu thức OCL:

```text
expressionId : String
name         : String
kind         : OclExpressionKind
contextType  : String
body         : String
```

Các `kind` chính:

```text
precondition
postcondition
guard
assertion
satisfaction
invariant
```

Metamodel cố ý lưu `body` như text. Việc compile/kiểm tra cú pháp OCL vẫn nên do USE `OCLCompiler` hoặc compiler hiện có trong project đảm nhận. Nhờ vậy phần metamodel không phải tự parse OCL.

## OCL Gắn Với BPMN

OCL có thể gắn lên:

```text
FlowElement.flowElementOcl
SequenceFlow.sequenceFlowOcl
```

Ý nghĩa:

```text
FlowElement OCL:
  invariant, assertion, guard, hoặc satisfaction condition tại một điểm BPMN.

SequenceFlow OCL:
  guard/assertion cho nhánh điều kiện.
```

Đây tương thích với hướng hiện có trong `goal/docs/BPMN2_OCL_OPTION2.md`, nơi `.bpmn2` cho phép:

```text
task approveClaim "Approve Claim" ocl {[ ... ]}
flow decide -> approveClaim : "valid" ocl {[ ... ]}
```

## Goal Và Satisfaction Condition

Mỗi `Goal` có đúng một `satisfactionCondition`:

```text
Goal --satisfactionCondition--> OclExpression(kind = satisfaction)
```

Ví dụ:

```text
Goal: Order is approved

context Order
inv OrderApproved:
  self.status = OrderStatus::APPROVED
```

Điều kiện goal được đánh giá trên trạng thái sau khi operation chạy.

## SystemState

Metamodel thêm phần state tối thiểu:

```text
SystemState
  objects: ObjectInstance[*]

ObjectInstance
  classifier: DomainClass
  slots: Slot[*]

Slot
  attribute: DomainAttribute
  valueLiteral: String
```

Mục đích là biểu diễn snapshot trước/sau:

```text
before: order1.status = PENDING
after:  order1.status = APPROVED
```

## Achievement Semantics

Ở mức execution cụ thể:

```text
OperationAchievesGoal(op, g, s, s') =
  pre(op, s)
  and post(op, s, s')
  and goal(g, s')
```

Ở mức thiết kế, tiêu chuẩn mạnh hơn là:

```text
forall s, s':
  pre(op, s) and post(op, s, s') implies goal(g, s')
```

Trong metamodel, nghĩa này được biểu diễn bằng `OperationGoalProof`:

```text
OperationGoalProof
  provenOperation: DomainOperation
  provenGoal: Goal
  kind: designImplication
```

Lý do cần lớp proof: `OclExpression.body` là text, nên bản thân metamodel không thể tự chứng minh logical implication giữa postcondition và satisfaction condition. Proof là kết quả của checker/USE/compiler bên ngoài.

## Achieves Và Contributes

Metamodel phân biệt:

```text
achieved:
  operation postcondition đủ để kéo theo goal satisfaction condition.

contributed:
  operation giúp tiến gần goal nhưng chưa đủ để bảo đảm goal.

violated:
  operation làm goal sai hoặc làm trạng thái sau vi phạm.

notAchieved:
  không chứng minh được achieved/contributed.
```

`GoalContribution.kind = make` không nên chỉ do người mô hình hóa tự gán. Vì vậy invariant `MakeContributionRequiresProof` yêu cầu phải có `OperationGoalProof(kind = designImplication)`.

## Goal Refinement

Metamodel hỗ trợ AND/OR decomposition:

```text
GoalRefinement
  kind: andRefinement | orRefinement
  parent: Goal
  children: Goal[1..*]
```

Diễn giải:

```text
AND:
  parent satisfied nếu tất cả child goals satisfied.

OR:
  parent satisfied nếu ít nhất một child goal satisfied.
```

Phần này là cấu trúc metamodel; thuật toán lan truyền satisfaction nên nằm ở checker/semantic analyzer.

## Các Invariant Chính

File `.use` có các nhóm invariant:

```text
BPMN structure:
  unique process/message/flow element ids
  sequence flow endpoints thuộc cùng process
  lane chỉ partition flow elements của process chứa lane
  StartEvent không có incoming
  EndEvent không có outgoing

OCL attachment:
  OclExpression.body không rỗng
  OclExpression.contextType không rỗng
  FlowElement chỉ nhận invariant/guard/assertion/satisfaction
  SequenceFlow chỉ nhận guard/assertion

Domain operation:
  preconditions phải có kind = precondition
  postconditions phải có kind = postcondition
  operation phải có ít nhất một postcondition

Goal:
  goal id duy nhất trong GoalModel
  satisfactionCondition phải có kind = satisfaction
  GoalRefinement không tự tham chiếu parent làm child

Verification:
  AchievementEvaluation.operation phải là operation được activity gọi
  result = achieved yêu cầu after state và design proof
  result = violated yêu cầu before/after state cụ thể
```

## Kết Luận Thiết Kế

Phần BPMN hiện tại đã đủ cho process structure. Không cần mở rộng thành full execution metamodel với token/trace/message runtime cho bài toán này.

Phần cần thêm và đã được đưa vào file `.use` là:

```text
1. DomainOperation
2. OCL precondition/postcondition
3. Goal.satisfactionCondition
4. SystemState before/after
5. OperationGoalProof để ghi nhận post(operation) implies goal(condition)
```

Tiêu chuẩn kiểm chứng cuối cùng:

```text
Activity achieves Goal
iff
exists DomainOperation invoked by Activity
and post(DomainOperation) implies Goal.satisfactionCondition
```

Nếu implication chưa đủ nhưng operation cải thiện một phần điều kiện goal, activity chỉ nên được đánh dấu `contributed`, không phải `achieved`.
