package org.vnu.sme.goal.dsl.bpmnscenario.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.vnu.sme.goal.dsl.bpmnscenario.ast.BpmnScenarioModelCS;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.ScenarioStmtCS;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.ValueCS;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.BpmnScenarioModel;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.NodeOccurrence;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.ScenarioStmt;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.TokenMark;
import org.vnu.sme.goal.dsl.bpmnscenario.mm.Value;

/** Converts BPMN Scenario AST objects into the runtime MM. */
public final class BpmnScenarioModelFactory {

    private BpmnScenarioModelFactory() {}

    public static BpmnScenarioModel build(BpmnScenarioModelCS cs) {
        List<ScenarioStmt> statements = cs.statements().stream()
                .map(BpmnScenarioModelFactory::buildStmt)
                .collect(Collectors.toList());
        return new BpmnScenarioModel(cs.name(), cs.modelFile(), statements);
    }

    private static ScenarioStmt buildStmt(ScenarioStmtCS cs) {
        return switch (cs) {
            case ScenarioStmtCS.ProcessDeclCS p -> new ScenarioStmt.ProcessDecl(p.instanceId(), p.processId());
            case ScenarioStmtCS.ActorDeclCS a -> new ScenarioStmt.ActorDecl(a.names(), a.actorType());
            case ScenarioStmtCS.BindCS b -> new ScenarioStmt.Bind(b.target().text(), buildValue(b.value()));
            case ScenarioStmtCS.FireCS f -> new ScenarioStmt.Fire(new NodeOccurrence(
                    f.target().processInstanceId(), f.target().elementId(), f.objectId(), f.actorId()));
            case ScenarioStmtCS.CompletedCS c -> new ScenarioStmt.Completed(new NodeOccurrence(
                    c.target().processInstanceId(), c.target().elementId(), c.objectId(), c.actorId()));
            case ScenarioStmtCS.ActiveCS a -> new ScenarioStmt.Active(new NodeOccurrence(
                    a.target().processInstanceId(), a.target().elementId(), a.objectId(), a.actorId()));
            case ScenarioStmtCS.TokenCS t -> new ScenarioStmt.Token(new TokenMark(
                    t.processInstanceId(), t.sourceId(), t.targetId(), t.objectId()));
            case ScenarioStmtCS.ValueCSStmt v -> new ScenarioStmt.ValueStmt(v.target().text(), buildValue(v.value()));
            case ScenarioStmtCS.AssertCS a -> new ScenarioStmt.Assert(a.expression());
        };
    }

    private static Value buildValue(ValueCS value) {
        return switch (value) {
            case ValueCS.AtomCS a -> new Value.Atom(a.text());
            case ValueCS.ListCS l -> new Value.ListValue(l.items());
        };
    }
}
