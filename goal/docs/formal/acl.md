# Đặc tả hình thức ACL

## 1. Schema

Một ACL schema là bộ:

\[ \\mathcal A=(PT,EN,E,G,R,Prop,gen,rparent,Rel,End,own,cmp,mult,nav), \]

với:

- `PT` là tập PrimitiveType;
- `EN` là tập Enumeration;
- `E`, `G`, `R` lần lượt là tập Entity, Group và Role type;
- `D=PT\uplus EN` là tập Datatype;
- `C=E\uplus G\uplus R` là tập Class;
- `K=D\uplus C` là tập Classifier;
- `Prop\subseteq C\times Name\times K\times\{1,0..1\}` là Property của Class cùng multiplicity;
- `gen\subseteq(E\times E)\cup(G\times G)` là generalization;
- `rparent:R\rightharpoonup R` là Role cha trực tiếp;
- `Rel\subseteq Name\times\{association,aggregation,composition\}` là các Relationship tường minh;
- `End:Rel\to EndPoint\times EndPoint` cấp đúng hai MemberEnd cho mỗi Relationship;
- `own\subseteq G\times(G\cup R)` là Owner;
- `cmp\subseteq G\times R\times R` là Compatibility có scope;
- `mult` và `nav` cho multiplicity và navigation name của MemberEnd/Owner.

Các tập con của `K` rời nhau theo metaclass. Đặc biệt:

\[ EN\\cap C=\\varnothing, \\qquad E\\cap G=E\\cap R=G\\cap R=\\varnothing. \]

## 2. Well-formedness của classifier

1. Tên classifier là duy nhất toàn schema.
2. Mỗi Enumeration có ít nhất một literal và các literal đôi một khác nhau.
3. Mỗi Property thuộc đúng một Class; tên Property không nhập nhằng trong Class sau khi xét generalization.
4. Type của Property thuộc `K`; default value phải thuộc miền của type.
5. Không tồn tại declaration trực tiếp cho Classifier, Datatype hoặc Relationship trừu tượng.
6. Property không có modifier multiplicity nhận mặc định `1`; chỉ modifier `optional` tạo multiplicity `0..1`. Hai modifier `optional` và `required`không được đồng thời xuất hiện; `required` là cách viết tường minh của `1`.

## 3. Generalization

\[ gen\\subseteq(E\\times E)\\cup(G\\times G). \]

Với mọi `(s,g)\in gen`:

1. `s\ne g`;
2. specific và general cùng metaclass;
3. mỗi specific có nhiều nhất một general trực tiếp;
4. bao đóng bắc cầu `gen^+` là phi phản xạ, tức không có chu trình;
5. Property và đặc trưng của general áp dụng cho specific.

Không có cạnh Role–Role trong `gen`. Role specialization được định nghĩa bởi `rparent`.

## 4. Role specialization và play

`rparent` là hàm riêng phần:

\[ rparent:R\\rightharpoonup R. \]

Các ràng buộc:

1. `rparent(r)\ne r`;
2. bao đóng `rparent^+` không có chu trình;
3. Role con không nhận Property của Role cha;
4. mỗi occurrence Role con có đúng một occurrence Role cha;
5. mỗi occurrence Role gốc có đúng một Agent;
6. lần ngược `play` từ Role occurrence bất kỳ kết thúc tại đúng một Agent.

Đặt `ownerRole:R\rightharpoonup G` suy ra từ `own` và `GAncestorsOrSelf(g)` là bao đóng phản xạ–bắc cầu của Owner Group–Group. Với mọi `p\in rparent^+(c)`:

\[ ownerRole(p)\\downarrow\\Rightarrow ownerRole(c)\\downarrow\\land ownerRole(p)\\in GAncestorsOrSelf(ownerRole(c)). \]

## 5. Relationship và MemberEnd

Mỗi Relationship `l\in Rel` có đúng hai MemberEnd:

\[ End(l)=(e_1,e_2), \\qquad target(e_i)\\in K. \]

Với `mult(e)=[lower(e)..upper(e)]`:

\[ 0\\le lower(e)\\le upper(e), \]

trong đó `upper(e)` có thể là `*`. Tên navigation phải xác định duy nhất phép navigation tại đầu đối diện.

Ràng buộc theo loại:

- Association không mang ownership; Association Role–Role hợp lệ.
- Aggregation là whole–part yếu và phải có ít nhất một endpoint Entity.
- Composition là whole–part mạnh, phải có ít nhất một endpoint Entity, part có nhiều nhất một composite owner và đồ thị composition không chu trình.
- Entity không được là whole của Composition có part là Group hoặc Role.

Quan hệ Group–Entity không được suy ra từ Group. Nó chỉ tồn tại nếu có một phần tử tương ứng trong `Rel`.

## 6. Owner

\[ own\\subseteq G\\times(G\\cup R). \]

Với `(g,x)\in own`:

1. source `g` là Group;
2. target `x` là Role hoặc Group;
3. mỗi target type có nhiều nhất một Owner trực tiếp;
4. `own\cap(G\times G)` không tạo chu trình;
5. mỗi target occurrence có đúng một source Group occurrence;
6. số target occurrence của một source occurrence thỏa `mult(g,x)`.

Không tồn tại `(g,e)\in own` với `e\in E`. Cú pháp Group cũng không có Entity member.

## 7. Compatibility

\[ cmp\\subseteq G\\times{(r_1,r_2)\\in R^2\\mid r_1\\ne r_2}. \]

Compatibility đối xứng trong từng scope:

\[ (g,r_1,r_2)\\in cmp\\iff(g,r_2,r_1)\\in cmp. \]

Hai endpoint phải là Role hợp lệ trong scope của `g`. Compatibility không là link occurrence và không thuộc `Rel`, `own`, `gen` hay `rparent`.

Đặt `Roles(g)` là các Role type có hiệu lực trong Group scope `g` và:

\[ DifferentPairs(g)={{r_1,r_2}\\mid r_1,r_2\\in Roles(g),r_1\\ne r_2}, \]

\[ CompatiblePairs(g)={{r_1,r_2}\\mid(g,r_1,r_2)\\in cmp}, \]

\[ Forbidden(g)=DifferentPairs(g)\\setminus CompatiblePairs(g). \]

## 8. State

Một state ACL là:

\[ \\Sigma=(Ag,O,Lk,type,val,play,ownerLink). \]

Trong đó:

- `Ag` là Agent instance;
- `O` là Entity, Group và Role occurrence;
- `Lk` là link occurrence của `Rel`;
- `type` định kiểu occurrence/link;
- `val` gán Property value;
- `play` nối Agent–Role gốc hoặc Role cha–Role con;
- `ownerLink` hiện thực `own`.

## 9. Well-formed state

`WF_A(\Sigma)` đúng khi và chỉ khi:

1. mọi object, value và link được định kiểu đúng;
2. Property thỏa type, required, default và mutability;
3. mọi MemberEnd và Owner thỏa multiplicity;
4. mọi Role occurrence có duy nhất đường play hợp lệ tới một Agent;
5. mọi part của Composition/Owner có nhiều nhất một owner và không có cycle;
6. với cùng Agent và cùng Group occurrence, không có hai occurrence cùng Role type;
7. với mỗi `{r_1,r_2}\in Forbidden(g)`, cùng Agent không đồng thời có occurrence `r_1` và `r_2` trong cùng occurrence của `g`.
8. với mỗi Role occurrence con và mỗi Role tổ tiên có Owner, Group occurrence owner của Role tổ tiên phải chính là occurrence đạt được khi đi lên chuỗi Owner từ Group occurrence của Role con.

## 10. Bảo toàn phép dịch

Backend đúng phải bảo toàn tính hợp lệ:

\[ WF_A(\\Sigma)\\iff WF\_{target}(\\llbracket\\Sigma\\rrbracket_A). \]

Do đó backend không được thêm constraint làm loại state ACL hợp lệ, cũng không được bỏ constraint khiến state ACL không hợp lệ trở thành hợp lệ ở target.