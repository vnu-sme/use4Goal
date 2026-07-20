package org.vnu.sme.goal.acl.ast;

import java.util.Objects;

public record AclSubgroupMembershipCS(AclGroupCS group, AclCardinalityCS cardinality,
                                      AclSourceLocationCS location) {
    public AclSubgroupMembershipCS {
        Objects.requireNonNull(group, "group");
        Objects.requireNonNull(cardinality, "cardinality");
        Objects.requireNonNull(location, "location");
    }
}
