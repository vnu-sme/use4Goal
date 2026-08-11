package org.vnu.sme.goal.dsl.acl.mm;

public interface CardinalityConstraint {
    CardinalityTarget target();
    AclCardinality cardinality();
}
