package org.vnu.sme.goal.istarscenario.ast;

import java.util.List;

/** Root AST — one {@code scenario} block: a name, the target .istar file, instance declarations, and its body. */
public final class IStarScenarioModelCS {

    private final String               name;
    private final String               modelFile;
    private final List<InstanceDeclCS> instances;
    private final List<ScenarioStmtCS> statements;

    public IStarScenarioModelCS(String name, String modelFile,
                                 List<InstanceDeclCS> instances, List<ScenarioStmtCS> statements) {
        this.name       = name;
        this.modelFile  = modelFile;
        this.instances  = List.copyOf(instances);
        this.statements = List.copyOf(statements);
    }

    public String               name()       { return name; }
    public String               modelFile()  { return modelFile; }
    public List<InstanceDeclCS> instances()  { return instances; }
    public List<ScenarioStmtCS> statements() { return statements; }
}
