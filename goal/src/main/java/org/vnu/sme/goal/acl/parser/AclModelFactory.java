package org.vnu.sme.goal.acl.parser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.vnu.sme.goal.acl.ast.*;
import org.vnu.sme.goal.acl.mm.*;

/** AST -> the single ACLStructuralMetamodel root (StructuralSpecification). */
public final class AclModelFactory {
    public record SemanticError(AclSourceLocationCS location, String message) {}
    public record Result(AclModel model, List<SemanticError> errors) {
        public Result { errors = List.copyOf(errors); }
        public boolean ok() { return errors.isEmpty() && model != null; }
    }
    private AclModelFactory() {}

    public static Result create(AclModelCS ast) { return new Build(ast).run(); }

    private static final class Build {
        private final AclModelCS ast;
        private final List<SemanticError> errors = new ArrayList<>();
        private final Map<String, AclEnumCS> enumCs = new LinkedHashMap<>();
        private final Map<String, AclRoleCS> roleCs = new LinkedHashMap<>();
        private final Map<String, AclEntityCS> entityCs = new LinkedHashMap<>();
        private final Map<String, AclGroupCS> groupCs = new LinkedHashMap<>();
        private final Map<String, AclEnum> enums = new LinkedHashMap<>();
        private final Map<String, AclRole> roles = new LinkedHashMap<>();
        private final Map<String, AclEntity> entities = new LinkedHashMap<>();
        private final Map<String, List<AclAttribute>> effective = new LinkedHashMap<>();

        Build(AclModelCS ast) { this.ast = Objects.requireNonNull(ast, "ast"); }

        Result run() {
            register();
            buildEnums();
            validateInheritance();
            buildEntities();
            buildRoles();
            if (ast.roles().isEmpty()) error(ast.location(), "StructuralSpecification requires at least one RoleDefinition");
            if (ast.groups().size() != 1) {
                error(ast.location(), "StructuralSpecification must contain exactly one rootGroup (found "
                        + ast.groups().size() + ")");
            }
            if (ast.groups().isEmpty()) return new Result(null, errors);
            AclGroup root = buildGroup(ast.groups().get(0), Collections.newSetFromMap(new IdentityHashMap<>()));
            List<AclRoleInheritance> inheritances = roleCs.values().stream()
                    .flatMap(r -> r.parentRoles().stream().map(p -> new AclRoleInheritance(r.name(), p))).toList();
            AclModel model = new AclModel(ast.version(), ast.name(), List.copyOf(enums.values()),
                    List.copyOf(roles.values()), List.copyOf(entities.values()), inheritances, root);
            return new Result(model, errors);
        }

        private void register() {
            Map<String, String> ids = new LinkedHashMap<>();
            for (AclEnumCS x : ast.enums()) put(ids, enumCs, x.name(), "enum", x, x.location());
            for (AclRoleCS x : ast.roles()) put(ids, roleCs, x.name(), "role", x, x.location());
            for (AclEntityCS x : ast.entities()) put(ids, entityCs, x.name(), "entity", x, x.location());
            for (AclGroupCS x : ast.groups()) put(ids, groupCs, x.name(), "group", x, x.location());
        }

        private <T> void put(Map<String, String> ids, Map<String, T> dest, String id, String kind,
                             T value, AclSourceLocationCS loc) {
            String old = ids.putIfAbsent(id, kind);
            if (old != null) error(loc, "duplicate top-level identifier '" + id + "' (already " + old + ")");
            else dest.put(id, value);
        }

        private void buildEnums() {
            for (AclEnumCS x : enumCs.values()) {
                Set<String> literals = new LinkedHashSet<>();
                for (String literal : x.literals()) if (!literals.add(literal))
                    error(x.location(), "duplicate enum literal '" + literal + "' in '" + x.name() + "'");
                enums.put(x.name(), new AclEnum(x.name(), List.copyOf(literals)));
            }
        }

        private void validateInheritance() {
            for (AclRoleCS role : roleCs.values()) {
                Set<String> seen = new LinkedHashSet<>();
                for (String parent : role.parentRoles()) {
                    if (!seen.add(parent)) error(role.location(), "duplicate parent role '" + parent + "'");
                    if (!roleCs.containsKey(parent)) error(role.location(), "unknown parent role '" + parent + "'");
                    if (parent.equals(role.name())) error(role.location(), "role inheritance cannot reference itself");
                }
            }
            for (String role : roleCs.keySet()) detectCycle(role, new LinkedHashSet<>());
        }

        private void detectCycle(String role, Set<String> path) {
            if (!path.add(role)) { error(roleCs.get(role).location(), "role inheritance cycle involving '" + role + "'"); return; }
            for (String parent : roleCs.get(role).parentRoles()) if (roleCs.containsKey(parent)) detectCycle(parent, path);
            path.remove(role);
        }

        private void buildEntities() {
            for (AclEntityCS x : entityCs.values()) entities.put(x.name(), new AclEntity(x.name(), attributes(x.name(), x.attributes())));
        }

        private void buildRoles() {
            for (AclRoleCS x : roleCs.values()) roles.put(x.name(), new AclRole(x.name(), x.isAbstract(), x.parentRoles(), effectiveAttributes(x.name())));
        }

        private List<AclAttribute> effectiveAttributes(String name) {
            List<AclAttribute> known = effective.get(name); if (known != null) return known;
            AclRoleCS x = roleCs.get(name); if (x == null) return List.of();
            Map<String, AclAttribute> merged = new LinkedHashMap<>();
            for (String parent : x.parentRoles()) for (AclAttribute a : effectiveAttributes(parent)) merged.putIfAbsent(a.name(), a);
            for (AclAttribute a : attributes(name, x.attributes())) {
                if (merged.containsKey(a.name()) && !merged.get(a.name()).equals(a)) error(x.location(), "attribute '" + a.name() + "' conflicts with inherited attribute");
                merged.put(a.name(), a);
            }
            List<AclAttribute> result = List.copyOf(merged.values()); effective.put(name, result); return result;
        }

        private List<AclAttribute> attributes(String owner, List<AclAttributeCS> source) {
            Map<String, AclAttribute> result = new LinkedHashMap<>();
            for (AclAttributeCS x : source) {
                if (result.containsKey(x.name())) { error(x.location(), "duplicate attribute '" + x.name() + "' in '" + owner + "'"); continue; }
                Optional<AclDataType> type = resolveType(x.typeName());
                if (type.isEmpty()) { error(x.location(), "unknown attribute type '" + x.typeName() + "'"); continue; }
                if (x.defaultValue().isPresent() && !validDefault(type.get(), x.defaultValue().get()))
                    error(x.location(), "default value '" + x.defaultValue().get() + "' is incompatible with type '" + x.typeName() + "'");
                result.put(x.name(), new AclAttribute(x.name(), type.get(), x.required(), x.mutable(), x.defaultValue()));
            }
            return List.copyOf(result.values());
        }

        private Optional<AclDataType> resolveType(String name) {
            Optional<AclPrimitiveType> primitive = AclPrimitiveType.fromSource(name);
            return primitive.<Optional<AclDataType>>map(Optional::of).orElseGet(() -> Optional.ofNullable(enums.get(name)));
        }

        private boolean validDefault(AclDataType type, String value) {
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

        private AclGroup buildGroup(AclGroupCS x, Set<AclGroupCS> active) {
            if (!active.add(x)) { error(x.location(), "cycle in subgroup structure at '" + x.name() + "'"); return emptyGroup(x.name()); }
            List<AclRoleMembership> roleMembers = new ArrayList<>();
            Set<String> roleSeen = new LinkedHashSet<>();
            for (AclRoleMembershipCS m : x.roles()) {
                AclRole role = roles.get(m.roleName());
                if (!roleSeen.add(m.roleName())) error(m.location(), "duplicate role membership '" + m.roleName() + "'");
                if (role == null) error(m.location(), "unknown role membership '" + m.roleName() + "'");
                else if (role.isAbstract()) error(m.location(), "abstract role '" + m.roleName() + "' cannot be enacted");
                parse(m.cardinality()).ifPresent(c -> roleMembers.add(new AclRoleMembership(m.roleName(), c)));
            }
            List<AclEntityMembership> entityMembers = new ArrayList<>();
            Set<String> entitySeen = new LinkedHashSet<>();
            for (AclEntityMembershipCS m : x.entities()) {
                if (!entitySeen.add(m.entityName())) error(m.location(), "duplicate entity membership '" + m.entityName() + "'");
                if (!entities.containsKey(m.entityName())) error(m.location(), "unknown entity membership '" + m.entityName() + "'");
                parse(m.cardinality()).ifPresent(c -> entityMembers.add(new AclEntityMembership(m.entityName(), c)));
            }
            List<AclSubgroupMembership> subgroups = new ArrayList<>();
            Set<String> subgroupSeen = new LinkedHashSet<>();
            for (AclSubgroupMembershipCS m : x.subgroups()) {
                if (!subgroupSeen.add(m.group().name())) error(m.location(), "duplicate subgroup '" + m.group().name() + "'");
                Optional<AclCardinality> c = parse(m.cardinality());
                AclGroup child = buildGroup(m.group(), active); if (c.isPresent()) subgroups.add(new AclSubgroupMembership(child, c.get()));
            }
            List<AclLink> links = x.links().stream().map(v -> link(x, v)).flatMap(Optional::stream).toList();
            List<AclCompatibility> compat = x.compatibilities().stream().map(v -> compatibility(x, v)).flatMap(Optional::stream).toList();
            Set<String> relationNames = new LinkedHashSet<>();
            for (AclRoleEntityRelationCS relation : x.roleEntityRelations()) if (!relationNames.add(relation.name()))
                error(relation.location(), "duplicate RoleEntityRelation name '" + relation.name() + "' in group '" + x.name() + "'");
            List<AclRoleEntityRelation> entityRelations = x.roleEntityRelations().stream().map(v -> entityRelation(x, v)).flatMap(Optional::stream).toList();
            AclGroup partial = new AclGroup(x.name(), roleMembers, entityMembers, subgroups, links, compat, entityRelations, List.of());
            List<AclCardinalityConstraint> constraints = x.cardinalityConstraints().stream().map(c -> constraint(partial, c)).flatMap(Optional::stream).toList();
            active.remove(x);
            return new AclGroup(x.name(), roleMembers, entityMembers, subgroups, links, compat, entityRelations, constraints);
        }

        private AclGroup emptyGroup(String name) { return new AclGroup(name, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of()); }

        private Optional<AclLink> link(AclGroupCS owner, AclLinkCS x) {
            Optional<AclLinkType> type = AclLinkType.fromSource(x.type());
            OptionValues o = options(x.options(), x.bidirectionalArrow(), x.location());
            endpoint(x.fromRole(), x.location(), "link"); endpoint(x.toRole(), x.location(), "link");
            if (type.isEmpty()) error(x.location(), "invalid link type '" + x.type() + "'");
            return type.isPresent() && o.scope != null ? Optional.of(new AclLink(x.fromRole(), x.toRole(), type.get(), o.scope, o.extendsSubgroups, o.bidirectional)) : Optional.empty();
        }

        private Optional<AclCompatibility> compatibility(AclGroupCS owner, AclCompatibilityCS x) {
            OptionValues o = options(x.options(), x.bidirectionalArrow(), x.location());
            endpoint(x.fromRole(), x.location(), "compatibility"); endpoint(x.toRole(), x.location(), "compatibility");
            return o.scope == null ? Optional.empty() : Optional.of(new AclCompatibility(x.fromRole(), x.toRole(), o.scope, o.extendsSubgroups, o.bidirectional));
        }

        private Optional<AclRoleEntityRelation> entityRelation(AclGroupCS owner, AclRoleEntityRelationCS x) {
            AclRole source = roles.get(x.sourceRole()); AclEntity target = entities.get(x.targetEntity());
            if (source == null) error(x.location(), "unknown RoleEntityRelation sourceRole '" + x.sourceRole() + "'");
            if (target == null) error(x.location(), "unknown RoleEntityRelation targetEntity '" + x.targetEntity() + "'");
            Optional<AclRoleEntityRelationType> type = AclRoleEntityRelationType.fromSource(x.type());
            if (type.isEmpty()) error(x.location(), "invalid RoleEntityRelation type '" + x.type() + "'");
            OptionValues o = options(x.options(), false, x.location());
            return source != null && target != null && type.isPresent() && o.scope != null
                    ? Optional.of(new AclRoleEntityRelation(x.name(), source, target, type.get(), o.scope, o.extendsSubgroups)) : Optional.empty();
        }

        private void endpoint(String name, AclSourceLocationCS loc, String kind) { if (!roles.containsKey(name)) error(loc, "unknown " + kind + " role endpoint '" + name + "'"); }

        private OptionValues options(List<AclLinkOptionCS> options, boolean arrowBidirectional, AclSourceLocationCS loc) {
            OptionValues r = new OptionValues(); r.bidirectional = arrowBidirectional;
            for (AclLinkOptionCS x : options) {
                if (x instanceof AclLinkOptionCS.ScopeCS s) {
                    Optional<AclScope> scope = AclScope.fromSource(s.value()); if (scope.isEmpty()) error(s.location(), "invalid scope '" + s.value() + "'"); else r.scope = scope.get();
                } else if (x instanceof AclLinkOptionCS.ExtendsSubgroupsCS e) r.extendsSubgroups = e.value();
                else if (x instanceof AclLinkOptionCS.BidirectionalCS b) r.bidirectional = b.value();
            }
            if (r.scope == null) error(loc, "missing scope option"); return r;
        }

        private Optional<AclCardinalityConstraint> constraint(AclGroup group, AclCardinalityConstraintCS x) {
            Optional<AclCardinalityTargetKind> kind = AclCardinalityTargetKind.fromSource(x.targetKind());
            if (kind.isEmpty()) { error(x.location(), "invalid cardinality target kind '" + x.targetKind() + "'"); return Optional.empty(); }
            AclCardinalityTarget target = findTarget(group, kind.get(), x.targetName());
            if (target == null) { error(x.location(), "cardinality target '" + x.targetName() + "' is not reachable from group '" + group.name() + "'"); return Optional.empty(); }
            return parse(x.cardinality()).map(c -> new AclCardinalityConstraint(kind.get(), target, c));
        }

        private AclCardinalityTarget findTarget(AclGroup group, AclCardinalityTargetKind kind, String name) {
            if (kind == AclCardinalityTargetKind.ROLE && group.roles().stream().anyMatch(m -> m.roleName().equals(name))) return roles.get(name);
            if (kind == AclCardinalityTargetKind.ENTITY && group.entities().stream().anyMatch(m -> m.entityName().equals(name))) return entities.get(name);
            for (AclSubgroupMembership s : group.subgroups()) { if (kind == AclCardinalityTargetKind.SUBGROUP && s.group().name().equals(name)) return s; AclCardinalityTarget nested = findTarget(s.group(), kind, name); if (nested != null) return nested; }
            return null;
        }

        private Optional<AclCardinality> parse(AclCardinalityCS x) {
            try {
                int min = new BigInteger(x.min()).intValueExact(); if (min < 0) throw new NumberFormatException();
                if (x.max().isEmpty()) return Optional.of(AclCardinality.unlimited(min));
                int max = new BigInteger(x.max().get()).intValueExact(); if (max < min) throw new NumberFormatException();
                return Optional.of(AclCardinality.bounded(min, max));
            } catch (RuntimeException ex) { error(x.location(), "invalid cardinality " + x.min() + ".." + x.max().orElse("*")); return Optional.empty(); }
        }

        private void error(AclSourceLocationCS loc, String message) { errors.add(new SemanticError(loc, message)); }
        private static final class OptionValues {
            private AclScope scope;
            private boolean extendsSubgroups;
            private boolean bidirectional;
        }
    }
}
