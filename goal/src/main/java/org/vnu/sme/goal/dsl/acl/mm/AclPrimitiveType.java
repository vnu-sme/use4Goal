package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Arrays;
import java.util.Optional;

public enum AclPrimitiveType implements AclDataType {
    STRING("String"),
    INTEGER("Integer"),
    REAL("Real"),
    BOOLEAN("Boolean");

    private final String sourceName;

    AclPrimitiveType(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return sourceName;
    }

    public String sourceName() {
        return sourceName;
    }

    public static Optional<AclPrimitiveType> fromSource(String source) {
        return Arrays.stream(values()).filter(value -> value.sourceName.equals(source)).findFirst();
    }
}
