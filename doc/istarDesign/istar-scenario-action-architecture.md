# Kien truc action iStar Scenario: `.istar` va `.iscn`

Tai lieu nay tom tat cach action **iStar Scenario** duoc thiet ke trong plugin USE, dua tren cac file chinh:

- `goal/src/main/resources/useplugin.xml`
- `goal/src/main/resources/grammars/IStar.g4`
- `goal/src/main/resources/grammars/IStarScenario.g4`
- `goal/src/main/java/org/vnu/sme/goal/istarscenario/...`
- `goal/src/main/java/org/vnu/sme/goal/conformance/semantics/IStarPropagation.java`

## 1. Muc dich cua action iStar Scenario

Action `ActionOpenIStarScenario` mo mot file kich ban `.iscn`, bien dich no cung voi goal model `.istar` ma no tham chieu, sau do hien thi mo hinh iStar da duoc gan badge trang thai.

No khong phai la viewer `.istar` thong thuong. Viewer `.istar` chi doc va ve cau truc goal model. Action `.iscn` them mot lop **scenario instance-level**: co bao nhieu actor cu the, actor nao da thuc hien task/goal nao, trang thai nao duoc gan truc tiep, va cac dieu kien tong hop `aggregate` co thoa hay khong.

Trong plugin, action nay duoc dang ky tai `useplugin.xml`:

```xml
class="org.vnu.sme.goal.istarscenario.action.ActionOpenIStarScenario"
tooltip="Run an i* satisfaction scenario (.iscn) against its goal model"
```

Luong chay nguoi dung nhin thay:

1. Chon menu/action **iStar Scenario**.
2. `ActionOpenIStarScenario` mo dialog `IStarScenarioForm`.
3. Dialog chon file `.iscn`.
4. `IStarScenarioCompiler.compile(...)` parse `.iscn`, compile `.istar`, validate semantic, execute scenario.
5. Ket qua duoc in ra text area.
6. `IStarScenarioView.openAllInstances(...)` mo diagram scenario-level voi badge `Fulfilled`, `Pending`, `True`, `False`, `Unknown`.

## 2. Y tuong ngon ngu

Thiet ke cua cap ngon ngu nay di theo tuong tu:

```text
.istar  ~ UML class diagram / domain model / type-level schema
.iscn   ~ UML object diagram / SOIL-style script / instance-level snapshot
```

`.istar` dinh nghia **tu vung va cau truc co the co** trong mien:

- actor type nao ton tai: `role`, `agent`
- intentional element nao thuoc actor: `goal`, `task`, `resource`, `quality`, `obstacle`
- quan he SR noi bo actor: AND/OR refinement, contribution, qualifies, needed-by, obstructs, resolves
- quan he SD giua actor: `depend ... -> ... -> ...`

`.iscn` dinh nghia **mot cau hinh cu the cua cac instance** tren model do:

- co nhung actor instance nao, moi instance thuoc actor type nao
- element nao duoc `fire`
- element nao duoc `assign` trang thai truc tiep
- can kiem tra dieu kien tong hop nao bang `aggregate`

Quan trong: scenario o day khong nhat thiet la mot cau chuyen day du tu dau den cuoi. No gan voi khai niem **snapshot/state**. File `.iscn` mo ta mot trang thai dang quan tam, co the la trang thai cuoi, trang thai giua chung, hoac mot trang thai mot phan.

## 3. Khac biet giua `.istar` va `.iscn`

| Khia canh | `.istar` | `.iscn` |
|---|---|---|
| Muc truu tuong | Type-level/domain model | Instance-level/scenario state |
| Cau hoi tra loi | Mien nay co actor, goal, task, quality, dependency nao? | Voi cac instance cu the nay, trang thai hien tai la gi? |
| Co instance cu the khong? | Khong | Co: `instance xing, amr : Participant;` |
| Co gan trang thai khong? | Khong | Co: `fire`, `assign` |
| Co kiem tra all/any khong? | Khong | Co: `aggregate ... : all/any ... over ...;` |
| Ket qua chinh | `GoalModel` cau truc | `IStarMarking`/scenario graph co badge |
| Vi tri grammar | `IStar.g4` | `IStarScenario.g4` |
| Parser/compiler chinh | `IStarCompiler` | `IStarScenarioCompiler` |

Noi ngan gon: `.istar` la **ban thiet ke loai**, `.iscn` la **mot lan dat cac doi tuong cu the vao ban thiet ke do va xem trang thai suy ra**.

## 4. Cu phap `.istar`

Grammar `IStar.g4` dinh nghia root:

```antlr
model : 'istar' IDENT '{' actorDef* dependency* '}' EOF ;
```

Actor chi co hai loai cu the:

```antlr
actorDef : actorKind IDENT '{' actorBody* '}' ;
actorKind : 'role' | 'agent' ;
```

Ben trong actor co cac intentional element:

```antlr
goal     GoalId [: Achieve|Maintain|Avoid] rel*
task     TaskId                            rel*
resource ResourceId                        rel*
quality  QualityId                         rel*
obstacle ObstacleId [: Prevention|Restoration|Mitigation] rel*
```

Quan he giua intentional element duoc viet inline tai khai bao cua child, theo mau:

```text
task SubmitApplication > ApplicationSubmitted
task FastPath > make QuickScheduling
obstacle MissingDocument > obstructs ApplicationApproved
```

Trong thiet ke hien tai, AND refinement la quan he mac dinh khi viet `> Parent` khong co keyword. Cac quan he con lai phai co keyword ro rang:

- `> or Parent`
- `> forall ActorType Parent`
- `> pick ActorType Parent`
- `> make|help|hurt|break Quality`
- `> qualifies Element`
- `> needed-by Task`
- `> obstructs GoalTask`
- `> resolves Obstacle`

Dependency cross-actor co form rieng:

```text
depend Depender[.DependerElement] -> goal|task|resource|quality Dependum -> Dependee[.DependeeElement]
```

Day la phan Strategic Dependency cua iStar: mot actor phu thuoc actor khac de dat mot dependum.

## 5. Cu phap `.iscn`

Grammar `IStarScenario.g4` dinh nghia root:

```antlr
scenario : 'scenario' IDENT 'for' STRING '{' instanceDecl* stmt* '}' EOF ;
```

Vi du:

```text
scenario MeetingOrganization_WithFiveParticipants for "mtg.istar" {
    instance abdul   : Initiator;
    instance matilda : Organizer;
    instance alex    : Secretary;
    instance xing, amr, naya, bao, chloe : Participant;

    fire DecideMeetingDetails;
    fire xing.CollectFromCalendar;
    fire alex.CollectConstraintsByPhone for naya;

    assign bao.Participate = Fulfilled;

    aggregate EveryoneAttended : all of Participant over MeetingAttendedByParticipant;
    aggregate SomeFastCase     : any of Participant over QuickScheduling;
}
```

Cac statement chinh:

```antlr
fireStmt      : 'fire' qualifiedId ('for' IDENT)? ';' ;
assignStmt    : 'assign' qualifiedId '=' statusValue ';' ;
aggregateStmt : 'aggregate' IDENT ':' aggMode ('of' IDENT)? 'over' IDENT ';' ;
```

`qualifiedId` co the la:

- `ElementId`: khong gan voi instance rieng, neu co nhieu instance thi broadcast cho moi private trace.
- `instanceId.ElementId`: chi ap dung cho instance do.

`fire` co nghia la element Goal/Task thuc su duoc thuc hien. Compiler validate rang `fire` chi ap dung cho `Goal`/`Task`.

`assign` la gan trang thai truc tiep:

- Goal/Task: `Fulfilled` hoac `Pending`
- Quality: `True` hoac `False`

`aggregate` kiem tra trang thai sau khi scenario da duoc execute:

- `all over X`: tat ca instance trong universe thoa `X`
- `any over X`: it nhat mot instance thoa `X`
- `of ActorType`: gioi han universe vao cac instance thuoc actor type do

## 6. Kien truc pipeline

Pipeline cua action co the doc theo cac lop sau:

```text
USE plugin action
  -> ActionOpenIStarScenario
  -> IStarScenarioForm
  -> IStarScenarioCompiler
       -> ANTLR lexer/parser from IStarScenario.g4
       -> IStarScenarioBuildingVisitor
       -> IStarScenarioModelFactory
       -> IStarCompiler for referenced .istar
       -> semantic validation
       -> IStarPropagation / IStarScenarioEvaluator
  -> IStarScenarioView
  -> IStarView
```

### 6.1. Action layer

`ActionOpenIStarScenario` rat mong. No chi lay `Session`, `MainWindow`, roi mo Swing dialog:

```java
IStarScenarioForm form = new IStarScenarioForm(session, mainWindow);
form.setResizable(true);
form.setVisible(true);
```

Thiet ke nay giu action delegate khong biet chi tiet parse/evaluate.

### 6.2. GUI layer

`IStarScenarioForm` phu trach:

- chon file `.iscn`
- goi `IStarScenarioCompiler.compile(Path)`
- hien thi loi syntax/semantic neu co
- in report marking theo tung instance
- mo view bang `IStarScenarioView.openAllInstances(...)`

No cung luu duong dan file gan nhat bang `Preferences` voi key `istarscenario.lastFile`.

### 6.3. Parser va AST layer

`IStarScenarioBuildingVisitor` di tu ANTLR parse tree sang concrete-syntax AST:

- `IStarScenarioModelCS`
- `InstanceDeclCS`
- `QualifiedIdCS`
- `ScenarioStmtCS`

Layer nay giu dung nhung gi nguoi dung viet. Vi du `instance a, b : Type;` van la mot declaration gom nhieu ten.

### 6.4. MM/runtime model layer

`IStarScenarioModelFactory` chuyen AST sang runtime MM:

- `IStarScenarioModel`
- `ScenarioInstance`
- `ScenarioStmt.Fire`
- `ScenarioStmt.Assign`
- `ScenarioStmt.Aggregate`

Tai day, `instance a, b : Type;` duoc flatten thanh hai object rieng:

```text
ScenarioInstance("a", "Type")
ScenarioInstance("b", "Type")
```

MM khong doc file va khong biet `GoalModel`; no chi giu script scenario da parse.

### 6.5. Compiler/semantic layer

`IStarScenarioCompiler` la noi noi `.iscn` voi `.istar`:

1. Parse `.iscn`.
2. Build AST va MM.
3. Resolve path model: file `.istar` nam tuong doi theo folder cua `.iscn`.
4. Goi `IStarCompiler.compile(modelFile)`.
5. Validate:
   - actor type trong `instance` phai ton tai trong `.istar`
   - instance name khong trung
   - target cua `fire`/`assign` phai ton tai
   - `fire` chi cho Goal/Task
   - `assign` dung mien gia tri: Goal/Task dung `Fulfilled/Pending`, Quality dung `True/False`
   - `aggregate over X` phai la Goal/Task hoac Quality
   - `aggregate of ActorType` phai co instance actor type do
6. Execute scenario thanh marking.
7. Evaluate aggregate.
8. Goi `IStarScenarioEvaluator.evaluate(...)` de tao scenario-level instance graph cho view.

### 6.6. Propagation semantics

Trang thai duoc bieu dien bang `IStarMarking`, gom hai nhom status:

- `GoalTaskStatus`: `UNKNOWN`, `FULFILLED`, `PENDING`
- `QualityStatus`: `UNKNOWN`, `TRUE`, `FALSE`

`IStarPropagation` la bo luat suy dien trang thai:

- `fire(...)`: danh dau leaf Goal/Task la `FULFILLED`, roi saturate.
- `assignGoalTask(...)`: gan Goal/Task truc tiep, roi saturate.
- `assignQuality(...)`: gan Quality truc tiep, roi saturate.

Qua trinh saturate lap den fixpoint:

- AND refinement: parent fulfilled khi moi child fulfilled.
- OR refinement: parent fulfilled khi co it nhat mot child fulfilled.
- Contribution `make`/`break`: Goal/Task fulfilled co the lam Quality thanh `TRUE`/`FALSE`.
- Khi contribution doi nghich cung tac dong mot Quality, contributor doi nghich co the bi dua ve `PENDING` de tranh dao trang thai vo han.

Day la diem quan trong cua ngon ngu: nguoi viet `.iscn` khong can tu tinh toan tat ca trang thai suy ra. Ho chi can khai bao state input (`assign`) hoac event da xay ra (`fire`); propagation se suy ra phan con lai tu cau truc `.istar`.

## 7. Hai co che instance trong thiet ke hien tai

Trong code hien co hai cach nhin bo sung nhau.

### 7.1. Private trace theo tung instance

`IStarScenarioCompiler` tao mot `IStarMarking` rieng cho moi declared instance. Ly do: mot actor "dung chung" nhu `HR` hay `Organizer` co the xu ly tung `Candidate`/`Participant` rieng. Neu chi co mot marking chung cho `ScreenApplication`, se khong phan biet duoc "da screen candidate c1" voi "da screen candidate c2".

Quy tac:

- Khong khai bao `instance`: dung mot default instance, tuong thich nguoc voi scenario cu.
- Co khai bao `instance`: moi instance co private trace rieng.
- Statement khong qualified, vi du `fire DecideMeetingDetails;`, broadcast den moi trace.
- Statement qualified, vi du `fire xing.Participate;`, chi ap dung cho `xing`.

### 7.2. Scenario-level instance graph

`IStarScenarioEvaluator` tao mot `GoalModel` moi o muc instance. Model nay co cac actor instance va element occurrence da duoc qualify theo scope. Muc dich chinh la de view mot diagram scenario-level duy nhat, thay vi mo nhieu overlay tren type-level model.

Evaluator lam cac viec:

- suy ra scope actor type cho element occurrence
- instantiate actor theo declared instance
- copy element/refinement/contribution/dependency sang occurrence id phu hop
- execute trace tren instance model
- tao label override cho actor/node
- tra ve `IStarScenarioEvaluation`

Sau do `IStarScenarioViewModel` bien marking thanh `NodeBadge` de hien thi.

## 8. `fire` va `assign` khac nhau nhu the nao?

Hai lenh nay cung thiet lap trang thai, nhung y nghia khac nhau.

`fire` la **state as derived result**:

```text
fire xing.Participate;
```

Nghia la action/leaf do that su da duoc thuc hien. Trang thai cua cac parent goal, quality lien quan duoc suy ra bang propagation.

`assign` la **state as input**:

```text
assign bao.Participate = Fulfilled;
assign naya.QuickScheduling = False;
```

Nghia la scenario muon noi truc tiep rang tai snapshot nay element do co trang thai do. Cach nay huu ich khi:

- trang thai den tu dependency, khong phai do actor tu fire action cua chinh minh
- muon mo ta partial/mid-way state ma khong can replay lich su
- muon test mot cau hinh cu the

## 9. Vi du doc nhanh

File `goal/src/main/resources/examples/mtg/meeting_five_participants.iscn` minh hoa kha ro:

```text
instance xing, amr, naya, bao, chloe : Participant;

fire xing.CollectFromCalendar;
fire amr.CollectFromCalendar;

fire alex.CollectConstraintsByPhone for naya;
fire alex.CollectConstraintsByPhone for bao;
fire alex.CollectConstraintsByPhone for chloe;

aggregate EveryoneAttended : all of Participant over MeetingAttendedByParticipant;
aggregate UniversalQuickScheduling : all of Participant over QuickScheduling;
```

Y nghia:

- Co 5 participant cu the.
- `xing`, `amr` thu thap lich bang calendar.
- `naya`, `bao`, `chloe` duoc secretary `alex` goi dien, nen statement co dang `fire alex.Task for participant`.
- Sau do kiem tra all/any tren tap instance `Participant`.

## 10. Tom tat thiet ke

Thiet ke cua action iStar Scenario co chu dich tach ro cac lop:

- `.istar`: ngon ngu khai bao domain/goal model.
- `.iscn`: ngon ngu khai bao scenario instance-level.
- AST CS: giu cau truc gan voi cu phap nguoi dung viet.
- MM: runtime script doc lap voi parser va file I/O.
- Compiler: noi scenario voi model, validate semantic, execute marking.
- Propagation: su dung lai semantics iStar thay vi hard-code ket qua.
- Evaluator/view: dung instance graph de hien thi scenario truc quan.

Tu goc nhin ngon ngu, day la mot co che "model + scenario" tuong tu USE `.use + .soil`: `.istar` noi **loai gi co the ton tai**, con `.iscn` noi **nhung instance cu the nao dang o trang thai nao va cac assertion nao phai dung**.
