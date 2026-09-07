# Mapping BPMN ↔ i* — Sales Forecast (theo ngữ nghĩa)

Nguồn: `process-sales-forecast.bpmn` ↔ `goal-sales-forecast.txt` (bản đã sửa cho đúng chuẩn i*/BPMN).
Bản gốc chưa sửa: `process-sales-forecast-default.bpmn`, `goal-sales-forecast-default.txt`.

## Quan hệ ngữ nghĩa dùng trong bảng

| Quan hệ | Ý nghĩa |
| --- | --- |
| **realizes** | Hoàn tất activity BPMN này = chính là lúc i* Task/Goal/dependency trở thành true. |
| **enables** | Nhánh/quyết định BPMN là điều kiện để i* Task/Goal *được xét tới*, chưa chắc đã thoả. |
| **operationalizes** | i* Task tả HOW ở mức trừu tượng; activity BPMN là cách cụ thể thực hiện HOW đó. |
| **may-violate** | Đến điểm này trong BPMN **không đảm bảo** i* Goal/Quality tương ứng đã thoả. |

## Bảng mapping — Customer Manager (nhánh chính)

| BPMN | Quan hệ | i* (actor) | Vì sao |
| --- | --- | --- | --- |
| *Start* "Deal opportunity" | enables | `Create and insert price proposal into the system` (Customer Manager) | Có cơ hội bán hàng là tiền đề để actor bắt đầu theo đuổi `Promote sales according to targets`. |
| `New customer?` → `Updating customer details` / `Creating Customer Ticket` | *(không có counterpart)* | — | Nghiệp vụ quản lý hồ sơ khách hàng, i* không mô hình hoá ở mức này — gap thật, không phải lỗi mapping. |
| `Inserting and updating deal details` | enables | `Create and insert price proposal into the system` (Customer Manager) | Bước khởi tạo dữ liệu deal — chưa đủ để goal `Promote sales according to targets` thoả, chỉ mở đầu chuỗi hoạt động AND-refine goal đó. |
| `Required Presale Engineer?` — **Yes** | enables | `Develop solutions for customers` (Presale Department) | Kích hoạt nhánh Presale — dependency `Get solution characterzation`/`Getting BoM and HLD` (CM → Presale) được gọi. |
| `Creating BOM` / `Writing HLD Document` | operationalizes | `Creating BoM includes PNs` / `Creating of HLD document` (Presale Department) | Cùng loại hoạt động kỹ thuật, đúng actor. |
| `Sending HLD to Customer Manager` | **realizes** | dependency `Getting BoM and HLD` (CM ← Presale Dept) | Gửi xong = dependum được thoả — quan hệ 1-1 chính xác dù không actor/goal nào tên "sending". |
| `There is vendor BOM?` → `Creating a BOM manually` / `Updating BOM and HLD` | operationalizes | `Build part numbers` (Customer Manager) | Cả 2 nhánh cùng đổ vào một Task i* (kết quả giống nhau về mặt ý định: có BOM/PN sẵn sàng), khác nhau ở cách làm — BPMN phân biệt "manual vs vendor", i* không cần phân biệt vì cùng một trạng thái đích. |
| `Missing PN?` → `Creating PNs` / `Updating PNs Price` | operationalizes | `Build part numbers` (Customer Manager) | Cùng lý do như trên. |
| `Updating percentages for service contract` | **realizes** | dependency `Add Service Agreement` (CM → Service Agreements Dept) | Hoạt động này chính là điểm kích hoạt dependency. |
| `Pending approval` / `Contract review and approval` (Service Agreements) | operationalizes | `Ensure that price proposal includes a service agreement` (Service Agreements Dept) | Đúng actor, đúng mục tiêu. |
| `Approved? = No` (service agreement) | enables (loop) | *(không falsify goal)* | Quay lại `Updating percentages…`, goal service-agreement vẫn "đang treo", chưa fail. |
| `Updating deal profitability and closure percentages` | enables | `Complete the mandatory price proposal elements` (Customer Manager) | Chuẩn bị dữ liệu để tiến vào vòng duyệt — chưa làm goal nào thoả. |
| `Profitability > 12%` | enables | `Request approval` → `Get Approval for price proposal to progress` (Customer Manager) | Bỏ qua toàn bộ chuỗi Dept/Sector/VP Manager — đi thẳng vào yêu cầu approval. |
| `Profitability < 12%` → Dept/Sector/VP Manager (Pending approval → examination → Approved?) | operationalizes | `Control and check the profitability` / `Approve price proposal` (Approvers, *is-a*: Director of Sales Sector, Sales Dept Manager, VP Business Division) | 3 lane BPMN cùng hiện thực hoá 1 cụm Task của actor `Approvers` — is-a generalize đúng. |
| `Approved? = No` (bất kỳ cấp nào) | enables (loop) | *(không falsify goal, chỉ trì hoãn)* | Quay lại `Updating deal profitability…`. |
| `>250,000 NIS?` — **Yes** → Financial Control (Pending approval → examination → Approved?) | **may-violate / no counterpart** | — | ⚠️ Không actor "Financial Control" nào tồn tại trong i* — nhánh BPMN này có thể chạy hoàn chỉnh mà **không có bất kỳ i* Goal nào ghi nhận việc đó xảy ra**. Đây là lỗ hổng thật, không phải do cách mapping. |
| `Approved? = Yes` (Credit Control) → `Deal approved` | **realizes** | `Get Approval for price proposal to progress` (Customer Manager) | Approve xong = goal thoả — dù `Credit Control` (actor i*, is-a Approvers) không có Task/Goal riêng để "operationalize" bước kiểm tra tín dụng đó. |
| `Completion of kit components` | **realizes** | `Complete kit components` (Customer Manager) | Tên hai bên tình cờ gần giống (cùng là "activity" nên được phép giống), nhưng mấu chốt là **thời điểm**: hoạt động này chạy *sau* khi `Get Approval…` đã `realizes` — xem Case A trong mục OCL. |
| `Transferring to order operator` | **realizes** | `Transfer price proposal to the operation` (Customer Manager) | Cùng hành động. |
| `Creating PO in CRM System` | *(không có counterpart)* | — | Bước hệ thống hoá, không có ý định i* nào tương ứng — gap. |
| *End* "PO created" | **may-violate** | `Achieve high sales targets performance` (Customer Manager) | BPMN hoàn tất không tự động suy ra softgoal này được thoả — đặc biệt nếu `Completion of kit components` đã làm thay đổi nội dung so với lúc approve (Case A). |

## i* không có BPMN nào realize (kể cả gián tiếp)

- **Management** (toàn nhánh): `Increasing the sales and revenues`, `Strategic sales development`, `Sales performance measurement`, `Ensure reliability`, `Ensure profitability rate`… — tầng chiến lược tổ chức, không activity nào trong quy trình 1 deal cụ thể chạm tới (đúng bản chất: đây là goal cấp portfolio/nhiều deal, không phải cấp 1 instance).
- **VP Business Division**: `Promote sales and profitability strategic view`.
- **Approvers**: `Approve and progress price proposals`, `Maintain profitability` (softgoal gốc — được argue *gián tiếp* qua các Task `Control and check the profitability`/`Approve price proposal` chứ không "realize" trực tiếp bởi 1 activity nào).
- **Project Department**: `Prepare for post sale activities` — quy trình Sales Forecast dừng ở tạo PO, không đi tới lắp đặt.

## Đã sửa trong `goal-sales-forecast.txt` (so với `-default.txt`)

1. `Ensure rellability` → `Ensure reliability`; `istar.Goal` → `istar.Quality` (Management).
2. Link `Ensure reliability → Increasing the sales and revenues`: `AndRefinementLink` → `QualificationLink` (khớp cách "Ensure profitability rate" đang được nối).
3. `Quality` (Presale Department) → đổi tên `Solution quality`.
4. `Calculate organizational considerations and constraints`: `istar.Goal` → `istar.Task` (VP Business Division) — là một bước phân tích cụ thể, ngang hàng Task `Control and check the profitability Org level` dưới cùng 1 Goal cha qua AND-refinement.

## Đã sửa trong `process-sales-forecast.bpmn` (so với `-default.bpmn`)

1. `Gw_loop_m`: `inclusiveGateway` → `exclusiveGateway` (chỉ đúng 1 trong 3 nhánh "No" đổ về tại một thời điểm).

*(Ba gateway merge còn lại — `Gw_svc_m`, `Gw_kit_m`, `Gw_appr_m` — đã là `exclusiveGateway` sẵn từ trước khi có yêu cầu này.)*

## Vì sao cần thêm OCL

Bảng trên đã tự phát hiện 2 việc bảng mapping-theo-tên trước đây bỏ lỡ: (1) nhánh `>250,000 NIS?` hoàn toàn không có counterpart i* (Financial Control vắng mặt), và (2) `Completion of kit components` realizes cùng Task đã có tên khớp — nhưng **thứ tự** (sau khi goal đã realizes) là thứ bảng tĩnh không thể hiện được. Xem file OCL đính kèm cho cách biểu diễn Case A bằng invariant.

## Ràng buộc OCL đã thêm vào 2 file (chỉ là ghi chú, không phải file .use thật)

Theo lựa chọn của bạn: OCL được nhúng dạng text tham khảo, không phải định dạng USE plugin nạp được.

- **BPMN** (`process-sales-forecast.bpmn`): mỗi task/gateway/event có thêm `<bpmn:documentation>` dạng
  `context <Role>::<operation>()` với `pre`/`post`, hoặc `context Deal inv <Guard>:` cho gateway.
- **i***: mỗi node/dependum có thêm `customProperties.OCL`, theo đúng quy ước
  `_condition()`/`_preHolds()`/`_postHolds()` — nhưng **chỉ ở leaf**: Goal/Task chỉ được viết OCL
  nếu KHÔNG phải target của bất kỳ `AndRefinementLink`/`OrRefinementLink` nào (tức không có
  con nào refine vào nó). Goal/Task là **parent** (có con refine vào) thì **không viết** — giá trị
  của nó suy ra từ con (AND: tất cả con phải đúng; OR: ít nhất 1 con đúng), viết tay sẽ dư thừa/mâu thuẫn.
  **Quality (softgoal) không viết OCL** — trạng thái của nó không do tự nó quyết định mà do các
  goal/task nào đã đạt rồi `help/hurt/make/break` vào nó, tức là suy ra từ contribution link,
  không có điều kiện Boolean độc lập để viết.
  Resource và dependum vẫn được viết OCL bình thường (không phải Quality, không nằm trong cây refinement).
- Cả 2 phía dùng chung một **domain vocabulary** (`context Deal`): `dealValue`, `profitabilityPct`,
  `serviceContractPct`, `bomReady`/`hldReady`/`pnsReady`, `approvedBy: Set(Role)`, `financialControlApproved`,
  `creditControlApproved`, `currentProposal`/`approvedProposal` (2 version-marker riêng biệt — chính là chỗ
  biểu diễn Case A).

---

## ACL redesign (structural pass)

`sales-forecast.acl` was reworked from a flag bag into a structural model:

- **All ten organisational participants are first-class `role`s with their
  own state** — `CustomerManager.authorityLevel`/`openDealCount`,
  `ServiceAgreements.minServiceFeePct`, and the escalation chain as a
  specialization hierarchy `Approver -> DeptManager / SectorManager /
  BusinessVp / FinancialControl / CreditControl / Economy`, each adding
  only its own datum (`profitabilityCeilingPct`, `dealValueThreshold`).
- **Quantities are numbers**: `serviceContractPct`, `profitabilityPct`,
  `dealValue`, `proposalVersion`, `approvedProposalVersion` are `Integer`.
- **The five per-level approval flags became a 3-valued enum**
  `ApprovalOutcome {Pending, Approved, Rejected}` (`deptApproval`,
  `sectorApproval`, ...), plus a first-class `ApprovalStep` sub-entity
  (`level`, `outcome`, `onProposalVersion`) linked to `Deal` and to the
  deciding `Approver` by the `dealApprovalChain` / `stepDecidedBy`
  associations. The i* leaf conditions and BPMN gateway guards were
  updated to `self.deal.deptApproval = ApprovalOutcome::Approved` etc.
- A coarse `DealPhase` enum sits on top; `dealHandledBy` /
  `dealForCustomer` associations carry the Deal↔Role structure.
- New file `sales-forecast.aclboundary` gives the bounded-verification
  scope (40 snapshots, 3 `ApprovalStep` rows, one loop).

The fine-grained milestone Booleans the paper's Case-A divergence relies
on (`dealDetailsInserted`, `bomReady`, `approvedProposalVersion` vs
`proposalVersion`, ...) are kept — they are genuine two-valued facts the
goals/gateways must observe individually.
