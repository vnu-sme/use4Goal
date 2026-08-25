package org.vnu.sme.goal.dsl.acl.ast;

import java.util.Objects;

/** Concrete-syntax representation of an OCL invariant embedded in ACL. */
public record AclInvariantCS(String contextType, String name, String expression,
                             AclSourceLocationCS location) {
    public AclInvariantCS {
        Objects.requireNonNull(contextType, "contextType");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(expression, "expression");
        Objects.requireNonNull(location, "location");
    }
}
