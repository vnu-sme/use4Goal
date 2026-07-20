package org.vnu.sme.goal.acl.mm;

/** YAML RoleInheritance edge (child role -> parent role). */
public interface RoleInheritance {
    String child();
    String parent();
}
