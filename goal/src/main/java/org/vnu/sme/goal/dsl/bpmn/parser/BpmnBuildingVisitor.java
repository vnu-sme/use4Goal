package org.vnu.sme.goal.dsl.bpmn.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.dsl.bpmn.ast.*;
// BpmnBaseVisitor/BpmnParser are ANTLR-generated from grammars/Bpmn.g4, whose
// '@header' targets this same package -- no cross-package import needed.

/**
 * Walks the ANTLR parse tree and builds the BPMN 2.0 AST (CS layer).
 * Use {@link BpmnModelFactory} to convert the resulting AST to the MM layer.
 *
 * <p>Every flow element (start/end/event/activity/gateway) is a top-level
 * declaration of the model: {@code visitTopElement} is never overridden
 * here, since the generated default {@code visitChildren} already
 * dispatches to whichever single alternative (visitStartDecl, ...)
 * actually matched and returns its result.
 */
public final class BpmnBuildingVisitor extends BpmnBaseVisitor<Object> {

    private BpmnModelCS model;

    public static BpmnModelCS build(BpmnParser.ModelContext ctx) {
        BpmnBuildingVisitor v = new BpmnBuildingVisitor();
        v.visitModel(ctx);
        return v.model;
    }

    // ── Root ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitModel(BpmnParser.ModelContext ctx) {
        List<ProcessCS> processes = ctx.pool().stream()
                .map(p -> (ProcessCS) visitPool(p))
                .collect(Collectors.toList());

        List<FlowElementCS> elements = ctx.topElement().stream()
                .map(this::visit)
                .filter(Objects::nonNull)
                .map(FlowElementCS.class::cast)
                .collect(Collectors.toList());

        List<MessageCS> messages = ctx.message().stream()
                .map(m -> (MessageCS) visitMessage(m))
                .collect(Collectors.toList());

        List<MessageFlowCS> msgFlows = ctx.messageFlow().stream()
                .map(mf -> (MessageFlowCS) visitMessageFlow(mf))
                .collect(Collectors.toList());

        model = new BpmnModelCS(ctx.IDENT().getText(), processes, elements, messages, msgFlows);
        return model;
    }

    // ── Process (pool) ──────────────────────────────────────────────────────

    @Override
    public Object visitPool(BpmnParser.PoolContext ctx) {
        String id = ctx.IDENT(0).getText();
        String groupClass = ctx.IDENT().size() > 1 ? ctx.IDENT(1).getText() : null;
        String name = name(ctx.nameProperty());

        List<LaneCS> lanes = ctx.laneDecl().stream()
                .map(l -> new LaneCS(l.IDENT().getText()))
                .collect(Collectors.toList());

        return new ProcessCS(id, name, groupClass, lanes);
    }

    // ── start / end / event (intermediate) ──────────────────────────────────

    @Override
    public Object visitStartDecl(BpmnParser.StartDeclContext ctx) {
        String trigger = ctx.triggerProperty().eventType().getText();
        String pre = ctx.preProperty() == null ? null
                : clauseBody(ctx.preProperty().stateClause().getText());
        return new FlowElementCS.StartEventCS(ctx.IDENT().getText(), ctx.laneProperty().IDENT().getText(),
                trigger, pre, ctx.flowProperty().IDENT().getText());
    }

    @Override
    public Object visitEndDecl(BpmnParser.EndDeclContext ctx) {
        String trigger = ctx.triggerProperty().eventType().getText();
        return new FlowElementCS.EndEventCS(ctx.IDENT().getText(), ctx.laneProperty().IDENT().getText(), trigger);
    }

    @Override
    public Object visitEventDecl(BpmnParser.EventDeclContext ctx) {
        String trigger = ctx.triggerProperty().eventType().getText();
        String direction = ctx.directionProperty().eventDir().getText();
        String flow = ctx.flowProperty() == null ? null : ctx.flowProperty().IDENT().getText();
        return new FlowElementCS.IntermediateEventCS(ctx.IDENT().getText(), ctx.laneProperty().IDENT().getText(),
                trigger, direction, flow);
    }

    // ── activity ─────────────────────────────────────────────────────────────

    @Override
    public Object visitActivityDecl(BpmnParser.ActivityDeclContext ctx) {
        String id = ctx.IDENT().getText();
        String name = name(ctx.nameProperty());
        String kind = ctx.activityTypeProperty().activityType().getText();
        String lane = ctx.laneProperty().IDENT().getText();
        String pre = ctx.preProperty() == null ? null : clauseBody(ctx.preProperty().stateClause().getText());
        String post = ctx.postProperty() == null ? null : clauseBody(ctx.postProperty().stateClause().getText());
        String flow = ctx.flowProperty() == null ? null : ctx.flowProperty().IDENT().getText();
        return new FlowElementCS.ActivityCS(id, name, kind, lane, pre, post, flow);
    }

    // ── gateway ──────────────────────────────────────────────────────────────

    @Override
    public Object visitGatewayDecl(BpmnParser.GatewayDeclContext ctx) {
        String id = ctx.IDENT().getText();
        String lane = ctx.laneProperty().IDENT().getText();
        String kind = ctx.gatewayTypeProperty().gwType().getText();
        String pre = ctx.preProperty() == null ? null : clauseBody(ctx.preProperty().stateClause().getText());
        List<GatewayFlowCS> flows = ctx.gatewayFlow().stream()
                .map(this::gatewayFlow)
                .collect(Collectors.toList());
        return new FlowElementCS.GatewayCS(id, lane, kind, pre, flows);
    }

    private GatewayFlowCS gatewayFlow(BpmnParser.GatewayFlowContext ctx) {
        String target = ctx.IDENT().getText();
        BpmnParser.GatewayFlowConditionContext cond = ctx.gatewayFlowCondition();
        if (cond == null) return new GatewayFlowCS(target, null, null, false);
        if (cond.stateClause() != null) {
            String expression = clauseBody(cond.stateClause().getText());
            if ("post".equals(cond.getStart().getText())) {
                return new GatewayFlowCS(target, expression, null, false);
            }
            return new GatewayFlowCS(target, null, expression, false);
        }
        return new GatewayFlowCS(target, null, null, true); // legacy 'default'
    }

    // ── messages ─────────────────────────────────────────────────────────────

    @Override
    public Object visitMessage(BpmnParser.MessageContext ctx) {
        return new MessageCS(ctx.IDENT().getText(), name(ctx.nameProperty()));
    }

    @Override
    public Object visitMessageFlow(BpmnParser.MessageFlowContext ctx) {
        List<org.antlr.v4.runtime.tree.TerminalNode> ids = ctx.IDENT();
        String messageId = ctx.messageProperty() == null ? null : ctx.messageProperty().IDENT().getText();
        return new MessageFlowCS(ids.get(0).getText(), ids.get(1).getText(), messageId);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String stripQuotes(String s) {
        if (s == null || s.length() < 2) return s;
        return s.substring(1, s.length() - 1);
    }

    private static String name(BpmnParser.NamePropertyContext ctx) {
        return ctx == null ? null : stripQuotes(ctx.STRING().getText());
    }

    private static String clauseBody(String raw) {
        int start = raw.indexOf("{[");
        int end = raw.lastIndexOf("]}");
        return start >= 0 && end >= start + 2
                ? raw.substring(start + 2, end).strip()
                : raw;
    }
}
