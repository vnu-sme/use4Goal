# Context tổng quan dự án `use-goal-BPMN`

Ngày lập: 2026-07-16. Cập nhật gần nhất: 2026-08-16. Tài liệu này tổng hợp lại toàn bộ project sau khi đọc mã
nguồn, tài liệu `doc/`, ví dụ `goal/src/main/resources/examples/`, và lịch sử
thảo luận thiết kế gần nhất (mở rộng Moise → ACL). Mục đích: một điểm khởi đầu
duy nhất để không phải dò lại từ đầu ở phiên làm việc sau.

> **Cách đọc:** mục 1–8 là ảnh chụp lịch sử của project vào ngày 2026-07-16.
> Mục 9 trở đi ghi lại toàn bộ chuỗi quyết định sau đó và là nguồn ưu tiên khi
> có mâu thuẫn với phần cũ. Candidate 3.0 trong các mục mới vẫn là thiết kế thử
> nghiệm, chưa tự động thay thế grammar/compiler 2.0.

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
| iStar 2.0 | `.istar` | `IStar.g4` | `istar` | Goal model type-level: actor(role/agent), goal/task/resource/quality, AND/OR/forall/pick refinement, contribution, dependency (SD) |
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

## 9. Diễn biến thiết kế sau mốc 2026-07-16

Chuỗi thảo luận sau mốc ban đầu đi qua bốn giai đoạn. Các giai đoạn trước vẫn
có giá trị như bằng chứng nghiên cứu, nhưng không phải tất cả đều là kiến trúc
cuối cùng.

### 9.1 Chuẩn hoá ngữ nghĩa ACL và luật ACL → USE

Các tài liệu ngữ nghĩa ACL và bảng luật chuyển được rà soát để thống nhất các
điểm sau:

- enumeration, entity, group và role ở ACL được biểu diễn bằng các cấu trúc
  class tương ứng trong USE; enum ACL phải trở thành USE enum;
- association, aggregation và composition phải giữ đúng loại quan hệ, không
  được gom thành một luật mơ hồ;
- `owner` cũ chỉ có scope group→role hoặc group→group và khi dịch được xem như
  composition; không tồn tại quan hệ đặc biệt “group owner entity”, trường hợp
  cấu trúc group→entity dùng composition thông thường;
- multiplicity quyết định kiểu quan hệ/thuộc tính toàn phần hay bộ phận. Không
  được mặc định mọi attribute đều có thể `undefined`; multiplicity bắt buộc
  phải sinh total function, còn optional mới sinh partial function;
- generalization của entity/group giữ ngữ nghĩa kế thừa kiểu; generalization
  role mang ngữ nghĩa enactment: muốn đóng role con thì subject phải đóng role
  cha. Khi dịch Event-B/USE cần biểu diễn nghĩa này bằng relation/invariant,
  không được đơn giản coi role con là tập con của role cha nếu occurrence role
  có identity độc lập;
- quan hệ role cha–con chỉ hợp lệ khi scope tổ chức của chúng tương thích qua
  chuỗi group ancestor; group graph và role inheritance graph phải vô chu trình;
- `compatible` là policy cho phép cùng agent đảm nhiệm một cặp role trong đúng
  scope tổ chức. Mặc định cặp role không khai báo compatible là không được đồng
  thời đảm nhiệm trong scope đó.

Điểm quan trọng rút ra: tài liệu transformation phải có thứ tự luật rõ ràng,
mỗi luật có ví dụ tích luỹ, và ví dụ cuối phải tạo thành một bản dịch hoàn chỉnh
của Meeting Scheduler thay vì các mảnh rời không phối hợp được.

### 9.2 Thử nghiệm USE và nhận ra giới hạn biểu diễn thời gian

Hướng ACL+iStar+BPMN→USE đã được khảo sát trước. USE phù hợp để biểu diễn class,
object state, association và invariant OCL; SOIL có thể phát lại một kịch bản.
Nhưng một `.use` thuần không tự nó là đặc tả đầy đủ của:

- toàn bộ không gian trạng thái và trạng thái mục tiêu của iStar;
- control-flow, token và thứ tự bắt buộc của BPMN;
- các thuộc tính temporal như dependency/liveness trên mọi execution.

Vì vậy USE vẫn hữu ích cho kiểm tra một concrete checkpoint trace, nhưng không
được tuyên bố là bản dịch hành vi đầy đủ cho mọi execution. Nhận định này dẫn
đến hướng Event-B để mô tả state machine và proof obligations rõ hơn.

### 9.3 Thử nghiệm Event-B theo UML-B, BPMN→Event-B và KAOS→Event-B

Ba nguồn nghiên cứu địa phương được dùng làm nền:

- `latex/paperssssss/core/` và `latex/paperssssss/eventB/` chứa các bản tham
  khảo UML-B, formal BPMN→Event-B và KAOS/iStar-like goal→Event-B;
- class-like declaration được đưa vào context như carrier set/type structure;
- object population, attribute value, link occurrence, control marking và
  transition nằm trong machine;
- BPMN Activity trở thành Event-B event với guard/action;
- goal/task/quality declaration là cấu trúc intentional, còn marking của từng
  instance là runtime state.

Các action dịch được định hướng thành bốn cấu hình độc lập:

1. ACL → Event-B;
2. ACL + BPMN → Event-B;
3. ACL + iStar → Event-B;
4. ACL + BPMN + iStar → Event-B.

Trong thiết kế hợp nhất, ACL phải được dịch trước về mặt phụ thuộc kiểu vì cả
BPMN và iStar dùng Actor/Entity của ACL. Tuy nhiên BPMN và iStar không bắt buộc
dịch tuần tự cho nhau: chúng có thể được biên dịch độc lập vào cùng context và
machine, rồi một bước composition thêm giao thức đồng bộ.

### 9.4 Quyết định đồng bộ BPMN–iStar

Không dùng mapping thủ công kiểu “Activity X cập nhật Goal Y”. Cách đó dễ bỏ
sót goal của quy trình khác hoặc goal cá nhân bị ảnh hưởng gián tiếp.

Quy tắc hiện hành là:

```text
BPMN event thực thi và tạo state Sigma(i+1)
    -> hệ thống chuyển sang trạng thái cần đánh giá
    -> iStar đánh giá lại toàn bộ intentional model trên Sigma(i+1)
    -> tạo một goal snapshot hoàn chỉnh
    -> chỉ khi snapshot hoàn chỉnh mới cho phép BPMN event kế tiếp
```

Có thể hiện thực bằng một event chung `EvaluateGoals` và biến pha, hoặc ghép
atomic phần đánh giá vào từng BPMN event. Phương án event chung dễ chứng minh
và tránh nhân bản action; invariant/guard pha phải bảo đảm không có hai BPMN
transition liên tiếp mà thiếu bước đánh giá.

## 10. Phân tầng M2–M1–M0 là nguyên tắc thiết kế bắt buộc

Các tranh luận về `Actor`, `Group`, `Role`, `Agent` không thể giải quyết chỉ từ
sơ đồ metamodel. Quy trình đã thống nhất là đi bottom-up:

1. **M0 — instance/runtime:** liệt kê identity thật, giá trị và link ở từng
   checkpoint; xác định điều gì tồn tại bền vững và điều gì thay đổi;
2. **M1 — model/domain declaration:** tìm các type, property, relationship,
   intention và process cần để sinh đúng các instance M0 đó;
3. **M2 — metamodel/Ecore:** chỉ giữ những metaclass và invariant cần thiết để
   mô tả nhất quán các model M1 đã được kiểm nghiệm.

Ví dụ M0 cốt lõi của Meeting Scheduler:

| Identity | Ý nghĩa | Có chủ đích | State thay đổi |
|---|---|---:|---:|
| `alice` | cá nhân/software agent cụ thể | có | có |
| `unit1` | occurrence của tổ chức/tập thể | có | có |
| `organizer1` | role slot/trách nhiệm trong `unit1` | có | có |
| `meeting1` | đối tượng miền thụ động | không | có |

`organizer1` phải tồn tại độc lập với Alice. Khi Carol thay Alice đảm nhiệm
Organizer, identity của role slot và goal gắn với slot không đổi; chỉ enactment
link thay đổi. Đây là lý do không thể đồng nhất agent occurrence với role.

## 11. Candidate ACL state-aware 3.0

Candidate hiện nằm riêng trong
`goal/model/development/acl-state.ecore`, namespace
`https://vnu.edu.vn/sme/goal/acl/state/3.0`. Nó chưa thay thế
`goal/model/acl.ecore` hay Java compiler hiện hành.

### 11.1 Khái niệm M2/M1

```text
Classifier
├── DataType
│   ├── PrimitiveType
│   └── Enumeration
└── Class
    ├── Actor
    └── Entity
```

- `Actor` là type cho mọi occurrence có thể làm chủ thể intentional;
- `ActorKind = INDIVIDUAL | COLLECTIVE | ROLE` giữ ba cách dùng khác nhau mà
  không tạo ba cây metaclass trùng lặp;
- `Entity` là type cho object có identity/state nhưng không có ý định;
- `Attribute` khai báo state slot tại M1, còn giá trị nằm trong snapshot M0;
- `Association`, `Aggregation`, `Composition` vẫn tách riêng vì lifecycle của
  link khác nhau;
- `Owner` bị bỏ. Composition diễn tả structure/lifecycle, ví dụ
  collective→role, collective→collective và actor→entity;
- `Compatibility` vẫn riêng vì đây là policy cấp type có scope collective,
  không phải một runtime structural link.

Composition không tự động truyền goal. Collective con chỉ đóng góp cho goal
collective cha khi iStar khai báo refinement hoặc dependency tương ứng.

### 11.2 Khái niệm runtime M0

- `SystemHistory`: model state history của một ACL schema;
- `RuntimeInstance`: identity ổn định qua nhiều checkpoint;
- `ActorOccurrence`: runtime instance typed bởi Actor;
- `EntityObject`: runtime instance typed bởi Entity;
- `SystemState`: immutable checkpoint `Sigma_i = (O_i, V_i, L_i)`;
- `AttributeValue`: giá trị của một attribute trên một identity tại một state;
- `LinkOccurrence`: instance của relationship M1 tại một state.

Tách identity khỏi snapshot là điều kiện để so sánh goal marking theo thời gian
mà không sửa ngược state cũ.

### 11.3 Invariant chính

- tên classifier và relationship duy nhất trong model;
- Actor generalization chỉ nối Actor cùng kind; Entity chỉ generalize Entity;
- composition có Actor ở phía part phải bắt đầu từ collective Actor;
- Entity không được composition-contain Actor;
- composition graph vô chu trình;
- occurrence phải được typed đúng loại và thuộc đúng ACL model;
- mọi value/link trong state chỉ dùng instance đang active;
- mỗi cặp `(instance, attribute)` có tối đa một value trong một state.

## 12. Candidate iStar state-aware 3.0

Candidate nằm trong `goal/model/development/istar-state.ecore`, namespace
`https://vnu.edu.vn/sme/goal/istar/state/3.0`. Obstacle đã bị loại khỏi iStar
hiện hành theo quyết định thiết kế.

### 12.1 Không sao chép Actor của ACL

`ActorView` tham chiếu trực tiếp `ACL::Actor`. Vì vậy:

- Actor kind `INDIVIDUAL` sở hữu goal nội tại của cá nhân/software agent;
- Actor kind `ROLE` sở hữu trách nhiệm của role occurrence;
- Actor kind `COLLECTIVE` sở hữu goal chung của tổ chức/group occurrence.

BPMN performer/lane về sau cũng phải tham chiếu Actor này. Trùng tên không được
coi là một phép mapping hợp lệ.

### 12.2 Intentional specification

- `Goal`: desired state với `activation`, `condition` và `GoalType`;
- `Task`: intended action contract với `precondition`, `postcondition`;
- `Quality`: intentional quality có thể có state predicate và contribution;
- `Resource`: thứ actor cần, khác ACL EntityObject là object miền thực tế;
- `StatePredicate`: OCL được đánh giá với `self`, `outer`, `state`;
- AND/OR/FORALL/PICK refinement tạo cấu trúc và phép tổng hợp marking;
- Dependency composition-own một `IntentionalElement` cụ thể làm dependum.

Dependency không có `DependumKind`: subtype của dependum đã cho biết nó là
Goal, Task, Quality hay Resource. Dependum là một element riêng được chứa bởi
Dependency, không phải tham chiếu lỏng đến một declaration đã có ở actor khác.

### 12.3 Goal runtime

- `GoalTrace` nối một GoalModel với đúng một ACL SystemHistory;
- `GoalSnapshot` tham chiếu một SystemState và chứa kết quả đánh giá tại state;
- `IntentionalMarking` có khóa `(element, subject, outer?)`;
- status gồm `INACTIVE`, `PENDING`, `SATISFIED`, `VIOLATED`;
- `complete = true` chỉ khi mọi intention/subject applicable đã được đánh giá.

Ba ví dụ marking khác nhau dù có thể liên quan cùng một người:

```text
AvoidOvertime(alice)                  -- goal nội tại cá nhân
HaveMeetingScheduled(organizer1)      -- trách nhiệm role slot
HaveMeetingOrganized(unit1)           -- goal tập thể
```

## 13. Ví dụ phát triển Meeting Scheduler

Bộ ví dụ state-aware tích luỹ hiện có:

- `goal/model/development/mtg-development.acl.xmi`: model ACL M1;
- `goal/model/development/mtg-development.istar.xmi`: model iStar M1 tham chiếu
  Actor từ ACL thay vì khai báo actor mới;
- `goal/model/development/mtg-development.state.xmi`: identity M0 và hai state
  `s0`, `s1`;
- `goal/model/development/mtg-development.goaltrace.xmi`: full re-evaluation
  trên hai state;
- các biến thể text lớn hơn ở
  `goal/src/main/resources/examples/mtg/mtg_gstar.{acl,istar,bpmn2}` dùng để
  khảo sát case study gần gStar.

Ví dụ phải chứa đủ ít nhất ba lớp ý định: goal cá nhân, goal role và goal
collective. Một tình huống kiểm tra quan trọng là Alice hoàn thành activity tổ
chức cuộc họp nhưng số giờ làm tăng quá ngưỡng: BPMN goal của meeting có thể
đạt trong khi `AvoidOvertime(alice)` chuyển sang violated. Full re-evaluation
phải phát hiện điều này dù không có Activity→AvoidOvertime mapping.

## 14. Ecore/Eclipse và validation

Ecore được dùng để kiểm nghiệm M2 và XMI để kiểm nghiệm M1/M0 trước khi sửa
grammar Java. Các invariant ngữ nghĩa phải nằm trong Ecore/OCL delegate để
Eclipse EMF Validation có thể báo lỗi, không chỉ tồn tại trong
`GoalModelValidator.java`.

Trạng thái kỹ thuật đã đạt ở candidate development:

- các file Ecore/XMI well-formed;
- EMF có thể load model;
- OCL delegates được khai báo;
- sample XMI không có unresolved proxy hoặc diagnostic error trong lần kiểm
  tra gần nhất.

Sirius `.aird` chỉ là representation/session, không phải model dữ liệu. Ecore
diagram dùng để chỉnh M2. Muốn có sơ đồ kéo-thả riêng cho instance XMI M1/M0
cần tạo Sirius viewpoint/representation cho metamodel đó; Sample Ecore Model
Editor mặc định chủ yếu là tree editor.

## 15. Vai trò hiện hành của ACL, iStar và BPMN

| Ngôn ngữ | Trách nhiệm duy nhất | Không được làm |
|---|---|---|
| ACL | định nghĩa Actor/Entity, structure, runtime identity và system state | tự suy ra goal hoặc process order |
| iStar | khai báo intention và đánh giá toàn bộ goal tree trên mỗi ACL state | tự sửa domain state, sao chép Actor ACL |
| BPMN | định nghĩa control-flow và transition làm thay đổi ACL state | tự quyết định goal nào cần cập nhật |

Kiến trúc đồng bộ mục tiêu:

```text
ACL schema + initial identities/state
              |
              v
BPMN event  -> new immutable ACL state
              |
              v
       full iStar evaluation
              |
              v
 complete GoalSnapshot -> BPMN event tiếp theo
```

Mô hình này cho phép một process ảnh hưởng goal của process khác, goal cá nhân
hoặc goal tập thể một cách khách quan vì tất cả đều đọc cùng ACL state.

## 16. Những quyết định chưa đóng băng

1. `ActorKind` trên một EClass Actor hay ba subclass riêng vẫn cần được đánh
   giá thêm bằng nhiều case M0; candidate hiện dùng một EClass + enum.
2. Cú pháp `.acl` và `.istar` 3.0 chưa được chốt; không migration compiler chỉ
   vì Ecore load được.
3. BPMN state-aware metamodel cần được điều chỉnh để performer tham chiếu Actor
   ACL và transition tạo SystemState mới đúng semantics.
4. Cần xác định đầy đủ temporal semantics cho `ACHIEVE`, `MAINTAIN`, `SUSTAIN`,
   `RECUR`, đặc biệt khi activation bật/tắt qua nhiều checkpoint.
5. Propagation chi tiết của contribution và dependency cần invariant/rule có
   thể kiểm chứng, không chỉ mô tả bằng prose.
6. Event-B translator và Rodin proof chỉ nên được coi là ổn định sau khi M0/M1/
   M2 state-aware được chấp nhận và các proof obligation phân biệt được lỗi bộ
   dịch với lỗi ngữ nghĩa model.

## 17. Tài liệu nguồn ưu tiên cho công việc tiếp theo

### Candidate 3.0

- `goal/model/development/README.md`
- `goal/model/development/acl-state.ecore`
- `goal/model/development/istar-state.ecore`
- `goal/docs/semantics/development/acl-state-model.md`
- `goal/docs/semantics/development/istar-state-model.md`
- `goal/docs/semantics/development/mtg-stateful-example.md`

### Ngữ nghĩa và transformation hiện hành

- `goal/docs/formal/acl.md`, `goal/docs/semantics/dsl/acl.md`
- `goal/docs/formal/istar.md`, `goal/docs/semantics/dsl/istar.md`
- `goal/docs/semantics/transformations/acl2eventb.md`
- `goal/docs/semantics/transformations/istar2eventb.md`
- `goal/docs/semantics/transformations/bpmn2eventb.md`

### Nguyên tắc tiếp tục

Không sửa canonical Ecore, grammar, compiler và transformation đồng loạt cho
đến khi một candidate vượt qua đủ ví dụ M0. Khi bắt đầu migration phải làm theo
thứ tự: chốt M0 → chốt M1 → chốt M2/OCL invariant → cập nhật tài liệu → grammar
và Java model → parser/validator/view → transformation → test và Rodin/USE
verification.
