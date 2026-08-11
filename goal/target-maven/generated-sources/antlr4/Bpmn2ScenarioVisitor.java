// Generated from Bpmn2Scenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.bpmn2scenario.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Bpmn2ScenarioParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Bpmn2ScenarioVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#scenario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenario(Bpmn2ScenarioParser.ScenarioContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtProcess}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtProcess(Bpmn2ScenarioParser.StmtProcessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtActor}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtActor(Bpmn2ScenarioParser.StmtActorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtBind}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtBind(Bpmn2ScenarioParser.StmtBindContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFire(Bpmn2ScenarioParser.StmtFireContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtCompleted}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtCompleted(Bpmn2ScenarioParser.StmtCompletedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtActive}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtActive(Bpmn2ScenarioParser.StmtActiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtToken}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtToken(Bpmn2ScenarioParser.StmtTokenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtValue}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtValue(Bpmn2ScenarioParser.StmtValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtAssert}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAssert(Bpmn2ScenarioParser.StmtAssertContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#processDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcessDecl(Bpmn2ScenarioParser.ProcessDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#actorDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActorDecl(Bpmn2ScenarioParser.ActorDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#bindStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBindStmt(Bpmn2ScenarioParser.BindStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFireStmt(Bpmn2ScenarioParser.FireStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#completedStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompletedStmt(Bpmn2ScenarioParser.CompletedStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#activeStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActiveStmt(Bpmn2ScenarioParser.ActiveStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#tokenStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTokenStmt(Bpmn2ScenarioParser.TokenStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#valueStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueStmt(Bpmn2ScenarioParser.ValueStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#assertStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertStmt(Bpmn2ScenarioParser.AssertStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#forClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForClause(Bpmn2ScenarioParser.ForClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#byClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitByClause(Bpmn2ScenarioParser.ByClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedId(Bpmn2ScenarioParser.QualifiedIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRef(Bpmn2ScenarioParser.RefContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(Bpmn2ScenarioParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#listValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListValue(Bpmn2ScenarioParser.ListValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code countExpr}
	 * labeled alternative in {@link Bpmn2ScenarioParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountExpr(Bpmn2ScenarioParser.CountExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compareExpr}
	 * labeled alternative in {@link Bpmn2ScenarioParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompareExpr(Bpmn2ScenarioParser.CompareExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link Bpmn2ScenarioParser#compOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompOp(Bpmn2ScenarioParser.CompOpContext ctx);
}