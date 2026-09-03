# ACL + BPMN → USE/OCL

Phép dịch tích hợp ghép class diagram/invariant của [ACL → USE](acl2use.md) với
operation theo lane Role và precedence TOCL của [BPMN → USE](bpmn2use.md).

Mỗi flow node trong `lane R` sinh operation trên class ACL Role `R`, không phải
trên Group `G`. `self` của OCL nguồn vẫn chỉ Group occurrence của pool, nên
translator chiếu nó qua navigation Role → Group; ví dụ OCL của lane `Organizer`
dùng `self.detailsDecided` sẽ thành `self.meetingUnit.detailsDecided`.

Không sinh `BPMN_*_ProcessState`, token attribute hoặc composition trạng thái.
Mỗi target operation được ràng buộc bằng TOCL để predecessor tương ứng phải có
lời gọi trong quá khứ trên Role occurrence thuộc cùng `G`. AND join dùng hội;
merge thường và XOR dùng tuyển giữa các predecessor.

TOCL hiện có giả định classifier có `id`, vì vậy riêng artefact tích hợp
ACL+BPMN bổ sung `id : Integer` nếu classifier chưa khai báo. Invariant temporal
đặt trên lane Role đích và không dùng biến iterator làm receiver của `isCalled`.
Cross-lane receiver chỉ được sinh khi Role nguồn có multiplicity tối đa là một;
trường hợp đa trị được báo warning và không sinh công thức sai.

OCL cũ dùng navigation như `source_Agent_plays_ChildRole` hoặc
`source_ParentRole_plays_ChildRole` được chuẩn hóa theo tên role ngắn. Nếu
`ChildRole extends ParentRole`, navigation ngược đúng là `.parentRole`; từ group
tới child role là `.childRole`, còn từ child role tới group là `.groupType`.
Translator thực hiện bước tương thích này trước khi USE compiler kiểm tra
contract.
