# Combining Goal Modelling with Business Process Modelling: Two Decades of Experience with the User Requirements Notation Standard

**Tác giả:** Daniel Amyot, Okhaide Akhigbe, Malak Baslyman, Sepideh Ghanavati, Mahdi Ghasemi, Jameleddine Hassine, Lysanne Lessard, Gunter Mussbacher, Kai Shen, Eric Yu  |  **Năm:** 2022  |  **Venue:** Enterprise Modelling and Information Systems Architectures (International Journal of Conceptual Modeling), Vol. 17, No. 2, Special Issue on 100 Years of Graphical Business Process Modelling

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Requirements Engineering** và **Business Process Management** — hai lĩnh vực nền tảng trong phát triển hệ thống thông tin doanh nghiệp. Đây là bài tổng kết gần hai thập kỷ kinh nghiệm với chuẩn User Requirements Notation (URN) của ITU-T — một chuẩn quốc tế tích hợp goal modelling và process modelling trong một ngôn ngữ thống nhất.

Tầm quan trọng xuất phát từ một thực tế cốt lõi: phần lớn hệ thống phần mềm thất bại không phải vì lỗi kỹ thuật mà vì **thiếu liên kết giữa ý định chiến lược và thực thi vận hành**. Khi xây dựng hệ thống, người ta cần trả lời hai nhóm câu hỏi hoàn toàn khác nhau:

- **Nhóm chiến lược (Why/Who)**: Tại sao hệ thống này tồn tại? Ai là các bên liên quan? Họ muốn đạt được điều gì? Khi nào một mục tiêu được coi là thỏa mãn?
- **Nhóm vận hành (What/How/When)**: Hệ thống làm gì cụ thể? Các hoạt động diễn ra theo trình tự nào? Điều kiện nào được kích hoạt khi nào?

Từ trước đến nay, hai nhóm câu hỏi này được xử lý bởi hai họ ngôn ngữ hoàn toàn tách biệt: **goal modelling** (GRL, i*, KAOS) trả lời "Why/Who", còn **process modelling** (BPMN, UML Activity Diagrams, Petri Nets) trả lời "What/How/When". Vấn đề: khi hai loại model tồn tại riêng rẽ, không ai có thể tự động kiểm tra xem quy trình vận hành có thực sự hiện thực hóa mục tiêu chiến lược hay không.

URN được thiết kế chính xác để giải quyết bài toán tích hợp này — và bài báo 2022 này là tổng kết toàn diện về 20 năm áp dụng URN trong các lĩnh vực thực tế, từ regulatory compliance đến process mining đến socio-cyber-physical systems.

### Bài toán cụ thể

**Đầu vào**: Goal models (GRL với actors, goals, softgoals, tasks, KPIs, strategies) và Process models (UCM với responsibilities, stubs, scenarios, conditions).

**Đầu ra**: Kết quả phân tích propagation — mức độ thỏa mãn từng goal theo từng strategy; kết quả duyệt scenarios với pre/post conditions; truy vết (traceability) giữa quyết định thiết kế và lý do chiến lược.

Bài toán cụ thể gồm bốn vấn đề song song:
1. **Goal-process alignment**: Tự động kiểm tra xem quy trình có thỏa mãn các mục tiêu hay không — và khi quy trình thay đổi, mục tiêu nào bị ảnh hưởng?
2. **Trade-off analysis**: Khi có nhiều chiến lược khả thi, cách nào tốt nhất cân bằng các mục tiêu xung đột?
3. **Compliance monitoring**: Khi quy định pháp lý thay đổi, quy trình cần thay đổi gì để tuân thủ?
4. **Goal-oriented process mining**: Từ event logs thực tế, khai phá được các process models phản ánh các traces đạt mục tiêu — thay vì toàn bộ hành vi hỗn loạn.

### Tại sao khó

**Thứ nhất**, không có ngôn ngữ thống nhất nào tích hợp cả hai năng lực trong cùng một semantic framework được chuẩn hóa. Goal modelling và process modelling phát triển độc lập trong nhiều thập kỷ với cộng đồng, tool, và ontology riêng biệt.

**Thứ hai**, liên kết thủ công giữa hai loại model không cho phép automated analysis — một tool chỉ "hiểu" ngôn ngữ của mình. Compliance và trade-off analysis đòi hỏi traversal qua cả hai lớp đồng thời.

**Thứ ba**, khi hệ thống lớn, models cả hai loại trở nên phức tạp đến mức không thể quản lý thủ công. Cần modularity, aspect-oriented extensions, và slicing để maintainability.

**Thứ tư**, "satisfaction" của goal là khái niệm chủ quan — cần cơ chế chuẩn hóa (như KPIs) để chuyển các đo lường thực tế thành mức thỏa mãn goal có thể propagate qua model.

### Đóng góp của bài

1. **Tổng kết 20 năm kinh nghiệm URN**: Review coarse-grained statistics của 1,466 URN-related papers (305 core papers) từ 37 quốc gia kể từ 1995.
2. **Sáu lĩnh vực ứng dụng chuyên sâu**: Goal/process alignment, regulatory compliance & intelligence, process adaptation, value co-creation, goal-oriented process mining, advanced techniques (AoURN, slicing, feature models).
3. **Hướng nghiên cứu tương lai**: AI/ML integration, natural language processing cho traceability, socio-cyber-physical systems.
4. **Điểm toàn cảnh** về nơi URN đứng sau 20 năm và những thách thức chưa giải quyết.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

Bài báo là bài tổng kết (survey/experience report) nên không có phần Related Work truyền thống. Thay vào đó, bài đặt URN trong bối cảnh của hai hướng nghiên cứu lớn mà URN cố gắng thống nhất.

### Hướng 1: Process Modelling Languages

BPMN, Petri Nets, UML Activity Diagrams — nhóm này xuất sắc trong việc mô tả *what, when, where, who, how* của hệ thống: trình tự hoạt động, điều kiện rẽ nhánh, parallel execution, roles thực hiện. Tuy nhiên, chúng **không trả lời được "why"** — không có nơi để ghi lý do chiến lược, intentions của stakeholders, hay trade-offs giữa các thiết kế khác nhau.

### Hướng 2: Goal Modelling Languages

i*, KAOS, Tropos, GRL — nhóm này xuất sắc trong việc mô tả intentions (*why* và *who*): actors, goals, softgoals, dependencies, contributions. Cho phép phân tích trade-offs, tìm chiến lược tốt nhất để thỏa mãn tập hợp goals xung đột. Tuy nhiên, chúng **không capture được sequential flow** của activities — không có khái niệm ordering, conditions, hay workflow.

### Khoảng trống và vị trí của URN

Tích hợp hai hướng là ý tưởng tự nhiên nhưng đã thất bại nhiều lần vì các ngôn ngữ và tool không được thiết kế để interoperate. URN (được chuẩn hóa bởi ITU-T năm 2003 thành Z.151 cho UCM và 2012 thành Z.151/Z.152 cho toàn bộ URN) là nỗ lực đầu tiên và duy nhất tạo ra một **chuẩn quốc tế** tích hợp hai paradigm trong một metamodel và toolset thống nhất.

Đáng chú ý: chỉ khoảng **10%** trong 305 core URN papers thực sự khai thác *cả hai* GRL và UCM cùng nhau — phần lớn papers chỉ dùng một trong hai. Đây là dấu hiệu rằng tiềm năng của URN chưa được khai thác hết.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

URN không phải là hai ngôn ngữ được ghép lại — mà là một **ngôn ngữ thống nhất với hai views bổ sung nhau**, được kết nối bởi URN Links (typed traceability links). Ý tưởng cốt lõi:

> *"Mọi activity trong process đều có một lý do chiến lược. Mọi goal chiến lược đều cần được hiện thực bởi ít nhất một process. URN cho phép modeler nối hai chiều này một cách formal và tự động phân tích."*

Người dùng có thể bắt đầu từ goals (và sau đó thiết kế processes), hoặc từ processes (và sau đó truy ngược lên goals), hoặc xây dựng song song và dùng URN Links để kết nối. Cả ba workflows đều được hỗ trợ.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

URN bao gồm hai ngôn ngữ con được tích hợp:

```
Thực tế / Vận hành
        ↓ (KPIs đo lường)
GRL — Goal-oriented Requirement Language (Why/Who)
  ├── Actors, Goals, Softgoals, Tasks
  ├── KPIs (Key Performance Indicators)
  ├── Contributions (qualitative/quantitative weights)
  ├── Decompositions (AND/OR/XOR)
  ├── Strategies (sets of initial satisfaction values)
  └── Propagation Algorithms → satisfaction scores [0..100]
        ↕ (URN Links — typed traceability)
UCM — Use Case Maps (What/How/When)
  ├── Responsibilities (activities)
  ├── Components (teams, actors, systems)
  ├── Start/End Points, OR/AND Forks/Joins
  ├── Stubs (dynamic sub-processes)
  ├── Plug-in Maps (modular scenarios)
  └── Scenario Definitions → executable traversal
        ↓
jUCMNav Tool (Eclipse-based)
  ├── Visual modeling + color-coded analysis
  ├── OCL constraint rules (18 goal-process alignment rules)
  ├── Scenario traversal → sequence diagrams
  ├── Slicing (static + forward)
  └── One-click quick fixes
```

### 3.3 Các Thành Phần Chính

**GRL — Goal-oriented Requirement Language**:

GRL capture intentions và social aspects. Các phần tử chính:
- *Actor*: stakeholder, tổ chức, system component — mang intentions
- *Goal*: trạng thái mong muốn đo được (achieved/not)
- *Softgoal*: chất lượng phi chức năng (security, usability) — chỉ "thỏa mãn phần nào"
- *Task*: course of action cụ thể
- *KPI (Key Performance Indicator)*: chuyển đổi đo lường thực tế → satisfaction level [0..100] bằng ba tham số: Target (satisfaction = 100), Threshold (satisfaction = 50), Worst Case (satisfaction = 0) + linear interpolation
- *Contributions*: links giữa tasks/goals với weights (Help, Hurt, Make, Break, Some+, Some-)
- *Decompositions*: AND/OR/XOR để phân rã goals thành sub-goals
- *Strategies*: tập hợp initial satisfaction values để test "what-if" scenarios
- *Propagation algorithms*: lan truyền satisfaction từ low-level lên high-level goals, hỗ trợ cả top-down optimization (dùng CPLEX) để tìm strategy tốt nhất

**UCM — Use Case Maps**:

UCM capture sequential flow của processes. Các phần tử chính:
- *Responsibilities*: activities/tasks được thực hiện bởi components
- *Components*: teams, actors, systems — là "nơi" responsibilities xảy ra
- *Start/End points*: điểm bắt đầu và kết thúc của flows
- *OR/AND Forks/Joins*: rẽ nhánh điều kiện và song song
- *Timers*: timeout conditions
- *Stubs*: dynamic sub-process placeholders — tương tự BPMN call activities
- *Plug-in Maps*: module scenarios có thể tái sử dụng
- *Scenario Definitions*: specifies start points, end points, initial variable values → executable traversal — UCM responsibilities có thể thực thi embedded code để update variables, variables này điều khiển OR-forks và dynamic stubs

**URN Links**:

Typed traceability links kết nối GRL và UCM elements. Ví dụ: link từ UCM component "Product Team" đến GRL actor "Product Team"; link từ UCM responsibility "Process Payment" đến GRL task "Process Payment". 18 OCL constraint rules trong jUCMNav tự động kiểm tra alignment — ví dụ: "mọi UCM component phải link đến một GRL actor."

**jUCMNav Tool**:

Eclipse-based, free, open-source. Khả năng: visual color feedback (xanh = thỏa mãn, đỏ = không thỏa mãn), OCL verification với one-click quick fixes, scenario traversal + xuất sequence diagrams, static/forward slicing, custom profiles. Hạn chế: thiếu debugging environment cho workflow execution.

**AoURN — Aspect-oriented URN**:

Extension để xử lý crosscutting concerns (security, authentication, logging). Encapsulate concern thành "aspect", compose aspect vào cả GRL và UCM models. Composition là semantic-based → refactoring-safe. Ví dụ: Authentication aspect tự động inject "Security" softgoal vào GRL và insert aspect stubs trước mọi user responsibility trong UCM của Incident Management System.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Xét ví dụ về một **dịch vụ telemonitoring bệnh nhân** (một trong các case studies trong bài):

**Bước 1 — Xây dựng GRL model**:
- Actor: Patient, HealthProvider, TelemonitoringSystem
- Goal(Patient): "Được chăm sóc sức khỏe từ xa"
- Goal(HealthProvider): "Giảm chi phí tái nhập viện"
- Softgoal: "Ease of Use" (Patient cần hệ thống dễ dùng)
- KPI: "Hospital Readmission Rate" → Target = 0%, Threshold = 5%, Worst Case = 20%
  - Nếu thực tế đo được 10% readmission → satisfaction = linear interpolation giữa 0% và 20% → khoảng 50/100
- Contribution: Task "Daily Vitals Check" --Help→ Goal "Giảm tái nhập viện"
- Strategy A: initial KPI = hiện tại 10%; Strategy B: dự báo sau deploy = 3%

**Bước 2 — Xây dựng UCM model**:
- Component: Patient, NurseStation, HealthApp
- Responsibilities: "MeasureVitals" (Patient), "TransmitData" (HealthApp), "ReviewAlerts" (NurseStation), "EscalateToDoctor" (NurseStation)
- Stub: "HandleEmergency" (dynamic — có thể plug in protocol A hoặc protocol B)
- Scenario Definition: start = "PatientLogsIn", variables = {alertThreshold = 85}, end = "SessionCompleted"
  - Khi vitals < alertThreshold → OR-fork kích hoạt "EscalateToDoctor"

**Bước 3 — Tạo URN Links**:
- Component "Patient" ↔ Actor "Patient" (traceability)
- Responsibility "ReviewAlerts" ↔ Task "Daily Review" trong GRL
- 18 OCL rules tự động check: "có component nào không link đến GRL actor không?" → jUCMNav flag và đề xuất quick fix

**Bước 4 — Phân tích**:
- Chạy Strategy A: KPI readmission = 10% → satisfaction "Giảm tái nhập viện" = 50 → GRL propagation lên Actor HealthProvider = partially satisfied
- Chạy Strategy B: KPI readmission = 3% → satisfaction = 85 → HealthProvider = mostly satisfied
- Trade-off: Strategy B tốt hơn về medical goal nhưng chi phí cao hơn — GRL cho thấy rõ ràng trade-off này
- Scenario traversal: chạy UCM với alertThreshold = 85 → verify rằng emergency flow hoạt động đúng

**Kết quả**: Analyst có thể thấy đồng thời "quy trình có hoạt động đúng không" (UCM traversal) VÀ "quy trình có đạt mục tiêu không" (GRL propagation) trong cùng một tool.

### 3.5 Điểm Mới So Với Trước

**Điểm 1 — Chuẩn hóa quốc tế**: URN là chuẩn ITU-T duy nhất tích hợp goal và process modelling — không phải academic proposal mà là industrial standard được adopt trong telecommunications, healthcare, government.

**Điểm 2 — KPI mechanism**: Cơ chế chuyển đổi measurements thực tế → satisfaction levels [0..100] qua Target/Threshold/Worst Case + linear interpolation là một bridge formal giữa "thế giới đo lường" và "thế giới goal satisfaction".

**Điểm 3 — OCL-based alignment verification**: 18 OCL constraint rules tự động kiểm tra goal-process alignment với one-click quick fixes — đây là automated verification, không phải manual review.

**Điểm 4 — Tính ứng dụng theo chiều rộng**: 20 năm, 1,466 papers, 70 patents, áp dụng từ telecommunications đến healthcare đến regulatory compliance — chứng minh generality.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Goal modelling nắm bắt intentions, goals, và social relationships của stakeholders để hỗ trợ trade-off analysis, trong khi business process modelling minh họa trình tự của các activities. Cùng nhau, chúng cung cấp một cái nhìn synergistic và đầy đủ về thiết kế hệ thống. Bài báo này báo cáo về gần hai thập kỷ kinh nghiệm sử dụng User Requirements Notation (URN) — một chuẩn ITU-T thống nhất goal và process modelling cả graphically lẫn textually. Bài review các statistics thô của URN literature và chi tiết hóa ứng dụng của nó trong các lĩnh vực như regulatory compliance, process mining, và process adaptation, đồng thời phác thảo các cơ hội nghiên cứu tương lai như ứng dụng AI data-driven và socio-cyber-physical systems.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

Bài báo là experience report, không có controlled experiment. Thay vào đó, tác giả báo cáo kết quả literature analysis và ứng dụng thực tế.

**Literature Analysis** (Scopus query ngày 8/3/2021):

| Query | Kết quả |
|---|---|
| Broad query (all metadata) | **1,466 URN-related papers** từ 76 quốc gia, kể từ 1995 |
| Strict query (Title + Abstract + Keywords) | **305 core URN papers** từ 37 quốc gia |
| 70 patents | Ảnh hưởng bởi URN (Google Scholar) |

**Phân bố 305 core papers theo domain**:

| Domain | Số papers |
|---|---|
| Software/Enterprise Architecture | 139 papers |
| Laws/Regulations | 133 papers |
| Business/Process/Value | 110 papers |
| Telecom/Networks | 89 papers |
| Health/Medicine | 71 papers |

**Quan sát quan trọng**: Chỉ khoảng **10%** papers khai thác *cả hai* GRL và UCM cùng nhau — phần lớn chỉ dùng một trong hai. Tiềm năng của URN chưa được khai thác hết.

**Kết quả ứng dụng cụ thể**:

- *Goal-process alignment*: jUCMNav với 18 OCL rules tự động phát hiện misalignments; quick fixes giảm thời gian sửa từ hàng giờ xuống vài phút.
- *Regulatory compliance (Legal-URN)*: Phân loại được 6 loại ambiguity pháp lý; GoRIM framework giúp regulators theo dõi xem regulations có đạt outcomes xã hội mong muốn không.
- *Healthcare process adaptation (AbPI)*: Áp dụng cho palliative care process — đo lường incremental impact của new activities trên stakeholder goals để minimize disruption.
- *Goal-oriented process mining (GoPED)*: Lọc event logs theo KPI satisfaction criteria → process models sạch hơn nhiều so với standard process mining (tránh "spaghetti" models).

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

### Hạn Chế Tác Giả Thừa Nhận

**Traceability maintenance**: Duy trì và cập nhật URN Links giữa GRL và UCM khi model thay đổi là công việc thủ công tốn thời gian và error-prone. Khi process thay đổi, analyst phải manually review tất cả URN Links để đảm bảo traceability còn hợp lệ.

**Scalability và modularity**: Xử lý models lớn và phức tạp vẫn là thách thức. Loose coupling của hai paradigm làm visual management khó khăn khi số lượng elements tăng lên hàng trăm.

**Symbol deficit**: Các ngôn ngữ visual gặp vấn đề về cognitive effectiveness — bindings, Boolean conditions, và scenario definitions không có graphical syntax riêng, phải dùng text annotations.

**Tooling gaps**: jUCMNav thiếu debugging environment cho workflow execution. Việc trace-through một scenario phức tạp để tìm bug khó khăn hơn so với các process modelling tools chuyên dụng.

**10% adoption gap**: Chỉ 10% papers khai thác full power của URN (cả GRL + UCM). Điều này cho thấy barrier về learning curve và tooling adoption vẫn cao.

### Hướng Nghiên Cứu Tiếp Theo

- **AI/ML integration**: Tích hợp URN với machine learning để học từ massive data streams — tự động cập nhật KPIs, phát hiện patterns, suggest strategies.
- **NLP cho automated traceability**: Dùng natural language processing để tự động tạo và duy trì URN Links từ text documentation — giảm manual overhead.
- **Socio-cyber-physical systems**: Mở rộng URN sang systems kết hợp con người, IoT, và autonomous agents — nơi goals và processes biến đổi real-time.
- **Advanced process mining**: Tích hợp sâu hơn với process mining tools (ProM, Celonis) để khai phá URN models từ event logs một cách automated.
- **Formal verification**: Tăng cường khả năng model checking với formal methods beyond OCL.

---

## PHẦN 7 — Kết Luận

Bài báo tổng kết gần hai thập kỷ kinh nghiệm với URN — chuẩn ITU-T duy nhất tích hợp goal modelling (GRL) và process modelling (UCM) trong một framework thống nhất. Với 1,466 papers liên quan, 305 core papers từ 37 quốc gia, và 70 patents, URN chứng minh sự chấp nhận rộng rãi trong cả học thuật và công nghiệp. Sáu lĩnh vực ứng dụng chính (goal-process alignment, regulatory compliance, process adaptation, value co-creation, goal-oriented process mining, advanced techniques) cho thấy breadth và depth của framework. Tuy nhiên, chỉ 10% papers khai thác đầy đủ cả hai GRL và UCM — cho thấy learning curve và tooling gaps cần tiếp tục cải thiện. Hướng nghiên cứu tương lai tập trung vào AI/ML integration, NLP cho automated traceability, và mở rộng sang socio-cyber-physical systems.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc chứng minh rằng sau 20 năm, khả năng tích hợp goal modelling và process modelling trong URN vẫn chưa được khai thác hết — chỉ 10% papers dùng cả hai GRL và UCM, trong khi đây chính là điểm sức mạnh cốt lõi của URN; và tương lai của URN nằm ở việc tích hợp với AI/ML để biến framework từ "ngôn ngữ mô hình hóa" thành "hệ thống ra quyết định thích ứng thời gian thực."
