package org.vnu.sme.goal.bpmn2scenario.ast;

/** Concrete-syntax statements for a BPMN scenario. */
public sealed interface ScenarioStmtCS permits
        ScenarioStmtCS.ProcessDeclCS,
        ScenarioStmtCS.ActorDeclCS,
        ScenarioStmtCS.BindCS,
        ScenarioStmtCS.FireCS,
        ScenarioStmtCS.CompletedCS,
        ScenarioStmtCS.ActiveCS,
        ScenarioStmtCS.TokenCS,
        ScenarioStmtCS.ValueCSStmt,
        ScenarioStmtCS.AssertCS {

    record ProcessDeclCS(String instanceId, String processId) implements ScenarioStmtCS {}

    record ActorDeclCS(java.util.List<String> names, String actorType) implements ScenarioStmtCS {
        public ActorDeclCS {
            names = java.util.List.copyOf(names);
        }
    }

    record BindCS(RefCS target, ValueCS value) implements ScenarioStmtCS {}

    record FireCS(QualifiedFlowNodeCS target, String objectId, String actorId) implements ScenarioStmtCS {}

    record CompletedCS(QualifiedFlowNodeCS target, String objectId, String actorId) implements ScenarioStmtCS {}

    record ActiveCS(QualifiedFlowNodeCS target, String objectId, String actorId) implements ScenarioStmtCS {}

    record TokenCS(String processInstanceId, String sourceId, String targetId, String objectId) implements ScenarioStmtCS {}

    record ValueCSStmt(RefCS target, ValueCS value) implements ScenarioStmtCS {}

    record AssertCS(String expression) implements ScenarioStmtCS {}
}
