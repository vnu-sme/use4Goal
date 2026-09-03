package org.vnu.sme.goal.dsl.dcr.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.dsl.dcr.ast.DcrModelCS;
import org.vnu.sme.goal.dsl.dcr.ast.EventCS;
import org.vnu.sme.goal.dsl.dcr.ast.MarkingCS;
import org.vnu.sme.goal.dsl.dcr.ast.MarkingFlagCS;
import org.vnu.sme.goal.dsl.dcr.ast.MarkingItemCS;
import org.vnu.sme.goal.dsl.dcr.ast.RelationCS;
import org.vnu.sme.goal.dsl.dcr.ast.RelationKindCS;
import org.vnu.sme.goal.dsl.dcr.mm.DcrMarking;

public final class DcrBuildingVisitor extends DCRBaseVisitor<Object> {

    public static DcrModelCS build(DCRParser.ModelContext ctx) {
        DcrBuildingVisitor visitor = new DcrBuildingVisitor();
        List<EventCS> events = new ArrayList<>();
        List<MarkingCS> markings = new ArrayList<>();
        List<RelationCS> relations = new ArrayList<>();

        for (DCRParser.StatementContext stmt : ctx.statement()) {
            Object item = visitor.visit(stmt);
            if (item instanceof EventCS event) {
                events.add(event);
            } else if (item instanceof MarkingCS marking) {
                markings.add(marking);
            } else if (item instanceof RelationCS relation) {
                relations.add(relation);
            }
        }
        return new DcrModelCS(ctx.IDENT().getText(), events, markings, relations);
    }

    @Override
    public Object visitEventStmt(DCRParser.EventStmtContext ctx) {
        String label = ctx.STRING() == null ? ctx.IDENT().getText() : unquote(ctx.STRING().getText());
        return new EventCS(ctx.IDENT().getText(), label);
    }

    @Override
    public Object visitMarkingStmt(DCRParser.MarkingStmtContext ctx) {
        List<MarkingItemCS> items = ctx.markItem().stream()
                .map(item -> (MarkingItemCS) visit(item))
                .toList();
        return new MarkingCS(ctx.IDENT().getText(), items);
    }

    @Override
    public Object visitRelationStmt(DCRParser.RelationStmtContext ctx) {
        List<TerminalNode> ids = ctx.IDENT();
        Integer time = ctx.relTime() == null ? null : (Integer) visit(ctx.relTime());
        return new RelationCS((RelationKindCS) visit(ctx.relKind()),
                ids.get(0).getText(), ids.get(1).getText(), time);
    }

    @Override
    public Object visitMarkExecuted(DCRParser.MarkExecutedContext ctx) {
        Integer age = ctx.INT() == null ? null : Integer.valueOf(ctx.INT().getText());
        return new MarkingItemCS(MarkingFlagCS.EXECUTED, age);
    }

    @Override public Object visitMarkIncluded(DCRParser.MarkIncludedContext ctx) {
        return new MarkingItemCS(MarkingFlagCS.INCLUDED, null);
    }

    @Override
    public Object visitMarkPending(DCRParser.MarkPendingContext ctx) {
        Integer deadline = ctx.deadline() == null ? null : parseDeadline(ctx.deadline());
        return new MarkingItemCS(MarkingFlagCS.PENDING, deadline);
    }

    @Override public Object visitRelCondition(DCRParser.RelConditionContext ctx) { return RelationKindCS.CONDITION; }
    @Override public Object visitRelResponse(DCRParser.RelResponseContext ctx) { return RelationKindCS.RESPONSE; }
    @Override public Object visitRelInclude(DCRParser.RelIncludeContext ctx) { return RelationKindCS.INCLUDE; }
    @Override public Object visitRelExclude(DCRParser.RelExcludeContext ctx) { return RelationKindCS.EXCLUDE; }
    @Override public Object visitRelMilestone(DCRParser.RelMilestoneContext ctx) { return RelationKindCS.MILESTONE; }
    @Override public Object visitConditionTime(DCRParser.ConditionTimeContext ctx) { return Integer.valueOf(ctx.INT().getText()); }
    @Override public Object visitResponseTime(DCRParser.ResponseTimeContext ctx) { return parseDeadline(ctx.deadline()); }

    private static Integer parseDeadline(DCRParser.DeadlineContext ctx) {
        if (ctx.INT() != null) {
            return Integer.valueOf(ctx.INT().getText());
        }
        return DcrMarking.OMEGA;
    }

    private static String unquote(String text) {
        String s = text.substring(1, text.length() - 1);
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
