package org.vnu.sme.goal.acl.mm;

import java.util.List;

public record AclEntity(String name, List<AclAttribute> attributes) {
    public AclEntity {
        attributes = List.copyOf(attributes);
    }
}
