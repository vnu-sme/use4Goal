# Verifying Goal-Oriented Specifications Used in Model-Driven Development Processes

**Tác giả:** Renata S. S. Guizzardi, Giovanni Guizzardi, et al.  |  **Năm:** 2014–2015  |  **Venue:** CAiSE 2014 / Requirements Engineering Journal

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo nằm ở giao điểm giữa hai lĩnh vực quan trọng trong kỹ thuật phần mềm: Goal-Oriented Requirements Engineering (GORE) và Model-Driven Development (MDD). GORE là hướng tiếp cận phát triển phần mềm bắt đầu từ mục tiêu của stakeholders — tại sao hệ thống cần tồn tại, ai cần gì, ai phụ thuộc vào ai — trước khi đặc tả những gì hệ thống phải làm. Ngôn ngữ i* (iStar) là một trong những ngôn ngữ goal-oriented phổ biến nhất, cho phép mô hình hóa actors (tác nhân), goals (mục tiêu), tasks (tác vụ), resources (tài nguyên) và dependencies (phụ thuộc) giữa các actors.

MDD, mặt khác, là cách tiếp cận phát triển phần mềm dựa trên model — thay vì viết code thủ công, kỹ sư xây dựng models ở mức trừu tượng cao và sử dụng model transformation tools để tự động sinh code. Integranova là một MDD platform điển hình: kỹ sư xây dựng class model (tương tự UML class diagram nhưng phong phú hơn) và Integranova tự động sinh toàn bộ application code.

Sự kết hợp tự nhiên giữa hai hướng này là: sử dụng i* để nắm bắt intentions và goals của stakeholders ở giai đoạn requirements, sau đó chuyển đổi i* model sang MDD model (class model của Integranova) để sinh code. Đây là một pipeline hấp dẫn vì nó nối liền hai giai đoạn quan trọng nhất của phát triển phần mềm — phân tích yêu cầu và thiết kế/cài đặt — thành một luồng tự động hoá.

Tuy nhiên, pipeline này có một điểm yếu chết người: nếu i* model có lỗi, lỗi sẽ lan truyền qua transformation và xuất hiện trong code được sinh ra. Việc phát hiện lỗi muộn — sau khi code đã được sinh — rất tốn kém. Hơn nữa, không phải tất cả kỹ sư yêu cầu đều hiểu đủ Integranova constraints để biết i* model của mình có "compatible" với MDD pipeline hay không.

### Bài toán cụ thể

Bài báo đặt câu hỏi: **làm thế nào để tự động xác minh rằng một i* model đủ chất lượng để đưa vào MDD pipeline trước khi thực hiện transformation?**

Cụ thể hơn, tác giả xác định hai loại lỗi nghiêm trọng cần phát hiện sớm:

1. **Non-Accessible Elements (NAE)**: Các phần tử i* không thể được ánh xạ sang bất kỳ phần tử nào trong Integranova class model. Ví dụ: một actor dependency trong i* không có resource definition tương ứng → transformation rule không thể áp dụng → class model thiếu element quan trọng.

2. **Non-Instantiable Classes (NIC)**: Các cấu trúc trong i* model mà khi được chuyển đổi, tạo ra Integranova class với các constraints mâu thuẫn — class xuất hiện trong model nhưng không thể có instance nào trong runtime. Đây là loại lỗi đặc biệt nguy hiểm vì class model nhìn bề ngoài có vẻ hợp lệ nhưng ứng dụng sinh ra sẽ có runtime errors.

Đầu vào của bài toán: i* model (actors, goals, tasks, resources, dependencies) + transformation rules i*→Integranova + integration metamodel. Đầu ra: danh sách lỗi phân loại (NAE Critical, NIC Critical, Warning) + Fixing Guidelines chỉ định cách sửa cấu trúc i* nào để xóa bỏ lỗi.

### Tại sao khó

Có bốn thách thức cốt lõi khiến bài toán này không tầm thường:

**Thứ nhất**, i* metamodel và Integranova metamodel có cấu trúc khác nhau về bản chất. Ánh xạ không phải 1-to-1: một actor i* có thể ánh xạ thành nhiều class, một dependency có thể ánh xạ thành association hoặc attribute hoặc method tùy theo context. Việc xác định điều kiện nào của i* đảm bảo transformation thành công đòi hỏi phải hiểu sâu cả hai metamodel.

**Thứ hai**, kỹ sư yêu cầu — người xây dựng i* model — thường không biết Integranova semantics. Ngược lại, kỹ sư MDD thường không biết i* semantics. Việc phát hiện lỗi cross-metamodel đòi hỏi kiến thức về cả hai phía.

**Thứ ba**, chưa có tiêu chí hình thức nào định nghĩa "i* model đủ tốt để chuyển đổi sang Integranova." Điều này nghĩa là việc đánh giá chất lượng hoàn toàn phụ thuộc vào kinh nghiệm cá nhân của kỹ sư — error-prone và không reproducible.

**Thứ tư**, lỗi NIC đặc biệt khó phát hiện thủ công: một class non-instantiable không hiển thị trực tiếp trong class model nhìn bề ngoài mà chỉ được phát hiện khi cố gắng tạo instance trong runtime. Kỹ sư phải đồng thời hiểu i* structure, transformation rule, và Integranova constraint semantics mới nhận ra vấn đề.

### Đóng góp của bài

1. **VeMI framework** (Verification for Model Integration): phương pháp đầu tiên xác minh i* model trước khi đưa vào MDD pipeline — pre-transformation verification.
2. **Integration Metamodel**: kết hợp i* metamodel và Integranova metamodel thành một metamodel chung, mã hóa transformation rules như cross-metamodel constraints.
3. **OCL Verification Measures**: các OCL queries thực thi được (executable) để phát hiện NAE và NIC — formal, không phải heuristic.
4. **NAE + NIC taxonomy**: phân loại đầy đủ các loại lỗi với tác động rõ ràng đến MDD output.
5. **Actionable Fixing Guidelines**: không chỉ báo lỗi mà hướng dẫn sửa i* model cụ thể cho từng loại lỗi.
6. **Bằng chứng thực nghiệm**: controlled experiment với 24 participants, industrial case study thực tế (photography agency).

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: i* Analysis Tools

Các công cụ phân tích i* hiện có như jUCMNav (URN Navigator) và OpenOME hỗ trợ phân tích goal satisfaction, contribution analysis, và conflict detection trong nội bộ i* model. Những công cụ này rất hữu ích để kiểm tra xem goals có đạt được không dựa trên task contributions. Tuy nhiên, chúng không biết gì về MDD transformation — không thể kiểm tra xem i* model có compatible với Integranova hay không vì chúng không encode Integranova constraints.

### Hướng 2: GORE → Code Generation (Tropos + JADE)

Một số công trình về tự động hóa từ goal models sang code, điển hình là Tropos framework kết hợp với JADE (Java Agent Development Framework). Ý tưởng: từ i*-based Tropos model, sinh Java agents. Hướng này không có giai đoạn verification trước transformation — giả định rằng nếu transformation rules được áp dụng đúng, output sẽ correct. Lỗi trong model nguồn không được phát hiện cho đến khi agent code có runtime problems.

### Hướng 3: Model Transformations (ATL, QVT)

Ngôn ngữ transformation như ATL (Atlas Transformation Language) và QVT (Query/View/Transformation) cung cấp khả năng tự động hóa model-to-model transformation. Chúng có thể mã hóa transformation rules i*→Integranova. Nhưng chúng giả định model nguồn đã đúng — preconditions của transformation rules được giả định thỏa mãn, không được kiểm tra trước.

### Hướng 4: OCL Constraints trong MDD

Object Constraint Language (OCL) được dùng phổ biến để kiểm tra constraints trên MDD models. Tuy nhiên, ứng dụng OCL truyền thống là trên model đích (class model) — không phải model nguồn (i* model). Nếu i* model tạo ra class model lỗi, OCL constraints trên class model chỉ phát hiện sau khi transformation đã xảy ra.

### Hướng 5: Goal-Oriented Requirements trong MDA

Một số nghiên cứu kết hợp goal modelling với Model-Driven Architecture (MDA), đặt goal model ở CIM level và process/class model ở PIM level. Stra2Bis (Koliadis & Ghose) là một ví dụ trong survey này. Tuy nhiên, các nghiên cứu này tập trung vào transformation methodology, không phải pre-transformation verification quality.

### Khoảng trống (Research Gap)

Chưa có công cụ nào tự động xác minh i* model trước khi đưa vào MDD pipeline, dựa trên hiểu biết đồng thời về cả i* metamodel và MDD tool metamodel. Cụ thể hơn: chưa có cơ chế formal nào phát hiện NAE và NIC — hai loại lỗi đặc trưng của i*→MDD transformation — trước khi transformation xảy ra. VeMI lấp đầy khoảng trống này bằng Integration Metamodel và OCL Verification Measures.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Ý tưởng trực quan của VeMI có thể giải thích như sau: nếu muốn kiểm tra xem một i* model có thể được chuyển đổi sang Integranova thành công, cần phải biết đồng thời (a) i* element nào đang có trong model, (b) transformation rule nào sẽ được áp dụng cho từng element, và (c) Integranova constraint nào mà element được sinh ra phải thỏa mãn. Thay vì để ba luồng thông tin này tồn tại riêng biệt trong ba metamodel khác nhau, VeMI hợp nhất chúng thành một Integration Metamodel duy nhất — rồi dùng OCL để truy vấn các vi phạm trực tiếp trên metamodel hợp nhất đó.

Điểm mấu chốt là **pre-transformation**: VeMI không chờ đến khi transformation xong mới kiểm tra. Nó phân tích i* model ngay từ đầu, dựa trên kiến thức về transformation rules và Integranova constraints, để dự đoán transformation nào sẽ thất bại và tại sao.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

VeMI hoạt động theo 4 bước tuần tự:

**Bước 1 — Transformation Rules Analysis**: Tác giả phân tích tất cả transformation rules từ i* metamodel sang Integranova metamodel. Cho mỗi rule, xác định: preconditions trên i* elements (element nào phải tồn tại, có attribute nào, có connection nào với element khác), và postconditions trên Integranova output (class được sinh ra có constraint nào từ Integranova semantics).

**Bước 2 — Integration Metamodel**: Kết quả của bước 1 được mã hóa thành Integration Metamodel — một metamodel kết hợp i* và Integranova. Không phải merge đơn giản: Integration Metamodel thêm các cross-metamodel links giữa i* elements và Integranova counterparts, và mã hóa transformation rules như conditional constraints trong metamodel. Metamodel này được implement trên Eclipse Modeling Framework (EMF).

**Bước 3 — OCL Verification Measures**: Trên Integration Metamodel, tác giả viết OCL queries để phát hiện vi phạm:
- *NAE queries*: quét các i* elements không có cross-metamodel link hợp lệ đến bất kỳ Integranova element nào dựa trên các transformation rules available.
- *NIC queries*: quét các cấu trúc i* mà khi transformation được áp dụng, tạo ra Integranova class với constraints mâu thuẫn (e.g., class với mandatory attribute nhưng không có cách khởi tạo attribute đó từ context của actor dependency).
- *Warning queries*: quét các cấu trúc rủi ro cao không chắc chắn gây lỗi nhưng cần kỹ sư xem xét.

**Bước 4 — Fixing Guidelines**: Mỗi loại lỗi NAE/NIC/Warning được đi kèm với một hoặc nhiều Fixing Guidelines cụ thể — hướng dẫn sửa đổi i* model để khắc phục lỗi. Sau khi sửa, kỹ sư chạy lại VeMI để xác nhận lỗi đã được giải quyết và không có lỗi mới xuất hiện.

### 3.3 Các Thành Phần Chính

**i* Metamodel**: Định nghĩa formal cú pháp và ngữ nghĩa của i*. VeMI cần i* metamodel formal (không phải chỉ graphical notation) — đây là điều kiện tiên quyết. Metamodel xác định: Actor, Goal, Task, Resource, Dependency (với depender và dependee), Decomposition, Contribution links.

**Integranova Metamodel**: Định nghĩa cấu trúc class model mà Integranova hiểu — Classes, Attributes (với multiplicity và type), Methods (function và event), Associations (với cardinality constraints), Population constraints. Đặc biệt quan trọng là các constraints về instantiation: class nào cần mandatory attribute được set ngay khi tạo, association nào phải được resolve ngay, v.v.

**Integration Metamodel**: Thành phần trung tâm của VeMI. Không chỉ là union của hai metamodel — nó bổ sung thêm: (a) cross-metamodel links thể hiện "i* Actor A có thể tương ứng với Integranova Class C", (b) conditional constraints mã hóa transformation rules như "nếu i* actor A có dependency D đến actor B về resource R, thì Integranova class của A cần association đến class của B với cardinality thỏa mãn constraint X."

**OCL Verification Measures (VMs)**: Tập hợp OCL queries executable trên Integration Metamodel. Mỗi VM có: mô tả lỗi, OCL expression để phát hiện, severity level (Critical/Warning), và pointer đến Fixing Guidelines tương ứng.

**Fixing Guidelines Catalog**: Mỗi entry trong catalog có: loại lỗi (NAE type X / NIC type Y), mô tả ảnh hưởng lên MDD output, và một hoặc nhiều sửa đổi cụ thể trên i* model (thêm element, sửa dependency type, tái cấu trúc actor structure).

**VeMI Tool Prototype**: Java-based prototype trên Eclipse EMF, tích hợp OCL Eclipse plugin. Kỹ sư import i* model, tool tự động chạy OCL VMs và xuất báo cáo lỗi + fixing guidelines.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Xét một i* model cho hệ thống quản lý thư viện với hai actors:

**Actor "Member"** (thành viên thư viện) với goal "Borrow Book", được decompose thành task "Search Catalog" và task "Submit Loan Request". Member có một dependency đến Actor "Librarian" về resource "Catalog Access" — tức là Member phụ thuộc Librarian để truy cập catalog.

**Actor "Librarian"** (thủ thư) với goal "Manage Loans", được decompose thành task "Process Loan Request" và task "Update Records". Librarian có dependency đến Actor "System" về task "Record Update" — phụ thuộc System để thực hiện cập nhật records.

Bây giờ, VeMI chạy trên i* model này:

**Bước 1 — Phân tích transformation rules**: Transformation rule TM-1 quy định: "Mỗi i* Actor được ánh xạ thành một Integranova Class." Rule TM-2: "Mỗi i* Resource dependency từ Actor A sang Actor B được ánh xạ thành một Association từ Class-A đến Class-B với mandatory cardinality [1..1]." Rule TM-3: "Mỗi i* Task của một Actor phải ánh xạ thành một Method trong Class tương ứng — và Method phải có signature rõ ràng."

**Bước 2 — Integration Metamodel**: Member → Class "Member", Librarian → Class "Librarian", System → Class "System". Resource dependency (Member → Librarian về "Catalog Access") → Association (Member → Librarian, cardinality [1..1]). Task dependency (Librarian → System về "Record Update") → Association (Librarian → System, cardinality [1..1]).

**Bước 3 — OCL Verification Measures**:

*NAE check*: OCL query quét xem Task "Search Catalog" của Actor "Member" có tương ứng với Method trong Class "Member" không. Nếu i* model không định nghĩa cụ thể task này cần output/parameter gì, VeMI không thể sinh Method signature — đây là **NAE: Task "Search Catalog" không có đủ thông tin để ánh xạ thành Method**. Severity: Warning (không phải Critical vì một số MDD tools có thể sinh stub method).

*NIC check*: OCL query kiểm tra Association (Librarian → System, [1..1]). Integranova constraint: class có Association mandatory [1..1] phải được instantiate cùng lúc với instance của associated class, hoặc có default value. Nếu i* model không định nghĩa mechanism để "System" được resolve khi tạo Librarian instance → **NIC: Class "Librarian" có mandatory Association đến "System" nhưng không có initialization mechanism → class được sinh ra nhưng không thể có instance**. Severity: Critical.

**Bước 4 — Fixing Guidelines**: VeMI xuất báo cáo:
- Warning: Task "Search Catalog" — Fixing Guideline: "Thêm Resource output cho Task này trong i* model, hoặc thêm Task attribute định nghĩa signature."
- Critical NIC: Librarian → System — Fixing Guideline: "Sửa Task dependency từ mandatory [1..1] thành optional [0..1], hoặc thêm Actor dependency mechanism cho phép System được khởi tạo cùng Librarian."

Kỹ sư sửa i* model theo Fixing Guidelines → chạy lại VeMI → không còn lỗi → thực hiện transformation sang Integranova → class model đầy đủ và instantiable → Integranova sinh code → ứng dụng chạy đúng.

### 3.5 Điểm Mới So Với Trước

Ba điểm khác biệt căn bản:

**Cross-metamodel verification**: Đây là lần đầu tiên một verification tool hiểu đồng thời ngữ nghĩa i* và Integranova. Tất cả công cụ trước đây chỉ hiểu một trong hai phía.

**Pre-transformation**: VeMI phát hiện lỗi trước khi transformation xảy ra. Chi phí sửa lỗi ở giai đoạn i* model thấp hơn nhiều so với sau khi code đã được sinh.

**Actionable fixing on source model**: Thay vì báo lỗi trên output (class model hoặc code), VeMI báo lỗi và hướng dẫn sửa trên input (i* model). Điều này phù hợp với quy trình tự nhiên: kỹ sư yêu cầu làm việc với i* model, không phải với class model.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Goal-oriented Requirements Engineering (GORE) với i* framework cung cấp cách biểu diễn intentions và dependencies của stakeholders từ sớm trong quá trình phát triển phần mềm. Model-Driven Development (MDD) với Integranova platform cho phép sinh code tự động từ class models. Việc kết hợp hai hướng này — chuyển đổi i* model sang Integranova class model để sinh code — tạo ra một pipeline hấp dẫn nhưng tồn tại rủi ro: lỗi trong i* model lan truyền sang code được sinh ra và chỉ được phát hiện muộn khi chi phí fix đã rất cao.

Bài báo đề xuất VeMI (Verification for Model Integration) — phương pháp đầu tiên xác minh i* model trước khi đưa vào MDD pipeline. VeMI xây dựng Integration Metamodel tích hợp i* metamodel và Integranova metamodel, sau đó sử dụng OCL Verification Measures để phát hiện hai loại lỗi critical: Non-Accessible Elements (NAE — i* elements không thể ánh xạ sang bất kỳ Integranova element nào) và Non-Instantiable Classes (NIC — cấu trúc i* tạo ra Integranova class không thể có instance trong runtime). Mỗi lỗi đi kèm với Fixing Guidelines cụ thể hướng dẫn kỹ sư sửa i* model.

Đánh giá qua controlled experiment với 24 người tham gia và industrial case study thực tế (photography agency) cho thấy kỹ sư sử dụng VeMI tạo ra i* models với completeness cao hơn và ít lỗi lan sang MDD class model hơn — đặc biệt lợi ích rõ rệt nhất với kỹ sư mới chưa quen với Integranova constraints. VeMI hoạt động như cơ chế transfer kiến thức cross-metamodel, cho phép kỹ sư yêu cầu phát hiện và sửa lỗi ngay trên i* model mà không cần am hiểu sâu về MDD internals.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset và thiết kế thực nghiệm**:

Tác giả sử dụng case study công nghiệp thực tế — một công ty nhiếp ảnh (Photography Agency) với i* model có quy mô khoảng 20–30 actors và goals, tương ứng với Integranova class model. Đây không phải synthetic benchmark mà là dự án phát triển IS thực tế, tạo độ tin cậy cao cho evaluation.

24 người tham gia được chia theo hai chiều: (a) kinh nghiệm với i* (mới vs. có kinh nghiệm) và (b) điều kiện thực nghiệm (with VeMI vs. without VeMI). Thiết kế controlled experiment với/without cho phép so sánh trực tiếp ảnh hưởng của VeMI.

**Kết quả chính theo nhóm người dùng**:

| Nhóm | Không có VeMI | Có VeMI | Nhận xét |
|---|---|---|---|
| Kỹ sư mới | Completeness thấp, nhiều lỗi lan sang MDD | Phát hiện lỗi sớm, completeness cao hơn | Lợi ích lớn nhất |
| Kỹ sư có kinh nghiệm | Tốt hơn nhưng vẫn có lỗi | Ít lỗi hơn, nhanh hơn | Lợi ích trung bình |
| Domain experts | Không nhận biết MDD constraints | VeMI bridge gap, có thể tham gia validation | Lợi ích về communication |

**Kết quả về loại lỗi**:
- NAE: VeMI phát hiện trước transformation; nhóm không dùng VeMI chỉ phát hiện sau khi class model đã được sinh và thiếu elements.
- NIC: Đặc biệt khó phát hiện thủ công — ngay cả kỹ sư có kinh nghiệm trong nhóm không dùng VeMI thường bỏ qua NIC vì class model nhìn bề ngoài có vẻ đúng.
- Fixing Guidelines được đánh giá là "actionable" và "easy to follow" bởi participants — kỹ sư có thể áp dụng trực tiếp mà không cần giải thích thêm.

**Trường hợp VeMI hiệu quả nhất**: i* models có nhiều actor dependencies phức tạp — đây là nơi NAE và NIC dễ xuất hiện nhất và khó phát hiện thủ công nhất.

**Giới hạn của evaluation**: Không có formal ablation study. Tác giả phân tích theo nhóm kinh nghiệm và loại lỗi nhưng không kiểm tra riêng từng OCL VM để xác định VM nào đóng góp nhiều nhất. Scale của case study (20–30 actors) chưa đại diện cho enterprise-level i* models (>100 actors).

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận**:

**Integranova-specific**: Integration Metamodel và OCL Verification Measures được thiết kế cho Integranova. Để adapt sang MDD tools khác (AndroMDA, Acceleo, Eclipse Papyrus MDD) cần xây dựng lại Integration Metamodel tương ứng — không phải công việc nhỏ vì mỗi MDD tool có metamodel và instantiation semantics khác nhau.

**i* metamodel dependency**: Phương pháp phụ thuộc vào formal i* metamodel definition. Các i* variants như iStar 2.0 (tiêu chuẩn mới được adopting rộng rãi hơn) và GRL (URN component) có metamodel khác với i* gốc — cần integration metamodel riêng cho mỗi variant.

**Structural-only verification**: OCL Verification Measures chỉ cover structural issues — kiểm tra xem elements có tồn tại và có kết nối đúng không. Không phát hiện semantic inconsistencies ở mức business logic: goals có conflicting với nhau không, dependencies có tạo vòng circular không từ góc độ business, actor intentions có coherent không. Những vấn đề này đòi hỏi reasoning ở mức cao hơn.

**Case study scale**: Photography agency là small-to-medium IS. Chưa được kiểm chứng với enterprise-scale i* models (>100 actors) nơi số lượng dependencies và potential NAE/NIC có thể tăng phi tuyến.

**Controlled experiment, không phải field study**: 24 participants là controlled experiment trong academic setting — không theo dõi adoption và impact trong real development projects theo thời gian.

**Hướng nghiên cứu tiếp theo tác giả đề xuất**:
- Mở rộng Integration Metamodel sang các MDD tools khác như AndroMDA, Eclipse Papyrus MDD, Acceleo
- Hỗ trợ iStar 2.0 metamodel trong VeMI (iStar 2.0 đang trở thành tiêu chuẩn de facto)
- Bổ sung semantic-level verification: goal conflict detection, dependency cycle analysis, actor intention coherence — bên cạnh structural checks hiện tại
- Tool integration với jUCMNav hoặc Papyrus để deploy VeMI trong workflow thực tế thay vì standalone prototype
- Longitudinal study: theo dõi i* model quality improvement qua nhiều iteration development trong dự án thực

---

## PHẦN 7 — Kết Luận

Bài báo đề xuất VeMI — phương pháp verification đầu tiên xác minh i* goal models trước khi đưa vào MDD pipeline. Điểm cốt lõi là Integration Metamodel tích hợp i* metamodel và Integranova metamodel thành một metamodel hợp nhất, trên đó OCL Verification Measures có thể phát hiện hai loại lỗi critical (NAE và NIC) trước khi transformation xảy ra. Mỗi lỗi được đi kèm Fixing Guidelines actionable, hướng dẫn kỹ sư sửa đổi ngay trên i* model. Controlled experiment với 24 participants và industrial case study thực tế (photography agency) cung cấp bằng chứng thực nghiệm rằng VeMI cải thiện completeness và correctness của MDD output — đặc biệt với kỹ sư mới.

Hạn chế chính là Integranova-specificity (cần effort để adapt sang MDD tools khác), giới hạn ở structural verification (không phát hiện semantic inconsistencies), và chưa được kiểm chứng với enterprise-scale models. Các hướng mở rộng quan trọng gồm hỗ trợ iStar 2.0 và tích hợp với i* tools thực tế.

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** ý tưởng "pre-transformation verification" — thay vì chờ transformation xong rồi mới phát hiện lỗi trên output, VeMI phát hiện lỗi ngay trên input bằng cách hiểu đồng thời cả hai metamodel và mã hóa transformation rules như cross-metamodel constraints có thể truy vấn bằng OCL; đây là một shift paradigm quan trọng: chuyển từ "fix output" sang "fix input sớm hơn."
