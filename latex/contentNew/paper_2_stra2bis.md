# Stra2Bis: A Model-Driven Method for Aligning Business Strategy and Business Processes

**Tác giả:** George Koliadis, Abhaya Ghose (University of Wollongong, Australia)  |  **Năm:** 2022  |  **Venue:** Asia-Pacific Conference on Conceptual Modelling (APCCM 2022)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Business Process Management (BPM)** kết hợp với **Strategic Management** và **Model-Driven Architecture (MDA)**. Đây là một trong những điểm giao thoa quan trọng nhất trong khoa học thông tin: làm thế nào để đảm bảo rằng quy trình nghiệp vụ hàng ngày của một tổ chức thực sự phản ánh và đo lường được chiến lược cấp cao mà ban lãnh đạo đề ra.

Vấn đề này được gọi là **strategy-process alignment** — sự liên kết giữa chiến lược và quy trình. Theo nghiên cứu tại thời điểm bài báo (2006), phần lớn các tổ chức thiết kế quy trình dựa trên cách họ đang làm việc chứ không dựa trên cách họ muốn làm việc trong tương lai. Kết quả là có một khoảng cách (misalignment) ngày càng lớn giữa **strategic intent** (ý định chiến lược) và **operational reality** (thực tế vận hành). Khi chiến lược thay đổi mà quy trình không được cập nhật đồng bộ, tổ chức lãng phí nguồn lực vào những hoạt động không còn phù hợp với mục tiêu mới.

Lĩnh vực MDA (Model-Driven Architecture) của OMG cung cấp một khung gồm ba tầng mô hình: **CIM** (Computation Independent Model — mức nghiệp vụ), **PIM** (Platform Independent Model — thiết kế hệ thống), và **PSM** (Platform Specific Model — mã nguồn). Trong thực tế, cộng đồng MDA năm 2006 tập trung chủ yếu vào chuyển đổi PIM→PSM (tức là từ thiết kế xuống code), bỏ qua câu hỏi quan trọng hơn: CIM từ đâu mà có? Ai và bằng phương pháp nào để tạo ra business process model ở tầng CIM một cách có hệ thống?

Đây là câu hỏi mà Stra2Bis cố gắng giải quyết.

### Bài toán cụ thể

Stra2Bis (Strategy-to-Business) đặt ra bài toán: **làm thế nào để chuyển đổi có hệ thống từ mô hình chiến lược kinh doanh sang mô hình quy trình nghiệp vụ trong khung MDA?**

- **Đầu vào**: Business Strategy Model được biểu diễn bằng **LiteStrat** — một ngôn ngữ chiến lược lightweight do tác giả đề xuất. LiteStrat bao gồm các yếu tố: organizational units (đơn vị tổ chức), roles (vai trò), goals (mục tiêu chiến lược), tactics (chiến thuật ngắn hạn), objectives (mục tiêu đo được theo thời gian), và influence dependencies (phụ thuộc tác động lẫn nhau giữa các units).
- **Đầu ra**: Business Process Model được biểu diễn bằng **Communication Analysis (CA) notation** — một ký hiệu quy trình tập trung vào actors và các sự kiện giao tiếp giữa họ, phù hợp với cấu trúc tổ chức đa đơn vị.

Mục tiêu không phải là tạo ra process model hoàn chỉnh ngay lập tức, mà là cung cấp **transformation guidelines** cụ thể, có thể thực thi được, giúp người thiết kế hệ thống xây dựng process model từ strategic context một cách nhất quán và theo dõi được.

### Tại sao khó

Thách thức xuất phát từ sự không tương thích căn bản giữa hai thế giới:

1. **Chiến lược thiếu formal semantics**: Chiến lược thường tồn tại dưới dạng tài liệu văn bản, slide PowerPoint, hay Balanced Scorecard — tất cả đều dùng ngôn ngữ tự nhiên, không có cấu trúc chính xác để transformation tự động.
2. **Ngôn ngữ process không hiểu chiến lược**: BPMN, flowcharts, và CA notation được thiết kế để mô tả *cách làm*, không phải *tại sao làm*. Chúng không có chỗ để gắn thông tin về strategic objectives hay inter-unit dependencies.
3. **Thiếu phương pháp cầu nối**: Cộng đồng MDA năm 2006 chưa có phương pháp chuẩn hóa nào để tạo CIM từ strategic context. Người thiết kế phải dựa vào kinh nghiệm cá nhân, dẫn đến inconsistency.
4. **Complexity của tổ chức**: Khi một tổ chức có nhiều units với phụ thuộc phức tạp, việc đảm bảo tất cả inter-unit communication được formalize trong process model đòi hỏi kỹ năng đặc biệt và dễ bỏ sót.

### Đóng góp của bài

Tác giả tuyên bố ba đóng góp chính:
1. **Formalize LiteStrat**: Đề xuất ngôn ngữ chiến lược lightweight có đủ formal semantics để serve như input cho transformation, nhưng đơn giản hơn i* hay GRL đủ để practitioners không chuyên về modeling có thể sử dụng.
2. **Ba transformation guidelines**: Cung cấp ba quy tắc có thể thực thi được để ánh xạ từng yếu tố chiến lược sang cấu trúc process tương ứng.
3. **MDA integration**: Định vị phương pháp trong khung MDA, chứng minh rằng Stra2Bis output tự nhiên tương thích với Domain-Driven Design và microservices architecture ở tầng PIM.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: MDA CIM-to-PIM Transformation

Cộng đồng MDA năm 2006 đã có nhiều công trình về chuyển đổi PIM→PSM nhưng gần như bỏ trống CIM→PIM. Các công trình như của Frankel (2003) hay Kleppe et al. (2003) tập trung vào code generation từ UML diagrams, không giải quyết câu hỏi "business process model đến từ đâu". Đây là khoảng trống mà Stra2Bis nhắm vào.

### Hướng 2: Goal Modelling (i\*, GRL, Tropos)

Các ngôn ngữ như i* (Yu, 1995), GRL trong URN (ITU-T), và Tropos framework (Bresciani et al., 2004) cung cấp phương pháp mô hình hóa mục tiêu và dependency giữa các actors xuất sắc. Tuy nhiên, chúng có hai hạn chế đối với bài toán này:
- **Quá phức tạp cho practitioners**: i* có nhiều loại relationships (dependency, contribution, decomposition...) đòi hỏi chuyên môn modeling cao.
- **Thiếu transformation rules**: Không có guidelines rõ ràng để chuyển từ i*/GRL sang BPMN hay CA notation — mapping thường là manual và ad-hoc.

Stra2Bis giải quyết điểm yếu này bằng cách dùng LiteStrat (đơn giản hơn i*) kết hợp với transformation guidelines cụ thể.

### Hướng 3: Business Strategy Frameworks

Balanced Scorecard (Kaplan & Norton, 1996) và Strategy Maps là công cụ phổ biến trong quản lý chiến lược. Chúng rất tốt cho communication với management nhưng không có formal semantics — không thể dùng làm input cho transformation tự động. Strategy Maps không nói gì về organizational structure hay inter-unit dependencies.

### Hướng 4: Process-First Approaches

Phần lớn BPM methodology (như ARIS, IDEF) bắt đầu từ việc mô hình hóa processes hiện tại rồi cố gắng liên kết với strategy sau. Đây là "alignment retrofitting" — tốn kém và thường không hoàn chỉnh vì process structure đã được định hình trước khi strategy được tích hợp.

### Khoảng trống (Research Gap)

Tất cả các hướng trên đều thiếu một thứ: **phương pháp có thể thực thi được để chuyển từ strategic intent sang process structure một cách hệ thống trong khung MDA**, đặc biệt cho organizational context có nhiều units với phụ thuộc và objectives đo được rõ ràng. Stra2Bis lấp đầy khoảng trống này.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Insight căn bản của Stra2Bis là: **chiến lược tổ chức đã chứa đựng đủ thông tin để suy ra cấu trúc process — nếu ta formalize chiến lược đúng cách.**

Cụ thể, nếu ta nhìn vào một chiến lược tốt, ta sẽ thấy:
- Ai chịu trách nhiệm gì (organizational units và roles)
- Ai phụ thuộc vào ai (influence dependencies)
- Mục tiêu thành công được đo như thế nào (objectives)

Ba thông tin này ánh xạ trực tiếp sang ba yếu tố của process model:
- Units → independent processes
- Dependencies → communication events giữa processes
- Objectives → measurement events trong processes

Thay vì để designer "sáng tạo" process structure từ đầu, Stra2Bis cung cấp một con đường systematic: đọc strategic model, áp dụng ba guidelines, thu được process skeleton.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

Pipeline của Stra2Bis gồm ba bước chính:

**Bước 1: Xác định Business Process Model hiện tại** — Đây là baseline, mô tả cách tổ chức đang vận hành trước khi áp dụng chiến lược mới. Được biểu diễn bằng Communication Analysis notation.

**Bước 2: Xây dựng Business Strategy Model bằng LiteStrat** — Tác giả/chiến lược gia mô hình hóa chiến lược mới thành LiteStrat model, bao gồm: organizational units, roles, goals, tactics, objectives (đo được theo thời gian), và influence dependencies giữa units.

**Bước 3: Áp dụng 3 Transformation Guidelines** để tạo ra process model phản ánh chiến lược:
- Guideline 1: Mỗi organizational unit → 1 independent process
- Guideline 2: Mỗi influence dependency → explicit communication events
- Guideline 3: Mỗi strategic objective → measurement event trong process

Output là Business Process Model đã được cập nhật, align với chiến lược mới, và sẵn sàng để tiếp tục xuống tầng PIM (microservices, API contracts) và PSM (code).

### 3.3 Các Thành Phần Chính

**LiteStrat — Ngôn ngữ chiến lược lightweight:**
LiteStrat là ngôn ngữ mô hình hóa chiến lược mà tác giả thiết kế đặc biệt cho Stra2Bis. So với i* hay GRL, LiteStrat cố tình đơn giản hơn (ít loại relationships hơn) để giảm ngưỡng entry cho practitioners không chuyên về formal modeling. Các element gồm: Organizational Unit (một bộ phận/team), Role (vai trò trong unit), Goal (mục tiêu chiến lược dài hạn), Tactic (hành động ngắn hạn để đạt goal), Objective (mục tiêu đo được — ví dụ "tăng doanh thu 20% trong Q3"), và Influence Dependency (khi unit A cần output từ unit B để đạt objectives của mình).

**Communication Analysis (CA) Notation:**
CA là process notation được phát triển từ công trình của Winograd & Flores (1986) về language/action perspective. Thay vì chỉ mô tả hoạt động (activity), CA nhấn mạnh communication acts — khi ai đó cam kết làm gì cho ai. Notation này đặc biệt phù hợp với organizational structure vì actors là central element, không phải tasks. Trong bài báo, CA được dùng vì nó tương thích với Situational Method Engineering (SME) framework mà tác giả sử dụng.

**Guideline 1 — Organizational Unit Independence:**
Mỗi organizational unit trong LiteStrat model ánh xạ thành 1 independent business process trong CA model. Process này có start event mang tên unit. Rationale: nếu units trong chiến lược được thiết kế là độc lập (Conway's Law), thì processes của họ cũng phải độc lập — coupling giữa processes phản ánh coupling giữa teams, vốn là điều chiến lược muốn tránh.

**Guideline 2 — Managed Strategic Dependencies:**
Mỗi influence dependency giữa unit A và unit B trong LiteStrat ánh xạ thành explicit communication events + actors trao đổi thông tin giữa process A và process B. Không có dependency nào được phép "ẩn" trong implementation mà không xuất hiện ở level process model. Rationale: dependency không được formalize dẫn đến ad-hoc meetings, implicit assumptions, và errors khó trace.

**Guideline 3 — Strategic Objectives Measurement:**
Mỗi strategic objective của một unit ánh xạ thành 1 measurement event + 1 receiver actor trong process của unit đó. Measurement event là thời điểm tổ chức kiểm tra xem objective có đang được đáp ứng không. Rationale: nếu objective không có chỗ trong process model, nó sẽ không được đo trong thực tế vận hành — chiến lược trở thành tài liệu chết.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Giả sử một công ty SaaS có chiến lược mới với hai units: **Sales Unit** và **Engineering Unit**. Chiến lược LiteStrat của họ như sau:
- Sales Unit: Goal = "Tăng customer acquisition", Objective = "Ký 50 hợp đồng mới trong Q3", Tactic = "Chạy outbound campaign"
- Engineering Unit: Goal = "Cải thiện product quality", Objective = "Giảm bug rate xuống < 2 bugs/sprint", Tactic = "Implement automated testing"
- Influence Dependency: Sales Unit depends on Engineering Unit for "stable product demo" (Sales cần demo ổn định để chốt deals)

Áp dụng ba guidelines:

**Guideline 1**: Tạo hai processes độc lập:
- Process "Sales": bắt đầu với start event "Sales Unit Operations"
- Process "Engineering": bắt đầu với start event "Engineering Unit Operations"

**Guideline 2**: Vì có dependency Sales→Engineering, thêm vào:
- Trong Process Engineering: một actor "Demo Coordinator" và một communication event "Send stable demo to Sales"
- Trong Process Sales: một actor "Demo Receiver" nhận communication event từ Engineering

**Guideline 3**: Vì Sales có objective "50 hợp đồng mới":
- Trong Process Sales: thêm measurement event "Contract Count Check" và actor "Sales Metrics Receiver" để record số hợp đồng ký được
Vì Engineering có objective "bug rate < 2":
- Trong Process Engineering: thêm measurement event "Bug Rate Check" và actor "Quality Metrics Receiver"

Kết quả: hai process models với explicit inter-process communication và embedded measurement points — tất cả đều truy vết được về strategic intent.

### 3.5 Điểm Mới So Với Trước

Stra2Bis khác các phương pháp trước ở ba điểm:

1. **Transformation có thể thực thi**: Ba guidelines không phải là conceptual advice mà là operational rules với input/output rõ ràng — áp dụng được một cách nhất quán bởi nhiều người.

2. **MDA integration thực sự**: Stra2Bis không chỉ nói "link strategy với process" mà định vị cụ thể trong MDA lifecycle: Stra2Bis output là CIM-level artifact, tự nhiên dẫn xuống PIM (microservices) và PSM (code).

3. **Phụ thuộc được formalize bắt buộc**: Không cho phép "ẩn" inter-unit dependencies trong implementation — mọi dependency phải xuất hiện như explicit communication events trong process model.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Sự liên kết giữa chiến lược kinh doanh và quy trình nghiệp vụ là một thách thức dai dẳng trong quản lý tổ chức. Các tổ chức thường thiết kế quy trình dựa trên thực tiễn hiện tại thay vì dựa trên chiến lược tương lai, dẫn đến khoảng cách ngày càng lớn giữa ý định chiến lược và thực tế vận hành. Bài báo này đề xuất Stra2Bis (Strategy-to-Business), một phương pháp model-driven để giải quyết vấn đề này. Stra2Bis sử dụng LiteStrat — một ngôn ngữ mô hình hóa chiến lược lightweight — làm đầu vào, và áp dụng ba transformation guidelines để tạo ra business process model theo Communication Analysis notation. Ba guidelines bao gồm: (1) ánh xạ mỗi organizational unit thành một independent process, (2) formalize mọi influence dependency thành explicit communication events, và (3) nhúng strategic objectives vào process dưới dạng measurement events. Phương pháp được định vị trong khung Model-Driven Architecture (MDA) ở tầng CIM, tự nhiên dẫn xuống tầng PIM và PSM. Validation qua focus groups với industry practitioners xác nhận tính phù hợp thực tiễn của ba guidelines. Bài báo cũng thảo luận về hệ quả xuống tầng PIM, bao gồm sự tương thích với Domain-Driven Design và microservices architecture.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

Stra2Bis là method paper với validation chủ yếu qua **focus groups** với industry practitioners, không có controlled experiments hay benchmark datasets.

**Focus group với practitioners:**
Tác giả tổ chức focus groups với practitioners từ consulting companies (đặc biệt là CSC — Computer Sciences Corporation) và SaaS companies. Kết quả:
- Tất cả participants đồng ý ba guidelines capture đúng transformation cần thiết từ strategic context sang process structure.
- **Guideline 1**: 100% practitioners xác nhận teams độc lập phải có processes độc lập. Vi phạm rule này dẫn đến "chaotic development process" — trích dẫn trực tiếp từ participants.
- **Guideline 2**: Practitioners xác nhận inter-team dependencies nếu không được formalize ở process level dẫn đến "several meetings between teams to define the flow" — lãng phí thời gian và error-prone.
- **Guideline 3**: Practitioners xác nhận objectives phải được embedded vào process để có thể đo được trong thực tế vận hành; nếu chỉ tồn tại trong tài liệu chiến lược, chúng không được theo dõi thường xuyên.

**PIM Implications (kết quả phân tích lý thuyết):**
Áp dụng Stra2Bis ở CIM level tạo ra process structure tương thích tự nhiên với:
- **Domain-Driven Design (DDD)**: Mỗi organizational unit → 1 bounded context
- **Microservices**: Mỗi independent process → 1 microservice với autonomous deployment
- **Event-Driven Architecture**: Mỗi communication event → 1 domain event trên message bus
- **Observability**: Mỗi measurement event → 1 telemetry point (metrics, logging)

**Ablation study:** Không có — đây là method paper.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **CSC context limitation**: Consulting companies thường không tham gia vào strategic planning của khách hàng — họ chỉ implement processes theo yêu cầu. Do đó, Stra2Bis không áp dụng trực tiếp cho CSC context nơi strategic model không available.

2. **Tactics chưa được xử lý**: LiteStrat có element "tactics" (hành động chiến lược ngắn hạn) nhưng Stra2Bis chưa có guideline để ánh xạ tactics sang process elements. Chỉ organizational units, dependencies, và objectives mới có guidelines rõ ràng.

3. **Thiếu tool automation**: Ba guidelines hiện tại là manual. Để scale cho các tổ chức lớn với nhiều units và objectives, cần tool support để automation transformation.

4. **Validation hạn chế**: Chỉ có focus groups — chưa có controlled experiments, empirical studies, hay longitudinal studies đo lường actual misalignment reduction sau khi áp dụng Stra2Bis.

5. **Giả định về organizational structure**: Phương pháp giả định units là semi-independent — không phù hợp với hierarchical, centralized organizations nơi tất cả decisions đều đi qua một trung tâm.

**Hướng nghiên cứu tiếp theo:**
- Tích hợp transformation từ tactics sang process elements (Guideline 4?)
- Phát triển tool support tự động hóa ba guidelines
- Mở rộng sang full MDA transformation chain: CIM → PIM → PSM
- Empirical study đo lường misalignment reduction khi dùng Stra2Bis so với ad-hoc process design
- Tích hợp với modern notations như BPMN thay cho Communication Analysis

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất Stra2Bis, một phương pháp model-driven với ba transformation guidelines để chuyển đổi có hệ thống từ business strategy model (LiteStrat) sang business process model (Communication Analysis notation) trong khung Model-Driven Architecture. Thay vì để process designers sáng tạo process structure từ đầu, Stra2Bis cung cấp con đường systematic: mỗi organizational unit thành 1 independent process, mỗi influence dependency thành explicit communication events, mỗi strategic objective thành measurement events trong process. Focus group validation với industry practitioners xác nhận tính phù hợp thực tiễn. Phương pháp còn hạn chế ở việc chưa xử lý tactics và chưa có tool automation, nhưng đặt nền móng cho CIM-level MDA development từ strategic context. Hướng tự nhiên tiếp theo là tích hợp Stra2Bis với modern BPMN notation và phát triển tool support.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc lần đầu tiên formalize con đường từ chiến lược sang quy trình bằng ba guidelines có thể thực thi được trong khung MDA, chứng minh rằng strategy-process alignment không phải là vấn đề triết học mà là vấn đề kỹ thuật có thể giải quyết bằng transformation rules cụ thể. Hệ quả xuống tầng PIM — tương thích tự nhiên với microservices và Domain-Driven Design — cho thấy tầm nhìn xa của bài báo dù được viết từ năm 2006.
