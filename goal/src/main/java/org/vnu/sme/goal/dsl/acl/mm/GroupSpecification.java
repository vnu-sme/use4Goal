package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;

/** YAML GroupSpecification role/entity/subgroup container. */
public interface GroupSpecification {
    String name();
    List<AclRoleMembership> roles();
    List<AclEntityMembership> entities();
    List<AclSubgroupMembership> subgroups();
}
