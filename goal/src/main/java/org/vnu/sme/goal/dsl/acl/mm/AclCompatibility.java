package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

public record AclCompatibility(String fromRole, String toRole, AclCompatibilityType type, AclScope scope,
                               boolean extendsSubgroups, boolean bidirectional, String groupName) implements AclRoleRelation, Compatibility {
    public AclCompatibility {
        Objects.requireNonNull(fromRole, "fromRole");
        Objects.requireNonNull(toRole, "toRole");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(scope, "scope");
        Objects.requireNonNull(groupName, "groupName");
    }
}
