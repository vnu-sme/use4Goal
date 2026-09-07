# Mapping BPMN ↔ i* — Customer Complaint Management (theo ngữ nghĩa)

Nguồn: `process-customer-complaint.bpmn` ↔ `goal-customer-complaint.txt` (bản đã sửa cho đúng chuẩn i*/BPMN).
Bản gốc chưa sửa: `process-customer-complaint-default.bpmn`, `goal-customer-complaint-defaultl.txt`.

## Vì sao không mapping theo tên

BPMN activity mô tả **hành động** ("Creating complaint", "Updating artifacts" — động từ + gerund). i* Goal mô tả
**trạng thái đã đạt được** ("Get financial compensation approval", "Satisfy the customers" — danh từ hoá / trạng thái).
Hai loại này *về nguyên tắc không thể trùng tên* — nên bảng dưới đây không dùng tiêu chí "tên giống nhau" mà dùng
4 quan hệ ngữ nghĩa:

| Quan hệ | Ý nghĩa |
| --- | --- |
| **realizes** | Hoạt động BPMN này hoàn tất **chính là** sự kiện làm cho i* Task/Goal/dependency trở thành true. |
| **enables** | Nhánh/quyết định BPMN này là điều kiện để i* Task/Goal *được xét tới*, chưa chắc đã thoả. |
| **operationalizes** | i* Task mô tả HOW ở mức trừu tượng; activity BPMN là cách cụ thể để thực hiện HOW đó (Task↔Task, tên có thể giống nhau vì cả 2 đều là "hoạt động" — không vi phạm nguyên tắc trên). |
| **may-violate** | Đến được điểm này trong BPMN **không đảm bảo** i* Goal/Quality tương ứng đã thoả — điểm mấu chốt để tìm lỗi. |

## Bảng mapping — nhánh chính (Field Man → kết thúc)

| BPMN | Quan hệ | i* (actor) | Vì sao |
| --- | --- | --- | --- |
| *Start* "Complaint received" | enables | `Creating complaint` (Field Person/Nutritionist) | Có complaint là tiền đề để actor bắt đầu theo đuổi goal của mình. |
| `Creating complaint` | operationalizes | `Creating complaint` (Field Person/Nutritionist) | Cùng một hành động — ghi nhận complaint mới. |
| `Updating artifacts` | operationalizes | `Updating customer complaint` (Field Person/Nutritionist) | Cùng loại hành động (cập nhật dữ liệu complaint), tên khác vì soạn thảo độc lập — không sao. |
| `Required additional examination?` — **No** | enables | `Handling customer complaints` (Field Person/Nutritionist) | Bỏ qua toàn bộ nhánh kiểm tra, đi thẳng tới compensation — goal "handling" được theo đuổi mà **không đi qua** `Complaints examination`. |
| `Required additional examination?` — **Yes** → `Gw_route` | enables | `Complaints examination` (Lab and Quality Manager) *hoặc* `Examining the findings and updating` (Customer and field Manager) *hoặc* `Updating and examining findings` (Field Person/Nutritionist) | 3 nhãn điều kiện mới (*Customer/account issue*, *Nutrition-related issue*, *Lab/quality-related issue*) chọn đúng 1 trong 3 nhánh nghiệp vụ — quan hệ ngữ nghĩa rõ ràng dù không actor nào tên trùng "route". |
| `Examining the artifacts and updating` (Customer Manager lane) | operationalizes | `Examining the findings and updating` (Customer and field Manager) | Cùng hành động kiểm tra hồ sơ, actor khớp. |
| `Examining the artifacts and updating` (Nutritionist lane) | operationalizes | `Updating and examining findings` (Field Person/Nutritionist) | i* gộp Nutritionist vào actor Field Person — xem cảnh báo bên dưới. |
| `Examining the artifacts and updating` (Lab Manager lane) → `Checking in proactive tests` | operationalizes | `Complaints examination` qua các Task con `Monitor complaints`/`Checking feed quality` (Lab and Quality Manager) | Cùng cụm hoạt động kiểm tra chất lượng. |
| `Quality improvement opportunity?` — **Yes** → `Updating as improvement suggestion` | enables | `To draw conclusions` → `Propose high quality feed only` (Lab and Quality Manager) | Kích hoạt nhánh dẫn tới đề xuất cải thiện chất lượng feed. |
| `Quality improvement opportunity?` — **No** | **may-violate** | `Improving feed mix` / `Propose high quality feed only` | Bỏ qua cơ hội cải thiện — 2 softgoal này không bao giờ được "argue for" trên nhánh này. |
| `Compensation suggestion` | enables | `Get financial compensation approval` (Field Person/Nutritionist) | Hoạt động này *khởi động* việc theo đuổi goal, chưa làm goal thoả (còn phải qua vòng duyệt). |
| `Refund?` — **No** | enables | `Handling customer complaints` trực tiếp | Không complaint nào cần compensation goal — bỏ qua toàn bộ nhánh CFO/CEO/Accounting, **không phải lỗi** vì goal đó vốn không bắt buộc trên nhánh này. |
| `Refund?` — **Yes** → `<5000 NIS`/`>5000 NIS` | enables | `Financial compensation approval` (Customer and field Manager) *hoặc* `Finance examination and approval` (CFO) | Chọn đúng 1 trong 2 goal cần theo đuổi tuỳ mức tiền. |
| `Suggestion examination and updating` (<5000, Customer Manager) → `Approve? = Yes` | **realizes** | `Financial compensation approval` (Customer and field Manager) | Approve xong = goal thoả. |
| `Suggestion examination and updating` (>5000, CFO) | operationalizes | `Complaint examination and updating` (CFO) | Cùng hành động. |
| `Approve? = Yes` (CFO) | enables | `Finance examination and approval` (CFO) tiến gần thoả — còn tuỳ `Complex complaint?` | Chưa chắc thoả hẳn nếu complex. |
| `Complex complaint?` | **realizes** | dependency `Get approval for complex complaint` (CFO → CEO) | Đây là quan hệ 1-1 chính xác nhất trong toàn bộ case: gateway *là* điểm kích hoạt dependency, không cần khớp tên. |
| `Complaint examination and updating` (CEO) | operationalizes | `Complaint examination and updating` (CEO) | Cùng hành động, cùng actor. |
| `Approve? = Yes` (CEO) | **realizes** | `Handling of complex complaint` + `Compensation approval` (CEO) | Approve xong = cả 2 goal của CEO thoả. |
| `Approve? = No` (bất kỳ CM/CFO/CEO) | enables (loop) | *(không falsify goal, chỉ trì hoãn)* | Goal quay lại trạng thái "chưa thoả nhưng vẫn đang theo đuổi", không phải "đã fail". |
| `Updating refund in the system` | **realizes** | `Update financial compensation` → (or-refines) `Provide accounting management to customers` (Accounting department) | Ghi nhận refund xong = cả Task lẫn Goal cha thoả. |
| `Updating the complaint` (lane **Field Man**) | operationalizes | `Update complaint` (actor **Customer and field Manager**) | ⚠️ Cùng hành động nhưng **khác actor sở hữu** giữa 2 mô hình — xem Ghi chú actor bên dưới. |
| *End* "Complaint closed" | **may-violate** | `Satisfy the customers`, `Handling customer complaints` (Field Person/Nutritionist) | Đây là câu hỏi nghiên cứu cốt lõi: BPMN complete không suy ra 2 phần tử này chắc chắn true — đặc biệt trên các nhánh "No" ở trên. |

## i* không có BPMN nào realize (kể cả gián tiếp)

- **Quality committee** (toàn actor — `Supervision on feed quality`, `Examination of quality metrics`, `Generating a complaints report`, `Decisions making on quality improvements`…): không một activity/gateway nào trong BPMN từng đưa token tới nhánh này. Đây là intentional structure hoàn toàn *aspirational*.
- `Customer` (actor): không tham gia BPMN như 1 participant/lane nào.

## Ghi chú actor còn tồn tại (không sửa trong lần này vì đây là lỗi *giữa 2 mô hình*, không phải lỗi nội tại)

- BPMN tách **Field Man** và **Nutritionist** thành 2 lane; i* gộp cả hai vào 1 actor `Field Person / Nutritionist`.
- `Update complaint` (i*, actor `Customer and field Manager`) ứng với activity `Updating the complaint` nằm ở lane **Field Man** trong BPMN.

## Đã sửa trong `goal-customer-complaint.txt` (so với `-defaultl.txt`)

1. `Getting customer status`: `istar.Quality` → `istar.Task` (hành xử như Task, không phải softgoal).
2. Xoá 2 `ParticipatesInLink` không hợp lệ (source phải là `istar.Agent`, ở đây cả 2 đầu đều là `istar.Role`).

## Đã sửa trong `process-customer-complaint.bpmn` (so với `-default.bpmn`)

1. `Gw_examdone`, `Gw_updcompl`, `Gw_refund_merge`: `inclusiveGateway` → `exclusiveGateway` (chỉ đúng 1 nhánh vào từng gateway tại một thời điểm, dùng inclusive là thừa/gây hiểu nhầm có thể đồng thời nhiều nhánh).
2. `Gw_route`: thêm nhãn điều kiện cho 3 outgoing flow (*Customer/account issue* / *Nutrition-related issue* / *Lab/quality-related issue*) — trước đó là exclusiveGateway với 3 nhánh không điều kiện, không hợp lệ theo chuẩn BPMN (XOR phải có điều kiện hoặc default).

## Ràng buộc OCL đã thêm vào 2 file (chỉ là ghi chú, không phải file .use thật)

Theo lựa chọn của bạn: OCL được nhúng dạng text tham khảo, không phải định dạng USE plugin nạp được.

- **BPMN** (`process-customer-complaint.bpmn`): mỗi task/gateway/event có thêm `<bpmn:documentation>` dạng
  `context <Role>::<operation>()` với `pre`/`post`, hoặc `context Complaint inv <Guard>:` cho gateway.
- **i***: mỗi node/dependum có thêm `customProperties.OCL`, theo đúng quy ước
  `_condition()`/`_preHolds()`/`_postHolds()` — nhưng **chỉ ở leaf**: Goal/Task chỉ được viết OCL
  nếu KHÔNG phải target của bất kỳ `AndRefinementLink`/`OrRefinementLink` nào (tức không có
  con nào refine vào nó). Goal/Task là **parent** (có con refine vào) thì **không viết** — giá trị
  của nó suy ra từ con (AND: tất cả con phải đúng; OR: ít nhất 1 con đúng), viết tay sẽ dư thừa/mâu thuẫn.
  **Quality (softgoal) không viết OCL** — trạng thái của nó không do tự nó quyết định mà do các
  goal/task nào đã đạt rồi `help/hurt/make/break` vào nó, tức là suy ra từ contribution link,
  không có điều kiện Boolean độc lập để viết.
  Resource và dependum vẫn được viết OCL bình thường (không phải Quality, không nằm trong cây refinement).
- Cả 2 phía dùng chung một **domain vocabulary** (`context Complaint`): `created`, `artifactsUpdated`,
  `examinedBy: Set(Role)`, `needsMoreExamination`, `qualityImprovementFlagged`, `refundRequested`,
  `refundAmount`, `suggestionApprovedBy: Set(Role)`, `complex`, `refundUpdatedInSystem`, `handled`, `closed`
  — `closed` và `handled` **cố tình tách riêng** vì đó chính là điểm mấu chốt: BPMN chỉ đảm bảo `closed = true`,
  không đảm bảo `handled = true` (xem `EndEvent_1` trong file .bpmn).

---

## ACL redesign (structural pass)

`customer-complaint.acl` was reworked into a structural model:

- **Seven participants as first-class `role`s with state**; the two
  refund-approval levels are a specialization hierarchy
  `FinancialApprover -> CustomerManager / Cfo` (and `Ceo`), with
  `CustomerManager.approvalCeilingAmount` the only level-specific datum.
- **`category`, `phase` and each approval `outcome` are enums**
  (`ComplaintCategory`, `ComplaintPhase`, `ApprovalOutcome`); the three
  `cmApproved`/`cfoApproved`/`ceoApproved` Booleans became
  `cmApproval`/`cfoApproval`/`ceoApproval : ApprovalOutcome`, and the i* /
  BPMN OCL was updated accordingly (`... = ApprovalOutcome::Approved`).
- **Structure via sub-entities + associations**: `Examination`
  (`complaintExaminations`) for the CM -> Nutritionist -> Lab Manager
  passes, `RefundApproval` (`complaintRefundChain` / `refundStepDecidedBy`)
  for the CM -> CFO -> CEO chain, `complaintOwnedBy` for the FieldMan link.
- `refundAmount` is an `Integer` (whole NIS).
- New file `customer-complaint.aclboundary` (30 snapshots, 3 `Examination`
  + 3 `RefundApproval` rows).

Milestone Booleans the goals/gateways observe one-by-one
(`artifactsUpdated`, `examinedBy*`, `qualityImprovementFlagged`,
`handled` vs `closed`, ...) are kept.
