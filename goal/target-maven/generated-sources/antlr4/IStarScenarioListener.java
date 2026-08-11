// Generated from IStarScenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.iscn.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link IStarScenarioParser}.
 */
public interface IStarScenarioListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link IStarScenarioParser#scenario}.
	 * @param ctx the parse tree
	 */
	void enterScenario(IStarScenarioParser.ScenarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarScenarioParser#scenario}.
	 * @param ctx the parse tree
	 */
	void exitScenario(IStarScenarioParser.ScenarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarScenarioParser#instanceDecl}.
	 * @param ctx the parse tree
	 */
	void enterInstanceDecl(IStarScenarioParser.InstanceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarScenarioParser#instanceDecl}.
	 * @param ctx the parse tree
	 */
	void exitInstanceDecl(IStarScenarioParser.InstanceDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtFire(IStarScenarioParser.StmtFireContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtFire(IStarScenarioParser.StmtFireContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtAssign}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtAssign(IStarScenarioParser.StmtAssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtAssign}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtAssign(IStarScenarioParser.StmtAssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtAggregate}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtAggregate(IStarScenarioParser.StmtAggregateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtAggregate}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtAggregate(IStarScenarioParser.StmtAggregateContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 */
	void enterFireStmt(IStarScenarioParser.FireStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 */
	void exitFireStmt(IStarScenarioParser.FireStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarScenarioParser#assignStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssignStmt(IStarScenarioParser.AssignStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarScenarioParser#assignStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssignStmt(IStarScenarioParser.AssignStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code svFulfilled}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void enterSvFulfilled(IStarScenarioParser.SvFulfilledContext ctx);
	/**
	 * Exit a parse tree produced by the {@code svFulfilled}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void exitSvFulfilled(IStarScenarioParser.SvFulfilledContext ctx);
	/**
	 * Enter a parse tree produced by the {@code svPending}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void enterSvPending(IStarScenarioParser.SvPendingContext ctx);
	/**
	 * Exit a parse tree produced by the {@code svPending}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void exitSvPending(IStarScenarioParser.SvPendingContext ctx);
	/**
	 * Enter a parse tree produced by the {@code svTrue}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void enterSvTrue(IStarScenarioParser.SvTrueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code svTrue}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void exitSvTrue(IStarScenarioParser.SvTrueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code svFalse}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void enterSvFalse(IStarScenarioParser.SvFalseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code svFalse}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 */
	void exitSvFalse(IStarScenarioParser.SvFalseContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedId(IStarScenarioParser.QualifiedIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedId(IStarScenarioParser.QualifiedIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarScenarioParser#aggregateStmt}.
	 * @param ctx the parse tree
	 */
	void enterAggregateStmt(IStarScenarioParser.AggregateStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarScenarioParser#aggregateStmt}.
	 * @param ctx the parse tree
	 */
	void exitAggregateStmt(IStarScenarioParser.AggregateStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggAll}
	 * labeled alternative in {@link IStarScenarioParser#aggMode}.
	 * @param ctx the parse tree
	 */
	void enterAggAll(IStarScenarioParser.AggAllContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggAll}
	 * labeled alternative in {@link IStarScenarioParser#aggMode}.
	 * @param ctx the parse tree
	 */
	void exitAggAll(IStarScenarioParser.AggAllContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggAny}
	 * labeled alternative in {@link IStarScenarioParser#aggMode}.
	 * @param ctx the parse tree
	 */
	void enterAggAny(IStarScenarioParser.AggAnyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggAny}
	 * labeled alternative in {@link IStarScenarioParser#aggMode}.
	 * @param ctx the parse tree
	 */
	void exitAggAny(IStarScenarioParser.AggAnyContext ctx);
}