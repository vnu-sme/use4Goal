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

Và một nhánh quan hệ:

```text
Relationship (abstract)
├── Association
├── Aggregation
├── Composition
└── Owner
```

Mỗi Relationship có đúng hai `MemberEnd`. Mỗi MemberEnd có:

- một `target` là Classifier;
- một tên navigation `role`;
- một multiplicity.

`Classifier`, `Datatype` và `Relationship` là khái niệm trừu tượng; không có
declaration trực tiếp cho chúng trong ACL.

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
4. Enum không phải Entity, Group hay Role; không có object identity, Owner hay
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
- trở thành target của Owner.

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

Role member và Group con tạo Owner; chúng không phải Property và không phải
Association miền thông thường.

`ChildGroup extends ParentGroup` là generalization thông thường. Group con
nhận Property của Group cha, nhưng generalization không đồng nghĩa với Owner:

- `Department extends Unit` nghĩa là Department là một Unit;
- `Company { Department [*]; }` nghĩa là Company owner các Department;
- hai mệnh đề này độc lập.

Group không được:

- chứa Entity member;
- specialize Entity hoặc Role;
- dùng generalization thay cho containment;
- suy diễn một quan hệ tới Entity khi không có Relationship declaration tường
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

Role specialization và Group Owner là hai quan hệ khác nhau, nhưng nếu Role
cha và Role con đều có Owner thì hai cây phải tương thích.

Đặt `ownerRole(r)` là Group owner Role `r`, và `AncestorsOrSelf(g)` là Group
`g` cùng mọi Group tổ tiên theo Owner Group–Group. Với mọi Role tổ tiên `p`
của Role `c`:

\[
ownerRole(p)\ne\bot
\Rightarrow
ownerRole(c)\ne\bot\land
ownerRole(p)\in AncestorsOrSelf(ownerRole(c)).
\]

Role cha không có Owner không hạn chế scope Role con. Nếu Role cha có Owner,
Role con phải có Owner tại cùng Group hoặc một Group con/cháu. Hai Group ở hai
nhánh không liên quan tạo một Role parent sai. Role cha có Owner nhưng Role con
không có Owner cũng sai vì scope của con bị mở rộng.

Phải xét toàn bộ bao đóng Role cha, kể cả khi Role trung gian không có Owner.
Ở mức instance, hai đường Role-play và Group-Owner phải dẫn tới **cùng Group
occurrence**, không chỉ cùng Group type.

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

## 8. Relationship và MemberEnd

Mỗi Relationship tường minh có đúng hai MemberEnd. Với mỗi MemberEnd:

1. Target Classifier phải tồn tại.
2. Tên role/navigation phải không rỗng và không gây nhập nhằng tại Classifier
   đối diện.
3. Multiplicity phải có dạng `[n]`, `[l..u]` hoặc `[*]`.
4. `0 <= l <= u`; `*` chỉ được dùng làm upper bound vô hạn.
5. Trong một state, số link nhìn từ một object ở đầu đối diện phải nằm trong
   multiplicity của MemberEnd.

Tên Relationship phải duy nhất trong schema. Hai Relationship có thể nối cùng
một cặp Classifier nếu chúng có tên khác nhau.

### 8.1 Association

Association biểu diễn liên kết không sở hữu và không ràng buộc lifecycle. Nó
có thể nối Entity, Group hoặc Role theo endpoint được khai báo.

Association Role–Role là hợp lệ và phải giữ nguyên endpoint Role. Không được
thay nó bằng association Agent–Agent hoặc Agent–Entity.

### 8.2 Aggregation

Aggregation biểu diễn quan hệ whole–part yếu: part có thể tồn tại độc lập với
whole. Aggregation không tạo Owner và không có cascade lifecycle như
Composition.

Theo ACL hiện tại, Aggregation phải có ít nhất một endpoint Entity.
Aggregation Role–Role hoặc Group–Group thuần túy là không hợp lệ.

### 8.3 Composition

Composition biểu diễn whole–part mạnh:

1. Part có nhiều nhất một composite owner tại một thời điểm.
2. Link composition quyết định containment/lifecycle của part.
3. Đồ thị composition phải không chu trình.
4. Entity không được làm whole để sở hữu một Role hoặc Group.
5. Theo ACL hiện tại, Composition tường minh phải có ít nhất một endpoint
   Entity; containment Group–Role/Group–Group thuộc Owner, không khai lại bằng
   Composition thường.

Quan hệ Group–Entity chỉ tồn tại khi có một Relationship declaration tường
minh. Ví dụ hợp lệ:

```acl
composition meetingAgenda {
  Meeting [1] role meeting;
  Agenda [1] role agenda;
}
```

Thiết kế sai: đặt `Agenda [1];` trong thân `Meeting` rồi ngầm hiểu nó là
Composition.

## 9. Owner

Owner là một Relationship containment chuyên biệt, không phải tên gọi khác
của mọi Composition.

Miền hợp lệ:

\[
Owner\subseteq Group\times(Role\cup Group).
\]

Ràng buộc:

1. Source của Owner phải là Group.
2. Target chỉ được là Role hoặc Group.
3. Target có nhiều nhất một Owner trực tiếp.
4. Owner Group–Group phải không tạo chu trình containment.
5. Multiplicity ở target là multiplicity ghi trong thân Group.
6. Mỗi target occurrence phải thuộc đúng một source Group occurrence.
7. Khi dịch sang USE, Owner trở thành Composition.

Owner không được:

- có Entity làm target;
- có Role hoặc Entity làm source;
- được dùng như Association miền thông thường;
- bị đồng nhất với generalization Group–Group.

## 10. Agent và play-chain

Agent là khái niệm instance dùng để đồng nhất chủ thể đang play các Role. Khi
dịch sang USE, sinh đúng một Class rỗng tên `Agent`.

Ràng buộc:

1. Agent không có Property ACL được chuyển vào nó.
2. Với mỗi Role type gốc `R`, sinh association Agent–R.
3. Không sinh association trực tiếp Agent–Role cho Role không gốc.
4. Mỗi Role occurrence gốc có đúng một Agent.
5. Mỗi Role occurrence không gốc có đúng một Role occurrence cha.
6. Lần ngược một play-chain phải kết thúc tại đúng một Agent.

Thiết kế sai: coi Role là subclass của Agent hoặc dồn Property của mọi Role
gốc vào class Agent.

## 11. Ngữ nghĩa mặc định của Role

Trong cùng một Group occurrence và đối với cùng một Agent:

1. Agent không được có hai occurrence của cùng một Role type
   (`NoSelfConflict`).
2. Mặc định, Agent không được đồng thời play hai Role type khác nhau
   (`NoConflict`).
3. Role cha và Role con trên cùng play-chain được phép cùng tồn tại vì Role
   con đã đòi hỏi Role cha; xung đột mặc định áp dụng cho các Role độc lập.

“Cùng Agent” được xác định bằng play-chain ở Mục 10. “Cùng Group” được xác
định bằng Owner link, không bằng Group type hoặc Role specialization.

## 12. Compatibility

Compatibility là constraint giữa hai Role type khác nhau trong một Group
scope. Nó không phải Association và không tạo link occurrence.

`R1 compatible R2` có nghĩa: trong scope đó, cùng một Agent được đồng thời
play R1 và R2. Ràng buộc:

1. Hai endpoint phải là Role đã khai báo và phải khác nhau.
2. Compatibility đối xứng; khai báo `R1 compatible R2` bao phủ cả hai chiều.
3. Declaration phải nằm trong một Group scope hợp lệ cho cả hai Role.
4. Không được khai trùng cùng một cặp trong cùng scope.
5. Compatibility chỉ loại xung đột khác kiểu; không loại `NoSelfConflict`.
6. Compatibility không tạo play-chain, Owner hoặc quan hệ giữa Group.
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

- Agent instance;
- Entity, Group và Role occurrence;
- giá trị Property;
- link của Relationship tường minh;
- Owner link;
- play link Agent–Role gốc và Role cha–Role con.

State hợp lệ khi đồng thời thỏa:

1. mọi occurrence và link được định kiểu đúng;
2. mọi Property value đúng type, required/default/mutability;
3. mọi MemberEnd và Owner thỏa multiplicity;
4. generalization Entity/Group hợp lệ;
5. mọi play-chain đầy đủ, đơn trị về phía cha và kết thúc tại Agent;
6. Owner/composition có owner duy nhất và không chu trình;
7. mọi link chỉ nối các endpoint được declaration cho phép;
8. `NoSelfConflict` và mọi `NoConflict` còn hiệu lực đều đúng.

## 14. Những thiết kế sai ngữ nghĩa

Không được thiết kế hoặc hiện thực ACL theo các cách sau:

- coi Enumeration là Class hoặc Entity;
- cho phép declaration trực tiếp `Classifier`, `Datatype` hoặc `Relationship`;
- cho Entity xuất hiện như member trong Group;
- tạo Owner Group–Entity;
- dùng Group generalization để biểu diễn containment;
- dùng Owner để biểu diễn generalization;
- dịch Role specialization thành class generalization;
- sao chép Property Role cha vào Role con hoặc Agent;
- cho phép Role con thuộc Group không liên quan với Group owner Role cha;
- chỉ kiểm tra cạnh Role cha trực tiếp mà bỏ qua tổ tiên Role nhiều cấp;
- nối Agent trực tiếp tới mọi Role thay vì chỉ Role gốc;
- quy Association Role–Role về Agent;
- dùng Aggregation như Composition hoặc áp cascade lifecycle cho Aggregation;
- cho part có nhiều composite owner hoặc tạo composition cycle;
- coi `compatible` là một link runtime;
- mặc định mọi Role đều compatible;
- dùng `compatible` để cho phép hai occurrence cùng Role type;
- tự sinh quan hệ Group–Entity khi schema không khai Relationship tương ứng.

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
