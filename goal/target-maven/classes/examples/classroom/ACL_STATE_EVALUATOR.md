# Classroom — ACL State Evaluator example

`classroom.acl` contains the structural model and four OCL invariants. The
evaluator operates directly on ACL classifier kinds and formal AOL v2 states;
it does not generate a USE shadow model.

## Independent ACL state evaluation

Load `classroom.acl`, then add these three snapshots:

| State file | Purpose | Expected OCL result |
|---|---|---|
| `classroom_state_01_initial.aol` | Initial state using ACL defaults | 4 true, 0 false |
| `classroom_state_02_completed.aol` | Completed consistent lesson | 4 true, 0 false |
| `classroom_state_03_invalid.aol` | Deliberately violates every invariant | 0 true, 4 false |

`Person`, `Teacher`, and `Student` remain separate Role objects. A declaration
such as `play teacherPerson -> teacher1` belongs to `sigma_Play`, while
`Classroom_contains_Teacher` belongs to `sigma_Assoc`. No Agent object is
created by this evaluator.

## Feature 1: supplied-scenario conformance

Use **Load state-only scenario...** and select
`classroom_process.aclscenario`. The file contains only ACL/BPMN paths and AOL
states; it never names the executed flow. Each adjacent state pair is exactly
one application of formal `EXEC`.

Repeated files are intentional. They represent a Start/Gateway step that
changes `sigma_F` while leaving `sigma_M` unchanged:

```text
acl-state-scenario v1.0 ClassroomElectronicRun {
  acl "classroom.acl";
  bpmn "classroom.bpmn2";
  state "classroom_state_01_initial.aol";
  state "classroom_state_01_initial.aol";
  state "classroom_state_01_initial.aol";
  state "classroom_trace_state_01_started.aol";
  // ...one state for every later formal flow step...
  state "classroom_trace_state_07_finished.aol";
}
```

The inferred execution starts and ends with:

```text
bottom -> classroomReady
-> classroomReady -> beginLesson
-> beginLesson -> prepSplit
-> ...
-> attendLesson -> classFinished
```

The End flow is executable: it checks the branch-specific postcondition
contributed by `attendLesson`, consumes that flow, and produces the empty flow
set. The classroom result is `AMBIGUOUS` but conforming because the two
parallel preparation flows may occur in either order.

Gateway branches use flow-specific postconditions, not separate guards:

```text
flow recordAttendanceElectronically post {[ self.electronicSystemAvailable ]}
flow recordAttendanceManually post {[ not self.electronicSystemAvailable ]}
```

The trace tab shows, for every step, the inferred flow, `Pass(f)`, `Next(f)`,
`Pre_B(f)`, `Post_B(f)`, and the resulting flow configuration.

## Feature 2: Kodkod whole-process validation

The **Whole BPMN validation** tab uses the same Kodkod/SAT4J relational engine
bundled with the cloned USE Validator. It does not load a scenario or any AOL
state. Load these four files instead:

- `classroom.acl`;
- `classroom.bpmn2`;
- `classroom.istar`;
- `classroom.aclboundary`.

The boundary declares the finite search space:

```text
acl-bpmn-boundary v1.0 ClassroomBound {
  snapshots 24;
  loop-bound 3;
  integer -8..8;
  objects Classroom 1;
  objects Person 2;
  objects Teacher 1;
  objects Student 1;
  links Classroom_contains_Teacher 1;
  links Classroom_contains_Student 1;
}
```

For every bounded BPMN execution, Kodkod creates the object-existence,
attribute, association, and `sigma_Play` relations independently at every
snapshot. ACL multiplicities and invariants hold at every generated state;
adjacent states are constrained by the corresponding `Pre_B/Post_B`.

For every realizable BPMN route, a second SAT query adds the iStar root-goal
formula over the same ACL path. Non-root goals/tasks only propagate through
AND/OR refinement; root tasks are not verdict targets. `CONSISTENT` means all
realizable bounded routes reach every root goal, `WEAKLY_CONSISTENT` means at
least one but not all routes do, and `INCONSISTENT` means none do.

The **BPMN–iStar mapping** tab reports the inferred correspondence from each
BPMN activity to an iStar leaf Goal/Task, including its actor/lane, score, and
the identifier/OCL evidence. This table explains the correspondence; the
verdict itself is solved from the state-path formulas rather than trusting the
mapping score.

## Negative and ambiguous scenarios

| Scenario | Expected result | Meaning |
|---|---|---|
| `classroom_error_postcondition.aclscenario` | `NON_CONFORMANT` | `Post_B(beginLesson,prepSplit)` is false. |
| `classroom_error_out_of_order.aclscenario` | `NON_CONFORMANT` | Neither incoming source post at the AND join explains early delivery. |
| `classroom_error_wrong_branch.aclscenario` | `NON_CONFORMANT` | The manual branch post is false and the electronic activity cannot explain a manual outcome. |
| `classroom_ambiguous_parallel.aclscenario` | `AMBIGUOUS` | Both interleavings of the parallel flows survive. |

ACL invariant failure and BPMN flow failure remain separate columns, but a
formal conforming scenario requires every supplied `sigma_M` to satisfy all ACL
invariants.

OCL postconditions are relational predicates, not implicit frame conditions.
An attribute not constrained by `Post_B(f)` may change. To forbid that change,
state its equality to the `@pre` value explicitly or introduce a future frame
construct. A chain that stops before consuming its End flow is a conformant
prefix whose `completedPaths` count remains zero.
