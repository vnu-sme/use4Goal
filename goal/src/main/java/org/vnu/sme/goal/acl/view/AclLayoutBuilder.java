package org.vnu.sme.goal.acl.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.acl.mm.*;

/** Builds a stable four-column Class-style layout: Entity, Role, Group, Enumeration. */
public final class AclLayoutBuilder {
    private static final int MARGIN = 42;
    private static final int COLUMN_GAP = 64;
    private static final int ROW_GAP = 32;
    private static final int MIN_WIDTH = 165;
    private static final int DETAIL_HEIGHT = 15;

    private AclLayoutBuilder() {}

    public static AclLayout build(AclModel model) {
        Map<String, AclNode> nodes = new LinkedHashMap<>();
        List<AclEdge> edges = new ArrayList<>();
        if (model == null) return new AclLayout(nodes, edges, 900, 600);

        int entityRight = layoutEntities(model, nodes, MARGIN);
        int roleX = entityRight + COLUMN_GAP;
        int roleRight = layoutRoles(model, nodes, roleX);
        int groupX = roleRight + COLUMN_GAP;
        int groupRight = layoutGroups(model, nodes, groupX);
        int enumX = groupRight + COLUMN_GAP;
        int enumRight = layoutEnums(model, nodes, enumX);

        model.generalizations().forEach(value -> edges.add(AclEdge.of(
                id(model, value.specific()), id(model, value.general()), AclEdgeKind.GENERALIZATION,
                null, null, null, false, false)));
        model.relations().forEach(value -> edges.add(AclEdge.of(
                id(model, value.source().type()), id(model, value.target().type()),
                AclEdgeKind.valueOf(value.kind().name()), cardinality(value.source().multiplicity()),
                cardinality(value.target().multiplicity()), value.name(), false, false)));
        model.owners().forEach(value -> edges.add(AclEdge.of(
                groupId(value.sourceGroup()), id(model, value.target()), AclEdgeKind.OWNER,
                null, cardinality(value.multiplicity()), null, false, false)));
        model.compatibilities().forEach(value -> edges.add(AclEdge.of(
                roleId(value.fromRole()), roleId(value.toRole()), AclEdgeKind.COMPATIBILITY,
                null, null, "compatible", value.bidirectional(),
                value.scope() == AclScope.INTER_GROUP)));
        assignParallelRoutes(edges);

        int bottom = nodes.values().stream().mapToInt(value -> value.y + value.h).max().orElse(600);
        return new AclLayout(nodes, edges, Math.max(enumRight + MARGIN, 1000), Math.max(bottom + MARGIN, 650));
    }

    private static int layoutEntities(AclModel model, Map<String, AclNode> nodes, int x) {
        int y = MARGIN, right = x;
        for (AclEntity value : model.entities()) {
            AclNode node = node(entityId(value.name()), value.name(), AclNodeKind.ENTITY, "entity",
                    attributeLines(value.attributes()));
            node.x = x; node.y = y; nodes.put(node.id, node);
            y += node.h + ROW_GAP; right = Math.max(right, node.x + node.w);
        }
        return Math.max(right, x + MIN_WIDTH);
    }

    private static int layoutRoles(AclModel model, Map<String, AclNode> nodes, int x) {
        int y = MARGIN, right = x;
        for (AclRole value : model.roles()) {
            AclNode node = node(roleId(value.name()), value.name(), AclNodeKind.ROLE,
                    value.isAbstract() ? "abstract role" : "role", attributeLines(value.attributes()));
            node.x = x; node.y = y; nodes.put(node.id, node);
            y += node.h + ROW_GAP; right = Math.max(right, node.x + node.w);
        }
        return Math.max(right, x + MIN_WIDTH);
    }

    private static int layoutGroups(AclModel model, Map<String, AclNode> nodes, int x) {
        int y = MARGIN, right = x;
        for (AclGroup value : model.groups()) {
            AclNode node = node(groupId(value.name()), value.name(), AclNodeKind.GROUP, "group",
                    attributeLines(value.attributes()));
            node.x = x; node.y = y; nodes.put(node.id, node);
            y += node.h + ROW_GAP; right = Math.max(right, node.x + node.w);
        }
        return Math.max(right, x + MIN_WIDTH);
    }

    private static int layoutEnums(AclModel model, Map<String, AclNode> nodes, int x) {
        int y = MARGIN, right = x;
        for (AclEnum value : model.enums()) {
            AclNode node = node(enumId(value.name()), value.name(), AclNodeKind.ENUM, "enumeration",
                    value.literals());
            node.x = x; node.y = y; nodes.put(node.id, node);
            y += node.h + ROW_GAP; right = Math.max(right, node.x + node.w);
        }
        return Math.max(right, x + MIN_WIDTH);
    }

    private static AclNode node(String id, String label, AclNodeKind kind, String subtitle, List<String> details) {
        AclNode node = new AclNode(id, label, kind, subtitle, details);
        int longest = Math.max(label.length(), details.stream().mapToInt(String::length).max().orElse(0));
        node.w = Math.max(MIN_WIDTH, Math.min(340, longest * 7 + 34));
        node.h = 44 + (details.isEmpty() ? 14 : details.size() * DETAIL_HEIGHT + 10);
        return node;
    }

    private static List<String> attributeLines(List<AclAttribute> attributes) {
        return attributes.stream().map(value -> value.name() + " : " + value.type().sourceName()).toList();
    }

    private static String id(AclModel model, String name) {
        if (model.findEntity(name).isPresent()) return entityId(name);
        if (model.findRole(name).isPresent()) return roleId(name);
        return groupId(name);
    }

    private static void assignParallelRoutes(List<AclEdge> edges) {
        Map<String, List<Integer>> routes = new LinkedHashMap<>();
        for (int i = 0; i < edges.size(); i++) {
            AclEdge edge = edges.get(i);
            String first = edge.fromId().compareTo(edge.toId()) <= 0 ? edge.fromId() : edge.toId();
            String second = edge.fromId().compareTo(edge.toId()) <= 0 ? edge.toId() : edge.fromId();
            routes.computeIfAbsent(first + "\u0000" + second, ignored -> new ArrayList<>()).add(i);
        }
        for (List<Integer> indexes : routes.values()) {
            for (int route = 0; route < indexes.size(); route++) {
                int index = indexes.get(route);
                edges.set(index, edges.get(index).withRoute(route, indexes.size()));
            }
        }
    }

    private static String cardinality(AclCardinality value) {
        if (value.min() == 0 && value.max().isEmpty()) return "*";
        if (value.max().isPresent() && value.min() == value.max().getAsInt()) return Integer.toString(value.min());
        return value.min() + ".." + (value.max().isPresent() ? value.max().getAsInt() : "*");
    }

    private static String roleId(String value) { return "role::" + value; }
    private static String entityId(String value) { return "entity::" + value; }
    private static String groupId(String value) { return "group::" + value; }
    private static String enumId(String value) { return "enum::" + value; }
}
