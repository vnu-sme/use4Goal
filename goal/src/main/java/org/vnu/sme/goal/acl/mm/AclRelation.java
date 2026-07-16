package org.vnu.sme.goal.acl.mm;

import java.util.List;

public record AclRelation(String kind, String name, List<AclEndpoint> endpoints) {
    public AclRelation {
        endpoints = List.copyOf(endpoints);
    }
}
