# ProposalReview case study

Small three-language reconstruction of the "proposal review missing a
re-validation step" defect (adapted from
`goal/src/main/resources/examples/sales_forecast/proposal_review_whole/`).

| File | Language | Role |
| ---- | -------- | ---- |
| `proposal-review.acl` | ACL v3.1 | structural model: `ProposalManager` / `Customer` roles with their own state, `Proposal` entity with an enumerated `phase` + the two revision counters, `ReviewRound` sub-entity, three associations |
| `proposal-review.aclboundary` | acl-bpmn-boundary v1.0 | bounded-verification scope (16 snapshots, one replan) |
| `proposal-review.istar` | iStar 2.1 | SR view; `CurrentRevisionValidated : Sustain` is the goal that detects the defect |
| `proposal-review.bpmn2` | state BPMN | AS-IS process: `updateProposal` advances `currentRevision` but leaves `validationRevision` behind |

**The defect.** On the changes-requested branch the process reaches
`sendProposal` with `currentRevision > validationRevision`: the customer's
requested changes were incorporated but never re-validated. The BPMN run
completes; the i* goal `CurrentRevisionValidated` does not hold.

Consistency: every BPMN lane is an ACL role, `pool ... for
ProposalReviewCase` binds to the orgContext, every i* actor is an ACL role,
and every `self.proposal.*` / `self.customer.*` / `self.proposalManager.*`
reference resolves to an ACL attribute.
