package org.vnu.sme.goal.acl.mm;

import java.util.Arrays;
import java.util.Optional;

public enum AclScope {
    INTRA_GROUP("intra-group"),
    INTER_GROUP("inter-group");

    private final String sourceName;

    AclScope(String sourceName) {
        this.sourceName = sourceName;
    }

    public String sourceName() {
        return sourceName;
    }

    public static Optional<AclScope> fromSource(String source) {
        return Arrays.stream(values()).filter(value -> value.sourceName.equals(source)).findFirst();
    }
}
