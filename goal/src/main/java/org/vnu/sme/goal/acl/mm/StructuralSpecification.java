package org.vnu.sme.goal.acl.mm;

import java.util.List;
import java.util.Objects;

/** The single ACL root class from acl.yaml; rootGroup is exactly one. */
public class StructuralSpecification {
    private final String id;
    private final List<AclEnum> enums;
    private final List<AclRole> roles;
    private final List<AclEntity> entities;
    private final List<AclRoleInheritance> inheritances;
    private final AclGroup rootGroup;

    public StructuralSpecification(String id, List<AclEnum> enums, List<AclRole> roles,
                                   List<AclEntity> entities, List<AclRoleInheritance> inheritances,
                                   AclGroup rootGroup) {
        this.id = Objects.requireNonNull(id, "id");
        this.enums = List.copyOf(enums);
        this.roles = List.copyOf(roles);
        this.entities = List.copyOf(entities);
        this.inheritances = List.copyOf(inheritances);
        this.rootGroup = Objects.requireNonNull(rootGroup, "rootGroup");
    }

    public String id() { return id; }
    public List<AclEnum> enums() { return enums; }
    public List<AclRole> roles() { return roles; }
    public List<AclEntity> entities() { return entities; }
    public List<AclRoleInheritance> inheritances() { return inheritances; }
    public AclGroup rootGroup() { return rootGroup; }
}
