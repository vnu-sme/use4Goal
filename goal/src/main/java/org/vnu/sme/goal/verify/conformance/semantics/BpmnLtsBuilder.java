package org.vnu.sme.goal.verify.conformance.semantics;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.Gateway;
import org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind;
import org.vnu.sme.goal.dsl.bpmn.mm.SequenceFlow;
import org.vnu.sme.goal.dsl.bpmn.mm.StartEvent;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;

/**
 * Builds the WF-net-style token semantics for a {@link BpmnModel} described in
 * §3.3.2 of doc/paper/conformance-istar-bpmn2.md. {@code BpmnModel} has no notion
 * of "place" (unlike a Petri net) — arcs of {@link SequenceFlow} play that role directly.
 *
 * <p>Simplifications made explicit here (documented as known MVP limitations):
 * <ul>
 *   <li>{@code Gateway(OR)} (inclusive) is treated like {@code XOR} for both split (one
 *       single-branch firing enumerated, not the full non-empty power set of branches) and
 *       join (simple merge, fires as soon as any one incoming arc has a token) — no tracking
 *       of an "expected merge set".</li>
 *   <li>{@code SubProcess} is treated as an opaque activity; its internal elements/flows are
 *       not expanded.</li>
 *   <li>Message flows (choreography) are out of scope — only sequence flow within pools is
 *       modelled.</li>
 * </ul>
 */
public final class BpmnLtsBuilder {

    private BpmnLtsBuilder() {}

    /**
     * Every Gateway must be classifiable as either a split (at most 1 incoming arc) or a join
     * (at most 1 outgoing arc) — the SESE well-formedness assumption of Gröner Definition 3,
     * required before the token semantics below are meaningful.
     */
    public static void validateWellFormed(BpmnModel pm) {
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : pm.processes()) {
            for (FlowElement n : allElements(process.flowElements())) {
                if (!(n instanceof Gateway gw)) continue;
                int in = incoming(pm, n.id()).size();
                int out = outgoing(pm, n.id()).size();
                boolean split = in <= 1;
                boolean join = out <= 1;
                if (!split && !join) {
                    throw new IllFormedProcessException("gateway '" + gw.id()
                            + "' is neither a split (<=1 incoming) nor a join (<=1 outgoing): in="
                            + in + ", out=" + out);
                }
            }
        }
    }

    public static List<BpmnFiring> enabledFirings(BpmnModel pm, BpmnMarking m) {
        List<BpmnFiring> result = new ArrayList<>();
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : pm.processes()) {
            for (FlowElement n : allElements(process.flowElements())) {
                result.addAll(firingsFor(pm, m, n));
            }
        }
        return result;
    }

    public static BpmnMarking fire(BpmnMarking m, BpmnFiring firing) {
        return m.moved(firing.consumed(), firing.produced());
    }

    // ── internals ────────────────────────────────────────────────────────────

    private static List<BpmnFiring> firingsFor(BpmnModel pm, BpmnMarking m, FlowElement n) {
        List<SequenceFlow> in = incoming(pm, n.id());
        List<SequenceFlow> out = outgoing(pm, n.id());

        if (n instanceof Gateway gw) {
            boolean isSplit = in.size() <= 1;
            return isSplit ? splitFirings(m, n, in, out, gw.kind()) : joinFirings(m, n, in, out, gw.kind());
        }

        // Non-gateway: 1 incoming arc (or the synthetic INIT arc for a StartEvent), fan-out
        // to every outgoing arc (normally exactly one).
        String incomingArc;
        if (n instanceof StartEvent se) {
            incomingArc = "INIT::" + se.id();
        } else if (!in.isEmpty()) {
            incomingArc = arcId(in.get(0));
        } else {
            return List.of();
        }
        if (!m.has(incomingArc)) return List.of();

        Set<String> produced = out.stream().map(BpmnLtsBuilder::arcId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return List.of(new BpmnFiring(n, Set.of(incomingArc), produced));
    }

    private static List<BpmnFiring> splitFirings(BpmnMarking m, FlowElement n, List<SequenceFlow> in,
            List<SequenceFlow> out, GatewayKind type) {
        if (in.size() != 1) return List.of();
        String incomingArc = arcId(in.get(0));
        if (!m.has(incomingArc)) return List.of();
        Set<String> consumed = Set.of(incomingArc);

        if (type == GatewayKind.AND) {
            Set<String> produced = out.stream().map(BpmnLtsBuilder::arcId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            return List.of(new BpmnFiring(n, consumed, produced));
        }
        // XOR / OR / EVENT_BASED (MVP simplification, §3.3.2): one firing per branch.
        List<BpmnFiring> firings = new ArrayList<>();
        for (SequenceFlow f : out) {
            firings.add(new BpmnFiring(n, consumed, Set.of(arcId(f))));
        }
        return firings;
    }

    private static List<BpmnFiring> joinFirings(BpmnMarking m, FlowElement n, List<SequenceFlow> in,
            List<SequenceFlow> out, GatewayKind type) {
        Set<String> produced = out.isEmpty() ? Set.of() : Set.of(arcId(out.get(0)));

        if (type == GatewayKind.AND) {
            Set<String> allIncoming = in.stream().map(BpmnLtsBuilder::arcId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            boolean allPresent = allIncoming.stream().allMatch(m::has);
            return allPresent ? List.of(new BpmnFiring(n, allIncoming, produced)) : List.of();
        }
        // XOR / OR / EVENT_BASED (MVP simplification, §3.3.2): simple merge, fire per ready arc.
        List<BpmnFiring> firings = new ArrayList<>();
        for (SequenceFlow f : in) {
            String arc = arcId(f);
            if (m.has(arc)) firings.add(new BpmnFiring(n, Set.of(arc), produced));
        }
        return firings;
    }

    private static List<SequenceFlow> incoming(BpmnModel pm, String nodeId) {
        return allFlows(pm).stream().filter(f -> f.target().id().equals(nodeId)).collect(Collectors.toList());
    }

    private static List<SequenceFlow> outgoing(BpmnModel pm, String nodeId) {
        return allFlows(pm).stream().filter(f -> f.source().id().equals(nodeId)).collect(Collectors.toList());
    }

    private static List<SequenceFlow> allFlows(BpmnModel pm) {
        List<SequenceFlow> flows = new ArrayList<>();
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : pm.processes()) {
            flows.addAll(process.sequenceFlows());
            collectSubProcessFlows(process.flowElements(), flows);
        }
        return flows;
    }

    private static String arcId(SequenceFlow f) {
        return f.source().id() + "::" + f.target().id();
    }

    private static List<FlowElement> allElements(List<FlowElement> roots) {
        List<FlowElement> result = new ArrayList<>();
        collectElements(roots, result);
        return result;
    }

    private static void collectElements(List<FlowElement> elements, List<FlowElement> result) {
        for (FlowElement element : elements) {
            result.add(element);
            if (element instanceof SubProcess sp) {
                collectElements(sp.flowElements(), result);
            }
        }
    }

    private static void collectSubProcessFlows(List<FlowElement> elements, List<SequenceFlow> result) {
        for (FlowElement element : elements) {
            if (element instanceof SubProcess sp) {
                result.addAll(sp.sequenceFlows());
                collectSubProcessFlows(sp.flowElements(), result);
            }
        }
    }
}
