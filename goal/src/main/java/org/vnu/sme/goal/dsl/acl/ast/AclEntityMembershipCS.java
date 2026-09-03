package org.vnu.sme.goal.dsl.acl.ast;

import java.util.Objects;

public record AclEntityMembershipCS(String entityName, AclCardinalityCS cardinality,
                                    AclSourceLocationCS location) {
    public AclEntityMembershipCS {
        Objects.requireNonNull(entityName, "entityName");
        Objects.requireNonNull(cardinality, "cardinality");
        Objects.requireNonNull(location, "location");
    }
}
