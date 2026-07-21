package org.vnu.sme.goal.acl.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.acl.ast.*;

public final class AclBuildingVisitor extends ACLBaseVisitor<AclModelCS> {
    @Override public AclModelCS visitModel(ACLParser.ModelContext ctx) {
        List<AclEnumCS> enums = new ArrayList<>(); List<AclEntityCS> entities = new ArrayList<>();
        List<AclRoleCS> roles = new ArrayList<>(); List<AclRelationCS> relations = new ArrayList<>();
        List<AclGroupCS> groups = new ArrayList<>(); List<AclCompatibilityCS> compatibilities = new ArrayList<>();
        for (var d : ctx.topLevelDecl()) {
            if (d.enumDecl() != null) enums.add(enumValue(d.enumDecl()));
            else if (d.entityDecl() != null) entities.add(entity(d.entityDecl()));
            else if (d.roleDecl() != null) roles.add(role(d.roleDecl()));
            else if (d.entityRelationDecl() != null) relations.add(relation(d.entityRelationDecl()));
            else if (d.compatibilityDecl() != null) compatibilities.add(compatibility(d.compatibilityDecl()));
            else if (d.groupDecl() != null) { GroupBuild b = group(d.groupDecl()); groups.add(b.group()); groups.addAll(b.nested()); compatibilities.addAll(b.compatibilities()); }
        }
        return new AclModelCS(ctx.VERSION().getText(), ctx.IDENT().getText(), enums, entities,
                roles, relations, groups, compatibilities, location(ctx));
    }

    private static AclEnumCS enumValue(ACLParser.EnumDeclContext c) {
        List<String> ids = c.IDENT().stream().map(TerminalNode::getText).toList();
        return new AclEnumCS(ids.get(0), ids.subList(1, ids.size()), location(c));
    }
    private static AclEntityCS entity(ACLParser.EntityDeclContext c) {
        return new AclEntityCS(c.IDENT().getText(), parent(c.specializesClause()), attrs(c.attributeBlock()), location(c));
    }
    private static AclRoleCS role(ACLParser.RoleDeclContext c) {
        return new AclRoleCS(c.IDENT().getText(), c.getStart().getText().equals("abstract"),
                parent(c.specializesClause()).stream().toList(), attrs(c.attributeBlock()), location(c));
    }
    private static AclRelationCS relation(ACLParser.EntityRelationDeclContext c) {
        List<AclEndpointCS> ends = c.endpointDecl().stream().map(e -> new AclEndpointCS(
                e.IDENT(0).getText(), cardinality(e.cardinality()), location(e))).toList();
        return new AclRelationCS(c.relationKind().getText(), c.IDENT().getText(), ends, location(c));
    }
    private static GroupBuild group(ACLParser.GroupDeclContext c) {
        return group(c.IDENT().getText(), c.groupItem(), location(c));
    }
    private static GroupBuild group(String name, List<ACLParser.GroupItemContext> items, AclSourceLocationCS loc) {
        List<AclAttributeCS> attributes = new ArrayList<>(); List<AclGroupMemberCS> members = new ArrayList<>();
        List<AclCompatibilityCS> compatibilities = new ArrayList<>(); List<AclGroupCS> nested = new ArrayList<>();
        for (var item : items) {
            if (item.attributeDecl() != null) attributes.add(attribute(item.attributeDecl()));
            else if (item.groupMemberDecl() != null) { var m=item.groupMemberDecl(); members.add(new AclGroupMemberCS(m.IDENT().getText(), cardinality(m.cardinality()), location(m))); }
            else if (item.legacyTypedMemberDecl() != null) { var m=item.legacyTypedMemberDecl(); members.add(new AclGroupMemberCS(m.IDENT().getText(), cardinality(m.cardinality()), location(m))); }
            else if (item.compatibilityDecl() != null) compatibilities.add(compatibility(item.compatibilityDecl()));
            else if (item.legacySubgroupDecl() != null) { var m=item.legacySubgroupDecl(); members.add(new AclGroupMemberCS(m.IDENT().getText(), cardinality(m.cardinality()), location(m))); GroupBuild child=group(m.IDENT().getText(),m.groupItem(),location(m)); nested.add(child.group()); nested.addAll(child.nested()); compatibilities.addAll(child.compatibilities()); }
        }
        return new GroupBuild(new AclGroupCS(name, attributes, members, List.of(), loc), nested, compatibilities);
    }
    private static AclCompatibilityCS compatibility(ACLParser.CompatibilityDeclContext c) {
        List<AclLinkOptionCS> options = new ArrayList<>();
        for (var option : c.compatibilityOption()) {
            String text = option.getText();
            if (text.startsWith("scope")) options.add(new AclLinkOptionCS.ScopeCS(
                    text.substring("scope".length()), location(option)));
            else if (text.startsWith("extends-subgroups")) options.add(new AclLinkOptionCS.ExtendsSubgroupsCS(
                    Boolean.parseBoolean(text.substring("extends-subgroups".length())), location(option)));
            else if (text.startsWith("bidirectional")) options.add(new AclLinkOptionCS.BidirectionalCS(
                    Boolean.parseBoolean(text.substring("bidirectional".length())), location(option)));
            else if (text.startsWith("type")) options.add(new AclLinkOptionCS.TypeCS(
                    text.substring("type".length()), location(option)));
        }
        return new AclCompatibilityCS(c.IDENT(0).getText(), c.IDENT(1).getText(),
                c.linkArrow().getText().equals("<->"), options, location(c));
    }
    private static List<AclAttributeCS> attrs(ACLParser.AttributeBlockContext b) { return b==null?List.of():b.attributeDecl().stream().map(AclBuildingVisitor::attribute).toList(); }
    private static AclAttributeCS attribute(ACLParser.AttributeDeclContext c) {
        boolean req=c.attributeModifier().stream().anyMatch(x->x.getText().equals("required"));
        boolean mut=c.attributeModifier().stream().anyMatch(x->x.getText().equals("mutable"));
        Optional<String> def=c.defaultClause()==null?Optional.empty():Optional.of(c.defaultClause().defaultValue().getText());
        return new AclAttributeCS(c.IDENT(0).getText(),c.IDENT(1).getText(),req,mut,def,location(c));
    }
    private static Optional<String> parent(ACLParser.SpecializesClauseContext c) { return c==null?Optional.empty():Optional.of(c.IDENT().getText()); }
    private static AclCardinalityCS cardinality(ACLParser.CardinalityContext c) {
        if(c.getText().equals("[*]")) return new AclCardinalityCS("0",Optional.empty(),location(c));
        List<TerminalNode> ints=c.INT(); String min=ints.get(0).getText();
        if(!c.getText().contains("..")) return new AclCardinalityCS(min,Optional.of(min),location(c));
        return new AclCardinalityCS(min,c.getText().contains("*")?Optional.empty():Optional.of(ints.get(1).getText()),location(c));
    }
    private static AclSourceLocationCS location(ParserRuleContext c) { return new AclSourceLocationCS(c.getStart().getLine(),c.getStart().getCharPositionInLine()); }
    private record GroupBuild(AclGroupCS group,List<AclGroupCS> nested,List<AclCompatibilityCS> compatibilities){}
}
