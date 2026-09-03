# Đặc tả hình thức iStar

Tài liệu này đặc tả iStar của dự án ở hai mức: mô hình khai báo và ngữ nghĩa
khi mô hình được đánh giá trên một kịch bản trạng thái ACL. iStar không trực
tiếp thay đổi trạng thái miền; BPMN thay đổi ACL, còn iStar đọc lại toàn bộ
trạng thái mới để tạo một goal marking tại mỗi checkpoint.

## 1. Mô hình khai báo

Một goal model là:

\[
\mathcal I=(Act,IE,Ref,Con,Qua,Need,Assoc,Dep,Ctr),
\]

trong đó:

- \(Act=Agent\uplus Role\) là tập actor; Role phải tham chiếu đến Role cùng tên
  trong ACL;
- \(IE=G\uplus T\uplus Q\uplus Res\) lần lượt là Goal, Task, Quality và
  Resource;
- \(Ref=Ref_{and}\uplus Ref_{or}\uplus Ref_{forall}\uplus Ref_{pick}\) là các
  quan hệ refinement;
- \(Con\subseteq IE\times Q\times\{make,help,hurt,break\}\) là contribution;
- \(Qua\subseteq Q\times(G\cup T\cup Res)\) là qualification;
- \(Need\subseteq T\times Res\) là needed-by;
- \(Assoc\) là quan hệ giữa các actor;
- \(Dep\) là strategic dependency;
- \(Ctr\) chứa OCL `activation`, `condition`, `pre` và `post`.

Một dependency có dạng:

\[
d=(a_d,e_d,x,a_e,e_e),
\]

với depender \(a_d\), phần tử biên tùy chọn \(e_d\), dependee \(a_e\), phần
tử biên tùy chọn \(e_e\), và **dependum \(x\) được dependency composition-own**.
\(x\) tự nó là một Goal, Task, Quality hoặc Resource cụ thể; không có
`DependumKind` lặp lại kiểu của nó.

`Obstacle`, `ObstacleType`, `Obstruction` và `Resolution` không thuộc
metamodel này.

## 2. Kịch bản, checkpoint và occurrence

Một kịch bản BPMN trên ACL sinh chuỗi trạng thái:

\[
\Gamma=\Sigma _0\xrightarrow{b_1}\Sigma _1
\xrightarrow{b_2}\cdots\xrightarrow{b_n}\Sigma _n,
\]

trong đó \(b_i\) là activity event và \(\Sigma_i\) là toàn bộ object system
ACL sau event đó. iStar tạo chuỗi marking đồng bộ:

\[
\mathcal T=
(\Sigma_0,M_0)\xrightarrow{b_1;evalAll}(\Sigma_1,M_1)
\cdots\xrightarrow{b_n;evalAll}(\Sigma_n,M_n).
\]

`evalAll` là một bước bắt buộc. Không activity nào được chạy tiếp giữa
\(b_i\) và `evalAll`. Bước này:

1. dựng lại mọi occurrence từ các Role instance và Group context trong
   \(\Sigma_i\);
2. đánh giá OCL của **tất cả** Goal/Task occurrence, không chỉ những phần tử
   được cho là chịu ảnh hưởng bởi \(b_i\);
3. cập nhật lịch sử thời gian của từng occurrence;
4. lan truyền refinement, quantified refinement, dependency và contribution
   đến fixpoint để thu được \(M_i\).

Nhờ vậy, một activity của một quy trình có thể làm thay đổi marking của goal
thuộc quy trình hay actor khác nếu cả hai cùng đọc một trạng thái ACL.

Mỗi intentional element \(e\) được mở rộng thành occurrence:

\[
o=(e,\rho),
\]

với \(\rho=(o_0,\ldots,o_k)\) là context path các ACL object được bind. Binding
trong cùng là `self`; binding bao ngoài kế tiếp là `self.outer`. AND/OR không
thêm binding. `forall`/`pick` thêm actor occurrence được lượng hóa. Dependency
truyền context của depender sang dependee. Hai role chỉ cùng một miền lượng
hóa khi chúng thuộc cùng Group-instance scope của ACL.

Occurrence không tồn tại trong \(\Sigma_i\) nằm ngoài miền của \(M_i\). Một
occurrence có tồn tại nhưng chưa active nhận status `UNKNOWN`.

Marking đầy đủ tại checkpoint \(i\) là:

\[
M_i=(M_G^i,M_T^i,M_Q^i),
\]

với \(M_G^i:GOcc_i\to(A,P,S)\),
\(M_T^i:TOcc_i\to(Q,R)\), và
\(M_Q^i:QOcc_i\to\{UNKNOWN,TRUE,FALSE\}\). Resource là dữ liệu/cấu trúc được
Goal và Task tham chiếu, không có marking runtime độc lập. Không có thành phần
marking cho Obstacle vì Obstacle không tồn tại trong metamodel.

## 3. Goal marking tại một checkpoint

Với mỗi Goal occurrence \(o\), trạng thái nội bộ là:

\[
M_G(o)=(A,P,S).
\]

- \(D_i(o)\): occurrence đang được cây goal hoặc dependency yêu cầu;
- \(\alpha_i(o)\): giá trị OCL `activation`, mặc định `true` nếu activation
  được kế thừa;
- \(p_i(o)\): giá trị OCL `condition` trên \(\Sigma_i\);
- \(A_i=D_i\land\alpha_i\): goal đang active;
- \(P_i\): predicate đã được xử lý theo GoalType;
- \(S_i\): từ đầu activation episode đến checkpoint hiện tại chưa có chuyển
  tiếp \(P=true\rightarrow P=false\).

Khi \(A_i=false\), episode kết thúc và marking reset:

\[
(A_i,P_i,S_i)=(false,false,true),\qquad status_i=UNKNOWN.
\]

Khi goal chuyển từ inactive sang active, đặt \(enter_i=true\). Baseline trước
checkpoint đầu của episode là:

\[
B_t=\begin{cases}
true & t=Maintain,\\
false & t\in\{Achieve,Sustain\}.
\end{cases}
\]

Đặt \(\widehat P_{i-1}=B_t\) nếu `enter`, ngược lại là \(P_{i-1}\); và
\(\widehat S_{i-1}=true\) nếu `enter`, ngược lại là \(S_{i-1}\). Khi đó:

\[
S_i=\widehat S_{i-1}\land
(\neg\widehat P_{i-1}\lor P_i).
\]

Ký hiệu status quan sát dùng trong các chuỗi dưới đây:

- `U` = `UNKNOWN`;
- `P` = `PENDING`;
- `F` = `FULFILLED`;
- `V` = `VIOLATED`.

### 3.1. Achieve — cuối cùng điều kiện phải đạt

Trong một activation episode, Achieve ghi nhớ việc predicate đã từng đúng:

\[
P_i=(\neg enter_i\land P_{i-1})\lor p_i.
\]

Nếu active và \(P_i=false\) thì status là `P`; nếu \(P_i=true\) thì là `F`.
Sau lần đầu đạt `F`, predicate có trở lại false cũng không làm goal mất kết
quả trong cùng episode.

Ví dụ với `activation = 0,1,1,1,1,0` và predicate
`condition = -,0,0,1,0,-`:

\[
U\rightarrow P\rightarrow P\rightarrow F\rightarrow F\rightarrow U.
\]

### 3.2. Maintain — đúng ngay khi active và không bao giờ được mất

Maintain phản ánh predicate hiện tại:

\[
P_i=p_i.
\]

Baseline của Maintain là true. Vì vậy condition false ngay checkpoint đầu của
episode đã là một lần vi phạm. Status chỉ là `F` khi \(P_i=true\land S_i=true\);
mọi trường hợp còn lại là `V`. Một lần `V` không thể phục hồi trong cùng
episode.

Ví dụ với predicate `-,1,1,0,1,-`:

\[
U\rightarrow F\rightarrow F\rightarrow V\rightarrow V\rightarrow U.
\]

### 3.3. Sustain — có thể chờ đạt, nhưng sau khi đạt không được mất

Sustain cũng dùng predicate hiện tại \(P_i=p_i\), nhưng baseline là false.
Trước lần đạt đầu tiên, goal là `P`; khi predicate đúng, goal là `F`. Nếu đã có
chuyển \(true\rightarrow false\), \(S_i=false\) và goal là `V` đến hết episode.

Ví dụ với predicate `-,0,1,1,0,1,-`:

\[
U\rightarrow P\rightarrow F\rightarrow F\rightarrow V\rightarrow V
\rightarrow U.
\]

Trên trace hữu hạn, marking này chỉ cho biết lần đạt hiện tại và các lần đạt
đã quan sát được. Thuộc tính vô hạn \(GF\,p\) không thể được chứng minh chỉ từ
một kịch bản hữu hạn; nó cần model checking với loop/fairness riêng.

## 4. Task marking

Mỗi Task occurrence có marking \((Q,R)\): \(Q\) cho biết precondition đã từng
đúng khi task được yêu cầu; \(R\) cho biết postcondition đã đúng sau đó.

\[
Q_i=Q_{i-1}\lor pre_i,
\qquad
R_i=R_{i-1}\lor(Q_i\land post_i).
\]

Ánh xạ status là:

\[
(false,false)\mapsto U,\quad
(true,false)\mapsto P,\quad
(true,true)\mapsto F.
\]

Task không nhận `VIOLATED` trong marking hiện tại. Khác Goal episode, lịch sử
Task được giữ qua các checkpoint để pre và post có thể cách nhau nhiều bước.

## 5. Propagation trên toàn cây

Sau khi đánh giá trực tiếp toàn bộ OCL, status được đóng trên cấu trúc goal:

- AND: `V` nếu có child `V`; `F` nếu mọi child `F`; `P` nếu chưa `V` và có
  child `P`; còn lại `U`;
- OR: `F` nếu có child `F`; nếu chưa có `F` thì `P` nếu có child `P`; `V` khi
  các nhánh đã biết đều thất bại; còn lại `U`;
- `forall R`: tạo occurrence cho mọi Role instance `R` trong cùng ACL Group
  scope, rồi aggregate như AND;
- `pick R`: tạo các candidate occurrence trong cùng scope; một `F` là đủ để
  parent `F`, mọi candidate `V` mới làm parent `V`;
- miền lượng hóa rỗng cho `U`, không dùng vacuous truth;
- dependency truyền demand và context sang dependee; khi phần tử thực hiện ở
  phía dependee hoàn tất, kết quả được truyền về phần tử depender tương ứng;
- `make`/`break` là contribution đủ để đặt Quality thành `TRUE`/`FALSE`;
  `help`/`hurt` không tự quyết định một Quality trong marking ba giá trị.

Nếu một Goal khai báo `condition` trực tiếp, condition đó là predicate có thẩm
quyền của Goal. Refinement không được dùng để ghi đè kết quả trực tiếp này.
Goal cấu trúc không có `condition` mới nhận status hoàn toàn từ các child.

## 6. Ví dụ checkpoint tổng hợp

Giả sử `ParticipantAttended : Achieve` có:

```ocl
condition {[ self.attended ]}
```

và được lượng hóa bằng `forall Participant`. Với hai occurrence `p1`, `p2`,
trace có thể là:

| Checkpoint | `p1.attended` | `p2.attended` | `p1` | `p2` | Parent forall |
|---|---:|---:|---|---|---|
| \(\Sigma_0\) | false | false | P | P | P |
| \(\Sigma_1\) | true | false | F | P | P |
| \(\Sigma_2\) | true | true | F | F | F |

Ở mỗi hàng, cả `p1` lẫn `p2` đều được đánh giá lại từ snapshot ACL tương ứng.
Không có ánh xạ cục bộ kiểu “activity này chỉ cập nhật goal kia”.
