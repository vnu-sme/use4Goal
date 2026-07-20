package org.vnu.sme.goal.acl.mm;

public interface CardinalityConstraint {
    CardinalityTarget target();
    AclCardinality cardinality();
}
