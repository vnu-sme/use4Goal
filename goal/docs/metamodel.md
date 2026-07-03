# MAXGoal Metamodel

## Class Diagram

```mermaid
classDiagram
    direction TB

    class IntentionalElement {
        + description : String
    }
    class Contribution {
        + con : ContributionType
    }
    class ContributionType {
        Make
        Help
        Hurt
        Break
    }
    class Actor
    class GoalModel
    class Agent
    class Role
    class Dependency
    class Quality
    class ConcreteIntentionalElement
    class Refinement
    class GoalTaskElement
    class Resource
    class Goal
    class Task
    class Gaude

    IntentionalElement <|-- Actor : is-a
    Actor <|-- GoalModel
    Actor <|-- Agent
    Actor <|-- Role

    IntentionalElement <|-- ConcreteIntentionalElement
    ConcreteIntentionalElement <|-- GoalTaskElement
    ConcreteIntentionalElement <|-- Resource
    ConcreteIntentionalElement <|-- Quality

    GoalTaskElement <|-- Goal
    GoalTaskElement <|-- Task

    Refinement <|-- `SEQ-Refinement`
    Refinement <|-- `PAR-Refinement`
    Refinement <|-- `IOR-Refinement`
    Refinement <|-- `XOR-Refinement`
    `SEQ-Refinement` <|-- `ITER-Refinement`

    Actor "1" *-- "0..*" GoalModel : participates-in
    Actor "1" --> "0..*" Dependency : Dependee
    Actor "1" --> "0..*" Dependency : Depender

    Dependency "0..*" --> "1" IntentionalElement : DependumEle
    Dependency "0..*" --> IntentionalElement : DependerEle
    Dependency "0..*" --> IntentionalElement : DependeeEle

    IntentionalElement "1" --> "0..*" ConcreteIntentionalElement : want

    Refinement "0..1" o-- "1" GoalTaskElement

    Contribution ..> Quality : qualifies
    Contribution "0..*" --> IntentionalElement

    Task "0..*" --> "0..*" Resource : needby

```

## Ghi chú quan hệ

| Refinement | Quan hệ với GoalTaskElement | Ý nghĩa |
|---|---|---|
| **SeqRefine** | `1 --> 2..*` ordered children | Con thực hiện tuần tự theo thứ tự danh sách |
| **IterRefine** *(extends SeqRefine)* | kế thừa ordered children + `until: String` | Như SEQ nhưng lặp đến khi guard đúng |
| **ParRefine** | `1 --> 2..*` parallel children | Tất cả con thực hiện song song |
| **IorRefine** | `1 *-- 2..* GuardedChild --> 1 GTE` | Một hoặc nhiều nhánh có guard đúng cùng kích hoạt |
| **XorRefine** *(extends IorRefine)* | kế thừa branches + ràng buộc exactly-one | Đúng một nhánh có guard đúng mới kích hoạt |

## Lưu ý thiết kế

- `GoalTaskElement` sở hữu `RefineSpec` (composition `◆`) — một goal/task có **0..1** cách phân rã.
- Mỗi `RefineSpec` tham chiếu đến **con** (cũng là `GoalTaskElement`) bằng tên (`String id`) để hỗ trợ forward reference trong file văn bản `.maxgoal`.
- `GuardedChild` là value object gộp `(condition, childId)` — không phải entity độc lập.
- `ResourceDef` không có refinement; chỉ kết nối qua quan hệ `needby` đến `GoalTaskElement`.
- `Actor` là ranh giới phạm vi — mọi `ConcreteIntentionalElement` đều thuộc đúng một `Actor`.
