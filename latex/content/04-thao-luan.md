# 4. Thảo Luận

File LaTeX tương lai: `\section{Thảo luận}\label{sec:discussion}`.

## Mục tiêu của phần

Diễn giải **ý nghĩa** của các quyết định thiết kế đã trình bày ở Phần 3, đối chiếu lại với Related
Work — khác Phần 5 (Hạn chế) ở chỗ: Thảo luận bàn về **đánh đổi có chủ đích và câu hỏi mở**, Hạn
chế liệt kê **những gì chưa làm được/biết là thiếu**. Không lặp lại nội dung 2 phần này lẫn nhau.

## Cấu trúc heading đề xuất

```
\subsection{So với các phương pháp chỉ xét hai chiều}
\subsection{Ý nghĩa của việc tách ba loại lỗi}
\subsection{Đánh đổi giữa vết thực thi cụ thể và tính tổng quát}
```

## Nội dung cốt lõi từng mục

### 4.1. So với các phương pháp chỉ xét hai chiều

- Quay lại đúng gap đã nêu ở 2.7: minh hoạ **định lượng** bằng chính ví dụ `MeetingScheduler` —
  nếu chỉ chạy 1 phương pháp thuộc nhóm 2.3 (Caballero-Villalobos-style) hoặc 2.4 (Gröner-style)
  trên riêng cặp i*/BPMN của `mtg.istar`/`mtg.bpmn2`, Tình huống 1 (Phần 3.1) hoàn toàn không thể
  phát hiện được — không phải vì các phương pháp đó "yếu hơn", mà vì bài toán của chúng **không có
  khái niệm** cấu trúc tổ chức để biểu diễn lỗi này. Đây là điểm cần nói rõ để tránh bị hiểu nhầm là
  đang tuyên bố "phương pháp này tốt hơn" — thực chất là **giải bài toán khác, rộng hơn**.
- Ngược lại, thừa nhận công bằng: nếu chỉ cần kiểm tra 2 chiều goal↔process thuần tuý, không có
  chiều tổ chức, thì \cite{caballero2026alig} cho verdict tổng quát hơn (đúng với mọi cách rẽ
  nhánh, không chỉ 1 vết thực thi) — nêu lại điểm này để dẫn vào 4.3.

### 4.2. Ý nghĩa của việc tách ba loại lỗi

- Bàn sâu hơn 3.4.3: việc tách `aclFailures`/`bpmnFailures`/`goalFailures` không chỉ là chi tiết kỹ
  thuật, mà phản ánh 1 quan sát về tổ chức thực tế — 3 loại thay đổi mô hình (tổ chức, quy trình,
  mục tiêu) thường do 3 vai trò khác nhau thực hiện và thường **không đồng bộ theo thời gian** (ví
  dụ: quy tắc tương thích vai trò trong ACL có thể được chính sách công ty cập nhật độc lập với
  việc BPMN thay đổi). Việc phân tách lỗi theo chiều giúp quy trách nhiệm sửa lỗi đúng chỗ nhanh
  hơn so với 1 verdict gộp.
- Nêu câu hỏi mở đáng bàn (không cần trả lời dứt khoát, phù hợp giọng "thảo luận"): nếu 1 checkpoint
  vi phạm đồng thời cả `aclFailures` và `bpmnFailures` (ví dụ vai trò không tương thích lại chính là
  actor thực hiện 1 activity vi phạm `pre`), thứ tự báo cáo/ưu tiên sửa nào hợp lý hơn — hiện tại
  `aclFailures` được kiểm fail-fast trước (3.7.4), nhưng đây là lựa chọn thực thi, chưa hẳn phản
  ánh đúng độ ưu tiên nghiệp vụ trong mọi trường hợp.

### 4.3. Đánh đổi giữa vết thực thi cụ thể và tính tổng quát

- Khai triển sâu đánh đổi đã nêu ở 3.4.2: kiểm trên 1 vết thực thi cụ thể trả lời được câu hỏi
  "kịch bản `.soil` này có dẫn tới hệ thống nhất quán hay không", nhưng **không** trả lời được câu
  hỏi tổng quát hơn "có tồn tại **bất kỳ** kịch bản nào dẫn tới bất nhất quán hay không" — đúng loại
  câu hỏi mà weak/strong compliance của \cite{caballero2026alig} trả lời được cho riêng cặp
  goal↔process.
- Gợi mở hướng kết hợp (không cam kết đã làm, chỉ là hướng thảo luận, tách biệt rõ với Đóng góp ở
  Phần 1): về nguyên tắc, `Bpmn2ExecutionEngine`/`IStarUseTraceCompiler` đã tách rời khỏi việc chọn
  **kịch bản nào** để chạy — nên có thể lặp lại toàn bộ pipeline 3.7 cho nhiều kịch bản `.soil`
  khác nhau (ví dụ sinh tự động theo tổ hợp guard khác nhau) để tăng độ bao phủ, dù chưa đạt được
  bảo đảm hình thức "đúng với mọi nhánh" như LTS đầy đủ.
- Kết đoạn bằng việc định vị lại đúng phạm vi đóng góp: bài báo không thay thế các phương pháp
  reachability-based ở nhóm 2.3, mà bổ sung 1 chiều kiểm tra (tổ chức) mà nhóm đó không có, đồng
  thời chọn cơ chế "chạy được ngay trên hạ tầng USE/SOIL có sẵn" làm ưu tiên thiết kế thay vì tính
  tổng quát hình thức.

## Nguồn tham chiếu / cơ sở

- 4.1: đối chiếu trực tiếp Tình huống 1 (03-1-dong-luc.md) với phạm vi bài toán mô tả ở nhóm 2.3–2.4
  (02-cong-trinh-lien-quan.md).
- 4.3: đánh đổi đã xác lập ở 03-4-tiep-can.md §3.4.2; khả năng tái sử dụng pipeline cho nhiều kịch
  bản dựa trên quan sát kiến trúc — `IStarUseTraceCompiler.compile` nhận `soilFile` như 1 tham số
  độc lập, không có logic chọn kịch bản gắn cứng trong bộ biên dịch.

## Ghi chú khi viết prose thật

- Phần này là nơi duy nhất được phép dùng giọng "gợi mở hướng nghiên cứu tiếp theo" mà không cần
  cam kết đã cài đặt — nhưng phải nói rõ ràng đó là hướng mở, không lẫn với Đóng góp đã có ở Phần 1
  (tránh overclaim khi phản biện đọc).
- Không nhắc lại các đề xuất PGA/Petri-invariant/game-theoretic trong
  `doc/paper/consistency-checking-methods-survey.md` §4 trở đi như thể đã là 1 phần của Thảo luận
  chính thức — các đề xuất đó chưa được xác nhận là mới (chính tài liệu đó tự thừa nhận chưa tra
  cứu học thuật đầy đủ) và **hoàn toàn chưa cài đặt**; nếu muốn nhắc, chỉ nhắc ngắn 1 câu ở cuối
  4.3 như "hướng mở rất xa", không khai triển.
