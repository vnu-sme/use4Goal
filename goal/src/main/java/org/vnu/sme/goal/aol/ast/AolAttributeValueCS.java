package org.vnu.sme.goal.aol.ast;

import java.util.Objects;

import org.vnu.sme.goal.acl.ast.AclSourceLocationCS;

/** rawValue keeps the source text verbatim (quotes included for strings), like AclAttributeCS.defaultValue. */
public record AolAttributeValueCS(String name, String rawValue, AclSourceLocationCS location) {
    public AolAttributeValueCS {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(rawValue, "rawValue");
        Objects.requireNonNull(location, "location");
    }
}
