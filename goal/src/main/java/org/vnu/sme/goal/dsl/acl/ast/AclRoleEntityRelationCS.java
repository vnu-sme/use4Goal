package org.vnu.sme.goal.dsl.acl.ast;

import java.util.List;
import java.util.Objects;

/** Concrete-syntax node for the ACL-only Role -> Entity relation. */
public record AclRoleEntityRelationCS(String name, String sourceRole, String targetEntity,
                                      String type, List<AclLinkOptionCS> options,
                                      AclSourceLocationCS location) {
    public AclRoleEntityRelationCS {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(sourceRole, "sourceRole");
        Objects.requireNonNull(targetEntity, "targetEntity");
        Objects.requireNonNull(type, "type");
        options = List.copyOf(options);
        Objects.requireNonNull(location, "location");
    }
}
