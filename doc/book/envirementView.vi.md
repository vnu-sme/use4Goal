# Chương 5 — Chiều Môi trường (Environment Dimension)

> Tổng hợp lại lý thuyết từ `envirementView.md`, dựa trên mô hình **Agents & Artifacts (A&A)** và nền tảng **CArtAgO** dùng trong JaCaMo.

## 1. Tổng quan

Môi trường (environment) là abstraction dùng để mô hình hoá **tài nguyên và công cụ (resources & tools)** mà agent có thể tạo, chia sẻ và sử dụng — những thứ **không** nên mô hình như agent (vì không tự trị/không chủ động theo goal).

- **Artifact**: đơn vị xây dựng cơ bản để tổ chức môi trường — vừa là đối tượng thiết kế (từ góc nhìn kỹ sư MAS), vừa là thực thể hạng nhất (first-class entity) mà agent tạo/khám phá/chia sẻ/sử dụng/quan sát.
- **Workspace**: container logic chứa agent + artifact, cho khái niệm về "vị trí" (locality) và phạm vi quan sát/tương tác.

| Nếu agent là... | thì artifact là... |
|---|---|
| viên gạch cơ bản cho phần **tự trị, hướng goal/task** của MAS | viên gạch cơ bản cho phần **không tự trị, hướng chức năng (function-oriented)** của MAS |

Ví dụ artifact: blackboard, cơ sở dữ liệu chia sẻ, kho tri thức chung.

## 2. Trừu tượng hoá Artifact

Một artifact được định nghĩa (trong JaCaMo, bằng lớp Java kế thừa `Artifact`) với các thành phần:

| Thành phần | Định nghĩa | Từ góc nhìn agent |
|---|---|---|
| **Operation** (thao tác) | Đơn vị chức năng cơ bản của artifact — nguyên tử hoặc là 1 chuỗi bước tính toán | Chính là **action** mà agent có thể thực hiện |
| **Observable property** (thuộc tính quan sát được) | Trạng thái quan sát được của artifact | Được ánh xạ thành **belief** trong agent quan sát nó |
| **Signal** (tín hiệu) | Sự kiện quan sát được, không nhất thiết gắn với 1 observable property | Không tự động thành belief; xử lý như thông điệp bất đồng bộ |
| **Action feedback** | Tham số đầu ra (output parameter) của operation | Kết quả trả về ngay cho agent gọi action |
| **Link interface** | Tập operation được đánh dấu `@LINK`, cho phép artifact khác gọi tới | Cho phép ghép nối (compose) nhiều artifact |
| **Hidden state** | Trạng thái riêng tư (private field), không quan sát được | — |

### 2.1 Phân loại artifact (taxonomy)

| Loại | Mô tả | Ví dụ |
|---|---|---|
| **Resource artifact** | Tài nguyên chung, phổ biến nhất | Counter, knowledge-base chia sẻ |
| **Coordination artifact** | Cung cấp chức năng điều phối tương tác giữa agent | Semaphore/barrier, blackboard, auction machine, workflow engine, artifact quản lý tổ chức (chương 8) |
| **Boundary artifact** | Cho phép agent tương tác với người dùng/hệ thống ngoài MAS | GUI |

## 3. Làm việc với Artifact từ góc nhìn Agent

Hành động của agent với artifact chia 3 nhóm:

1. **Tạo/tìm/huỷ artifact.**
2. **Sử dụng artifact** (gọi operation, quan sát property/signal).
3. **Liên kết (link)/huỷ liên kết artifact.**

### 3.1 Tạo và khám phá artifact

| Hành động | Cú pháp | Ý nghĩa |
|---|---|---|
| Tạo artifact | `makeArtifact(Name,Template,Params,Id)` | Tạo instance mới tên `Name` từ `Template`, trả về `Id` (định danh duy nhất do hệ thống sinh) |
| Huỷ artifact | `disposeArtifact(Id)` | Xoá artifact khỏi workspace |
| Tra cứu artifact | `lookupArtifact(Name,Id)` | Lấy `Id` từ tên logic (logical name) trong workspace |

- **Logical name** là duy nhất *trong 1 workspace* (artifact ở workspace khác có thể trùng tên); **Id** luôn duy nhất toàn hệ thống.

### 3.2 Thực thi operation trên artifact

- Agent gọi action `op(Params)` → kích hoạt operation tương ứng trên artifact trong workspace.
- Nếu nhiều artifact cùng cung cấp operation đó, có thể chỉ định rõ bằng annotation `[artifact_id(Id)]`; nếu không chỉ định, hệ thống chọn không xác định (nondeterministic).
- Action **thành công** nếu operation hoàn tất thành công; **thất bại** nếu operation không tồn tại trong usage interface hoặc lỗi khi thực thi.
- Khi agent gọi 1 action, **intention gọi bị suspend** cho tới khi có sự kiện hoàn tất action (action event) — nhưng agent **không bị block**: reasoning cycle vẫn tiếp tục xử lý các intention khác.
- Vì artifact có thể được tạo/huỷ động → **tập action khả dụng của agent là động (dynamic)**.

**Predefined artifacts** (có sẵn mặc định trong mỗi workspace):

| Artifact | Chức năng |
|---|---|
| `workspace` | Tạo/tìm/quản lý artifact; cung cấp belief `artifact(Name, Template, Id)` |
| `console` | Tương tác input/output chuẩn (vd `println`) |
| `blackboard` | Blackboard đơn giản kiểu tuple-space, hỗ trợ điều phối agent |

### 3.3 Quan sát artifact

| Hành động | Cú pháp | Ý nghĩa |
|---|---|---|
| Bắt đầu quan sát | `focus(Id,Filter)` | Bắt đầu nhận observable property/signal từ artifact `Id` (có thể lọc bằng `Filter`) |
| Dừng quan sát | `stopFocus(Id)` | Ngừng quan sát artifact |

- Observable property → ánh xạ trực tiếp thành **belief**; mỗi lần property thay đổi → sự kiện được sinh tự động → belief cập nhật → belief change event.
- Belief về observable property được annotate: `artifact_id(Id)`, `artifact_name(Name)`, `workspace(Id)`.
- Signal **không** tạo belief mặc định — xử lý như message bất đồng bộ từ phía artifact.

**Ba nguyên tắc ngữ nghĩa quan sát (observation semantics):**

| Nguyên tắc | Ý nghĩa |
|---|---|
| **Observation completeness** | Không mất sự kiện: mọi trạng thái quan sát được mới đều được mọi agent đang quan sát nhận biết |
| **Event ordering** | Sự kiện từ **cùng 1 artifact** được nhận theo đúng thứ tự sinh ra; không có thứ tự đảm bảo giữa các artifact khác nhau |
| **Atomic perception** | Nếu 2 property đổi trong cùng 1 lần thực thi operation → được nhận qua **1 percept duy nhất**, cập nhật belief trong cùng 1 reasoning cycle (dù có thể sinh nhiều event xử lý ở các cycle sau) |

### 3.4 Liên kết artifact (Linking artifacts)

| Hành động | Cú pháp | Ý nghĩa |
|---|---|---|
| Liên kết 2 artifact | `linkArtifacts(LinkingArId, LinkedArId, Port)` | Artifact "linking" có thể gọi operation trên artifact "linked" qua `Port` |
| Gọi operation liên kết | `execLinkedOp(Port,OpName,OpArgs)` | Thực thi operation trên artifact đã liên kết |

- Bên artifact liên kết (linked) phải khai báo **link interface** bằng annotation `@LINK` trên operation được phép gọi từ bên ngoài.
- Ngữ nghĩa gọi operation liên kết giống hệt agent gọi action: request bị suspend tới khi có kết quả (thành công/thất bại).

## 4. Workspace

- Là **container logic** (giống thư mục trong file system), tổ chức topology của môi trường.
- Mỗi workspace: 1 cha, nhiều con (hierarchy) — mặc định có `main` là root.
- Đường dẫn logic: `/my_bakery/cake_room` (tuyệt đối); đường dẫn không bắt đầu bằng `/` là **tương đối** với **current workspace** (workspace vừa join gần nhất — gắn theo từng intention).

| Hành động | Ý nghĩa |
|---|---|
| `joinWorkspace(Path,Id)` | Tham gia 1 workspace (theo đường dẫn đầy đủ) |
| `quitWorkspace(Id)` | Rời workspace |
| `createWorkspace(ParentId,Name)` | Tạo workspace con mới |
| `removeWorkspace(Path)` | Xoá workspace |
| `linkWorkspaces(From,To,Name)` | Tạo access link giữa 2 workspace |

- Agent được sinh ra (spawn) trong **home workspace**, có thể join thêm nhiều workspace khác cùng lúc.
- Nhiều workspace của cùng 1 MAS có thể chạy trên **nhiều node mạng khác nhau** → hệ thống phân tán (distributed).

## 5. Thực thi Môi trường (Environment Execution)

### 5.1 Mô hình đồng thời (concurrency)

| Nguyên tắc | Ý nghĩa |
|---|---|
| Operation trên **các artifact khác nhau** | Có thể chạy **song song (concurrent)** |
| Operation trên **cùng 1 artifact** | Chạy **tuần tự**, đảm bảo **mutual exclusion** (loại trừ lẫn nhau) — chỉ 1 operation thực thi tại 1 thời điểm |

- Nếu có request khi artifact đang bận → request được **xếp hàng (enqueue)**.
- Khi operation hoàn tất → sinh **action event** (thành công/thất bại) gửi tới agent yêu cầu → intention agent đó được resume.

### 5.2 Ngữ nghĩa cập nhật trạng thái quan sát được

| Nguyên tắc | Cơ chế API tương ứng |
|---|---|
| Có thể cập nhật nhiều property mà **chưa** công bố ngay | `updateValue` — cập nhật nhưng chưa observable ngay |
| Trạng thái mới chỉ được công bố (observable) khi | operation kết thúc, HOẶC khi sinh signal, HOẶC gọi tường minh `commitObsState()` |
| Thứ tự thay đổi phải được giữ nguyên khi agent quan sát | Không được mất/đảo thứ tự sự kiện |

## 6. So sánh Artifact với Object (OOP) và Monitor

| Tiêu chí | Object (OOP) | Monitor | Artifact |
|---|---|---|---|
| Gọi hành vi | Method call — chuyển control đồng bộ | Entry call — chuyển control đồng bộ | Action/operation — **không** chuyển control, thực thi bởi luồng khác (môi trường); agent tiếp tục reasoning cycle song song |
| Trạng thái quan sát được | Không có khái niệm observable state (field nên private) | Không có | Có **observable property** — agent cảm nhận như percept, giống asynchronous stream |
| An toàn đồng thời (thread-safety) | Cần tự đảm bảo | Có (mutual exclusion built-in) | Có — chỉ 1 operation thực thi tại 1 thời điểm (giống monitor) |
| Ghép nối module khác | Gọi method trực tiếp (dễ deadlock nếu lồng nhau) | Có thể gọi entry monitor khác (dễ deadlock, không release lock) | Dùng **linkability** — cách kỷ luật hơn để tương tác giữa artifact |

## 7. Bảng tổng hợp khái niệm chính

| Khái niệm | Vai trò |
|---|---|
| Artifact | Đơn vị tài nguyên/công cụ phi tự trị, hướng chức năng |
| Workspace | Container logic tổ chức topology agent + artifact |
| Operation | Chức năng agent có thể kích hoạt (action) trên artifact |
| Observable property | Trạng thái artifact → ánh xạ thành belief agent |
| Signal | Sự kiện quan sát được, không phải belief mặc định |
| Action feedback | Tham số đầu ra của operation, phản hồi ngay cho agent |
| Link interface | Cơ chế artifact gọi artifact khác (`@LINK`, `execLinkedOp`) |
| makeArtifact / disposeArtifact | Tạo/huỷ artifact |
| lookupArtifact | Tìm artifact theo tên logic |
| focus / stopFocus | Bắt đầu/dừng quan sát artifact |
| joinWorkspace / quitWorkspace | Vào/ra workspace |
| createWorkspace / removeWorkspace | Tạo/xoá workspace |

## 8. Ghi chú nguồn gốc lý thuyết

- Mô hình A&A (Agents & Artifacts): dựa trên activity theory và distributed cognition; Ricci et al. (2006, 2010) — phân loại artifact (resource/coordination/boundary).
- Environment như abstraction hạng nhất trong agent-oriented software engineering: Weyns et al. (2007); Weyns & Holvoet (2006) — mức kiến trúc.
- Environment Interface Standard (EIS): Behrens et al. (2011) — tích hợp agent đa công nghệ vào cùng 1 môi trường.
