---
name: latex-rules
description: Quy ước LaTeX và văn phong học thuật tiếng Việt áp dụng cho mọi file .tex trong workspace này. Kích hoạt khi: kiểm tra hoặc sửa file .tex, viết nội dung mới, hoặc khi người dùng hỏi về quy tắc format. Skill này LUÔN được áp dụng ngầm khi viết/sửa bất kỳ nội dung LaTeX nào.
---

# Skill: Quy ước LaTeX & Văn phong học thuật

## Dấu câu và ký tự đặc biệt

| Trường hợp | Sai | Đúng |
|-----------|-----|------|
| Gạch ngang (en dash) | `-` hoặc `---` | `--` |
| Phạm vi số | `1-5` | `1--5` |
| Ngoặc kép | `"text"` hoặc `'text'` | `` ``text'' `` |
| Dấu ba chấm | `...` | `\ldots` |
| Ký hiệu phần trăm | `%` trong văn bản | `\%` |

**Ưu tiên dấu phẩy thay gạch ngang** cho mệnh đề phụ bổ nghĩa:
```latex
% Sai
Thành phần A --- đây là phần quan trọng nhất --- được thiết kế để...

% Đúng
Thành phần A, đây là phần quan trọng nhất, được thiết kế để...
```

**Viết tắt trong ngoặc dùng dấu phẩy, không dùng gạch ngang:**
```latex
% Sai
Song sinh số (Digital Twin --- DT)

% Đúng
Song sinh số (Digital Twin, DT)
```

---

## Danh sách (itemize / enumerate)

- Kết thúc mỗi `\item` bằng **dấu chấm phẩy** (`;`), riêng item cuối cùng bằng **dấu chấm** (`.`)
- Không dùng `\item` cho câu hoàn chỉnh trừ khi toàn bộ list là câu hoàn chỉnh
- Mỗi item: nhất quán về dạng (tất cả là cụm danh từ, hoặc tất cả là câu)

```latex
% Đúng
\begin{itemize}
  \item Thành phần A xử lý dữ liệu đầu vào;
  \item Thành phần B thực hiện suy luận;
  \item Thành phần C xuất kết quả.
\end{itemize}
```

---

## Văn phong

- **Không đại từ nhân xưng:** không dùng "chúng tôi", "tôi", "chúng ta"
  - Thay bằng: "luận văn đề xuất...", "đề xuất này...", "kết quả cho thấy..."
- **Không đoạn một câu:** mỗi đoạn ≥ 2 câu
- **Không ngoặc trong tiêu đề:** `\section`, `\subsection` không có `()`
- **Không section dính:** sau tiêu đề section phải có ≥1 đoạn văn trước subsection con

---

## Hình, bảng, listing

Mỗi đối tượng float phải:
1. Có `\label{}` với prefix đúng: `fig:`, `tab:`, `lst:`, `alg:`
2. Được tham chiếu bằng `\ref{}` **trước hoặc ngay sau** vị trí float
3. Có câu giải thích ý nghĩa trong văn bản (không chỉ "Hình X cho thấy...")

```latex
% Sai
\begin{figure}...\end{figure}
% (không có ref trong text)

% Đúng
Hình~\ref{fig:architecture} minh họa kiến trúc đề xuất với năm tầng chính.
\begin{figure}[htbp]
  \centering
  \includegraphics[...]{...}
  \caption{Kiến trúc phân tầng cho hệ WoDT}
  \label{fig:architecture}
\end{figure}
```

**Caption:** tự giải thích — người đọc hiểu hình/bảng mà không cần đọc text.

**Bảng:** caption đặt **trên** bảng (`\caption` trước `\begin{tabular}`).

**Hình:** caption đặt **dưới** hình (`\caption` sau `\includegraphics`).

---

## Tham chiếu chéo

```latex
% Chương / Section
Chương~\ref{chap:methodology} trình bày...
Phần~\ref{sec:evaluation} đánh giá...

% Hình / Bảng / Listing
Hình~\ref{fig:arch}    Bảng~\ref{tab:result}    Listing~\ref{lst:code}

% Phương trình
Phương trình~(\ref{eq:main})  % hoặc \eqref{eq:main}
```

---

## Tiêu đề section

- Viết hoa chữ cái đầu mỗi từ quan trọng (danh từ, động từ)
- Không kết thúc bằng dấu câu
- Không viết tắt chưa được giải thích
- Độ dài: ≤ 8 từ là lý tưởng
