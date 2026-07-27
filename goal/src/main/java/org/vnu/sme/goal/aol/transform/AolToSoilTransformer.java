package org.vnu.sme.goal.aol.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclDataType;
import org.vnu.sme.goal.acl.mm.AclEntity;
import org.vnu.sme.goal.acl.mm.AclEnum;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclOwner;
import org.vnu.sme.goal.acl.mm.AclPrimitiveType;
import org.vnu.sme.goal.acl.mm.AclRelation;
import org.vnu.sme.goal.acl.mm.AclRole;
import org.vnu.sme.goal.aol.mm.AolEntityInstance;
import org.vnu.sme.goal.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.aol.mm.AolLink;
import org.vnu.sme.goal.aol.mm.AolModel;
import org.vnu.sme.goal.aol.mm.AolPlay;

/**
 * Transforms a validated AOL snapshot into the line-oriented shell/SOIL input
 * consumed by {@code IStarUseTraceCompiler}.
 *
 * <p>The generated classifier and association identifiers deliberately follow
 * {@code AclUseTranslator}: explicit ACL relations reserve names first, then
 * each owner reserves its generated associations in declaration order.</p>
 */
public final class AolToSoilTransformer {
    private AolToSoilTransformer() {
    }

    public static String transform(AclModel acl, AolModel aol) {
        Objects.requireNonNull(acl, "acl");
        Objects.requireNonNull(aol, "aol");

        AssociationCatalog associations = associationCatalog(acl);
        StringBuilder soil = new StringBuilder()
                .append("-- Generated from AOL. Regenerate instead of editing this file.\n")
                .append("-- Class and association identifiers mirror AclUseTranslator.\n\n");

        Set<String> emittedObjectNames = new LinkedHashSet<>();
        Map<String, InstanceRef> instances = new LinkedHashMap<>();
        List<GroupOccurrence> groups = new ArrayList<>();

        String agentClass = generatedAgentName(acl);
        for (String agentId : aol.agents()) {
            emitObject(soil, emittedObjectNames, agentId, agentClass, List.of(), Map.of());
        }

        for (AolGroupInstance group : aol.groupInstances()) {
            emitGroupObjects(soil, acl, group, groups, instances, emittedObjectNames);
        }
        for (AolEntityInstance entity : aol.topLevelEntities()) {
            emitEntityObject(soil, acl, entity, instances, emittedObjectNames);
        }

        soil.append('\n');
        for (GroupOccurrence occurrence : groups) {
            emitGroupLinks(soil, acl, associations, occurrence, instances);
        }
        for (AolLink link : aol.links()) {
            emitExplicitLink(soil, acl, associations, link, instances);
        }
        return soil.toString();
    }

    private static void emitGroupObjects(StringBuilder soil, AclModel acl, AolGroupInstance group,
                                         List<GroupOccurrence> groups,
                                         Map<String, InstanceRef> instances,
                                         Set<String> emittedObjectNames) {
        AclGroup groupType = findGroup(acl, group.typeName());
        String groupObject = emitObject(soil, emittedObjectNames, group.instanceId(), group.typeName(),
                groupAttributes(acl, group.typeName()), Map.of());
        registerInstance(instances,
                new InstanceRef(InstanceKind.GROUP, group.typeName(), group.instanceId(), groupObject));
        groups.add(new GroupOccurrence(group, groupObject));

        for (AolPlay play : group.plays()) {
            AclRole role = acl.findRole(play.roleType())
                    .orElseThrow(() -> new IllegalArgumentException("Unknown ACL role '" + play.roleType() + "'"));
            String objectName = emitObject(soil, emittedObjectNames, play.instanceId(), role.name(),
                    roleAttributes(acl, role.name()), play.attributeValues());
            registerInstance(instances,
                    new InstanceRef(InstanceKind.PLAY, role.name(), play.instanceId(), objectName));
        }
        for (AolEntityInstance entity : group.entities()) {
            emitEntityObject(soil, acl, entity, instances, emittedObjectNames);
        }
        for (AolGroupInstance subgroup : group.subgroups()) {
            emitGroupObjects(soil, acl, subgroup, groups, instances, emittedObjectNames);
        }
    }

    private static void emitEntityObject(StringBuilder soil, AclModel acl, AolEntityInstance entity,
                                         Map<String, InstanceRef> instances,
                                         Set<String> emittedObjectNames) {
        AclEntity entityType = acl.findEntity(entity.entityType())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown ACL entity '" + entity.entityType() + "'"));
        String objectName = emitObject(soil, emittedObjectNames, entity.instanceId(), entityType.name(),
                entityAttributes(acl, entityType.name()), entity.attributeValues());
        registerInstance(instances,
                new InstanceRef(InstanceKind.ENTITY, entityType.name(), entity.instanceId(), objectName));
    }

    private static String emitObject(StringBuilder soil, Set<String> emittedObjectNames,
                                     String instanceId, String typeName,
                                     List<AclAttribute> attributes,
                                     Map<String, String> explicitValues) {
        String objectName = id(instanceId);
        if (!emittedObjectNames.add(objectName)) {
            throw new IllegalArgumentException("Duplicate USE object identifier '" + objectName + "'");
        }
        soil.append("!create ").append(objectName).append(" : ").append(id(typeName)).append('\n');
        emitAttributes(soil, objectName, attributes, explicitValues);
        return objectName;
    }

    private static void emitAttributes(StringBuilder soil, String objectName,
                                       List<AclAttribute> attributes,
                                       Map<String, String> explicitValues) {
        for (AclAttribute attribute : attributes) {
            String rawValue = explicitValues.get(attribute.name());
            if (rawValue == null) {
                rawValue = attribute.defaultValue().orElse(null);
            }
            if (rawValue == null) {
                continue;
            }
            soil.append("!set ").append(objectName).append('.').append(id(attribute.name()))
                    .append(" := ").append(soilLiteral(attribute.type(), rawValue)).append('\n');
        }
    }

    private static void emitGroupLinks(StringBuilder soil, AclModel acl,
                                       AssociationCatalog associations,
                                       GroupOccurrence occurrence,
                                       Map<String, InstanceRef> instances) {
        AolGroupInstance group = occurrence.group();

        for (AolPlay play : group.plays()) {
            RoleOwnerAssociation owner = findRoleOwner(acl, associations, group.typeName(), play.roleType());
            InstanceRef playRef = requireInstance(instances, play.instanceId());
            emitInsert(soil, id(play.agentId()), playRef.objectName(), owner.playsAssociation());
            emitInsert(soil, occurrence.objectName(), playRef.objectName(), owner.membershipAssociation());
        }

        for (AolEntityInstance entity : group.entities()) {
            RelationBinding membership = findGroupEntityRelation(
                    acl, associations, group.typeName(), entity.entityType());
            InstanceRef entityRef = requireInstance(instances, entity.instanceId());
            if (membership.groupIsSource()) {
                emitInsert(soil, occurrence.objectName(), entityRef.objectName(),
                        membership.relation().associationName());
            } else {
                emitInsert(soil, entityRef.objectName(), occurrence.objectName(),
                        membership.relation().associationName());
            }
        }

        for (AolGroupInstance subgroup : group.subgroups()) {
            GroupOwnerAssociation owner = findGroupOwner(
                    acl, associations, group.typeName(), subgroup.typeName());
            InstanceRef subgroupRef = requireInstance(instances, subgroup.instanceId());
            emitInsert(soil, occurrence.objectName(), subgroupRef.objectName(), owner.associationName());
        }
    }

    private static void emitExplicitLink(StringBuilder soil, AclModel acl,
                                         AssociationCatalog associations, AolLink link,
                                         Map<String, InstanceRef> instances) {
        TranslatedRelation translated = associations.relations().stream()
                .filter(value -> value.relation().name().equals(link.relationName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown ACL relation '" + link.relationName() + "'"));

        InstanceRef source = requireInstance(instances, link.sourceInstanceId());
        AclRelation relation = translated.relation();
        boolean sourceAtAclSource;
        String expectedTargetType;
        if (matchesEndpointType(acl, source, relation.source().type())) {
            sourceAtAclSource = true;
            expectedTargetType = relation.target().type();
        } else if (matchesEndpointType(acl, source, relation.target().type())) {
            sourceAtAclSource = false;
            expectedTargetType = relation.source().type();
        } else {
            throw new IllegalArgumentException("Instance '" + link.sourceInstanceId()
                    + "' does not match relation '" + link.relationName() + "'");
        }

        for (String targetId : link.targetInstanceIds()) {
            InstanceRef target = requireInstance(instances, targetId);
            if (!matchesEndpointType(acl, target, expectedTargetType)) {
                throw new IllegalArgumentException("Instance '" + targetId
                        + "' does not match relation '" + link.relationName()
                        + "' endpoint '" + expectedTargetType + "'");
            }
            if (sourceAtAclSource) {
                emitInsert(soil, source.objectName(), target.objectName(), translated.associationName());
            } else {
                emitInsert(soil, target.objectName(), source.objectName(), translated.associationName());
            }
        }
    }

    private static void emitInsert(StringBuilder soil, String sourceObject,
                                   String targetObject, String associationName) {
        soil.append("!insert (").append(sourceObject).append(", ").append(targetObject)
                .append(") into ").append(associationName).append('\n');
    }

    private static AssociationCatalog associationCatalog(AclModel acl) {
        Set<String> usedNames = new HashSet<>();
        List<TranslatedRelation> relations = new ArrayList<>();
        for (AclRelation relation : acl.relations()) {
            relations.add(new TranslatedRelation(relation, unique(usedNames, relation.name())));
        }

        List<RoleOwnerAssociation> roleOwners = new ArrayList<>();
        List<GroupOwnerAssociation> groupOwners = new ArrayList<>();
        for (AclOwner owner : acl.owners()) {
            if (acl.findRole(owner.target()).isPresent()) {
                String plays = unique(usedNames, "Agent_plays_" + owner.target());
                String membership = unique(usedNames, owner.target() + "_in_" + owner.sourceGroup());
                roleOwners.add(new RoleOwnerAssociation(owner, plays, membership));
            } else {
                String name = unique(usedNames, "Owner_" + owner.sourceGroup() + "_" + owner.target());
                groupOwners.add(new GroupOwnerAssociation(owner, name));
            }
        }
        return new AssociationCatalog(
                List.copyOf(relations), List.copyOf(roleOwners), List.copyOf(groupOwners));
    }

    private static RoleOwnerAssociation findRoleOwner(AclModel acl, AssociationCatalog associations,
                                                      String groupType, String roleType) {
        for (RoleOwnerAssociation value : associations.roleOwners()) {
            if (value.owner().sourceGroup().equals(groupType)
                    && value.owner().target().equals(roleType)) {
                return value;
            }
        }
        return associations.roleOwners().stream()
                .filter(value -> isGroupOrSubtype(acl, groupType, value.owner().sourceGroup())
                        && isRoleOrSubtype(acl, roleType, value.owner().target()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No ACL owner maps role '" + roleType + "' into group '" + groupType + "'"));
    }

    private static GroupOwnerAssociation findGroupOwner(AclModel acl, AssociationCatalog associations,
                                                        String parentType, String childType) {
        for (GroupOwnerAssociation value : associations.groupOwners()) {
            if (value.owner().sourceGroup().equals(parentType)
                    && value.owner().target().equals(childType)) {
                return value;
            }
        }
        return associations.groupOwners().stream()
                .filter(value -> isGroupOrSubtype(acl, parentType, value.owner().sourceGroup())
                        && isGroupOrSubtype(acl, childType, value.owner().target()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No ACL owner maps subgroup '" + childType + "' into group '" + parentType + "'"));
    }

    private static RelationBinding findGroupEntityRelation(AclModel acl,
                                                           AssociationCatalog associations,
                                                           String groupType, String entityType) {
        RelationBinding exact = null;
        for (TranslatedRelation translated : associations.relations()) {
            AclRelation relation = translated.relation();
            if (relation.source().type().equals(groupType)
                    && relation.target().type().equals(entityType)) {
                exact = new RelationBinding(translated, true);
            } else if (relation.target().type().equals(groupType)
                    && relation.source().type().equals(entityType)) {
                exact = new RelationBinding(translated, false);
            }
        }
        if (exact != null) {
            return exact;
        }

        RelationBinding inherited = null;
        for (TranslatedRelation translated : associations.relations()) {
            AclRelation relation = translated.relation();
            if (isGroupOrSubtype(acl, groupType, relation.source().type())
                    && isEntityOrSubtype(acl, entityType, relation.target().type())) {
                inherited = new RelationBinding(translated, true);
            } else if (isGroupOrSubtype(acl, groupType, relation.target().type())
                    && isEntityOrSubtype(acl, entityType, relation.source().type())) {
                inherited = new RelationBinding(translated, false);
            }
        }
        if (inherited == null) {
            throw new IllegalArgumentException("No ACL relation maps entity '" + entityType
                    + "' into group '" + groupType + "'");
        }
        return inherited;
    }

    private static boolean matchesEndpointType(AclModel acl, InstanceRef instance, String declaredType) {
        return switch (instance.kind()) {
            case PLAY -> isRoleOrSubtype(acl, instance.typeName(), declaredType);
            case ENTITY -> isEntityOrSubtype(acl, instance.typeName(), declaredType);
            case GROUP -> isGroupOrSubtype(acl, instance.typeName(), declaredType);
        };
    }

    private static boolean isRoleOrSubtype(AclModel acl, String actual, String declared) {
        String current = actual;
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            if (current.equals(declared)) {
                return true;
            }
            AclRole role = acl.findRole(current).orElse(null);
            if (role == null || role.parentRoles().isEmpty()) {
                return false;
            }
            current = role.parentRoles().get(0);
        }
        return false;
    }

    private static boolean isEntityOrSubtype(AclModel acl, String actual, String declared) {
        String current = actual;
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            if (current.equals(declared)) {
                return true;
            }
            AclEntity entity = acl.findEntity(current).orElse(null);
            if (entity == null || entity.specializes().isEmpty()) {
                return false;
            }
            current = entity.specializes().get();
        }
        return false;
    }

    private static boolean isGroupOrSubtype(AclModel acl, String actual, String declared) {
        String current = actual;
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            if (current.equals(declared)) {
                return true;
            }
            AclGroup group = findGroupOrNull(acl, current);
            if (group == null || group.specializes().isEmpty()) {
                return false;
            }
            current = group.specializes().get();
        }
        return false;
    }

    private static List<AclAttribute> roleAttributes(AclModel acl, String roleName) {
        List<AclRole> hierarchy = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        String current = roleName;
        while (seen.add(current)) {
            AclRole role = acl.findRole(current).orElse(null);
            if (role == null) {
                break;
            }
            hierarchy.add(role);
            if (role.parentRoles().isEmpty()) {
                break;
            }
            current = role.parentRoles().get(0);
        }
        Collections.reverse(hierarchy);
        LinkedHashMap<String, AclAttribute> result = new LinkedHashMap<>();
        hierarchy.forEach(role -> role.attributes().forEach(attribute -> result.put(attribute.name(), attribute)));
        return List.copyOf(result.values());
    }

    private static List<AclAttribute> entityAttributes(AclModel acl, String entityName) {
        List<AclEntity> hierarchy = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        String current = entityName;
        while (seen.add(current)) {
            AclEntity entity = acl.findEntity(current).orElse(null);
            if (entity == null) {
                break;
            }
            hierarchy.add(entity);
            if (entity.specializes().isEmpty()) {
                break;
            }
            current = entity.specializes().get();
        }
        Collections.reverse(hierarchy);
        LinkedHashMap<String, AclAttribute> result = new LinkedHashMap<>();
        hierarchy.forEach(entity -> entity.attributes().forEach(attribute -> result.put(attribute.name(), attribute)));
        return List.copyOf(result.values());
    }

    private static List<AclAttribute> groupAttributes(AclModel acl, String groupName) {
        List<AclGroup> hierarchy = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        String current = groupName;
        while (seen.add(current)) {
            AclGroup group = findGroupOrNull(acl, current);
            if (group == null) {
                break;
            }
            hierarchy.add(group);
            if (group.specializes().isEmpty()) {
                break;
            }
            current = group.specializes().get();
        }
        Collections.reverse(hierarchy);
        LinkedHashMap<String, AclAttribute> result = new LinkedHashMap<>();
        hierarchy.forEach(group -> group.attributes().forEach(attribute -> result.put(attribute.name(), attribute)));
        return List.copyOf(result.values());
    }

    private static String soilLiteral(AclDataType type, String rawValue) {
        if (type == AclPrimitiveType.STRING) {
            return "'" + escapeUseString(decodeString(rawValue)) + "'";
        }
        if (type instanceof AclEnum) {
            String literal = rawValue.strip();
            if (literal.startsWith("#")) {
                literal = literal.substring(1);
            }
            return "#" + id(literal);
        }
        return rawValue.strip();
    }

    private static String decodeString(String rawValue) {
        String value = rawValue;
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                value = value.substring(1, value.length() - 1);
            }
        }

        StringBuilder decoded = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current != '\\' || i + 1 >= value.length()) {
                decoded.append(current);
                continue;
            }
            char escaped = value.charAt(++i);
            switch (escaped) {
                case 'n' -> decoded.append('\n');
                case 'r' -> decoded.append('\r');
                case 't' -> decoded.append('\t');
                case 'b' -> decoded.append('\b');
                case 'f' -> decoded.append('\f');
                case '\\' -> decoded.append('\\');
                case '\'' -> decoded.append('\'');
                case '"' -> decoded.append('"');
                default -> decoded.append(escaped);
            }
        }
        return decoded.toString();
    }

    private static String escapeUseString(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            switch (current) {
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\\' -> escaped.append("\\\\");
                case '\'' -> escaped.append("\\'");
                case '"' -> escaped.append("\\\"");
                default -> escaped.append(current);
            }
        }
        return escaped.toString();
    }

    private static void registerInstance(Map<String, InstanceRef> instances, InstanceRef instance) {
        if (instances.putIfAbsent(instance.instanceId(), instance) != null) {
            throw new IllegalArgumentException(
                    "Duplicate AOL instance identifier '" + instance.instanceId() + "'");
        }
    }

    private static InstanceRef requireInstance(Map<String, InstanceRef> instances, String instanceId) {
        InstanceRef value = instances.get(instanceId);
        if (value == null) {
            throw new IllegalArgumentException("Unknown AOL instance '" + instanceId + "'");
        }
        return value;
    }

    private static AclGroup findGroup(AclModel acl, String groupName) {
        AclGroup value = findGroupOrNull(acl, groupName);
        if (value == null) {
            throw new IllegalArgumentException("Unknown ACL group '" + groupName + "'");
        }
        return value;
    }

    private static AclGroup findGroupOrNull(AclModel acl, String groupName) {
        return acl.groups().stream()
                .filter(group -> group.name().equals(groupName))
                .findFirst()
                .orElse(null);
    }

    private static String generatedAgentName(AclModel model) {
        boolean occupied = model.entities().stream().anyMatch(value -> value.name().equals("Agent"))
                || model.roles().stream().anyMatch(value -> value.name().equals("Agent"))
                || model.groups().stream().anyMatch(value -> value.name().equals("Agent"));
        return occupied ? "ACLAgent" : "Agent";
    }

    private static String unique(Set<String> used, String proposed) {
        String base = id(proposed);
        String value = base;
        for (int suffix = 2; !used.add(value); suffix++) {
            value = base + "_" + suffix;
        }
        return value;
    }

    private static String id(String value) {
        if (value == null || value.isBlank()) {
            return "unnamed";
        }
        String clean = value.replaceAll("[^A-Za-z0-9_]", "_");
        return Character.isDigit(clean.charAt(0)) ? "_" + clean : clean;
    }

    private enum InstanceKind {
        PLAY,
        ENTITY,
        GROUP
    }

    private record InstanceRef(InstanceKind kind, String typeName,
                               String instanceId, String objectName) {
    }

    private record GroupOccurrence(AolGroupInstance group, String objectName) {
    }

    private record TranslatedRelation(AclRelation relation, String associationName) {
    }

    private record RoleOwnerAssociation(AclOwner owner, String playsAssociation,
                                        String membershipAssociation) {
    }

    private record GroupOwnerAssociation(AclOwner owner, String associationName) {
    }

    private record AssociationCatalog(List<TranslatedRelation> relations,
                                      List<RoleOwnerAssociation> roleOwners,
                                      List<GroupOwnerAssociation> groupOwners) {
    }

    private record RelationBinding(TranslatedRelation relation, boolean groupIsSource) {
    }
}
