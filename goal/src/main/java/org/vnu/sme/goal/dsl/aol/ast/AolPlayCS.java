package org.vnu.sme.goal.dsl.aol.ast;

import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;

public record AolPlayCS(String roleType, String instanceId, String agentId,
                        List<AolAttributeValueCS> attributeValues, AclSourceLocationCS location) {
    public AolPlayCS {
        Objects.requireNonNull(roleType, "roleType");
        Objects.requireNonNull(instanceId, "instanceId");
        Objects.requireNonNull(agentId, "agentId");
        attributeValues = List.copyOf(attributeValues);
        Objects.requireNonNull(location, "location");
    }
}
