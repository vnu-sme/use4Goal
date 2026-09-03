// Generated from AOL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.aol.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link AOLParser}.
 */
public interface AOLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link AOLParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(AOLParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(AOLParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#topLevelDecl}.
	 * @param ctx the parse tree
	 */
	void enterTopLevelDecl(AOLParser.TopLevelDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#topLevelDecl}.
	 * @param ctx the parse tree
	 */
	void exitTopLevelDecl(AOLParser.TopLevelDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#agentDecl}.
	 * @param ctx the parse tree
	 */
	void enterAgentDecl(AOLParser.AgentDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#agentDecl}.
	 * @param ctx the parse tree
	 */
	void exitAgentDecl(AOLParser.AgentDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#groupInstanceDecl}.
	 * @param ctx the parse tree
	 */
	void enterGroupInstanceDecl(AOLParser.GroupInstanceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#groupInstanceDecl}.
	 * @param ctx the parse tree
	 */
	void exitGroupInstanceDecl(AOLParser.GroupInstanceDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#groupItemDecl}.
	 * @param ctx the parse tree
	 */
	void enterGroupItemDecl(AOLParser.GroupItemDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#groupItemDecl}.
	 * @param ctx the parse tree
	 */
	void exitGroupItemDecl(AOLParser.GroupItemDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#playDecl}.
	 * @param ctx the parse tree
	 */
	void enterPlayDecl(AOLParser.PlayDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#playDecl}.
	 * @param ctx the parse tree
	 */
	void exitPlayDecl(AOLParser.PlayDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#roleInstanceDecl}.
	 * @param ctx the parse tree
	 */
	void enterRoleInstanceDecl(AOLParser.RoleInstanceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#roleInstanceDecl}.
	 * @param ctx the parse tree
	 */
	void exitRoleInstanceDecl(AOLParser.RoleInstanceDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#playLinkDecl}.
	 * @param ctx the parse tree
	 */
	void enterPlayLinkDecl(AOLParser.PlayLinkDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#playLinkDecl}.
	 * @param ctx the parse tree
	 */
	void exitPlayLinkDecl(AOLParser.PlayLinkDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#entityInstanceDecl}.
	 * @param ctx the parse tree
	 */
	void enterEntityInstanceDecl(AOLParser.EntityInstanceDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#entityInstanceDecl}.
	 * @param ctx the parse tree
	 */
	void exitEntityInstanceDecl(AOLParser.EntityInstanceDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#linkDecl}.
	 * @param ctx the parse tree
	 */
	void enterLinkDecl(AOLParser.LinkDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#linkDecl}.
	 * @param ctx the parse tree
	 */
	void exitLinkDecl(AOLParser.LinkDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#attributeValueBlock}.
	 * @param ctx the parse tree
	 */
	void enterAttributeValueBlock(AOLParser.AttributeValueBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#attributeValueBlock}.
	 * @param ctx the parse tree
	 */
	void exitAttributeValueBlock(AOLParser.AttributeValueBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void enterAttributeValue(AOLParser.AttributeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#attributeValue}.
	 * @param ctx the parse tree
	 */
	void exitAttributeValue(AOLParser.AttributeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link AOLParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(AOLParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link AOLParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(AOLParser.ValueContext ctx);
}