package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclModelCS(String version, String name,
                         List<AclEnumCS> enums,
                         List<AclRoleCS> roles,
                         List<AclEntityCS> entities,
                         List<AclGroupCS> groups,
                         AclSourceLocationCS location) {
    public AclModelCS {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(name, "name");
        enums = List.copyOf(enums);
        roles = List.copyOf(roles);
        entities = List.copyOf(entities);
        groups = List.copyOf(groups);
        Objects.requireNonNull(location, "location");
    }
}
