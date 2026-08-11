package org.vnu.sme.goal.dsl.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclRoleCS(String name, List<String> parentRoles, List<AclAttributeCS> attributes,
                        AclSourceLocationCS location) {
    public AclRoleCS {
        Objects.requireNonNull(name, "name");
        parentRoles = List.copyOf(parentRoles);
        attributes = List.copyOf(attributes);
        Objects.requireNonNull(location, "location");
    }
}
