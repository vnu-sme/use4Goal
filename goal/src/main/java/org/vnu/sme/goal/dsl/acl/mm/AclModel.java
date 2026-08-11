package org.vnu.sme.goal.dsl.acl.mm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Concrete ACL model derived from the Classifier/Class/Relationship metamodel. */
public final class AclModel extends StructuralSpecification {
    private final String version;
    private final String name;
    private final List<AclGroup> groups;
    private final List<AclRelation> relations;
    private final List<AclOwner> owners;
    private final List<AclCompatibility> compatibilities;
    private final List<AclGeneralization> generalizations;

    public AclModel(String version, String name, List<AclEnum> enums, List<AclEntity> entities,
                    List<AclRole> roles, List<AclGroup> groups, List<AclRelation> relations,
                    List<AclOwner> owners, List<AclCompatibility> compatibilities,
                    List<AclGeneralization> generalizations) {
        super(name, enums, roles, entities,
                generalizations.stream().filter(x -> roles.stream().anyMatch(r -> r.name().equals(x.specific())))
                        .map(x -> new AclRoleInheritance(x.specific(), x.general())).toList(),
                groups.isEmpty() ? emptyRoot() : groups.get(0));
        this.version = Objects.requireNonNull(version, "version"); this.name = Objects.requireNonNull(name, "name");
        this.groups = List.copyOf(groups); this.relations = List.copyOf(relations);
        this.owners = List.copyOf(owners); this.compatibilities = List.copyOf(compatibilities);
        this.generalizations = List.copyOf(generalizations);
    }
    public String version() { return version; }
    public String name() { return name; }
    public List<AclGroup> groups() { return groups; }
    public List<AclRelation> relations() { return relations; }
    public List<AclOwner> owners() { return owners; }
    public List<AclCompatibility> compatibilities() { return compatibilities; }
    public List<AclGeneralization> generalizations() { return generalizations; }
    public Optional<AclRole> findRole(String value) { return roles().stream().filter(x -> x.name().equals(value)).findFirst(); }
    public Optional<AclEntity> findEntity(String value) { return entities().stream().filter(x -> x.name().equals(value)).findFirst(); }
    private static AclGroup emptyRoot() {
        return new AclGroup("__root__", Optional.empty(), List.of(), List.of(), List.of(), List.of(), List.of());
    }
}
