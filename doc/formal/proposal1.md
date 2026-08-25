Today 9:09 AM

mr-dissertation.pdf

PDF

giải thích cho tôi các định nghĩa của 3.1 đến hết không giải thích các định nghĩa từ 4.1 trở đi

Được. Tôi hiểu “3.1 đến hết” ở đây là **Definition 3.1 cho đến Definition 3.13 của Chương 3**, và **dừng hoàn toàn trước Definition 4.1**. Tôi sẽ bám sát định nghĩa hình thức của Richters; phần tôi thêm vào chủ yếu là giải nghĩa ký hiệu và trực giác toán học, không lấy các định nghĩa ở Chương 4 để lấp vào.

Điểm quan trọng trước khi đọc 3.1–3.13 là: Chương 3 đang xây dựng một **object model hình thức** làm nền cho OCL. Tác giả chia nó thành hai nửa:

cuˊ phaˊp của model​⟶caˊc trạng thaˊi coˊ thể coˊ của model​

Definitions **3.1–3.9** chủ yếu định nghĩa **model gồm những gì**; Definitions **3.10–3.13** định nghĩa **model đó có nghĩa là gì**. Chính tác giả nói object model gồm classes, attributes, operations, associations với role/multiplicity, và generalization hierarchy; tạm thời ông giả sử có một signature Σ=(T,Ω), trong đó T là tập kiểu và Ω là các phép toán trên kiểu.

---

# Một số ký hiệu phải hiểu trước

Trong Chương 3:

N

là tập các tên hợp lệ. Ví dụ `Person`, `age`, `Employee`, `salary`, `Employment` đều có thể xem là các phần tử của N.

T

là tập các type. Ở thời điểm Chương 3, tác giả **chưa chính thức xây dựng đầy đủ hệ kiểu OCL**; việc đó để Chương 4. Chương 3 chỉ giả sử T đã có sẵn, gồm những kiểu như `Integer`, `Real`, `Boolean`, `String`, collection types và sau này cả object types.

Ký hiệu

f:A→B

ở đây thường được dùng như một **signature**: đầu vào thuộc kiểu A, đầu ra thuộc kiểu B.

Ví dụ:

age:Person→Integer

đừng hiểu ngay là một hàm đang chứa dữ liệu tuổi của tất cả mọi người. Ở Definition 3.2 nó trước hết là **khai báo cấu trúc**:

> một `Person` có thuộc tính `age`, và giá trị của thuộc tính đó thuộc kiểu `Integer`.

Đến Definition 3.12, khi có một **system state cụ thể**, mới xuất hiện hàm thực sự gán giá trị, chẳng hạn:

σAtt​(age)(e1​)=47.

Đây là sự phân biệt cực kỳ quan trọng giữa **model/schema** và **state/instance**.

---

# Definition 3.1 — Classes

Tác giả định nghĩa:

Class⊆N

và Class là một tập hữu hạn các tên class. Mỗi class c∈Class sinh ra một object type tc​∈T có cùng tên với class đó.

Ví dụ:

Class={Branch, Car, CarGroup, Check, Customer, Employee, Person, Rental, ServiceDepot}.

## Ý nghĩa

Ở đây `Person` trước hết là **một phần tử cú pháp của model**:

Person∈Class.

Nó nói rằng model có khai báo một class tên là `Person`.

Nhưng class `Person` đồng thời tạo ra một **type** cũng tên là `Person`. Vì vậy ta có thể viết những thứ như:

manager:Branch→Employee.

Ý của câu “each class induces an object type” là:

class Person⇝type Person.

Hai khái niệm có cùng tên nhưng không hoàn toàn giống nhau.

`Person` với tư cách **class** là một phần của model.

`Person` với tư cách **type** mô tả miền giá trị mà các biểu thức OCL kiểu `Person` có thể nhận.

Tác giả còn lưu ý object type sau này chứa một giá trị đặc biệt “undefined”, trong khi class tự thân không phải là một miền giá trị.

### Tại sao Class hữu hạn?

Bởi vì một model cụ thể chỉ khai báo hữu hạn class. Ví dụ hệ thống thuê xe chỉ có một số class xác định.

Nhưng điều này **không có nghĩa mỗi class chỉ sinh được hữu hạn object**. Definition 3.10 sẽ cố ý cho mỗi class một tập vô hạn object identifiers tiềm năng.

---

# Definition 3.2 — Attributes

Với một class c, tập thuộc tính của nó được viết:

Attc​.

Một thuộc tính có dạng:

a:tc​→t

trong đó:

a∈N,tc​∈T,t∈T.

Tức là một attribute là một signature từ object type của class chứa nó sang type của giá trị thuộc tính.

Ví dụ:

age:Person→Integer

có nghĩa:

> với một object thuộc `Person`, thuộc tính `age` của nó có giá trị thuộc miền `Integer`.

Tương tự:

firstname:Person→String

và

email:Person→Set(String).

Tác giả cho:

AttPerson​={​firstname:Person→String,lastname:Person→String,age:Person→Integer,isMarried:Person→Boolean,email:Person→Set(String)}.​

## Điều cần phân biệt

Definition 3.2 **không nói**:

age(Person)=25.

Nó chỉ nói:

age:Person→Integer.

Tức là đây là **type declaration**, chưa phải dữ liệu.

Có thể hình dung:

Definition 3.2​age:Person→Integer

là schema, còn:

Definition 3.12​σAtt​(age)(e1​)=47

là một giá trị trong một snapshot cụ thể.

## Tên attribute phải duy nhất trong cùng class

Tác giả yêu cầu nếu có:

a:tc​→t

và đồng thời:

a:tc​→t′

thì nhất thiết:

t=t′.

Nói đơn giản, không được khai báo kiểu:

```
class Person
```

    age : Integer

    age : String

end

vì cùng một attribute `age` trong cùng class không thể đồng thời có hai kiểu khác nhau. 

---

# Definition 3.3 — Operations

Operations của class c được chứa trong:

Opc​.

Một operation có signature:

ω:tc​×t1​×⋯×tn​→t.

Ở đây:

tc​

là kiểu của object nhận operation, tức object tương ứng với `self`.

Còn:

t1​,…,tn​

là kiểu của các tham số thông thường.

Và:

t

là return type.

Ví dụ:

raiseSalary:Employee×Real→Real.

Có thể đọc thành:

> `raiseSalary` được gọi trên một `Employee`, nhận thêm một đối số `Real`, và trả về một `Real`.

Trong cú pháp gần với lập trình:

```
employee.raiseSalary(x)
```

thì về mặt signature hình thức có thể tưởng tượng:

raiseSalary(employee,x).

Object `employee` chính là đối số đầu tiên tc​.

Một ví dụ khác:

rentalsForDay:Branch×String→Set(Rental).

Nghĩa là một `Branch` và một ngày dạng `String` cho ra tập các `Rental`. Các ví dụ operation của model được tác giả ghi trực tiếp ở đây. 

## Tại sao `self` lại xuất hiện như tham số đầu?

Đây là cách toán học hóa lời gọi phương thức kiểu object-oriented.

Cú pháp OO:

o.f(x)

được nhìn về mặt toán học gần như:

f(o,x).

Vì vậy:

ω:tc​×t1​→t

là cách đưa receiver vào signature một cách rõ ràng.

---

# Definition 3.4 — Associations

Một association có hai thành phần chính.

Thứ nhất là tập hữu hạn tên association:

Assoc⊆N.

Thứ hai là hàm:

associates:Assoc→Class+.

Với một association as:

associates(as)=⟨c1​,…,cn​⟩,n≥2.

 

## `Class+` nghĩa là gì?

Ở đây nó là một **dãy không rỗng các class**, và Definition 3.4 còn yêu cầu association có ít nhất hai đầu:

n≥2.

Ví dụ:

associates(Employment)=⟨Branch,Employee⟩.

Nghĩa là association `Employment` kết nối `Branch` với `Employee`.

Với association ba ngôi:

associates(Maintenance)=⟨ServiceDepot,Check,Car⟩.

Nó kết nối cùng lúc ba class.

Tác giả gọi n là **degree** của association.

Nếu:

n=2

thì binary association.

Nếu:

n=3

thì ternary association.

Vân vân.

## Self-association

Một điểm hay là danh sách class có thể lặp.

Ví dụ:

associates(Quality)=⟨CarGroup,CarGroup⟩.

Đây là self-association: hai đầu đều là `CarGroup`. 

Điều đó dẫn tới vấn đề:

> nếu cả hai đầu đều là `CarGroup`, làm sao biết object nào đang đóng vai trò nào?

Definition 3.5 giải quyết chính xác việc này.

---

# Definition 3.5 — Role names

Giả sử:

associates(as)=⟨c1​,…,cn​⟩.

Tác giả định nghĩa:

roles(as)=⟨r1​,…,rn​⟩

và mỗi ri​ là tên role tương ứng với đầu association thứ i.

Ngoài ra:

i=j⇒ri​=rj​.

Tức các role names trong cùng một association phải khác nhau. 

Ví dụ:

associates(Employment)=⟨Branch,Employee⟩

đi cùng:

roles(Employment)=⟨employer,employee⟩.

Còn:

associates(Quality)=⟨CarGroup,CarGroup⟩

đi cùng:

roles(Quality)=⟨lower,higher⟩.

Nhờ đó hai đầu `CarGroup` không còn nhập nhằng:

lower

và

higher

nói rõ mỗi object đang đóng vai trò gì.

## Tại sao role name đặc biệt quan trọng với OCL?

Vì role name về sau chính là thứ cho phép navigation.

Ví dụ nếu từ `Branch` có thể đi qua association tới `Employee` với role `employee`, thì biểu thức dạng:

```
self.employee
```

có thể được hiểu là navigation qua association.

Tác giả vì thế còn xây dựng hàm:

navends(c,as)

để trả về các role names mà ta có thể **đi tới từ class c** qua association as. 

Ví dụ:

navends(Car,Maintenance)={check,serviceDepot}.

Vì khi ta đang ở một `Car` trong ternary association `Maintenance`, hai đầu còn lại mà ta có thể hướng tới là `Check` và `ServiceDepot`.

---

# Definition 3.6 — Multiplicities

Giả sử:

associates(as)=⟨c1​,…,cn​⟩.

Tác giả định nghĩa:

multiplicities(as)=⟨M1​,…,Mn​⟩

với:

Mi​⊆N0​,

Mi​ không rỗng và:

Mi​={0}.

Đây là một cách rất đẹp để biến UML multiplicity thành toán học.

Ví dụ UML:

```
0..1
```

được biểu diễn thành:

{0,1}.

Multiplicity:

```
1
```

thành:

{1}.

Multiplicity:

```
*
```

có thể được xem là:

N0​={0,1,2,3,…}.

Multiplicity:

```
1..*
```

tương ứng trực giác với:

{1,2,3,…}.

Tác giả cho ví dụ:

multiplicities(Maintenance)=⟨{0,1},N0​,N0​⟩.

### Nhưng Definition 3.6 mới chỉ định nghĩa **cú pháp**

Ở đây nó mới nói:

> mỗi association end được gắn với một tập các cardinality hợp lệ.

Nó **chưa định nghĩa chính xác thế nào là một trạng thái thỏa multiplicity**.

Việc đó được hoãn đến **Definition 3.12**. Đây là một điểm nên nhớ:

Def 3.6: khai baˊo multiplicity​

và:

Def 3.12: kiểm tra link-set coˊ thỏa multiplicity khoˆng​.

---

# Definition 3.7 — Generalization hierarchy

Tác giả định nghĩa quan hệ:

≺

trên Class, biểu diễn generalization hierarchy. Nếu:

c1​≺c2​

thì:

c1​=child class,c2​=parent class.

Ví dụ:

Customer≺Person

và:

Employee≺Person.

Nên:

```
       Person
```

       /    \\

 Customer  Employee

Tác giả biểu diễn ví dụ này bằng:

≺={(Customer,Person),(Employee,Person)}.

## Hướng của ký hiệu rất quan trọng

Nó đi từ:

child≺parent​

chứ không phải parent → child.

Do đó:

Employee≺Person

đọc là:

> Employee chuyên biệt hơn Person / Employee là con của Person.

Điều này sẽ trở nên cực kỳ quan trọng ở Definition 3.10.

---

# Definition 3.8 — Full descriptor of a class

Đây là một định nghĩa quan trọng vì nó chính thức hóa **inheritance**.

Đầu tiên tác giả định nghĩa:

parents(c)={c′∣c≺c′}.

Tức là tập các parent/ancestor của c. 

Sau đó xây dựng toàn bộ attributes mà c nhìn thấy:

Attc∗​=Attc​∪c′∈parents(c)⋃​Attc′​.

Toàn bộ operations:

Opc∗​=Opc​∪c′∈parents(c)⋃​Opc′​.

Và toàn bộ navigable role names:

navends∗(c)=navends(c)∪c′∈parents(c)⋃​navends(c′).

Từ đó:

FDc​=(Attc∗​,Opc∗​,navends∗(c)).

Đó chính là **full descriptor** của class c. 

## Ví dụ với Employee

Giả sử:

```
Person
```

  firstname

  lastname

  age

Employee extends Person

  salary

Thì:

AttEmployee​={salary}

chỉ là những gì **Employee tự khai báo**.

Nhưng:

AttEmployee∗​={salary,firstname,lastname,age,…}.

Đây mới là toàn bộ các thuộc tính mà một `Employee` thực sự có.

Vì thế dấu sao:

Attc∗​

có thể nhớ đơn giản là:

> closure của properties qua inheritance.

---

## Các well-formedness rules đi cùng Definition 3.8

Phần này rất đáng đọc vì nó giải thích model nào được coi là hợp lệ.

### WF-2: attribute không được định nghĩa lại gây nhập nhằng

Một full descriptor không được có hai attribute cùng tên đến từ hai class khác nhau theo cách xung đột. Tác giả muốn mỗi property xác định được duy nhất nó đến từ đâu và có kiểu gì. 

### WF-3: cùng một operation signature không được định nghĩa hai lần trong hierarchy

Overloading vẫn có thể xảy ra nếu số lượng hoặc kiểu parameters khác nhau.

Nhưng cùng chính xác một operation signature thì không được có hai nơi định nghĩa trong full descriptor. 

Tác giả cũng phân biệt **overloading** với **overriding**. Vì luận văn đang ở mức specification, ông không đi sâu vào method overriding. 

### WF-4: navigable role names không được xung đột qua inheritance

Nếu class con thừa hưởng role names từ nhiều parent, chúng cũng phải phân biệt được. 

### WF-5: attribute name và role name không được trùng

Lý do liên quan trực tiếp đến OCL.

Nếu:

```
self.x
```

thì `x` có thể là attribute hoặc một navigation role. Nếu cả hai cùng tồn tại, parser/type checker sẽ không biết tác giả muốn cái nào.

Do đó:

a=r

cho mọi attribute name a và navigable role name r trong full descriptor. 

Ngược lại operation có thể trùng tên, vì:

```
self.age
```

và:

```
self.age()
```

phân biệt được bằng cú pháp.

---

# Definition 3.9 — Syntax of object models

Đây là lúc Definitions 3.1–3.8 được gom lại thành **một object model hoàn chỉnh**.

Tác giả định nghĩa:

M=(Class,Attc​,Opc​,Assoc,associates,roles,multiplicities,≺).

Nói cách khác, một object model M không còn là “một hình class diagram”.

Về mặt hình thức, nó là **một tuple chứa toàn bộ thông tin cấu trúc cần thiết**.

Có thể đọc:

M=coˊ những class naˋoClass​​,class coˊ attributes gıˋAtt​​,operations gıˋOp​​,associations naˋoAssoc​​,association noˆˊi class naˋoassociates​​,role ở từng đaˆˋuroles​​,cardinalitymultiplicities​​,inheritance≺​​.

Đây chính là **abstract syntax toán học** của Basic Modeling Language.

Một lưu ý nhỏ trong bản luận văn: ở mục ii của Definition 3.9, văn bản gọi Attc​ là “a set of operation signatures for functions mapping an object ... to an attribute value”, nhưng nó đồng thời trỏ trực tiếp về Definition 3.2. Theo cấu trúc mà tác giả vừa xây dựng, Attc​ ở đây chính là các **attribute signatures** của Definition 3.2; cụm “operation signatures” có vẻ là cách viết không nhất quán trong câu đó. 

Đến đây ta đã hoàn thành phần:

SYNTAX​

Bây giờ luận văn chuyển sang:

SEMANTICS / INTERPRETATION​.

---

# Definition 3.10 — Object identifiers

Đây là bước đầu tiên biến một class từ một **ký hiệu trong model** thành một **miền các object có thể tồn tại**.

Với mỗi class c, tác giả cấp một tập vô hạn object identifiers:

oid(c)={c1​,c2​,…}.

Sau đó định nghĩa semantic domain của class:

IClass​(c)=⋃{oid(c′)∣c′∈Class∧c′⪯c}.

Hãy để ý ở công thức thứ hai tác giả dùng quan hệ kiểu:

c′⪯c,

nghĩa là bao gồm cả chính c và các class chuyên biệt hơn nó.

## Đây là một định nghĩa rất quan trọng

Giả sử:

Employee≺Person.

Thì mọi identifier của `Employee` cũng nằm trong interpretation của `Person`:

oid(Employee)⊆I(Person).

Do đó:

I(Employee)⊆I(Person).

Tác giả minh họa:

I(Person)={p1​,p2​,…}∪I(Customer)∪I(Employee).

Vậy một object:

e1​∈oid(Employee)

đương nhiên cũng là:

e1​∈I(Person).

Đây chính là ý nghĩa hình thức của câu:

> Every Employee is a Person.

Nó không còn chỉ là mũi tên tam giác trong UML nữa, mà trở thành **quan hệ bao hàm tập hợp**:

Employee≺Person⇒I(Employee)⊆I(Person).

Tác giả nhấn mạnh điều này để có substitutability: object thuộc class chuyên biệt có thể được dùng nơi object của class tổng quát được yêu cầu. 

Hình 3.1 trên trang 46 của luận văn minh họa rất trực quan: I(c1​) và I(c2​) là các tập nằm bên trong I(p). Với multiple inheritance ở Figure 3.2, miền của class con I(c) nằm trong phần giao của hai miền parent I(p1​) và I(p2​). 

---

# Definition 3.11 — Links

Ta đã có objects. Bây giờ cần tạo quan hệ giữa chúng.

Giả sử:

associates(as)=⟨c1​,…,cn​⟩.

Tác giả diễn giải association as thành Cartesian product:

IAssoc​(as)=IClass​(c1​)×⋯×IClass​(cn​).

Một **link** là một phần tử:

las​∈IAssoc​(as).

Ví dụ:

associates(Assignment)=⟨Rental,Car⟩.

Thì:

I(Assignment)=I(Rental)×I(Car).

Một link có thể là:

(r7​,c15​).

Nghĩa là Rental r7​ được nối với Car c15​.

Với association ba ngôi:

I(Maintenance)=I(ServiceDepot)×I(Check)×I(Car).

Một link:

(sd1​,ch4​,car22​)

nghĩa là ba object đó cùng tham gia **một occurrence của association Maintenance**. Các ví dụ Cartesian product này được tác giả ghi ngay sau Definition 3.11. 

## Điểm cực kỳ quan trọng

IAssoc​(as)

chưa phải các links **đang thực sự tồn tại**.

Nó là tập **mọi link có khả năng tồn tại theo type structure**.

Ví dụ nếu:

I(Rental)={r1​,r2​,…}

và:

I(Car)={c1​,c2​,…}

thì Cartesian product chứa:

(r1​,c1​),(r1​,c2​),(r2​,c1​),…

Nhưng tại một thời điểm thực tế có thể chỉ có:

(r1​,c2​)

đang tồn tại.

Tập links **thực sự tồn tại trong snapshot hiện tại** sẽ là:

σAssoc​(as)

ở Definition 3.12.

Đây lại là phân biệt:

IAssoc​(as)=coˊ thể toˆˋn tại​

với:

σAssoc​(as)=đang toˆˋn tại​.

---

# Definition 3.12 — System state

Đây có lẽ là định nghĩa quan trọng nhất của toàn bộ Chương 3 đối với việc hiểu semantics của OCL.

Tác giả định nghĩa một trạng thái của model M:

σ(M)=(σClass​,σAtt​,σAssoc​).

Nó là **snapshot hoàn chỉnh của hệ thống tại một thời điểm**.

Ta xem từng phần.

---

## 1. σClass​: những object nào đang tồn tại?

Với mỗi class c:

σClass​(c)⊂oid(c).

Và tập này hữu hạn. 

Ví dụ:

σClass​(Branch)={b1​}

và:

σClass​(Employee)={e1​,e2​}.

Tức snapshot hiện tại có một Branch và hai Employee.

Chú ý sự khác biệt:

oid(Employee)={e1​,e2​,e3​,…}

là **không gian identifiers tiềm năng**, còn:

σClass​(Employee)={e1​,e2​}

là những Employee **hiện đang tồn tại**.

---

## 2. σAtt​: giá trị attributes hiện tại là gì?

Nếu:

a:tc​→t

là attribute trong full descriptor Attc∗​, tác giả định nghĩa:

σAtt​(a):σClass​(c)→I(t).

Definition 3.2 chỉ nói:

age:Person→Integer.

Definition 3.12 mới nói, trong state cụ thể:

σAtt​(age)(e1​)=47.

Trong ví dụ của tác giả:

σAtt​(firstname)(e1​)=John\
σAtt​(lastname)(e1​)=Clark\
σAtt​(age)(e1​)=47\
σAtt​(salary)(e1​)=7200.

Vậy:

Att

là **schema của attribute**, còn:

σAtt​

là **store chứa giá trị attribute trong state này**.

---

## 3. σAssoc​: links nào đang tồn tại?

Với mỗi association:

σAssoc​(as)⊂IAssoc​(as).

Ví dụ:

σAssoc​(Management)={(b1​,e1​)}

và:

σAssoc​(Employment)={(b1​,e1​),(b1​,e2​)}.

Điều này nói rằng tại snapshot đó:

```
b1 Employment e1
```

b1 Employment e2

b1 Management e1

và không có link khác của hai associations này.

Figure 3.3 chính là object diagram thể hiện state này: một `Branch` b1​, hai `Employee` e1​,e2​, cùng các attribute values và links `Employment`, `Management`. 

---

# Phần khó nhất của Definition 3.12: multiplicity

Tác giả yêu cầu σAssoc​(as) phải thỏa multiplicity khai báo ở Definition 3.6:

∀i,∀l∈σAssoc​(as):\
∣{l′∣l′∈σAssoc​(as)∧πˉi​(l′)=πˉi​(l)}∣∈πi​(multiplicities(as)).

Công thức trông khó nhưng ý tưởng khá đơn giản.

Giả sử binary association:

as⊆A×B.

Một link là:

l=(a,b).

Muốn kiểm tra multiplicity ở đầu B, ta **giữ cố định a** rồi đếm xem có bao nhiêu b′ liên kết với nó:

(a,b1​),(a,b2​),…

Sau đó kiểm tra số lượng đó có nằm trong tập multiplicity cho phép hay không.

Ví dụ multiplicity là:

{0,1}.

Thì với một a, số object ở đầu kia không được lớn hơn một.

### Ý nghĩa của πi​

πi​(l)

lấy thành phần thứ i của tuple.

Nếu:

l=(a,b,c),

thì:

π1​(l)=a,π2​(l)=b,π3​(l)=c.

Còn:

πˉi​(l)

lấy **tất cả thành phần ngoại trừ thành phần i**.

Ví dụ:

πˉ1​(a,b,c)=(b,c).

Vì vậy với association ba ngôi, multiplicity ở đầu thứ nhất được kiểm tra bằng cách:

> giữ cố định toàn bộ các object ở những đầu còn lại, rồi đếm có bao nhiêu object ở đầu thứ nhất có thể kết hợp với chúng.

Đây là lý do công thức của Richters tổng quát được cho cả binary lẫn n-ary associations.

---

# Definition 3.13 — Interpretation of object models

Sau tất cả những gì trên, Definition 3.13 lại rất ngắn:

> Interpretation của một object model M là tập hợp tất cả các system states có thể có của M. 

Có thể ký hiệu ý tưởng là:

I(M)={σ∣σ laˋ một system state hợp lệ của M}.

Đây là **model-theoretic semantics** cốt lõi của toàn bộ cách tiếp cận.

Một UML model không chỉ có nghĩa là:

> “đây là vài hộp và vài đường nối”.

Ý nghĩa của nó là:

tập taˆˊt cả những snapshot hệ thoˆˊng maˋ caˆˊu truˊc naˋy cho pheˊp​

Ví dụ nếu model nói:

```
Branch ---- Employee
```

        1     \*

thì semantics không phải một object diagram duy nhất.

Nó là **tất cả object diagrams hợp lệ có thể được dựng lên** sao cho:

- \
  objects thuộc đúng classes,
- \
  attribute values có đúng type,
- \
  links thuộc đúng associations,
- \
  multiplicities được thỏa,
- \
  inheritance được tôn trọng.

Một object diagram cụ thể chỉ là:

σ1​∈I(M).

Một object diagram khác:

σ2​∈I(M).

Một object diagram vi phạm multiplicity thì:

σbad​∈/I(M).

Đây chính là nền móng để OCL hoạt động.

---

# Ghép toàn bộ Definitions 3.1–3.13 thành một bức tranh

Ta có thể nhìn toàn bộ Chương 3 bằng chuỗi sau:

3.1 Class↓3.2 Attributes↓3.3 Operations↓3.4 Associations↓3.5 Role names↓3.6 Multiplicities↓3.7 Generalization↓3.8 Full descriptor↓3.9 Object model M​​

Đến **3.9**, ta đã có hoàn chỉnh **cú pháp của model**. Tác giả cũng mô tả Definition 3.9 đúng theo cách đó: M gom `Class`, Attc​, Opc​, `Assoc`, `associates`, `roles`, `multiplicities` và generalization relation vào một cấu trúc duy nhất. 

Sau đó:

3.10 Object domains↓3.11 Possible links↓3.12 One concrete state σ↓3.13 All possible states​​

Và đây mới là **semantics**.

Tóm lại bằng một công thức tinh thần:

M↦{σ1​,σ2​,σ3​,…}​

Trong đó M là specification, còn mỗi σi​ là một system snapshot hợp lệ.

Điểm này rất quan trọng để sang phần OCL sau này: **OCL constraint sẽ tiếp tục thu hẹp tập states này**. Class diagram/BML cho ta một không gian trạng thái khả dĩ; constraint sẽ loại bỏ những state không thỏa điều kiện. Chương 3 vì thế không phải phần “phụ trước OCL”; nó chính là nền semantic mà OCL trong luận văn dựa lên. Tác giả nói ngay từ đầu Chương 3 rằng object model cung cấp context cần thiết cho OCL và formalization ở Chapters 4–5 dựa trên framework này. 

**Tôi dừng ở Definition 3.13 như bạn yêu cầu, không đi vào Definition 4.1 trở đi.**

Đúng, cách bạn đang hình dung **gần đúng**, nhưng có một chỗ rất quan trọng: **Chương 4 không định nghĩa “OCL nói chung”, và Chương 5 không chỉ định nghĩa constraints**. Ba chương 3–4–5 được xây nối tiếp nhau thành một chuỗi khá đẹp:

Chương 3: UML object model⟶Chương 4: kiểu + pheˊp toaˊn của OCL⟶Chương 5: biểu thức OCL + constraints​

Chính tác giả cũng tóm tắt cấu trúc luận văn như vậy: Chương 3 cung cấp một subset UML làm context cho OCL; Chương 4 formalize **types và operations của OCL**; Chương 5 formalize **syntax/semantics của expressions và constraints**, gồm invariants và pre/postconditions. 

## Chương 3 — “Thế giới UML mà OCL sẽ nói về”

Chương 3 có nhiệm vụ định nghĩa hình thức một **object model**.

Tức là nó trả lời các câu hỏi:

- \
  Model có những class nào?
- \
  Class có attribute và operation gì?
- \
  Các class nối với nhau bằng association nào?
- \
  Role name, multiplicity là gì?
- \
  Inheritance/generalization được hiểu thế nào?
- \
  Một object là gì?
- \
  Một link là gì?
- \
  Một **system state** là gì?
- \
  Và cuối cùng: semantics của một object model là tập những system state nào?

Tác giả nói rất rõ rằng object model này chỉ giữ những yếu tố UML cần thiết cho static structure và nó được xây dựng vì OCL cần một context chính xác để hoạt động. Formalization OCL ở Chương 4 và 5 dựa trên framework của Chương 3. 

Có thể tưởng tượng Chương 3 tạo ra:

M

là UML model, và:

σ

là một snapshot/state cụ thể của model đó.

Ví dụ model M có:

```
Person
```

    age : Integer

Employee --|&gt; Person

Branch ----- Employee

Một state σ có thể chứa:

```
p1 : Person
```

age = 25

e1 : Employee

age = 40

b1 : Branch

cùng các links giữa b1​ và e1​.

Cho nên bạn có thể nhớ:

Chương 3 = UML model + trạng thaˊi của model​

---

# Chương 4 — “OCL có những loại giá trị nào và được phép làm gì với chúng?”

Đây là phần dễ bị nhầm nhất.

Chương 4 **không còn chủ yếu định nghĩa object model nữa**. Object model đã có ở Chương 3.

Chương 4 lấy object model M đó làm đầu vào và xây dựng **type system của OCL**.

Ngay đầu chương, tác giả nói OCL là strongly typed language: mọi OCL expression đều có một type. Chương này formalize type system, xác định domain của các type, đồng thời định nghĩa abstract syntax và semantics của các operation. 

Nói dễ hiểu, trước khi có thể hiểu:

```
self.age > 18
```

ta phải biết:

1. `self` có type gì?
2. `age` có thể được áp dụng lên `self` không?
3. `age` trả về type gì?
4. `18` thuộc type gì?
5. \
   toán tử `>` nhận những type nào?
6. `>` trả về type gì?
7. \
   nếu giá trị undefined thì chuyện gì xảy ra?

Đó chính là việc của **Chương 4**.

---

## 4.1 Những type nào tồn tại?

Chương 4 định nghĩa những type như:

Integer, Real, Boolean, String

rồi enumeration types, object types, collection types:

Set(T), Bag(T), Sequence(T), Collection(T)

và special type như:

OclAny.

Nó cũng định nghĩa subtype hierarchy, chẳng hạn:

Integer≤Real

và các quan hệ giữa collection types. Hình 4.1 của luận văn chính là sơ đồ hệ kiểu này.  

---

# Một kết nối cực kỳ quan trọng giữa Chương 3 và Chương 4

Giả sử Chương 3 có:

Person∈Class.

Sang Chương 4, class đó tạo ra **OCL object type**:

Person.

Tức là:

Ch3: Person laˋ class​

dẫn đến:

Ch4: Person cu˜ng cho ta một OCL type​

Tác giả ghi rõ object type tương ứng với classifier/class trong object model. 

Đây là cầu nối UML → OCL.

---

# Chương 4 còn định nghĩa Operations

Đây là phần rất quan trọng.

Một OCL expression về cơ bản sẽ sử dụng rất nhiều **operations**.

Ví dụ:

```
1 + 2
```

dùng:

+:Integer×Integer→Integer.

Hay:

```
self.age
```

cũng được formalize như một operation kiểu:

age:Person→Integer.

Hay:

```
self.employee
```

navigation qua association cũng được chuyển thành một **navigation operation**.

Hay:

```
employees->size()
```

sử dụng:

size:Collection(T)→Integer.

Chương 4 thậm chí định nghĩa semantics của operation thành một hàm toán học:

I(ω):I(t1​)×⋯×I(tn​)→I(t).

Ví dụ `includes` về trực giác có semantics:

includes(C,x)=(x∈C).

`size`:

size(C)=∣C∣.

Bảng 4.4 của luận văn thể hiện chính xác kiểu này cho collection operations. 

---

# Vậy kết quả cuối cùng của Chương 4 là gì?

Cuối chương, tất cả được gom thành một thứ gọi là **data signature**:

ΣM​=(TM​,≤,ΩM​)​

Trong đó:

TM​

= tất cả các OCL types có thể dùng với model M,

≤

= subtype hierarchy,

và:

ΩM​

= tất cả các operations có thể sử dụng.

Tác giả nói thẳng rằng data signature này là đầu vào trực tiếp cho Chương 5. 

Đây là chìa khóa để hiểu cấu trúc luận văn:

MChapter 4​ΣM​​

Hay bằng lời:

> **Cho tôi một UML object model M, Chương 4 sẽ xác định toàn bộ “vũ trụ kiểu và phép toán OCL” có thể sử dụng để nói về model đó.**

---

# Chương 5 — Bây giờ mới thực sự xây “câu OCL”

Đây là bước tiếp theo.

Chương 4 mới cho ta:

> những type gì tồn tại và những operations gì được phép dùng.

Nhưng nó vẫn chưa nói đầy đủ:

> một **OCL expression** được cấu tạo như thế nào?

Ví dụ:

```
self.age > 18
```

hay:

```
self.employee->forAll(e | e.salary > 0)
```

hay:

```
if self.age > 18 then ... endif
```

Đó là việc của **Chương 5**.

Ngay đầu Chương 5, tác giả nói:

> Core của OCL là một **expression language**.

Và expressions sau đó có thể được dùng trong nhiều context khác nhau để tạo invariants, preconditions và postconditions. Chương 5 formalize syntax và semantics của OCL expressions rồi định nghĩa chính xác context, invariant và pre/postcondition. 

---

# Do đó Chương 5 thực ra có hai tầng

Đây là cách tôi nghĩ bạn nên nhìn nó:

5.1 Expression language​

rồi trên đó:

5.1/5.2 Constraints​

Chứ không phải ngay từ đầu:

Chapter 5=constraints.

---

## Tầng 1: Expression

Chương 5 lấy:

ΣM​=(TM​,≤,ΩM​)

của Chương 4 và nói:

> từ những thành phần này, ta có thể xây expression như thế nào?

Tác giả nói rõ expression syntax được xây **dựa trên data signature của Chương 4**. 

Ví dụ từ operation:

&gt;:Integer×Integer→Boolean

và:

age:Person→Integer

ta có thể xây:

&gt;(age(self),18).

Concrete OCL viết:

```
self.age > 18
```

Và kết quả là expression thuộc:

ExprBoolean​.

Definition 5.1 formalize chính chuyện này: nếu có operation

ω:t1​×⋯×tn​→t

và các expression ei​ đúng type, thì:

ω(e1​,…,en​)

là một expression type t. 

---

# Nhưng syntax thôi chưa đủ

Ta còn phải biết:

```
self.age > 18
```

**được tính như thế nào** trong một state σ.

Đó là phần **semantics of expressions**.

Nói khái niệm, nó có dạng:

I\[\[e\]\]σ,β​

tức:

> evaluate expression e trong system state σ, dưới một variable environment/binding β.

Đây là chỗ Chương 3 quay lại.

Chương 3 cho ta:

σ.

Chương 4 cho ta semantics của `age` và `>`.

Chương 5 ghép chúng lại để evaluate cả expression:

self.age&gt;18.

---

# Sau khi có expression, constraint trở nên rất đơn giản về mặt ý tưởng

Đây chính là chỗ câu hỏi của bạn:

> “chương 5 đi định nghĩa về các ràng buộc?”

**Đúng. Nhưng constraint được định nghĩa trên nền expression.**

Ví dụ:

```
context Person
```

inv:

    self.age &gt; 0

Phần:

```
self.age > 0
```

là một:

OCL Boolean expression​

Còn:

```
context Person inv:
```

nói:

> expression Boolean này được sử dụng với **vai trò là invariant của Person**.

Đó là sự khác biệt rất quan trọng:

expression=constraint​

mà đúng hơn:

constraint=expression được đặt trong một constraint context​

---

# Một expression không nhất thiết phải là constraint

Ví dụ:

```
self.age
```

có type:

Integer.

Nó không phải constraint.

Hay:

```
self.employee
```

có thể có type:

Set(Employee).

Nó cũng không phải constraint.

Đây có thể là **query**.

Nhưng:

```
self.age > 18
```

có type:

Boolean.

Nó có thể được dùng làm constraint.

Chính Chương 5 cũng phân biệt invariant, query và các context khác nhau. 

---

# Invariant là gì trong toàn bộ framework?

Bây giờ ta ghép Chương 3–4–5 lại.

Có model:

M.

Chương 3 cho ta các trạng thái:

σ∈I(M).

Giả sử constraint:

```
context Person inv:
```

    self.age &gt; 0

Chương 5 nói một state σ hợp lệ đối với invariant nếu invariant evaluate thành `true`; nếu kết quả undefined thì state đó cũng không hợp lệ. 

Trực giác là:

∀p∈σ(Person):age(p)&gt;0.

Như vậy constraint **lọc** các state.

Ban đầu Chương 3 cho:

I(M)={mọi state phuˋ hợp class model}.

Thêm OCL constraint C:

I(M,C)={σ∈I(M)∣C đuˊng trong σ}.

Đây gần như là bức tranh trung tâm của cả luận văn.

---

# Precondition và postcondition hơi khác invariant

Invariant chủ yếu nhìn **một state**:

σ.

Ví dụ:

```
context Employee inv:
```

    self.salary &gt; 0

Còn một operation:

```
raiseSalary(amount)
```

có thể có:

```
pre:
```

    amount &gt; 0

post:

    self.salary = self.salary@pre + amount

Lúc đó ta cần:

σpre​

và:

σpost​.

Postcondition mô tả quan hệ giữa state trước và state sau.

Vì vậy Chapter 5.2 riêng hẳn ra để định nghĩa semantics của pre/postconditions. Ngay phần giới thiệu chương cũng nói Section 5.2 định nghĩa meaning của operation specifications với pre/postconditions. 

---

# Nếu rút gọn cực mạnh ba chương

Bạn có thể ghi vào đầu tài liệu thế này:

| Chương | Câu hỏi nó trả lời |
| --- | --- |
| **3 — Static Structure Modeling** | **Hệ thống/model mà OCL đang nói về là cái gì?** |
| **4 — OCL Types and Operations** | **Trong OCL có những loại giá trị nào và ta được phép làm những phép toán nào lên chúng?** |
| **5 — OCL Expressions and Constraints** | **Từ các type và operations đó, một biểu thức OCL được tạo/evaluate thế nào, và khi dùng biểu thức làm invariant/pre/postcondition thì nghĩa chính xác là gì?** |

Hay bằng ký hiệu:

Ch.3M,σ​​→Ch.4ΣM​=(TM​,≤,ΩM​)​​→Ch.5Expr, I\[\[Expr\]\]​​→Ch.5Invariant, Pre, Post​​​

Tôi nghĩ đây là cách quan trọng nhất để đọc luận văn này.

---

## Một ví dụ duy nhất xuyên suốt 3 chương

Giả sử UML có:

```
Person
```

\----------------

age : Integer

### Chương 3

Formalize:

Person∈Class

và:

age:Person→Integer.

Một state:

σClass​(Person)={p1​,p2​}\
σAtt​(age)(p1​)=20\
σAtt​(age)(p2​)=−5.

Chương 3 chỉ nói state này **có cấu trúc hợp lệ**.

---

### Chương 4

Bây giờ OCL có:

Person

là object type,

Integer

là basic type,

và operation:

age:Person→Integer

cũng như:

&gt;:Integer×Integer→Boolean.

Tức Chương 4 cung cấp vật liệu để viết OCL.

---

### Chương 5

Từ đó xây expression:

self.age&gt;0.

Nó có type:

Boolean.

Rồi đặt expression đó vào context:

```
context Person inv:
```

    self.age &gt; 0

Bây giờ nó trở thành invariant.

Evaluate:

p1​:20&gt;0=true

nhưng:

p2​:−5&gt;0=false.

Do đó state σ này bị invariant loại bỏ.

---

Và đây chính là ý tưởng tổng thể:

Ch3 tạo ra khoˆng gian trạng thaˊi​\
Ch4 tạo ra ngoˆn ngữ kiểu + pheˊp toaˊn để quan saˊt trạng thaˊi​\
Ch5 tạo ra biểu thức OCL vaˋ duˋng chuˊng để đặt đieˆˋu kiện leˆn trạng thaˊi​

Cho nên câu của bạn **“Chương 3 định nghĩa object model, Chương 4 định nghĩa OCL, Chương 5 định nghĩa constraints”** có thể sửa cho chính xác hơn thành:

> **Chương 3 formalize phần UML/object model làm miền mà OCL nói về. Chương 4 formalize tầng type system và operations của OCL trên object model đó. Chương 5 dùng tầng Chương 4 để formalize chính expression language của OCL, rồi mới định nghĩa invariant, query, precondition và postcondition dựa trên expressions.**

Đặc biệt, nếu bạn đang cố hiểu luận văn để sau này **formalize USE/OCL**, thì tôi sẽ coi **3.9 → 3.12 → 4.16 → 5.1 → 5.2** là “xương sống” của toàn bộ formalization: object-model syntax → state → OCL data signature → expression syntax → expression semantics.

<img src="https://chatgpt.com/backend-api/estuary/content?id=file_00000000aa0881f89bd83232b2ed4253&amp;ts=496451&amp;p=fs&amp;cid=1&amp;sig=964800962f944fc16cc59e09fd7e9c4cef2ed4e2c1b871a2820e5f89958f0c81&amp;v=0" alt="9b5d3e37-ff9b-473b-b1ad-bf1e196dd9a0.png" width="659" />ojs.acl

File

Tôi có một ngôn ngữ mở rộng hơn của class diagram. Trong đó tôi mở rộng hơn để biểu diễn được cả các thực thể có ý định. Trong đó bao gồm actor là các thực thể có ý đinh. Tôi chia nhỏ actor hơn thành group và role là 2 khái niệm khác nhau. Trong đó. group biểu diễn những nhóm actor, rằng trong groupd có thể có nhiều actor khác và các actor này có mục đích là hỗ trojw để đạt được mục đich duy nhất. Trong khi đó role lại thể hiện các phần tử có mục đích riêng lẻ của chúng và chỉ cố gắng để đạt được mục tiêu của chúng, và co thể mục tiêu đó đạt được mucjtieeu của group. Tôi có gửi cho bạn file mô tả bằng ACL và meta model của nó. Bạn xem qua và liệu rằng có thể đặc tả nó giống với model object của bài báo được không ? 

Có. Và theo tôi, **ngôn ngữ ACL của bạn hoàn toàn có thể được đặc tả theo đúng “phong cách” Chương 3 của Richters**. Thậm chí cấu trúc hiện tại của metamodel khá thuận lợi để làm việc đó.

Nhưng có một điểm quan trọng: nếu bạn muốn đặc tả **không chỉ cấu trúc Group/Role mà cả ý nghĩa “có mục đích/intention” của chúng**, thì metamodel và file ACL hiện tại **chưa đủ thông tin**. Hiện tại chúng cho phép phân biệt `group`, `role`, `entity` về mặt cú pháp, nhưng chưa có `Goal` hoặc quan hệ “supports goal”, nên chưa thể formalize chính xác câu:

> Role theo đuổi mục tiêu riêng; các mục tiêu này có thể hỗ trợ mục tiêu chung duy nhất của Group.

Tôi sẽ tách vấn đề này ra ở dưới.

---

# 1. Trước hết: cách bạn đang nghĩ về Richters là đúng

Richters làm đại khái thế này:

Abstract syntax​⟶Interpretation / system state​

Ở Chương 3 ông không formalize toàn bộ UML, mà chọn một tập các khái niệm cấu trúc đủ dùng cho OCL. Object model gồm class, attributes, operations, associations, role names, multiplicities và generalization. Sau đó ông định nghĩa object, link, system state và cuối cùng semantics của model là tập các system states hợp lệ. 

Với ngôn ngữ của bạn, ta có thể làm đúng cùng một việc:

ACL model​⟶caˊc ACL system states hợp lệ​

Chỉ khác là **vocabulary của bạn giàu hơn BML của Richters**.

---

# 2. Mapping giữa Richters và ngôn ngữ của bạn

Từ metamodel bạn gửi, tôi đọc được cấu trúc chính như sau:

Classifier

là abstract superclass, bên dưới có ít nhất `Datatype`, `Class`, `Relationship`.

`Class` lại có các loại:

Entity,Group,Role.

Còn `Relationship` có:

Association,Composition,Aggregation

và trong hình còn có `Owner`.

Ngoài ra có:

- `Property`,
- `MemberEnd`,
- `Generalization`,
- `Compatibility`.

File `ojs.acl` cũng xác nhận đúng tinh thần này. Ví dụ bạn có:

```
entity Issue
```

entity PublishedArticle

...

role Admin

role Viewer

role Author

...

group OJS

group Journal

group Submission

...

và:

```
role Member specializes GroupMember;
```

role Main specializes GroupMember;

cũng như:

```
GroupMember compatible Author;
```

Viewer compatible Author;

và các relationship:

```
association reviewBelongsToRound {...}
```

composition journalIssues {...}

Cho nên nếu so với Chương 3 của Richters thì có thể hình dung:

| Richters BML | ACL của bạn |
| --- | --- |
| `Class` | `Entity ∪ Role ∪ Group` |
| `Attribute` | `Property` |
| `Generalization` | `specializes` |
| `Association` | `Relationship` |
| association ends | `MemberEnd` |
| role name của association end | `MemberEnd.role : string` |
| multiplicity | `[1]`, `[0..*]`, `[1..*]`, ... |
| — | `Compatibility` |
| — | Group membership |
| — | distinction Group / Role / Entity |
| — | intentional semantics |

Tức ACL là một **mở rộng khá tự nhiên** của object model của Richters.

---

# 3. Nhưng có một vấn đề thuật ngữ bạn nên sửa ngay khi formalize

Trong Richters, từ **role** dùng trong “role name” của association end.

Ví dụ:

roles(Employment)=⟨employer,employee⟩.

Nhưng trong ngôn ngữ của bạn, `Role` lại là một **loại actor/classifier thực sự**:

```
role Author;
```

role Review;

role SectionEditor;

Trong metamodel của bạn còn có:

```
MemberEnd
```

\----------------

role : string

Như vậy bạn đang có **hai khái niệm hoàn toàn khác nhau cùng tên Role**:

Role classifier

và

association-end role name.

Khi viết formal semantics, tôi rất khuyên đổi cái thứ hai thành, chẳng hạn:

endName

hoặc:

memberEndName

để tránh những công thức cực kỳ khó đọc kiểu:

roles(Role,…).

Đây không phải vấn đề của implementation, nhưng là vấn đề lớn khi viết luận văn hình thức.

---

# 4. Nếu formalize ACL giống Definition 3.1–3.9 thì tôi sẽ bắt đầu thế này

Ta giả sử có tập tên:

N.

Sau đó định nghĩa tập các class-like classifiers:

Class⊆N.

Nhưng thay vì Richters chỉ có `Class`, ta chia nó thành:

Class=Entity∪˙Role∪˙Group.

Dấu:

∪˙

có nghĩa là disjoint union.

Tức là một classifier không đồng thời vừa là `Entity`, vừa là `Role`, vừa là `Group`, nếu đó đúng là ý định thiết kế của bạn.

Theo cách bạn giải thích, tôi còn có thể định nghĩa:

Actor=Role∪Group.

Còn:

Entity

là các thực thể domain thông thường không nhất thiết có intentionality.

Đây là một điểm mà bạn nên viết rất rõ trong luận văn:

Actor=Role∪Group​

nếu đúng với ngôn ngữ bạn đang xây dựng.

---

# 5. Ví dụ với chính file OJS của bạn

Ta có thể thu được:

Entity={​Issue, PublishedArticle, ArticleFile,SubmissionRecord, ReviewRound}.​

Và:

Role={​Admin, Viewer, Guest, GroupMember, Member, Main,SectionEditor, Author, Edit, Review}.​

Trong khi:

Group={OJS, Journal, GroupUser, Section, Submission}.

Đây gần như tương đương trực tiếp với Definition 3.1 của Richters, chỉ có điều ta phân loại class rõ hơn.

---

# 6. Property có thể formalize gần như y hệt Attribute của Richters

Richters có:

a:tc​→t.

Bạn cũng có thể định nghĩa:

Propc​

là tập properties của classifier c.

Mỗi property:

p:tc​→t.

Ví dụ từ:

```
entity Issue {
```

    volume : Integer required;

}

ta có:

volume:Issue→Integer.

Từ:

```
role SectionEditor {
```

    sectionEditorID : Integer required;

}

ta có:

sectionEditorID:SectionEditor→Integer.

Từ:

```
group Journal {
```

    journalID : Integer required;

    title : String required;

}

ta có:

journalID:Journal→Integer

và:

title:Journal→String.

Như vậy **Group và Role vẫn có properties như class bình thường**.

Điều này hoàn toàn tương thích với cách Richters xây attribute signatures.

---

# 7. Generalization của bạn cũng gần như Definition 3.7–3.8

Trong file:

```
role Member specializes GroupMember;
```

role Main specializes GroupMember;

ta có thể đặt:

Member≺GroupMember

và:

Main≺GroupMember.

Sau đó định nghĩa full descriptor giống Richters:

Propc∗​=Propc​∪c′∈parents(c)⋃​Propc′​.

Nếu sau này Role có operations/constraints thì cũng tương tự.

Như vậy `Member` sẽ thừa hưởng properties của `GroupMember`.

Ví dụ `GroupMember` có:

```
description : String;
```

thì:

description∈PropMember∗​.

Đây gần như bê nguyên cơ chế Definition 3.8 của Richters sang được.

---

# 8. Relationship cũng formalize được rất tự nhiên

Richters dùng:

Assoc

và:

associates(as)=⟨c1​,…,cn​⟩.

Bạn có thể tổng quát thành:

Relationship=Association∪˙Composition∪˙Aggregation\[∪˙Owner\].

Với mỗi relationship r:

ends(r)=⟨e1​,…,en​⟩.

Mỗi member end có thể là:

ei​=(ci​,namei​,Mi​)

trong đó:

- ci​: target classifier,
- namei​: member-end name,
- Mi​: multiplicity.

Ví dụ:

```
association reviewBelongsToRound {
```

    Review \[1..\*\];

    ReviewRound \[1\];

}

formalize thành:

ends(reviewBelongsToRound)=⟨(Review,{1,2,…}),(ReviewRound,{1})⟩.

Còn:

```
association submissionContextPublication {
```

    Submission \[1\];

    PublishedArticle \[0..1\];

}

thành:

ends(submissionContextPublication)=⟨(Submission,{1}),(PublishedArticle,{0,1})⟩.

Đây chính xác là kiểu formalization multiplicity của Definition 3.6.

---

# 9. Nhưng Group membership của bạn **không nên đánh đồng với Association**

Đây là điểm rất quan trọng.

Trong file của bạn:

```
group Submission {
```

    Author \[1\];

    Edit   \[1..\*\];

    Review \[1..\*\];

}

có một ý nghĩa khác với:

```
association submissionContextRecord {
```

    Submission \[1\];

    SubmissionRecord \[1\];

}

Và chính comment trong file cũng nhấn mạnh điều này đối với:

```
composition journalIssues
```

rằng association/composition với organizational context **không tự động biến entity thành group member**.

Vậy formal model của bạn phải có **một khái niệm Group Membership riêng**.

Tôi sẽ định nghĩa chẳng hạn:

members:Group→P(Class×Mult).

Ví dụ:

members(Submission)={(Author,{1}),(Edit,{1,2,…}),(Review,{1,2,…})}.

Và:

members(OJS)={(Journal,{1,2,…}),(GroupUser,{1,2,…})}.

Điều này cực kỳ quan trọng bởi vì nó chính là **phần ACL có mà object model của Richters không có**.

---

# 10. Sau đó mới đến semantics — tương đương Definitions 3.10–3.13

Đây là chỗ rất thú vị.

Ta có thể làm gần như Richters.

Với mỗi classifier c, có:

oid(c)={c1​,c2​,…}

là các identifiers tiềm năng.

Rồi một system state ACL có thể định nghĩa:

σ(M)=(σClass​,σProp​,σRel​,σMember​)​

So với Richters:

(σClass​,σAtt​,σAssoc​)

thì bạn thêm:

σMember​.

---

# 11. σClass​ — actor/entity nào đang tồn tại

Ví dụ:

σClass​(Author)={a1​,a2​}\
σClass​(Review)={r1​,r2​,r3​}\
σClass​(Submission)={s1​}.

Nghĩa là snapshot hiện tại có:

- \
  2 Author,
- \
  3 Review actors,
- \
  1 Submission group.

Generalization vẫn cho semantics kiểu inclusion như Richters:

Member≺GroupMember⇒I(Member)⊆I(GroupMember).

Richters cũng dùng chính domain-inclusion semantics này cho inheritance. 

---

# 12. σProp​ — giá trị thuộc tính hiện tại

Ví dụ:

σProp​(reviewID)(r1​)=10

hay:

σProp​(title)(s1​)="Paper X".

Hoàn toàn tương tự:

σAtt​

của Richters.

---

# 13. σRel​ — links của Association / Composition / Aggregation

Ví dụ:

σRel​(reviewBelongsToRound)={(r1​,rr1​),(r2​,rr1​)}.

Về bản chất vẫn là:

σRel​(r)⊆I(c1​)×⋯×I(cn​).

Giống Definition 3.11–3.12 của Richters cho association/link.

---

# 14. Và bạn cần thêm σMember​ cho Group

Đây mới là phần đặc trưng của ngôn ngữ của bạn.

Ta có thể định nghĩa:

σMember​(g,c)⊆σClass​(g)×σClass​(c).

Ví dụ:

σMember​(Submission,Author)={(s1​,a1​)}.\
σMember​(Submission,Review)={(s1​,r1​),(s1​,r2​)}.

Nếu declaration nói:

```
Review [1..*]
```

thì với mỗi instance s của `Submission`:

∣{r∣(s,r)∈σMember​(Submission,Review)}∣≥1.

Đây chính là cách bạn biến syntax:

```
Review [1..*]
```

thành semantics thực sự.

---

# 15. Như vậy một ACL state có hình dáng

Tôi nghĩ cấu trúc sau khá hợp lý:

σ(M)=(σC​,σP​,σR​,σG​)​

trong đó:

σC​

= classifier instances,

σP​

= property values,

σR​

= relationship links,

σG​

= group membership.

Và interpretation của model:

I(M)={σ∣σ thỏa taˆˊt cả structural rules của M}​

hoàn toàn tương đương tinh thần Definition 3.13 của Richters, nơi semantics của object model là tập tất cả system states có thể có. 

---

# 16. Compatibility là phần bạn phải quyết định semantics

Đây là một phần tôi chưa thể tự kết luận chỉ từ metamodel/file.

Bạn viết:

```
GroupMember compatible Author;
```

và:

```
Viewer compatible Author;
```

Ta có thể định nghĩa cú pháp rất dễ:

Compatible⊆Role×Role

hoặc, nếu compatibility có scope theo group:

Compatible⊆Group×Role×Role.

Ví dụ:

(OJS,GroupMember,Author)∈Compatible

và:

(Journal,Viewer,Author)∈Compatible.

Tôi hơi nghiêng về dạng thứ hai vì declaration `compatible` đang nằm **bên trong group block**, nghĩa là nó có vẻ mang context của group.

Nhưng semantics của nó thì tôi chưa nên tự suy đoán.

Ví dụ có thể bạn muốn nói:

> cùng một actor instance được phép đồng thời đóng hai Role này.

Nếu đúng, semantics có thể là một rule về role assignment.

Nhưng nếu `compatible` có nghĩa khác — chẳng hạn hai goal có thể đồng tồn tại, hoặc một role có thể substitute role khác — thì semantics phải khác.

**Phần này cần chính định nghĩa ngôn ngữ của bạn quyết định.**

---

# 17. Composition và Aggregation cũng cần semantics riêng

Richters thực ra cố tình né việc đưa aggregation/composition vào core object model vì semantics phức tạp; ông nói các restriction như “object không được trực tiếp hay gián tiếp là part của chính nó” có thể biểu diễn bằng constraint. 

Ngôn ngữ của bạn thì đưa:

Composition, Aggregation

thành first-class concepts.

Không có vấn đề gì, nhưng lúc đó bạn phải quyết định những rule như:

- \
  composition có yêu cầu một part chỉ có tối đa một composite không?
- \
  containment có được tạo cycle không?
- \
  aggregation có cho phép shared part không?
- \
  xóa owner có kéo theo xóa part không?
- \
  composition là state relation hay ownership relation?

Những thứ này **không thể chỉ dừng ở metamodel inheritance**:

Composition&lt;:Relationship.

Bạn cần định nghĩa semantic conditions trên:

σRel​(composition).

---

# 18. Phần quan trọng nhất: Group và Role hiện vẫn chưa được phân biệt bởi “intention”

Đây là điểm tôi nghĩ bạn cần đặc biệt chú ý.

Bạn giải thích rất rõ về mặt khái niệm:

### Group

Group chứa nhiều actors và:

> các actor trong group phối hợp/hỗ trợ để đạt được **một mục tiêu chung của group**.

### Role

Role là một actor intentional riêng:

> nó có mục tiêu riêng của chính nó và cố gắng đạt mục tiêu ấy; mục tiêu đó **có thể** góp phần vào mục tiêu của group.

Đây là một semantic distinction rất mạnh.

Nhưng trong metamodel hiện tại tôi chỉ thấy:

Group&lt;:Class

và:

Role&lt;:Class.

Trong ACL cũng chỉ có:

```
group ...
```

role ...

Tôi **không thấy một construct** `Goal` trong file bạn gửi.

Do đó với metamodel hiện tại, ta mới formalize được:

Group=Role

ở mức **syntactic category**.

Nhưng chưa formalize được:

Group coˊ collective goal​

hay:

Role coˊ individual goal​

hay:

goal(Role) supports goal(Group)​

---

# 19. Nếu intentionality là cốt lõi của ngôn ngữ thì tôi sẽ thêm Goal vào formal model

Ví dụ thêm:

Goal

là tập các goal specifications.

Nếu mỗi Group có đúng **một mục tiêu chung**, ta có thể định nghĩa:

groupGoal:Group→Goal.

Nếu một Role có một hoặc nhiều individual goals:

roleGoal:Role→P+(Goal).

Sau đó thêm quan hệ:

supports⊆Goal×Goal.

Ví dụ:

g1​∈roleGoal(Author)

và:

g2​=groupGoal(Submission).

Nếu goal của Author hỗ trợ collective goal của Submission:

(g1​,g2​)∈supports.

Bây giờ ta mới formalize được câu bạn vừa mô tả bằng ngôn ngữ tự nhiên.

---

# 20. Thậm chí có thể định nghĩa điều kiện cho Group

Giả sử g∈Group.

Các actor types thuộc group:

members(g).

Ta có thể nói collective goal của group là:

Gg​=groupGoal(g).

Và các individual goals của members có quan hệ đóng góp:

supports(Gr​,Gg​).

Nhưng tôi **không khuyên ngay lập tức đặt luật rằng mọi member goal bắt buộc phải support group goal**, bởi vì chính bạn nói:

> role có mục đích riêng và **có thể** mục tiêu đó đạt/hỗ trợ mục tiêu của group.

“Có thể” khác với “bắt buộc”.

Vậy có lẽ relation:

supports

nên là explicit relation trong model, chứ không phải một invariant mặc định.

---

# 21. Tôi sẽ chia formalization của ACL thành ba tầng

Đây là cấu trúc mà tôi nghĩ sẽ rất sạch trong luận văn của bạn.

### Tầng 1 — Structural abstract syntax

Định nghĩa:

Entity, Role, Group,Property,Relationship,Generalization,Compatibility,Membership,Multiplicity.

Đây tương đương phần **3.1–3.9** của Richters.

---

### Tầng 2 — Structural semantics

Định nghĩa:

Object/Actor instances,Property values,Relationship links,Group membership,System state.

Sau đó:

I(M)=all valid states.

Đây tương đương **3.10–3.13**.

---

### Tầng 3 — Intentional semantics

Đây là phần mới của riêng ngôn ngữ bạn:

Goal,ActorGoal,GroupGoal,supports/contributesTo,...

và các well-formedness/semantic rules liên quan.

Tôi sẽ **không trộn phần này vào ordinary class-diagram semantics quá sớm**.

Tách ra sẽ làm thesis dễ đọc hơn rất nhiều.

---

# 22. Nếu viết một tuple tương tự Definition 3.9, ACL của bạn có thể trông như thế này

Một phiên bản đầu tiên có thể là:

MACL​=(Entity,Role,Group,Prop,Rel,ends,mult,member,≺,compatible)​

Nếu thêm intentionality:

MACL​=(Entity,Role,Group,Prop,Rel,ends,mult,member,≺,compatible,Goal,actorGoal,groupGoal,supports)​

Tôi còn có thể làm kỹ hơn và đưa `Datatype`, `Enumeration`, `PrimitiveType`, relationship kinds vào tuple, nhưng về tư tưởng thì đây chính là analogue của:

M=(Class,Attc​,Opc​,Assoc,associates,roles,multiplicities,≺)

của Richters.

---

# 23. Và tôi nghĩ đây mới là điểm mạnh trong hướng của bạn

Bạn không cần cố chứng minh:

> “ACL cũng chỉ là UML class diagram”.

Ngược lại, cách hợp lý hơn là nói:

ACL generalizes the structural object model​

bằng cách thêm một phân loại actor có intentional semantics:

Actor=Group∪Role,

thêm explicit group membership:

Member,

thêm:

Compatibility,

và giữ các structural constructs quen thuộc:

Entity, Property, Generalization, Relationship, Multiplicity.

Sau đó bạn có thể nói rằng semantics được xây **theo cùng kiểu set-theoretic/state-based approach** của Richters.

Đây là một câu chuyện nghiên cứu khá mạch lạc.

---

# 24. Có một sơ đồ tổng quát rất dễ nhớ

Richters:

Class model→Objects + links→System state​

ACL của bạn:

Entity + Role + Group+Properties + Relationships+Group membership+Generalization + Compatibility​​\
↓\
Entity instancesRole instancesGroup instancesRelationship linksMembership links​​\
↓\
σACL​​

rồi:

I(MACL​)={σACL​∣σACL​ hợp lệ}.​

Sau đó nếu thêm intentionality:

Goal+supports/contributesTo​

đặt lên trên framework đó.

---

**Vì vậy câu trả lời là: có, và tôi nghĩ đây chính là cách rất phù hợp để formalize ngôn ngữ của bạn.** Nhưng tôi sẽ không bê nguyên 13 definitions của Richters rồi chỉ đổi tên. Ta nên lấy **phương pháp của ông ấy**:

syntax→interpretation→state→set of valid states,

rồi thêm những semantic concepts riêng của ACL.

Điểm tôi muốn xử lý tiếp theo trước tiên là **Group/Role/Entity + group membership + compatibility**, bởi vì đó là phần quyết định ACL thực sự khác object model thông thường ở đâu. Sau đó mới thêm Goal/intention. Nếu làm như vậy, ta có thể viết ra một bộ **Definition ACL.1, ACL.2, …** tương ứng với phong cách Definition 3.1–3.13 của Richters, và dùng luôn chính ví dụ `SystemOJS` của bạn để kiểm tra từng định nghĩa.

CÓ một chút sujw khác biệt. Ở object model chỉ có 2 khái niệm là object và link. Nhưng của tôi thêm một khái niệm nữa là agent. Agent chỉ có mỗi tên. Khác Với object mang tính kế thừa, và chỉ cần một thể hiện của object đó là được. Ví dụ như emploee kế thừa lớp person trong class thì khi ở bên object, chỉ cần objet : employee là object vừa là employee vừa là person rồi. Tuy nhiên trong phương pháp của tôi, nó chỉ áp dụng được với group với entity. Còn với role như employee kế thừa lớp person. thì tức là agent đó phải play lớp person trước thì mới đóng vai employee được. 

À, vậy thì sự khác biệt của ngôn ngữ bạn **cơ bản hơn tôi hiểu ở câu trước**. Với mô tả mới này, tôi sẽ **không formalize** `Role` **giống** `Class` **của Richters nữa**.

Trong semantics của bạn thực ra có **ba loại thực thể ở runtime**:

Object,Link,Agent​

và `Entity`, `Group`, `Role` không có cùng một kiểu semantics.

Điểm mấu chốt là:

Entity/Group duˋng semantics kiểu object classification​

nhưng:

Role duˋng semantics kiểu agent plays role​

Đây là một khác biệt rất quan trọng.

---

## 1. Với Entity và Group: inheritance giống object model của Richters

Giả sử:

Employee≺Person

và ở đây `Employee`, `Person` là các classifier kiểu **Entity** hoặc **Group**.

Richters dùng semantics dạng inclusion:

I(Employee)⊆I(Person).

Nghĩa là chỉ cần tạo:

```
e1 : Employee
```

thì:

e1​∈I(Employee)

và do inheritance:

e1​∈I(Person).

Không cần thêm một instance `Person` khác.

Object đó **đồng thời được classify bởi Employee và Person**.

Đúng như bạn nói:

> chỉ cần một object `e1 : Employee`, object đó đã vừa là Employee vừa là Person.

Đây là standard subtype/classification semantics.

---

# 2. Nhưng Role của bạn không hoạt động như vậy

Giả sử:

EmployeeRole≺PersonRole.

Ở đây ta không nói:

> một object EmployeeRole tự động là một object PersonRole.

Bởi vì **Role không có object instance theo nghĩa thông thường**.

Thứ tồn tại thực sự là:

Agent​

và agent **play** role.

Ví dụ:

a1​∈Agent.

Sau đó có quan hệ:

plays(a1​,PersonRole).

Rồi agent đó mới có thể:

plays(a1​,EmployeeRole).

Vậy picture đúng hơn là:

```
                     Role classifiers
```

                       PersonRole

                           ▲

                           │ specializes

                       EmployeeRole

Runtime:

                         a1 : Agent

                         /       \\

                      plays     plays

                       /           \\

                PersonRole     EmployeeRole

Chứ **không phải**:

```
e1 : EmployeeRole
```

theo semantics object thông thường.

---

# 3. Vì thế ta cần tách hai quan hệ rất rõ

Tôi sẽ không dùng một quan hệ instance-of duy nhất cho tất cả nữa.

Ta cần:

### Với Entity và Group

instanceOf(o,c)

hoặc tương đương dùng tập:

σObj​(c).

### Với Role

plays(a,r).

Trong đó:

a∈Agent,r∈Role.

Đây là hai cơ chế khác nhau.

---

# 4. Runtime universe của bạn nên tách thành Object và Agent

Ví dụ:

Object

là tập các object identifiers.

Agent

là tập các agent identifiers.

Bạn nói agent chỉ có tên, vậy có thể định nghĩa rất đơn giản:

Agent⊆NA​

hoặc:

Agent={a1​,a2​,…}.

Và một hàm:

nameA​:Agent→String.

Nếu “tên” chính là identifier thì thậm chí không cần thêm `name_A`; agent identifier tự nó là tên.

Ví dụ:

Agent={John,Mary,Alice}.

---

# 5. Entity và Group sinh ra Objects

Ta có thể định nghĩa:

ObjectClassifier=Entity∪Group.

Với mỗi:

c∈ObjectClassifier

có tập object identifiers:

oid(c).

Và generalization của `Entity`/`Group` có semantics như Richters:

c1​≺O​c2​⇒I(c1​)⊆I(c2​).

Tôi dùng:

≺O​

để cố ý phân biệt nó với specialization giữa roles.

---

# 6. Role không sinh object identifiers

Đây là thay đổi rất quan trọng so với Richters.

Ta **không nên định nghĩa**:

oid(r)

cho:

r∈Role.

Bởi vì theo semantics bạn vừa mô tả, role không phải entity có identity độc lập.

Không có:

employeeRole1​,employeeRole2​,…

theo kiểu object.

Thay vào đó có một quan hệ:

Play⊆Agent×Role.

Ví dụ:

(John,Person)∈Play

nghĩa là:

> agent John đang play role Person.

Và:

(John,Employee)∈Play

nghĩa là:

> chính agent John đó đang play role Employee.

---

# 7. Lúc này specialization của Role có semantics riêng

Giả sử:

Employee≺R​Person.

Tôi dùng:

≺R​

cho role specialization.

Nếu semantics chỉ là điều kiện tại một snapshot, ta có:

(a,Employee)∈Play⇒(a,Person)∈Play​

cho mọi:

a∈Agent.

Hay:

players(Employee)⊆players(Person)

với:

players(r)={a∈Agent∣(a,r)∈Play}.

Nhìn bề ngoài nó lại tạo ra inclusion:

players(Employee)⊆players(Person),

nhưng **lý do của inclusion hoàn toàn khác với object inheritance**.

---

# 8. Đây là chỗ rất cần phân biệt

Với object classification:

Employee≺O​Person

thì:

e∈I(Employee)⇒e∈I(Person).

Đây là:

> **same object, multiple classifier membership by inheritance.**

Còn với roles:

Employee≺R​Person

thì:

plays(a,Employee)⇒plays(a,Person).

Đây là:

> **same agent, multiple role enactments constrained by role specialization.**

Hai công thức nhìn khá giống:

I(Employee)⊆I(Person)

và:

players(Employee)⊆players(Person)

nhưng ontology khác nhau hoàn toàn.

Đó là điểm bạn nên nhấn mạnh trong formal definition.

---

# 9. Tuy nhiên câu “phải play Person trước” còn mạnh hơn

Đây là chỗ tôi muốn đặc biệt lưu ý.

Nếu bạn nói:

> agent **phải play Person trước**, sau đó mới được play Employee,

thì chỉ viết:

plays(a,Employee)⇒plays(a,Person)

**chưa diễn đạt chữ “trước”**.

Nó chỉ nói tại state hiện tại:

> nếu đang play Employee thì cũng phải đang play Person.

Nó không phân biệt hai trường hợp:

```
thời điểm 1: play Person
```

thời điểm 2: play Employee

với:

```
thời điểm 1: gán Person và Employee cùng lúc
```

Trong cả hai trường hợp state cuối cùng đều có:

{Person,Employee}.

---

# 10. Nếu “trước” thật sự là temporal prerequisite thì bạn cần state transition

Khi đó framework của bạn sẽ phải đi xa hơn Chapter 3 của Richters một chút.

Ta có state:

σ.

Trong state này:

Playσ​⊆Agent×Role.

Giả sử muốn thực hiện action:

play(a,Employee).

Nếu:

Employee≺R​Person

thì transition này chỉ hợp lệ nếu **trong pre-state** đã có:

(a,Person)∈Playσ​.

Sau transition:

σplay(a,Employee)​σ′

ta có:

Playσ′​=Playσ​∪{(a,Employee)}.

Và precondition:

Employee≺R​Person⇒(a,Person)∈Playσ​​

Đây mới chính xác là:

> phải đóng vai Person **trước khi** được đóng Employee.

---

# 11. Ví dụ rất rõ

Ban đầu:

Playσ0​​=∅.

Agent:

John∈Agent.

Role hierarchy:

Employee≺R​Person.

Nếu cố:

σ0​play(John,Employee)​?

thì transition **không hợp lệ**, vì:

(John,Person)∈/Playσ0​​.

Trước tiên phải:

σ0​play(John,Person)​σ1​.

Ta được:

Playσ1​​={(John,Person)}.

Bây giờ mới:

σ1​play(John,Employee)​σ2​.

Và:

Playσ2​​={(John,Person),(John,Employee)}.

Đây chính xác với điều bạn vừa mô tả.

---

# 12. Có nghĩa là generalization của bạn thực chất có hai semantics

Tôi nghĩ formalization nên chia rõ:

Generalization=GeneralizationO​∪˙SpecializationR​.

### Object generalization

Áp dụng cho:

Entity, Group.

Nếu:

c1​≺O​c2​

thì:

I(c1​)⊆I(c2​).

Không có lifecycle riêng cho việc “trở thành parent”.

---

### Role specialization

Áp dụng cho:

Role.

Nếu:

r1​≺R​r2​

thì role r2​ là prerequisite/general role của r1​.

Static condition:

plays(a,r1​)⇒plays(a,r2​).

Nếu bạn cần đúng nghĩa “trước”, thêm transition condition:

play(a,r1​) được pheˊp tại σ⇒playsσ​(a,r2​).

---

# 13. Điều này còn cho ta một distinction rất đẹp về identity

### Object

Object có identity gắn với classifier structure.

Ví dụ:

o:Employee

và do inheritance:

o:Person.

Nhưng vẫn chỉ là **một object o**.

---

### Agent

Agent có identity **độc lập với Role**.

Ví dụ:

John

vẫn là agent John dù hiện tại không play role nào:

rolesσ​(John)=∅.

Sau đó:

rolesσ′​(John)={Person}.

Sau nữa:

rolesσ′′​(John)={Person,Employee}.

Agent không được tạo ra khi Role được instantiate.

Đây là sự khác biệt ontological khá lớn:

Object identity derives from object existence​

trong khi:

Agent identity exists independently of roles played​

---

# 14. Vì vậy system state của ACL nên sửa lại

Ở câu trước tôi đề xuất:

σ=(σC​,σP​,σR​,σG​).

Bây giờ sau clarification của bạn, tôi nghĩ cấu trúc tốt hơn sẽ là:

σ(M)=(σObj​,σAgent​,σProp​,σLink​,σPlay​,σMember​)​

Trong đó:

### σObj​

Objects hiện tồn tại của `Entity` và `Group`.

σObj​(c)⊆oid(c),c∈Entity∪Group.

### σAgent​

Các agents hiện tồn tại.

Có thể đơn giản là một finite set:

σAgent​⊆Agent.

### σProp​

Property values.

### σLink​

Relationship links.

### σPlay​

Role enactment relation:

σPlay​⊆σAgent​×Role.

### σMember​

Nếu group membership là khái niệm riêng của bạn:

σMember​

mô tả member nào thuộc group instance nào.

---

# 15. Thậm chí tôi nghĩ “Agent chỉ có tên” làm semantics sạch hơn

Nếu Agent thực sự chỉ có identity/name và mọi property nằm trên **Role mà nó đang play**, thì ta có:

Agent

chỉ là carrier set.

Ví dụ:

Agent={John,Alice,Bob,…}.

Còn nếu role `Employee` có property:

```
salary : Real
```

thì vấn đề thú vị xuất hiện:

salary

không còn đơn giản là:

Employee→Real

vì `Employee` không có object instances.

Ta phải quyết định property của role được gắn vào **agent-while-playing-role** như thế nào.

Một cách tự nhiên là:

σProp​(salary):playersσ​(Employee)→Real.

Ví dụ:

σProp​(salary)(John)=5000

nếu:

(John,Employee)∈σPlay​.

Đây là một điểm formalization rất quan trọng mà semantics object truyền thống không gặp.

---

# 16. Nếu cùng agent play nhiều Role có property trùng tên thì sao?

Ví dụ:

```
role Person {
```

    name : String;

}

role Employee specializes Person {

    status : String;

}

thì không vấn đề.

Nhưng giả sử:

```
role Employee {
```

    id : Integer;

}

role Author {

    id : String;

}

và cùng agent `John` play cả hai.

Nếu bạn chỉ viết:

σProp​(id)(John)

thì bị nhập nhằng.

Do đó role property có lẽ phải được index bởi role:

σRoleProp​(r,p,a).

Hoặc:

σRoleProp​(r,p):players(r)→I(t).

Ví dụ:

σRoleProp​(Employee,id)(John)=123

và:

σRoleProp​(Author,id)(John)="A−77".

Tôi nghĩ đây sẽ là cách formal sạch hơn.

---

# 17. Có thể định nghĩa “role occurrence” nhưng không cần biến nó thành Object

Một lựa chọn khác là tạo pair:

(a,r)

và xem nó là một **role enactment**.

Ví dụ:

(John,Employee).

Nó không phải object có identity riêng; identity của nó được xác định hoàn toàn bởi:

Agent+Role.

Khi đó property của role có thể là:

salary:Enactment(Employee)→Real

với:

Enactment(r)={(a,r)∣plays(a,r)}.

Tôi khá thích cách này vì nó làm rõ:

- \
  Agent ≠ Role;
- \
  Role ≠ Object;
- `(Agent, Role)` là một enactment;
- \
  role properties thuộc enactment.

---

# 18. Lúc đó semantics có 3 tầng runtime rất đẹp

Object​

là instance của Entity/Group.

Agent​

là intentional individual có identity riêng.

RoleEnactment=(Agent,Role)​

là việc agent đang đóng một role.

Còn:

Link​

là relationship giữa các runtime participants.

Nếu relationship end cho phép Role, thì link endpoint thực sự có thể phải tham chiếu đến:

(a,r)

chứ không chỉ a.

Đây sẽ là vấn đề kế tiếp cần định nghĩa rõ.

---

# 19. Ví dụ relationship với Role

Giả sử ACL có:

```
association writes {
```

    Author \[1\];

    Article \[\*\];

}

Nếu `Author` là Role và `Article` là Entity, một runtime link **không nên chỉ là**:

(John,article1​)

nếu ta muốn biết John tham gia link trong tư cách nào.

Nó nên là:

((John,Author),article1​).

Hoặc dùng notation:

(John@Author,article1​).

Điều này giữ lại semantics:

> John liên hệ với article **trong vai Author**.

Nếu John đồng thời play `Reviewer`, thì association khác có thể chứa:

(John@Reviewer,article1​).

Cùng agent, khác role enactment.

Đây là lý do việc thêm Agent của bạn thực sự không phải một thay đổi nhỏ.

---

# 20. Bây giờ tôi sẽ mô tả ngôn ngữ của bạn khác Richters thế nào

Richters:

Classifier→Object​

và:

Association→Link​

Trong ACL của bạn:

Entity/Group→Object​

nhưng:

Role→Object​

mà:

Agent×Role→RoleEnactment​

và:

Relationship→Link​

trong đó endpoint có thể là Object hoặc RoleEnactment.

Đây là một mở rộng rất rõ về mặt formal.

---

# 21. Tôi nghĩ metamodel hiện tại của bạn chưa thể hiện hết điều này

Hình bạn gửi hiện đang cho:

```
             Class
```

          /    |    \\

      Group  Entity  Role

Điều này dễ khiến người đọc hiểu:

> Group, Entity, Role đều là những class có instance theo cùng một cách.

Nhưng semantics bạn vừa giải thích nói rằng **không phải như vậy**.

Về abstract syntax, chúng có thể đều kế thừa `Class` để reuse property/generalization syntax.

Nhưng semantic interpretation phải tách:

\[\[Entity\]\]⊆Object\
\[\[Group\]\]⊆Object

nhưng:

\[\[Role\]\]⊆Agent×Role

hoặc nói chính xác hơn:

players(r)⊆Agent.

Tức cùng chung metaclass `Class` **không có nghĩa chúng phải có cùng semantic domain**.

Đây là điều bạn cần nói thẳng trong formalization.

---

# 22. Tôi đề xuất bộ định nghĩa lõi mới

Nếu viết theo phong cách Richters, tôi sẽ đi theo thứ tự này:

 1. **Classifiers**

    Class=Entity∪˙Group∪˙Role.

 2. **Properties**.

 3. **Relationships / MemberEnds / multiplicities**.

 4. **Object generalization** cho `Entity ∪ Group`.

 5. **Role specialization** cho `Role`.

 6. **Formal syntax of ACL model**.

 7. **Objects**\
    \
    chỉ cho Entity/Group.

 8. **Agents**\
    \
    một tập runtime riêng.

 9. **Role enactments**

    Playσ​⊆Agent×Role.

10. **Links**\
    \
    endpoints có thể là object hoặc role enactment.

11. **Group membership**.

12. **System state**.

13. **Role specialization validity**:

    plays(a,r1​)∧r1​≺R​r2​⇒plays(a,r2​).

14. \
    Nếu “trước” là temporal thật sự: **state transitions / play transition rule**.

15. **Interpretation of ACL model**:\
    \
    tập tất cả valid states, hoặc nếu có temporal semantics thì tập valid runs/traces.

---

# 23. Đây là điểm cuối cùng rất quan trọng

Nếu ngôn ngữ của bạn chỉ muốn nói:

> Employee chỉ có thể được play nếu Person cũng được play,

thì **state semantics** là đủ:

Employee≺R​Person⇒players(Employee)⊆players(Person).

Nhưng nếu thật sự muốn:

> agent phải **đã đóng Person trước đó**, rồi mới chuyển sang/nhận thêm Employee,

thì Definition kiểu 3.13:

I(M)={valid states}

**không đủ nữa**.

Bạn sẽ cần semantics của **chuỗi trạng thái**:

ρ=σ0​→σ1​→σ2​→⋯

và interpretation trở thành:

I(M)={ρ∣ρ laˋ một execution/run hợp lệ của M}​

hoặc giữ hai mức:

IS​(M)={valid states}

và:

IT​(M)={valid transitions/traces}.

Theo mô tả mới của bạn, đây là điểm tôi muốn xác định **trước khi viết formal definitions hoàn chỉnh**: từ “phải play `Person` trước rồi mới play `Employee`” của bạn là **ràng buộc thứ tự thời gian thật sự**, hay chỉ có nghĩa rằng **một agent đang play Employee thì bắt buộc đồng thời cũng phải play Person**? Hai semantics này khác nhau khá đáng kể.

à quên, Với role thì cũng có thể dịch sang được object. Chỉ là kế thừa của role khá với object thôi. và phải có thêm một object là agent nữa,và link tới các role. kiểu như thế

À, như vậy thì bức tranh đúng hơn rồi. **Role vẫn có instance dưới dạng object**, nhưng **cách diễn giải specialization/generalization của Role khác với Entity/Group**.

Nói ngắn gọn:

Entity, Group, Role đeˆˋu sinh ra object​

nhưng:

generalization của Entity/Group = cuˋng một object​

trong khi:

specialization của Role = caˊc role-object khaˊc nhau, cuˋng ga˘ˊn với một Agent​

Đây là khác biệt rất quan trọng.

---

## 1. Với Entity/Group: giữ nguyên semantics của Richters

Ví dụ:

Employee≺Person

nếu `Employee` và `Person` là Entity.

Ta có một object:

e1​:Employee

và do generalization:

e1​∈I(Employee)⊆I(Person).

Tức là **chỉ có một object e1​**.

Nó vừa là `Employee`, vừa là `Person`.

Đây chính là domain-inclusion semantics mà Richters dùng cho generalization:

c1​≺c2​⇒I(c1​)⊆I(c2​).

Hình dung:

```
          Person
```

            ▲

            |

         Employee

runtime:

       e1 : Employee

       (= cũng là Person)

---

# 2. Với Role thì khác

Giả sử:

EmployeeRole≺PersonRole.

Trong trường hợp này **không phải** chỉ có:

```
r1 : EmployeeRole
```

và coi ngay r1​ cũng là `PersonRole`.

Thay vào đó sẽ có một `Agent` riêng:

```
a1 : Agent
```

và các **role objects** riêng:

```
p1 : PersonRole
```

e1 : EmployeeRole

sau đó Agent liên kết với cả hai:

```
             a1 : Agent
```

              /      \\

           plays     plays

            /          \\

 p1 : PersonRole    e1 : EmployeeRole

Trong đó ở model level:

```
EmployeeRole specializes PersonRole
```

Nhưng ở instance level:

e1​=p1​.

Đây chính là điểm khác hoàn toàn với object inheritance thông thường.

---

# 3. Vậy Role vẫn là object classifier

Điều này sửa một điểm tôi nói ở câu trước.

Ta vẫn có:

oid(r)

cho:

r∈Role.

Ví dụ:

oid(PersonRole)={p1​,p2​,…}

và:

oid(EmployeeRole)={e1​,e2​,…}.

Do đó `Role` hoàn toàn có thể có attributes/properties giống object bình thường.

Ví dụ:

```
role Employee {
```

    salary : Real;

}

thì vẫn có thể formalize:

salary:Employee→Real.

Và trong state:

σAtt​(salary)(e1​)=5000.

Như vậy không cần chế ra một semantics property đặc biệt cho Role.

Điểm đặc biệt **chỉ nằm ở cách inheritance của Role được diễn giải** và việc role instance phải liên hệ với Agent.

---

# 4. Tôi nghĩ lúc này runtime model của bạn có ba khái niệm đúng như bạn nói

Richters có:

Object, Link​

Ngôn ngữ của bạn có:

Object, Agent, Link​

Trong đó `Agent` bản thân cũng có thể được coi là một loại object đặc biệt về mặt implementation, nhưng **trong semantic domain của ngôn ngữ bạn nên giữ nó thành một khái niệm riêng**, vì nó có vai trò riêng.

Ta có thể định nghĩa:

AgentId

là tập các agent identifiers.

Ví dụ:

AgentId={a1​,a2​,…}.

Nếu Agent chỉ có `name`, thì:

name:Agent→String.

---

# 5. Cần thêm một relationship `plays`

Tôi sẽ xem:

Play

là một relation đặc biệt:

Play⊆Agent×RoleObject.

Trong một state:

σPlay​⊆σAgent​×σRole​.

Ví dụ:

(a1​,p1​)∈σPlay​

nghĩa là agent a1​ đang đóng instance p1​ của role `Person`.

Và:

(a1​,e1​)∈σPlay​

nghĩa là cùng agent đó đang đóng instance e1​ của `Employee`.

Có thể viết:

plays(a1​,p1​)

và:

plays(a1​,e1​).

---

# 6. Đây mới là semantics đúng của Role specialization

Giả sử model có:

Employee≺R​Person.

Với inheritance thông thường ta **không được** viết:

I(Employee)⊆I(Person),

vì như vậy lại biến object `Employee` thành chính object `Person`.

Trong ngôn ngữ của bạn, điều cần nói là:

> nếu một agent đóng một `Employee` role object, thì agent đó phải đồng thời có một `Person` role object.

Formal:

∀a∈Agent,∀e∈σRole​(Employee):\
plays(a,e)⇒∃p∈σRole​(Person):plays(a,p).

Đây tôi nghĩ chính là công thức trung tâm.

Nó nói:

Employee≺R​Person​

không dẫn tới:

e∈Person,

mà dẫn tới:

agent playing Employee⇒same agent plays some Person​

---

# 7. So sánh hai loại inheritance cạnh nhau

Đây sẽ là bảng rất quan trọng trong luận văn của bạn:

|  | Entity/Group generalization | Role specialization |
| --- | --- | --- |
| Model | C1​≺C2​ | R1​≺R​R2​ |
| Runtime objects | **1 object** | **2 role objects** |
| Identity | Child object chính là parent instance | Child-role object khác parent-role object |
| Semantics | I(C1​)⊆I(C2​) | `plays(a,r1)` đòi hỏi tồn tại `plays(a,r2)` |
| Ví dụ | `e1:Employee` đồng thời là `Person` | `agent1` link tới `employeeRole1` và `personRole1` |

Đó là sự khác biệt cốt lõi.

---

# 8. Ví dụ cụ thể

Model:

```
role Person {
```

    name : String;

}

role Employee specializes Person {

    employeeId : Integer;

}

Agent:

```
a1 : Agent
```

name = "John"

Nếu John là Employee, state phải có:

```
p1 : Person
```

e1 : Employee

và:

```
plays(a1, p1)
```

plays(a1, e1)

Trong đó:

p1​=e1​.

Ta có:

p1​∈σClass​(Person)\
e1​∈σClass​(Employee)

nhưng **không suy ra**:

e1​∈σClass​(Person).

Thay vào đó suy ra:

∃p1​:p1​∈σClass​(Person)∧plays(a1​,p1​).

---

# 9. Điều này dẫn tới một cách rất đẹp để formalize `Role`

Tôi sẽ định nghĩa trước hàm:

player:RoleObject→Agent.

Nếu mỗi role object chỉ được một agent đóng.

Ví dụ:

player(e1​)=a1​\
player(p1​)=a1​.

Thay vì relation `plays`, ta dùng hàm này thì công thức specialization còn đẹp hơn.

Nếu:

Employee≺R​Person

thì:

∀e∈σClass​(Employee):∃p∈σClass​(Person):player(e)=player(p).

Đọc đúng từng chữ:

> Với mọi instance của specialized Role `Employee`, phải tồn tại một instance của general Role `Person` được đóng bởi **cùng một Agent**.

Tôi khá thích formulation này.

---

# 10. Nhưng có thể vẫn nên dùng `plays`

Nếu một role object luôn có đúng một agent thì:

player:RoleObject→Agent

rất sạch.

Nếu có khả năng:

- \
  một role object chưa được agent nào nhận,
- \
  nhiều agent cùng đóng một role object,

thì cần relation:

plays⊆Agent×RoleObject.

Theo mô tả hiện tại của bạn, tôi đoán **một role object thuộc một agent**, vậy dùng `player` có vẻ tự nhiên hơn. Nhưng chỗ này nên để definition ngôn ngữ quyết định, không nên tự giả định.

---

# 11. System state lúc này cũng rõ hơn rất nhiều

Richters có:

σ(M)=(σClass​,σAtt​,σAssoc​).

Bạn có thể mở rộng rất nhẹ:

σ(M)=(σClass​,σAgent​,σAtt​,σAssoc​,σPlay​)​

Trong đó:

### σClass​

chứa object instances của:

Entity∪Group∪Role.

### σAgent​

chứa các Agent hiện tồn tại.

### σAtt​

attribute/property values của object và có thể của Agent.

### σAssoc​

các ordinary association/composition/aggregation links.

### σPlay​

đặc biệt biểu diễn Agent–Role-instance links.

---

# 12. Ví dụ state

Giả sử:

Employee≺R​Person.

Ta có:

σAgent​={a1​}\
σClass​(Person)={p1​}\
σClass​(Employee)={e1​}.

Và:

σPlay​={(a1​,p1​),(a1​,e1​)}.

State này hợp lệ.

Nhưng:

σPlay​={(a1​,e1​)}

thì **không hợp lệ**, vì specialization requirement của `Employee` chưa được thỏa.

---

# 13. Đây có thể được viết thành một well-formedness condition rất gọn

Cho:

specializesR​⊆Role×Role.

Nếu:

(rs​,rg​)∈specializesR​

thì:

∀os​∈σClass​(rs​):∃og​∈σClass​(rg​):player(os​)=player(og​)​

Nếu dùng `plays`:

∀a,os​:plays(a,os​)∧os​∈σClass​(rs​)⇒∃og​:og​∈σClass​(rg​)∧plays(a,og​)​

Đây chính là analogue của:

I(child)⊆I(parent)

nhưng dành riêng cho Role.

---

# 14. Và nó giải quyết đúng câu “phải có thêm một object Agent”

Ví dụ:

```
Person <|-- Employee
```

với ordinary objects:

```
e1 : Employee
```

là đủ.

Không cần:

```
p1 : Person
```

riêng.

Nhưng với Role:

```
PersonRole <|-- EmployeeRole
```

runtime phải có:

```
a1 : Agent
```

p1 : PersonRole

e1 : EmployeeRole

cùng:

```
a1 --plays--> p1
```

a1 --plays--&gt; e1

Vậy role specialization được thể hiện bằng **một cấu trúc ba object**, thay vì một object được classified hai lần:

a1​,p1​,e1​​

---

# 15. Có một hệ quả rất hay: Role object có state riêng

Vì `PersonRole` và `EmployeeRole` là hai object khác nhau, chúng có thể có properties khác nhau.

Ví dụ:

```
role Person {
```

    displayName : String;

}

role Employee specializes Person {

    salary : Real;

}

Ta có:

σAtt​(displayName)(p1​)="John"

và:

σAtt​(salary)(e1​)=5000.

Agent a1​ chỉ là identity của intentional actor:

name(a1​)="John".

Như vậy bạn có ba mức thông tin rất rõ:

Agent properties​\
general Role properties​\
specialized Role properties​

Đây thực ra là một semantics khá mạnh.

---

# 16. Group và Entity vẫn theo Richters

Nếu:

```
group SubGroup specializes Organization
```

nếu cả hai là Group và bạn muốn generalization thông thường:

I(SubGroup)⊆I(Organization).

Một object:

```
g1 : SubGroup
```

đồng thời:

g1​:Organization.

Tương tự Entity.

Do đó ta thực sự có hai interpretation functions cho generalization:

≺EG​

và:

≺R​.

---

# 17. Metamodel của bạn vì thế hoàn toàn có thể giữ `Classifier`

Metamodel hiện tại:

```
                 Classifier
```

                     |

                    Class

                  /   |   \\

              Group Entity Role

vẫn ổn.

Bạn không cần sửa inheritance hierarchy ở metamodel chỉ vì runtime semantics khác.

Chỉ cần formal semantics ghi rõ:

### Nếu c∈Entity∪Group

generalization được diễn giải bằng domain inclusion.

### Nếu r∈Role

specialization được diễn giải bằng **co-enactment through a common Agent**.

Đây là một ví dụ rất điển hình của:

> cùng một abstract-syntax relation nhưng semantics phụ thuộc vào subtype của operands.

---

# 18. Tôi nghĩ notation tốt nhất sẽ là thế này

Tách:

ObjClass=Entity∪Group

và:

RoleClass=Role.

Generalization:

≺ ⊆Class×Class

nhưng có hai semantic clauses:

c1​,c2​∈ObjClass∧c1​≺c2​⇒I(c1​)⊆I(c2​).

Còn:

r1​,r2​∈RoleClass∧r1​≺r2​

thì:

∀o1​∈σClass​(r1​):∃o2​∈σClass​(r2​):player(o1​)=player(o2​).

Tôi thích cách này hơn việc tạo hai syntax relations khác nhau, bởi trong ngôn ngữ ACL của bạn cả hai đều dùng cùng từ khóa:

```
specializes
```

Chỉ có **semantic interpretation của** `specializes` **thay đổi theo classifier kind**.

---

# 19. Như vậy giờ ta có thể sửa toàn bộ mapping với Richters rất chính xác

Richters:

M=(Class,Att,Op,Assoc,…,≺).

ACL của bạn có thể bắt đầu:

MACL​=(Entity,Group,Role,AgentType,Prop,Rel,ends,multiplicity,specializes,compatible,member,…).

Và runtime:

σACL​(M)=(σClass​,σAgent​,σProp​,σRel​,σPlay​).

Rồi generalization semantics chia hai case:

Entity/Group:I(c1​)⊆I(c2​)​

và:

Role:specialized-role object→same Agent must own/play general-role object​

Đây mới là formalization phản ánh đúng điều bạn đang mô tả.

---

Và tôi nghĩ **đây là điểm khác biệt đáng giá nhất giữa ACL object model của bạn và object model trong Chương 3 của Richters**. Không phải chỉ là “thêm `Agent` vào tập classes”, mà là `Agent` làm cho **semantics của Role specialization trở thành một quan hệ giữa nhiều object instances**, thay vì phép bao hàm miền như inheritance thông thường.

Nếu viết tiếp thành Definitions theo đúng phong cách luận văn, tôi sẽ đặt phần này thành một định nghĩa riêng kiểu **“Interpretation of specialization”**, với hai case `Entity/Group` và `Role`, thay vì nhét nó vào định nghĩa object identifiers.