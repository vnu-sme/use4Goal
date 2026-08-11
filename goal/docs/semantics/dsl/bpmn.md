# BPMN

BPMN mô tả **quan hệ chuyển trạng thái** và control flow của process. ACL xác
định state hợp lệ; BPMN xác định activity nào được phép chuyển state đó thành
state kế tiếp; iStar kiểm tra execution sinh ra có đáp ứng ý định hay không.

## Mô hình thực thi

- Pool có thể khai `for GroupType`; khi đó mỗi Group occurrence có một process
  instance và `self` là Group occurrence ấy.
- Activity có `pre` và `post`: pre là guard bắt đầu; post mô tả trạng thái phải
  đúng sau khi hoàn tất. Với fragment gán được hỗ trợ, runtime suy ra phép cập
  nhật từ post; các post khác chỉ dùng để kiểm tra và cần external adapter tạo
  trạng thái mới.
- Sequence flow giữ token. XOR chọn một nhánh, AND split sinh nhiều token và
  AND join đợi đủ token.
- Gateway chỉ định tuyến và không tự thay đổi domain state.

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

Model finding không cần một trace đầu vào: backend USE có thể biểu diễn snapshot,
token và quan hệ kế tiếp, rồi Model Validator tự sinh một execution hữu hạn thỏa
các OCL constraint hoặc một counterexample vi phạm iStar.

Định nghĩa transition system chuẩn thuộc [formal/bpmn.md](../../formal/bpmn.md).
