package org.vnu.sme.goal.acl.ast;

import java.util.List;

public record AclGroupCS(String name,
                         String specializes,
                         List<AclAttributeCS> attributes,
                         List<AclGroupMemberCS> members) {
    public AclGroupCS {
        attributes = List.copyOf(attributes);
        members = List.copyOf(members);
    }
}
