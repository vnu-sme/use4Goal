package org.vnu.sme.goal.dsl.bpmnscenario.ast;

import java.util.List;

/** Root AST for one BPMN scenario block. */
public final class BpmnScenarioModelCS {

    private final String name;
    private final String modelFile;
    private final List<ScenarioStmtCS> statements;

    public BpmnScenarioModelCS(String name, String modelFile, List<ScenarioStmtCS> statements) {
        this.name = name;
        this.modelFile = modelFile;
        this.statements = List.copyOf(statements);
    }

    public String name() { return name; }
    public String modelFile() { return modelFile; }
    public List<ScenarioStmtCS> statements() { return statements; }
}
