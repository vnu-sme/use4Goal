package org.vnu.sme.goal.dsl.aol.ast;

import java.util.Objects;
import java.util.List;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;

public record AolAgentCS(String name, String profileRole, List<AolAttributeValueCS> attributeValues,
                         AclSourceLocationCS location) {
    public AolAgentCS {
        Objects.requireNonNull(name, "name");
        attributeValues = List.copyOf(attributeValues);
        Objects.requireNonNull(location, "location");
    }
}
