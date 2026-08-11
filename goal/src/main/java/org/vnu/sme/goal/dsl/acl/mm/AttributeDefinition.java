package org.vnu.sme.goal.dsl.acl.mm;

/** YAML-facing name for the ACL attribute definition. */
public interface AttributeDefinition {
    String name();
    AclDataType type();
    /** True only for multiplicity [0..1]; absence of this modifier means [1]. */
    boolean optional();
    boolean mutable();
}
