# BPMN2 + OCL End-to-End Audit

Date: 2026-07-26

## 1. Scope

This audit verifies the current BPMN2 + OCL implementation by running real
inputs through the code. Conclusions below are based on source code and command
outputs, not assumptions.

The audited pipeline is the implemented "BPMN2 OCL Option 2" path:

```text
.bpmn2 file
  -> Bpmn2Compiler
  -> ANTLR4 Bpmn2Lexer/Bpmn2Parser
  -> Bpmn2BuildingVisitor
  -> bpmn2.ast records
  -> Bpmn2ModelFactory
  -> bpmn2.mm runtime model
  -> USECompiler for .use domain model
  -> Bpmn2OclContextMapParser for .bpmn2oclmap
  -> Bpmn2OclConstraintCompiler
  -> USE OCLCompiler.compileExpression
  -> CLI text output
```

Important: this implementation compiles BPMN-attached OCL expressions against a
USE domain model. It does not evaluate those expressions against a runtime
system state, and it does not check structural BPMN invariants such as "process
has a start event".

## 2. Project Purpose

The `goal` module is a USE plugin for goal/process modeling and validation.
According to `goal/docs/ARCHITECTURE.md`, the intended shape is:

```text
source file -> parser -> AST -> metamodel -> semantic processing -> view / analysis
```

For the audited BPMN2 OCL path, the implemented purpose is narrower:

```text
Can BPMN execution points carry domain OCL conditions that compile against a USE model?
```

The answer, based on executed tests, is: yes for compilation, no for evaluation
or proof of goal achievement.

## 3. Inputs And Outputs

Inputs:

- `.bpmn2`: simplified BPMN model with optional `ocl {[ ... ]}` blocks.
- `.use`: USE domain model.
- `.bpmn2oclmap`: maps BPMN node ids or `source::target` sequence-flow ids to USE classes.

Output:

- CLI prints `BPMN OCL validation OK` and compiled constraint ids on success.
- CLI prints collected errors and exits with status `1` for many OCL/context errors.
- Some BPMN model-building errors throw uncaught exceptions instead of returning `Result.errors`.

## 4. Responsible Classes

| Responsibility | Class / method | Evidence |
|---|---|---|
| CLI entry point | `Bpmn2OclValidationDemoMain.main` | `goal/src/main/java/org/vnu/sme/goal/bpmn2/ocl/Bpmn2OclValidationDemoMain.java:15-32` |
| Orchestration | `Bpmn2OclValidationCompiler.compile` | `.../Bpmn2OclValidationCompiler.java:36-68` |
| Read BPMN | `Bpmn2Compiler.compile(Path)` | `.../Bpmn2Compiler.java:16-18` |
| Parse BPMN | `Bpmn2Lexer`, `Bpmn2Parser`, `parser.model()` | `.../Bpmn2Compiler.java:27-43` |
| BPMN grammar | `Bpmn2.g4` | `goal/src/main/resources/grammars/Bpmn2.g4:30-52` |
| Build BPMN AST | `Bpmn2BuildingVisitor.build` | `.../Bpmn2BuildingVisitor.java:16-19` |
| Extract OCL raw text from BPMN | `Bpmn2BuildingVisitor.oclSource` | `.../Bpmn2BuildingVisitor.java:158-165` |
| Transform AST -> MM | `Bpmn2ModelFactory.build` | `.../Bpmn2ModelFactory.java:28-48` |
| Resolve sequence flows | `Bpmn2ModelFactory.resolveSequenceFlow` | `.../Bpmn2ModelFactory.java:103-108` |
| Read/parse USE model | `USECompiler.compileSpecification` | `.../Bpmn2OclValidationCompiler.java:46-51` |
| Read/parse context map | `Bpmn2OclContextMapParser.parse` | `.../Bpmn2OclContextMapParser.java:23-43` |
| Parse/compile OCL | `OCLCompiler.compileExpression` | `.../Bpmn2OclConstraintCompiler.java:103-120` |
| Validate missing context/class/OCL syntax | `Bpmn2OclConstraintCompiler.compileOne` | `.../Bpmn2OclConstraintCompiler.java:86-121` |
| Goal-BPMN OCL coverage check | `Bpmn2GoalOclCoverageValidator.validate` | `.../Bpmn2GoalOclCoverageValidator.java:23-43` |
| GUI BPMN open | `Bpmn2Form.open` | `.../Bpmn2Form.java:113-139` |
| Plugin action | `ActionOpenBpmn2.performAction` | `.../ActionOpenBpmn2.java:13-21` |

No class in `org.vnu.sme.goal.bpmn2.ocl` evaluates compiled expressions; grep
found only compile/coverage code, no `Evaluator` or `eval(...)` call in that
package.

## 5. Build / Run Discovery

Documented Maven command from `goal/docs/BPMN2_OCL_OPTION2.md`:

```bash
mvn -pl goal "-Dexec.mainClass=org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain" "-Dexec.classpathScope=compile" org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

Actual result:

```text
Non-resolvable parent POM for org.tzi.use:goal:7.1.1
Could not transfer artifact org.tzi.use:use:pom:7.1.1 ... Permission denied
```

`mvn validate` at repo root failed with the same parent POM resolution error.
Therefore the documented Maven path is not runnable in this environment.

Workaround used for audit:

1. Compile missing generated ANTLR classes into `target/classes`.
2. Compile stale/broken `Bpmn2Compiler`, `USECompiler`, and `OCLCompiler`.
3. Run Java directly with a classpath.

Direct run command shape:

```powershell
$cp='goal\target\classes;use\use-core\target\classes;use\use-gui\target\classes;C:\Users\Dao Huy Hung\.m2\repository\org\antlr\antlr4-runtime\4.9.3\antlr4-runtime-4.9.3.jar;C:\Users\Dao Huy Hung\.m2\repository\org\antlr\antlr-runtime\3.4\antlr-runtime-3.4.jar;C:\Users\Dao Huy Hung\.m2\repository\com\google\guava\guava\33.6.0-jre\guava-33.6.0-jre.jar;C:\Users\Dao Huy Hung\.m2\repository\com\google\guava\failureaccess\1.0.3\failureaccess-1.0.3.jar'
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain . <bpmn> <use> <map>
```

## 6. Official Sample Run

Command:

```powershell
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain
```

Actual output:

```text
BPMN OCL validation OK
Compiled constraints: 9
  claim_received [node, self : Claim]
  register_claim [node, self : Claim]
  validate_claim [node, self : Claim]
  eligibility_decision [node, self : Claim]
  approve_claim [node, self : Claim]
  reject_claim [node, self : Claim]
  claim_closed [node, self : Claim]
  eligibility_decision::approve_claim [sequenceFlow, self : Claim]
  eligibility_decision::reject_claim [sequenceFlow, self : Claim]
```

Conclusion: the official sample compiles BPMN-attached OCL successfully after
the generated parser/classpath workaround.

## 7. New Audit Use Case

Created files:

```text
goal/src/main/resources/examples/bpmn_ocl/audit/order_domain.use
goal/src/main/resources/examples/bpmn_ocl/audit/order_process_valid.bpmn2
goal/src/main/resources/examples/bpmn_ocl/audit/order_context.bpmn2oclmap
```

BPMN structure:

```text
StartEvent order_received
  -> Task review_order
  -> XOR Gateway decide_order
    -> Task approve_order
    -> Task reject_order
  -> EndEvent order_closed
```

OCL clauses:

| Owner | Constraint meaning | Expected |
|---|---|---|
| `order_received` | order must be created | compile OK |
| `review_order` | order reviewed and amount positive | compile OK |
| `decide_order` | gateway reached only after review | compile OK |
| `approve_order` | approval sets approved status and flag | compile OK |
| `reject_order` | rejection sets rejected status and flag | compile OK |
| `order_closed` | closed state accepts approved or rejected | compile OK |
| `decide_order::approve_order` | priority branch guard | compile OK |
| `decide_order::reject_order` | standard branch guard | compile OK |

Command:

```powershell
java -cp $cp org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain . goal\src\main\resources\examples\bpmn_ocl\audit\order_process_valid.bpmn2 goal\src\main\resources\examples\bpmn_ocl\audit\order_domain.use goal\src\main\resources\examples\bpmn_ocl\audit\order_context.bpmn2oclmap
```

Actual output:

```text
BPMN OCL validation OK
Compiled constraints: 8
  order_received [node, self : Order]
  review_order [node, self : Order]
  decide_order [node, self : Order]
  approve_order [node, self : Order]
  reject_order [node, self : Order]
  order_closed [node, self : Order]
  decide_order::approve_order [sequenceFlow, self : Order]
  decide_order::reject_order [sequenceFlow, self : Order]
```

## 8. Invalid Data Results

| Case | File(s) | Expected for robust validator | Actual |
|---|---|---|---|
| Missing StartEvent | `invalid_missing_start.bpmn2` | reject | accepted, 2 constraints compiled, exit 0 |
| Missing EndEvent | `invalid_missing_end.bpmn2` | reject | accepted, 2 constraints compiled, exit 0 |
| Bad sequence target | `invalid_bad_sequence.bpmn2` | clean error | uncaught `IllegalStateException: Unknown flow target: missing_task`, exit 1 |
| Duplicate flow element id | `invalid_duplicate_id.bpmn2` + `duplicate_context.bpmn2oclmap` | reject duplicate id | accepted, 3 constraints compiled, exit 0 |
| OCL syntax error | `invalid_ocl_syntax.bpmn2` | reject | rejected: `no viable alternative at input '<EOF>'`, exit 1 |
| OCL missing property | `invalid_ocl_missing_property.bpmn2` | reject | rejected: undefined operation/property `nonExistingFlag`, exit 1 |
| Missing context map entries | valid BPMN + `invalid_missing_context.bpmn2oclmap` | reject | rejected missing mappings for node/flows, exit 1 |
| Unknown USE context class | valid BPMN + `invalid_unknown_class.bpmn2oclmap` | reject | rejected: no `.use` class named `UnknownOrder`, exit 1 |
| Malformed context line | valid BPMN + `invalid_context_syntax.bpmn2oclmap` | reject | rejected: cannot parse line, exit 1 |

## 9. Edge Case Results

| Case | File | Actual |
|---|---|---|
| Empty process | `edge_empty_process.bpmn2` | accepted, 0 constraints, exit 0 |
| Start-End only | `edge_start_end_only.bpmn2` | accepted, 2 constraints, exit 0 |
| Task only | `edge_task_only.bpmn2` | accepted, 1 constraint, exit 0 |
| Nested subprocess | `edge_nested_subprocess.bpmn2` | accepted, 9 constraints including nested node/flow OCL |
| Contradictory OCL | `edge_contradictory_ocl.bpmn2` | accepted, 2 constraints, exit 0 |

The contradictory OCL case proves that the implementation compiles expressions
but does not evaluate satisfiability or truth over a system state.

## 10. Expected vs Actual Implementation

### BPMN parsing

Expected: read valid concrete syntax and report syntax errors.

Actual: works for grammar-level syntax. Evidence:

- `Bpmn2Compiler.compileStream` installs ANTLR error listener and returns errors only before AST/MM build.
- Valid audit BPMN compiled.

Match: partial.

### BPMN semantic validation

Expected: catch missing start/end, duplicate ids, invalid gateway shape, bad flows.

Actual:

- Missing start/end accepted.
- Empty process accepted.
- Task-only process accepted.
- Duplicate id accepted.
- Bad flow target throws uncaught exception.

Match: no.

### OCL parsing/compilation

Expected: compile BPMN OCL using USE context class.

Actual:

- Valid OCL compiled.
- Syntax error rejected.
- Missing property rejected.
- Missing/unknown context rejected.

Match: yes for compilation.

### OCL evaluation

Expected if full validation: evaluate constraints on a runtime state.

Actual:

- `Bpmn2OclConstraintCompiler` stores `Expression expr` in `ConstraintInfo`.
- No `eval` or `Evaluator` call exists in `org.vnu.sme.goal.bpmn2.ocl`.
- Contradictory expression compiles successfully.

Match: no, evaluation is not implemented in this BPMN2 OCL path.

### Transform model

Expected: AST to runtime MM with resolved references.

Actual:

- `Bpmn2ModelFactory.build` builds `Bpmn2Model`.
- Sequence flows resolve inside process/subprocess scope.
- Missing target crashes because `IllegalStateException` is not caught by `Bpmn2Compiler`.

Match: partial.

### Generate output

Expected: clear report.

Actual:

- CLI prints only compiled constraints.
- No detailed model summary, no evaluation result, no structural report.

Match: partial.

## 11. Code Review Findings

### High: Maven build is not runnable from repo root in this environment

Evidence:

```text
Non-resolvable parent POM for org.tzi.use:goal:7.1.1
```

Impact: documented Maven command cannot be used directly.

Likely fix: make `goal/pom.xml` parent resolution explicit and verify root
reactor build; add CI command for `mvn validate` / demo exec.

### High: Stale/broken compiled classes in target

Evidence:

```text
java.lang.Error: Unresolved compilation problems:
  Bpmn2Lexer cannot be resolved to a type
```

and later:

```text
USELexer cannot be resolved to a type
```

Impact: running existing `target/classes` fails before recompiling generated
parser classes.

Fix: clean rebuild must compile generated ANTLR sources into package paths;
avoid committing or relying on stale target artifacts.

### High: BPMN semantic errors can crash instead of returning errors

Evidence: bad flow target throws at `Bpmn2ModelFactory.resolveSequenceFlow`
lines 103-108, uncaught by `Bpmn2Compiler.compileStream`.

Impact: user gets stack trace, not validation error.

Fix: catch `IllegalStateException` around `Bpmn2ModelFactory.build(ast)` and
return `Result(null, errors)`.

### High: No structural BPMN validation

Evidence: missing start, missing end, empty process, task-only process, duplicate
ids all exit 0.

Impact: invalid BPMN-like models are accepted.

Fix: add `Bpmn2SemanticValidator` after MM construction: unique ids, at least
one start/end, endpoint membership, gateway split/join rules, reachability.

### Medium: Duplicate ids silently overwrite indexes

Evidence: `Bpmn2Model.addProcess` indexes flow elements with
`flowElementIndex.put(fe.id(), fe)` without duplicate check.

Impact: `findFlowElement` and OCL constraint map may point to the last duplicate
while both elements remain in `Process.flowElements`.

Fix: detect duplicates while building per-process and global model indexes.

### Medium: OCL is compiled but not evaluated

Evidence: no evaluator call in BPMN2 OCL package; contradictory OCL accepted.

Impact: output "validation OK" can be misread as constraints being true.

Fix: rename output to "BPMN OCL compilation OK" or add runtime state + evaluator.

### Medium: Context map parser is very strict and line-oriented

Evidence: `split("\\s+")`, exactly 4 tokens, lines 34-39.

Impact: class names with namespaces or richer mapping metadata are not possible.

Fix: keep simple format if intentional, otherwise parse with a small grammar.

## 12. Correctness Checklist

Functional:

- BPMN grammar-level syntax is parsed: yes.
- BPMN semantic validity is checked: no.
- BPMN OCL raw clauses are preserved: yes, verified through compiled constraints.
- USE domain model is parsed: yes after generated parser/classpath fix.
- OCL syntax and type checking happen: yes.
- OCL is evaluated on runtime state: no.
- Invalid structural BPMN is rejected: mostly no.
- Invalid OCL is rejected: yes.
- Output is a clear validation report: partial.

Technical:

- Maven command works: no in this environment.
- No runtime exception for invalid input: no.
- Error messages are clear: OCL/map errors yes; BPMN semantic errors no.
- Generated parser integration is reliable: no, current target/classes were stale/broken.

Architecture:

- Parser -> AST -> MM separation exists: yes.
- OCL compilation separated from BPMN model: yes.
- Semantic validation layer for BPMN is missing/incomplete.
- Evaluation/proof layer is not implemented for BPMN2 OCL Option 2.

Testing gaps:

- Add unit test for valid BPMN2 OCL compile.
- Add tests for missing start/end.
- Add tests for duplicate ids.
- Add tests for unknown sequence-flow endpoints returning clean errors.
- Add tests for nested subprocess OCL.
- Add tests proving invalid OCL syntax/type errors are returned.
- Add tests documenting compile-only behavior vs evaluation behavior.

## 13. Conclusion

1. End-to-end use case ran successfully after build/classpath workarounds: yes.
2. It reflects the implemented goal of BPMN-attached domain OCL compilation: yes.
3. It does not reflect full BPMN structural validation or goal-achievement proof: those are not implemented in this path.
4. Bugs found: Maven/target build issues, uncaught bad-flow exception, missing structural validation, duplicate id acceptance, misleading "validation OK" wording.
5. Missing pieces: OCL evaluation, BPMN semantic validator, proof of `post(operation) implies goal(condition)`.
6. Confidence: High for the audited BPMN2 OCL compile path, because conclusions are backed by source lines and executed outputs. Medium for broader project claims outside this path.
