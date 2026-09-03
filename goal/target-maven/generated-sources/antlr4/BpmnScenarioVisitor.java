// Generated from BpmnScenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.bpmnscenario.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BpmnScenarioParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BpmnScenarioVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#scenario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenario(BpmnScenarioParser.ScenarioContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtProcess}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtProcess(BpmnScenarioParser.StmtProcessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtActor}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtActor(BpmnScenarioParser.StmtActorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtBind}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtBind(BpmnScenarioParser.StmtBindContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtFire(BpmnScenarioParser.StmtFireContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtCompleted}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtCompleted(BpmnScenarioParser.StmtCompletedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtActive}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtActive(BpmnScenarioParser.StmtActiveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtToken}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtToken(BpmnScenarioParser.StmtTokenContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtValue}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtValue(BpmnScenarioParser.StmtValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stmtAssert}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmtAssert(BpmnScenarioParser.StmtAssertContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#processDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcessDecl(BpmnScenarioParser.ProcessDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#actorDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActorDecl(BpmnScenarioParser.ActorDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#bindStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBindStmt(BpmnScenarioParser.BindStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFireStmt(BpmnScenarioParser.FireStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#completedStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompletedStmt(BpmnScenarioParser.CompletedStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#activeStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActiveStmt(BpmnScenarioParser.ActiveStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#tokenStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTokenStmt(BpmnScenarioParser.TokenStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#valueStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueStmt(BpmnScenarioParser.ValueStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#assertStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertStmt(BpmnScenarioParser.AssertStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#forClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForClause(BpmnScenarioParser.ForClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#byClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitByClause(BpmnScenarioParser.ByClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedId(BpmnScenarioParser.QualifiedIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#ref}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRef(BpmnScenarioParser.RefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(BpmnScenarioParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#listValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListValue(BpmnScenarioParser.ListValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code countExpr}
	 * labeled alternative in {@link BpmnScenarioParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCountExpr(BpmnScenarioParser.CountExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compareExpr}
	 * labeled alternative in {@link BpmnScenarioParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompareExpr(BpmnScenarioParser.CompareExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link BpmnScenarioParser#compOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompOp(BpmnScenarioParser.CompOpContext ctx);
}