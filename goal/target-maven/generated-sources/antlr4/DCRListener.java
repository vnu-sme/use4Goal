// Generated from DCR.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dcr.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DCRParser}.
 */
public interface DCRListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DCRParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(DCRParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link DCRParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(DCRParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code eventStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEventStmt(DCRParser.EventStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code eventStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEventStmt(DCRParser.EventStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code markingStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterMarkingStmt(DCRParser.MarkingStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code markingStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitMarkingStmt(DCRParser.MarkingStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relationStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterRelationStmt(DCRParser.RelationStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relationStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitRelationStmt(DCRParser.RelationStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code markExecuted}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 */
	void enterMarkExecuted(DCRParser.MarkExecutedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code markExecuted}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 */
	void exitMarkExecuted(DCRParser.MarkExecutedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code markIncluded}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 */
	void enterMarkIncluded(DCRParser.MarkIncludedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code markIncluded}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 */
	void exitMarkIncluded(DCRParser.MarkIncludedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code markPending}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 */
	void enterMarkPending(DCRParser.MarkPendingContext ctx);
	/**
	 * Exit a parse tree produced by the {@code markPending}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 */
	void exitMarkPending(DCRParser.MarkPendingContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relCondition}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void enterRelCondition(DCRParser.RelConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relCondition}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void exitRelCondition(DCRParser.RelConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relResponse}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void enterRelResponse(DCRParser.RelResponseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relResponse}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void exitRelResponse(DCRParser.RelResponseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relInclude}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void enterRelInclude(DCRParser.RelIncludeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relInclude}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void exitRelInclude(DCRParser.RelIncludeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relExclude}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void enterRelExclude(DCRParser.RelExcludeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relExclude}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void exitRelExclude(DCRParser.RelExcludeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relMilestone}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void enterRelMilestone(DCRParser.RelMilestoneContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relMilestone}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 */
	void exitRelMilestone(DCRParser.RelMilestoneContext ctx);
	/**
	 * Enter a parse tree produced by the {@code conditionTime}
	 * labeled alternative in {@link DCRParser#relTime}.
	 * @param ctx the parse tree
	 */
	void enterConditionTime(DCRParser.ConditionTimeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code conditionTime}
	 * labeled alternative in {@link DCRParser#relTime}.
	 * @param ctx the parse tree
	 */
	void exitConditionTime(DCRParser.ConditionTimeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code responseTime}
	 * labeled alternative in {@link DCRParser#relTime}.
	 * @param ctx the parse tree
	 */
	void enterResponseTime(DCRParser.ResponseTimeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code responseTime}
	 * labeled alternative in {@link DCRParser#relTime}.
	 * @param ctx the parse tree
	 */
	void exitResponseTime(DCRParser.ResponseTimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DCRParser#deadline}.
	 * @param ctx the parse tree
	 */
	void enterDeadline(DCRParser.DeadlineContext ctx);
	/**
	 * Exit a parse tree produced by {@link DCRParser#deadline}.
	 * @param ctx the parse tree
	 */
	void exitDeadline(DCRParser.DeadlineContext ctx);
}