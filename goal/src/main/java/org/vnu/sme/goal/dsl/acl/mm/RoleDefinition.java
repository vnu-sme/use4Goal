package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;

/** YAML-facing role definition name. */
public interface RoleDefinition extends CardinalityTarget {
    String name();
    List<String> parentRoles();
    List<AclAttribute> attributes();
}
