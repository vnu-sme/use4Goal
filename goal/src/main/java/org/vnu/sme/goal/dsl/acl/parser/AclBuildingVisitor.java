package org.vnu.sme.goal.dsl.acl.parser;


/**
 * =============================================================================
 * MODULE: ACL parse-tree to CST visitor
 * =============================================================================
 * 1. PURPOSE:
 *    Walks the generated ANTLR parse tree and creates typed ACL CST records. Parser contexts are the input; a ACLModelCS tree is the output.
 *
 * 2. CORE MAPPING / LOGIC RULES:
 *    - Map each grammar alternative to its matching *CS node.
 *    - Preserve declaration order and optional clauses for deterministic diagnostics.
 *    - Do not resolve semantic references while the parse tree is being traversed.
 *    - Main operations exposed by this file: visitModel(), enumValue(), entity(), role(), relation(), group(), compatibility().
 *
 * 3. PIPELINE / WORKFLOW:
 *      1. visitModel / visit...(...)
 *      2. visit child grammar contexts
 *      3. construct *CS records
 *      4. return ACLModelCS to model factory
 * =============================================================================
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.dsl.acl.ast.*;

public final class AclBuildingVisitor extends ACLBaseVisitor<AclModelCS> {/**
 * WHY: Scope and compatibility are transitive across group relationships, so visitModel
 * must evaluate the reachable context before accepting or rejecting a local relationship.
 */

    @Override public AclModelCS visitModel(ACLParser.ModelContext ctx) {
        List<AclEnumCS> enums = new ArrayList<>(); List<AclEntityCS> entities = new ArrayList<>();
        List<AclRoleCS> roles = new ArrayList<>(); List<AclRelationCS> relations = new ArrayList<>();
        List<AclGroupCS> groups = new ArrayList<>(); List<AclCompatibilityCS> compatibilities = new ArrayList<>();
        List<AclInvariantCS> invariants = new ArrayList<>();
        for (var d : ctx.topLevelDecl()) {
            if (d.enumDecl() != null) enums.add(enumValue(d.enumDecl()));
            else if (d.entityDecl() != null) entities.add(entity(d.entityDecl()));
            else if (d.roleDecl() != null) roles.add(role(d.roleDecl()));
            else if (d.orgContextDecl() != null) {
                orgContext(d.orgContextDecl(), entities, roles, groups, compatibilities);
            }
            else if (d.entityRelationDecl() != null) relations.add(relation(d.entityRelationDecl()));
            else if (d.groupDecl() != null) { GroupBuild b = group(d.groupDecl()); groups.add(b.group()); compatibilities.addAll(b.compatibilities()); }
            else if (d.invariantDecl() != null) invariants.add(invariant(d.invariantDecl()));
        }
        return new AclModelCS(ctx.VERSION().getText(), ctx.IDENT().getText(), enums, entities,
                roles, relations, groups, compatibilities, invariants, location(ctx));
    }

    private static AclInvariantCS invariant(ACLParser.InvariantDeclContext c) {
        return new AclInvariantCS(c.IDENT(0).getText(), c.IDENT(1).getText(),
                c.oclExpression().oclToken().stream().map(ParserRuleContext::getText)
                        .collect(java.util.stream.Collectors.joining(" ")), location(c));
    }

    private static AclEnumCS enumValue(ACLParser.EnumDeclContext c) {
        List<String> ids = c.IDENT().stream().map(TerminalNode::getText).toList();
        return new AclEnumCS(ids.get(0), ids.subList(1, ids.size()), location(c));
    }
    private static AclEntityCS entity(ACLParser.EntityDeclContext c) {
        return new AclEntityCS(c.IDENT().getText(), parent(c.specializesClause()), attrs(c.attributeBlock()), location(c));
    }
    private static AclRoleCS role(ACLParser.RoleDeclContext c) {
        return new AclRoleCS(c.IDENT().getText(), parent(c.specializesClause()).stream().toList(),
                attrs(c.attributeBlock()), location(c));
    }
    private static AclRelationCS relation(ACLParser.EntityRelationDeclContext c) {
        List<AclEndpointCS> ends = c.endpointDecl().stream().map(e -> new AclEndpointCS(
                e.IDENT(0).getText(), cardinality(e.cardinality()),
                e.IDENT().size() > 1 ? Optional.of(e.IDENT(1).getText()) : Optional.empty(), location(e))).toList();
        return new AclRelationCS(c.relationKind().getText(), c.IDENT().getText(), ends, location(c));
    }
    private static GroupBuild group(ACLParser.GroupDeclContext c) {
        return group(c.IDENT().getText(), parent(c.specializesClause()), c.groupItem(), location(c));
    }/**
 * WHY: Scope and compatibility are transitive across group relationships, so group
 * must evaluate the reachable context before accepting or rejecting a local relationship.
 */

    private static GroupBuild group(String name, Optional<String> specializes,
                                    List<ACLParser.GroupItemContext> items, AclSourceLocationCS loc) {
        List<AclAttributeCS> attributes = new ArrayList<>(); List<AclGroupMemberCS> members = new ArrayList<>();
        List<AclCompatibilityCS> compatibilities = new ArrayList<>();
        for (var item : items) {
            if (item.attributeDecl() != null) attributes.add(attribute(item.attributeDecl()));
            else if (item.groupMemberDecl() != null) { var m=item.groupMemberDecl(); members.add(new AclGroupMemberCS(m.IDENT().getText(), cardinality(m.cardinality()), location(m))); }
            else if (item.compatibilityDecl() != null) compatibilities.add(compatibility(item.compatibilityDecl(), name));
        }
        return new GroupBuild(new AclGroupCS(name, specializes, attributes, members, List.of(), loc), compatibilities);
    }/**
 * WHY: Scope and compatibility are transitive across group relationships, so compatibility
 * must evaluate the reachable context before accepting or rejecting a local relationship.
 */

    private static AclCompatibilityCS compatibility(ACLParser.CompatibilityDeclContext c, String groupName) {
        return new AclCompatibilityCS(c.IDENT(0).getText(), c.IDENT(1).getText(), true,
                groupName, List.of(), location(c));
    }

    private static void orgContext(ACLParser.OrgContextDeclContext context,
                                   List<AclEntityCS> entities,
                                   List<AclRoleCS> roles,
                                   List<AclGroupCS> groups,
                                   List<AclCompatibilityCS> compatibilities) {
        String name = context.IDENT().getText();
        List<AclGroupMemberCS> members = new ArrayList<>();
        List<ACLParser.OrgContextDeclContext> nested = new ArrayList<>();
        for (var item : context.orgContextItem()) {
            if (item.entityDecl() != null) {
                entities.add(entity(item.entityDecl()));
                members.add(requiredMember(item.entityDecl().IDENT().getText(), item.entityDecl()));
            } else if (item.roleDecl() != null) {
                roles.add(role(item.roleDecl()));
                members.add(requiredMember(item.roleDecl().IDENT().getText(), item.roleDecl()));
            } else if (item.orgContextDecl() != null) {
                members.add(requiredMember(item.orgContextDecl().IDENT().getText(), item.orgContextDecl()));
                nested.add(item.orgContextDecl());
            } else if (item.compatibilityDecl() != null) {
                compatibilities.add(compatibility(item.compatibilityDecl(), name));
            }
        }
        // Register the parent first: AclModel treats the first structural
        // context as the root context for an execution.
        groups.add(new AclGroupCS(name, Optional.empty(), List.of(), members,
                List.of(), true, location(context)));
        nested.forEach(child -> orgContext(child, entities, roles, groups, compatibilities));
    }

    private static AclGroupMemberCS requiredMember(String type, ParserRuleContext context) {
        AclSourceLocationCS loc = location(context);
        return new AclGroupMemberCS(type,
                new AclCardinalityCS("1", Optional.of("1"), loc), loc);
    }
    private static List<AclAttributeCS> attrs(ACLParser.AttributeBlockContext b) { return b==null?List.of():b.attributeDecl().stream().map(AclBuildingVisitor::attribute).toList(); }
    private static AclAttributeCS attribute(ACLParser.AttributeDeclContext c) {
        boolean optional=c.attributeModifier().stream().anyMatch(x->x.getText().equals("optional"));
        boolean required=c.attributeModifier().stream().anyMatch(x->x.getText().equals("required"));
        boolean mut=c.attributeModifier().stream().anyMatch(x->x.getText().equals("mutable"));
        Optional<String> def=c.defaultClause()==null?Optional.empty():Optional.of(c.defaultClause().defaultValue().getText());
        return new AclAttributeCS(c.IDENT(0).getText(),c.IDENT(1).getText(),optional,required,mut,def,location(c));
    }
    private static Optional<String> parent(ACLParser.SpecializesClauseContext c) { return c==null?Optional.empty():Optional.of(c.IDENT().getText()); }
    private static AclCardinalityCS cardinality(ACLParser.CardinalityContext c) {
        if(c.getText().equals("[*]")) return new AclCardinalityCS("0",Optional.empty(),location(c));
        List<TerminalNode> ints=c.INT(); String min=ints.get(0).getText();
        if(!c.getText().contains("..")) return new AclCardinalityCS(min,Optional.of(min),location(c));
        return new AclCardinalityCS(min,c.getText().contains("*")?Optional.empty():Optional.of(ints.get(1).getText()),location(c));
    }
    private static AclSourceLocationCS location(ParserRuleContext c) { return new AclSourceLocationCS(c.getStart().getLine(),c.getStart().getCharPositionInLine()); }
    private record GroupBuild(AclGroupCS group,List<AclCompatibilityCS> compatibilities){}
}
