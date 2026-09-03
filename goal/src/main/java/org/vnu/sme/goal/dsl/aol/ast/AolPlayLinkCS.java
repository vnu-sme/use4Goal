package org.vnu.sme.goal.dsl.aol.ast;

import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;

/** One sigma_Play(parentRole, childRole) link between Role object identifiers. */
public record AolPlayLinkCS(String parentInstanceId, String childInstanceId,
                            AclSourceLocationCS location) {
    public AolPlayLinkCS {
        Objects.requireNonNull(parentInstanceId, "parentInstanceId");
        Objects.requireNonNull(childInstanceId, "childInstanceId");
        Objects.requireNonNull(location, "location");
    }
}
