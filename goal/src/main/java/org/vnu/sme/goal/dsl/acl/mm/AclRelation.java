package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;
import java.util.List;

/** Entity relationship: association, aggregation, or composition. */
public record AclRelation(RelationKind kind, String name, List<AclEndpoint> endpoints) {
    public AclRelation {
        Objects.requireNonNull(kind, "kind"); Objects.requireNonNull(name, "name");
        endpoints = List.copyOf(endpoints);
        if (endpoints.size() < 2) throw new IllegalArgumentException("An association needs at least two ends");
    }
    public AclRelation(RelationKind kind, String name, AclEndpoint source, AclEndpoint target) {
        this(kind, name, List.of(source, target));
    }
    /** Legacy binary consumers must not silently truncate an n-ary association. */
    public AclEndpoint source() { requireBinary(); return endpoints.get(0); }
    public AclEndpoint target() { requireBinary(); return endpoints.get(1); }
    private void requireBinary() {
        if (endpoints.size() != 2)
            throw new UnsupportedOperationException("Association '" + name
                    + "' has " + endpoints.size() + " ends; this backend only supports binary associations");
    }
}
