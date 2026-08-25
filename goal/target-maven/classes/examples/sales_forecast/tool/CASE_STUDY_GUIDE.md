# Sales Forecast — hướng dẫn đọc case study

Hai file trong thư mục này trình bày lại case study ở mức tổng quan để có thể
mở trực tiếp bằng piStar và bpmn.io:

- `sales_forecast_full_sr_pistar.txt`: mô hình mục tiêu và dependency giữa các
  actor.
- `sales_forecast_full_process.bpmn`: quy trình chính thức và workaround được
  quan sát trong case study.

Các file `sales_forecast_full.istar` và `sales_forecast_full.bpmn2` ở thư mục
cha vẫn được giữ làm bản đối chiếu chi tiết với tài liệu nguồn. Bản trong thư
mục `tool` ưu tiên khả năng đọc: nó giữ các actor, mục tiêu, trách nhiệm và
điểm quyết định chính, nhưng gom các vòng lặp nội bộ vào collapsed subprocess.

## 1. Bối cảnh nghiệp vụ

Một **Customer Manager** tiếp nhận yêu cầu mua hàng và chuẩn bị một price
proposal. Proposal có thể cần thông tin kỹ thuật như bill of materials (BoM)
và high-level design (HLD), điều khoản dịch vụ, đánh giá profitability, kiểm
tra tài chính và kiểm tra tín dụng. Sau khi nhận đủ các phê duyệt bắt buộc,
proposal được gửi cho khách hàng và giao dịch được chuyển thành purchase order.

Quy trình phải dung hòa hai góc nhìn:

- Customer Manager muốn tiến triển giao dịch, phản hồi nhanh và làm hài lòng
  khách hàng.
- Management muốn duy trì lợi nhuận, kiểm soát rủi ro và đặc biệt là không gửi
  proposal chưa được phê duyệt.

Customer Manager không tự hoàn thành được toàn bộ công việc mà phụ thuộc vào
Presale Engineer, Service Agreements, Commercial Approver, Financial Control
và Credit Control.

## 2. Cách đọc mô hình iStar

Mở `sales_forecast_full_sr_pistar.txt` bằng piStar và đọc từ ba actor trung tâm:

1. **Management** có goal gốc `Governed sales proposal completed`. Goal này
   được phân rã thành bốn trạng thái cần đạt: một proposal hợp lệ đã được giao;
   profitability đã được đánh giá; các clearance bắt buộc đã được cấp; và
   thông tin doanh số dự kiến đã được ghi nhận.
2. **Customer Manager** có goal `Price proposal progressed`. Goal này đòi hỏi
   proposal được chuẩn bị, được phê duyệt và được giao cho khách hàng. Các task
   của actor này ghi nhận deal, yêu cầu thông tin kỹ thuật, kiểm tra điều khoản
   dịch vụ, gửi yêu cầu phê duyệt, gửi proposal và chuyển deal sang order.
3. **Commercial Approvers** đánh giá profitability và kết thúc một approval
   round bằng phê duyệt, từ chối hoặc yêu cầu sửa proposal.

Các actor hỗ trợ bổ sung phần việc chuyên môn:

- **Presale Engineer** chuẩn bị BoM và HLD.
- **Service Agreements** kiểm tra và phê duyệt điều khoản dịch vụ.
- **Financial and Credit Control** kiểm tra deal giá trị lớn và tín dụng khách
  hàng.
- **Customer** nhận price proposal và quan tâm đến tính kịp thời, khả dụng của
  nó.

Dependency nối qua actor boundary cho biết ai cần kết quả của ai. Ví dụ,
Customer Manager phụ thuộc Presale Engineer về `Technical package`, phụ thuộc
Commercial Approvers về `Approved price proposal`, còn Management phụ thuộc
Customer Manager để đạt trạng thái `Authorized proposal delivered`.

Mỗi goal có một custom attribute chuỗi tên `condition`. Giá trị của attribute
là biểu thức OCL xác định trạng thái làm goal đó thỏa mãn. Ví dụ:

```ocl
-- Proposal delivered to customer
self.group.proposalSent

-- Authorized proposal delivered
self.group.proposalSent
and self.group.approvalStatus = #approved
and self.group.sentRevision = self.group.approvedRevision
and self.group.sentRevision = self.group.currentRevision
```

Như vậy tên goal mô tả trạng thái đích; OCL mới là định nghĩa chính xác của
trạng thái đó. Goal không còn được viết dưới dạng một luật điều khiển process.

Task `Prepare and send proposal outside IS` nằm riêng ở cuối Customer Manager.
Nó mô tả workaround thực tế, không phải một bước thuộc quy trình chính thức.

## 3. Cách đọc BPMN chính thức

Pool trên cùng, `PRESCRIBED PROCESS`, là quy trình được tổ chức quy định. Đọc
theo sequence flow:

1. Customer Manager ghi nhận khách hàng và deal.
2. Presale Engineer chuẩn bị technical package cần thiết.
3. Service Agreements xác nhận điều khoản dịch vụ.
4. Customer Manager gửi proposal vào vòng phê duyệt.
5. Commercial Approver đánh giá profitability và đưa ra quyết định thương mại.
6. Financial Control và Credit Control thực hiện clearance khi loại deal hoặc
   khách hàng cụ thể yêu cầu.
7. Gateway `All required approvals granted?` kiểm tra kết quả tổng hợp. Nhánh
   `No` kết thúc bằng `Proposal rejected`; nhánh `Yes` mới cho phép thực hiện
   `Send approved price proposal`.
8. Order Operations hoàn tất kit components và tạo purchase order.

Các điều kiện như “có cần HLD không?”, “deal có vượt ngưỡng tài chính không?”,
hay “proposal bị trả lại để sửa không?” thuộc logic nội bộ của bộ phận tương
ứng. Vì vậy chúng được đặt trong collapsed subprocess thay vì bung thành hàng
loạt gateway ở sơ đồ tổng thể. Sơ đồ tổng thể chỉ giữ gateway quyết định có ý
nghĩa xuyên suốt toàn quy trình.

## 4. Activity thuộc lane nào?

| Lane | Activity ở mức tổng quan |
|---|---|
| Customer Manager | Record customer and deal; Submit proposal for approval; kiểm tra kết quả phê duyệt; Send approved price proposal |
| Presale Engineer | Prepare required technical package |
| Service Agreements | Validate service terms |
| Commercial Approver | Obtain commercial approval |
| Financial Control | Obtain financial clearance if required |
| Credit Control | Obtain credit clearance if required |
| Order Operations | Complete kit components; Create purchase order |

Danh sách này cũng được đặt thành text annotation ngay trong từng lane của file
BPMN, vì vậy có thể xem trực tiếp trong bpmn.io mà không cần quay lại tài liệu
này.

## 5. Workaround được quan sát

Pool thứ hai là một process riêng, không phải nhánh hợp lệ của prescribed
process:

```text
Urgent customer request
→ Prepare proposal outside IS
→ Send proposal before approval
```

Workaround phản ánh tình huống Customer Manager muốn trả lời khách hàng nhanh
nên chuẩn bị proposal ngoài hệ thống rồi gửi trực tiếp. Hai process đều gửi
message đến cùng participant `Customer`, nhưng trạng thái nghiệp vụ tại thời
điểm gửi khác nhau: quy trình chính thức đã đi qua vòng phê duyệt, workaround
thì chưa.

## 6. Tại sao case study vẫn chứa lỗi?

Lỗi không phải là lỗi cú pháp BPMN hay một deadlock trong control flow. Mỗi
process riêng lẻ vẫn có thể chạy từ start đến end. Lỗi nằm ở sự không nhất quán
giữa hành vi workaround và ý định của tổ chức:

- `Send proposal before approval` vẫn làm
  `self.group.proposalSent = true`, nên trạng thái `Proposal delivered to
  customer` của Customer Manager được đạt.
- Tuy nhiên `approvalStatus` chưa phải `#approved` và revision được gửi chưa
  trùng revision được duyệt. Vì vậy trạng thái `Authorized proposal delivered`
  của Management không được đạt.

Do đó lỗi không còn được diễn đạt bằng một câu cấm đặt trong goal model. Nó
được quan sát bằng hai condition trên cùng trạng thái ACL: workaround đạt trạng
thái giao hàng nhưng không đạt trạng thái giao hàng đã được ủy quyền. Ở giai
đoạn hiện tại, hai mô hình chỉ cần làm rõ tình huống nghiệp vụ đó; việc kiểm
chứng được tách sang các verification model của case study.
