# Sales Forecast — ACL State Evaluator example

`sales_forecast.acl` contains the shared organizational/proposal schema and
seven OCL invariants on `SalesCase`, formalizing the lifecycle ordering
enforced by `sales_forecast.bpmn2`'s activity pre/postconditions:

- `SalesCase::RequestRequiresPreparation`
- `SalesCase::ReviewRequiresRequest`
- `SalesCase::SentRequiresReview`
- `SalesCase::SentRequiresApproval`
- `SalesCase::ApprovedRevisionMatchesCurrent`
- `SalesCase::SentRevisionMatchesCurrent`
- `SalesCase::CompletionRequiresSending`

Open **GoalModel Plugin → ACL State Evaluator**, load `sales_forecast.acl`,
then add the five AOL snapshots in filename order.

| State file | Purpose | Expected OCL result |
|---|---|---|
| `sales_forecast_state_01_initial.aol` | Before `proposalReceived`; ACL defaults | 7 true, 0 false |
| `sales_forecast_state_02_prepared.aol` | After `prepareProposal` + `submitForApproval` | 7 true, 0 false |
| `sales_forecast_state_03_approved_on_time.aol` | Prescribed process, `official_on_time.soil` end state | 7 true, 0 false |
| `sales_forecast_state_04_approved_delayed.aol` | Prescribed process, `official_delayed.soil` end state | 7 true, 0 false |
| `sales_forecast_state_05_workaround_invalid.aol` | Workaround process, `workaround.soil` end state | 5 true, 2 false |

State 05 is the deliberately invalid snapshot: `sendUnapprovedProposal` reaches
`proposalSent = true` while `approvalStatus` is still `draft` and
`reviewCompleted` is still `false`, so it violates exactly
`SentRequiresApproval` and `SentRequiresReview`. The other five invariants
stay vacuously true because the approval/review/revision-matching attributes
they constrain were never touched by the workaround.

States 03 and 04 are structurally identical for these seven invariants —
both are approval-gated, so all seven hold in either case. That is the point
of keeping both: the ACL invariants alone cannot separate the on-time
("conformant") trace from the delayed ("non-conformant") one described in
`README.md`; only the `sales_forecast.istar` goal predicates
(`RespondQuicklyAndFlexibly`, `CustomerSatisfied`) can. ACL invariants check
structural/process-ordering validity of a state; they are not a substitute
for goal-level conformance checking.

These files use the formal AOL v2 state representation (see
`docs/semantics/dsl/acl.md` sec. 16). `Management`, `CustomerManager`,
`Approver` and `Customer` are root Role types in this ACL (none `extends`
another Role), so each snapshot declares one instance of each directly and
links it into `salescase1` via the auto-generated
`SalesCase_contains_<Role>` composition — no `play` statements are needed
here because there is no Role specialization chain in this model. The
evaluator operates directly on the ACL classifier kinds and AOL object state;
it does not generate or load a USE class model.
