package org.vnu.sme.goal.acl.ast;

import java.util.Objects;

public record AclEndpointCS(String type, AclCardinalityCS multiplicity,
                            AclSourceLocationCS location) {
    public AclEndpointCS {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
        Objects.requireNonNull(location, "location");
    }
}
