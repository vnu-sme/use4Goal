# 3.7. Kiểm Tra i\* Với BPMN

File LaTeX tương lai: `\subsection{Kiểm tra i* với BPMN}\label{sec:checking}`.

Đây là subsection dài và quan trọng nhất của Phần 3 — trình bày thuật toán trung tâm của bài báo.
Nên cân nhắc tách thành `\subsubsection` rõ ràng như dàn ý dưới, mỗi `\subsubsection` có thể kèm 1
đoạn pseudocode/algorithm box riêng.

## Mục tiêu của phần

Trình bày đủ chi tiết để thuật toán **lặp lại được**: từ 4 đầu vào tới `Result` 3 danh sách lỗi,
gồm cả cách xây vết thực thi, cách lan truyền trạng thái goal/task, và cách đánh giá 3 loại điều
kiện tại đúng checkpoint.

## Cấu trúc heading đề xuất

```
\subsubsection{Xây dựng kế hoạch thực thi từ BPMN}
\subsubsection{Biên dịch vết checkpoint}
\subsubsection{Lan truyền trạng thái goal và task}
\subsubsection{Ba phép kiểm trên vết checkpoint}
\subsubsection{Áp dụng lên ví dụ minh hoạ}
```

## Nội dung cốt lõi từng mục

### 3.7.1. Xây dựng kế hoạch thực thi từ BPMN

- Đầu vào: `Bpmn2Model` (đã biên dịch từ `.bpmn2`) và nội dung `.soil` ban đầu.
- Bước 1 — sắp thứ tự activity: dùng **sắp xếp topo** (Kahn's algorithm) trên đồ thị
  activity/gateway/event nối bằng `SequenceFlow`, bắt đầu từ `StartEvent` (hoặc mọi node in-degree
  0 nếu không có start event tường minh). Nêu rõ hạn chế đã có ý thức từ thiết kế: đây **không**
  phải thực thi BPMN thật (không rẽ nhánh theo `guard`, không phân biệt XOR/AND/OR) — chỉ là 1 thứ
  tự tuyến tính hợp lệ về mặt phụ thuộc dữ liệu/luồng, dùng làm khung cho việc nối tiếp các `effect`.
- Bước 2 — nối `effect`: với mỗi activity theo đúng thứ tự đã sắp, nếu có khối `effect {[ ... ]}`
  (SOIL) thì nối thêm 1 dòng SOIL mới vào cuối kịch bản thực thi đang xây, sau khi chuẩn hoá về 1
  dòng vật lý (`replaceAll("\\s+", " ")`) vì bộ biên dịch vết chỉ nhận đúng 1 statement SOIL trên
  1 dòng.
- Đầu ra: 1 `ExecutionPlan` gồm (a) toàn văn SOIL mở rộng (kịch bản `.soil` gốc + các dòng effect
  nối thêm), (b) danh sách `ActivityStep` — mỗi activity gắn với **số thứ tự checkpoint** ngay
  trước (`preCheckpoint`) và ngay sau (`postCheckpoint`) hiệu ứng của nó, (c) checkpoint khởi tạo
  (`initialCheckpoint`, bằng đúng số dòng SOIL hợp lệ trong kịch bản gốc, trước khi nối effect nào).

### 3.7.2. Biên dịch vết checkpoint

- Vai trò: biến 1 kịch bản SOIL thành 1 dãy **checkpoint**, mỗi checkpoint = trạng thái hệ thống
  đối tượng USE tại đúng thời điểm sau khi thực thi xong 1 dòng SOIL, cộng theo dõi trạng thái
  Goal/Task/Quality của i* tại đúng thời điểm đó.
- 2 bước đánh giá mỗi checkpoint (nguyên văn ý tưởng thiết kế, diễn giải lại theo văn phong học
  thuật khi viết thật, không chép nguyên javadoc):
  1. Với mỗi phần tử i* có OCL guard đã biên dịch, đánh giá guard đó **theo từng instance** của
     đúng actor type sở hữu nó, gán kết quả vào marking riêng của instance đó — giống bước `assign`
     trong 1 kịch bản `.iscn` viết tay (ngôn ngữ scenario DSL cũ của dự án).
  2. Với mỗi phần tử i* là con lượng từ hoá của 1 quan hệ `forall`/`pick`, gộp (aggregate) các giá
     trị per-instance ở bước 1 theo đúng ngữ nghĩa lượng từ (`forall` = ALL, `pick` = ANY), rồi gán
     kết quả gộp vào **mọi** instance của actor sở hữu quan hệ đó — để bước lan truyền AND/OR ở
     3.7.3 tiếp tục cuộn lên đúng như đã làm cho `.iscn`.
- Mỗi checkpoint lưu: chỉ số, dòng SOIL tương ứng, `MSystemState` (trạng thái đối tượng USE tại
  thời điểm đó), và `Map<InstanceKey, IStarMarking>` — marking i* riêng cho từng cặp (loại actor,
  tên object).

### 3.7.3. Lan truyền trạng thái goal và task

- Mỗi Goal/Task có trạng thái thuộc `{UNKNOWN, FULFILLED, PENDING}` (3.2.1); mỗi Quality có trạng
  thái Boolean-hoá tương tự.
- Luật lan truyền tái sử dụng đúng ý tưởng ngữ nghĩa marking AND/OR/Make/Break đã được hình thức
  hoá trong \cite{caballero2026alig} (Definition 3.1 — 3 giá trị hợp lệ `{(?,?), (⊤,⊥), (⊤,⊤)}` mà
  dự án hiện thực hoá lại đúng dưới 3 tên `UNKNOWN`/`FULFILLED`/`PENDING`):
  - `AND`-refinement: cha `FULFILLED` khi **mọi** con `FULFILLED`.
  - `OR`-refinement: cha `FULFILLED` khi **ít nhất 1** con `FULFILLED`.
  - `Contribution` dương (`make`/`help`...): quality liên quan được đánh dấu đạt khi phần tử nguồn
    `FULFILLED`; tương tự cho contribution âm.
- Khác biệt cần nêu rõ so với 2.3/\cite{caballero2026alig}: ở đây luật lan truyền chạy **theo từng
  instance actor cụ thể** trên 1 object graph USE thật (vì i* trong dự án cho phép 1 actor type có
  nhiều instance đồng thời, ví dụ nhiều `Participant`), không chạy trên 1 goal model trừu tượng duy
  nhất — đây là lý do cần bước gộp `forall`/`pick` ở 3.7.2 trước khi cuộn AND/OR lên actor cha.

### 3.7.4. Ba phép kiểm trên vết checkpoint

Trình bày dạng 3 đoạn song song, đúng cấu trúc `Result` đã giới thiệu ở 3.3:

**(a) Bất biến ACL/USE** — kiểm tại checkpoint khởi tạo và tại `postCheckpoint` của **mọi**
activity có effect (không kiểm mọi checkpoint, chỉ kiểm các điểm object graph thực sự thay đổi):
dùng thẳng cơ chế `check()` gốc của USE trên `MSystemState` (kiểm mọi `inv` của model, gồm cả các
bất biến `NoConflict_*` sinh tự động ở 3.6.3). Dừng và báo lỗi tại checkpoint **đầu tiên** vi phạm
(fail-fast) — vì mọi checkpoint sau đó có thể đã kế thừa 1 object graph không hợp lệ, không có ý
nghĩa kiểm tiếp.

**(b) Tiền/hậu điều kiện BPMN** — với mỗi `ActivityStep`, biên dịch (`OCLCompiler.compileExpression`)
và đánh giá từng `pre` tại `preCheckpoint`, từng `post` tại `postCheckpoint`, trên đúng
`MSystemState` của checkpoint đó. Không fail-fast — thu thập **toàn bộ** vi phạm để báo cáo đầy đủ,
vì mỗi vi phạm ở đây độc lập theo từng activity, không kế thừa lỗi như trường hợp (a).

**(c) Goal gốc i\*** — xác định tập goal gốc (goal không phải con của bất kỳ `AND`/`OR`/`forall`/
`pick` refinement nào), rồi tại **checkpoint cuối cùng** của vết thực thi, với mọi instance actor
đã xuất hiện, goal gốc phải ở trạng thái `FULFILLED` (bỏ qua nếu vẫn `UNKNOWN` — actor đó chưa từng
được nhắc tới trong kịch bản, không tính là lỗi). Chỉ kiểm tại checkpoint cuối, không kiểm dọc
đường — khác biệt có chủ đích so với (a)/(b), phản ánh đúng ngữ nghĩa "goal là đích cuối cùng cần
đạt, không phải bất biến phải đúng mọi lúc" (khác bản chất với `inv` ở (a)).

### 3.7.5. Áp dụng lên ví dụ minh hoạ

- Đi lại đúng 3 tình huống đã kể ở Motivation (3.1), lần này bằng ngôn ngữ hình thức của 3.7.1–3.7.4:
  - Tình huống 1 (Secretary/Participant): phát hiện bởi phép kiểm (a) tại checkpoint gán vai trò
    cho agent — `aclFailures` không rỗng, `bpmnFailures`/`goalFailures` không liên quan.
  - Tình huống 2 (BPMN cục bộ đúng, goal gốc không đạt): phép kiểm (b) trả về rỗng (mọi `pre`/
    `post` activity đã liệt kê đều thoả), nhưng phép kiểm (c) phát hiện `HaveMeetingOrganized`
    chưa `FULFILLED` tại checkpoint cuối — minh hoạ trực tiếp lý do cần tách riêng 3 phép kiểm
    (3.4.3), vì (b) một mình sẽ báo "PASS" sai lệch.
- Đây là chỗ thích hợp để đặt 1 bảng checkpoint-by-checkpoint cụ thể (giống phong cách trace tay đã
  làm cho case study `construction_permit` trong `conformance-istar-bpmn2.md` §5.2), nhưng dựng lại
  hoàn toàn bằng ngữ nghĩa **đã cài đặt thật** ở đây, không sao chép bảng cũ (bảng cũ dựa trên
  product-LTS chưa từng cài đặt, xem cảnh báo ở [index.md](index.md) mục 4).

## Nguồn tham chiếu / cơ sở

- 3.7.1: hàm `executionPlan`, `executionOrder`, record `ExecutionPlan`/`ActivityStep` — toàn bộ
  trong `AclBpmnIStarConformanceChecker.java` (dòng 104–202).
- 3.7.2: toàn bộ javadoc lớp và chữ ký `compile`/record `Checkpoint` trong
  `IStarUseTraceCompiler.java` (dòng 41–80) — đã đọc phần đầu, cần đọc tiếp phần thân hàm `compile`
  (sau dòng 100) trước khi viết bản thảo thật, để mô tả chính xác cách `MStatement`/`SoilCompiler`
  được dùng cho từng dòng SOIL.
- 3.7.3: `GoalTaskStatus.java` (enum + javadoc trích Definition 3.1 Caballero-Villalobos); lớp
  `IStarPropagation` (tên xuất hiện trong import của `AclBpmnIStarConformanceChecker.java`, chưa
  đọc thân — cần đọc `goal/src/main/java/org/vnu/sme/goal/conformance/semantics/IStarPropagation.java`
  trước khi viết phần luật AND/OR/Make/Break chi tiết bằng pseudocode).
- 3.7.4: hàm `evaluateAclInvariants` (dòng 126–146, fail-fast), `evaluateBpmnOcl`/
  `evaluateOclAtCheckpoint` (dòng 204–247, thu thập toàn bộ), `evaluateRootGoals`/`rootGoalIds`
  (dòng 249–281, chỉ checkpoint cuối) — toàn bộ trong `AclBpmnIStarConformanceChecker.java`.
- 3.7.5: dữ liệu cụ thể lấy lại từ `mtg.acl`/`mtg.istar`/`mtg.bpmn2`/`mtg.soil` — cần **tự chạy**
  `AclBpmnIStarConformanceMain` trên bộ 4 file này để lấy checkpoint/verdict thật trước khi viết
  bảng trace, không suy đoán kết quả.

## Ghi chú khi viết prose thật

- Đây là phần cần verify bằng cách **chạy thật** công cụ trên `mtg.*` trước khi hoàn thiện bản
  thảo — dàn ý này mô tả đúng cơ chế theo mã nguồn đã đọc, nhưng số liệu checkpoint cụ thể ở 3.7.5
  (bao nhiêu checkpoint, giá trị chính xác) phải lấy từ 1 lần chạy thật, không suy diễn.
- Nên có 1 algorithm box (`algorithm`/`algorithmic` package) tổng hợp toàn bộ 3.7.1–3.7.4 thành 1
  pseudocode duy nhất, đặt cuối subsection trước 3.7.5, để người đọc muốn implement lại có 1 điểm
  tham chiếu duy nhất thay vì phải gom từ 4 đoạn văn.
- Giữ đúng ranh giới đã nêu ở 3.4.2: không dùng từ "duyệt mọi nhánh"/"reachability" khi mô tả cơ
  chế này — đây là điểm dễ viết sai nhất nếu lỡ tay chép lại giọng văn của
  `conformance-istar-bpmn2.md` (tài liệu đó mô tả 1 thiết kế khác, chưa cài đặt).
