package org.vnu.sme.goal.istarscenario.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;

import org.vnu.sme.goal.istarscenario.ast.InstanceDeclCS;
import org.vnu.sme.goal.istarscenario.ast.IStarScenarioModelCS;
import org.vnu.sme.goal.istarscenario.ast.QualifiedIdCS;
import org.vnu.sme.goal.istarscenario.ast.ScenarioStmtCS;

/**
 * Walks the ANTLR parse tree and builds the i* Scenario AST (CS layer).
 * Use {@link IStarScenarioModelFactory} to convert the resulting AST to the MM layer.
 */
public final class IStarScenarioBuildingVisitor extends IStarScenarioBaseVisitor<Object> {

    public static IStarScenarioModelCS build(IStarScenarioParser.ScenarioContext ctx) {
        String name = ctx.IDENT().getText();
        String modelFile = unquote(ctx.STRING().getText());

        List<InstanceDeclCS> instances = ctx.instanceDecl().stream()
                .map(IStarScenarioBuildingVisitor::buildInstanceDecl)
                .collect(Collectors.toList());

        List<ScenarioStmtCS> statements = new ArrayList<>();
        for (IStarScenarioParser.StmtContext s : ctx.stmt()) {
            statements.add(buildStmt(s));
        }

        return new IStarScenarioModelCS(name, modelFile, instances, statements);
    }

    private static InstanceDeclCS buildInstanceDecl(IStarScenarioParser.InstanceDeclContext ctx) {
        List<TerminalNode> idents = ctx.IDENT();
        List<String> names = idents.subList(0, idents.size() - 1).stream()
                .map(TerminalNode::getText).collect(Collectors.toList());
        String actorType = idents.get(idents.size() - 1).getText();
        return new InstanceDeclCS(names, actorType);
    }

    private static ScenarioStmtCS buildStmt(IStarScenarioParser.StmtContext ctx) {
        return switch (ctx) {
            case IStarScenarioParser.StmtFireContext s -> buildFire(s.fireStmt());
            case IStarScenarioParser.StmtAssignContext s -> buildAssign(s.assignStmt());
            case IStarScenarioParser.StmtAggregateContext s -> buildAggregate(s.aggregateStmt());
            default -> throw new IllegalStateException("Unknown stmt: " + ctx.getText());
        };
    }

    private static ScenarioStmtCS.FireCS buildFire(IStarScenarioParser.FireStmtContext ctx) {
        String objectInstanceId = ctx.IDENT() == null ? null : ctx.IDENT().getText();
        return new ScenarioStmtCS.FireCS(buildQualifiedId(ctx.qualifiedId()), objectInstanceId);
    }

    private static ScenarioStmtCS.AssignCS buildAssign(IStarScenarioParser.AssignStmtContext ctx) {
        return new ScenarioStmtCS.AssignCS(buildQualifiedId(ctx.qualifiedId()), ctx.statusValue().getText());
    }

    private static QualifiedIdCS buildQualifiedId(IStarScenarioParser.QualifiedIdContext ctx) {
        List<TerminalNode> idents = ctx.IDENT();
        return idents.size() == 2
                ? new QualifiedIdCS(idents.get(0).getText(), idents.get(1).getText())
                : new QualifiedIdCS(null, idents.get(0).getText());
    }

    private static ScenarioStmtCS.AggregateCS buildAggregate(IStarScenarioParser.AggregateStmtContext ctx) {
        List<TerminalNode> idents = ctx.IDENT();
        String label = idents.get(0).getText();
        String elementId = idents.get(idents.size() - 1).getText();
        String actorType = idents.size() == 3 ? idents.get(1).getText() : null;
        return new ScenarioStmtCS.AggregateCS(label, ctx.aggMode().getText(), actorType, elementId);
    }

    private static String unquote(String raw) {
        return raw.substring(1, raw.length() - 1);
    }
}
