# 5. Hạn Chế

File LaTeX tương lai: `\section{Hạn chế}\label{sec:limitations}`.

## Mục tiêu của phần

Liệt kê trung thực những gì phương pháp **chưa** làm được hoặc **biết là** có thể sai trong tình
huống cụ thể — khác Phần 4 (bàn về đánh đổi có chủ đích, có lý do thiết kế). Ở đây chỉ liệt kê,
không cần biện minh dài dòng, nhưng mỗi hạn chế nên có 1 câu gợi ý mức độ nghiêm trọng/điều kiện
xảy ra để người đọc tự đánh giá rủi ro khi áp dụng.

## Cấu trúc heading đề xuất

Không cần subsection — 1 danh sách có cấu trúc (`enumerate`), mỗi mục 1 hạn chế, sắp theo nhóm:
kỹ thuật thuật toán trước, phạm vi ngôn ngữ sau, thực nghiệm cuối cùng.

## Nội dung cốt lõi

### Nhóm A — Hạn chế của cơ chế kiểm tra dựa trên vết thực thi

1. **Không bao phủ mọi nhánh rẽ của BPMN.** Thứ tự thực thi được suy ra bằng sắp xếp topo tuyến
   tính (3.7.1), không mô phỏng gateway `XOR`/`AND`/`OR` theo đúng ngữ nghĩa rẽ nhánh — 1 nhánh
   không nằm trên đường topo được chọn sẽ không được kiểm tra trong lần chạy đó. Kết quả `PASS`
   trên 1 vết thực thi **không** đảm bảo mọi vết thực thi khác của cùng BPMN model đều `PASS` —
   đã nêu chi tiết đánh đổi này ở Phần 3.4.2 và 4.3, nhắc lại ở đây theo đúng tinh thần liệt kê hạn
   chế của `simplify`/review workflow.
2. **Phụ thuộc chất lượng kịch bản `.soil` đầu vào.** Nếu người viết kịch bản không tạo đủ object
   (ví dụ chỉ tạo 1 `Participant` trong khi thực tế cần ≥ 2 theo cardinality ACL), phép kiểm goal
   gốc (3.7.4.c) có thể bỏ qua các nhánh liên quan vì actor đó chưa từng "xuất hiện" trong vết —
   không phải lỗi thuật toán, nhưng là rủi ro sử dụng cần nêu rõ trong tài liệu người dùng.
3. **Kiểm bất biến ACL theo kiểu fail-fast có thể che khuất lỗi sau đó.** Vì `evaluateAclInvariants`
   dừng tại checkpoint vi phạm đầu tiên (3.7.4.a), nếu sửa xong lỗi đầu tiên vẫn có thể còn lỗi cấu
   trúc khác ở checkpoint sau — người dùng phải chạy lại nhiều lần để dò hết, không có báo cáo đầy
   đủ 1 lần như phép kiểm (b).

### Nhóm B — Hạn chế phạm vi ngôn ngữ

4. **BPMN chỉ dừng ở predicate Boolean, không có domain-state mutation nội tại đầy đủ.** `effect`
   trên activity BPMN là 1 khối SOIL đơn giản hoá; BPMN model không tự thực thi hành vi thật (theo
   đúng ghi chú thiết kế "no SOIL action" trong `mtg.bpmn2`) — mọi hành vi thật phải đi qua bộ nối
   `.soil`, giới hạn khả năng biểu diễn quy trình có logic phức tạp bên trong 1 activity.
5. **Compatibility trong ACL chỉ xét cặp vai trò, chưa xét ràng buộc bậc cao hơn.** Luật sinh bất
   biến ở 3.6.3 chỉ xử lý từng **cặp** `(R1, R2)` — chưa hỗ trợ ràng buộc kiểu "không quá 2 trong 3
   vai trò X/Y/Z cùng lúc" hoặc ràng buộc phụ thuộc số lượng, nếu tổ chức thật cần biểu diễn quy tắc
   phức tạp hơn cặp đôi.

### Nhóm C — Hạn chế về đánh giá thực nghiệm

6. **Chưa có đánh giá trên nhiều case study độc lập.** Toàn bộ minh hoạ trong bài báo dựa trên 1 ví
   dụ (`MeetingScheduler`) — cần nêu rõ đây là **hạn chế về phạm vi thực nghiệm**, không phải hạn
   chế kỹ thuật của phương pháp; nếu trước khi nộp bài có thêm case study khác đã có sẵn trong repo
   (`security_compliance`, `job_application_review`, `incident_response_acl`...) thì mục này cần
   cập nhật lại, không giữ nguyên phát biểu "chỉ 1 case study".
7. **Chưa đo hiệu năng.** Không có số liệu về thời gian chạy theo kích thước object graph/độ dài
   kịch bản `.soil` — cần nêu là hướng đánh giá còn thiếu nếu bài báo hướng tới venue quan tâm khả
   năng mở rộng (scalability).

## Nguồn tham chiếu / cơ sở

- Mục 1–3: hành vi thuật toán quan sát trực tiếp từ `AclBpmnIStarConformanceChecker.java`
  (`executionOrder` dùng Kahn's algorithm không xét guard; `evaluateAclInvariants` fail-fast qua
  `break`; phụ thuộc `rootGoalIds`/`UNKNOWN` bị bỏ qua trong `evaluateRootGoals`).
- Mục 4: ghi chú thiết kế nguyên văn trong `mtg.bpmn2` dòng 1–2 ("BPMN contains predicates only;
  there is no SOIL action") và `goal/docs/BPMN2_OCL_OPTION2.md` mục "Execution boundary" (dòng
  66–75, liệt kê rõ "Full BPMN join synchronization, nested subprocess lifecycle, persistence,
  retries, and human task assignment remain concerns for a production workflow runtime").
- Mục 5: quan sát từ vòng lặp đôi `for i / for j` trên `concreteRoles` trong
  `AclUseTranslator.renderCompatibilityInvariants` — chỉ xử lý tổ hợp 2 phần tử.
- Mục 6–7: xác nhận bằng kiểm kê thư mục `goal/src/main/resources/examples/` — nhiều case study
  ACL+i*+BPMN khác đã tồn tại sẵn (`security_compliance`, `job_application_review`,
  `incident_response_acl`, `shop_order_fulfillment`) nhưng chưa rõ đã được chạy qua
  `AclBpmnIStarConformanceChecker` hay chưa — cần xác minh trước khi viết bản thảo thật, vì nếu đã
  chạy được thì mục 6 phải sửa lại thành liệt kê **nhiều** case study, không phải hạn chế.

## Ghi chú khi viết prose thật

- Không viết hạn chế bằng giọng phòng thủ ("tuy nhiên phương pháp vẫn hoạt động tốt trong hầu hết
  trường hợp...") — theo đúng tinh thần dự án (tài liệu nội bộ `conformance-istar-bpmn2.md` §5.3
  đã làm rất tốt việc này khi tự phê phán chính hạn chế của Caballero-Villalobos, nên giữ cùng
  chuẩn thẳng thắn khi tự phê phán phương pháp của chính bài báo).
- Mục 6 và 7 cần được **xác minh lại ngay trước khi nộp bài**, không phải lúc viết dàn ý này — kiểm
  tra xem tới thời điểm hoàn thiện bản thảo, có case study/benchmark nào mới đã chạy hay chưa.
