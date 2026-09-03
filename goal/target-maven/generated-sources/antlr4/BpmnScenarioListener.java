// Generated from BpmnScenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.bpmnscenario.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BpmnScenarioParser}.
 */
public interface BpmnScenarioListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#scenario}.
	 * @param ctx the parse tree
	 */
	void enterScenario(BpmnScenarioParser.ScenarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#scenario}.
	 * @param ctx the parse tree
	 */
	void exitScenario(BpmnScenarioParser.ScenarioContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtProcess}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtProcess(BpmnScenarioParser.StmtProcessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtProcess}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtProcess(BpmnScenarioParser.StmtProcessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtActor}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtActor(BpmnScenarioParser.StmtActorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtActor}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtActor(BpmnScenarioParser.StmtActorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtBind}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtBind(BpmnScenarioParser.StmtBindContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtBind}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtBind(BpmnScenarioParser.StmtBindContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtFire(BpmnScenarioParser.StmtFireContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtFire}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtFire(BpmnScenarioParser.StmtFireContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtCompleted}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtCompleted(BpmnScenarioParser.StmtCompletedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtCompleted}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtCompleted(BpmnScenarioParser.StmtCompletedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtActive}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtActive(BpmnScenarioParser.StmtActiveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtActive}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtActive(BpmnScenarioParser.StmtActiveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtToken}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtToken(BpmnScenarioParser.StmtTokenContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtToken}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtToken(BpmnScenarioParser.StmtTokenContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtValue}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtValue(BpmnScenarioParser.StmtValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtValue}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtValue(BpmnScenarioParser.StmtValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stmtAssert}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmtAssert(BpmnScenarioParser.StmtAssertContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stmtAssert}
	 * labeled alternative in {@link BpmnScenarioParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmtAssert(BpmnScenarioParser.StmtAssertContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#processDecl}.
	 * @param ctx the parse tree
	 */
	void enterProcessDecl(BpmnScenarioParser.ProcessDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#processDecl}.
	 * @param ctx the parse tree
	 */
	void exitProcessDecl(BpmnScenarioParser.ProcessDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#actorDecl}.
	 * @param ctx the parse tree
	 */
	void enterActorDecl(BpmnScenarioParser.ActorDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#actorDecl}.
	 * @param ctx the parse tree
	 */
	void exitActorDecl(BpmnScenarioParser.ActorDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#bindStmt}.
	 * @param ctx the parse tree
	 */
	void enterBindStmt(BpmnScenarioParser.BindStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#bindStmt}.
	 * @param ctx the parse tree
	 */
	void exitBindStmt(BpmnScenarioParser.BindStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 */
	void enterFireStmt(BpmnScenarioParser.FireStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#fireStmt}.
	 * @param ctx the parse tree
	 */
	void exitFireStmt(BpmnScenarioParser.FireStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#completedStmt}.
	 * @param ctx the parse tree
	 */
	void enterCompletedStmt(BpmnScenarioParser.CompletedStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#completedStmt}.
	 * @param ctx the parse tree
	 */
	void exitCompletedStmt(BpmnScenarioParser.CompletedStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#activeStmt}.
	 * @param ctx the parse tree
	 */
	void enterActiveStmt(BpmnScenarioParser.ActiveStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#activeStmt}.
	 * @param ctx the parse tree
	 */
	void exitActiveStmt(BpmnScenarioParser.ActiveStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#tokenStmt}.
	 * @param ctx the parse tree
	 */
	void enterTokenStmt(BpmnScenarioParser.TokenStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#tokenStmt}.
	 * @param ctx the parse tree
	 */
	void exitTokenStmt(BpmnScenarioParser.TokenStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#valueStmt}.
	 * @param ctx the parse tree
	 */
	void enterValueStmt(BpmnScenarioParser.ValueStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#valueStmt}.
	 * @param ctx the parse tree
	 */
	void exitValueStmt(BpmnScenarioParser.ValueStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#assertStmt}.
	 * @param ctx the parse tree
	 */
	void enterAssertStmt(BpmnScenarioParser.AssertStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#assertStmt}.
	 * @param ctx the parse tree
	 */
	void exitAssertStmt(BpmnScenarioParser.AssertStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#forClause}.
	 * @param ctx the parse tree
	 */
	void enterForClause(BpmnScenarioParser.ForClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#forClause}.
	 * @param ctx the parse tree
	 */
	void exitForClause(BpmnScenarioParser.ForClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#byClause}.
	 * @param ctx the parse tree
	 */
	void enterByClause(BpmnScenarioParser.ByClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#byClause}.
	 * @param ctx the parse tree
	 */
	void exitByClause(BpmnScenarioParser.ByClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedId(BpmnScenarioParser.QualifiedIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#qualifiedId}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedId(BpmnScenarioParser.QualifiedIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#ref}.
	 * @param ctx the parse tree
	 */
	void enterRef(BpmnScenarioParser.RefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#ref}.
	 * @param ctx the parse tree
	 */
	void exitRef(BpmnScenarioParser.RefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(BpmnScenarioParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(BpmnScenarioParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#listValue}.
	 * @param ctx the parse tree
	 */
	void enterListValue(BpmnScenarioParser.ListValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#listValue}.
	 * @param ctx the parse tree
	 */
	void exitListValue(BpmnScenarioParser.ListValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code countExpr}
	 * labeled alternative in {@link BpmnScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCountExpr(BpmnScenarioParser.CountExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code countExpr}
	 * labeled alternative in {@link BpmnScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCountExpr(BpmnScenarioParser.CountExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code compareExpr}
	 * labeled alternative in {@link BpmnScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCompareExpr(BpmnScenarioParser.CompareExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code compareExpr}
	 * labeled alternative in {@link BpmnScenarioParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCompareExpr(BpmnScenarioParser.CompareExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link BpmnScenarioParser#compOp}.
	 * @param ctx the parse tree
	 */
	void enterCompOp(BpmnScenarioParser.CompOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link BpmnScenarioParser#compOp}.
	 * @param ctx the parse tree
	 */
	void exitCompOp(BpmnScenarioParser.CompOpContext ctx);
}