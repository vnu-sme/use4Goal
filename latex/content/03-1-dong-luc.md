# 3.1. Ví Dụ Minh Hoạ

File LaTeX tương lai: `\subsection{Ví dụ minh hoạ}\label{sec:motivation}`.

## Mục tiêu của phần

Không giải thích lý thuyết ở đây — chỉ dùng **1 ví dụ cụ thể, chạy được thật** để người đọc tự
thấy: (1) 3 mô hình độc lập đều "đúng cục bộ" theo tiêu chuẩn riêng của chúng, (2) vẫn có ít nhất 1
lỗi thật chỉ lộ ra khi xét cả 3 mô hình cùng lúc, (3) không phương pháp nào ở Phần 2 phát hiện được
lỗi đó một mình. Ví dụ này được dùng lại xuyên suốt Phần 3.3–3.7, không đổi sang ví dụ khác.

## Cấu trúc heading đề xuất

Không cần subsection con — 1 đoạn giới thiệu bối cảnh + 1 hình kiến trúc 3 mô hình + 2–3 tình
huống lỗi minh hoạ, trình bày dạng liệt kê ngắn hoặc kịch bản kể chuyện.

## Ví dụ đã chọn: MeetingScheduler

Ví dụ lấy nguyên trạng từ `goal/src/main/resources/examples/mtg/` — không phải ví dụ giả định,
đã tồn tại và chạy được trong repo với đầy đủ 4 file `.acl`/`.istar`/`.bpmn2`/`.soil`.

**Bối cảnh nghiệp vụ**: một đơn vị tổ chức cuộc họp (`MeetingUnit`) gồm 4 loại vai trò: người khởi
xướng (`Initiator`), người điều phối lịch (`Organizer`), thư ký (`Secretary`, tuỳ chọn), và người
tham dự (`Participant`, 2 trở lên). Quy trình: `Initiator` chốt nội dung cuộc họp, `Organizer` thu
thập lịch rảnh của từng `Participant` (trực tiếp qua lịch điện tử, hoặc nhờ `Secretary` gọi điện
cho những người không có lịch điện tử), chọn thời gian, rồi thông báo; mỗi `Participant` sau đó
tham dự.

### Ba lát cắt độc lập của cùng 1 hệ thống

1. **ACL** (`mtg.acl`) đặc tả *ai được phép đóng vai trò gì*: `Initiator` tương thích với
   `Participant` trong cùng group; `Organizer` tương thích với `Secretary` trong cùng group; mọi
   cặp vai trò khác **mặc định không tương thích** (nguyên tắc "không khai báo = incompatible" của
   ACL, xem 3.5). Đặc biệt: `Secretary` và `Participant` **không** được khai báo tương thích.
2. **i\*** (`mtg.istar`) đặc tả *hệ thống tồn tại để đạt được gì*: goal gốc `HaveMeetingOrganized`
   chỉ thoả khi cả `HaveSchedulingPerformedByOrganizer` (lịch đã chốt) và
   `MeetingAttendedByParticipant` (mọi người tham dự) đều thoả, với 2 quality phụ trợ
   `Inclusivity` (thu lịch qua điện thoại cho người không có lịch điện tử) và `QuickScheduling`
   (thu lịch qua lịch điện tử — nhanh hơn).
3. **BPMN2** (`mtg.bpmn2`) đặc tả *hệ thống làm gì, theo thứ tự nào*: 3 lane
   (`Initiator`/`Organizer`/`Secretary`) với activity `decideMeetingDetails → checkCalendar` /
   `requestSecretaryCall → chooseTimeAndDate → announceMeeting`, mỗi activity có `pre`/`post`/
   `effect` OCL riêng.

### Tình huống 1 — Lỗi phân công vai trò mà cả i* và BPMN đều không thấy

Giả sử người vận hành phân công cùng 1 agent vừa là `Secretary` vừa là `Participant` trong cùng
`MeetingUnit` (ví dụ để tiết kiệm nhân sự cho 1 cuộc họp nhỏ). Xét riêng từng mô hình:

- BPMN không có khái niệm "agent nào đóng vai trò nào" — activity `collectConstraintsByPhone`
  (lane `Secretary`) và activity liên quan tới `Participant` chỉ tham chiếu đúng theo `pre`/`post`
  OCL trên object `Participant`/`Meeting`, hoàn toàn hợp lệ dù object đó có "kiêm nhiệm" gì khác.
- i* cũng không phát hiện gì bất thường — mọi goal/task vẫn được đánh giá qua OCL guard đúng theo
  actor type đã khai (`Secretary`, `Participant`), không có ràng buộc "1 agent không được vừa là
  X vừa là Y".
- Chỉ ACL, cụ thể là bất biến `NoConflict_Secretary_Participant` được sinh tự động trong quá trình
  dịch sang USE (xem 3.6), mới phát hiện được vi phạm này — vì `Secretary <-> Participant` không
  nằm trong danh sách compatibility đã khai báo.

Đây chính là bằng chứng cho RQ1: 1 lỗi thật, chỉ lộ ra khi kiểm tra ACL, nhưng ACL đơn thân không
đủ để nói "hệ thống đúng" — nó chỉ trả lời được câu hỏi cấu trúc tổ chức, không trả lời được câu
hỏi "goal `HaveMeetingOrganized` có đạt hay không".

### Tình huống 2 — BPMN cục bộ đúng nhưng goal gốc không đạt

Xét task `announceMeeting` (lane `Organizer`): tiền điều kiện chỉ yêu cầu
`Meeting.allInstances()->exists(m | m.timeChosen)`, hậu điều kiện chỉ yêu cầu mọi `Participant`
có `notified = #sent`. Về mặt cục bộ, task này hoàn toàn hợp lệ nếu chạy đúng thứ tự sau
`chooseTimeAndDate`. Nhưng goal gốc `HaveMeetingOrganized` trong i* còn phụ thuộc nhánh
`MeetingAttendedByParticipant` (mọi `Participant` phải có `attendance = #attended`, thoả qua task
`participate` — nằm ngoài phạm vi các activity đã liệt kê ở lane `Organizer`/`Initiator`). Nếu vết
thực thi BPMN dừng lại sau `announceMeeting` (ví dụ không ai thật sự tham dự), **mọi `pre`/`post`
OCL của từng activity BPMN đều thoả**, nhưng goal gốc i* vẫn ở trạng thái chưa `FULFILLED` tại
checkpoint cuối. Đây là bằng chứng cho việc chỉ kiểm BPMN cục bộ (nhóm 2.3–2.4 ở Phần 2) là không
đủ — cần đối chiếu với trạng thái goal i* tại checkpoint cuối cùng của vết thực thi.

### Tình huống 3 — Vì sao cần đúng 1 miền ngữ nghĩa thực thi chung

Cả 2 tình huống trên đều tham chiếu tới **cùng 1 object graph** (`Meeting`, `Participant`,
`Secretary`...). Nếu ACL, i*, và BPMN được kiểm tra bằng 3 công cụ/formalism tách biệt (như hầu hết
nhóm ở Phần 2 làm), sẽ phải tự đồng bộ 3 bản sao trạng thái hệ thống bằng tay — chính nguồn lỗi mà
nhóm 2.1 (ánh xạ tường minh) đã chỉ ra. Động lực trực tiếp cho lựa chọn thiết kế ở Phần 3.4: dịch
ACL thành 1 mô hình đối tượng USE duy nhất, để cả 3 mô hình cùng đọc/ghi trên 1 hệ thống trạng thái
thật, tránh việc phải đồng bộ tay.

## Nguồn tham chiếu / cơ sở

- Toàn bộ chi tiết vai trò/cardinality/compatibility: `goal/src/main/resources/examples/mtg/mtg.acl`.
- Toàn bộ cấu trúc goal/quality/dependency: `goal/src/main/resources/examples/mtg/mtg.istar`.
- Toàn bộ activity/pre/post/effect: `goal/src/main/resources/examples/mtg/mtg.bpmn2`.
- Xác nhận đây là ví dụ đang phát triển tích cực (không phải ví dụ tĩnh, ổn định lâu): `git status`
  đầu phiên làm việc cho thấy `mtg_mtg.acl_default.dlt` và `mtg_shadow_default.clt` đang bị sửa
  đổi trong working tree.

## Ghi chú khi viết prose thật

- Nên có 1 hình (`\label{fig:mtg-overview}`) vẽ 3 khối ACL/i*/BPMN với vài phần tử tiêu biểu
  (`Secretary`, `Participant`, `HaveMeetingOrganized`, `announceMeeting`) và mũi tên chỉ ra chỗ mỗi
  tình huống lỗi xảy ra — tham chiếu hình này ngay khi kể Tình huống 1/2.
- Tình huống 1 và 2 nên trình bày như 2 đoạn văn kể chuyện (narrative), không dùng bảng khô khan —
  đúng tinh thần "motivation" là thuyết phục bằng câu chuyện cụ thể, để lại ấn tượng, không phải
  liệt kê đặc tả.
- Không đưa thuật toán/pseudocode vào phần này — pseudocode của 3.7 mới trình bày cách phát hiện
  Tình huống 1/2 một cách hình thức.
