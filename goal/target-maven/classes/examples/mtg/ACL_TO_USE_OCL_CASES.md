# Luật dịch ACL sang USE/OCL

Tài liệu này định nghĩa cả **luật dịch** và **thứ tự áp dụng luật**. Nguồn
ngữ nghĩa chuẩn là `goal/docs/formal/acl.md` và
`goal/docs/semantics/dsl/acl.md`.

## 1. Nguyên tắc

Phép dịch tách thành ba lớp, áp dụng theo đúng thứ tự:

1. Tạo các classifier USE: Enum giữ nguyên thành `enum`; Entity, Group và Role
   trở thành `class`; sau đó tạo thêm class rỗng `Agent`.
2. Tạo cấu trúc: generalization Entity/Group, association cho kế thừa Role,
   association từ `Agent` tới Role gốc, các quan hệ khai báo và Owner.
3. Sinh OCL cho ngữ nghĩa mặc định của Role và các ngoại lệ `compatible`.

Không được xử lý `compatible` trước khi đã dựng xong chuỗi association
Agent–Role gốc–Role con và các composition Owner, vì OCL cần navigation do
những bước này tạo ra.

## 2. Ngữ nghĩa mặc định của Role và `compatible`

Trong một Group instance:

- cùng một Agent không được có hai occurrence của cùng một Role type;
- mặc định, cùng một Agent không được đồng thời play hai Role type khác nhau;
- `R1 compatible R2` là ngoại lệ cho mặc định thứ hai: Agent được đồng thời
  play `R1` và `R2` trong scope Group chứa khai báo;
- `compatible` không cho phép hai occurrence của cùng một Role type;
- kế thừa Role không tự tạo compatibility.

OCL là ngôn ngữ ràng buộc nên `compatible` không được dịch thành một invariant
"cho phép" độc lập. Translator phải tính tập cặp Role bị cấm:

\[
Forbidden(G)=DifferentRolePairs(G)\setminus CompatiblePairs(G)
\]

Sau đó sinh một invariant `NoConflict` cho mỗi cặp trong `Forbidden(G)`.
Vì vậy khai báo `compatible` làm thay đổi **tập invariant OCL được sinh**, chứ
không đảo ngược ngữ nghĩa mặc định của Role.

## 3. Các luật dịch

### R1 — Enum giữ nguyên thành enum

USE có sẵn enum với cùng ngữ nghĩa tập literal đóng, vì vậy giữ nguyên tên
Enum và toàn bộ literal.

```acl
enum Priority { LOW, HIGH }
```

```use
enum Priority { LOW, HIGH }
```

### R2 — Entity thành class

Mỗi Entity trở thành một class cùng tên; các thuộc tính được giữ tên và kiểu.

```acl
entity Document { title : String; }
```

```use
class Document
attributes
  title : String
end
```

### R3 — Group thành class

Mỗi Group trở thành một class cùng tên; chỉ các thuộc tính của Group được đặt
trong class. Role member và Group con được xử lý sau bởi R10 (Owner).

```acl
group Team {
  code : String;
}
```

```use
class Team
attributes
  code : String
end
```

### R4 — Role thành class

Mỗi Role trở thành một class độc lập cùng tên. Không dùng USE generalization
giữa hai Role và không sao chép thuộc tính Role cha vào Role con.

```acl
role Person { name : String; }
role Employee extends Person { employeeId : String; }
```

```use
class Person
attributes
  name : String
end

class Employee
attributes
  employeeId : String
end
```

### R5 — Sinh class Agent rỗng

Mỗi model sinh đúng một class tên `Agent`. Class này không có thuộc tính và
không kế thừa class nào. Các liên kết của nó được sinh ở R8.

```acl
acl v2.0 Organization {
  role Person;
}
```

```use
class Agent
end
```

### R6 — Giữ generalization Entity–Entity và Group–Group

Kế thừa Entity và kế thừa Group được giữ nguyên bằng USE generalization. Đây
là kế thừa classifier thông thường nên class con nhận thuộc tính của class
cha.

```acl
entity Document { title : String; }
entity Report extends Document { approved : Boolean; }

group Unit { code : String; }
group Department extends Unit { budget : Real; }
```

```use
class Document attributes title : String end
class Report < Document attributes approved : Boolean end

class Unit attributes code : String end
class Department < Unit attributes budget : Real end
```

Không áp dụng R6 cho Role.

### R7 — Kế thừa Role thành association một chiều từ cha đến con

Với `Child extends Parent`, sinh association `Parent_plays_Child`. Mỗi
occurrence `Child` phải nối tới đúng một occurrence `Parent`; từ một Parent có
thể đi tới nhiều Child. Chiều navigation chuẩn là từ Role cha tới Role con;
navigation ngược được dùng nội bộ khi cần tìm chuỗi về Agent.

```acl
role Person;
role Employee extends Person;
role Manager extends Employee;
```

```use
association Person_plays_Employee between
  Person[1] role parent
  Employee[0..*] role children
end

association Employee_plays_Manager between
  Employee[1] role parent
  Manager[0..*] role children
end
```

Luật này diễn tả điều kiện play: muốn play `Manager` phải play `Employee`, và
muốn play `Employee` phải play `Person`. Nếu các Role trong chuỗi có Owner,
schema còn phải thỏa tính đơn điệu Group scope ở R11.

### R8 — Nối Agent tới tất cả Role gốc

Role gốc là Role không `extends` Role nào. Với mỗi Role gốc `R`, sinh một
association `Agent_plays_R`. Không nối Agent trực tiếp tới Role không gốc.

```acl
role Person;
role Observer;
role Employee extends Person;
```

```use
association Agent_plays_Person between
  Agent[1] role agent
  Person[0..*] role roles
end

association Agent_plays_Observer between
  Agent[1] role agent
  Observer[0..*] role roles
end
```

Không sinh `Agent_plays_Employee`, vì `Employee` đã nối tới `Person` theo R7.

### R9 — Giữ nguyên association, aggregation và composition khai tường minh

Mỗi quan hệ khai tường minh được dịch sang quan hệ USE cùng loại, cùng tên,
cùng endpoint, role name và multiplicity. Association Role–Role cũng áp dụng
đúng luật này; không quy endpoint Role về Agent.

```acl
role Manager;
role Reviewer;
entity Catalogue;
entity Document;
group Team;
entity Report;

association collaborates {
  Manager [0..*] role managers;
  Reviewer [0..*] role reviewers;
}

aggregation catalogues {
  Catalogue [1] role catalogue;
  Document [0..*] role documents;
}

composition records {
  Team [1] role team;
  Report [0..*] role reports;
}
```

```use
association collaborates between
  Manager[0..*] role managers
  Reviewer[0..*] role reviewers
end

aggregation catalogues between
  Catalogue[1] role catalogue
  Document[0..*] role documents
end

composition records between
  Team[1] role team
  Report[0..*] role reports
end
```

Quan hệ Group–Entity chỉ tồn tại khi được khai tường minh theo R9. Không có cú
pháp Entity member trong Group và không suy diễn quan hệ Group–Entity.

### R10 — Owner Group–Role và Group–Group thành composition

Owner chỉ có hai dạng hợp lệ:

- Group owner Role;
- Group owner Group con.

Mỗi Owner trở thành một USE composition và giữ multiplicity của target.

```acl
role Member;

group Team {
  Member [1..*];
}

group Company {
  Team [0..*];
}
```

```use
composition Owner_Team_Member between
  Team[1] role owner
  Member[1..*] role members
end

composition Owner_Company_Team between
  Company[1] role owner
  Team[0..*] role children
end
```

Không tồn tại Owner Group–Entity. Ví dụ composition khai tường minh sau được
xử lý bởi R9:

```acl
entity Report;
composition teamReports {
  Team [1] role team;
  Report [0..*] role reports;
}
```

### R11 — Đồng bộ Role hierarchy với Group Owner hierarchy

Với mọi Role tổ tiên `P` của Role `C`, nếu `P` có Owner thì `C` cũng phải có
Owner; Group owner `P` phải là cùng Group hoặc Group tổ tiên trực tiếp/gián
tiếp của Group owner `C`. Hai Group ở hai nhánh không liên quan làm schema bị
từ chối.

```acl
role Employee;
role Manager extends Employee;

group Company {
  Employee [0..*];
  Division [0..*];
}
group Division { Department [0..*]; }
group Department { Manager [0..*]; }
```

Ngoài kiểm tra type-level, sinh invariant bảo đảm hai đường dẫn tới đúng cùng
`Company` occurrence:

```use
context Manager inv RoleOwnerScope_Employee_Manager:
  self.source_Employee_plays_Manager.source_Employee_in_Company =
  self.source_Manager_in_Department
      .source_Owner_Division_Department
      .source_Owner_Company_Division
```

Phải xét toàn bộ Role tổ tiên, không chỉ Role cha trực tiếp.

### R12 — Sinh invariant OCL cho Role mặc định và `compatible`

R12 chạy sau R7–R11.

Với mỗi Role `R` thuộc Group `G`, sinh `NoSelfConflict_R` để một Agent không
có hai occurrence `R` trong cùng `G` instance.

Với mỗi cặp Role type khác nhau cùng thuộc scope `G`:

1. nếu cặp không có trong `CompatiblePairs(G)`, sinh `NoConflict_R1_R2`;
2. nếu có khai báo `R1 compatible R2`, không sinh invariant cấm cho cặp đó;
3. declaration được hiểu đối xứng: `R1 compatible R2` tương đương
   `R2 compatible R1`;
4. so sánh cùng Agent bằng cách lần ngược R7 tới cùng Role tổ tiên gần nhất,
   hoặc tới `Agent` qua R8 nếu hai Role không có tổ tiên Role chung.

```acl
role Person;
role Organizer extends Person;
role Secretary extends Person;
role Observer extends Person;

group Meeting {
  Organizer [1];
  Secretary [1..*];
  Observer [0..*];
  Organizer compatible Secretary;
}
```

Kết quả OCL về mặt ngữ nghĩa:

```use
constraints

-- Luôn sinh: cùng Agent không được làm Organizer hai lần trong một Meeting.
context Person inv NoSelfConflict_Organizer:
  -- hai Organizer con khác nhau của self không cùng owner Meeting

-- Không sinh NoConflict_Organizer_Secretary vì đã khai compatible.

-- Vẫn sinh vì không có khai báo compatible cho hai cặp này.
context Person inv NoConflict_Organizer_Observer:
  -- Organizer và Observer của self không cùng owner Meeting

context Person inv NoConflict_Secretary_Observer:
  -- Secretary và Observer của self không cùng owner Meeting
```

Các comment trên mô tả predicate bắt buộc; tên navigation cụ thể phụ thuộc quy
ước đặt tên association của backend. Điểm quyết định là `compatible` loại đúng
cặp khỏi tập invariant cấm, không loại `NoSelfConflict`.

## 4. Thứ tự chuyển đổi bắt buộc

Translator phải áp dụng theo trình tự sau:

1. Đọc và kiểm tra tên, kiểu thuộc tính, endpoint và multiplicity.
2. Áp dụng R1 để tạo các USE enum, rồi R2–R4 để tạo class từ Entity, Group và
   Role.
3. Áp dụng R5 để tạo class rỗng `Agent`.
4. Áp dụng R6 cho generalization Entity và Group.
5. Áp dụng R7 cho toàn bộ cạnh kế thừa Role, từ cha tới con.
6. Xác định Role gốc rồi áp dụng R8; bước này phải chạy sau R7.
7. Áp dụng R9 cho association, aggregation và composition khai tường minh,
   bao gồm association Role–Role và composition Group–Entity.
8. Áp dụng R10 cho Owner Group–Role và Group–Group.
9. Áp dụng R11 để kiểm tra tính đơn điệu Group scope và sinh
   `RoleOwnerScope` cho từng Role tổ tiên có Owner.
10. Tính Role scope, chuỗi về Agent và `CompatiblePairs(G)`.
11. Áp dụng R12 để sinh `NoSelfConflict` và `NoConflict`.
12. Kiểm tra lại mọi OCL navigation sau khi toàn bộ association/composition đã
    tồn tại.

Tóm tắt dependency:

```text
R1–R4 -> R5 -> R6 -> R7 -> R8 -> R9 -> R10 -> R11 -> R12
 types    Agent  gen   role  root  relation Owner  scope   conflict
                      chain roles                  OCL     OCL
```

## 5. Ví dụ tổng thể

```acl
acl v2.0 Example {
  enum Priority { LOW, HIGH }

  entity Document { title : String; }
  entity Report extends Document { approved : Boolean; }

  role Person { name : String; }
  role Organizer extends Person;
  role Secretary extends Person;

  group Unit { code : String; }
  group Meeting extends Unit {
    Organizer [1];
    Secretary [1..*];
    Organizer compatible Secretary;
  }

  association cooperates {
    Organizer [0..*] role organizers;
    Secretary [0..*] role secretaries;
  }

  composition meetingReports {
    Meeting [1] role meeting;
    Report [0..*] role reports;
  }
}
```

Áp dụng lần lượt:

1. Tạo enum `Priority`; tạo các class `Document`, `Report`, `Person`,
   `Organizer`, `Secretary`, `Unit`, `Meeting` và class rỗng `Agent`.
2. Tạo `Report < Document` và `Meeting < Unit`.
3. Tạo `Person_plays_Organizer` và `Person_plays_Secretary`.
4. Vì `Person` là Role gốc duy nhất, tạo `Agent_plays_Person`.
5. Giữ nguyên association Role–Role `cooperates`.
6. Giữ nguyên composition Group–Entity `meetingReports`; đây không phải Owner.
7. Chuyển hai member `Organizer`, `Secretary` của `Meeting` thành composition
   Owner.
8. Sinh `NoSelfConflict` cho từng Role trong `Meeting`.
9. Không sinh `NoConflict_Organizer_Secretary`, vì cặp này đã được khai
   `compatible`.

## 6. Những phép dịch bị cấm

- Không dịch Role inheritance thành USE generalization.
- Không sao chép thuộc tính Role cha vào Role con.
- Không nối Agent trực tiếp tới Role không gốc.
- Không tạo Owner Group–Entity.
- Không cho phép Entity member trong thân Group.
- Không tạo quan hệ Group–Entity nếu ACL không có declaration quan hệ tường
  minh tương ứng.
- Không đổi association Role–Role thành association giữa các Agent.
- Không hiểu `compatible` là mặc định; mặc định là xung đột.
- Không dùng `compatible` để cho phép hai occurrence cùng Role type.
- Không suy ra quan hệ Group cha–con từ chuỗi kế thừa Role.
