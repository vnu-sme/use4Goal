package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclGroupCS(String name, List<AclAttributeCS> attributes,
                         List<AclGroupMemberCS> members,
                         List<AclCompatibilityCS> compatibilities,
                         AclSourceLocationCS location) {
    public AclGroupCS {
        Objects.requireNonNull(name, "name"); attributes = List.copyOf(attributes);
        members = List.copyOf(members); compatibilities = List.copyOf(compatibilities);
        Objects.requireNonNull(location, "location");
    }
}
