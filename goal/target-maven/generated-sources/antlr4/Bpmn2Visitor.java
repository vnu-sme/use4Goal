// Generated from Bpmn2.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.bpmn2.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Bpmn2Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Bpmn2Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(Bpmn2Parser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#pool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPool(Bpmn2Parser.PoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#laneDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaneDecl(Bpmn2Parser.LaneDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#topElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopElement(Bpmn2Parser.TopElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#eventDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventDecl(Bpmn2Parser.EventDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#eventTypeProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventTypeProperty(Bpmn2Parser.EventTypePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#eventKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventKind(Bpmn2Parser.EventKindContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#activityDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityDecl(Bpmn2Parser.ActivityDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#activityTypeProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityTypeProperty(Bpmn2Parser.ActivityTypePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#activityType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityType(Bpmn2Parser.ActivityTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#gatewayDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayDecl(Bpmn2Parser.GatewayDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#gatewayTypeProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayTypeProperty(Bpmn2Parser.GatewayTypePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#gatewayFlow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayFlow(Bpmn2Parser.GatewayFlowContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#gatewayFlowCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGatewayFlowCondition(Bpmn2Parser.GatewayFlowConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#laneProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaneProperty(Bpmn2Parser.LanePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#flowProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlowProperty(Bpmn2Parser.FlowPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#message}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessage(Bpmn2Parser.MessageContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#messageFlow}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageFlow(Bpmn2Parser.MessageFlowContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#nameProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameProperty(Bpmn2Parser.NamePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#triggerProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTriggerProperty(Bpmn2Parser.TriggerPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#directionProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectionProperty(Bpmn2Parser.DirectionPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#messageProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMessageProperty(Bpmn2Parser.MessagePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#preProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreProperty(Bpmn2Parser.PrePropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#postProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostProperty(Bpmn2Parser.PostPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#effectProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffectProperty(Bpmn2Parser.EffectPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#stateClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStateClause(Bpmn2Parser.StateClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#eventType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventType(Bpmn2Parser.EventTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#eventDir}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventDir(Bpmn2Parser.EventDirContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2Parser#gwType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGwType(Bpmn2Parser.GwTypeContext ctx);
}