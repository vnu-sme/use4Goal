package org.vnu.sme.goal.acl.mm;

import java.util.Objects;

/** MOISE role inheritance edge (child -> parent). */
public record AclRoleInheritance(String child, String parent) implements RoleInheritance {
    public AclRoleInheritance {
        Objects.requireNonNull(child, "child");
        Objects.requireNonNull(parent, "parent");
    }
}
