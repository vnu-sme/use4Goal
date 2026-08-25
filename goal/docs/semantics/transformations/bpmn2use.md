# BPMN → USE/OCL

## Nguồn và đích

Đầu vào là BPMN model đã qua semantic validation. Khi chạy action tích hợp,
ACL cung cấp class diagram, BPMN lane xác định Role thực hiện operation, còn
pool `for GroupType` xác định scope của một execution. Đầu ra gồm `.use` và
file `.tocl` chứa thứ tự gọi operation.

## Ánh xạ

| BPMN | USE |
|---|---|
| `pool P for G` | scope `G` dùng để liên hệ các Role occurrence cùng quy trình |
| lane `R` | class ACL Role `R` |
| flow node `n` trong lane `R` | operation `R::n()` |
| activity `pre` | domain precondition của `n()` |
| activity `post` | domain postcondition của `n()` |
| Start event `s` | TOCL: `s()` bắt buộc xảy ra ít nhất một lần |
| sequence flow `s -> t` | TOCL: gọi `t()` suy ra `s()` đã được gọi trong quá khứ |
| AND join | mọi predecessor phải đã được gọi |
| merge thường/XOR | ít nhất một predecessor phải đã được gọi |

OCL viết trong BPMN vẫn có `self` mang nghĩa Group của pool. Vì operation nằm
trên Role, translator chèn navigation ngược tới Group. Ví dụ `self.detailsDecided`
trong activity của lane `Initiator` trở thành
`self.meetingUnit.detailsDecided` trong `Initiator::activity()`.

Thứ tự không còn được lưu bằng một object/bảng trạng thái phụ. Với flow
`start -> decide`, translator sinh TOCL có dạng:

```tocl
sometime isCalled(start())

always (
  isCalled(decide())
  implies sometimePast isCalled(start())
)
```

Ràng buộc đầu tiên là bắt buộc: precondition của Start chỉ được đánh giá khi
Start thực sự xuất hiện trong filmstrip. Nếu thiếu liveness này, filmstrip rỗng
không gọi bất kỳ operation nào vẫn thỏa mọi luật precedence một cách rỗng.

Artefact thật định danh receiver và Group occurrence, để một lần gọi trong
MeetingUnit khác không thỏa nhầm predecessor của execution hiện tại.
Để tương thích với TOCL filmstrip hiện có, mỗi classifier trong file USE tích
hợp có thuộc tính `id : Integer`. Invariant TOCL luôn đặt trên Role của target;
flow cùng lane dùng `isCalled(op())`, còn flow đổi lane chọn Role nguồn duy nhất
trong cùng Group bằng `id` rồi dùng receiver-qualified `isCalled(...)`.

## Giới hạn

- OR và event-based gateway vẫn bị semantic validator từ chối.
- Mỗi lane phải khớp một ACL Role thuộc Group khai trong pool.
- TOCL kiểm tra precedence trên filmstrip; nó không tạo runtime token hay cấm
  lời gọi ngay trong USE shell khi TOCL chưa được nạp.
- Ràng buộc `sometimePast` diễn tả precedence lịch sử, không phân biệt nhiều
  lần chạy lặp của cùng process trên cùng Group nếu không có execution identity.
- Nếu flow đổi lane có predecessor Role đa trị, profile TOCL hiện tại không thể
  chọn đúng một receiver. Translator bỏ constraint đó và phát warning thay vì
  sinh TOCL sai kiểu.

Chi tiết ngữ nghĩa DSL nằm tại [BPMN](../dsl/bpmn.md), còn transition system
hình thức nằm tại [formal/bpmn.md](../../formal/bpmn.md).
