package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

public record AclEndpoint(String type, AclCardinality multiplicity) {
    public AclEndpoint {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
    }
}
