// Generated from ACL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.acl.parser; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ACLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ACLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ACLParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(ACLParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#topLevelDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTopLevelDecl(ACLParser.TopLevelDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#invariantDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvariantDecl(ACLParser.InvariantDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#oclExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOclExpression(ACLParser.OclExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#oclToken}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOclToken(ACLParser.OclTokenContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#enumDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumDecl(ACLParser.EnumDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#entityDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntityDecl(ACLParser.EntityDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#roleDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoleDecl(ACLParser.RoleDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#specializesClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSpecializesClause(ACLParser.SpecializesClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#attributeBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeBlock(ACLParser.AttributeBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#attributeDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeDecl(ACLParser.AttributeDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#attributeModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeModifier(ACLParser.AttributeModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#defaultClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultClause(ACLParser.DefaultClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#defaultValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefaultValue(ACLParser.DefaultValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#groupDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupDecl(ACLParser.GroupDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#groupItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupItem(ACLParser.GroupItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#groupMemberDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupMemberDecl(ACLParser.GroupMemberDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#entityRelationDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntityRelationDecl(ACLParser.EntityRelationDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#relationKind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationKind(ACLParser.RelationKindContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#endpointDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndpointDecl(ACLParser.EndpointDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#compatibilityDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompatibilityDecl(ACLParser.CompatibilityDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link ACLParser#cardinality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCardinality(ACLParser.CardinalityContext ctx);
}