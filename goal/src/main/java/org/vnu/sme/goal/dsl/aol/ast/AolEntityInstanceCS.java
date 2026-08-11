package org.vnu.sme.goal.dsl.aol.ast;

import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;

public record AolEntityInstanceCS(String entityType, String instanceId,
                                  List<AolAttributeValueCS> attributeValues, AclSourceLocationCS location) {
    public AolEntityInstanceCS {
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(instanceId, "instanceId");
        attributeValues = List.copyOf(attributeValues);
        Objects.requireNonNull(location, "location");
    }
}
