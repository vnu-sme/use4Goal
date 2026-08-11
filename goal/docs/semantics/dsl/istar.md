# iStar

iStar mô tả **ý định và trách nhiệm** trên trạng thái ACL. Actor Role được bind
với Role occurrence; vì vậy một intentional element có thể sinh nhiều
occurrence theo Agent, Group và context path.

## Thành phần

- Intentional element: Goal, Task, Resource, Quality và Obstacle.
- Refinement: AND, OR, `forall` và `pick`.
- Quan hệ: contribution, qualification, needed-by, obstructs/resolves và
  strategic dependency.
- Goal type: `Achieve`, `Maintain`, `Sustain`, `Recur`.

`forall R` tạo một nhánh marking cho mọi occurrence của Role `R`; `pick R` tạo
các candidate và cần một candidate thành công. Chúng không chỉ là alias của
OCL `forAll`/`exists`, bởi mỗi nhánh còn giữ actor, context và marking riêng.

## Contract và context

Goal dùng `activation` và `condition`; Task dùng `pre` và `post`. Root Goal cần
nguồn activation, trừ khi demand đến qua dependency. Child của AND/forall kế
thừa demand; child của OR/pick có thể có activation làm eligibility guard.

Context path đi từ ngoài vào trong. `self` là binding cuối; `self.outer` trở về
binding trước. AND/OR không thêm binding, `forall`/`pick` thêm binding, còn
dependency truyền context sang actor chịu trách nhiệm.

## Ví dụ

```istar
role Initiator {
  goal MeetingOrganized : Achieve
    activation {[ true ]}
    condition {[ self.group.detailsDecided ]}

  goal EveryParticipantAttended : Achieve
  goal ParticipantAttended : Achieve
    condition {[ self.attended ]}
    > forall Participant EveryParticipantAttended
}
```

Marking Goal dùng `(A,P,S)` và Task dùng `(Q,R)`; status quan sát thống nhất là
`UNKNOWN`, `PENDING`, `FULFILLED`, `VIOLATED`. Chi tiết toán học thuộc
[formal/istar.md](../../formal/istar.md).
