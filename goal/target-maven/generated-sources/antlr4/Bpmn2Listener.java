// Generated from Bpmn2.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.bpmn2.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Bpmn2Parser}.
 */
public interface Bpmn2Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(Bpmn2Parser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(Bpmn2Parser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#pool}.
	 * @param ctx the parse tree
	 */
	void enterPool(Bpmn2Parser.PoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#pool}.
	 * @param ctx the parse tree
	 */
	void exitPool(Bpmn2Parser.PoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#laneDecl}.
	 * @param ctx the parse tree
	 */
	void enterLaneDecl(Bpmn2Parser.LaneDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#laneDecl}.
	 * @param ctx the parse tree
	 */
	void exitLaneDecl(Bpmn2Parser.LaneDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#topElement}.
	 * @param ctx the parse tree
	 */
	void enterTopElement(Bpmn2Parser.TopElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#topElement}.
	 * @param ctx the parse tree
	 */
	void exitTopElement(Bpmn2Parser.TopElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#eventDecl}.
	 * @param ctx the parse tree
	 */
	void enterEventDecl(Bpmn2Parser.EventDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#eventDecl}.
	 * @param ctx the parse tree
	 */
	void exitEventDecl(Bpmn2Parser.EventDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#eventTypeProperty}.
	 * @param ctx the parse tree
	 */
	void enterEventTypeProperty(Bpmn2Parser.EventTypePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#eventTypeProperty}.
	 * @param ctx the parse tree
	 */
	void exitEventTypeProperty(Bpmn2Parser.EventTypePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#eventKind}.
	 * @param ctx the parse tree
	 */
	void enterEventKind(Bpmn2Parser.EventKindContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#eventKind}.
	 * @param ctx the parse tree
	 */
	void exitEventKind(Bpmn2Parser.EventKindContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#activityDecl}.
	 * @param ctx the parse tree
	 */
	void enterActivityDecl(Bpmn2Parser.ActivityDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#activityDecl}.
	 * @param ctx the parse tree
	 */
	void exitActivityDecl(Bpmn2Parser.ActivityDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#activityTypeProperty}.
	 * @param ctx the parse tree
	 */
	void enterActivityTypeProperty(Bpmn2Parser.ActivityTypePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#activityTypeProperty}.
	 * @param ctx the parse tree
	 */
	void exitActivityTypeProperty(Bpmn2Parser.ActivityTypePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#activityType}.
	 * @param ctx the parse tree
	 */
	void enterActivityType(Bpmn2Parser.ActivityTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#activityType}.
	 * @param ctx the parse tree
	 */
	void exitActivityType(Bpmn2Parser.ActivityTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#gatewayDecl}.
	 * @param ctx the parse tree
	 */
	void enterGatewayDecl(Bpmn2Parser.GatewayDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#gatewayDecl}.
	 * @param ctx the parse tree
	 */
	void exitGatewayDecl(Bpmn2Parser.GatewayDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#gatewayTypeProperty}.
	 * @param ctx the parse tree
	 */
	void enterGatewayTypeProperty(Bpmn2Parser.GatewayTypePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#gatewayTypeProperty}.
	 * @param ctx the parse tree
	 */
	void exitGatewayTypeProperty(Bpmn2Parser.GatewayTypePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#gatewayFlow}.
	 * @param ctx the parse tree
	 */
	void enterGatewayFlow(Bpmn2Parser.GatewayFlowContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#gatewayFlow}.
	 * @param ctx the parse tree
	 */
	void exitGatewayFlow(Bpmn2Parser.GatewayFlowContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#gatewayFlowCondition}.
	 * @param ctx the parse tree
	 */
	void enterGatewayFlowCondition(Bpmn2Parser.GatewayFlowConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#gatewayFlowCondition}.
	 * @param ctx the parse tree
	 */
	void exitGatewayFlowCondition(Bpmn2Parser.GatewayFlowConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#laneProperty}.
	 * @param ctx the parse tree
	 */
	void enterLaneProperty(Bpmn2Parser.LanePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#laneProperty}.
	 * @param ctx the parse tree
	 */
	void exitLaneProperty(Bpmn2Parser.LanePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#flowProperty}.
	 * @param ctx the parse tree
	 */
	void enterFlowProperty(Bpmn2Parser.FlowPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#flowProperty}.
	 * @param ctx the parse tree
	 */
	void exitFlowProperty(Bpmn2Parser.FlowPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#message}.
	 * @param ctx the parse tree
	 */
	void enterMessage(Bpmn2Parser.MessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#message}.
	 * @param ctx the parse tree
	 */
	void exitMessage(Bpmn2Parser.MessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#messageFlow}.
	 * @param ctx the parse tree
	 */
	void enterMessageFlow(Bpmn2Parser.MessageFlowContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#messageFlow}.
	 * @param ctx the parse tree
	 */
	void exitMessageFlow(Bpmn2Parser.MessageFlowContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#nameProperty}.
	 * @param ctx the parse tree
	 */
	void enterNameProperty(Bpmn2Parser.NamePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#nameProperty}.
	 * @param ctx the parse tree
	 */
	void exitNameProperty(Bpmn2Parser.NamePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#triggerProperty}.
	 * @param ctx the parse tree
	 */
	void enterTriggerProperty(Bpmn2Parser.TriggerPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#triggerProperty}.
	 * @param ctx the parse tree
	 */
	void exitTriggerProperty(Bpmn2Parser.TriggerPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#directionProperty}.
	 * @param ctx the parse tree
	 */
	void enterDirectionProperty(Bpmn2Parser.DirectionPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#directionProperty}.
	 * @param ctx the parse tree
	 */
	void exitDirectionProperty(Bpmn2Parser.DirectionPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#messageProperty}.
	 * @param ctx the parse tree
	 */
	void enterMessageProperty(Bpmn2Parser.MessagePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#messageProperty}.
	 * @param ctx the parse tree
	 */
	void exitMessageProperty(Bpmn2Parser.MessagePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#preProperty}.
	 * @param ctx the parse tree
	 */
	void enterPreProperty(Bpmn2Parser.PrePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#preProperty}.
	 * @param ctx the parse tree
	 */
	void exitPreProperty(Bpmn2Parser.PrePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#postProperty}.
	 * @param ctx the parse tree
	 */
	void enterPostProperty(Bpmn2Parser.PostPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#postProperty}.
	 * @param ctx the parse tree
	 */
	void exitPostProperty(Bpmn2Parser.PostPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#effectProperty}.
	 * @param ctx the parse tree
	 */
	void enterEffectProperty(Bpmn2Parser.EffectPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#effectProperty}.
	 * @param ctx the parse tree
	 */
	void exitEffectProperty(Bpmn2Parser.EffectPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#stateClause}.
	 * @param ctx the parse tree
	 */
	void enterStateClause(Bpmn2Parser.StateClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#stateClause}.
	 * @param ctx the parse tree
	 */
	void exitStateClause(Bpmn2Parser.StateClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#eventType}.
	 * @param ctx the parse tree
	 */
	void enterEventType(Bpmn2Parser.EventTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#eventType}.
	 * @param ctx the parse tree
	 */
	void exitEventType(Bpmn2Parser.EventTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#eventDir}.
	 * @param ctx the parse tree
	 */
	void enterEventDir(Bpmn2Parser.EventDirContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#eventDir}.
	 * @param ctx the parse tree
	 */
	void exitEventDir(Bpmn2Parser.EventDirContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2Parser#gwType}.
	 * @param ctx the parse tree
	 */
	void enterGwType(Bpmn2Parser.GwTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2Parser#gwType}.
	 * @param ctx the parse tree
	 */
	void exitGwType(Bpmn2Parser.GwTypeContext ctx);
}