package org.vnu.sme.goal.acl.mm;

import java.util.List;

public record AclActor(String kind,
                       boolean isAbstract,
                       String name,
                       String specializes,
                       List<AclAttribute> attributes) {
    public AclActor {
        attributes = List.copyOf(attributes);
    }
}
