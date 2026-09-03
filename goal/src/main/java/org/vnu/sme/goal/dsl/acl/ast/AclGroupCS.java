package org.vnu.sme.goal.dsl.acl.ast;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record AclGroupCS(String name, Optional<String> specializes,
                         List<AclAttributeCS> attributes,
                         List<AclGroupMemberCS> members,
                         List<AclCompatibilityCS> compatibilities,
                         boolean organizationalContext,
                         AclSourceLocationCS location) {
    public AclGroupCS(String name, Optional<String> specializes,
                      List<AclAttributeCS> attributes,
                      List<AclGroupMemberCS> members,
                      List<AclCompatibilityCS> compatibilities,
                      AclSourceLocationCS location) {
        this(name, specializes, attributes, members, compatibilities, false, location);
    }

    public AclGroupCS {
        Objects.requireNonNull(name, "name"); Objects.requireNonNull(specializes, "specializes");
        attributes = List.copyOf(attributes);
        members = List.copyOf(members); compatibilities = List.copyOf(compatibilities);
        Objects.requireNonNull(location, "location");
    }
}
