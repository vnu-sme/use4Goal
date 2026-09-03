package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;
import java.util.Objects;

public record AclRole(String name, List<String> parentRoles, List<AclAttribute> attributes)
        implements AclCardinalityTarget, RoleDefinition {
    public AclRole {
        Objects.requireNonNull(name, "name");
        parentRoles = List.copyOf(parentRoles);
        attributes = List.copyOf(attributes);
    }

    @Override public String id() { return name; }

    /**
     * Compatibility shim for downstream modules awaiting migration from legacy
     * abstract roles. ACL Roles are always concrete.
     */
    @Deprecated(forRemoval = true)
    public boolean isAbstract() { return false; }
}
