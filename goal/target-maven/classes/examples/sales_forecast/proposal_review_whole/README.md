# ProposalReview — bounded ACL/BPMN/iStar inconsistency

This is the executable counterpart of the paper's `ProposalReview`
motivating example. It is intentionally separate from the older
`tool/proposal_review.*` reconstruction, whose ACL and BPMN currently use
different state schemas.

## Files

- `proposal_review.acl`: the only system-state space;
- `proposal_review.bpmn2`: the AS-IS process with the missing re-validation;
- `proposal_review.istar`: intentions evaluated over generated ACL paths;
- `proposal_review.aclboundary`: finite Kodkod scope;
- `ProposalReviewWholeConsistencyTest`: repeatable expected result.

Load the four model files in **ACL State Evaluator**, then press
**Validate whole consistency**. No AOL snapshot or scenario is needed.

## Shared-state distinction

The essential ACL attributes are:

```text
currentRevision    : { none, r1, r2 }
validationRevision : { none, r1, r2 }
```

`Validate proposal` establishes:

```ocl
self.validationRevision = self.currentRevision
```

If the customer requests changes, `Update proposal` establishes:

```ocl
self.currentRevision = #r2
and self.validationRevision = self.validationRevision@pre
```

Thus the update creates the state

```text
currentRevision = r2
validationRevision = r1
```

The later Finalize and Send activities preserve both revision attributes.
They do not silently repair the invalidated goal.

The ACL intentionally does **not** declare “current revision is validated” as
an invariant. It is an iStar requirement. If it were an ACL invariant, the
defective branch would simply become an impossible ACL/BPMN path instead of a
cross-model counterexample.

## Two bounded BPMN executions

The XOR gateway yields exactly two maximal executions in this boundary.

| Customer decision | Relevant final state | Root-goal result |
|---|---|---|
| No changes | `currentRevision=r1`, `validationRevision=r1` | fulfilled |
| Changes requested | `currentRevision=r2`, `validationRevision=r1` | violated |

Both executions reach `proposalCompleted`, so there is no BPMN deadlock. The
second execution fails only after BPMN effects and iStar goal conditions are
interpreted over the same evolving ACL state.

Expected whole result:

```text
WEAKLY_CONSISTENT
realizable BPMN executions = 2
goal-achieving executions  = 1
```

The returned counterexample contains:

```text
changesRequested -> updateProposal
updateProposal -> feedbackMerge
feedbackMerge -> finalizeProposal
finalizeProposal -> sendProposal
sendProposal -> proposalCompleted
```

## iStar evaluation

The verdict targets only these root Goals:

```text
ProposalManager.ProposalSuccessfullyCompleted
Customer.ProposalReceived
```

`ProposalSuccessfullyCompleted` has no condition of its own. It is propagated
from three branches:

1. the proposal is prepared, reviewed, and its **current** revision remains
   validated;
2. customer feedback is resolved by acceptance or incorporation;
3. the current revision is finalized and sent.

`CurrentRevisionValidated` is a `Sustain` leaf. It becomes true after
validation of r1, but the r2 update makes it false and it stays violated. The
other child Goals may still be fulfilled; AND propagation therefore prevents
the root Goal from being fulfilled on that branch.

Root Tasks are excluded from the verdict. The leaf Tasks in the model exist
to make the explanatory BPMN–iStar mapping inspectable. For example, the UI
maps `validateProposal` to `ValidateProposal` and `updateProposal` to
`UpdateProposal` using their identifiers and OCL vocabulary. The SAT verdict
does not depend on this heuristic mapping.

## Why this is the motivating inconsistency

At the static model level, every expected activity appears present: prepare,
validate, review, update, finalize, and send. A static activity-to-goal mapping
therefore looks complete. The inconsistency is temporal: Update changes the
identity of the current revision after validation. Only evaluation over the
shared ACL state path exposes that the earlier validation no longer concerns
the proposal that is finally sent.
