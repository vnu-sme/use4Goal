package org.vnu.sme.goal.maxgoal.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Interval;
import org.vnu.sme.goal.maxgoal.mm.*;

public final class MAXGoalBuildingVisitor extends MAXGoalBaseVisitor<Object> {

    private MAXGoalModel model;

    public static MAXGoalModel build(MAXGoalParser.ModelContext ctx) {
        MAXGoalBuildingVisitor v = new MAXGoalBuildingVisitor();
        v.visitModel(ctx);
        return v.model;
    }

    // ── Root ────────────────────────────────────────────────────────────────

    @Override
    public Object visitModel(MAXGoalParser.ModelContext ctx) {
        model = new MAXGoalModel(ctx.IDENT().getText());
        for (MAXGoalParser.ActorDefContext a : ctx.actorDef()) {
            model.addActor((Actor) visitActorDef(a));
        }
        if (ctx.dependBlock() != null) {
            visitDependBlock(ctx.dependBlock());
        }
        return model;
    }

    // ── Actor ───────────────────────────────────────────────────────────────

    @Override
    public Object visitActorDef(MAXGoalParser.ActorDefContext ctx) {
        String    actorName = ctx.IDENT().getText();
        ActorKind kind      = ActorKind.from(ctx.actorKind().getText());
        List<Intentional> items = new ArrayList<>();
        for (MAXGoalParser.IntentionalContext ic : ctx.intentional()) {
            Intentional item = (Intentional) visit(ic);
            if (item != null) items.add(item);
        }
        return new Actor(actorName, kind, items);
    }

    @Override
    public Object visitGoalDecl(MAXGoalParser.GoalDeclContext ctx) {
        String actorName = currentActorName(ctx);
        String goalName  = ctx.IDENT().getText();
        String clause    = null;
        String expr      = null;
        RefineSpec refine = null;

        for (MAXGoalParser.GoalAttrContext attr : ctx.goalAttr()) {
            if (attr instanceof MAXGoalParser.GaAchieveContext  ac) { clause = "achieve";  expr = cond(ac.condition()); }
            else if (attr instanceof MAXGoalParser.GaMaintainContext mc) { clause = "maintain"; expr = cond(mc.condition()); }
            else if (attr instanceof MAXGoalParser.GaAvoidContext   av) { clause = "avoid";    expr = cond(av.condition()); }
            else if (attr instanceof MAXGoalParser.GaRefineContext  rf) { refine = (RefineSpec) visit(rf.refineSpec()); }
        }
        return new GoalDef(goalName, actorName, clause, expr, refine);
    }

    @Override
    public Object visitTaskDecl(MAXGoalParser.TaskDeclContext ctx) {
        String actorName = currentActorName(ctx);
        String taskName  = ctx.IDENT().getText();
        String pre = null, post = null, needby = null;
        RefineSpec refine = null;

        for (MAXGoalParser.TaskAttrContext attr : ctx.taskAttr()) {
            if (attr instanceof MAXGoalParser.TaPreContext  p) pre    = cond(p.condition());
            else if (attr instanceof MAXGoalParser.TaPostContext po)  post   = cond(po.condition());
            else if (attr instanceof MAXGoalParser.TaRefineContext rf) refine = (RefineSpec) visit(rf.refineSpec());
            else if (attr instanceof MAXGoalParser.TaNeedbyContext nb) needby = nb.IDENT().getText();
        }
        return new TaskDef(taskName, actorName, pre, post, needby, refine);
    }

    @Override
    public Object visitResourceDecl(MAXGoalParser.ResourceDeclContext ctx) {
        String actorName = currentActorName(ctx);
        String resName   = ctx.IDENT().getText();
        ResKind kind     = ResKind.from(ctx.resourceKind().getText());
        return new ResourceDef(resName, actorName, kind);
    }

    // ── Dependencies ────────────────────────────────────────────────────────

    @Override
    public Object visitDependBlock(MAXGoalParser.DependBlockContext ctx) {
        for (MAXGoalParser.DepStmtContext d : ctx.depStmt()) {
            model.addDependency(new Dependency(
                    d.IDENT(0).getText(),
                    d.IDENT(1).getText()));
        }
        return null;
    }

    // ── Refine specs ─────────────────────────────────────────────────────────

    @Override
    public Object visitSeqRefine(MAXGoalParser.SeqRefineContext ctx) {
        return RefineSpec.SeqRefine.of(identList(ctx.IDENT()));
    }

    @Override
    public Object visitParRefine(MAXGoalParser.ParRefineContext ctx) {
        return new RefineSpec.ParRefine(identList(ctx.IDENT()));
    }

    @Override
    public Object visitXorRefine(MAXGoalParser.XorRefineContext ctx) {
        return new RefineSpec.XorRefine(guardedChildren(ctx.guardedChild()));
    }

    @Override
    public Object visitIorRefine(MAXGoalParser.IorRefineContext ctx) {
        return RefineSpec.IorRefine.of(guardedChildren(ctx.guardedChild()));
    }

    @Override
    public Object visitIterRefine(MAXGoalParser.IterRefineContext ctx) {
        return new RefineSpec.IterRefine(identList(ctx.IDENT()), cond(ctx.condition()));
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private String currentActorName(org.antlr.v4.runtime.ParserRuleContext ctx) {
        org.antlr.v4.runtime.ParserRuleContext p = ctx.getParent();
        while (p != null) {
            if (p instanceof MAXGoalParser.ActorDefContext a) return a.IDENT().getText();
            p = p.getParent();
        }
        return "";
    }

    private List<String> identList(List<? extends org.antlr.v4.runtime.tree.TerminalNode> nodes) {
        return nodes.stream().map(t -> t.getText()).collect(Collectors.toList());
    }

    private List<GuardedChild> guardedChildren(List<MAXGoalParser.GuardedChildContext> list) {
        return list.stream()
                .map(g -> new GuardedChild(cond(g.condition()), g.IDENT().getText()))
                .collect(Collectors.toList());
    }

    private static String cond(MAXGoalParser.ConditionContext ctx) {
        if (ctx == null) return "";
        Interval i = new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
        return ctx.start.getInputStream().getText(i);
    }
}
