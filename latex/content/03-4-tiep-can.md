# 3.4. Chiến Lược Tiếp Cận

File LaTeX tương lai: `\subsection{Chiến lược tiếp cận}\label{sec:approach}`.

## Mục tiêu của phần

Đây là phần "vì sao thiết kế như vậy" — lập luận bằng cách loại trừ so với các lựa chọn thay thế
đã liệt kê ở Phần 2, không mô tả lại pipeline (đã làm ở 3.3) và không đi vào chi tiết cú
pháp/thuật toán (để dành 3.5–3.7). Đây là phần dễ bị lặp nội dung với 3.3 nhất — phải giữ kỷ luật:
3.3 = "cái gì", 3.4 = "vì sao".

## Cấu trúc heading đề xuất

3 quyết định thiết kế, mỗi quyết định 1 đoạn, có thể dùng `\subsubsection` nếu cần độ dài lớn, hoặc
giữ dạng đoạn văn liên tục có gạch đầu dòng nhỏ nếu ngắn:

```
\subsubsection{Vì sao dịch về một miền ngữ nghĩa chung}
\subsubsection{Vì sao kiểm trên vết thực thi thay vì duyệt toàn bộ trạng thái}
\subsubsection{Vì sao tách ba loại lỗi thay vì một verdict duy nhất}
```

## Nội dung cốt lõi từng quyết định

### 3.4.1. Vì sao dịch về một miền ngữ nghĩa chung

- Lựa chọn thay thế đã bị loại: (a) tự định nghĩa 1 formalism trung gian mới (như hướng B ở Phần
  2.1, hoặc như snapshot pattern tự tạo của JUCS \cite{dang2010jucs}) — chi phí xây + kiểm chứng
  formalism mới, không tận dụng được công cụ có sẵn; (b) giữ 3 công cụ/evaluator tách biệt cho
  ACL/i*/BPMN rồi đồng bộ tay — chính nguồn lỗi minh hoạ ở Tình huống 3 (Phần 3.1).
- Lựa chọn đã chọn: tận dụng UML class diagram + OCL đã có sẵn công cụ hình thức hoá trưởng thành
  (USE \cite{gogolla2007use}) làm **đích dịch chung** cho phần cấu trúc (ACL), và làm **miền đánh
  giá chung** cho cả 3 loại điều kiện (bất biến ACL, `pre`/`post` BPMN, guard i*) vì cả 3 vốn đã
  được viết bằng OCL ngay trong file `.acl`/`.bpmn2`/`.istar` gốc (xem 3.2.2–3.2.3) — không cần
  dịch ngữ nghĩa, chỉ cần 1 system state chung để `eval()` trên đó.
- Đánh đổi cần nêu thẳng (không né tránh, chuẩn bị cho Phần 5): lựa chọn này **không sinh ra 1
  formalism hình thức mới có thể chứng minh tính chất tổng quát** (kiểu Definition/Theorem như
  \cite{caballero2026alig}) — đổi lại lấy khả năng chạy trực tiếp trên công cụ trưởng thành có sẵn.

### 3.4.2. Vì sao kiểm trên vết thực thi thay vì duyệt toàn bộ trạng thái

- Lựa chọn thay thế đã bị loại: dựng product LTS và duyệt BFS 2 chiều toàn bộ không gian trạng thái
  đạt được như \cite{caballero2026alig} — cho verdict tổng quát hơn (đúng với **mọi** cách rẽ nhánh
  có thể) nhưng phải tự hiện thực hoá toàn bộ ngữ nghĩa marking AND/OR/Make/Break cho i* và ngữ
  nghĩa token cho BPMN từ đầu, cộng thêm rủi ro bùng nổ trạng thái khi có vòng lặp.
- Lựa chọn đã chọn: nhận **1 kịch bản thực thi cụ thể** (`.soil`) làm đầu vào, cộng 1 thứ tự thực
  thi BPMN suy ra bằng sắp xếp topo (không mô phỏng song song đa nhánh) — đổi lấy việc tái sử dụng
  trực tiếp engine SOIL/OCL có sẵn của USE để "chạy thật" object graph qua từng bước, thay vì tự
  viết bộ mô phỏng marking.
- Hệ quả cần nêu rõ ngay ở đây (không đợi tới Phần 5): kết quả kiểm tra chỉ đúng với **vết thực thi
  đã cho**, không phải phát biểu phổ quát cho mọi cách hệ thống có thể vận hành — khác bản chất so
  với weak/strong compliance của \cite{caballero2026alig}. Đây là lựa chọn có chủ đích đánh đổi
  tính tổng quát lấy khả năng cài đặt được ngay trên hạ tầng USE/SOIL sẵn có, không phải thiếu sót
  bị bỏ quên.

### 3.4.3. Vì sao tách ba loại lỗi thay vì một verdict duy nhất

- Lựa chọn thay thế đã bị loại: gộp mọi vi phạm thành 1 nhãn nhị phân/rời rạc như phần lớn nhóm ở
  Phần 2 (`CONFORMANT`/`NON_COMPLIANT`, `STRONG`/`POTENTIAL inconsistency`...).
- Lựa chọn đã chọn: giữ riêng `aclFailures` (vi phạm cấu trúc tổ chức), `bpmnFailures` (vi phạm
  tiền/hậu điều kiện quy trình), `goalFailures` (goal gốc chưa đạt) — vì 3 loại lỗi này có **người
  chịu trách nhiệm sửa khác nhau** trong thực tế (quản trị tổ chức sửa ACL, business analyst sửa
  BPMN, requirement engineer xem lại goal model) — tách nhãn giúp công cụ gỡ lỗi ở Phần 6 định
  tuyến đúng thông tin cho đúng vai trò người dùng.
- Đây là lựa chọn thiết kế trực tiếp phục vụ RQ3 (Phần 1) — không có nhóm nào ở Phần 2 phân tách
  lỗi theo 3 chiều tổ chức/hành vi/ý định vì không nhóm nào xét cả 3 chiều cùng lúc.

## Nguồn tham chiếu / cơ sở

- 3.4.1: bằng chứng OCL dùng chung trong cả 3 loại file — `mtg.acl` (dịch ra `inv` OCL qua
  `AclUseTranslator`), `mtg.bpmn2` (`pre`/`post` là OCL trực tiếp), `mtg.istar` (`pre`/`post` là
  OCL trực tiếp, ví dụ dòng khai `pre {[ Meeting.allInstances()->exists(m | not m.detailsDecided) ]}`).
- 3.4.2: bằng chứng "1 kịch bản cụ thể, không duyệt nhánh song song" — hàm `executionOrder` trong
  `AclBpmnIStarConformanceChecker.java` dùng sắp xếp topo (Kahn's algorithm qua `indegree`/`queue`),
  không có logic rẽ nhánh XOR/OR song song nào trong `executionPlan`.
- 3.4.3: bằng chứng 3 danh sách lỗi tách biệt — record `Result` trong
  `AclBpmnIStarConformanceChecker.java`.

## Ghi chú khi viết prose thật

- Đây là phần thích hợp nhất để dùng cấu trúc "lựa chọn thay thế X có nhược điểm Y → chọn Z vì W"
  lặp lại 3 lần — giữ nhất quán cấu trúc câu giữa 3.4.1–3.4.3 để dễ đọc.
- Không giấu đánh đổi ở 3.4.2 — đây là nội dung sẽ được Phần 4 (Thảo luận) và Phần 5 (Hạn chế) khai
  triển tiếp, nên câu văn ở đây chỉ cần nêu ngắn gọn, trỏ trước bằng `\ref{sec:limitations}` nếu
  cần, không lặp lại toàn bộ lập luận.
- Tránh dùng từ "tối ưu"/"tốt nhất" cho các lựa chọn thiết kế — đúng tinh thần academic writing,
  chỉ nói "phù hợp với ràng buộc X đã nêu ở 3.1", tránh overclaim.
