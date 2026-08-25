package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

/** A named OCL state predicate evaluated over every ACL object in its context. */
public record AclInvariant(String contextType, String name, String expression) {
    public AclInvariant {
        Objects.requireNonNull(contextType, "contextType");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(expression, "expression");
    }

    public String qualifiedName() {
        return contextType + "::" + name;
    }
}
