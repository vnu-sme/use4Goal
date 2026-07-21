package org.vnu.sme.goal.aol.ast;

import java.util.Objects;

import org.vnu.sme.goal.acl.ast.AclSourceLocationCS;

public record AolAgentCS(String name, AclSourceLocationCS location) {
    public AolAgentCS {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(location, "location");
    }
}
