package org.vnu.sme.goal.acl.mm;

import java.util.List;

/** YAML-facing role definition name. */
public interface RoleDefinition extends CardinalityTarget {
    String name();
    boolean isAbstract();
    List<String> parentRoles();
    List<AclAttribute> attributes();
}
