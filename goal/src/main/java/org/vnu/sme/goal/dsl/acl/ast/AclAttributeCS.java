package org.vnu.sme.goal.dsl.acl.ast;

import java.util.Objects;
import java.util.Optional;

public record AclAttributeCS(String name, String typeName, boolean optional,
                             boolean explicitRequired, boolean mutable,
                             Optional<String> defaultValue, AclSourceLocationCS location) {
    public AclAttributeCS {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(typeName, "typeName");
        defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
        Objects.requireNonNull(location, "location");
    }
}
