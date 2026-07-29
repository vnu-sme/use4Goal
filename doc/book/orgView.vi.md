# Chương 8 — Chiều Tổ chức (Organization Dimension)

> Tổng hợp lại lý thuyết từ `orgView.md`, dựa trên mô hình tổ chức **Moise** dùng trong JaCaMo.

## 1. Tổng quan

Tổ chức (organization) mô hình hoá **hiện tượng ở mức trên cá nhân (supra-individual)**: các mẫu hợp tác có cấu trúc, vượt ra ngoài hoạt động của một agent đơn lẻ.

- **Organization specification** (đặc tả tổ chức): mô tả **khai báo (declarative)** — trả lời câu hỏi "cái gì" (what), tức hành vi kỳ vọng của các agent, KHÔNG nói "làm thế nào" (how) — điều này thuộc về chiều agent.
- **Organization entity** (thực thể tổ chức): sự **hiện thực hoá (enactment)** của đặc tả bởi các agent thật — mô tả trạng thái đang tiến triển của hành vi phối hợp/điều tiết.

| | Organization specification | Organization entity |
|---|---|---|
| Câu hỏi trả lời | "Cái gì" (what) | "Ai đang làm gì, thế nào" |
| Tính chất | Tĩnh, là khuôn mẫu (template) | Động, thay đổi liên tục khi agent tạo/xoá nhóm, nhận/rời vai trò |
| Ví dụ | Nhóm `scheduling_staff` có vai trò `planner` | Agent `bob` đang đóng vai `planner` trong nhóm `week` |

Khi agent hành xử không khớp đặc tả → **vi phạm (violation)** → có thể bị **chế tài (sanction)**.

**Reorganization** (tái tổ chức): thay đổi đặc tả hoặc thực thể tổ chức, có thể do chính các agent quyết định.

## 2. Ba khía cạnh (facets) của tổ chức

| Khía cạnh | Khái niệm | Trả lời câu hỏi |
|---|---|---|
| **Structural** (cấu trúc) | role, link, group | Ai có thể là ai, liên kết với ai, trong nhóm nào? |
| **Functional** (chức năng) | goal, mission, plan, social scheme | Cần phối hợp làm gì, ai chịu trách nhiệm gì? |
| **Normative** (quy phạm) | norm | Ai *phải/được phép* làm gì? (ràng buộc cấu trúc + chức năng bằng deontic) |

Structural và Functional là **độc lập** với nhau; Normative đóng vai trò **kết nối** hai khía cạnh trên lại.

## 3. Cấu trúc (Structural Abstractions)

| Khái niệm | Định nghĩa |
|---|---|
| **Role** (vai trò) | Trừu tượng cho **vị trí** mà 1 agent có thể chiếm giữ trong tổ chức. Có thể kế thừa (inheritance) từ role khác; mọi role mặc định kế thừa từ `soc` (root role) |
| **Link** (liên kết) | Quan hệ có nhãn giữa 2 role trong 1 nhóm. Ba loại: `communication` (ai giao tiếp được với ai), `authority` (ai có quyền với ai), `acquaintance` (ai biết/truy cập thông tin của ai) |
| **Group** (nhóm) | Trừu tượng cho 1 **cộng đồng** agent có thể tồn tại trong tổ chức; là **điểm vào (entry point)** của agent vào tổ chức (agent nhận role → vào group) |

### 3.1 Structural specification

Gồm:
- Danh sách định nghĩa role (có thể kế thừa).
- Một cây phân cấp (hierarchy) các group — mỗi group gồm: các role, các link giữa role, các subgroup, và các **group-formation constraint**.

### 3.2 Group-formation constraints (ràng buộc hình thành nhóm)

| Ràng buộc | Ý nghĩa |
|---|---|
| **Role-compatibility** | Quan hệ có hướng cho phép 1 agent đóng đồng thời 2 role (mặc định 2 role là *không tương thích* — 1 agent không thể đóng cả hai) |
| **Role-cardinality** | Giới hạn dưới/trên số agent có thể đóng 1 role trong 1 group entity |
| **Group-cardinality** | Giới hạn dưới/trên số subgroup entity có thể tạo từ 1 định nghĩa subgroup |

## 4. Chức năng (Functional Abstractions)

| Khái niệm | Định nghĩa |
|---|---|
| **Organizational goal** | Trạng thái cần được thoả mãn bởi 1 hoặc nhiều agent |
| **Mission** | Tập hợp các goal mà **1 agent** chịu trách nhiệm (cam kết — *commit*) |
| **Social plan** | Cấu trúc các goal liên quan với nhau (goal decomposition), thể hiện phụ thuộc giữa các goal cần nhiều agent phối hợp |
| **Social scheme** | Gộp: 1 social plan + các organizational goal + các mission tương ứng → mô tả hành vi tập thể kỳ vọng |

### 4.1 Ba toán tử phân rã goal (trong social plan)

| Toán tử | Cú pháp | Ý nghĩa |
|---|---|---|
| **Sequence** | `g1 = g2, g3` | g1 thoả mãn ⟺ g2 rồi sau đó g3 được thoả mãn |
| **Choice** | `g1 = g2\|g3` | g1 thoả mãn ⟺ đúng 1 trong g2 hoặc g3 được thoả mãn |
| **Parallel** | `g1 = g2\|\|g3` | g1 thoả mãn ⟺ cả g2 và g3 được thoả mãn (có thể song song) |

- **Goal cardinality**: giới hạn số agent chịu trách nhiệm đạt/duy trì 1 goal.
- **Mission cardinality**: giới hạn số agent có thể commit vào 1 mission.
- Dùng **goal** (chứ không phải action) làm đơn vị nguyên thuỷ → tổ chức chỉ quan tâm *trạng thái cần đạt*, không ràng buộc *cách thức* (việc đó thuộc chiều agent).

## 5. Quy phạm (Normative Abstractions)

**Norm** (quy phạm) là khái niệm trung tâm: xác định **quyền và nghĩa vụ** của agent, kết nối structural + functional bằng **deontic modality** (thức quy phạm).

### 5.1 Thành phần 1 norm trừu tượng

| Thành phần | Mô tả |
|---|---|
| **Deontic modality** | `obligation` (nghĩa vụ) hoặc `permission` (được phép) |
| **Norm bearer** | Role chịu trách nhiệm (người mang nghĩa vụ/quyền) |
| **Norm mission** | Mission mà deontic modality áp dụng |
| **Activation condition** (tuỳ chọn) | Điều kiện để norm được kích hoạt; mặc định `true` (kích hoạt ngay khi tổ chức được tạo) |
| **Time constraint** (tuỳ chọn) | Giới hạn thời gian hoàn thành; quá hạn → norm coi là **unfulfilled** (chưa hoàn thành/vi phạm) |

Norm là **trừu tượng** (dùng role/mission) — khi hiện thực hoá (enactment), role được thay bằng agent cụ thể đang đóng role đó.

### 5.2 Vì sao cần đủ 3 đặc tả?

| Thiếu | Hậu quả |
|---|---|
| Thiếu structural | Agent phải tự thương lượng ai dẫn dắt, ai hợp tác với ai |
| Thiếu functional | Agent phải tự suy luận lại kế hoạch tập thể mỗi lần hành động chung; không có "bộ nhớ tổ chức" để lưu kế hoạch |
| Thiếu cả hai | Cộng dồn cả 2 vấn đề trên |
| Có đủ cả 3, tách biệt structural/functional | Cho phép **tái tổ chức linh hoạt**: đổi cấu trúc mà không đổi cách phối hợp, và ngược lại |

## 6. Thực thi Tổ chức (Organization Execution)

Trạng thái tổ chức (organization entity) được **phân tán** vào 3 loại thực thể, mỗi loại có vòng đời (life cycle) riêng:

| Thực thể | Trạng thái quản lý |
|---|---|
| **Group entity** | Chủ sở hữu (owner), liên kết cha/con, tập `⟨agent, role⟩` cùng link giữa chúng |
| **Social scheme entity** | Owner, các group entity chịu trách nhiệm cung cấp agent, cam kết mission, trạng thái từng goal |
| **Normative entity** | Trạng thái tập hợp norm (được sinh ra khi 1 group entity trở thành chịu trách nhiệm cho 1 scheme entity) |

### 6.1 Vòng đời Organization entity

1. **Tạo** dựa trên 1 organization specification được chọn.
2. **Thực thi**: liên tục cập nhật qua việc nhận/rời role, tạo nhóm, cam kết mission, hoàn thành/vi phạm norm...
3. **Huỷ** khi tất cả các thực thể con đã bị xoá.

### 6.2 Vòng đời Group entity

1. Tạo group entity (nối vào cây cha/con theo structural specification).
2. Agent **adopt role** (nhận vai trò) → trở thành role player.
3. Kiểm tra **well-formed** (đúng cấu trúc): thoả mãn group-formation constraints (role-compatibility, cardinality...).
4. Group entity được gán **chịu trách nhiệm (responsible)** cho 1/nhiều social scheme entity → tạo kèm 1 normative entity.
5. Ngắt kết nối scheme entity khi không còn agent commit vào mission nào của nó.
6. Xoá group entity khi: không còn subgroup, không còn scheme responsibility, và mọi agent đã rời role.

### 6.3 Vòng đời Social scheme entity

1. Tạo dựa trên 1 social scheme trong functional specification.
2. Khi thuộc trách nhiệm của 1 group entity → agent trong group được **permitted/obliged** commit vào mission (theo norm).
3. Khi **well-formed** (đủ số agent commit theo cardinality) → agent theo đuổi goal theo thứ tự trong plan.
4. Scheme kết thúc khi root goal đạt được hoặc bị coi là **impossible** (không thể đạt).
5. Khi không còn agent commit → detach khỏi group entity → có thể xoá.

**Vòng đời của 1 Goal** (trong scheme entity):

| Trạng thái | Ý nghĩa |
|---|---|
| `waiting` | Chưa thể theo đuổi (còn phụ thuộc goal khác hoặc scheme chưa well-formed) — trạng thái khởi đầu |
| `enabled` | Có thể theo đuổi (scheme well-formed + tiền điều kiện đã thoả mãn) |
| `achieved` | Agent đã đạt được goal |
| `impossible` | Agent kết luận không thể đạt goal |

(waiting→enabled do social scheme entity thực hiện; enabled→achieved do hành vi của agent.)

### 6.4 Vòng đời Normative entity

1. Tạo, kết nối với group entity + social scheme entity tương ứng.
2. Trạng thái quy phạm = **mission norms** (norm trừu tượng thay role bằng agent cụ thể).
3. Khi scheme well-formed → sinh thêm **goal norms** khi goal chuyển sang `enabled` (obligation trên mission → obligation trên goal; permission trên mission → obligation trên goal *sau khi* đã commit).
4. Khi mọi norm đã fulfilled → normative entity detach; agent chỉ được rút cam kết nếu nghĩa vụ đã hoàn thành.

**Vòng đời của 1 Norm** (mission norm hoặc goal norm):

| Trạng thái | Ý nghĩa |
|---|---|
| `active` | Activation condition đang đúng |
| `fulfilled` | Đối tượng của deontic modality đã thành công (đã commit / đã đạt goal) |
| `unfulfilled` | Chưa hoàn thành nghĩa vụ hoặc đã quá hạn (deadline) → **vi phạm** |
| `inactive` | Activation condition không còn đúng |

### 6.5 Vòng đời Agent (role-player) trong tổ chức

1. Nhận role (adopt role) trong group — bị ràng buộc bởi role-cardinality/compatibility.
2. Cam kết mission trong scheme entity — bị ràng buộc bởi đặc tả quy phạm + ràng buộc mission.
3. Theo đuổi các goal thuộc mission đã cam kết, theo tiến trình của scheme.
4. Rút cam kết — chỉ được phép nếu nghĩa vụ đã hoàn thành.
5. Có thể rời role (nếu không còn cam kết) và rời group.

> Lưu ý: agent **chủ động** thay đổi group/scheme entity (tạo nhóm, nhận role, cam kết mission); còn normative entity chỉ **quan sát** các thay đổi đó để kích hoạt/hủy kích hoạt norm — agent không có hành động trực tiếp lên normative entity.

## 7. Đặt Tổ chức trong Môi trường (Constitution)

Khi norm cần được giám sát (kích hoạt, vi phạm, hoàn thành), cần xác định **cái gì trong môi trường tương ứng với khái niệm quy phạm** — gọi là **constitution** (Searle 1997/2010).

Ví dụ: trong đấu giá, norm quy định việc trả tiền/đặt giá — nhưng "giơ tay = đặt giá" là quy ước **cấu thành (constitutive)** phải khai báo riêng, không nằm trong norm.

Hai vấn đề đặt ra:
1. Ánh xạ khái niệm tổ chức ↔ khái niệm môi trường (vd: thực hiện operation trên artifact = đạt 1 organizational goal).
2. Cho phép **nhiều cách** hiện thực hoá cùng 1 norm (vd: giơ tay HOẶC chớp mắt đều tính là đặt giá).

→ Mô hình **situated artificial institution** (de Brito et al.) tích hợp vào JaCaMo giải quyết vấn đề này bằng **constitutive entity/rules** trung gian giữa môi trường và thực thể quy phạm.

## 8. Bảng tổng hợp toàn bộ khái niệm

| Facet | Khái niệm | Vai trò chính |
|---|---|---|
| Structural | role | Vị trí agent có thể chiếm giữ |
| Structural | link | Quan hệ communication/authority/acquaintance giữa 2 role |
| Structural | group | Cộng đồng agent, điểm vào tổ chức |
| Functional | organizational goal | Trạng thái cần đạt |
| Functional | mission | Gói goal do 1 agent chịu trách nhiệm |
| Functional | social plan | Cấu trúc phân rã goal (seq/choice/parallel) |
| Functional | social scheme | Plan + goals + missions |
| Normative | norm | Quyền/nghĩa vụ ràng buộc role–mission |
| Thực thi | group entity | Hiện thực hoá 1 group |
| Thực thi | social scheme entity | Hiện thực hoá 1 social scheme |
| Thực thi | normative entity | Trạng thái các norm đang áp dụng |

## 9. Ghi chú nguồn gốc lý thuyết

- Mô hình gốc: **Moise** (Hübner et al.), phát triển từ Hannoun et al. (2000).
- Các mô hình tổ chức khác: AGR (Ferber & Gutknecht), TeamCore (Tambe), Islander (Esteva et al.).
- Nền tảng: Gasser (2001) — hiện tượng supra-individual; Malone (1999) — management science; Bernoux (1985) — sociology.
- Constitution: Searle (1997, 2010); mô hình situated artificial institution: de Brito et al. (2017, 2018), tích hợp vào JaCaMo.
