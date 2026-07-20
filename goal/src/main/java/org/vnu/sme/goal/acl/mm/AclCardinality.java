package org.vnu.sme.goal.acl.mm;

import java.util.OptionalInt;

public record AclCardinality(int min, OptionalInt max) implements Cardinality {
    public AclCardinality {
        if (min < 0) throw new IllegalArgumentException("minimum cardinality must be non-negative");
        if (max.isPresent() && max.getAsInt() < min) {
            throw new IllegalArgumentException("maximum cardinality must be greater than or equal to minimum");
        }
    }

    public static AclCardinality bounded(int min, int max) {
        return new AclCardinality(min, OptionalInt.of(max));
    }

    public static AclCardinality unlimited(int min) {
        return new AclCardinality(min, OptionalInt.empty());
    }
}
