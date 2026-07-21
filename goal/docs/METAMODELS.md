# GOAL / OCL / BPMN Metamodel Overview

## 1. Purpose

This document summarizes the language-visible AST and runtime MM layers used by the plugin.

It is intentionally concise and should be read together with `ARCHITECTURE.md`.

## 2. GOAL

### 2.1 GOAL AST

Package:

- `org.vnu.sme.goal.ast`

Main nodes:

- `GoalModelCS`
- `ActorDeclCS`
- `ActorCS`
- `AgentCS`
- `RoleCS`
- `IntentionalElementCS`
- `GoalCS`
- `TaskCS`
- `QualityCS`
- `ResourceCS`
- `DependencyCS`
- `OutgoingLink`

Role:

- stores parsed GOAL declaration structure
- stores unresolved relations
- stores embedded OCL clauses through AST-level OCL nodes

### 2.2 GOAL MM

Package:

- `org.vnu.sme.goal.mm`

Main nodes:

- `GoalModel`
- `Actor`
- `Agent`
- `Role`
- `IntentionalElement`
- `Goal`
- `Task`
- `Quality`
- `Resource`
- `Dependency`
- `Contribution`
- `Refinement`
- `AndRefinement`
- `OrRefinement`
- `GoalClause`
- `Achieve`
- `Maintain`
- `Avoid`
- `Pre`
- `Post`

Role:

- runtime semantic model for diagram rendering and analysis

## 3. OCL Embedded in GOAL

### 3.1 OCL AST

Package:

- `org.vnu.sme.goal.ast.ocl`

Main nodes:

- `OclExpressionCS`
- `OclBinaryExpCS`
- `OclUnaryExpCS`
- `OclSelfExpCS`
- `OclVariableExpCS`
- `OclFeatureCallExpCS`
- `OclPropertyCallExpCS`
- `OclOperationCallExpCS`
- `OclIteratorExpCS`
- `OclAggregateCallExpCS`
- `OclAtPreExpCS`
- `OclLiteralExpCS`
- `OclStringLiteralExpCS`
- `OclIntegerLiteralExpCS`
- `OclRealLiteralExpCS`
- `OclBooleanLiteralExpCS`
- `OclNullLiteralExpCS`
- `OclEnumLiteralExpCS`
- `OclVariableDeclarationCS`

Role:

- parser-facing OCL AST stage
- syntax-only representation for embedded OCL
- separates GOAL parsing from runtime OCL MM
- conversion to runtime OCL MM happens only in `OclModelBuilder`

### 3.2 OCL MM

Package:

- `org.vnu.sme.goal.mm.ocl`

Main nodes:

- `Expression`
- `BinaryExp`
- `UnaryExp`
- `SelfExp`
- `VariableExp`
- `PropertyCallExp`
- `OperationCallExp`
- `IteratorExp`
- `AggregateCallExp`
- `AtPreExp`
- `BooleanLiteralExp`
- `IntegerLiteralExp`
- `RealLiteralExp`
- `StringLiteralExp`
- `EnumLiteralExp`
- `NullLiteralExp`
- `OpaqueExpression`
- `VariableDeclaration`

Role:

- runtime OCL representation used by semantic analyzers and compatibility layers

## 4. BPMN

### 4.1 BPMN AST

Package:

- `org.vnu.sme.goal.ast.bpmn`

Main nodes:

- `BpmnModelCS`
- `BpmnProcessCS`
- `BpmnNodeCS`
- `BpmnFlowCS`
- `BpmnNodeTypeCS`

Role:

- syntax-oriented process description
- preserves process declarations before semantic runtime construction

### 4.2 BPMN MM

Package:

- `org.vnu.sme.goal.mm.bpmn`

Main nodes:

- `BpmnModel`
- `BpmnProcess`
- `BpmnNode`
- `BpmnFlow`
- `BpmnNodeType`

Role:

- runtime process graph used by GOAL + BPMN verification

### 4.3 BPMN OCL Option 2

Package:

- `org.vnu.sme.goal.bpmn2.ocl`

Main nodes:

- `Bpmn2OclConstraintCompiler`
- `Bpmn2OclContextMapParser`
- `Bpmn2OclValidationCompiler`
- `Bpmn2OclValidationDemoMain`
- `Bpmn2GoalOclCoverageValidator`

Role:

- stores explicit Boolean `pre`/`post` predicates on BPMN elements and
  `guard` predicates on sequence flows
- compiles those clauses with USE `OCLCompiler`
- binds `self` through caller-provided BPMN element/flow id to USE class mappings
- supports goal-achievement checking by making BPMN-side behavioral conditions available
- has a runnable validation flow for `.bpmn2 + .use + .bpmn2oclmap`

Syntax:

```bpmn2
task Approve {
  name "Approve"
  post {[ self.status = #approved ]}
}

flow Decide -> Approve {
  name "valid"
  guard {[ self.valid = true ]}
}
```

## 5. Semantic Ownership

The semantic owner of each MM is:

- GOAL MM: `GoalModelFactory`, `GoalSemanticAnalyzer`
- OCL MM: `OclModelBuilder`, `OclSemanticAnalyzer`, `GoalOclService`
- BPMN MM: `BpmnModelFactory`, `GoalBpmnAnalyzer`

## 6. View Ownership

The semantic results are exposed through:

- `GoalDiagramView`
- GOAL OCL validation report
- GOAL status table
- GOAL design analysis report
- GOAL + BPMN verification report
