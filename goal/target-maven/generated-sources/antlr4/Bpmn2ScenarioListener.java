// Generated from Bpmn2Scenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.bpmn2scenario.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Bpmn2ScenarioParser}.
 */
public interface Bpmn2ScenarioListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#scenario}.
	 * @param ctx the parse tree
	 */
	void enterScenario(Bpmn2ScenarioParser.ScenarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#scenario}.
	 * @param ctx the parse tree
	 */
	void exitScenario(Bpmn2ScenarioParser.ScenarioContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtProcess}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtProcess(Bpmn2ScenarioParser.StmtProcessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtProcess}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtProcess(Bpmn2ScenarioParser.StmtProcessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtActor}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtActor(Bpmn2ScenarioParser.StmtActorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtActor}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtActor(Bpmn2ScenarioParser.StmtActorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtBind}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtBind(Bpmn2ScenarioParser.StmtBindContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtBind}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtBind(Bpmn2ScenarioParser.StmtBindContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtFire(Bpmn2ScenarioParser.StmtFireContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtFire(Bpmn2ScenarioParser.StmtFireContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtCompleted}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtCompleted(Bpmn2ScenarioParser.StmtCompletedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtCompleted}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtCompleted(Bpmn2ScenarioParser.StmtCompletedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtActive}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtActive(Bpmn2ScenarioParser.StmtActiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtActive}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtActive(Bpmn2ScenarioParser.StmtActiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtToken}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtToken(Bpmn2ScenarioParser.StmtTokenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtToken}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtToken(Bpmn2ScenarioParser.StmtTokenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtValue}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtValue(Bpmn2ScenarioParser.StmtValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtValue}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtValue(Bpmn2ScenarioParser.StmtValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtAssert}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtAssert(Bpmn2ScenarioParser.StmtAssertContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtAssert}
	 * labeled alternative in {@link Bpmn2ScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtAssert(Bpmn2ScenarioParser.StmtAssertContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#processDecl}.
	 * @param ctx the parse tree
	 */
	void enterProcessDecl(Bpmn2ScenarioParser.ProcessDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#processDecl}.
	 * @param ctx the parse tree
	 */
	void exitProcessDecl(Bpmn2ScenarioParser.ProcessDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#actorDecl}.
	 * @param ctx the parse tree
	 */
	void enterActorDecl(Bpmn2ScenarioParser.ActorDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#actorDecl}.
	 * @param ctx the parse tree
	 */
	void exitActorDecl(Bpmn2ScenarioParser.ActorDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#bindStmt}.
	 * @param ctx the parse tree
	 */
	void enterBindStmt(Bpmn2ScenarioParser.BindStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#bindStmt}.
	 * @param ctx the parse tree
	 */
	void exitBindStmt(Bpmn2ScenarioParser.BindStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 */
	void enterFireStmt(Bpmn2ScenarioParser.FireStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 */
	void exitFireStmt(Bpmn2ScenarioParser.FireStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#completedStmt}.
	 * @param ctx the parse tree
	 */
	void enterCompletedStmt(Bpmn2ScenarioParser.CompletedStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#completedStmt}.
	 * @param ctx the parse tree
	 */
	void exitCompletedStmt(Bpmn2ScenarioParser.CompletedStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#activeStmt}.
	 * @param ctx the parse tree
	 */
	void enterActiveStmt(Bpmn2ScenarioParser.ActiveStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#activeStmt}.
	 * @param ctx the parse tree
	 */
	void exitActiveStmt(Bpmn2ScenarioParser.ActiveStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#tokenStmt}.
	 * @param ctx the parse tree
	 */
	void enterTokenStmt(Bpmn2ScenarioParser.TokenStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#tokenStmt}.
	 * @param ctx the parse tree
	 */
	void exitTokenStmt(Bpmn2ScenarioParser.TokenStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#valueStmt}.
	 * @param ctx the parse tree
	 */
	void enterValueStmt(Bpmn2ScenarioParser.ValueStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#valueStmt}.
	 * @param ctx the parse tree
	 */
	void exitValueStmt(Bpmn2ScenarioParser.ValueStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#assertStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssertStmt(Bpmn2ScenarioParser.AssertStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#assertStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssertStmt(Bpmn2ScenarioParser.AssertStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#forClause}.
	 * @param ctx the parse tree
	 */
	void enterForClause(Bpmn2ScenarioParser.ForClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#forClause}.
	 * @param ctx the parse tree
	 */
	void exitForClause(Bpmn2ScenarioParser.ForClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#byClause}.
	 * @param ctx the parse tree
	 */
	void enterByClause(Bpmn2ScenarioParser.ByClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#byClause}.
	 * @param ctx the parse tree
	 */
	void exitByClause(Bpmn2ScenarioParser.ByClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedId(Bpmn2ScenarioParser.QualifiedIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedId(Bpmn2ScenarioParser.QualifiedIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#ref}.
	 * @param ctx the parse tree
	 */
	void enterRef(Bpmn2ScenarioParser.RefContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#ref}.
	 * @param ctx the parse tree
	 */
	void exitRef(Bpmn2ScenarioParser.RefContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(Bpmn2ScenarioParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(Bpmn2ScenarioParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#listValue}.
	 * @param ctx the parse tree
	 */
	void enterListValue(Bpmn2ScenarioParser.ListValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#listValue}.
	 * @param ctx the parse tree
	 */
	void exitListValue(Bpmn2ScenarioParser.ListValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code countExpr}
	 * labeled alternative in {@link Bpmn2ScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCountExpr(Bpmn2ScenarioParser.CountExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code countExpr}
	 * labeled alternative in {@link Bpmn2ScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCountExpr(Bpmn2ScenarioParser.CountExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compareExpr}
	 * labeled alternative in {@link Bpmn2ScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCompareExpr(Bpmn2ScenarioParser.CompareExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compareExpr}
	 * labeled alternative in {@link Bpmn2ScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCompareExpr(Bpmn2ScenarioParser.CompareExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link Bpmn2ScenarioParser#compOp}.
	 * @param ctx the parse tree
	 */
	void enterCompOp(Bpmn2ScenarioParser.CompOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link Bpmn2ScenarioParser#compOp}.
	 * @param ctx the parse tree
	 */
	void exitCompOp(Bpmn2ScenarioParser.CompOpContext ctx);
}