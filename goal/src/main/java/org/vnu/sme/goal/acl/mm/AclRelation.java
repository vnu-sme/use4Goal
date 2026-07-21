package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

/** Entity relationship: association, aggregation, or composition. */
public record AclRelation(RelationKind kind, String name, AclEndpoint source, AclEndpoint target) {
    public AclRelation {
        Objects.requireNonNull(kind, "kind"); Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source"); Objects.requireNonNull(target, "target");
    }
    public java.util.List<AclEndpoint> endpoints() { return java.util.List.of(source, target); }
}
