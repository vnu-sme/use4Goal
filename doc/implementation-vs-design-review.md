# Implementation vs Design Review

Ngay doc nay doi chieu implementation hien tai voi muc tieu duoc noi trong README, docs, paper va comment code. Ket luan chi dua tren tai lieu, code, va ket qua chay thuc te trong repo.

## Nguon Da Doc

- `README.md`: chi co tieu de `# use-goal# goal_model` va `# USE-BPMN`, khong du de xac dinh feature.
- `goal/docs/ARCHITECTURE.md`: mo ta baseline architecture cho pipeline ngon ngu; nhan manh "Parsing success alone is not enough" va can semantic checks.
- `goal/docs/BPMN2_OCL_OPTION2.md`: mo ta Option 2 cho OCL gan vao BPMN, co gioi han ro rang: parser chi lay raw OCL, compile bang USE `OCLCompiler`, chua tu chung minh goal achievement.
- `goal/docs/METAMODELS.md`: tong hop GOAL/OCL/BPMN metamodel, nhung co nhieu ten class cu.
- `goal/docs/PROGRESS.md`: tai lieu tien do MAGoalTax `.goal`, nhung khong khop source hien tai.
- `doc/paper/conformance-istar-bpmn2.md`: thiet ke conformance i*/BPMN2, mapping, structural checker, LTS, product LTS, compliance checker.
- Comment trong code o cac package `istar`, `bpmn2`, `bpmn2.ocl`, `istarusebridge`, `conformance`.

## Bang Doi Chieu Feature

| Feature | Thiet ke yeu cau | Implementation hien tai | Hoan thanh? | Bang chung |
|---|---|---|---|---|
| USE plugin entry point | Plugin USE co actions de mo cac view/analyzer. | `Main` la `IPlugin`, actions dang ky qua `useplugin.xml`. | Co | `goal/src/main/java/org/vnu/sme/goal/Main.java:6-20`; `goal/src/main/resources/useplugin.xml` dang ky iStar, scenario, BPMN, DCR, conformance. |
| MAGoalTax `.goal` pipeline | `PROGRESS.md` noi `.goal -> ANTLR parser -> AST -> MM -> GoalDiagramView` va cac muc `GOAL.g4`, `GOALCompiler`, `GoalDiagramView` la DONE. | Source hien tai khong co `GOAL.g4`, `GOALCompiler`, `GoalDiagramView`; grammar thuc te la `IStar.g4`, `Bpmn2.g4`, `DCR.g4`, scenario grammars. | Khong khop tai lieu cu | `goal/docs/PROGRESS.md:10`, `goal/docs/PROGRESS.md:17`, `goal/docs/PROGRESS.md:251-259`; `rg` khong tim thay `GOAL.g4`/`GOALCompiler`; `goal/src/main/resources/grammars/IStar.g4:1`. Day co ve la tai lieu cu/superseded, khong nen coi la bug chuc nang hien tai neu project da chuyen sang i*. |
| iStar parser + AST + MM | Architecture yeu cau pipeline parser -> AST -> MM -> semantic. | `IStarCompiler` parse ANTLR4, build AST bang `IStarBuildingVisitor`, build MM bang `IStarModelFactory`, chay `GoalModelValidator`. | Co | `goal/src/main/java/org/vnu/sme/goal/istar/parser/IStarCompiler.java:20-21`, `:52-64`; `IStarModelFactory.java` comment "AST -> runtime MetaModel"; `GoalModelValidator.java:13-23`. |
| Semantic validation cho iStar | Parser chi dam bao syntax; validator dam bao well-formed i* model. | Co validator cho unique id, refinement, dependency, contribution, association, cycles. | Co | `GoalModelValidator.java:23` va cac method `validateElementIdUniqueness`, `validateRefinements`, `validateDependencies`. |
| BPMN parser + AST + MM | `Bpmn2.g4` mo ta BPMN 2.0 Process + Collaboration; architecture yeu cau BPMN pipeline. | `Bpmn2Compiler` parse `model ...`, build `Bpmn2ModelCS`, roi `Bpmn2ModelFactory.build`. | Co ve syntax/MM | `goal/src/main/resources/grammars/Bpmn2.g4:30`; `Bpmn2Compiler.java:16-20`, `:46-49`; `Bpmn2ModelFactory.java:19`, `:50-72`. |
| BPMN semantic validation chung | Architecture noi parse thanh cong la chua du, can semantic checks. | `Bpmn2Compiler` khong goi semantic validator; bad sequence target bi `Bpmn2ModelFactory` throw `IllegalStateException` thay vi result errors; thieu start/end/duplicate id da duoc test va van accepted trong BPMN-OCL path. | Mot phan | `ARCHITECTURE.md:58`, `:64`; `Bpmn2Compiler.java:46-49`; `Bpmn2ModelFactory.java:103-108`; ket qua e2e truoc: missing start/end accepted, bad sequence target uncaught exception, duplicate id accepted. Day la thieu so voi baseline architecture, khong phai thieu cua Option 2 rieng. |
| BPMN OCL Option 2 - raw OCL | Option 2 yeu cau parser chi capture raw text trong `ocl {[ ... ]}`, khong parse OCL bang ANTLR rieng. | `Bpmn2BuildingVisitor` lay raw body; `FlowElement`/`SequenceFlow` giu `oclSource`. | Co | `BPMN2_OCL_OPTION2.md:47-48`; `Bpmn2BuildingVisitor.java:158-165`; `Bpmn2OclConstraintCompiler.java:43-48`. |
| BPMN OCL Option 2 - compile bang USE | Compile BPMN + `.use` + context map, delegate cho USE `OCLCompiler`. | `Bpmn2OclValidationCompiler` compile BPMN, compile USE, parse map, roi `Bpmn2OclConstraintCompiler`; `self` duoc bind bang `Symtable`. | Co | `BPMN2_OCL_OPTION2.md:68`; `Bpmn2OclValidationCompiler.java:36-68`; `Bpmn2OclConstraintCompiler.java:86-121`. E2E da chay: valid sample compile 8/9 constraints; OCL syntax/missing property/missing context bi reject. |
| BPMN OCL evaluate/prove goal achievement | Tai lieu Option 2 noi ro chua prove goal achievement, chi cung cap BPMN-side OCL data. | Implementation compile constraints, khong evaluate runtime BPMN OCL. | Dung voi scope | `BPMN2_OCL_OPTION2.md:222`; `Bpmn2OclConstraintCompiler.ConstraintInfo` chi giu `Expression`; demo chi print compiled constraints. Khong phai bug. |
| Coverage giua i* OCL va BPMN OCL | Step 7 chi check mapped i*/goal elements co OCL thi BPMN node cung co OCL; khong prove achievement. | `Bpmn2GoalOclCoverageValidator` tra warning khi mapped BPMN node thieu OCL. | Co, muc nhe | `Bpmn2GoalOclCoverageValidator.java:13-18`, `:23-43`. |
| iStar + USE trace | Comment code yeu cau compile `.istar + .use + .soil`, chay concrete execution, evaluate OCL guards theo checkpoint. | `IStarUseTraceCompiler` compile iStar, USE, OCL; execute SOIL; evaluate boolean OCL tren `MSystemState`; update `IStarMarking`. | Co | `IStarUseTraceCompiler.java:35-58`, `:74-126`, `:141-185`, `:231-236`; `IStarOclConstraintCompiler.java:23-25`, `:38-72`. |
| BPMN scenario `.bscn` | Plugin co BPMN Scenario Viewer; compiler comment noi end-to-end cho `.bscn`. | Co parse scenario, load BPMN, validate references/tokens, evaluate only count assertions; non-count assertions "stored, not evaluated by MVP compiler". | Mot phan theo MVP | `Bpmn2ScenarioCompiler.java:32`, `:49-97`, `:225`; day la gioi han duoc code comment/return text noi ro. |
| Conformance mapping parser | Paper yeu cau explicit mapping `actor ... -> pool/lane`, `map ... -> node ...`; validate unknown refs, non-leaf mapping, function property. | Parser co format do; validate element mapping unknown i*, unknown BPMN, non-leaf, duplicate BPMN node. Actor mappings duoc parse/lưu nhung chua thay validate actor/pool/lane. | Mot phan | `doc/paper/conformance-istar-bpmn2.md:94-98`, `:225`, `:271`; `ConformanceMappingParser.java:22-41`; `ConformanceMapping.java:68-88`. Thieu actor mapping validation la chua implement trong `validate()`, khong du bang chung de noi ngoai muc tieu. |
| Structural conformance checker/exclusivity | Paper thiet ke `ExclusivityAnalyzer` va `StructuralConformanceChecker` de check AND/IOR/XOR consistency. | Khong co package/class `structural`; `rg` chi thay ten nay trong docs/paper. | Chua implement | `doc/paper/conformance-istar-bpmn2.md:229-230`, `:298-304`, `:330`; ket qua `rg` khong tim thay trong `goal/src/main/java`. Day la feature thiet ke nhung chua co code. |
| BPMN LTS | Paper yeu cau token semantics/WF-net, validate well-formed truoc LTS. | `Bpmn2LtsBuilder` co `validateWellFormed`, `enabledFirings`, `fire`. | Co trong MVP | `doc/paper/conformance-istar-bpmn2.md:147-156`, `:376-378`; `Bpmn2LtsBuilder.java:34-43`, `:74-143`. |
| Gioi han BPMN LTS | Paper noi OR join MVP don gian, message flow ngoai MVP; code comment cung noi. | Code xu ly OR nhu XOR, SubProcess opaque, message flow out of scope. | Dung voi MVP, khong phai bug | `doc/paper/conformance-istar-bpmn2.md:150-152`, `:586-588`; `Bpmn2LtsBuilder.java:22-30`, `:114`, `:132`. |
| Product LTS i*/BPMN | Paper yeu cau synchronous product: BPMN firing cap nhat i* marking neu node mapped. | `ProductLts.successors` lay enabled BPMN firing, fire BPMN, lookup mapping, goi `IStarPropagation.fire`. | Co | `doc/paper/conformance-istar-bpmn2.md:242`, `:386-387`; `ProductLts.java:13-17`, `:35-50`. |
| Compliance checker weak/strong/stability | Paper yeu cau algorithms weak/stability/strong, output verdict + trace. | `ComplianceChecker.check` BFS reachable, backward reachability success, stability theo qualities, verdict `NON/WEAK/STRONG`, counterexample trace. | Co | `doc/paper/conformance-istar-bpmn2.md:398-403`, `:532`; `ComplianceChecker.java:17-23`, `:27-82`. |
| Conformance GUI | Plugin can form chon `.istar`, `.bpmn2`, `.map`, hien 2 diagrams, report verdict. | `ConformanceForm` co 3 file fields, load compilers, show views, run checker; action dang ky trong plugin. | Co | `ConformanceForm.java:48-57`, `:191-248`; `ActionCheckConformance.java:11-21`; `useplugin.xml` conformance action. |
| Conformance official demo | Comment noi runnable demo dung `construction_permit.{istar,bpmn2,map}`. Paper cung ky vong case nay chay `ComplianceChecker`. | Chay thuc te fail o BPMN parse vi file sample dung `collaboration`, grammar hien tai yeu cau `model`. | Khong hoan thanh / bug tai lieu-sample-run | `ConformanceDemoMain.java:22-32`, `:46-49`; `Bpmn2.g4:30`; `construction_permit.bpmn2` dong dau model la `collaboration ConstructionPermitApproval`; ket qua chay: `line 6:0 missing 'model' at 'collaboration'`. Day la bug da chung minh bang runtime. |
| Sample corpus `.bpmn2` | Sample nen phu hop grammar hien tai neu duoc dung de demo/view. | Nhieu sample cu dung `collaboration`; `job_application_review.bpmn2` comment noi no da duoc viet theo current `model` grammar. | Mot phan | `job_application_review.bpmn2` comment current grammar; `construction_permit.bpmn2`, `shop_order_fulfillment.bpmn2`, `generic_example.bpmn2`, `security_compliance.bpmn2` dung `collaboration`. |
| EMF/Ecore metamodel | User co nhac EMF/Ecore, nhung docs/code hien tai mo ta Java runtime MM packages hon la `.ecore`. | Khong co `.ecore` file; MM la Java records/classes trong `org.vnu.sme.goal.*.mm`. | Khong phai muc tieu hien tai theo source/doc da doc | `rg --files | rg "\\.ecore$|Ecore|EMF"` khong co ket qua; `IStarModelFactory`/`Bpmn2ModelFactory` build Java MM. |

## Ket Qua Chay Thuc Te Da Dung Lam Bang Chung

### BPMN OCL Option 2

Da chay duong dan compile `.bpmn2 + .use + .bpmn2oclmap` bang `Bpmn2OclValidationDemoMain`.

- Sample official `claim_handling_ocl`: OK, compile 9 constraints.
- Sample audit valid `order_process_valid`: OK, compile 8 constraints.
- OCL sai syntax/missing property/missing context/unknown class: reject co error.
- BPMN thieu start/end/duplicate id/empty process/task-only: van accepted trong path Option 2.
- Sequence flow sai target: uncaught `IllegalStateException: Unknown flow target`.
- Constraint mau thuan nhau: van accepted neu compile duoc, vi Option 2 chi compile, chua evaluate/prove.

Ket qua nay khop voi scope Option 2, nhung khong khop neu xem no la BPMN semantic validator day du.

### Conformance Demo

Command da chay tu repo root:

```powershell
java -cp <goal/use/antlr classpath> org.vnu.sme.goal.conformance.ConformanceDemoMain
```

Ket qua thuc te:

```text
line 6:0 missing 'model' at 'collaboration'
line 6:14 extraneous input 'ConstructionPermitApproval' expecting '{'
```

Sau khi doc file/grammar:

- `ConformanceDemoMain` hardcode sample `goal/src/main/resources/examples/construction_permit/construction_permit.{istar,bpmn2,map}`.
- `construction_permit.bpmn2` dung keyword `collaboration`.
- `Bpmn2.g4` hien tai yeu cau `model`.

Day la bug ro rang o sample/demo/documentation compatibility. Logic conformance core co nhieu thanh phan da implement, nhung demo chinh thuc hien tai khong chay duoc voi file duoc comment chi dinh.

## Phan Loai Thieu Sot

| Thieu sot | Phan loai | Ly do |
|---|---|---|
| `GOAL.g4`, `GOALCompiler`, `GoalDiagramView` khong co | Tai lieu cu/superseded, chua du can cu coi la bug runtime hien tai | Plugin hien tai expose iStar 2.0 thay vi `.goal`; README qua thieu, `PROGRESS.md` lech source. |
| `StructuralConformanceChecker`, `ExclusivityAnalyzer` khong co | Chua implement feature thiet ke | Paper liet ke class/package cu the, `rg` khong tim thay trong source. |
| Actor mapping khong validate pool/lane | Chua implement trong mapping validator | Parser luu actor mappings, nhung `ConformanceMapping.validate()` chi lap qua `elements`. |
| BPMN compiler khong validate semantic chung | Thieu so voi baseline architecture; Option 2 khong yeu cau day du | Architecture noi parse success chua du; test cho thay missing start/end/duplicate id accepted. |
| BPMN OR join/SubProcess/message flow day du | Khong phai bug trong MVP | Paper va code comment noi ro OR simplified, SubProcess opaque, MessageFlow out of scope. |
| BPMN OCL khong evaluate/prove achievement | Khong phai bug cua Option 2 | `BPMN2_OCL_OPTION2.md` noi ro chua prove goal achievement. |
| Conformance demo official fail do `collaboration` vs `model` | Bug da chung minh | Comment noi runnable demo voi `construction_permit`; runtime fail va grammar/file mau thuan truc tiep. |

## Ket Luan

Implementation hien tai khong dong nhat voi toan bo thiet ke trong repo.

- Neu xet `BPMN2_OCL_OPTION2.md`, implementation dung voi scope chinh: doc BPMN, doc USE, doc context map, compile OCL bang USE; khong evaluate/prove la co chu dich.
- Neu xet architecture/paper conformance, implementation moi hoan thanh mot phan: da co iStar semantic validation, mapping parser nhe, BPMN LTS MVP, product LTS, compliance checker, GUI; nhung structural checker/exclusivity chua co, actor mapping validate chua day du, BPMN semantic validation chung con mong.
- Neu xet kha nang chay demo conformance chinh thuc, hien tai khong dung: sample duoc code comment chi dinh khong parse duoc voi grammar hien tai.

Muc do tin cay: Medium-High. Bang chung gom tai lieu, code, `rg` negative evidence, va ket qua chay thuc te. Diem chua High tuyet doi vi chua chay duoc Maven build chuan trong environment hien tai do loi parent POM/quyen local `.m2` da gap truoc do.
