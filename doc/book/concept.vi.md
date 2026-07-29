# Chương 2 — Tổng quan về Lập trình Hướng Tác tử Đa tác nhân (MAOP)

> Tổng hợp lại lý thuyết từ `concept.md` — chương đặt nền cho toàn bộ cuốn sách, giới thiệu hệ đa tác tử (MAS) và cách tiếp cận MAOP với 3 chiều: Agent, Environment, Organization.

## 1. Hệ đa tác tử (Multi-Agent Systems) là gì

MAS là một **paradigm** (hệ hình) để mô hình hoá và xây dựng hệ thống phức tạp — tức là một tập hợp concept, kỹ thuật, công nghệ và phương pháp luận.

| Ngữ cảnh sử dụng | Mục đích | Ví dụ |
|---|---|---|
| **Simulation** (mô phỏng) | Mô tả & mô phỏng hệ thống phức tạp có sẵn (tự nhiên/nhân tạo) để phân tích tính chất | Xe = agent, thành phố/biển báo = environment, trong mô phỏng giao thông |
| **Engineering** (kỹ nghệ — trọng tâm của sách) | Thiết kế & xây dựng hệ thống/ứng dụng mới | Xe tự hành trong smart city, tương tác với dịch vụ số của hạ tầng thông minh |

**Ý tưởng cốt lõi**: một hệ thống phức tạp được mô hình hoá (và thiết kế, lập trình) như **một tổ chức các agent tự trị**, được đặt trong và tương tác với một **môi trường (logic)**.

| Thành phần | Vai trò |
|---|---|
| **Agent** | Thực thể ra quyết định — tự trị theo đuổi goal, đóng gói 1 luồng điều khiển logic riêng, tự quyết định hành vi & tương tác |
| **Environment** | Bối cảnh nơi agent hành động — agent chỉ quan sát/tác động được **một phần** môi trường tại mỗi thời điểm, dù môi trường có thể lớn và phân tán |
| **Organization** | Nắm bắt tường minh các khía cạnh đặc trưng cho nhiệm vụ/chức năng/hành vi/tính chất của **toàn hệ thống** |

Các agent giao tiếp qua **agent communication language (ACL)** ở mức cao và hợp tác để đạt goal chung.

### 1.1 MAS so với các paradigm khác

| Tiêu chí | Object (OOP) | Actor | Agent |
|---|---|---|---|
| Ẩn giấu trạng thái nội bộ (information hiding) | Có | Có | Có |
| Cách giao tiếp | Method call (đồng bộ) | Message passing | Message passing (dựa trên ACL) |
| Có luồng điều khiển riêng? | **Không** — thụ động (passive) | Có | Có |
| Khi giao tiếp, control có chuyển giao? | Có (như procedure call) | Không — control giữ nguyên bên gửi | Không — control luôn được đóng gói bên trong agent |
| Chủ động hay chỉ phản ứng? | — | **Chỉ phản ứng (reactive)** — xử lý xong message thì idle | **Chủ động (proactive)** — hành động để đạt goal kể cả khi không có message |
| Môi trường có là abstraction hạng nhất? | Không (mọi thứ là object) | Không (mọi thứ là actor) | **Có** — tách biệt phần ra quyết định (agent) khỏi phần bị điều khiển (environment) |

→ Điểm khác biệt cốt lõi của agent-oriented modeling: **không mô hình hoá mọi thứ thành agent** — có sự **phân tách rõ ràng (separation of concerns)** giữa phần đóng gói việc ra quyết định và phần đại diện cho thực thể cần được điều khiển/quan sát.

## 2. Lập trình Hướng Tác tử Đa tác nhân (MAOP)

Về nguyên tắc, công nghệ lập trình nào cũng có thể dùng để cài đặt MAS — nhưng rủi ro là rơi vào cách hiểu **"agent-centred"**, trong đó environment và organization chỉ tồn tại ngầm trong "đầu" của agent (không tường minh).

**MAOP** giải quyết việc này bằng cách cổ vũ sử dụng **trừu tượng lập trình hạng nhất (first-class programming abstractions)** cho 3 chiều:

| Chiều | Câu hỏi trọng tâm | Đặc trưng chính |
|---|---|---|
| **Agent dimension** | Ai ra quyết định, và ra quyết định thế nào? | **Autonomy** (tự trị) |
| **Environment dimension** | Cái gì được dùng/điều khiển để đạt goal? | **Situatedness** (tính tại chỗ) |
| **Organization dimension** | Các agent phối hợp & bị ràng buộc thế nào? | **Coordination** (điều phối) & **Regulation** (điều tiết) |

### 2.1 Agent dimension

- Khái niệm trung tâm: **goal** — biểu diễn tường minh trạng thái tương lai agent muốn đạt được; then chốt cho **autonomy** và **proactivity**.
- Agent tự quyết định (rational choice) goal nào nên theo đuổi lúc runtime, và phương tiện nào để đạt goal đó.

**Ba năng lực làm nên autonomy:**

| Năng lực | Ý nghĩa |
|---|---|
| **Proactivity** (chủ động) | Tự khởi xướng hành động để đạt goal |
| **Reactivity** (phản ứng) | Nhanh chóng thích nghi hành vi theo sự kiện cảm nhận từ môi trường |
| **Social ability** (khả năng xã hội) | Giao tiếp & hợp tác với agent khác |

> Ở mức tổ chức, autonomy nghĩa là **điều khiển phi tập trung**: agent bị **ảnh hưởng** nhưng không bị **áp đặt tuyệt đối** bởi norm hay agent khác — agent tự trị có thể chọn không tuân theo norm.

### 2.2 Environment dimension

- Khái niệm trung tâm: **workspace** — vùng topo/ký hiệu của môi trường, chứa **artifact** và agent.
- **Artifact**: đại diện cho 1 tài nguyên (thật hoặc khái niệm) qua tập **operations** (agent dùng để hành động) và **properties** (agent quan sát để tạo belief).
- Artifact **không** tự trị, không chủ động — khác hẳn agent.
- Artifact có thể được agent **tạo/huỷ động** → giúp môi trường **module hoá** và **linh hoạt**.
- Đây chính là điều làm agent **situated** (tại chỗ): agent được đặt trong 1 bối cảnh cung cấp tập hành động để tác động, và phơi bày trạng thái/sự kiện quan sát được.

### 2.3 Organization dimension

- Khái niệm trung tâm: **group** — cho hệ thống 1 **cấu trúc xã hội**, hỗ trợ định nghĩa hành vi phối hợp kỳ vọng cũng như quyền & nghĩa vụ của agent.

| Khái niệm | Vai trò |
|---|---|
| **Role** | Xác định tương tác & quan hệ trong 1 group; tham gia định nghĩa quyền/nghĩa vụ qua norm |
| **Norm** | Biểu diễn quyền & nghĩa vụ gắn với role |
| **Organizational goal** | Trạng thái kỳ vọng, hợp thành **social plan** |
| **Social plan** | Cây phân rã goal, nằm trong **social scheme** |
| **Social scheme** | Được thực thi dưới trách nhiệm của **group** |

- Đặc trưng chính: **coordination** (quản lý phụ thuộc giữa hoạt động của nhiều agent để đạt điều họ muốn — cá nhân hoặc tập thể) và **regulation** (kiềm chế autonomy của agent tham gia, chủ yếu qua norm — luật xã hội mà agent được kỳ vọng tuân theo, có thể bị phạt nếu không tuân theo).

> Toàn bộ các trừu tượng của 3 chiều được **platform** (JaCaMo) duy trì **lúc runtime** → hệ thống có thể thay đổi và tái tổ chức linh hoạt: tạo instance tổ chức/artifact lúc chạy, agent gia nhập/rời tổ chức qua việc chọn role.

## 3. Bảng tổng hợp trừu tượng chính theo từng chiều

| Chiều | Trừu tượng trung tâm | Các khái niệm liên quan |
|---|---|---|
| Agent | **Goal** | belief, action |
| Environment | **Workspace** | artifact, operation, property |
| Organization | **Group** | role, norm, organizational goal, social plan, social scheme |

## 4. Góc nhìn tích hợp (Integrated View)

Một MAS hoàn chỉnh = tập agent + 1 environment + tập organization **tương tác** với nhau. Các **quan hệ động (dynamic relations)** nối 3 chiều lại thành 1 vòng khép kín khi hệ thống đang chạy:

| Quan hệ | Giữa hai chiều | Cơ chế |
|---|---|---|
| **Communicate** | Agent ↔ Agent | Dựa trên **speech act theory**: giao tiếp là 1 hành động làm thay đổi mental state (belief/goal) của cả bên gửi và bên nhận |
| **Perceive / Act** | Agent ↔ Environment | Agent quan sát artifact (trực tiếp) và tác động lên artifact để đổi trạng thái |
| **Indirectly** | Agent ↔ Agent (qua Environment) | 1 agent hành động trên artifact, agent khác cảm nhận thay đổi đó → tương tác gián tiếp qua môi trường chung |
| **Count as** | Environment → Organization | Thay đổi trạng thái môi trường có thể **"tính là"** thay đổi trạng thái tổ chức (vd: 1 agent hoàn thành task → tổ chức yêu cầu agent phụ thuộc hành động tiếp) |
| **Empower** | Organization → Environment | Ngược lại, tổ chức có thể **trao quyền** cho phần tử môi trường để kiểm soát/điều tiết hành động hay cảm nhận của agent (vd đèn giao thông ở ngã tư) |
| **Regulate** | Organization → Agent | Tổ chức kiểm soát hoạt động của agent (qua norm) |
| **Coordinate** | Organization → Agent | Tổ chức quản lý phụ thuộc giữa các hoạt động do agent thực hiện |

> Lưu ý quan trọng: quan hệ **regulate/coordinate** chỉ có hiệu lực nếu agent **tự nguyện chọn tham gia** vào 1 hay nhiều tổ chức đang tồn tại — vì agent luôn tự trị.

## 5. MAOP giải quyết các thách thức của hệ thống hiện đại thế nào

| Thách thức | Cách MAOP giải quyết |
|---|---|
| **Autonomy** | Tách biệt rõ: agent (tự trị) ↔ artifact (phi tự trị); organization cung cấp trừu tượng điều phối/điều tiết nhắm vào các thực thể tự trị, "thuần hoá" autonomy để hệ hoạt động nhất quán |
| **Decentralization & Distribution** | Giao tiếp trực tiếp (speech act) + gián tiếp (qua environment) tạo **loose coupling**; organization cho lập trình khai báo (declarative) các mẫu điều phối/điều tiết **mà không cần điểm điều khiển trung tâm**; các module (agent, workspace, artifact, organization, group, scheme) có thể **phân tán** trên nhiều máy |
| **Openness** | Agent, artifact, workspace, organization/group/scheme đều có thể được **tạo/khám phá/huỷ lúc runtime** bởi chính agent → hệ thống thích nghi được với agent mới, agent lỗi, artifact mới, norm mới mà không cần biết trước lúc thiết kế |
| **Heterogeneity** | Mỗi chiều đóng vai trò 1 điểm **interoperability**: **Agent–Agent** (ngôn ngữ giao tiếp chung), **Agent–Organization** (biểu diễn tổ chức để agent đọc/suy luận về norm), **Agent–Environment** (usage manual tường minh mô tả action/percept của artifact) |
| **Adaptability** | 3 chiều ứng với 3 quy mô thời gian: ngắn hạn (action/percept ở environment), dài hạn (quản lý goal ở agent), trung hạn (chiến lược/chính sách ở organization); kiến trúc **BDI** cho phép chọn plan khác nhau theo context và ngắt plan giữa chừng khi thất bại/môi trường đổi |
| **Explainability** | Goal tường minh ở agent → giải thích được lựa chọn hành động của từng agent; biểu diễn tường minh về cấu trúc điều phối/điều tiết/quyền hạn ở organization → giải thích được hành vi tổng thể của hệ |
| **AI integration** | Kiến trúc BDI cho phép tích hợp có kỷ luật các kỹ thuật AI (planning, reinforcement learning) — ở mức cá nhân (agent) hoặc mức hệ thống (multi-agent planning/learning); AI cũng có thể được gói thành artifact (dịch vụ, vd nhận dạng giọng nói) |

## 6. Bảng tổng hợp toàn bộ khái niệm chương 2

| Khái niệm | Ý nghĩa |
|---|---|
| MAS | Hệ thống = tổ chức các agent tự trị, tại chỗ trong 1 môi trường chung |
| MAOP | Cách tiếp cận lập trình MAS bằng trừu tượng hạng nhất theo 3 chiều |
| Agent dimension | goal, belief, action — nơi autonomy được định nghĩa |
| Environment dimension | workspace, artifact, operation, property — nơi agent situated |
| Organization dimension | group, role, norm, organizational goal, social plan/scheme — nơi coordination & regulation được định nghĩa |
| Dynamic relations | communicate, perceive/act, indirectly, count as, empower, regulate, coordinate |
| Interoperability | Agent–Agent, Agent–Organization, Agent–Environment |

## 7. Ghi chú nguồn gốc lý thuyết

- Hình mẫu 3 chiều MAOP: khởi nguồn từ **Vowels decomposition paradigm** (Demazeau 1995); tổng quan MAOP: Boissier et al. (2013, 2019).
- Sách nền tảng về agent/MAS nói chung: Weiss (1999), Wooldridge (2009), Ferber (1999); góc nhìn game-theoretic: Shoham & Leyton-Brown (2008).
- Về mô hình hoá/thiết kế hướng agent: Sterling & Taveter (2009), Padgham & Winikoff (2004); tổng hợp nhiều cách tiếp cận lập trình đa tác tử: Bordini et al. (2009).
- Nền tảng platform cụ thể: Jason (Bordini et al. 2007), JADE (Bellifemine et al. 2007).
- Các bài báo tổng quan quan trọng: Jennings (2001) — giới thiệu paradigm agent; Wooldridge & Jennings (1995) — lý thuyết & thực hành intelligent agents.
- Hội nghị chính của cộng đồng: AAMAS (từ 2002, hợp nhất Autonomous Agents, ICMAS, ATAL); các workshop lập trình/kỹ nghệ MAS (ProMAS, AOSE, DALT) hợp nhất thành **EMAS** từ 2012.
