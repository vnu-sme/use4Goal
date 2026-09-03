# Semantic Task–Activity mapping

The Event-B export also runs a trace-independent analyzer over the compiled ACL,
iStar, and BPMN models. It always produces a partial mapping report when the three
source models are well formed; consistency is a verification result, not a
precondition for producing the table.

## Generated files

For project `P`, the exporter adds:

- `P_mapping.md`: Task–Activity/fragment mappings, Goal and Task coverage,
  Activity classification, diagnostics, and verification obligation ids;
- `P_mapping.csv`: stable machine-readable mapping relation;
- `P_mapping_diff.md`: `NEW`, `RETAINED`, `CHANGED`, and `REMOVED` relations,
  when a previous mapping CSV is supplied;
- mapping-soundness properties in `P_properties.ltl` for ProB;
- theorem guards labelled `map_*_sound` in `P_machine.bum` for Rodin.

The analyzer uses ACL properties as the shared semantic vocabulary. A candidate
is ranked using postcondition coverage, compatible produced values, Actor/Lane
context, and names only as a final weak tie-breaker. Name equality is never
sufficient by itself.

Relations have the following meanings:

- `REALIZES`: one BPMN Activity covers an iStar Task postcondition;
- `COMPOSITE_REALIZES`: a BPMN fragment jointly covers a Task postcondition;
- `ENABLES`: an Activity effect covers a Task precondition.

Activity classifications distinguish `MAPPED`, `ENABLES_TASK`,
`DIRECT_GOAL_EFFECT`, `CONTROL_ONLY`, and `ORPHAN`. An unmapped Task and an
uncovered root Goal are reported separately because an optional Task need not
make its root Goal uncovered.

## Verification status

`STATICALLY_SUPPORTED` is not a proof result. For a supported atomic mapping,
the exporter translates the Task postcondition to Event-B, substitutes the
BPMN Event actions to obtain its weakest precondition, and inserts that formula
as an Event theorem guard. Rodin must discharge the corresponding `THM` proof
obligation before the mapping can be called certified.

The same implication is emitted as a ProB LTL property over reachable states.
A ProB counterexample refutes the candidate. An open Rodin proof obligation
without a ProB counterexample is inconclusive, not false. Multi-occurrence
`forall`/`pick` Tasks and composite mappings remain explicit unsupported
obligations until an atomicity/group-refinement translation is supplied.

## Headless use

```text
EventBExportMain model.acl model.istar model.bpmn2 output-directory ProjectName
EventBExportMain model.acl model.istar model.bpmn2 output-directory ProjectName previous_mapping.csv
```

The USE Event-B export action offers the same optional mapping-evolution input.
