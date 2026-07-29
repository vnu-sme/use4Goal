---
name: thesis-layout
description: Quản lý bố cục tổng thể luận văn/khóa luận tiếng Việt: cấu trúc mục lục, quy tắc viết abstract/tóm tắt, phần mở đầu, kết luận, và tính dẫn dắt giữa các chương. Kích hoạt khi: "kiểm tra bố cục luận văn", "viết abstract/tóm tắt", "viết phần mở đầu", "viết kết luận chương", "cấu trúc mục lục có đúng không", "kiểm tra tính liên kết giữa các chương".
---

# Skill: Quản lý bố cục Luận văn

## Bố cục tổng thể chuẩn (Luận văn ThS)

```
Trang bìa
Lời cam đoan
Lời cảm ơn
Tóm tắt (tiếng Việt)        ← ≤ nửa trang
Abstract (tiếng Anh)        ← ≤ nửa trang
Mục lục
Danh mục hình vẽ / bảng biểu / từ viết tắt
─────────────────────────────────────────
Phần mở đầu                 ← không đánh số chương
Chương 1: Giới thiệu        ← (hoặc gộp vào Mở đầu)
Chương 2: Nền tảng + CCTL
Chương 3: Phương pháp đề xuất
Chương 4: Thực nghiệm & Đánh giá
Chương 5: Kết luận & Hướng phát triển
─────────────────────────────────────────
Tài liệu tham khảo
Phụ lục (nếu có)
```

---

## Tóm tắt / Abstract

**5 yếu tố bắt buộc, theo thứ tự:**
1. Bối cảnh — lĩnh vực và bài toán tổng quát (1–2 câu)
2. Vấn đề — gap cụ thể chưa được giải quyết (1–2 câu)
3. Phương pháp — tên đề xuất và cơ chế chính (2–3 câu)
4. Kết quả — thực nghiệm trên gì, kết quả ra sao (1–2 câu)
5. Đóng góp — ý nghĩa rộng hơn (1 câu)

**Quy tắc:**
- Độ dài: ≤ nửa trang (khoảng 150–200 từ)
- Không trích dẫn `\cite{}`
- Không dùng đại từ nhân xưng
- Tự đứng độc lập — người đọc không cần đọc bài mới hiểu được

---

## Phần mở đầu

Cấu trúc 4 đoạn:
1. **Tính cấp thiết** — bài toán quan trọng vì sao với thực tế
2. **Mục tiêu và phạm vi** — đề xuất gì, trong phạm vi nào
3. **Đóng góp** — liệt kê cụ thể (bullet, ≥2 điểm)
4. **Cấu trúc luận văn** — mỗi chương một câu mô tả

---

## Đoạn mở chương (bắt buộc)

Mỗi chương phải có đoạn mở **trước** section đầu tiên. Đoạn này:
- Nhắc lại ngắn gọn chương trước kết thúc ở đâu
- Giải thích chương này sẽ làm gì và tại sao cần
- Mô tả cấu trúc chương (các section chính)

> Ví dụ: "Chương trước đã xác định bốn khoảng trống nghiên cứu. Chương này đề xuất kiến trúc phân tầng để lấp đầy các khoảng trống đó. Phần đầu trình bày tổng quan kiến trúc, phần sau đi vào từng thành phần."

---

## Kết luận chương (bắt buộc)

Mỗi chương phải có `\section*{Kết luận Chương X}`. Cấu trúc:
- Câu 1: "Chương này đã trình bày..."
- Câu 2: Tóm tắt kết quả/đóng góp chính của chương
- Câu 3: Cầu nối sang chương tiếp theo: "Chương tiếp theo sẽ..."

Không liệt kê bullet — viết thành đoạn văn liên kết (≥ 3 câu).

---

## Chương Kết luận (Chương cuối)

```
Section 1: Tóm tắt đóng góp
  - Trả lời từng câu hỏi nghiên cứu (RQ1, RQ2...)
  - Đóng góp cụ thể (không mơ hồ)

Section 2: Hạn chế
  - Liệt kê thẳng thắn, cụ thể
  - Tại sao hạn chế này tồn tại (không apologize)

Section 3: Hướng nghiên cứu tiếp theo
  - ≥ 3 hướng, mỗi hướng ≥ 2 câu giải thích
  - Ưu tiên: hướng giải quyết hạn chế đã nêu
```

---

## Kiểm tra tính dẫn dắt giữa các chương

Khi review toàn bộ luận văn, kiểm tra chuỗi:

```
Mở đầu → đặt câu hỏi nghiên cứu
  ↓
Chương Nền tảng → xác định gap (phải khớp với câu hỏi)
  ↓
Chương Phương pháp → đề xuất giải quyết gap đó
  ↓
Chương Thực nghiệm → chứng minh đề xuất hoạt động
  ↓
Chương Kết luận → trả lời câu hỏi nghiên cứu ban đầu
```

Nếu bất kỳ mắt xích nào không khớp → báo lỗi dẫn dắt.
