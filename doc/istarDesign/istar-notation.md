# iStar 2.0 — Ký hiệu cụ thể (concrete notation)

Ngày lập: 2026-07-05

Tài liệu này mô tả cách `IStarView` (`goal/src/main/java/org/vnu/sme/goal/istar/view/IStarView.java`)
vẽ cụ thể một model `.istar` lên màn hình — không lặp lại phần metamodel (khái niệm,
quan hệ, multiplicity), phần đó đã có ở [`04-istar-metamodel.drawio`](04-istar-metamodel.drawio)
và ngữ pháp [`IStar.g4`](../goal/src/main/resources/grammars/IStar.g4).

---

## 1. Màu sắc: nền trắng, viền/chữ đen

Toàn bộ diagram chỉ dùng 2 màu: nền trắng (`Color.WHITE`) và viền/chữ đen (`Color.BLACK`)
— không tô màu phân biệt theo loại phần tử như phiên bản trước. Xem hằng số palette ở
đầu `IStarView`:

```java
private static final Color C_BG   = Color.WHITE;
private static final Color C_LINE = Color.BLACK;
private static final Color C_TXT  = Color.BLACK;
```

## 2. Lưu / nạp layout: file `.clt`

`.clt` là quy ước file layout thật của USE core (`use/use-gui/.../DiagramView.java`,
`ActionSaveLayout.java` — ví dụ `_default.clt`, `_comdia.clt`). Plugin `goal/` không kế
thừa `DiagramView`/`PlaceableNode`/`DirectedGraph` (xem `doc/use-core-design-rules.md`
mục 4), nên không dùng schema XML/DOM thật của USE core — thay vào đó `IStarView` lưu
toạ độ bằng `java.util.Properties` phẳng, chỉ đổi tên phần mở rộng file thành `.clt` để
nhất quán với quy ước đặt tên của USE:

- File: `<tên-file-nguồn>.istar.clt`, nằm cạnh file `.istar`.
- Vì 1 model có 2 chế độ xem (SD/SR, xem mục 9) với toạ độ khác nhau, file `.clt` lưu
  **cả 2 bộ toạ độ** trong cùng 1 file, phân biệt bằng tiền tố khoá:
  `sd.node.<id>.x/y/w/h` và `sr.node.<id>.x/y/w/h`.
- Menu chuột phải: **Save Layout** / **Load Layout** / **Reset Layout** (Reset dựng lại
  toạ độ mặc định bằng `IStarLayoutBuilder`, không đọc file).

## 3–5. Actor / Role / Agent: hình tròn có tên bên trong

`Actor` là abstract trong metamodel — chỉ `Role` và `Agent` là instantiable
(`ActorKind` chỉ còn `ROLE`, `AGENT`; xem mục 8). Cả 3 trường hợp đều vẽ bằng
`drawActorGlyph(...)`: 1 hình tròn, tên actor căn giữa **bên trong** hình tròn (tự động
cắt bớt + thêm `…` nếu tên quá dài để vừa đường kính), và 1 nét riêng phân biệt loại:

| Kind    | Nét phân biệt (bên trong hình tròn)                          |
|---------|---------------------------------------------------------------|
| (chung) | Không có nét thêm — chỉ hình tròn trơn                        |
| Role    | 1 cung tròn (nửa hình tròn) ở **nửa dưới**                    |
| Agent   | 1 đường thẳng kẻ ngang ở **nửa trên**                          |

Glyph này dùng chung cho cả 2 view (SD lẫn SR — xem mục 9), chỉ khác vị trí đặt:

- **SR view**: hình tròn nằm ở mép trên, chính giữa khung boundary (rounded-rect) chứa
  các intentional element mà actor đó sở hữu — boundary tự co giãn theo các phần tử bên
  trong (`recomputeActorBounds`).
- **SD view**: hình tròn đứng độc lập (không có boundary/element nào được vẽ), chỉ nối
  với actor khác qua Dependency.

## 6. Dependency: bắt buộc 3 phần + phần mở rộng tùy chọn

Một dependency **bắt buộc** có đủ 3 phần, đúng ngữ nghĩa iStar 2.0:

- **depender**: actor bị phụ thuộc phải đi qua bên kia (nguồn mũi tên).
- **dependee**: actor mà depender phụ thuộc vào (đích mũi tên).
- **dependum**: intentional element (goal/task/resource/quality) là nội dung phụ thuộc,
  hiển thị trong bong bóng "D" ở giữa cạnh nối.

Cú pháp: `depend Depender[.DependerElmt] -> Dependum -> Dependee[.DependeeElmt]`.
Phần `.DependerElmt` / `.DependeeElmt` là tùy chọn — đây là tính năng "boundary opening"
thật của iStar 2.0 (metamodel: `Dependency.dependerElmt`/`dependeeElmt`, 0..1): cho phép
mũi tên SD nối thẳng vào 1 SR element cụ thể bên trong boundary của actor đó (ví dụ
`Applicant.SubmitApplication -> ... -> Municipality`), thay vì nối vào bản thân actor.
Nếu không khai báo, cạnh nối mặc định vào actor.

> Giới hạn hiện tại: ở chế độ SD (không hiển thị boundary/element), cạnh Dependency vẫn
> luôn nối vào hình tròn actor bất kể có khai báo `.DependerElmt`/`.DependeeElmt` hay
> không — việc vẽ điểm nối "mở" riêng biệt ở SD view là việc cần làm tiếp theo, dữ liệu
> đã được ghi nhận đầy đủ từ AST → MM → layout (`IStarLayoutBuilder` đã ưu tiên nối vào
> `dependerElmt`/`dependeeElmt` khi ở SR view).

## 7. Chọn + kéo-giãn: chỉ áp dụng cho intentional element, không áp dụng cho actor

- Click vào 1 goal/task/resource/quality → **chọn** phần tử đó (vẽ 4 ô vuông nhỏ ở 4 góc
  — resize handle).
- Kéo vào thân phần tử đã chọn → di chuyển; kéo vào 1 trong 4 ô góc → kéo giãn kích
  thước (tối thiểu 44×24px).
- Khung/hình tròn actor **không có resize handle** — chỉ kéo được để di chuyển (cùng với
  toàn bộ phần tử nó sở hữu, ở SR view).

Việc chọn actor bị bỏ qua có chủ ý (`selectedNode` chỉ được gán khi
`n.kind != IStarNodeKind.ACTOR`) — actor không phải là thứ người dùng cần chỉnh kích
thước, kích thước của nó luôn được tính lại tự động từ các phần tử bên trong.

## 8. Metamodel — áp dụng vào `IStar.g4`

Xem [`04-istar-metamodel.drawio`](04-istar-metamodel.drawio) cho toàn bộ class/quan hệ.
`IStar.g4` chỉ khai báo **intentional element** (`goal`/`task`/`resource`/`quality`/
`obstacle`); mọi quan hệ element-to-element được viết **ngay tại chỗ khai báo phần tử
con**, giống cách Java viết `class Foo extends Bar` ngay tại khai báo `Foo` thay vì một
statement riêng ở chỗ khác:

```
rel : '>' relKind? IDENT ;      // '>' đích, keyword phân biệt loại quan hệ

relKind : 'or' | contribType | 'qualifies' | 'needed-by' | 'obstructs' | 'resolves' ;
contribType : 'make' | 'help' | 'hurt' | 'break' ;
```

- **Không có keyword** = AND-refinement (mặc định) — vì đây là quan hệ **duy nhất** cùng
  loại nối cùng loại (`GoalTaskElement` → `GoalTaskElement`). Nhiều phần tử con cùng viết
  `> CùngMộtCha` sẽ được `IStarModelFactory` **gộp lại thành 1** `Refinement.And` duy nhất
  cho cha đó (nhóm theo target, xem `IStarModelFactory.Relations.andChildren`).
- **`or`** = OR-refinement — luôn phải viết rõ, không bao giờ là mặc định.
- `make|help|hurt|break` = contributes (tới 1 quality); `qualifies` = qualifies (từ
  quality tới 1 `ConcreteIntentionalElement` bất kỳ — resource/goal/task/obstacle, không
  chỉ giới hạn goal/task); `needed-by` = needed-by (từ resource tới task); `obstructs`
  (từ obstacle tới goal/task); `resolves` (từ goal/task tới obstacle).
- `goal Id [: Achieve|Maintain|Avoid]` và `obstacle Id [: Prevention|Restoration|
  Mitigation]` — `GoalType`/`ObstacleType` tùy chọn, khớp 2 enum mới trong metamodel.
- `is-a`/`participates-in` (actor-to-actor) và `depend` (SD, cross-actor) **không** nằm
  trong cú pháp `'>'` này — chúng không phải quan hệ giữa các intentional element nên vẫn
  giữ dạng statement riêng như cũ.

Ví dụ (`construction_permit.istar`):
```
agent Municipality {
  goal ApplicationProcessed
  goal ApplicationAssessed > ApplicationProcessed
  goal ApplicationClosed   > ApplicationProcessed
  task Approve > or ApplicationClosed > make  CityBusinessGrowthSupported
  task Deny    > or ApplicationClosed > break CityBusinessGrowthSupported
  quality CityBusinessGrowthSupported
}
```

Tầng MM (`goal/src/main/java/.../istar/mm/`) — **mỗi class trong
`04-istar-metamodel.drawio` ứng với đúng 1 file cùng tên** (20 file, đối chiếu 1-1 để
tự kiểm tra bằng mắt):

| Class trong hình | File | Class trong hình | File |
|---|---|---|---|
| GoalModel | `GoalModel.java` | GoalTaskElement | `GoalTaskElement.java` |
| Actor | `Actor.java` | Goal | `Goal.java` |
| Agent | `Agent.java` | Task | `Task.java` |
| Role | `Role.java` | ContributionType | `ContributionType.java` |
| IntentionalElement | `IntentionalElement.java` | Refinement | `Refinement.java` |
| Dependency | `Dependency.java` | Contribution | `Contribution.java` |
| Resource | `Resource.java` | ConcreteIntentionalElement | `ConcreteIntentionalElement.java` |
| Quality | `Quality.java` | And-Refinement | `AndRefinement.java` |
| Obstacle | `Obstacle.java` | OR-Refinement | `OrRefinement.java` |
| GoalType | `GoalType.java` | ObstacleType | `ObstacleType.java` |

`Actor`/`GoalTaskElement`/`IntentionalElement`/`ConcreteIntentionalElement`/`Refinement`
là `sealed interface` (đúng abstract trong hình); `permits` của mỗi file trỏ sang đúng
các file con ở trên — không còn nested record lồng bên trong lớp cha như bản trước.

Ngoài 20 file trên, `mm/` còn 6 file **không có ô trong hình** vì chúng biểu diễn
**quan hệ** (đường nối), không phải class: `AssocKind`, `Association` (is-a/
participates-in), `NeededBy`, `Qualification`, `Obstruction` (obstacle → goal/task),
`Resolution` (goal/task → obstacle).

Áp dụng cho `IStar.g4`:
- `actorKind : 'role' | 'agent' ;` — bỏ `'actor'` (Actor là abstract, chỉ tồn tại dưới
  dạng `Agent`/`Role` — factory chọn đúng record dựa theo từ khóa khai báo).
- `refines`/`to` chỉ nhắm `GoalTaskElement`; `neededBy` nhắm `Task`; `qualifies` nhắm
  bất kỳ `ConcreteIntentionalElement` nào (rộng hơn `GoalTaskElement`).
- `ContributionType` còn 4 giá trị `MAKE/HELP/HURT/BREAK` (bỏ Unknown/Some+/Some-).
- `is-a` và `participates-in` (2 self-association trên `Actor`) giữ nguyên tên.

## 9. Chuyển đổi SD ⇄ SR

1 cửa sổ `IStarView` duy nhất, không phải 2 action/menu riêng. `IStarView` giữ cả 2
layout đã build sẵn (`srLayout`, `sdLayout`, từ `IStarLayoutBuilder.build(model, mode)`)
song song — chuyển chế độ chỉ đổi con trỏ `mode` đang active, không build lại, nên các
chỉnh sửa layout thủ công (kéo/giãn) ở mỗi chế độ được giữ nguyên khi qua lại giữa 2 view.
Chuột phải → **Switch to SD View** / **Switch to SR View**.

- **SD View**: chỉ vẽ hình tròn actor (mục 3–5) + cạnh Dependency (mục 6). Không có
  boundary, không có intentional element nào được vẽ.
- **SR View**: đầy đủ như hiện tại — boundary actor chứa goal/task/resource/quality +
  refinement (AND/OR) + contribution + qualification + needed-by + dependency (ưu tiên
  nối vào `dependerElmt`/`dependeeElmt` nếu có khai báo).
