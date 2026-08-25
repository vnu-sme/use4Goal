package org.vnu.sme.goal.translate.aclbpmn2use;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclGroup;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclPropertyResolver;
import org.vnu.sme.goal.dsl.bpmn.mm.ActivityConstraint;
import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Gateway;
import org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind;
import org.vnu.sme.goal.dsl.bpmn.mm.Lane;
import org.vnu.sme.goal.dsl.bpmn.mm.Process;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.StartEvent;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;
import org.vnu.sme.goal.translate.acl2use.Acl2UseTranslator;

/**
 * Compiles ACL data state and BPMN behaviour into USE/OCL plus TOCL.
 *
 * <p>Every BPMN flow node is translated to an operation on the ACL Role named
 * by its lane. Activity {@code pre}/{@code post} clauses become the matching
 * operation contracts. BPMN OCL is group-scoped at source level; after the
 * operation is moved to a Role, {@code self} is therefore qualified through
 * that Role's owning Group (for example {@code self.meetingUnit}).</p>
 *
 * <p>No process-state class or token attributes are generated. Sequence-flow
 * precedence is emitted in the companion TOCL model: whenever a target node is
 * called, the required predecessor operation must have been called in the past
 * for a Role occurrence belonging to the same Group occurrence. Multiple
 * incoming flows are conjunctive for an AND join and alternative otherwise.</p>
 */
public final class AclBpmn2UseTranslator {

    private static final Pattern SELF = Pattern.compile("\\bself\\b");
    private static final Set<String> USE_KEYWORDS = Set.of(
            "abstract", "aggregation", "association", "attributes", "begin", "between",
            "class", "composition", "constraints", "context", "declare", "delete", "destroy",
            "and", "div", "do", "else", "end", "endif", "enum", "false", "for", "from",
            "if", "implies", "in", "insert", "into", "inv", "iterate", "let", "model", "new",
            "operations", "ordered", "not", "or", "post", "pre", "redefines", "role", "subsets",
            "then", "true", "undefined", "while", "xor");

    private AclBpmn2UseTranslator() {}

    public record Result(String useText, String toclText, List<String> diagnostics) {
        public boolean ok() { return diagnostics.stream().noneMatch(x -> x.startsWith("Error:")); }
    }

    private record ProcessPlan(
            Process process,
            String groupType,
            Map<FlowElement, String> roleByElement,
            Set<String> singleValuedRoles,
            List<SequenceFlow> flows) {

        String role(FlowElement element) {
            return roleByElement.get(element);
        }

        boolean boundToGroup() {
            return groupType != null;
        }
    }

    public static Result translate(AclModel acl, BpmnModel bpmn) {
        String baseUse = Acl2UseTranslator.translate(acl);
        return translateOnto(acl, bpmn, baseUse,
                sanitize(bpmn.name()) + "_Verification");
    }

    /**
     * Adds BPMN operations, contracts and temporal precedence to an existing
     * ACL-derived USE model.  This composition point is shared by the
     * ACL+BPMN and ACL+iStar+BPMN pipelines.
     */
    public static Result translateOnto(AclModel acl, BpmnModel bpmn,
                                       String baseUseText, String modelName) {
        List<String> diagnostics = new ArrayList<>();
        List<ProcessPlan> plans = bpmn.processes().stream()
                .map(process -> plan(acl, process, diagnostics))
                .toList();

        String useText = baseUseText.replaceFirst("model\\s+\\S+", "model " + sanitize(modelName));

        Map<String, LinkedHashSet<String>> operationsByRole = new LinkedHashMap<>();
        for (ProcessPlan plan : plans) {
            for (FlowElement element : allFlowElements(plan.process())) {
                String role = plan.role(element);
                if (role == null) continue;
                operationsByRole.computeIfAbsent(role, ignored -> new LinkedHashSet<>())
                        .add("  " + operationName(element.id()) + "()");
            }
            for (SequenceFlow flow : plan.flows()) {
                if (flow.isDefault() || flow.guardSource() == null || flow.guardSource().isBlank()) continue;
                String role = plan.role(flow.source());
                if (role == null) continue;
                operationsByRole.computeIfAbsent(role, ignored -> new LinkedHashSet<>())
                        .add("  " + flowGuardOperation(flow) + "() : Boolean =\n    "
                                + rewriteGroupScoped(acl, role, plan.groupType(), flow.guardSource()));
            }
        }
        for (var entry : operationsByRole.entrySet()) {
            useText = Acl2UseTranslator.spliceOperations(
                    useText, entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        StringBuilder contracts = new StringBuilder();
        contracts.append("\nconstraints\n\n")
                .append("-- ===== BPMN operation contracts, hosted by lane Roles =====\n\n");
        for (ProcessPlan plan : plans) {
            for (FlowElement element : allFlowElements(plan.process())) {
                emitOperationContract(plan, element, acl, contracts);
            }
        }

        StringBuilder tocl = new StringBuilder();
        tocl.append("-- BPMN sequence-flow constraints generated from ")
                .append(bpmn.name()).append(".bpmn2\n")
                .append("-- TOCL-compatible profile: each invariant is hosted by the target lane Role.\n\n");
        for (ProcessPlan plan : plans) {
            emitRequiredStartOccurrences(plan, tocl);
            emitTemporalPrecedence(plan, tocl, diagnostics);
        }

        return new Result(useText + contracts, tocl.toString(), List.copyOf(diagnostics));
    }

    private static ProcessPlan plan(AclModel acl, Process process, List<String> diagnostics) {
        Set<String> groups = acl.groups().stream().map(AclGroup::name)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        String groupType = process.groupClass();
        if (groupType == null || !groups.contains(groupType)) {
            if (groupType == null) {
                diagnostics.add("Warning: process '" + process.id()
                        + "' has no groupClass; temporal ordering cannot be scoped to one ACL Group.");
            } else {
                diagnostics.add("Error: process '" + process.id() + "' groupClass '" + groupType
                        + "' has no matching ACL Group.");
                groupType = null;
            }
        }

        Map<FlowElement, String> roles = new IdentityHashMap<>();
        for (FlowElement element : allFlowElements(process)) {
            Lane lane = laneOf(process, element);
            if (lane == null) {
                diagnostics.add("Error: BPMN node '" + element.id() + "' in process '"
                        + process.id() + "' is not assigned to a lane.");
                continue;
            }
            if (acl.findRole(lane.id()).isEmpty()) {
                diagnostics.add("Error: BPMN lane '" + lane.id() + "' in process '"
                        + process.id() + "' does not resolve to an ACL Role.");
                continue;
            }
            if (groupType != null) {
                String expectedGroup = groupType;
                boolean owned = acl.owners().stream().anyMatch(owner ->
                        owner.sourceGroup().equals(expectedGroup) && owner.target().equals(lane.id()));
                if (!owned) {
                    diagnostics.add("Error: BPMN lane Role '" + lane.id()
                            + "' is not owned by process Group '" + groupType + "'.");
                    continue;
                }
            }
            roles.put(element, sanitize(lane.id()));
        }
        Set<String> singleValuedRoles = new LinkedHashSet<>();
        if (groupType != null) {
            String group = groupType;
            acl.owners().stream()
                    .filter(owner -> owner.sourceGroup().equals(group))
                    .filter(owner -> owner.multiplicity().max().isPresent()
                            && owner.multiplicity().max().getAsInt() <= 1)
                    .map(owner -> sanitize(owner.target()))
                    .forEach(singleValuedRoles::add);
        }
        return new ProcessPlan(process, groupType, roles, Set.copyOf(singleValuedRoles),
                List.copyOf(allSequenceFlows(process)));
    }

    private static void emitOperationContract(ProcessPlan plan, FlowElement element, AclModel acl,
                                              StringBuilder out) {
        String contextRole = plan.role(element);
        if (contextRole == null) return;
        String label = "BPMN_" + sanitize(plan.process().id()) + "_" + sanitize(element.id());
        String domainPre = oclBodyOf(element, ActivityConstraint.Kind.PRE);
        String domainPost = oclBodyOf(element, ActivityConstraint.Kind.POST);
        out.append("context ").append(contextRole).append("::")
                .append(operationName(element.id())).append("()\n");
        if (domainPre != null) {
            out.append("  pre ").append(label).append("_DomainPre:\n    ")
                    .append(rewriteGroupScoped(acl, contextRole, plan.groupType(), domainPre))
                    .append("\n");
        }
        if (domainPost != null) {
            out.append("  post ").append(label).append("_DomainPost:\n    ")
                    .append(rewriteGroupScoped(acl, contextRole, plan.groupType(), domainPost))
                    .append("\n");
        }
        if (domainPre == null && domainPost == null) {
            out.append("  pre ").append(label).append("_Enabled:\n    true\n");
        }
        out.append("\n");
    }

    private static void emitTemporalPrecedence(ProcessPlan plan, StringBuilder out,
                                               List<String> diagnostics) {
        Map<FlowElement, List<SequenceFlow>> incomingByTarget = new LinkedHashMap<>();
        for (SequenceFlow flow : plan.flows()) {
            incomingByTarget.computeIfAbsent(flow.target(), ignored -> new ArrayList<>()).add(flow);
        }
        for (var entry : incomingByTarget.entrySet()) {
            FlowElement target = entry.getKey();
            String targetRole = plan.role(target);
            List<SequenceFlow> incoming = entry.getValue().stream()
                    .filter(flow -> plan.role(flow.source()) != null)
                    .toList();
            if (targetRole == null || incoming.isEmpty()) continue;

            String invariant = "BPMN_" + sanitize(plan.process().id())
                    + "_before_" + sanitize(target.id());

            List<String> predecessorCalls = new ArrayList<>();
            List<String> unsupported = new ArrayList<>();
            for (SequenceFlow flow : incoming) {
                String invocation = predecessorInvocation(plan, target, flow.source());
                if (invocation == null) unsupported.add(flow.source().id());
                else predecessorCalls.add("sometimePast (" + invocation + ")");
            }
            boolean andJoin = target instanceof Gateway gateway
                    && gateway.kind() == GatewayKind.AND && incoming.size() > 1;
            if (!unsupported.isEmpty() && (andJoin || predecessorCalls.isEmpty())) {
                diagnostics.add("Warning: TOCL precedence for target '" + target.id()
                        + "' was omitted: predecessor Role has multiple occurrences or no shared Group ("
                        + String.join(", ", unsupported) + ").");
                continue;
            }
            if (!unsupported.isEmpty()) {
                diagnostics.add("Warning: TOCL precedence for target '" + target.id()
                        + "' omits unsupported alternative predecessor(s): "
                        + String.join(", ", unsupported) + ".");
            }

            String connector = andJoin ? "\n      and " : "\n      or ";
            out.append("-- Incoming sequence flow(s) of ").append(target.id()).append("\n")
                    .append("context ").append(targetRole).append("\n")
                    .append("inv ").append(invariant).append(":\n")
                    .append("  always (\n")
                    .append("    isCalled(").append(operationName(target.id())).append("())\n")
                    .append("    implies\n")
                    .append("    (\n      ").append(String.join(connector, predecessorCalls)).append("\n    )\n")
                    .append("  )\n\n");
        }
    }

    /**
     * A precondition only constrains a Start operation when that operation is
     * present in the filmstrip.  Without an explicit liveness property, a
     * model containing no process execution satisfies every precedence rule
     * vacuously.  Require every Start event of the process to occur at least
     * once so an empty execution is not a valid BPMN interpretation.
     */
    private static void emitRequiredStartOccurrences(ProcessPlan plan, StringBuilder out) {
        for (FlowElement element : allFlowElements(plan.process())) {
            if (!(element instanceof StartEvent)) continue;
            String role = plan.role(element);
            if (role == null) continue;

            out.append("-- Required occurrence of process Start event ")
                    .append(element.id()).append("\n")
                    .append("context ").append(role).append("\n")
                    .append("inv BPMN_").append(sanitize(plan.process().id()))
                    .append("_start_").append(sanitize(element.id())).append("_occurs:\n")
                    .append("  sometime isCalled(")
                    .append(operationName(element.id())).append("())\n\n");
        }
    }

    /**
     * Uses only the receiver forms supported by the existing TOCL translator.
     * Same-lane calls are unqualified. A cross-lane predecessor is selected by
     * Group identity and is only sound when that source Role is single-valued.
     */
    private static String predecessorInvocation(ProcessPlan plan, FlowElement target,
                                                FlowElement source) {
        String targetRole = plan.role(target);
        String sourceRole = plan.role(source);
        if (sourceRole.equals(targetRole)) {
            return "isCalled(" + operationName(source.id()) + "())";
        }
        if (!plan.boundToGroup() || !plan.singleValuedRoles().contains(sourceRole)) return null;

        String candidate = lowerFirst(sourceRole) + "Candidate";
        String groupNav = lowerFirst(plan.groupType());
        String receiver = sourceRole + ".allInstances()"
                + "->select(" + candidate + " : " + sourceRole + " | "
                + candidate + "." + groupNav + ".id = self." + groupNav + ".id)"
                // The original TOCL type checker records the last postfix type
                // visited inside a receiver. A second identity select keeps the
                // typed candidate last, resolving the owner to sourceRole.
                + "->select(" + candidate + " : " + sourceRole + " | "
                + candidate + " = " + candidate + ")"
                + "->any(true)";
        return "isCalled(" + receiver + "." + operationName(source.id()) + "())";
    }

    private static String rewriteGroupScoped(AclModel acl, String contextRole,
                                             String groupType, String source) {
        String qualified = source;
        if (groupType != null && !groupType.isBlank()) {
            qualified = SELF.matcher(source).replaceAll(
                    Matcher.quoteReplacement("self." + lowerFirst(groupType)));
        }
        return AclOclPropertyResolver.rewrite(acl, contextRole, qualified);
    }

    private static String flowGuardOperation(SequenceFlow flow) {
        return operationName(flow.source().id() + "_to_" + flow.target().id() + "_guard");
    }

    private static Lane laneOf(Process process, FlowElement element) {
        return process.lanes().stream()
                .filter(lane -> lane.flowElements().contains(element))
                .findFirst().orElse(null);
    }

    private static List<FlowElement> allFlowElements(Process process) {
        List<FlowElement> result = new ArrayList<>();
        collectFlowElements(process.flowElements(), result);
        return result;
    }

    private static void collectFlowElements(List<FlowElement> elements, List<FlowElement> out) {
        for (FlowElement element : elements) {
            out.add(element);
            if (element instanceof SubProcess subProcess) {
                collectFlowElements(subProcess.flowElements(), out);
            }
        }
    }

    private static List<SequenceFlow> allSequenceFlows(Process process) {
        List<SequenceFlow> result = new ArrayList<>(process.sequenceFlows());
        collectNestedFlows(process.flowElements(), result);
        return result;
    }

    private static void collectNestedFlows(List<FlowElement> elements, List<SequenceFlow> out) {
        for (FlowElement element : elements) {
            if (element instanceof SubProcess subProcess) {
                out.addAll(subProcess.sequenceFlows());
                collectNestedFlows(subProcess.flowElements(), out);
            }
        }
    }

    private static String oclBodyOf(FlowElement element, ActivityConstraint.Kind kind) {
        return element.constraints().stream()
                .filter(constraint -> constraint.kind() == kind)
                .map(ActivityConstraint::oclBody)
                .reduce((left, right) -> "(" + left + ") and (" + right + ")")
                .orElse(null);
    }

    /** Convert arbitrary name to a valid USE identifier. */
    static String sanitize(String name) {
        if (name == null || name.isBlank()) return "unnamed";
        String clean = name.replaceAll("[^A-Za-z0-9_]", "_");
        return Character.isDigit(clean.charAt(0)) ? "_" + clean : clean;
    }

    private static String lowerFirst(String value) {
        String clean = sanitize(value);
        return Character.toLowerCase(clean.charAt(0)) + clean.substring(1);
    }

    private static String operationName(String name) {
        String clean = sanitize(name);
        return USE_KEYWORDS.contains(clean.toLowerCase()) ? "_" + clean : clean;
    }
}
