# BPMN

BPMN mô tả **quan hệ chuyển trạng thái** và control flow của process. ACL xác
định state hợp lệ; BPMN xác định activity nào được phép chuyển state đó thành
state kế tiếp; iStar kiểm tra execution sinh ra có đáp ứng ý định hay không.

## Mô hình thực thi

- Pool có thể khai `for GroupType`; Group occurrence này là scope chung để liên
  hệ các Role occurrence nằm trong những lane của cùng quy trình.
- Hợp đồng được ánh xạ lên flow `f = (u,v)`: `Pre_B(f)` lấy `pre` của node đích
  `v`; `Post_B(f)` lấy `post` của node nguồn `u`, hoặc `post` riêng khai trên
  outgoing flow của gateway.
- Sequence flow giữ token. XOR chọn một nhánh, AND split sinh nhiều token và
  AND join đợi đủ token.
- Gateway chỉ định tuyến. Mỗi outgoing flow có thể khai
  `flow target post {[ ... ]}`; bộ đánh giá hình thức không cần một khái niệm
  guard riêng.

## Operation contract và thứ tự khi dịch sang USE/TOCL

Mỗi flow node trong lane `R` được dịch thành operation `R::node()`. Với activity
`a`, `pre`/`post` nguồn trở thành `DomainPre_a`/`DomainPost_a`. OCL nguồn coi
`self` là Group của pool; trong operation trên Role, translator thêm navigation
Role → Group trước các biểu thức đó.

Sequence flow không sinh class hay token state. File TOCL đi kèm phát biểu:
khi operation của target được gọi, operation predecessor đã được gọi ở một
snapshot quá khứ trên Role thuộc cùng Group. AND join cần tất cả predecessor;
merge thường/XOR chỉ cần một predecessor.

Để chạy bằng TOCL tool hiện tại, invariant được đặt trên Role của target và
classifier USE có `id`. Predecessor ở lane khác chỉ được định danh trực tiếp
khi Role nguồn là đơn trị trong Group; predecessor đa trị được báo warning.

Không có `post` nghĩa là operation không có domain postcondition, không phải
`post = false`. Control-flow postcondition vẫn luôn được sinh.

## Ví dụ

```bpmn2
pool MeetingOrganization for MeetingUnit {
  name "Meeting organization"
  lane Initiator;
}

start begin {
  lane Initiator
  flow decideDetails
}

activity decideDetails {
  type task
  lane Initiator
  pre    {[ not self.detailsDecided ]}
  post   {[ self.detailsDecided ]}
  flow finish
}

end finish {
  lane Initiator
}
```

Gateway với postcondition riêng trên từng flow:

```bpmn2
gateway choice {
  lane Initiator
  type xor
  flow electronic post {[ self.electronicAvailable ]}
  flow manual post {[ not self.electronicAvailable ]}
}
```

USE kiểm tra domain pre/post của từng bước, còn TOCL plugin kiểm tra thứ tự trên
filmstrip tạo bởi chuỗi lời gọi operation. Không có class `ProcessState` riêng.

Định nghĩa transition system chuẩn thuộc [formal/bpmn.md](../../formal/bpmn.md).
