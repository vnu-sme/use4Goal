package org.vnu.sme.goal.acl.ast;

import java.util.List;

public record AclActorCS(String kind,
                         boolean isAbstract,
                         String name,
                         String specializes,
                         List<AclAttributeCS> attributes) {
    public AclActorCS {
        attributes = List.copyOf(attributes);
    }
}
