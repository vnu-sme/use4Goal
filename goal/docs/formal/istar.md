# Đặc tả hình thức iStar

Một iStar model là:

\[
\mathcal I=(Actors,IE,Ref,Con,Qual,Need,Obs,Res,Assoc,Dep,Contract).
\]

Intentional element được mở rộng thành occurrence theo ACL state và context
path. AND/OR giữ nguyên context; `forall`/`pick` thêm một Role occurrence;
dependency truyền path sang actor chịu trách nhiệm.

Marking tổng:

\[
M_I=(M_G,M_T,M_Q,M_O),
\]

với `M_G:GOcc→(A,P,S)` và `M_T:TOcc→(Q,R)`. Khi Goal không active, marking
reset thành `(false,false,true)` và status là `UNKNOWN`. Trong một activation
episode:

\[
S'=S\land(\neg P_{prev}\lor P').
\]

- Achieve latch `P`; status là `PENDING` cho tới khi `P=true`, sau đó
  `FULFILLED` trong episode.
- Maintain yêu cầu `P=true` ngay và luôn giữ `S=true`; sai một lần là
  `VIOLATED` trong episode.
- Sustain chờ `P` lần đầu; sau đó mất `P` làm `S=false` và `VIOLATED`.
- Recur phản ánh trạng thái hiện tại: `P=true` là `FULFILLED`, ngược lại
  `PENDING`; thuộc tính vô hạn `GF P` cần model checking riêng.

Task cập nhật:

\[
Q'=Q\lor pre,\qquad R'=R\lor(Q'\land post).
\]

`(F,F)=UNKNOWN`, `(T,F)=PENDING`, `(T,T)=FULFILLED`. Refinement aggregate
status theo AND/OR; `forall` aggregate mọi occurrence và `pick` cần một
occurrence. Tập lượng hóa rỗng cho kết quả `UNKNOWN`, không dùng vacuous truth.
