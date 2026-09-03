package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Arrays;
import java.util.Optional;

public enum AclCardinalityTargetKind {
    ROLE("role"),
    ENTITY("entity"),
    SUBGROUP("subgroup");

    private final String sourceName;

    AclCardinalityTargetKind(String sourceName) {
        this.sourceName = sourceName;
    }

    public String sourceName() {
        return sourceName;
    }

    public static Optional<AclCardinalityTargetKind> fromSource(String source) {
        return Arrays.stream(values()).filter(value -> value.sourceName.equals(source)).findFirst();
    }
}
