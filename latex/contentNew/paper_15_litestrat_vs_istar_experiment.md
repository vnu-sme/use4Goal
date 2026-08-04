# Including Business Strategy in Model-Driven Methods: An Experiment

**Tác giả:** Rene Noel, Jose Ignacio Panach, Oscar Pastor  |  **Năm:** 2023  |  **Venue:** Requirements Engineering Journal (2023) 28:411–440  |  **DOI:** 10.1007/s00766-023-00400-3

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo nằm ở giao điểm giữa Business Strategy Modelling, Goal-Oriented Requirements Engineering (GORE), và Model-Driven Development (MDD). Đây là một lĩnh vực ngày càng quan trọng trong bối cảnh các tổ chức phát triển phần mềm hiện đại — đặc biệt là các "software-centric organisations" (SCO) — đang nỗ lực liên kết chiến lược kinh doanh với thiết kế hệ thống thông tin.

Model-Driven Architecture (MDA) — chuẩn của Object Management Group (OMG) — chia phát triển hệ thống thành nhiều lớp trừu tượng. Lớp trừu tượng cao nhất là Computation-Independent Model (CIM), đại diện cho các mối quan tâm ở cấp độ business: goals (mục tiêu), processes (quy trình), commercial models (mô hình thương mại). CIM sau đó được chuyển đổi thành Platform-Independent Model (PIM) và cuối cùng là Platform-Specific Model (PSM) để sinh code.

Trong thực tế, những tổ chức phát triển phần mềm thành công nhất là những tổ chức có khả năng thiết kế cấu trúc tổ chức của mình xung quanh các mục tiêu chiến lược — và sau đó "phản chiếu" cấu trúc đó vào các hệ thống phần mềm của mình. Đây là tiên đề được dự đoán bởi Conway's Law: "tổ chức sẽ tái tạo cấu trúc của mình trong mọi thứ họ thiết kế." Các frameworks mở rộng agility trong doanh nghiệp như SAFe, LeSS đều khuyến nghị liên tục điều chỉnh cấu trúc tổ chức theo thiết kế hệ thống mong muốn.

### Bài toán cụ thể

Vấn đề trung tâm: **làm thế nào để đưa thông tin chiến lược kinh doanh (business strategy) vào CIM level của MDA một cách chính xác và có hệ thống?**

Có hai hướng tiếp cận chính đang tồn tại:

**Hướng 1 — Enterprise Architecture (EA) frameworks**: ArchiMate và Business Motivation Model (BMM) cung cấp các khái niệm để biểu diễn mục tiêu cấp cao và các hướng hành động. Tuy nhiên, EA frameworks được thiết kế cho strategic alignment của toàn bộ tổ chức ở cấp IT — không cover đặc tả chi tiết của hệ thống thông tin cần thiết cho model-driven development, và không xem xét khái niệm cấu trúc tổ chức (organisational structure).

**Hướng 2 — Goal-oriented modelling với i***: i* framework đã được áp dụng thành công cho organizational modelling. Các social constructs của i* (actors, dependencies) có thể được dùng để biểu diễn cấu trúc tổ chức. Tuy nhiên, i* thiếu một số khái niệm business strategy cụ thể, không có modelling procedure rõ ràng để hướng dẫn người dùng chọn construct nào — điều này làm khó tích hợp i* models vào MDA frameworks.

Để giải quyết, nhóm nghiên cứu đã thiết kế **LiteStrat** — một phương pháp mô hình hóa chiến lược kinh doanh được thiết kế đặc biệt để tương thích với MDD. Bài báo này không mô tả LiteStrat lần đầu tiên (đó là công trình trước, Noel et al. 2021) mà **thực hiện một so sánh thực nghiệm (empirical experiment) giữa LiteStrat và i*** để đo lường xem LiteStrat có thực sự tốt hơn i* trong việc mô hình hóa chiến lược kinh doanh hay không.

Đầu vào của thực nghiệm: mô tả một tình huống chiến lược kinh doanh bằng văn bản tự nhiên. Đầu ra: model được tạo bởi participants, được chấm điểm theo các tiêu chí Accuracy (chính xác) và Completeness (đầy đủ) dựa trên một oracle model.

### Tại sao khó

**Thứ nhất**, i* là framework được nghiên cứu rộng rãi nhất trong GORE, có cộng đồng người dùng lớn. Cho rằng LiteStrat — một ngôn ngữ mới, hẹp hơn — có thể tốt hơn i* cho một use case cụ thể (business strategy) là một claim cần evidence mạnh.

**Thứ hai**, việc đo lường "chất lượng" của một model ngôn ngữ mô hình hóa không tầm thường. Không có ground truth tuyệt đối — mỗi người có thể biểu diễn cùng một tình huống theo nhiều cách khác nhau với i*. Tác giả cần thiết kế một measurement procedure thuyết phục.

**Thứ ba**, so sánh công bằng giữa hai ngôn ngữ mô hình hóa đòi hỏi thiết kế thực nghiệm cẩn thận: kiểm soát biến, phân nhóm người dùng, loại bỏ bias do problem description, v.v.

**Thứ tư**, LiteStrat được thiết kế với quy trình mô hình hóa chặt chẽ hơn i* — câu hỏi là liệu sự chặt chẽ đó có làm giảm efficiency hay satisfaction của người dùng hay không.

### Đóng góp của bài

1. **Literature review** về experimental comparisons của modelling languages — cung cấp bức tranh toàn cảnh về cách đo lường chất lượng ngôn ngữ mô hình hóa.
2. **Thiết kế thực nghiệm và measurement approach** để so sánh semantic quality của hai ngôn ngữ mô hình hóa — reusable cho các nghiên cứu tương lai.
3. **Bằng chứng thực nghiệm**: LiteStrat cải thiện đáng kể accuracy và completeness so với i* trong business strategy modelling, mà không ảnh hưởng đến efficiency và satisfaction của người dùng.

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

Bài báo có phần related work tập trung vào các nghiên cứu so sánh thực nghiệm của các ngôn ngữ mô hình hóa — không phải về business strategy hay GORE nói chung. Tác giả thực hiện targeted literature review trên Web of Science và tìm được 13 nghiên cứu liên quan.

### Hướng 1: Information Retrieval (IR) Approach

Nhóm nghiên cứu này đo lường Completeness và Correctness của models thông qua precision và recall metrics — lấy cảm hứng từ information retrieval.

Abrahao et al. so sánh i* và value@GRL cho incremental software development với 40 M.Sc. students + 28 software engineers + 12 analysts. Kết quả: value@GRL tạo ra models chất lượng cao hơn, người dùng productive hơn. Nghiên cứu được replicate trong gia đình 184 subjects, xác nhận kết quả ban đầu.

Jesus Souza et al. so sánh Context-aware Feature Model và Tropos Goal Model cho Dynamic Software Product Lines — tìm thấy khác biệt đáng kể về precision.

**Điểm yếu**: Tạo oracle model có thể đưa vào bias vì oracle chỉ là một trong nhiều cách biểu diễn hợp lệ của một tình huống. Scanniello và Erra (2014) đã chỉ ra vấn đề này nhưng các nghiên cứu khác không thảo luận.

### Hướng 2: Semantic Quality Inspection Approach

Đây là hướng kiểm tra trực tiếp bởi expert xem model có biểu diễn đúng domain không — thay vì dựa vào precision/recall.

Kabeli và Shoval so sánh FOOM và POOM với 156 undergraduate students. Kết quả: FOOM tạo ra models chính xác và comprehensible hơn.

Peleg và Dori so sánh OPM và OMT với 86 undergraduate students. Grading scheme có 38 items, mỗi item từ 0 đến 1, với lỗi nhỏ/vừa/lớn được tính điểm khác nhau (0.25, 0.5, 0.75). 8 trong 38 items có significant difference ủng hộ OPM.

**Ưu điểm so với IR**: Linh hoạt hơn — không cần oracle cứng nhắc. Đánh giá trực tiếp về semantic correctness của việc sử dụng constructs.

### Hướng 3: Các So Sánh Khác

Các nghiên cứu khác so sánh: Entity-Relationship vs Object-Oriented (Siau et al.), Business Process Models vs Use Cases (Dobing & Parsons), ENSURE vs Traditional RE (Dieste et al.), Tangible Modelling vs Computer-aided Modelling, i* vs KAOS (Horkoff et al.).

Kết quả đa dạng: một số nghiên cứu tìm thấy significant differences, một số không.

**Điểm quan trọng từ i* vs KAOS**: Nghiên cứu của Horkoff et al. với 19 graduate students cho thấy KAOS language quality cao hơn i*, nhưng i* models có higher completeness. Điều này cho thấy quality và completeness có thể tách rời.

### Khoảng trống (Research Gap)

Không có nghiên cứu nào so sánh thực nghiệm LiteStrat với i* (hoặc bất kỳ ngôn ngữ nào khác) cho business strategy modelling trong MDD context. Bài báo lấp đầy khoảng trống này bằng controlled experiment được thiết kế cẩn thận, kết hợp IR approach với semantic quality inspection, với thiết kế 2×2 factorial để kiểm soát biến.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

LiteStrat được xây dựng trên quan sát sau: i* là một ngôn ngữ tổng quát, cố ý "mở" để linh hoạt trong nhiều use cases. Chính sự mở này lại là điểm yếu khi áp dụng cho một use case cụ thể như business strategy modelling trong MDD: người dùng không biết construct nào nên dùng, mọi người tạo ra các models rất khác nhau cho cùng một tình huống, và transformation rules sang MDA artifacts khó xác định vì không có chuẩn hóa.

LiteStrat giải quyết điều này bằng cách: **thiết kế một ngôn ngữ chuyên biệt hơn với procedure mô hình hóa bắt buộc**. Ít linh hoạt hơn, nhưng đổi lại — chuẩn hóa hơn, chính xác hơn, và dễ tích hợp vào MDA pipeline hơn.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**LiteStrat trong MDA pipeline**:

```
Business Strategy (thực tế)
          ↓
LiteStrat Model (CIM level)
  - External Actors + Influences
  - Organisation Units + Goals + Strategies
  - Roles + Objectives + Tactics
  - Outcome Influences
          ↓
Model-to-Model Transformation
  (Assignments → Business Processes)
          ↓
PIM (Platform-Independent Model)
  - Business Process Models
  - Class Diagrams
          ↓
PSM (Platform-Specific Model)
          ↓
Generated Application Code
```

LiteStrat hoạt động ở CIM level — lớp cao nhất và trừu tượng nhất. Thông tin về chiến lược và cấu trúc tổ chức được mô hình hóa ở đây, sau đó được chuyển đổi sang PIM (business processes, class diagrams) và cuối cùng là code.

**Quy trình mô hình hóa "outside-in" của LiteStrat (4 bước bắt buộc)**:

**Bước 1**: Xác định các external actors bên ngoài tổ chức (competitors, customers, regulators) và mô hình hóa Influence của họ lên tổ chức. Xác định overarching Goals của tổ chức phản hồi với các influences đó.

**Bước 2**: Mô hình hóa một tập hợp Strategies để đạt được Goals. Tinh chỉnh Strategies thành các Tactics cụ thể hơn và gán Tactics cho các Organisation Units có khả năng thực hiện.

**Bước 3**: Tinh chỉnh Tactics thành các Objectives đo lường được và gán Objectives cho các Roles cụ thể trong Organisation Units.

**Bước 4**: Mô hình hóa outcomes kỳ vọng của mỗi Organisation Unit như các Influences tác động lên external actors (ví dụ: khách hàng) và các units khác. Bổ sung thêm external actors và influences còn thiếu.

### 3.3 Các Thành Phần Chính

**Các khái niệm trong LiteStrat language**:

| Concept | Định nghĩa | So sánh với i* |
|---|---|---|
| **Goal** | Trạng thái mong muốn cấp cao, dành riêng cho Organisation Units | i* "Goal" dùng cho mọi level |
| **Strategy** | Các hành động cấp cao, rõ ràng nhằm đạt Goal | Gần với i* "Task" nhưng chỉ ở high-level |
| **Objective** | Trạng thái mong muốn cụ thể và đo lường được, dành riêng cho Roles | i* không phân biệt Goal vs Objective |
| **Tactic** | Hành động cụ thể, process-focused để triển khai Strategy | i* "Task" không phân biệt Strategy vs Tactic |
| **Organisation Unit** | Biểu diễn tổ chức hoặc đơn vị nội bộ (team, department) | i* "Agent" dùng cho mọi loại entity |
| **Actor** | Entity bên ngoài tổ chức (competitor, customer) có intentions không biết đầy đủ | i* "Actor" dùng cho cả internal và external |
| **Influence** | Quan hệ biểu diễn ảnh hưởng có/không có intentions rõ ràng | i* "Dependency" ngụ ý intentional delegation |

**Điểm khác biệt then chốt**: LiteStrat loại bỏ Resource và Quality từ i*, thay thế bằng strict hierarchy Goal→Strategy→Tactic→Objective với assignments bắt buộc. Đây là thiết kế có chủ đích: không để người dùng tự do chọn constructs mà force một cấu trúc chuẩn hóa.

**Measurement framework (cho phần thực nghiệm)**:

Tác giả sử dụng semantic quality inspection approach. Họ phân tách description của business case thành các "statements" thuộc bốn loại:
- *Motivation statements*: các động lực, influences bên ngoài
- *Actions statements*: các strategies, tactics ở cấp cao và cụ thể
- *Roles and Responsibilities statements*: roles và objectives được gán
- *Outcomes statements*: kết quả kỳ vọng

Mỗi statement được chấm theo hai chiều:
- **Accuracy** (0-2 điểm): có sử dụng construct ngữ nghĩa đúng không? 2 = hoàn toàn đúng, 1 = phần lớn đúng nhưng có misuse, 0 = sai hoặc thiếu.
- **Completeness** (0-2 điểm): có đủ domain elements không? 2 = đầy đủ, 1 = phần lớn đủ, 0 = thiếu nhiều. Completeness không quan tâm construct có đúng hay không.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Giả sử chúng ta có một tình huống chiến lược kinh doanh như sau (tương tự insurance case trong thực nghiệm):

> *"TechInsure là công ty bảo hiểm công nghệ. Các insurtech mới nổi (competitors) đang tung ra sản phẩm bảo hiểm theo yêu cầu với giá thấp hơn. TechInsure đặt mục tiêu giữ vững thị phần. Để đạt được điều này, họ triển khai chiến lược ra mắt sản phẩm digital. Nhóm Product (organisation unit) phụ trách chiến lược này thực hiện các tactics: xây dựng mobile app và tích hợp real-time risk assessment. Product Manager (role) cần đạt objective: tung ra ít nhất 2 sản phẩm mới mỗi năm."*

**Với i*** (không có procedure bắt buộc):
Một người dùng có thể mô hình hóa: Actor(TechInsure), Actor(insurtech), Task(ra mắt sản phẩm digital), Goal(giữ thị phần), Dependency(TechInsure→Product Manager for Task). Người khác có thể dùng hoàn toàn khác: Resource(mobile app), Task(tích hợp risk assessment), v.v. Không có chuẩn hóa.

**Với LiteStrat** (procedure outside-in bắt buộc):

*Bước 1*: Xác định external actors và influences:
- Actor: insurtech competitors
- Influence: "insurtech → TechInsure: tung sản phẩm giá thấp" (threat)
- Organisation Unit: TechInsure
- Goal(TechInsure): "giữ vững thị phần"

*Bước 2*: Strategies và Tactics:
- Strategy(TechInsure→Goal): "ra mắt sản phẩm digital"
- Organisation Unit: Product Team
- Tactic(Product Team): "xây dựng mobile app"
- Tactic(Product Team): "tích hợp real-time risk assessment"

*Bước 3*: Objectives và Roles:
- Role: Product Manager (trong Product Team)
- Objective(Product Manager): "tung ra ≥2 sản phẩm mới/năm"

*Bước 4*: Outcomes:
- Influence: "Product Team → Customer: sản phẩm bảo hiểm digital on-demand"

**Chấm điểm ví dụ**:
Statement "insurtech đang tung sản phẩm giá thấp":
- i* model dùng Task("giảm giá") → Accuracy = 1 (dùng Task thay vì Influence, không đúng semantics); Completeness = 1 (có đề cập nhưng thiếu hướng tác động)
- LiteStrat model dùng Actor(insurtech) + Influence("tung sản phẩm giá thấp"→TechInsure) → Accuracy = 2 (construct đúng, Influence capture được tính chất không-intentional của external event); Completeness = 2 (đầy đủ)

Statement "Product Manager cần đạt objective tung ≥2 sản phẩm/năm":
- i* model: nhiều người dùng i* có thể bỏ qua hoặc mô hình hóa là Goal không gán cho Role cụ thể → Accuracy = 0-1, Completeness = 0-1
- LiteStrat model: bắt buộc Objective gán cho Role → Accuracy = 2, Completeness = 2

### 3.5 Điểm Mới So Với Trước

**So với i***: LiteStrat không phải là i* "tốt hơn" theo nghĩa tổng quát. Nó là ngôn ngữ hẹp hơn với procedure cứng hơn — đánh đổi tính linh hoạt của i* lấy tính chính xác và chuẩn hóa trong một use case cụ thể.

**So với EA frameworks**: LiteStrat bổ sung cấu trúc tổ chức (Organisation Units + Roles) và procedure hướng MDD vào business strategy — điều mà ArchiMate và BMM không làm.

**Điểm sáng tạo thực sự**: Concept **Influence** là đổi mới quan trọng nhất. i* "Dependency" ngụ ý intentional delegation — A giao B làm X. Influence trong LiteStrat không yêu cầu intention — nó capture được những sự kiện bên ngoài không có chủ đích (competitor tung sản phẩm, quy định thay đổi, thị trường biến động). Điều này phù hợp hơn với thực tế business strategy, nơi nhiều yếu tố tác động lên tổ chức mà không ai "muốn" chúng tác động theo cách đó.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Các tổ chức lấy phần mềm làm trung tâm (software-centric organisations) thiết kế cấu trúc tổ chức lỏng lẻo kết nối xung quanh các mục tiêu chiến lược, phản chiếu thiết kế này vào business processes và information systems của họ. Ngày nay, việc xử lý business strategy trong bối cảnh model-driven development là một thách thức vì các khái niệm then chốt như cấu trúc tổ chức và strategic ends/means chủ yếu được giải quyết ở cấp độ enterprise architecture cho strategic alignment của toàn bộ tổ chức, mà chưa được tích hợp vào các phương pháp MDD như một requirements source. Để vượt qua vấn đề này, các nhà nghiên cứu đã thiết kế LiteStrat — một phương pháp mô hình hóa chiến lược kinh doanh tương thích với MDD để phát triển information systems. Bài báo này trình bày một so sánh thực nghiệm giữa LiteStrat và i* — một trong những models được sử dụng nhiều nhất cho strategic alignment trong MDD context. Bài báo đóng góp: một literature review về so sánh thực nghiệm các modelling languages, thiết kế nghiên cứu để đo lường và so sánh semantic quality của các modelling languages, và bằng chứng thực nghiệm về sự khác biệt giữa LiteStrat và i*. Evaluation bao gồm một factorial experiment 2×2 với 28 undergraduate subjects. Các khác biệt có ý nghĩa thống kê ủng hộ LiteStrat được tìm thấy cho accuracy và completeness của models, trong khi không có khác biệt về efficiency và satisfaction của người mô hình hóa. Các kết quả này cung cấp bằng chứng về tính phù hợp của LiteStrat cho business strategy modelling trong model-driven context.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

### Thiết kế và Participants

- **28 undergraduate students** từ khóa học Requirements Engineering năm 3 tại Universidad de Valparaíso, Chile.
- **Thiết kế 2×2 factorial**: factor chính là modelling method (LiteStrat vs. i*); blocking variable là problem (Telecommunications case và Insurance case) để loại bỏ bias do đặc thù của từng problem.
- **Quy trình**: 1 giờ training về khái niệm business strategy và phương pháp được gán, sau đó 1 giờ mô hình hóa bằng tay (bút và giấy) một trong hai business cases.

### Kết Quả Chính

**Accuracy (chính xác trong sử dụng constructs)**:

| Dimension | LiteStrat Mean | i* Mean | p-value | Kết luận |
|---|---|---|---|---|
| Total Accuracy | **14.697** | 9.696 | 0.014 | ✓ LiteStrat tốt hơn |
| Motivation Accuracy | **4.879** | 2.527 | < 0.001 | ✓ LiteStrat tốt hơn đáng kể |
| Actions Accuracy | — | — | 0.117 | ✗ Không có khác biệt |
| Role-Responsibility Accuracy | — | — | 0.051 | ~ Biên giới significance |

**Completeness (đầy đủ về domain elements)**:

| Dimension | LiteStrat Mean | i* Mean | p-value | Kết luận |
|---|---|---|---|---|
| Total Completeness | **16.381** | 12.268 | 0.015 | ✓ LiteStrat tốt hơn |
| Motivation Completeness | **5.095** | 2.660 | < 0.0001 | ✓ LiteStrat tốt hơn đáng kể |
| Actions Completeness | **4.821** | 3.884 | 0.046 | ✓ LiteStrat tốt hơn |
| Role-Responsibility Completeness | — | — | 0.178 | ✗ Không có khác biệt |

**Efficiency và Satisfaction**:

| Variable | LiteStrat Mean | i* Mean | p-value | Kết luận |
|---|---|---|---|---|
| Efficiency (minutes) | 50.214 | 58.232 | 0.102 | ✗ Không có khác biệt |
| Perceived Ease of Use | — | — | > 0.05 | ✗ Không có khác biệt |
| Perceived Usefulness | — | — | > 0.05 | ✗ Không có khác biệt |
| Intention to Use | — | — | > 0.05 | ✗ Không có khác biệt |

### Phân Tích Chất Lượng

**Tại sao Motivation là nơi khác biệt lớn nhất?** LiteStrat có construct Influence đặc biệt để mô hình hóa ảnh hưởng của external actors — điều mà i* không có construct tương đương. Khi người dùng i* cần mô hình hóa "competitor tung sản phẩm mới gây áp lực", họ không có construct phù hợp và phải dùng workaround (Task, Goal, hay bỏ qua hoàn toàn). 6/7 LiteStrat users đạt điểm accuracy hoàn hảo cho high-level action statements, so với chỉ 3/7 i* users.

**Tại sao Actions Accuracy không có khác biệt nhưng Actions Completeness có?** i* users thường bỏ qua hoàn toàn các detailed actions (tactics), giả định chúng được ngụ ý bởi high-level tasks — điều này ảnh hưởng đến completeness nhưng không ảnh hưởng accuracy của những gì họ có mô hình hóa.

**Structural heterogeneity của i***: i* users tạo ra 6 workaround khác nhau để biểu diễn roles và responsibilities. LiteStrat users tạo ra models đồng nhất hơn nhiều — đây là yếu tố quan trọng cho MDD integration vì transformation rules cần predictable structures.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

### Hạn Chế Tác Giả Thừa Nhận

**Statistical power thấp**: Với chỉ 28 subjects, sample size không đủ để detect medium hoặc small effect sizes với độ tin cậy cao. Ideal size cho experiment này là ít nhất 39 subjects. Điều này giải thích tại sao một số so sánh (Actions Accuracy, Role-Responsibility Completeness) không đạt significance dù LiteStrat có mean cao hơn.

**Sử dụng students thay vì practitioners**: Undergraduate students thiếu kinh nghiệm công nghiệp. Kết quả có thể không generalize hoàn toàn cho professional experts, những người có thể navigate i*'s flexibility tốt hơn dựa trên kinh nghiệm.

**Artificial problems**: Hai business cases được tạo ra đặc biệt cho thực nghiệm — không phải từ real-world, large-scale industrial projects. Business strategy của các tổ chức thực tế phức tạp hơn nhiều.

**LiteStrat-specific trong MDD**: Bài báo thực nghiệm LiteStrat trong bối cảnh MDD, nhưng kết quả có thể không áp dụng cho các use cases khác (ví dụ: agile development, startups không dùng MDA).

**Nhận xét bổ sung**: Thực nghiệm đo lường short-term modeling (1 giờ) — không đo lường long-term usability khi người dùng đã quen với cả hai phương pháp. Sau một thời gian training sâu hơn, sự khác biệt efficiency có thể thay đổi theo hướng không dự đoán được.

### Hướng Nghiên Cứu Tiếp Theo

- **Replication với industry practitioners**: để kiểm tra xem kết quả có generalize không khi người dùng có kinh nghiệm công nghiệp thực tế.
- **Real-world industrial case studies**: áp dụng LiteStrat cho các dự án thực tế quy mô lớn để validate applicability.
- **Tích hợp với MDA tools**: phát triển tooling support cho LiteStrat và model transformation rules đến business processes / class models.
- **Extension sang iStar 2.0**: khảo sát liệu iStar 2.0 (tiêu chuẩn mới hơn với richer ontology) có thu hẹp khoảng cách với LiteStrat không.
- **Longitudinal study**: theo dõi chất lượng models qua nhiều iterations để đo tác động dài hạn của quy trình mô hình hóa.

---

## PHẦN 7 — Kết Luận

Bài báo cung cấp bằng chứng thực nghiệm đầu tiên rằng LiteStrat — một phương pháp mô hình hóa chiến lược kinh doanh được thiết kế đặc biệt cho MDD context — tốt hơn i* một cách có ý nghĩa thống kê về accuracy và completeness của models, đặc biệt trong phần Motivation (biểu diễn ảnh hưởng bên ngoài) và Actions (phân biệt strategies và tactics). Quan trọng không kém là LiteStrat đạt được những cải tiến này mà không ảnh hưởng đến efficiency và satisfaction của người dùng — chứng minh rằng có thể tăng tính chính xác và chuẩn hóa của ngôn ngữ mô hình hóa mà không trả giá bằng usability.

Hạn chế chính là sample size nhỏ (28 subjects), sử dụng students thay vì professionals, và artificial problems. Các replication studies với practitioners và industrial cases là bước cần thiết tiếp theo.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** việc chứng minh thực nghiệm rằng thiết kế ngôn ngữ mô hình hóa với procedure bắt buộc ("outside-in") và phân cấp khái niệm chặt chẽ (Goal→Strategy→Tactic→Objective) thực sự cải thiện chất lượng model mà không làm giảm user experience — phá vỡ quan điểm thông thường cho rằng linh hoạt hơn luôn tốt hơn trong ngôn ngữ mô hình hóa.
