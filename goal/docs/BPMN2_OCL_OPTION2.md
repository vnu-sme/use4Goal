# BPMN state predicates and execution

The BPMN dialect describes Boolean state conditions only. It contains no SOIL
and performs no domain-state mutation.

## Friendly syntax

Names and conditions are explicit properties inside a block. All properties
are optional except the `type` of a gateway.

```bpmn2
task registerClaim {
  name "Register claim"
  pre {[
    self.submitted
  ]}
  post {[
    self.registered
  ]}
}

gateway decision {
  name "Eligibility decision"
  type xor
}

flow decision -> approve {
  name "eligible"
  guard {[
    self.eligible
  ]}
}
```

- Missing `name` means the element ID is used as its technical identity.
- Missing `pre` means the activity can start whenever a token reaches it.
- Missing `post` means completion needs no resulting-state assertion.
- Multiple `pre` or `post` clauses are combined with logical AND.
- Missing `guard` means the sequence flow is unconditional.

`pre`, `post`, and `guard` contain Boolean OCL. The parser preserves their OCL
body and USE's `OCLCompiler` compiles it later against the mapped domain class.

## Gateway execution

Branch conditions belong to outgoing sequence flows, not to the gateway:

```bpmn2
gateway route {
  type xor
}

flow route -> accepted {
  guard {[ self.valid ]}
}
flow route -> rejected {
  guard {[ not self.valid ]}
}
```

For an XOR gateway, flows are checked in declaration order and the first true
guard is chosen. This gives deterministic “first satisfied branch wins”
semantics. If no branch is true, execution stops with an error. AND/OR routing
accepts every outgoing flow whose guard is true.

## Execution boundary

`Bpmn2ExecutionEngine` is a token executor for one BPMN process. The intended
cycle is:

1. `start(state)` checks start-event conditions and routes the initial token.
2. `begin(activityId, beforeState)` verifies that the activity is enabled and
   all of its preconditions are true.
3. An external adapter performs the real action and produces a new USE state.
4. `complete(step, afterState)` verifies all postconditions and routes the token.
5. Gateways evaluate guards against the current state automatically.

```java
var engine = new Bpmn2ExecutionEngine(process);
engine.start(oclEvaluatorFor(currentState));

var step = engine.begin("registerClaim", oclEvaluatorFor(currentState));
currentState = actionAdapter.execute(step.elementId(), currentState);
engine.complete(step, oclEvaluatorFor(currentState));
```

The action adapter may call an application service, a REST API, a human-task
handler, or a simulator. It is deliberately outside BPMN, so the process model
states what must be true without prescribing how state is changed.

## Compilation pipeline

```text
.bpmn2
  -> Bpmn2.g4
  -> Bpmn2BuildingVisitor
  -> AST records
  -> Bpmn2ModelFactory
  -> runtime model
  -> Bpmn2OclConstraintCompiler
  -> USE OCLCompiler
```

Context-map keys use the element ID for `pre`/`post` and
`sourceId::targetId` for a flow guard. See:

```text
goal/src/main/resources/examples/bpmn_ocl/claim_handling_ocl.bpmn2
goal/src/main/resources/examples/bpmn_ocl/claim_handling.bpmn2oclmap
```

This executor currently handles a single process token graph. Full BPMN join
synchronization, nested subprocess lifecycle, persistence, retries, and human
task assignment remain concerns for a production workflow runtime.
