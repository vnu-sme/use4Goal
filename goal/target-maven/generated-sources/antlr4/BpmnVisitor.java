// Generated from Bpmn.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.bpmn.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BpmnParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BpmnVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BpmnParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(BpmnParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#pool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPool(BpmnParser.PoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#laneDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaneDecl(BpmnParser.LaneDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#topElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopElement(BpmnParser.TopElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#startDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStartDecl(BpmnParser.StartDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#endDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndDecl(BpmnParser.EndDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#eventDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventDecl(BpmnParser.EventDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#activityDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityDecl(BpmnParser.ActivityDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#activityTypeProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityTypeProperty(BpmnParser.ActivityTypePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#activityType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityType(BpmnParser.ActivityTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#gatewayDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayDecl(BpmnParser.GatewayDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#gatewayTypeProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayTypeProperty(BpmnParser.GatewayTypePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#gatewayFlow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayFlow(BpmnParser.GatewayFlowContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#gatewayFlowCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayFlowCondition(BpmnParser.GatewayFlowConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#laneProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaneProperty(BpmnParser.LanePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#flowProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlowProperty(BpmnParser.FlowPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessage(BpmnParser.MessageContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#messageFlow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageFlow(BpmnParser.MessageFlowContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#nameProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameProperty(BpmnParser.NamePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#triggerProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTriggerProperty(BpmnParser.TriggerPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#directionProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectionProperty(BpmnParser.DirectionPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#messageProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageProperty(BpmnParser.MessagePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#preProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreProperty(BpmnParser.PrePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#postProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostProperty(BpmnParser.PostPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#stateClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateClause(BpmnParser.StateClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#eventType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventType(BpmnParser.EventTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#eventDir}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventDir(BpmnParser.EventDirContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnParser#gwType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGwType(BpmnParser.GwTypeContext ctx);
}