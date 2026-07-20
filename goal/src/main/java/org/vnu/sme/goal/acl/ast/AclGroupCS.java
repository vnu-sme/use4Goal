package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclGroupCS(String name,
                         List<AclRoleMembershipCS> roles,
                         List<AclEntityMembershipCS> entities,
                         List<AclSubgroupMembershipCS> subgroups,
                         List<AclLinkCS> links,
                         List<AclCompatibilityCS> compatibilities,
                         List<AclRoleEntityRelationCS> roleEntityRelations,
                         List<AclCardinalityConstraintCS> cardinalityConstraints,
                         AclSourceLocationCS location) {
    public AclGroupCS {
        Objects.requireNonNull(name, "name");
        roles = List.copyOf(roles);
        entities = List.copyOf(entities);
        subgroups = List.copyOf(subgroups);
        links = List.copyOf(links);
        compatibilities = List.copyOf(compatibilities);
        roleEntityRelations = List.copyOf(roleEntityRelations);
        cardinalityConstraints = List.copyOf(cardinalityConstraints);
        Objects.requireNonNull(location, "location");
    }
}
