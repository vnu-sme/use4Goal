package org.vnu.sme.goal.acl.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.acl.ast.AclAttributeCS;
import org.vnu.sme.goal.acl.ast.AclCardinalityCS;
import org.vnu.sme.goal.acl.ast.AclCardinalityConstraintCS;
import org.vnu.sme.goal.acl.ast.AclCompatibilityCS;
import org.vnu.sme.goal.acl.ast.AclEntityCS;
import org.vnu.sme.goal.acl.ast.AclEntityMembershipCS;
import org.vnu.sme.goal.acl.ast.AclEnumCS;
import org.vnu.sme.goal.acl.ast.AclGroupCS;
import org.vnu.sme.goal.acl.ast.AclLinkCS;
import org.vnu.sme.goal.acl.ast.AclLinkOptionCS;
import org.vnu.sme.goal.acl.ast.AclModelCS;
import org.vnu.sme.goal.acl.ast.AclRoleCS;
import org.vnu.sme.goal.acl.ast.AclRoleEntityRelationCS;
import org.vnu.sme.goal.acl.ast.AclRoleMembershipCS;
import org.vnu.sme.goal.acl.ast.AclSourceLocationCS;
import org.vnu.sme.goal.acl.ast.AclSubgroupMembershipCS;

public final class AclBuildingVisitor extends ACLBaseVisitor<AclModelCS> {

    @Override
    public AclModelCS visitModel(ACLParser.ModelContext ctx) {
        List<AclEnumCS> enums = new ArrayList<>();
        List<AclRoleCS> roles = new ArrayList<>();
        List<AclEntityCS> entities = new ArrayList<>();
        List<AclGroupCS> groups = new ArrayList<>();

        for (ACLParser.TopLevelDeclContext declaration : ctx.topLevelDecl()) {
            if (declaration.enumDecl() != null) enums.add(buildEnum(declaration.enumDecl()));
            else if (declaration.roleDecl() != null) roles.add(buildRole(declaration.roleDecl()));
            else if (declaration.entityDecl() != null) entities.add(buildEntity(declaration.entityDecl()));
            else if (declaration.groupDecl() != null) groups.add(buildGroup(declaration.groupDecl()));
        }

        return new AclModelCS(ctx.VERSION().getText(), ctx.IDENT().getText(), enums, roles, entities,
                groups, location(ctx));
    }

    private static AclEnumCS buildEnum(ACLParser.EnumDeclContext ctx) {
        List<String> identifiers = ctx.IDENT().stream().map(TerminalNode::getText).toList();
        return new AclEnumCS(identifiers.get(0), identifiers.subList(1, identifiers.size()), location(ctx));
    }

    private static AclRoleCS buildRole(ACLParser.RoleDeclContext ctx) {
        List<String> parents = ctx.extendsClause() == null
                ? List.of()
                : ctx.extendsClause().IDENT().stream().map(TerminalNode::getText).toList();
        return new AclRoleCS(ctx.IDENT().getText(), ctx.getStart().getText().equals("abstract"), parents,
                attributes(ctx.attributeBlock()), location(ctx));
    }

    private static AclEntityCS buildEntity(ACLParser.EntityDeclContext ctx) {
        return new AclEntityCS(ctx.IDENT().getText(), attributes(ctx.attributeBlock()), location(ctx));
    }

    private static List<AclAttributeCS> attributes(ACLParser.AttributeBlockContext block) {
        if (block == null) return List.of();
        return block.attributeDecl().stream().map(AclBuildingVisitor::buildAttribute).toList();
    }

    private static AclAttributeCS buildAttribute(ACLParser.AttributeDeclContext ctx) {
        boolean required = ctx.attributeModifier().stream().anyMatch(modifier -> modifier.getText().equals("required"));
        boolean mutable = ctx.attributeModifier().stream().anyMatch(modifier -> modifier.getText().equals("mutable"));
        Optional<String> defaultValue = ctx.defaultClause() == null
                ? Optional.empty()
                : Optional.of(ctx.defaultClause().defaultValue().getText());
        return new AclAttributeCS(ctx.IDENT(0).getText(), ctx.IDENT(1).getText(), required, mutable,
                defaultValue, location(ctx));
    }

    private static AclGroupCS buildGroup(ACLParser.GroupDeclContext ctx) {
        return buildGroup(ctx.IDENT().getText(), ctx.groupItem(), location(ctx));
    }

    private static AclGroupCS buildGroup(String name, List<ACLParser.GroupItemContext> items,
                                         AclSourceLocationCS sourceLocation) {
        List<AclRoleMembershipCS> roles = new ArrayList<>();
        List<AclEntityMembershipCS> entities = new ArrayList<>();
        List<AclSubgroupMembershipCS> subgroups = new ArrayList<>();
        List<AclLinkCS> links = new ArrayList<>();
        List<AclCompatibilityCS> compatibilities = new ArrayList<>();
        List<AclRoleEntityRelationCS> roleEntityRelations = new ArrayList<>();
        List<AclCardinalityConstraintCS> constraints = new ArrayList<>();

        for (ACLParser.GroupItemContext item : items) {
            if (item.roleMembership() != null) {
                var membership = item.roleMembership();
                roles.add(new AclRoleMembershipCS(membership.IDENT().getText(),
                        buildCardinality(membership.cardinality()), location(membership)));
            } else if (item.entityMembership() != null) {
                var membership = item.entityMembership();
                entities.add(new AclEntityMembershipCS(membership.IDENT().getText(),
                        buildCardinality(membership.cardinality()), location(membership)));
            } else if (item.subgroupMembership() != null) {
                var membership = item.subgroupMembership();
                AclGroupCS subgroup = buildGroup(membership.IDENT().getText(), membership.groupItem(),
                        location(membership));
                subgroups.add(new AclSubgroupMembershipCS(subgroup, buildCardinality(membership.cardinality()),
                        location(membership)));
            } else if (item.linkDecl() != null) {
                links.add(buildLink(item.linkDecl()));
            } else if (item.compatibilityDecl() != null) {
                compatibilities.add(buildCompatibility(item.compatibilityDecl()));
            } else if (item.roleEntityRelationDecl() != null) {
                roleEntityRelations.add(buildRoleEntityRelation(item.roleEntityRelationDecl()));
            } else if (item.cardinalityConstraint() != null) {
                constraints.add(buildConstraint(item.cardinalityConstraint()));
            }
        }
        return new AclGroupCS(name, roles, entities, subgroups, links, compatibilities, roleEntityRelations, constraints,
                sourceLocation);
    }

    private static AclLinkCS buildLink(ACLParser.LinkDeclContext ctx) {
        return new AclLinkCS(ctx.IDENT(0).getText(), ctx.IDENT(1).getText(), ctx.linkType().getText(),
                ctx.linkArrow().getText().equals("<->"),
                ctx.linkOption().stream().map(AclBuildingVisitor::buildOption).toList(), location(ctx));
    }

    private static AclCompatibilityCS buildCompatibility(ACLParser.CompatibilityDeclContext ctx) {
        return new AclCompatibilityCS(ctx.IDENT(0).getText(), ctx.IDENT(1).getText(),
                ctx.linkArrow().getText().equals("<->"),
                ctx.compatibilityOption().stream().map(AclBuildingVisitor::buildOption).toList(), location(ctx));
    }

    private static AclLinkOptionCS buildOption(ACLParser.LinkOptionContext ctx) {
        if (ctx.scopeValue() != null) return new AclLinkOptionCS.ScopeCS(ctx.scopeValue().getText(), location(ctx));
        boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
        if (ctx.getStart().getText().equals("extends-subgroups")) {
            return new AclLinkOptionCS.ExtendsSubgroupsCS(value, location(ctx));
        }
        return new AclLinkOptionCS.BidirectionalCS(value, location(ctx));
    }

    private static AclLinkOptionCS buildOption(ACLParser.CompatibilityOptionContext ctx) {
        if (ctx.scopeValue() != null) return new AclLinkOptionCS.ScopeCS(ctx.scopeValue().getText(), location(ctx));
        boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
        if (ctx.getStart().getText().equals("extends-subgroups")) {
            return new AclLinkOptionCS.ExtendsSubgroupsCS(value, location(ctx));
        }
        return new AclLinkOptionCS.BidirectionalCS(value, location(ctx));
    }

    private static AclRoleEntityRelationCS buildRoleEntityRelation(ACLParser.RoleEntityRelationDeclContext ctx) {
        List<TerminalNode> ids = ctx.IDENT();
        String name;
        String source;
        String target;
        if (ids.size() == 2) {
            source = ids.get(0).getText();
            target = ids.get(1).getText();
            name = ctx.relationType().getText() + "_" + source + "_" + target;
        } else {
            name = ids.get(0).getText();
            source = ids.get(1).getText();
            target = ids.get(2).getText();
        }
        List<AclLinkOptionCS> options = new ArrayList<>();
        for (ACLParser.RelationOptionContext option : ctx.relationOption()) {
            if (option.scopeValue() != null) options.add(new AclLinkOptionCS.ScopeCS(option.scopeValue().getText(), location(option)));
            else options.add(new AclLinkOptionCS.ExtendsSubgroupsCS(Boolean.parseBoolean(option.BOOLEAN().getText()), location(option)));
        }
        return new AclRoleEntityRelationCS(name, source, target, ctx.relationType().getText(), options, location(ctx));
    }

    private static AclCardinalityConstraintCS buildConstraint(ACLParser.CardinalityConstraintContext ctx) {
        return new AclCardinalityConstraintCS(ctx.targetKind().getText(), ctx.IDENT().getText(),
                buildCardinality(ctx.cardinality()), location(ctx));
    }

    private static AclCardinalityCS buildCardinality(ACLParser.CardinalityContext ctx) {
        List<TerminalNode> integers = ctx.INT();
        Optional<String> max = ctx.getText().contains("*")
                ? Optional.empty()
                : Optional.of(integers.get(1).getText());
        return new AclCardinalityCS(integers.get(0).getText(), max, location(ctx));
    }

    private static AclSourceLocationCS location(ParserRuleContext ctx) {
        return new AclSourceLocationCS(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
}
