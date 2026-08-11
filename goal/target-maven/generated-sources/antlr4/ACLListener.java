// Generated from ACL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.acl.parser; 
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ACLParser}.
 */
public interface ACLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ACLParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(ACLParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(ACLParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#topLevelDecl}.
	 * @param ctx the parse tree
	 */
	void enterTopLevelDecl(ACLParser.TopLevelDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#topLevelDecl}.
	 * @param ctx the parse tree
	 */
	void exitTopLevelDecl(ACLParser.TopLevelDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#enumDecl}.
	 * @param ctx the parse tree
	 */
	void enterEnumDecl(ACLParser.EnumDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#enumDecl}.
	 * @param ctx the parse tree
	 */
	void exitEnumDecl(ACLParser.EnumDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#entityDecl}.
	 * @param ctx the parse tree
	 */
	void enterEntityDecl(ACLParser.EntityDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#entityDecl}.
	 * @param ctx the parse tree
	 */
	void exitEntityDecl(ACLParser.EntityDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#roleDecl}.
	 * @param ctx the parse tree
	 */
	void enterRoleDecl(ACLParser.RoleDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#roleDecl}.
	 * @param ctx the parse tree
	 */
	void exitRoleDecl(ACLParser.RoleDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#specializesClause}.
	 * @param ctx the parse tree
	 */
	void enterSpecializesClause(ACLParser.SpecializesClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#specializesClause}.
	 * @param ctx the parse tree
	 */
	void exitSpecializesClause(ACLParser.SpecializesClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#attributeBlock}.
	 * @param ctx the parse tree
	 */
	void enterAttributeBlock(ACLParser.AttributeBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#attributeBlock}.
	 * @param ctx the parse tree
	 */
	void exitAttributeBlock(ACLParser.AttributeBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#attributeDecl}.
	 * @param ctx the parse tree
	 */
	void enterAttributeDecl(ACLParser.AttributeDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#attributeDecl}.
	 * @param ctx the parse tree
	 */
	void exitAttributeDecl(ACLParser.AttributeDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#attributeModifier}.
	 * @param ctx the parse tree
	 */
	void enterAttributeModifier(ACLParser.AttributeModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#attributeModifier}.
	 * @param ctx the parse tree
	 */
	void exitAttributeModifier(ACLParser.AttributeModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#defaultClause}.
	 * @param ctx the parse tree
	 */
	void enterDefaultClause(ACLParser.DefaultClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#defaultClause}.
	 * @param ctx the parse tree
	 */
	void exitDefaultClause(ACLParser.DefaultClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void enterDefaultValue(ACLParser.DefaultValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#defaultValue}.
	 * @param ctx the parse tree
	 */
	void exitDefaultValue(ACLParser.DefaultValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#groupDecl}.
	 * @param ctx the parse tree
	 */
	void enterGroupDecl(ACLParser.GroupDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#groupDecl}.
	 * @param ctx the parse tree
	 */
	void exitGroupDecl(ACLParser.GroupDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#groupItem}.
	 * @param ctx the parse tree
	 */
	void enterGroupItem(ACLParser.GroupItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#groupItem}.
	 * @param ctx the parse tree
	 */
	void exitGroupItem(ACLParser.GroupItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#groupMemberDecl}.
	 * @param ctx the parse tree
	 */
	void enterGroupMemberDecl(ACLParser.GroupMemberDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#groupMemberDecl}.
	 * @param ctx the parse tree
	 */
	void exitGroupMemberDecl(ACLParser.GroupMemberDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#legacyTypedMemberDecl}.
	 * @param ctx the parse tree
	 */
	void enterLegacyTypedMemberDecl(ACLParser.LegacyTypedMemberDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#legacyTypedMemberDecl}.
	 * @param ctx the parse tree
	 */
	void exitLegacyTypedMemberDecl(ACLParser.LegacyTypedMemberDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#legacySubgroupDecl}.
	 * @param ctx the parse tree
	 */
	void enterLegacySubgroupDecl(ACLParser.LegacySubgroupDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#legacySubgroupDecl}.
	 * @param ctx the parse tree
	 */
	void exitLegacySubgroupDecl(ACLParser.LegacySubgroupDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#entityRelationDecl}.
	 * @param ctx the parse tree
	 */
	void enterEntityRelationDecl(ACLParser.EntityRelationDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#entityRelationDecl}.
	 * @param ctx the parse tree
	 */
	void exitEntityRelationDecl(ACLParser.EntityRelationDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#relationKind}.
	 * @param ctx the parse tree
	 */
	void enterRelationKind(ACLParser.RelationKindContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#relationKind}.
	 * @param ctx the parse tree
	 */
	void exitRelationKind(ACLParser.RelationKindContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#endpointDecl}.
	 * @param ctx the parse tree
	 */
	void enterEndpointDecl(ACLParser.EndpointDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#endpointDecl}.
	 * @param ctx the parse tree
	 */
	void exitEndpointDecl(ACLParser.EndpointDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#compatibilityDecl}.
	 * @param ctx the parse tree
	 */
	void enterCompatibilityDecl(ACLParser.CompatibilityDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#compatibilityDecl}.
	 * @param ctx the parse tree
	 */
	void exitCompatibilityDecl(ACLParser.CompatibilityDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#linkArrow}.
	 * @param ctx the parse tree
	 */
	void enterLinkArrow(ACLParser.LinkArrowContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#linkArrow}.
	 * @param ctx the parse tree
	 */
	void exitLinkArrow(ACLParser.LinkArrowContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#linkScope}.
	 * @param ctx the parse tree
	 */
	void enterLinkScope(ACLParser.LinkScopeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#linkScope}.
	 * @param ctx the parse tree
	 */
	void exitLinkScope(ACLParser.LinkScopeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#compatibilityOption}.
	 * @param ctx the parse tree
	 */
	void enterCompatibilityOption(ACLParser.CompatibilityOptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#compatibilityOption}.
	 * @param ctx the parse tree
	 */
	void exitCompatibilityOption(ACLParser.CompatibilityOptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#compatibilityType}.
	 * @param ctx the parse tree
	 */
	void enterCompatibilityType(ACLParser.CompatibilityTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#compatibilityType}.
	 * @param ctx the parse tree
	 */
	void exitCompatibilityType(ACLParser.CompatibilityTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#scopeValue}.
	 * @param ctx the parse tree
	 */
	void enterScopeValue(ACLParser.ScopeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#scopeValue}.
	 * @param ctx the parse tree
	 */
	void exitScopeValue(ACLParser.ScopeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ACLParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void enterCardinality(ACLParser.CardinalityContext ctx);
	/**
	 * Exit a parse tree produced by {@link ACLParser#cardinality}.
	 * @param ctx the parse tree
	 */
	void exitCardinality(ACLParser.CardinalityContext ctx);
}