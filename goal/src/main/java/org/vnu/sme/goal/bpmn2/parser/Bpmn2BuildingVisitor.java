package org.vnu.sme.goal.bpmn2.parser;

import java.util.*;
import java.util.stream.Collectors;

import org.vnu.sme.goal.bpmn2.ast.*;

/**
 * Walks the ANTLR parse tree and builds the BPMN 2.0 AST (CS layer).
 * Use {@link Bpmn2ModelFactory} to convert the resulting AST to the MM layer.
 */
public final class Bpmn2BuildingVisitor extends Bpmn2BaseVisitor<Object> {

    private Bpmn2CollaborationCS collab;

    public static Bpmn2CollaborationCS build(Bpmn2Parser.CollaborationContext ctx) {
        Bpmn2BuildingVisitor v = new Bpmn2BuildingVisitor();
        v.visitCollaboration(ctx);
        return v.collab;
    }

    // ── Root ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitCollaboration(Bpmn2Parser.CollaborationContext ctx) {
        List<PoolCS> pools = ctx.pool().stream()
                .map(p -> (PoolCS) visitPool(p))
                .collect(Collectors.toList());

        List<MessageFlowCS> msgFlows = ctx.messageFlow().stream()
                .map(mf -> (MessageFlowCS) visitMessageFlow(mf))
                .collect(Collectors.toList());

        collab = new Bpmn2CollaborationCS(ctx.IDENT().getText(), pools, msgFlows);
        return collab;
    }

    // ── Pool ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitPool(Bpmn2Parser.PoolContext ctx) {
        String id    = ctx.IDENT().getText();
        String label = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : id;

        List<LaneCS>         lanes    = ctx.lane().stream()
                .map(l -> (LaneCS) visitLane(l))
                .collect(Collectors.toList());
        List<FlowNodeCS>     elements = ctx.poolElement().stream()
                .map(e -> (FlowNodeCS) visit(e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SequenceFlowCS> flows    = ctx.sequenceFlow().stream()
                .map(sf -> (SequenceFlowCS) visitSequenceFlow(sf))
                .collect(Collectors.toList());

        return new PoolCS(id, label, lanes, elements, flows);
    }

    // ── Lane ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitLane(Bpmn2Parser.LaneContext ctx) {
        String id    = ctx.IDENT().getText();
        String label = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : id;

        List<FlowNodeCS> elements = ctx.poolElement().stream()
                .map(e -> (FlowNodeCS) visit(e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new LaneCS(id, label, elements);
    }

    // ── Pool elements ─────────────────────────────────────────────────────────

    @Override
    public Object visitElemStart(Bpmn2Parser.ElemStartContext ctx) {
        String type = ctx.eventType() != null ? ctx.eventType().getText() : "none";
        return new FlowNodeCS.StartEventCS(ctx.IDENT().getText(), type);
    }

    @Override
    public Object visitElemEnd(Bpmn2Parser.ElemEndContext ctx) {
        String type = ctx.eventType() != null ? ctx.eventType().getText() : "none";
        return new FlowNodeCS.EndEventCS(ctx.IDENT().getText(), type);
    }

    @Override
    public Object visitElemIntermediate(Bpmn2Parser.ElemIntermediateContext ctx) {
        String  type     = ctx.eventType() != null ? ctx.eventType().getText() : "none";
        boolean catching = ctx.eventDir() == null || ctx.eventDir().getText().equals("catching");
        return new FlowNodeCS.IntermediateEventCS(ctx.IDENT().getText(), type, catching);
    }

    @Override
    public Object visitElemTask(Bpmn2Parser.ElemTaskContext ctx) {
        String id    = ctx.IDENT().getText();
        String label = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : id;
        return new FlowNodeCS.TaskCS(id, label);
    }

    @Override
    public Object visitElemSubProcess(Bpmn2Parser.ElemSubProcessContext ctx) {
        String id    = ctx.IDENT().getText();
        String label = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : id;

        List<FlowNodeCS> children = ctx.poolElement().stream()
                .map(e -> (FlowNodeCS) visit(e))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SequenceFlowCS> flows = ctx.sequenceFlow().stream()
                .map(sf -> (SequenceFlowCS) visitSequenceFlow(sf))
                .collect(Collectors.toList());
        return new FlowNodeCS.SubProcessCS(id, label, children, flows);
    }

    @Override
    public Object visitElemGateway(Bpmn2Parser.ElemGatewayContext ctx) {
        return new FlowNodeCS.GatewayCS(ctx.IDENT().getText(), ctx.gwType().getText());
    }

    // ── Flows ─────────────────────────────────────────────────────────────────

    @Override
    public Object visitSequenceFlow(Bpmn2Parser.SequenceFlowContext ctx) {
        List<org.antlr.v4.runtime.tree.TerminalNode> ids = ctx.IDENT();
        String cond = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;
        return new SequenceFlowCS(ids.get(0).getText(), ids.get(1).getText(), cond);
    }

    @Override
    public Object visitMessageFlow(Bpmn2Parser.MessageFlowContext ctx) {
        List<org.antlr.v4.runtime.tree.TerminalNode> ids = ctx.IDENT();
        String label = ctx.STRING() != null ? stripQuotes(ctx.STRING().getText()) : null;
        return new MessageFlowCS(ids.get(0).getText(), ids.get(1).getText(), label);
    }

    private static String stripQuotes(String s) {
        if (s == null || s.length() < 2) return s;
        return s.substring(1, s.length() - 1);
    }
}
