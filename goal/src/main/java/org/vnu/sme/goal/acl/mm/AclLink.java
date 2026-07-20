package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

public record AclLink(String fromRole, String toRole, AclLinkType type, AclScope scope,
                      boolean extendsSubgroups, boolean bidirectional) implements AclRoleRelation, Link {
    public AclLink {
        Objects.requireNonNull(fromRole, "fromRole");
        Objects.requireNonNull(toRole, "toRole");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(scope, "scope");
    }
}
