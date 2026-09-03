package org.vnu.sme.goal.dsl.acl.mm;

import java.util.Objects;

public record AclSubgroupMembership(AclGroup group, AclCardinality cardinality)
        implements AclCardinalityTarget, SubGroup {
    public AclSubgroupMembership {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(cardinality, "cardinality");
    }

    @Override public String id() { return group.name(); }
}
