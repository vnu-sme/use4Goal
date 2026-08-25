# ACL/BPMN validation boundary

An `.aclboundary` file defines the finite relational universe for **Whole BPMN
validation**. It is not a scenario and does not reference AOL files.

```text
acl-bpmn-boundary v1.0 ClassroomBound {
  snapshots 24;
  loop-bound 3;
  integer -8..8;

  objects Classroom 1;
  objects Person 2;
  objects Teacher 1;
  objects Student 1..4;

  links Classroom_contains_Teacher 1;
  links Classroom_contains_Student 1..4;

  string "optional finite string atom";
  real 0.0;
}
```

Declarations:

- `snapshots n`: maximum number of generated ACL states in one execution;
- `loop-bound n`: maximum executions of each cyclic BPMN sequence flow;
- `objects C l..u`: per-snapshot lower and upper scope for classifier `C`;
- `links R l..u`: optional per-snapshot total link scope for association `R`;
- `integer l..u`: finite integer value domain;
- `string` and `real`: additional finite primitive atoms.

Every ACL Entity, Role, and Group must have an `objects` declaration. Association
link scopes are optional because endpoint multiplicities already constrain them.

Kodkod creates independent symbolic copies of
`sigma_Class`, `sigma_Att`, `sigma_Assoc`, and `sigma_Play` for each snapshot.
The encoder enforces ACL object scopes, total attributes, association
multiplicities, required Role-play parents, and all ACL/OCL invariants. BPMN
`Pre_B` is evaluated in the preceding snapshot and `Post_B` relates the
preceding and following snapshots through `@pre`.

The symbolic OCL backend supports Boolean connectives, equality and ordered
comparisons over finite scalar domains, object/association navigation,
`allInstances`, `forAll`, `exists`, `includes`, `isEmpty`, `notEmpty`, and
property-path `@pre`. An unsupported operation yields `INCONCLUSIVE`, never a
false `VALID` or `INVALID` result.
