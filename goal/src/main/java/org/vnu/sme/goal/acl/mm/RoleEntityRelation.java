package org.vnu.sme.goal.acl.mm;

/** Dedicated ACL Role -> Entity relation; never a MOISE Link. */
public interface RoleEntityRelation {
    String name();
    AclRole sourceRole();
    AclEntity targetEntity();
    AclRoleEntityRelationType type();
    AclScope scope();
    boolean extendsSubgroups();
}
