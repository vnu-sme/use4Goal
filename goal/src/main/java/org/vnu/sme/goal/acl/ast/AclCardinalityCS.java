package org.vnu.sme.goal.acl.ast;

import java.util.Objects;
import java.util.Optional;

public record AclCardinalityCS(String min, Optional<String> max, AclSourceLocationCS location) {
    public AclCardinalityCS {
        Objects.requireNonNull(min, "min");
        max = Objects.requireNonNull(max, "max");
        Objects.requireNonNull(location, "location");
    }
}
