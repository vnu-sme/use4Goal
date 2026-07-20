package org.vnu.sme.goal.acl.mm;

import java.util.Arrays;
import java.util.Optional;

public enum AclRoleEntityRelationType {
    CREATES("creates"), READS("reads"), WRITES("writes"), USES("uses"),
    OWNS("owns"), PROVIDES("provides"), CONSUMES("consumes"), PARTICIPATES_IN("participates-in");

    private final String sourceName;
    AclRoleEntityRelationType(String sourceName) { this.sourceName = sourceName; }
    public String sourceName() { return sourceName; }
    public static Optional<AclRoleEntityRelationType> fromSource(String source) {
        return Arrays.stream(values()).filter(v -> v.sourceName.equals(source)).findFirst();
    }
}
