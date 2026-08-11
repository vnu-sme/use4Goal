package org.vnu.sme.goal.translate.aclbpmn2use;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.dsl.acl.mm.AclGroup;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclRole;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;
import org.vnu.sme.goal.dsl.bpmn.mm.ActivityConstraint;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Gateway;
import org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind;
import org.vnu.sme.goal.dsl.bpmn.mm.Lane;
import org.vnu.sme.goal.dsl.bpmn.mm.Process;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;

/**
 * Translates the pair (AclModel, BpmnModel) into a {@code .use} specification
 * (plain OCL), mirroring {@code org.vnu.sme.goal.translate.aclistar2use.AclIStar2UseTranslator}'s
 * shape and reusing the same building blocks ({@link Acl2UseTranslator} for the
 * class diagram, {@link AclOclPropertyResolver} for {@code self.group}/{@code
 * self.agent} rewriting).
 *
 * <p><b>Context resolution</b> (which class hosts a Task/Gateway/Event's guard
 * operations): {@code Bpmn2.g4}'s own doc comment on {@code pool} is explicit
 * that "every activity/gateway declared for this pool evaluates its
 * pre/effect/post with self bound to one concrete instance of that
 * [groupClass] class" -- so {@link Process#groupClass()} (an ACL Group), when
 * declared, is the host for every one of that pool's flow elements,
 * unconditionally (a {@link Lane} is documented as purely visual grouping, not
 * an ownership/scoping relation). For a pool that does not declare a
 * groupClass, this translator falls back to matching each {@link Lane}'s name
 * against ACL Role names (mirroring how iStar actors match ACL roles); a flow
 * element in neither ends up on a standalone class.
 *
 * <p><b>Scope</b>: every {@code FlowElement} (Task/CallActivity/SubProcess/
 * Start-End-IntermediateEvent/Gateway) gets {@code <id>_preHolds()}/{@code
 * _postHolds()} operations from its {@link ActivityConstraint}s (same shape as
 * an iStar Task). Each non-default {@link SequenceFlow} with a guard gets a
 * {@code <source>_to_<target>_guard()} operation. Gateway branching itself is
 * also modeled: {@code XOR}/{@code EVENT_BASED} gateways get "exactly one guard
 * holds" (or "at most one" when a default flow exists), {@code OR} gateways get
 * "at least one guard holds" (skipped when a default flow exists), and {@code
 * AND} gateways get no branch-selection invariant at all (parallel split/join
 * has none).
 *
 * <p>BPMN currently has no temporal-property source analogous to iStar's
 * {@code GoalType} (ACHIEVE/MAINTAIN/...), so {@link Result#toclText()} is
 * always empty; no {@code .tocl} file is produced by the caller in that case.
 */
public final class AclBpmn2UseTranslator {

    private AclBpmn2UseTranslator() {}

    public record Result(String useText, String toclText, List<String> diagnostics) {
        public boolean ok() { return diagnostics.isEmpty(); }
    }

    public static Result translate(AclModel acl, BpmnModel bpmn) {
        List<String> diagnostics = new ArrayList<>();

        Set<String> aclRoleNames = acl.roles().stream().map(AclRole::name)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        Set<String> aclGroupNames = acl.groups().stream().map(AclGroup::name)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));

        // ── Pass 0: resolve each FlowElement's host class, then compute its
        // preHolds/postHolds (and each outgoing SequenceFlow's guard) operations. ─
        Map<String, String> elementContextType = new LinkedHashMap<>();
        Map<String, List<String>> contextOperationLines = new LinkedHashMap<>();

        for (Process process : bpmn.processes()) {
            resolveContext(process, aclRoleNames, aclGroupNames, elementContextType, diagnostics);
        }
        for (Process process : bpmn.processes()) {
            emitElementOperations(process, acl, elementContextType, contextOperationLines);
        }

        // ── Layer 1: ACL class diagram, with each resolved host class's
        // operations spliced into its already-rendered class block. ────────────
        String aclPart = Acl2UseTranslator.translate(acl);
        String unifiedName = sanitize(bpmn.name()) + "_Verification";
        String useText = aclPart.replaceFirst("model\\s+\\S+", "model " + unifiedName);

        Set<String> consumed = new LinkedHashSet<>();
        for (var entry : contextOperationLines.entrySet()) {
            String type = entry.getKey();
            if (!aclRoleNames.contains(type) && !aclGroupNames.contains(type)) continue;
            useText = Acl2UseTranslator.spliceOperations(useText, sanitize(type), entry.getValue());
            consumed.add(type);
        }

        StringBuilder out = new StringBuilder(useText);

        // ── Layer 2: standalone classes for Lane/Process fallback context types
        // that matched no ACL role/group. ───────────────────────────────────────
        for (var entry : contextOperationLines.entrySet()) {
            String type = entry.getKey();
            if (consumed.contains(type)) continue;
            out.append("-- Standalone class (no matching ACL role/group)\n");
            out.append("class ").append(sanitize(type)).append("\n");
            if (!entry.getValue().isEmpty()) {
                out.append("operations\n");
                entry.getValue().forEach(line -> out.append(line).append("\n"));
            }
            out.append("end\n\n");
        }

        out.append("constraints\n\n");
        out.append("-- ===== BPMN activity/gateway guards are class operations, ")
           .append("declared above on each host class (see USE 'operations' section) =====\n\n");

        // ── Layer 3: Gateway branching structural invariants. ──────────────────
        out.append("-- ===== Gateway structural invariants =====\n\n");
        for (Process process : bpmn.processes()) {
            emitGatewayInvariants(process, out, elementContextType);
        }

        return new Result(out.toString(), "", diagnostics);
    }

    // ── Context resolution (Lane -> ACL Role, fallback Process.groupClass) ────

    private static void resolveContext(Process process, Set<String> aclRoleNames, Set<String> aclGroupNames,
                                       Map<String, String> elementContextType, List<String> diagnostics) {
        // Bpmn2.g4's own doc comment on 'pool' is explicit: "every activity/gateway
        // declared for this pool evaluates its pre/effect/post with self bound to
        // one concrete instance of that [groupClass] class" -- self-binding for a
        // whole pool is groupClass, unconditionally, when the pool declares one.
        // Lane is documented as purely visual grouping (Lane javadoc: "does NOT
        // own them") and is only used here as a fallback naming convention (Lane
        // name == ACL Role name, mirroring iStar actor<->ACL role) for pools that
        // don't declare a groupClass at all.
        String groupClass = process.groupClass();
        boolean groupClassValid = groupClass != null && aclGroupNames.contains(groupClass);
        if (groupClassValid) {
            for (FlowElement fe : allFlowElements(process)) elementContextType.put(fe.id(), groupClass);
            return;
        }
        if (groupClass != null) {
            diagnostics.add("Warning: process '" + process.name() + "' groupClass '" + groupClass
                    + "' has no matching ACL group — falling back to per-Lane context resolution.");
        }

        Set<String> laneCovered = new LinkedHashSet<>();
        for (Lane lane : process.lanes()) {
            String laneName = lane.name();
            if (!aclRoleNames.contains(laneName)) {
                diagnostics.add("Warning: BPMN lane '" + laneName + "' in process '" + process.name()
                        + "' has no matching ACL role — emitted as standalone class.");
            }
            for (FlowElement fe : lane.flowElements()) {
                elementContextType.put(fe.id(), laneName);
                laneCovered.add(fe.id());
            }
        }

        boolean warnedFallback = false;
        for (FlowElement fe : allFlowElements(process)) {
            if (laneCovered.contains(fe.id())) continue;
            if (!warnedFallback) {
                diagnostics.add("Warning: process '" + process.name()
                        + "' has flow elements outside any lane and no (valid) groupClass — emitted as standalone class.");
                warnedFallback = true;
            }
            elementContextType.put(fe.id(), process.name());
        }
    }

    // ── Operations: preHolds/postHolds per FlowElement, guard() per SequenceFlow ─

    private static void emitElementOperations(Process process, AclModel acl,
                                               Map<String, String> elementContextType,
                                               Map<String, List<String>> contextOperationLines) {
        for (FlowElement fe : allFlowElements(process)) {
            String contextType = elementContextType.get(fe.id());
            String eid = sanitize(fe.id());

            String preOcl = oclBodyOf(fe, ActivityConstraint.Kind.PRE);
            String preExpr = preOcl != null ? AclOclPropertyResolver.rewrite(acl, contextType, preOcl) : "true";
            String postOcl = oclBodyOf(fe, ActivityConstraint.Kind.POST);
            String postExpr = postOcl != null ? AclOclPropertyResolver.rewrite(acl, contextType, postOcl) : "false";

            contextOperationLines.computeIfAbsent(contextType, k -> new ArrayList<>()).addAll(List.of(
                    "  " + eid + "_preHolds() : Boolean =\n    " + preExpr,
                    "  " + eid + "_postHolds() : Boolean =\n    " + postExpr));
        }
        for (SequenceFlow flow : allSequenceFlows(process)) {
            if (flow.isDefault()) continue; // the fallback branch needs no guard of its own
            String contextType = elementContextType.get(flow.source().id());
            if (contextType == null) continue;

            String guardOcl = flow.guardSource();
            String guardExpr = guardOcl != null ? AclOclPropertyResolver.rewrite(acl, contextType, guardOcl) : "true";
            contextOperationLines.computeIfAbsent(contextType, k -> new ArrayList<>())
                    .add("  " + flowGuardOpName(flow) + "() : Boolean =\n    " + guardExpr);
        }
    }

    // ── Gateway XOR/OR/AND branch-selection invariants ─────────────────────────

    private static void emitGatewayInvariants(Process process, StringBuilder out,
                                              Map<String, String> elementContextType) {
        List<SequenceFlow> allFlows = allSequenceFlows(process);
        for (FlowElement fe : allFlowElements(process)) {
            if (!(fe instanceof Gateway gateway) || gateway.kind() == GatewayKind.AND) continue;

            List<SequenceFlow> outgoing = allFlows.stream()
                    .filter(f -> f.source().id().equals(gateway.id())).toList();
            List<String> guardCalls = outgoing.stream()
                    .filter(f -> !f.isDefault())
                    .map(f -> "self." + flowGuardOpName(f) + "()")
                    .toList();
            if (guardCalls.size() < 2) continue; // nothing to arbitrate between
            boolean hasDefault = outgoing.stream().anyMatch(SequenceFlow::isDefault);

            String comparison = switch (gateway.kind()) {
                case XOR, EVENT_BASED -> hasDefault ? " <= 1" : " = 1";
                case OR -> hasDefault ? null : " >= 1";
                case AND -> null; // filtered out above
            };
            if (comparison == null) continue;

            String contextType = elementContextType.get(gateway.id());
            String countExpr = "Sequence{" + String.join(", ", guardCalls) + "}->select(b : Boolean | b)->size()";
            out.append("context ").append(sanitize(contextType)).append("\n")
               .append("  inv Gateway_").append(sanitize(gateway.id())).append(":\n")
               .append("    ").append(countExpr).append(comparison).append("\n\n");
        }
    }

    // ── Recursive FlowElement/SequenceFlow flattening (SubProcess nests its own) ─

    private static List<FlowElement> allFlowElements(Process process) {
        List<FlowElement> result = new ArrayList<>();
        collectFlowElements(process.flowElements(), result);
        return result;
    }

    private static void collectFlowElements(List<FlowElement> elements, List<FlowElement> out) {
        for (FlowElement fe : elements) {
            out.add(fe);
            if (fe instanceof SubProcess sp) collectFlowElements(sp.flowElements(), out);
        }
    }

    private static List<SequenceFlow> allSequenceFlows(Process process) {
        List<SequenceFlow> result = new ArrayList<>(process.sequenceFlows());
        collectNestedFlows(process.flowElements(), result);
        return result;
    }

    private static void collectNestedFlows(List<FlowElement> elements, List<SequenceFlow> out) {
        for (FlowElement fe : elements) {
            if (fe instanceof SubProcess sp) {
                out.addAll(sp.sequenceFlows());
                collectNestedFlows(sp.flowElements(), out);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String flowGuardOpName(SequenceFlow flow) {
        return sanitize(flow.source().id()) + "_to_" + sanitize(flow.target().id()) + "_guard";
    }

    private static String oclBodyOf(FlowElement fe, ActivityConstraint.Kind kind) {
        return fe.constraints().stream()
                .filter(c -> c.kind() == kind)
                .map(ActivityConstraint::oclBody)
                .reduce((a, b) -> "(" + a + ") and (" + b + ")")
                .orElse(null);
    }

    /** Convert arbitrary name to a valid USE identifier. */
    static String sanitize(String name) {
        if (name == null || name.isBlank()) return "unnamed";
        String clean = name.replaceAll("[^A-Za-z0-9_]", "_");
        return Character.isDigit(clean.charAt(0)) ? "_" + clean : clean;
    }
}
