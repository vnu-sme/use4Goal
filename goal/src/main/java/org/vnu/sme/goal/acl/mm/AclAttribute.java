package org.vnu.sme.goal.acl.mm;

import java.util.Objects;
import java.util.Optional;

public record AclAttribute(String name, AclDataType type, boolean required, boolean mutable,
                           Optional<String> defaultValue) implements AttributeDefinition {
    public AclAttribute {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        defaultValue = Objects.requireNonNull(defaultValue, "defaultValue");
    }
}
