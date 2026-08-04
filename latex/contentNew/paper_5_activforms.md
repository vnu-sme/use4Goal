# ActivFORMS: A Formally Founded Model-based Approach to Engineer Self-adaptive Systems

**Tác giả:** Danny Weyns, Usman M. Iftikhar  |  **Năm:** 2016  |  **Venue:** ICSE 2016 (International Conference on Software Engineering)

---

## PHẦN 1 — Bối Cảnh & Giới Thiệu

### Lĩnh vực và tầm quan trọng

Bài báo thuộc lĩnh vực **Self-adaptive Systems (SAS)** — các hệ thống phần mềm có khả năng tự điều chỉnh hành vi tại runtime dựa trên điều kiện môi trường thay đổi. SAS là xu hướng thiết yếu trong kỷ nguyên IoT, điện toán đám mây, và autonomous systems, nơi môi trường hoạt động không thể được dự đoán hoàn toàn tại thời điểm thiết kế.

Kiến trúc chuẩn cho SAS là vòng lặp **MAPE-K** (Monitor–Analyze–Plan–Execute over Knowledge): hệ thống liên tục quan sát môi trường (Monitor), phân tích dữ liệu quan sát (Analyze), lên kế hoạch thích ứng (Plan), và thực thi thay đổi (Execute) — tất cả được điều phối bởi một knowledge base chung (K).

Tầm quan trọng của lĩnh vực này không thể phủ nhận: một hệ thống IoT không thể "tắt đi để cấu hình lại" mỗi khi điều kiện mạng thay đổi. Một dịch vụ cloud không thể ngừng phục vụ khách hàng để scale up. Self-adaptation là yêu cầu thiết yếu của hệ thống hiện đại.

### Bài toán cụ thể

Thách thức trung tâm mà ActivFORMS giải quyết là: **làm thế nào để kỹ thuật hóa SAS một cách có cơ sở hình thức (formally founded), đảm bảo các thuộc tính chất lượng xuyên suốt toàn bộ vòng đời hệ thống — từ thiết kế, triển khai, vận hành tại runtime, đến tiến hóa mục tiêu?**

- **Đầu vào**: Mô hình Timed Automata của vòng lặp MAPE-K, quality goals (ví dụ: packet loss < 10%, energy budget), mô hình IoT network (topology, sensor readings)
- **Đầu ra**: Hệ thống tự thích ứng đang chạy với đảm bảo hình thức, có khả năng cập nhật goals an toàn tại runtime mà không cần restart

### Tại sao khó

Bốn thách thức kỹ thuật căn bản:

1. **State explosion problem tại runtime**: Exhaustive formal verification (như model checking truyền thống) cần duyệt toàn bộ state space của hệ thống. Với hệ thống IoT có nhiều nodes, state space tăng theo cấp số mũ — không khả thi để verify tại runtime trong thời gian thực.

2. **Khoảng cách model-to-code (semantic gap)**: Phần lớn phương pháp model-driven engineering (MDE) kiểm chứng model rồi dịch sang code. Nhưng bước dịch này có thể introduce bugs mới, phá vỡ các đảm bảo hình thức đã được chứng minh trên model.

3. **Runtime goal evolution**: Hệ thống phải tiếp tục vận hành trong khi mục tiêu chất lượng được cập nhật. Không thể dừng hệ thống để reconfigure, nhưng cũng không thể thay đổi tùy tiện khi đang xử lý requests.

4. **Feedback loop chưa được kiểm chứng**: Phần lớn các phương pháp chỉ kiểm chứng hành vi thích ứng của managed system, không kiểm chứng tính đúng đắn của chính vòng lặp MAPE-K (feedback loop). Đây là thiếu sót căn bản vì feedback loop là "não bộ" của SAS.

### Đóng góp của bài

ActivFORMS (Active Formally-founded Models for Self-adaptive Systems) đề xuất:
1. **Vòng đời 4 giai đoạn với nền tảng hình thức xuyên suốt**: Design → Deploy → Verify&Adapt → Evolve
2. **Trusted Virtual Machine (TVM)**: Thực thi trực tiếp Timed Automata model — loại bỏ hoàn toàn khoảng cách model-to-code
3. **Statistical Model Checking (SMC)**: Verification xác suất tại runtime — giải quyết state explosion problem
4. **Online Update Manager (OUM)**: Cập nhật goals/model an toàn tại quiescent states mà không dừng hệ thống

---

## PHẦN 2 — Các Nghiên Cứu Liên Quan

### Hướng 1: Reference Architectures cho SAS

Rainbow (Garlan et al.), MUSIC (Blair et al.), và FORMS (Weyns et al. trước đây) cung cấp kiến trúc tham chiếu cho SAS. Ưu điểm: hướng dẫn cách tổ chức MAPE-K components. Nhược điểm: thiếu nền tảng hình thức chặt chẽ — không có formal verification, không có đảm bảo về quality properties, không có runtime evolution support.

### Hướng 2: Runtime Quantitative Verification (RQV)

RQV của Calinescu et al. là hướng tiếp cận gần nhất với ActivFORMS. RQV sử dụng exhaustive model checking tại runtime để evaluate các adaptation options. Kết quả chính xác nhưng có vấn đề scalability nghiêm trọng: với 10+ IoT devices, RQV tiêu thụ gấp 20× bộ nhớ so với ActivFORMS và gặp state explosion hoàn toàn với 25+ devices. Hơn nữa, RQV không hỗ trợ runtime goal evolution.

### Hướng 3: Model-Driven Engineering (MDE) Approaches

Các phương pháp MDE tạo code từ model (generate-then-deploy). Ưu điểm: formal verification có thể được áp dụng trên model. Nhược điểm căn bản: bước dịch model → code phá vỡ đảm bảo hình thức. Nếu code generator có bug, tất cả formal guarantees đã chứng minh trở nên vô nghĩa.

### Hướng 4: Goal-driven Self-adaptation

Các phương pháp như goalBPM, requirement-driven adaptation sử dụng goal models để hướng dẫn adaptation decisions. Tuy nhiên, chúng thường thiếu formal foundation — decisions dựa trên heuristics hay rules, không phải verified properties.

### Khoảng trống (Research Gap)

Chưa có phương pháp nào giải quyết đồng thời: (1) formal verification của feedback loop (không chỉ managed system), (2) zero model-to-code gap, (3) scalable runtime verification, (4) safe runtime goal evolution. ActivFORMS là phương pháp đầu tiên bao trùm cả bốn yêu cầu này trong một khung nhất quán.

---

## PHẦN 3 — Phương Pháp Đề Xuất

### 3.1 Ý Tưởng Cốt Lõi

Ba intuitions căn bản của ActivFORMS:

**Intuition 1**: Thay vì dịch model sang code (gây semantic gap), hãy để model **chạy trực tiếp**. Nếu model đã được kiểm chứng là đúng, và model chính là code, thì hệ thống đang chạy chính xác là hệ thống đã được kiểm chứng.

**Intuition 2**: Thay vì exhaustive verification tại runtime (quá tốn kém), dùng **Statistical Model Checking (SMC)** — chạy nhiều simulation runs để ước lượng xác suất đạt quality goals. SMC có thể được tune: nhiều simulations = chính xác hơn nhưng chậm hơn. Đây là đánh đổi linh hoạt phù hợp với context runtime.

**Intuition 3**: Khi cần cập nhật goals tại runtime, chỉ thực hiện thay đổi tại **quiescent states** — những khoảnh khắc hệ thống không có transition đang được xử lý. Như vậy, update không interfere với ongoing operations.

### 3.2 Kiến Trúc / Pipeline Tổng Thể

**Giai đoạn 1 — Design**:
Kỹ sư mô hình hóa vòng lặp MAPE-K bằng Timed Automata templates trong Uppaal. Sau đó, Uppaal model checker thực hiện exhaustive verification trước khi deploy: kiểm tra deadlock-freedom, safety properties (ví dụ: "không bao giờ cấu hình một node khi nó đang transmit"), và liveness properties.

**Giai đoạn 2 — Deploy**:
Thay vì compile model sang code, **Trusted Virtual Machine (TVM)** — một interpreter cho Timed Automata — thực thi trực tiếp model. TVM là bridge giữa Uppaal model và runtime execution, đảm bảo semantic fidelity tuyệt đối: behavior của hệ thống đang chạy là chính xác behavior đã được kiểm chứng.

**Giai đoạn 3 — Verify & Adapt (Runtime)**:
Khi cần đưa ra adaptation decision, SMC engine chạy N simulation runs (ví dụ: 1000 lần) trên Timed Automata model với các adaptation options khác nhau. Mỗi run cho biết option này có thỏa mãn quality goals không. Sau N runs, có ước lượng xác suất P(option → satisfy goals). Option có xác suất cao nhất được chọn.

**Giai đoạn 4 — Evolve (Runtime Goal Update)**:
Online Update Manager (OUM) liên tục monitor trạng thái của TVM. Khi phát hiện quiescent state (không có transition nào đang xử lý), OUM apply pending goal updates một cách atomic và safe. Hệ thống tiếp tục chạy mà không restart.

### 3.3 Các Thành Phần Chính

**Timed Automata Templates**:
Ngôn ngữ mô hình hóa MAPE-K. Timed Automata là finite automata với clocks — phù hợp để mô hình hóa real-time behavior của IoT systems. Uppaal cung cấp graphical editor và model checker cho Timed Automata. Mỗi MAPE component (Monitor, Analyzer, Planner, Executor) được mô hình hóa như một automaton riêng, communicate qua channels.

**Uppaal Model Checker**:
Tool exhaustive verification cho Timed Automata, chạy tại design time. Kiểm tra properties dưới dạng TCTL (Timed Computation Tree Logic). Kết quả: hoặc "property satisfied" hoặc "counterexample" (execution trace dẫn đến vi phạm).

**Trusted Virtual Machine (TVM)**:
Core innovation của ActivFORMS. TVM là interpreter cho Timed Automata — đọc model, simulate các transitions, và interface với managed system. Key property: TVM thực thi chính xác semantics của Timed Automata, không có approximation. Zero semantic gap giữa verified model và running system.

**SMC Engine (Uppaal SMC)**:
Phiên bản statistical của Uppaal. Thay vì duyệt toàn bộ state space, SMC chạy N simulation runs ngẫu nhiên và thu thập statistics. Sử dụng Sequential Probability Ratio Test (SPRT) để xác định khi nào đã có đủ evidence để kết luận với confidence level cho trước.

**Online Update Manager (OUM)**:
Monitor TVM execution state để phát hiện quiescent states. Khi nhận được update request (new goal, modified model), OUM queue update và chờ quiescent state để apply atomically. Đảm bảo: không có ongoing computation bị interrupted, new model bắt đầu từ consistent state.

### 3.4 Giải Thích Trên Ví Dụ Cụ Thể

Xét hệ thống **DeltaIoT** — mạng cảm biến không dây IoT với 15 nodes định tuyến dữ liệu về gateway:

**Quality goals ban đầu**:
- G1: Packet loss < 10%
- G2: Minimize energy consumption

**Scenario runtime adaptation**:
Tại thời điểm t=100s, Monitor phát hiện packet loss = 12% (vi phạm G1). MAPE-K kích hoạt:

1. **Monitor**: Thu thập packet loss rate = 12%, energy = 85% budget
2. **Analyze**: 12% > 10% → vi phạm G1 → cần thích ứng
3. **Plan**: SMC evaluate 3 options:
   - Option A (tăng transmission power): SMC chạy 1000 simulations → P(satisfy G1) = 0.95, P(satisfy G2) = 0.60
   - Option B (thay đổi routing): P(satisfy G1) = 0.88, P(satisfy G2) = 0.85
   - Option C (reduce packet frequency): P(satisfy G1) = 0.72, P(satisfy G2) = 0.95
   - → Chọn Option A (ưu tiên G1 - safety goal)
4. **Execute**: TVM gửi configuration update tới 3 nodes có packet loss cao nhất

**Scenario runtime goal evolution** (t=500s):
Stakeholder muốn thêm goal mới: G3: latency < 50ms

1. OUM nhận request "add G3"
2. OUM monitor TVM state
3. Tại t=520s: quiescent state detected (không có ongoing transition)
4. OUM atomically update Timed Automata model với G3 specification
5. Hệ thống tiếp tục với 3 goals, không restart

Kết quả: latency giảm xuống 0% vi phạm, nhưng packet loss tăng nhẹ từ 7% lên 9% (vẫn trong ngưỡng), energy tăng ~5%.

### 3.5 Điểm Mới So Với Trước

Ba điểm đột phá:

1. **Zero semantic gap**: TVM là phương pháp đầu tiên loại bỏ hoàn toàn bước model-to-code translation. Verified model IS the running system.

2. **Scalable formal verification tại runtime**: SMC giải quyết state explosion problem bằng statistical approximation — 6× nhanh hơn và 20× ít bộ nhớ hơn exhaustive RQV, với trade-off rõ ràng về accuracy.

3. **Safe runtime goal evolution**: OUM là cơ chế đầu tiên cho phép cập nhật goals của SAS mà không dừng hệ thống, với đảm bảo formal về safety của update.

---

## PHẦN 4 — Abstract (Tiếng Việt)

Self-adaptive systems (SAS) là hệ thống phần mềm tự điều chỉnh hành vi tại runtime để đáp ứng các mục tiêu chất lượng trong môi trường thay đổi. Các phương pháp hiện có để kỹ thuật hóa SAS thiếu nền tảng hình thức xuyên suốt: hoặc không kiểm chứng feedback loop, hoặc gặp state explosion tại runtime, hoặc tạo ra khoảng cách semantic giữa model đã kiểm chứng và code đang chạy. Bài báo này đề xuất ActivFORMS — một phương pháp có cơ sở hình thức bao trùm bốn giai đoạn vòng đời SAS: (1) kiểm chứng exhaustive tại design time bằng Uppaal model checker trên Timed Automata; (2) triển khai qua Trusted Virtual Machine thực thi trực tiếp model, loại bỏ semantic gap; (3) verification và adaptation tại runtime sử dụng Statistical Model Checking — giải quyết state explosion với scalability vượt trội; (4) runtime goal evolution an toàn qua Online Update Manager tại quiescent states. Validation trên case study DeltaIoT (mạng IoT thực tế) cho thấy ActivFORMS giảm 27% energy consumption, xử lý trong <5 giây (so với >30 giây của exhaustive RQV), dùng 20× ít bộ nhớ hơn, và scale được lên 25 thiết bị trong khi RQV gặp state explosion.

---

## PHẦN 5 — Kết Quả Thực Nghiệm

**Dataset:**
**DeltaIoT** — mạng cảm biến không dây thực tế với 10–25 IoT nodes. Quality goals: packet loss < 10%, minimize energy consumption. Đây là case study được phát triển cùng với industrial partner.

**Baselines:**
1. Static approach: cấu hình cố định, không có self-adaptation
2. RQV (Calinescu et al.): exhaustive model checking tại runtime — state of the art trước ActivFORMS

**Kết quả chính:**

| Tiêu chí | Static | RQV | ActivFORMS |
|---|---|---|---|
| Packet loss | Borderline đạt | Đạt | Đạt |
| Energy consumption | Baseline | ~baseline | **Giảm 27%** |
| Thời gian verify (10 nodes) | N/A | >30 giây | **<5 giây** |
| RAM (10 nodes) | N/A | **20× nhiều hơn** | ~100MB |
| Scalability (25 nodes) | N/A | **State explosion** | Vài giây, ~100MB RAM |

**Runtime goal evolution:**
- Thêm latency goal tại runtime: latency violations giảm xuống 0%
- Trade-off chấp nhận được: packet loss tăng nhẹ (~2%), energy tăng ~5%
- Hệ thống không bị restart — tiếp tục phục vụ trong suốt quá trình update

**Ablation:** Không có formal ablation study. So sánh với 2 baselines trên 3 metrics chính.

---

## PHẦN 6 — Hạn Chế & Hướng Nghiên Cứu Tương Lai

**Hạn chế tác giả thừa nhận:**

1. **SMC chỉ cho kết quả ước lượng xác suất**: Không phải đảm bảo chính xác tuyệt đối — có xác suất nhỏ chọn sai adaptation option. Với safety-critical systems, đây là vấn đề nghiêm trọng.

2. **TVM chỉ hỗ trợ Timed Automata**: Chưa tổng quát cho các formalism khác như Petri nets, statecharts, hay probabilistic models.

3. **Single case study**: DeltaIoT là một hệ thống cụ thể — chưa validate với cloud systems, autonomous vehicles, hay other SAS domains.

4. **Overhead của OUM chưa được đánh giá đầy đủ**: Trong hard real-time systems với deadline nghiêm ngặt, overhead của việc detect quiescent states có thể không chấp nhận được.

**Hướng nghiên cứu tiếp theo:**
- Mở rộng TVM hỗ trợ thêm các formalism (Petri nets, statecharts, probabilistic models)
- Kết hợp machine learning với SMC để cải thiện dự đoán quality attributes
- Validation trong các domain khác: cloud autoscaling, autonomous vehicles, healthcare systems
- Formal guarantees cho online evolution (hiện tại chỉ dựa trên quiescent state assumption)
- Hybrid approach: SMC cho runtime decisions + exhaustive MC cho critical properties

---

## PHẦN 7 — Kết Luận

ActivFORMS giải quyết một trong những thách thức khó nhất trong self-adaptive systems engineering: đảm bảo formal guarantees xuyên suốt toàn bộ vòng đời — từ design đến runtime evolution — mà không gặp scalability problems. Ba contribution chính — TVM (zero semantic gap), SMC (scalable runtime verification), OUM (safe goal evolution) — tạo nên một phương pháp end-to-end đầu tiên bao trùm cả 4 giai đoạn SAS lifecycle. Validation trên DeltaIoT cho thấy kết quả ấn tượng về energy efficiency (giảm 27%), scalability (20× ít RAM hơn RQV), và runtime goal evolution (không restart).

**Tóm lại, điểm đáng chú ý nhất của bài báo này là** triết lý "model IS code" được hiện thực hóa qua Trusted Virtual Machine — một sự thay đổi paradigm căn bản từ "verify rồi translate" sang "verify rồi execute directly". Điều này không chỉ giải quyết semantic gap mà còn mở ra khả năng runtime adaptation mà traditional compile-and-deploy không bao giờ đạt được.
