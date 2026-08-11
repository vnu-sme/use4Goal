package org.vnu.sme.goal.dsl.bpmnscenario.mm;

import java.util.List;

/** Runtime model for a BPMN scenario, before/after semantic validation against a .bpmn2 model. */
public final class BpmnScenarioModel {

    private final String name;
    private final String modelFile;
    private final List<ScenarioStmt> statements;

    public BpmnScenarioModel(String name, String modelFile, List<ScenarioStmt> statements) {
        this.name = name;
        this.modelFile = modelFile;
        this.statements = List.copyOf(statements);
    }

    public String name() { return name; }
    public String modelFile() { return modelFile; }
    public List<ScenarioStmt> statements() { return statements; }
}
