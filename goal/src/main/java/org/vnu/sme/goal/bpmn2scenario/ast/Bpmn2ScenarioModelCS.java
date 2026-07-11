package org.vnu.sme.goal.bpmn2scenario.ast;

import java.util.List;

/** Root AST for one BPMN scenario block. */
public final class Bpmn2ScenarioModelCS {

    private final String name;
    private final String modelFile;
    private final List<ScenarioStmtCS> statements;

    public Bpmn2ScenarioModelCS(String name, String modelFile, List<ScenarioStmtCS> statements) {
        this.name = name;
        this.modelFile = modelFile;
        this.statements = List.copyOf(statements);
    }

    public String name() { return name; }
    public String modelFile() { return modelFile; }
    public List<ScenarioStmtCS> statements() { return statements; }
}
