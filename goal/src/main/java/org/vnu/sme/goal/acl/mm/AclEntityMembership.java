package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

public record AclEntityMembership(String entityName, AclCardinality cardinality) implements GroupEntity {
    public AclEntityMembership {
        Objects.requireNonNull(entityName, "entityName");
        Objects.requireNonNull(cardinality, "cardinality");
    }
}
