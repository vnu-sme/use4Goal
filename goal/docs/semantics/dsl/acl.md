# Ngữ nghĩa ACL

Tài liệu này là đặc tả ngữ nghĩa chuẩn của ACL. Các từ **phải**, **không
được** và **chỉ** biểu thị ràng buộc bắt buộc, không phải gợi ý hiện thực.

ACL mô tả schema của một trạng thái tổ chức và miền ứng dụng. ACL không mô tả
execution và không tự tạo instance.

## 1. Không gian khái niệm

Metamodel ACL có hai nhánh classifier tách biệt:

```text
Classifier (abstract)
├── Datatype (abstract)
│   ├── PrimitiveType
│   └── Enumeration
└── Class
    ├── Entity
    ├── Group
    └── Role
```

Các quan hệ tạo thành một phân cấp trong đó Association là khái niệm cha:

```text
Association
├── Aggregation
└── Composition
```

Mỗi Association có đúng hai `MemberEnd`. Mỗi MemberEnd có:

- một `target` là Class;
- một tên navigation `role`;
- một multiplicity.

`Classifier` và `Datatype` là khái niệm trừu tượng; không có declaration trực
tiếp cho chúng trong ACL. ACL không có metaclass hoặc quan hệ `Owner` độc lập;
ngữ nghĩa sở hữu được biểu diễn bằng Composition.

## 2. Datatype

### 2.1 PrimitiveType

PrimitiveType là kiểu giá trị nguyên tử do ngôn ngữ cung cấp, ví dụ `Boolean`,
`Integer`, `Real` và `String`. Primitive value không có identity kiểu object
và không có lifecycle độc lập.

### 2.2 Enumeration

Enumeration là Datatype có tập literal hữu hạn, đóng và có thứ tự khai báo.

```acl
enum Priority { LOW, NORMAL, HIGH }
```

Các ràng buộc:

1. Enum phải có ít nhất một literal.
2. Tên literal phải duy nhất trong Enum.
3. Một giá trị Enum chỉ có thể là một literal đã khai báo.
4. Enum không phải Entity, Group hay Role; không có object identity hoặc
   MemberEnd như một Class occurrence.
5. Khi dịch sang USE, Enum phải giữ nguyên thành USE `enum`.

Thiết kế sai: chuyển Enum thành class và tạo object cho các literal. Cách đó
làm mất ngữ nghĩa value type và tập giá trị đóng.

## 3. Class và Property

Entity, Group và Role là ba loại Class khác nhau. Chúng cùng có thể khai
Property nhưng không vì vậy mà có cùng ngữ nghĩa.

Một Property có tên, type, modifier và có thể có default value. Các ràng buộc:

1. Tên Property phải duy nhất trong Class sau khi xét kế thừa hợp lệ.
2. Type phải tham chiếu một kiểu đã khai báo hoặc PrimitiveType có sẵn.
3. Default value phải tương thích với type.
4. Property scalar mặc định có multiplicity `[1]`: mọi Class occurrence đang
   tồn tại phải có đúng một giá trị Property trong mọi state hợp lệ.
5. Modifier `optional` đổi multiplicity thành `[0..1]`, vì vậy Property có thể
   chưa xác định nhưng không bao giờ có nhiều hơn một giá trị. `required` chỉ
   là cách viết tường minh/tương thích ngược của mặc định `[1]`; không được
   khai đồng thời `required` và `optional`.
6. Property không `mutable` không được đổi giá trị sau khi được khởi tạo.
7. Property của Role occurrence thuộc chính occurrence đó; nó không tự động
   trở thành Property của Agent.

## 4. Entity

Entity biểu diễn đối tượng miền có identity, trạng thái và quan hệ riêng.

Entity có thể:

- khai Property;
- tham gia Association, Aggregation hoặc Composition;
- specialize đúng một Entity cha.

`ChildEntity extends ParentEntity` là generalization thông thường:

- mọi instance ChildEntity cũng là instance ParentEntity;
- ChildEntity nhận Property của ParentEntity;
- navigation áp dụng cho ParentEntity cũng áp dụng cho ChildEntity;
- chuỗi generalization phải không chu trình.

Entity không được:

- specialize Group hoặc Role;
- xuất hiện như member trong thân Group;
- xuất hiện như member được sở hữu trong thân Group.

## 5. Group

Group biểu diễn context tổ chức có identity và lifecycle riêng. Thân Group
chỉ được chứa:

- Property của Group;
- Role member;
- Group con đã khai báo;
- declaration `compatible` có scope là Group đó.

```acl
role Leader;
role Member;
group SubTeam { }

group Team {
  code : String;
  Member [1..*];
  SubTeam [0..*];
  Leader compatible Member;
}
```

Role member và Group con tạo Composition từ Group chứa tới member; chúng không
phải Property hoặc Association miền không sở hữu.

`ChildGroup extends ParentGroup` là generalization thông thường. Group con
nhận Property của Group cha, nhưng generalization không đồng nghĩa với
Composition containment:

- `Department extends Unit` nghĩa là Department là một Unit;
- `Company { Department [*]; }` nghĩa là Company chứa các Department bằng
  Composition;
- hai mệnh đề này độc lập.

Group không được:

- chứa Entity member;
- specialize Entity hoặc Role;
- dùng generalization thay cho containment;
- suy diễn một quan hệ tới Entity khi không có Association declaration tường
  minh.

## 6. Role

Role biểu diễn vị trí mà Agent có thể play. Mỗi lần tham gia là một Role
occurrence riêng và giữ Property riêng.

### 6.1 Role gốc

Role không specialize Role nào là Role gốc. Một Role occurrence gốc phải được
đúng một Agent play trực tiếp.

### 6.2 Role specialization

`ChildRole extends ParentRole` có ngữ nghĩa **play specialization**, không
phải generalization kiểu OO:

1. ChildRole và ParentRole vẫn là hai Class độc lập.
2. ChildRole không nhận hoặc sao chép Property của ParentRole.
3. Một ChildRole occurrence phải nối tới đúng một ParentRole occurrence.
4. Muốn play ChildRole, Agent phải play ParentRole trước.
5. Agent của ChildRole là Agent ở cuối chuỗi ParentRole.
6. Quan hệ Role cha phải đơn và chuỗi Role cha phải không chu trình.
7. Property của Role cha chỉ được đọc bằng navigation qua chuỗi play.

Ví dụ:

```acl
role Person { name : String; }
role Employee extends Person { employeeId : String; }
role Manager extends Employee { level : Integer; }
```

Một Manager occurrence phải nối tới một Employee occurrence; Employee đó phải
nối tới một Person occurrence; Person đó được một Agent play. `Manager` không
có trực tiếp `name` hoặc `employeeId`.

### 6.3 Tính đơn điệu của Group scope

Role specialization và Group Composition là hai quan hệ khác nhau, nhưng nếu
Role cha và Role con đều thuộc một Group qua Composition thì hai cây phải
tương thích.

Đặt `ownerRole(r)` là phép chiếu suy dẫn trả về Group ở đầu whole của
Composition chứa Role `r`, và `AncestorsOrSelf(g)` là Group `g` cùng mọi Group
tổ tiên theo Composition Group–Group. `ownerRole` chỉ là ký hiệu suy dẫn,
không phải một quan hệ trong metamodel. Với mọi Role tổ tiên `p` của Role `c`:

\[
ownerRole(p)\ne\bot
\Rightarrow
ownerRole(c)\ne\bot\land
ownerRole(p)\in AncestorsOrSelf(ownerRole(c)).
\]

Role cha không thuộc Composition của Group nào thì không hạn chế scope Role
con. Nếu Role cha thuộc một Group, Role con phải thuộc cùng Group hoặc một
Group con/cháu. Hai Group ở hai nhánh không liên quan tạo một Role parent sai.
Role cha có Group chứa nhưng Role con không có Group chứa cũng sai vì scope của
con bị mở rộng.

Phải xét toàn bộ bao đóng Role cha, kể cả khi Role trung gian không thuộc
Composition của Group nào.
Ở mức instance, hai đường Role-play và Group-Composition phải dẫn tới **cùng
Group occurrence**, không chỉ cùng Group type.

## 7. Generalization

Generalization có một đầu `specific` và một đầu `general`.

Ràng buộc well-formedness:

1. Specific và general phải tồn tại và khác nhau.
2. Entity chỉ generalize Entity; Group chỉ generalize Group.
3. Mỗi Entity hoặc Group có nhiều nhất một general trực tiếp trong ACL hiện
   tại.
4. Đồ thị generalization phải không chu trình.
5. Không dùng Generalization cho Role; Role specialization dùng play-chain ở
   Mục 6.
6. Enumeration và PrimitiveType không tham gia Generalization của ACL.

## 8. Association và MemberEnd

Mỗi Association tường minh có đúng hai MemberEnd. Với mỗi MemberEnd:

1. Target Class phải tồn tại.
2. Tên role/navigation phải không rỗng và không gây nhập nhằng tại Class
   đối diện.
3. Multiplicity phải có dạng `[n]`, `[l..u]` hoặc `[*]`.
4. `0 <= l <= u`; `*` chỉ được dùng làm upper bound vô hạn.
5. Trong một state, số link nhìn từ một object ở đầu đối diện phải nằm trong
   multiplicity của MemberEnd.

Tên Association phải duy nhất trong schema. Hai Association có thể nối cùng
một cặp Class nếu chúng có tên khác nhau.

### 8.1 Association

Association biểu diễn liên kết không sở hữu và không ràng buộc lifecycle. Nó
có thể nối các Class theo endpoint được khai báo, miễn là thỏa WF-6.

Association Role–Role không hợp lệ. Nếu hai endpoint đều thuộc
`Role ∪ Group`, WF-6 yêu cầu association phải là Composition nhị phân với đầu
thứ nhất là Group và đầu thứ hai là Role hoặc Group.

### 8.2 Aggregation

Aggregation biểu diễn quan hệ whole–part yếu: part có thể tồn tại độc lập với
whole. Aggregation không tạo sở hữu mạnh và không có cascade lifecycle như
Composition.

Theo ACL hiện tại, Aggregation phải có ít nhất một endpoint Entity.
Aggregation Role–Role hoặc Group–Group thuần túy là không hợp lệ.

### 8.3 Composition

Composition biểu diễn whole–part mạnh:

1. Part có nhiều nhất một composite owner tại một thời điểm.
2. Link composition quyết định containment/lifecycle của part.
3. Đồ thị composition phải không chu trình.
4. Entity không được làm whole để sở hữu một Role hoặc Group.
5. Khai báo member Group–Role/Group–Group là cú pháp rút gọn và được chuẩn hóa
   thành Composition nhị phân có Group ở đầu whole.

Quan hệ Group–Entity chỉ tồn tại khi có một Association declaration tường
minh. Ví dụ hợp lệ:

```acl
composition meetingAgenda {
  Meeting [1] role meeting;
  Agenda [1] role agenda;
}
```

Thiết kế sai: đặt `Agenda [1];` trong thân `Meeting` rồi ngầm hiểu nó là
Composition.

## 9. Composition của Group

Không có metaclass `Owner`. Một khai báo member trong thân Group là cú pháp
rút gọn của đúng một Composition từ Group tới Role hoặc Group đó. Ví dụ
`Classroom { Teacher [1]; }` tạo association chuẩn
`Classroom_contains_Teacher` với source `Classroom`, target `Teacher` và
multiplicity target `[1]`. Link instance của membership nằm trong
`sigma_Assoc(Classroom_contains_Teacher)`.

API Java `owners()` chỉ là projection deprecated từ các Composition để module
dịch cũ tiếp tục biên dịch; nó không phải một phần của ACL metamodel chuẩn.

## 10. Role object và `sigma_Play`

Không có classifier hay object `Agent`. Mỗi Role `r` có miền định danh riêng
`oid(r)`, và một Role con instance không đồng thời là Role cha instance.

Với mỗi cạnh kế thừa trực tiếp từ Role con `r'` tới Role cha `r`, state chứa:

\[
sigma_{Play}(r,r'):
sigma_{Class}(r)\to\mathcal{P}(sigma_{Class}(r')).
\]

Mỗi Role con instance phải có đúng một Role cha instance ở phía ngược của
link. AOL v2 viết link này dưới dạng `play parentId -> childId`.

## 11. Thuộc tính Role kế thừa

Role object chỉ lưu attribute khai báo trực tiếp tại Role type của nó. Truy
cập property được khai báo tại Role cha phải đi qua Role cha instance thực sự
trong `sigma_Play`. Surface expression `self.p` có thể được evaluator hạ thành
`self.playOf.p`; không được sao chép `p` vào object Role con.

## 12. Compatibility

Compatibility là constraint giữa hai Role type khác nhau trong một Group
scope. Nó không phải Association và không tạo link occurrence.

`R1 compatible R2` là quan hệ mức type và được dịch thành OCL constraint trên
Role objects/play-links tương ứng. Ràng buộc:

1. Hai endpoint phải là Role đã khai báo và phải khác nhau.
2. Compatibility đối xứng; khai báo `R1 compatible R2` bao phủ cả hai chiều.
3. Declaration phải nằm trong một Group scope hợp lệ cho cả hai Role.
4. Không được khai trùng cùng một cặp trong cùng scope.
5. Compatibility không tạo object hoặc link runtime.
6. Compatibility không tạo play-chain hoặc quan hệ giữa Group.
7. Compatibility không được suy ra chỉ từ Role specialization.

Đặt:

\[
Forbidden(G)=DifferentRolePairs(G)\setminus CompatiblePairs(G).
\]

Translator sinh `NoConflict` cho mỗi cặp trong `Forbidden(G)`. Không sinh một
invariant “cho phép” riêng cho cặp compatible, vì OCL chỉ thu hẹp tập state;
việc cho phép được biểu diễn bằng cách bỏ đúng invariant cấm tương ứng.

## 13. Trạng thái và tính hợp lệ

Một state ACL gồm:

- `sigma_Class`: các object Entity, Role và Group đang tồn tại;
- `sigma_Att`: giá trị attribute của các object đó;
- `sigma_Assoc`: các link của Association, Aggregation và Composition;
- `sigma_Play`: các link từ Role cha instance tới Role con instance trực tiếp.

Không có Agent object và không có thành phần Owner trong state. Khai báo member
trong Group là cú pháp rút gọn của một Composition `Group -> Role/Group`, nên
instance membership được lưu trong `sigma_Assoc`.

State hợp lệ khi đồng thời thỏa:

1. mọi occurrence và link được định kiểu đúng;
2. mọi Property value đúng type, required/default/mutability;
3. mọi Association end thỏa multiplicity;
4. generalization Entity/Group hợp lệ;
5. mỗi Role con instance có đúng một Role cha instance cho từng cạnh kế thừa
   trực tiếp theo `sigma_Play`;
6. mọi Composition link tuân theo các ràng buộc Group–Role/Group;
7. mọi link chỉ nối các endpoint được declaration cho phép;
8. `NoSelfConflict` và mọi `NoConflict` còn hiệu lực đều đúng.

## 14. Những thiết kế sai ngữ nghĩa

Không được thiết kế hoặc hiện thực ACL theo các cách sau:

- coi Enumeration là Class hoặc Entity;
- cho phép declaration trực tiếp `Classifier` hoặc `Datatype`;
- cho Entity xuất hiện như member trong Group;
- tạo Owner Group–Entity;
- dùng Group generalization để biểu diễn containment;
- dùng Owner để biểu diễn generalization;
- dịch Role specialization thành class generalization;
- sao chép Property Role cha vào Role con hoặc Agent;
- cho phép Role con thuộc Group không liên quan với Group chứa Role cha;
- chỉ kiểm tra cạnh Role cha trực tiếp mà bỏ qua tổ tiên Role nhiều cấp;
- nối Agent trực tiếp tới mọi Role thay vì chỉ Role gốc;
- quy Association Role–Role về Agent;
- dùng Aggregation như Composition hoặc áp cascade lifecycle cho Aggregation;
- cho part có nhiều composite owner hoặc tạo composition cycle;
- coi `compatible` là một link runtime;
- mặc định mọi Role đều compatible;
- dùng `compatible` để cho phép hai occurrence cùng Role type;
- tự sinh quan hệ Group–Entity khi schema không khai Association tương ứng.

## 15. Biên tương thích implementation

Compiler ACL chuẩn từ chối ở syntax/semantic boundary: `abstract role`, Entity
member trong Group, `subgroup` inline, compatibility top-level, `intra`/`inter`,
arrow/options compatibility, `relationship` và `partOf` alias.

MM chuẩn không còn lưu trạng thái abstract Role hay Entity membership. Một số
accessor deprecated tạm thời còn tồn tại để các module downstream cũ vẫn
compile, nhưng luôn trả về giá trị canonical (`false` hoặc danh sách rỗng).
Chúng không được dùng để diễn giải ACL.

Các module AOL/SOIL và artefact scenario được tạo theo cơ chế Agent-profile cũ
cần được migrate riêng. Lỗi của các module đó không làm thay đổi ngữ nghĩa ACL
chuẩn ở tài liệu này.

Định nghĩa tập hợp tương ứng nằm tại [formal/acl.md](../../formal/acl.md); thứ
tự dịch sang USE/OCL nằm tại
[`ACL_TO_USE_OCL_CASES.md`](../../../src/main/resources/examples/mtg/ACL_TO_USE_OCL_CASES.md).

## 16. OCL nhúng trong ACL và đánh giá state trực tiếp

ACL cho phép đặt invariant OCL ngay trong đặc tả cấu trúc:

```acl
context Student inv MarkedStudentMustBePresent:
  self.attendanceMarked = true implies self.present = true;
```

`context` phải là một Entity, Role hoặc Group đã khai báo trong cùng model.
Tên invariant phải duy nhất trong context đó. Biểu thức sau dấu `:` là một
biểu thức Boolean trên trạng thái và `self` lần lượt được gắn với từng
occurrence của context. Với Entity và Group, context bao gồm object của subtype
theo domain inclusion. Với Role, context chỉ gồm object trong đúng `oid(r)`;
Role con là một object khác và được nối qua `sigma_Play`. Invariant đúng trên
một state khi biểu thức đúng với mọi occurrence của context; nếu context không
có occurrence thì invariant đúng theo lượng từ phổ quát trên tập rỗng.

Đường đánh giá chuẩn của chức năng **ACL State Evaluator** là:

```text
ACL structure + embedded OCL + formal AOL v2 snapshot
                    ↓
       typed ACL system state
                    ↓
       native ACL/OCL evaluation
```

Đường này không sinh UML/USE class model, không chuyển state thành SOIL và
không dùng `MSystemState`. Phân loại Entity/Role/Group của ACL được giữ nguyên
trong state evaluator. Khả năng dịch ACL sang USE/OCL vẫn có thể tồn tại như
một chức năng tích hợp riêng, nhưng không phải ngữ nghĩa thực thi của bộ đánh
giá state ACL.

AOL v2 dùng `role R as r1` để tạo object thuộc `sigma_Class(R)`,
`play parent -> child` để thêm một cặp vào `sigma_Play`, và `link A : x -> y`
để thêm một link vào `sigma_Assoc(A)`. Cú pháp AOL v1 dựa trên `agent`/`by`
chỉ còn dành cho module legacy và bị ACL State Evaluator từ chối.
