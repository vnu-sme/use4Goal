package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

public record AclGroupMember(String type, AclCardinality multiplicity) {
    public AclGroupMember {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(multiplicity, "multiplicity");
    }
}
