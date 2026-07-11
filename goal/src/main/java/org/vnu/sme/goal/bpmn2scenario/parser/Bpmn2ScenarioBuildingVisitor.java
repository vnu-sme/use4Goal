package org.vnu.sme.goal.bpmn2scenario.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.bpmn2scenario.ast.Bpmn2ScenarioModelCS;
import org.vnu.sme.goal.bpmn2scenario.ast.QualifiedFlowNodeCS;
import org.vnu.sme.goal.bpmn2scenario.ast.RefCS;
import org.vnu.sme.goal.bpmn2scenario.ast.ScenarioStmtCS;
import org.vnu.sme.goal.bpmn2scenario.ast.ValueCS;

/** Builds the BPMN Scenario AST from the ANTLR parse tree. */
public final class Bpmn2ScenarioBuildingVisitor extends Bpmn2ScenarioBaseVisitor<Object> {

    public static Bpmn2ScenarioModelCS build(Bpmn2ScenarioParser.ScenarioContext ctx) {
        String name = ctx.IDENT().getText();
        String modelFile = unquote(ctx.STRING().getText());

        List<ScenarioStmtCS> statements = new ArrayList<>();
        for (Bpmn2ScenarioParser.StmtContext stmt : ctx.stmt()) {
            statements.add(buildStmt(stmt));
        }
        return new Bpmn2ScenarioModelCS(name, modelFile, statements);
    }

    private static ScenarioStmtCS buildStmt(Bpmn2ScenarioParser.StmtContext ctx) {
        return switch (ctx) {
            case Bpmn2ScenarioParser.StmtProcessContext s -> new ScenarioStmtCS.ProcessDeclCS(
                    s.processDecl().IDENT(0).getText(), s.processDecl().IDENT(1).getText());
            case Bpmn2ScenarioParser.StmtActorContext s -> buildActor(s.actorDecl());
            case Bpmn2ScenarioParser.StmtBindContext s -> new ScenarioStmtCS.BindCS(
                    buildRef(s.bindStmt().ref()), buildValue(s.bindStmt().value()));
            case Bpmn2ScenarioParser.StmtFireContext s -> new ScenarioStmtCS.FireCS(
                    buildQualified(s.fireStmt().qualifiedId()), objectId(s.fireStmt().forClause()), actorId(s.fireStmt().byClause()));
            case Bpmn2ScenarioParser.StmtCompletedContext s -> new ScenarioStmtCS.CompletedCS(
                    buildQualified(s.completedStmt().qualifiedId()), objectId(s.completedStmt().forClause()), actorId(s.completedStmt().byClause()));
            case Bpmn2ScenarioParser.StmtActiveContext s -> new ScenarioStmtCS.ActiveCS(
                    buildQualified(s.activeStmt().qualifiedId()), objectId(s.activeStmt().forClause()), actorId(s.activeStmt().byClause()));
            case Bpmn2ScenarioParser.StmtTokenContext s -> new ScenarioStmtCS.TokenCS(
                    s.tokenStmt().IDENT(0).getText(), s.tokenStmt().IDENT(1).getText(),
                    s.tokenStmt().IDENT(2).getText(), objectId(s.tokenStmt().forClause()));
            case Bpmn2ScenarioParser.StmtValueContext s -> new ScenarioStmtCS.ValueCSStmt(
                    buildRef(s.valueStmt().ref()), buildValue(s.valueStmt().value()));
            case Bpmn2ScenarioParser.StmtAssertContext s -> new ScenarioStmtCS.AssertCS(
                    buildExpr(s.assertStmt().expr()));
            default -> throw new IllegalStateException("Unknown BPMN scenario stmt: " + ctx.getText());
        };
    }

    private static ScenarioStmtCS.ActorDeclCS buildActor(Bpmn2ScenarioParser.ActorDeclContext ctx) {
        List<TerminalNode> idents = ctx.IDENT();
        List<String> names = idents.subList(0, idents.size() - 1).stream()
                .map(TerminalNode::getText)
                .collect(Collectors.toList());
        return new ScenarioStmtCS.ActorDeclCS(names, idents.get(idents.size() - 1).getText());
    }

    private static QualifiedFlowNodeCS buildQualified(Bpmn2ScenarioParser.QualifiedIdContext ctx) {
        return new QualifiedFlowNodeCS(ctx.IDENT(0).getText(), ctx.IDENT(1).getText());
    }

    private static RefCS buildRef(Bpmn2ScenarioParser.RefContext ctx) {
        return new RefCS(ctx.IDENT().stream().map(TerminalNode::getText).collect(Collectors.toList()));
    }

    private static ValueCS buildValue(Bpmn2ScenarioParser.ValueContext ctx) {
        if (ctx.listValue() != null) {
            return new ValueCS.ListCS(ctx.listValue().IDENT().stream()
                    .map(TerminalNode::getText)
                    .collect(Collectors.toList()));
        }
        if (ctx.STRING() != null) return new ValueCS.AtomCS(unquote(ctx.STRING().getText()));
        if (ctx.NUMBER() != null) return new ValueCS.AtomCS(ctx.NUMBER().getText());
        return new ValueCS.AtomCS(ctx.IDENT().getText());
    }

    private static String buildExpr(Bpmn2ScenarioParser.ExprContext ctx) {
        return switch (ctx) {
            case Bpmn2ScenarioParser.CountExprContext c -> "count(" + c.IDENT(0).getText()
                    + " where " + c.IDENT(1).getText() + " = " + c.IDENT(2).getText()
                    + ") " + c.compOp().getText() + " " + c.NUMBER().getText();
            case Bpmn2ScenarioParser.CompareExprContext c -> buildRef(c.ref()).text()
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

    private static String objectId(Bpmn2ScenarioParser.ForClauseContext ctx) {
        return ctx == null ? null : ctx.IDENT().getText();
    }

    private static String actorId(Bpmn2ScenarioParser.ByClauseContext ctx) {
        return ctx == null ? null : ctx.IDENT().getText();
    }

    private static String unquote(String raw) {
        return raw.substring(1, raw.length() - 1);
    }
}
