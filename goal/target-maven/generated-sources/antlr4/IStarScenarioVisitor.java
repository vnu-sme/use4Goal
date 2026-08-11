// Generated from IStarScenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.iscn.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link IStarScenarioParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface IStarScenarioVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link IStarScenarioParser#scenario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenario(IStarScenarioParser.ScenarioContext ctx);
	/**
	 * Visit a parse tree produced by {@link IStarScenarioParser#instanceDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstanceDecl(IStarScenarioParser.InstanceDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFire(IStarScenarioParser.StmtFireContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtAssign}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAssign(IStarScenarioParser.StmtAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtAggregate}
	 * labeled alternative in {@link IStarScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAggregate(IStarScenarioParser.StmtAggregateContext ctx);
	/**
	 * Visit a parse tree produced by {@link IStarScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFireStmt(IStarScenarioParser.FireStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link IStarScenarioParser#assignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(IStarScenarioParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code svFulfilled}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSvFulfilled(IStarScenarioParser.SvFulfilledContext ctx);
	/**
	 * Visit a parse tree produced by the {@code svPending}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSvPending(IStarScenarioParser.SvPendingContext ctx);
	/**
	 * Visit a parse tree produced by the {@code svTrue}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSvTrue(IStarScenarioParser.SvTrueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code svFalse}
	 * labeled alternative in {@link IStarScenarioParser#statusValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSvFalse(IStarScenarioParser.SvFalseContext ctx);
	/**
	 * Visit a parse tree produced by {@link IStarScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedId(IStarScenarioParser.QualifiedIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link IStarScenarioParser#aggregateStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregateStmt(IStarScenarioParser.AggregateStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggAll}
	 * labeled alternative in {@link IStarScenarioParser#aggMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggAll(IStarScenarioParser.AggAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggAny}
	 * labeled alternative in {@link IStarScenarioParser#aggMode}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggAny(IStarScenarioParser.AggAnyContext ctx);
}