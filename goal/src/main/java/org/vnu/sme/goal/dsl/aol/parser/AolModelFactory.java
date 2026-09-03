package org.vnu.sme.goal.dsl.aol.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;
import org.vnu.sme.goal.dsl.acl.mm.AclAttribute;
import org.vnu.sme.goal.dsl.acl.mm.AclCardinality;
import org.vnu.sme.goal.dsl.acl.mm.AclCardinalityConstraint;
import org.vnu.sme.goal.dsl.acl.mm.AclCompatibility;
import org.vnu.sme.goal.dsl.acl.mm.AclCompatibilityType;
import org.vnu.sme.goal.dsl.acl.mm.AclDataType;
import org.vnu.sme.goal.dsl.acl.mm.AclEndpoint;
import org.vnu.sme.goal.dsl.acl.mm.AclEntity;
import org.vnu.sme.goal.dsl.acl.mm.AclEntityMembership;
import org.vnu.sme.goal.dsl.acl.mm.AclEnum;
import org.vnu.sme.goal.dsl.acl.mm.AclGroup;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclOwner;
import org.vnu.sme.goal.dsl.acl.mm.AclPrimitiveType;
import org.vnu.sme.goal.dsl.acl.mm.AclRelation;
import org.vnu.sme.goal.dsl.acl.mm.AclRole;
import org.vnu.sme.goal.dsl.acl.mm.AclRoleMembership;
import org.vnu.sme.goal.dsl.acl.mm.AclSubgroupMembership;
import org.vnu.sme.goal.dsl.aol.ast.AolAttributeValueCS;
import org.vnu.sme.goal.dsl.aol.ast.AolAgentCS;
import org.vnu.sme.goal.dsl.aol.ast.AolEntityInstanceCS;
import org.vnu.sme.goal.dsl.aol.ast.AolGroupInstanceCS;
import org.vnu.sme.goal.dsl.aol.ast.AolLinkCS;
import org.vnu.sme.goal.dsl.aol.ast.AolModelCS;
import org.vnu.sme.goal.dsl.aol.ast.AolPlayCS;
import org.vnu.sme.goal.dsl.aol.mm.AolEntityInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolLink;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;

/**
 * Validates an .aol object snapshot against the .acl StructuralSpecification it targets:
 * type/cardinality checking (like an object diagram against its class diagram), plus the
 * one check a class diagram could never express -- that no Agent ends up holding two Role
 * types that were never declared compatible (default: incompatible).
 */
public final class AolModelFactory {
    public record SemanticError(AclSourceLocationCS location, String message) {}
    public record Result(AolModel model, List<SemanticError> errors) {
        public Result { errors = List.copyOf(errors); }
        public boolean ok() { return errors.isEmpty() && model != null; }
    }

    private AolModelFactory() {}

    public static Result create(AolModelCS ast, AclModel acl) { return new Build(ast, acl).run(); }

    private static final class Build {
        private final AolModelCS ast;
        private final AclModel acl;
        private final List<SemanticError> errors = new ArrayList<>();
        private final Set<String> agentNames = new LinkedHashSet<>();
        private final Map<String, String> agentProfiles = new LinkedHashMap<>();
        private final Map<String, Map<String, String>> agentValues = new LinkedHashMap<>();
        private final List<Occurrence> occurrences = new ArrayList<>();

        /** Every play, entity instance and group instance id, model-wide -- so 'link'
         *  declarations (which are not scoped to any Group) can resolve an id unambiguously
         *  regardless of which Group instance it was declared under. */
        private final Set<String> instanceIds = new LinkedHashSet<>();
        private final Map<String, InstanceRef> instanceIndex = new LinkedHashMap<>();

        /** One agent playing one role type, at one point in the group-instance tree. */
        private record Occurrence(String instanceId, String agentId, String roleType, List<AclGroup> typePath,
                                  List<String> instancePath) {}

        private enum InstanceKind { AGENT, PLAY, ENTITY, GROUP }
        private record InstanceRef(InstanceKind kind, String typeName) {}

        Build(AolModelCS ast, AclModel acl) {
            this.ast = Objects.requireNonNull(ast, "ast");
            this.acl = Objects.requireNonNull(acl, "acl");
        }

        Result run() {
            for (AolAgentCS a : ast.agents()) {
                if (!agentNames.add(a.name())) error(a.location(), "duplicate agent '" + a.name() + "'");
                if (a.profileRole() == null) {
                    agentValues.put(a.name(), Map.of());
                    continue;
                }
                acl.findRole(a.profileRole()).ifPresentOrElse(profile -> {
                    if (!profile.isAbstract()) {
                        error(a.location(), "agent profile role '" + a.profileRole() + "' must be abstract");
                    }
                    agentProfiles.put(a.name(), a.profileRole());
                    agentValues.put(a.name(), attributeValues(a.attributeValues(), effectiveRoleAttributes(profile.name()),
                            a.location(), "agent '" + a.name() + "' with profile '" + profile.name() + "'"));
                    registerInstance(a.location(), a.name(), InstanceKind.AGENT, profile.name());
                }, () -> error(a.location(), "unknown agent profile role '" + a.profileRole() + "'"));
            }

            List<AolEntityInstance> topLevelEntities = new ArrayList<>();
            for (AolEntityInstanceCS e : ast.entities()) {
                registerInstance(e.location(), e.instanceId(), InstanceKind.ENTITY, e.entityType());
                acl.findEntity(e.entityType()).ifPresentOrElse(entity -> {
                    Map<String, String> values = attributeValues(e.attributeValues(), entity.attributes(), e.location(),
                            "entity '" + e.entityType() + "'");
                    topLevelEntities.add(new AolEntityInstance(e.entityType(), e.instanceId(), values));
                }, () -> error(e.location(), "unknown entity type '" + e.entityType() + "'"));
            }

            List<AolGroupInstance> roots = new ArrayList<>();
            for (AolGroupInstanceCS g : ast.groupInstances()) {
                AclGroup actualRoot = findGroup(g.typeName());
                if (actualRoot == null || !isGroupOrSubtype(g.typeName(), acl.rootGroup().name())) {
                    error(g.location(), "top-level group instance '" + g.instanceId() + "' must be of root group type '"
                            + acl.rootGroup().name() + "' or a subtype, found '" + g.typeName() + "'");
                    continue;
                }
                registerInstance(g.location(), g.instanceId(), InstanceKind.GROUP, g.typeName());
                roots.add(buildGroupInstance(g, actualRoot, List.of(actualRoot),
                        List.of(g.instanceId())));
            }
            checkAgentProfiles();
            checkInheritedRoleContexts();
            checkCompatibility();
            List<AolLink> links = checkLinks();
            if (!errors.isEmpty()) return new Result(null, errors);
            List<String> agents = List.copyOf(agentNames);
            return new Result(new AolModel(ast.version(), ast.name(), ast.aclFile(), agents, agentProfiles,
                    agentValues, roots,
                    topLevelEntities, links), errors);
        }

        private List<AclAttribute> effectiveRoleAttributes(String roleName) {
            Map<String, AclAttribute> result = new LinkedHashMap<>();
            String current = roleName;
            Set<String> seen = new LinkedHashSet<>();
            while (current != null && seen.add(current)) {
                AclRole role = acl.findRole(current).orElse(null);
                if (role == null) break;
                for (AclAttribute attribute : role.attributes()) {
                    result.putIfAbsent(attribute.name(), attribute);
                }
                current = role.parentRoles().isEmpty() ? null : role.parentRoles().get(0);
            }
            return List.copyOf(result.values());
        }

        private void checkAgentProfiles() {
            for (Occurrence occurrence : occurrences) {
                String profile = agentProfiles.get(occurrence.agentId());
                if (profile != null && !isRoleOrSubtype(occurrence.roleType(), profile)) {
                    errorNoLocation("agent '" + occurrence.agentId() + "' has profile '" + profile
                            + "' and cannot play unrelated role '" + occurrence.roleType() + "'");
                }
            }
        }

        /**
         * Concrete ancestor Role attributes belong to an ancestor occurrence, not to the
         * Agent and not to the child occurrence. The matching occurrence must be played by
         * the same Agent and live in the same Group instance or one of its containers.
         * Abstract ancestors are Agent profiles and therefore need no occurrence here.
         */
        private void checkInheritedRoleContexts() {
            for (Occurrence child : occurrences) {
                String current = child.roleType();
                Set<String> seen = new LinkedHashSet<>();
                while (seen.add(current)) {
                    AclRole role = acl.findRole(current).orElse(null);
                    if (role == null || role.parentRoles().isEmpty()) break;
                    current = role.parentRoles().get(0);
                    AclRole ancestor = acl.findRole(current).orElse(null);
                    if (ancestor == null || ancestor.isAbstract() || ancestor.attributes().isEmpty()) continue;

                    List<Occurrence> candidates = occurrences.stream()
                            .filter(value -> value.agentId().equals(child.agentId()))
                            .filter(value -> value.roleType().equals(ancestor.name()))
                            .filter(value -> isPrefix(value.instancePath(), child.instancePath()))
                            .toList();
                    if (candidates.isEmpty()) {
                        errorNoLocation("role occurrence '" + child.instanceId() + "' of type '" + child.roleType()
                                + "' inherits state from concrete Role '" + ancestor.name()
                                + "', but agent '" + child.agentId() + "' has no '" + ancestor.name()
                                + "' occurrence in the same or a containing Group instance");
                        continue;
                    }
                    int nearestDepth = candidates.stream().mapToInt(value -> value.instancePath().size()).max().orElse(-1);
                    long nearest = candidates.stream().filter(value -> value.instancePath().size() == nearestDepth).count();
                    if (nearest != 1) {
                        errorNoLocation("role occurrence '" + child.instanceId() + "' has " + nearest
                                + " equally-near '" + ancestor.name() + "' ancestor occurrences for agent '"
                                + child.agentId() + "'; inherited attribute lookup must be unambiguous");
                    }
                }
            }
        }

        private static boolean isPrefix(List<String> prefix, List<String> value) {
            return prefix.size() <= value.size() && value.subList(0, prefix.size()).equals(prefix);
        }

        private boolean registerInstance(AclSourceLocationCS loc, String id, InstanceKind kind, String typeName) {
            if (!instanceIds.add(id)) {
                error(loc, "duplicate instance id '" + id + "' -- ids must be unique across the whole model"
                        + " so 'link' declarations can resolve them unambiguously");
                return false;
            }
            instanceIndex.put(id, new InstanceRef(kind, typeName));
            return true;
        }

        private AolGroupInstance buildGroupInstance(AolGroupInstanceCS x, AclGroup type,
                                                    List<AclGroup> typePath,
                                                    List<String> instancePath) {
            Map<String, String> groupValues = attributeValues(x.attributeValues(), effectiveGroupAttributes(type), x.location(),
                    "group '" + type.name() + "'");
            Map<String, AclRoleMembership> roleTypes = new LinkedHashMap<>();
            groupHierarchy(type).forEach(group -> group.roles().forEach(m -> roleTypes.put(m.roleName(), m)));
            Map<String, AclEntityMembership> entityTypes = new LinkedHashMap<>();
            entityTypes.putAll(directEntityTypes(type));
            Map<String, AclSubgroupMembership> subgroupTypes = new LinkedHashMap<>();
            subgroupTypes.putAll(ownedSubgroupTypes(type));

            List<AolPlay> plays = new ArrayList<>();
            Set<String> playIds = new LinkedHashSet<>();
            Map<String, Integer> roleCounts = new LinkedHashMap<>();
            for (AolPlayCS p : x.plays()) {
                boolean uniqueLocally = playIds.add(p.instanceId());
                if (!uniqueLocally) error(p.location(), "duplicate role instance id '" + p.instanceId() + "'");
                AclRoleMembership membership = roleTypes.get(p.roleType());
                if (membership == null) {
                    error(p.location(), "role type '" + p.roleType() + "' is not a member of group '" + type.name() + "'");
                    continue;
                }
                acl.findRole(p.roleType()).ifPresentOrElse(role -> {
                    if (role.isAbstract()) {
                        error(p.location(), "abstract role '" + p.roleType() + "' cannot be played directly");
                        return;
                    }
                    if (!agentNames.contains(p.agentId())) {
                        error(p.location(), "unknown agent '" + p.agentId() + "' playing '" + p.roleType() + "'");
                    }
                    Map<String, String> values = attributeValues(p.attributeValues(), role.attributes(), p.location(),
                            "role '" + p.roleType() + "'");
                    plays.add(new AolPlay(p.roleType(), p.instanceId(), p.agentId(), values));
                    roleCounts.merge(p.roleType(), 1, Integer::sum);
                    if (uniqueLocally) registerInstance(p.location(), p.instanceId(), InstanceKind.PLAY, p.roleType());
                    if (agentNames.contains(p.agentId())) {
                        occurrences.add(new Occurrence(p.instanceId(), p.agentId(), p.roleType(), typePath, instancePath));
                    }
                }, () -> error(p.location(), "unknown role type '" + p.roleType() + "'"));
            }
            roleTypes.forEach((name, membership) -> checkCardinality(x.location(), "role", name,
                    membership.cardinality(), roleCounts.getOrDefault(name, 0), type.name()));

            List<AolEntityInstance> entities = new ArrayList<>();
            Set<String> entityIds = new LinkedHashSet<>();
            Map<String, Integer> entityCounts = new LinkedHashMap<>();
            for (AolEntityInstanceCS e : x.entities()) {
                boolean uniqueLocally = entityIds.add(e.instanceId());
                if (!uniqueLocally) error(e.location(), "duplicate entity instance id '" + e.instanceId() + "'");
                if (!entityTypes.containsKey(e.entityType())) {
                    error(e.location(), "entity type '" + e.entityType() + "' is not a member of group '" + type.name() + "'");
                    continue;
                }
                acl.findEntity(e.entityType()).ifPresentOrElse(entity -> {
                    Map<String, String> values = attributeValues(e.attributeValues(), entity.attributes(), e.location(),
                            "entity '" + e.entityType() + "'");
                    entities.add(new AolEntityInstance(e.entityType(), e.instanceId(), values));
                    entityCounts.merge(e.entityType(), 1, Integer::sum);
                    if (uniqueLocally) registerInstance(e.location(), e.instanceId(), InstanceKind.ENTITY, e.entityType());
                }, () -> error(e.location(), "unknown entity type '" + e.entityType() + "'"));
            }
            entityTypes.forEach((name, membership) -> checkCardinality(x.location(), "entity", name,
                    membership.cardinality(), entityCounts.getOrDefault(name, 0), type.name()));

            List<AolGroupInstance> subgroups = new ArrayList<>();
            Set<String> subgroupIds = new LinkedHashSet<>();
            Map<String, Integer> subgroupCounts = new LinkedHashMap<>();
            for (AolGroupInstanceCS sub : x.subgroups()) {
                boolean uniqueLocally = subgroupIds.add(sub.instanceId());
                if (!uniqueLocally) error(sub.location(), "duplicate group instance id '" + sub.instanceId() + "'");
                AclSubgroupMembership membership = subgroupTypes.get(sub.typeName());
                if (membership == null) {
                    error(sub.location(), "'" + sub.typeName() + "' is not a subgroup of '" + type.name() + "'");
                    continue;
                }
                if (uniqueLocally) registerInstance(sub.location(), sub.instanceId(), InstanceKind.GROUP, sub.typeName());
                List<AclGroup> childPath = new ArrayList<>(typePath);
                childPath.add(membership.group());
                List<String> childInstances = new ArrayList<>(instancePath);
                childInstances.add(sub.instanceId());
                subgroups.add(buildGroupInstance(sub, membership.group(), List.copyOf(childPath),
                        List.copyOf(childInstances)));
                subgroupCounts.merge(sub.typeName(), 1, Integer::sum);
            }
            subgroupTypes.forEach((name, membership) -> checkCardinality(x.location(), "subgroup", name,
                    membership.cardinality(), subgroupCounts.getOrDefault(name, 0), type.name()));

            for (AclCardinalityConstraint constraint : type.cardinalityConstraints()) {
                Map<String, Integer> counts = switch (constraint.targetKind()) {
                    case ROLE -> roleCounts;
                    case ENTITY -> entityCounts;
                    case SUBGROUP -> subgroupCounts;
                };
                checkCardinality(x.location(), constraint.targetKind().sourceName() + " constraint",
                        constraint.targetName(), constraint.cardinality(),
                        counts.getOrDefault(constraint.targetName(), 0), type.name());
            }

            return new AolGroupInstance(x.typeName(), x.instanceId(), subgroups, plays, entities, groupValues);
        }

        /** Group-to-Group ownership is represented by AclOwner in the current metamodel. */
        private Map<String, AclSubgroupMembership> ownedSubgroupTypes(AclGroup source) {
            Map<String, AclSubgroupMembership> result = new LinkedHashMap<>();
            for (AclOwner owner : acl.owners()) {
                if (!isGroupOrSubtype(source.name(), owner.sourceGroup())) continue;
                acl.groups().stream().filter(group -> group.name().equals(owner.target())).findFirst()
                        .ifPresent(group -> result.put(group.name(),
                                new AclSubgroupMembership(group, owner.multiplicity())));
            }
            return result;
        }

        /**
         * Structural conformance constraint for Entity placement. An Entity
         * may occur inside a Group instance only when the ACL specification
         * declares a direct Relationship between that Group type and the
         * Entity type. Reachability through another Entity is deliberately
         * insufficient: it would allow AOL to invent a different hierarchy.
         */
        private Map<String, AclEntityMembership> directEntityTypes(AclGroup source) {
            Map<String, AclEntityMembership> result = new LinkedHashMap<>();
            for (var relation : acl.relations()) {
                String entityName = null;
                AclCardinality multiplicity = null;
                if (isGroupOrSubtype(source.name(), relation.source().type())
                        && acl.findEntity(relation.target().type()).isPresent()) {
                    entityName = relation.target().type();
                    multiplicity = relation.target().multiplicity();
                } else if (isGroupOrSubtype(source.name(), relation.target().type())
                        && acl.findEntity(relation.source().type()).isPresent()) {
                    entityName = relation.source().type();
                    multiplicity = relation.source().multiplicity();
                }
                if (entityName != null) {
                    result.put(entityName, new AclEntityMembership(entityName, multiplicity));
                }
            }
            return result;
        }

        private void checkCardinality(AclSourceLocationCS loc, String kind, String targetName,
                                      AclCardinality bound, int actual, String groupTypeName) {
            if (!withinBound(bound, actual)) {
                error(loc, "group '" + groupTypeName + "': " + kind + " '" + targetName + "' has " + actual
                        + " instance(s), expected [" + boundText(bound) + "]");
            }
        }

        private static boolean withinBound(AclCardinality bound, int actual) {
            boolean withinMax = bound.max().isEmpty() || actual <= bound.max().getAsInt();
            return actual >= bound.min() && withinMax;
        }

        private static String boundText(AclCardinality bound) {
            return bound.min() + ".." + (bound.max().isPresent() ? Integer.toString(bound.max().getAsInt()) : "*");
        }

        private Map<String, String> attributeValues(List<AolAttributeValueCS> values, List<AclAttribute> attributes,
                                                     AclSourceLocationCS loc, String owner) {
            Map<String, AclAttribute> byName = new LinkedHashMap<>();
            attributes.forEach(a -> byName.put(a.name(), a));
            Map<String, String> result = new LinkedHashMap<>();
            for (AolAttributeValueCS v : values) {
                AclAttribute attribute = byName.get(v.name());
                if (attribute == null) {
                    error(v.location(), "unknown attribute '" + v.name() + "' for " + owner);
                    continue;
                }
                if (result.containsKey(v.name())) error(v.location(), "duplicate attribute value '" + v.name() + "' for " + owner);
                if (!validValue(attribute.type(), v.rawValue())) {
                    error(v.location(), "value '" + v.rawValue() + "' is incompatible with type '"
                            + attribute.type().sourceName() + "' for attribute '" + v.name() + "' of " + owner);
                }
                result.put(v.name(), v.rawValue());
            }
            for (AclAttribute attribute : attributes) {
                if (!attribute.optional() && attribute.defaultValue().isEmpty() && !result.containsKey(attribute.name())) {
                    error(loc, "missing required attribute '" + attribute.name() + "' for " + owner);
                }
            }
            return result;
        }

        private boolean validValue(AclDataType type, String value) {
            if (type instanceof AclEnum e) return e.literals().contains(value);
            if (!(type instanceof AclPrimitiveType primitive)) return false;
            String raw = value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")
                    ? value.substring(1, value.length() - 1) : value;
            try {
                return switch (primitive) {
                    case STRING -> value.startsWith("\"") && value.endsWith("\"");
                    case INTEGER -> { Integer.parseInt(raw); yield true; }
                    case REAL -> { Double.parseDouble(raw); yield true; }
                    case BOOLEAN -> raw.equals("true") || raw.equals("false");
                };
            } catch (NumberFormatException ex) { return false; }
        }

        /** Default: two distinct Role types held by the same Agent are incompatible unless
         *  declared 'compatible' at the nearest common ancestor group type -- and if either
         *  occurrence sits in a deeper subgroup than that ancestor, the declaration must also
         *  say extends-subgroups true. */
        private void checkCompatibility() {
            Map<String, List<Occurrence>> byAgent = new LinkedHashMap<>();
            occurrences.forEach(o -> byAgent.computeIfAbsent(o.agentId(), k -> new ArrayList<>()).add(o));
            for (var entry : byAgent.entrySet()) {
                List<Occurrence> list = entry.getValue();
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        Occurrence a = list.get(i);
                        Occurrence b = list.get(j);
                        if (a.roleType().equals(b.roleType())) continue;
                        checkPair(entry.getKey(), a, b);
                    }
                }
            }
        }

        private void checkPair(String agentId, Occurrence a, Occurrence b) {
            int lca = 0;
            while (lca < a.instancePath().size() && lca < b.instancePath().size()
                    && a.instancePath().get(lca).equals(b.instancePath().get(lca))) lca++;
            lca--;
            if (lca < 0) {
                // Plays in different root Group instances do not share a compatibility scope.
                return;
            }
            AclGroup scope = a.typePath().get(lca);
            boolean aDirect = a.typePath().size() - 1 == lca;
            boolean bDirect = b.typePath().size() - 1 == lca;
            boolean sameGroupInstance = aDirect && bDirect;
            boolean ok = acl.compatibilities().stream().anyMatch(c -> {
                boolean matches = (isRoleOrSubtype(a.roleType(), c.fromRole()) && isRoleOrSubtype(b.roleType(), c.toRole()))
                        || (c.bidirectional() && isRoleOrSubtype(a.roleType(), c.toRole())
                        && isRoleOrSubtype(b.roleType(), c.fromRole()));
                if (!matches || c.type() != AclCompatibilityType.COMPATIBLE
                        || !scope.name().equals(c.groupName())) return false;
                // 'intra-group' only holds for two occurrences directly in the same group
                // instance; extendsSubgroups has no meaning there. 'inter-group' keeps the
                // original reach-into-subgroups escape hatch.
                return switch (c.scope()) {
                    case INTRA_GROUP -> sameGroupInstance;
                    case INTER_GROUP -> sameGroupInstance || c.extendsSubgroups();
                };
            });
            if (!ok) {
                errorNoLocation("agent '" + agentId + "' cannot hold both '" + a.roleType() + "' and '" + b.roleType()
                        + "' at once in group '" + scope.name() + "' -- not declared compatible (default: incompatible)");
            }
        }

        private boolean isRoleOrSubtype(String actual, String declared) {
            String current = actual;
            Set<String> seen = new LinkedHashSet<>();
            while (seen.add(current)) {
                if (current.equals(declared)) return true;
                var role = acl.findRole(current);
                if (role.isEmpty() || role.get().parentRoles().isEmpty()) return false;
                current = role.get().parentRoles().get(0);
            }
            return false;
        }

        private boolean isEntityOrSubtype(String actual, String declared) {
            String current = actual;
            Set<String> seen = new LinkedHashSet<>();
            while (seen.add(current)) {
                if (current.equals(declared)) return true;
                var entity = acl.findEntity(current);
                if (entity.isEmpty() || entity.get().specializes().isEmpty()) return false;
                current = entity.get().specializes().get();
            }
            return false;
        }

        private boolean isGroupOrSubtype(String actual, String declared) {
            String current = actual;
            Set<String> seen = new LinkedHashSet<>();
            while (seen.add(current)) {
                if (current.equals(declared)) return true;
                String currentName = current;
                var group = acl.groups().stream().filter(g -> g.name().equals(currentName)).findFirst();
                if (group.isEmpty() || group.get().specializes().isEmpty()) return false;
                current = group.get().specializes().get();
            }
            return false;
        }

        private AclGroup findGroup(String name) {
            return acl.groups().stream().filter(group -> group.name().equals(name)).findFirst().orElse(null);
        }

        /** Base-to-derived order, so a subtype can refine a same-named inherited member. */
        private List<AclGroup> groupHierarchy(AclGroup type) {
            List<AclGroup> hierarchy = new ArrayList<>();
            Set<String> seen = new LinkedHashSet<>();
            AclGroup current = type;
            while (current != null && seen.add(current.name())) {
                hierarchy.add(0, current);
                current = current.specializes().map(this::findGroup).orElse(null);
            }
            return hierarchy;
        }

        private List<AclAttribute> effectiveGroupAttributes(AclGroup type) {
            Map<String, AclAttribute> result = new LinkedHashMap<>();
            groupHierarchy(type).forEach(group -> group.attributes()
                    .forEach(attribute -> result.put(attribute.name(), attribute)));
            return List.copyOf(result.values());
        }

        private boolean matchesEndpointType(InstanceRef ref, String endpointType) {
            return switch (ref.kind()) {
                case AGENT -> isRoleOrSubtype(ref.typeName(), endpointType)
                        && acl.findRole(endpointType).map(AclRole::isAbstract).orElse(false);
                case PLAY -> isRoleOrSubtype(ref.typeName(), endpointType);
                case ENTITY -> isEntityOrSubtype(ref.typeName(), endpointType);
                case GROUP -> isGroupOrSubtype(ref.typeName(), endpointType);
            };
        }

        /** Instantiates every 'link' against the ACL relation it names: resolves both ends
         *  through the global instance index built while walking agents/entities/groups above,
         *  checks each end's (sub)type against the relation's two endpoints (order-agnostic --
         *  ACL relations, like UML associations, don't fix which end is "source"), and checks
         *  the target count against the matched endpoint's cardinality. */
        private List<AolLink> checkLinks() {
            List<AolLink> result = new ArrayList<>();
            for (AolLinkCS link : ast.links()) {
                AclRelation relation = acl.relations().stream()
                        .filter(r -> r.name().equals(link.relationName())).findFirst().orElse(null);
                if (relation == null) {
                    error(link.location(), "unknown relation '" + link.relationName() + "'");
                    continue;
                }
                InstanceRef source = instanceIndex.get(link.sourceInstanceId());
                if (source == null) {
                    error(link.location(), "unknown instance '" + link.sourceInstanceId()
                            + "' as source of link '" + link.relationName() + "'");
                    continue;
                }
                AclEndpoint sourceEndpoint = matchesEndpointType(source, relation.source().type()) ? relation.source()
                        : matchesEndpointType(source, relation.target().type()) ? relation.target() : null;
                if (sourceEndpoint == null) {
                    error(link.location(), "instance '" + link.sourceInstanceId() + "' (" + source.typeName()
                            + ") does not match either end of relation '" + link.relationName() + "'");
                    continue;
                }
                AclEndpoint targetEndpoint = sourceEndpoint == relation.source() ? relation.target() : relation.source();

                List<String> validTargets = new ArrayList<>();
                for (String targetId : link.targetInstanceIds()) {
                    InstanceRef target = instanceIndex.get(targetId);
                    if (target == null) {
                        error(link.location(), "unknown instance '" + targetId + "' as target of link '"
                                + link.relationName() + "'");
                        continue;
                    }
                    if (!matchesEndpointType(target, targetEndpoint.type())) {
                        error(link.location(), "instance '" + targetId + "' (" + target.typeName()
                                + ") does not match relation '" + link.relationName() + "' endpoint type '"
                                + targetEndpoint.type() + "'");
                        continue;
                    }
                    validTargets.add(targetId);
                }
                if (!withinBound(targetEndpoint.multiplicity(), validTargets.size())) {
                    error(link.location(), "link '" + link.relationName() + "' from '" + link.sourceInstanceId()
                            + "' has " + validTargets.size() + " '" + targetEndpoint.type()
                            + "' target(s), expected [" + boundText(targetEndpoint.multiplicity()) + "]");
                }
                result.add(new AolLink(link.relationName(), link.sourceInstanceId(), validTargets));
            }
            return result;
        }

        private void error(AclSourceLocationCS loc, String message) { errors.add(new SemanticError(loc, message)); }
        private void errorNoLocation(String message) { errors.add(new SemanticError(new AclSourceLocationCS(1, 0), message)); }
    }
}
