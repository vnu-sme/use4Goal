package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;

/** YAML-facing entity definition; ACL keeps the AclEntity adapter name too. */
public interface EntityDefinition extends CardinalityTarget {
    String name();
    List<AclAttribute> attributes();
}
