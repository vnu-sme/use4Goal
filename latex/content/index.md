# Dàn Ý Bài Báo

Tiêu đề tạm thời: Kiểm Chứng Tương Thích Giữa Mô Hình Ý Định i* và Mô Hình Tiến Trình BPMN Qua Ngôn Ngữ Cấu Trúc Tổ Chức ACL Trên Miền Ngữ Nghĩa USE/OCL

Tên tắt đề xuất: ACL-i*-BPMN Conformance Checking, hoặc gọi tắt trong bài là AIB-Check.

## 1. Trạng Thái Tài Liệu

Đây là dàn ý (outline) từng phần của bài báo, chưa phải bản thảo hoàn chỉnh. Mỗi file dưới đây
tương ứng một section/subsection LaTeX tương lai, gồm: mục tiêu của phần, cấu trúc heading đề
xuất, nội dung cốt lõi cần trình bày, và nguồn tham chiếu cụ thể (file mã nguồn hoặc tài liệu
trong repo, cùng khoá trích dẫn học thuật đề xuất). Khi bắt đầu viết prose thật, dùng các skill
`latex-rules` và `citation-rules` đã cấu hình sẵn cho dự án.

**Việc còn thiếu trước khi viết bản thảo thật:**

- Chưa có `references.bib` trong `latex/`. Các khoá trích dẫn đề xuất trong các file dàn ý (dạng
  `\cite{dang2010jucs}`, `\cite{caballero2026alig}`, ...) được suy ra từ ghi chú trong
  `doc/paper/conformance-istar-bpmn2.md` và `doc/paper/consistency-checking-methods-survey.md`.
  Cần tự tra cứu lại thông tin xuất bản chính xác (venue, năm, số trang, DOI) trước khi đưa vào
  bibliography chính thức — xem ghi chú tự phê phán ở đầu hai file đó về việc tác giả AI chưa
  chạy tra cứu học thuật đầy đủ (Scopus/DBLP/Google Scholar).
- Chưa có khung sườn `.tex` (`main.tex`, `references.bib`) trong `latex/paper/` — thư mục hiện
  trống.

## 2. Bố Cục Bài Báo Và File Dàn Ý Tương Ứng

| # | Section LaTeX dự kiến | File dàn ý | Vai trò |
|---|---|---|---|
| 1 | `\section{Giới thiệu}` | [01-gioi-thieu.md](01-gioi-thieu.md) | Bối cảnh, hiện trạng, hạn chế, đóng góp, research question, bố cục bài báo |
| 2 | `\section{Công trình liên quan}` | [02-cong-trinh-lien-quan.md](02-cong-trinh-lien-quan.md) | Danh sách 20 bài (đã đọc SURVEY nội bộ + 4 bài Tier 1 toàn văn), gộp thành 5 nhóm cơ chế, mạnh/yếu từng nhóm, khoảng trống |
| 2.0 | (bảng kỹ thuật hỗ trợ, không phải section riêng) | [02-0-bang-so-sanh-9-phuong-phap.md](02-0-bang-so-sanh-9-phuong-phap.md) | Bảng so sánh chi tiết 9 phương pháp Tier 1 theo 4 trục: goal lang./process lang./chất lượng-softgoal/phương pháp cụ thể (định nghĩa, luật chuyển, input, output, thuật toán); có việc-cần-làm chưa giải quyết |
| 3 | `\section{Phương pháp đề xuất}` | (section cha, không có file riêng — xem 7 subsection bên dưới) | |
| 3.1 | `\subsection{Ví dụ minh hoạ}` | [03-1-dong-luc.md](03-1-dong-luc.md) | Motivation: ví dụ `MeetingScheduler` xuyên suốt bài báo |
| 3.2 | `\subsection{Kiến thức nền}` | [03-2-kien-thuc-nen.md](03-2-kien-thuc-nen.md) | i*, BPMN, OCL/USE — đủ để đọc hiểu phần 3.3–3.7 |
| 3.3 | `\subsection{Tổng quan phương pháp}` | [03-3-tong-quan.md](03-3-tong-quan.md) | Kiến trúc pipeline 4 file đầu vào → verdict |
| 3.4 | `\subsection{Chiến lược tiếp cận}` | [03-4-tiep-can.md](03-4-tiep-can.md) | Vì sao chọn "miền ngữ nghĩa chung + vết thực thi", so với các lựa chọn khác |
| 3.5 | `\subsection{Ngôn ngữ ACL}` | [03-5-acl.md](03-5-acl.md) | Cú pháp/ngữ nghĩa ACL, nền tảng MOISE |
| 3.6 | `\subsection{Chuyển đổi ACL sang USE}` | [03-6-transform-acl2use.md](03-6-transform-acl2use.md) | Luật dịch `AclUseTranslator`, sinh bất biến compatibility |
| 3.7 | `\subsection{Kiểm tra i* với BPMN}` | [03-7-kiem-tra-istar-bpmn.md](03-7-kiem-tra-istar-bpmn.md) | `AclBpmnIStarConformanceChecker` + `IStarUseTraceCompiler`, thuật toán checkpoint |
| 4 | `\section{Thảo luận}` | [04-thao-luan.md](04-thao-luan.md) | Diễn giải kết quả, so sánh lại với related work, các quyết định thiết kế gây tranh cãi |
| 5 | `\section{Hạn chế}` | [05-han-che.md](05-han-che.md) | Giới hạn kỹ thuật và phạm vi đã biết, không che giấu |
| 6 | `\section{Công cụ hỗ trợ}` | [06-ho-tro-cong-cu.md](06-ho-tro-cong-cu.md) | Kiến trúc plugin `goal` trong USE, GUI step-by-step debugger |

Chưa có file riêng cho Kết luận — bổ sung khi bài báo gần hoàn thiện, sau khi đã biết chính xác
kết quả thực nghiệm nào được đưa vào.

**Cập nhật sau góp ý (2026-07-22)**: Section 1 và 2 đã được viết lại theo đúng phản hồi — Introduction
không còn giả định ACL đã được biết trước; bài toán trung tâm là kiểm tra tương thích i*↔BPMN, với
JUCS \cite{dang2010jucs} là nền tảng lý thuyết được nêu tường minh, dẫn tới đúng 2 câu hỏi nghiên
cứu: **RQ1** (phương pháp đặc tả cấu trúc hệ thống trong giai đoạn phân tích — ACL là câu trả lời,
xuất hiện lần đầu ở đây) và **RQ2** (phương pháp kiểm tra nhất quán yêu cầu i*↔giải pháp BPMN —
thuật toán checkpoint là câu trả lời).

**Cập nhật lần 2 (cùng ngày)**: Related Work đọc thêm
`latex/context/SURVEY_kiem_tra_tuong_thich_goal_process.md` (khảo sát nội bộ 16+ bài gốc + N1–N12
bổ sung) và đọc toàn văn 4 bài Tier 1 cốt lõi (`paper_3`, `paper_11`, `paper_4`, `paper_14` trong
`academic-research-skills-main/public/paperSurvey/aligementGOALBPMN/`), chia lại thành **5 nhóm
theo cơ chế kỹ thuật** (20 bài, mỗi nhóm 3–5 bài): (A) suy luận tĩnh trên hình thức luận chung —
gần nhất về kỹ thuật với chính phương pháp đề xuất; (B) ngữ nghĩa vận hành theo vết thực thi — chứa
`dang2010jucs`; (C) căn chỉnh định lượng/liên tục; (D) đối chiếu ràng buộc phẳng bên ngoài;
(E) đặc tả cấu trúc vai trò tổ chức — trả lời RQ1. Phát hiện phụ: `paper_13` trong SURVEY và
`groner2014vadl` dùng ở bản trước nhiều khả năng là **cùng 1 bài** (trùng tiêu đề tuyệt đối), SURVEY
đã đoán nhầm tác giả — đã gộp lại, cần tự xác nhận qua DBLP trước khi trích dẫn chính thức.

## 3. Ví Dụ Xuyên Suốt Đã Chọn: MeetingScheduler

Toàn bộ dàn ý dùng chung 1 case study thật, đã có sẵn đầy đủ 4 file trong repo (không phải ví dụ
giả định) tại `goal/src/main/resources/examples/mtg/`:

| File | Vai trò |
|---|---|
| `mtg.acl` | Cấu trúc tổ chức: entity `PhoneContact`/`Meeting`, role `Initiator`/`Organizer`/`Secretary`/`Participant`, group `MeetingUnit` với cardinality và 2 ràng buộc `compatibility` |
| `mtg.istar` | Goal model: gốc `HaveMeetingOrganized`, các goal/task con theo AND/OR/`forall`/`pick`, quality `Inclusivity`/`QuickScheduling` |
| `mtg.bpmn2` | Tiến trình: 3 lane (`Initiator`, `Organizer`, `Secretary`) với `pre`/`post`/`effect` OCL trên từng task |
| `mtg.soil` | 1 kịch bản thực thi cụ thể (khởi tạo object graph) dùng làm input cho `IStarUseTraceCompiler` |

File này hiện đang **được sửa đổi trong working tree** (`mtg_mtg.acl_default.dlt`,
`mtg_shadow_default.clt` — theo `git status` đầu phiên làm việc), nghĩa là đây đúng là ví dụ đang
được phát triển tích cực, phù hợp để dùng làm motivating example vì tác giả đã quen thuộc nhất.

## 4. Bảng Ánh Xạ Nguồn Grounding Chính

Dùng bảng này để tra nhanh "khẳng định X trong bài báo dựa trên đâu trong code/tài liệu":

| Chủ đề | Nguồn chính trong repo |
|---|---|
| Kiến trúc pipeline goal plugin (GOAL/OCL/BPMN) | `goal/docs/ARCHITECTURE.md` |
| Metamodel AST/MM của GOAL, OCL, BPMN | `goal/docs/METAMODELS.md` |
| Ngữ nghĩa `pre`/`post`/`guard`/`effect` của BPMN2 + engine token | `goal/docs/BPMN2_OCL_OPTION2.md` |
| Đặc tả hình thức ACL (StructuralSpecification, Role, Group, Compatibility...) | `doc/reference/acl.yaml` |
| Ký hiệu đồ hoạ ACL, ma trận quan hệ hợp lệ | `Metamodel/tmp.doc/ACL_doc.md` |
| Grammar ACL cụ thể | `goal/src/main/resources/grammars/ACL.g4` |
| Luật dịch ACL → USE (class, association, invariant compatibility) | `goal/src/main/java/org/vnu/sme/goal/acl/use/AclUseTranslator.java` |
| Bộ kiểm tra tương thích 3 mô hình (thuật toán chính của bài báo) | `goal/src/main/java/org/vnu/sme/goal/conformance/AclBpmnIStarConformanceChecker.java` |
| Bộ biên dịch vết thực thi i*+USE+SOIL thành checkpoint trace | `goal/src/main/java/org/vnu/sme/goal/istarusebridge/IStarUseTraceCompiler.java` |
| Ngữ nghĩa marking Goal/Task (`UNKNOWN`/`FULFILLED`/`PENDING`) | `goal/src/main/java/org/vnu/sme/goal/conformance/semantics/GoalTaskStatus.java` |
| CLI end-to-end 4 file (`acl`, `soil`, `istar`, `bpmn2`) | `goal/src/main/java/org/vnu/sme/goal/conformance/AclBpmnIStarConformanceMain.java` |
| GUI debugger từng bước (dùng cho phần Công cụ hỗ trợ) | `goal/src/main/java/org/vnu/sme/goal/conformance/gui/StepConformanceForm.java` |
| Khảo sát các họ phương pháp kiểm tra tương thích 2 mô hình (nền cho Related Work) | `doc/paper/consistency-checking-methods-survey.md` |
| Thiết kế chi tiết theo khung JUCS + alig + vaDL (dòng D/E trong khảo sát) | `doc/paper/conformance-istar-bpmn2.md` |
| Case study i*/BPMN gốc dùng để đối chiếu (construction permit) | `goal/src/main/resources/examples/construction_permit/` |

**Lưu ý quan trọng khi viết phần Approach/ACL/Transform/Checking**: `conformance-istar-bpmn2.md`
và `consistency-checking-methods-survey.md` mô tả một thiết kế lý thuyết dựa trên **product LTS
và duyệt toàn bộ không gian trạng thái (BFS)** theo khung Caballero-Villalobos. Đây **không phải**
là những gì `AclBpmnIStarConformanceChecker` thực sự cài đặt. Cài đặt thật hiện tại kiểm tra trên
**một vết thực thi cụ thể** (`.soil` + thứ tự topo của BPMN), không duyệt nhánh XOR/OR song song.
Hai tài liệu kia là ghi chú thiết kế/khảo sát ý tưởng, dùng làm nguồn cho Related Work và Thảo
luận (hướng mở rộng tương lai), **không dùng làm mô tả cho thuật toán đã cài đặt** ở mục 3.7.

## 5.5. Bản Thảo LaTeX Thật (Đã Bắt Đầu)

Section 1 và Section 2 đã có bản thảo `.tex` thật (không còn chỉ là dàn ý) tại `latex/paper/`:

| File | Nội dung |
|---|---|
| `latex/paper/index.tex` | Tài liệu chủ — preamble (xelatex/polyglossia), `\input` các section, bibliography. Biên dịch bằng `xelatex`. |
| `latex/paper/01-gioi-thieu.tex` | Bản thảo đầy đủ `\section{Giới Thiệu}`, 5 đoạn văn theo đúng dàn ý ở [01-gioi-thieu.md](01-gioi-thieu.md) |
| `latex/paper/02-cong-trinh-lien-quan.tex` | Bản thảo đầy đủ `\section{Công Trình Liên Quan}`, 5 nhóm + khoảng trống theo đúng dàn ý ở [02-cong-trinh-lien-quan.md](02-cong-trinh-lien-quan.md) |
| `latex/paper/references.bib` | 20 entry BibTeX cho toàn bộ khoá đã trích trong 2 file trên — phần lớn đánh dấu `% TODO verify`, 2 entry (`n3_2019_reqdepgraph`, `n9_2025_goalsynthesis`) có tác giả chưa xác định |

Section 3–6 chưa có bản `.tex` — `index.tex` để sẵn dòng `\input` đã comment cho từng section, bỏ
comment khi file tương ứng được viết. Các `\ref{}` trỏ tới nhãn của section 3–6 (`sec:motivation`,
`sec:acl-language`, ...) trong 2 file đã viết sẽ hiện `??` cho tới lúc đó — đúng quy trình viết
tăng dần bình thường của LaTeX nhiều file.

**Cập nhật văn phong (sau góp ý "viết như mèo mửa")**: 2 file `.tex` đã viết lại theo đúng khuôn
mẫu tu từ của 1 bài báo thật cùng đề tài đã có sẵn trong repo, `doc/paper/alig.md` (bản đầy đủ của
Caballero-Villalobos et al., *Information and Software Technology* 196, 2026 — §1 Introduction và
§8 Related work). Các khuôn mẫu đã học và áp dụng lại:
- Introduction: mở rộng → thu hẹp bằng 1 **Ví dụ minh hoạ** đặt ngay trong phần mở đầu (không đợi
  tới Motivation) → chỉ ra cụ thể kỹ thuật hiện có bó tay ở đâu trên chính ví dụ đó → nêu thách
  thức dưới dạng liệt kê "Thứ nhất/Thứ hai/Thứ ba" → đoạn `\textbf{Đóng góp.}` dạng enumerate,
  đoạn `\textbf{Hạn chế.}`, đoạn `\textbf{Bố cục.}` — đều là các đoạn có nhãn chữ đậm mở đầu, đúng
  cách `alig.md` dùng "Contributions.", "Limitations and threats to validity.", "Overview.".
- Related Work: mỗi nhóm kết bằng 1 đoạn tương phản tường minh "Phương pháp đề xuất khác biệt ở
  N điểm: Thứ nhất..., Thứ hai...", lặp lại cho **từng nhóm** (không chỉ ở đoạn tổng hợp cuối) —
  đúng cách `alig.md` §8.1–§8.5 mỗi cluster đều kết bằng "Our approach differs in N aspects."

## 5. Quy Ước Khi Viết Từ Dàn Ý Này

- Tuân thủ `latex-rules`: không đại từ nhân xưng, `--` thay cho `-`/`---`, mỗi đoạn ≥ 2 câu, tiêu
  đề section không ngoặc và ≤ 8 từ.
- Tuân thủ `citation-rules`: mỗi claim so sánh với công trình khác phải có `\cite{}` ngay sau
  claim, không đặt cuối câu dài; không để citation mồ côi.
- Mỗi hình/bảng minh hoạ kiến trúc hoặc ví dụ `MeetingScheduler` phải có `\label{}` đúng prefix
  (`fig:`, `tab:`) và được `\ref{}` trong văn bản trước hoặc ngay sau vị trí đặt.
