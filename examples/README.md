# Official case studies

This directory is the single source of example models for the **Conformance of
Processes and Goals** plugin. The plugin and evaluation tools do not load
examples from `goal/src/main/resources/examples/`.

Each executable case study uses the following public file types:

- `.bpmn2`: process model
- `.istar`: goal model
- `.csl`: state model
- `.cslboundary`: bounded checking configuration
- `.aol`: optional concrete initial-state snapshot

The five evaluated case studies are `ProposalReview`, `Forecast`,
`CustomerComplaintManagement`, `StudentRegistration`, and `PurchaseOrder`.
The four adapted cases also contain a `paper/` directory holding the models
used by the reproducible evaluation in `evaluation/`.

CSL replaces the former ACL public name. The compiler still accepts the legacy
`acl` header and old boundary header during migration, while all official files
and generated output use `csl`.
