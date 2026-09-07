# Example model pairs — BPMN process + i* SR goal model

Each example is a **pair** of models describing the *same* case study, transcribed from the
online appendix of:

> Nesi Outmazgin, Pnina Soffer, Irit Hadar.
> *Leveraging Workarounds for a Problem-Focused Improvement of Business Processes.*
> Business & Information Systems Engineering (2026).
> Source PDF: `latex/codex/12599_2026_985_MOESM1_ESM.pdf`

| Pair | BPMN (`.bpmn`) | i* SR model (`.pistar.json`) | Source figures |
| ---- | -------------- | --------------------------- | -------------- |
| Process 1 – Sales Forecast | `process1-sales-forecast.bpmn` | `process1-sales-forecast.pistar.json` | Appendix 2 p.6 (BPMN), Appendix 3 p.10 (i*) |
| Process 2 – Customer Complaint Management | `process2-customer-complaint.bpmn` | `process2-customer-complaint.pistar.json` | Appendix 2 p.7 (BPMN), Appendix 3 p.11 (i*) |

## How to open

* **`.bpmn`** — [demo.bpmn.io](https://demo.bpmn.io) → *Open file*, or the `bpmn-js` /
  Camunda Modeler stack. Valid BPMN 2.0 (collaboration + one participant + lanes + DI).
  Parses with `bpmn-moddle` with 0 warnings; every flow element has a DI shape/edge.
* **`.pistar.json`** — the [piStar tool](https://www.cin.ufpe.br/~jhcp/pistar/tool/)
  (v2.1.0) → *File ▸ Open model*. Same JSON schema as the provided `latex/codex/goalModel.txt`
  sample (`tool: "pistar.2.1.0"`, `istar: "2.0"`).

## How to regenerate

The models are emitted from compact Python specs so coordinates / IDs stay consistent:

```bash
cd examples
python3 gen_p1.py     # -> process1-sales-forecast.{bpmn,pistar.json}
python3 gen_p2.py     # -> process2-customer-complaint.{bpmn,pistar.json}
```

`genlib.py` holds the two emitters (`BpmnModel`, `IStarModel`) plus a grid auto-layout
helper for i* actor boundaries. Edit the `*_spec` dicts in `gen_p1.py` / `gen_p2.py`
to correct or extend a model, then re-run.

## What was transcribed

For every figure the reading order was: **count the nodes**, then **read the labels**,
then **connect nodes by their business meaning** and cross-check each edge against the
surrounding text (Appendix 4 misalignment tables, and the analysis notes in
`latex/codex/*.md`).

### BPMN — element mapping

* rounded blue boxes → `bpmn:task`
* yellow **X** diamond → `bpmn:exclusiveGateway`
* yellow **circle** diamond → `bpmn:inclusiveGateway` (used for the "merge / OR"
  join points and *Required Presale Engineer?* / *Service fee* merges)
* green circle → none-`startEvent`; red circle with envelope → message `endEvent`
* the two BPMN phase bands (*Preparation / Approvals*, *Complaint examination /
  Customer Compensation action*) are kept only as lane/pool context, not modelled as
  sub-processes.
* layout is schematic (column = flow order, row = lane); rearrange freely in bpmn.io.

### i* — element mapping

* blue hexagon → `istar.Task`
* green stadium → `istar.Goal`
* green wavy blob → `istar.Quality` (soft goal)
* green rectangle → `istar.Resource`
* tick-marked links → `istar.AndRefinementLink`
* `make / help / hurt / break` → `istar.ContributionLink`
* filled-ball link from a resource → `istar.NeededByLink`
* `is-a` → `istar.IsALink`
* half-circle dependency links → a dependum in `dependencies[]` + two
  `istar.DependencyLink`s (depender→dependum→dependee)

## Fidelity notes (places the source figure is hard to read)

* **Process 1 i\*** is a *best-effort* transcription. The source figure is a very dense
  hairball; all actors, intentional elements, refinements and the dependency network are
  captured, but a handful of the ~30 `make/help/hurt/break` contribution links onto the
  Customer-Manager soft goals (*Respond quickly and flexibly*, *Satisfy the customer*,
  *Short delivery time*) are inferred from Appendix 4 Table A rather than pixel-traced.
* **Dependency direction** (which actor is depender vs. dependee) was decided from
  business meaning where the little "D" glyphs were ambiguous.
* **`participates-in`** links visible in the Process 1/2 figures were **omitted** — piStar
  only allows `participates-in` from an `Agent` to a `Role`, and the actor kinds/direction
  could not be confirmed from the figure. Only the unambiguous `is-a` links are kept
  (`CFO`/`Customer and field Manager` → `Financial approvers`;
  `Director of Sales Sector` / `Sales Department Manager` / `Credit Control` /
  `VP Business Division` → `Approvers`).
* **Process 1 BPMN** `>10% / >5%` profitability checks are modelled as explicit gateways
  after each approver's *Approved?* gateway; in the figure the two labels sit next to a
  single diamond and could be one 3-way split.
* **Process 2 BPMN** the examination routing (*Customer Manager / Nutritionist / Lab
  Manager*) is modelled as a 3-way exclusive split that can escalate examiner-to-examiner,
  with all "no more examination" paths merging back to *Updating artifacts*.
