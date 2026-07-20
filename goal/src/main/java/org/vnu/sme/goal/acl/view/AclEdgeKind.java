package org.vnu.sme.goal.acl.view;

public enum AclEdgeKind {
    INHERITANCE,
    ROLE_COMPOSITION,
    ENTITY_COMPOSITION,
    SUBGROUP_COMPOSITION,
    ACQUAINTANCE,
    COMMUNICATION,
    AUTHORITY,
    COMPATIBILITY,
    ROLE_ENTITY_RELATION,
    GROUP_CARDINALITY;

    public boolean isComposition() {
        return this == ROLE_COMPOSITION || this == ENTITY_COMPOSITION
                || this == SUBGROUP_COMPOSITION || this == GROUP_CARDINALITY;
    }

    public boolean isRoleLink() {
        return this == ACQUAINTANCE || this == COMMUNICATION
                || this == AUTHORITY || this == COMPATIBILITY;
    }

    public boolean isRoleEntityRelation() { return this == ROLE_ENTITY_RELATION; }
}
