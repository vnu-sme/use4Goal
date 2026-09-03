---
name: citation-rules
description: Quy tắc trích dẫn tài liệu tham khảo trong luận văn/khóa luận tiếng Việt. Kích hoạt khi: "cách trích dẫn bài báo", "sửa tham chiếu", "kiểm tra citation", "thêm tài liệu tham khảo", "cite ở đâu trong câu", "format bibliography".
---

# Skill: Quy tắc trích dẫn (Citation Rules)

## Nguyên tắc cơ bản

Mỗi **claim không phải kiến thức phổ thông** phải có citation. Nguyên tắc: nếu một người đọc có thể hỏi "Bạn lấy điều này từ đâu?" thì cần cite.

---

## Vị trí đặt citation trong câu

**Đặt sau claim cụ thể, không đặt cuối câu dài.**

```latex
% Đúng — cite ngay sau claim
Song sinh số là biểu diễn số của thực thể vật lý~\cite{grieves2017}.

% Đúng — nhiều claim, cite riêng từng cái
Đô thị thông minh~\cite{shahat2021} và y tế số~\cite{croatti2020}
đều ứng dụng DT rộng rãi.

% Sai — cite cuối câu dài nhưng claim đầu câu không có nguồn
Song sinh số là biểu diễn số (định nghĩa từ A),
được ứng dụng trong y tế (từ B) và sản xuất (từ C)~\cite{chỉ_cite_một_nguồn}.
```

---

## Khi nào PHẢI cite

- Định nghĩa khái niệm kỹ thuật (lần đầu xuất hiện)
- Số liệu thống kê, tỉ lệ, con số cụ thể
- Kết quả từ nghiên cứu khác ("A cho thấy X đạt Y%")
- Phương pháp hoặc thuật toán được kế thừa
- Nhận định về hạn chế của hướng tiếp cận khác

---

## Khi nào KHÔNG cần cite

- Kiến thức phổ thông trong lĩnh vực ("máy học là...")
- Mô tả framework/công cụ đã được giới thiệu trong cùng section
- Kết luận từ thực nghiệm của chính luận văn

---

## Format theo loại tài liệu (BibTeX)

### Bài báo hội nghị
```bibtex
@inproceedings{key,
  author    = {Họ, Tên and ...},
  title     = {Tiêu đề bài báo},
  booktitle = {Tên hội nghị (tên đầy đủ)},
  year      = {2024},
  pages     = {1--10}
}
```

### Bài báo tạp chí
```bibtex
@article{key,
  author  = {...},
  title   = {...},
  journal = {Tên tạp chí},
  volume  = {12},
  number  = {3},
  year    = {2024},
  pages   = {100--115}
}
```

### Tài liệu kỹ thuật / Chuẩn / Đặc tả
```bibtex
@techreport{key,
  author      = {Tên tổ chức},
  title       = {Tên tài liệu},
  institution = {Tên tổ chức phát hành},
  year        = {2024},
  note        = {Available: URL}
}
```

### Trang web / Tài liệu online
```bibtex
@misc{key,
  author       = {Tên tác giả hoặc tổ chức},
  title        = {Tên tài liệu},
  howpublished = {\url{https://...}},
  year         = {2024},
  note         = {Truy cập: tháng năm}
}
```

---

## Cách viết câu có citation

**Không để citation lơ lửng (orphan citation):** mỗi `\cite{}` phải có văn cảnh giải thích vai trò của tài liệu đó.

```latex
% Sai — orphan
Hệ thống đã được nghiên cứu~\cite{abc2020}.

% Đúng — có vai trò rõ
Ricci và cộng sự đề xuất kiến trúc WoDT dựa trên đồ thị tri thức
phân tán~\cite{ricci2022web}.

% Đúng — nhiều nguồn, gom lại
Nhiều nghiên cứu xác nhận xu hướng tích hợp DT với
MAS~\cite{mariani2022, pretel2024}.
```

---

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| Cite cùng một nguồn liên tục | Chỉ cite lần đầu định nghĩa, sau đó dùng tên viết tắt |
| Không cite khi mô tả limitation của người khác | Phải cite — đây là claim về tác phẩm của họ |
| URL trực tiếp trong văn bản | Dùng `\url{}` hoặc đưa vào `\footnote{}` |
| Thiếu năm hoặc tên hội nghị trong bib | Kiểm tra entry có đủ `author`, `title`, `year`, `venue` |
