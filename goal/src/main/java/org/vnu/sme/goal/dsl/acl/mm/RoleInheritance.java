package org.vnu.sme.goal.dsl.acl.mm;

/** YAML RoleInheritance edge (child role -> parent role). */
public interface RoleInheritance {
    String child();
    String parent();
}
