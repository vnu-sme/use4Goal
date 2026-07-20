package org.vnu.sme.goal.acl.ast;

import java.util.List;

public record AclEnumCS(String name, List<String> literals) {
    public AclEnumCS {
        literals = List.copyOf(literals);
    }
}
