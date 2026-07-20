package org.vnu.sme.goal.acl.view;

import java.util.Objects;

import org.vnu.sme.goal.acl.mm.AclScope;

public record AclEdge(String fromId, String toId, AclEdgeKind kind, String cardinality,
                      String label, AclScope scope, boolean extendsSubgroups, boolean bidirectional,
                      int routeIndex, int routeCount) {
    public AclEdge {
        Objects.requireNonNull(fromId, "fromId");
        Objects.requireNonNull(toId, "toId");
        Objects.requireNonNull(kind, "kind");
        if (kind.isRoleLink() || kind.isRoleEntityRelation()) Objects.requireNonNull(scope, "scope");
        if (routeIndex < 0 || routeCount < 1 || routeIndex >= routeCount) {
            throw new IllegalArgumentException("Invalid ACL edge route");
        }
    }

    public static AclEdge inheritance(String childId, String parentId) {
        return new AclEdge(childId, parentId, AclEdgeKind.INHERITANCE,
                null, null, null, false, false, 0, 1);
    }

    public static AclEdge composition(String memberId, String groupId,
                                      AclEdgeKind kind, String cardinality) {
        if (!kind.isComposition() || kind == AclEdgeKind.GROUP_CARDINALITY) {
            throw new IllegalArgumentException("Membership edge requires a composition kind");
        }
        return new AclEdge(memberId, groupId, kind, cardinality,
                null, null, false, false, 0, 1);
    }

    public static AclEdge roleLink(String fromId, String toId, AclEdgeKind kind,
                                   AclScope scope, boolean extendsSubgroups,
                                   boolean bidirectional) {
        if (!kind.isRoleLink()) throw new IllegalArgumentException("Role link kind expected");
        return new AclEdge(fromId, toId, kind, null, null, scope,
                extendsSubgroups, bidirectional, 0, 1);
    }

    public static AclEdge roleEntityRelation(String roleId, String entityId, String label,
                                             AclScope scope, boolean extendsSubgroups) {
        return new AclEdge(roleId, entityId, AclEdgeKind.ROLE_ENTITY_RELATION, null,
                label, scope, extendsSubgroups, false, 0, 1);
    }

    public static AclEdge groupCardinality(String targetId, String groupId, String cardinality) {
        return new AclEdge(targetId, groupId, AclEdgeKind.GROUP_CARDINALITY,
                cardinality, null, null, false, false, 0, 1);
    }

    public AclEdge withRoute(int index, int count) {
        return new AclEdge(fromId, toId, kind, cardinality, label, scope,
                extendsSubgroups, bidirectional, index, count);
    }
}
