package org.vnu.sme.goal.dsl.acl.mm;

/** YAML SubGroup membership with its own cardinality. */
public interface SubGroup extends CardinalityTarget {
    AclGroup group();
    AclCardinality cardinality();
}
