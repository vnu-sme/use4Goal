# Hướng xây dựng bộ kiểm chứng UML/OCL + BPMN dựa trên trạng thái và quy trình thực thi

## 1. Mục tiêu

Ý tưởng của hệ thống là mở rộng cách kiểm tra dựa trên **system state** của UML/OCL sang việc kiểm tra cả **quy trình thực thi**.

Có thể xem hệ thống gồm ba lớp thông tin chính:

1. **UML Class Diagram**  
   Định nghĩa cấu trúc dữ liệu và không gian trạng thái hợp lệ của hệ thống.

2. **OCL invariants và pre/postconditions**  
   Định nghĩa các ràng buộc trên trạng thái và quan hệ giữa trạng thái trước/sau của một bước thực thi.

3. **BPMN process model**  
   Định nghĩa thứ tự, phân nhánh, đồng bộ và các quy tắc control-flow mà các bước thực thi phải tuân theo.

Mục tiêu không nhất thiết phải là tự động sinh toàn bộ hành vi của hệ thống. Một chức năng rất quan trọng và gần với cách các tool OCL hiện tại hoạt động là:

> Người dùng cung cấp một trạng thái hoặc một chuỗi trạng thái / chuỗi sự kiện thực thi, sau đó tool kiểm tra xem trạng thái hoặc execution trace đó có thỏa đặc tả hay không.

Với OCL thuần túy, đặc tả chủ yếu nói về trạng thái và operation contracts.

Khi bổ sung BPMN, đặc tả còn nói thêm:

> Các operation/activity có được thực hiện đúng thứ tự và đúng cấu trúc quy trình đã quy định hay không?

---

# 2. Nền tảng từ mô hình trạng thái của UML/OCL

Theo cách formalize trong Chương 3 của Richters, một system state của model \(M\) có dạng:

\[
\sigma(M)
=
(\sigma_{Class},\sigma_{Att},\sigma_{Assoc})
\]

Trong đó:

- \(\sigma_{Class}\) cho biết những object nào đang tồn tại;
- \(\sigma_{Att}\) cho biết giá trị attribute của các object;
- \(\sigma_{Assoc}\) cho biết những association link nào đang tồn tại.

Như vậy một trạng thái cụ thể:

\[
\sigma
\]

có thể được xem như một snapshot của hệ thống.

Một OCL invariant:

```ocl
context Order
inv:
    self.total >= 0
```

được đánh giá trên một trạng thái cụ thể:

\[
\sigma \models Inv
\]

hay không.

Về bản chất, một OCL evaluator có thể được xem như thực hiện:

\[
Eval(M,\sigma,C)
\rightarrow
\{true,false\}
\]

trong đó:

- \(M\) là model;
- \(\sigma\) là system state;
- \(C\) là constraint cần đánh giá.

---

# 3. Hai chế độ kiểm tra khác nhau nhưng liên quan chặt chẽ

Tool UML/OCL + BPMN có thể có ít nhất hai chế độ.

## 3.1. Kiểm tra một trạng thái

Đây là chế độ gần nhất với OCL evaluator hiện tại.

Input:

\[
(M,\sigma,C)
\]

Tool kiểm tra:

\[
\sigma\models C
\]

Ví dụ người dùng cung cấp:

```text
o1 : Order
status = "approved"
total = 120
```

và invariant:

```ocl
context Order
inv:
    total >= 0
```

Tool trả về:

```text
VALID
```

hoặc:

```text
INVALID
```

kèm constraint bị vi phạm.

---

## 3.2. Kiểm tra một execution trace

Người dùng không chỉ đưa vào một snapshot mà đưa vào cả một quy trình thực thi đã xảy ra:

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

- \(\sigma_i\) là trạng thái hệ thống;
- \(a_i\) là activity/operation được thực thi.

Tool sẽ kiểm tra trace này trên nhiều phương diện.

Đây là điểm mà BPMN bổ sung sức mạnh quan trọng.

---

# 4. BPMN không thay thế OCL mà bổ sung control-flow

Nếu chỉ có UML/OCL, ta có thể biết:

```text
ApproveOrder
```

có precondition và postcondition gì.

Ví dụ:

\[
Pre_{Approve}(\sigma)
\]

và:

\[
Post_{Approve}(\sigma,\sigma')
\]

Nhưng chỉ riêng các contract này chưa nhất thiết nói:

- `ApproveOrder` có được phép xảy ra sau `CreateOrder` hay không;
- `ShipOrder` có được phép chạy trước `ApproveOrder` không;
- sau một gateway thì branch nào được phép được chọn;
- hai activity có phải chạy song song hay không;
- activity nào phải hoàn thành trước activity nào.

BPMN cung cấp chính phần này.

Do đó:

\[
\boxed{
UML/OCL = quy tắc về trạng thái và biến đổi dữ liệu
}
\]

\[
\boxed{
BPMN = quy tắc về control-flow
}
\]

Khi kết hợp hai phần, một execution trace chỉ hợp lệ nếu đồng thời thỏa:

\[
\boxed{
\text{data constraints}
\land
\text{operation contracts}
\land
\text{process control-flow}
}
\]

---

# 5. Trạng thái toàn cục

Để formalize BPMN cùng với object state, nên phân biệt hai loại state.

## 5.1. Data state

\[
\sigma
\]

là UML/OCL system state:

\[
\sigma=
(\sigma_{Class},\sigma_{Att},\sigma_{Assoc})
\]

---

## 5.2. Process state

\[
\mu
\]

biểu diễn trạng thái hiện tại của BPMN process.

Ví dụ \(\mu\) có thể biểu diễn token/marking hiện tại:

```text
CreateOrder đã xong
ApproveOrder đang enabled
ShipOrder chưa enabled
```

---

## 5.3. Global state

Trạng thái đầy đủ của hệ thống nên là:

\[
\boxed{
S=(\mu,\sigma)
}
\]

Trong đó:

- \(\mu\): control state;
- \(\sigma\): object/data state.

Một execution:

\[
S_0
\rightarrow
S_1
\rightarrow
S_2
\rightarrow
\cdots
\]

tương đương:

\[
(\mu_0,\sigma_0)
\rightarrow
(\mu_1,\sigma_1)
\rightarrow
(\mu_2,\sigma_2)
\rightarrow
\cdots
\]

---

# 6. Semantics của một activity

Với mỗi activity \(a\), ta có:

\[
Pre_a(\sigma)
\]

và:

\[
Post_a(\sigma,\sigma')
\]

BPMN quyết định activity \(a\) có được phép chạy tại process state \(\mu\) hay không:

\[
Enabled_{BPMN}(a,\mu)
\]

Nếu sau khi chạy activity, control-flow chuyển sang:

\[
\mu'
\]

thì có thể định nghĩa transition:

\[
\boxed{
(\mu,\sigma)
\xrightarrow{a}
(\mu',\sigma')
}
\]

khi và chỉ khi:

\[
Enabled_{BPMN}(a,\mu)
\]

và:

\[
Pre_a(\sigma)
\]

và:

\[
Post_a(\sigma,\sigma')
\]

và:

\[
ControlStep(\mu,a,\mu')
\]

đều đúng.

Nếu model còn có global invariants \(Inv\), có thể yêu cầu thêm:

\[
Inv(\sigma)
\]

và:

\[
Inv(\sigma')
\]

tùy theo semantics mong muốn.

---

# 7. Chỉnh lại vấn đề "postcondition luôn đúng"

Một nhận xét dễ gây lệch hướng là:

> Nếu state sau được sinh từ postcondition rồi lại kiểm tra postcondition thì kết quả tất nhiên luôn đúng.

Điều này đúng trong một trường hợp rất cụ thể, nhưng **không phải hạn chế chính của tool đang được đề xuất**.

Tool không nhất thiết phải tự sinh trace.

Nó có thể hoạt động giống OCL evaluator:

> Người dùng hoặc một hệ thống bên ngoài cung cấp execution trace thực tế; tool chỉ kiểm tra trace đó có phù hợp với specification hay không.

Ví dụ input:

\[
\sigma_0
\xrightarrow{CreateOrder}
\sigma_1
\xrightarrow{ShipOrder}
\sigma_2
\]

Tool có thể phát hiện:

```text
ShipOrder không được phép chạy tại đây
```

vì BPMN yêu cầu:

```text
CreateOrder
    |
    v
ApproveOrder
    |
    v
ShipOrder
```

Ngay cả khi:

\[
Post_{ShipOrder}(\sigma_1,\sigma_2)
\]

đúng hoàn toàn, execution vẫn sai vì:

\[
Enabled_{BPMN}(ShipOrder,\mu_1)=false.
\]

Đây chính là một loại lỗi mà chỉ kiểm tra pre/postcondition không thể biểu diễn đầy đủ.

Do đó có hai mode hợp lệ:

### Validation mode

Trace được đưa từ bên ngoài:

\[
\tau_{input}
\]

Tool kiểm tra:

\[
\tau_{input}\models Specification
\]

### Generation / exploration mode

Tool tự sinh các successor thỏa postcondition và BPMN rồi tìm reachable states hoặc counterexamples.

Hai mode này khác nhau nhưng có thể dùng chung formal semantics.

---

# 8. Hướng kiểm chứng 1 — State invariant checking

Đây là chức năng cơ bản nhất.

Với mỗi state \(\sigma_i\) trong trace:

\[
Inv(\sigma_i)
\]

phải đúng.

Verification obligation:

\[
\boxed{
\forall i:\; Inv(\sigma_i)
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
σ1: balance = 40
σ2: balance = -20
```

Tool báo:

```text
Invariant violation at σ2
```

Đây gần như chính xác là việc áp dụng OCL evaluator lặp lại trên từng trạng thái của trace.

---

# 9. Hướng kiểm chứng 2 — Precondition checking

Nếu activity \(a_i\) được thực thi từ state \(\sigma_i\), cần kiểm tra:

\[
\boxed{
Pre_{a_i}(\sigma_i)
}
\]

Ví dụ:

```ocl
context Order::approve()
pre:
    status = 'created'
```

Trace:

```text
σ0:
status = "cancelled"

execute ApproveOrder
```

thì tool phát hiện:

\[
Pre_{Approve}(\sigma_0)=false
\]

và báo:

```text
Precondition violation
```

---

# 10. Hướng kiểm chứng 3 — Postcondition checking

Nếu execution log cung cấp cả pre-state và post-state:

\[
\sigma_i
\xrightarrow{a}
\sigma_{i+1}
\]

tool kiểm tra:

\[
\boxed{
Post_a(\sigma_i,\sigma_{i+1})
}
\]

Ví dụ:

```ocl
context Order::approve()
post:
    status = 'approved'
```

Nếu:

```text
σ0:
status = "created"

ApproveOrder

σ1:
status = "created"
```

thì:

\[
Post_{Approve}(\sigma_0,\sigma_1)=false.
\]

Tool phát hiện implementation/execution thực tế không tuân theo contract.

Đây không phải tautology vì \(\sigma_{i+1}\) được cung cấp từ execution thực tế chứ không phải bắt buộc do verifier tự dựng từ postcondition.

---

# 11. Hướng kiểm chứng 4 — BPMN control-flow conformance

Đây là chức năng bổ sung quan trọng nhất.

Giả sử BPMN:

```text
CreateOrder
    |
    v
ApproveOrder
    |
    v
ShipOrder
```

Execution trace:

```text
CreateOrder
ShipOrder
ApproveOrder
```

OCL pre/post của từng operation đôi khi vẫn có thể thỏa trong những data state cụ thể.

Nhưng trace:

\[
CreateOrder
\rightarrow
ShipOrder
\rightarrow
ApproveOrder
\]

không thuộc control-flow được BPMN cho phép.

Ta kiểm tra tại mỗi bước:

\[
\boxed{
Enabled_{BPMN}(a_i,\mu_i)
}
\]

Nếu sai:

\[
Enabled_{BPMN}(a_i,\mu_i)=false
\]

thì trace vi phạm process model.

---

# 12. Hướng kiểm chứng 5 — Kiểm tra đồng thời control-flow và data-flow

Một execution step hợp lệ cần thỏa cả hai nhóm điều kiện:

\[
Enabled_{BPMN}(a,\mu)
\]

và:

\[
Pre_a(\sigma).
\]

Ví dụ BPMN cho phép `ShipOrder` chạy sau `ApproveOrder`.

Nhưng state hiện tại:

```text
status = "cancelled"
```

và `ShipOrder` có:

```ocl
pre:
    status = 'approved'
```

thì BPMN có thể nói:

\[
Enabled_{BPMN}(ShipOrder,\mu)=true
\]

nhưng OCL nói:

\[
Pre_{ShipOrder}(\sigma)=false.
\]

Do đó activity vẫn không hợp lệ.

Một bước chỉ được chấp nhận nếu:

\[
\boxed{
Enabled_{BPMN}(a,\mu)
\land
Pre_a(\sigma)
}
\]

---

# 13. Hướng kiểm chứng 6 — Invariant preservation

Nếu tool có khả năng suy luận hoặc tự sinh successor state, có thể kiểm tra mạnh hơn:

> Một activity có luôn bảo toàn global invariant không?

Proof obligation:

\[
\boxed{
Inv(\sigma)
\land
Pre_a(\sigma)
\land
Post_a(\sigma,\sigma')
\Rightarrow
Inv(\sigma')
}
\]

Để tìm counterexample, solver có thể giải:

\[
Inv(\sigma)
\land
Pre_a(\sigma)
\land
Post_a(\sigma,\sigma')
\land
\neg Inv(\sigma').
\]

Nếu SAT thì tồn tại execution hợp lệ theo contract nhưng phá invariant.

Nếu UNSAT thì activity bảo toàn invariant trong miền đang xét.

---

# 14. Hướng kiểm chứng 7 — Feasibility của operation contract

Một operation có precondition hợp lệ nhưng postcondition mâu thuẫn có thể không có successor state nào.

Kiểm tra:

\[
\boxed{
Inv(\sigma)
\land
Pre_a(\sigma)
\Rightarrow
\exists\sigma':
Post_a(\sigma,\sigma')
\land
Inv(\sigma')
}
\]

Nếu tồn tại \(\sigma\) thỏa precondition nhưng không tồn tại \(\sigma'\), operation có một vùng input mà contract không thể thực hiện được.

Counterexample query có dạng:

\[
Inv(\sigma)
\land
Pre_a(\sigma)
\land
\neg
\exists\sigma':
Post_a(\sigma,\sigma')
\land
Inv(\sigma').
\]

---

# 15. Hướng kiểm chứng 8 — Compatibility giữa các activity liên tiếp

Nếu BPMN có:

```text
A --> B
```

ta có thể hỏi:

> Mọi state sinh ra hợp lệ sau A có luôn đủ điều kiện để B chạy không?

Proof obligation mạnh:

\[
\boxed{
Inv(\sigma)
\land
Pre_A(\sigma)
\land
Post_A(\sigma,\sigma')
\Rightarrow
Pre_B(\sigma')
}
\]

Nếu không đúng, solver có thể trả một state:

\[
\sigma'
\]

mà:

\[
Post_A(\sigma,\sigma')
\]

đúng nhưng:

\[
Pre_B(\sigma')=false.
\]

Điều này phát hiện **data-dependent process deadlock**.

---

# 16. Hướng kiểm chứng 9 — Gateway guard completeness

Ví dụ XOR gateway có hai branch:

```text
amount < 1000
```

và:

```text
amount > 1000
```

Ta kiểm tra:

\[
\boxed{
guard_1(\sigma)\lor guard_2(\sigma)
}
\]

có luôn đúng trong mọi state reachable tại gateway hay không.

Nếu:

\[
amount=1000
\]

thì:

\[
guard_1=false
\]

và:

\[
guard_2=false.
\]

Process bị kẹt.

Có thể kiểm tra bằng:

\[
ReachableGatewayState(\sigma)
\land
\neg guard_1(\sigma)
\land
\neg guard_2(\sigma).
\]

Nếu SAT thì tìm được counterexample.

---

# 17. Hướng kiểm chứng 10 — Gateway guard exclusiveness

Với XOR gateway, đôi khi cần đảm bảo không có hai branch cùng đúng:

\[
\boxed{
\neg(guard_i(\sigma)\land guard_j(\sigma))
}
\]

cho mọi:

\[
i\neq j.
\]

Nếu:

\[
guard_1(\sigma)\land guard_2(\sigma)
\]

SAT thì gateway có thể bị ambiguity.

---

# 18. Hướng kiểm chứng 11 — Reachability

Có thể kiểm tra:

> Có tồn tại một execution hợp lệ dẫn tới trạng thái \(Bad\) hay không?

Formal:

\[
\boxed{
\exists
S_0\rightarrow S_1\rightarrow\cdots\rightarrow S_n
:
Bad(S_n)
}
\]

Ví dụ:

```text
Có thể đạt tới trạng thái:
Order.status = "shipped"
AND
Order.paymentStatus != "paid"
?
```

Nếu có, tool trả counterexample trace.

---

# 19. Hướng kiểm chứng 12 — Forbidden sequence

BPMN/process specification có thể quy định rằng một số chuỗi activity không được xuất hiện.

Ví dụ:

```text
ShipOrder không được xuất hiện trước ApproveOrder.
```

Một trace:

```text
CreateOrder
ShipOrder
ApproveOrder
```

bị reject ngay cả nếu từng state riêng lẻ không vi phạm invariant.

Property có thể biểu diễn dưới dạng trace constraint:

\[
Ship
\Rightarrow
Previously(Approve)
\]

hoặc qua trạng thái BPMN:

\[
Enabled_{BPMN}(Ship,\mu)=false
\]

cho tới khi `Approve` đã hoàn thành.

---

# 20. Hướng kiểm chứng 13 — Required sequence / response property

Ví dụ:

> Sau `ApproveOrder`, cuối cùng phải có `ShipOrder` hoặc `CancelOrder`.

Có thể biểu diễn kiểu temporal property:

\[
G(
Approve
\Rightarrow
F(Ship\lor Cancel)
)
\]

Trong đó:

- \(G\): globally;
- \(F\): eventually.

Loại property này không thể kiểm tra chỉ bằng một system state đơn lẻ.

Nó là property của execution trace.

---

# 21. Hướng kiểm chứng 14 — Deadlock detection

Một global state:

\[
S=(\mu,\sigma)
\]

là deadlock nếu:

- process chưa kết thúc;
- không activity nào có thể thực thi.

Có thể formalize:

\[
\boxed{
\neg Final(\mu)
\land
\forall a:
\neg(
Enabled_{BPMN}(a,\mu)
\land
Pre_a(\sigma)
)
}
\]

Điểm quan trọng là deadlock có thể xuất phát từ hai nguồn:

### Control deadlock

BPMN structure không cho transition nào.

### Data deadlock

BPMN có activity enabled về control-flow nhưng tất cả precondition đều false.

Đây là lợi thế quan trọng của việc kết hợp BPMN với OCL.

---

# 22. Hướng kiểm chứng 15 — Unreachable activity

Một BPMN activity \(a\) có thể tồn tại trong model nhưng không bao giờ thực thi được.

Kiểm tra:

\[
\boxed{
\exists S:
Reachable(S)
\land
Enabled(a,S)
}
\]

Nếu UNSAT thì activity unreachable.

Nguyên nhân có thể do:

- control-flow;
- gateway;
- contradictory precondition;
- invariant;
- postcondition của activity trước;
- tổ hợp tất cả các yếu tố trên.

---

# 23. Hướng kiểm chứng 16 — Conformance của execution log thực tế

Đây là một use case rất sát với ý tưởng tool.

Giả sử hệ thống thật ghi log:

```text
t0 CreateOrder
t1 UpdateOrder
t2 ShipOrder
t3 ApproveOrder
```

và có các snapshot tương ứng:

\[
\sigma_0,\sigma_1,\sigma_2,\sigma_3,\sigma_4.
\]

Tool kiểm tra từng transition:

\[
(\mu_i,\sigma_i)
\xrightarrow{a_i}
(\mu_{i+1},\sigma_{i+1})
\]

theo ba lớp:

### Layer A — OCL state constraints

\[
Inv(\sigma_i)
\]

### Layer B — operation contract

\[
Pre_{a_i}(\sigma_i)
\land
Post_{a_i}(\sigma_i,\sigma_{i+1})
\]

### Layer C — BPMN control-flow

\[
Enabled_{BPMN}(a_i,\mu_i)
\land
ControlStep(\mu_i,a_i,\mu_{i+1})
\]

Nếu một trong ba lớp sai, execution trace không conform.

---

# 24. Hướng kiểm chứng 17 — Whole-process correctness

Ta có thể đặt một global safety invariant:

\[
P(\sigma)
\]

và hỏi:

> Trong mọi reachable state của mọi execution hợp lệ, \(P\) có luôn đúng không?

\[
\boxed{
\forall S:
Reachable(S)
\Rightarrow
P(S)
}
\]

Equivalent counterexample search:

\[
\exists S:
Reachable(S)
\land
\neg P(S).
\]

Đây là model checking theo nghĩa mạnh hơn việc validate một trace đầu vào.

---

# 25. Hướng kiểm chứng 18 — Bounded process verification

Nếu state space quá lớn hoặc vô hạn, có thể giới hạn độ dài trace:

\[
k
\]

và hỏi:

\[
S_0
\rightarrow
S_1
\rightarrow
\cdots
\rightarrow
S_k
\]

có tồn tại vi phạm hay không.

Ví dụ:

\[
\exists S_0,\ldots,S_{10}
:
Execution(S_0,\ldots,S_{10})
\land
Bad(S_{10}).
\]

Đây là hướng phù hợp với SMT solving.

---

# 26. Hai phong cách sử dụng tool

## 26.1. Runtime / trace validation

Người dùng cung cấp:

```text
Model
+
BPMN
+
OCL
+
execution trace
```

Tool trả:

```text
VALID
```

hoặc:

```text
INVALID

Violation:
- Step 7
- Activity: ShipOrder
- BPMN control violation
- Expected predecessor: ApproveOrder
```

Đây là hướng gần với OCL evaluator nhất.

---

## 26.2. Static verification / model checking

Người dùng chỉ cung cấp:

```text
Model
+
BPMN
+
OCL
```

Tool tự tìm:

- reachable states;
- counterexample traces;
- deadlocks;
- invariant violations;
- unreachable tasks;
- incompatible activity contracts.

Đây là hướng mạnh hơn nhưng computationally khó hơn.

---

# 27. Kiến trúc verifier có thể chia thành các tầng

## Layer 1 — UML model checker

Kiểm tra:

- class;
- attribute;
- association;
- multiplicity;
- inheritance;
- type system.

---

## Layer 2 — OCL evaluator

Đánh giá:

\[
Eval(e,\sigma)
\]

và:

\[
EvalPost(e,\sigma,\sigma').
\]

---

## Layer 3 — BPMN execution engine

Quản lý:

\[
\mu
\]

và xác định:

\[
Enabled_{BPMN}(a,\mu).
\]

---

## Layer 4 — Combined transition checker

Kiểm tra:

\[
(\mu,\sigma)
\xrightarrow{a}
(\mu',\sigma')
\]

có hợp lệ không.

---

## Layer 5 — Trace verifier

Kiểm tra:

\[
S_0
\xrightarrow{a_1}
S_1
\xrightarrow{a_2}
\cdots
\xrightarrow{a_n}
S_n.
\]

---

## Layer 6 — Model checker / solver

Tìm:

- counterexample;
- reachable bad state;
- deadlock;
- impossible contract;
- incompatible process branch.

---

# 28. Một định nghĩa tổng quát cho validity của execution trace

Cho trace:

\[
\tau=
S_0
\xrightarrow{a_1}
S_1
\xrightarrow{a_2}
\cdots
\xrightarrow{a_n}
S_n
\]

với:

\[
S_i=(\mu_i,\sigma_i).
\]

Ta có thể định nghĩa:

\[
ValidTrace(\tau)
\]

khi:

### Initial state hợp lệ

\[
Initial(\mu_0)
\land
Inv(\sigma_0)
\]

### Mỗi step hợp lệ

Với mọi:

\[
i\in\{0,\ldots,n-1\}
\]

phải có:

\[
Enabled_{BPMN}(a_{i+1},\mu_i)
\]

\[
Pre_{a_{i+1}}(\sigma_i)
\]

\[
Post_{a_{i+1}}(\sigma_i,\sigma_{i+1})
\]

\[
ControlStep(\mu_i,a_{i+1},\mu_{i+1})
\]

\[
Inv(\sigma_{i+1}).
\]

Do đó:

\[
\boxed{
ValidTrace(\tau)
\iff
\text{tất cả các step đều hợp lệ theo BPMN + OCL}
}
\]

---

# 29. Điều tool BPMN + OCL kiểm tra thêm so với OCL state validation

Nếu chỉ xét OCL state:

\[
\sigma_i\models Inv
\]

thì có thể cả ba trạng thái:

\[
\sigma_1,\sigma_2,\sigma_3
\]

đều hợp lệ.

Nhưng sequence:

\[
\sigma_1
\xrightarrow{Ship}
\sigma_2
\xrightarrow{Approve}
\sigma_3
\]

vẫn có thể là một execution **không hợp lệ**.

Tức là:

\[
\boxed{
\forall i:\sigma_i\models Inv
}
\]

không suy ra:

\[
\boxed{
\tau\models Process
}
\]

Đây chính là khoảng trống mà BPMN lấp vào.

---

# 30. Một cách nhìn tổng quát về semantics

Có thể coi UML/OCL định nghĩa:

\[
\Sigma_M
\]

là không gian data states.

BPMN định nghĩa:

\[
Q_B
\]

là không gian process/control states.

Global state space:

\[
\boxed{
S
=
Q_B\times\Sigma_M
}
\]

Các activity tạo ra transition relation:

\[
\boxed{
\rightarrow
\;\subseteq\;
S\times Act\times S
}
\]

Toàn bộ hệ thống trở thành một labeled transition system:

\[
\boxed{
TS=
(S,S_0,Act,\rightarrow)
}
\]

Sau khi có \(TS\), ta có thể thực hiện:

- state validation;
- trace validation;
- reachability checking;
- invariant checking;
- deadlock checking;
- temporal verification;
- conformance checking.

---

# 31. Danh sách proof obligations cốt lõi

## PO-1 — State validity

\[
Inv(\sigma)
\]

---

## PO-2 — Precondition validity

\[
Pre_a(\sigma)
\]

---

## PO-3 — Postcondition validity

\[
Post_a(\sigma,\sigma')
\]

---

## PO-4 — BPMN transition validity

\[
Enabled_{BPMN}(a,\mu)
\]

---

## PO-5 — Combined transition validity

\[
Enabled_{BPMN}(a,\mu)
\land
Pre_a(\sigma)
\land
Post_a(\sigma,\sigma')
\]

---

## PO-6 — Invariant preservation

\[
Inv(\sigma)
\land
Pre_a(\sigma)
\land
Post_a(\sigma,\sigma')
\Rightarrow
Inv(\sigma')
\]

---

## PO-7 — Contract feasibility

\[
Inv(\sigma)
\land
Pre_a(\sigma)
\Rightarrow
\exists\sigma':
Post_a(\sigma,\sigma')
\land
Inv(\sigma')
\]

---

## PO-8 — Sequential compatibility

Nếu:

\[
A\rightarrow B
\]

thì:

\[
Post_A(\sigma,\sigma')
\Rightarrow
Pre_B(\sigma')
\]

có thể là một property cần kiểm tra.

---

## PO-9 — Gateway completeness

\[
guard_1\lor\cdots\lor guard_n
\]

---

## PO-10 — XOR exclusiveness

\[
\forall i\neq j:
\neg(guard_i\land guard_j)
\]

---

## PO-11 — Deadlock freedom

\[
Reachable(S)
\land
\neg Final(S)
\Rightarrow
\exists a,S':
S\xrightarrow{a}S'
\]

---

## PO-12 — Safety

\[
Reachable(S)
\Rightarrow
Safe(S)
\]

---

## PO-13 — Reachability

\[
\exists S:
Reachable(S)
\land
Goal(S)
\]

---

## PO-14 — Process conformance

Với trace đầu vào:

\[
\tau
\]

kiểm tra:

\[
ValidTrace(\tau).
\]

---

# 32. Kết luận

Hướng xây dựng tool này có thể được hiểu như một sự mở rộng tự nhiên từ OCL state evaluation sang process-aware verification.

OCL evaluator truyền thống chủ yếu trả lời:

\[
\boxed{
\text{State này có thỏa constraint không?}
}
\]

Tool UML/OCL + BPMN có thể trả lời thêm:

\[
\boxed{
\text{Transition này có hợp lệ không?}
}
\]

\[
\boxed{
\text{Execution trace này có tuân theo process không?}
}
\]

\[
\boxed{
\text{Mọi state trong trace có thỏa invariant không?}
}
\]

\[
\boxed{
\text{Mỗi activity có thỏa pre/postcondition không?}
}
\]

\[
\boxed{
\text{Có execution nào dẫn tới bad state hoặc deadlock không?}
}
\]

Điểm quan trọng là BPMN không chỉ bổ sung thêm một tập constraint trên data.

Nó bổ sung:

\[
\boxed{
\text{semantics của thứ tự và cấu trúc thực thi}
}
\]

Do đó một execution hợp lệ không chỉ cần:

\[
\sigma_i\models Inv
\]

mà còn phải thỏa:

\[
\boxed{
\text{state correctness}
+
\text{transition correctness}
+
\text{process-flow correctness}
}
\]

Đây là nền tảng phù hợp để xây một verifier kết hợp:

\[
\boxed{
UML
+
OCL
+
BPMN
}
\]

có khả năng vừa **validate execution trace thực tế**, vừa có thể mở rộng sang **symbolic verification/model checking** nếu cần.

---

## Nguồn nền tảng

Phần định nghĩa state/object model trong tài liệu này dựa trên cách formalize của Richters ở Chương 3, đặc biệt:

- Definition 3.9 — Syntax of object models
- Definition 3.10 — Object identifiers
- Definition 3.11 — Links
- Definition 3.12 — System state
- Definition 3.13 — Interpretation of object models

Các phần về BPMN + transition system, trace validation và proof obligations là sự mở rộng/formalization đề xuất cho kiến trúc tool đang thảo luận.
