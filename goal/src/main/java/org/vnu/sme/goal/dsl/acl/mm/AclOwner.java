package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

/** @deprecated Projection of a Group composition retained for legacy translators. */
@Deprecated(forRemoval = false)
public record AclOwner(String sourceGroup, String target, AclCardinality multiplicity) {
    public AclOwner {
        Objects.requireNonNull(sourceGroup, "sourceGroup");
        Objects.requireNonNull(target, "target"); Objects.requireNonNull(multiplicity, "multiplicity");
    }
}
