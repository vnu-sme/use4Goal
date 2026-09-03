// Generated from AOL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.aol.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AOLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AOLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AOLParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(AOLParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#topLevelDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopLevelDecl(AOLParser.TopLevelDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#agentDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentDecl(AOLParser.AgentDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#groupInstanceDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupInstanceDecl(AOLParser.GroupInstanceDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#groupItemDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupItemDecl(AOLParser.GroupItemDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#playDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayDecl(AOLParser.PlayDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#roleInstanceDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoleInstanceDecl(AOLParser.RoleInstanceDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#playLinkDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlayLinkDecl(AOLParser.PlayLinkDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#entityInstanceDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntityInstanceDecl(AOLParser.EntityInstanceDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#linkDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLinkDecl(AOLParser.LinkDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#attributeValueBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeValueBlock(AOLParser.AttributeValueBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#attributeValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeValue(AOLParser.AttributeValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link AOLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(AOLParser.ValueContext ctx);
}