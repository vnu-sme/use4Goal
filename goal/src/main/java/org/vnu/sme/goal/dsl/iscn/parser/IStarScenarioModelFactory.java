package org.vnu.sme.goal.dsl.iscn.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.vnu.sme.goal.dsl.iscn.ast.IStarScenarioModelCS;
import org.vnu.sme.goal.dsl.iscn.ast.ScenarioStmtCS;
import org.vnu.sme.goal.dsl.iscn.mm.AggregateMode;
import org.vnu.sme.goal.dsl.iscn.mm.IStarScenarioModel;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioInstance;
import org.vnu.sme.goal.dsl.iscn.mm.ScenarioStmt;

/**
 * Converts the i* Scenario AST (CS layer) into the runtime MetaModel (MM layer).
 * Dependency direction: factory -> ast, factory -> mm. Neither ast nor mm knows the other.
 */
public final class IStarScenarioModelFactory {

    private IStarScenarioModelFactory() {}

    public static IStarScenarioModel build(IStarScenarioModelCS cs) {
        // Flatten each 'instance a, b : Type;' declaration into individual objects — the
        // grouping is only how it was written (AST), not a real grouping concept in the MM.
        List<ScenarioInstance> instances = cs.instances().stream()
                .flatMap(i -> i.names().stream().map(name -> new ScenarioInstance(name, i.actorType())))
                .collect(Collectors.toList());

        List<ScenarioStmt> statements = cs.statements().stream()
                .map(IStarScenarioModelFactory::buildStmt)
                .collect(Collectors.toList());

        return new IStarScenarioModel(cs.name(), cs.modelFile(), instances, statements);
    }

    private static ScenarioStmt buildStmt(ScenarioStmtCS cs) {
        return switch (cs) {
            case ScenarioStmtCS.FireCS f ->
                    new ScenarioStmt.Fire(f.target().instanceId(), f.target().elementId(), f.objectInstanceId());
            case ScenarioStmtCS.AssignCS a ->
                    new ScenarioStmt.Assign(a.target().instanceId(), a.target().elementId(), a.statusValue());
            case ScenarioStmtCS.AggregateCS a ->
                    new ScenarioStmt.Aggregate(a.label(), AggregateMode.from(a.mode()), a.actorType(), a.elementId());
        };
    }
}
