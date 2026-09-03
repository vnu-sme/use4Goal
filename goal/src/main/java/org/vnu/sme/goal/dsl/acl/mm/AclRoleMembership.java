package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

public record AclRoleMembership(String roleName, AclCardinality cardinality) {
    public AclRoleMembership {
        Objects.requireNonNull(roleName, "roleName");
        Objects.requireNonNull(cardinality, "cardinality");
    }
}
