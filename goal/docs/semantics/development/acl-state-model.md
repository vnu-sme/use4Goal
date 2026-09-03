# ACL state model — bản phát triển 3.0

Tài liệu này mô tả bản thử nghiệm bottom-up trong
[`acl-state.ecore`](../../../model/development/acl-state.ecore). Bản 3.0 tồn tại
cạnh ACL 2.0 để kiểm chứng ngữ nghĩa trước khi migration compiler; nó chưa thay
đổi cú pháp `.acl` hiện hành.

## 1. Điểm xuất phát ở M0

Meeting Scheduler cho thấy bốn loại occurrence thực tế:

| Occurrence M0 | Có identity | Có chủ đích | Có state | Có thể đổi người đảm nhiệm |
|---|---:|---:|---:|---:|
| `alice` | có | có | có | không áp dụng |
| `unit1` | có | có, ở cấp tập thể | có | thành viên có thể đổi |
| `organizer1` | có | có, dưới dạng trách nhiệm vị trí | có | có |
| `meeting1` | có | không | có | không áp dụng |

Ba occurrence đầu cùng có thể làm chủ thể của intentional element nhưng có cách
sử dụng khác nhau. Vì vậy ACL 3.0 dùng một metaclass `Actor` và thuộc tính
`ActorKind`, thay vì ba metaclass ngang hàng `Agent`, `Group`, `Role`:

```text
ActorKind::INDIVIDUAL  alice, một người hoặc software agent cụ thể
ActorKind::COLLECTIVE  unit1, một đơn vị tổ chức có identity và goal chung
ActorKind::ROLE        organizer1, một vị trí mang trách nhiệm theo context
```

Sự phân biệt vẫn còn để validation biết một composition tổ chức hoặc một
compatibility có hợp lệ hay không. Điều bị loại bỏ chỉ là ba cây class trùng lặp.

`Entity` được giữ riêng vì `meeting1` có identity và state nhưng không tự mong
muốn trạng thái nào.

## 2. Các khái niệm M1

### `Classifier`, `DataType` và `Class`

`Classifier` là kiểu dùng để khai báo model. `DataType` chứa value không có
object identity; `Class` có occurrence mang identity.

```text
Classifier
├── DataType
│   ├── PrimitiveType
│   └── Enumeration
└── Class
    ├── Actor
    └── Entity
```

Sự tồn tại của `DataType` tránh biến `Boolean`, `String` hoặc enum literal thành
object. Sự tồn tại của `Class` cung cấp chỗ chung cho Attribute và relationship
endpoint.

### `Actor`

`Actor` tồn tại vì một model cần khai báo kiểu cho mọi occurrence có thể làm
chủ thể intentional. `kind` cho biết cách dùng:

- `INDIVIDUAL`: kiểu của người hoặc software agent;
- `COLLECTIVE`: kiểu của tổ chức, department, team hoặc group;
- `ROLE`: kiểu của vị trí/trách nhiệm theo context.

`Actor` không chứa goal trong ACL. iStar tạo `ActorView` tham chiếu chính Actor
này. Nhờ vậy ACL, iStar và BPMN có thể dùng chung một identity M1 mà không phải
mapping bằng tên.

### `Entity`

`Entity` tồn tại để biểu diễn đối tượng miền thụ động. Entity occurrence có thể
được Actor quan sát hoặc thay đổi, nhưng không làm chủ intentional element.

Ví dụ: `Meeting`, `Calendar`, `MeetingRequest`.

### `Attribute`

Attribute mô tả phần state có giá trị của một Actor hoặc Entity occurrence.
Multiplicity thuộc về declaration M1; giá trị cụ thể nằm trong
`AttributeValue` của từng snapshot M0.

Không lưu giá trị hiện tại ngay trên `Attribute`, vì làm vậy sẽ trộn schema với
runtime state.

### Relationship

Bản phát triển vẫn giữ `Association`, `Aggregation`, `Composition` vì ba loại
này tạo link occurrence có lifecycle khác nhau. `Owner` bị loại bỏ:

- composition `COLLECTIVE → ROLE` khai báo các role slot của một đơn vị;
- composition `COLLECTIVE → COLLECTIVE` khai báo đơn vị con;
- composition `Actor → Entity` khai báo artifact thuộc lifecycle của Actor;
- composition `Entity → Entity` giữ ngữ nghĩa whole–part mạnh thông thường.

Composition chỉ mô tả cấu trúc và lifecycle. Nó không truyền goal. Việc một
collective con đóng góp cho goal của collective cha phải được khai báo bằng
refinement hoặc dependency trong iStar.

`Compatibility` vẫn là concept riêng vì nó là policy cấp type, không phải một
link runtime. Nó chỉ hợp lệ khi scope là `COLLECTIVE` và hai endpoint là `ROLE`.

## 3. Các khái niệm M0

### `SystemHistory`

`SystemHistory` gom các identity runtime ổn định và chuỗi snapshot của cùng một
ACL model. Nó tồn tại để phân biệt:

```text
identity của alice              không đổi
alice.workingHours tại s0, s1   có thể đổi
```

### `RuntimeInstance`

Đây là identity tồn tại qua nhiều checkpoint. Hai subtype làm rõ ranh giới
intentional:

- `ActorOccurrence` phải có `type : Actor`;
- `EntityObject` phải có `type : Entity`.

Một `ROLE` occurrence như `organizer1` tồn tại độc lập với người đang đảm nhiệm
nó. Thay Alice bằng Carol chỉ thay link enactment; không đổi identity của role
slot hay goal gắn với role slot.

### `SystemState`

Một `SystemState` là checkpoint bất biến:

\[
\Sigma_i = (O_i,V_i,L_i)
\]

trong đó:

- `O_i` là các runtime identity đang active;
- `V_i` là các `AttributeValue` tại checkpoint;
- `L_i` là các `LinkOccurrence` tại checkpoint.

State mới được tạo sau một transition. State cũ không bị sửa, vì iStar cần đánh
giá lại và so sánh goal marking theo thời gian.

### `AttributeValue`

`AttributeValue(owner, attribute, value)` gắn một giá trị với một runtime
identity tại đúng một SystemState. Trong một state, một cặp
`(owner, attribute)` có tối đa một value.

### `LinkOccurrence`

`LinkOccurrence(type, source, target)` là instance của một Association,
Aggregation hoặc Composition M1. Cùng một cơ chế biểu diễn:

```text
org1      composition→ unit1
unit1     composition→ organizer1
alice     association→ organizer1
unit1     composition→ meeting1
```

Link giữa `alice` và `organizer1` có thể đổi trong khi hai identity vẫn tồn tại.

## 4. Điều kiện hợp lệ chính

1. Tên Classifier và Relationship duy nhất trong một ACL model.
2. Actor generalization chỉ nối Actor cùng `ActorKind`; Entity chỉ generalize
   Entity.
3. Composition chứa Actor phải bắt đầu từ một `COLLECTIVE` Actor.
4. Entity không composition-contain Actor.
5. Composition type graph không có cycle.
6. ActorOccurrence chỉ được typed bởi Actor; EntityObject chỉ bởi Entity.
7. Runtime instance type phải thuộc đúng ACL model của SystemHistory.
8. Mọi value và link trong state chỉ dùng instance đang active.
9. Mỗi `(instance, attribute)` có nhiều nhất một value trong một state.

## 5. Giới hạn có chủ ý

Bản này chưa quyết định cú pháp text mới và chưa migration compiler. Ecore/XMI
được dùng trước để trả lời ba câu hỏi:

1. Một Actor chung có đủ biểu đạt individual, collective và role occurrence?
2. Chuỗi state có đủ để đánh giá lại iStar khách quan sau mọi transition?
3. Composition có thay Owner mà không làm mất structure và lifecycle?

Chỉ sau khi các ví dụ M0 trả lời được ba câu hỏi này mới nên đóng băng M2 và sửa
parser Java.
