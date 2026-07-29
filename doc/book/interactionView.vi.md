# Chương 10 — Tích hợp với Công nghệ khác (Integration / Interaction)

> Tổng hợp lại lý thuyết từ `interactionVIew.md`: cách MAOP (qua JaCaMo) tích hợp với thư viện, framework, nền tảng khác, và các ứng dụng trong lĩnh vực chủ đạo (mobile, web, robot), cũng như tương tác giữa các nền tảng MAS khác nhau.

## 1. Hai hướng tích hợp cơ bản

| Hướng | Cách thực hiện | Ý nghĩa khái niệm |
|---|---|---|
| **Agent extension** (mở rộng agent) | Mở rộng kiến trúc/tập internal action của agent để bọc (wrap) công nghệ ngoài | Công nghệ trở thành **năng lực cá nhân (personal capability)** của agent |
| **Artifact embedding** (nhúng vào artifact) | Thiết kế artifact mới bọc công nghệ ngoài | Công nghệ trở thành **tài nguyên/công cụ trong môi trường**, có thể chia sẻ với agent khác |

Lựa chọn hướng nào phụ thuộc loại công nghệ:

| Loại công nghệ | Đặc điểm | Nên tích hợp bằng |
|---|---|---|
| **Library** (thư viện) | Chức năng qua API/module, không có vấn đề concurrency/control | Internal action (nếu stateless, nhẹ) **hoặc** artifact (nếu cần chia sẻ, tính toán nặng/có trạng thái) |
| **Framework** | Có kiến trúc điều khiển riêng (control architecture), thường event-driven | Artifact (để cô lập rõ giữa logic thực thi của framework và MAS) |
| **Platform** (nền tảng ứng dụng) | Định nghĩa cấu trúc ứng dụng riêng | Tích hợp MAS **như 1 thành phần** của platform, hoặc chạy MAS **process riêng** rồi bắc cầu qua IPC |

## 2. Tích hợp Thư viện (Libraries)

### 2.1 Internal action vs. Artifact — khi nào dùng gì

| Tiêu chí | Internal action | Artifact |
|---|---|---|
| Luồng thực thi | Đồng bộ, trong chính thread của reasoning cycle | Bất đồng bộ, thread riêng của environment runtime |
| Hiệu năng | Tốt hơn (không context switch, chi phí ~ gọi hàm) | Có overhead nhưng không chặn reasoning cycle |
| Phù hợp | Hàm chức năng nhẹ, không trạng thái (vd: parse JSON) | Tính toán nặng/dài hạn, hoặc tài nguyên cần **chia sẻ** giữa nhiều agent (vd: FFT Calculator) |
| Rủi ro nếu chọn sai | Action nặng sẽ **chặn reasoning cycle**, giảm khả năng phản ứng (reactivity) của agent | — |

Ví dụ minh hoạ trong sách:
- **JSON library** → gói thành internal action (`json_to_list`) vì là hàm thuần, không trạng thái.
- **FFT (Fast Fourier Transform)** → gói thành artifact (`FFTCalculator`) vì tính toán nặng, dùng chung.
- Để thao tác object Java trong Jason: dùng **JavaLibrary** (`cartago.new_obj`, `cartago.new_array`, `cartago.invoke_obj`).

### 2.2 Thư viện có luồng riêng (own threads) — ví dụ RabbitMQ

- Thư viện phức tạp hơn có thể dùng **lập trình bất đồng bộ** với thread riêng (vd Message-Oriented-Middleware như RabbitMQ).
- Vấn đề: callback được gọi bởi **thread của thư viện**, không phải thread của environment → không được cập nhật trạng thái artifact trực tiếp.
- Giải pháp: API `beginExtSession` / `endExtSession` — cho phép thread ngoài **an toàn** cập nhật observable property và sinh signal.

## 3. Tích hợp Framework — ví dụ JavaFX

- Framework GUI (như JavaFX) có kiến trúc event-driven, chạy trên 1 thread riêng cho GUI.
- Artifact (vd `MainWindowArtifact`) đóng 2 vai trò:
  1. Cung cấp **giao diện mức cao** cho agent (vd observable property `button`: `pressed`/`not_pressed`).
  2. Là **cầu nối (bridge)** ẩn đi cơ chế nội bộ của framework (callback, thread GUI...) khỏi agent.
- Điểm mấu chốt: agent **không** phải tiếp xúc với cơ chế framework mức thấp — sự kiện GUI được biểu diễn ở **mức trừu tượng của agent**.

## 4. Tích hợp MAS vào Platform

| Cách | Cơ chế |
|---|---|
| MAS là 1 thành phần của platform (Java-based) | API `jacamo.infra.JaCaMoLauncher` — sinh (spawn) MAS theo chương trình từ bất kỳ chương trình Java/JVM nào |
| MAS chạy process riêng, bắc cầu qua IPC | Dùng **boundary artifact** để ẩn/đóng gói cơ chế IPC (vd socket) tương tác với platform ngoài |

### 4.1 Environment Interface Standard (EIS)

- Cung cấp "glue code" kết nối **bất kỳ agent platform nào hỗ trợ EIS** với **bất kỳ environment nào** implement EIS (game, simulator, robot...).
- Bên agent: API kết nối tới controllable entity trong environment.
- Bên environment: cho phép bất kỳ agent platform nào hỗ trợ EIS kết nối tới, không phụ thuộc platform cụ thể.

## 5. Ứng dụng trong các lĩnh vực chủ đạo

### 5.1 Mobile & Wearable Apps — JaCa-Android

- Ứng dụng: **personal assistant** (trợ lý cá nhân) — lập lịch, nhắc nhở, tìm kiếm/chia sẻ thông tin, hỗ trợ đàm phán...
- **JaCa-Android**: mở rộng/chuyên biệt hoá JaCaMo để chạy trên Android — không chỉ là "port" mà là **bản thiết kế (blueprint)** để xây app di động như 1 hệ đa tác tử.

| Khái niệm Android | Vai trò |
|---|---|
| **Activity** | Điểm vào tương tác người dùng (1 màn hình UI) |
| **Service** | Điểm vào chạy nền |
| Kiến trúc điều khiển | Event-driven, chỉ 1 activity foreground chạy trên 1 thread (event loop) |

Thư viện artifact đặc thù của JaCa-Android:

| Loại artifact | Vai trò |
|---|---|
| Artifact biểu diễn Activity | Chỉ mô hình **UI**, phần điều khiển vẫn nằm ở agent quan sát/sử dụng activity đó |
| Artifact cảm biến/actuator | Vd `BatteryService`, `GPSService`, `SMSService` — lấy thông tin ngữ cảnh người dùng |
| Artifact tương tác activity/app khác | Kích hoạt và tương tác app/activity khác trên thiết bị |

→ So với Android framework gốc, JaCa-Android cung cấp **mức trừu tượng cao hơn**, đơn giản hoá phát triển trợ lý cá nhân dưới dạng agent Jason dùng artifact để quan sát/tương tác với người dùng.

### 5.2 Web Technologies

Hai chiều tích hợp ở "enabling level" (mức cho phép kết nối, chưa tính mức semantic web):

| Chiều | Vai trò |
|---|---|
| **Client side** | Agent khai thác dịch vụ/ứng dụng web có sẵn |
| **Server/Service side** | Agent dùng để hiện thực dịch vụ/ứng dụng web |

**Client side**: dùng boundary artifact với 2 chức năng:
1. Ẩn chi tiết kỹ thuật web (invocation semantics...).
2. Bắc cầu định dạng dữ liệu trao đổi cho phù hợp phía agent.

- Ví dụ: artifact REST dùng Apache HTTPComponents; hoặc artifact mức domain hơn (vd `MapArtifact` bọc Google Map API) — cho phép **tái sử dụng cùng interface artifact** dù đổi API web bên dưới.
- Lưu ý khái niệm: dùng **artifact** (không dùng internal action) vì internal action nên chỉ ảnh hưởng trạng thái **nội tại của agent**, không phải môi trường — trừ khi dịch vụ web đó về bản chất là "bộ nhớ phụ trợ" của agent.

**Server/Service side**: boundary artifact làm trung gian giữa request người dùng web và agent xử lý:
- Artifact cung cấp `acceptXXX` operations để cấu hình request cần phục vụ, báo qua **signal**.
- Operation `sendResponse` để trả lời và đóng request.
- Agent phản ứng theo tín hiệu và xử lý logic nghiệp vụ.

**MAOP và Web of Things (góc nhìn thứ 3)**:
- Thay vì đặt agent/dịch vụ song song rồi bắc cầu, coi **web là chất keo (glue) nền tảng** kết nối MỌI thực thể trong MAS (agent, artifact, organization...).
- Then chốt: coi **environment là abstraction hạng nhất** trong MAS → khi đó web không chỉ là transport layer cho message giữa agent, mà là **application layer** hỗ trợ mọi tương tác qua trung gian môi trường (vd theo W3C Web of Things Thing Description).

### 5.3 Robotic Integration — ROS / Jason-ROS

- ROS (Robot Operating System) cho truy cập phần cứng qua **topic** trừu tượng: nghe (subscribe) để lấy thông tin, publish để điều khiển.

| Cách tích hợp | Mô tả |
|---|---|
| Artifact dịch topic ↔ observable property/operation | Cách thông thường, không đổi cách agent perceive/act |
| Tuỳ biến kiến trúc agent (Jason-ROS) | Ghi đè `act` và `perceive`, cho phép kiểm soát/tối ưu sâu hơn |

Ví dụ Turtle Bot: `pose` (perception), `cmd_vel` (action — vận tốc/hướng), `set_pen` (action — màu vẽ).

- Cấu hình bằng file: ánh xạ topic ROS ↔ belief (perceive) và action ↔ publish topic (act).
- Từ góc nhìn lập trình agent: **không có gì khác biệt** — vẫn dùng belief/action như thường; mọi action qua custom architecture được coi là **external action** (thực thi bất đồng bộ, giống operation trên artifact).
- Đánh đổi: kiểm soát nhiều hơn (không cần mô hình concurrency của CArtAgO) nhưng **effort cao hơn** — vd cần tự hiện thực **belief update function (BUF)**.

## 6. Tích hợp giữa các nền tảng MAS khác nhau (Interoperability)

Hai hướng chính để agent/MAS viết bằng công nghệ khác nhau cùng làm việc (hệ thống của các hệ thống — system of systems):

| Hướng | Cơ chế | Vai trò của Ontology |
|---|---|---|
| **1. Chung ngôn ngữ giao tiếp (ACL)** | FIPA ACL, KQML — định nghĩa performative verb (communicative act) và ngữ nghĩa (vd `ask-one`). JaCaMo hỗ trợ cả 2 (FIPA ACL qua tích hợp JADE) | Cần **ontology chung** để agent hiểu nội dung message, không chỉ chung cú pháp |
| **2. Chung môi trường chia sẻ** | CArtAgO — thiết kế để dùng với nhiều mô hình/công nghệ agent khác nhau, có cầu nối (bridge) riêng cho từng ngôn ngữ | Cần ontology chung về **interface & chức năng artifact** — ghi trong "artifact manual" (mô hình A&A) |
| **3. EIS (bổ sung)** | Cung cấp mô hình chung cho **interface truy cập** môi trường (không ràng buộc metamodel môi trường cụ thể) | — |

### 6.1 Ontology & Agent

- **KIF** (Knowledge Interchange Format, Stanford AI Lab): ngôn ngữ trao đổi tri thức, dùng làm ngôn ngữ mô tả nội dung message trong ACL (đặc biệt KQML).
- **Semantic Web** (RDF, OWL): định nghĩa ontology chung, quan trọng để đưa MAS giao tiếp lên World Wide Web.
- Trong lập trình agent: ontology dùng để (a) định nghĩa nội dung message/giao thức tương tác, và (b) biểu diễn tri thức nội bộ agent (belief trong mô hình BDI). Ví dụ: **JASDL** (Klapiscak & Bordini 2009) mở rộng Jason dùng OWL để biểu diễn belief.

## 7. Bảng tổng hợp toàn bộ khái niệm

| Khái niệm | Ý nghĩa |
|---|---|
| Agent extension | Mở rộng agent (internal action/kiến trúc) để bọc công nghệ ngoài |
| Artifact embedding | Bọc công nghệ ngoài bằng artifact mới trong môi trường |
| beginExtSession/endExtSession | Cho phép thread ngoài an toàn cập nhật trạng thái artifact |
| JaCaMoLauncher | Sinh 1 MAS theo chương trình từ ứng dụng Java/JVM khác |
| EIS | Chuẩn giao diện kết nối agent platform ↔ environment bất kỳ |
| JaCa-Android | JaCaMo chuyên biệt cho Android, mô hình app di động như MAS |
| Web client-side artifact | Bọc + bắc cầu dữ liệu để agent dùng dịch vụ web |
| Web server-side artifact | Bọc cơ chế nhận request để agent hiện thực dịch vụ web |
| Web of Things | Web là glue layer kết nối mọi thực thể MAS qua environment |
| Jason-ROS | Kiến trúc agent tuỳ biến để tích hợp trực tiếp với ROS |
| ACL (FIPA/KQML) | Ngôn ngữ giao tiếp chung giữa agent khác công nghệ |
| Ontology (KIF/RDF/OWL) | Ngữ nghĩa chung cho nội dung message / artifact interface / belief |

## 8. Ghi chú nguồn gốc lý thuyết

- Personal assistants: Maes (1994); các ứng dụng cụ thể: Modi et al. (2005), Chalupsky et al. (2001), Tambe (2008).
- Web & agent: Huhns & Singh (2005); Web of Things: Ciortea et al. (2019).
- EIS: Behrens et al. (2011, 2012); tích hợp GOAL vào Unreal (Hindriks & Dix 2014).
- Robot: Jason-ROS (dự án riêng, tích hợp qua tuỳ biến kiến trúc agent).
- ACL & Ontology: FIPA, KQML (Finin et al. 1994); Semantic Web (Berners-Lee et al. 2001); JASDL (Klapiscak & Bordini 2009).
