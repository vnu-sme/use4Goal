# BPMN flow-state semantics and bounded validation

The normative formulas are in `doc/formal/latex/bpmn.tex`. The executable
runtime follows its flow-based configuration:

\[
\sigma_F'=(\sigma_F\setminus Pass(f))\cup Next(f).
\]

Every adjacent pair in a supplied `.aclscenario` represents one execution of
one flow. For `f=(u,v)`, the runtime evaluates the target precondition and the
source/branch postcondition only when `f` executes:

\[
Pre_B(f)=pre_T(v),\qquad Post_B(f)=post_F(f).
\]

An End-targeting flow is executable. It has `Pre_B=true`, checks the source
branch postcondition, consumes itself, and adds no successor flow.

## Kodkod backend

Integrated whole-process validation implements the bounded formulas

\[
\forall\rho_B\in Max(\mathcal R_{k,s}(B))\;\exists P_M\in D_\beta^{n+1}:
P_M\models_M\rho_B,
\]

where `D` is the set of ACL object diagrams admitted by the loaded
`.aclboundary`. It is defined by classifier/link scopes and finite primitive
domains, not by loading AOL snapshots. A sequence flow whose target can reach
its source is cyclic and is limited by `loop-bound`.

The universal BPMN choice is enumerated by the formal `Pass/Next/EXEC`
runtime. For each maximal bounded route, Kodkod/SAT4J solves the existential
ACL path by creating time-indexed relations:

- `Class_C_i`: existing objects of classifier `C` at snapshot `i`;
- `Attr_a_i`: attribute values at snapshot `i`;
- `Assoc_r_i` and `Play_i`: association and role-play links;
- ACL multiplicity and invariant formulas at every snapshot;
- `Pre_B(f_i)` over snapshot `i-1` and `Post_B(f_i)` over the pair
  `(i-1,i)`.

The route formula is:

\[
\bigwedge_i WF_A(\sigma_M^i)\land Inv_M(\sigma_M^i)
\land
\bigwedge_{i>0}
Pre_B(f_i)(\sigma_M^{i-1})\land
Post_B(f_i)(\sigma_M^{i-1},\sigma_M^i).
\]

The loaded iStar model adds no independent state variables. Its markings are
derived at each position from the same generated ACL path. Only root Goals are
verdict targets; non-root Goal/Task values are used solely by AND/OR
propagation. For each BPMN route the engine first solves the route formula, and
then solves it again conjoined with `Achieves_I(P_M)`.

- `SAT` gives a decoded, generated object-diagram witness path.
- `UNSAT` gives a bounded BPMN counterexample with no ACL witness in `D`.
- `CONSISTENT` means every realizable bounded route also satisfies all root
  iStar Goals.
- `WEAKLY_CONSISTENT` means at least one but not every realizable route does.
- `INCONSISTENT` means no realizable route reaches the root Goals.
- `INCONCLUSIVE` means a required binding/state universe is absent or an
  exploration safety limit was reached.

This is a bounded relational proof over every object diagram admitted by the
boundary. AOL files are used only by scenario conformance and never by whole
validation. The activity-to-leaf mapping returned by the UI is explanatory;
the solver verdict comes from the OCL state-path formula.
