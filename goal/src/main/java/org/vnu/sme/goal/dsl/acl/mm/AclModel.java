package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Concrete ACL model derived from the Classifier/Class/Relationship metamodel. */
public final class AclModel extends StructuralSpecification {
    private final String version;
    private final String name;
    private final List<AclNamedDataType> namedDataTypes;
    private final List<AclGroup> groups;
    private final List<AclRelation> relations;
    private final List<AclCompatibility> compatibilities;
    private final List<AclGeneralization> generalizations;
    private final List<AclInvariant> invariants;

    public AclModel(String version, String name, List<AclEnum> enums, List<AclEntity> entities,
                    List<AclRole> roles, List<AclGroup> groups, List<AclRelation> relations,
                    List<AclOwner> owners, List<AclCompatibility> compatibilities,
                    List<AclGeneralization> generalizations) {
        this(version, name, enums, entities, roles, groups, relations, owners,
                compatibilities, generalizations, List.of(), List.of());
    }

    public AclModel(String version, String name, List<AclEnum> enums, List<AclEntity> entities,
                    List<AclRole> roles, List<AclGroup> groups, List<AclRelation> relations,
                    List<AclOwner> owners, List<AclCompatibility> compatibilities,
                    List<AclGeneralization> generalizations, List<AclInvariant> invariants) {
        this(version, name, enums, entities, roles, groups, relations, owners,
                compatibilities, generalizations, invariants, List.of());
    }

    public AclModel(String version, String name, List<AclEnum> enums, List<AclEntity> entities,
                    List<AclRole> roles, List<AclGroup> groups, List<AclRelation> relations,
                    List<AclOwner> owners, List<AclCompatibility> compatibilities,
                    List<AclGeneralization> generalizations, List<AclInvariant> invariants,
                    List<AclNamedDataType> namedDataTypes) {
        super(name, enums, roles, entities,
                generalizations.stream().filter(x -> roles.stream().anyMatch(r -> r.name().equals(x.specific())))
                        .map(x -> new AclRoleInheritance(x.specific(), x.general())).toList(),
                groups.isEmpty() ? emptyRoot() : groups.get(0));
        this.version = Objects.requireNonNull(version, "version"); this.name = Objects.requireNonNull(name, "name");
        this.namedDataTypes = List.copyOf(namedDataTypes);
        this.groups = List.copyOf(groups); this.relations = List.copyOf(relations);
        this.compatibilities = List.copyOf(compatibilities);
        this.generalizations = List.copyOf(generalizations);
        this.invariants = List.copyOf(invariants);
    }
    public String version() { return version; }
    /** v4 is the revised M1 core, not the legacy runtime/play-chain encoding. */
    public boolean isRevisedCore() { return version.startsWith("v4."); }
    /** Prevent old execution encodings from silently changing v4 semantics. */
    public void requireLegacyRuntime() {
        if (isRevisedCore()) {
            throw new IllegalArgumentException("ACL core v4 supports structural parsing and validation. "
                    + "This runtime backend has not been migrated to same-instance Role inheritance, "
                    + "OrgCtx players and declaration-only containment; it cannot execute a v4 model.");
        }
    }
    public String name() { return name; }
    public List<AclNamedDataType> namedDataTypes() { return namedDataTypes; }
    public List<AclGroup> groups() { return groups; }
    public List<AclGroup> orgContexts() {
        return groups.stream().filter(AclGroup::isOrganizationalContext).toList();
    }
    public List<AclRelation> relations() { return relations; }
    /**
     * Legacy projection for translators that have not yet migrated to
     * Group-to-Role/Group composition relations. Owner is not a metamodel or
     * system-state component in the formal ACL semantics.
     */
    @Deprecated(forRemoval = false)
    public List<AclOwner> owners() {
        java.util.Set<String> groupNames = groups.stream()
                .map(AclGroup::name).collect(java.util.stream.Collectors.toSet());
        return relations.stream()
                .filter(relation -> relation.kind() == RelationKind.COMPOSITION)
                .filter(relation -> groupNames.contains(relation.source().type()))
                .filter(relation -> findRole(relation.target().type()).isPresent()
                        || findGroup(relation.target().type()).isPresent())
                .map(relation -> new AclOwner(relation.source().type(), relation.target().type(),
                        relation.target().multiplicity()))
                .toList();
    }
    public List<AclCompatibility> compatibilities() { return compatibilities; }
    public List<AclGeneralization> generalizations() { return generalizations; }
    public List<AclInvariant> invariants() { return invariants; }
    public Optional<AclRole> findRole(String value) { return roles().stream().filter(x -> x.name().equals(value)).findFirst(); }
    public Optional<AclEntity> findEntity(String value) { return entities().stream().filter(x -> x.name().equals(value)).findFirst(); }
    public Optional<AclGroup> findGroup(String value) { return groups.stream().filter(x -> x.name().equals(value)).findFirst(); }
    public Optional<AclGroup> findOrgContext(String value) {
        return orgContexts().stream().filter(x -> x.name().equals(value)).findFirst();
    }
    private static AclGroup emptyRoot() {
        return new AclGroup("__root__", Optional.empty(), List.of(), List.of(), List.of(), List.of(), List.of());
    }
}
