package org.vnu.sme.goal.dsl.acl.mm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Well-formedness rules for the canonical ACL metamodel. */
public final class AclSemanticValidator {
    private AclSemanticValidator() {}

    public static List<String> validate(AclModel model) {
        List<String> errors = new ArrayList<>();
        Map<String, String> kinds = classifierKinds(model, errors);
        Map<String, AclRole> roles = new LinkedHashMap<>();
        model.roles().forEach(role -> roles.putIfAbsent(role.name(), role));
        Set<String> entities = names(model.entities().stream().map(AclEntity::name).toList());
        Set<String> groups = names(model.groups().stream().map(AclGroup::name).toList());

        validateGeneralizations(model, kinds, errors);
        validateInheritedProperties(model, errors);
        validateRelations(model.relations(), entities, groups, kinds.keySet(), errors);
        validateCompatibility(model.compatibilities(), roles, groups, errors);
        return List.copyOf(errors);
    }

    private static Map<String, String> classifierKinds(AclModel model, List<String> errors) {
        Map<String, String> kinds = new LinkedHashMap<>();
        model.enums().forEach(value -> unique(kinds, value.name(), "Enumeration", errors));
        model.entities().forEach(value -> unique(kinds, value.name(), "Entity", errors));
        model.roles().forEach(value -> unique(kinds, value.name(), "Role", errors));
        model.groups().forEach(value -> unique(kinds, value.name(), "Group", errors));
        if (kinds.containsKey("Agent")) {
            errors.add("classifier name 'Agent' is reserved for the generated ACL Agent class");
        }
        return kinds;
    }

    private static void validateGeneralizations(AclModel model, Map<String, String> kinds,
            List<String> errors) {
        Map<String, String> parent = new LinkedHashMap<>();
        for (AclGeneralization generalization : model.generalizations()) {
            String specificKind = kinds.get(generalization.specific());
            String generalKind = kinds.get(generalization.general());
            if (specificKind == null || generalKind == null) {
                errors.add("specialization '" + generalization.specific() + " -> "
                        + generalization.general() + "' references an unknown classifier");
                continue;
            }
            if (!specificKind.equals(generalKind)) {
                errors.add("specialization '" + generalization.specific() + " -> "
                        + generalization.general() + "' must connect classifiers of the same kind");
                continue;
            }
            if (generalization.specific().equals(generalization.general())) {
                errors.add("classifier '" + generalization.specific() + "' cannot specialize itself");
            }
            String previous = parent.putIfAbsent(generalization.specific(), generalization.general());
            if (previous != null && !previous.equals(generalization.general())) {
                errors.add("classifier '" + generalization.specific() + "' has more than one parent: '"
                        + previous + "' and '" + generalization.general() + "'");
            }
        }
        for (String classifier : parent.keySet()) {
            Set<String> path = new LinkedHashSet<>();
            String current = classifier;
            while (current != null) {
                if (!path.add(current)) {
                    errors.add("specialization cycle involving '" + current + "'");
                    break;
                }
                current = parent.get(current);
            }
        }
    }

    private static void validateInheritedProperties(AclModel model, List<String> errors) {
        Map<String, List<AclAttribute>> attributes = new HashMap<>();
        model.entities().forEach(value -> attributes.put(value.name(), value.attributes()));
        model.groups().forEach(value -> attributes.put(value.name(), value.attributes()));
        Map<String, String> parent = new HashMap<>();
        model.entities().forEach(value -> value.specializes().ifPresent(p -> parent.put(value.name(), p)));
        model.groups().forEach(value -> value.specializes().ifPresent(p -> parent.put(value.name(), p)));

        for (String classifier : attributes.keySet()) {
            Map<String, String> inherited = new LinkedHashMap<>();
            String current = classifier;
            Set<String> seen = new HashSet<>();
            while (current != null && seen.add(current)) {
                for (AclAttribute attribute : attributes.getOrDefault(current, List.of())) {
                    String previous = inherited.putIfAbsent(attribute.name(), attribute.type().sourceName());
                    if (previous != null) {
                        errors.add("property '" + attribute.name() + "' is redefined in inheritance chain of '"
                                + classifier + "'");
                    }
                }
                current = parent.get(current);
            }
        }
    }

    private static void validateRelations(List<AclRelation> relations, Set<String> entities,
            Set<String> groups, Set<String> classifiers, List<String> errors) {
        Set<String> names = new HashSet<>();
        Set<String> groupCompositionPairs = new HashSet<>();
        for (AclRelation relation : relations) {
            if (!names.add(relation.name())) errors.add("duplicate relationship '" + relation.name() + "'");
            if (!classifiers.contains(relation.source().type()) || !classifiers.contains(relation.target().type())) {
                errors.add("relationship '" + relation.name() + "' references an unknown endpoint classifier");
                continue;
            }
            if (relation.source().roleName().isPresent() && relation.target().roleName().isPresent()
                    && relation.source().roleName().get().equals(relation.target().roleName().get())) {
                errors.add("relationship '" + relation.name() + "' has duplicate MemberEnd role name '"
                        + relation.source().roleName().get() + "'");
            }
            boolean sourceEntity = entities.contains(relation.source().type());
            boolean targetEntity = entities.contains(relation.target().type());
            if (!sourceEntity && !targetEntity) {
                if (relation.kind() != RelationKind.COMPOSITION
                        || !groups.contains(relation.source().type())
                        || relation.source().type().equals(relation.target().type())) {
                    errors.add("relationship '" + relation.name()
                            + "' between Role/Group classifiers must be a binary composition"
                            + " from a Group to a different Role or Group");
                } else if (!groupCompositionPairs.add(relation.source().type() + "\0"
                        + relation.target().type())) {
                    errors.add("Group '" + relation.source().type() + "' and member '"
                            + relation.target().type() + "' have more than one composition");
                }
            }
        }
    }

    private record OwnerIndex(Map<String, String> targetOwner, Map<String, String> groupParent) {}

    private static OwnerIndex validateOwners(List<AclOwner> owners, Set<String> roles, Set<String> groups,
            List<String> errors) {
        Map<String, String> targetOwner = new LinkedHashMap<>();
        Map<String, String> groupParent = new LinkedHashMap<>();
        for (AclOwner owner : owners) {
            if (!groups.contains(owner.sourceGroup())) {
                errors.add("Owner source '" + owner.sourceGroup() + "' must be a Group");
            }
            boolean roleTarget = roles.contains(owner.target());
            boolean groupTarget = groups.contains(owner.target());
            if (!roleTarget && !groupTarget) {
                errors.add("Owner target '" + owner.target()
                        + "' must be a Role or Group; Entity members are forbidden");
                continue;
            }
            String previous = targetOwner.putIfAbsent(owner.target(), owner.sourceGroup());
            if (previous != null) {
                errors.add("Owner target '" + owner.target() + "' has more than one owner Group: '"
                        + previous + "' and '" + owner.sourceGroup() + "'");
            }
            if (groupTarget) groupParent.put(owner.target(), owner.sourceGroup());
        }
        detectCycles(groupParent, "Group Owner", errors);
        return new OwnerIndex(Map.copyOf(targetOwner), Map.copyOf(groupParent));
    }

    /** A child Role's owned scope may stay equal or narrow down the Group Owner tree. */
    private static void validateRoleOwnerScopeMonotonicity(Map<String, AclRole> roles,
            OwnerIndex owners, List<String> errors) {
        for (AclRole child : roles.values()) {
            String childOwner = owners.targetOwner().get(child.name());
            String ancestorName = child.name();
            Set<String> seen = new LinkedHashSet<>();
            while (seen.add(ancestorName)) {
                AclRole current = roles.get(ancestorName);
                if (current == null || current.parentRoles().isEmpty()) break;
                ancestorName = current.parentRoles().get(0);
                String ancestorOwner = owners.targetOwner().get(ancestorName);
                if (ancestorOwner == null) continue;
                if (childOwner == null) {
                    errors.add("Role '" + child.name() + "' specializes owned Role '" + ancestorName
                            + "' in Group '" + ancestorOwner + "' but has no Owner; a child Role scope"
                            + " cannot be wider than its parent Role scope");
                } else if (!sameOrDescendantGroup(childOwner, ancestorOwner, owners.groupParent())) {
                    errors.add("invalid Role parent: Role '" + child.name() + "' is owned by Group '"
                            + childOwner + "' and specializes Role '" + ancestorName + "' owned by unrelated"
                            + " Group '" + ancestorOwner + "'; the parent Role Group must be the same Group"
                            + " or an Owner ancestor of the child Role Group");
                }
            }
        }
    }

    private static void validateCompatibility(List<AclCompatibility> compatibilities,
            Map<String, AclRole> roles, Set<String> groups, List<String> errors) {
        Set<String> seen = new HashSet<>();
        for (AclCompatibility compatibility : compatibilities) {
            if (!roles.containsKey(compatibility.fromRole()) || !roles.containsKey(compatibility.toRole())) {
                errors.add("Compatibility endpoints must both be declared Roles: '"
                        + compatibility.fromRole() + "' and '" + compatibility.toRole() + "'");
                continue;
            }
            if (compatibility.fromRole().equals(compatibility.toRole())) {
                errors.add("Compatibility endpoints must be two different Role types; self-compatibility for '"
                        + compatibility.fromRole() + "' cannot override NoSelfConflict");
            }
            if (!groups.contains(compatibility.groupName())) {
                errors.add("Compatibility between '" + compatibility.fromRole() + "' and '"
                        + compatibility.toRole() + "' must be declared inside an existing Group");
                continue;
            }
            String a = compatibility.fromRole().compareTo(compatibility.toRole()) <= 0
                    ? compatibility.fromRole() : compatibility.toRole();
            String b = a.equals(compatibility.fromRole())
                    ? compatibility.toRole() : compatibility.fromRole();
            if (!seen.add(compatibility.groupName() + "\0" + a + "\0" + b)) {
                errors.add("duplicate Compatibility between '" + a + "' and '" + b
                        + "' in Group '" + compatibility.groupName() + "'");
            }
        }
    }

    private static void validateCompatibilityEndpointScope(String role, String declaringGroup,
            OwnerIndex owners, List<String> errors) {
        String owner = owners.targetOwner().get(role);
        if (owner != null && !sameOrDescendantGroup(owner, declaringGroup, owners.groupParent())) {
            errors.add("Compatibility Role '" + role + "' is never owned by declaring Group '"
                    + declaringGroup + "' or one of its nested Groups; its actual owner is '" + owner + "'");
        }
    }

    private static boolean sameOrDescendantGroup(String candidate, String ancestor,
            Map<String, String> groupParent) {
        String current = candidate;
        Set<String> seen = new HashSet<>();
        while (current != null && seen.add(current)) {
            if (current.equals(ancestor)) return true;
            current = groupParent.get(current);
        }
        return false;
    }

    private static void detectCycles(Map<String, String> parent, String label, List<String> errors) {
        for (String node : parent.keySet()) {
            Set<String> path = new LinkedHashSet<>();
            String current = node;
            while (current != null) {
                if (!path.add(current)) {
                    errors.add(label + " cycle involving '" + current + "'");
                    break;
                }
                current = parent.get(current);
            }
        }
    }

    private static Set<String> names(List<String> values) {
        return new LinkedHashSet<>(values);
    }

    private static void unique(Map<String, String> kinds, String name, String kind, List<String> errors) {
        String previous = kinds.putIfAbsent(name, kind);
        if (previous != null) {
            errors.add("duplicate classifier name '" + name + "' (already declared as " + previous + ")");
        }
    }
}
