# 3.6. Chuyển Đổi ACL Sang USE

File LaTeX tương lai: `\subsection{Chuyển đổi ACL sang USE}\label{sec:acl2use}`.

## Mục tiêu của phần

Trình bày luật dịch `AclModel → .use` đủ chi tiết để lặp lại được (reproducible) — đây là đóng góp
kỹ thuật cụ thể thứ nhất, cần có bảng luật dịch tường minh, không chỉ mô tả bằng lời.

## Cấu trúc heading đề xuất

```
\subsubsection{Dịch cấu trúc: lớp, quan hệ, kế thừa}
\subsubsection{Dịch quyền sở hữu Group--Role}
\subsubsection{Sinh bất biến tương thích}
```

## Nội dung cốt lõi từng mục

### 3.6.1. Dịch cấu trúc: lớp, quan hệ, kế thừa

- Nguyên tắc nền: `Entity`/`Role`/`Group` trong ACL dịch **trực tiếp 1-1** thành `class` trong USE
  — không cần lớp trung gian, vì ACL vốn đã được thiết kế theo ngữ nghĩa UML class diagram (3.5.1).
- Bảng luật dịch cấp 1:

| ACL | USE |
|---|---|
| `entity E { attrs }` | `class E attributes ... end` |
| `role R extends R'` | `class R < R'` (generalization) |
| `group G` | `class G` |
| `association a { A[m]; B[n]; }` | `association a between A[m] B[n] end` |
| `aggregation`/`composition` | `aggregation`/`composition` USE tương ứng cùng cardinality |
| `enum En { l1, l2 }` | `enum En { l1, l2 }` |

- Nêu 1 trường hợp đặc biệt cần giải thích rõ (tránh gây khó hiểu cho người đọc quen UML thuần):
  quan hệ **thành viên nhóm** (`member_*`, tức 1 Entity là thành viên của 1 Group) vốn là quan hệ
  cấu trúc ngầm định trong ACL, nhưng vẫn phải xuất hiện tường minh trong class diagram USE — nên
  được dịch thành 1 `association` bình thường, không phải 1 loại quan hệ mới.
- Đồng thời sinh 1 lớp `Agent` (tên do bộ dịch tự đặt, không xuất hiện trong `.acl` gốc) đóng vai
  trò "người thật/tác nhân thật" có thể đóng nhiều vai trò cùng lúc — cần giải thích rõ **vì sao**
  cần lớp trung gian này: bản thân `Role` trong ACL là 1 lớp mô tả *tư cách*, còn `Agent` mới là
  thực thể có thể đồng thời giữ nhiều tư cách — đây là điểm mấu chốt để 3.6.3 sinh được bất biến
  "1 Agent không được đồng thời giữ 2 role không tương thích".

### 3.6.2. Dịch quyền sở hữu Group--Role

- Với mỗi khai báo `Owner` (1 Group sở hữu 1 Role hoặc 1 Group con), bộ dịch sinh **2** association
  riêng biệt, không phải 1:
  1. `Agent_plays_<Role>`: nối `Agent` (bội số `[1..1]`) với `Role` (bội số `[0..*]`) — biểu diễn
     "1 agent thật đứng sau 0 hoặc nhiều instance của vai trò này".
  2. `<Role>_in_<Group>`: nối `Group` với `Role` theo đúng cardinality đã khai trong `.acl` —
     biểu diễn "instance vai trò này thuộc về instance nhóm nào".
- Với `Owner` giữa `Group` cha và `Group` con, dịch thành 1 `composition` (thay vì 2 association
  như trường hợp Role) — vì quan hệ Group cha–Group con trong ACL vốn đã mang ngữ nghĩa sở hữu vòng
  đời mạnh (không cho phép chu trình, mỗi Group con tối đa 1 Group cha — xem 3.5.2), khớp tự nhiên
  với ngữ nghĩa `composition` của UML.
- Giải thích tên biến `GroupNav`/điều hướng ngược (navigation) được bộ dịch theo dõi nội bộ để
  phục vụ bước sinh bất biến ở 3.6.3 — không cần đi sâu code, chỉ cần nêu ý tưởng: bộ dịch phải nhớ
  "từ 1 instance Role, đi ngược thế nào để tới đúng instance Group chứa nó" để viết được điều kiện
  "cùng 1 instance nhóm" trong OCL.

### 3.6.3. Sinh bất biến tương thích

Đây là nội dung kỹ thuật quan trọng nhất của 3.6, trực tiếp giải quyết Tình huống 1 (Phần 3.1).

- Với **mọi cặp vai trò cụ thể** (`concreteRoles`, tức vai trò không trừu tượng và thực sự có ít
  nhất 1 agent có thể đóng), bộ dịch xét từng cặp `(R1, R2)` và quyết định có cần sinh 1 `inv` hay
  không, theo đúng logic đã cài đặt:
  1. Nếu 2 vai trò **không thể** cùng thuộc 1 phạm vi nhóm nào (`sharedScope = false`, ví dụ thuộc
     2 cây Group hoàn toàn tách biệt) → bỏ qua, không cần bất biến vì không bao giờ xung đột được.
  2. Ngược lại, kiểm tra danh sách `compatibility` đã khai trong `.acl`: nếu **có** khai báo compatible
     bao trùm đúng phạm vi (`intra-group` khớp instance nhóm, hoặc `inter-group` với
     `extendsSubgroups`) → bỏ qua, không sinh bất biến (vì đã được phép).
  3. Còn lại mọi trường hợp khác — kể cả khi hoàn toàn **không có khai báo** `compatibility` nào
     cho cặp đó — sinh 1 bất biến `inv NoConflict_<R1>_<R2>` trên lớp `Agent`.
- Dạng OCL sinh ra, tổng quát hoá từ code (không phải diễn giải suông — đúng cấu trúc):

  ```ocl
  context Agent inv NoConflict_R1_R2:
    self.<đường điều hướng tới mọi R1>->forAll(r1 : R1 |
      self.<đường điều hướng tới mọi R2>->forAll(r2 : R2 |
        not (<điều kiện cùng phạm vi nhóm>)
        [ or (<điều kiện phạm vi được phép theo compatibility>) ]))
  ```

- Áp trực tiếp lên ví dụ `mtg.acl`: vì `Secretary <-> Participant` không được khai `compatibility`,
  bộ dịch sinh `inv NoConflict_Secretary_Participant` trên lớp `Agent` sinh ra — chính bất biến này
  phát hiện lỗi ở Tình huống 1 khi được kiểm tra tại checkpoint bởi
  `AclBpmnIStarConformanceChecker.evaluateAclInvariants` (chi tiết thuật toán ở 3.7).
- Nêu rõ tính đúng đắn cần khẳng định (không chứng minh hình thức đầy đủ trong bài báo, chỉ phát
  biểu bất biến): bộ dịch bảo toàn đúng ngữ nghĩa "closed world" của ACL (3.5.2) — mọi cặp vai trò
  không tường minh cho phép compatible đều bị cấm bằng OCL, không có trường hợp nào lọt qua do
  thiếu sót của bộ dịch (trừ khi có lỗi cài đặt, xem Phần 5).

## Nguồn tham chiếu / cơ sở

- Toàn bộ luật dịch: `goal/src/main/java/org/vnu/sme/goal/acl/use/AclUseTranslator.java`, đặc biệt
  hàm `translate` (dòng 22–74), `renderCompatibilityInvariants` (dòng 82–134), và `sameGroupInstance`
  (dòng 142+).
- Ghi chú thiết kế gốc của tác giả ngay trong code (rất hữu ích để trích dẫn ý tưởng, diễn giải lại
  bằng văn phong học thuật thay vì chép nguyên văn comment code):
  > "One `inv NoConflict_R1_R2` per pair of concrete roles an Agent could simultaneously hold,
  > mirroring `AolModelFactory.checkCompatibility`/`checkPair` exactly: undeclared pairs are always
  > forbidden; declared-compatible pairs are still scope-gated..."
  (dòng 76–81, `AclUseTranslator.java`).
- Header comment giải thích 3 dòng đầu file sinh ra (`out.append(...)` dòng 23–27) — có thể trích
  gần nguyên văn làm chú thích listing minh hoạ file `.use` sinh tự động.

## Ghi chú khi viết prose thật

- Nên có 1 `\lstlisting` cụ thể trích đoạn `.use` **thật sự sinh ra** từ `mtg.acl` (chạy
  `AclUseTranslator.translate` trên `mtg.acl` để lấy output thật, không tự bịa output) — đặt cạnh
  listing `mtg.acl` gốc ở 3.5.4 để đối chiếu song song, giúp người đọc thấy trực quan phép dịch.
- Mục 3.6.3 nên có 1 bảng ví dụ cụ thể áp 3 nhánh quyết định (bỏ qua/bỏ qua/sinh bất biến) lên 3
  cặp vai trò thật trong `mtg.acl`: `(Initiator, Participant)` → đã khai compatible, không sinh;
  `(Organizer, Secretary)` → đã khai compatible, không sinh; `(Secretary, Participant)` → không
  khai, sinh bất biến. Bảng này biến lập luận trừu tượng thành ví dụ đếm được, dễ kiểm chứng.
- Tránh paraphrase quá sát code khiến đoạn văn đọc như tài liệu API — ưu tiên diễn giải ý nghĩa
  ("tại sao cần 2 association thay vì 1") hơn là liệt kê lại tên biến Java.
