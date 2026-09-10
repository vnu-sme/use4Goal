package org.vnu.sme.goal.dsl.acl.ast;

import java.util.Objects;

/** Concrete-syntax declaration of an opaque, named ACL data type. */
public record AclDataTypeCS(String name, AclSourceLocationCS location) {
    public AclDataTypeCS {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
    }
}
