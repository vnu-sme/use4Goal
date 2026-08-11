package org.vnu.sme.goal.dsl.acl.ast;

import java.util.Objects;

public record AclCardinalityConstraintCS(String targetKind, String targetName,
                                         AclCardinalityCS cardinality, AclSourceLocationCS location) {
    public AclCardinalityConstraintCS {
        Objects.requireNonNull(targetKind, "targetKind");
        Objects.requireNonNull(targetName, "targetName");
        Objects.requireNonNull(cardinality, "cardinality");
        Objects.requireNonNull(location, "location");
    }
}
