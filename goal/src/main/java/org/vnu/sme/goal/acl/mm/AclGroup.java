package org.vnu.sme.goal.acl.mm;

import java.util.List;

public record AclGroup(String name,
                       String specializes,
                       List<AclAttribute> attributes,
                       List<AclGroupMember> members) {
    public AclGroup {
        attributes = List.copyOf(attributes);
        members = List.copyOf(members);
    }
}
