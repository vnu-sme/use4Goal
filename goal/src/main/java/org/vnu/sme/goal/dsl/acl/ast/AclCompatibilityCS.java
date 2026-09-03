package org.vnu.sme.goal.dsl.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclCompatibilityCS(String fromRole, String toRole, boolean bidirectionalArrow,
                                 String groupName, List<AclLinkOptionCS> options, AclSourceLocationCS location) {
    public AclCompatibilityCS {
        Objects.requireNonNull(fromRole, "fromRole");
        Objects.requireNonNull(toRole, "toRole");
        options = List.copyOf(options);
        Objects.requireNonNull(location, "location");
    }
}
