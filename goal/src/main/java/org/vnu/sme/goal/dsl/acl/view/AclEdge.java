package org.vnu.sme.goal.dsl.acl.view;

import java.util.Objects;

public record AclEdge(String fromId, String toId, AclEdgeKind kind,
                      String sourceCardinality, String targetCardinality, String label,
                      boolean bidirectional, boolean interGroup, int routeIndex, int routeCount) {
    public AclEdge {
        Objects.requireNonNull(fromId, "fromId");
        Objects.requireNonNull(toId, "toId");
        Objects.requireNonNull(kind, "kind");
        if (routeIndex < 0 || routeCount < 1 || routeIndex >= routeCount) {
            throw new IllegalArgumentException("Invalid ACL edge route");
        }
    }

    public static AclEdge of(String from, String to, AclEdgeKind kind,
                             String sourceCardinality, String targetCardinality,
                             String label, boolean bidirectional, boolean interGroup) {
        return new AclEdge(from, to, kind, sourceCardinality, targetCardinality,
                label, bidirectional, interGroup, 0, 1);
    }

    public AclEdge withRoute(int index, int count) {
        return new AclEdge(fromId, toId, kind, sourceCardinality, targetCardinality,
                label, bidirectional, interGroup, index, count);
    }
}
