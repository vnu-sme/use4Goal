package org.vnu.sme.goal.acl.mm;

/** YAML-facing name for the ACL attribute definition. */
public interface AttributeDefinition {
    String name();
    AclDataType type();
    boolean required();
    boolean mutable();
}
