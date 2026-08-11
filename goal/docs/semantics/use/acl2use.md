# Luật dịch ACL → USE

## Mục tiêu

Phép dịch nhận một `AclModel` đã qua semantic validation và sinh một USE class
model. Nó bảo toàn kiểu, multiplicity, navigation, ownership, compatibility và
well-formedness cần kiểm tra bằng OCL.

## Ánh xạ

| ACL | USE |
|---|---|
| Entity, Group | class |
| Entity/Group `extends` | generalization |
| Role | class của role occurrence |
| Role `extends` | association trong chuỗi play, không đồng nhất object |
| attribute, enum | attribute, enum |
| named relation | association/aggregation/composition cùng tên |
| Role/Group member trong Group | Owner, rồi thành composition cùng multiplicity |
| Group–Entity | chỉ từ quan hệ tường minh, giữ nguyên loại quan hệ |
| Agent plays Role | association từ Agent hoặc role cha đến role occurrence |
| Role/Group scope alignment | OCL `RoleOwnerScope_*` trên Role con |
| compatibility | OCL invariant cấm các cặp play không được khai báo |

Tên navigation công khai của ACL phải được bảo toàn. Tên kỹ thuật cần sinh để
tránh va chạm chỉ là chi tiết backend và không được xuất hiện trong OCL nguồn.

## Ví dụ

Nguồn:

```acl
role Participant {
  attended : Boolean mutable default false;
}

group MeetingUnit {
  Participant [2..*];
}
```

Đích rút gọn:

```use
class Agent
end

class Participant
attributes
  attended : Boolean
end

class MeetingUnit
end

composition Participant_in_MeetingUnit between
  MeetingUnit[1] role group
  Participant[2..*] role participants
end

association Agent_plays_Participant between
  Agent[1] role agent
  Participant[*] role participantOccurrences
end
```

Nếu hai Role khác kiểu thuộc cùng Agent và cùng Group scope nhưng không có
`compatible`, translator sinh invariant loại nghiệm đó. Nếu construct không
thể dịch chính xác, translator phải dừng với diagnostic.
