package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

/** An opaque domain data type declared by {@code datatype Name;}. */
public record AclNamedDataType(String sourceName) implements AclDataType {
    public AclNamedDataType {
        Objects.requireNonNull(sourceName, "sourceName");
    }
}
