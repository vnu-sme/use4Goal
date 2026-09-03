package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

public record AclGroupMember(String type, AclCardinality multiplicity) {
    public AclGroupMember {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
    }
}
