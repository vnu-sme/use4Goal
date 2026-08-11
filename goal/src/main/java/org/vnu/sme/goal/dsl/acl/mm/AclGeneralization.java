package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

public record AclGeneralization(String specific, String general) {
    public AclGeneralization {
        Objects.requireNonNull(specific, "specific"); Objects.requireNonNull(general, "general");
    }
}
