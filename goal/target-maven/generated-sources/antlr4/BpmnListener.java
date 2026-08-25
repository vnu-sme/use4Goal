// Generated from Bpmn.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.bpmn.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BpmnParser}.
 */
public interface BpmnListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BpmnParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(BpmnParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(BpmnParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#pool}.
	 * @param ctx the parse tree
	 */
	void enterPool(BpmnParser.PoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#pool}.
	 * @param ctx the parse tree
	 */
	void exitPool(BpmnParser.PoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#laneDecl}.
	 * @param ctx the parse tree
	 */
	void enterLaneDecl(BpmnParser.LaneDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#laneDecl}.
	 * @param ctx the parse tree
	 */
	void exitLaneDecl(BpmnParser.LaneDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#topElement}.
	 * @param ctx the parse tree
	 */
	void enterTopElement(BpmnParser.TopElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#topElement}.
	 * @param ctx the parse tree
	 */
	void exitTopElement(BpmnParser.TopElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#startDecl}.
	 * @param ctx the parse tree
	 */
	void enterStartDecl(BpmnParser.StartDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#startDecl}.
	 * @param ctx the parse tree
	 */
	void exitStartDecl(BpmnParser.StartDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#endDecl}.
	 * @param ctx the parse tree
	 */
	void enterEndDecl(BpmnParser.EndDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#endDecl}.
	 * @param ctx the parse tree
	 */
	void exitEndDecl(BpmnParser.EndDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#eventDecl}.
	 * @param ctx the parse tree
	 */
	void enterEventDecl(BpmnParser.EventDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#eventDecl}.
	 * @param ctx the parse tree
	 */
	void exitEventDecl(BpmnParser.EventDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#activityDecl}.
	 * @param ctx the parse tree
	 */
	void enterActivityDecl(BpmnParser.ActivityDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#activityDecl}.
	 * @param ctx the parse tree
	 */
	void exitActivityDecl(BpmnParser.ActivityDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#activityTypeProperty}.
	 * @param ctx the parse tree
	 */
	void enterActivityTypeProperty(BpmnParser.ActivityTypePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#activityTypeProperty}.
	 * @param ctx the parse tree
	 */
	void exitActivityTypeProperty(BpmnParser.ActivityTypePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#activityType}.
	 * @param ctx the parse tree
	 */
	void enterActivityType(BpmnParser.ActivityTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#activityType}.
	 * @param ctx the parse tree
	 */
	void exitActivityType(BpmnParser.ActivityTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#gatewayDecl}.
	 * @param ctx the parse tree
	 */
	void enterGatewayDecl(BpmnParser.GatewayDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#gatewayDecl}.
	 * @param ctx the parse tree
	 */
	void exitGatewayDecl(BpmnParser.GatewayDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#gatewayTypeProperty}.
	 * @param ctx the parse tree
	 */
	void enterGatewayTypeProperty(BpmnParser.GatewayTypePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#gatewayTypeProperty}.
	 * @param ctx the parse tree
	 */
	void exitGatewayTypeProperty(BpmnParser.GatewayTypePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#gatewayFlow}.
	 * @param ctx the parse tree
	 */
	void enterGatewayFlow(BpmnParser.GatewayFlowContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#gatewayFlow}.
	 * @param ctx the parse tree
	 */
	void exitGatewayFlow(BpmnParser.GatewayFlowContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#gatewayFlowCondition}.
	 * @param ctx the parse tree
	 */
	void enterGatewayFlowCondition(BpmnParser.GatewayFlowConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#gatewayFlowCondition}.
	 * @param ctx the parse tree
	 */
	void exitGatewayFlowCondition(BpmnParser.GatewayFlowConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#laneProperty}.
	 * @param ctx the parse tree
	 */
	void enterLaneProperty(BpmnParser.LanePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#laneProperty}.
	 * @param ctx the parse tree
	 */
	void exitLaneProperty(BpmnParser.LanePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#flowProperty}.
	 * @param ctx the parse tree
	 */
	void enterFlowProperty(BpmnParser.FlowPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#flowProperty}.
	 * @param ctx the parse tree
	 */
	void exitFlowProperty(BpmnParser.FlowPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#message}.
	 * @param ctx the parse tree
	 */
	void enterMessage(BpmnParser.MessageContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#message}.
	 * @param ctx the parse tree
	 */
	void exitMessage(BpmnParser.MessageContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#messageFlow}.
	 * @param ctx the parse tree
	 */
	void enterMessageFlow(BpmnParser.MessageFlowContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#messageFlow}.
	 * @param ctx the parse tree
	 */
	void exitMessageFlow(BpmnParser.MessageFlowContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#nameProperty}.
	 * @param ctx the parse tree
	 */
	void enterNameProperty(BpmnParser.NamePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#nameProperty}.
	 * @param ctx the parse tree
	 */
	void exitNameProperty(BpmnParser.NamePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#triggerProperty}.
	 * @param ctx the parse tree
	 */
	void enterTriggerProperty(BpmnParser.TriggerPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#triggerProperty}.
	 * @param ctx the parse tree
	 */
	void exitTriggerProperty(BpmnParser.TriggerPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#directionProperty}.
	 * @param ctx the parse tree
	 */
	void enterDirectionProperty(BpmnParser.DirectionPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#directionProperty}.
	 * @param ctx the parse tree
	 */
	void exitDirectionProperty(BpmnParser.DirectionPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#messageProperty}.
	 * @param ctx the parse tree
	 */
	void enterMessageProperty(BpmnParser.MessagePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#messageProperty}.
	 * @param ctx the parse tree
	 */
	void exitMessageProperty(BpmnParser.MessagePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#preProperty}.
	 * @param ctx the parse tree
	 */
	void enterPreProperty(BpmnParser.PrePropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#preProperty}.
	 * @param ctx the parse tree
	 */
	void exitPreProperty(BpmnParser.PrePropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#postProperty}.
	 * @param ctx the parse tree
	 */
	void enterPostProperty(BpmnParser.PostPropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#postProperty}.
	 * @param ctx the parse tree
	 */
	void exitPostProperty(BpmnParser.PostPropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#stateClause}.
	 * @param ctx the parse tree
	 */
	void enterStateClause(BpmnParser.StateClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#stateClause}.
	 * @param ctx the parse tree
	 */
	void exitStateClause(BpmnParser.StateClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#eventType}.
	 * @param ctx the parse tree
	 */
	void enterEventType(BpmnParser.EventTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#eventType}.
	 * @param ctx the parse tree
	 */
	void exitEventType(BpmnParser.EventTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#eventDir}.
	 * @param ctx the parse tree
	 */
	void enterEventDir(BpmnParser.EventDirContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#eventDir}.
	 * @param ctx the parse tree
	 */
	void exitEventDir(BpmnParser.EventDirContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnParser#gwType}.
	 * @param ctx the parse tree
	 */
	void enterGwType(BpmnParser.GwTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnParser#gwType}.
	 * @param ctx the parse tree
	 */
	void exitGwType(BpmnParser.GwTypeContext ctx);
}