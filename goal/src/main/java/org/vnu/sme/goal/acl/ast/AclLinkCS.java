package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclLinkCS(String fromRole, String toRole, String type, boolean bidirectionalArrow,
                        List<AclLinkOptionCS> options, AclSourceLocationCS location) {
    public AclLinkCS {
        Objects.requireNonNull(fromRole, "fromRole");
        Objects.requireNonNull(toRole, "toRole");
        Objects.requireNonNull(type, "type");
        options = List.copyOf(options);
        Objects.requireNonNull(location, "location");
    }
}
