# Context tổng quan dự án `use-goal-BPMN`

Ngày lập: 2026-07-16. Tài liệu này tổng hợp lại toàn bộ project sau khi đọc mã
nguồn, tài liệu `doc/`, ví dụ `goal/src/main/resources/examples/`, và lịch sử
thảo luận thiết kế gần nhất (mở rộng Moise → ACL). Mục đích: một điểm khởi đầu
duy nhất để không phải dò lại từ đầu ở phiên làm việc sau.

## 1. Dự án này là gì

`use-goal-BPMN` là một **plugin cho USE** (UML-based Specification Environment
— công cụ đặc tả/kiểm chứng hình thức dựa trên UML class diagram + OCL của
nhóm TZI, Bremen). Plugin bổ sung cho USE một họ ngôn ngữ DSL riêng để mô hình
hoá goal-oriented requirements (i*), business process (BPMN2), tổ chức đa tác
tử (ACL, dựa trên Moise), và compliance graph (DCR) — rồi bắc cầu các mô hình
đó sang chính cơ chế class-diagram + OCL + soil-script gốc của USE để kiểm
chứng hình thức. Đây là dự án nghiên cứu/luận văn (VNU-SME), không phải sản
phẩm thương mại.

Chủ đề nghiên cứu trung tâm xuyên suốt: **kiểm tra tính nhất quán (conformance)
giữa một goal model (i*) và một process model (BPMN2)** — dựa trên lý thuyết
"scenario synchronization" (JUCS, Dang/Truong/Gogolla) và "goal-model-based
compliance checking" (Caballero-Villalobos & Baumeister). ACL (ngôn ngữ tổ
chức) là một hướng mở rộng mới hơn, đang phát triển, nhằm thêm tầng cấu trúc
tổ chức + state/concept mà i*/BPMN2 chưa mô tả tốt.

## 2. Cấu trúc repo

Multi-module Maven (`pom.xml` gốc, packaging `pom`, `groupId org.tzi.use`,
Java 21):

```
use/               <- git submodule (.gitmodules), vendor hoá chính USE tool
  use-core/         (lõi USE: MModel, OCLCompiler, SoilCompiler, MSystem, ANTLR3 grammar)
  use-gui/          (Swing GUI framework của USE, plugin API)
  use-assembly/     (đóng gói bản chạy được, nơi thả plugin .jar vào lib/plugins)
goal/               <- module chính của project này ("Goal Plugin", artifactId `goal`)
  src/main/java/org/vnu/sme/goal/...
  src/main/resources/grammars/*.g4      (ANTLR4 4.9.3)
  src/main/resources/examples/...
  src/main/resources/useplugin.xml      (đăng ký action với USE)
```

`goal` build ra shaded jar, copy/symlink vào `use/use-assembly/target/use*/lib/plugins`
để USE nạp lúc chạy (`use/README.md` có "SME Notes" ghi rõ quy trình build/dev).

## 3. Các ngôn ngữ DSL trong `goal` — bảng tổng hợp

Mỗi ngôn ngữ đều theo đúng một khuôn kiến trúc lớp (giống hệt nhau, xem
`use-plugin-skill`): `grammars/X.g4` → `ast/` (Concrete Syntax, giữ sát cú
pháp gốc) → `parser/` (XCompiler + XBuildingVisitor + XModelFactory, build
+ validate semantic) → `mm/` (metamodel runtime, Java record thuần) →
`view/` (layout + diagram) → `gui/XForm` (Swing dialog) → `action/ActionOpenX`
(đăng ký trong `useplugin.xml`).

| Ngôn ngữ | File nguồn | Grammar | Package | Vai trò |
|---|---|---|---|---|
| iStar 2.0 | `.istar` | `IStar.g4` | `istar` | Goal model type-level: actor(role/agent), goal/task/resource/quality/obstacle, AND/OR/order/guard refinement, contribution, dependency (SD) |
| i* Scenario | `.iscn` | `IStarScenario.g4` | `iscn` (+ `iscn.goalmodelinstance`) | Instance-level snapshot trên 1 `.istar`: `instance`, `fire`, `assign`, `aggregate all/any` |
| BPMN2 | `.bpmn2` | `Bpmn2.g4` | `bpmn2` | Process model type-level: pool/lane, task, event, gateway (xor/and/or/event-based), flow, message |
| BPMN Scenario | `.bscn` | `Bpmn2Scenario.g4` | `bpmn2scenario` | Instance-level trên 1 `.bpmn2`: `bind`, `fire/completed/active`, `token`, `assert` |
| ACL v2.0 | `.acl` | `ACL.g4` | `acl` | Tổ chức đa tác tử (Moise mở rộng) + entity/state — **xem mục 6** |
| DCR | `.dcr` | `DCR.g4` | `dcr` | Dynamic Condition Response graph: event, marking, condition/response/include/exclude/milestone. Hiện **chưa có** lớp scenario/instance riêng, chỉ là viewer |
| Conformance | (dùng `.map` + 1 `.istar` + 1 `.bpmn2`) | — | `conformance` | Kiểm tra compliance i* ↔ BPMN2 qua product LTS (xem mục 5) |
| i*+USE bridge | (dùng `.istar` + `.use` + `.soil`) | — | `istarusebridge` | Thay `.iscn` bằng chính object thật trong `.use`/`.soil` — xem mục 4 |

Đăng ký trong `useplugin.xml` (plugin "GoalModel Plugin" v3.0, target USE
7.1.1): 8 action — iStar Viewer, i* Scenario Viewer, i*+USE Scenario Viewer,
ACL Viewer, BPMN2 Viewer, BPMN Scenario Viewer, DCR Viewer, i*/BPMN2
Conformance Checker.

Ví dụ đầy đủ nhất, dùng chung mọi ngôn ngữ trên cùng 1 domain (họp — meeting
scheduling): `goal/src/main/resources/examples/mtg/` — có đủ
`.acl/.istar/.iscn/.bpmn2/.bscn/.use/.soil/.map`-tương-đương.

## 4. Nguyên tắc thiết kế xuyên suốt: Domain model (type) vs Scenario (instance)

Đây là quy ước lặp lại nhất quán ở **mọi** ngôn ngữ trong project, tài liệu ở
`doc/istarDesign/concept-domain-model-vs-scenario.md`:

- **Domain model** (`.istar`, `.bpmn2`, `.acl`, `.use`) = type-level: định
  nghĩa loại thực thể nào tồn tại, quan hệ cấu trúc, luật luôn đúng
  (invariant). Không nhắc instance cụ thể, không có "trước/sau".
- **Scenario** (`.iscn`, `.bscn`, `.soil`) = instance-level: một **snapshot**
  cụ thể — khai instance, gán trạng thái (trực tiếp `assign`/`value`, hoặc
  gián tiếp qua `fire`/`token` rồi suy luận bằng propagation/saturation đến
  fixpoint), rồi khẳng định điều kiện (`aggregate`/`assert`). Scenario
  **không bắt buộc** là trace đầy đủ từ đầu đến cuối — có thể là trạng thái
  dang dở, miễn nhất quán với domain model.
- Tương tự UML class diagram ↔ object diagram, hoặc USE `.use` ↔ `.soil`.

`istarusebridge` là biến thể thú vị của nguyên tắc này: thay vì viết `.iscn`
(scenario DSL riêng), nó lấy chính `.use` (structure thật) + `.soil` (script
thật) làm scenario, rồi tại **mỗi dòng soil** dựng 1 checkpoint và evaluate
guard OCL của từng Goal/Task trong `.istar` lên đúng những object USE thật đó
(`IStarOclConstraintCompiler` compile OCL qua `Symtable`/`MModel` thật của
USE, `IStarUseTraceCompiler` replay `.soil` qua `SoilCompiler`/`MSystem` thật,
`IStarUseTraceEvaluator` dựng 1 `GoalModel` instance-level/checkpoint rồi tái
dùng chung bộ máy AND/OR/contribution saturation `IStarPropagation` mà
`iscn`/`conformance` cũng dùng).

`dcr` là ngoại lệ: chưa có tầng scenario/instance riêng.

## 5. Conformance checking (i* ↔ BPMN2) — nền lý thuyết

Package `conformance` hiện thực hoá lý thuyết trong `doc/paper/`:

- `JUCS.md` — Dang, Truong, Gogolla: "Checking the Conformance between Models
  Based on Scenario Synchronization".
- `alig.md` — Caballero-Villalobos & Baumeister: "Aligning processes with
  high-level requirements: Goal-model-based compliance checking" (định nghĩa
  DCR process P, marking, LTS, product automaton).
- `dcr.md` — López/Debois/Slaats/Hildebrandt: "Business Process Compliance
  using Reference Models of Law" (nền DCR graph gốc).
- `vaDL.md` — Gröner et al: "Validation of user intentions in process
  orchestration and choreography".
- `conformance-istar-bpmn2.md` + `consistency-checking-methods-survey.md` —
  tổng hợp riêng của project, khảo sát method + thiết kế API cụ thể cho
  project này (lưu ý file `alig.md`/`vaDL.md` bị đặt tên ngược so với suy
  đoán ban đầu — đã ghi chú lại trong chính file đó).

Pipeline code: `mapping/ConformanceMapping` (đọc `.map`, nối element BPMN ↔
element i*) → `semantics/Bpmn2LtsBuilder` (BPMN → LTS) +
`semantics/IStarPropagation` (bộ luật saturation AND/OR/contribution dùng
chung toàn project) → `semantics/ProductLts` (LTS × i*-marking) →
`semantics/ComplianceChecker` (BFS forward/backward, thuật toán weak/strong
compliance + stability) → `action/ActionCheckConformance` hiển thị kết quả.
Diagram tham chiếu lý thuyết này: `doc/drawio/07-state-conceptmodel.drawio`
(GoalModel/IStarMarking/ProcessMarking/ProductLts/ProductState/Transition/
ConformanceMapping).

## 6. ACL — ngôn ngữ tổ chức (Moise mở rộng)

### 6.1 Động lực

Moise mô tả rất tốt quan hệ **cấu trúc tổ chức** (role, group, link:
authority/communication/acquaintance/compatibility) nhưng yếu ở tầng dữ liệu.
Quyết định thiết kế trong project này: lấy Moise làm nền, mở rộng thêm khái
niệm **entity/concept** (kiểu class/UML) và **invariant OCL** để mô tả tại một
thời điểm, tổ chức có cấu trúc gì và hệ thống ở trạng thái nào — tách bạch rõ
với i* (i* lo goal/task/dependency, đã có sẵn semantics propagation riêng).
**Quyết định đã chốt: không thêm norm/mission/deadline (Normative/Functional
Specification của Moise) vào ACL — vì i* + `ocl {[...]}` guard trên task đã
đóng đúng vai trò đó.**

### 6.2 Grammar (`ACL.g4`)

`model : 'acl' VERSION? IDENT '{' decl* '}'`, với `decl` gồm: `enum`,
`entity` (name + attribute), `agent|role|abstract role` (actor, có
`specializes`, attribute), `relationship`/`partOf` (n-ary qua `endpoint: type
multiplicity roleName`), `group` (attribute + `groupMember: type
multiplicity`, có `specializes`), `link` (kind
authority/communication/acquaintance/compatibility + `intra/inter` scope),
`invariant ... context ... ocl {[ ... ]}` (OCL nguyên văn, không parse ở tầng
ACL, để USE tự parse sau).

Metamodel đầy đủ (class diagram của chính ACL, không phải model .acl cụ thể)
đã được vẽ ở **`doc/drawio/01-moise*.drawio` — Page-2 "ACL Metamodel"** (thêm
trong phiên làm việc này), đối chiếu chính xác với `acl/mm/*.java`. Page-1
cùng file là Moise gốc chưa mở rộng (Group/Role/Entity/Link/LinkType).

### 6.3 Kiến trúc code

`acl/parser/` (`AclCompiler` chỉ dừng ở parse + build `AclModel`, **KHÔNG có
bước sinh `.use`** — mọi file `.use` gắn nhãn "Generated from ACL" trong
`examples/` hiện tại là **viết tay**, không phải thật sự generate được) →
`acl/mm/` (record thuần: `AclModel`, `AclEntity`, `AclActor`, `AclRelation`,
`AclEndpoint`, `AclGroup`, `AclGroupMember`, `AclLink`, `AclInvariant`,
`AclEnum`, `AclAttribute`) → `acl/view/` (diagram) → `acl/gui/AclForm` →
`acl/action/ActionOpenAcl`.

### 6.4 Hạn chế đã xác định (đã thảo luận kỹ, kết luận cuối cùng)

Tách 2 nhóm rõ ràng:

**Nhóm A — nợ kỹ thuật engine/grammar, vá dễ, không phải giới hạn ngữ nghĩa:**
1. Group lồng group (nested subgroup, kiểu CLB chứa Team) — **hiện fail** vì
   `AclModelFactory.validate()` giới hạn `endpointTypes` (dùng để check cả
   group-member lẫn relationship/partOf endpoint) chỉ gồm
   entity+actor, không có group. Grammar tự nó không cấm — chỉ validator quá
   chặt tay.
2. `entityDecl` không có `specializes` (khác `actorDecl`/`groupDecl`) → entity
   không kế thừa được.
3. `relationshipDecl`/`partOfDecl` không cho `attributeBlock` → không biểu
   diễn được quan hệ mang dữ liệu riêng (kiểu associationclass, thứ mà `.use`
   target đã hỗ trợ sẵn).

**Nhóm B — hạn chế ngữ nghĩa thật, đã thu hẹp lại đúng phạm vi phân tích
(type-level), không lẫn với instance/execution:**
- `actorDecl` sinh mỗi role thành 1 class cô lập, **thiếu association ngược**
  về (a) group sở hữu và (b) một khái niệm định danh dùng chung giữa các
  role-occupation. Hệ quả: chính những ràng buộc ACL tự khai báo —
  `Coordinator [1..1]` (cardinality trong group) và `link compatibility`
  (loại trừ chéo-role) — **không viết được thành OCL invariant kiểm chứng
  được**, dù hoàn toàn ở tầng type/phân tích, không liên quan instance nào.
  Đây là điểm mở duy nhất còn lại cần giải quyết ở ACL; **chưa thiết kế giải
  pháp cụ thể, đang chờ quyết định tiếp theo.**

Bằng chứng cụ thể dùng khi thảo luận: so sánh
`examples/incident_response_acl/incident_response.acl` (role phẳng) với
`incident_response_shadow1.use` (viết tay, phải bịa thêm `Person`/`Incident`/
`associationclass Role between Incident,Person` mới đủ biểu diễn) — và xác
nhận `mtg_shadow.use` cũng đã âm thầm làm y hệt vậy từ trước (comment giải
thích rõ lý do). Người dùng xác nhận các file `.use` này **chỉ là thử nghiệm
cá nhân**, hướng chính thức là dùng ACL + tự viết compiler ACL→USE sau.

## 7. Bản đồ tài liệu `doc/`

- `doc/drawio/` — metamodel chính thức dạng class diagram: `01-moise*.drawio`
  (Moise gốc + ACL Metamodel), `04-istar-metamodel.drawio` (nhiều page: SR
  diagram, quantified istar, goal-model-instance, iscn scenario action, USE
  metamodel tham chiếu, và 1 page ACL cũ/lỗi thời "acl-metamodel-page-10" —
  **đã lỗi thời, đừng dùng làm nguồn**, tham khảo Page-2 của `01-moise*.drawio`
  thay thế), `05-bpmn2-metamodel.drawio`, `06-dcr-metamodel.drawio`,
  `07-state-conceptmodel.drawio` (lý thuyết conformance), `08-conceptual-data-metamodel.drawio`
  (metamodel dữ liệu khái niệm tham khảo từ lý thuyết nền — Entity/Attribute/
  Relationship/Role/Domain/Rule/Event/Action, không phải ACL implementation).
- `doc/projectArchitecture/` — 3 drawio: project overview, current pipeline
  architecture, target pipeline architecture (mới dời từ `doc/drawio/`, các
  file cũ cùng tên ở `doc/drawio/` đã bị xoá).
- `doc/istarDesign/` — `concept-domain-model-vs-scenario.md` (nguyên tắc mục
  4), `istar-scenario-action-architecture.md` (kiến trúc `.istar`/`.iscn` chi
  tiết), `istar-notation.md` (quy ước vẽ `IStarView`).
- `doc/scenario/` — `init.md`/`init2.md` (kịch bản họp mtg song song 2 phía
  BPMN vs i*), `istar-scenario-diagram-method.md` (cách vẽ iStar scenario
  diagram cho 1 kịch bản cụ thể), 3 drawio kịch bản mtg.
- `doc/paper/` — 4 bài báo nền lý thuyết (JUCS, alig, dcr, vaDL — full text
  converted, ~900-3300 dòng mỗi bài) + 2 tổng hợp riêng của project
  (conformance-istar-bpmn2.md, consistency-checking-methods-survey.md, có đề
  xuất 11... phương pháp mới — xem file để biết chi tiết).
- `doc/bpmnSource/` — `bpmn.ecore` + `bpmn.drawio` (nguồn tham chiếu metamodel
  BPMN chuẩn Eclipse, dùng khi thiết kế `Bpmn2.g4`).
- `doc/contexts/` — chính file này.
- `metamodel.md` (root) — **tài liệu về MAXGoal, ngôn ngữ tiền nhiệm đã bị xoá
  khỏi code** (xem commit "Remove legacy MAXGoal/MAXBpmn code, keep iStar 2.0
  + BPMN2") — giữ lại chỉ để tham khảo lịch sử, không phản ánh code hiện tại.

## 8. Trạng thái hiện tại (nhánh `quyen`)

Theo git status/log lúc lập tài liệu này: đang tập trung sửa
`istarusebridge` (`IStarOclConstraintCompiler`, `IStarUseTraceCompiler`,
`IStarUseTraceDemoMain`, `IStarUseTraceEvaluator`, `view/IStarUseTraceView`),
thêm mới package `acl` hoàn chỉnh + ví dụ `examples/acl/`,
`examples/incident_response_acl/`, `examples/incident_response_bridge/`, và
cập nhật `mtg.*` (`iscn`, `soil`, `dlt`) theo cùng. Commit gần nhất: "update
example" (47afba1), "fix alothrom for transform model" (dba1355), "validator
parse istar" (f6cb9ab). `doc/drawio/01-moise*.drawio`, `04-istar-metamodel.drawio`,
`05-bpmn2-metamodel.drawio` đang có thay đổi chưa commit.

Việc còn mở, chưa quyết định thiết kế cụ thể (xem mục 6.4 nhóm B): cách thêm
association ngược role→group và role→identity-dùng-chung vào `ACL.g4`/`acl/mm`
để cardinality và compatibility constraint viết được OCL. Chưa có compiler
ACL→`.use` chính thức — đây là việc người dùng dự định tự làm ("tôi cũng sẽ có
phương pháp để dịch từ acl sang use").
