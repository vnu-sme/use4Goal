---
name: chap-experiment
description: Viết chương Thực nghiệm và Đánh giá cho luận văn/khóa luận tiếng Việt. Kích hoạt khi: "viết chương thực nghiệm", "mô tả môi trường thực nghiệm", "trình bày kết quả thực nghiệm", "viết phần cài đặt/framework", "review chương experiments".
---

# Skill: Chương Thực nghiệm & Đánh giá

## Mục tiêu của chương

Chứng minh đề xuất ở chương trước **hoạt động được trong thực tế**. Người đọc phải có thể hiểu được: chạy cái gì, trên cái gì, kết quả ra sao, và kết quả đó có nghĩa gì.

---

## Cấu trúc chuẩn

```
[Đoạn mở chương]  ← mục tiêu thực nghiệm, các câu hỏi cần trả lời

SECTION 1 — CÀI ĐẶT THỰC NGHIỆM
  1.1 Kiến trúc phần mềm / Framework
  1.2 Môi trường và công nghệ sử dụng
  1.3 Kịch bản thực nghiệm

SECTION 2 — CÁC CA THỰC NGHIỆM
  Ca 1: [tên cơ chế kiểm chứng]
  Ca 2: ...

SECTION 3 — KẾT QUẢ VÀ PHÂN TÍCH
  Kết quả theo từng câu hỏi nghiên cứu

SECTION 4 — ĐÁNH GIÁ VÀ HẠN CHẾ

[Kết luận chương]
```

---

## Quy tắc viết từng phần

### Phần Cài đặt — Kiến trúc phần mềm

- **Hình kiến trúc phần mềm là bắt buộc**, khác với hình kiến trúc đề xuất ở chương trước
- Mô tả rõ: thành phần nào tự phát triển, thành phần nào dùng thư viện/framework có sẵn
- Phân tầng rõ ràng: tầng nào giao tiếp với tầng nào qua giao thức gì

### Phần Cài đặt — Môi trường

Trình bày dạng bảng hoặc danh sách có cấu trúc:
```
- Ngôn ngữ/Framework: [tên + phiên bản]
- Cơ sở dữ liệu: [tên + phiên bản]
- Hệ điều hành / phần cứng: [...]
- Dữ liệu thực nghiệm: [tên dataset + kích thước + nguồn]
```

### Phần Kịch bản thực nghiệm

Mỗi kịch bản cần:
1. **Mục tiêu** — cơ chế nào đang được kiểm chứng
2. **Điều kiện** — input, trạng thái ban đầu
3. **Kỳ vọng** — kết quả mong đợi là gì

### Phần Kết quả và phân tích

- Không chỉ báo cáo số liệu — phải **giải thích tại sao** kết quả như vậy
- Mỗi bảng/hình kết quả phải được nhắc đến trong văn bản trước hoặc ngay sau
- Mỗi câu hỏi nghiên cứu cần một đoạn trả lời rõ ràng

> Sai: "Bảng 4.1 cho thấy kết quả đạt được."
> Đúng: "Kết quả trong Bảng~\ref{tab:result} cho thấy cơ chế X thành công vì... Điều này xác nhận RQ1."

### Phần Đánh giá và hạn chế

Trả lời thẳng thắn:
- Đề xuất trả lời được bao nhiêu phần của câu hỏi nghiên cứu?
- Hạn chế cụ thể là gì? (không nói chung chung)
- Hạn chế đó ảnh hưởng đến tính tổng quát như thế nào?

---

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Mô tả kịch bản nhưng không nêu mục tiêu kiểm chứng | Thêm câu "Ca này kiểm chứng cơ chế..." |
| Kết quả chỉ có bảng, không có phân tích | Viết ≥2 câu phân tích cho mỗi bảng/hình |
| Hạn chế viết chung chung | Mỗi hạn chế phải cụ thể và giải thích tác động |
| Thiếu hình kiến trúc phần mềm | Vẽ và thêm hình, tham chiếu trong văn bản |
| Không trả lời câu hỏi nghiên cứu | Thêm subsection đánh giá theo từng RQ |
