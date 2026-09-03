package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;
import java.util.Optional;

public record AclEndpoint(String type, AclCardinality multiplicity, Optional<String> roleName) {
    public AclEndpoint {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
        roleName = Objects.requireNonNull(roleName, "roleName");
    }
}
