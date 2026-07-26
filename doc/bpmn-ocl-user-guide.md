# Hướng Dẫn Chạy BPMN2 + OCL Cho Người Mới

Tài liệu này hướng dẫn cách chạy project theo use case BPMN2 + OCL đã được kiểm thử end-to-end.

Bạn không cần đọc source code để chạy theo tài liệu này.

## 1. Project Này Dùng Để Làm Gì

Project `use4Goal` chứa USE platform và plugin `goal`.

Trong phạm vi BPMN2 + OCL, project dùng để:

```text
Đọc một mô hình BPMN2 dạng text
Đọc các OCL constraint gắn trong BPMN2
Đọc một USE domain model
Map mỗi BPMN node/flow có OCL sang một USE class
Compile các OCL expression bằng USE OCLCompiler
Báo constraint nào compile được hoặc lỗi compile ở đâu
```

Nói ngắn gọn: pipeline hiện tại kiểm tra rằng các OCL gắn trên BPMN có cú pháp và kiểu hợp lệ theo domain model `.use`.

## 2. Input Của Project

Use case BPMN2 + OCL cần 3 loại input chính:

```text
.bpmn2
.use
.bpmn2oclmap
```

Các file mẫu đã tạo nằm tại:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/
```

## 3. Ý Nghĩa Từng Loại File

### `.bpmn2`

File `.bpmn2` mô tả quy trình BPMN2 bằng DSL text của project.

Ví dụ:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_process_valid.bpmn2
```

File này chứa:

```text
model
pool
start event
task
gateway
end event
sequence flow
OCL block gắn trên node hoặc flow
```

Ví dụ OCL gắn trên task:

```bpmn2
task approve_order "Approve Order" ocl {[
  self.status = #approved and self.approved = true
]}
```

Ý nghĩa: tại bước `approve_order`, điều kiện domain mong muốn là order có status `approved` và flag `approved = true`.

### `.use`

File `.use` là USE domain model.

Ví dụ:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_domain.use
```

File này định nghĩa các class, attribute, enum mà OCL được phép tham chiếu.

Trong use case mẫu, domain model có:

```text
enum OrderStatus {newOrder, reviewed, approved, rejected, closed}

class Order
  created  : Boolean
  reviewed : Boolean
  approved : Boolean
  rejected : Boolean
  priority : Boolean
  amount   : Integer
  status   : OrderStatus
```

Nếu OCL viết `self.status = #approved`, compiler hiểu `self` là một object thuộc class `Order`.

### `.bpmn2oclmap`

File `.bpmn2oclmap` map BPMN element sang USE class.

Ví dụ:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_context.bpmn2oclmap
```

Nội dung có dạng:

```text
context <bpmnElementId> -> <UseClassName>
context <sourceId>::<targetId> -> <UseClassName>
```

Ví dụ:

```text
context approve_order -> Order
context decide_order::approve_order -> Order
```

Ý nghĩa:

- OCL gắn trên BPMN node `approve_order` sẽ được compile với `self : Order`.
- OCL gắn trên sequence flow `decide_order -> approve_order` sẽ được compile với `self : Order`.

Nếu thiếu mapping, hệ thống sẽ báo lỗi:

```text
bpmn ocl '<id>': missing USE context type mapping
```

## 4. Use Case Đã Tạo

Use case mẫu là quy trình xử lý order.

### BPMN Mô Tả Quy Trình Gì

File:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_process_valid.bpmn2
```

Quy trình:

```text
Order received
  -> Review Order
  -> Decide Order
     -> Approve Order
     -> Reject Order
  -> Order Closed
```

Cấu trúc BPMN:

```text
Start Event: order_received
Task:        review_order
Gateway:     decide_order, loại xor
Task:        approve_order
Task:        reject_order
End Event:   order_closed
```

Các flow chính:

```text
order_received -> review_order
review_order -> decide_order
decide_order -> approve_order
decide_order -> reject_order
approve_order -> order_closed
reject_order -> order_closed
```

### USE Model Gồm Những Class Nào

File:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_domain.use
```

Domain model gồm:

```text
OrderStatus
Order
```

`OrderStatus` là enum:

```text
newOrder, reviewed, approved, rejected, closed
```

`Order` là class có các attribute:

```text
created
reviewed
approved
rejected
priority
amount
status
```

### OCL Kiểm Tra Điều Gì

OCL trong file `.bpmn2` là các domain condition tại từng điểm BPMN.

| BPMN element | OCL | Ý nghĩa |
|---|---|---|
| `order_received` | `self.created = true` | Order đã được tạo |
| `review_order` | `self.reviewed = true and self.amount > 0` | Order đã review và amount hợp lệ |
| `decide_order` | `self.reviewed = true` | Chỉ quyết định sau khi đã review |
| `approve_order` | `self.status = #approved and self.approved = true` | Nhánh approve tạo trạng thái approved |
| `reject_order` | `self.status = #rejected and self.rejected = true` | Nhánh reject tạo trạng thái rejected |
| `order_closed` | `self.status = #approved or self.status = #rejected` | Order đóng khi đã approve hoặc reject |
| `decide_order::approve_order` | `self.priority = true` | Flow approve dành cho priority order |
| `decide_order::reject_order` | `self.priority = false` | Flow reject dành cho non-priority order |

### Mapping Hoạt Động Ra Sao

File:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_context.bpmn2oclmap
```

Mapping nói rằng toàn bộ OCL trong BPMN use case này đều được compile với:

```text
self : Order
```

Ví dụ:

```text
context review_order -> Order
```

Khi compiler gặp:

```text
self.reviewed = true and self.amount > 0
```

nó kiểm tra rằng class `Order` có attribute `reviewed` và `amount`, đồng thời expression trả về Boolean hợp lệ.

## 5. Hướng Dẫn Chạy Từng Bước

### Step 1. Mở Terminal Ở Thư Mục Root

Thư mục chạy:

```text
D:\master\use4Goal
```

Kiểm tra đang ở đúng thư mục:

```powershell
pwd
```

Output mong đợi:

```text
D:\master\use4Goal
```

### Step 2. Thiết Lập Classpath

Trong PowerShell, chạy:

```powershell
$cp='goal\target\classes;use\use-core\target\classes;use\use-gui\target\classes;C:\Users\Dao Huy Hung\.m2\repository\org\antlr\antlr4-runtime\4.9.3\antlr4-runtime-4.9.3.jar;C:\Users\Dao Huy Hung\.m2\repository\org\antlr\antlr-runtime\3.4\antlr-runtime-3.4.jar;C:\Users\Dao Huy Hung\.m2\repository\com\google\guava\guava\33.6.0-jre\guava-33.6.0-jre.jar;C:\Users\Dao Huy Hung\.m2\repository\com\google\guava\failureaccess\1.0.3\failureaccess-1.0.3.jar'
```

Output mong đợi:

```text
Không có output.
```

Biến `$cp` dùng để Java tìm:

- class của plugin `goal`
- class của USE core/gui
- ANTLR4 runtime cho BPMN parser
- ANTLR3 runtime cho USE/OCL parser
- Guava dependency của USE core

### Step 3. Chạy Use Case Hợp Lệ

Command:

```powershell
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain . goal\src\main\resources\examples\bpmn_ocl\audit\order_process_valid.bpmn2 goal\src\main\resources\examples\bpmn_ocl\audit\order_domain.use goal\src\main\resources\examples\bpmn_ocl\audit\order_context.bpmn2oclmap
```

File đầu vào:

```text
order_process_valid.bpmn2
order_domain.use
order_context.bpmn2oclmap
```

Output mong đợi:

```text
BPMN OCL validation OK
Compiled constraints: 8
  order_received [node, self : Order]
  review_order [node, self : Order]
  decide_order [node, self : Order]
  approve_order [node, self : Order]
  reject_order [node, self : Order]
  order_closed [node, self : Order]
  decide_order::approve_order [sequenceFlow, self : Order]
  decide_order::reject_order [sequenceFlow, self : Order]
```

### Step 4. Chạy Official Sample Có Sẵn

Command:

```powershell
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain
```

Output mong đợi:

```text
BPMN OCL validation OK
Compiled constraints: 9
  claim_received [node, self : Claim]
  register_claim [node, self : Claim]
  validate_claim [node, self : Claim]
  eligibility_decision [node, self : Claim]
  approve_claim [node, self : Claim]
  reject_claim [node, self : Claim]
  claim_closed [node, self : Claim]
  eligibility_decision::approve_claim [sequenceFlow, self : Claim]
  eligibility_decision::reject_claim [sequenceFlow, self : Claim]
```

Official sample dùng các file:

```text
goal/src/main/resources/examples/bpmn_ocl/claim_handling_ocl.bpmn2
goal/src/main/resources/examples/bpmn_ocl/claim_handling.use
goal/src/main/resources/examples/bpmn_ocl/claim_handling.bpmn2oclmap
```

### Step 5. Chạy Một Case Sai OCL

Command:

```powershell
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain . goal\src\main\resources\examples\bpmn_ocl\audit\invalid_ocl_missing_property.bpmn2 goal\src\main\resources\examples\bpmn_ocl\audit\order_domain.use goal\src\main\resources\examples\bpmn_ocl\audit\simple_context.bpmn2oclmap
```

Output mong đợi:

```text
bpmn ocl 'order_received' (self : Order): ... Undefined operation named `nonExistingFlag' ...
```

Ý nghĩa:

OCL có dùng:

```text
self.nonExistingFlag = true
```

Nhưng class `Order` trong `.use` không có attribute `nonExistingFlag`, nên compiler báo lỗi đúng.

### Step 6. Chạy Một Case Thiếu Context Mapping

Command:

```powershell
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain . goal\src\main\resources\examples\bpmn_ocl\audit\order_process_valid.bpmn2 goal\src\main\resources\examples\bpmn_ocl\audit\order_domain.use goal\src\main\resources\examples\bpmn_ocl\audit\invalid_missing_context.bpmn2oclmap
```

Output mong đợi:

```text
bpmn ocl 'review_order': missing USE context type mapping
bpmn ocl 'decide_order::approve_order': missing USE context type mapping
bpmn ocl 'decide_order::reject_order': missing USE context type mapping
```

Ý nghĩa:

Các BPMN element/flow này có OCL, nhưng file `.bpmn2oclmap` không nói `self` thuộc USE class nào.

## 6. Kết Quả Mong Đợi

Nếu chạy thành công use case hợp lệ, hệ thống in:

```text
BPMN OCL validation OK
Compiled constraints: 8
```

Không có output file được sinh ra.

Không có object runtime được tạo.

Không có system state được validate.

Output chỉ là text report trên console.

## 7. Giải Thích Vì Sao Output Đó Là Đúng

Dòng:

```text
BPMN OCL validation OK
```

có nghĩa là:

```text
BPMN file parse được
USE model parse được
context map parse được
tất cả OCL block trong BPMN compile được bằng USE OCLCompiler
```

Dòng:

```text
Compiled constraints: 8
```

có nghĩa là hệ thống tìm thấy và compile thành công 8 OCL expression:

- 6 expression gắn trên BPMN node
- 2 expression gắn trên BPMN sequence flow

Dòng:

```text
review_order [node, self : Order]
```

có nghĩa là:

- OCL thuộc BPMN node `review_order`
- expression được compile với biến `self`
- type của `self` là class `Order` trong USE model

Dòng:

```text
decide_order::approve_order [sequenceFlow, self : Order]
```

có nghĩa là:

- OCL thuộc sequence flow từ `decide_order` tới `approve_order`
- expression được compile với `self : Order`

Điều hệ thống vừa làm:

```text
Parse BPMN
Build BPMN runtime model
Parse USE domain model
Read mapping
Compile OCL expressions
Report compiled expressions
```

Điều hệ thống chưa làm:

```text
Chưa tạo Order object
Chưa chạy process
Chưa evaluate OCL là true/false
Chưa kiểm trạng thái before/after
Chưa chứng minh goal achievement
```

## 8. Checklist Test

| Input | Expected behavior | Expected output | Pass/Fail criteria |
|---|---|---|---|
| `order_process_valid.bpmn2` + `order_domain.use` + `order_context.bpmn2oclmap` | BPMN parse, USE parse, map parse, 8 OCL compile | `BPMN OCL validation OK`, `Compiled constraints: 8` | Pass nếu exit code 0 và đủ 8 constraint |
| Official claim sample | Compile 9 OCL constraints | `Compiled constraints: 9` | Pass nếu exit code 0 và đủ 9 constraint |
| `invalid_ocl_missing_property.bpmn2` | OCL semantic/type error | Undefined `nonExistingFlag` | Pass nếu exit code 1 và báo đúng property không tồn tại |
| `invalid_ocl_syntax.bpmn2` | OCL syntax error | `no viable alternative at input '<EOF>'` | Pass nếu exit code 1 |
| Valid BPMN + `invalid_missing_context.bpmn2oclmap` | Missing context mapping | `missing USE context type mapping` | Pass nếu exit code 1 và nêu id bị thiếu |
| Valid BPMN + `invalid_unknown_class.bpmn2oclmap` | Unknown USE class | `no .use class named 'UnknownOrder'` | Pass nếu exit code 1 |
| Valid BPMN + `invalid_context_syntax.bpmn2oclmap` | Context map syntax error | `cannot parse BPMN OCL context line` | Pass nếu exit code 1 |
| `invalid_missing_start.bpmn2` | Robust validator nên reject | Hiện tại lại `validation OK` | Đây là known limitation/Fail về BPMN semantic validation |
| `invalid_missing_end.bpmn2` | Robust validator nên reject | Hiện tại lại `validation OK` | Đây là known limitation/Fail về BPMN semantic validation |
| `invalid_duplicate_id.bpmn2` | Robust validator nên reject duplicate id | Hiện tại accepted | Đây là known limitation/Fail |
| `invalid_bad_sequence.bpmn2` | Robust validator nên báo lỗi sạch | Hiện tại uncaught exception | Fail về error handling |
| `edge_nested_subprocess.bpmn2` | Compile OCL trong subprocess | `Compiled constraints: 9` | Pass nếu exit code 0 và thấy nested constraints |
| `edge_contradictory_ocl.bpmn2` | Compile được nhưng không evaluate | `validation OK`, `Compiled constraints: 2` | Pass nếu hiểu đây chỉ là compile-only |

## 9. Những Điều KHÔNG Nên Kỳ Vọng

Không nên kỳ vọng hệ thống hiện tại làm các việc sau trong pipeline BPMN2 OCL này:

### Chưa Evaluate OCL

Hệ thống compile OCL, nhưng không chạy OCL trên object/state cụ thể.

Ví dụ expression mâu thuẫn:

```text
self.created = true and self.created = false
```

vẫn compile được vì nó đúng cú pháp và đúng type.

### Chưa Kiểm Semantic BPMN Đầy Đủ

Các case sau hiện vẫn có thể được chấp nhận:

```text
process thiếu StartEvent
process thiếu EndEvent
process rỗng
process chỉ có Task
duplicate flow element id
gateway không đủ outgoing flow
```

### Chưa Validate Runtime State

Hệ thống chưa tạo object `Order`, chưa set attribute, chưa kiểm OCL trên state thật.

### Chưa Chạy BPMN Process

Hệ thống không mô phỏng token, trace, execution path, hoặc process instance.

### Chưa Chứng Minh Goal Achievement

Pipeline hiện tại chưa chứng minh:

```text
post(operation) implies goal.satisfactionCondition
```

Nó chỉ cung cấp bước nền: BPMN node/flow có OCL hợp lệ để một tầng checker khác có thể dùng sau này.

### Không Có Output File

Kết quả hiện tại chỉ in ra console.

Không có file report, không có model transformed được ghi ra disk.

## 10. Ghi Chú Về Maven

Tài liệu cũ có command Maven:

```powershell
mvn -pl goal "-Dexec.mainClass=org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain" "-Dexec.classpathScope=compile" org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

Trong audit thực tế, command này không chạy được trong môi trường hiện tại do lỗi parent POM resolution.

Vì vậy tài liệu này dùng `java -cp ...` để chạy trực tiếp từ các class đã build trong `target/classes`.

Nếu Maven build được sửa ổn định sau này, có thể thay phần Step 2 và Step 3 bằng Maven command chính thức.
