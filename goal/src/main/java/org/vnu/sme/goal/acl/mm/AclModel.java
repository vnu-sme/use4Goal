package org.vnu.sme.goal.acl.mm;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class AclModel extends StructuralSpecification {
    private final String version;

    public AclModel(String version, String name, List<AclEnum> enums, List<AclRole> roles,
                    List<AclEntity> entities, List<AclRoleInheritance> inheritances, AclGroup rootGroup) {
        super(name, enums, roles, entities, inheritances, rootGroup);
        this.version = Objects.requireNonNull(version, "version");
    }

    public String version() { return version; }
    public String name() { return id(); }
    /** Compatibility view; the metamodel itself contains only rootGroup. */
    public List<AclGroup> groups() { return List.of(rootGroup()); }
    public Optional<AclRole> findRole(String roleName) {
        return roles().stream().filter(role -> role.name().equals(roleName)).findFirst();
    }
    public Optional<AclEntity> findEntity(String entityName) {
        return entities().stream().filter(entity -> entity.name().equals(entityName)).findFirst();
    }
}

/* legacy source compatibility constructor is intentionally not provided: a
 * specification has exactly one rootGroup per acl.yaml. */
/*
    public AclModel {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(name, "name");
        enums = List.copyOf(enums);
        roles = List.copyOf(roles);
        entities = List.copyOf(entities);
        groups = List.copyOf(groups);
    }

    public Optional<AclRole> findRole(String roleName) {
        return roles.stream().filter(role -> role.name().equals(roleName)).findFirst();
    }

    public Optional<AclEntity> findEntity(String entityName) {
        return entities.stream().filter(entity -> entity.name().equals(entityName)).findFirst();
    }
}
*/
