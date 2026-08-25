# Ngữ nghĩa hình thức của Object Model và OCL

Tài liệu này chắt lọc các định nghĩa cần thiết để xây dựng một công cụ đọc Object Model, đọc OCL và đánh giá một trạng thái của model có thỏa các ràng buộc OCL hay không.

Nguồn chính là luận văn *A Precise Approach to Validating UML Models and OCL Constraints* của Mark Richters. Các số định nghĩa và số trang dưới đây là số được in trong luận văn, không phải chỉ số trang của trình đọc PDF.

> **Phạm vi.** Luận văn dùng UML 1.3 và OCL đời đầu. Ký hiệu cụ thể của OCL hiện đại có thể khác, nhưng cấu trúc ngữ nghĩa cốt lõi vẫn là: cú pháp có kiểu → trạng thái hệ thống → môi trường đánh giá → hàm định giá biểu thức → quan hệ thỏa mãn.

### Quy ước công thức

Tài liệu dùng cú pháp KaTeX tương thích với Notion và các Markdown renderer có hỗ trợ toán học:

- công thức trong dòng dùng `$ ... $`;
- công thức độc lập dùng `$$ ... $$` trên cùng một dòng;
- phần nằm giữa hai dấu `$` không được đặt trong backtick hoặc code fence.

Nếu trình xem chỉ hiện nguyên các dấu `$$`, cần bật tùy chọn Math/KaTeX/MathJax. CommonMark thuần túy không định nghĩa cú pháp công thức toán học.

Phần áp dụng nền tảng này trực tiếp cho ACL, iStar và BPMN được trình bày trong
[ACL-IStar-BPMN-formal.md](./ACL-IStar-BPMN-formal.md).

## 1. Câu trả lời ngắn

Đúng: nếu muốn công cụ không chỉ parse mà còn kết luận OCL là `true`, `false`
hay không xác định, ta phải định nghĩa ý nghĩa toán học của từng cấu trúc trong
Object Model và từng biểu thức OCL.

Không nhất thiết mã nguồn phải chứa nguyên văn các công thức toán học. Tuy nhiên, mỗi nhánh của evaluator phải là một hiện thực hóa nhất quán của các công thức đó. Nếu chỉ có grammar, công cụ mới biết một chuỗi **có đúng cú pháp hay không**; nó chưa biết chuỗi đó **có nghĩa gì** trên một model và một trạng thái cụ thể.

Luồng ý nghĩa được luận văn xây dựng là:

```text
Object Model M
    ↓ sinh ra kiểu và phép toán Σ_M
System state σ = objects + attribute values + links
    ↓ kết hợp với variable assignment β
Environment τ = (σ, β)
    ↓
Evaluation I⟦e⟧(τ) ∈ I(type(e))
    ↓
Satisfaction: invariant đúng khi và chỉ khi kết quả đúng bằng true
```

## 2. Ba câu hỏi “model có đúng không” khác nhau

Cần phân biệt ba bài toán. Gọi `M` là Object Model, `Inv` là tập invariant và `σ` là một snapshot.

> Ba công thức trong mục này là cách tổng hợp các định nghĩa của luận văn thành các bài toán mà một tool cần giải; chúng không phải ba Definition được tác giả đánh số riêng.

### 2.1. Kiểm tra snapshot hiện tại

$$ \\operatorname{ValidSnapshot}(M, Inv, \\sigma) \\iff \\sigma \\in \\llbracket M \\rrbracket\_{OM} \\land \\forall inv \\in Inv:\\ \\operatorname{Sat}(\\sigma, inv). $$

Đây là việc USE thực hiện trực tiếp khi người dùng tạo objects, gán attributes, chèn links rồi yêu cầu kiểm tra invariants.

### 2.2. Kiểm tra đặc tả có nghiệm

$$ \\operatorname{Satisfiable}(M, Inv) \\iff \\exists \\sigma \\in \\llbracket M \\rrbracket\_{OM}:\\ \\forall inv \\in Inv:\\ \\operatorname{Sat}(\\sigma, inv). $$

Một model “có nghiệm” khi tồn tại ít nhất một trạng thái hợp lệ. Model finder có thể tìm một `σ` như vậy, thường trong một phạm vi hữu hạn.

### 2.3. Chứng minh invariant luôn đúng

$$ M \\models inv \\iff \\forall \\sigma \\in \\llbracket M \\rrbracket\_{OM}:\\ \\operatorname{Sat}(\\sigma, inv). $$

Đây mới là chứng minh tổng quát. Một snapshot trả về `true` chỉ chứng minh snapshot đó hợp lệ; nó không đủ để suy ra invariant đúng với mọi trạng thái có thể có. Với miền vô hạn, số lượng object không bị chặn hoặc phép toán đủ mạnh, bài toán tổng quát có thể không quyết định được. Vì vậy nhiều công cụ dùng bounded checking, SMT/SAT hoặc yêu cầu chứng minh tương tác.

## 3. Cú pháp hình thức của Object Model

### 3.1. Các thành phần cơ bản

Gọi `N` là miền tên và `T` là tập kiểu.

**Lớp — Definition 3.1, trang 34**

$$ Class \\subseteq N $$

`Class` là tập hữu hạn các tên lớp. Mỗi lớp `c ∈ Class` sinh ra một object type `t_c ∈ T` cùng tên.

**Thuộc tính — Definition 3.2, trang 34**

Với mỗi lớp `c`, tập thuộc tính có dạng:

$$ Att_c = {,a:t_c\\to t\\mid a\\in N,\\ t\\in T,}. $$

Tên thuộc tính trong cùng một lớp phải phân biệt. Chữ ký cho biết thuộc tính `a`nhận một object thuộc `c` và trả về một giá trị thuộc kiểu `t`.

**Phép toán do model khai báo — Definition 3.3, trang 36**

$$ Op_c = {,\\omega:t_c\\times t_1\\times\\cdots\\times t_n\\to t,}. $$

Tham số đầu tiên là receiver (`self`). Phép toán có thể có nhiều tham số nhưng có một kiểu kết quả.

**Association — Definition 3.4, trang 36–37**

$$ Assoc \\subseteq N, $$

$$ associates:Assoc\\to Class^+, \\qquad associates(as)=\\langle c_1,\\ldots,c_n\\rangle,\\ n\\ge 2. $$

**Role và multiplicity — Definitions 3.5–3.6, trang 38–40**

$$ roles(as)=\\langle r_1,\\ldots,r_n\\rangle, $$

$$ multiplicities(as)=\\langle M_1,\\ldots,M_n\\rangle, \\qquad \\varnothing\\ne M_i\\subseteq\\mathbb N_0, \\quad M_i\\ne{0}. $$

`M_i` là tập các cardinality được phép tại đầu association thứ `i`. Chẳng hạn `0..1` được biểu diễn bởi `{0,1}` và `*` bởi `ℕ₀`.

**Generalization — Definition 3.7, trang 41**

$$ \\prec\\ \\subseteq Class\\times Class $$

là một thứ tự bộ phận. `c_1 \prec c_2` nghĩa là `c_1` là lớp con của `c_2`.

### 3.2. Object Model hoàn chỉnh

**Definition 3.9, trang 43–44** định nghĩa cú pháp Object Model bằng cấu trúc:

$$ M= (Class,Att_c,Op_c,Assoc,associates,roles,multiplicities,\\prec). $$

Đây là kết quả mà parser và semantic analyzer của tool nên tạo ra. AST thô chưa đủ; tool còn phải giải quyết tên, kiểm tra kiểu, tính full descriptor gồm các thành viên kế thừa và kiểm tra các well-formedness rules.

## 4. Miền ngữ nghĩa của Object Model

Cú pháp ở trên chỉ mô tả *schema*. Để đánh giá OCL, luận văn ánh xạ schema sang objects, links và system states.

### 4.1. Object identifiers

**Definition 3.10, trang 44**

Với mỗi lớp `c` có một tập vô hạn các định danh tiềm năng:

$$ oid(c)={c_1,c_2,\\ldots}. $$

Miền của lớp bao gồm định danh của lớp và các lớp con của nó:

$$ I\_{Class}(c) = \\bigcup {,oid(c')\\mid c'\\in Class\\land c'\\preceq c,}. $$

Điều này mô hình hóa tính thay thế: một instance của subclass có thể được dùng ở nơi yêu cầu superclass.

### 4.2. Links

**Definition 3.11, trang 46**

Nếu:

$$ associates(as)=\\langle c_1,\\ldots,c_n\\rangle, $$

thì miền link tiềm năng của association là:

$$ I\_{Assoc}(as) = I\_{Class}(c_1)\\times\\cdots\\times I\_{Class}(c_n). $$

Một link là một tuple thuộc tích Descartes này.

### 4.3. System state

**Definition 3.12, trang 47**

Một snapshot của model `M` là:

$$ \\sigma(M)=(\\sigma\_{Class},\\sigma\_{Att},\\sigma\_{Assoc}). $$

Trong đó:

1. Tập object đang tồn tại là hữu hạn:

   $$ \\sigma\_{Class}(c)\\subset oid(c). $$

2. Mỗi thuộc tính gán một giá trị cho từng object đang tồn tại:

   $$ \\sigma\_{Att}(a):\\sigma\_{Class}(c)\\to I(t), \\qquad a:t_c\\to t\\in Att_c^\*. $$

   `Att_c^*` là tập thuộc tính đầy đủ của `c`, kể cả thuộc tính kế thừa.

3. Tập link hiện tại là hữu hạn:

   $$ \\sigma\_{Assoc}(as)\\subset I\_{Assoc}(as). $$

4. Link phải thỏa multiplicity. Với `π_i` lấy thành phần thứ `i` và `\bar π_i`lấy tất cả thành phần trừ `i`:

   $$ \\forall i\\in{1,\\ldots,n},\\ \\forall l\\in\\sigma\_{Assoc}(as): $$

   $$ \\left| {,l'\\mid l'\\in\\sigma\_{Assoc}(as) \\land \\bar\\pi_i(l')=\\bar\\pi_i(l),} \\right| \\in \\pi_i(multiplicities(as)). $$

Công thức multiplicity là một phần của kiểm tra cấu trúc trạng thái, chạy trước hoặc cùng với OCL invariants.

### 4.4. Ý nghĩa của toàn bộ Object Model

**Definition 3.13, trang 49**

Ngữ nghĩa của `M` là tập tất cả system states có thể có của `M`:

$$ \\llbracket M\\rrbracket\_{OM} = {,\\sigma\\mid\\sigma\\text{ là một system state hợp lệ của }M,}. $$

Đây là bước quan trọng: một model không được ánh xạ sang một chương trình cụ thể, mà sang **một tập các trạng thái được phép**.

## 5. Kiểu, giá trị và phép toán của OCL

### 5.1. Basic types và giá trị không xác định

**Definitions 4.1–4.2, trang 55**

$$ T_B={Integer,Real,Boolean,String}, $$

$$ I(Integer)=\\mathbb Z\\cup{\\bot}, $$

$$ I(Real)=\\mathbb R\\cup{\\bot}, $$

$$ I(Boolean)={true,false,\\bot}, $$

$$ I(String)=A^\*\\cup{\\bot}. $$

`⊥` biểu diễn giá trị không xác định hoặc lỗi khi đánh giá. Luận văn dùng logic ba giá trị cho Boolean. Do đó evaluator không nên ép `⊥` thành `false`; riêng việc kiểm tra invariant yêu cầu kết quả **đúng bằng** `true`.

**Definition 4.4, trang 57** biến mỗi operation thành hàm toàn phần:

$$ I(\\omega:t_1\\times\\cdots\\times t_n\\to t): I(t_1)\\times\\cdots\\times I(t_n)\\to I(t). $$

Các phép toán vốn là hàm bộ phận được toàn phần hóa bằng `⊥`. Ví dụ:

$$ I(+)(i_1,i_2)= \\begin{cases} i_1+i_2,&i_1\\ne\\bot\\land i_2\\ne\\bot,\\ \\bot,&\\text{ngược lại.} \\end{cases} $$

Ngoại lệ quan trọng là các phép Boolean được định nghĩa bằng bảng logic ba giá trị. Ví dụ `false and ⊥ = false`, `true or ⊥ = true`, nhưng `true and ⊥ = ⊥`.

### 5.2. Object types

**Definitions 4.7–4.8, trang 62**

Mỗi lớp sinh ra một object type và:

$$ I(t_c)=I\_{Class}(c)\\cup{\\bot}. $$

### 5.3. `allInstances`

Với `t_c=typeOf(c)`:

$$ allInstances\_{t_c}:\\to Set(t_c), $$

$$ I(allInstances\_{t_c})(\\sigma)=\\sigma\_{Class}(c). $$

Kết quả phụ thuộc snapshot hiện tại và luôn hữu hạn. Khi triển khai inheritance, quy tắc instance của subclass có được đưa vào `C.allInstances` hay không phải được cố định nhất quán với phiên bản OCL mà tool tuyên bố hỗ trợ.

### 5.4. Truy cập attribute

**Definition 4.9, trang 63**

Với `a:t_c→t`, ý nghĩa của `o.a` trong trạng thái `σ` là:

$$ I\_{Att}(a)(\\sigma,o)= \\begin{cases} \\sigma\_{Att}(a)(o),&o\\text{ tồn tại và phù hợp với }c,\\ \\bot,&\\text{ngược lại.} \\end{cases} $$

### 5.5. Navigation qua association

**Definitions 4.10–4.11, trang 65–66**

Từ source object `c_i`, tập target objects `c_j` nối qua association `as` là:

$$ L\_{as,i\\to j}^{\\sigma}(c_i) = {,c_j\\mid (c_1,\\ldots,c_i,\\ldots,c_j,\\ldots,c_n) \\in\\sigma\_{Assoc}(as),}. $$

Nếu multiplicity đích là `0..1` hoặc `1`, navigation trả về một object hay `⊥`:

$$ I(r_j)(\\sigma,c_i)= \\begin{cases} c_j,&c_j\\in L\_{as,i\\to j}^{\\sigma}(c_i),\\ \\bot,&\\text{không có target.} \\end{cases} $$

Nếu navigation có thể có nhiều kết quả:

$$ I(r_j)(\\sigma,c_i)=L\_{as,i\\to j}^{\\sigma}(c_i). $$

### 5.6. Collection types

**Definitions 4.12–4.13, trang 67–68**

Gọi `F(S)` là tập mọi tập con hữu hạn của `S`, `S*` là các chuỗi hữu hạn và `B(S)` là các multiset hữu hạn:

$$ I(Set(t))=F(I(t))\\cup{\\bot}, $$

$$ I(Sequence(t))=(I(t))^\*\\cup{\\bot}, $$

$$ I(Bag(t))=B(I(t))\\cup{\\bot}, $$

$$ I(Collection(t))= I(Set(t))\\cup I(Sequence(t))\\cup I(Bag(t)). $$

### 5.7. Data signature của một model

**Definition 4.16, trang 78** gom toàn bộ kiểu và operation có thể dùng trong OCL của model `M`:

$$ \\Sigma_M=(T_M,\\le,\\Omega_M). $$

- `T_M` là tập kiểu biểu thức, gồm basic, enum, object, special và collection types.
- `≤` là quan hệ subtype.
- `Ω_M` là tập operation, gồm operation dựng sẵn, attribute access, navigation và operation do model khai báo.

Ngữ nghĩa của signature là:

$$ I(\\Sigma_M)=(I(T_M),I(!\\le!),I(\\Omega_M)), $$

với điều kiện:

$$ t'\\le t\\implies I(t')\\subseteq I(t), $$

và mỗi `ω ∈ Ω_M` được ánh xạ sang một hàm toàn phần có kiểu tương ứng.

## 6. Cú pháp và hàm đánh giá biểu thức OCL

### 6.1. Cú pháp trừu tượng có kiểu

**Definition 5.1, trang 88–90** định nghĩa biểu thức bằng quy nạp trên `Σ_M`. Các node cốt lõi gồm:

- variable;
- `let`;
- operation call;
- `if–then–else`;
- type cast và type test;
- `iterate`;
- quy tắc thay thế subtype.

Vì vậy AST nên gắn `type` cho mọi node. Một node chỉ được tạo thành OCL AST hợp lệ sau name resolution và type checking.

### 6.2. Environment

**Definition 5.2, trang 90–91** dùng môi trường:

$$ \\tau=(\\sigma,\\beta), $$

trong đó `σ` là system state và:

$$ \\beta:Var_t\\to I(t) $$

gán giá trị cho biến như `self`, iterator variables, parameters và biến cục bộ.

### 6.3. Hàm định giá

Với biểu thức `e ∈ Expr_t`:

$$ I\\llbracket e\\rrbracket:Env\\to I(t). $$

Các phương trình evaluator quan trọng là:

**Variable**

$$ I\\llbracket v\\rrbracket(\\sigma,\\beta)=\\beta(v). $$

**Let**

$$ I\\llbracket let\\ v=e_1\\ in\\ e_2\\rrbracket(\\sigma,\\beta) = I\\llbracket e_2\\rrbracket (\\sigma,\\beta\[v\\mapsto I\\llbracket e_1\\rrbracket(\\sigma,\\beta)\]). $$

**Operation call**

$$ I\\llbracket\\omega(e_1,\\ldots,e_n)\\rrbracket(\\tau) = I(\\omega)(\\tau) (I\\llbracket e_1\\rrbracket(\\tau),\\ldots, I\\llbracket e_n\\rrbracket(\\tau)). $$

Việc truyền `τ` cho phép attribute access, navigation và `allInstances` đọc snapshot hiện tại.

**Conditional**

$$ I\\llbracket if\\ e_1\\ then\\ e_2\\ else\\ e_3\\ endif\\rrbracket(\\tau) = \\begin{cases} I\\llbracket e_2\\rrbracket(\\tau),&I\\llbracket e_1\\rrbracket(\\tau)=true,\\ I\\llbracket e_3\\rrbracket(\\tau),&I\\llbracket e_1\\rrbracket(\\tau)=false,\\ \\bot,&\\text{ngược lại.} \\end{cases} $$

**Cast**

$$ I\\llbracket e\\ asType\\ t'\\rrbracket(\\tau) = \\begin{cases} I\\llbracket e\\rrbracket(\\tau),&I\\llbracket e\\rrbracket(\\tau)\\in I(t'),\\ \\bot,&\\text{ngược lại.} \\end{cases} $$

### 6.4. Iterator là nền tảng của collection expressions

Luận văn định nghĩa `exists`, `forAll`, `select`, `reject`, `collect` và `isUnique` bằng `iterate` (mục 5.1.3, trang 94–95). Hai phép lượng từ quan trọng:

$$ I\\llbracket C\\to exists(x\\mid P)\\rrbracket = I\\llbracket C\\to iterate(x;acc=false\\mid acc\\ or\\ P)\\rrbracket, $$

$$ I\\llbracket C\\to forAll(x\\mid P)\\rrbracket = I\\llbracket C\\to iterate(x;acc=true\\mid acc\\ and\\ P)\\rrbracket. $$

Nhờ logic ba giá trị, `exists` vẫn là `true` nếu có ít nhất một phần tử làm `P`đúng dù phần tử khác cho `⊥`. Tương tự, `forAll` là `false` ngay khi có một phần tử làm `P` sai.

Với `Set` và `Bag`, thứ tự duyệt không được xác định. Phép tích lũy phải độc lập thứ tự; tính kết hợp và giao hoán là điều kiện đủ. Nếu kết quả phụ thuộc thứ tự, nguồn phải được chuyển thành `Sequence` với thứ tự xác định.

## 7. Ngữ nghĩa của invariants

Mục 5.1.5, trang 97–98 định nghĩa invariant là biểu thức Boolean có các biến ngữ cảnh. Invariant:

```ocl
context C inv:
  body
```

tương đương với:

```ocl
C.allInstances->forAll(self : C | body)
```

Tổng quát, với:

```ocl
context v1 : C1, ..., vn : Cn inv:
  body
```

ý nghĩa là:

$$ C_1.allInstances\\to forAll(v_1\\mid\\cdots C_n.allInstances\\to forAll(v_n\\mid body)\\cdots). $$

Đặt `close(inv)` là biểu thức đã đóng hết biến tự do bằng các lượng từ trên. Khi đó quan hệ thỏa mãn của một snapshot là:

$$ \\sigma\\models inv \\iff I\\llbracket close(inv)\\rrbracket(\\sigma,\\varnothing)=true. $$

Điểm phải giữ nguyên trong implementation:

$$ I\\llbracket close(inv)\\rrbracket=\\bot \\quad\\Longrightarrow\\quad \\sigma\\not\\models inv. $$

Nói cách khác, invariant chỉ pass khi kết quả **chính xác là** `true`. Cả `false` và `⊥` đều làm snapshot không hợp lệ, nhưng report nên phân biệt chúng để người dùng biết là vi phạm logic hay lỗi/undefined khi đánh giá.

## 8. Ngữ nghĩa của precondition và postcondition

Postcondition cần hai trạng thái:

$$ \\tau\_{pre}=(\\sigma\_{pre},\\beta\_{pre}), \\qquad \\tau\_{post}=(\\sigma\_{post},\\beta\_{post}). $$

**Definition 5.4, trang 107** mở rộng evaluator thành:

$$ I\\llbracket e\\rrbracket:Env\\times Env\\to I(t). $$

Biểu thức thông thường đọc post-state; `@pre` đổi việc đọc operation hiện tại sang pre-state:

$$ I\\llbracket \\omega@pre(e_1,\\ldots,e_n) \\rrbracket(\\tau\_{pre},\\tau\_{post}) = I(\\omega)(\\tau\_{pre}) \\bigl( I\\llbracket e_1\\rrbracket(\\tau\_{pre},\\tau\_{post}),\\ldots, I\\llbracket e_n\\rrbracket(\\tau\_{pre},\\tau\_{post}) \\bigr). $$

Quan hệ thỏa mãn là:

$$ \\tau\_{pre}\\models P \\iff I\\llbracket P\\rrbracket(\\tau\_{pre})=true, $$

$$ (\\tau\_{pre},\\tau\_{post})\\models Q \\iff I\\llbracket Q\\rrbracket(\\tau\_{pre},\\tau\_{post})=true. $$

**Definition 5.5, trang 108** cho ngữ nghĩa của một operation specification là quan hệ các chuyển trạng thái hợp lệ:

$$ R= {,(\\tau\_{pre},\\tau\_{post}) \\mid \\tau\_{pre}\\models P \\land (\\tau\_{pre},\\tau\_{post})\\models Q,}. $$

**Definition 5.6, trang 108** nói chương trình `S` thỏa đặc tả theo total correctness nếu:

$$ f_S:dom(R)\\to im(R) $$

là hàm toàn phần và:

$$ graph(f_S)\\subseteq R. $$

Tức là với mọi input state thỏa precondition, chương trình phải kết thúc và tạo ra output state thỏa postcondition.

## 9. Thiết kế tối thiểu của một OCL validation tool

### 9.1. Front end

1. Lexer và parser cho Object Model tạo model AST.
2. Symbol table giải quyết class, attribute, role, operation và inheritance.
3. Well-formedness checker tạo cấu trúc `M` của Definition 3.9.
4. Parser OCL tạo expression AST.
5. Type checker kiểm tra AST theo `Σ_M` và gắn kiểu cho mọi node.

Grammar chỉ giải quyết bước 1 và 4. Các bước còn lại cần static semantics.

### 9.2. Runtime semantic domain

Tool cần biểu diễn đúng `σ=(σClass,σAtt,σAssoc)`:

- object identity và dynamic type;
- giá trị attribute;
- association links;
- collection values;
- `true`, `false` và `⊥`;
- variable environment `β`.

### 9.3. Evaluator

Evaluator nên là một hàm gần với:

```text
eval(expression, state σ, bindings β) -> Value | Undefined
```

Mỗi loại AST node triển khai đúng một phương trình trong Definition 5.2. Các operation phụ thuộc trạng thái nhận `σ`; các operation thuần chỉ nhận giá trị đối số.

### 9.4. Invariant checker

```text
checkSnapshot(M, invariants, σ):
    check that σ is a structurally legal state of M

    for each invariant context C inv body:
        for each object o in allInstances(C, σ):
            result = eval(body, σ, { self ↦ o })
            if result is not exactly true:
                report C, o, result and evaluation trace
                return false

    return true
```

Nếu invariant khai báo nhiều biến ngữ cảnh, checker duyệt tích Descartes của các tập instance tương ứng. Trong implementation thực tế có thể tối ưu, nhưng kết quả phải tương đương lượng từ phổ quát.

### 9.5. Pre/post checker

Khi gọi operation:

1. lưu `σ_pre` và bindings của `self`, parameters;
2. đánh giá tất cả preconditions;
3. chỉ cho phép thực hiện nếu tất cả trả về `true`;
4. sau thay đổi, lấy `σ_post`, bind `result`;
5. đánh giá postconditions với cả `τ_pre` và `τ_post`;
6. `@pre` đọc từ `τ_pre`, biểu thức thường đọc từ `τ_post`.

## 10. Kết luận kỹ thuật

“Ngữ nghĩa hình thức” ở đây không phải chỉ là mô tả bằng lời. Nó là tập các ánh xạ và quan hệ sau:

1. `M` mô tả cú pháp trừu tượng của Object Model.
2. `⟦M⟧OM` xác định tất cả system states được model cho phép.
3. `Σ_M` xác định kiểu và operation mà OCL được phép dùng trong model đó.
4. `τ=(σ,β)` cung cấp ngữ cảnh runtime.
5. `I⟦e⟧(τ)` gán một giá trị toán học cho từng biểu thức OCL.
6. `σ ⊨ inv` biến kết quả đánh giá thành phán quyết pass/fail.
7. `R⊆Env×Env` mô tả các chuyển trạng thái hợp lệ của pre/postconditions.

Đó chính là “hợp đồng toán học” để parser, type checker, evaluator và validator cùng hiểu một model theo một cách duy nhất. Tool là hiện thực hóa có thể chạy được của hợp đồng này.

Điểm cuối cùng cần giữ rõ: USE trong luận văn chứng minh **tính khả thi của ngữ nghĩa bằng validation thực thi được**. Nó cho phép tạo snapshot và đánh giá OCL trên snapshot đó. Muốn nâng từ validation lên chứng minh `M ⊨ inv`, ta còn cần một proof engine hoặc một phép tìm kiếm bao phủ toàn bộ miền trạng thái (thường phải giới hạn miền), chứ evaluator đơn lẻ chưa đủ.
