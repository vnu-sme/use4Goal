package org.vnu.sme.goal.acl.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclCardinality;
import org.vnu.sme.goal.acl.mm.AclCardinalityConstraint;
import org.vnu.sme.goal.acl.mm.AclCardinalityTargetKind;
import org.vnu.sme.goal.acl.mm.AclEntity;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclLinkType;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclRole;
import org.vnu.sme.goal.acl.mm.AclRoleEntityRelation;

public final class AclLayoutBuilder {
    private static final int MARGIN = 36;
    private static final int NODE_GAP = 46;
    private static final int ROLE_LEVEL_GAP = 72;
    private static final int ENTITY_ROW_GAP = 28;
    private static final int GROUP_TOP_GAP = 112;
    private static final int GROUP_LEVEL_GAP = 112;
    private static final int GROUP_NODE_GAP = 64;

    private static final int ROLE_MIN_WIDTH = 170;
    private static final int ENTITY_MIN_WIDTH = 230;
    private static final int GROUP_MIN_WIDTH = 154;
    private static final int DETAIL_HEIGHT = 16;

    private AclLayoutBuilder() {}

    public static AclLayout build(AclModel model) {
        Map<String, AclNode> nodes = new LinkedHashMap<>();
        List<AclEdge> edges = new ArrayList<>();
        if (model == null) return new AclLayout(nodes, edges, 900, 600);

        for (AclRole role : model.roles()) {
            AclNode node = node(roleId(role.name()), role.name(), AclNodeKind.ROLE, role.isAbstract() ? "abstract role" : "role",
                    attributeLines(role.attributes()));
            nodes.put(node.id, node);
            for (String parent : role.parentRoles()) {
                edges.add(AclEdge.inheritance(node.id, roleId(parent)));
            }
        }

        RoleBounds roleBounds = layoutRoles(model.roles(), nodes);
        int entityX = roleBounds.right + NODE_GAP * 2;
        int entityY = MARGIN;
        int entityBottom = MARGIN;
        for (AclEntity entity : model.entities()) {
            AclNode node = node(entityId(entity.name()), entity.name(), AclNodeKind.ENTITY, "entity",
                    attributeLines(entity.attributes()));
            node.x = entityX;
            node.y = entityY;
            nodes.put(node.id, node);
            entityY += node.h + ENTITY_ROW_GAP;
            entityBottom = Math.max(entityBottom, node.y + node.h);
        }

        List<GroupPlacement> placements = new ArrayList<>();
        AclGroup group = model.rootGroup();
        addGroup(group, group.name(), null, null, 0, nodes, edges, placements);

        int groupBaseY = Math.max(roleBounds.bottom, entityBottom) + GROUP_TOP_GAP;
        layoutGroups(placements, nodes, groupBaseY);
        assignParallelRoutes(edges);

        int maxBottom = nodes.values().stream().mapToInt(value -> value.y + value.h).max().orElse(600);
        int maxRight = nodes.values().stream().mapToInt(value -> value.x + value.w).max().orElse(900);
        return new AclLayout(nodes, edges,
                Math.max(maxRight + MARGIN, 1000), Math.max(maxBottom + MARGIN, 650));
    }

    private static RoleBounds layoutRoles(List<AclRole> roles, Map<String, AclNode> nodes) {
        Map<String, AclRole> byName = new LinkedHashMap<>();
        roles.forEach(role -> byName.put(role.name(), role));
        Map<String, Integer> depths = new HashMap<>();
        int maxDepth = 0;
        for (AclRole role : roles) {
            maxDepth = Math.max(maxDepth, roleDepth(role.name(), byName, depths, new HashSet<>()));
        }

        int y = MARGIN;
        int right = MARGIN;
        for (int depth = 0; depth <= maxDepth; depth++) {
            int x = MARGIN;
            int rowHeight = 0;
            for (AclRole role : roles) {
                if (depths.getOrDefault(role.name(), 0) != depth) continue;
                AclNode node = nodes.get(roleId(role.name()));
                node.x = x;
                node.y = y;
                x += node.w + NODE_GAP;
                rowHeight = Math.max(rowHeight, node.h);
                right = Math.max(right, node.x + node.w);
            }
            if (rowHeight > 0) y += rowHeight + ROLE_LEVEL_GAP;
        }
        return new RoleBounds(right, Math.max(MARGIN, y - ROLE_LEVEL_GAP));
    }

    private static int roleDepth(String name, Map<String, AclRole> roles,
                                 Map<String, Integer> memo, Set<String> visiting) {
        Integer known = memo.get(name);
        if (known != null) return known;
        AclRole role = roles.get(name);
        if (role == null || !visiting.add(name)) return 0;
        int depth = 0;
        for (String parent : role.parentRoles()) {
            depth = Math.max(depth, roleDepth(parent, roles, memo, visiting) + 1);
        }
        visiting.remove(name);
        memo.put(name, depth);
        return depth;
    }

    private static void addGroup(AclGroup group, String path, String parentId,
                                 String membershipCardinality, int depth,
                                 Map<String, AclNode> nodes, List<AclEdge> edges,
                                 List<GroupPlacement> placements) {
        String id = groupId(path);
        AclNode node = node(id, group.name(), AclNodeKind.GROUP, "group", List.of());
        nodes.put(id, node);

        List<String> memberIds = new ArrayList<>();
        group.roles().forEach(value -> memberIds.add(roleId(value.roleName())));
        group.entities().forEach(value -> memberIds.add(entityId(value.entityName())));
        List<String> childIds = group.subgroups().stream()
                .map(value -> groupId(path + "/" + value.group().name())).toList();
        placements.add(new GroupPlacement(id, depth, memberIds, childIds));

        if (parentId != null) {
            edges.add(AclEdge.composition(id, parentId, AclEdgeKind.SUBGROUP_COMPOSITION,
                    membershipCardinality));
        }
        group.roles().forEach(value -> edges.add(AclEdge.composition(roleId(value.roleName()), id,
                AclEdgeKind.ROLE_COMPOSITION, cardinality(value.cardinality()))));
        group.entities().forEach(value -> edges.add(AclEdge.composition(entityId(value.entityName()), id,
                AclEdgeKind.ENTITY_COMPOSITION, cardinality(value.cardinality()))));

        group.links().forEach(value -> edges.add(AclEdge.roleLink(
                roleId(value.fromRole()), roleId(value.toRole()), linkKind(value.type()),
                value.scope(), value.extendsSubgroups(), value.bidirectional())));
        group.compatibilities().forEach(value -> edges.add(AclEdge.roleLink(
                roleId(value.fromRole()), roleId(value.toRole()), AclEdgeKind.COMPATIBILITY,
                value.scope(), value.extendsSubgroups(), value.bidirectional())));
        for (AclRoleEntityRelation value : group.roleEntityRelations()) {
            edges.add(AclEdge.roleEntityRelation(roleId(value.sourceRole().name()),
                    entityId(value.targetEntity().name()), value.type().sourceName(),
                    value.scope(), value.extendsSubgroups()));
        }

        for (AclCardinalityConstraint constraint : group.cardinalityConstraints()) {
            String targetId = constraintTargetId(group, path, constraint);
            if (targetId != null) {
                edges.add(AclEdge.groupCardinality(targetId, id,
                        cardinality(constraint.cardinality())));
            }
        }

        for (var subgroup : group.subgroups()) {
            String childPath = path + "/" + subgroup.group().name();
            addGroup(subgroup.group(), childPath, id, cardinality(subgroup.cardinality()),
                    depth + 1, nodes, edges, placements);
        }
    }

    private static void layoutGroups(List<GroupPlacement> placements,
                                     Map<String, AclNode> nodes, int baseY) {
        int maxDepth = placements.stream().mapToInt(GroupPlacement::depth).max().orElse(0);
        Map<Integer, List<GroupPlacement>> byDepth = new HashMap<>();
        placements.forEach(value -> byDepth.computeIfAbsent(value.depth(), ignored -> new ArrayList<>()).add(value));

        for (int depth = maxDepth; depth >= 0; depth--) {
            List<GroupPlacement> row = byDepth.getOrDefault(depth, List.of());
            List<PreferredGroup> preferred = new ArrayList<>();
            for (int i = 0; i < row.size(); i++) {
                GroupPlacement placement = row.get(i);
                List<Integer> centers = new ArrayList<>();
                placement.memberIds().forEach(memberId -> addCenter(centers, nodes.get(memberId)));
                placement.childIds().forEach(childId -> addCenter(centers, nodes.get(childId)));
                int fallback = MARGIN + i * (GROUP_MIN_WIDTH + GROUP_NODE_GAP) + GROUP_MIN_WIDTH / 2;
                int center = centers.isEmpty()
                        ? fallback
                        : (int) Math.round(centers.stream().mapToInt(Integer::intValue).average().orElse(fallback));
                preferred.add(new PreferredGroup(placement, center));
            }
            preferred.sort(Comparator.comparingInt(PreferredGroup::center));
            int cursor = MARGIN;
            for (PreferredGroup item : preferred) {
                AclNode node = nodes.get(item.placement().id());
                node.x = Math.max(cursor, item.center() - node.w / 2);
                node.y = baseY + (maxDepth - depth) * GROUP_LEVEL_GAP;
                cursor = node.x + node.w + GROUP_NODE_GAP;
            }
        }
    }

    private static void addCenter(List<Integer> centers, AclNode node) {
        if (node != null) centers.add(node.x + node.w / 2);
    }

    private static void assignParallelRoutes(List<AclEdge> edges) {
        Map<String, List<Integer>> routes = new LinkedHashMap<>();
        for (int i = 0; i < edges.size(); i++) {
            AclEdge edge = edges.get(i);
            if (edge.kind() == AclEdgeKind.INHERITANCE) continue;
            String first = edge.fromId().compareTo(edge.toId()) <= 0 ? edge.fromId() : edge.toId();
            String second = edge.fromId().compareTo(edge.toId()) <= 0 ? edge.toId() : edge.fromId();
            routes.computeIfAbsent(first + "\u0000" + second, ignored -> new ArrayList<>()).add(i);
        }
        for (List<Integer> indexes : routes.values()) {
            if (indexes.size() < 2) continue;
            for (int route = 0; route < indexes.size(); route++) {
                int edgeIndex = indexes.get(route);
                edges.set(edgeIndex, edges.get(edgeIndex).withRoute(route, indexes.size()));
            }
        }
    }

    private static AclEdgeKind linkKind(AclLinkType type) {
        return switch (type) {
            case ACQUAINTANCE -> AclEdgeKind.ACQUAINTANCE;
            case COMMUNICATION -> AclEdgeKind.COMMUNICATION;
            case AUTHORITY -> AclEdgeKind.AUTHORITY;
        };
    }

    private static String constraintTargetId(AclGroup group, String path,
                                             AclCardinalityConstraint constraint) {
        if (constraint.targetKind() == AclCardinalityTargetKind.ROLE) return roleId(constraint.targetName());
        if (constraint.targetKind() == AclCardinalityTargetKind.ENTITY) return entityId(constraint.targetName());
        return findSubgroupId(group, path, constraint.targetName());
    }

    private static String findSubgroupId(AclGroup group, String path, String name) {
        for (var subgroup : group.subgroups()) {
            String childPath = path + "/" + subgroup.group().name();
            if (subgroup.group().name().equals(name)) return groupId(childPath);
            String nested = findSubgroupId(subgroup.group(), childPath, name);
            if (nested != null) return nested;
        }
        return null;
    }

    private static AclNode node(String id, String label, AclNodeKind kind, String subtitle,
                                List<String> details) {
        AclNode node = new AclNode(id, label, kind, subtitle, details);
        int longest = Math.max(label.length(), details.stream().mapToInt(String::length).max().orElse(0));
        node.w = switch (kind) {
            case ROLE -> Math.max(ROLE_MIN_WIDTH, Math.min(350, longest * 7 + 42));
            case ENTITY -> Math.max(ENTITY_MIN_WIDTH, Math.min(390, longest * 7 + 28));
            case GROUP -> Math.max(GROUP_MIN_WIDTH, label.length() * 8 + 30);
        };
        node.h = switch (kind) {
            case ROLE -> 46 + (details.isEmpty() ? 0 : details.size() * DETAIL_HEIGHT + 12);
            case ENTITY -> 50 + Math.max(1, details.size()) * DETAIL_HEIGHT + 10;
            case GROUP -> 62;
        };
        return node;
    }

    private static List<String> attributeLines(List<AclAttribute> attributes) {
        return attributes.stream().map(attribute -> {
            StringBuilder line = new StringBuilder(attribute.name()).append(" : ").append(attribute.type().sourceName());
            if (attribute.required()) line.append(" required");
            if (attribute.mutable()) line.append(" mutable");
            attribute.defaultValue().ifPresent(value -> line.append(" = ").append(value));
            return line.toString();
        }).toList();
    }

    private static String cardinality(AclCardinality cardinality) {
        String max = cardinality.max().isPresent() ? Integer.toString(cardinality.max().getAsInt()) : "*";
        return cardinality.min() + ".." + max;
    }

    private static String roleId(String name) { return "role::" + name; }
    private static String entityId(String name) { return "entity::" + name; }
    private static String groupId(String path) { return "group::" + path; }

    private record RoleBounds(int right, int bottom) {}
    private record GroupPlacement(String id, int depth, List<String> memberIds, List<String> childIds) {}
    private record PreferredGroup(GroupPlacement placement, int center) {}
}
