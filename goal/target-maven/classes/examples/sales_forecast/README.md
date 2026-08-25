# Sales Forecast motivating case

This directory is an executable reconstruction of the industrial price-
proposal conflict reported by Nesi Outmazgin, Pnina Soffer, and Irit Hadar.

Sources:

- *Workarounds in Business Processes: A Goal-Based Analysis*, CAiSE 2020,
  DOI `10.1007/978-3-030-49435-3_23`.
- *Leveraging Workarounds for a Problem-Focused Improvement of Business
  Processes*, BISE 2026, DOI `10.1007/s12599-026-00985-3`, especially Online
  Appendices 2--4.

The source authors already identify the conflict qualitatively using manually
assigned `Hurt`/`Break` contributions. This reconstruction uses their result as
an independent oracle; it does not claim that they missed the conflict.

## Files and roles

- `sales_forecast.acl`: shared organizational and proposal state, including
  seven OCL invariants on `SalesCase` that formalize the lifecycle ordering
  (preparation before request, request before review, review before
  approval-gated sending, approved/sent revision consistency).
- `sales_forecast_state_01_initial.aol` through `sales_forecast_state_05_workaround_invalid.aol`:
  five AOL v2 state snapshots for the **ACL State Evaluator**, tracing the
  process lifecycle from the ACL defaults through the on-time, delayed, and
  workaround end states; see `ACL_STATE_EVALUATOR.md` for the expected
  true/false OCL result of each.
- `sales_forecast.istar`: goals and OCL predicates; deliberately contains no
  `Hurt`/`Break` link that would encode the expected answer.
- `sales_forecast_full.istar`: complete structural reconstruction of the
  published Sales Forecast SR diagram (17 actors, 45 intentional elements,
  and 21 dependencies), including its qualitative contribution links.
- `sales_forecast_full.bpmn2`: complete control-flow reconstruction of the
  published Sales Forecast process (eight lanes and 51 flow elements), used
  for source comparison and structural translation checks.
- `sales_forecast.bpmn2`: prescribed approval process, including on-time and
  delayed review outcomes; this is the executable verification slice.
- `sales_forecast_workaround.bpmn2`: observed direct-send deviation, kept out
  of the prescribed process.
- `official_on_time.soil`: conformant prescribed execution input.
- `official_delayed.soil`: prescribed counterexample input.
- `workaround.soil`: direct-send counterexample input.
- `ANALYSIS.md`: source evidence, modelling boundary, and validity claims.
- `tool/sales_forecast_full_sr_pistar.txt`: readable piStar 2.1.0 JSON view
  centered on seven actors and the state-dependent cross-model impact.
- `tool/sales_forecast_full_process.bpmn`: readable BPMN 2.0 collaboration
  showing the prescribed process and observed workaround as separate pools.
- `tool/CASE_STUDY_GUIDE.md`: Vietnamese walkthrough connecting the actors,
  complete process, workaround, shared state, and research gap.
- `tool/generate_tool_models.mjs`: deterministic generator for both tool
  interchange files.
- `proposal_review_whole/`: bounded ACL+BPMN+iStar motivating example in
  which both BPMN branches reach End, but the update branch sends revision r2
  with validation still attached to r1. Whole consistency returns
  `WEAKLY_CONSISTENT` with one goal-achieving path out of two realizable paths.

## Expected executions

| Input and process | Relevant final goal states | Verdict |
|---|---|---|
| `official_on_time.soil` + prescribed BPMN | approval, compliance, speed, and satisfaction fulfilled | conformant |
| `official_delayed.soil` + prescribed BPMN | approval/compliance fulfilled; speed/satisfaction pending | non-conformant |
| `workaround.soil` + workaround BPMN | speed/satisfaction fulfilled; approved-only sending violated and compliance pending | non-conformant |

The delayed trace is the execution-level reproduction of the published
misalignment: the process correctly approves and sends the current revision,
yet the Customer Manager's `RespondQuicklyAndFlexibly` and
`CustomerSatisfied` goals remain unsatisfied. The workaround reverses the
trade-off.

The two iStar files are intentionally not interchangeable. The complete SR
model is the source-faithful view used to inspect the whole organizational
rationale. The smaller executable model is the verification slice: it omits
the source's manually assigned negative contributions so that the checker must
derive the conflict from the process-generated ACL state.

## Why a full BPMN and two executable slices?

The complete BPMN preserves the preparation route, optional presale work,
service-agreement review, the profitability approval hierarchy, financial and
credit control, rejection loops, and order completion shown in the published
appendix. The source does not publish executable state effects for these
activities, so this model does not invent their postconditions. The smaller
prescribed BPMN contains only the state-changing approval/delay slice needed
to reproduce the motivating conflict.

The e-mail/Word/Excel workaround is a deviation from the prescribed process,
not an official gateway choice. Putting it in the same BPMN would incorrectly
turn non-compliance into permitted control flow. The two processes share the
same ACL and iStar model, so their final intentional verdicts remain directly
comparable.

## Repeatable check

The three expected verdicts and the structural well-formedness of both
complete published-model reconstructions are executable acceptance tests:

```text
mvn -f goal/pom.xml \
  -Dtest=org.vnu.sme.goal.verify.conformance.SalesForecastConformanceTest test
```

The test drives the BPMN token engine, applies activity postconditions to the
ACL/USE state, reevaluates the iStar predicates at every checkpoint, and checks
the final root goals. It does not assign goal statuses by hand.

The full BPMN also passes BPMN semantic validation and ACL+BPMN-to-USE/OCL
translation without diagnostics. Both focused ACL+iStar+BPMN combinations can
be exported to Event-B without translation diagnostics. Rodin proof discharge
remains a separate verification step.

## Explicit abstraction

The papers report approval delay qualitatively but do not publish a response-
time threshold. `approvalDelayed`, `responseWindowOpen`, and
`sentWithinWindow` therefore form a Boolean scenario abstraction. Proposal
revision is bounded to `{none, r1, r2}` because the current OCL-to-Event-B
translator does not support the required Integer arithmetic. Neither
abstraction is presented as an empirical value reported by the source study.

The appendix uses an inclusive marker for the Yes/No decision on whether a
presale engineer is required. Because these alternatives are mutually
exclusive and the current executable dialect has no inclusive-gateway
semantics, the reconstruction uses XOR. Published numerical thresholds are
represented by Boolean ACL classifications because the source supplies no
data schema or calculation formula from which their values could be derived.
