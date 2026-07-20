# BPMN2 OCL Option 2

This document describes the implemented Option 2 approach: OCL is embedded in
BPMN process elements as behavioral/domain conditions. It is not used as a
complete BPMN metamodel invariant language.

## 1. Purpose

Option 2 answers this question:

```text
Can BPMN execution points carry domain OCL conditions that can later be compared
with mapped i-star/goal OCL conditions?
```

The implemented answer is yes. BPMN flow elements and sequence flows may now
carry raw OCL clauses. The clauses are preserved through parsing, stored in the
runtime BPMN metamodel, and compiled against a USE domain model when the caller
provides the USE context class for each BPMN element.

## 2. Supported Syntax

Every BPMN flow element accepts an optional OCL block:

```bpmn2
task approveClaim "Approve Claim" ocl {[
  self.status = #approved
]}

gateway decide : xor ocl {[
  self.isComplete = true
]}
```

Sequence flows also accept OCL. This is the natural place for branch guards:

```bpmn2
flow decide -> approveClaim : "valid" ocl {[
  self.valid = true
]}

flow decide -> rejectClaim : "invalid" ocl {[
  self.valid = false
]}
```

The parser captures only the raw text inside `ocl {[ ... ]}`. It does not parse
OCL with ANTLR. Compilation is delegated to USE's existing `OCLCompiler`.

Full sample:

```text
goal/src/main/resources/examples/bpmn_ocl/claim_handling_ocl.bpmn2
```

## 3. Implemented Flow

The implementation follows the existing project architecture:

```text
.bpmn2 text
  -> Bpmn2.g4
  -> Bpmn2BuildingVisitor
  -> bpmn2.ast CS records
  -> Bpmn2ModelFactory
  -> bpmn2.mm runtime objects
  -> Bpmn2OclConstraintCompiler
  -> USE OCLCompiler
```

Step by step:

1. `Bpmn2.g4` recognizes optional `ocl {[ ... ]}` clauses on `poolElement` and
   `sequenceFlow`.
2. `Bpmn2BuildingVisitor` strips the delimiters and stores the raw body in the
   AST/CS layer.
3. `FlowElementCS` and `SequenceFlowCS` carry `oclSource`.
4. `Bpmn2ModelFactory` copies `oclSource` into the runtime metamodel.
5. Runtime BPMN classes expose the text through `oclSource()`.
6. `Bpmn2OclConstraintCompiler.compile(...)` compiles each clause with USE.
7. `Bpmn2GoalOclCoverageValidator` checks whether mapped i-star/goal elements
   that have OCL are realized by BPMN nodes that also have BPMN OCL.

The compiler is wired into an end-to-end service:

```text
Bpmn2OclValidationCompiler
  -> Bpmn2Compiler
  -> USECompiler
  -> Bpmn2OclContextMapParser
  -> Bpmn2OclConstraintCompiler
```

There is also a runnable demo:

```text
Bpmn2OclValidationDemoMain
```

## 4. Runtime Metamodel Changes

The following runtime objects can now carry OCL:

```text
FlowElement
  StartEvent
  EndEvent
  IntermediateEvent
  Task
  CallActivity
  SubProcess
  Gateway

SequenceFlow
```

`FlowElement` has a default method:

```java
String oclSource()
```

The default returns `null`. Concrete classes override it when they store an OCL
clause. `SequenceFlow` has its own `oclSource()` method.

Existing constructors are preserved. New overloaded constructors accept
`oclSource`, so existing Java code that creates BPMN elements still compiles.

## 5. OCL Compilation

The compiler class is:

```text
org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclConstraintCompiler
```

It compiles BPMN OCL against a USE `MModel`:

```java
Bpmn2OclConstraintCompiler.Result result =
    Bpmn2OclConstraintCompiler.compile(bpmnModel, useModel, contextTypes);
```

`contextTypes` maps BPMN OCL owners to USE class names:

```text
approveClaim         -> Claim
decide::approveClaim -> Claim
```

For flow elements, the key is the BPMN element id.

For sequence flows, the key is:

```text
sourceId::targetId
```

During compilation, `self` is bound to the mapped USE class.

The context map file format is line-oriented:

```text
context <bpmnElementId> -> <UseClassName>
context <sourceId>::<targetId> -> <UseClassName>
```

Example file:

```text
goal/src/main/resources/examples/bpmn_ocl/claim_handling.bpmn2oclmap
```

Example context mapping for the sample file:

```text
claim_received                  -> Claim
register_claim                  -> Claim
validate_claim                  -> Claim
eligibility_decision            -> Claim
approve_claim                   -> Claim
reject_claim                    -> Claim
claim_closed                    -> Claim
eligibility_decision::approve_claim -> Claim
eligibility_decision::reject_claim  -> Claim
```

## 6. Running the Wired Compiler

Sample inputs:

```text
goal/src/main/resources/examples/bpmn_ocl/claim_handling_ocl.bpmn2
goal/src/main/resources/examples/bpmn_ocl/claim_handling.use
goal/src/main/resources/examples/bpmn_ocl/claim_handling.bpmn2oclmap
```

Run from the repository root after compilation:

```bash
mvn -pl goal "-Dexec.mainClass=org.vnu.sme.goal.bpmn2.ocl.Bpmn2OclValidationDemoMain" "-Dexec.classpathScope=compile" org.codehaus.mojo:exec-maven-plugin:3.5.0:java
```

Expected output:

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

## 7. Goal Achievement Position

This implementation does not yet prove goal achievement by itself. It provides
the BPMN-side OCL data needed for that proof.

The intended goal-achievement flow is:

```text
i-star/goal element has OCL
  -> conformance mapping maps it to a BPMN node
  -> BPMN node/flow has OCL
  -> USE compiles both OCL expressions
  -> execution/path checker compares BPMN effects/guards with the goal condition
```

Useful verdicts for the next layer:

```text
MAY_ACHIEVE   at least one complete BPMN path satisfies the goal OCL
MUST_ACHIEVE  every complete BPMN path satisfies the goal OCL
VIOLATES      some complete path contradicts the goal OCL
UNKNOWN       mapping or OCL information is missing
```

## 8. Difference From Option 1

Option 1 would express BPMN structural validity as OCL over the BPMN metamodel,
for example "every process has a start event" or "a gateway is a split or join".

Option 2 expresses domain conditions at BPMN execution points, for example "this
task establishes `claim.status = #approved`". That is the required foundation
for checking whether a BPMN process can achieve a business goal.
