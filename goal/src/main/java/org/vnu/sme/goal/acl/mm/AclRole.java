package org.vnu.sme.goal.acl.mm;

import java.util.List;
import java.util.Objects;

public record AclRole(String name, boolean isAbstract, List<String> parentRoles, List<AclAttribute> attributes)
        implements AclCardinalityTarget, RoleDefinition {
    public AclRole {
        Objects.requireNonNull(name, "name");
        parentRoles = List.copyOf(parentRoles);
        attributes = List.copyOf(attributes);
    }

    @Override public String id() { return name; }
}
