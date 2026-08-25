package org.vnu.sme.goal.dsl.aol.ast;

import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;

/** One object in sigma_Class(r), where r is an ACL Role. */
public record AolRoleInstanceCS(String roleType, String instanceId,
                                List<AolAttributeValueCS> attributeValues,
                                AclSourceLocationCS location) {
    public AolRoleInstanceCS {
        Objects.requireNonNull(roleType, "roleType");
        Objects.requireNonNull(instanceId, "instanceId");
        attributeValues = List.copyOf(attributeValues);
        Objects.requireNonNull(location, "location");
    }
}
