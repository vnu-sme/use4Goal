package org.vnu.sme.goal.istarscenario.parser;

import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.conformance.semantics.IStarMarking;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istarscenario.mm.AggregateResult;

public record IStarScenarioEvaluation(GoalModel instanceModel,
                                      IStarMarking instanceMarking,
                                      List<AggregateResult> aggregates,
                                      Map<String, String> actorLabels,
                                      Map<String, String> nodeLabels,
                                      Map<String, String> elementScopeActorType) {
    public IStarScenarioEvaluation {
        aggregates = List.copyOf(aggregates);
        actorLabels = Map.copyOf(actorLabels);
        nodeLabels = Map.copyOf(nodeLabels);
        elementScopeActorType = Map.copyOf(elementScopeActorType);
    }
}
