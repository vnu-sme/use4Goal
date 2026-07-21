package org.vnu.sme.goal.acl.ast;

import java.util.List;
import java.util.Objects;

public record AclRelationCS(String kind, String name, List<AclEndpointCS> endpoints,
                            AclSourceLocationCS location) {
    public AclRelationCS {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(name, "name");
        endpoints = List.copyOf(endpoints);
        Objects.requireNonNull(location, "location");
    }
}
