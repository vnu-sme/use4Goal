# Báo cáo chuẩn hóa metamodel và ký hiệu đồ thị ACL

## 1. Mục đích

Tài liệu này xác định các concept chính và bộ ký hiệu đồ họa thống nhất cho
ngôn ngữ ACL. Đây là cơ sở để chỉnh sửa `01-ACL-metamodel.drawio` và sau đó
đồng bộ cách hiển thị đồ thị trong mã nguồn Java.

Mục tiêu của bộ ký hiệu là:

- mỗi loại quan hệ chỉ mang một ngữ nghĩa;
- không dùng hình thoi của composition cho compatibility;
- thể hiện rõ chiều của quan hệ và phía sở hữu;
- phân biệt được ký hiệu ngay cả khi sơ đồ được in đen trắng;
- giữ ngữ nghĩa association, aggregation, composition và inheritance tương
  thích với UML class diagram.

## 2. Các concept chính

| Concept | Ý nghĩa | Hình dạng đề xuất |
|---|---|---|
| `StructuralSpecification` | Phần tử gốc của một đặc tả ACL | Khung UML viền kép, tiêu đề `«root» StructuralSpecification`, ngăn thuộc tính phía dưới |
| `EntityDefinition` | Định nghĩa một entity và các thuộc tính của entity | Dùng nguyên component UML Class của USE: tên class ở header, đường kẻ ngang, attributes phía dưới; không tạo renderer Entity riêng |
| `RoleDefinition` | Định nghĩa một role và các thuộc tính của role | Node Role gọn; thêm một icon hình người nhỏ trong header, tên Role và attributes giữ cách trình bày hiện có |
| `GroupSpecification` | Định nghĩa một group | Hình chữ nhật bo góc có tab ở góc trên, tiêu đề `«group» GroupName` |
| `AttributeDefinition` | Thuộc tính thuộc Entity hoặc Role | Một dòng trong ngăn attributes theo dạng `name: Type` cùng các modifier |
| `EnumDefinition` | Kiểu liệt kê do ACL định nghĩa | Khung UML có stereotype `«enumeration»`, các literal nằm dưới đường kẻ ngang |
| `Cardinality` | Miền số lượng của một đầu quan hệ | Nhãn đặt cạnh đầu quan hệ theo dạng `[min..max]`, trong đó `*` là không giới hạn |
| `MemberEnd` | Đầu có kiểu của một quan hệ | Chỉ lưu `multiplicity`; không khai báo `role`/role name |
| `EntityRelation` | Quan hệ có ít nhất một đầu là Entity | Cạnh mang một trong ba loại: association, aggregation hoặc composition |
| `Compatibility` | Khai báo hai Role có thể tương thích | Vòng tròn rỗng; nét liền nếu intra-group, nét đứt nếu inter-group |
| `EntityInheritance` | Quan hệ kế thừa giữa hai Entity | Generalization UML, tam giác rỗng hướng về Entity cha |
| `RoleInheritance` | Quan hệ kế thừa giữa hai Role | Generalization UML, tam giác rỗng hướng về Role cha |
| `Owner` | Group sở hữu Role hoặc Group khác | Đường liền với hình vuông nhỏ ở đầu Group cha: `Group □──── Role/Group` |

`Entity`, `Role` và `Group` phải có hình dạng hoặc stereotype khác nhau. Không
được chỉ dựa vào màu sắc để phân biệt các concept này.

Quy tắc renderer: Entity kế thừa trực tiếp `ClassNode` của USE và không sửa
component đó. Role không mô phỏng lại ClassNode; dấu hiệu bổ sung duy nhất ở
header là icon hình người kích thước nhỏ.

## 3. Nguyên tắc chung cho các cạnh

1. Hình thoi luôn đặt ở phía đối tượng sở hữu.
2. Hình thoi rỗng chỉ biểu diễn aggregation.
3. Hình thoi đặc chỉ biểu diễn composition.
4. Tam giác rỗng của inheritance luôn hướng về kiểu cha.
5. Mũi tên mở của association luôn hướng về target.
6. Compatibility không sử dụng bất kỳ loại hình thoi nào.
7. Cardinality đặt gần đầu quan hệ mà nó mô tả.
8. Scope được thể hiện bằng nhãn, không thay đổi ngữ nghĩa của kiểu nét.
9. `MemberEnd` chỉ chứa multiplicity. Concrete syntax của endpoint là
   `Classifier [multiplicity];`, không có role name.
10. `Owner` không có relation name. Group là source ngầm định bởi group chứa
    member; chỉ target được biểu diễn qua `MemberEnd` và multiplicity.

Concrete syntax chuẩn của một quan hệ có hai `MemberEnd`:

```acl
association know {
  MeetingParty [1];
  PhoneContact [*];
}
```

Nếu `MeetingParty` vừa biết `PhoneContact` vừa sở hữu mạnh nó, hai ngữ nghĩa
phải được khai báo bằng hai Relationship độc lập:

```acl
association know {
  MeetingParty [1];
  PhoneContact [*];
}

composition phoneContacts {
  MeetingParty [1];
  PhoneContact [*];
}
```

## 4. Quan hệ có Entity

Khi một trong hai đầu là Entity, quan hệ có thể là association, aggregation
hoặc composition. Quan hệ có thể đi theo cả hai hướng, tùy đối tượng nào đóng
vai trò source hoặc owner:

- `Entity -> Entity`, `Entity -> Role`, `Entity -> Group`;
- `Role -> Entity`, `Group -> Entity`;
- quan hệ Entity--Entity cũng có thể không chỉ định hướng.

### 4.1. Association

Association chỉ biểu diễn liên kết cấu trúc, không biểu diễn sở hữu hoặc phụ
thuộc vòng đời.

```text
A ───────── B       association không chỉ định hướng
A ────────> B       association có hướng từ A đến B
```

Quy tắc:

- dùng đường liền;
- không có hình thoi;
- nếu có hướng, dùng mũi tên mở ở phía target;
- có thể đặt cardinality ở cả hai đầu.

### 4.2. Aggregation

Aggregation biểu diễn sở hữu yếu. Part vẫn có thể tồn tại độc lập với Owner.

```text
Owner ◇──────── Part
```

Quy tắc:

- dùng đường liền;
- đặt hình thoi rỗng `◇` phía Owner;
- có thể dùng cho Entity--Entity, Entity--Role và Entity--Group theo cả hai
  hướng;
- không áp dụng ràng buộc vòng đời như composition.

Ví dụ:

```text
Entity ◇──── Role
Role   ◇──── Entity
Entity ◇──── Group
Group  ◇──── Entity
```

### 4.3. Composition

Composition biểu diễn sở hữu mạnh. Part phụ thuộc vòng đời vào Owner.

```text
Owner ◆──────── Part
```

Quy tắc:

- dùng đường liền;
- đặt hình thoi đặc `◆` phía Owner;
- có thể dùng cho Entity--Entity, Entity--Role và Entity--Group theo cả hai
  hướng;
- một Part chỉ có tối đa một composite owner;
- không cho phép chu trình composition;
- không bắt buộc phải vẽ Part nằm bên trong khung Owner.

Ví dụ:

```text
Entity ◆──── Entity
Entity ◆──── Role
Role   ◆──── Entity
Entity ◆──── Group
Group  ◆──── Entity
```

## 5. Kế thừa của Entity

Entity chỉ được kế thừa Entity:

```text
ChildEntity ─────────▷ ParentEntity
```

Quy tắc:

- dùng đường liền;
- đặt tam giác rỗng phía Entity cha;
- Entity không kế thừa Role hoặc Group;
- không cho phép tự kế thừa;
- đồ thị kế thừa Entity không được có chu trình.

## 6. Quan hệ Role--Role

Role--Role có hai loại quan hệ độc lập: compatibility và inheritance.

### 6.1. Compatibility

Chỉ vẽ cạnh compatibility khi hai Role được khai báo `compatible`. Nếu không
có khai báo thì mặc định hai Role là `incompatible`, vì vậy không cần vẽ hàng
loạt cạnh incompatible.

```text
RoleA ○╌╌╌╌╌○ RoleB
       «compatible»
```

Quy tắc:

- dùng đường liền cho `intra-group`;
- dùng đường nét đứt cho `inter-group`;
- dùng vòng tròn rỗng `○`, tuyệt đối không dùng hình thoi;
- compatibility hai chiều có vòng tròn ở cả hai đầu;
- compatibility một chiều chỉ có vòng tròn ở đầu target;
- nhãn cơ bản là `«compatible»`;
- scope và tùy chọn mở rộng được ghi trong nhãn.

Ví dụ nhãn:

```text
«compatible, intra-group»
«compatible, inter-group»
«compatible, extends-subgroups»
```

### 6.2. Role inheritance

Role chỉ được kế thừa Role:

```text
ChildRole ─────────▷ ParentRole
```

Quy tắc:

- dùng đường liền;
- đặt tam giác rỗng phía Role cha;
- Role không kế thừa Entity hoặc Group;
- không cho phép tự kế thừa;
- đồ thị kế thừa Role không được có chu trình.

## 7. Quan hệ Owner giữa Group--Role

Group sở hữu Role bằng quan hệ `Owner` bắt buộc:

```text
Group □──────── Role
                 [min..max]
```

Quy tắc:

- dùng đường liền và hình vuông nhỏ `□` ở phía Group cha;
- ký hiệu rút gọn chính thức là `Group □──── Role`;
- quan hệ bắt buộc là `Owner`, không dùng association, aggregation hoặc
  composition;
- cardinality đặt phía Role;
- không cần vẽ Role nằm bên trong Group;
- không tồn tại quan hệ có source là Role và target là Group.
- không hiển thị tên quan hệ trên cạnh Owner.

Tóm lại:

```text
Group -> Role : hợp lệ, chỉ Owner
Role  -> Group: không hợp lệ
```

## 8. Quan hệ Owner giữa Group--Group

Group cha sở hữu Group con bằng quan hệ `Owner` bắt buộc:

```text
ParentGroup □──────── ChildGroup
                         [min..max]
```

Quy tắc:

- dùng đường liền và hình vuông nhỏ `□` ở phía ParentGroup;
- ký hiệu rút gọn chính thức là `ParentGroup □──── ChildGroup`;
- không dùng association, aggregation hoặc composition;
- cardinality đặt phía Group con;
- một Group con chỉ có tối đa một Group cha theo Owner;
- không cho phép chu trình Owner giữa các Group.
- không hiển thị tên quan hệ trên cạnh Owner.

## 9. Ma trận quan hệ chính thức

| Cặp concept | Association | Aggregation | Composition | Owner | Compatibility | Inheritance |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| Entity--Entity | Có | Có | Có | Không | Không | Có |
| Entity--Role | Có, hai hướng | Có, hai hướng | Có, hai hướng | Không | Không | Không |
| Entity--Group | Có, hai hướng | Có, hai hướng | Có, hai hướng | Không | Không | Không |
| Role--Role | Không | Không | Không | Không | Có | Có |
| Role--Group | Không | Không | Không | Chỉ `Group -> Role` | Không | Không |
| Group--Group | Không | Không | Không | Chỉ Group cha -> Group con | Không | Không |

Trong bảng trên, "hai hướng" có nghĩa là source/owner có thể nằm ở một trong
hai phía. Nó không bắt buộc mọi quan hệ phải là bidirectional.

## 10. Bảng ký hiệu rút gọn

| Ngữ nghĩa | Ký hiệu | Kiểu nét | Marker |
|---|---|---|---|
| Association không hướng | `A ───── B` | Liền | Không có |
| Association có hướng | `A ────> B` | Liền | Mũi tên mở phía target |
| Aggregation | `Owner ◇──── Part` | Liền | Thoi rỗng phía Owner |
| Composition | `Owner ◆──── Part` | Liền | Thoi đặc phía Owner |
| Owner | `Group □──── Role/Group` | Liền | Vuông nhỏ phía Group cha |
| Compatibility intra-group | `Role ○────○ Role` | Liền | Vòng tròn rỗng |
| Compatibility inter-group | `Role ○╌╌╌○ Role` | Đứt | Vòng tròn rỗng |
| Entity inheritance | `ChildEntity ───▷ ParentEntity` | Liền | Tam giác rỗng phía Entity cha |
| Role inheritance | `ChildRole ───▷ ParentRole` | Liền | Tam giác rỗng phía Role cha |

## 11. Các ràng buộc hợp lệ

1. Association, aggregation và composition chỉ áp dụng khi quan hệ có ít
   nhất một đầu là Entity.
2. Entity inheritance chỉ có endpoint Entity--Entity.
3. Role inheritance chỉ có endpoint Role--Role.
4. Compatibility chỉ có endpoint Role--Role.
5. Không khai báo compatibility đồng nghĩa mặc định incompatible.
6. Group--Role chỉ hợp lệ theo chiều Group sở hữu Role bằng Owner.
7. Group--Group chỉ hợp lệ theo chiều Group cha sở hữu Group con bằng Owner.
8. Role--Group không tồn tại theo chiều Role làm source.
9. Hình thoi đặc không được dùng cho compatibility.
10. Các đồ thị inheritance, composition và Owner không được có chu trình.

## 12. Kết luận

Bộ ký hiệu được chốt như sau:

- đường liền không marker: association;
- đường liền và hình thoi rỗng: aggregation;
- đường liền và hình thoi đặc: composition;
- đường liền và hình vuông nhỏ phía Group cha: Owner, viết `Group □──── Role/Group`;
- vòng tròn rỗng: Compatibility; nét liền cho intra-group và nét đứt cho inter-group;
- đường liền và tam giác rỗng: inheritance cùng loại;
- Group--Role và Group--Group bắt buộc dùng Owner;
- Entity có thể tham gia association, aggregation hoặc composition với
  Entity, Role và Group theo cả hai hướng;
- Entity chỉ kế thừa Entity, Role chỉ kế thừa Role.

Owner không dùng tam giác hoặc hình thoi: hình vuông nhỏ luôn nằm ở đầu Group
cha trên đường liền. Generalization tiếp tục dùng tam giác rỗng. Đây là chuẩn
ký hiệu được phản ánh thống nhất trong grammar, metamodel Java và renderer ACL.
