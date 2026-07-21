package org.vnu.sme.goal.acl.ast;

import java.util.Objects;

public record AclGroupMemberCS(String type, AclCardinalityCS multiplicity,
                               AclSourceLocationCS location) {
    public AclGroupMemberCS {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
        Objects.requireNonNull(location, "location");
    }
}
