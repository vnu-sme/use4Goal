package org.vnu.sme.goal.acl.ast;

import java.util.List;

public record AclEntityCS(String name, List<AclAttributeCS> attributes) {
    public AclEntityCS {
        attributes = List.copyOf(attributes);
    }
}
