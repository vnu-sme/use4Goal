# 6. Công Cụ Hỗ Trợ

File LaTeX tương lai: `\section{Công cụ hỗ trợ}\label{sec:tool-support}`.

## Mục tiêu của phần

Chứng minh phương pháp ở Phần 3 không chỉ là thiết kế trên giấy mà đã tích hợp thành công cụ dùng
được, gắn trực tiếp vào 1 môi trường mô hình hoá có sẵn (USE) thay vì 1 script CLI rời rạc — đây là
bằng chứng hỗ trợ cho tính khả thi đã tuyên bố ở Đóng góp 3 (Phần 1).

## Cấu trúc heading đề xuất

```
\subsection{Kiến trúc plugin trong USE}
\subsection{Hai chế độ kiểm tra}
\subsection{Trình gỡ lỗi trực quan từng bước}
```

## Nội dung cốt lõi từng mục

### 6.1. Kiến trúc plugin trong USE

- Công cụ được xây như 1 plugin (`goal`) tích hợp vào USE (UML-based Specification Environment)
  \cite{gogolla2007use}, tuân theo đúng quy ước kiến trúc lõi của USE: mỗi ngôn ngữ hỗ trợ đều đi
  qua cùng 1 chuỗi giai đoạn `source file → parser → AST → metamodel (MM) → semantic processing →
  view/analysis`, mô phỏng lại đúng cách USE core tự tổ chức `org.tzi.use.parser.use`/`.ocl`/
  `.soil`.
- Plugin áp dụng đúng chuỗi giai đoạn này cho **4 ngôn ngữ**: GOAL/i* (`org.vnu.sme.goal.istar`),
  OCL nhúng trong GOAL, BPMN (`org.vnu.sme.goal.bpmn2`), và ACL (`org.vnu.sme.goal.acl`) — mỗi
  ngôn ngữ có compiler, gói AST, gói MM, và view riêng, độc lập nạp qua các action riêng
  (`ActionOpenGOAL`, `ActionOpenBPMN`, `ActionOpenAcl`...).
- Nêu 1 hình kiến trúc lớp (`fig:plugin-architecture`), phỏng theo đúng sơ đồ nạp mô hình đã có
  trong tài liệu kiến trúc dự án — không tự vẽ mới, chuyển thể trực tiếp từ tài liệu nội bộ.

### 6.2. Hai chế độ kiểm tra

- **Chế độ hàng loạt (batch)**: `ActionCheckConformance` — nhận 4 file `.acl`/`.soil`/`.istar`/
  `.bpmn2` (đúng như CLI `AclBpmnIStarConformanceMain`, 3.3), chạy toàn bộ pipeline 1 lần, hiển thị
  kết quả `Result` gồm 3 danh sách lỗi (3.7.4) trong 1 form báo cáo tổng hợp (`ConformanceForm`) —
  phù hợp khi người dùng chỉ cần biết "có tương thích hay không, sai ở đâu", không cần theo dõi
  từng bước.
- **Chế độ gỡ lỗi từng bước (step-by-step)**: `ActionStepConformance` — mở `StepConformanceForm`,
  công cụ chính được trình bày chi tiết ở 6.3.
- Cả 2 action được đăng ký vào khung plugin action của USE qua `useplugin.xml`, hiện thực
  `IPluginActionDelegate` — nghĩa là xuất hiện như 1 menu/toolbar item bình thường trong giao diện
  USE, không phải cửa sổ tách rời ngoài môi trường.

### 6.3. Trình gỡ lỗi trực quan từng bước

- Đây là đóng góp công cụ đáng chú ý nhất — không chỉ chạy pipeline rồi in kết quả, mà cho phép
  **phát lại (replay)** vết checkpoint đã biên dịch ở 3.7.2 theo từng activity, với giao diện chia
  đôi: `IStarView` (trạng thái goal i*) bên trái, `Bpmn2View` (trạng thái thực thi BPMN) bên phải,
  đồng bộ theo cùng 1 checkpoint hiện hành.
- Mỗi bước điều hướng (`previousButton`/`nextButton`) tương ứng đúng 1 `ActivityStep` đã xây ở
  3.7.1 — khi người dùng bấm "tiến 1 bước", form cập nhật đồng thời:
  - `IStarView`: tô màu badge (`NodeBadge`) từng Goal/Task theo đúng 4 trạng thái đã định nghĩa ở
    3.2.1/3.7.3 (`FULFILLED` xanh lá, `PENDING` vàng cam, sai/`false` đỏ, `UNKNOWN` xám).
  - `Bpmn2View`: đánh dấu activity vừa hoàn thành và activity kế tiếp, phản ánh đúng token position
    tại checkpoint đó.
- Ý nghĩa sư phạm/thực tiễn: người dùng không cần đọc log lỗi dạng text (`aclFailures`/
  `bpmnFailures`/`goalFailures` như ở chế độ batch) mà có thể **thấy trực tiếp** đúng thời điểm và
  đúng vị trí trên 2 diagram nơi trạng thái goal lệch khỏi kỳ vọng — hiện thực hoá đúng nhu cầu đã
  nêu ở Motivation (Phần 3.1): mỗi loại lỗi có "người chịu trách nhiệm sửa" riêng, và người đó cần
  nhìn thấy lỗi trên đúng loại diagram họ quen thuộc (i* cho requirement engineer, BPMN cho
  business analyst).
- Nêu rõ giới hạn hiện tại của UI (chuẩn bị dẫn vào Phần 5 nếu cần trỏ chéo): form hiện chưa hiển
  thị trực tiếp vi phạm bất biến ACL trên 1 diagram ACL riêng — chỉ có 2 view (i*/BPMN), chưa có
  view thứ 3 cho ACL trong cùng trình gỡ lỗi từng bước, dù ACL đã có view riêng (`AclView`) ở chế
  độ xem model thông thường.

## Nguồn tham chiếu / cơ sở

- 6.1: toàn bộ `goal/docs/ARCHITECTURE.md` (đặc biệt §1 "Scope", §3 "Language Pipelines", §4
  "Package Layout") — mô tả kiến trúc pipeline 4 ngôn ngữ, dù tài liệu gốc liệt kê 3 (GOAL/OCL/
  BPMN) và chưa cập nhật ACL vào chính văn bản đó; cần đối chiếu thêm hoặc tự bổ sung khi viết bản
  thảo thật vì `ARCHITECTURE.md` có thể đã lỗi thời so với việc ACL đã được thêm vào sau.
- 6.2: `ActionStepConformance.java` (mở `StepConformanceForm`); tên `ActionCheckConformance`,
  `ConformanceForm` xuất hiện trong import của `StepConformanceForm.java` (dòng 45) — xác nhận tồn
  tại chế độ batch riêng biệt, cần đọc trực tiếp `ActionCheckConformance.java`/`ConformanceForm.java`
  trước khi viết chi tiết 6.2 (chưa đọc thân 2 file này trong phiên khảo sát này).
- 6.3: `StepConformanceForm.java` (toàn bộ phần đã đọc: constructor, `buildUI`, khai báo
  `IStarView`/`Bpmn2View`/màu badge `C_FULFILLED`/`C_PENDING`/`C_FALSE`/`C_UNKNOWN` dòng 63–118).
- Xác nhận đây là công cụ đang phát triển tích cực, không phải đã hoàn thiện từ lâu:
  `ActionStepConformance.java` và `StepConformanceForm.java` là **file chưa được `git add`**
  (untracked, theo `git status` đầu phiên) — nên nêu trong bài báo (nếu phù hợp venue) rằng đây là
  công cụ đang trong giai đoạn phát triển, không phải sản phẩm đã ổn định lâu dài.

## Ghi chú khi viết prose thật

- Cần chụp ảnh màn hình thật (screenshot) của `StepConformanceForm` đang chạy trên ví dụ
  `MeetingScheduler` để làm hình minh hoạ chính (`fig:step-debugger`) — không dùng hình dựng/mockup,
  vì đây là phần chứng minh tính khả thi thực tế, hình ảnh thật có giá trị thuyết phục cao hơn.
- Trước khi hoàn thiện 6.2, đọc thêm `ActionCheckConformance.java` và `ConformanceForm.java` để mô
  tả chính xác giao diện báo cáo hàng loạt (hiện dàn ý chỉ suy luận từ tên class và import, chưa
  xác nhận chi tiết UI).
- Đoạn cuối 6.3 (giới hạn UI, chưa có ACL view trong debugger) nên cân nhắc chuyển sang liệt kê ở
  Phần 5 (Hạn chế) thay vì để trong Công cụ hỗ trợ, tuỳ bố cục cuối cùng — hiện đặt tạm ở đây vì
  liên quan trực tiếp tới nội dung 6.3, nhưng có thể tách ra nếu Phần 5 cần thêm 1 mục về giới hạn
  công cụ.
