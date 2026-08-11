# Bản đồ tài liệu GOAL plugin

Đây là điểm bắt đầu duy nhất của tài liệu trong `goal/docs`. Tài liệu được chia
thành hai nhánh: **semantics** giải thích hệ thống và ý nghĩa vận hành;
**formal** dành cho các định nghĩa toán học dùng để kiểm chứng.

## 1. Kiến trúc

- [ARCHITECTURE.md](ARCHITECTURE.md): package, module, chiều phụ thuộc và luồng
  action–parser–model–view của GOAL plugin.
- [semantics/CONCEPTS.md](semantics/CONCEPTS.md): từ điển khái niệm dùng chung.
- [semantics/MODULES.md](semantics/MODULES.md): trách nhiệm và API của module.

## 2. Ngữ nghĩa DSL

- [ACL](semantics/dsl/acl.md): schema tổ chức và miền trạng thái.
- [iStar](semantics/dsl/istar.md): intentional model, occurrence và marking.
- [BPMN](semantics/dsl/bpmn.md): control flow và chuyển trạng thái.
- [AOL](semantics/dsl/aol.md): instance/snapshot của ACL.
- [ISCN](semantics/dsl/iscn.md): kịch bản thay đổi trạng thái và marking iStar.

## 3. Luật chuyển

- [ACL → USE](semantics/transformations/acl2use.md)
- [iStar → USE](semantics/transformations/istar2use.md)
- [BPMN → USE](semantics/transformations/bpmn2use.md)
- [ACL + iStar → USE](semantics/transformations/aclIstar2use.md)
- [ACL + BPMN → USE](semantics/transformations/aclBpmn2use.md)
- [ACL + iStar + BPMN → USE](semantics/transformations/aclIstarBpmn2use.md)
- [ACL + iStar + BPMN → Event-B](semantics/transformations/aclIstarBpmn2eventB.md)

Mỗi luật dịch hoàn chỉnh phải nêu: nguồn, đích, ánh xạ khái niệm, OCL/TOCL
được sinh, điều kiện bảo toàn ngữ nghĩa, construct chưa hỗ trợ và ít nhất một
ví dụ source–target.

## 4. Đặc tả hình thức

- [formal/acl.md](formal/acl.md)
- [formal/istar.md](formal/istar.md)
- [formal/bpmn.md](formal/bpmn.md)

Ba file này là nơi duy nhất đặt định nghĩa tập hợp, marking, transition system,
well-formedness và các mệnh đề cần chứng minh. Tài liệu semantics được phép giải
thích lại bằng lời nhưng không được đưa ra một công thức cạnh tranh.

## 5. Thứ tự đọc

```text
ARCHITECTURE
    -> semantics/dsl/{ACL,ISTAR,BPMN}
    -> semantics/transformations/*
    -> formal/{ACL,ISTAR,BPMN}
```

AOL và ISCN là đầu vào instance/scenario, không phải điều kiện bắt buộc của
model finding. Khi dùng USE Model Validator, trạng thái và execution hữu hạn có
thể được sinh từ mô hình USE cùng các OCL constraint.
