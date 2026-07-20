package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

public record AclCardinalityConstraint(AclCardinalityTargetKind targetKind, CardinalityTarget target,
                                       AclCardinality cardinality) implements CardinalityConstraint {
    public AclCardinalityConstraint {
        Objects.requireNonNull(targetKind, "targetKind");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(cardinality, "cardinality");
    }

    public String targetName() { return target.id(); }
}
