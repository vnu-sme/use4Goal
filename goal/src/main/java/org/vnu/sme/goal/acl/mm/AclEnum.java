package org.vnu.sme.goal.acl.mm;

import java.util.List;

public record AclEnum(String name, List<String> literals) {
    public AclEnum {
        literals = List.copyOf(literals);
    }
}
