package org.vnu.sme.goal.bpmn2scenario.mm;

import java.util.List;

/** Statements in the BPMN scenario runtime model. */
public sealed interface ScenarioStmt permits
        ScenarioStmt.ProcessDecl,
        ScenarioStmt.ActorDecl,
        ScenarioStmt.Bind,
        ScenarioStmt.Fire,
        ScenarioStmt.Completed,
        ScenarioStmt.Active,
        ScenarioStmt.Token,
        ScenarioStmt.ValueStmt,
        ScenarioStmt.Assert {

    record ProcessDecl(String instanceId, String processId) implements ScenarioStmt {}

    record ActorDecl(List<String> names, String actorType) implements ScenarioStmt {
        public ActorDecl {
            names = List.copyOf(names);
        }
    }

    record Bind(String target, Value value) implements ScenarioStmt {}

    record Fire(NodeOccurrence occurrence) implements ScenarioStmt {}

    record Completed(NodeOccurrence occurrence) implements ScenarioStmt {}

    record Active(NodeOccurrence occurrence) implements ScenarioStmt {}

    record Token(TokenMark mark) implements ScenarioStmt {}

    record ValueStmt(String target, Value value) implements ScenarioStmt {}

    record Assert(String expression) implements ScenarioStmt {}
}
