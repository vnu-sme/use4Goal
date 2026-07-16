package org.vnu.sme.goal.acl.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.acl.ast.AclActorCS;
import org.vnu.sme.goal.acl.ast.AclAttributeCS;
import org.vnu.sme.goal.acl.ast.AclEndpointCS;
import org.vnu.sme.goal.acl.ast.AclEntityCS;
import org.vnu.sme.goal.acl.ast.AclEnumCS;
import org.vnu.sme.goal.acl.ast.AclGroupCS;
import org.vnu.sme.goal.acl.ast.AclGroupMemberCS;
import org.vnu.sme.goal.acl.ast.AclInvariantCS;
import org.vnu.sme.goal.acl.ast.AclLinkCS;
import org.vnu.sme.goal.acl.ast.AclModelCS;
import org.vnu.sme.goal.acl.ast.AclRelationCS;

public final class AclBuildingVisitor extends ACLBaseVisitor<AclModelCS> {

    @Override
    public AclModelCS visitModel(ACLParser.ModelContext ctx) {
        List<AclEnumCS> enums = new ArrayList<>();
        List<AclEntityCS> entities = new ArrayList<>();
        List<AclActorCS> actors = new ArrayList<>();
        List<AclRelationCS> relations = new ArrayList<>();
        List<AclGroupCS> groups = new ArrayList<>();
        List<AclLinkCS> links = new ArrayList<>();
        List<AclInvariantCS> invariants = new ArrayList<>();

        for (ACLParser.DeclContext decl : ctx.decl()) {
            if (decl.enumDecl() != null) enums.add(buildEnum(decl.enumDecl()));
            else if (decl.entityDecl() != null) entities.add(buildEntity(decl.entityDecl()));
            else if (decl.actorDecl() != null) actors.add(buildActor(decl.actorDecl()));
            else if (decl.relationshipDecl() != null) relations.add(buildRelation("relationship",
                    decl.relationshipDecl().IDENT().getText(), decl.relationshipDecl().endpointBlock().endpoint()));
            else if (decl.partOfDecl() != null) relations.add(buildRelation("partOf",
                    decl.partOfDecl().IDENT().getText(), decl.partOfDecl().endpointBlock().endpoint()));
            else if (decl.groupDecl() != null) groups.add(buildGroup(decl.groupDecl()));
            else if (decl.linkDecl() != null) links.add(buildLink(decl.linkDecl()));
            else if (decl.invariantDecl() != null) invariants.add(buildInvariant(decl.invariantDecl()));
        }

        String version = ctx.VERSION() == null ? "v2.0" : ctx.VERSION().getText();
        return new AclModelCS(version, ctx.IDENT().getText(), enums, entities, actors, relations, groups, links, invariants);
    }

    private static AclEnumCS buildEnum(ACLParser.EnumDeclContext ctx) {
        List<String> ids = ctx.IDENT().stream().map(TerminalNode::getText).toList();
        return new AclEnumCS(ids.get(0), ids.subList(1, ids.size()));
    }

    private static AclEntityCS buildEntity(ACLParser.EntityDeclContext ctx) {
        List<ACLParser.AttributeContext> attributes = ctx.attributeBlock() == null
                ? List.of()
                : ctx.attributeBlock().attribute();
        return new AclEntityCS(ctx.IDENT().getText(), attributes(attributes));
    }

    private static AclActorCS buildActor(ACLParser.ActorDeclContext ctx) {
        String rawKind = ctx.actorKind().getText();
        boolean isAbstract = rawKind.equals("abstractrole");
        String kind = isAbstract ? "role" : rawKind;
        String specializes = ctx.specializes() == null ? null : ctx.specializes().IDENT().getText();
        List<ACLParser.AttributeContext> attributes = ctx.attributeBlock() == null
                ? List.of()
                : ctx.attributeBlock().attribute();
        return new AclActorCS(kind, isAbstract, ctx.IDENT().getText(), specializes, attributes(attributes));
    }

    private static AclRelationCS buildRelation(String kind, String name, List<ACLParser.EndpointContext> endpoints) {
        return new AclRelationCS(kind, name, endpoints.stream().map(AclBuildingVisitor::buildEndpoint).toList());
    }

    private static AclGroupCS buildGroup(ACLParser.GroupDeclContext ctx) {
        String specializes = ctx.specializes() == null ? null : ctx.specializes().IDENT().getText();
        List<AclAttributeCS> attributes = new ArrayList<>();
        List<AclGroupMemberCS> members = new ArrayList<>();
        for (ACLParser.GroupItemContext item : ctx.groupItem()) {
            if (item.attribute() != null) {
                attributes.add(attribute(item.attribute()));
            } else if (item.groupMember() != null) {
                ACLParser.GroupMemberContext member = item.groupMember();
                members.add(new AclGroupMemberCS(member.IDENT().getText(), multiplicity(member.multiplicity())));
            }
        }
        return new AclGroupCS(ctx.IDENT().getText(), specializes, attributes, members);
    }

    private static AclLinkCS buildLink(ACLParser.LinkDeclContext ctx) {
        String scopeKind = ctx.linkScope() == null ? null : ctx.linkScope().getChild(0).getText();
        String scopeGroup = ctx.linkScope() == null ? null : ctx.linkScope().IDENT().getText();
        return new AclLinkCS(ctx.linkKind().getText(), ctx.IDENT(0).getText(), ctx.IDENT(1).getText(),
                scopeKind, scopeGroup);
    }

    private static AclInvariantCS buildInvariant(ACLParser.InvariantDeclContext ctx) {
        return new AclInvariantCS(ctx.IDENT(0).getText(), ctx.IDENT(1).getText(), oclBody(ctx.oclClause().getText()));
    }

    private static List<AclAttributeCS> attributes(List<ACLParser.AttributeContext> attributes) {
        return attributes.stream().map(AclBuildingVisitor::attribute).toList();
    }

    private static AclAttributeCS attribute(ACLParser.AttributeContext attribute) {
        return new AclAttributeCS(attribute.IDENT().getText(), attribute.typeRef().getText());
    }

    private static AclEndpointCS buildEndpoint(ACLParser.EndpointContext ctx) {
        return new AclEndpointCS(ctx.IDENT(0).getText(), multiplicity(ctx.multiplicity()), ctx.IDENT(1).getText());
    }

    private static String multiplicity(ACLParser.MultiplicityContext ctx) {
        String text = ctx.getText();
        return text.substring(1, text.length() - 1);
    }

    private static String oclBody(String raw) {
        int start = raw.indexOf("{[");
        int end = raw.lastIndexOf("]}");
        return start >= 0 && end > start ? raw.substring(start + 2, end).strip() : raw;
    }
}
