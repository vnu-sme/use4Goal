package org.vnu.sme.goal.acl.mm;

/** YAML abstract MOISE RoleRelation. */
public interface RoleRelation {
    String fromRole();
    String toRole();
    AclScope scope();
    boolean extendsSubgroups();
    boolean bidirectional();
}
