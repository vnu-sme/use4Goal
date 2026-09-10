package org.vnu.sme.goal.dsl.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclModelCS(String version, String name, List<AclEnumCS> enums,
                         List<AclDataTypeCS> dataTypes,
                         List<AclEntityCS> entities, List<AclRoleCS> roles,
                         List<AclRelationCS> relations, List<AclGroupCS> groups,
                         List<AclCompatibilityCS> compatibilities,
                         List<AclInvariantCS> invariants,
                         AclSourceLocationCS location) {
    public AclModelCS {
        Objects.requireNonNull(version, "version"); Objects.requireNonNull(name, "name");
        enums = List.copyOf(enums); dataTypes = List.copyOf(dataTypes);
        entities = List.copyOf(entities); roles = List.copyOf(roles);
        relations = List.copyOf(relations); groups = List.copyOf(groups);
        compatibilities = List.copyOf(compatibilities); invariants = List.copyOf(invariants);
        Objects.requireNonNull(location, "location");
    }
}
