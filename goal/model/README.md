# Canonical Ecore metamodels

Thư mục này chứa ba metamodel động dùng để thiết kế và Validate trực tiếp
trong Eclipse Modeling Tools:

| Ngôn ngữ | Ecore | GenModel | Sirius session | Ví dụ đầy đủ |
|---|---|---|---|---|
| ACL | `acl.ecore` | `acl.genmodel` | `acl.aird` | `mtg.acl.xmi` |
| iStar | `istar.ecore` | `istar.genmodel` | `istar.aird` | `mtg.istar.xmi` |
| BPMN | `bpmn.ecore` | `bpmn.genmodel` | `bpmn.aird` | `mtg.bpmn.xmi` |

Các `.ecore` là đặc tả cấu trúc chuẩn. Java trong compiler hiện vẫn là
metamodel viết tay; không được thay nó bằng Java sinh từ `.genmodel` cho đến
khi parser và toàn bộ consumer được migration trong cùng một thay đổi.

## Mở class diagram trong Eclipse

Import thư mục `goal/model` bằng **Existing Projects into Workspace**, không
copy project vào workspace. Sau đó:

1. chuyển sang **Design perspective**;
2. mở **Window → Show View → Sirius → Model Explorer**;
3. mở rộng file `.aird` tương ứng;
4. double-click representation:
   - `AclMetamodel`,
   - `IstarMetamodel`, hoặc
   - `BpmnMetamodel`.

`istar.aird` đã lưu toàn bộ layout. `acl.aird` và `bpmn.aird` chứa
representation trỏ đúng tới EPackage tương ứng; ở lần mở đầu tiên, nếu canvas
chưa có node, chọn **Refresh** trên diagram để EcoreTools materialize toàn bộ
EClass/EReference rồi sắp xếp và lưu lại. Không dùng palette **Class** chỉ để
khởi tạo, vì nó tạo một EClass thật trong `.ecore`.

Thay đổi classifier, attribute, reference và multiplicity được lưu vào
`.ecore`; vị trí hình được lưu vào `.aird`.

## Mở và Validate dynamic instance

Right-click một file `.xmi`, chọn:

```text
Open With → Sample Ecore Model Editor
```

Chọn object root rồi dùng **Validate**. Validate trực tiếp `.ecore` chỉ kiểm
tra Ecore có well-formed hay không; nó không chạy invariant của ngôn ngữ trên
một model M1. Muốn chạy invariant ACL/iStar/BPMN phải Validate root của dynamic
instance.

Kết quả mong đợi:

| File | Kết quả |
|---|---|
| `acl-valid.xmi` | hợp lệ |
| `acl-invalid-owner.xmi` | lỗi `ValidTarget` vì Owner trỏ tới Entity |
| `mtg.acl.xmi` | hợp lệ |
| `istar-valid.xmi` | hợp lệ |
| `istar-invalid-dependency.xmi` | lỗi `DistinctActors` |
| `mtg.istar.xmi` | hợp lệ |
| `bpmn-valid.xmi` | hợp lệ |
| `bpmn-invalid-gateway.xmi` | lỗi `SupportedGatewayKind` vì OR chưa được hỗ trợ ở version 1 |
| `mtg.bpmn.xmi` | hợp lệ |

## ACL Ecore

`acl.ecore` có namespace:

```text
https://vnu.edu.vn/sme/goal/acl/2.0
```

Nó chứa 19 EClass và một EEnum. Những quyết định ngữ nghĩa quan trọng:

- `Classifier` tách thành `DataType` và `Class`;
- `PrimitiveType` và `Enumeration` là value type;
- `Entity`, `Group`, `Role` là ba concrete Class;
- `Attribute.type` tới `Classifier`, đúng với đặc tả hình thức và class
  metamodel: Property có thể mang value type hoặc một tham chiếu đối tượng
  một chiều;
- Association/Aggregation/Composition có đúng hai `MemberEnd` có
  multiplicity;
- Entity và Group dùng `Generalization` thông thường;
- Role dùng `RoleSpecialization`, không nhận Attribute từ Role cha;
- `Owner` chỉ đi từ Group tới Role hoặc Group, tuyệt đối không tới Entity;
- `Compatibility` có Group scope và hai Role endpoint.

OCL invariant kiểm tra tên duy nhất, enum literal, multiplicity, loại endpoint,
generalization/role/owner/composition cycle, single parent/owner, scope đơn điệu
của Role specialization và scope của Compatibility.

Java metamodel viết tay hiện chỉ nhận `AclDataType` cho Attribute; đó là
implementation subset của parser hiện tại. Ecore không làm hẹp ngữ nghĩa chuẩn
theo giới hạn này. Model dùng Class-typed Attribute cần được hạ thành
Relationship hoặc bổ sung hỗ trợ parser trước khi đưa qua compiler Java.

`mtg.acl.xmi` là biểu diễn động của
`goal/src/main/resources/examples/mtg/mtg.acl`, gồm các primitive type,
`TimetableChannel`, năm Role, `MeetingUnit`, Role specialization, Owner,
Compatibility và Association `knowsPhoneOf`.

## iStar Ecore

`istar.ecore` có namespace:

```text
https://vnu.edu.vn/sme/goal/istar/2.0
```

Nó loại bỏ `Obstacle`, `ObstacleType`, `Obstruction` và `Resolution`.
`Dependency.dependum` composition-own đúng một IntentionalElement cụ thể nên
không có `DependumKind`. Goal sở hữu `activation`/`condition`; Task sở hữu
`precondition`/`postcondition`.

Các invariant kiểm tra unique actor/element, exclusive Actor/dependum
ownership, OCL body, endpoint scope, single refinement parent/kind, refinement
cycle, GoalType compatibility, activation inheritance, actor association và
Dependency.

## BPMN Ecore

`bpmn.ecore` có namespace:

```text
https://vnu.edu.vn/sme/goal/bpmn/2.0
```

Nó chứa 17 EClass và ba EEnum. Những quyết định ngữ nghĩa quan trọng:

- `BpmnModel` owns Process, Message và MessageFlow;
- một Process tương ứng một pool và có thể bind `groupClass` tới ACL Group;
- Process owns Lane, FlowElement và SequenceFlow;
- Lane chỉ partition FlowElement và lưu `roleName` của ACL Role thực hiện;
- FlowElement tách thành Activity, Gateway và Event;
- Activity tách thành Task, CallActivity và SubProcess;
- Event tách thành StartEvent, EndEvent và IntermediateEvent;
- `precondition`, `postcondition` và flow `guard` là containment tới
  `BpmnOclConstraint`;
- SequenceFlow tham chiếu source/target, có guard hoặc default;
- version 1 chỉ thực thi XOR và AND; OR, EVENT_BASED và MessageFlow được biểu
  diễn trong metamodel nhưng bị invariant báo unsupported.

OCL invariant kiểm tra identifier, đúng một Start, ít nhất một End, lane
partition, endpoint scope, reachability, Start/End boundary, gateway kind,
default/guard, OCL body và giới hạn MessageFlow hiện tại.

`mtg.bpmn.xmi` là biểu diễn động đầy đủ của
`goal/src/main/resources/examples/mtg/mtg.bpmn2`: bốn Lane dùng lại bốn ACL
Role, 11 FlowElement, 11 SequenceFlow và toàn bộ OCL pre/post/guard.

## Semantic validation delegate

Cả ba EPackage khai Classic Eclipse OCL validation delegate:

```text
http://www.eclipse.org/emf/2002/Ecore/OCL/LPG
```

Mỗi constrained EClass có:

- annotation `http://www.eclipse.org/emf/2002/Ecore` liệt kê tên invariant;
- annotation `http://www.eclipse.org/emf/2002/Ecore/OCL/LPG` chứa OCL body.

Multiplicity và concrete EType được Ecore kiểm tra trước; các quy tắc toàn mô
hình được OCL delegate kiểm tra sau.
