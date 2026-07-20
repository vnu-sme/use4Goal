package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclEnumCS(String name, List<String> literals, AclSourceLocationCS location) {
    public AclEnumCS {
        Objects.requireNonNull(name, "name");
        literals = List.copyOf(literals);
        Objects.requireNonNull(location, "location");
    }
}
