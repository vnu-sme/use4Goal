---
name: chap-method
description: Viết chương Phương pháp đề xuất cho luận văn/khóa luận tiếng Việt. Kích hoạt khi: "viết chương phương pháp", "trình bày kiến trúc đề xuất", "viết phần thiết kế hệ thống", "giải thích thuật toán/cơ chế", "review chương methodology".
---

# Skill: Chương Phương pháp đề xuất

## Nguyên tắc cốt lõi

Trình bày **từ tổng quan đến chi tiết**. Người đọc phải hiểu bức tranh toàn cảnh trước khi đi vào từng thành phần. Mỗi thành phần cần ví dụ minh họa chạy xuyên suốt.

---

## Cấu trúc chuẩn

```
[Đoạn mở chương]  ← nhắc lại gap từ chương trước, giới thiệu tên đề xuất

SECTION 1 — TỔNG QUAN KIẾN TRÚC
  Hình kiến trúc tổng thể  ← BẮT BUỘC
  Mô tả luồng từ input → output
  Giới thiệu tên các thành phần chính

SECTION 2 — THÀNH PHẦN / TẦNG 1
  Tại sao cần thành phần này (trước tiên)
  Thiết kế chi tiết
  Ví dụ minh họa

SECTION 3 — THÀNH PHẦN / TẦNG 2
  ...

SECTION N — CƠ CHẾ TƯƠNG TÁC / TÍCH HỢP
  Các thành phần phối hợp như thế nào

[Kết luận chương]
```

---

## Quy tắc viết từng phần

### Tổng quan kiến trúc

- **Hình kiến trúc là bắt buộc** và phải được tham chiếu ngay: `Hình~\ref{fig:architecture} minh họa...`
- Mô tả hình theo luồng xử lý: "Đầu vào A đi qua thành phần B, được xử lý thành C, sau đó..."
- Đặt tên rõ cho từng thành phần — tên này phải nhất quán trong toàn chương

### Mỗi thành phần

Viết theo thứ tự: **Tại sao → Cái gì → Như thế nào**

```
[Tại sao] Thành phần X được đặt ra để giải quyết vấn đề Y, vốn chưa được
các hướng tiếp cận trước đây xử lý vì...

[Cái gì] X bao gồm ba yếu tố: A, B và C.

[Như thế nào] A hoạt động bằng cách... [kèm algorithm/listing/diagram]
```

> Sai: Giải thích thuật toán từng dòng mà không nói tại sao cần nó
> Đúng: Nêu vấn đề → nêu giải pháp → rồi mới chi tiết cơ chế

### Ví dụ minh họa

Chọn **một ví dụ cụ thể** và duy trì xuyên suốt chương (không đổi ví dụ giữa chừng):
- Giới thiệu ví dụ ở đầu chương (Section 1)
- Mỗi thành phần tiếp theo áp dụng lên cùng ví dụ đó
- Kết thúc: ví dụ được giải quyết hoàn chỉnh

### Section tích hợp / cơ chế phối hợp

Trả lời: "Khi A cần B thì điều gì xảy ra?" — mô tả luồng tương tác, không chỉ liệt kê API.

---

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Mô tả chi tiết trước khi có tổng quan | Thêm section tổng quan + hình kiến trúc |
| Giải thích "cái gì" mà không giải thích "tại sao" | Thêm đoạn motivation trước mỗi thành phần |
| Subsubsection dính (không có đoạn nối) | Thêm 1–2 câu bridge sau listing/itemize |
| Không có ví dụ minh họa | Chọn 1 running example và áp dụng xuyên suốt |
| Hình không được tham chiếu | Mỗi `\figure` phải có `\ref{}` + câu giải thích |
