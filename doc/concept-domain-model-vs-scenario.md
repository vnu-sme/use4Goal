# Khái niệm: Domain Model vs Kịch bản (Scenario)

Tài liệu này định nghĩa khái niệm **tổng quát**, không gắn với một ngôn ngữ cụ
thể nào (iStar, BPMN2, DCR, hay ngôn ngữ thứ N sau này).

## 1. Định nghĩa

### 1.1. Domain model — mô tả TỪ VỰNG của một miền

Domain model là **type-level**: nó định nghĩa những *loại* thực thể nào tồn
tại trong một miền, quan hệ cấu trúc giữa các loại đó, và những luật luôn
đúng (invariant). Nó không nhắc tới bất kỳ thực thể cụ thể nào có tên riêng,
và không có khái niệm "trước/sau" hay "trạng thái hiện tại" — nó chỉ là cái
khung mà mọi thực thể cụ thể sau này phải tuân theo.

Ví dụ tổng quát: "một Participant có task Participate", "một process có các
lane, mỗi lane gán cho một role" — đây là phát biểu về *loại*, không phải về
một participant hay process cụ thể nào.

### 1.2. Kịch bản — mô tả MỘT TRẠNG THÁI cụ thể của các instance

Đây là điểm hay bị hiểu nhầm nhất: **kịch bản không phải là một "câu chuyện"
kể lại toàn bộ hành trình từ lúc bắt đầu đến lúc kết thúc.** Kịch bản là một
**phát biểu về trạng thái** (state / configuration) của một tập instance cụ
thể, thuộc các type đã khai báo trong domain model, tại một thời điểm mà ta
quan tâm — có thể là trạng thái cuối cùng, cũng có thể là một trạng thái giữa
chừng, dang dở, chỉ đúng một phần của quá trình.

Nói cách khác: domain model trả lời "cái gì *có thể* xảy ra", còn kịch bản
trả lời "tại cấu hình instance này, trạng thái là gì — và trạng thái đó có
thoả các điều kiện ta kỳ vọng hay không". Nó là một **lát cắt** (snapshot),
không phải nhật ký đầy đủ của cả vòng đời.

### 1.3. Một kịch bản "trông như thế nào"

Bất kể ngôn ngữ nào, một kịch bản luôn gồm 3 phần, và cả 3 đều xoay quanh
việc *mô tả trạng thái*, không phải kể chuyện:

1. **Instance hoá** — khai báo những thực thể cụ thể nào tồn tại, mỗi thực
   thể thuộc đúng một type đã có sẵn trong domain model (ví dụ: "đây là 3
   Participant tên A, B, C", "đây là 1 process instance").

2. **Thiết lập trạng thái cho các instance đó** — đây là phần dễ nhầm là
   "kịch bản = trace từ đầu tới cuối", nhưng thực ra có hai cách diễn đạt
   khác nhau cho cùng một mục đích (đặc tả một trạng thái), không cách nào
   bắt buộc phải chạy lại từ gốc:
   - **Gán trực tiếp** (trạng thái là input): ấn định thẳng giá trị/nhãn cho
     từng instance — "instance này đã hoàn tất bước X", "instance kia đang ở
     nhánh Y", "thuộc tính Z = giá trị nào". Cách này không cần biết trạng
     thái trước đó ra sao, không cần replay gì cả — nó *là* trạng thái, viết
     thẳng ra. Đây là cách để đặc tả một trạng thái **dang dở/một phần**
     (partial state) mà không phải diễn giải làm sao tới được đó.
   - **Gán gián tiếp qua kích hoạt sự kiện** (trạng thái là kết quả suy ra):
     liệt kê những sự kiện/hành động nào đã xảy ra trên instance nào; trạng
     thái cuối được *suy ra* bằng lan truyền/bão hoà (propagation/saturation)
     từ các sự kiện đó, không phải do người viết tự tính tay. Thứ tự liệt kê
     ở đây chỉ để đủ dữ liệu kích hoạt lan truyền đúng — nó không phải là một
     "kịch bản kể chuyện" theo nghĩa văn học, mục đích cuối cùng vẫn chỉ là
     một trạng thái để kiểm tra, không phải bản thân trình tự đó.

3. **Khẳng định về trạng thái** — sau khi trạng thái đã được thiết lập (bằng
   một trong hai cách trên, hoặc kết hợp), kịch bản phát biểu điều gì phải
   đúng tại trạng thái đó: một thuộc tính cụ thể bằng giá trị gì, một điều
   kiện tổng hợp trên toàn bộ instance có thoả không (kiểu "tất cả"/"tồn
   tại"), v.v. Đây là phần duy nhất mà kịch bản thật sự "khẳng định" điều gì
   — hai phần trên chỉ là *thiết lập* để có trạng thái mà khẳng định lên đó.

Vì bản chất là snapshot, một kịch bản **không bắt buộc phải hoàn chỉnh**:
nó có thể chỉ mô tả một phần nhỏ của toàn bộ vòng đời (ví dụ: "tại thời điểm
này, 2 trong 5 việc đã xong, 3 việc còn đang chờ") mà vẫn là một kịch bản hợp
lệ — miễn trạng thái đó nhất quán với domain model.

### 1.4. So sánh

| | Domain model | Kịch bản (Scenario) |
|---|---|---|
| Mức | **Type-level** (lược đồ) | **Instance-level** — một trạng thái/cấu hình cụ thể |
| Trả lời câu hỏi | "Những loại gì tồn tại, quan hệ với nhau ra sao, luật nào luôn đúng?" | "Với tập instance cụ thể này, ở trạng thái này, các điều kiện ta quan tâm có đúng không?" |
| Có nhắc instance cụ thể (tên riêng) không? | Không | Có |
| Có phải là toàn bộ trace từ đầu đến cuối không? | Không áp dụng | **Không bắt buộc** — có thể chỉ là một lát cắt/trạng thái dang dở |
| Đứng độc lập được không? | Có | Không — luôn gắn với đúng một domain model |
| Kết quả | Định nghĩa cấu trúc | Một tập khẳng định (assertion) về một trạng thái |

### 1.5. Loại tương tự ở lĩnh vực khác

- UML: class diagram (model) ↔ **object diagram** (kịch bản) — object diagram
  vốn dĩ cũng chỉ chụp lại một trạng thái tại một thời điểm, không phải toàn
  bộ vòng đời của hệ thống.
- Kiểm thử phần mềm: schema/type ↔ **fixture/state** của một test case cụ
  thể — test không bắt buộc phải dựng lại toàn bộ lịch sử, chỉ cần trạng thái
  đầu vào đúng.
- Ngữ nghĩa hình thức: toàn bộ không gian trạng thái có thể (LTS/automaton)
  ↔ **một trạng thái cụ thể** trong không gian đó, có thể đạt tới bằng nhiều
  cách khác nhau.
