# Chương 4 — Chiều Agent (Agent Dimension)

> Tổng hợp lại lý thuyết từ `agentView.md`, dựa trên kiến trúc **BDI (Belief–Desire–Intention)** áp dụng trong lập trình đa tác tử (MAOP).

## 1. Tổng quan

Một agent trong kiến trúc BDI có ba thành phần cốt lõi:

- **Beliefs (niềm tin)**: những gì agent tin là đúng về trạng thái hiện tại của môi trường, của các agent khác và của tổ chức.
- **Goals (mục tiêu)**: các trạng thái tương lai mà agent (hoặc người thiết kế nó) mong muốn đạt được.
- **Reasoning → Action**: agent suy luận từ beliefs + goals để quyết định **hành động (action)** tốt nhất, làm thay đổi trạng thái môi trường.

Một loại hành động đặc biệt là **communicative action** (hành động giao tiếp) — dựa trên **speech act theory** — cho phép agent gửi thông điệp tới agent khác. Mỗi thông điệp gồm:

1. Nội dung (content): tri thức, sở thích, hay know-how.
2. **Performative verb** (động từ ngôn hành): ví dụ `tell`, `ask`, `achieve` — xác định mục đích của thông điệp và agent nhận sẽ xử lý nội dung đó như thế nào.

| Performative | Ý nghĩa khi agent nhận thông điệp                                                   |
| ------------ | ----------------------------------------------------------------------------------------- |
| `tell`     | Agent gửi muốn agent nhận**tin rằng** nội dung là đúng                      |
| `ask`      | Agent gửi muốn agent nhận**trả lời** xem nội dung có đúng không           |
| `achieve`  | Agent gửi muốn agent nhận**hành động** để làm nội dung trở thành đúng |

## 2. Các khái niệm trừu tượng của Agent (Agent Abstractions)

### 2.1 Belief (Niềm tin)

- Biểu diễn dưới dạng **literal** (như logic programming), có thể kèm **annotation** trong dấu `[...]`.
- Annotation quan trọng nhất: `source` — cho biết nguồn gốc thông tin (`percept`, tên một agent khác, hoặc `self` khi agent tự ghi chú — gọi là **mental note**).
- Belief nhắc ta rằng thông tin agent có **có thể sai hoặc không đầy đủ** (môi trường thay đổi, cảm biến lỗi, không quan sát được toàn bộ môi trường).

### 2.2 Goal (Mục tiêu)

- Biểu diễn giống belief nhưng có tiền tố `!` (achievement goal).
- Gắn với **proactive behavior** (hành vi chủ động): agent hành động để đạt trạng thái mới.
- Nguồn của goal (annotation `source`) cho biết agent nào đã **ủy quyền (delegate)** goal đó.

**Hai cách dùng goal:**

| Cách dùng                       | Mô tả                                                                                                                                                              |
| --------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Khai báo (declarative)** | Goal gắn với một mệnh đề mà agent hiện chưa tin là đúng; đạt goal nghĩa là agent tin mệnh đề đó đã đúng. Ví dụ`!finished(cake)`.        |
| **Thủ tục (procedural)**  | Goal chỉ đơn thuần là tên cho một chuỗi hành động cần thực hiện; coi như hoàn thành khi hành động đã chạy xong, bất kể kết quả thực tế. |

### 2.3 Event (Sự kiện)

- Đại diện cho **thay đổi** trong belief hoặc goal của agent (thêm/xoá).
- Gắn với **reactive behavior** (khi belief thay đổi) và **proactive behavior** (khi có goal mới).
- Chính các event này kích hoạt agent thực thi kế hoạch (plan).

### 2.4 Plan (Kế hoạch) — "biết làm" (know-how)

Một plan gồm 3 phần:

| Phần                                     | Vai trò                                                                                                                          |
| ----------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| **Trigger** (sự kiện kích hoạt) | Xác định goal/reaction mà plan này xử lý; dùng để tìm**relevant plans** (kế hoạch liên quan) khớp với event |
| **Context** (ngữ cảnh)            | Điều kiện (thường là hội các belief) để plan**applicable** (khả thi) tại thời điểm hiện tại                |
| **Body** (thân kế hoạch)         | Chuỗi hành động / sub-goal mà agent thực hiện để xử lý event                                                           |

- Tập hợp tất cả plan của agent = **plan library** (thư viện kế hoạch) = **know-how** của agent.
- Nhiều plan có thể cùng trigger nhưng khác context → agent chọn plan phù hợp hoàn cảnh.

### 2.5 Action (Hành động)

| Loại                                | Mô tả                                                                                                                                                                                  |
| ------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **External** (bên ngoài)     | Thực hiện qua**effectors/actuators**, thay đổi môi trường thật (vd: điều khiển cánh tay robot)                                                                         |
| **Internal** (nội tại)       | Chạy nguyên tử (atomic) trong kiến trúc agent, thường để gọi code cũ hoặc thay đổi**mental state** (trạng thái tinh thần) — không được block reasoning cycle |
| **Communicative** (giao tiếp) | Gửi thông điệp gồm: người nhận, performative verb, nội dung                                                                                                                     |

### 2.6 Các cấu trúc bổ sung

- **Test goal** (`?goal`): truy vấn belief base để lấy thông tin mới nhất; nếu không có sẵn, có thể có plan để agent tìm cách lấy thông tin đó.
- **Inference rules** (luật suy diễn): giống luật Prolog, dùng để suy ra belief mới từ belief đã có (vd: suy ra "bánh chay" từ "không chứa sản phẩm động vật").

## 3. Thực thi Agent (Agent Execution) — Reasoning Cycle

Agent chạy lặp lại **reasoning cycle** (chu trình suy luận): bắt đầu bằng cảm nhận môi trường (percept), kết thúc bằng chọn 1 hành động thực thi.

### 3.1 Các cấu trúc dữ liệu chính

| Cấu trúc                  | Vai trò                                                                                                                |
| --------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| **Percept**           | Thông tin ký hiệu hoá về trạng thái môi trường (từ cảm biến/camera), ảnh hưởng trực tiếp đến belief |
| **Message**           | Giao tiếp bất đồng bộ nhận từ agent khác                                                                        |
| **Belief base**       | Toàn bộ thông tin agent đang giữ (annotate nguồn gốc: percept / agent khác / self)                              |
| **Event queue**       | Hàng đợi các event đã xảy ra (belief/goal thay đổi)                                                            |
| **Plan library**      | Tập hợp toàn bộ plan (có thể thay đổi lúc runtime — trao đổi qua giao tiếp hoặc AI planning)              |
| **Set of intentions** | Tập các**intention** đang theo đuổi                                                                          |

### 3.2 Từ Event đến Action

1. **Relevant plans**: các plan có trigger khớp với event đang xét.
2. **Applicable plans**: trong số plan liên quan, các plan có context đúng với belief hiện tại.
3. **Intended means**: bản sao của 1 plan (đã chọn) từ plan library, agent cam kết thực hiện.
4. **Intention**: một **stack** các intended means (vì plan có thể cần đạt sub-goal trước khi tiếp tục) — mỗi intention là một "tiêu điểm chú ý" (focus of attention) riêng biệt của agent.

### 3.3 Ba hàm lựa chọn (Selection Functions)

| Hàm chọn                                | Vai trò                                                 | Chính sách mặc định                          |
| ----------------------------------------- | -------------------------------------------------------- | ------------------------------------------------- |
| **Event selection function**        | Chọn 1 event từ event queue để xử lý mỗi chu kỳ  | FIFO                                              |
| **Means/Option selection function** | Chọn 1 plan trong số các applicable plans             | Plan đầu tiên theo thứ tự trong plan library |
| **Intention selection function**    | Chọn 1 intention để thực thi hành động tiếp theo | Round-robin (công bằng giữa các intention)    |

- **Suspended intention** (intention bị treo): xảy ra khi đang chờ xác nhận thực thi action, hoặc đang chờ chọn plan cho 1 sub-goal.

### 3.4 Bốn bước của Reasoning Cycle

| Bước                            | Nội dung                                                                                                                                                        |
| --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Nhận thức                    | Lấy percept + message hiện có; percept cập nhật belief, message có thể ảnh hưởng belief/goal/plan; mọi thay đổi → event queue                      |
| 2. Chọn event & lọc plan        | Event selection function chọn 1 event → tìm relevant plans → kiểm tra context → được applicable plans                                                   |
| 3. Chọn plan                     | Means selection function chọn 1 intended means từ applicable plans → thêm vào intention mới hoặc intention đang có (nếu là sub-goal)                  |
| 4. Chọn intention & hành động | Intention selection function chọn 1 intention → thực thi hành động tiếp theo; cập nhật belief base / event queue / set of intentions (có thể suspend) |

## 4. Bảng tổng hợp khái niệm chính

| Khái niệm       | Ký hiệu / cú pháp               | Ý nghĩa                                       |
| ----------------- | ----------------------------------- | ----------------------------------------------- |
| Belief            | `finished(cake)[source(percept)]` | Niềm tin hiện tại, có annotation nguồn     |
| Achievement goal  | `!finished(cake)`                 | Trạng thái muốn đạt (chưa tin là đúng) |
| Test goal         | `?phone(Client,N)`                | Truy vấn belief base                           |
| Event (belief +)  | `+finished(cake)`                 | Belief mới được thêm                       |
| Event (belief −) | `-finished(cake)`                 | Belief bị xoá                                 |
| Event (goal +)    | `+!finished(cake)`                | Goal mới được kích hoạt                   |
| Plan              | trigger : context <- body           | Công thức hành động cho 1 event            |
| Intention         | stack các intended means           | Một dòng thực thi/tiêu điểm chú ý       |
| Performative      | `tell`, `ask`, `achieve`      | Mục đích giao tiếp                          |

## 5. Ghi chú nguồn gốc lý thuyết

- Nền tảng triết học/lý thuyết: Bratman et al. (1988) — mô hình BDI; Dennett (1987) — intentional stance; Bratman (1987) — vai trò của intention trong suy luận thực hành.
- Georgeff & Lansky (1987) — reactive planning → PRS (Georgeff & Ingrand 1989), nền tảng BDI thực thi đầu tiên.
- Logic hình thức: Cohen & Levesque (1990), Wooldridge, Singh...
- Ngôn ngữ lập trình agent: AgentSpeak(L) (Rao 1996), 3APL, Jason (Bordini et al. 2007) — nền tảng của JaCaMo dùng trong sách.
