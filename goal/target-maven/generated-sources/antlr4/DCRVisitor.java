// Generated from DCR.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.dcr.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DCRParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DCRVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DCRParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(DCRParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code eventStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEventStmt(DCRParser.EventStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code markingStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMarkingStmt(DCRParser.MarkingStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relationStmt}
	 * labeled alternative in {@link DCRParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationStmt(DCRParser.RelationStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code markExecuted}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMarkExecuted(DCRParser.MarkExecutedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code markIncluded}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMarkIncluded(DCRParser.MarkIncludedContext ctx);
	/**
	 * Visit a parse tree produced by the {@code markPending}
	 * labeled alternative in {@link DCRParser#markItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMarkPending(DCRParser.MarkPendingContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relCondition}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelCondition(DCRParser.RelConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relResponse}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelResponse(DCRParser.RelResponseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relInclude}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelInclude(DCRParser.RelIncludeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relExclude}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExclude(DCRParser.RelExcludeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relMilestone}
	 * labeled alternative in {@link DCRParser#relKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelMilestone(DCRParser.RelMilestoneContext ctx);
	/**
	 * Visit a parse tree produced by the {@code conditionTime}
	 * labeled alternative in {@link DCRParser#relTime}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionTime(DCRParser.ConditionTimeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code responseTime}
	 * labeled alternative in {@link DCRParser#relTime}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResponseTime(DCRParser.ResponseTimeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DCRParser#deadline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeadline(DCRParser.DeadlineContext ctx);
}