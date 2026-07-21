package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

/** Group ownership of a Role or child Group. Rendered solid with a small square at the parent Group. */
public record AclOwner(String sourceGroup, String target, AclCardinality multiplicity) {
    public AclOwner {
        Objects.requireNonNull(sourceGroup, "sourceGroup");
        Objects.requireNonNull(target, "target"); Objects.requireNonNull(multiplicity, "multiplicity");
    }
}
