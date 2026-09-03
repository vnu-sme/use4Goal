package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

/** ACL-specific relation; deliberately not a MOISE Link/RoleRelation. */
public record AclRoleEntityRelation(String name, AclRole sourceRole, AclEntity targetEntity,
                                    AclRoleEntityRelationType type, AclScope scope,
                                    boolean extendsSubgroups) implements RoleEntityRelation {
    public AclRoleEntityRelation {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sourceRole, "sourceRole");
        Objects.requireNonNull(targetEntity, "targetEntity");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(scope, "scope");
    }
}
