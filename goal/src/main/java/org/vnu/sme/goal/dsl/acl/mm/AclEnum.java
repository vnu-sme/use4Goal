package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;
import java.util.Objects;

public record AclEnum(String name, List<String> literals) implements AclDataType {
    public AclEnum {
        Objects.requireNonNull(name, "name");
        literals = List.copyOf(literals);
    }

    @Override
    public String sourceName() {
        return name;
    }
}
