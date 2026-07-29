# 3.2. Kiến Thức Nền

File LaTeX tương lai: `\subsection{Kiến thức nền}\label{sec:background}`.

## Mục tiêu của phần

Trang bị đủ khái niệm để đọc hiểu Phần 3.3–3.7 mà không cần tra thêm tài liệu ngoài. Chỉ trình bày
những gì Phần 3 thật sự dùng đến — không viết background bách khoa toàn thư về i*/BPMN/OCL nói
chung. Mỗi khái niệm nêu ra phải được dùng lại ít nhất 1 lần ở 3.5–3.7.

## Cấu trúc heading đề xuất

```
\subsubsection{Mô hình ý định i*}
\subsubsection{Mô hình tiến trình BPMN}
\subsubsection{OCL và môi trường USE}
```

## Nội dung cốt lõi từng mục

### 3.2.1. Mô hình ý định i*

- Định nghĩa i* là ngôn ngữ mô hình hoá hướng mục tiêu \cite{yu1997towards}: actor (`Actor`,
  `Agent`, `Role`), phần tử ý định (`Goal`, `Task`, `Quality`, `Resource`), và quan hệ refinement
  (`AND`, `OR`, `for` — lượng từ hoá theo mọi thực thể 1 actor type, `pick` — lượng từ hoá theo ít
  nhất 1 thực thể).
- Nêu contribution link (`make`/`help`/`hurt`/`break`...) nối 1 phần tử ý định tới 1 quality.
- Nêu `depend`: 1 actor phụ thuộc actor khác qua 1 task/goal trung gian.
- Giải thích ngắn gọn AND/OR refinement bằng đúng ví dụ `mtg.istar` đã dùng ở Motivation: goal
  `HaveMeetingOrganized` AND của `HaveSchedulingPerformedByOrganizer` và `MeetingAttendedByParticipant`
  — tái dùng ví dụ, không bịa ví dụ mới.
- Nêu khái niệm **trạng thái goal/task** dùng xuyên suốt Phần 3.7: mỗi Goal/Task có thể ở 1 trong 3
  trạng thái `UNKNOWN`/`FULFILLED`/`PENDING`, theo đúng định nghĩa marking Δ×Δ giới hạn còn
  `{(?,?), (⊤,⊥), (⊤,⊤)}` của \cite{caballero2026alig} — dự án hiện thực hoá lại chính 3 giá trị
  này (không phải phát minh mới), xem `GoalTaskStatus.java`.

### 3.2.2. Mô hình tiến trình BPMN

- Định nghĩa BPMN là ký hiệu chuẩn hoá cho mô hình tiến trình nghiệp vụ \cite{omg2013bpmn}: pool,
  lane, activity (task/sub-process), event (start/end), gateway (`XOR`/`AND`/`OR`), sequence flow.
- Nêu rõ: bài báo dùng 1 **phương ngữ BPMN đơn giản hoá** (không phải đặc tả BPMN 2.0 XML đầy đủ) —
  chỉ mô tả điều kiện trạng thái Boolean, không có SOIL/domain-state mutation bên trong bản thân
  BPMN. Mỗi activity có tối đa `pre`, `post` (điều kiện OCL Boolean) và `effect` (thay đổi trạng
  thái, tuỳ chọn); mỗi sequence flow rời khỏi gateway có `guard` OCL.
- Nêu ngữ nghĩa gateway: XOR chọn nhánh guard đúng đầu tiên theo thứ tự khai báo ("first satisfied
  branch wins"), AND/OR nhận mọi nhánh có guard đúng.
- Nêu khái niệm token executor tuyến tính (`Bpmn2ExecutionEngine`): `start` → lặp `begin`/
  `complete` cho từng activity → route theo gateway — đây chính là cơ chế Phần 3.7 tái sử dụng để
  xây **vết thực thi** (execution trace), không phải cơ chế duyệt-mọi-nhánh song song.

### 3.2.3. OCL và môi trường USE

- USE (UML-based Specification Environment) là công cụ hình thức hoá mô hình UML class diagram và
  ràng buộc OCL, cho phép tạo hệ thống đối tượng cụ thể (object system/system state) và kiểm tra
  bất biến (`inv`), tiền/hậu điều kiện (`pre`/`post`) trên hệ thống đó \cite{gogolla2007use}.
- Nêu 2 khái niệm USE mà bài báo dùng trực tiếp ở Phần 3.6–3.7:
  - **`.use` model**: đặc tả lớp/quan hệ/kế thừa/bất biến — đích của phép dịch ACL→USE.
  - **SOIL**: ngôn ngữ script mệnh lệnh của USE để tạo đối tượng, gán liên kết, thay đổi thuộc
    tính trên 1 system state — dùng làm "kịch bản thực thi cụ thể" đầu vào cho Phần 3.7 (ví dụ
    `mtg.soil`).
- Nêu khái niệm **system state** (`MSystemState`) và việc OCL boolean expression có thể được biên
  dịch (`OCLCompiler`) và đánh giá (`eval`) trên 1 system state cụ thể tại 1 thời điểm — đây là cơ
  chế duy nhất mà Phần 3.7 dùng để kiểm cả 3 loại điều kiện (bất biến ACL, `pre`/`post` BPMN, độ
  thoả goal i*), không cần 3 evaluator riêng biệt.

## Nguồn tham chiếu / cơ sở

- 3.2.1: cấu trúc AST/MM của GOAL — `goal/docs/METAMODELS.md` §2; ví dụ `HaveMeetingOrganized` —
  `goal/src/main/resources/examples/mtg/mtg.istar`; định nghĩa `GoalTaskStatus` —
  `goal/src/main/java/org/vnu/sme/goal/conformance/semantics/GoalTaskStatus.java`.
- 3.2.2: ngữ nghĩa `pre`/`post`/`guard`/`effect` và token executor — `goal/docs/BPMN2_OCL_OPTION2.md`
  toàn bộ (đặc biệt mục "Gateway execution" và "Execution boundary").
- 3.2.3: vai trò `.use`/SOIL/system state trong pipeline kiểm tra — quan sát trực tiếp từ chữ ký
  hàm trong `AclBpmnIStarConformanceChecker.java` (`Files.writeString(generatedUse, ...)`,
  `executionSoil`, `MSystemState`) và `IStarUseTraceCompiler.java`.

## Ghi chú khi viết prose thật

- Không đưa cú pháp ACL vào mục này — ACL có subsection riêng ở 3.5, tách biệt có chủ đích vì đây
  là đóng góp của bài báo, không phải kiến thức nền có sẵn.
- 3.2.1 và 3.2.2 nên dùng đúng thuật ngữ tiếng Anh giữ nguyên trong ngoặc lần đầu xuất hiện
  (`refinement`, `contribution`, `gateway`...) theo quy ước phổ biến của venue mục tiêu — quyết
  định cụ thể (giữ tiếng Anh hay dịch hẳn) nên thống nhất 1 lần rồi áp dụng toàn bài, tránh trộn
  lẫn.
- Tránh trình bày OCL như 1 ngôn ngữ mới cần dạy từ đầu — giả định người đọc venue mục tiêu đã biết
  OCL cơ bản, chỉ nhấn khái niệm USE-specific (system state, SOIL) thật sự mới với người đọc ngoài
  cộng đồng USE.
