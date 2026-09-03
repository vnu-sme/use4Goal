# Customer Complaint Management bounded example

This directory contains an executable abstraction of *Process 2: Customer
Complaint Management* described in
`latex/codex/process2_bpmn_vs_istar_goal_analysis.md`.

## Files

- `customer_complaint_management.acl`: shared organizational and complaint
  state.
- `customer_complaint_management.bpmn2`: the executable BPMN process.
- `customer_complaint_management.istar`: the intentional requirements.
- `customer_complaint_management.aclboundary`: the finite exploration bound.

The plugin uses the `.bpmn2` suffix for its textual BPMN DSL.

## Modelling assumption

The source analysis classifies closing a pending complaint without handling
or updating it as a *possible* misalignment, but the source BPMN diagram does
not expose that behaviour as a confirmed Start-to-End branch. For bounded
verification, this example makes that possibility explicit as the
`handlingDecision -> closePendingComplaint` branch. The result therefore
applies to this executable abstraction; it must not be reported as a defect
proven directly from the original diagram.

All remaining branches model ordinary complaint handling, optional
examination and improvement, and the no-refund, low-value-refund, and
high-value-refund routes. Monetary values use thousands of NIS, so the ACL
integer boundary `0..8` contains the threshold `5` and representative values
`4` and `6`.

## Reproducing the result

From the `goal` directory, run:

```sh
/run/media/qbert/1C8099B8809998BA/env/apache-maven-3.9.9/bin/mvn \
  -q -DisolatedPluginBuild \
  -Dtest=org.vnu.sme.goal.aclstate.CustomerComplaintManagementConsistencyTest \
  test
```

Expected result:

| Nodes | Complete executions | Conformant | Non-conformant | Risky | Classification |
|---:|---:|---:|---:|---:|---|
| 22 | 10 | 9 | 1 | 1 | `WEAKLY_CONSISTENT`, `RISK_PRONE` |

The counterexample is the premature-closure execution. It changes
`ResolutionIntegrity` from satisfied to violated by setting `closed=true`
while `resolved=false`. The nine ordinary executions achieve every root
Goal. Three isolated verification runs after compilation took 4.015 s,
3.088 s, and 3.403 s on the development machine (median: 3.403 s).
