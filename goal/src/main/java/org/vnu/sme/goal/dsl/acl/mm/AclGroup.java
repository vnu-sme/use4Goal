package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Structural context. Legacy {@code group} declarations remain supported. */
public final class AclGroup implements GroupSpecification {
    private final String name;
    private final Optional<String> specializes;
    private final List<AclAttribute> attributes;
    private final List<AclGroupMember> members;
    private final List<AclRoleMembership> roles;
    private final List<AclEntityMembership> entities;
    private final List<AclSubgroupMembership> subgroups;
    private final List<AclCompatibility> compatibilities;
    private final List<AclRoleEntityRelation> roleEntityRelations;
    private final List<AclCardinalityConstraint> cardinalityConstraints;
    private final boolean organizationalContext;

    public AclGroup(String name, Optional<String> specializes, List<AclAttribute> attributes,
                    List<AclGroupMember> members, List<AclRoleMembership> roles,
                    List<AclSubgroupMembership> subgroups,
                    List<AclCompatibility> compatibilities) {
        this(name, specializes, attributes, members, roles, List.of(), subgroups,
                compatibilities, List.of(), List.of(), false);
    }

    /** Compatibility constructor for the former nested-group metamodel. */
    public AclGroup(String name, List<AclRoleMembership> roles, List<AclEntityMembership> entities,
                    List<AclSubgroupMembership> subgroups, List<AclCompatibility> compatibilities,
                    List<AclRoleEntityRelation> roleEntityRelations,
                    List<AclCardinalityConstraint> cardinalityConstraints) {
        java.util.ArrayList<AclGroupMember> projected = new java.util.ArrayList<>();
        roles.forEach(x -> projected.add(new AclGroupMember(x.roleName(), x.cardinality())));
        entities.forEach(x -> projected.add(new AclGroupMember(x.entityName(), x.cardinality())));
        subgroups.forEach(x -> projected.add(new AclGroupMember(x.group().name(), x.cardinality())));
        this.name = Objects.requireNonNull(name, "name");
        this.specializes = Optional.empty();
        this.attributes = List.of();
        this.members = List.copyOf(projected);
        this.roles = List.copyOf(roles);
        this.entities = List.copyOf(entities);
        this.subgroups = List.copyOf(subgroups);
        this.compatibilities = List.copyOf(compatibilities);
        this.roleEntityRelations = List.copyOf(roleEntityRelations);
        this.cardinalityConstraints = List.copyOf(cardinalityConstraints);
        this.organizationalContext = true;
    }

    public AclGroup(String name, Optional<String> specializes, List<AclAttribute> attributes,
                    List<AclGroupMember> members, List<AclRoleMembership> roles,
                    List<AclEntityMembership> entities, List<AclSubgroupMembership> subgroups,
                    List<AclCompatibility> compatibilities,
                    List<AclRoleEntityRelation> roleEntityRelations,
                    List<AclCardinalityConstraint> cardinalityConstraints,
                    boolean organizationalContext) {
        this.name = Objects.requireNonNull(name, "name");
        this.specializes = Objects.requireNonNull(specializes, "specializes");
        this.attributes = List.copyOf(attributes);
        this.members = List.copyOf(members);
        this.roles = List.copyOf(roles);
        this.entities = List.copyOf(entities);
        this.subgroups = List.copyOf(subgroups);
        this.compatibilities = List.copyOf(compatibilities);
        this.roleEntityRelations = List.copyOf(roleEntityRelations);
        this.cardinalityConstraints = List.copyOf(cardinalityConstraints);
        this.organizationalContext = organizationalContext;
    }

    @Override public String name() { return name; }
    public Optional<String> specializes() { return specializes; }
    public List<AclAttribute> attributes() { return attributes; }
    public List<AclGroupMember> members() { return members; }
    @Override public List<AclRoleMembership> roles() { return roles; }
    @Override public List<AclEntityMembership> entities() { return entities; }
    @Override public List<AclSubgroupMembership> subgroups() { return subgroups; }
    public List<AclCompatibility> compatibilities() { return compatibilities; }
    public List<AclRoleEntityRelation> roleEntityRelations() { return roleEntityRelations; }
    public List<AclCardinalityConstraint> cardinalityConstraints() { return cardinalityConstraints; }
    public boolean isOrganizationalContext() { return organizationalContext; }
}
