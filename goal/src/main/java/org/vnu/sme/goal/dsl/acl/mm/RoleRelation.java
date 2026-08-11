package org.vnu.sme.goal.dsl.acl.mm;

/** YAML abstract MOISE RoleRelation. */
public interface RoleRelation {
    String fromRole();
    String toRole();
    AclScope scope();
    boolean extendsSubgroups();
    boolean bidirectional();
}
