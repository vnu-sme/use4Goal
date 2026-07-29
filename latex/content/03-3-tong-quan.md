# 3.3. Tổng Quan Phương Pháp

File LaTeX tương lai: `\subsection{Tổng quan phương pháp}\label{sec:overview}`.

## Mục tiêu của phần

Cho người đọc 1 bức tranh toàn cảnh pipeline **trước khi** đi vào chi tiết từng thành phần ở
3.4–3.7 — trả lời "đầu vào là gì, đầu ra là gì, dữ liệu chảy qua các bước nào" bằng 1 hình kiến
trúc duy nhất. Không giải thích thuật toán chi tiết ở đây.

## Cấu trúc heading đề xuất

Không cần subsection con — 1 hình kiến trúc (`fig:pipeline`) + 1 đoạn văn dẫn giải hình + 1 bảng
liệt kê 4 file đầu vào/vai trò.

## Nội dung cốt lõi

### Bốn đầu vào

Phương pháp nhận đúng 4 file làm đầu vào (đúng như chữ ký `AclBpmnIStarConformanceChecker.check`
và `AclBpmnIStarConformanceMain`):

| File | Nội dung | Vai trò trong pipeline |
|---|---|---|
| `.acl` | Cấu trúc tổ chức: entity, role, group, cardinality, compatibility | Nguồn sinh mô hình đối tượng USE (3.6) |
| `.soil` | 1 kịch bản khởi tạo/thực thi cụ thể trên object graph | Nguồn sinh vết thực thi checkpoint (3.7) |
| `.istar` | Goal model: actor, refinement, contribution, dependency, OCL guard | Nguồn đánh giá độ thoả goal tại checkpoint cuối (3.7) |
| `.bpmn2` | Tiến trình: activity với `pre`/`post`/`effect` OCL | Nguồn sinh thứ tự thực thi + điều kiện kiểm tại từng checkpoint (3.7) |

### Năm bước xử lý

Mô tả bằng 1 hình pipeline tuyến tính, đúng thứ tự lệnh gọi trong
`AclBpmnIStarConformanceChecker.check`:

```
.acl ──[AclCompiler]──► AclModel ──[AclUseTranslator]──► .use (sinh tự động)
                                                              │
.bpmn2 ─[Bpmn2Compiler]──► Bpmn2Model ──[topo order + effect]──► kế hoạch thực thi (.soil mở rộng)
                                                              │
.soil (ban đầu) ────────────────────────────────────────────►│
                                                              ▼
                                          [IStarUseTraceCompiler]
                                     (nạp .istar + .use sinh ra + soil mở rộng)
                                                              │
                                                              ▼
                                    vết checkpoint (mỗi dòng SOIL = 1 checkpoint,
                                    gồm system state + marking Goal/Task từng actor)
                                                              │
                        ┌─────────────────────────────────────┼─────────────────────────────────────┐
                        ▼                                     ▼                                     ▼
             kiểm bất biến ACL/USE               kiểm pre/post OCL của BPMN            kiểm goal gốc i* tại
             tại checkpoint khởi tạo              tại checkpoint pre/post              checkpoint cuối cùng
             và sau mỗi effect (3.7)              của từng activity (3.7)              (3.7)
                        │                                     │                                     │
                        └─────────────────────────────────────┼─────────────────────────────────────┘
                                                              ▼
                                    Result { aclFailures, bpmnFailures, goalFailures }
                                    conformant() = 3 danh sách đều rỗng
```

- Nhấn mạnh **điểm hội tụ trung tâm**: cả 3 nhánh kiểm tra cuối cùng đều đọc trên **cùng 1 chuỗi
  checkpoint**, sinh ra từ **cùng 1 hệ thống đối tượng USE** — đây chính là hiện thực hoá của "miền
  ngữ nghĩa thực thi chung" đã nêu ở Motivation (Tình huống 3).
- Nêu rõ đầu ra `Result` có 3 danh sách lỗi tách biệt theo loại (`aclFailures`, `bpmnFailures`,
  `goalFailures`), không gộp chung thành 1 verdict nhị phân duy nhất — người dùng biết chính xác
  **lỗi thuộc chiều nào** trong 3 chiều, phục vụ trực tiếp công cụ gỡ lỗi ở Phần 6.

### Vị trí của từng subsection còn lại trong pipeline

- 3.4 (Chiến lược tiếp cận) giải thích **vì sao** chọn kiến trúc "dịch về 1 miền ngữ nghĩa chung +
  kiểm trên vết thực thi" thay vì các lựa chọn khác đã liệt kê ở Phần 2.
- 3.5 (Ngôn ngữ ACL) chi tiết hoá khối `.acl`.
- 3.6 (Chuyển đổi ACL sang USE) chi tiết hoá mũi tên `AclUseTranslator`.
- 3.7 (Kiểm tra i* với BPMN) chi tiết hoá toàn bộ phần dưới của hình, từ `IStarUseTraceCompiler`
  tới 3 nhánh kiểm tra.

## Nguồn tham chiếu / cơ sở

- Toàn bộ trình tự bước lấy trực tiếp từ thân hàm `AclBpmnIStarConformanceChecker.check` (dòng
  69–96 của file, đã đọc khi khảo sát mã nguồn) — không suy diễn, bám sát đúng thứ tự lệnh gọi thật
  trong code: `AclCompiler.compile` → `Bpmn2Compiler.compile` → `AclUseTranslator.translate` →
  `executionPlan` → `IStarUseTraceCompiler.compile` → `evaluateAclInvariants` +
  `evaluateBpmnOcl` + `evaluateRootGoals`.
- Cấu trúc `Result` (3 danh sách lỗi tách biệt + `ok()`/`conformant()`):
  `AclBpmnIStarConformanceChecker.java` (record `Result`, dòng 53–65).
- CLI tham chiếu thứ tự tham số `acl soil istar bpmn2`: `AclBpmnIStarConformanceMain.java`.

## Ghi chú khi viết prose thật

- Hình pipeline nên vẽ dạng sơ đồ khối chuẩn (không cần giữ nguyên dạng ASCII trên) — dùng TikZ
  hoặc draw.io export, đặt `\label{fig:pipeline}` và tham chiếu ngay ở câu mở đầu subsection.
- Tránh lặp lại y nguyên tên hàm/class Java trong prose chính — giữ tên class trong `\texttt{}` chỉ
  ở phần chú thích hình hoặc footnote, còn văn bản chính mô tả bằng ngôn ngữ khái niệm ("bộ dịch
  ACL sang USE", "bộ biên dịch vết thực thi") để giữ văn phong học thuật, không đọc như tài liệu kỹ
  thuật nội bộ.
- Bảng 4 đầu vào nên đặt trước hình, hình đặt sau — theo đúng thứ tự đọc tự nhiên (input trước,
  luồng xử lý sau).
