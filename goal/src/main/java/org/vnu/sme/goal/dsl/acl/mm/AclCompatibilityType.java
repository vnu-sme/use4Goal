package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Arrays;
import java.util.Optional;

public enum AclCompatibilityType {
    COMPATIBLE("compatible"),
    INCOMPATIBLE("incompatible");

    private final String sourceName;

    AclCompatibilityType(String sourceName) {
        this.sourceName = sourceName;
    }

    public String sourceName() {
        return sourceName;
    }

    public static Optional<AclCompatibilityType> fromSource(String source) {
        return Arrays.stream(values()).filter(value -> value.sourceName.equals(source)).findFirst();
    }
}
