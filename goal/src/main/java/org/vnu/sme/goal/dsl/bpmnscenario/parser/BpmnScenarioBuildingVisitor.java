package org.vnu.sme.goal.dsl.bpmnscenario.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.BpmnScenarioModelCS;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.QualifiedFlowNodeCS;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.RefCS;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.ScenarioStmtCS;
import org.vnu.sme.goal.dsl.bpmnscenario.ast.ValueCS;
// BpmnScenarioBaseVisitor/BpmnScenarioParser are ANTLR-generated from
// grammars/BpmnScenario.g4, whose '@header' targets this same package --
// no cross-package import needed.

/** Builds the BPMN Scenario AST from the ANTLR parse tree. */
public final class BpmnScenarioBuildingVisitor extends BpmnScenarioBaseVisitor<Object> {

    public static BpmnScenarioModelCS build(BpmnScenarioParser.ScenarioContext ctx) {
        String name = ctx.IDENT().getText();
        String modelFile = unquote(ctx.STRING().getText());

        List<ScenarioStmtCS> statements = new ArrayList<>();
        for (BpmnScenarioParser.StmtContext stmt : ctx.stmt()) {
            statements.add(buildStmt(stmt));
        }
        return new BpmnScenarioModelCS(name, modelFile, statements);
    }

    private static ScenarioStmtCS buildStmt(BpmnScenarioParser.StmtContext ctx) {
        return switch (ctx) {
            case BpmnScenarioParser.StmtProcessContext s -> new ScenarioStmtCS.ProcessDeclCS(
                    s.processDecl().IDENT(0).getText(), s.processDecl().IDENT(1).getText());
            case BpmnScenarioParser.StmtActorContext s -> buildActor(s.actorDecl());
            case BpmnScenarioParser.StmtBindContext s -> new ScenarioStmtCS.BindCS(
                    buildRef(s.bindStmt().ref()), buildValue(s.bindStmt().value()));
            case BpmnScenarioParser.StmtFireContext s -> new ScenarioStmtCS.FireCS(
                    buildQualified(s.fireStmt().qualifiedId()), objectId(s.fireStmt().forClause()), actorId(s.fireStmt().byClause()));
            case BpmnScenarioParser.StmtCompletedContext s -> new ScenarioStmtCS.CompletedCS(
                    buildQualified(s.completedStmt().qualifiedId()), objectId(s.completedStmt().forClause()), actorId(s.completedStmt().byClause()));
            case BpmnScenarioParser.StmtActiveContext s -> new ScenarioStmtCS.ActiveCS(
                    buildQualified(s.activeStmt().qualifiedId()), objectId(s.activeStmt().forClause()), actorId(s.activeStmt().byClause()));
            case BpmnScenarioParser.StmtTokenContext s -> new ScenarioStmtCS.TokenCS(
                    s.tokenStmt().IDENT(0).getText(), s.tokenStmt().IDENT(1).getText(),
                    s.tokenStmt().IDENT(2).getText(), objectId(s.tokenStmt().forClause()));
            case BpmnScenarioParser.StmtValueContext s -> new ScenarioStmtCS.ValueCSStmt(
                    buildRef(s.valueStmt().ref()), buildValue(s.valueStmt().value()));
            case BpmnScenarioParser.StmtAssertContext s -> new ScenarioStmtCS.AssertCS(
                    buildExpr(s.assertStmt().expr()));
            default -> throw new IllegalStateException("Unknown BPMN scenario stmt: " + ctx.getText());
        };
    }

    private static ScenarioStmtCS.ActorDeclCS buildActor(BpmnScenarioParser.ActorDeclContext ctx) {
        List<TerminalNode> idents = ctx.IDENT();
        List<String> names = idents.subList(0, idents.size() - 1).stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        return new ScenarioStmtCS.ActorDeclCS(names, idents.get(idents.size() - 1).getText());
    }

    private static QualifiedFlowNodeCS buildQualified(BpmnScenarioParser.QualifiedIdContext ctx) {
        return new QualifiedFlowNodeCS(ctx.IDENT(0).getText(), ctx.IDENT(1).getText());
    }

    private static RefCS buildRef(BpmnScenarioParser.RefContext ctx) {
        return new RefCS(ctx.IDENT().stream().map(TerminalNode::getText).collect(Collectors.toList()));
    }

    private static ValueCS buildValue(BpmnScenarioParser.ValueContext ctx) {
        if (ctx.listValue() != null) {
            return new ValueCS.ListCS(ctx.listValue().IDENT().stream()
                    .map(TerminalNode::getText)
                    .collect(Collectors.toList()));
        }
        if (ctx.STRING() != null) return new ValueCS.AtomCS(unquote(ctx.STRING().getText()));
        if (ctx.NUMBER() != null) return new ValueCS.AtomCS(ctx.NUMBER().getText());
        return new ValueCS.AtomCS(ctx.IDENT().getText());
    }

    private static String buildExpr(BpmnScenarioParser.ExprContext ctx) {
        return switch (ctx) {
            case BpmnScenarioParser.CountExprContext c -> "count(" + c.IDENT(0).getText()
                    + " where " + c.IDENT(1).getText() + " = " + c.IDENT(2).getText()
                    + ") " + c.compOp().getText() + " " + c.NUMBER().getText();
            case BpmnScenarioParser.CompareExprContext c -> buildRef(c.ref()).text()
                    + " " + c.compOp().getText() + " " + displayValue(buildValue(c.value()));
            default -> ctx.getText();
        };
    }

    private static String displayValue(ValueCS value) {
        return switch (value) {
            case ValueCS.AtomCS a -> a.text();
            case ValueCS.ListCS l -> "[" + String.join(", ", l.items()) + "]";
        };
    }

    private static String objectId(BpmnScenarioParser.ForClauseContext ctx) {
        return ctx == null ? null : ctx.IDENT().getText();
    }

    private static String actorId(BpmnScenarioParser.ByClauseContext ctx) {
        return ctx == null ? null : ctx.IDENT().getText();
    }

    private static String unquote(String raw) {
        return raw.substring(1, raw.length() - 1);
    }
}
