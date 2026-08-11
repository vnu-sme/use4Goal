package org.vnu.sme.goal.verify.conformance.semantics;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.bpmn.mm.FlowElement;
import org.vnu.sme.goal.dsl.bpmn.mm.StartEvent;
import org.vnu.sme.goal.dsl.bpmn.mm.SubProcess;

/**
 * Token-on-arc marking of a {@link BpmnModel}, restricted to at most one token per
 * arc ("safe net" assumption) — §3.3.2 of doc/paper/conformance-istar-bpmn2.md. An arc id is
 * either:
 * <ul>
 *   <li>{@code "<source>::<target>"} for a real SequenceFlow, or</li>
 *   <li>{@code "INIT::<startEventId>"} for the synthetic arc that seeds each StartEvent.</li>
 * </ul>
 */
public final class BpmnMarking {

    private final Set<String> tokens;

    private BpmnMarking(Set<String> tokens) {
        this.tokens = tokens;
    }

    public static BpmnMarking initial(BpmnModel pm) {
        Set<String> tokens = new LinkedHashSet<>();
        for (org.vnu.sme.goal.dsl.bpmn.mm.Process process : pm.processes()) {
            for (FlowElement n : allElements(process.flowElements())) {
                if (n instanceof StartEvent se) {
                    tokens.add("INIT::" + se.id());
                }
            }
        }
        return new BpmnMarking(tokens);
    }

    public boolean has(String arcId) {
        return tokens.contains(arcId);
    }

    public BpmnMarking moved(Collection<String> consumed, Collection<String> produced) {
        Set<String> next = new LinkedHashSet<>(tokens);
        next.removeAll(consumed);
        next.addAll(produced);
        return new BpmnMarking(next);
    }

    public Set<String> tokens() {
        return Collections.unmodifiableSet(tokens);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BpmnMarking m && tokens.equals(m.tokens);
    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
    }

    @Override
    public String toString() {
        return "BpmnMarking" + tokens;
    }

    private static List<FlowElement> allElements(List<FlowElement> roots) {
        List<FlowElement> result = new java.util.ArrayList<>();
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
}
