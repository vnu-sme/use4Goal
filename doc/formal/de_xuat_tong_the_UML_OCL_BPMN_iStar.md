# Đề xuất tổng thể: Khung kiểm chứng UML/OCL + BPMN + iStar dựa trên State, Transition và Execution Trace

## 0. Mục đích của tài liệu

Tài liệu này tổng hợp và viết lại toàn bộ hướng thiết kế đã thảo luận cho một bộ công cụ có khả năng kết hợp:

- **UML Class Diagram** để định nghĩa cấu trúc dữ liệu và miền trạng thái;
- **OCL** để định nghĩa ràng buộc trên trạng thái và pre/postcondition của hành động;
- **BPMN** để định nghĩa control-flow của quy trình;
- **iStar** để mô hình hóa mục tiêu, nhiệm vụ và quan hệ refinement ở tầng intentional;
- **Scenario / execution trace** để làm đối tượng đánh giá cụ thể;
- và về sau có thể mở rộng sang **symbolic execution / model checking** để tự tìm counterexample.

Ý tưởng trung tâm là không xem bốn ngôn ngữ này như bốn hệ độc lập.

Thay vào đó, tất cả được nối vào một semantic core chung:

\[
\boxed{
\text{State}
\;+\;
\text{Transition}
\;+\;
\text{Trace}
}
\]

Từ semantic core này ta có thể trả lời ba loại câu hỏi độc lập nhưng liên quan:

\[
\boxed{
\text{Execution có hợp lệ về dữ liệu không?}
}
\]

\[
\boxed{
\text{Execution có tuân thủ quy trình không?}
}
\]

\[
\boxed{
\text{Execution có đạt được mục tiêu của stakeholder không?}
}
\]

---

# 1. Ý tưởng cốt lõi

Một execution của hệ thống được biểu diễn dưới dạng:

\[
\tau =
\sigma_0
\xrightarrow{a_1}
\sigma_1
\xrightarrow{a_2}
\sigma_2
\cdots
\xrightarrow{a_n}
\sigma_n
\]

Trong đó:

- \(\sigma_i\) là một **system state**;
- \(a_i\) là một **activity / task / operation**;
- toàn bộ \(\tau\) là một **scenario / execution trace**.

Với cấu trúc này:

- OCL invariant được đánh giá trên từng \(\sigma_i\);
- OCL pre/postcondition được đánh giá trên từng cặp \((\sigma_i,\sigma_{i+1})\);
- BPMN kiểm tra \(a_i\) có được phép xảy ra tại vị trí đó hay không;
- iStar kiểm tra trace đó có làm goal được fulfilled hay không.

Do đó, cùng một trace có thể được dùng bởi nhiều evaluator khác nhau.

---

# 2. Bốn tầng ngữ nghĩa

## 2.1. UML — State structure

UML Class Diagram trả lời:

> Một trạng thái hợp lệ của hệ thống có thể chứa những gì?

Nó định nghĩa:

- classes;
- attributes;
- associations;
- multiplicities;
- inheritance;
- object types.

Theo formalization kiểu Richters, một state có dạng:

\[
\sigma(M)
=
(\sigma_{Class},\sigma_{Att},\sigma_{Assoc})
\]

Trong đó:

- \(\sigma_{Class}\): những object đang tồn tại;
- \(\sigma_{Att}\): giá trị attribute;
- \(\sigma_{Assoc}\): những link đang tồn tại.

Ta ký hiệu không gian các data state hợp lệ là:

\[
\Sigma_M.
\]

---

## 2.2. OCL — State predicates và transition predicates

OCL có hai vai trò chính trong framework.

### State predicate

Một expression:

\[
\phi(\sigma)
\]

được đánh giá trên một state.

Ví dụ:

```ocl
context Account
inv Solvent:
    balance >= 0
```

Tương ứng:

\[
Inv(\sigma).
\]

### Transition predicate

Một operation contract liên hệ pre-state với post-state:

\[
Post_a(\sigma,\sigma').
\]

Ví dụ:

```ocl
context Order::approve()

pre:
    status = #reviewed

post:
    status = #approved
```

Precondition:

\[
Pre_{Approve}(\sigma)
\]

Postcondition:

\[
Post_{Approve}(\sigma,\sigma').
\]

Như vậy OCL cung cấp ngôn ngữ predicate trên:

\[
\Sigma_M
\]

và:

\[
\Sigma_M\times\Sigma_M.
\]

---

# 3. BPMN — Control-flow semantics

UML/OCL chưa nói activity nào được phép xảy ra tiếp theo.

BPMN bổ sung control state:

\[
\mu.
\]

Ví dụ \(\mu\) biểu diễn:

- token đang ở đâu;
- activity nào enabled;
- gateway nào đã được đi qua;
- parallel branch nào còn đang active.

Ta ký hiệu tập process states:

\[
Q_B.
\]

Một global state của hệ thống là:

\[
\boxed{
S=(\mu,\sigma)
}
\]

với:

\[
\mu\in Q_B
\]

và:

\[
\sigma\in\Sigma_M.
\]

BPMN định nghĩa:

\[
Enabled_B(a,\mu)
\]

và:

\[
ControlStep_B(\mu,a,\mu').
\]

---

# 4. iStar — Intentional satisfaction semantics

iStar trả lời một câu hỏi khác với BPMN:

> Hệ thống hoặc actor muốn đạt được điều gì?

Trong iStar 2.0, Goal là một state of affairs với tiêu chí achievement rõ ràng; Task là một hành động mà actor muốn được thực hiện.

Trong framework đề xuất ở đây, ta mở rộng Goal và Task bằng predicates OCL.

Đây là một **semantic extension của iStar**, không phải tuyên bố rằng iStar 2.0 chuẩn vốn đã có Achieve/Maintain và OCL contracts.

Ta đề xuất:

\[
Goal =
(name,kind,condition)
\]

với:

\[
kind\in
\{achieve,maintain\}
\]

ở phiên bản đầu.

Và:

\[
Task=
(name,pre,post).
\]

Các predicate:

\[
condition,\;pre,\;post
\]

được viết bằng OCL.

---

# 5. Semantic core thống nhất

Ta có global state:

\[
S=(\mu,\sigma).
\]

Một activity \(a\) tạo transition:

\[
\boxed{
(\mu,\sigma)
\xrightarrow{a}
(\mu',\sigma')
}
\]

khi:

\[
Enabled_B(a,\mu)
\]

\[
\land
Pre_a(\sigma)
\]

\[
\land
Post_a(\sigma,\sigma')
\]

\[
\land
ControlStep_B(\mu,a,\mu').
\]

Nếu yêu cầu invariant giữ sau mỗi bước:

\[
\land Inv(\sigma')
\]

cũng được thêm vào transition validity.

Đây có thể được xem là semantic kernel của tool.

---

# 6. Scenario / execution trace

Một scenario là:

\[
\tau=
S_0
\xrightarrow{a_1}
S_1
\xrightarrow{a_2}
\cdots
\xrightarrow{a_n}
S_n.
\]

Nếu chỉ quan tâm data state:

\[
\tau_\Sigma=
\sigma_0
\xrightarrow{a_1}
\sigma_1
\cdots
\xrightarrow{a_n}
\sigma_n.
\]

Scenario có thể dài:

\[
n=1
\]

hoặc:

\[
n>1.
\]

Do đó cùng một semantics hỗ trợ:

- kiểm tra một bước;
- kiểm tra một use case;
- kiểm tra một process instance;
- kiểm tra một execution log dài.

---

# 7. Hai chế độ sử dụng chính của tool

## 7.1. Validation mode

Người dùng hoặc hệ thống thực đưa vào:

\[
\tau_{input}.
\]

Tool không tự tạo state.

Nó chỉ hỏi:

\[
\tau_{input}\models Specification?
\]

Đây là chế độ gần với OCL evaluator hiện tại nhất.

Ví dụ input:

```text
σ0
CreateOrder
σ1
ShipOrder
σ2
```

Tool có thể kiểm tra:

- invariant của \(\sigma_0,\sigma_1,\sigma_2\);
- pre/post của `CreateOrder`;
- pre/post của `ShipOrder`;
- `ShipOrder` có đúng vị trí BPMN hay không;
- goal nào được đạt / bị vi phạm.

---

## 7.2. Verification / exploration mode

Tool tự xây hoặc biểu diễn symbolic các state khả dĩ.

Nó tìm:

- bad states;
- deadlocks;
- unreachable tasks;
- counterexample traces;
- invariant violations;
- process violations;
- goal failures.

Đây là bước mở rộng sang:

\[
\text{model checking}
\]

hoặc:

\[
\text{symbolic execution}.
\]

Hai chế độ có thể dùng chung semantic core.

---

# 8. OCL invariant checking

Với trace:

\[
\tau=
\sigma_0,\ldots,\sigma_n
\]

và invariant \(Inv\), ta kiểm tra:

\[
\boxed{
\forall i\in[0,n]:
Inv(\sigma_i)
}
\]

Ví dụ:

```ocl
context Account
inv:
    balance >= 0
```

Trace:

```text
σ0: balance = 100
σ1: balance = 30
σ2: balance = -20
σ3: balance = 10
```

Mặc dù state cuối hợp lệ:

\[
balance=10,
\]

trace vẫn chứa violation tại:

\[
\sigma_2.
\]

---

# 9. Task semantics

Một Task không nên được xem đơn thuần là một predicate trên state.

Nó là một **event / transition occurrence**.

Ta định nghĩa:

\[
Executed(t,i,\tau)
\]

nghĩa là task \(t\) được thực hiện tại transition thứ \(i\).

Một occurrence hợp lệ:

\[
\boxed{
ValidExec(t,i,\tau)
}
\]

khi:

\[
Executed(t,i,\tau)
\]

\[
\land
Pre_t(\sigma_{i-1})
\]

\[
\land
Post_t(\sigma_{i-1},\sigma_i).
\]

Điểm này tránh sai lầm:

\[
Pre\Rightarrow Post
\]

tự động đúng khi \(Pre=false\).

Task phải thực sự xuất hiện trong execution.

---

# 10. Task fulfillment và task conformance

Nên phân biệt hai khái niệm.

## 10.1. Fulfilled

Task đã được thực hiện thành công ít nhất một lần:

\[
\boxed{
Fulfilled(t,\tau)
\iff
\exists i:
ValidExec(t,i,\tau)
}
\]

---

## 10.2. Conformant

Mọi occurrence của task đều đúng contract:

\[
\boxed{
Conformant(t,\tau)
\iff
\forall i:
Executed(t,i,\tau)
\Rightarrow
ValidExec(t,i,\tau)
}
\]

Ví dụ task được gọi ba lần:

```text
Approve #1  valid
Approve #2  invalid precondition
Approve #3  valid
```

Ta có thể báo:

```text
Fulfilled: YES
Conformant: NO
```

Đây là thông tin tốt hơn một Boolean duy nhất.

---

# 11. Achieve Goal

Cho goal:

\[
g=Achieve(\phi)
\]

với OCL condition:

\[
\phi.
\]

Semantics đơn giản:

\[
\boxed{
Sat(Achieve(\phi),\tau)
\iff
\exists i\in[0,n]:
\phi(\sigma_i)
}
\]

Tức:

> Goal được fulfilled nếu có ít nhất một state trong scenario đạt condition.

Nó gần với temporal pattern:

\[
F\phi.
\]

---

# 12. Hai interpretation của Achieve cần phân biệt

## 12.1. State-of-affairs semantics

Nếu ngay state đầu:

\[
\phi(\sigma_0)=true,
\]

goal đã satisfied.

\[
\exists i:\phi(\sigma_i).
\]

Đây phù hợp với cách hiểu:

> trạng thái mong muốn tồn tại.

---

## 12.2. Achievement-event semantics

Có thể yêu cầu một chuyển đổi:

\[
false\rightarrow true.
\]

Formal:

\[
\boxed{
\exists i>0:
\neg\phi(\sigma_{i-1})
\land
\phi(\sigma_i)
}
\]

Đây phù hợp nếu muốn phân biệt:

> goal đã đúng từ đầu

với:

> execution thực sự làm goal trở thành đúng.

Tool có thể hỗ trợ cả hai.

Phiên bản đầu nên chọn một semantics mặc định và ghi rõ.

---

# 13. Maintain Goal

Cho:

\[
g=Maintain(\phi).
\]

Semantics:

\[
\boxed{
Sat(Maintain(\phi),\tau)
\iff
\forall i\in[0,n]:
\phi(\sigma_i)
}
\]

Nó gần với:

\[
G\phi.
\]

Ví dụ:

```ocl
balance >= 0
```

Trace:

\[
100\rightarrow80\rightarrow20
\]

satisfied.

Trace:

\[
100\rightarrow-5\rightarrow20
\]

không satisfied.

Maintain khác Achieve ở chỗ việc condition đúng ở state cuối không đủ.

---

# 14. Có thể mở rộng goal kinds

Sau Achieve/Maintain, framework có thể mở rộng:

## Avoid

\[
\boxed{
Sat(Avoid(\phi),\tau)
\iff
\forall i:\neg\phi(\sigma_i)
}
\]

gần với:

\[
G\neg\phi.
\]

## Cease

\[
\boxed{
Sat(Cease(\phi),\tau)
\iff
\exists i:\neg\phi(\sigma_i)
}
\]

gần với:

\[
F\neg\phi.
\]

Các pattern này có tiền lệ trong goal-oriented requirements, đặc biệt KAOS.

Nhưng chúng nên được xem là extension của iStar trong framework này.

---

# 15. OCL không cần trở thành temporal language ngay

Không nhất thiết viết:

```text
eventually(...)
always(...)
```

trong OCL.

Thay vào đó:

```text
Goal.kind = Achieve
Goal.condition = <OCL expression>
```

Evaluator biết:

```text
Achieve → tồn tại state thỏa condition
Maintain → mọi state thỏa condition
```

Như vậy:

\[
OCL
\]

chỉ làm nhiệm vụ:

\[
\boxed{
\text{đánh giá predicate trên state/transition}
}
\]

còn:

\[
iStar\ evaluator
\]

làm nhiệm vụ:

\[
\boxed{
\text{đánh giá temporal satisfaction trên trace}
}
\]

Cách phân tầng này đơn giản và sạch.

---

# 16. BPMN process conformance

BPMN kiểm tra activity có được phép thực hiện tại control state hiện tại không.

Với:

\[
S_i=(\mu_i,\sigma_i)
\]

và activity \(a\):

\[
\boxed{
Enabled_B(a,\mu_i)
}
\]

phải đúng.

Ví dụ BPMN:

```text
CreateOrder
    |
    v
ApproveOrder
    |
    v
ShipOrder
```

Trace:

```text
CreateOrder
ShipOrder
ApproveOrder
```

có thể có state data hợp lệ nhưng vẫn:

\[
\neg Enabled_B(ShipOrder,\mu_1).
\]

Do đó process không conform.

---

# 17. Kết hợp BPMN và task contract

Một activity chỉ hợp lệ nếu cả control-flow và data conditions cùng đúng:

\[
\boxed{
Enabled_B(a,\mu)
\land
Pre_a(\sigma)
}
\]

và sau transition:

\[
\boxed{
Post_a(\sigma,\sigma')
}
\]

cũng phải đúng.

Do đó:

\[
\boxed{
ProcessEnabled
\neq
DataEnabled
}
\]

Hai điều kiện này độc lập.

Một task có thể được BPMN enable nhưng precondition OCL false.

Hoặc precondition OCL true nhưng BPMN chưa cho phép task chạy.

---

# 18. Ba dimension đánh giá độc lập

Một execution có thể được đánh giá trên ba dimension:

## D1 — Data/contract correctness

\[
OCLCorrect(\tau)
\]

## D2 — Process conformance

\[
BPMNConformant(\tau)
\]

## D3 — Goal satisfaction

\[
GoalSatisfied(\tau).
\]

Không nên gộp ba Boolean này quá sớm.

Ví dụ:

```text
OCL:  true
BPMN: false
Goal: true
```

có nghĩa:

> execution đạt goal và các transition data đều hợp lệ, nhưng đạt bằng một quy trình không được phép.

Ngược lại:

```text
OCL:  true
BPMN: true
Goal: false
```

nghĩa:

> hệ thống thực hiện đúng quy trình, nhưng mục tiêu stakeholder vẫn chưa đạt.

Đây là thông tin cực kỳ giá trị.

---

# 19. Ví dụ tổng hợp

## 19.1. UML

```text
Order
-----------------
status : Status
paid   : Boolean
received : Boolean
```

Invariant:

```ocl
context Order
inv:
    self.status = #shipped implies self.paid
```

---

## 19.2. Tasks

### ApproveOrder

```ocl
pre:
    status = #reviewed

post:
    status = #approved
```

### ShipOrder

```ocl
pre:
    status = #approved and paid

post:
    status = #shipped
```

---

## 19.3. BPMN

```text
ReviewOrder
    |
    v
ApproveOrder
    |
    v
ShipOrder
```

---

## 19.4. iStar goals

### Goal 1

```text
Achieve [Order Shipped]
```

Condition:

```ocl
status = #shipped
```

### Goal 2

```text
Maintain [No Unpaid Shipment]
```

Condition:

```ocl
status = #shipped implies paid
```

### Goal 3

```text
Achieve [Customer Receives Order]
```

Condition:

```ocl
received = true
```

---

## 19.5. Trace

\[
\sigma_0:
status=reviewed,\ paid=true,\ received=false
\]

\[
\xrightarrow{ApproveOrder}
\]

\[
\sigma_1:
status=approved,\ paid=true,\ received=false
\]

\[
\xrightarrow{ShipOrder}
\]

\[
\sigma_2:
status=shipped,\ paid=true,\ received=false.
\]

Kết quả:

```text
OCL invariants:
    PASS

Task contracts:
    PASS

BPMN conformance:
    PASS

Achieve [Order Shipped]:
    SATISFIED

Maintain [No Unpaid Shipment]:
    SATISFIED

Achieve [Customer Receives Order]:
    NOT SATISFIED
```

Đây là ví dụ cho thấy process correctness không đồng nhất với goal satisfaction.

---

# 20. iStar refinement

iStar 2.0 có AND và inclusive-OR refinement.

Với AND refinement:

\[
g_1,\ldots,g_n
\rightarrow_{AND}
g.
\]

Fulfillment của tất cả children làm parent fulfilled theo semantics iStar.

Trong extension này, ta có hai tầng cần phân biệt:

1. **structural fulfillment** theo refinement;
2. **semantic condition satisfaction** từ OCL condition của parent.

---

# 21. Vấn đề parent condition và refinement

Giả sử:

```text
Parent Goal:
Achieve [Order Completed]

condition:
    status = #completed
```

AND refined thành:

```text
ApproveOrder
ShipOrder
```

Nếu cả hai task fulfilled nhưng:

\[
status\neq completed,
\]

ta có xung đột:

```text
Refinement-based fulfillment: TRUE
Condition-based fulfillment: FALSE
```

Framework phải quyết định xử lý.

Không nên bỏ qua xung đột này.

---

# 22. Đề xuất: tách hai loại satisfaction

Ta có thể định nghĩa:

\[
Sat_{cond}(g,\tau)
\]

dựa trên OCL condition.

Và:

\[
Sat_{ref}(g,\tau)
\]

dựa trên refinement.

Sau đó tool báo cả hai.

Ví dụ:

```text
Goal: Order Completed

Condition satisfaction:
    FALSE

Refinement fulfillment:
    TRUE

Consistency:
    CONFLICT
```

Điều này có giá trị phân tích cao hơn việc cưỡng ép một kết quả.

---

# 23. Refinement correctness như proof obligation

Ta có thể kiểm tra liệu refinement có thực sự đủ để bảo đảm parent condition hay không.

Với AND refinement:

\[
g_1\land\cdots\land g_n
\rightarrow g.
\]

Proof obligation:

\[
\boxed{
Sat(g_1,\tau)
\land\cdots\land
Sat(g_n,\tau)
\Rightarrow
Sat(g,\tau)
}
\]

Trên một trace cụ thể đây là validation.

Ở verification mode, có thể tìm trace:

\[
Sat(g_1,\tau)
\land\cdots\land Sat(g_n,\tau)
\land
\neg Sat(g,\tau).
\]

Nếu tìm được, refinement không sound theo semantics đang dùng.

---

# 24. OR refinement

Với inclusive OR:

\[
g_1\lor\cdots\lor g_n
\rightarrow g.
\]

Có thể kiểm tra:

\[
\boxed{
\left(
\bigvee_i Sat(g_i,\tau)
\right)
\Rightarrow
Sat(g,\tau)
}
\]

Nếu một child được cho là “means” của parent nhưng child fulfilled mà parent condition không đạt, mapping/refinement có vấn đề.

---

# 25. Task refinement

Một parent task:

\[
T
\]

có:

\[
Pre_T,\ Post_T.
\]

Nó được refine thành:

\[
T_1,\ldots,T_n.
\]

Nếu children được thực hiện sequentially:

\[
\sigma_0
\xrightarrow{T_1}
\sigma_1
\xrightarrow{T_2}
\cdots
\xrightarrow{T_n}
\sigma_n,
\]

ta có thể kiểm tra:

\[
\boxed{
Pre_T(\sigma_0)
\land
\bigwedge_i Post_{T_i}(\sigma_{i-1},\sigma_i)
\Rightarrow
Post_T(\sigma_0,\sigma_n)
}
\]

Đây là refinement correctness ở mức operation/task.

---

# 26. Relation giữa iStar Task và BPMN Activity

Cần có mapping:

\[
map_{TB}:
Task_{iStar}
\rightarrow
Activity_{BPMN}
\]

hoặc tổng quát:

\[
map_{TB}
\subseteq
Task_{iStar}
\times
Activity_{BPMN}.
\]

Không nhất thiết mapping luôn 1-1.

Có thể có:

### 1 task → 1 BPMN activity

Trường hợp đơn giản.

### 1 task → nhiều BPMN activities

Một task iStar trừu tượng được operationalize bằng nhiều bước quy trình.

### nhiều tasks → 1 activity

Một activity có thể đóng góp cho nhiều intentional tasks/goals.

Tool nên cho phép mapping rõ ràng thay vì dựa vào tên.

---

# 27. Goal operationalization

Một goal có thể được operationalize bởi một hoặc nhiều tasks.

Ví dụ:

```text
Achieve [Order Approved]
        |
        OR
       /  \
ManualApprove
AutoApprove
```

Nếu goal condition:

```ocl
status = #approved
```

thì tool có thể kiểm tra:

\[
SuccessfulExecution(ManualApprove)
\Rightarrow
status'=approved
\]

và:

\[
SuccessfulExecution(AutoApprove)
\Rightarrow
status'=approved.
\]

Nếu một operationalization không làm condition đạt được, tool phát hiện mismatch.

---

# 28. Goal evaluation trên subtrace

Không phải mọi goal đều nên đánh giá trên toàn bộ trace từ system start đến system end.

Một goal có thể active từ một điểm:

\[
i_{start}
\]

đến một điểm:

\[
i_{end}.
\]

Ta định nghĩa interval:

\[
\tau[i_{start}:i_{end}].
\]

Khi đó:

\[
Achieve(\phi)
\]

chỉ cần đạt trong interval.

\[
Maintain(\phi)
\]

chỉ cần giữ trong interval.

Đây là extension quan trọng nếu tool hỗ trợ nhiều process instances hoặc goals có activation conditions.

---

# 29. Goal trigger / activation condition

Có thể mở rộng:

\[
Goal=
(trigger,kind,target)
\]

Ví dụ:

```text
When:
    Order.status = #paid

Achieve:
    Order.status = #shipped
```

Semantics kiểu:

\[
Trigger(\sigma_i)
\Rightarrow
\exists j\ge i:
Target(\sigma_j).
\]

Điều này gần hơn với temporal goal patterns đầy đủ.

Phiên bản MVP có thể chưa cần.

---

# 30. Pre/postcondition của Task và goal achievement không nên đồng nhất

Một Task có:

\[
Post_t(\sigma,\sigma')
\]

không nhất thiết phải bằng goal condition:

\[
\phi_g(\sigma').
\]

Ví dụ task:

```text
SendPaymentRequest
```

postcondition:

```text
invoice.sent = true
```

nhưng goal:

```text
Achieve [Invoice Paid]
```

condition:

```text
invoice.paid = true
```

Task có thể đóng góp cho goal nhưng không trực tiếp đạt goal.

Do đó framework cần phân biệt:

\[
TaskEffect
\]

với:

\[
GoalTarget.
\]

---

# 31. Softgoal / Quality cần semantics khác

iStar Quality không nên bị ép thành Boolean OCL condition giống hard goal.

Một quality như:

```text
High Usability
```

không có achievement criterion rõ như:

```text
status = #approved.
```

Phiên bản đầu có thể:

- chỉ formalize Goal và Task;
- giữ Quality ở dạng qualitative contribution;
- hoặc cho phép metric-based evaluation sau.

Ví dụ:

\[
responseTime < 2s
\]

có thể formalize nếu có measurement rõ.

Nhưng không nên mặc định mọi Quality là hard invariant.

---

# 32. Contribution links

Các contribution:

```text
Make
Help
Hurt
Break
```

có thể được xử lý ở một tầng riêng.

Một hướng:

\[
Contribution(e,q,\tau)
\]

được tính từ:

- satisfaction của source;
- metric/value của target;
- contribution type.

Nhưng phần này nên là extension sau MVP.

---

# 33. Dependency links

iStar dependency có:

- depender;
- dependee;
- dependum.

Trong execution semantics, dependency có thể được kiểm tra bằng:

> nếu depender cần dependum để đạt goal, dependee có thực hiện/đạt dependum trong trace hay không?

Ví dụ:

\[
Depends(A,B,g)
\]

và:

\[
Sat(g,\tau_B).
\]

Có thể xây dependency satisfaction ở mức multi-actor traces.

Đây cũng nên là extension sau.

---

# 34. Proof obligation PO-1 — State validity

\[
\boxed{
Inv(\sigma_i)
}
\]

cho mọi state cần kiểm tra.

---

# 35. PO-2 — Task precondition

Nếu \(t\) chạy tại \(i\):

\[
\boxed{
Pre_t(\sigma_{i-1})
}
\]

---

# 36. PO-3 — Task postcondition

\[
\boxed{
Post_t(\sigma_{i-1},\sigma_i)
}
\]

---

# 37. PO-4 — BPMN activity enabledness

\[
\boxed{
Enabled_B(t,\mu_{i-1})
}
\]

---

# 38. PO-5 — Combined transition correctness

\[
\boxed{
Enabled_B(t,\mu)
\land
Pre_t(\sigma)
\land
Post_t(\sigma,\sigma')
}
\]

---

# 39. PO-6 — Achieve goal satisfaction

\[
\boxed{
\exists i:\phi_g(\sigma_i)
}
\]

---

# 40. PO-7 — Maintain goal satisfaction

\[
\boxed{
\forall i:\phi_g(\sigma_i)
}
\]

---

# 41. PO-8 — Task fulfillment

\[
\boxed{
\exists i:
ValidExec(t,i,\tau)
}
\]

---

# 42. PO-9 — Task conformance

\[
\boxed{
\forall i:
Executed(t,i,\tau)
\Rightarrow
ValidExec(t,i,\tau)
}
\]

---

# 43. PO-10 — Invariant preservation

\[
\boxed{
Inv(\sigma)
\land
Pre_t(\sigma)
\land
Post_t(\sigma,\sigma')
\Rightarrow
Inv(\sigma')
}
\]

Counterexample query:

\[
Inv(\sigma)
\land
Pre_t(\sigma)
\land
Post_t(\sigma,\sigma')
\land
\neg Inv(\sigma').
\]

---

# 44. PO-11 — Contract feasibility

\[
\boxed{
Inv(\sigma)
\land
Pre_t(\sigma)
\Rightarrow
\exists\sigma':
Post_t(\sigma,\sigma')
\land
Inv(\sigma')
}
\]

Nếu false, có state hợp lệ mà task được enable theo precondition nhưng không có legal post-state.

---

# 45. PO-12 — Sequential compatibility

Nếu BPMN có:

\[
A\rightarrow B,
\]

kiểm tra:

\[
\boxed{
Post_A(\sigma,\sigma')
\Rightarrow
Pre_B(\sigma')
}
\]

trong context thích hợp.

Nếu không đúng, A có thể đưa process vào data deadlock trước B.

---

# 46. PO-13 — Gateway completeness

Với guards:

\[
g_1,\ldots,g_n,
\]

kiểm tra:

\[
\boxed{
g_1\lor\cdots\lor g_n
}
\]

trên mọi reachable gateway state.

Nếu false, có state mà không branch nào chạy được.

---

# 47. PO-14 — XOR exclusiveness

\[
\boxed{
\forall i\neq j:
\neg(g_i\land g_j)
}
\]

nếu semantics yêu cầu guards mutually exclusive.

---

# 48. PO-15 — Deadlock freedom

\[
\boxed{
Reachable(S)
\land
\neg Final(S)
\Rightarrow
\exists a,S':
S\xrightarrow{a}S'
}
\]

Deadlock có thể là:

- control deadlock;
- data deadlock;
- combination deadlock.

---

# 49. PO-16 — Reachability

\[
\boxed{
\exists S:
Reachable(S)
\land
GoalState(S)
}
\]

Có thể dùng cho cả:

- bad-state reachability;
- desired-goal reachability.

---

# 50. PO-17 — Safety property

\[
\boxed{
Reachable(S)
\Rightarrow
Safe(S)
}
\]

Ví dụ:

```text
Never ship unpaid order.
```

---

# 51. PO-18 — Response / eventuality

Ví dụ:

> nếu order được approve thì cuối cùng phải ship hoặc cancel.

\[
\boxed{
G(
Approve
\Rightarrow
F(Ship\lor Cancel)
)
}
\]

Đây là trace property.

---

# 52. PO-19 — Goal refinement consistency

Với AND refinement:

\[
\boxed{
\bigwedge_i Sat(child_i,\tau)
\Rightarrow
Sat(parent,\tau)
}
\]

---

# 53. PO-20 — Task refinement correctness

\[
\boxed{
Pre_T(\sigma_0)
\land
ExecChildren(\sigma_0,\ldots,\sigma_n)
\Rightarrow
Post_T(\sigma_0,\sigma_n)
}
\]

---

# 54. PO-21 — Operationalization correctness

Nếu task \(t\) được xem là cách đạt goal \(g\):

\[
\boxed{
SuccessfulExecution(t,\sigma,\sigma')
\Rightarrow
Target_g(\sigma')
}
\]

hoặc với Achieve trên subtrace:

\[
\boxed{
SuccessfulExecution(t)
\Rightarrow
Sat(g,\tau_{relevant})
}
\]

---

# 55. PO-22 — BPMN–iStar mapping consistency

Nếu BPMN activity \(a\) được map tới iStar task \(t\):

\[
map(t)=a,
\]

mọi occurrence của \(a\) trong log phải được nhận diện là execution của \(t\).

Nếu task có contract, activity occurrence phải thỏa contract đó.

---

# 56. PO-23 — Whole scenario validity

Một trace được xem là globally valid nếu:

\[
\boxed{
ValidTrace(\tau)
}
\]

với tất cả transition đều thỏa:

\[
Enabled_B(a_i,\mu_{i-1})
\]

\[
Pre_{a_i}(\sigma_{i-1})
\]

\[
Post_{a_i}(\sigma_{i-1},\sigma_i)
\]

\[
ControlStep_B(\mu_{i-1},a_i,\mu_i)
\]

và các invariant cần thiết.

Goal satisfaction có thể được báo riêng, thay vì bắt buộc mọi goal phải true để trace là syntactically/process-valid.

---

# 57. Tại sao không nên định nghĩa "valid trace iff tất cả goals đều satisfied"?

Vì:

> Một execution có thể hợp lệ nhưng chưa hoàn thành.

Ví dụ trace dừng giữa process:

```text
CreateOrder
ReviewOrder
```

BPMN/OCL đều đúng nhưng goal:

```text
Order Shipped
```

chưa đạt.

Tool nên báo:

```text
Trace validity:
    VALID PREFIX

Goal:
    NOT YET SATISFIED
```

Thay vì:

```text
INVALID
```

Điều này quan trọng với runtime monitoring.

---

# 58. Prefix semantics

Với trace chưa kết thúc, Maintain Goal có thể được đánh giá ngay:

\[
\forall i\le n:\phi(\sigma_i).
\]

Nhưng Achieve Goal chưa đạt không có nghĩa đã bị vi phạm vĩnh viễn.

Nên có trạng thái ba giá trị:

\[
\boxed{
SATISFIED,\;
VIOLATED,\;
PENDING
}
\]

Ví dụ:

### Achieve

Condition chưa từng true nhưng process còn tiếp tục:

```text
PENDING
```

### Maintain

Condition false một lần:

```text
VIOLATED
```

### Achieve

Condition true:

```text
SATISFIED
```

Đây là một extension rất hữu ích cho runtime monitoring.

---

# 59. End-of-scenario semantics

Để nói Achieve bị violated, cần biết:

\[
End(\tau)
\]

hoặc deadline/termination condition.

Nếu trace là complete:

\[
Complete(\tau)
\]

và:

\[
\neg\exists i:\phi(\sigma_i),
\]

thì:

\[
Achieve(\phi)=VIOLATED.
\]

Nếu trace chỉ là prefix, kết quả nên là:

\[
PENDING.
\]

---

# 60. Điều này dẫn tới runtime verification

Tool có thể đọc execution theo thời gian:

```text
event 1
event 2
event 3
...
```

Sau mỗi event nó cập nhật:

- current system state;
- current BPMN marking;
- task contract result;
- goal status.

Ví dụ:

```text
Goal: Order Shipped
status: PENDING

Goal: No Unpaid Shipment
status: SATISFIED_SO_FAR

Process:
conformant_so_far = true
```

Nếu sau đó vi phạm:

```text
Maintain Goal violated at step 8.
```

Đây là runtime monitoring.

---

# 61. Kiến trúc tool đề xuất

## Module 1 — UML Metamodel Loader

Input:

```text
UML Class Diagram
```

Output:

\[
M
\]

và type environment.

---

## Module 2 — OCL Parser / Type Checker / Evaluator

Chức năng:

\[
Eval(\phi,\sigma)
\]

và:

\[
EvalPost(\psi,\sigma,\sigma').
\]

---

## Module 3 — State Representation

Lưu:

\[
\sigma=
(\sigma_{Class},\sigma_{Att},\sigma_{Assoc}).
\]

Có thể import:

- object diagram;
- JSON snapshot;
- execution DB state;
- generated model.

---

## Module 4 — BPMN Parser

Parse:

- activities;
- sequence flows;
- gateways;
- events;
- subprocesses;
- markings/control states.

---

## Module 5 — BPMN Execution Semantics

Tính:

\[
Enabled_B(a,\mu)
\]

và:

\[
ControlStep_B(\mu,a,\mu').
\]

---

## Module 6 — iStar Parser / Metamodel

Parse:

- actors;
- goals;
- tasks;
- qualities;
- resources;
- dependencies;
- refinements;
- contributions.

Phiên bản đầu chỉ formalize trực tiếp:

- Goal;
- Task;
- AND/OR refinement.

---

## Module 7 — OCL Annotation Layer

Cho phép gắn:

```text
Goal.condition
Task.precondition
Task.postcondition
```

và có thể:

```text
Goal.kind
```

---

## Module 8 — Mapping Layer

Mapping:

\[
iStarTask
\leftrightarrow
BPMNActivity.
\]

Có thể mapping thủ công hoặc bán tự động.

---

## Module 9 — Trace Loader

Input:

\[
\tau.
\]

Ví dụ format:

```json
{
  "states": [...],
  "events": [...]
}
```

---

## Module 10 — Combined Trace Evaluator

Tại mỗi transition:

1. kiểm tra source-state invariant;
2. kiểm tra BPMN enabledness;
3. kiểm tra task precondition;
4. kiểm tra task postcondition;
5. kiểm tra target-state invariant;
6. cập nhật process marking;
7. cập nhật goal satisfaction.

---

## Module 11 — Goal Evaluator

Hỗ trợ:

\[
Achieve
\]

\[
Maintain
\]

và sau:

\[
Avoid,\ Cease.
\]

---

## Module 12 — Refinement Analyzer

Tính:

\[
Sat_{ref}
\]

và so sánh:

\[
Sat_{ref}
\quad\text{vs}\quad
Sat_{cond}.
\]

---

## Module 13 — Verification Engine

Có thể dùng:

- SMT;
- SAT;
- bounded model checking;
- explicit-state exploration.

---

## Module 14 — Counterexample Reporter

Trả về trace nhỏ nhất hoặc hữu ích nhất:

```text
Step 0: ...
Step 1: ...
Step 2: violation
```

kèm:

- violated invariant;
- violated precondition;
- violated postcondition;
- BPMN illegal transition;
- failed goal;
- refinement inconsistency.

---

# 62. Output của tool

Không nên chỉ trả:

```text
TRUE/FALSE
```

Nên trả report nhiều chiều.

Ví dụ:

```text
Execution Trace Report

UML/OCL
-------
State invariants: PASS
Task contracts: FAIL
Violation at step 7:
    ShipOrder.pre = false

BPMN
----
Process conformance: PASS

iStar
-----
Achieve [Order Shipped]: PENDING
Maintain [No Unpaid Shipment]: SATISFIED
Achieve [Customer Receives Order]: PENDING

Refinement
----------
Order Fulfilled:
    refinement = TRUE
    condition = FALSE
    consistency = CONFLICT
```

---

# 63. Bounded verification

Nếu tool tự explore behavior, state space có thể vô hạn.

Có thể giới hạn:

\[
|\sigma_{Class}(c)|\le k_c
\]

và:

\[
length(\tau)\le k.
\]

Sau đó hỏi:

\[
\exists\tau,\; |\tau|\le k:
Violation(\tau)?
\]

Đây phù hợp với SMT/SAT.

---

# 64. Ví dụ bounded invariant search

Muốn tìm transition phá invariant:

\[
Inv(\sigma)
\]

ta solve:

\[
Inv(\sigma)
\land
Pre_t(\sigma)
\land
Post_t(\sigma,\sigma')
\land
\neg Inv(\sigma').
\]

SAT:

> có counterexample.

UNSAT:

> không có counterexample trong miền/encoding đã xét.

---

# 65. Bounded goal failure

Với Achieve Goal:

\[
F\phi,
\]

trong bound \(k\), tìm:

\[
\bigwedge_{i=0}^{k}
\neg\phi(\sigma_i)
\]

cùng với complete-process condition.

Nếu SAT:

> tồn tại complete execution dài tối đa \(k\) không đạt goal.

---

# 66. Bounded Maintain violation

Tìm:

\[
\exists i\le k:
\neg\phi(\sigma_i).
\]

Đây thường đơn giản hơn Achieve.

---

# 67. State explosion

Các yếu tố gây explosion:

- object creation/deletion;
- unbounded Integer;
- collection values;
- BPMN loops;
- parallel gateways;
- multiple process instances;
- many associations;
- nondeterministic postconditions.

Do đó MVP nên có bounds.

---

# 68. Nondeterministic postconditions

Không nên mặc định operation là hàm:

\[
op:\sigma\rightarrow\sigma'.
\]

OCL postcondition thường định nghĩa quan hệ:

\[
\boxed{
R_{op}\subseteq
\Sigma\times\Sigma
}
\]

Ví dụ:

```ocl
post:
    balance > balance@pre
```

cho phép nhiều \(\sigma'\).

Đây là lý do transition semantics nên relational.

---

# 69. Phân biệt validation với generation

Nếu trace do người dùng cung cấp:

\[
\sigma
\xrightarrow{a}
\sigma',
\]

ta chỉ kiểm tra:

\[
Post_a(\sigma,\sigma').
\]

Không có tautology.

Nếu tool tự sinh \(\sigma'\) bằng \(Post_a\), không nên sau đó coi việc:

\[
Post_a(\sigma,\sigma')
\]

đúng là một “proof” mới.

Trong generation mode, câu hỏi hữu ích là:

- successor có tồn tại không?
- mọi successor có giữ invariant không?
- successor có làm next task executable không?
- có bad successor không?
- có trace nào phá goal/process property không?

---

# 70. Quan hệ giữa OCL evaluator và tool đề xuất

OCL evaluator:

\[
(\sigma,\phi)
\mapsto
value.
\]

Tool đề xuất:

\[
(\tau,Specification)
\mapsto
AnalysisReport.
\]

Trong đó:

\[
Specification=
(UML,OCL,BPMN,iStar,Mappings).
\]

OCL evaluator vẫn là một component bên trong tool.

---

# 71. Một cách định nghĩa tổng quát

Cho:

\[
Spec=
(M,C,B,G,\Lambda)
\]

với:

- \(M\): UML object model;
- \(C\): OCL constraints/contracts;
- \(B\): BPMN model;
- \(G\): iStar goal model extension;
- \(\Lambda\): mappings giữa các model.

Một trace:

\[
\tau
\]

được đánh giá bởi:

\[
\boxed{
EvalSpec(Spec,\tau)
=
(R_{OCL},R_{BPMN},R_{Goal},R_{Ref})
}
\]

thay vì chỉ một Boolean.

---

# 72. Formal validity relation

Có thể định nghĩa:

\[
\tau\models_{OCL} C
\]

\[
\tau\models_{BPMN} B
\]

\[
\tau\models_{Goal} G.
\]

Và:

\[
\tau\models Spec
\]

chỉ khi cần một notion tổng hợp.

Không nhất thiết mọi use case đều cần aggregate Boolean.

---

# 73. Tách semantics khỏi syntax

Một nguyên tắc quan trọng:

> Không nên gắn semantics vào cách vẽ.

Ví dụ Goal node chỉ là syntax.

Semantics của:

```text
Achieve
```

được định nghĩa bằng:

\[
\exists i:\phi(\sigma_i).
\]

BPMN edge cũng là syntax.

Semantics của nó được định nghĩa bằng:

\[
ControlStep_B.
\]

Điều này giúp framework dễ formalize và implement.

---

# 74. Các quyết định semantic cần chốt trong nghiên cứu

## D-1 — Achieve nếu true ở initial state?

Có satisfied không?

---

## D-2 — Maintain bắt đầu từ khi nào?

System start?

Process start?

Goal activation?

---

## D-3 — Achieve khi nào bị violated?

Khi process end?

Khi deadline hết?

---

## D-4 — Một task cần chạy một lần hay mọi occurrence phải đúng?

Nên tách Fulfilled và Conformant.

---

## D-5 — Parent Goal condition và refinement mâu thuẫn xử lý thế nào?

Nên báo inconsistency.

---

## D-6 — Task–BPMN mapping 1-1 hay n-m?

Nên cho phép relation tổng quát.

---

## D-7 — Invariant kiểm tra ở mọi intermediate state hay chỉ stable states?

Cần quy định rõ nếu operation có internal microsteps.

---

## D-8 — Parallel BPMN semantics

Trace total-order có đủ không?

Có thể cần partial-order/event structure về sau.

---

## D-9 — Multiple actors/process instances

Goal satisfaction scope thuộc actor nào/process instance nào?

---

## D-10 — Quality semantics

Boolean, metric hay qualitative?

---

# 75. MVP đề xuất

Phiên bản đầu không nên ôm toàn bộ iStar/BPMN.

## MVP 1

Hỗ trợ:

- UML class model;
- finite object states;
- OCL invariants;
- task pre/postconditions;
- linear scenario;
- Achieve Goal;
- Maintain Goal.

Không cần BPMN.

Mục tiêu:

\[
Trace
\rightarrow
OCL + Goal\ evaluation.
\]

---

## MVP 2

Thêm:

- BPMN sequence;
- XOR gateway;
- process conformance;
- task/activity mapping.

Mục tiêu:

\[
Trace
\rightarrow
OCL + BPMN + Goal.
\]

---

## MVP 3

Thêm:

- AND/OR iStar refinement;
- refinement consistency;
- operationalization correctness.

---

## MVP 4

Thêm:

- SMT backend;
- bounded counterexample generation;
- deadlock;
- invariant preservation;
- goal reachability.

---

## MVP 5

Thêm:

- parallel BPMN;
- multi-instance;
- Avoid/Cease;
- runtime monitoring;
- dependencies;
- qualities.

---

# 76. Một prototype syntax khả thi

## Goal

```text
goal OrderShipped {
    type: achieve

    condition:
        Order.allInstances()
            ->exists(o | o.status = #shipped)
}
```

---

## Maintain Goal

```text
goal NoUnpaidShipment {
    type: maintain

    condition:
        Order.allInstances()
            ->forAll(o |
                o.status = #shipped implies o.paid
            )
}
```

---

## Task

```text
task ApproveOrder {
    pre:
        self.status = #reviewed

    post:
        self.status = #approved
}
```

---

## Mapping

```text
map iStarTask ApproveOrder
to BPMNActivity ApproveOrderTask
```

---

# 77. Trace format khả thi

```text
state S0
    o1 : Order
    o1.status = reviewed
    o1.paid = true

execute ApproveOrder

state S1
    o1 : Order
    o1.status = approved
    o1.paid = true

execute ShipOrder

state S2
    o1 : Order
    o1.status = shipped
    o1.paid = true
```

Evaluator chuyển nó thành:

\[
\sigma_0
\xrightarrow{ApproveOrder}
\sigma_1
\xrightarrow{ShipOrder}
\sigma_2.
\]

---

# 78. Kiến trúc logic tổng quát

Có thể xem toàn bộ framework là:

\[
\boxed{
\text{Domain Model}
}
\]

\[
\downarrow
\]

\[
\boxed{
\Sigma_M
}
\]

state space

\[
\downarrow
\]

\[
\boxed{
OCL\ contracts
}
\]

transition constraints

\[
+\]

\[
\boxed{
BPMN
}
\]

control constraints

\[
\downarrow
\]

\[
\boxed{
Transition\ System
}
\]

\[
\downarrow
\]

\[
\boxed{
Execution\ Traces
}
\]

\[
\downarrow
\]

\[
\boxed{
iStar\ Goal\ Satisfaction
}
\]

---

# 79. Một cách nhìn khác

UML/OCL định nghĩa:

\[
\boxed{
\text{What states are legal?}
}
\]

Task contracts định nghĩa:

\[
\boxed{
\text{What state changes are legal?}
}
\]

BPMN định nghĩa:

\[
\boxed{
\text{What execution order is legal?}
}
\]

iStar định nghĩa:

\[
\boxed{
\text{Why does this execution matter?}
}
\]

Scenario là:

\[
\boxed{
\text{What actually happened?}
}
\]

Verifier so sánh cái đã xảy ra với bốn loại specification phía trên.

---

# 80. Giá trị nghiên cứu tiềm năng

Điểm mới không nhất thiết nằm ở từng thành phần riêng lẻ.

Các thành phần sau đã có lịch sử nghiên cứu riêng:

- OCL state constraints;
- operation pre/postconditions;
- BPMN formal semantics;
- goal-oriented temporal patterns;
- iStar refinement.

Giá trị có thể nằm ở **semantic integration**:

\[
\boxed{
UML/OCL
+
BPMN
+
iStar
+
Trace-based evaluation
}
\]

với cùng một notion thống nhất về state và transition.

Đặc biệt:

1. Goal được đánh giá trực tiếp từ execution states;
2. Task contracts được đánh giá trên actual transitions;
3. BPMN được dùng để đánh giá process conformance;
4. iStar refinement được kiểm tra chứ không chỉ được giả định;
5. cùng một trace cho ra một báo cáo multi-dimensional.

---

# 81. Các câu hỏi nghiên cứu có thể hình thành

## RQ1

Có thể định nghĩa một semantics thống nhất cho UML/OCL, BPMN và iStar trên execution traces hay không?

## RQ2

OCL có đủ biểu đạt để làm state/transition predicate language cho goal/task extension hay không?

## RQ3

Có thể đánh giá Achieve/Maintain goals một cách decidable/effective trên bounded traces hay không?

## RQ4

Có thể tự động kiểm tra consistency giữa iStar refinement và OCL goal conditions hay không?

## RQ5

Có thể phát hiện execution đạt goal nhưng vi phạm BPMN, và execution conform BPMN nhưng không đạt goal hay không?

## RQ6

Có thể dùng SMT để sinh counterexample traces cho violations của invariants, contracts, process flow và goal satisfaction hay không?

## RQ7

State-space explosion ở mô hình kết hợp này nghiêm trọng đến mức nào, và abstraction/bounding nào hiệu quả?

---

# 82. Một đóng góp luận án có thể mô tả theo 4 lớp

## Contribution 1 — Formal metamodel integration

Định nghĩa mapping giữa:

- UML;
- OCL;
- BPMN;
- iStar.

---

## Contribution 2 — Trace semantics

Định nghĩa:

\[
Sat,
ValidExec,
ValidTrace,
Conformant,
Fulfilled.
\]

---

## Contribution 3 — Verification rules

Định nghĩa proof obligations PO-1 đến PO-23.

---

## Contribution 4 — Tool implementation

Parser + evaluator + trace checker + solver backend + counterexample report.

---

# 83. Quan hệ với iStar 2.0

Framework nên ghi rõ:

- giữ lại actor, goal, task, refinement từ iStar;
- không thay đổi ý nghĩa cơ bản rằng Goal là desired state of affairs và Task là action;
- bổ sung formal annotations và execution semantics;
- Achieve/Maintain là extension được lấy cảm hứng từ temporal goal patterns, không phải core metaclasses bắt buộc của iStar 2.0.

Điều này tránh claim quá mạnh.

---

# 84. Quan hệ với KAOS

KAOS là precedent quan trọng cho ý tưởng:

\[
Goal
=
property\ over\ histories.
\]

Achieve gần với:

\[
F\phi
\]

và Maintain gần với:

\[
G\phi.
\]

Điểm khác của framework đề xuất là dùng:

\[
OCL
\]

làm predicate language trên object states được định nghĩa bởi UML, sau đó dùng iStar structure để tổ chức goal/task/refinement.

---

# 85. Quan hệ với OCL

OCL không nhất thiết cần bị sửa semantics cốt lõi.

Có thể reuse:

\[
Eval_{OCL}(\phi,\sigma)
\]

và operation contracts.

Framework mới nằm ở bên ngoài evaluator:

\[
GoalEval(kind,\phi,\tau).
\]

Do đó:

\[
GoalEval(Achieve,\phi,\tau)
=
\exists\sigma_i\in\tau:
Eval_{OCL}(\phi,\sigma_i).
\]

Và:

\[
GoalEval(Maintain,\phi,\tau)
=
\forall\sigma_i\in\tau:
Eval_{OCL}(\phi,\sigma_i).
\]

---

# 86. Quan hệ với BPMN

BPMN cũng không nhất thiết phải biết goal semantics.

BPMN evaluator chỉ cần expose:

\[
Enabled_B
\]

và:

\[
ControlStep_B.
\]

Combined evaluator sẽ nối BPMN activity với iStar Task thông qua mapping.

Điều này tạo modular architecture.

---

# 87. Formal core tối thiểu

Nếu cần rút toàn bộ framework xuống vài định nghĩa cốt lõi, có thể dùng:

## State

\[
\sigma\in\Sigma_M.
\]

## Global state

\[
S=(\mu,\sigma).
\]

## Transition

\[
S\xrightarrow{a}S'.
\]

## Trace

\[
\tau=S_0a_1S_1\cdots a_nS_n.
\]

## Task execution

\[
ValidExec(t,i,\tau).
\]

## Achieve

\[
Sat(Achieve(\phi),\tau)
\iff
\exists i:\phi(\sigma_i).
\]

## Maintain

\[
Sat(Maintain(\phi),\tau)
\iff
\forall i:\phi(\sigma_i).
\]

## BPMN conformance

\[
\forall i:
Enabled_B(a_i,\mu_{i-1})
\land
ControlStep_B(\mu_{i-1},a_i,\mu_i).
\]

## Contract conformance

\[
\forall i:
Pre_{a_i}(\sigma_{i-1})
\land
Post_{a_i}(\sigma_{i-1},\sigma_i).
\]

Chỉ từng này đã đủ dựng một prototype có ý nghĩa.

---

# 88. Kết luận tổng thể

Framework có thể được tóm tắt bằng:

\[
\boxed{
\text{UML}
\Rightarrow
\text{state space}
}
\]

\[
\boxed{
\text{OCL}
\Rightarrow
\text{state/transition predicates}
}
\]

\[
\boxed{
\text{BPMN}
\Rightarrow
\text{allowed control-flow}
}
\]

\[
\boxed{
\text{iStar}
\Rightarrow
\text{intentional goals/tasks/refinements}
}
\]

\[
\boxed{
\text{Scenario}
\Rightarrow
\text{actual execution trace}
}
\]

Tool thực hiện:

\[
\boxed{
Specification
+
Trace
\longrightarrow
Verification\ Report
}
\]

Một execution có thể:

- hợp lệ về OCL nhưng sai BPMN;
- đúng BPMN nhưng không đạt goal;
- đạt goal nhưng dùng process không hợp lệ;
- task fulfilled nhưng có occurrence vi phạm contract;
- children refined elements fulfilled nhưng parent condition không đạt;
- tất cả local steps hợp lệ nhưng global Maintain Goal bị vi phạm;
- process structurally executable nhưng data preconditions gây deadlock.

Đây chính là lợi ích của việc không gộp UML/OCL, BPMN và iStar thành một ngôn ngữ duy nhất, mà để chúng đóng các vai trò semantic khác nhau trên cùng một execution model.

---

# 89. Hướng ưu tiên nếu bắt đầu xây thật

Thứ tự triển khai được khuyến nghị:

1. Formalize system state \(\sigma\).
2. Reuse/implement OCL evaluator.
3. Định nghĩa trace format.
4. Định nghĩa Task pre/post evaluation.
5. Định nghĩa Achieve/Maintain trace semantics.
6. Xây trace validator chưa có BPMN.
7. Thêm BPMN control state \(\mu\).
8. Thêm Task–Activity mapping.
9. Thêm refinement evaluation.
10. Thêm SMT/counterexample generation.

Nếu làm theo thứ tự này, mỗi bước đều tạo ra một tool chạy được và một semantics kiểm thử được, thay vì phải hoàn thành toàn bộ framework mới có kết quả.

---

# 90. Tài liệu nền tảng nên dùng khi viết formalization

## UML/OCL state semantics

Sử dụng formalization kiểu Richters:

- Object model syntax;
- object identifiers;
- association interpretation;
- system state;
- interpretation of object model.

Đây là nền cho:

\[
\Sigma_M.
\]

## iStar

Dùng iStar 2.0 Language Guide để giữ đúng:

- Goal;
- Task;
- Actor;
- AND refinement;
- inclusive OR refinement;
- fulfillment terminology.

## Temporal goal semantics

Dùng KAOS làm precedent cho:

- Achieve;
- Maintain;
- Avoid;
- Cease;
- goal satisfaction over histories.

## BPMN

Chọn một formal execution semantics rõ ràng cho:

- sequence flow;
- XOR;
- AND;
- events;
- termination.

Sau đó định nghĩa mapping sang:

\[
Q_B
\]

và:

\[
ControlStep_B.
\]

---

# 91. Câu mô tả ngắn nhất cho ý tưởng nghiên cứu

Có thể mô tả framework trong một câu:

> **A trace-based verification framework that integrates UML/OCL state semantics, BPMN process semantics, and an OCL-annotated iStar goal/task semantics to evaluate data correctness, process conformance, and stakeholder goal satisfaction over concrete or symbolically generated execution scenarios.**

Hoặc bằng toán:

\[
\boxed{
(UML+OCL)
+
BPMN
+
(iStar+GoalContracts)
+
Trace
\;\longrightarrow\;
Multi\text{-}dimensional\ Verification
}
\]

---

# 92. Điểm cần giữ khi phát triển tiếp

Ba nguyên tắc quan trọng nhất:

### Nguyên tắc 1

Không đánh đồng:

\[
Goal\ satisfaction
\]

với:

\[
Process\ conformance.
\]

### Nguyên tắc 2

Không đánh đồng:

\[
Task\ occurrence
\]

với:

\[
Task\ contract\ satisfaction.
\]

### Nguyên tắc 3

Không bắt OCL tự mang toàn bộ temporal semantics.

OCL nên đánh giá state/transition predicates; temporal interpretation nên do Goal/Trace evaluator đảm nhiệm.

Giữ được ba nguyên tắc này sẽ làm semantics của framework sáng sủa, modular và dễ chứng minh hơn.

---

# Tài liệu tham khảo định hướng

1. Mark Richters, *A Precise Approach to Validating UML Models and OCL Constraints* — nền formal cho object model, system state và OCL semantics.
2. Fabiano Dalpiaz, Xavier Franch, Jennifer Horkoff, *iStar 2.0 Language Guide* — định nghĩa core concepts và fulfillment/refinement trong iStar 2.0.
3. Axel van Lamsweerde và các công trình KAOS — goal patterns, histories, Achieve/Maintain/Avoid/Cease và temporal formalization.
4. Các công trình formal semantics/model checking cho BPMN — dùng làm nền để chọn \(Q_B\), marking và \(ControlStep_B\).
5. Các công trình process/goal compliance — dùng để so sánh framework đề xuất với những hướng nối process models và goal models đã tồn tại.

