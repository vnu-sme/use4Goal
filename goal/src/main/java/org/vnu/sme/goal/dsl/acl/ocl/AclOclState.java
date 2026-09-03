package org.vnu.sme.goal.dsl.acl.ocl;

/** Minimal typed ACL system-state protocol consumed by OCL evaluation. */
public interface AclOclState {
    Object property(Object base, String name);
    String identity(Object value);
}
