package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

public record AclCompatibility(String fromRole, String toRole, AclScope scope,
                               boolean extendsSubgroups, boolean bidirectional) implements AclRoleRelation, Compatibility {
    public AclCompatibility {
        Objects.requireNonNull(fromRole, "fromRole");
        Objects.requireNonNull(toRole, "toRole");
        Objects.requireNonNull(scope, "scope");
    }
}
