package org.vnu.sme.goal.acl.mm;

import java.util.List;
import java.util.Objects;

public record AclGroup(String name,
                       List<AclRoleMembership> roles,
                       List<AclEntityMembership> entities,
                       List<AclSubgroupMembership> subgroups,
                       List<AclLink> links,
                       List<AclCompatibility> compatibilities,
                       List<AclRoleEntityRelation> roleEntityRelations,
                       List<AclCardinalityConstraint> cardinalityConstraints) implements GroupSpecification {
    public AclGroup {
        Objects.requireNonNull(name, "name");
        roles = List.copyOf(roles);
        entities = List.copyOf(entities);
        subgroups = List.copyOf(subgroups);
        links = List.copyOf(links);
        compatibilities = List.copyOf(compatibilities);
        roleEntityRelations = List.copyOf(roleEntityRelations);
        cardinalityConstraints = List.copyOf(cardinalityConstraints);
    }
}
