package org.vnu.sme.goal.acl.use;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.acl.mm.*;

/** Translates the current ACL Class/Relationship metamodel to USE syntax. */
public final class AclUseTranslator {
    private AclUseTranslator() {}

    /** Where a Role instance's owning Group instance can be reached from -- one entry per
     *  Group type that owns this Role (a Role can be a member of more than one Group type,
     *  e.g. a role that is also a member of one of its own subgroups). */
    private record GroupNav(String groupType, String navRoleName) {}

    public static String translate(AclModel model) {
        StringBuilder out = new StringBuilder()
                .append("-- Generated from ACL. Regenerate instead of editing this file.\n")
                .append("-- ACL classes/generalizations map directly to USE classes/generalizations.\n")
                .append("-- Agent--Role and Role--Group are ordinary associations; no association classes are generated.\n")
                .append("-- Compatibility is translated into 'inv NoConflict_<R1>_<R2>' invariants on the Agent class.\n\n")
                .append("model ").append(id(model.name())).append("\n\n");

        model.enums().forEach(value -> out.append("enum ").append(id(value.name())).append(" {")
                .append(String.join(", ", value.literals().stream().map(AclUseTranslator::id).toList()))
                .append("}\n\n"));
        String agentClass = generatedAgentName(model);
        renderClass(out, agentClass, java.util.Optional.empty(), false, List.of());
        model.entities().forEach(value -> renderClass(out, value.name(), value.specializes(), false, value.attributes()));
        model.roles().forEach(value -> renderClass(out, value.name(), value.parentRoles().stream().findFirst(),
                value.isAbstract(), value.attributes()));
        model.groups().forEach(value -> renderClass(out, value.name(), value.specializes(), false, value.attributes()));

        Set<String> names = new HashSet<>();
        for (AclRelation relation : model.relations()) {
            // A Group's Entity member is an implicit structural link in ACL. It must still
            // appear in the USE class diagram; translate membership as an ordinary association.
            String kind = relation.name().startsWith("member_") ? "association" : relation.kind().sourceName();
            relationship(out, unique(names, relation.name()), kind,
                    relation.source().type(), relation.source().multiplicity(),
                    relation.target().type(), relation.target().multiplicity());
        }
        Map<String, List<String>> agentPlaysByRole = new LinkedHashMap<>();
        Map<String, List<GroupNav>> roleGroupNavByRole = new LinkedHashMap<>();
        Map<String, List<GroupNav>> groupParentNavByGroup = new LinkedHashMap<>();
        for (AclOwner owner : model.owners()) {
            if (model.findRole(owner.target()).isPresent()) {
                String plays = unique(names, "Agent_plays_" + owner.target());
                relationship(out, plays, "association", agentClass, AclCardinality.bounded(1, 1),
                        owner.target(), AclCardinality.unlimited(0));
                String inGroup = unique(names, owner.target() + "_in_" + owner.sourceGroup());
                relationship(out, inGroup, "association", owner.sourceGroup(), AclCardinality.bounded(1, 1),
                        owner.target(), owner.multiplicity());
                agentPlaysByRole.computeIfAbsent(owner.target(), k -> new ArrayList<>()).add("target_" + plays);
                roleGroupNavByRole.computeIfAbsent(owner.target(), k -> new ArrayList<>())
                        .add(new GroupNav(owner.sourceGroup(), "source_" + inGroup));
            } else {
                String name = unique(names, "Owner_" + owner.sourceGroup() + "_" + owner.target());
                relationship(out, name, "composition", owner.sourceGroup(), AclCardinality.bounded(1, 1),
                        owner.target(), owner.multiplicity());
                groupParentNavByGroup.computeIfAbsent(owner.target(), k -> new ArrayList<>())
                        .add(new GroupNav(owner.sourceGroup(), "source_" + name));
            }
        }
        renderCompatibilityInvariants(out, model, agentClass, agentPlaysByRole, roleGroupNavByRole,
                groupParentNavByGroup, names);
        return out.toString();
    }

    /** One 'inv NoConflict_R1_R2' per pair of concrete roles an Agent could simultaneously
     *  hold, mirroring AolModelFactory.checkCompatibility/checkPair exactly: undeclared pairs
     *  are always forbidden; declared-compatible pairs are still scope-gated (INTRA_GROUP only
     *  when both occurrences share the same Group instance, INTER_GROUP also when
     *  extendsSubgroups is set). Roles with no 'Agent_plays_*' association (never actually a
     *  Group member) are skipped -- there is nothing to navigate for them. */
    private static void renderCompatibilityInvariants(StringBuilder out, AclModel model, String agentClass,
                                                       Map<String, List<String>> agentPlaysByRole,
                                                       Map<String, List<GroupNav>> roleGroupNavByRole,
                                                       Map<String, List<GroupNav>> groupParentNavByGroup,
                                                       Set<String> names) {
        List<AclRole> concreteRoles = model.roles().stream().filter(r -> !r.isAbstract())
                .filter(r -> agentPlaysByRole.containsKey(r.name())).toList();
        StringBuilder invariants = new StringBuilder();
        for (int i = 0; i < concreteRoles.size(); i++) {
            for (int j = i + 1; j < concreteRoles.size(); j++) {
                AclRole r1 = concreteRoles.get(i);
                AclRole r2 = concreteRoles.get(j);
                Map<String, List<String>> r1Scopes = groupScopes("r1",
                        roleGroupNavByRole.getOrDefault(r1.name(), List.of()), groupParentNavByGroup);
                Map<String, List<String>> r2Scopes = groupScopes("r2",
                        roleGroupNavByRole.getOrDefault(r2.name(), List.of()), groupParentNavByGroup);
                String sharedScope = sameScope(r1Scopes, r2Scopes);
                if ("false".equals(sharedScope)) continue; // Different Group trees never conflict.

                List<String> allowedClauses = new ArrayList<>();
                for (AclCompatibility c : model.compatibilities()) {
                    if (c.type() != AclCompatibilityType.COMPATIBLE) continue;
                    boolean matches = (isRoleOrSubtype(model, r1.name(), c.fromRole()) && isRoleOrSubtype(model, r2.name(), c.toRole()))
                            || (c.bidirectional() && isRoleOrSubtype(model, r1.name(), c.toRole())
                            && isRoleOrSubtype(model, r2.name(), c.fromRole()));
                    if (!matches) continue;
                    String sameDirectGroup = sameGroupInstance("r1", "r2",
                            roleGroupNavByRole.getOrDefault(r1.name(), List.of()),
                            roleGroupNavByRole.getOrDefault(r2.name(), List.of()));
                    allowedClauses.add(sameDirectGroup);
                    if (c.scope() == AclScope.INTER_GROUP && c.extendsSubgroups()) {
                        allowedClauses.add(sharedScope);
                    }
                }
                // The declaration permits every pair that shares the only possible scope;
                // emitting `not scope or scope` would be correct but useless noise.
                if (allowedClauses.contains(sharedScope)) continue;

                String r1Plays = unionExpr(agentPlaysByRole.get(r1.name()));
                String r2Plays = unionExpr(agentPlaysByRole.get(r2.name()));
                String invName = unique(names, "NoConflict_" + r1.name() + "_" + r2.name());
                invariants.append("context ").append(id(agentClass)).append(" inv ").append(invName).append(":\n");
                invariants.append("  ").append(r1Plays).append("->forAll(r1 : ").append(id(r1.name())).append(" |\n")
                        .append("    ").append(r2Plays).append("->forAll(r2 : ").append(id(r2.name())).append(" |\n")
                        .append("      not (").append(sharedScope).append(")");
                if (!allowedClauses.isEmpty()) {
                    invariants.append(" or (").append(String.join(" or ", allowedClauses)).append(")");
                }
                invariants.append("))\n\n");
            }
        }
        if (!invariants.isEmpty()) out.append("constraints\n\n").append(invariants);
    }

    private static String unionExpr(List<String> navs) {
        StringBuilder sb = new StringBuilder("self.").append(navs.get(0));
        for (int k = 1; k < navs.size(); k++) sb.append("->union(self.").append(navs.get(k)).append(')');
        return sb.toString();
    }

    private static String sameGroupInstance(String r1Var, String r2Var, List<GroupNav> navs1, List<GroupNav> navs2) {
        List<String> clauses = new ArrayList<>();
        for (GroupNav n1 : navs1) {
            for (GroupNav n2 : navs2) {
                if (n1.groupType().equals(n2.groupType())) {
                    clauses.add(r1Var + "." + n1.navRoleName() + " = " + r2Var + "." + n2.navRoleName());
                }
            }
        }
        return clauses.isEmpty() ? "false" : String.join(" or ", clauses);
    }

    private static Map<String, List<String>> groupScopes(String roleVar, List<GroupNav> direct,
            Map<String, List<GroupNav>> parents) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        record Pending(String type, String expression, Set<String> path) {}
        java.util.ArrayDeque<Pending> queue = new java.util.ArrayDeque<>();
        for (GroupNav nav : direct) {
            queue.add(new Pending(nav.groupType(), roleVar + "." + nav.navRoleName(), Set.of(nav.groupType())));
        }
        while (!queue.isEmpty()) {
            Pending current = queue.removeFirst();
            result.computeIfAbsent(current.type(), k -> new ArrayList<>()).add(current.expression());
            for (GroupNav parent : parents.getOrDefault(current.type(), List.of())) {
                if (current.path().contains(parent.groupType())) continue;
                Set<String> path = new LinkedHashSet<>(current.path());
                path.add(parent.groupType());
                queue.add(new Pending(parent.groupType(), current.expression() + "." + parent.navRoleName(), path));
            }
        }
        return result;
    }

    private static String sameScope(Map<String, List<String>> left, Map<String, List<String>> right) {
        List<String> clauses = new ArrayList<>();
        for (var entry : left.entrySet()) {
            for (String l : entry.getValue()) {
                for (String r : right.getOrDefault(entry.getKey(), List.of())) clauses.add(l + " = " + r);
            }
        }
        return clauses.isEmpty() ? "false" : String.join(" or ", clauses);
    }

    /** Ported from AolModelFactory.isRoleOrSubtype -- role inheritance is single-parent in
     *  practice (only parentRoles().get(0) is ever consumed anywhere in this codebase). */
    private static boolean isRoleOrSubtype(AclModel model, String actual, String declared) {
        String current = actual;
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            if (current.equals(declared)) return true;
            var role = model.findRole(current);
            if (role.isEmpty() || role.get().parentRoles().isEmpty()) return false;
            current = role.get().parentRoles().get(0);
        }
        return false;
    }

    private static String generatedAgentName(AclModel model) {
        boolean occupied = model.entities().stream().anyMatch(x -> x.name().equals("Agent"))
                || model.roles().stream().anyMatch(x -> x.name().equals("Agent"))
                || model.groups().stream().anyMatch(x -> x.name().equals("Agent"));
        return occupied ? "ACLAgent" : "Agent";
    }

    private static void renderClass(StringBuilder out, String name, java.util.Optional<String> parent,
                                    boolean isAbstract, List<AclAttribute> attributes) {
        if (isAbstract) out.append("abstract ");
        out.append("class ").append(id(name));
        parent.ifPresent(value -> out.append(" < ").append(id(value)));
        out.append("\n");
        if (!attributes.isEmpty()) {
            out.append("attributes\n");
            attributes.forEach(value -> out.append("  ").append(id(value.name())).append(" : ")
                    .append(id(value.type().sourceName())).append("\n"));
        }
        out.append("end\n\n");
    }

    private static void relationship(StringBuilder out, String name, String kind,
                                     String sourceType, AclCardinality sourceMultiplicity,
                                     String targetType, AclCardinality targetMultiplicity) {
        String relation = id(name);
        out.append(kind).append(' ').append(relation).append(" between\n")
                .append("  ").append(id(sourceType)).append(multiplicity(sourceMultiplicity))
                .append(" role ").append(id("source_" + relation)).append("\n")
                .append("  ").append(id(targetType)).append(multiplicity(targetMultiplicity))
                .append(" role ").append(id("target_" + relation)).append("\n")
                .append("end\n\n");
    }

    private static String multiplicity(AclCardinality value) {
        if (value.max().isPresent() && value.min() == value.max().getAsInt()) return "[" + value.min() + "]";
        return "[" + value.min() + ".." + (value.max().isPresent() ? value.max().getAsInt() : "*") + "]";
    }

    private static String unique(Set<String> used, String proposed) {
        String base = id(proposed), value = base;
        for (int suffix = 2; !used.add(value); suffix++) value = base + "_" + suffix;
        return value;
    }

    private static String id(String value) {
        if (value == null || value.isBlank()) return "unnamed";
        String clean = value.replaceAll("[^A-Za-z0-9_]", "_");
        return Character.isDigit(clean.charAt(0)) ? "_" + clean : clean;
    }
}
