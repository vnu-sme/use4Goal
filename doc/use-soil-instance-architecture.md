# Kien truc USE: tu class diagram `.use` den instance scenario `.soil`

Tai lieu nay giai thich co che cua USE core khi:

- File `.use` dinh nghia class diagram/metamodel: class, attribute, association, composition, invariant.
- File `.soil` dinh nghia kich ban trang thai: tao object, gan attribute value, tao link giua object.
- USE thuc thi script de tao ra cac instance trong runtime system state.

Vi du chinh trong repo:

- Model: `goal/src/main/resources/examples/Families2Persons/Families.use`
- Scenario/object script: `goal/src/main/resources/examples/Families2Persons/input01.soil`
- USE core parser/runtime: `use/use-core/src/main/java/org/tzi/use/...`

## 1. Vai tro cua `.use` va `.soil`

`.use` la phan tinh. No mo ta loai doi tuong nao duoc phep ton tai.

Vi du trong `Families.use`:

```use
class Family
attributes
    name: String
end

class FamilyMember
attributes
    name: String
end

composition Father between
    Family [0..1] role familyFather
    FamilyMember [0..1] role father
end
```

Y nghia:

- `Family` la mot class.
- `Family.name` co type `String`.
- `FamilyMember` la mot class.
- `FamilyMember.name` co type `String`.
- `Father` la association/composition noi mot `Family` voi mot `FamilyMember`.

`.soil` la phan dong. No khong tao class moi, ma tao object/link/value dua tren class diagram da load.

Vi du trong `input01.soil`:

```soil
!new FamilyRegister('fr')
!new Family('fm')
!insert (fr,fm) into FamilyRegistration
!new FamilyMember('father')
!insert (fm,father) into Father
!set father.name := 'Hanh'
```

Y nghia runtime:

- Tao object `fr` la instance cua `FamilyRegister`.
- Tao object `fm` la instance cua `Family`.
- Tao link `FamilyRegistration(fr, fm)`.
- Tao object `father` la instance cua `FamilyMember`.
- Tao link `Father(fm, father)`.
- Gan slot attribute `father.name` bang string value `'Hanh'`.

Trong USE shell, dau `!` la lenh thuc thi. Shell cat dau `!` roi bien phan con lai thanh SOIL statement. Vi du:

- `!new Family('fm')` -> SOIL statement `new Family('fm')`
- `!set father.name := 'Hanh'` -> legacy shell command map thanh SOIL attribute assignment `father.name := 'Hanh'`

## 2. Pipeline tong quat

```text
Families.use
  -> USECompiler / USEParser
  -> ASTModel
  -> MModel
       - MClass
       - MAttribute
       - MAssociation

input01.soil / shell lines starting with !
  -> Shell.cmdExec(...)
  -> ShellCommandCompiler or SoilCompiler
  -> ASTStatement
  -> MStatement
  -> MSystem.execute(...)
  -> MSystemState changes
       - MObject
       - MObjectState attribute slots
       - MLink
```

Trong do:

- `MModel` la schema/metamodel runtime cua class diagram.
- `MSystem` la runtime system dang giu mot `MModel` va current `MSystemState`.
- `MSystemState` la snapshot/dang trang thai hien tai: objects, object states, links.
- `MStatement` la lenh da compile va co the execute.

## 3. `.use` duoc bien thanh class diagram runtime nhu the nao

USE core dung grammar fragments trong:

- `use/use-core/src/main/resources/grammars/use/USE.gpart`
- `use/use-core/src/main/resources/grammars/base/USEBase.gpart`
- Cac fragment OCL/SOIL dung chung khi `.use` co invariant, pre/post, operation body.

Sau parse, `ASTModel.gen(Context)` tao ra `MModel` theo nhieu pass. Cac pass quan trong trong `ASTModel`:

1. Tao model rong: `ModelFactory.createModel(...)`.
2. Tao empty class truoc: `ASTClass.genEmptyClass(...)` -> `ModelFactory.createClass(...)`.
3. Dua class vao type table/model de cac phan sau tra cuu duoc ten class.
4. Gan attributes/generalization: `ASTClass.genAttributesOperationSignaturesAndGenSpec(...)`.
5. Tao association/composition: `ASTAssociation.gen(...)`.
6. Tao operation body, derived attributes, constraints sau khi tat ca class/association da biet.

Ly do phai nhieu pass: association va expression can tham chieu den class/role/attribute. USE phai tao danh muc class truoc, roi moi gan chi tiet sau.

Class va attribute duoc tao nhu sau:

- `ASTClass.genEmptyClass(...)` tao `MClass`.
- `ASTAttribute.gen(...)` tao `MAttribute` voi type da resolve.
- `MClass.addAttribute(...)` gan attribute vao class.

Association duoc tao nhu sau:

- `ASTAssociation.gen(...)` tao `MAssociation`.
- Moi association end resolve class dich, multiplicity, role name.
- `composition` duoc map vao aggregation kind `COMPOSITION`.

Sau khi compile `.use`, USE biet rang:

- Class `FamilyMember` co attribute `name: String`.
- Association `Father` noi `Family` va `FamilyMember`.
- Object tao tu `FamilyMember` phai co slot cho `name`.
- Gia tri gan vao `name` phai conform voi type `String`.

## 4. `.soil` duoc parse va compile thanh statement nhu the nao

SOIL grammar nam trong:

- `use/use-core/src/main/resources/grammars/soil/Soil.gpart`
- `use/use-core/src/main/resources/grammars/base/SoilBase.gpart`
- Shell legacy mapping: `use/use-core/src/main/resources/grammars/base/ShellCommandBase.gpart`

Mot so rule quan trong trong `SoilBase.gpart`:

- `objCreateStat`: parse `new ClassName('objectName')`.
- `attAssignStat`: parse `object.attribute := value`.
- `lnkInsStat`: parse `insert (obj1,obj2) into Association`.
- `lnkDelStat`: parse `delete (...) from Association`.
- `condExStat`, `iterStat`, `whileStat`, `blockStat`: dieu kien, lap, block.

`SoilCompiler.compileStatement(...)` lam 2 buoc:

1. `constructAST(...)`: ANTLR lexer/parser tao `ASTStatement`.
2. `constructStatement(...)`: `ASTStatement.generateStatement(...)` tao `MStatement`.

Shell co them lop mapping cu phap legacy:

- `!create IBM : Company` map thanh `ASTNewObjectStatement`.
- `!set IBM.name := '...'` map thanh `ASTAttributeAssignmentStatement`.
- `!insert (IBM,MediAid) into CarriesOut` di truc tiep vao link insertion.

Trong GUI/shell, `Shell.cmdExec(...)`:

1. Nhan line sau dau `!`.
2. Goi `ShellCommandCompiler.compileShellCommand(...)`.
3. Nhan `MStatement`.
4. Goi `system.execute(statement)`.

## 5. Tao object: vi sao object la instance cua class

Voi lenh:

```soil
!new FamilyMember('father')
```

Luon chay nhu sau:

```text
Shell.cmdExec("new FamilyMember('father')")
  -> ShellCommandCompiler
  -> ASTNewObjectStatement
  -> ASTNewObjectStatement.generateStatement()
       - resolve type FamilyMember trong MModel
       - kiem tra day la MClass, khong phai datatype
       - kiem tra khong tao association class sai cach
       - tao MNewObjectStatement(MClass FamilyMember, objectName expression)
  -> MSystem.execute(MNewObjectStatement)
  -> MNewObjectStatement.execute(...)
  -> MSystem.createObject(...)
  -> MSystemState.createObject(...)
```

Tai `MSystemState.createObject(MClass cls, String name)`:

- Kiem tra object name hop le.
- Kiem tra class khong abstract.
- Goi `MSystem.createObject(cls, name)` de tao `MObject`.
- Tao `MObjectState(obj)`.
- Dua object vao cac index:
  - `fObjectStates`
  - `fClassObjects`
  - `fObjectNames`
- Khoi tao attribute init expression neu co.

`MObjectState` la cho bao dam object co day du attribute slot. Constructor cua `MObjectState` lay:

```java
List<MAttribute> atts = obj.cls().allAttributes();
```

Sau do moi attribute duoc tao slot va gan mac dinh:

```java
fAttrSlots.put(attr, UndefinedValue.instance);
```

Vi vay sau `!new FamilyMember('father')`, object `father` da co slot `name`, nhung value ban dau la `Undefined` neu khong co init expression.

## 6. Gan attribute value: vi sao String phai la gia tri String cu the

Voi lenh:

```soil
!set father.name := 'Hanh'
```

Shell legacy parser map `set father.name := 'Hanh'` thanh attribute assignment. Luong chay:

```text
ASTAttributeAssignmentStatement.generateStatement()
  -> generateObjectExpression(father)
  -> getAttributeSafe(object, "name")
  -> generate rValue cho 'Hanh'
  -> kiem tra rValue.type conformsTo attribute.type
  -> MAttributeAssignmentStatement

MAttributeAssignmentStatement.execute(...)
  -> evaluate object expression -> MObject father
  -> evaluate rValue -> StringValue("Hanh")
  -> MSystem.assignAttribute(...)
  -> MObjectState.setAttributeValue(attribute, value)
```

Co hai lop kiem tra type:

1. Compile-time cua SOIL AST:
   - `ASTAttributeAssignmentStatement.generateStatement(...)` kiem tra `rValue.getType().conformsTo(attribute.type())`.
2. Runtime state:
   - `MObjectState.setAttributeValue(...)` kiem tra `newVal.type().conformsTo(attr.type())`.

Neu `name: String` ma gan so, object, enum sai type, USE se bao loi. Neu gan `'Hanh'`, literal nay la OCL string expression nen hop le voi `String`.

## 7. Tao link: vi sao association trong `.use` dieu khien link trong `.soil`

Voi lenh:

```soil
!insert (fm,father) into Father
```

Luong chay:

```text
ASTLinkInsertionStatement.generateStatement()
  -> getAssociationSafe("Father")
  -> generateAssociationParticipants(association, participants)
       - resolve fm thanh object
       - resolve father thanh object
       - kiem tra object co type phu hop voi association ends
  -> MLinkInsertionStatement

MLinkInsertionStatement.execute(...)
  -> evaluate participants thanh List<MObject>
  -> MSystem.createLink(...)
  -> MSystemState.createLink(...)
```

Association `Father` trong `.use` noi:

- end 1: `Family`
- end 2: `FamilyMember`

Vi vay `(fm, father)` hop le neu:

- `fm` la instance cua `Family` hoac subclass phu hop.
- `father` la instance cua `FamilyMember` hoac subclass phu hop.
- Multiplicity/composition constraints khong bi vi pham.

Link duoc luu rieng voi object attribute. Attribute la slot nam trong `MObjectState`; association link nam trong link set cua `MSystemState`.

## 8. `.soil` tao "kich ban instance" nhu the nao

Mot file `.soil` thuc chat la chuoi cac lenh state-changing. Khi shell `open input01.soil`, USE doc tung dong va thuc thi cac lenh bat dau bang `!`.

Voi `input01.soil`, sau khi chay het file, state co the hieu nhu sau:

```text
Objects:
  fr     : FamilyRegister
  fm     : Family
  father : FamilyMember

Attribute slots:
  fm.name       = Undefined
  father.name   = 'Hanh'

Links:
  FamilyRegistration(fr, fm)
  Father(fm, father)
```

Day la "kich ban cac instance" vi file `.soil` khong chi dinh class diagram moi; no chi dinh trang thai cu the cua mot model da load. Moi object duoc tao ra deu bi rang buoc boi `MModel`:

- Object class phai ton tai.
- Attribute phai ton tai tren class cua object.
- Gia tri attribute phai dung type.
- Link phai dung association va dung type tai cac association end.
- Multiplicity/invariant co the duoc check boi USE sau khi state thay doi.

## 9. Cac lop chinh can nho

Phan class diagram/metamodel:

- `org.tzi.use.parser.use.USECompiler`
- `org.tzi.use.parser.use.ASTModel`
- `org.tzi.use.parser.use.ASTClass`
- `org.tzi.use.parser.use.ASTAttribute`
- `org.tzi.use.parser.use.ASTAssociation`
- `org.tzi.use.uml.mm.MModel`
- `org.tzi.use.uml.mm.MClass`
- `org.tzi.use.uml.mm.MAttribute`
- `org.tzi.use.uml.mm.MAssociation`

Phan SOIL/shell:

- `org.tzi.use.main.shell.Shell`
- `org.tzi.use.parser.shell.ShellCommandCompiler`
- `org.tzi.use.parser.soil.SoilCompiler`
- `org.tzi.use.parser.soil.ast.ASTNewObjectStatement`
- `org.tzi.use.parser.soil.ast.ASTAttributeAssignmentStatement`
- `org.tzi.use.parser.soil.ast.ASTLinkInsertionStatement`

Phan runtime system:

- `org.tzi.use.uml.sys.MSystem`
- `org.tzi.use.uml.sys.MSystemState`
- `org.tzi.use.uml.sys.MObject`
- `org.tzi.use.uml.sys.MObjectState`
- `org.tzi.use.uml.sys.MLink`
- `org.tzi.use.uml.sys.soil.MNewObjectStatement`
- `org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement`
- `org.tzi.use.uml.sys.soil.MLinkInsertionStatement`

## 10. Ket luan ngan

`.use` tao ra ban thiet ke: class nao, attribute nao, association nao, type nao.

`.soil` tao ra trang thai cu the: object nao, link nao, value nao.

USE noi hai phan nay bang `MModel` va `MSystemState`. Khi `.soil` duoc compile, moi statement deu tra cuu lai `MModel` de dam bao no dung voi class diagram. Khi statement duoc execute, `MSystemState` tao `MObject`, tao `MObjectState` voi day du attribute slots, gan value co type dung, va tao `MLink` hop le theo association. Vi vay tu file `.soil`, USE co the sinh ra mot kich ban instance nhat quan voi class diagram `.use`.
