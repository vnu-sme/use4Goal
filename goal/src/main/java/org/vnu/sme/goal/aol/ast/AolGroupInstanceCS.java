package org.vnu.sme.goal.aol.ast;

import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.acl.ast.AclSourceLocationCS;

public record AolGroupInstanceCS(String typeName, String instanceId,
                                 List<AolGroupInstanceCS> subgroups,
                                 List<AolPlayCS> plays,
                                 List<AolEntityInstanceCS> entities,
                                 AclSourceLocationCS location) {
    public AolGroupInstanceCS {
        Objects.requireNonNull(typeName, "typeName");
        Objects.requireNonNull(instanceId, "instanceId");
        subgroups = List.copyOf(subgroups);
        plays = List.copyOf(plays);
        entities = List.copyOf(entities);
        Objects.requireNonNull(location, "location");
    }
}
