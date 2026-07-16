package org.vnu.sme.goal.acl.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.acl.mm.AclActor;
import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclEndpoint;
import org.vnu.sme.goal.acl.mm.AclEntity;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclLink;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclRelation;

public final class AclLayoutBuilder {
    private static final int MARGIN = 36;
    private static final int COL_GAP = 88;
    private static final int ROW_GAP = 22;
    private static final int BOX_W = 220;
    private static final int BASE_H = 60;
    private static final int LINE_H = 18;

    private AclLayoutBuilder() {}

    public static AclLayout build(AclModel model) {
        Map<String, AclNode> nodes = new LinkedHashMap<>();
        List<AclEdge> edges = new ArrayList<>();
        if (model == null) return new AclLayout(nodes, List.of(), 900, 600);

        int y = MARGIN;
        for (AclEntity entity : model.entities()) {
            AclNode node = entityNode(entity);
            place(node, MARGIN, y);
            y += node.h + ROW_GAP;
            nodes.put(node.id, node);
        }
        int maxY = y;

        y = MARGIN;
        int actorX = MARGIN + BOX_W + COL_GAP;
        for (AclActor actor : model.actors()) {
            AclNode node = actorNode(actor);
            place(node, actorX, y);
            y += node.h + ROW_GAP;
            nodes.put(node.id, node);
            if (actor.specializes() != null) {
                edges.add(new AclEdge(actor.name(), actor.specializes(), AclEdgeKind.SPECIALIZES, "specializes"));
            }
        }
        maxY = Math.max(maxY, y);

        y = MARGIN;
        int groupX = actorX + BOX_W + COL_GAP;
        for (AclGroup group : model.groups()) {
            AclNode node = groupNode(group);
            place(node, groupX, y);
            y += node.h + ROW_GAP;
            nodes.put(node.id, node);
            for (var member : group.members()) {
                edges.add(new AclEdge(groupId(group.name()), member.type(), AclEdgeKind.GROUP_MEMBER, member.multiplicity()));
            }
        }
        maxY = Math.max(maxY, y);

        for (AclRelation relation : model.relations()) {
            if (relation.endpoints().size() < 2) continue;
            AclEndpoint first = relation.endpoints().get(0);
            for (int i = 1; i < relation.endpoints().size(); i++) {
                AclEdgeKind kind = relation.kind().equals("partOf") ? AclEdgeKind.PART_OF : AclEdgeKind.RELATIONSHIP;
                edges.add(new AclEdge(first.type(), relation.endpoints().get(i).type(), kind, relation.name()));
            }
        }
        for (AclLink link : model.links()) {
            edges.add(new AclEdge(link.sourceRole(), link.targetRole(), AclEdgeKind.LINK, link.kind()));
        }

        int width = groupX + BOX_W + MARGIN;
        return new AclLayout(nodes, edges, Math.max(width, 900), Math.max(maxY + MARGIN, 600));
    }

    private static AclNode entityNode(AclEntity entity) {
        return new AclNode(entity.name(), entity.name(), AclNodeKind.ENTITY, "entity", attributeLines(entity.attributes()));
    }

    private static AclNode actorNode(AclActor actor) {
        AclNodeKind kind = actor.kind().equals("agent") ? AclNodeKind.AGENT : AclNodeKind.ROLE;
        String subtitle = actor.isAbstract() ? "abstract " + actor.kind() : actor.kind();
        return new AclNode(actor.name(), actor.name(), kind, subtitle, attributeLines(actor.attributes()));
    }

    private static AclNode groupNode(AclGroup group) {
        List<String> lines = new ArrayList<>();
        lines.addAll(attributeLines(group.attributes()));
        group.members().stream()
                .map(m -> m.type() + " [" + m.multiplicity() + "]")
                .forEach(lines::add);
        return new AclNode(groupId(group.name()), group.name(), AclNodeKind.GROUP, "group", lines);
    }

    private static List<String> attributeLines(List<AclAttribute> attributes) {
        return attributes.stream().map(a -> a.name() + " : " + a.type()).toList();
    }

    private static void place(AclNode node, int x, int y) {
        node.x = x;
        node.y = y;
        node.w = BOX_W;
        node.h = Math.max(BASE_H, BASE_H + node.details.size() * LINE_H);
    }

    static String groupId(String name) {
        return "group:" + name;
    }
}
