# 1. Giới Thiệu

File LaTeX tương lai: `\section{Giới thiệu}\label{sec:introduction}`.

**Đã viết lại theo góp ý**: bản trước đưa ACL vào Introduction như thể đã được giới thiệu — sai,
vì đây là phần mở đầu, ACL chưa xuất hiện ở đâu cả. Bối cảnh đúng của bài báo là bài toán **kiểm
tra tương thích giữa i* và BPMN** (2 mô hình của giai đoạn phân tích), không phải bài toán 3 chiều
ACL–i*–BPMN ngay từ đầu — chiều thứ 3 (cấu trúc) chỉ xuất hiện tự nhiên qua RQ1, như 1 câu hỏi mở
ra từ chính hạn chế của bài toán 2 chiều, chứ không phải tiền đề có sẵn.

## Mục tiêu của phần

Thuyết phục người đọc bằng đúng 1 mạch lập luận độc lập, không giả định người đọc đã biết ACL:
bài toán i*↔BPMN quan trọng và khó → JUCS là nền tảng lý thuyết hợp lý để giải nhưng còn khoảng
trống → khoảng trống đó dẫn tới 2 câu hỏi nghiên cứu cụ thể → đóng góp trả lời đúng 2 câu hỏi đó.

## Cấu trúc heading đề xuất

Không tách subsection — 5 đoạn văn liền mạch theo `latex-rules` (Introduction là prose liên tục).

## Nội dung cốt lõi từng đoạn

### Đoạn 1 — Bối cảnh: tầm quan trọng của bài toán i*↔BPMN

- Nêu thẳng bài toán, không vòng vo: hệ thống doanh nghiệp thường được đặc tả bằng **2 mô hình bổ
  sung nhau** ở giai đoạn phân tích — mô hình ý định i* \cite{yu1997towards} trả lời **yêu cầu là
  gì** (actor muốn đạt goal nào, vì sao), và mô hình tiến trình BPMN \cite{omg2013bpmn} trả lời
  **giải pháp vận hành ra sao** (hoạt động nào, theo thứ tự nào để hiện thực hoá yêu cầu đó).
- Nêu tầm quan trọng bằng bằng chứng cụ thể từ khảo sát tài liệu (không nói chung chung "rất quan
  trọng"): khoảng cách giữa mô hình yêu cầu và mô hình quy trình là nguyên nhân trực tiếp gây phát
  sinh lại chi phí (rework) và lỗi hiện thực hoá trong dự án phần mềm phức tạp; chỉ 1 phần nhỏ hệ
  thống thông tin thực sự được phát triển đúng theo quy trình nghiệp vụ mà chúng lẽ ra phải phục
  vụ \cite{alignment2019slr}, và tới nay **chưa có 1 chuẩn metamodel truy vết (traceability)** được
  công nhận rộng rãi giữa BPMN và mô hình yêu cầu.
- Kết đoạn bằng phát biểu bài toán cụ thể: kiểm tra 1 mô hình BPMN đã cho có **thật sự hiện thực
  hoá đúng** ý định đã đặc tả trong 1 mô hình i* đã cho hay không — đây là bài toán trung tâm, độc
  lập, của toàn bộ bài báo.

### Đoạn 2 — Hạn chế của các hướng giải quyết đã có

- Không liệt kê từng bài — tóm gọn theo đúng 4 nhóm cơ chế đã khảo sát ở Phần 2 (2.2–2.5, nhóm A–D):
  (a) nhóm suy luận tĩnh trên hình thức luận chung (2.2/A — DL, OCL trên metamodel hợp nhất) chỉ so
  khớp cấu trúc tại 1 thời điểm, không có khái niệm vết thực thi theo thời gian; (b) nhóm ngữ nghĩa
  vận hành theo vết thực thi (2.3/B) hoặc dừng ở thao tác viết tay, hoặc bùng nổ trạng thái khi có
  vòng lặp, và tự thừa nhận verdict có thể "vô lý về nghiệp vụ" trong một số tình huống; (c) nhóm
  căn chỉnh định lượng (2.4/C) phụ thuộc trọng số chủ quan do con người gán, chưa tự động hoá; (d)
  nhóm đối chiếu ràng buộc phẳng bên ngoài (2.5/D) phẳng hoá yêu cầu thành luật/KPI/log, đánh mất
  cấu trúc ý định (actor, refinement, contribution) của goal model. Không nhóm nào trong 4 nhóm này
  xét thêm ràng buộc cấu trúc tổ chức, thuộc phạm vi 1 nhóm riêng (2.6/E) hoàn toàn tách biệt.
- 1 câu trỏ `\ref{sec:related-work}` để không lặp lại chi tiết đã có ở Phần 2.

### Đoạn 3 — Nền tảng đề xuất

- Bài báo chọn xây trên nền tảng khung khái niệm của JUCS \cite{dang2010jucs}: 1 mô hình được xem
  như tập **kịch bản** (scenario), mỗi kịch bản là 1 chuỗi **hành động** (action) đi kèm **snapshot
  pattern** mô tả trạng thái hệ thống ngay trước và ngay sau hành động đó; 2 mô hình được đối chiếu
  bằng cách đồng bộ kịch bản của bên này với kịch bản của bên kia theo từng bước.
- Nêu rõ điểm kế thừa và điểm khác biệt (không đi vào chi tiết kỹ thuật ở đây — chỉ định vị, chi
  tiết dành cho Phần 3.4): JUCS để người dùng **viết tay** snapshot pattern (biểu thức pre/post) cho
  từng hành động; bài báo kế thừa đúng cấu trúc khái niệm "kịch bản = chuỗi hành động + snapshot"
  nhưng thay việc viết tay bằng cách **suy ra snapshot tự động** từ OCL đã có sẵn trong chính mô
  hình i*/BPMN, đánh giá trên 1 hệ thống đối tượng hình thức hoá được (USE \cite{gogolla2007use}).

### Đoạn 4 — Câu hỏi nghiên cứu và đóng góp

- Khi cụ thể hoá bài toán bằng khung JUCS, lộ ra 1 vấn đề mà JUCS gốc không xử lý: cả i* lẫn BPMN
  **đều không có khái niệm cấu trúc tổ chức** (ai được phép đóng vai trò nào, đồng thời với vai trò
  nào khác) — nhưng đây là thông tin cần thiết để snapshot pattern phản ánh đúng trạng thái hệ
  thống thật (xem ví dụ cụ thể ở Phần 3.1). Từ đó hình thành 2 câu hỏi nghiên cứu:

  - **RQ1**: Có phương pháp nào để đặc tả **cấu trúc hệ thống** (thực thể, vai trò, ràng buộc giữa
    các vai trò) một cách hình thức, ngay trong giai đoạn phân tích, khi mà i* và BPMN — 2 ngôn ngữ
    chính của giai đoạn phân tích trong dự án này — đều không có sẵn khái niệm này?
  - **RQ2**: Có phương pháp nào để kiểm tra tính nhất quán giữa **yêu cầu** (goal model i*, đặc tả
    "hệ thống cần đạt gì") và **giải pháp** (mô hình tiến trình BPMN, đặc tả "hệ thống làm gì để đạt
    điều đó") một cách tự động, kế thừa khung khái niệm JUCS nhưng khắc phục các hạn chế đã nêu ở
    Đoạn 2?

- Đóng góp, ứng đúng thứ tự 2 RQ:
  1. Trả lời RQ1: ngôn ngữ ACL đặc tả cấu trúc hệ thống (thực thể, vai trò, nhóm, ràng buộc tương
     thích vai trò), cùng luật dịch tự động sang mô hình lớp và bất biến OCL trong USE (Phần
     3.5–3.6);
  2. Trả lời RQ2: 1 thuật toán kiểm tra tính nhất quán yêu cầu↔giải pháp dựa trên vết thực thi
     checkpoint, kế thừa khung "kịch bản + snapshot" của JUCS nhưng suy snapshot tự động từ OCL,
     đồng thời tận dụng kết quả của RQ1 để snapshot phản ánh đúng cả ràng buộc cấu trúc (Phần 3.7);
  3. Công cụ hỗ trợ tích hợp cả 2 đóng góp trên vào môi trường USE (Phần 6).

### Đoạn 5 — Bố cục bài báo

- 1 câu tóm bố cục, trỏ đúng số section: Phần 2 khảo sát công trình liên quan theo 5 nhóm cơ chế;
  Phần 3 trình bày phương pháp đề xuất qua ví dụ minh hoạ, kiến thức nền, tổng quan kiến trúc, ngôn
  ngữ ACL, phép dịch ACL sang USE, và thuật toán kiểm tra; Phần 4 thảo luận; Phần 5 nêu hạn chế;
  Phần 6 trình bày công cụ hỗ trợ.

## Nguồn tham chiếu / cơ sở

- Đoạn 1: số liệu/nhận định về khoảng cách yêu cầu↔quy trình và thiếu chuẩn traceability lấy từ kết
  quả `WebSearch` "survey requirements engineering business process modeling alignment gap" thực
  hiện trong phiên làm việc này — khoá `alignment2019slr` là đề xuất tạm cho bài "Alignment between
  Business Requirement, Business Process, and Software System: A Systematic Literature Review",
  cần tự xác nhận lại thông tin xuất bản đầy đủ trước khi đưa vào `references.bib`.
- Đoạn 2: chi tiết từng hạn chế lấy từ [02-cong-trinh-lien-quan.md](02-cong-trinh-lien-quan.md)
  §2.2–2.5 (nhóm A–D), không lặp lại ở đây, chỉ tóm 1 câu mỗi nhóm.
- Đoạn 3: mô tả JUCS (scenario/action/snapshot pattern) lấy từ ghi chú đã có sẵn trong
  `doc/paper/conformance-istar-bpmn2.md` dòng 41 (bảng "Mỗi bài đóng góp gì", hàng JUCS) — đã đọc
  và xác nhận đúng khi khảo sát ban đầu.
- Đoạn 4: 2 RQ và 3 đóng góp khớp trực tiếp với cấu trúc pipeline đã xác nhận bằng đọc mã nguồn
  thật ở Phần 3 — xem bảng ánh xạ nguồn ở [index.md](index.md) mục 4.

## Ghi chú khi viết prose thật

- Không nhắc tên "ACL" trước khi RQ1 xuất hiện trong đoạn 4 — đây là quy tắc cứng cho việc viết
  prose thật, đúng góp ý đã nhận: Introduction phải đọc được độc lập, người đọc chỉ biết "có 1 ngôn
  ngữ đặc tả cấu trúc" khi RQ1 dẫn tới nó, không phải trước đó.
- Đoạn 3 (Nền tảng đề xuất) là đoạn mới hoàn toàn so với bản trước — cần đọc lại kỹ
  `conformance-istar-bpmn2.md` §1 trước khi viết prose thật để diễn giải đúng khái niệm JUCS bằng
  văn phong riêng, không chép lại nguyên văn ghi chú nội bộ.
- Giữ đúng cảnh báo đã có ở bản trước: Đoạn 3 chỉ nói bài báo **kế thừa khung khái niệm** JUCS
  (kịch bản/hành động/snapshot), không nói kế thừa cơ chế product-LTS/BFS của
  `caballero2026alig` — thuật toán thật đã cài đặt (Phần 3.7) gần với JUCS hơn (1 vết thực thi cụ
  thể) chứ không dựng toàn bộ không gian trạng thái.
