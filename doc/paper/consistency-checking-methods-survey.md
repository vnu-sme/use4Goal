# Khảo sát các phương pháp kiểm tra tương thích giữa hai mô hình + Đề xuất phương pháp mới

Ngày lập: 2026-07-06

Phạm vi: tài liệu này (1) hệ thống hoá các họ phương pháp kiểm tra **conformance/consistency**
giữa hai mô hình ở hai mức trừu tượng khác nhau (không riêng goal↔process, nhưng đối chiếu áp
dụng cụ thể cho cặp **i\* goal model ↔ BPMN process model** của project), và (2) đề xuất **11
phương pháp chưa xuất hiện trong 4 tài liệu đã có trong repo** (`JUCS.md`, `alig.md`, `vaDL.md`,
`conformance-istar-bpmn2.md`) — 10 đề xuất đầu (§4–13) là **thuật toán kiểm tra**, mỗi cái dựa trên
1 nền tảng toán học/lý thuyết khác nhau (xác suất, đại số tuyến tính, lý thuyết trò chơi, tối ưu tổ
hợp, lập luận phi đơn điệu, lý thuyết phạm trù, kiểm chứng thời gian thực, tìm kiếm metaheuristic,
suy luận nhân quả, embedding ngôn ngữ tự nhiên) để tối đa hoá tính bổ sung lẫn nhau; đề xuất thứ 11
(§14) khác **loại** — không phải thuật toán mà là 1 **chiến lược kiến trúc biểu diễn** (hợp nhất 2
metamodel thành 1, do người dùng gợi ý), có thể làm vật chứa rẻ hơn cho một số thuật toán trong 10
đề xuất kia.

> **Lưu ý về tính mới**: mục 4–13 là các *đề xuất tổng hợp* (synthesis) từ kỹ thuật đã biết ở các
> lĩnh vực khác, ghép lại theo cách cụ thể mà tôi (trợ lý) không tìm thấy tiền lệ khi đối chiếu với
> 4 bài đã đọc trong repo. Tôi **không có khả năng chạy tìm kiếm học thuật đầy đủ**
> (Scopus/DBLP/Google Scholar) trong phiên làm việc này, nên đây là "chưa thấy ai làm đúng như vậy
> trong phạm vi đã đọc", không phải "đã xác nhận chưa từng công bố". Trước khi đầu tư viết
> bài/code cho bất kỳ đề xuất nào, nên tự kiểm tra bằng từ khoá gợi ý ở cuối mỗi mục.

---

## 1. Bảng phân loại tổng quan

| # | Họ phương pháp | Cơ chế cốt lõi | Đầu ra | Đã dùng trong repo? |
|---|---|---|---|---|
| A | Biến đổi mô hình (ATL/QVT) | Sinh mô hình đích từ mô hình nguồn qua transformation rule, so khớp kết quả sinh ra với mô hình đích thật | Mô hình đích (hoặc lệch = danh sách khác biệt) | Không — project không dùng ATL, dùng ANTLR compiler tay (`step-5-compiler.md`) |
| B | Hợp nhất ngữ nghĩa hình thức chung + chứng minh | Dịch cả 2 ngôn ngữ sang 1 formalism chung (Z, Alloy, CSP...) rồi chứng minh tương đương/refinement bằng thuật toán | Bằng chứng hình thức (chứng minh hoặc phản ví dụ) | Không |
| C | Đồng bộ kịch bản qua Triple Graph Grammar + OCL (**JUCS**) | Snapshot pattern → action → scenario; triple rule đồng bộ 2 kịch bản; thuật toán build cặp kịch bản hợp lệ | Cặp kịch bản hợp lệ, hoặc báo bước không tiếp tục được | Có — backbone khái niệm của `conformance-istar-bpmn2.md` |
| D | Ngữ nghĩa vận hành + LTS + reachability BFS (**Caballero-Villalobos**, `alig.md`) | Goal marking (AND/OR/Make/Break) + LTS của process (WF-net/DCR) → product LTS → weak/strong/monotonic compliance bằng BFS thuận-nghịch | 3 mức verdict + counterexample trace | Có — §3.3–3.8, §4.3 của `conformance-istar-bpmn2.md` |
| E | Suy luận Description Logic tĩnh (**Gröner**, `vaDL.md`) | Ánh xạ actor/task ↔ pool/activity, subsumption reasoning phát hiện *strong/potential inconsistency* giữa cấu trúc loại-trừ (XOR) và AND-refine | Danh sách inconsistency + phân loại | Có — §3.7, §4.2 (Bước A pre-check) |
| F | Phân tích cặp tới hạn trên luật graph transformation (critical pair analysis) | Xem use case/activity diagram như luật đồ thị, phân tích xung đột/phụ thuộc giữa các chuỗi luật (Jurack et al. 2008, Hausmann et al. 2002 — nhắc ở JUCS §5) | Danh sách cặp luật xung đột | Không — mới nhắc trong related work của JUCS, chưa hiện thực |
| G | Kiểm tra refinement/bisimulation hành vi thuần tuý | Không cần metamodel chung; chuyển mỗi model thành automaton, chứng minh quan hệ mô phỏng/bisimulation hoặc failure-refinement (kiểu CSP) | Đúng/sai quan hệ refine | Không |
| H | Hợp đồng tĩnh / nghĩa vụ chứng minh (Design-by-Contract + SMT) | Phát biểu `post(design) ⟹ post(usecase)` làm proof obligation, verify tĩnh bằng SMT/theorem prover, không cần build kịch bản | Chứng minh hoặc phản ví dụ cho từng cặp action | Không |
| I | Kiểm thử tuân thủ dựa trên sinh test (test-based / ioco) | Sinh test case từ 1 model, "chạy" test đó lên model kia (black-box), đo pass/fail | Bộ test + tỉ lệ pass | Không |
| J | So khớp tĩnh qua weaving model + OCL invariant (không chạy động) | Định nghĩa correspondence model bằng constraint khai báo, validate trực tiếp trên instance có sẵn, không cần graph transformation engine | Danh sách vi phạm invariant | Một phần — `ConformanceMapping.validate()` ở §4.1 làm việc này ở mức nhẹ (chỉ check leaf-mapping), chưa phải weaving model đầy đủ |
| K | Process mining conformance checking cổ điển (fitness/precision) | So khớp **event log thật** với 1 process model (token-based replay, alignment-based) — vốn không liên quan goal model | Fitness/precision score | Không, và về bản chất không so 2 *model* mà so log với model |
| L | Phân tích dựa trên embedding/học máy (semantic drift detection) | Nhúng 2 đồ thị vào cùng 1 không gian vector (GNN/contrastive), đo khoảng cách embedding giữa các node tương ứng, cảnh báo khi khoảng cách vượt ngưỡng | Điểm "trôi" (drift score) liên tục, không nhị phân | Không — nhưng hạ tầng graph/embedding đã có sẵn qua MCP `code-review-graph` (`embed_graph_tool`, `semantic_search_nodes_tool`) dùng cho *code*, chưa áp cho goal/process model |

Các dòng C, D, E là 3 phương pháp đã chọn và thiết kế chi tiết trong `conformance-istar-bpmn2.md`
(P1 = E, P2 = D, khung tổng thể = C). Dòng F–L là các hướng **chưa** hiện thực trong repo. Mục 4
đề xuất một phương pháp **không trùng với bất kỳ dòng nào ở trên**, khai thác đúng khoảng trống mà
D và E để lại (xem §3 để hiểu rõ khoảng trống đó là gì).

---

## 2. Diễn giải ngắn từng phương pháp

### A. Model transformation (ATL/QVT)
So khớp bằng cách sinh model đích từ model nguồn qua transformation rule rồi diff kết quả với model
đích thật. Phù hợp khi có quan hệ *derivation* một chiều rõ ràng (nguồn → đích), yếu khi hai model
được phát triển độc lập rồi mới cần đối chiếu (trường hợp phổ biến của goal↔process: BPMN thường do
business analyst vẽ tay, không phải sinh tự động từ i*).

### B. Hợp nhất ngữ nghĩa hình thức chung
Dịch cả use case model và design model (hoặc goal model và process model) sang cùng 1 formalism
(Z, Alloy, CSP, Petri net...) rồi áp thuật toán chứng minh tương đương có sẵn của formalism đó. Ưu
điểm: tận dụng công cụ chứng minh trưởng thành. Nhược điểm: mất thông tin ngữ nghĩa đặc thù của
từng ngôn ngữ khi ép về 1 formalism trung gian (ví dụ contribution Help/Hurt không có tương đương
tự nhiên trong CSP thuần).

### C–E. Đã thiết kế trong repo
Xem `doc/paper/conformance-istar-bpmn2.md` — không nhắc lại ở đây.

### F. Critical pair analysis
Thay vì đồng bộ **kịch bản cụ thể** (JUCS) hay build **toàn bộ reachable state space** (Caballero),
critical pair analysis coi mỗi use case/activity như 1 tập luật graph transformation và phân tích
tĩnh xem 2 luật có *xung đột* (không thể áp cùng lúc) hay *phụ thuộc* (luật này tạo điều kiện cho
luật kia) hay không — không cần thực thi bất kỳ kịch bản nào. Đây là kỹ thuật gốc của công cụ AGG
(critical pair analysis tool), mạnh khi cần phát hiện xung đột giữa các *biến thể* của cùng 1 use
case (ví dụ 2 alternate flow có tranh chấp tài nguyên) — khác mục tiêu của conformance goal↔process
hiện tại (kiểm tra 1 process có thực hiện đúng goal, không phải kiểm tra các biến thể use case có
xung đột nội tại).

### G. Behavioral refinement/bisimulation
Không dùng correspondence model tường minh; thay vào đó chứng minh 1 model là *refinement* của model
kia trong 1 quan hệ thứ tự chuẩn (ví dụ failure-refinement của CSP: mọi trace + failure của process
model đều nằm trong tập được goal model "cho phép"). Về bản chất **rất gần** với LTS+reachability
của Caballero-Villalobos (dòng D) — điểm khác là bisimulation/refinement không cần khái niệm
"quality/marking" của i*, chỉ cần 2 LTS thuần và 1 quan hệ mô phỏng — tổng quát hơn nhưng mất đi
ngữ nghĩa contribution Make/Break đặc thù của i* (phải mã hoá lại thành nhãn LTS, mất tính đọc
được). Vì Caballero-Villalobos đã làm phần này tốt hơn cho đúng cặp i*/BPMN, không cần làm lại bằng
G thuần tuý.

### H. Hợp đồng tĩnh (Design-by-Contract + SMT)
Thay vì build LTS và duyệt reachability, phát biểu trực tiếp: "nếu design-level operation X kết
thúc đúng postcondition, thì postcondition đó phải kéo theo (⟹) postcondition của use-case action
tương ứng". Verify bằng SMT solver (Z3...) từng cặp action một, không cần dựng toàn bộ state space.
Nhanh hơn D khi số action lớn (không bùng nổ trạng thái) nhưng **không phát hiện được lỗi về thứ tự
thực thi** (chỉ kiểm tra local implication, không kiểm tra global reachability) — bù đắp bằng D nếu
cần.

### I. Test-based conformance (ioco)
Sinh test case từ 1 model trừu tượng hơn (input-output conformance theo lý thuyết `ioco` của
Tretmans), chạy test đó lên implementation/model kia, đo tỉ lệ pass. Phù hợp khi model kia là hệ
thống đang chạy thật (không phải model tĩnh) — ít phù hợp với bài toán goal↔process vì cả 2 phía ở
đây đều là model tĩnh, chưa có hệ thống thực thi.

### J. Weaving model tĩnh
Định nghĩa mapping bằng constraint khai báo (giống hướng Gröner nhưng không cần suy luận DL, chỉ
check trực tiếp OCL invariant trên instance có sẵn). `ConformanceMapping.validate()` trong thiết kế
hiện tại đã làm 1 phần việc này (cảnh báo map non-leaf) — có thể mở rộng thêm nhiều invariant hơn
(ví dụ: mọi Dependency phải map sang 1 MessageFlow hoặc phải nằm trong cùng Pool) mà không cần thêm
engine mới.

### K. Process mining conformance (fitness/precision)
Đối tượng so sánh ở đây là **log thực thi thật** với **1 model** (không phải 2 model đối chiếu
nhau) — nên nghiêm ngặt mà nói không cùng loại bài toán. Tuy nhiên đây là nguồn cảm hứng chính cho
đề xuất ở §4: nếu coi goal model như "model tham chiếu" và log thực thi BPMN như "hành vi thực", ta
có thể mượn ý tưởng fitness/precision (đo continuous, không nhị phân) áp dụng cho goal↔process.

### L. Embedding/ML-based drift detection
Học 1 không gian vector chung cho 2 đồ thị (goal model + process model) sao cho các cặp node đã
biết là tương ứng (từ mapping ban đầu) gần nhau trong không gian đó; sau đó theo dõi khoảng cách này
liên tục khi 1 trong 2 model bị sửa — nếu khoảng cách vượt ngưỡng, cảnh báo "có thể đã trôi khỏi
nhất quán" (không khẳng định vi phạm cụ thể như D/E, chỉ là tín hiệu sớm rẻ tiền để chạy D/E đầy đủ
khi cần). Hạ tầng graph-embedding cho *code* đã có sẵn trong project qua MCP `code-review-graph`
nhưng graph đó là graph mã nguồn Java, không phải graph goal/process — muốn dùng lại phải build 1
graph riêng cho `.istar`/`.bpmn2` (dùng `query_graph`/`embed_graph_tool` style trên metamodel mới).

---

## 3. Khoảng trống mà D (Caballero-Villalobos) và E (Gröner) để lại

Cả D và E đều có 2 đặc điểm chung:

1. **Nhị phân/rời rạc**: verdict luôn là 1 trong hữu hạn nhãn (`NON_COMPLIANT` /
   `WEAK_COMPLIANT` / `STRONG_COMPLIANT`, hoặc `STRONG_INCONSISTENCY` / `POTENTIAL_INCONSISTENCY`
   / không có gì). Không có khái niệm "gần đạt được" hay "đạt được bao nhiêu phần trăm".
2. **Chỉ xét model khai báo (declarative), không xét dữ liệu vận hành thật**: cả marking-propagation
   của D lẫn subsumption-checking của E đều suy luận **thuần trên cấu trúc của model** (Refinement,
   Contribution, Gateway...), không bao giờ chạm vào log thực thi thật của quy trình (BPMN engine
   thật, nếu có) hay tần suất rẽ nhánh thực tế.
3. **Một lần, tĩnh (one-shot)**: cả D và E giả định 2 model đã hoàn chỉnh, chạy 1 lần, ra 1 kết quả.
   Không có khái niệm theo dõi liên tục qua thời gian khi process được vận hành thật và có thể trôi
   dần khỏi ý định ban đầu của goal model.
4. **Không đưa ra gợi ý sửa gì để cải thiện** ngoài counterexample trace — biết *ở đâu* sai nhưng
   không biết *sửa nhánh nào của gateway* sẽ cải thiện alignment nhiều nhất.

Đề xuất ở §4 nhắm chính xác vào 4 khoảng trống này: biến verdict rời rạc thành **điểm số liên tục**,
đưa **dữ liệu vận hành thực tế** (hoặc mô phỏng có xác suất) vào, hỗ trợ **giám sát liên tục theo
thời gian**, và cho **gợi ý định lượng nên sửa gì**.

---

## 4. Đề xuất #1: Giám sát Tương thích Goal–Process Định lượng qua Mạng Đóng góp có Trọng số, hiệu chỉnh bằng Log thực thi

**Tên tắt đề xuất: PGA — Probabilistic Goal-process Alignment**

### 4.1. Ý tưởng cốt lõi

Thay vì hỏi *"process này có tuân thủ goal model hay không?"* (câu hỏi nhị phân của D/E), PGA hỏi:

> *"Ở mức xác suất, process này (hoặc: process này đang thực sự chạy trong log) hỗ trợ các quality
> gốc của goal model đến mức nào, xu hướng đó đang tăng hay giảm theo thời gian, và nếu muốn cải
> thiện thì nên tác động vào nhánh gateway nào?"*

Ba câu hỏi phụ này — **định lượng**, **giám sát theo thời gian (drift)**, **gợi ý sửa (sensitivity)**
— không câu nào được D hay E trả lời, vì cả hai đều dừng ở verdict rời rạc một lần.

### 4.2. Hình thức hoá sơ bộ

Mượn đúng cấu trúc `Refinement`/`Contribution` đã có của `IStarModel`, nhưng thay `GoalTaskStatus`
3 giá trị `{Unknown, Fulfilled, Pending}` bằng biến ngẫu nhiên liên tục:

```
Với mỗi Goal/Task leaf e:  X_e ∈ [0,1]   (xác suất "được thực hiện", không còn nhị phân)
Với mỗi Quality q:         S_q ∈ [0,1]   (mức độ được thoả mãn kỳ vọng)
```

**Nguồn của X_e** — 2 chế độ:

```
Chế độ thiết kế (design-time, chưa có log):
  Mỗi nhánh XOR/OR-split trong BPMN gán 1 xác suất ước lượng p (mặc định uniform theo số nhánh,
  hoặc do người dùng khai báo — giống "simulation probability" chuẩn trong BPMN Simulation).
  X_e = xác suất tích luỹ đi từ start event tới node map⁻¹(e), tính theo p của các gateway trên
  đường đi (giống thuật toán tính xác suất tới 1 trạng thái trong Markov chain rời rạc suy ra
  trực tiếp từ cấu trúc BPMN — không cần chạy engine ngoài).

Chế độ vận hành (run-time, có log thật):
  X_e = f(e) = (số instance mà node map⁻¹(e) đã fire) / (tổng số process instance trong log),
  đọc từ file log chuẩn XES (định dạng process-mining phổ biến) hoặc CSV event log tối giản.
```

**Lan truyền lên Refinement** (thay AND/OR nhị phân bằng noisy-AND / noisy-OR xác suất, tương thích
ngược: khi mọi input ∈ {0,1} thì công thức dưới đây suy biến đúng về luật `P_AND`/`P_OR` của D):

```
Refinement.And(parent, children):   X_parent = min_i X_child_i        (cận trên bảo thủ)
                                     — hoặc  Π_i X_child_i  nếu coi các child độc lập
Refinement.Or(parent, children):    X_parent = 1 − Π_i (1 − X_child_i)   (noisy-OR chuẩn)
```

**Lan truyền lên Quality** qua contribution có trọng số (chuẩn hoá `ContribType` → trọng số thay vì
2 lớp Make⁺/Break⁻ nhị phân như D):

```
w(MAKE)=+1.0  w(HELP)=+0.6  w(SOME_PLUS)=+0.3
w(HURT)=−0.6  w(BREAK)=−1.0 w(SOME_MINUS)=−0.3   w(UNKNOWN)=0 (loại khỏi tính, như D)

S_q = σ( Σ_e  w(e,q) · X_e )      // σ = sigmoid, ép về [0,1], giống 1 influence diagram tối giản
```

**Điểm số Alignment** cho 1 quality gốc (hoặc trung bình có trọng số nhiều quality):

```
AS(GM,PM) = S_{q_root}          (hoặc weighted average nếu nhiều quality gốc)
verdict:  AS ≥ τ_high  → ALIGNED
          τ_low ≤ AS < τ_high → AT_RISK
          AS < τ_low  → MISALIGNED
```

**Drift theo thời gian** (khi có ≥2 lần đọc log, ví dụ theo tuần):

```
Δ(t1,t2) = AS(GM,PM,log_t2) − AS(GM,PM,log_t1)
Cảnh báo nếu |Δ| vượt ngưỡng kiểm soát thống kê (kiểu control chart / SPC: ví dụ 2σ so với biến
động lịch sử của AS) — instrument liên tục, không phải chạy 1 lần rồi thôi như D/E.
```

**Sensitivity** (gợi ý sửa gateway nào để cải thiện AS nhiều nhất) — vì toàn bộ công thức trên là
đại số đóng (min/product/noisy-OR/sigmoid, đều khả vi từng khúc), có thể tính đạo hàm riêng phần
`∂AS / ∂p_branch` cho từng nhánh gateway bằng vi phân tự động (automatic differentiation) hoặc sai
phân hữu hạn đơn giản (finite-difference, đủ dùng vì số gateway thường nhỏ):

```
rank các gateway branch theo |∂AS/∂p_branch| giảm dần
→ "nếu tăng xác suất đi nhánh Approve thêm 10%, AS dự kiến tăng 0.14"
```

Không phương pháp nào trong D/E/F–L ở mục 2 cho ra được loại gợi ý định lượng "sửa gì thì lợi bao
nhiêu" này — D/E chỉ cho biết đúng/sai + 1 counterexample trace.

### 4.3. Vì sao đây là tổ hợp mới, không phải chỉ là D + K dán lại

- Quantitative/probabilistic reasoning trên chính *i\* goal model* đã tồn tại độc lập trong literature
  (ví dụ Giorgini & Mylopoulos "Reasoning with Goal Models" 2002, và GRL quantitative contribution
  trong URN/jUCMNav) — nhưng các công trình đó dừng ở goal model, **không đồng bộ với 1 process
  model cụ thể**.
- Process mining conformance (fitness/precision, dòng K) đã tồn tại độc lập — nhưng so log với
  *1 process model*, **không có khái niệm goal/quality ở phía bên kia**.
- Điểm mới ở đây là **vòng lặp khép kín cụ thể**: log thực thi BPMN → xác suất kích hoạt từng
  leaf task → lan truyền có trọng số qua đúng cấu trúc `Refinement`/`Contribution` của i* đã có sẵn
  trong project → điểm alignment liên tục → giám sát trôi theo thời gian → sensitivity ngược lại chỉ
  đúng gateway cần sửa trong BPMN. Đây là closed loop goal↔process↔log↔goal mà D (không dùng log
  thật, chỉ LTS suy diễn từ model) và K (không có phía goal) đều không có.

### 4.4. Khác biệt cụ thể so với D và E khi áp lên case study `construction_permit`

Nhắc lại §5.2 của `conformance-istar-bpmn2.md`: D cho verdict `NON_COMPLIANT` vì nhánh `Deny` dẫn
tới terminal state mà `CityBusinessGrowthSupported = ⊥`. Đây là *đúng về mặt hình thức* nhưng — như
chính tài liệu đó đã tự phê phán ở §5.3 — "vô lý về nghiệp vụ" vì thực tế một số đơn *nên* bị từ
chối.

Áp PGA lên đúng case study này: giả sử log thực tế cho thấy 80% đơn được Approve, 20% bị Deny (hợp
lý về nghiệp vụ — hầu hết đơn hợp lệ). Khi đó:

```
X_Approve ≈ 0.8,  X_Deny ≈ 0.2
S_CityBusinessGrowthSupported = σ(1.0×0.8 + (−1.0)×0.2) = σ(0.6) ≈ 0.65
```

→ `AS ≈ 0.65`, có thể đọc là **AT_RISK** hoặc **ALIGNED** tuỳ ngưỡng τ do stakeholder chọn — một
kết luận **có sắc thái** ("phần lớn được hỗ trợ, nhưng chưa hoàn toàn"), thay vì nhị phân
`NON_COMPLIANT` cứng nhắc của D. Đây chính xác là khoảng trống mà §5.3 của tài liệu D đã tự nhận là
"giới hạn đã biết của framework nguồn": PGA giải quyết đúng giới hạn đó bằng cách đổi câu hỏi từ "có
tồn tại đường nào không đạt hay không" (D) sang "về tổng thể/kỳ vọng, mức độ đạt là bao nhiêu" (PGA)
— hai câu hỏi bổ sung cho nhau, không thay thế: D vẫn cần để tìm counterexample cụ thể, PGA cần để
đánh giá mức độ nghiêm trọng và theo dõi xu hướng.

### 4.5. Vị trí trong kiến trúc hiện có

Không cần đổi package đã thiết kế ở `conformance-istar-bpmn2.md` §4 — thêm 1 package song song:

```
org.vnu.sme.goal.conformance.probabilistic/
├── ContributionWeight.java        (map ContribType → double, thay ContributionPolarity nhị phân)
├── BranchProbability.java         (record: gatewayId, flowId, double p — nguồn: khai báo tay hoặc log)
├── EventLog.java / XesLogReader.java   (đọc log XES/CSV → tần suất fire mỗi FlowNode)
├── ProbabilisticMarking.java      (X_e, S_q dạng double thay vì GoalTaskStatus/QualityStatus)
├── AlignmentPropagation.java      (noisy-AND/OR + sigmoid, thuần hàm — dễ unit test bằng case study)
├── AlignmentScore.java            (record: score, verdict ALIGNED/AT_RISK/MISALIGNED)
├── DriftMonitor.java              (so 2 AlignmentScore theo thời gian, cảnh báo SPC-style)
└── SensitivityAnalyzer.java       (finite-difference trên BranchProbability, xếp hạng theo |∂AS|)
```

`ProbabilisticMarking`/`AlignmentPropagation` đọc thẳng `IStarModel.goalsAndTasks()` (hàm tiện ích
đã được lên kế hoạch thêm ở §6.3 mục 1 của `conformance-istar-bpmn2.md`) và `Refinement`/
`Contribution` hiện có — **không cần sửa metamodel `.istar`/`.bpmn2` nào**, chỉ thêm 1 lớp diễn giải
số học phía trên. `BranchProbability` là input ngoài (file cấu hình riêng hoặc suy từ `EventLog`),
tương tự cách `ConformanceMapping` là input ngoài của D/E.

### 4.6. Hạn chế cần nêu rõ (không giấu)

1. **Cần dữ liệu** — chế độ run-time cần log thật (XES/CSV), project hiện là công cụ modeling, chưa
   có execution engine sinh log → giai đoạn đầu chỉ chạy được chế độ design-time (xác suất khai báo
   tay/uniform), giá trị thực tiễn thấp hơn cho tới khi có nguồn log thật hoặc mô phỏng Monte Carlo
   thay thế.
2. **Giả định độc lập** khi dùng product thay vì min cho AND — cần nêu rõ giả định này với người
   dùng cuối (giống cách D phải công khai việc gộp 7 `ContribType` về 2 lớp).
3. **Không thay thế D/E** — PGA không tìm counterexample cụ thể (D làm tốt việc này), không phát
   hiện xung đột cấu trúc XOR/AND (E làm tốt việc này) — PGA nên chạy **sau** D/E như lớp bổ sung
   đo mức độ nghiêm trọng, không phải lớp thay thế.
4. **Ngưỡng τ_high/τ_low mang tính chủ quan** — giống hệt vấn đề mà `alig.md` gặp với "quality đại
   diện cho nghĩa vụ vs. kết quả mong muốn" (§5.3 của D) — PGA không giải quyết vấn đề triết học đó,
   chỉ làm cho nó *đo được bằng số* thay vì nhị phân.

### 4.7. Việc cần làm trước khi công bố là "phương pháp mới"

Tra cứu các cụm từ khoá sau để xác nhận không trùng công trình đã có, trước khi viết thành bài báo:
- "quantitative goal model" + "business process" + "conformance/compliance"
- "probabilistic i* / GRL" + "process mining"
- "Bayesian network" + "NFR framework" + "process alignment"
- "goal satisfaction" + "event log" + "sensitivity analysis"
- tác giả liên quan trực tiếp nhất cần rà trước: Giorgini, Horkoff, Amyot (quantitative GRL/i*),
  và nhóm process-mining compliance (van der Aalst, Governatori — đã được `alig.md` trích dẫn ở
  phần đầu, nên rà chính references của `alig.md` trước tiên vì cùng chủ đề gần nhất).

---

## 5. Đề xuất #2: Chứng minh bảo toàn goal bằng bất biến tuyến tính kiểu Petri net (Invariant-Based Structural Proof)

**Nền tảng**: đại số tuyến tính trên ma trận incidence của Petri net (P-invariant), khác hẳn cơ chế
tìm kiếm đồ thị (BFS) của D.

### 5.1. Ý tưởng cốt lõi

D chứng minh compliance bằng cách **duyệt** toàn bộ không gian trạng thái (forward/backward BFS) —
tốn `Θ(|S_C|+m)`, và với process có vòng lặp (loop-back sau Deny, như chính `alig.md` §5.3 nhắc tới
DCR graph "cho phép lặp lại vô hạn") không gian trạng thái có thể vô hạn hoặc rất lớn. Petri net lý
thuyết cổ điển có 1 công cụ mạnh hơn BFS cho đúng loại câu hỏi "có luôn giữ được 1 tính chất bảo
toàn hay không": **P-invariant** — một tổ hợp tuyến tính của các place mà tổng luôn không đổi qua
mọi lần bắn transition, chứng minh được **mà không cần liệt kê bất kỳ trạng thái nào**, chỉ cần giải
hệ phương trình tuyến tính `x·C = 0` trên ma trận incidence `C`.

### 5.2. Hình thức hoá sơ bộ

Coi BPMN đã biên dịch thành WF-net (đã có trong D, `Bpmn2LtsBuilder`). Gán cho mỗi place một trọng
số `λ(p) ∈ ℝ` sao cho:

```
Σ_p λ(p)·m(p)  = hằng số C₀   với mọi marking m đạt được từ marking khởi tạo qua mọi dãy bắn hợp lệ
                                     (định nghĩa P-invariant chuẩn: x là nghiệm trái của x·A = 0,
                                      A = ma trận incidence N×N của WF-net)
```

Gán `λ` theo đúng trọng số contribution đã có (`Make⁺` → `+1`, `Break⁻` → `−1`, giống PGA nhưng
**không cần xác suất/log**, chỉ cần cấu trúc): nếu tìm được 1 P-invariant sao cho vế trái đúng bằng
tổng "điểm goal" đã lan truyền tới quality gốc, thì **quality đó được bảo toàn ở MỌI marking đạt
được**, không chỉ ở các marking đã duyệt trong BFS. Nếu không tồn tại invariant nào phù hợp (hệ
phương trình vô nghiệm), đó là tín hiệu: consistency (nếu có) chỉ mang tính "cục bộ theo từng
kịch bản" chứ không phải **bất biến cấu trúc** — bản thân việc "không tìm được invariant" đã là
1 thông tin chẩn đoán hữu ích mà D không có (D chỉ nói "compliant/non-compliant" cho model cụ thể
đã cho, không nói được compliance đó có *ổn định về cấu trúc* hay chỉ đúng do may mắn không có
loop).

### 5.3. Khác gì D và PGA

- D chứng minh bằng cách duyệt (tốn kém khi state space lớn/vô hạn do loop); phương pháp này chứng
  minh bằng **giải hệ phương trình tuyến tính 1 lần** — độ phức tạp là giải ma trận (đa thức theo số
  place/transition), không phụ thuộc kích thước không gian trạng thái, nên **áp dụng được cho
  process có vòng lặp** mà D phải giới hạn hoặc không đảm bảo dừng.
- PGA cho điểm số kỳ vọng dựa trên xác suất (có thể sai nếu ước lượng xác suất sai); phương pháp này
  cho **chứng minh chắc chắn (proof), không phụ thuộc phân phối xác suất nào** — bù đắp đúng điểm
  yếu "giả định phân phối" của PGA.

### 5.4. Hạn chế

Chỉ áp dụng tốt cho contribution "cộng tuyến tính" (Make/Break dạng +1/−1); các luật phi tuyến như
`P_OR` (không phải phép cộng) khó biểu diễn thành 1 P-invariant tuyến tính thuần — cần tuyến tính
hoá gần đúng (over-approximation) cho phần OR-refine, nghĩa là kết quả "tìm được invariant" là điều
kiện đủ chứ không phải cần và đủ cho toàn bộ ngữ nghĩa AND/OR/Make/Break đầy đủ.

**Từ khoá tra cứu**: "place invariant" + "goal preservation", "Petri net" + "requirements
compliance", "S-invariant business process compliance".

---

## 6. Đề xuất #3: Kiểm tra tương thích qua trò chơi đối kháng (Game-Theoretic Obstacle Consistency)

**Nền tảng**: lý thuyết trò chơi trên đồ thị (game on graphs), khác hẳn propagation (D/PGA) và
đại số tuyến tính (#2).

### 6.1. Ý tưởng cốt lõi

Thay vì hỏi "process này *có xu hướng* đạt goal hay không" (PGA) hay "*luôn* đạt goal ở mọi
marking" (#2), phương pháp này hỏi theo tinh thần **obstacle analysis** của KAOS (Van Lamsweerde):
*"Có tồn tại một cách môi trường/actor bên ngoài chọn nhánh XOR/OR sao cho, dù process luôn thực
thi đúng cấu trúc của nó, quality gốc vẫn bị chặn không đạt?"* — mô hình hoá thành **trò chơi 2
người tổng-không (zero-sum) trên đồ thị**: người chơi **Environment** (đại diện actor bên ngoài,
khách hàng, hệ thống lỗi...) chọn nhánh tại các gateway XOR/OR/event-based mà use case coi là ngoài
tầm kiểm soát của hệ thống; người chơi **System** chọn nhánh tại các gateway do hệ thống tự quyết
định (business rule nội bộ). Consistency = **System có chiến lược thắng** (winning strategy) đảm
bảo luôn đạt quality gốc bất kể Environment chơi thế nào.

### 6.2. Hình thức hoá sơ bộ

```
Game G = (V, V_sys ⊎ V_env, E, Win)
V        = ProductState (giống D, ProductState(IStarMarking, BpmnMarking))
V_sys    = các ProductState mà FlowNode enabled tiếp theo là gateway "nội bộ" (do BPMN Pool nội bộ
           kiểm soát — annotate thủ công, hoặc mặc định: mọi gateway trong lane không phải actor
           bên ngoài)
V_env    = các ProductState mà gateway enabled là do actor/dependee bên ngoài chọn (annotate qua
           ActorMapping đã có sẵn — Dependency trỏ tới actor ngoài)
Win      = { s ∈ V | s.istar().isSuccess(gm) }     // giống F_C của D

Verdict:  ALIGNED nếu System thắng trò chơi đạt-tới-Win (reachability game, giải bằng backward
          fixpoint chuẩn của lý thuyết trò chơi vô hạn/hữu hạn trên đồ thị — thuật toán zielonka
          hoặc attractor computation, đã có sẵn nhiều cài đặt tham khảo)
          MISALIGNED nếu Environment có chiến lược ngăn cản vĩnh viễn (Environment thắng trò chơi
          "tránh Win" — safety game đối ngẫu)
```

### 6.3. Khác gì D, E, và các đề xuất trước

- D coi mọi lựa chọn tại gateway là **như nhau** (duyệt hết mọi nhánh, không phân biệt "ai chọn");
  phương pháp này là phương pháp **đầu tiên phân biệt rạch ròi giữa rẽ nhánh do hệ thống tự quyết và
  rẽ nhánh do actor ngoài quyết định** — đúng tinh thần obstacle analysis/anti-goal của goal-oriented
  RE (KAOS, i* Secure Tropos) nhưng **chưa từng được áp dụng ở mức đồng bộ với BPMN process thật**
  trong 3 bài đã đọc (D/E chỉ có 1 loại actor trong mapping, không phân vai trò "đối thủ" khi
  reasoning).
- Khác PGA/#2 ở chỗ đây là **worst-case đối kháng có chủ đích** (Environment chủ động chọn nhánh
  xấu nhất), không phải trung bình có trọng số (PGA) hay bảo toàn ở mọi nhánh như nhau (#2, D).

### 6.4. Hạn chế

Cần phân loại thủ công gateway nào do "hệ thống" và gateway nào do "actor ngoài" kiểm soát — nếu
phân loại sai, kết quả trò chơi vô nghĩa; đây là input ngoài tương tự `ConformanceMapping`, không
phải hạn chế thuật toán.

**Từ khoá tra cứu**: "obstacle analysis" + "reachability game", "anti-goal" + "business process",
"KAOS obstacle" + "BPMN verification".

---

## 7. Đề xuất #4: Phát hiện tương thích không cần mapping thủ công, qua chi phí so khớp đồ thị tối ưu (Alignment-Free Consistency via Graph-Matching Cost)

**Nền tảng**: bài toán gán tối ưu (assignment problem) / optimal transport trên đồ thị, khác hẳn
mọi phương pháp trên ở chỗ **không đòi hỏi file `.map` do người dùng viết tay**.

### 7.1. Ý tưởng cốt lõi

Cả D, E, PGA, #2, #3 đều giả định đã có sẵn `ConformanceMapping` (file `.map`) do con người viết —
bản thân việc viết mapping đúng là 1 nguồn lỗi (chính `conformance-istar-bpmn2.md` §4.1 đã phải
thêm `validate()` để bắt lỗi map nhầm non-leaf). Phương pháp này **không giả định có mapping**, mà
coi việc *"có tồn tại 1 phép gán chi phí thấp giữa 2 đồ thị hay không"* chính là phép đo consistency:
nếu 2 model thực sự nhất quán về ý định, tồn tại 1 phép gán node-to-node chi phí thấp (dựa trên cấu
trúc lân cận + nhãn); nếu chi phí tối ưu cao bất thường, đó là dấu hiệu 2 model đã "trôi" khỏi nhau
đến mức không còn tương ứng hợp lý nữa — kể cả khi mapping thủ công vẫn còn tồn tại trên giấy tờ.

### 7.2. Hình thức hoá sơ bộ

```
Xây 2 đồ thị:  G_istar = (IE, Refinement ∪ Contribution ∪ Dependency)
               G_bpmn  = (FlowNode, SequenceFlow ∪ MessageFlow), gộp theo Pool/Lane

Định nghĩa graph edit distance có trọng số GED(G_istar, G_bpmn | mapping ứng viên π) hoặc, tổng quát
hơn, dùng Gromov–Wasserstein distance (optimal transport giữa 2 không gian đo được định nghĩa từ ma
trận khoảng cách nội tại của mỗi đồ thị — không cần 2 đồ thị cùng số node) để tìm phép gán xác suất
π* tối thiểu hoá chi phí biến dạng cấu trúc.

Consistency score = 1 / (1 + GW-distance(G_istar, G_bpmn))
So khớp với `ConformanceMapping` đã có (nếu có): π* nên trùng phần lớn với mapping thủ công —
lệch nhiều giữa π* (tối ưu tự động) và mapping thủ công hiện tại là tín hiệu "mapping đã lỗi thời".
```

### 7.3. Khác gì các đề xuất trước

Đây là phương pháp duy nhất trong 10 đề xuất **tự phát hiện correspondence** thay vì giả định đã
cho — dùng để (a) kiểm tra 1 file `.map` có còn hợp lý hay không (bằng cách so π* với mapping viết
tay), và (b) gợi ý mapping ban đầu khi chưa có file `.map` nào (bootstrap).

### 7.4. Hạn chế

Graph edit distance/optimal transport trên đồ thị lớn là bài toán NP-khó về bản chất (GED) hoặc cần
xấp xỉ (GW-distance thường giải bằng gradient descent, không đảm bảo tối ưu toàn cục) — chỉ khả thi
với heuristic/xấp xỉ, không phải thuật toán đúng đắn chính xác như D; nên coi là **công cụ gợi ý**,
không phải bằng chứng hình thức.

**Từ khoá tra cứu**: "graph edit distance" + "model matching" + "requirements traceability",
"Gromov-Wasserstein" + "graph alignment", "automatic traceability link recovery".

---

## 8. Đề xuất #5: Lập luận phi đơn điệu song cực cho contribution có xung đột/chu trình (Bipolar Argumentation Consistency)

**Nền tảng**: lý thuyết lập luận hình thức (Dung's Abstract Argumentation Framework, mở rộng song
cực — bipolar AF), khác hẳn propagation số học của PGA/#2.

### 8.1. Ý tưởng cốt lõi

`P_Make`/`P_Break`/`BP_fulfill`/`BP_deny` của D (và bản mở rộng số của PGA) là **luật lan truyền
fixpoint** — khi contribution graph có **chu trình** (ví dụ `A --help--> B`, `B --hurt--> A`, hoàn
toàn hợp lệ về cú pháp `.istar`), thứ tự áp luật có thể ảnh hưởng tới kết quả, và ngữ nghĩa "đúng"
của 1 chu trình tự-mâu-thuẫn là không rõ ràng trong cả D lẫn PGA (PGA: sigmoid trên chu trình có thể
không hội tụ về điểm cố định duy nhất). Lý thuyết lập luận hình thức (Dung 1995) được thiết kế
**chính xác cho bài toán này**: coi mỗi Goal/Task/Quality là 1 "argument", mỗi `Break`/`Hurt` là 1
quan hệ **attack**, mỗi `Make`/`Help` là 1 quan hệ **support** (bipolar AF, Cayrol–Lagasquie-Schiex
2005), rồi dùng **grounded extension** (ngữ nghĩa hoài nghi — skeptical, luôn tồn tại duy nhất, tính
được bằng thuật toán lặp đơn giản hội tụ hữu hạn kể cả khi đồ thị có chu trình) để xác định tập
argument "được chấp nhận" một cách rõ ràng, có nền tảng lý thuyết vững hơn heuristic sigmoid.

### 8.2. Hình thức hoá sơ bộ

```
AF = (Args, Attacks, Supports)
Args     = GoalTaskInstances đã "fired" (từ BPMN, giống P_leaf của D) ∪ Quality nodes
Attacks  = { (e,q) | Contribution(e, Break⁻, q) }
Supports = { (e,q) | Contribution(e, Make⁺, q) }

Grounded extension E* = hợp điểm cố định nhỏ nhất của toán tử đặc trưng F (characteristic function)
  chuẩn của Dung AF, mở rộng cho support theo "necessity"/"deductive" semantics của bipolar AF.

quality q "được chấp nhận" (accepted)  ⟺  q ∈ E*
quality q "bị bác bỏ" (rejected)       ⟺  q bị attack bởi 1 argument trong E* và không được defend
quality q "chưa xác định" (undecided)  ⟺  còn lại (đúng ngữ nghĩa hoài nghi — không đoán liều)
```

Verdict consistency: `ALIGNED` nếu mọi quality gốc ∈ E*; `UNDECIDED` (nhãn thứ 3, trung thực hơn ép
về nhị phân) nếu rơi vào vùng chưa xác định do chu trình tự-mâu-thuẫn — bản thân nhãn `UNDECIDED`
này là thông tin: nó nói cho người dùng biết "goal model của bạn có mâu thuẫn nội tại (chu trình
attack/support), không phải lỗi của process" — một loại lỗi mà D/E hoàn toàn không phát hiện được
vì cả hai đều ngầm giả định contribution graph không có chu trình mâu thuẫn.

### 8.3. Khác gì PGA/#2

PGA và #2 đều **giả định ngầm** contribution graph "well-behaved" (không chu trình mâu thuẫn khó
xử) — PGA dùng sigmoid có thể cho ra 1 giá trị dù đồ thị có chu trình (nhưng giá trị đó có thể phụ
thuộc thứ tự cập nhật, không ổn định/well-defined). Argumentation framework xử lý chu trình **có
nguyên tắc rõ ràng** (grounded semantics luôn well-defined, duy nhất, không phụ thuộc thứ tự) — bù
đắp đúng lỗ hổng lý thuyết này.

### 8.4. Hạn chế

Grounded semantics "hoài nghi" — nhiều trường hợp cho kết quả `UNDECIDED` mà người dùng muốn 1 câu
trả lời dứt khoát hơn (có thể chuyển sang preferred/stable semantics để bớt hoài nghi, đánh đổi lấy
có thể nhiều "extension" thay vì 1 — cần chọn semantics phù hợp mục đích).

**Từ khoá tra cứu**: "bipolar argumentation" + "goal model", "Dung argumentation framework" +
"requirements", "argumentation-based reasoning i*".

---

## 9. Đề xuất #6: Kiểm tra tương thích theo kiểu hợp thành qua ngữ nghĩa hàm tử (Compositional Consistency via Functorial Semantics)

**Nền tảng**: lý thuyết phạm trù (category theory) — khác hẳn mọi phương pháp trên ở chỗ nhắm vào
**khả năng hợp thành (compositionality)**, không phải 1 phép kiểm tra toàn cục.

### 9.1. Ý tưởng cốt lõi

D phải build **toàn bộ** product LTS trước khi kết luận — không tận dụng được cấu trúc phân rã theo
Pool/Lane/ActorDef đã có sẵn (mỗi actor lẽ ra có thể kiểm tra độc lập rồi hợp lại). Lý thuyết phạm
trù cho 1 công cụ chuẩn để làm "kiểm tra cục bộ, suy ra đảm bảo toàn cục" một cách có nguyên tắc:
coi mỗi cặp (goal-model-con-của-1-actor, process-con-của-1-Pool/Lane) là 1 vật (object) trong phạm
trù `C`, và định nghĩa **quan hệ "hiện thực hoá đúng" là một hàm tử (functor) `F: Proc → Goal`** bảo
toàn cấu trúc composition (nếu process P1 ghép nối tuần tự/song song với P2 qua 1 toán tử ⊗, thì
`F(P1 ⊗ P2)` phải bằng `F(P1) ⊗' F(P2)` theo đúng toán tử ghép nối tương ứng phía goal model, ví dụ
AND-refine). Nếu chứng minh được `F` là hàm tử hợp lệ ở **từng actor/Pool riêng lẻ**, định lý bảo
toàn composition của category theory đảm bảo hợp lệ ở **toàn hệ thống ghép từ nhiều actor** mà không
cần build lại toàn bộ product LTS toàn cục.

### 9.2. Hình thức hoá sơ bộ (rất tóm lược — đây là hướng lý thuyết nặng nhất trong 10 đề xuất)

```
Proc  = phạm trù có vật là (Pool, ràng buộc control-flow nội bộ), morphism là "process refinement"
        (P ⊑ P' nếu P' chỉ thêm chi tiết không đổi hành vi quan sát được ở giao diện Pool)
Goal  = phạm trù có vật là (ActorDef, cây Refinement của actor đó), morphism là "goal refinement"
        tương tự (AND-refine thêm con là 1 morphism)
F: Proc → Goal    bảo toàn identity và composition:  F(P1;P2) = F(P1);F(P2)  (; = ghép tuần tự)

Kiểm tra cục bộ: với mỗi actor a, verify F thoả trên (Pool_a, GoalTree_a) — bài toán nhỏ, độc lập.
Định lý hợp thành (functor composition, chuẩn category theory): nếu đúng ở mọi a riêng lẻ và các
điểm ghép nối (message flow giữa pool) tương thích kiểu (type-compatible tại biên), thì đúng ở toàn
hệ thống — không cần duyệt lại state space toàn cục như D.
```

### 9.3. Khác gì D

D là **monolithic**: build 1 product LTS cho toàn bộ collaboration rồi BFS 1 lần — không tận dụng
được việc BPMN vốn đã chia theo Pool/Lane. Phương pháp này là phương pháp **duy nhất trong 10 đề
xuất giải quyết vấn đề co giãn (scalability) bằng cấu trúc hợp thành toán học**, thay vì bằng
heuristic (tối ưu BFS) hay xấp xỉ (Monte Carlo như PGA).

### 9.4. Hạn chế

Đây là hướng **hình thức hoá nặng nhất, khó triển khai thực tế nhất** trong 10 đề xuất — đòi hỏi
định nghĩa nghiêm ngặt "morphism" cho cả 2 phía sao cho định lý composition thực sự áp dụng được
(nếu định nghĩa sai, "định lý miễn phí" không còn đúng) — phù hợp làm hướng nghiên cứu lý thuyết dài
hạn hơn là hạng mục triển khai ngắn hạn.

**Từ khoá tra cứu**: "compositional verification" + "category theory" + "business process",
"functorial semantics" + "requirements refinement", "open Petri net" + "compositional".

---

## 10. Đề xuất #7: Kiểm chứng thời gian thực trên engine đang chạy (Online Runtime Monitor Synthesis)

**Nền tảng**: lý thuyết kiểm chứng thời gian thực (runtime verification, RV3/LTL3 automaton) —
khác hẳn mọi phương pháp trên ở chỗ chạy **trong lúc** process đang thực thi thật (online), không
phải phân tích tĩnh/offline.

### 10.1. Ý tưởng cốt lõi

D, E, #2, #6 đều phân tích model **trước khi/không cần** chạy; PGA dùng log **đã ghi xong** (batch,
offline). Không phương pháp nào giám sát **đang** chạy để cảnh báo ngay khi 1 instance cụ thể có
nguy cơ vi phạm, trước khi nó kết thúc — hữu ích khi có thể **can thiệp** (ví dụ route lại 1 workflow
instance đang chạy dở sang path khác) thay vì chỉ biết sau khi đã xong (PGA) hay biết trước khi
chạy bất kỳ instance nào (D/E/#2/#6).

### 10.2. Hình thức hoá sơ bộ

```
Với mỗi Quality gốc q, dịch cấu trúc contribution dẫn tới q thành 1 công thức LTL (linear temporal
logic) theo mẫu chuẩn (property pattern của Dwyer et al., đã dùng rộng rãi cho runtime verification):
  ví dụ  φ_q = F(fire(Approve) ∨ fire(other-Make-task))   // "cuối cùng sẽ có 1 hành động Make nào
                                                            đó cho q, trước khi process kết thúc"
Biên dịch φ_q → LTL3 monitor (automaton 3 giá trị {true, false, unknown} — chuẩn RV, có công cụ
sẵn: LTL3Tools, MonPoly...). Gắn monitor này vào engine BPMN thật (Camunda/Flowable, qua execution
listener) — mỗi khi 1 task fire, monitor cập nhật trạng thái {true,false,unknown} cho instance đó
NGAY LẬP TỨC, không đợi instance kết thúc.
```

### 10.3. Khác gì PGA

PGA đọc log **sau khi** đã có (aggregate qua nhiều instance, cho điểm trung bình); phương pháp này
giám sát **từng instance đơn lẻ, tại thời điểm đang chạy** — bổ sung theo trục "per-instance,
real-time" mà PGA (trục "aggregate, offline") không phủ tới. Hai phương pháp có thể chạy song song:
RV monitor cảnh báo sớm cho 1 instance cụ thể; PGA tổng hợp xu hướng toàn cục theo thời gian.

### 10.4. Hạn chế

Cần có engine BPMN thật đang chạy (project hiện tại là công cụ modeling, không phải execution
engine) — chỉ khả thi nếu output của `Bpmn2Collaboration` được export sang 1 engine chuẩn (BPMN XML
chuẩn OMG, engine như Camunda đọc được) — phụ thuộc hạ tầng ngoài phạm vi hiện tại của repo.

**Từ khoá tra cứu**: "runtime verification" + "business process" + "LTL3", "online conformance
checking" + "process mining", "requirements monitoring" + "BPMN execution".

---

## 11. Đề xuất #8: Tìm kịch bản xấu nhất bằng tìm kiếm metaheuristic (Search-Based Worst-Case Alignment Discovery)

**Nền tảng**: tìm kiếm dựa trên metaheuristic (search-based software engineering — SBSE: genetic
algorithm/simulated annealing), khác hẳn duyệt vét cạn (D) và giải tích số học đóng (PGA/#2).

### 11.1. Ý tưởng cốt lõi

Khi process có nhiều gateway lồng nhau + vòng lặp, không gian kịch bản khả thi tăng theo cấp số
nhân/vô hạn (loop) — D cần giới hạn (bound) số vòng lặp để BFS dừng, #2 chỉ chứng minh được các
tính chất tuyến tính. Thay vì cố duyệt **hết**, dùng metaheuristic để **tìm kiếm có định hướng** 1
kịch bản gần-xấu-nhất (near-worst-case) — đủ dùng cho mục đích thực tế "tìm phản ví dụ nghiêm trọng
nhất để sửa trước", đúng tinh thần search-based testing (đã dùng rộng rãi để tìm test case vi phạm
trong software testing, nhưng chưa thấy áp dụng cho cặp goal-model/process-model cụ thể).

### 11.2. Hình thức hoá sơ bộ

```
Cá thể (individual) = 1 dãy quyết định tại mỗi gateway XOR/OR gặp phải dọc 1 lần chạy process
                       (ví dụ vector bit "chọn nhánh nào" tại mỗi gateway, độ dài = số gateway ×
                       số lần lặp tối đa cho phép)
Hàm fitness(individual) = − AlignmentPropagation(...).score    // càng thấp AS càng "tốt" cho search
                                                                  (search tìm minimum của AS = tệ nhất)
Toán tử: mutation (đổi 1 quyết định gateway ngẫu nhiên), crossover (ghép nửa đầu dãy cá thể A với
nửa sau cá thể B) — chuẩn GA, không cần thiết kế riêng.
Output: cá thể có fitness thấp nhất tìm được sau N thế hệ → dịch ngược thành counterexampleTrace
        (giống định dạng `ComplianceResult.counterexampleTrace` đã có ở D, tái dùng được nguyên
        vẹn kiểu dữ liệu này).
```

### 11.3. Khác gì D

D **đảm bảo đúng đắn/đầy đủ** (nếu nói NON_COMPLIANT thì chắc chắn có counterexample, nếu nói
COMPLIANT thì chắc chắn không có) nhưng cần duyệt hết không gian trạng thái — không scale với loop
vô hạn. Phương pháp này **không đảm bảo tìm được worst-case thật** (metaheuristic có thể kẹt ở cực
tiểu địa phương) nhưng **scale tốt với process rất lớn/có loop** mà D phải cắt bớt (bounded model
checking) — hai phương pháp bổ sung nhau theo trục "đảm bảo lý thuyết" (D) vs. "scale thực tế"
(#8), giống quan hệ kinh điển giữa model checking và search-based testing trong kiểm chứng phần
mềm nói chung.

### 11.4. Hạn chế

Không có đảm bảo lý thuyết (soundness/completeness) — chỉ nên dùng khi D không kịp chạy hết (state
space quá lớn) như 1 "lớp lọc nhanh, không chính xác tuyệt đối" trước khi (nếu cần) chạy D đầy đủ
trên vùng đã thu hẹp.

**Từ khoá tra cứu**: "search-based software engineering" + "business process compliance",
"genetic algorithm" + "counterexample generation", "metaheuristic" + "requirements violation".

---

## 12. Đề xuất #9: Quy trách nhiệm nhân quả cho sai lệch (Causal Responsibility Attribution)

**Nền tảng**: mô hình nhân quả cấu trúc (Structural Causal Model — Judea Pearl) + suy luận phản
thực (counterfactual) + actual causation (Halpern–Pearl) — khác hẳn sensitivity (đạo hàm, tương
quan) của PGA.

### 12.1. Ý tưởng cốt lõi

`SensitivityAnalyzer` của PGA (§4.5) cho biết `∂AS/∂p_branch` — đây là độ nhạy **tương quan/đạo
hàm**, không phải quan hệ **nhân quả**: đạo hàm cao không nhất thiết nghĩa là "gateway đó là NGUYÊN
NHÂN THỰC SỰ" của 1 lần vi phạm cụ thể (2 biến có thể cùng bị ảnh hưởng bởi 1 nguyên nhân chung mà
không nhân quả trực tiếp với nhau). Với **1 counterexample trace cụ thể** đã tìm được (từ D hoặc
#8), câu hỏi thực tế thường là: *"Trong lần chạy CỤ THỂ này, quyết định tại gateway nào là NGUYÊN
NHÂN THỰC SỰ khiến quality bị denied — nếu đổi riêng quyết định đó (giữ nguyên mọi thứ khác) thì
kết quả có đổi không?"* — đây là câu hỏi phản thực (counterfactual) kinh điển, trả lời bằng SCM.

### 12.2. Hình thức hoá sơ bộ

```
Xây SCM từ đúng cấu trúc Refinement/Contribution đã có (không cần dữ liệu mới):
  mỗi node = 1 biến; structural equation = luật P_AND/P_OR/P_Make/P_Break của D, viết lại dưới dạng
  hàm nhân quả  V := f_V(Parents(V))  thay vì luật lan truyền fixpoint thuần tuý.

Với 1 trace cụ thể τ = (n1, n2, ..., nk) đã biết dẫn tới denied (từ D.counterexampleTrace):
  với mỗi quyết định gateway n_i trong τ, tính counterfactual:
    τ' = τ với n_i được thay bằng nhánh khác, giữ nguyên phần còn lại (theo đúng định nghĩa "actual
         cause" của Halpern-Pearl: cần AC1 — n_i thực sự xảy ra trong τ; AC2 — tồn tại 1 phép "đặt
         lại" (contingency) sao cho thay n_i thì kết quả đổi; AC3 — tối thiểu, không thêm biến thừa)
  n_i là "nguyên nhân thực sự" (actual cause) nếu τ' cho isSuccess(gm) = true.

Output: xếp hạng các gateway trong τ theo "có phải actual cause hay không" (nhị phân theo định nghĩa
Halpern-Pearl, chặt chẽ hơn xếp hạng liên tục |∂AS/∂p| của PGA) + độ trách nhiệm (responsibility,
Chockler-Halpern) = 1/(số biến tối thiểu cần đổi cùng lúc để đảo kết quả).
```

### 12.3. Khác gì PGA

PGA cho 1 con số tương quan tổng hợp trên **nhiều instance** (kỳ vọng thống kê); phương pháp này cho
**lời giải thích nhân quả chặt chẽ cho 1 instance/counterexample cụ thể** — đúng loại câu trả lời
người review muốn khi debug 1 lần vi phạm nhất định ("tại sao lần NÀY nó sai, không phải trung bình
toàn bộ log"). Bổ sung tốt cho `report/ConformanceOverlay` đã thiết kế: thay vì chỉ tô đỏ toàn bộ
trace, tô đậm hơn đúng (các) node là actual cause.

### 12.4. Hạn chế

Định nghĩa "actual cause" của Halpern-Pearl có nhiều biến thể (AC1–AC3, "modified" HP definition...)
và với đồ thị nhân quả có nhiều biến, việc tìm contingency tối thiểu là bài toán tổ hợp (có thể tốn
kém với goal model lớn) — cần giới hạn phạm vi (chỉ xét biến trên đúng 1 trace, không toàn model)
để khả thi.

**Từ khoá tra cứu**: "actual causation" + "requirements violation", "Halpern-Pearl causality" +
"root cause analysis", "counterfactual explanation" + "business process compliance".

---

## 13. Đề xuất #10: Phát hiện "mapping mục nát" qua embedding ngôn ngữ tự nhiên (Semantic Drift Detection over Natural-Language Labels)

**Nền tảng**: embedding văn bản (sentence embedding kiểu Sentence-BERT) trên **nội dung nhãn**, khác
hẳn dòng L đã liệt kê ở §2 (L dùng embedding **cấu trúc đồ thị** qua GNN) — đây là embedding **ý
nghĩa ngôn ngữ tự nhiên** của tên gọi, một tầng hoàn toàn khác (semantic layer, không phải structural
layer).

### 13.1. Ý tưởng cốt lõi

Mọi phương pháp D/E/PGA/#2–#9 đều tin tưởng tuyệt đối vào `ConformanceMapping` (file `.map`): nếu
`.map` nói `Approve ↔ approve`, các phương pháp coi đó là đúng và suy luận tiếp. Nhưng trong thực tế
co-tiến hoá (developer sửa tên activity trong `.bpmn2` từ `approve` thành `finalizeContract` do yêu
cầu nghiệp vụ đổi, nhưng quên cập nhật `.map`), mapping vẫn **hợp lệ về mặt cú pháp** (2 id vẫn tồn
tại) nhưng đã **sai về ý nghĩa** — không phương pháp cấu trúc/hình thức nào ở trên phát hiện được
loại lỗi này vì chúng không đọc *tên gọi*, chỉ đọc *cấu trúc đồ thị*. Phương pháp #10 bổ sung đúng
tầng còn thiếu này: kiểm tra bằng embedding ngữ nghĩa tên gọi (label, description nếu có) của cặp
phần tử đã map, cảnh báo khi độ tương đồng ngữ nghĩa giữa 2 nhãn đã map giảm xuống dưới ngưỡng sau
khi 1 trong 2 bên đổi tên.

### 13.2. Hình thức hoá sơ bộ

```
embed: String → ℝ^d       (sentence embedding, có thể chạy local — không nhất thiết cần gọi LLM
                            cloud, có thể dùng mô hình embedding nhỏ chạy offline)

Với mỗi ElementMapping(istarElementId, bpmnNodeId):
  sim = cosine( embed(label(istarElementId)), embed(label(bpmnNodeId)) )
  history: lưu sim tại lần cuối mapping được xác nhận đúng (baseline)
  cảnh báo "semantic mapping rot" nếu sim hiện tại < baseline − ngưỡng (ví dụ 0.15), ngay cả khi
  cấu trúc file `.map` không đổi 1 ký tự nào — vì chỉ có tên tại 1 trong 2 phía đã đổi.
```

### 13.3. Khác gì dòng L (embedding cấu trúc) đã liệt kê ở §2

Dòng L nhúng **đồ thị** (quan hệ, lân cận) — bắt được trôi về **cấu trúc** (thêm/bớt Refinement,
đổi contribution...). #10 nhúng **chuỗi ký tự tên gọi** — bắt được trôi về **ý nghĩa/từ vựng**, một
loại lỗi hoàn toàn khác (cấu trúc không đổi, chỉ tên đổi) mà dòng L không thấy vì GNN cấu trúc không
quan tâm nội dung text của nhãn (thường chỉ dùng làm feature phụ, không phải tín hiệu chính).

### 13.4. Hạn chế

Ngưỡng "giảm bao nhiêu thì coi là rot" mang tính kinh nghiệm (giống τ của PGA); nhãn tiếng Việt lẫn
tiếng Anh trong cùng project (ví dụ file ví dụ `.istar` có thể đặt tên tiếng Anh, comment tiếng
Việt) cần mô hình embedding đa ngôn ngữ, không phải mọi mô hình sentence-embedding nhỏ đều tốt cho
cả 2 ngôn ngữ như nhau — cần benchmark riêng trước khi tin ngưỡng cảnh báo.

**Từ khoá tra cứu**: "semantic drift detection" + "traceability link", "mapping decay" +
"model co-evolution", "sentence embedding" + "requirements traceability maintenance".

---

## 14. Đề xuất #11: Hợp nhất Metamodel — Kiểm tra Well-formedness trên MỘT Model Duy Nhất (Unified/Woven Metamodel qua chính USE core)

**Nền tảng**: model weaving / megamodel (Bézivin, Del Fabro — AMW) trong lý thuyết MDE, nhưng ở đây
gắn cụ thể với khả năng **dùng lại chính engine `MModel`/`MSystem`/OCL của USE core** mà project đã
sẵn phụ thuộc — khác **về bản chất** với 10 đề xuất trên: đây không phải 1 thuật toán kiểm tra, mà
là 1 **chiến lược biểu diễn** (representation strategy) có thể làm vật chứa (host) rẻ hơn nhiều cho
một số thuật toán trong 10 đề xuất kia.

### 14.0. Ý tưởng của bạn, phát biểu lại chính xác

Thay vì giữ 2 model (`IStarModel`, `Bpmn2Collaboration`) tách biệt và định nghĩa **quan hệ** giữa
chúng (correspondence mapping + thuật toán đồng bộ, như D/E/PGA/#2–#10 đều làm), ta **hợp nhất**
chúng thành 1 metamodel lớn hơn — 1 metamodel "có nhiều ngữ nghĩa hơn", chứa đủ khái niệm để biểu
diễn *cả* goal-model *lẫn* process-model trong cùng 1 không gian đối tượng. Đầu vào: `.istar` +
`.bpmn2` + luật `.map`. Đầu ra: 1 **instance** của metamodel hợp nhất đó (1 object graph duy nhất).
Kiểm tra tương thích lúc này không còn là "đồng bộ 2 model" nữa, mà **suy biến về đúng bài toán kiểm
tra well-formedness của 1 model duy nhất** — bài toán mà OCL/USE vốn được thiết kế để giải từ đầu.

### 14.1. Vì sao đây là phát hiện thực sự có giá trị cho *đúng project này*

Đọc trực tiếp `doc/use-core-design-rules.md` (§2, §4) xác nhận: `IStarModelFactory`/
`Bpmn2ModelFactory` sinh ra **object Java thuần** (`record`/`sealed interface`) — không phải
`org.tzi.use.uml.mm.MClass`/`MObject`, không đi qua `MSystem`/`ModelFactory` của USE core, chỉ được
`IStarView`/`Bpmn2View` đọc để vẽ. Nghĩa là: toàn bộ 10 đề xuất D/E/PGA/#2–#10 (và cả bản thiết kế
đã có trong `conformance-istar-bpmn2.md`) đều **tự viết lại từ đầu** một bộ máy kiểm tra
(marking/propagation/BFS/DL-reasoning) bằng Java tay, trong khi USE core **đã có sẵn** đúng loại bộ
máy đó (OCL invariant evaluator + SOIL statement executor + `MSystemState`) — chỉ là chưa từng được
nối vào cho `.istar`/`.bpmn2`. Ý tưởng hợp nhất metamodel, nếu hiện thực bằng cách **chiếu
(project) instance của `.istar`+`.bpmn2` thành `MObject`/`MLink` thật** (qua `ModelFactory` — không
phải viết tay `.use`), sẽ đưa project quay lại **đúng phương pháp luận gốc của chính JUCS** (§3.6,
Fig. 12 của `JUCS.md`: sinh 1 file USE từ script kịch bản, dùng lệnh USE để chạy và kiểm tra) — điều
mà `conformance-istar-bpmn2.md` đã **chủ động rẽ khỏi** để dùng Java tay (lý do nêu ở §3.2 tài liệu
đó: "Không cài TGG engine đầy đủ"). Đề xuất #11 gợi ý: có thể không cần Java tay **cho phần kiểm
tra**, chỉ cần Java tay **cho phần chiếu (projection) 1 lần** — rẻ hơn nhiều so với viết lại BFS/DL
reasoner/propagation fixpoint.

### 14.2. Hình thức hoá sơ bộ

```
Bước 1 — Định nghĩa Unified Metamodel UMM (1 lần, không đổi theo từng cặp model cụ thể):
  UMM = classes(IStarModel) ⊎ classes(Bpmn2Collaboration) ⊎ { Realizes }
  Realizes: association 2 ngôi — IntentionalElement.realizedBy : Set(FlowNode)
                                  FlowNode.realizes           : Set(IntentionalElement)
  (thay `ElementMapping(String,String)` bằng 1 association THẬT trong UMM, có thể navigate 2 chiều
   bằng OCL ngay: self.realizedBy, self.realizes — không cần `ConformanceMapping.bpmnNodeOf()` tra
   bằng String id nữa)

Bước 2 — Với 1 cặp (.istar, .bpmn2, .map) cụ thể, build UMM INSTANCE (không phải UMM class mới):
  với mỗi IntentionalElement e trong IStarModel  → 1 MObject của MClass tương ứng trong UMM
  với mỗi FlowNode n trong Bpmn2Collaboration    → 1 MObject của MClass tương ứng
  với mỗi Refinement/Contribution/Dependency/SequenceFlow → 1 MLink
  với mỗi ElementMapping(e,n) trong .map          → 1 MLink của association Realizes(e,n)
  → dựng bằng `ModelFactory`/`MSystem` Java API (batch), KHÔNG phải sinh text `.use` rồi parse lại
    (nhanh hơn, không qua ANTLR3 vòng 2)

Bước 3 — Viết LẠI (1 lần, dùng chung mọi case study) các luật của D/E dưới dạng OCL invariant trên
UMM, ví dụ luật P_AND của Caballero-Villalobos:

  context Refinement.And inv andSatisfiedImpliesParentSatisfied:
    self.children->forAll(c | c.realizedBy->exists(n | n.hasFired)) implies
      self.parent.realizedBy->exists(n | n.hasFired)

  hoặc luật exclusivity của Gröner (structural pre-check, §3.7 conformance-istar-bpmn2.md):

  context Refinement.And inv noExclusiveBranchesAmongChildren:
    self.children->forAll(c1, c2 |
      c1 <> c2 implies
        not exclusiveBranch(c1.realizedBy, c2.realizedBy))   -- 'exclusiveBranch' = OCL operation
                                                                phụ trợ, định nghĩa 1 lần bằng cách
                                                                duyệt gateway XOR trong Bpmn2 UMM

Bước 4 — Kiểm tra: gọi thẳng lệnh `check` sẵn có của USE (`MSystemState.check(...)`) trên UMM
instance — không cần `StructuralConformanceChecker`/`ComplianceChecker` tự viết.

Bước 5 (phần động, tương đương LTS của D) — sinh SOIL statement cho mỗi action "fire 1 FlowNode",
chạy qua `MSystem.execute(...)`, sau mỗi bước gọi lại `check`; dùng khả năng lưu/khôi phục state sẵn
có của USE (undo) để làm backtracking cho BFS — thay vì tự viết `ProductLts`/`BpmnMarking`/
`IStarMarking` bằng tay.
```

### 14.3. Cái được (so với 10 đề xuất trên, và so với thiết kế Java tay hiện có)

- **Không cần viết lại bộ máy đánh giá OCL/fixpoint/BFS** — USE core đã có, đã test kỹ qua nhiều năm,
  chỉ cần viết ĐÚNG invariant (dễ review hơn nhiều so với review 1 thuật toán Java tự viết).
- **1 invariant OCL có thể navigate xuyên cả 2 "ngôn ngữ" tự nhiên** (`self.realizedBy.oclIsKindOf
  (Gateway)`) — không cần tra id qua `ConformanceMapping` như thiết kế hiện tại.
- **Được "miễn phí" các công cụ khác của USE** đi kèm engine: USE có tính năng **sinh instance thoả
  mãn invariant** (model validator / snapshot generator) — nếu UMM có invariant nhưng KHÔNG tìm được
  instance nào thoả (given 1 phần cấu trúc cố định từ `.istar`/`.bpmn2` đã có, phần còn thiếu để hệ
  tự sinh), đó là tín hiệu chẩn đoán bổ sung mà không 1 phương pháp nào trong 10 đề xuất trên có —
  **không cần code thêm gì**, vì đây là tính năng có sẵn của USE core.
- Là **vật chứa (host) tốt** cho #2 (invariant tuyến tính — OCL invariant thuần), #5 (bipolar
  argumentation — có thể biểu diễn attack/support như 2 association riêng rồi viết invariant kiểu
  "accepted" bằng OCL đệ quy), và cả D/E gốc (marking propagation, exclusivity check) — tức 4/10 đề
  xuất trước có thể **triển khai rẻ hơn hẳn** nếu đi qua UMM thay vì viết Java riêng cho từng cái.

### 14.4. Cái KHÔNG được — hạn chế cần nói rõ, không tô hồng

- **Không phải "miễn phí"**: bước 2 (chiếu instance sang `MObject`/`MLink` qua `ModelFactory`) là
  **hạ tầng hoàn toàn chưa tồn tại** trong repo (đã xác nhận qua đọc `use-core-design-rules.md`) —
  cần viết 1 tầng "projector" mới, tự nó không nhỏ (phải hiểu đúng API `ModelFactory`/`MSystem` của
  USE core, vốn được thiết kế cho *người dùng viết `.use` bằng tay*, không phải cho 1 plugin build
  instance bằng code — có thể có ma sát API không lường trước).
- **OCL không hợp với bài toán tìm kiếm/tối ưu/trò chơi** — #3 (game-theoretic), #4 (graph-matching/
  optimal transport), #6 (category theory composition), #8 (metaheuristic search), #9 (causal
  counterfactual) **không tự nhiên viết được thành invariant khai báo thuần tuý** — những đề xuất
  này vẫn cần thuật toán Java riêng dù có UMM hay không; UMM chỉ giúp *biểu diễn dữ liệu đầu vào*
  cho chúng gọn hơn (navigate OCL để lấy dữ liệu), không thay được chính thuật toán.
- **Phần "động" (LTS/marking theo thời gian) qua SOIL vẫn phải tự sinh script** — USE không tự biết
  "thứ tự fire hợp lệ của BPMN token" là gì, vẫn cần code Java để dịch luật enable/fire của
  `Bpmn2LtsBuilder` thành chuỗi lệnh SOIL — phần này KHÔNG được lược bỏ, chỉ đổi "nơi state được lưu
  và invariant được đánh giá" từ Java tự viết sang USE core.
- **Rủi ro hiệu năng chưa rõ**: chưa có benchmark nào cho biết `MSystem`/OCL evaluator của USE core
  xử lý model lớn (hàng trăm FlowNode + IntentionalElement) nhanh hay chậm hơn code Java tay tối ưu
  riêng cho bài toán này — cần đo thử trước khi cam kết.

### 14.5. Khuyến nghị cụ thể

Không nên coi #11 là "thay thế toàn bộ 10 đề xuất trên" — nên coi là **câu hỏi kiến trúc cần trả lời
trước khi code bất kỳ đề xuất nào trong nhóm "chỉ cần OCL invariant thuần"** (D, E, #2, #5): làm 1
prototype nhỏ (ví dụ chỉ case study `construction_permit`) build UMM instance bằng `ModelFactory`
thật, viết thử 2–3 invariant tương đương luật đã có ở `conformance-istar-bpmn2.md` §3.3.1/§3.7, đo
xem có thực sự đơn giản hơn code Java tương ứng hay không — trước khi quyết định toàn bộ `P1`/`P2`
của lộ trình hiện có nên đi theo hướng Java tay (như đã thiết kế) hay hướng UMM+OCL (đề xuất #11)
này.

**Từ khoá tra cứu**: "model weaving" + "AMW" + "megamodel", "USE OCL tool" + "model integration",
"multi-language model consistency" + "single formalism reduction".

---

## 15. Bảng so sánh tổng hợp 11 đề xuất

| # | Tên | Nền tảng | Câu hỏi trả lời | Đầu ra | Cần dữ liệu ngoài? | Bổ sung cho D/E ở điểm nào |
|---|---|---|---|---|---|---|
| 1 | PGA | Xác suất/Bayesian | Đạt goal *bao nhiêu*, xu hướng ra sao? | Điểm số liên tục + drift + sensitivity | Log thật (hoặc xác suất khai báo) | Định lượng thay vì nhị phân |
| 2 | Invariant tuyến tính | Đại số tuyến tính (Petri net) | Goal có được bảo toàn *ở MỌI marking*, kể cả loop vô hạn? | Chứng minh/không tìm được invariant | Không | Chứng minh không cần duyệt state space |
| 3 | Game-theoretic obstacle | Lý thuyết trò chơi | System có *luôn thắng* dù Environment chơi xấu nhất? | Có/không winning strategy | Phân loại gateway (system/env) | Phân biệt ai kiểm soát rẽ nhánh |
| 4 | Graph-matching cost | Tối ưu tổ hợp/optimal transport | Có còn tồn tại 1 mapping hợp lý hay không? | Consistency score + mapping gợi ý | Không (tự suy ra mapping) | Không cần `.map` viết tay trước |
| 5 | Bipolar argumentation | Lập luận phi đơn điệu | Quality được chấp nhận/bác bỏ/chưa rõ khi có chu trình mâu thuẫn? | 3 nhãn: accepted/rejected/undecided | Không | Xử lý đúng chu trình attack/support |
| 6 | Functorial composition | Lý thuyết phạm trù | Kiểm cục bộ từng actor có suy ra đúng toàn cục không? | Đúng/sai theo từng actor + định lý hợp thành | Không | Scale theo Pool/Lane, không cần LTS toàn cục |
| 7 | Online RV monitor | Runtime verification/LTL3 | Instance đang chạy có đang lệch hướng không (real-time)? | Cảnh báo tức thời per-instance | Engine BPMN thật đang chạy | Per-instance, tại thời điểm chạy (D/PGA đều offline) |
| 8 | Search-based worst-case | Metaheuristic (SBSE) | Kịch bản gần-tệ-nhất là gì khi state space quá lớn cho BFS? | Counterexample gần-tối-ưu | Không | Scale khi D không kịp duyệt hết (loop lớn) |
| 9 | Causal attribution | Structural Causal Model | Trong 1 lần vi phạm cụ thể, node nào là NGUYÊN NHÂN THỰC SỰ? | Actual cause + độ trách nhiệm | Không (dùng lại counterexample của D/#8) | Giải thích nhân quả, không chỉ tương quan (PGA) |
| 10 | Semantic drift (NLP) | Sentence embedding | Mapping còn đúng NGHĨA không, dù cấu trúc file `.map` không đổi? | Cảnh báo "mapping rot" theo cặp | Mô hình embedding | Bắt lỗi tên gọi trôi nghĩa, cấu trúc không phát hiện được |
| 11 | Unified Metamodel (UMM) | Model weaving/megamodel + USE OCL engine | Kiểm well-formedness trên 1 model hợp nhất thay vì đồng bộ 2 model | Vi phạm invariant (dùng lại `check` của USE) | Không (nhưng cần viết tầng projector mới) | Không phải thuật toán mới — là **kiến trúc host** rẻ hơn cho #2/#5/D/E |

Trục phân hoá rõ nhất giữa 11 đề xuất: **(a) chắc chắn hay xấp xỉ** (2, 5, 6, 3, 11 cho bằng chứng
chặt chẽ; 1, 4, 8, 10 cho tín hiệu/gợi ý, không đảm bảo tuyệt đối), **(b) tĩnh/offline hay động/
online** (7 là online duy nhất; còn lại tĩnh hoặc offline-batch), **(c) cần input ngoài hay không**
(1 cần log, 3 cần phân loại gateway, 7 cần engine thật, 10 cần mô hình embedding; 2, 5, 6, 8, 9 chỉ
cần đúng cấu trúc `.istar`/`.bpmn2`/`.map` đã có sẵn trong project — nhóm này rủi ro tích hợp thấp
nhất, nên ưu tiên nếu muốn triển khai sớm), **(d) thuật toán hay kiến trúc** — #11 là đề xuất
**duy nhất khác loại**: không cạnh tranh với 10 đề xuất kia, mà là câu hỏi "nên implement chúng
bằng Java tay hay bằng OCL/USE core" — cần trả lời trước, không phải sau.

---

## 16. Tóm tắt khuyến nghị

- Tài liệu `conformance-istar-bpmn2.md` đã bao phủ tốt 3 phương pháp C/D/E (JUCS backbone +
  Caballero-Villalobos LTS reachability + Gröner DL pre-check) — nên triển khai theo đúng lộ trình
  P1→P2→P3 đã có trước khi mở rộng thêm.
- **Quyết định kiến trúc cần trả lời TRƯỚC TIÊN, trước cả P1**: đi theo hướng Java tay (như
  `conformance-istar-bpmn2.md` đã thiết kế) hay hướng **#11 (Unified Metamodel qua USE core)** —
  vì #11 thay đổi *cách* mọi phương pháp còn lại được viết ra, không phải *có nên làm* chúng hay
  không. Khuyến nghị: làm 1 prototype nhỏ của #11 trên case study `construction_permit` trước
  (§14.5) để đo chi phí thật, vì hạ tầng projector (`.istar`/`.bpmn2` → `MObject`/`MLink` thật của
  USE core) **hoàn toàn chưa tồn tại**, đã xác nhận qua đọc `use-core-design-rules.md`.
- Trong 10 đề xuất thuật toán, nhóm **rủi ro tích hợp thấp nhất** (không cần dữ liệu/hạ tầng ngoài,
  chỉ cần đúng cấu trúc `.istar`/`.bpmn2`/`.map` đã có) là **#2 (invariant tuyến tính)**, **#5
  (bipolar argumentation)**, **#8 (search-based worst-case)**, **#9 (causal attribution)** — đây
  cũng đúng là nhóm mà #11 làm host rẻ nhất (trừ #8, cần tìm kiếm không hợp OCL thuần).
- **#9 (causal attribution)** đặc biệt rẻ để thêm vì **dùng lại nguyên counterexample đã có từ D**
  (`ComplianceResult.counterexampleTrace`), chỉ cần thêm 1 bước hậu xử lý (tính counterfactual trên
  đúng trace đã tìm được), không cần xây lại state space.
- **#1 (PGA)** vẫn là lựa chọn có giá trị thực tiễn cao nhất **về lâu dài** (khi có log thật/engine
  vận hành) nhưng giá trị thấp hơn ở giai đoạn hiện tại (project còn là modeling tool, chưa có log).
- **#6 (functorial composition)** và **#7 (online RV)** là 2 hướng nặng nhất về hạ tầng/lý thuyết —
  nên coi là hướng nghiên cứu dài hạn, không phải hạng mục triển khai gần.
- Khuyến nghị thứ tự tổng thể: **#11 (quyết định kiến trúc, prototype nhỏ) → #9 → #2 → #5 → #8**,
  rồi mới tới #1 (PGA) khi có nguồn log, rồi #10/#4/#3, để lại #6/#7 cho giai đoạn nghiên cứu xa
  hơn.
