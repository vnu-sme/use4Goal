# An Improved User Requirements Notation (URN) Models' Construction Approach

**Tác giả:** Gunter Mussbacher, Daniel Amyot, et al.  |  **Năm:** 2012  |  **Venue:** RE 2012 / Journal of Software and Systems Modeling

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Requirements Engineering**, cụ thể là phương pháp luận xây dựng mô hình **User Requirements Notation (URN)** — chuẩn ITU-T Z.151. URN là ngôn ngữ tích hợp gồm hai thành phần bổ trợ nhau: **GRL** (Goal-oriented Requirements Language — mô hình hóa mục tiêu và ý định của các tác nhân) và **UCM** (Use Case Maps — mô hình hóa các kịch bản và quy trình). Bài báo đầu tiên trong survey này (paper_1) đã trình bày URN như một framework tích hợp — bài này đi sâu vào vấn đề *thực hành*: làm thế nào để xây dựng GRL và UCM đúng cách.

URN được thiết kế với triết lý "goals explain why, processes describe how" — GRL giải thích *tại sao* hệ thống cần tồn tại và phải đạt được mục tiêu gì, còn UCM mô tả *như thế nào* các kịch bản được thực thi. Sự kết hợp này rất mạnh trên lý thuyết: nếu process model và goal model được đồng bộ hoàn hảo, ta có thể trace từng hoạt động trong UCM về một goal cụ thể trong GRL, và ngược lại.

Tuy nhiên, trong thực tiễn, sức mạnh này thường không được tận dụng vì hai mô hình bị xây dựng theo cách không nhất quán.

### Bài toán cụ thể

Vấn đề trung tâm: trong quy trình xây dựng URN truyền thống (theo chuẩn ITU-T Z.151), GRL và UCM thường được phát triển **độc lập song song** hoặc UCM được xây dựng trước rồi GRL được thêm vào sau. Cả hai cách đều dẫn đến **thiếu nhất quán** giữa hai mô hình.

Hậu quả cụ thể:
- GRL và UCM có thể mô tả hai "phiên bản" khác nhau của cùng một hệ thống
- Không có cơ chế traceability để xác minh "goal X được thực hiện bởi scenario nào"
- Non-functional requirements (NFRs) và softgoals trong GRL bị bỏ qua hoàn toàn trong UCM

- **Đầu vào**: Yêu cầu thô từ stakeholders, bối cảnh hệ thống, strategic và operational goals
- **Đầu ra**: Cặp mô hình GRL + UCM nhất quán, có traceability rõ ràng, và NFRs được tích hợp vào UCM dưới dạng CNF-Actions

### Tại sao khó

1. **Hai ngôn ngữ với semantics khác nhau**: GRL dùng khái niệm actors, goals, softgoals, tasks, contributions — UCM dùng stub, component, path, branch, fork. Không có ánh xạ tự nhiên và rõ ràng giữa hai thế giới này.

2. **Thiên vị của kỹ sư**: Kỹ sư thường quen với một ngôn ngữ hơn, dẫn đến xu hướng tự nhiên là bắt đầu từ ngôn ngữ quen thuộc và coi ngôn ngữ kia là phụ — phá vỡ mục tiêu tích hợp của URN.

3. **NFRs/Softgoals là thách thức đặc biệt**: Softgoals như "secure", "performant", "usable" không ánh xạ trực tiếp thành activities cụ thể trong process model. Cần cơ chế đặc biệt để represent chúng trong UCM mà không làm mất semantics.

4. **Chuẩn ITU-T thiếu hướng dẫn thực hành**: Z.151 định nghĩa ngữ nghĩa của GRL và UCM nhưng không chỉ định thứ tự và cách thức xây dựng — để lại khoảng trống lớn cho practitioners.

### Đóng góp của bài

Tác giả đề xuất **quy trình URN cải tiến** với hai giai đoạn rõ ràng:
1. **Xây dựng GRL trước và hoàn thiện** — bao gồm cả functional requirements (FRs) và non-functional requirements (NFRs)
2. **Xây dựng UCM sau, kế thừa từ GRL** — với bridging mechanism có hệ thống và CNF-Actions để tích hợp NFRs

Đóng góp kỹ thuật chính là **CNF-Actions** (Complementary Non-Functional Actions) — một loại action mới trong UCM để biểu diễn NFRs được tích hợp vào process flow, và **bridging mechanism** — bảng ánh xạ tường minh từ GRL elements sang UCM elements.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: Quy trình URN gốc (ITU-T Z.151)

Chuẩn ITU-T Z.151 cho phép xây dựng GRL và UCM theo bất kỳ thứ tự nào — song song, GRL-first, hay UCM-first. Tính linh hoạt này về lý thuyết là ưu điểm, nhưng trong thực tế dẫn đến inconsistency vì không có hướng dẫn về cách coordinate hai quá trình. Hơn nữa, chuẩn không đề cập đến cơ chế bridging hay traceability.

### Hướng 2: UCM-first Approaches

Một số nghiên cứu đề xuất xây dựng UCM trước (dựa trên use cases hoặc scenarios từ stakeholders), sau đó bổ sung GRL sau. Đây là cách tự nhiên khi practitioners quen với scenario-based elicitation. Nhược điểm căn bản: process không được dẫn dắt bởi goals — thiếu alignment chiến lược. UCM được thiết kế trước khi rõ ràng "tại sao" nó cần tồn tại.

### Hướng 3: KAOS và i\*

KAOS (van Lamsweerde et al.) và i* (Yu) là những goal modeling languages thành thục. Chúng rất tốt cho modeling goals và dependencies nhưng thiếu companion process modeling language. Không có cơ chế chuyển đổi từ goal model sang process model được standardized.

### Hướng 4: GRL-to-UCM Transformation Research

Một số nghiên cứu đề xuất rules để transform GRL sang UCM (như công trình của Bresciani et al. trong Tropos). Tuy nhiên, chúng thường thiếu: (a) hướng dẫn về NFRs — softgoals không được xử lý, và (b) validation thực tiễn — kỹ sư thực tế khó áp dụng.

### Khoảng trống (Research Gap)

Thiếu một quy trình URN tường minh: (1) ưu tiên GRL-first với bridging mechanism cụ thể, (2) tích hợp NFRs/softgoals vào UCM, (3) đảm bảo traceability hai chiều, (4) cung cấp hướng dẫn step-by-step đủ cụ thể để practitioners áp dụng mà không cần chuyên môn sâu.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Nguyên lý cốt lõi: **Goals là nguyên nhân, processes là hệ quả.** Do đó, GRL phải được xây dựng và hoàn thiện trước, sau đó đóng vai trò là *nguồn duy nhất* (single source of truth) để sinh UCM.

Nếu mọi UCM element đều có thể truy vết về ít nhất một GRL element, thì:
- Không có "orphan activities" (activities không có goal nào justify)
- Không có "missed goals" (goals không có activity nào implement)
- Consistency được đảm bảo by construction, không cần kiểm tra sau

Với softgoals (NFRs): thay vì bỏ qua chúng trong UCM (như hầu hết approaches làm), tác giả đề xuất **CNF-Actions** — một loại action UCM đặc biệt biểu diễn "hành động đảm bảo NFR X". CNF-Actions không phải là main functional activity mà là supplementary actions được thêm vào UCM paths để capture NFR semantics.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**Giai đoạn 1 — Xây dựng GRL cải tiến:**
- Elicitation yêu cầu với stakeholders: xác định actors, strategic goals, và operational goals
- Phân tích FRs: phân rã goals thành tasks và resources cụ thể theo AND/OR decomposition
- Phân tích NFRs: xác định softgoals (security, performance, usability...) và operationalizations (cách đạt softgoal)
- Hoàn thiện GRL với contribution links từ tasks đến softgoals

**Bridging Mechanism:**
- Trích xuất từ GRL đã hoàn thiện: generic tasks (from functional goal decomposition), softgoals + operationalizations, resources
- Tạo bảng ánh xạ: mỗi GRL element ánh xạ sang loại UCM element tương ứng
- Bảng này là artifact trung gian chính thức — không phải mental mapping

**Giai đoạn 2 — Xây dựng UCM cải tiến:**
- Tasks từ GRL → stubs/components/paths trong UCM
- Kết nối paths theo điều kiện (AND/OR branching từ GRL goal decomposition)
- NFRs/Softgoals từ GRL → CNF-Actions trong UCM paths
- Xây dựng traceability matrix: GRL element ↔ UCM element

Output: cặp GRL + UCM nhất quán với traceability đầy đủ.

### 3.3 Các Thành Phần Chính

**GRL (Goal-oriented Requirements Language):**
Ngôn ngữ mô hình hóa mục tiêu trong URN. Các element: Actor (người dùng, hệ thống, tổ chức), Intentional Element (Goal, Softgoal, Task, Resource), và Relationship (Contribution, Dependency, Decomposition). Trong quy trình cải tiến, GRL phải hoàn chỉnh cả FRs lẫn NFRs trước khi chuyển sang giai đoạn UCM.

**UCM (Use Case Maps):**
Ngôn ngữ scenario/process trong URN. Các element: Component (actor context), Path (sequence of responsibilities), Stub (abstract entry/exit point), Branch/Fork/Join (control flow). UCM hiển thị "causal path" — luồng nhân quả của responsibilities được thực thi để đạt kết quả.

**Bridging Mechanism:**
Cầu nối hình thức giữa GRL và UCM. Bảng ánh xạ xác định: Goal → scenario start condition; Task → stub/responsibility; Resource → component; Softgoal → CNF-Action; OR-decomposition → branch; AND-decomposition → fork/join.

**CNF-Actions (Complementary Non-Functional Actions):**
Đây là contribution kỹ thuật chính của bài. CNF-Actions là loại action đặc biệt trong UCM, được gán với một softgoal từ GRL. Chúng không phải là main functional activities mà là "side actions" để đảm bảo NFRs. Ví dụ: nếu GRL có softgoal "Secure Authentication", CNF-Action trong UCM sẽ là actions như "Encrypt credentials", "Log access attempt", "Verify session token" — những hành động này thường bị bỏ sót trong process design thông thường.

**Traceability Matrix:**
Ma trận hai chiều GRL ↔ UCM cho phép: (a) forward traceability: từ goal → scenario/activity nào implement nó; (b) backward traceability: từ activity → goal nào justify nó.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Xét **hệ thống bỏ phiếu điện tử** với actors: Voter, Election Official, System Admin.

**Giai đoạn 1 — GRL:**

Xác định goals:
- Actor Voter: Goal "Cast Valid Vote" → phân rã thành tasks: "Authenticate Voter", "Select Candidate", "Submit Ballot"
- Softgoals: "Ensure Ballot Secrecy" (NFR security), "Minimize Voting Time" (NFR usability)
- Contribution links: task "Authenticate Voter" contributes positively to softgoal "Ensure Ballot Secrecy"

**Bridging Mechanism:**
Tạo bảng ánh xạ:

| GRL Element | UCM Element |
|---|---|
| Goal "Cast Valid Vote" | Start condition of main scenario |
| Task "Authenticate Voter" | Stub "Authentication" in UCM |
| Task "Select Candidate" | Responsibility "Candidate Selection" in UCM |
| Task "Submit Ballot" | Responsibility "Ballot Submission" in UCM |
| Softgoal "Ensure Ballot Secrecy" | CNF-Actions in UCM |
| Softgoal "Minimize Voting Time" | CNF-Actions in UCM |

**Giai đoạn 2 — UCM:**

Main path: Voter → [Authentication Stub] → [Candidate Selection] → [Ballot Submission] → End

CNF-Actions được thêm vào paths:
- Tại Authentication Stub: CNF-Action "Encrypt voter credentials" và "Log auth attempt" (từ "Ensure Ballot Secrecy")
- Tại Ballot Submission: CNF-Action "Anonymize ballot before storage" (từ "Ensure Ballot Secrecy")
- Tại toàn bộ path: CNF-Action "Cache intermediate results" (từ "Minimize Voting Time")

Kết quả: UCM không chỉ có functional flow mà còn embedded security và usability actions — tất cả truy vết được về GRL softgoals.

**Traceability matrix** cho thấy mọi UCM element đều có GRL element tương ứng → không có orphan activities.

### 3.5 Điểm Mới So Với Trước

Hai điểm khác biệt căn bản:

1. **Thứ tự bắt buộc GRL-first**: Không phải recommendation mà là constraint — UCM không thể bắt đầu trước khi GRL hoàn thiện. Điều này đảm bảo consistency by construction.

2. **CNF-Actions — cơ chế mới cho NFRs**: Lần đầu tiên có cơ chế hình thức để represent softgoals trong UCM process model, thay vì bỏ qua chúng hoặc chỉ annotate informally.

---

## PHẦN 4 — Abstract (Tiếng Việt)

User Requirements Notation (URN) tích hợp hai ngôn ngữ mạnh: GRL cho goal modeling và UCM cho process/scenario modeling. Tuy nhiên, quy trình xây dựng URN truyền thống thường dẫn đến thiếu nhất quán vì GRL và UCM được phát triển song song hoặc UCM được xây dựng trước. Bài báo này đề xuất quy trình URN cải tiến với hai giai đoạn rõ ràng: (1) hoàn thiện GRL bao gồm cả functional requirements và non-functional requirements (softgoals), sau đó (2) xây dựng UCM kế thừa từ GRL thông qua bridging mechanism có hệ thống. Đóng góp kỹ thuật chính là CNF-Actions (Complementary Non-Functional Actions) — một loại action mới trong UCM để tích hợp softgoals từ GRL vào process flow. Cơ chế cầu nối tường minh với bảng ánh xạ đảm bảo traceability hai chiều giữa GRL và UCM. Quy trình được minh họa và validate trên case study hệ thống bỏ phiếu điện tử sử dụng tool jUCMNav, cho thấy cải thiện rõ rệt về consistency, NFR coverage, và traceability so với quy trình URN truyền thống.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
**Hệ thống bỏ phiếu điện tử (Electronic Voting System)** — hệ thống thực tế với nhiều actors (Voter, Election Official, System Admin) và nhiều softgoals quan trọng (ballot secrecy, auditability, usability). Quy mô vừa — đủ để minh họa tất cả cơ chế của quy trình.

**Baseline:**
Quy trình URN truyền thống (theo chuẩn ITU-T Z.151): xây dựng song song GRL và UCM cho cùng hệ thống voting.

**Kết quả định tính (so sánh cũ vs. mới):**

| Vấn đề trong quy trình cũ | Kết quả sau cải tiến |
|---|---|
| GRL và UCM phát triển song song → mâu thuẫn | GRL hoàn thiện trước → UCM kế thừa nhất quán |
| NFRs (security, auditability) bị bỏ qua trong UCM | CNF-Actions tích hợp đầy đủ tất cả softgoals chính |
| Không có bảng traceability | Mọi UCM path đều liên kết tường minh với GRL task/goal |
| UCM có paths không xuất phát từ goals | Mọi UCM element đều được justify bởi GRL element |

**Observations cụ thể:**
- Quy trình cải tiến phát hiện ra rằng mô hình UCM cũ có 3 activities không có goal nào justify (orphan activities) — đây là lãng phí thiết kế
- 5 softgoals quan trọng (ballot secrecy, auditability, accessibility, availability, integrity) đều được tích hợp vào UCM qua CNF-Actions — trong khi mô hình UCM cũ không có bất kỳ NFR nào
- Traceability matrix cho thấy 100% coverage: mọi goal có ít nhất một UCM path implement nó

**Tool:** jUCMNav (Eclipse plugin) được sử dụng để vẽ và quản lý cả GRL và UCM models.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **Một case study duy nhất**: Chỉ validate trên voting system — cần thêm nhiều case studies đa dạng về domain và quy mô để khẳng định tính tổng quát.

2. **Không có controlled experiment**: Không có thực nghiệm kiểm soát để đánh giá định lượng usability của quy trình (thời gian, số lỗi, user satisfaction) so với quy trình cũ.

3. **Bridging mechanism là manual**: Kỹ sư phải thủ công tạo bảng ánh xạ và CNF-Actions — không scalable cho các hệ thống lớn với hàng trăm goals và activities.

4. **Reverse traceability chưa được xử lý**: Khi UCM thay đổi sau khi đã xây dựng (do new requirements, change requests), cần cơ chế cập nhật GRL tương ứng. Quy trình hiện tại chỉ hỗ trợ forward direction (GRL → UCM).

**Hướng nghiên cứu tiếp theo:**
- Tự động hóa bridging mechanism trong jUCMNav tool
- Controlled experiment với kỹ sư yêu cầu trong industry để đánh giá định lượng
- Mở rộng quy trình cho các notations khác (BPMN với Goals extension, UML use cases)
- Nghiên cứu reverse traceability: change impact analysis khi requirements thay đổi
- Tích hợp với Agile RE processes — làm thế nào để áp dụng quy trình 2-phase trong sprint-based development

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất quy trình URN cải tiến với thứ tự xây dựng bắt buộc GRL-first, bridging mechanism tường minh, và CNF-Actions để tích hợp NFRs vào UCM. So với quy trình URN truyền thống, phương pháp mới đảm bảo consistency by construction, tránh orphan activities, và đảm bảo không có goal nào bị bỏ sót trong process model. Validate trên voting system cho thấy cải thiện rõ rệt về tất cả tiêu chí định tính. Hạn chế chính là thiếu empirical validation với controlled experiments và tool automation — đây là hướng mở rộng quan trọng.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc hiện thực hóa nguyên lý "goals explain why, processes describe how" thành một quy trình công cụ cụ thể — đặc biệt là CNF-Actions, một cơ chế đơn giản nhưng hiệu quả để đưa non-functional requirements từ goal model vào process model mà không làm mất semantics của chúng. Đây là bridge thực sự giữa requirements engineering và process design.
