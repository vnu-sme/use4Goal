package org.vnu.sme.goal.verify.conformance.semantics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.vnu.sme.goal.dsl.bpmn.mm.BpmnModel;
import org.vnu.sme.goal.verify.conformance.mapping.ConformanceMapping;
import org.vnu.sme.goal.dsl.istar.mm.GoalModel;

/**
 * Synchronous product of the i* goal-model LTS and the BPMN2 process LTS — Definition 4.1
 * (Caballero-Villalobos), instantiated with the token semantics of {@link BpmnLtsBuilder}
 * (§3.3.2) and the propagation rules of {@link IStarPropagation} (§3.3.1). This is the
 * "triple transformation" / execution-state machinery that JUCS calls {@code trT}.
 */
public final class ProductLts {

    private final GoalModel gm;
    private final BpmnModel pm;
    private final ConformanceMapping map;

    public ProductLts(GoalModel gm, BpmnModel pm, ConformanceMapping map) {
        this.gm = gm;
        this.pm = pm;
        this.map = map;
    }

    public ProductState initial() {
        return new ProductState(IStarMarking.initial(gm), BpmnMarking.initial(pm));
    }

    /**
     * Rule (1)/(2) of Definition 4.1: every enabled BPMN firing induces exactly one product
     * transition; if the fired node is mapped ({@code map(n) != epsilon}) the i* marking is
     * updated via {@link IStarPropagation#fire}, otherwise it is left unchanged (silent
     * transition).
     */
    public List<Transition> successors(ProductState s) {
        List<Transition> result = new ArrayList<>();
        for (BpmnFiring firing : BpmnLtsBuilder.enabledFirings(pm, s.bpmn())) {
            BpmnMarking nextBpmn = BpmnLtsBuilder.fire(s.bpmn(), firing);
            Optional<String> mapped = map.istarElementOf(firing.node().id());
            IStarMarking nextIstar = mapped
                    .map(e -> IStarPropagation.fire(gm, s.istar(), e))
                    .orElse(s.istar());
            result.add(new Transition(firing.node(), new ProductState(nextIstar, nextBpmn)));
        }
        return result;
    }
}
