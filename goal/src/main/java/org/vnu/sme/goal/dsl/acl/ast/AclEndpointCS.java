package org.vnu.sme.goal.dsl.acl.ast;

import java.util.Objects;
import java.util.Optional;

public record AclEndpointCS(String type, AclCardinalityCS multiplicity,
                            Optional<String> roleName,
                            AclSourceLocationCS location) {
    public AclEndpointCS {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
        roleName = Objects.requireNonNull(roleName, "roleName");
        Objects.requireNonNull(location, "location");
    }
}
