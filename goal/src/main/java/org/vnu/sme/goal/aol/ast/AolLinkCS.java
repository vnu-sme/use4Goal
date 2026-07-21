package org.vnu.sme.goal.aol.ast;

import java.util.List;
import java.util.Objects;

import org.vnu.sme.goal.acl.ast.AclSourceLocationCS;

/** Instantiates a named ACL relation from one source instance id to one-or-more target instance ids. */
public record AolLinkCS(String relationName, String sourceInstanceId, List<String> targetInstanceIds,
                        AclSourceLocationCS location) {
    public AolLinkCS {
        Objects.requireNonNull(relationName, "relationName");
        Objects.requireNonNull(sourceInstanceId, "sourceInstanceId");
        targetInstanceIds = List.copyOf(targetInstanceIds);
        Objects.requireNonNull(location, "location");
    }
}
