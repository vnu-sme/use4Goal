# Phương pháp tiếp theo: ACL, BPMN+OCL, và conformance 4 file

Ngày cập nhật: 2026-07-16.

## 1. Bổ sung OCL vào BPMN

Mục tiêu: `.bpmn2` không chỉ mô tả control-flow của giải pháp đề xuất, mà còn
mô tả điều kiện trạng thái trước/sau khi một activity chạy.

Cú pháp đã chọn:

```bpmn
task ActivityId "Activity label"
  pre ocl {[
    -- điều kiện phải đúng trước khi activity được phép chạy
  ]}
  post ocl {[
    -- điều kiện phải đúng sau khi activity chạy xong
  ]}
```

Áp dụng cho `task`, `call-activity`, và `subprocess`. OCL được giữ nguyên văn
ở tầng BPMN parser/MM, giống cách `.istar` đang giữ raw `ocl {[...]}` cho
goal/task và `.acl` đang giữ invariant OCL. Việc compile/evaluate OCL nên dùng
USE `OCLCompiler` ở tầng kiểm tra, không parse OCL bằng grammar BPMN.

Để BPMN thật sự chạy được từ initial `.soil`, activity cũng có thể mang effect:

```bpmn
task ActivityId "Activity label"
  pre ocl {[ ... ]}
  do soil {[
    -- cập nhật USE state khi activity chạy
  ]}
  post ocl {[ ... ]}
```

Ý nghĩa kiểm tra:

- `pre`: guard khả thi của activity tại checkpoint hiện tại.
- `post`: trạng thái mong muốn sau activity. Nếu dùng `.soil` chỉ để khởi tạo
  và mô phỏng trạng thái, checker có thể so sánh checkpoint trước/sau mỗi bước
  BPMN hoặc kiểm tra post khi activity được đánh dấu hoàn thành.
- Điều kiện flow dạng text (`flow A -> B : "label"`) vẫn chỉ là nhãn mô tả,
  không thay thế `pre/post ocl`.

Trạng thái code hiện tại:

- `Bpmn2.g4` đã nhận `pre/post ocl {[...]}` cho `task`, `call-activity`,
  `subprocess`, và đã nhận `do soil {[...]}` làm activity effect.
- BPMN AST/MM đã có `ActivityConstraint` và `ActivityEffect`.
- `mtg.bpmn2` đã có OCL và SOIL effect cho các activity chính.
- Checker 4 input sinh một `Execution SOIL` tạm từ initial `.soil` + BPMN
  effects, replay bằng USE, rồi kiểm `pre` trước activity và `post` sau
  activity theo checkpoint.

## 2. Phương pháp dịch ACL sang `.use`

ACL là domain/structure model ở giai đoạn phân tích. `.use` là target hình thức
để USE compile và evaluate OCL. Vì vậy ACL→USE nên là một action/compiler riêng:

```text
.acl -> AclCompiler -> AclModel -> AclUseTranslator -> .use
```

Mapping đề xuất:

- `enum` trong ACL -> `enum` trong USE.
- `entity` -> `class`.
- `role`/`agent` -> nên sinh `associationclass` nối ít nhất một identity class
  và một context/group/entity chứa vai trò đó. Với MTG hiện tại, pattern viết
  tay là `Role between Meeting, Person`; các role cụ thể kế thừa từ `Role`.
- `abstract role` -> `abstract associationclass` hoặc abstract superclass.
- `attribute` -> USE attributes.
- `relationship` -> `association`.
- `partOf` -> `composition`.
- `group` -> class/entity context hoặc association owner; các member
  multiplicity của group phải sinh được association ends để OCL kiểm tra
  cardinality.
- `link authority/communication/acquaintance/compatibility` -> association
  hoặc invariant sinh tự động. Riêng `compatibility` cần identity dùng chung
  để cấm cùng một người giữ hai role không tương thích trong cùng context.
- `invariant ... context T ocl {[...]}` -> `constraints context T inv ...`.

Điểm thiết kế còn phải chốt trước khi compiler chính thức đủ mạnh: ACL cần
biểu diễn rõ role instance thuộc group/context nào và role instance đại diện
cho identity/person nào. Nếu thiếu hai trục này thì không sinh được OCL cho
cardinality trong group và compatibility giữa role.

Trạng thái code hiện tại:

- Đã có `AclUseTranslator`.
- Đã có CLI `AclToUseDemoMain input.acl`.
- `mtg.acl` đã được chỉnh để khai báo cùng vocabulary với `mtg.istar` và
  `mtg.soil`: `Person`, `PhoneContact`, context `Meeting`.
- USE sinh từ `mtg.acl` đã compile được bằng USECompiler.
- Bridge `.istar + generated .use + .soil` đã chạy được với MTG.
- Link ACL (`authority`, `communication`, `compatibility`) hiện mới giữ ở ACL
  source; chưa sinh invariant OCL tự động.

## 3. Phương pháp kiểm tra conformance trên 4 file

Input mục tiêu:

```text
model.acl    -> dịch thành model.use
init.soil    -> chỉ khởi tạo trạng thái ban đầu và/hoặc các checkpoint trạng thái
model.istar  -> mục tiêu hệ thống, có OCL guard cho goal/task
model.bpmn2  -> giải pháp đề xuất, có pre/post OCL cho activity
```

Pipeline đề xuất:

```text
ACL -> .use
.use + .soil -> USE MSystem checkpoints
.istar + .use -> compile OCL guards của i*
.bpmn2 + .use -> compile pre/post OCL của BPMN activity
BPMN LTS + USE checkpoints + i* propagation -> conformance verdict
```

Các câu hỏi kiểm tra chính:

1. BPMN có chạy hết quy trình không?
   Mỗi activity chỉ được fire nếu `pre` đúng tại trạng thái hiện tại; sau khi
   fire, `post` phải đúng ở trạng thái sau. Gateway/flow vẫn dùng BPMN LTS để
   xác định thứ tự hợp lệ.
2. BPMN chạy xong có làm i* thỏa mãn không?
   Ở checkpoint cuối, evaluate OCL guard của các goal/task i*, sau đó chạy
   propagation/saturation AND/OR/contribution/dependency hiện có. Verdict đạt
   khi goal đích hoặc tập goal/quality cần kiểm tra được satisfied.
3. ACL/OCL có bị vi phạm không?
   Sau mỗi checkpoint, chạy invariant của `.use` được sinh từ ACL và invariant
   OCL tự viết trong ACL. Nếu invariant fail, trace/process không hợp lệ dù
   BPMN control-flow vẫn chạy được.

Với MTG hiện tại, `mtg.soil` kết thúc khi `chloe.attendance = #unknown`, trong
khi `mtg.bpmn2` có postcondition của `participate` yêu cầu mọi Participant
`attendance = #attended`. Đây là case tốt để checker báo quy trình/giải pháp
chưa đảm bảo mục tiêu cuối cùng.

Trạng thái code hiện tại:

- Đã có CLI `AclBpmnIStarConformanceMain model.acl init.soil model.istar model.bpmn2`.
- CLI này sinh `.use` từ ACL, replay `.soil`, evaluate i* bằng bridge hiện có,
  chạy BPMN effects để sinh execution state, compile/evaluate BPMN activity
  OCL theo checkpoint trước/sau activity, và báo verdict.
- Chạy với MTG hiện trả:
  - `BPMN OCL: PASS`.
  - `i* root goals: PASS`.
  - `Verdict: CONFORMANT`.

Giới hạn còn lại:

- BPMN execution hiện là topological execution đơn giản theo sequence flow và
  chạy mọi activity reachable; chưa có semantics đầy đủ cho gateway condition,
  token, loop, parallel synchronization nâng cao.
- `do soil` trong MTG hiện còn viết theo instance cụ thể của ví dụ
  (`meeting1`, `xing`, `amr`, ...). Muốn tổng quát cần thêm binding/parameter
  policy cho BPMN activity.
- Chưa sinh OCL invariant tự động từ ACL `group` multiplicity và `compatibility`.
