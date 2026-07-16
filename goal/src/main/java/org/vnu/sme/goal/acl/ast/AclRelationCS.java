package org.vnu.sme.goal.acl.ast;

import java.util.List;

public record AclRelationCS(String kind, String name, List<AclEndpointCS> endpoints) {
    public AclRelationCS {
        endpoints = List.copyOf(endpoints);
    }
}
