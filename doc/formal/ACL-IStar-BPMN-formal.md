# Ngữ nghĩa hình thức hợp nhất cho ACL, iStar, BPMN và OCL

Tài liệu này đề xuất nền tảng toán học cho một tool có thể tự đọc, kiểm tra và
thực thi ba DSL trong repository:

- ACL mô tả cấu trúc tổ chức và dữ liệu miền;
- iStar mô tả goal, task, refinement, contribution và dependency;
- BPMN mô tả control flow và các bước làm thay đổi trạng thái;
- OCL mô tả predicate trên trạng thái và quan hệ giữa pre-state/post-state.

Các công thức dùng KaTeX tương thích Notion và Markdown renderer có hỗ trợ
MathJax/KaTeX. Mỗi công thức độc lập được đặt trong một cặp `$$ ... $$` trên
cùng một dòng.

## 0. Quyết định phạm vi của dự án

Tài liệu dùng bộ `goal/src/main/resources/examples/classroom/classroom.*` làm
ví dụ chuẩn. Các example cũ mâu thuẫn với grammar hiện tại không được dùng để
suy ra semantics.

Phạm vi đã chốt:

1. iStar hiện chỉ có refinement AND/OR thông thường. Không đưa `forall/pick`
   refinement trở lại grammar. `forAll` trong `classroom.bpmn2` là iterator OCL,
   không phải relation của iStar.
2. Runtime binding khác nhau giữa iStar `agent` và `role` được hoãn để xem xét
   sau. Executable core hiện bind actor vào ACL Role occurrence.
3. Goal semantics trước mắt chỉ gồm `Achieve`, `Maintain` và `Sustain`. `Recur`
   nằm ngoài normative/executable core.
4. BPMN OR gateway, event-based gateway và message-flow nằm ngoài executable
   core hiện tại.
5. OCL `post` vẫn là relational predicate. Việc tạo post-state do một transition
   mechanism riêng đảm nhiệm; mục 9.4 định nghĩa phương án executable-post cho
   prototype hiện tại.

## 1. Quyết định quan trọng về OCL

### 1.1. Không thay đổi ngữ nghĩa lõi của OCL

OCL không bắt buộc phải chỉ chạy trên một UML Object Diagram. Object Diagram
chỉ là cách trực quan hóa một snapshot. Thứ OCL thực sự cần là:

1. một hệ kiểu;
2. một tập object đang tồn tại;
3. giá trị property;
4. các link để navigation;
5. một môi trường gán biến.

ACL có đủ các thành phần trên. Vì vậy ta thay Object Model của Richters bằng ACL
schema, và thay UML system state bằng ACL state. Hàm đánh giá OCL vẫn giữ dạng:

$$ \mathrm{Eval}_{\mathcal A}(e,\Sigma,\beta)\in\mathrm{Val}(\mathrm{type}(e)). $$

Trong đó `A` là ACL schema, `Σ` là ACL snapshot và `β` là variable binding.

### 1.2. Những gì nằm ngoài OCL

OCL tiêu chuẩn không tự cung cấp:

- token semantics của BPMN;
- lịch sử thỏa mãn goal Achieve/Maintain/Sustain;
- luật lan truyền AND/OR, contribution và dependency;
- phép tạo ra trạng thái mới có side effect.

Các phần này phải là những semantic layer riêng. OCL chỉ được chúng gọi để
đánh giá predicate tại một trạng thái hoặc trên một cặp trạng thái.

### 1.3. Khi nào mới thực sự làm thay đổi OCL

Chỉ coi là thay đổi OCL nếu tool làm một trong các việc sau:

- đổi bảng logic ba giá trị;
- coi `undefined` là `false`;
- đổi ý nghĩa `allInstances`, navigation hoặc collection iterator;
- thêm toán tử thời gian trực tiếp vào cú pháp OCL;
- cho postcondition tự gây side effect.

Nếu cần các lựa chọn này, ngôn ngữ nên được đặt tên là một dialect riêng, ví dụ
`ACL-OCL`, thay vì tuyên bố là OCL chuẩn.

## 2. Kiến trúc ngữ nghĩa bốn lớp

Một đặc tả hợp nhất được ký hiệu:

$$ \mathcal U=(\mathcal A,\mathcal I,\mathcal B,\mathcal X). $$

Trong đó:

- `A` là ACL schema;
- `I` là iStar model;
- `B` là BPMN model;
- `X` là tập cross-model bindings.

Một trạng thái runtime hợp nhất là:

$$ s_i=(\Sigma_i,\kappa_i,M_i). $$

Trong đó:

- `Σᵢ` là ACL state;
- `κᵢ` là BPMN control marking;
- `Mᵢ` là iStar marking.

OCL chỉ đọc `Σᵢ` và variable environment. BPMN tạo quan hệ chuyển từ `Σᵢ` sang
`Σᵢ₊₁`; iStar quan sát chuỗi checkpoint để cập nhật `Mᵢ`.

## 3. Cú pháp hình thức của ACL

### 3.1. ACL schema

Một ACL schema là tuple:

$$ \mathcal A=(D,E,G,R,P,L,O,H,\mathrm{Gen},\mathrm{Cmp},\mathrm{mult}). $$

Các thành phần tương ứng trực tiếp với `ACL.g4` và package `dsl/acl/mm`:

- `D` là tập datatype, gồm primitive types và enum;
- `E` là tập Entity types;
- `G` là tập Group types;
- `R` là tập Role types;
- `P` là tập typed properties;
- `L` là tập association, aggregation và composition;
- `O ⊆ G × (G ∪ R)` là quan hệ Group owner;
- `H ⊆ R × R` là Role inheritance;
- `Gen ⊆ (E × E) ∪ (G × G)` là generalization thông thường;
- `Cmp ⊆ G × R × R` là compatibility được khai báo trong một Group;
- `mult` gán cardinality cho property và mỗi relationship end.

Các tập classifier rời nhau:

$$ E\cap G=E\cap R=G\cap R=\varnothing. $$

Đặt:

$$ C=E\uplus G\uplus R. $$

Mỗi property có các hàm:

$$ \mathrm{owner}_P:P\to C,\qquad \mathrm{type}_P:P\to D,\qquad \mathrm{opt}:P\to\mathbb B. $$

Mỗi domain relation có:

$$ \mathrm{src},\mathrm{tgt}:L\to C,\qquad \mathrm{kind}:L\to\{assoc,aggr,comp\}. $$

Cardinality được biểu diễn bằng:

$$ \mathrm{mult}(x)=[\mathrm{lo}(x),\mathrm{hi}(x)],\qquad \mathrm{hi}(x)\in\mathbb N_0\cup\{\infty\}. $$

Một số lượng `n` thỏa cardinality `q` khi:

$$ \mathrm{CardOK}(q,n)\iff \mathrm{lo}(q)\le n\land(\mathrm{hi}(q)=\infty\lor n\le\mathrm{hi}(q)). $$

### 3.2. Well-formedness tĩnh của ACL

Tên classifier là duy nhất:

$$ \forall c_1,c_2\in C:\ \mathrm{name}(c_1)=\mathrm{name}(c_2)\Rightarrow c_1=c_2. $$

Entity chỉ kế thừa Entity, Group chỉ kế thừa Group và Role chỉ kế thừa Role:

$$ \mathrm{Gen}\subseteq(E\times E)\cup(G\times G),\qquad H\subseteq R\times R. $$

Các đồ thị kế thừa và Owner phải phi chu trình:

$$ \mathrm{irreflexive}(\mathrm{Gen}^{+}),\qquad \mathrm{irreflexive}(H^{+}),\qquad \mathrm{irreflexive}(O_G^{+}). $$

Trong đó `O_G = O ∩ (G × G)`.

Mỗi Role hoặc child Group có không quá một owner type:

$$ \forall c\in G\cup R:\ \left|\{g\in G\mid(g,c)\in O\}\right|\le 1. $$

Compatibility chỉ áp dụng cho hai Role khác nhau được sở hữu trong scope Group:

$$ (g,r_1,r_2)\in\mathrm{Cmp}\Rightarrow g\in G\land r_1,r_2\in R\land r_1\ne r_2. $$

Association, aggregation và composition phải có ít nhất một Entity endpoint:

$$ \forall l\in L:\ \mathrm{src}(l)\in E\lor\mathrm{tgt}(l)\in E. $$

## 4. Ngữ nghĩa instance của ACL

### 4.1. Identity carriers

Mỗi root Entity hoặc Group type `c` có một miền định danh cố định `U_c`. Các
specialization thông thường dùng lại carrier của parent. Mỗi Role có carrier
occurrence riêng `U_r`. Agent có carrier riêng `U_A`.

Các carrier không phải là tập object đang tồn tại. Chúng là tập định danh có thể
dùng; extent runtime chỉ là tập con hữu hạn.

### 4.2. ACL state

Một ACL state là:

$$ \Sigma=\bigl(A_\Sigma,(X_c)_{c\in C},(v_p)_{p\in P},(\ell_l)_{l\in L},(\mathrm{own}_o)_{o\in O},(\mathrm{play}_r)_{r\in R}\bigr). $$

Trong đó:

$$ A_\Sigma\subseteq U_A. $$

Với root Entity, root Group và mọi Role:

$$ X_c\subseteq U_c. $$

Với specialization `c Gen c_p`:

$$ X_c\subseteq X_{c_p}. $$

Các extent `A_Σ` và `X_c` tại một checkpoint đều hữu hạn.

### 4.3. Property values

Gọi `Val(d)` là miền giá trị của datatype `d`.

Property bắt buộc là hàm toàn phần:

$$ \neg\mathrm{opt}(p)\Rightarrow v_p:X_{\mathrm{owner}_P(p)}\to\mathrm{Val}(\mathrm{type}_P(p)). $$

Property tùy chọn là hàm bộ phận:

$$ \mathrm{opt}(p)\Rightarrow v_p:X_{\mathrm{owner}_P(p)}\rightharpoonup\mathrm{Val}(\mathrm{type}_P(p)). $$

Ở tầng OCL, giá trị ngoài miền xác định được nâng thành `⊥`:

$$ \mathrm{read}_\Sigma(p,x)=\begin{cases}v_p(x),&x\in\mathrm{dom}(v_p),\\ \bot,&x\notin\mathrm{dom}(v_p).\end{cases} $$

### 4.4. Domain relations

Với relation `l:c↔d`:

$$ \ell_l\subseteq X_c\times X_d. $$

Số target của source `x` là:

$$ \deg_l^{d}(x)=\left|\{y\in X_d\mid(x,y)\in\ell_l\}\right|. $$

Số source của target `y` là:

$$ \deg_l^{c}(y)=\left|\{x\in X_c\mid(x,y)\in\ell_l\}\right|. $$

Multiplicity hợp lệ khi:

$$ \forall x\in X_c:\ \mathrm{CardOK}(\mathrm{mult}(l,d),\deg_l^d(x)). $$

$$ \forall y\in X_d:\ \mathrm{CardOK}(\mathrm{mult}(l,c),\deg_l^c(y)). $$

### 4.5. Group ownership

Với owner declaration `o=(g,c)`:

$$ \mathrm{own}_o\subseteq X_g\times X_c. $$

Multiplicity của member type `c` trong mỗi Group occurrence:

$$ \forall x\in X_g:\ \mathrm{CardOK}(\mathrm{mult}(o,c),|\mathrm{own}_o[\{x\}]|). $$

Mỗi owned occurrence có đúng một Group owner runtime:

$$ \forall y\in X_c:\ \sum_{o=(g,c)\in O}|\mathrm{own}_o^{-1}[\{y\}]|=1. $$

Do Owner acyclic và functional ở phía child, ta suy ra được hàm context:

$$ \mathrm{group}_\Sigma:X_R\cup X_G\to X_G\cup\{\bot\}. $$

### 4.6. Agent plays Role

Với root Role `r`:

$$ \mathrm{play}_r\subseteq A_\Sigma\times X_r. $$

Với child Role `(r_p,r)∈H`:

$$ \mathrm{play}_r\subseteq X_{r_p}\times X_r. $$

Mỗi Role occurrence có đúng một direct player:

$$ \forall\rho\in X_r:\ |\mathrm{play}_r^{-1}[\{\rho\}]|=1. $$

Vì Role inheritance acyclic, chuỗi `play` kết thúc tại đúng một Agent. Ta ký
hiệu Agent cuối chuỗi là:

$$ \mathrm{agent}_\Sigma(\rho)\in A_\Sigma. $$

### 4.7. Composition

Một composition part có nhiều nhất một composite owner:

$$ \forall y:\ \sum_{l\in L,\ \mathrm{kind}(l)=comp}|\ell_l^{-1}[\{y\}]|\le 1. $$

Quan hệ composition runtime không được tạo chu trình:

$$ \mathrm{irreflexive}\left(\left(\bigcup_{\mathrm{kind}(l)=comp}\ell_l\right)^+\right). $$

### 4.8. Closed-world Role compatibility

Tập Role types mà Agent `a` giữ trong Group occurrence `g` là:

$$ \mathrm{Held}_\Sigma(a,g)=\{r\in R\mid\exists\rho\in X_r:\ \mathrm{agent}_\Sigma(\rho)=a\land\mathrm{group}_\Sigma(\rho)=g\}. $$

Hai Role cùng Agent và cùng Group chỉ hợp lệ nếu chúng nằm trên cùng chuỗi Role
inheritance hoặc được khai báo compatible:

$$ \mathrm{Allowed}(g,r,s)\iff(r,s)\in H^*\lor(s,r)\in H^*\lor(\mathrm{type}(g),r,s)\in\mathrm{Cmp}^{\leftrightarrow}. $$

ACL state phải thỏa:

$$ \forall a,g,\ \forall r\ne s\in\mathrm{Held}_\Sigma(a,g):\ \mathrm{Allowed}(g,r,s). $$

Không có declaration trong `Cmp` đồng nghĩa incompatible. Đây là closed-world
rule đặc trưng của ACL, không phải quy tắc mặc định của UML/OCL.

### 4.9. Tập trạng thái hợp lệ của ACL

Gọi `Inv_A` là hội của tất cả công thức typing, multiplicity, ownership,
composition, play và compatibility ở trên.

$$ \Sigma\models\mathrm{Inv}_{\mathcal A}\iff \mathrm{Eval}_{\mathcal A}(\mathrm{Inv}_{\mathcal A},\Sigma,\varnothing)=true. $$

Ngữ nghĩa của ACL schema là:

$$ [\![\mathcal A]\!]=\{\Sigma\mid\Sigma\models\mathrm{Inv}_{\mathcal A}\}. $$

## 5. Gắn OCL vào ACL mà không đổi OCL

### 5.1. Model-dependent signature

ACL sinh một OCL signature:

$$ \Sigma_{\mathcal A}^{OCL}=(T_{\mathcal A},\le,\Omega_{\mathcal A}). $$

Trong đó:

$$ T_{\mathcal A}=D\cup E\cup G\cup R\cup\{Agent\}\cup T_{Collection}. $$

`Ω_A` gồm:

- basic OCL operations;
- property access từ `P`;
- navigation từ `L`;
- navigation `group`, `agent` từ `own` và `play`;
- `allInstances` cho Entity, Group, Role và Agent.

### 5.2. `allInstances`

Với ACL classifier `c`:

$$ \mathrm{Ext}_\Sigma(c)=X_c\cup\bigcup_{d\in E\cup G,\ (d,c)\in\mathrm{Gen}^{+}}X_d. $$

$$ \mathrm{Ext}_\Sigma(r)=X_r\qquad(r\in R). $$

$$ \mathrm{Eval}(c.allInstances(),\Sigma,\beta)=\mathrm{Ext}_\Sigma(c). $$

Entity/Group generalization dùng subtype extent. Role inheritance của ACL là
quan hệ occurrence/play chứ không phải ordinary subtype, nên `ParentRole`
không tự động đồng nhất object của `ChildRole`; muốn đi lên parent phải dùng
navigation qua `play`.

### 5.3. Property và navigation

$$ \mathrm{Eval}(x.p,\Sigma,\beta)=\mathrm{read}_\Sigma(p,\mathrm{Eval}(x,\Sigma,\beta)). $$

Với association navigation từ `c` sang `d`:

$$ \mathrm{Eval}(x.r,\Sigma,\beta)=\ell_l[\{\mathrm{Eval}(x,\Sigma,\beta)\}]. $$

Nếu multiplicity target là `0..1` hoặc `1`, tập singleton được unbox thành object;
tập rỗng cho kết quả `⊥`.

### 5.4. Môi trường và satisfaction

$$ \tau=(\Sigma,\beta). $$

$$ \mathrm{Eval}_{\mathcal A}(e):\mathrm{Env}_{\mathcal A}\to\mathrm{Val}(\mathrm{type}(e)). $$

Một state predicate chỉ thỏa khi kết quả chính xác là `true`:

$$ \Sigma,\beta\models e\iff\mathrm{Eval}(e,\Sigma,\beta)=true. $$

Vì vậy:

$$ \mathrm{Eval}(e,\Sigma,\beta)=\bot\Rightarrow\Sigma,\beta\not\models e. $$

### 5.5. Pre/postcondition

$$ \tau_{pre}=(\Sigma_{pre},\beta_{pre}),\qquad\tau_{post}=(\Sigma_{post},\beta_{post}). $$

$$ (\tau_{pre},\tau_{post})\models Q\iff\mathrm{Eval}(Q,\tau_{pre},\tau_{post})=true. $$

Biểu thức thường đọc post-state; `@pre` đọc pre-state. Đây là ngữ nghĩa OCL cũ,
không cần thay đổi cho BPMN.

## 6. Cú pháp hình thức của iStar mở rộng

### 6.1. iStar model

Một iStar model là:

$$ \mathcal I=(Ac,IE,\mathrm{kind},\mathrm{owner},\mathrm{gtype},Ref_\land,Ref_\lor,Con,Qual,Need,Dep,cond,pre,post). $$

Trong đó:

- `Ac = AgentActor ⊎ RoleActor` là actor definitions;
- `IE = Goal ⊎ Task ⊎ Resource ⊎ Quality`;
- `owner:IE→Ac` gán mỗi intentional element cho một Actor;
- `gtype:Goal→{Achieve,Maintain,Sustain}` trong executable core;
- `Ref∧` và `Ref∨` là AND/OR refinements;
- `Con ⊆ IE × {make,help,hurt,break} × Quality`;
- `Qual ⊆ Quality × (Goal ∪ Task ∪ Resource)`;
- `Need ⊆ Resource × Task`;
- `Dep` là strategic dependencies;
- `cond`, `pre`, `post` là OCL expressions trên ACL state.

Mỗi actor definition phải được bind tới một ACL Role type:

$$ \chi_A:Ac\to R. $$

Trong concrete syntax hiện tại, binding mặc định là name equality:

$$ \chi_A(a)=r\iff\mathrm{name}(a)=\mathrm{name}(r). $$

### 6.2. Well-formedness tĩnh của iStar

Mỗi intentional element có id duy nhất:

$$ \forall e_1,e_2\in IE:\ \mathrm{id}(e_1)=\mathrm{id}(e_2)\Rightarrow e_1=e_2. $$

Refinement chỉ nối Goal/Task trong cùng Actor:

$$ Ref_\land\cup Ref_\lor\subseteq(Goal\cup Task)\times(Goal\cup Task). $$

Mỗi child có nhiều nhất một refinement parent và đồ thị refinement phi chu trình:

$$ \forall c:\left|\{p\mid(c,p)\in Ref_\land\cup Ref_\lor\}\right|\le1. $$

$$ \mathrm{irreflexive}((Ref_\land\cup Ref_\lor)^+). $$

Contribution phải target Quality; `needed-by` phải đi từ Resource tới Task.
Dependency endpoints phải thuộc đúng Actor và không được trỏ vào refinement
parent theo constraint hiện tại của repository.

### 6.3. Intentional-element occurrences

iStar element type không được đánh giá một lần ở cấp model. Nó được instantiate
cho mỗi ACL Role occurrence tương ứng.

$$ \Omega_I(\Sigma)=\{(e,\rho)\mid e\in IE\land\rho\in X_{\chi_A(\mathrm{owner}(e))}\}. $$

Với dependency tạo context path, occurrence tổng quát là:

$$ \omega=(e,\langle\rho_0,\ldots,\rho_k\rangle). $$

`ρ_k` là context trong cùng, được bind cho `self`:

$$ \beta_\omega(self)=\rho_k. $$

Các context ngoài có thể được truy cập bằng một derived navigation `outer`:

$$ \beta_\omega(self.outer^j)=\rho_{k-j}. $$

`outer` là extension signature của ACL-iStar context, không phải operation chuẩn
của OCL.

## 7. Ngữ nghĩa động của iStar

### 7.1. iStar marking

Tại checkpoint `i`:

$$ M_i=(M_G^i,M_T^i,M_Q^i). $$

Với mỗi Goal occurrence `ω`:

$$ M_G^i(\omega)=(A_i(\omega),P_i(\omega),S_i(\omega))\in\mathbb B^3. $$

- `A` cho biết goal đang active/demanded;
- `P` cho biết condition hoặc refinement hiện đúng;
- `S` cho biết obligation còn stable trong activation episode.

Với Task occurrence:

$$ M_T^i(\omega)=(Q_i(\omega),R_i(\omega))\in\mathbb B^2. $$

- `Q` cho biết task đã được requested;
- `R` cho biết postcondition đã được realized.

Quality có marking:

$$ M_Q^i(\omega)\in\{UNKNOWN,TRUE,FALSE\}. $$

### 7.2. Đánh giá condition bằng OCL

Với Goal có direct condition:

$$ p_i(\omega)=\bigl(\mathrm{Eval}(cond(g),\Sigma_i,\beta_\omega)=true\bigr). $$

Với Task:

$$ q_i(\omega)=D_i(\omega)\land\mathrm{hasPre}(t)\land\bigl(\mathrm{Eval}(pre(t),\Sigma_i,\beta_\omega)=true\bigr). $$

$$ r_i(\omega)=D_i(\omega)\land\mathrm{hasPost}(t)\land\bigl(\mathrm{Eval}(post(t),\Sigma_i,\beta_\omega)=true\bigr). $$

`Dᵢ` là demanded status suy từ root/refinement/dependency activation graph.

Với prototype hiện tại, đặt `Par(c)=p` khi `p` là refinement/dependency
activation parent của `c`. Structural demand là:

$$ D_i(\omega)=true\iff\text{chuỗi }\omega,Par(\omega),Par^2(\omega),\ldots\text{ kết thúc tại một root}. $$

Do graph được yêu cầu phi chu trình, mọi node không có parent cũng là root và
được demanded. Nếu muốn activation động, phải bổ sung activation predicate vào
grammar và thay công thức này bằng propagation phụ thuộc checkpoint.

### 7.3. Goal update theo loại

Khi `Aᵢ=false`, marking reset:

$$ M_G^i(\omega)=(false,false,true). $$

Đặt:

$$ enter_i=A_i\land\neg A_{i-1}. $$

Baseline của Maintain là `true`, các loại còn lại là `false`:

$$ base(k)=\begin{cases}true,&k=Maintain,\\false,&\text{loại khác}.\end{cases} $$

Giá trị condition history:

$$ P_i=\begin{cases}(\neg enter_i\land P_{i-1})\lor p_i,&k=Achieve,\\p_i,&k\in\{Maintain,Sustain\}.\end{cases} $$

Đặt:

$$ P_{prev}=\begin{cases}base(k),&enter_i,\\P_{i-1},&\neg enter_i.\end{cases} $$

$$ S_{prev}=\begin{cases}true,&enter_i,\\S_{i-1},&\neg enter_i.\end{cases} $$

Stable update:

$$ S_i=S_{prev}\land(\neg P_{prev}\lor P_i). $$

Observable status của Goal:

$$ \mathrm{status}_G(k,A,P,S)=\begin{cases}UNKNOWN,&\neg A,\\FULFILLED,&k=Achieve\land P,\\PENDING,&k=Achieve\land\neg P,\\FULFILLED,&k=Maintain\land S\land P,\\VIOLATED,&k=Maintain\land\neg(S\land P),\\VIOLATED,&k=Sustain\land\neg S,\\FULFILLED,&k=Sustain\land S\land P,\\PENDING,&k=Sustain\land S\land\neg P.\end{cases} $$

`Recur` có thể vẫn được lexer/parser nhận vì lý do tương thích file cũ, nhưng
semantic validator của executable profile nên báo `unsupported goal type` cho
đến khi có marking lưu episode/rising-edge history.

### 7.4. Task update

Nếu task không demanded, marking giữ nguyên. Nếu demanded:

$$ Q_i=Q_{i-1}\lor q_i. $$

$$ R_i=R_{i-1}\lor(Q_i\land r_i). $$

$$ \mathrm{status}_T(Q,R)=\begin{cases}FULFILLED,&R,\\PENDING,&Q\land\neg R,\\UNKNOWN,&\neg Q.\end{cases} $$

### 7.5. AND/OR refinement

Với AND parent `p` có children `Ch(p)`:

$$ \mathrm{AND}(p)=\begin{cases}VIOLATED,&\exists c\in Ch(p):status(c)=VIOLATED,\\FULFILLED,&\forall c\in Ch(p):status(c)=FULFILLED,\\PENDING,&\exists c\in Ch(p):status(c)=PENDING,\\UNKNOWN,&\text{ngược lại}.\end{cases} $$

Với OR parent:

$$ \mathrm{OR}(p)=\begin{cases}FULFILLED,&\exists c\in Ch(p):status(c)=FULFILLED,\\PENDING,&\neg\exists c:status(c)=FULFILLED\land\exists c:status(c)=PENDING,\\VIOLATED,&\exists c:status(c)=VIOLATED\land\forall c:status(c)\in\{UNKNOWN,VIOLATED\},\\UNKNOWN,&\text{ngược lại}.\end{cases} $$

Goal có direct condition dùng condition đó làm authoritative predicate. Goal
không có direct condition nhận status từ refinement/dependency propagation.

### 7.6. Contributions

Đặt polarity:

$$ \mathrm{positive}(k)\iff k\in\{make,help\}. $$

$$ \mathrm{negative}(k)\iff k\in\{hurt,break\}. $$

Nếu source element fulfilled:

$$ status(e)=FULFILLED\land(e,k,q)\in Con\land\mathrm{positive}(k)\Rightarrow M_Q(q)=TRUE. $$

$$ status(e)=FULFILLED\land(e,k,q)\in Con\land\mathrm{negative}(k)\Rightarrow M_Q(q)=FALSE. $$

Nếu hai polarity đối lập cùng áp dụng, tool phải có conflict policy xác định.
Prototype hiện tại cho contribution được xử lý sau thắng và chuyển contributor
đối lập về `PENDING`. Nếu muốn semantics độc lập thứ tự, nên trả về một giá trị
`CONFLICT` thay vì dựa vào thứ tự duyệt.

### 7.7. Dependencies

Với dependency từ depender element `d_r` tới dependee element `d_e`, nếu
depender không có direct goal condition:

$$ status(d_e)\in\{FULFILLED,PENDING\}\Rightarrow status(d_r):=status(d_e). $$

Các luật refinement, contribution và dependency được áp dụng đến least
fixpoint:

$$ M_i=\mu M.\ \mathrm{Propagate}_{\mathcal I}(M,\Sigma_i). $$

Một implementation phải phát hiện non-convergence thay vì âm thầm dừng ở một
giới hạn iteration.

## 8. Cú pháp hình thức của simplified BPMN

### 8.1. BPMN model

Một BPMN model trên ACL schema là:

$$ \mathcal B=(Proc,Lane,N,F,Msg,MF,s,e,\mathrm{kind},\mathrm{pool},\mathrm{lane},\mathrm{scope},pre,post,guard). $$

Trong đó:

$$ N=Act\uplus Gw\uplus Ev. $$

- `Proc` là processes/pools;
- `Lane` là lane partitions;
- `Act` gồm Task, CallActivity và SubProcess;
- `Gw` gồm XOR, AND, OR và event-based gateways ở tầng cú pháp;
- `Ev` gồm start, end và intermediate events;
- `F ⊆ N × N` là sequence flows;
- `Msg` và `MF` là message/message flows;
- `scope:Proc→G` bind pool tới ACL Group type;
- `lane:Act→Lane` và lane type bind tới ACL Role;
- `pre`, `post`, `guard` là OCL expressions trên ACL state.

Cross binding của Lane:

$$ \chi_L:Lane\to R. $$

### 8.2. Well-formedness BPMN core

Mỗi process có ít nhất một start và một end event:

$$ \forall p\in Proc:\ |Start(p)|\ge1\land|End(p)|\ge1. $$

Start không có incoming flow; end không có outgoing flow:

$$ \forall s\in Start:\ \mathrm{In}(s)=\varnothing. $$

$$ \forall e\in End:\ \mathrm{Out}(e)=\varnothing. $$

Mọi non-start node phải reachable từ một start và có đường tới một end:

$$ \forall n\in N\setminus Start:\ \exists s\in Start:sF^*n. $$

$$ \forall n\in N\setminus End:\ \exists e\in End:nF^*e. $$

Một activity dùng `self` chỉ hợp lệ khi pool có `scope` Group binding.

## 9. Ngữ nghĩa vận hành BPMN

### 9.1. Runtime configuration

Gọi `Π_B` là tập process instances. Token marking tổng quát là:

$$ m:\Pi_B\times F\to\mathbb N_0. $$

Khi xét riêng một process instance `π`, viết tắt `m_π(f)=m(π,f)`.

Tập activity đang chạy là `run ⊆ Act`. Một BPMN/ACL configuration là:

$$ c=(m,run,\Sigma),\qquad\Sigma\models\mathrm{Inv}_{\mathcal A}. $$

Binding cho một activity execution gồm process instance, Group occurrence và
performer Role occurrence:

$$ \eta=(\pi,g,\rho). $$

Binding hợp lệ khi:

$$ g\in X_{\mathrm{scope}(\mathrm{pool}(a))}. $$

$$ \rho\in X_{\chi_L(\mathrm{lane}(a))}\land\mathrm{group}_\Sigma(\rho)=g. $$

OCL environment của activity bind:

$$ \beta_\eta(self)=g,\qquad\beta_\eta(performer)=\rho. $$

### 9.2. Activity enabledness

Đặt `TokEnabled(a,m)` là điều kiện token theo join semantics. Với activity
thông thường có một incoming flow:

$$ \mathrm{TokEnabled}(a,m,\pi)\iff\exists f\in\mathrm{In}(a):m(\pi,f)>0. $$

Activity được enable khi:

$$ \mathrm{Enabled}(a,c,\eta)\iff\mathrm{TokEnabled}(a,m,\pi)\land\mathrm{BindingOK}(a,\eta,\Sigma)\land\mathrm{Eval}(pre(a),\Sigma,\beta_\eta)=true. $$

Precondition bị thiếu được hiểu là `true` ở tầng BPMN.

### 9.3. Activity transition

Một activity step là:

$$ (m,\Sigma)\xrightarrow{a,\eta}_{\mathcal B}(m',\Sigma'). $$

Nó hợp lệ khi:

$$ \mathrm{Enabled}(a,(m,\Sigma),\eta). $$

$$ (\Sigma,\Sigma',\beta_\eta)\models post(a). $$

$$ \Sigma'\models\mathrm{Inv}_{\mathcal A}. $$

Và token được cập nhật:

$$ m'_\pi=m_\pi-\mathrm{consume}(\mathrm{In}(a))+\mathrm{produce}(\mathrm{Out}(a)). $$

Nếu activity không có postcondition, mặc định:

$$ \Sigma'=\Sigma. $$

### 9.4. Postcondition không phải effect

OCL là side-effect free. Công thức:

$$ (\Sigma,\Sigma')\models post(a) $$

chỉ định nghĩa tập post-state hợp lệ; nó không tự tính ra duy nhất một `Σ'`.

Có hai cách triển khai đúng:

1. model checker chọn một `Σ'` bất kỳ thỏa postcondition;
2. execution engine dùng một effect language/state transformer riêng.

Nếu dùng executable subset của postcondition, cần một compiler:

$$ \mathrm{compileEffect}(post(a))=u_a. $$

$$ \Sigma'=u_a(\Sigma,\eta). $$

Sau đó vẫn phải kiểm tra lại:

$$ (\Sigma,\Sigma')\models post(a)\land\Sigma'\models\mathrm{Inv}_{\mathcal A}. $$

Prototype hiện tại nhận các equality assignment-shaped atoms trong `post` để
sinh effect. Các postcondition ngoài subset đó chỉ có thể dùng để verify.

#### Phương án A — OCL thuần theo quan hệ

Tập post-state hợp lệ của activity là:

$$ \mathrm{PostSet}_a(\Sigma,\eta)=\{\Sigma'\in[\![\mathcal A]\!]\mid(\Sigma,\Sigma',\beta_\eta)\models post(a)\}. $$

Đây là semantics đầy đủ chỉ bằng OCL. Tuy nhiên để chạy, engine vẫn phải gọi
constraint solver và chọn một phần tử:

$$ \Sigma'\in\mathrm{choose}(\mathrm{PostSet}_a(\Sigma,\eta)). $$

Nếu `PostSet` rỗng thì transition không thực hiện được; nếu có nhiều phần tử thì
postcondition là nondeterministic/under-specified. Muốn có đúng một kết quả:

$$ |\mathrm{PostSet}_a(\Sigma,\eta)|=1. $$

Như vậy “chỉ viết OCL” đủ cho specification/model checking, nhưng không loại bỏ
nhu cầu có state generator hoặc solver.

#### Phương án B — executable OCL post profile

Đây là phương án ít thay đổi grammar nhất và phù hợp prototype hiện tại. Chỉ
nhận conjunction của các atom có target ghi được:

```ocl
self.booleanProperty
not self.booleanProperty
self.property = expression
collection->forAll(x | x.booleanProperty)
collection->forAll(x | x.property = expression)
```

Chúng được chuẩn hóa thành simultaneous assignments:

```text
self.p                 => self.p := true
not self.p             => self.p := false
self.p = e             => self.p := evalPre(e)
C->forAll(x | x.p)     => for each x in evalPre(C): x.p := true
```

Để tránh nhầm equality predicate với assignment, mọi RHS đọc một property cũng
được ghi trong cùng postcondition phải dùng `@pre`. Ví dụ hợp lệ:

```ocl
self.count = self.count@pre + 1
```

Gọi `W_a` là tập cặp `(object, property)` được compiler nhận là write target.
State transformer cập nhật đồng thời các target này và thêm frame condition cho
mọi property còn lại:

$$ (x,p)\notin W_a\Rightarrow v'_p(x)=v_p(x). $$

Nếu effect không có create/delete/link/unlink thì extents và relations cũng giữ
nguyên:

$$ A_{\Sigma'}=A_\Sigma\land\forall c:X'_c=X_c\land\forall l:\ell'_l=\ell_l. $$

Sau khi tạo candidate state, engine bắt buộc đánh giá lại OCL bằng ngữ nghĩa
chuẩn:

$$ \Sigma'=u_a(\Sigma,\eta)\land(\Sigma,\Sigma')\models post(a)\land\Sigma'\models\mathrm{Inv}_{\mathcal A}. $$

Nếu post chứa `or`, `implies`, `exists`, equality không có writable LHS hoặc
nhiều assignment mâu thuẫn tới cùng target, compiler đánh dấu `verification-only`
và không cố thực thi.

#### Phương án C — action/effect DSL riêng

Khi cần tạo/xóa object, thêm/xóa link hoặc update phức tạp, action language riêng
rõ ràng hơn:

```text
effect {
  set self.lessonStarted := true;
  set each student in self.target_Student_in_Classroom
      attendanceMarked := true;
}
post {[
  self.lessonStarted
  and self.target_Student_in_Classroom->forAll(s | s.attendanceMarked)
]}
```

`effect` tạo `Σ'`; OCL `post` kiểm chứng kết quả. Đây là phương án khuyến nghị
cho tool đầy đủ. Với MVP dùng `classroom`, phương án B đủ và không cần sửa
concrete syntax ngay. Dù chọn B hay C, transition mechanism vẫn là một component
riêng; không nên thay đổi evaluator để OCL tự gây side effect.

### 9.5. XOR gateway

Các outgoing flow có guard đúng:

$$ EnabledOut(g,\Sigma)=\{f\in Out(g)\mid guard(f)=\varnothing\lor\mathrm{Eval}(guard(f),\Sigma,\beta)=true\}. $$

XOR phải chọn đúng một flow:

$$ g.kind=XOR\Rightarrow |Chosen(g,\Sigma)|=1. $$

Default flow chỉ được chọn khi không guard thường nào đúng.

### 9.6. AND gateway

AND split tạo token trên mọi outgoing flow:

$$ g.kind=AND\land|In(g)|=1\Rightarrow\forall f\in Out(g):m'(\pi,f)=m(\pi,f)+1. $$

AND join chỉ enable khi mọi incoming flow có token:

$$ g.kind=AND\land|In(g)|>1\Rightarrow\mathrm{TokEnabled}(g,m,\pi)\iff\forall f\in In(g):m(\pi,f)>0. $$

### 9.7. OR, event-based và message-flow

Các cấu trúc này có mặt trong grammar/metamodel nhưng semantic validator hiện
tại từ chối chúng. Vì vậy chúng thuộc syntax nhưng chưa thuộc executable core.

Để hỗ trợ OR đúng, join phải đợi đúng những nhánh đã được kích hoạt ở split,
không phải tất cả incoming flows. Event-based gateway cần event race/cancellation.
Message-flow cần message queue hoặc multiset state, ví dụ:

$$ q:Msg\to\mathrm{Seq}(Payload). $$

Không nên tuyên bố hỗ trợ ba cấu trúc này trước khi có transition rules tương
ứng.

### 9.8. Completion

Process complete khi end event đã nhận token, không activity còn chạy và không
token hữu ích còn lại:

$$ \mathrm{Complete}_{\mathcal B}(c)\iff\mathrm{EndReached}(m)\land run=\varnothing\land\mathrm{NoLiveToken}(m). $$

## 10. Ngữ nghĩa hợp nhất

### 10.1. Cross-model bindings

Tập binding là:

$$ \mathcal X=(\chi_A,\chi_P,\chi_L,\chi_C). $$

- `χ_A: iStarActor → ACLRole`;
- `χ_P: BPMNPool → ACLGroup`;
- `χ_L: BPMNLane → ACLRole`;
- `χ_C` xác định context path giữa dependency/process instance và ACL objects.

Name equality có thể là default convention, nhưng sau name resolution phải lưu
binding thành reference tường minh trong IR.

### 10.2. Unified step

Từ unified state:

$$ s_i=(\Sigma_i,\kappa_i,M_i), $$

BPMN thực hiện một bước:

$$ (\kappa_i,\Sigma_i)\xrightarrow{x_i,\eta_i}_{\mathcal B}(\kappa_{i+1},\Sigma_{i+1}). $$

ACL validity gate:

$$ \Sigma_{i+1}\models\mathrm{Inv}_{\mathcal A}. $$

iStar được đánh giá và propagate tới fixpoint:

$$ M_{i+1}=\mathrm{Fix}_{\mathcal I}(M_i,\Sigma_i,\Sigma_{i+1},\mathcal X). $$

Do đó unified transition là:

$$ (\Sigma_i,\kappa_i,M_i)\xrightarrow{x_i,\eta_i}_{\mathcal U}(\Sigma_{i+1},\kappa_{i+1},M_{i+1}). $$

### 10.3. Checkpoint trace

Một trace là:

$$ \Gamma=\langle s_0\xrightarrow{x_1,\eta_1}s_1\xrightarrow{x_2,\eta_2}\cdots\xrightarrow{x_n,\eta_n}s_n\rangle. $$

OCL được đánh giá ở các checkpoint `Σᵢ`; goal temporal semantics đọc cả chuỗi
marking chứ không cố nhét lịch sử vào một Object Diagram.

## 11. Conformance

### 11.1. Root goals

Root goal occurrences áp dụng ở final state là:

$$ Roots_I(\Sigma_n)=\{\omega\in\Omega_I(\Sigma_n)\mid\omega\text{ không có activation parent}\}. $$

Final root satisfaction:

$$ \mathrm{SatRoots}_{\mathcal I}(M_n)\iff\forall\omega\in Roots_I(\Sigma_n):status_{M_n}(\omega)=FULFILLED. $$

### 11.2. Scenario conformance

Với một scenario trace `Γ`:

$$ \mathrm{Conform}(\Gamma)\iff\left(\bigwedge_{i=0}^{n}\Sigma_i\models\mathrm{Inv}_{\mathcal A}\right)\land\left(\bigwedge_{i=1}^{n}s_{i-1}\xrightarrow{x_i,\eta_i}_{\mathcal U}s_i\right)\land\mathrm{Complete}_{\mathcal B}(s_n)\land\mathrm{SatRoots}_{\mathcal I}(M_n). $$

Đây là verdict cho một trace cụ thể, không phải chứng minh cho mọi BPMN path.

### 11.3. Chứng minh toàn cục

Strong conformance yêu cầu mọi complete reachable execution đều thỏa root goals:

$$ \mathrm{StrongConform}(\mathcal U)\iff\forall s\in Reach(\mathcal U):\ \mathrm{Complete}_{\mathcal B}(s)\Rightarrow\mathrm{SatRoots}_{\mathcal I}(M_s). $$

Weak conformance chỉ yêu cầu tồn tại một complete execution tốt:

$$ \mathrm{WeakConform}(\mathcal U)\iff\exists s\in Reach(\mathcal U):\ \mathrm{Complete}_{\mathcal B}(s)\land\mathrm{SatRoots}_{\mathcal I}(M_s). $$

Muốn kết luận strong conformance, tool phải duyệt state space hữu hạn, dùng
bounded model checking, SMT/Event-B hoặc proof engine. Chạy một scenario không
thể chứng minh công thức `∀` này.

## 12. Kiến trúc tool tự xây

### 12.1. Compiler pipeline

```text
ACL.g4   -> ACL AST   -> ACL IR   -> static well-formedness
IStar.g4 -> iStar AST -> iStar IR -> static well-formedness
Bpmn.g4  -> BPMN AST  -> BPMN IR  -> static well-formedness
                              |
                              v
                    Cross-model linker X
                              |
                              v
                    OCL parser + type checker
```

OCL type checker phải chạy sau ACL IR và cross-linking vì kiểu của `self`,
properties và navigation đều phụ thuộc context.

### 12.2. Runtime components

```text
AclStateStore
  - objects, extents, attributes, links, owner, play

OclEvaluator
  - Value = normal value | Undefined
  - three-valued Boolean
  - collections, navigation, iterators

BpmnEngine
  - token marking
  - begin/complete activity
  - guard/pre/post checks
  - external or compiled effects

IStarMonitor
  - occurrence instantiation
  - temporal goal/task histories
  - refinement/contribution/dependency fixpoint

ConformanceChecker
  - ACL gate + BPMN step + iStar update + trace report
```

### 12.3. Evaluator contract

Không nên để evaluator chỉ trả Java `boolean`. Contract tối thiểu là:

```text
eval(expression, state, bindings) -> TypedValue

BooleanValue = TRUE | FALSE | UNDEFINED
```

Invariant, precondition, guard và postcondition chỉ pass khi kết quả là `TRUE`.
Diagnostic phải phân biệt `FALSE` với `UNDEFINED`.

## 13. Khoảng cách giữa công thức và prototype hiện tại

### 13.1. Native OCL evaluator

`NativeOclEvaluator` hiện chỉ hỗ trợ:

- `true`, `false`, string và enum literal;
- `and`, `or`, `not`, `=`, `<>`;
- property navigation;
- `includes`, `forAll`, `exists`.

Nó dùng Java `boolean` và `bool(null)=false`. Do đó nó chưa tương đương ngữ
nghĩa OCL ba giá trị trong tài liệu Richters. Để giữ nguyên OCL, cần thêm
`UndefinedValue`, bảng logic ba giá trị và propagation rules.

Ngoài ra, `AclSnapshot.isType` hiện coi child Role là một instance của parent
Role. Điều này mâu thuẫn với ACL schema ở trên, nơi Role extension tạo hai
occurrence carriers nối bằng `play`. Cần chốt một trong hai semantics; tài liệu
này chọn occurrence/play semantics vì nó giữ được identity riêng của từng Role
occurrence.

### 13.2. Phạm vi iStar đã chốt

`IStar.g4` hiện hỗ trợ AND, OR, contribution, qualifies, needed-by và dependency.
Đây là phạm vi có chủ đích. `forall/pick` không thuộc iStar refinement language;
các example cũ còn dùng chúng được coi là stale. `classroom.istar` là mẫu chuẩn.

Grammar có `agent` actor nhưng native monitor hiện instantiate actor bằng ACL
Role occurrences. Phân biệt binding runtime của `agent` và `role` được hoãn;
executable core hiện chỉ cam kết Role-occurrence semantics.

`Recur` cũng được hoãn. Normative goal semantics hiện chỉ gồm Achieve, Maintain
và Sustain. Nếu hỗ trợ Recur sau này, marking phải thêm episode counter hoặc
rising-edge history.

Repository hiện gom `make/help` thành positive sufficient và `hurt/break` thành
negative sufficient. Đây là policy của prototype; nếu muốn đúng các mức đóng
góp định tính của iStar, `help/hurt` cần một aggregation semantics riêng thay vì
được xử lý mạnh như `make/break`.

### 13.3. BPMN executable subset đã chốt

Grammar/metamodel chứa OR, event-based gateway và message-flow, nhưng
`BpmnSemanticValidator` hiện từ chối chúng. Executable semantics hiện được bảo
đảm cho XOR/AND và process nội bộ một pool. OR, event-based và message-flow được
ghi nhận là future work, chưa cần xử lý trong executable core hiện tại.

### 13.4. BPMN postcondition

`BpmnExecutionEngine` chỉ kiểm predicate và chủ ý không mutate ACL state; adapter
bên ngoài thực hiện thay đổi. Đây là thiết kế đúng với tính side-effect-free của
OCL. Tuy nhiên executable post subset và external effect contract cần trở thành
một interface hình thức, không nên để ngầm trong implementation.

### 13.5. Fixpoint termination

iStar propagation hiện có iteration guard `10,000`. Formal semantics dùng least
fixpoint. Tool nên:

- chứng minh operator monotone trên lattice hữu hạn; hoặc
- phát hiện cycle/conflicting contribution và trả lỗi non-convergence.

Giới hạn iteration chỉ là safety guard, không phải định nghĩa toán học.

## 14. Áp dụng chuẩn lên ví dụ `classroom`

Ba file chuẩn là:

- `goal/src/main/resources/examples/classroom/classroom.acl`;
- `goal/src/main/resources/examples/classroom/classroom.istar`;
- `goal/src/main/resources/examples/classroom/classroom.bpmn2`.

### 14.1. ACL instance schema

Ví dụ không khai báo Entity; các classifier chính là:

$$ E=\varnothing,\qquad G=\{Classroom\},\qquad R=\{Person,Teacher,Student\}. $$

Role inheritance là:

$$ H=\{(Person,Teacher),(Person,Student)\}. $$

Owner declarations và multiplicity:

$$ O=\{(Classroom,Teacher),(Classroom,Student)\}. $$

$$ \mathrm{mult}(Classroom,Teacher)=[1,1],\qquad\mathrm{mult}(Classroom,Student)=[1,\infty]. $$

Một Classroom occurrence `c` hợp lệ phải có đúng một Teacher occurrence và ít
nhất một Student occurrence:

$$ |\mathrm{own}_{Teacher}[\{c\}]|=1\land|\mathrm{own}_{Student}[\{c\}]|\ge1. $$

### 14.2. iStar structure

Actor bindings:

$$ \chi_A(TeacherActor)=Teacher,\qquad\chi_A(StudentActor)=Student. $$

Teacher root dùng AND-refinement:

$$ ClassCompleted=LessonDelivered\land AttendanceSummaryRecorded. $$

Attendance summary dùng OR-refinement:

$$ AttendanceSummaryRecorded=RecordAttendanceManually\lor RecordAttendanceElectronically. $$

Mỗi Student occurrence có tree riêng:

$$ ParticipatesInClass=AttendanceMarked\land PresentForLesson. $$

Không có iStar `forall/pick`. Việc mọi Student occurrence phải thỏa goal được
suy từ occurrence semantics:

$$ \Omega_I(\Sigma)\supseteq\{(ParticipatesInClass,s)\mid s\in X_{Student}\}. $$

### 14.3. BPMN bindings

Pool và lanes được bind như sau:

$$ \chi_P(TeachingSession)=Classroom. $$

$$ \chi_L(TeacherLane)=Teacher,\qquad\chi_L(StudentLane)=Student. $$

Process dùng AND split/join cho chuẩn bị song song và XOR cho lựa chọn ghi nhận
điểm danh. OR/event-based/message-flow không xuất hiện.

### 14.4. `forAll` trong BPMN là OCL

Postcondition:

```ocl
self.target_Student_in_Classroom->forAll(student |
  student.attendanceMarked)
```

là OCL iterator trên collection:

$$ \forall s\in\mathrm{own}_{Student}[\{self\}]:\ \mathrm{read}_{\Sigma'}(attendanceMarked,s)=true. $$

Nó không tạo quantified refinement trong iStar.

### 14.5. Biên dịch post thành state update

Với `beginLesson`, executable-post compiler nhận các write target:

$$ W_{begin}=\{(self,lessonStarted),(teacher,attendanceStarted)\}. $$

Và tạo đồng thời:

$$ lessonStarted'(self)=true\land attendanceStarted'(teacher)=true. $$

Với `recordAttendanceElectronically`:

$$ W_{electronic}=\{(self,attendanceRecorded),(self,attendanceMethod)\}\cup\{(s,attendanceMarked)\mid s\in Students(self)\}. $$

Candidate post-state phải thỏa:

$$ attendanceRecorded'(self)=true. $$

$$ attendanceMethod'(self)=electronic. $$

$$ \forall s\in Students(self):attendanceMarked'(s)=true. $$

Mọi property không thuộc `W_electronic` giữ nguyên bởi frame condition. Sau đó
engine đánh giá lại nguyên postcondition OCL và toàn bộ `Inv_A`.

Với `answerQuestions`, atom:

```ocl
not self.needsClarification
```

được executable profile chuẩn hóa thành:

$$ needsClarification'(self)=false. $$

### 14.6. Conformance cuối trace

Khi BPMN tới `classFinished`, checker không chỉ kiểm control-flow completion.
Nó còn yêu cầu:

$$ status(ClassCompleted,teacher)=FULFILLED. $$

$$ \forall s\in X_{Student}:status(ParticipatesInClass,s)=FULFILLED. $$

Hai công thức per-instance này là lý do ACL snapshot phải giữ identity riêng cho
từng Student thay vì chỉ đánh giá một lần trên classifier `Student`.

## 15. Kết luận

Không cần chuyển iStar hoặc BPMN thành USE để có ngữ nghĩa hình thức. USE chỉ là
một backend hiện thực hóa Object Model/OCL semantics. Ta có thể thay nó bằng
`AclStateStore + OclEvaluator + BpmnEngine + IStarMonitor` nếu các component tuân
theo hợp đồng toán học trong tài liệu này.

OCL được giữ nguyên ở vai trò predicate language. Điều thay đổi là model-dependent
signature và semantic domain mà OCL đọc. BPMN tokens và iStar temporal markings
nằm cạnh ACL state trong unified state, không bị ép thành OCL objects trừ khi ta
chủ động muốn cho OCL query chính metamodel/runtime của chúng.

Ranh giới cuối cùng là:

$$ \boxed{\text{ACL định nghĩa state}\quad+\quad\text{BPMN định nghĩa transition}\quad+\quad\text{iStar định nghĩa intention}\quad+\quad\text{OCL định nghĩa predicate}} $$
