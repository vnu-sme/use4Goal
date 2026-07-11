package org.vnu.sme.goal.bpmn2.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.bpmn2.ast.*;

/**
 * Walks the ANTLR parse tree and builds the BPMN 2.0 AST (CS layer).
 * Use {@link Bpmn2ModelFactory} to convert the resulting AST to the MM layer.
 */
public final class Bpmn2BuildingVisitor extends Bpmn2BaseVisitor<Object> {

    private Bpmn2ModelCS model;

    public static Bpmn2ModelCS build(Bpmn2Parser.ModelContext ctx) {
        Bpmn2BuildingVisitor v = new Bpmn2BuildingVisitor();
        v.visitModel(ctx);
        return v.model;
    }

    // ── Root ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitModel(Bpmn2Parser.ModelContext ctx) {
        List<ProcessCS> processes = ctx.pool().stream()
                .map(p -> (ProcessCS) visitPool(p))
                .collect(Collectors.toList());

        List<MessageCS> messages = ctx.message().stream()
                .map(m -> (MessageCS) visitMessage(m))
                .collect(Collectors.toList());

        List<MessageFlowCS> msgFlows = ctx.messageFlow().stream()
                .map(mf -> (MessageFlowCS) visitMessageFlow(mf))
                .collect(Collectors.toList());

        model = new Bpmn2ModelCS(ctx.IDENT().getText(), processes, messages, msgFlows);
        return model;
    }

    // ── Process (pool) ──────────────────────────────────────────────────────

    @Override
    public Object visitPool(Bpmn2Parser.PoolContext ctx) {
        String id   = ctx.IDENT().getText();
        String name = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;

        List<LaneCS> lanes = ctx.lane().stream()
                .map(l -> (LaneCS) visitLane(l))
                .collect(Collectors.toList());
        List<FlowElementCS> elements = ctx.poolElement().stream()
                .map(e -> (FlowElementCS) visit(e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SequenceFlowCS> flows = ctx.sequenceFlow().stream()
                .map(sf -> (SequenceFlowCS) visitSequenceFlow(sf))
                .collect(Collectors.toList());

        return new ProcessCS(id, name, lanes, elements, flows);
    }

    // ── Lane ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitLane(Bpmn2Parser.LaneContext ctx) {
        String id   = ctx.IDENT().getText();
        String name = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;

        List<FlowElementCS> elements = ctx.poolElement().stream()
                .map(e -> (FlowElementCS) visit(e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new LaneCS(id, name, elements);
    }

    // ── Pool elements (flow elements) ────────────────────────────────────────

    @Override
    public Object visitElemStart(Bpmn2Parser.ElemStartContext ctx) {
        String trigger = ctx.eventType() != null ? ctx.eventType().getText() : "none";
        return new FlowElementCS.StartEventCS(ctx.IDENT().getText(), trigger);
    }

    @Override
    public Object visitElemEnd(Bpmn2Parser.ElemEndContext ctx) {
        String trigger = ctx.eventType() != null ? ctx.eventType().getText() : "none";
        return new FlowElementCS.EndEventCS(ctx.IDENT().getText(), trigger);
    }

    @Override
    public Object visitElemIntermediate(Bpmn2Parser.ElemIntermediateContext ctx) {
        String trigger   = ctx.eventType() != null ? ctx.eventType().getText() : "none";
        String direction = ctx.eventDir() != null ? ctx.eventDir().getText() : "catching";
        return new FlowElementCS.IntermediateEventCS(ctx.IDENT().getText(), trigger, direction);
    }

    @Override
    public Object visitElemTask(Bpmn2Parser.ElemTaskContext ctx) {
        String id   = ctx.IDENT().getText();
        String name = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;
        return new FlowElementCS.TaskCS(id, name);
    }

    @Override
    public Object visitElemCallActivity(Bpmn2Parser.ElemCallActivityContext ctx) {
        return new FlowElementCS.CallActivityCS(ctx.IDENT().getText());
    }

    @Override
    public Object visitElemSubProcess(Bpmn2Parser.ElemSubProcessContext ctx) {
        String id   = ctx.IDENT().getText();
        String name = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;

        List<FlowElementCS> children = ctx.poolElement().stream()
                .map(e -> (FlowElementCS) visit(e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SequenceFlowCS> flows = ctx.sequenceFlow().stream()
                .map(sf -> (SequenceFlowCS) visitSequenceFlow(sf))
                .collect(Collectors.toList());
        return new FlowElementCS.SubProcessCS(id, name, children, flows);
    }

    @Override
    public Object visitElemGateway(Bpmn2Parser.ElemGatewayContext ctx) {
        return new FlowElementCS.GatewayCS(ctx.IDENT().getText(), ctx.gwType().getText());
    }

    // ── Flows / messages ──────────────────────────────────────────────────────

    @Override
    public Object visitSequenceFlow(Bpmn2Parser.SequenceFlowContext ctx) {
        List<org.antlr.v4.runtime.tree.TerminalNode> ids = ctx.IDENT();
        String label = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;
        return new SequenceFlowCS(ids.get(0).getText(), ids.get(1).getText(), label);
    }

    @Override
    public Object visitMessage(Bpmn2Parser.MessageContext ctx) {
        String id   = ctx.IDENT().getText();
        String name = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;
        return new MessageCS(id, name);
    }

    @Override
    public Object visitMessageFlow(Bpmn2Parser.MessageFlowContext ctx) {
        List<org.antlr.v4.runtime.tree.TerminalNode> ids = ctx.IDENT();
        String messageId = ids.size() > 2 ? ids.get(2).getText() : null;
        return new MessageFlowCS(ids.get(0).getText(), ids.get(1).getText(), messageId);
    }

    private static String stripQuotes(String s) {
        if (s == null || s.length() < 2) return s;
        return s.substring(1, s.length() - 1);
    }
}
