package org.vnu.sme.goal.acl.ast;

import java.util.Objects;

public record AclRoleMembershipCS(String roleName, AclCardinalityCS cardinality,
                                  AclSourceLocationCS location) {
    public AclRoleMembershipCS {
        Objects.requireNonNull(roleName, "roleName");
        Objects.requireNonNull(cardinality, "cardinality");
        Objects.requireNonNull(location, "location");
    }
}
