package org.vnu.sme.goal.acl.mm;

/** YAML GroupEntity composition (EntityDefinition + typed cardinality). */
public interface GroupEntity {
    String entityName();
    AclCardinality cardinality();
}
