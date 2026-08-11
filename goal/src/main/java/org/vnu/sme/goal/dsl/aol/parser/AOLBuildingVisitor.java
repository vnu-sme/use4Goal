package org.vnu.sme.goal.dsl.aol.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;
import org.vnu.sme.goal.dsl.aol.ast.AolAgentCS;
import org.vnu.sme.goal.dsl.aol.ast.AolAttributeValueCS;
import org.vnu.sme.goal.dsl.aol.ast.AolEntityInstanceCS;
import org.vnu.sme.goal.dsl.aol.ast.AolGroupInstanceCS;
import org.vnu.sme.goal.dsl.aol.ast.AolLinkCS;
import org.vnu.sme.goal.dsl.aol.ast.AolModelCS;
import org.vnu.sme.goal.dsl.aol.ast.AolPlayCS;

public final class AOLBuildingVisitor extends AOLBaseVisitor<AolModelCS> {

    @Override
    public AolModelCS visitModel(AOLParser.ModelContext ctx) {
        List<AolAgentCS> agents = new ArrayList<>();
        List<AolGroupInstanceCS> groups = new ArrayList<>();
        List<AolEntityInstanceCS> entities = new ArrayList<>();
        List<AolLinkCS> links = new ArrayList<>();
        for (AOLParser.TopLevelDeclContext decl : ctx.topLevelDecl()) {
            if (decl.agentDecl() != null) agents.addAll(buildAgents(decl.agentDecl()));
            else if (decl.groupInstanceDecl() != null) groups.add(buildGroupInstance(decl.groupInstanceDecl()));
            else if (decl.entityInstanceDecl() != null) entities.add(buildEntityInstance(decl.entityInstanceDecl()));
            else if (decl.linkDecl() != null) links.add(buildLink(decl.linkDecl()));
        }
        return new AolModelCS(ctx.VERSION().getText(), ctx.IDENT().getText(), unquote(ctx.STRING_LITERAL().getText()),
                agents, groups, entities, links, location(ctx));
    }

    private static AolLinkCS buildLink(AOLParser.LinkDeclContext ctx) {
        List<TerminalNode> ids = ctx.IDENT();
        String relationName = ids.get(0).getText();
        String sourceInstanceId = ids.get(1).getText();
        List<String> targetInstanceIds = ids.subList(2, ids.size()).stream().map(TerminalNode::getText).toList();
        return new AolLinkCS(relationName, sourceInstanceId, targetInstanceIds, location(ctx));
    }

    private static List<AolAgentCS> buildAgents(AOLParser.AgentDeclContext ctx) {
        List<AolAttributeValueCS> values = attributeValues(ctx.attributeValueBlock());
        if (ctx.attributeValueBlock() != null) {
            return List.of(new AolAgentCS(ctx.IDENT(1).getText(), ctx.IDENT(0).getText(), values, location(ctx)));
        }
        return ctx.IDENT().stream().map(id -> new AolAgentCS(id.getText(), null, List.of(), location(ctx))).toList();
    }

    private static AolGroupInstanceCS buildGroupInstance(AOLParser.GroupInstanceDeclContext ctx) {
        List<TerminalNode> ids = ctx.IDENT();
        String typeName = ids.get(0).getText();
        String instanceId = ids.get(1).getText();

        List<AolGroupInstanceCS> subgroups = new ArrayList<>();
        List<AolPlayCS> plays = new ArrayList<>();
        List<AolEntityInstanceCS> entities = new ArrayList<>();
        List<AolAttributeValueCS> values = new ArrayList<>();
        for (AOLParser.GroupItemDeclContext item : ctx.groupItemDecl()) {
            if (item.groupInstanceDecl() != null) subgroups.add(buildGroupInstance(item.groupInstanceDecl()));
            else if (item.playDecl() != null) plays.add(buildPlay(item.playDecl()));
            else if (item.entityInstanceDecl() != null) entities.add(buildEntityInstance(item.entityInstanceDecl()));
            else if (item.attributeValue() != null) values.add(buildAttributeValue(item.attributeValue()));
        }
        return new AolGroupInstanceCS(typeName, instanceId, subgroups, plays, entities, values, location(ctx));
    }

    private static AolPlayCS buildPlay(AOLParser.PlayDeclContext ctx) {
        List<TerminalNode> ids = ctx.IDENT();
        return new AolPlayCS(ids.get(0).getText(), ids.get(1).getText(), ids.get(2).getText(),
                attributeValues(ctx.attributeValueBlock()), location(ctx));
    }

    private static AolEntityInstanceCS buildEntityInstance(AOLParser.EntityInstanceDeclContext ctx) {
        List<TerminalNode> ids = ctx.IDENT();
        return new AolEntityInstanceCS(ids.get(0).getText(), ids.get(1).getText(),
                attributeValues(ctx.attributeValueBlock()), location(ctx));
    }

    private static List<AolAttributeValueCS> attributeValues(AOLParser.AttributeValueBlockContext block) {
        if (block == null) return List.of();
        return block.attributeValue().stream()
                .map(v -> new AolAttributeValueCS(v.IDENT().getText(), v.value().getText(), location(v)))
                .toList();
    }

    private static AolAttributeValueCS buildAttributeValue(AOLParser.AttributeValueContext value) {
        return new AolAttributeValueCS(value.IDENT().getText(), value.value().getText(), location(value));
    }

    private static String unquote(String text) {
        return text.length() >= 2 ? text.substring(1, text.length() - 1) : text;
    }

    private static AclSourceLocationCS location(ParserRuleContext ctx) {
        return new AclSourceLocationCS(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
}
