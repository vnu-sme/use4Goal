package org.vnu.sme.goal.acl.mm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record AclEntity(String name, Optional<String> specializes, List<AclAttribute> attributes)
        implements AclCardinalityTarget, EntityDefinition {
    public AclEntity {
        Objects.requireNonNull(name, "name"); specializes = Objects.requireNonNull(specializes, "specializes");
        attributes = List.copyOf(attributes);
    }
    public AclEntity(String name, List<AclAttribute> attributes) { this(name, Optional.empty(), attributes); }
    @Override public String id() { return name; }
}
