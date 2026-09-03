// Generated from IStar.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.istar.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link IStarParser}.
 */
public interface IStarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link IStarParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(IStarParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(IStarParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#actorDef}.
	 * @param ctx the parse tree
	 */
	void enterActorDef(IStarParser.ActorDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#actorDef}.
	 * @param ctx the parse tree
	 */
	void exitActorDef(IStarParser.ActorDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#actorKind}.
	 * @param ctx the parse tree
	 */
	void enterActorKind(IStarParser.ActorKindContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#actorKind}.
	 * @param ctx the parse tree
	 */
	void exitActorKind(IStarParser.ActorKindContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bodyGoal}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void enterBodyGoal(IStarParser.BodyGoalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bodyGoal}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void exitBodyGoal(IStarParser.BodyGoalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bodyTask}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void enterBodyTask(IStarParser.BodyTaskContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bodyTask}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void exitBodyTask(IStarParser.BodyTaskContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bodyResource}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void enterBodyResource(IStarParser.BodyResourceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bodyResource}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void exitBodyResource(IStarParser.BodyResourceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bodyQuality}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void enterBodyQuality(IStarParser.BodyQualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bodyQuality}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void exitBodyQuality(IStarParser.BodyQualityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bodyIsA}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void enterBodyIsA(IStarParser.BodyIsAContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bodyIsA}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void exitBodyIsA(IStarParser.BodyIsAContext ctx);
	/**
	 * Enter a parse tree produced by the {@code bodyParticipates}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void enterBodyParticipates(IStarParser.BodyParticipatesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code bodyParticipates}
	 * labeled alternative in {@link IStarParser#actorBody}.
	 * @param ctx the parse tree
	 */
	void exitBodyParticipates(IStarParser.BodyParticipatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#goalType}.
	 * @param ctx the parse tree
	 */
	void enterGoalType(IStarParser.GoalTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#goalType}.
	 * @param ctx the parse tree
	 */
	void exitGoalType(IStarParser.GoalTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#goalTypeName}.
	 * @param ctx the parse tree
	 */
	void enterGoalTypeName(IStarParser.GoalTypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#goalTypeName}.
	 * @param ctx the parse tree
	 */
	void exitGoalTypeName(IStarParser.GoalTypeNameContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relAnd}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void enterRelAnd(IStarParser.RelAndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relAnd}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void exitRelAnd(IStarParser.RelAndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relOr}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void enterRelOr(IStarParser.RelOrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relOr}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void exitRelOr(IStarParser.RelOrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relContribute}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void enterRelContribute(IStarParser.RelContributeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relContribute}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void exitRelContribute(IStarParser.RelContributeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relQualifies}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void enterRelQualifies(IStarParser.RelQualifiesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relQualifies}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void exitRelQualifies(IStarParser.RelQualifiesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relNeededBy}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void enterRelNeededBy(IStarParser.RelNeededByContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relNeededBy}
	 * labeled alternative in {@link IStarParser#rel}.
	 * @param ctx the parse tree
	 */
	void exitRelNeededBy(IStarParser.RelNeededByContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#dependency}.
	 * @param ctx the parse tree
	 */
	void enterDependency(IStarParser.DependencyContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#dependency}.
	 * @param ctx the parse tree
	 */
	void exitDependency(IStarParser.DependencyContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#dependumRef}.
	 * @param ctx the parse tree
	 */
	void enterDependumRef(IStarParser.DependumRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#dependumRef}.
	 * @param ctx the parse tree
	 */
	void exitDependumRef(IStarParser.DependumRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#dependumKind}.
	 * @param ctx the parse tree
	 */
	void enterDependumKind(IStarParser.DependumKindContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#dependumKind}.
	 * @param ctx the parse tree
	 */
	void exitDependumKind(IStarParser.DependumKindContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#depEnd}.
	 * @param ctx the parse tree
	 */
	void enterDepEnd(IStarParser.DepEndContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#depEnd}.
	 * @param ctx the parse tree
	 */
	void exitDepEnd(IStarParser.DepEndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ctMake}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void enterCtMake(IStarParser.CtMakeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ctMake}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void exitCtMake(IStarParser.CtMakeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ctHelp}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void enterCtHelp(IStarParser.CtHelpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ctHelp}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void exitCtHelp(IStarParser.CtHelpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ctHurt}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void enterCtHurt(IStarParser.CtHurtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ctHurt}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void exitCtHurt(IStarParser.CtHurtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ctBreak}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void enterCtBreak(IStarParser.CtBreakContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ctBreak}
	 * labeled alternative in {@link IStarParser#contribType}.
	 * @param ctx the parse tree
	 */
	void exitCtBreak(IStarParser.CtBreakContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#oclCondition}.
	 * @param ctx the parse tree
	 */
	void enterOclCondition(IStarParser.OclConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#oclCondition}.
	 * @param ctx the parse tree
	 */
	void exitOclCondition(IStarParser.OclConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link IStarParser#goalCondition}.
	 * @param ctx the parse tree
	 */
	void enterGoalCondition(IStarParser.GoalConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link IStarParser#goalCondition}.
	 * @param ctx the parse tree
	 */
	void exitGoalCondition(IStarParser.GoalConditionContext ctx);
}