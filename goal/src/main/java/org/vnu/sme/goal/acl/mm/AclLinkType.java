package org.vnu.sme.goal.acl.mm;

import java.util.Arrays;
import java.util.Optional;

public enum AclLinkType {
    AUTHORITY("authority"),
    COMMUNICATION("communication"),
    ACQUAINTANCE("acquaintance");

    private final String sourceName;

    AclLinkType(String sourceName) {
        this.sourceName = sourceName;
    }

    public String sourceName() {
        return sourceName;
    }

    public static Optional<AclLinkType> fromSource(String source) {
        return Arrays.stream(values()).filter(value -> value.sourceName.equals(source)).findFirst();
    }
}
