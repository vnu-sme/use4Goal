# Source analysis and modelling boundary

## Evidence base

This example is grounded in the Sales Forecast / price-proposal process studied
by Nesi Outmazgin, Pnina Soffer, and Irit Hadar:

1. *Workarounds in Business Processes: A Goal-Based Analysis*, CAiSE 2020,
   DOI `10.1007/978-3-030-49435-3_23`.
2. *Leveraging Workarounds for a Problem-Focused Improvement of Business
   Processes*, BISE 2026, DOI `10.1007/s12599-026-00985-3`, and its online
   supplement.

The 2020 study reports an industrial organization, stakeholder interviews,
organizational/process documents, information-system material, and a BPMN
model validated with interviewees. The 2026 supplement publishes the full
Sales Forecast BPMN and iStar SR diagrams and reports 46,098 Sales Forecast
events among the collected material.

## Relevant requirements and behaviour

The focused conflict involves four parties:

- Management wants to avoid sending unapproved price proposals and risky
  commitments.
- The Customer Manager wants to respond quickly and flexibly, satisfy the
  customer, and progress sales.
- Approvers review the proposal and its commercial risk before approval.
- The customer receives the resulting proposal.

The prescribed process prepares a price proposal, collects supporting inputs,
submits it through approval, handles rejection/revision when necessary, and
sends the proposal only after approval. The full source process distributes
approval work among several organizational units; this executable
reconstruction abstracts them as one multi-valued `Approver` role because the
motivating conflict depends on completion of the approval round, not on which
particular approval office performs a step.

The observed workaround is materially different: a Customer Manager prepares
a proposal in Word or Excel and sends it directly by e-mail before it exists in
the information system and before approval. It improves response speed but can
commit the organization to an unprofitable or risky deal.

## Did the source authors miss this error?

No. This must not be claimed in the paper.

The 2020 paper explicitly identifies the misalignment between Management's
`Avoid sending unapproved proposals` goal and the Customer Manager's `Respond
quickly and flexibly` and `Satisfy the customer` goals (PDF page 11). It models
the prescribed sending task as `Break`/`Hurt` to those goals. It then describes
the direct e-mail workaround and its organizational risk (PDF page 12). The
2026 supplement repeats the conflict in Sales Forecast situations 1.2--1.4 and
1.8--1.9 (PDF page 14).

Consequently, this is not evidence that the prior goal-based analysis produces
a wrong verdict. It is evidence of a real and independently documented
goal--process conflict.

## What remains new and testable here

The source analysis represents the answer qualitatively by manually authored
`Hurt` and `Break` contribution links. It does not calculate the verdict for a
particular proposal execution from shared business state. The present
reconstruction deliberately omits those negative links and asks three
execution-level questions instead:

The source-faithful structure is retained separately in
`sales_forecast_full.istar`: 17 actors, 45 intentional elements, 21 strategic
dependencies, actor specializations, resources, and the published qualitative
contributions. The executable `sales_forecast.istar` remains a deliberately
smaller verification slice so those contributions do not predetermine its
verdict.

1. Can the prescribed process satisfy approval and response goals when review
   completes inside the response window?
2. When the same prescribed process is delayed, does it still send only the
   current approved revision, and which response goals remain unsatisfied?
3. Does the workaround trace respond within the window, and does it still
   satisfy the approval/risk-control goals?

The ACL predicates make the two outcomes observable:

| Trace | Approved/current proposal | Within response window | Resulting gap |
|---|---:|---:|---|
| Prescribed, on time | yes | yes | none; every root goal is fulfilled |
| Prescribed, delayed | yes | no | response and satisfaction goals remain pending |
| Direct-send workaround | no | yes | approved-only sending is violated; compliance and approval goals remain pending |

This is the defensible motivation for shared-state scenario checking. It does
not prove that every related technique misses the conflict; it shows that a
state-based checker can derive a concrete counterexample without encoding the
counterexample itself as a negative contribution.

## Explicit abstractions

- `approvalDelayed`, `responseWindowOpen`, and `sentWithinWindow` formalize
  on-time and tight-response scenarios. The papers report delay qualitatively
  but publish no response-time threshold from which a numeric deadline can be
  reproduced.
- `ProposalRevision = {none, r1, r2}` bounds the rejection/revision loop to one
  revision. This avoids claiming unbounded numeric reasoning and remains in the
  supported OCL-to-Event-B fragment.
- `Approver [1..*]` abstracts the source's several approval units. Those units
  can be expanded later without changing the conflict predicate.
- The prescribed process and observed workaround are separate BPMN files. They
  share ACL and iStar state semantics but the workaround is never presented as
  an officially permitted branch. Both are focused executable reconstructions.
- `sales_forecast_full.bpmn2` separately reconstructs the full appendix
  process with eight original lanes and 51 flow elements. It is a structural
  source-comparison model: the source publishes control flow but not executable
  state effects, so no unreported postconditions are added.
- The source's inclusive marker at `Required Presale Engineer?` is represented
  by XOR: its outgoing branches are explicitly Yes/No, while the current BPMN
  engine does not implement inclusive-gateway semantics.
- Source thresholds (service fee, profitability, and deal amount) are Boolean
  ACL classifications rather than invented arithmetic formulas. This retains
  the decisions visible in the diagram without claiming an unpublished data
  model.

## Current validation status

- ACL parser: passed.
- Complete SR iStar syntax/semantic validation: passed (17 actors, 45
  intentional elements, and 21 dependencies).
- Executable iStar parser and ACL+iStar-to-USE/OCL generation: passed.
- BPMN parser: passed for the full published-process reconstruction (eight
  lanes and 51 flow elements), the prescribed process (eight activities, two
  guarded flows), and the workaround process (two activities).
- Full BPMN ACL+BPMN-to-USE/OCL translation: passed without diagnostics.
- Acceptance tests: passed for both complete-model well-formedness checks and
  all three expected execution verdicts.
- Integrated ACL+iStar+BPMN-to-Event-B generation: passed for both BPMN files
  with no translation diagnostics.
- Rodin proof obligations and ProB state-space properties have not yet been
  executed; successful generation is not a proof result.
