package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclEntityCS(String name, List<AclAttributeCS> attributes, AclSourceLocationCS location) {
    public AclEntityCS {
        Objects.requireNonNull(name, "name");
        attributes = List.copyOf(attributes);
        Objects.requireNonNull(location, "location");
    }
}
