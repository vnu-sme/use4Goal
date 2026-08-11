# MTG input scenarios and conformance cases

SOIL files in this directory are input fixtures, not execution traces. They only create the
concrete meeting, agents, role assignments, group membership, capabilities and known contact
data that exist before the BPMN StartEvent. BPMN effects generate all later state changes.

## ACL/AOL state ownership

`MeetingParty` is an abstract Agent profile. Its `name`, `phone`, and `hasCalendar` values are
persistent Agent state and are therefore initialized on `Agent`, once per person. `Initiator`,
`Organizer`, `Secretary`, and `Participant` are concrete occurrences inside one `MeetingUnit`.
Only meeting-specific state (`timetableCollected`, `timetableChannel`, `notified`, and `attended`)
belongs to `Participant`.

The directed `knowsPhoneOf` association is separate from the `phone` value: having a phone
number does not imply that every other Agent knows it. In `mtg.aol`, Bob explicitly knows
Alice's and Carol's numbers.

`mtg.aol` is the canonical object-language example of this separation. The `.soil` files are
equivalent executable fixtures retained for the conformance runner.

## Naming

`mtg_i<I>o<O>p<P>s<S>.soil` records the number of Initiator, Organizer, Participant and
Secretary role instances. Extra input dimensions are documented inside the file because they
do not change the structural role-count signature.

| SOIL input | Calendar/contact input | Expected trace with `mtg.bpmn2` |
|---|---|---|
| `mtg_i1o1p2s1.soil` | mixed calendar; participant phone profiles available | phone then calendar |
| `mtg_i1o1p3s1.soil` | every participant has calendar | calendar only |
| `mtg_i1o1p4s1.soil` | two calendar, two phone; required phone profiles available | phone then calendar |

`mtg.soil` remains the small default input and now also contains only neutral pre-execution
state.

## Positive and negative conformance cases

A negative conformance case is **not** an invalid initial snapshot. It is a valid ACL/SOIL
input for which a BPMN solution reaches EndEvent while at least one root goal remains pending.

| ACL | SOIL | BPMN | Expected verdict |
|---|---|---|---|
| `mtg.acl` | `mtg_i1o1p3s1.soil` | `mtg.bpmn2` | CONFORMANT |
| `mtg.acl` | `mtg_i1o1p4s1.soil` | `mtg.bpmn2` | CONFORMANT |
| `mtg.acl` | `mtg_i1o1p3s1.soil` | `mtg_goal_gap.bpmn2` | NOT CONFORMANT |
| `mtg.acl` | `mtg_i1o1p3s1.soil` | `mtg_nondeterministic.bpmn2` | WEAKLY CONFORMANT |

The last BPMN deliberately omits `participate`. It nevertheless reaches EndEvent, thereby
exposing that the attendance part of the i* root goal is not fulfilled.

The nondeterministic BPMN has two simultaneously valid XOR completions. One performs
`participate`, the other reaches EndEvent without attendance. The explorer therefore reports
weak=true, strong=false and lets the debugger select either generated trace.
