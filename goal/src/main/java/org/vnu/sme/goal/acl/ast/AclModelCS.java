package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclModelCS(String version, String name, List<AclEnumCS> enums,
                         List<AclEntityCS> entities, List<AclRoleCS> roles,
                         List<AclRelationCS> relations, List<AclGroupCS> groups,
                         List<AclCompatibilityCS> compatibilities,
                         AclSourceLocationCS location) {
    public AclModelCS {
        Objects.requireNonNull(version, "version"); Objects.requireNonNull(name, "name");
        enums = List.copyOf(enums); entities = List.copyOf(entities); roles = List.copyOf(roles);
        relations = List.copyOf(relations); groups = List.copyOf(groups);
        compatibilities = List.copyOf(compatibilities); Objects.requireNonNull(location, "location");
    }
}
