package org.vnu.sme.goal.verify.conformance.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.dsl.istar.mm.Actor;
import org.vnu.sme.goal.dsl.istar.mm.AndRefinement;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;
import org.vnu.sme.goal.dsl.istar.mm.OrRefinement;
import org.vnu.sme.goal.dsl.istar.mm.Refinement;

/**
 * Correspondence relation between an i* goal model and a BPMN2 model — the runtime
 * counterpart of the Correspondence Graph (CG) of a Triple Graph Grammar (JUCS §3.4), made
 * concrete here as an explicit, user-authored mapping model (see
 * doc/paper/conformance-istar-bpmn2.md, §3.2 / §4.1).
 */
public final class ConformanceMapping {

    private final List<ElementMapping> elements;
    private final List<ActorMapping> actors;

    private final Map<String, String> istarToBpmn = new LinkedHashMap<>();
    private final Map<String, String> bpmnToIstar = new LinkedHashMap<>();

    public ConformanceMapping(List<ElementMapping> elements, List<ActorMapping> actors) {
        this.elements = List.copyOf(elements);
        this.actors = List.copyOf(actors);
        for (ElementMapping m : this.elements) {
            istarToBpmn.put(m.istarElementId(), m.bpmnNodeId());
            bpmnToIstar.put(m.bpmnNodeId(), m.istarElementId());
        }
    }

    /** Lookup in the direction a mapping author naturally writes: i* element -> BPMN node. */
    public Optional<String> bpmnNodeOf(String istarElementId) {
        return Optional.ofNullable(istarToBpmn.get(istarElementId));
    }

    /** {@code map(n)} as in Definition 3.8: BPMN node -> i* element, empty if silent (map(n) = epsilon). */
    public Optional<String> istarElementOf(String bpmnNodeId) {
        return Optional.ofNullable(bpmnToIstar.get(bpmnNodeId));
    }

    public List<ElementMapping> elements() {
        return elements;
    }

    public List<ActorMapping> actors() {
        return actors;
    }

    /**
     * Structural sanity checks that do not require building any LTS:
     *  - every referenced i* element / BPMN node actually exists;
     *  - map is a function of the BPMN node (no bpmnNodeId mapped twice);
     *  - the mapped i* element is a leaf (no Refinement has it as parent) — only leaf
     *    elements are affected by rule P_leaf (§3.3.1 of the design doc); mapping a
     *    non-leaf element is legal syntax but never fires any propagation rule.
     * Returns a list of human-readable warnings (empty if nothing to report).
     */
    public List<String> validate(GoalModel gm, BpmnModel pm) {
        List<String> warnings = new ArrayList<>();
        Set<String> seenBpmnNodes = new HashSet<>();

        for (ElementMapping m : elements) {
            if (gm.findElement(m.istarElementId()).isEmpty()) {
                warnings.add("map: i* element '" + m.istarElementId() + "' does not exist in the goal model");
            }
            if (pm.findFlowElement(m.bpmnNodeId()).isEmpty()) {
                warnings.add("map: BPMN node '" + m.bpmnNodeId() + "' does not exist in the process model");
            }
            if (isRefinementParent(gm, m.istarElementId())) {
                warnings.add("map: '" + m.istarElementId() + "' is not a leaf (it is the parent of a "
                        + "refinement) — rule P_leaf will never fire for it, this mapping has no effect");
            }
            if (!seenBpmnNodes.add(m.bpmnNodeId())) {
                warnings.add("map: BPMN node '" + m.bpmnNodeId() + "' is mapped more than once (map must be a function)");
            }
        }
        return warnings;
    }

    private static boolean isRefinementParent(GoalModel gm, String elementId) {
        for (Actor actor : gm.getActors()) {
            for (Refinement r : actor.refinements()) {
                String parent = switch (r) {
                    case AndRefinement and -> and.parent();
                    case OrRefinement or -> or.parent();
                };
                if (parent.equals(elementId)) return true;
            }
        }
        return false;
    }
}
