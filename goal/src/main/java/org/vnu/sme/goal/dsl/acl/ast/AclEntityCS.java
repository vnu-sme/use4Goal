package org.vnu.sme.goal.dsl.acl.ast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record AclEntityCS(String name, Optional<String> specializes,
                          List<AclAttributeCS> attributes, AclSourceLocationCS location) {
    public AclEntityCS {
        Objects.requireNonNull(name, "name");
        specializes = Objects.requireNonNull(specializes, "specializes");
        attributes = List.copyOf(attributes);
        Objects.requireNonNull(location, "location");
    }
}
