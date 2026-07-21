package org.vnu.sme.goal.acl.mm;

import java.util.Arrays;
import java.util.Optional;

/** The only Role/Group -> Entity relation kind: ownership (aggregation). */
public enum AclRoleEntityRelationType {
    OWNS("owns");

    private final String sourceName;
    AclRoleEntityRelationType(String sourceName) { this.sourceName = sourceName; }
    public String sourceName() { return sourceName; }
    public static Optional<AclRoleEntityRelationType> fromSource(String source) {
        return Arrays.stream(values()).filter(v -> v.sourceName.equals(source)).findFirst();
    }
}
