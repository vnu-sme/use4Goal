package org.vnu.sme.goal.acl.mm;

import java.util.List;
import java.util.Objects;

public record AclEntity(String name, List<AclAttribute> attributes)
        implements AclCardinalityTarget, EntityDefinition {
    public AclEntity {
        Objects.requireNonNull(name, "name");
        attributes = List.copyOf(attributes);
    }

    @Override public String id() { return name; }
}
