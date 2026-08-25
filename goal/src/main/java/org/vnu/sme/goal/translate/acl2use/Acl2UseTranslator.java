package org.vnu.sme.goal.translate.acl2use;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.*;

/**
 * Translates the ACL Class/Relationship metamodel to USE syntax.
 *
 * <p>Role inheritance ({@code extends}) is translated as a "play chain", not as USE
 * generalization -- see {@code goal/docs/semantics/dsl/acl.md} and
 * {@code goal/src/main/resources/examples/mtg/ACL_TO_USE_OCL_CASES.md}. No two Role
 * classes are ever related by {@code <}: a root Role (no {@code extends}) gets an
 * {@code Agent_plays_<Role>} association; a Role that {@code extends} another gets a
 * {@code <Parent>_plays_<Role>} association instead, with the parent-side multiplicity fixed
 * at {@code [1]} (playing the child requires an existing occurrence of the parent). The
 * child-side multiplicity is {@code [0..*]}. Owner and compatibility constraints are rendered
 * only after the complete play and Group-owner graphs exist. Generated association-end roles
 * use compact semantic names: {@code agent.play_participant},
 * {@code participant.agent}, {@code meetingUnit.participant}, and
 * {@code participant.meetingUnit}.
 */
public final class Acl2UseTranslator {
    private Acl2UseTranslator() {}

    /** Where a Role instance's owning Group instance can be reached from -- one entry per
     *  Group type that owns this Role (a Role can be a member of more than one Group type,
     *  e.g. a role that is also a member of one of its own subgroups). */
    private record GroupNav(String groupType, String navRoleName) {}

    /** One hop of a Role's play chain: {@code ownerType} is the Role's own parent Role, or the
     *  Agent class if the Role is a chain root; {@code navRoleName} is the USE navigation role
     *  name to go from an occurrence of {@code ownerType} to the {@code [0..1]}/{@code [0..*]}
     *  collection of {@code roleType} occurrences it plays. */
    private record ChainHop(String ownerType, String childNavRoleName, String parentNavRoleName,
                            boolean multiValued, String roleType) {}

    public static String translate(AclModel model) {
        StringBuilder out = new StringBuilder()
                .append("-- Generated from ACL. Regenerate instead of editing this file.\n")
                .append("-- Role 'extends' is a play chain (Owner[1]--Role[mult] association), not USE\n")
                .append("-- generalization: no two Role classes are ever related by '<'. Every generated\n")
                .append("-- class has a technical id : Integer used to preserve object identity in filmstrips.\n")
                .append("-- Every play association is parent[1]--child[0..*].\n")
                .append("-- Compatibility is 'inv NoConflict_<R1>_<R2>' on the nearest shared play-chain\n")
                .append("-- ancestor; every Owner-target Role also gets 'inv NoSelfConflict_<R>'.\n\n")
                .append("model ").append(id(model.name())).append("\n\n");

        model.enums().forEach(value -> out.append("enum ").append(id(value.name())).append(" {")
                .append(String.join(", ", value.literals().stream().map(Acl2UseTranslator::id).toList()))
                .append("}\n\n"));

        String agentClass = generatedAgentName(model);
        renderClass(out, agentClass, java.util.Optional.empty(), List.of());
        model.entities().forEach(value -> renderClass(out, value.name(), value.specializes(), value.attributes()));
        // Every Role is its own independent class, never generalized from another Role.
        model.roles().forEach(value -> renderClass(out, value.name(), java.util.Optional.empty(), value.attributes()));
        model.groups().forEach(value -> renderClass(out, value.name(), value.specializes(), value.attributes()));

        // Association/aggregation/composition declared explicitly in the .acl keep their
        // declared endpoint types verbatim -- no rewriting toward Agent for a Role endpoint.
        Set<String> names = new HashSet<>();
        for (AclRelation relation : model.relations()) {
            String relationName = unique(names, relation.name());
            String sourceRole = relation.source().roleName().orElse("source_" + relationName);
            String targetRole = relation.target().roleName().orElse("target_" + relationName);
            relationship(out, relationName, relation.kind().sourceName(),
                    relation.source().type(), relation.source().multiplicity(), sourceRole,
                    relation.target().type(), relation.target().multiplicity(), targetRole);
        }

        Set<String> ownerTargetRoles = new LinkedHashSet<>();
        for (AclOwner owner : model.owners()) {
            if (model.findRole(owner.target()).isPresent()) ownerTargetRoles.add(owner.target());
        }

        // R5/R6/R7: one "<Owner>_plays_<Role>" association per Role, Owner being the Role's own
        // parent Role (R6) or the Agent class if it has none (R5).
        Map<String, ChainHop> chainHopByRole = new LinkedHashMap<>();
        for (AclRole role : model.roles()) {
            String ownerType = role.parentRoles().stream().findFirst().orElse(agentClass);
            String assocName = unique(names, ownerType + "_plays_" + role.name());
            relationship(out, assocName, "association", ownerType, AclCardinality.bounded(1, 1),
                    ownerType.equals(agentClass) ? "agent" : lowerFirst(ownerType),
                    role.name(), AclCardinality.unlimited(0), playRole(role.name()));
            chainHopByRole.put(role.name(), new ChainHop(ownerType, playRole(role.name()),
                    ownerType.equals(agentClass) ? "agent" : lowerFirst(ownerType), true, role.name()));
        }

        // Owner Group->Role and Group->Group both become composition.
        Map<String, List<GroupNav>> roleGroupNavByRole = new LinkedHashMap<>();
        Map<String, List<GroupNav>> groupParentNavByGroup = new LinkedHashMap<>();
        for (AclOwner owner : model.owners()) {
            if (model.findRole(owner.target()).isPresent()) {
                String inGroup = unique(names, owner.target() + "_in_" + owner.sourceGroup());
                relationship(out, inGroup, "composition", owner.sourceGroup(), AclCardinality.bounded(1, 1),
                        lowerFirst(owner.sourceGroup()), owner.target(), owner.multiplicity(),
                        lowerFirst(owner.target()));
                roleGroupNavByRole.computeIfAbsent(owner.target(), k -> new ArrayList<>())
                        .add(new GroupNav(owner.sourceGroup(), lowerFirst(owner.sourceGroup())));
            } else {
                String name = unique(names, "Owner_" + owner.sourceGroup() + "_" + owner.target());
                relationship(out, name, "composition", owner.sourceGroup(), AclCardinality.bounded(1, 1),
                        lowerFirst(owner.sourceGroup()), owner.target(), owner.multiplicity(),
                        lowerFirst(owner.target()));
                groupParentNavByGroup.computeIfAbsent(owner.target(), k -> new ArrayList<>())
                        .add(new GroupNav(owner.sourceGroup(), lowerFirst(owner.sourceGroup())));
            }
        }

        boolean constraintsStarted = renderRoleOwnerScopeInvariants(out, model, chainHopByRole, roleGroupNavByRole,
                groupParentNavByGroup, names);
        renderCompatibilityInvariants(out, model, agentClass, chainHopByRole, roleGroupNavByRole,
                groupParentNavByGroup, ownerTargetRoles, names, constraintsStarted);
        return out.toString();
    }

    /** Root-to-leaf play-chain hops for {@code roleName}, i.e. {@code Agent -> ... -> roleName}. */
    private static List<ChainHop> chainFromAgent(String roleName, Map<String, ChainHop> chainHopByRole,
                                                 String agentClass) {
        List<ChainHop> hops = new ArrayList<>();
        String current = roleName;
        while (true) {
            ChainHop hop = chainHopByRole.get(current);
            if (hop == null) break; // defensive: should not happen for a well-formed model
            hops.add(hop);
            if (hop.ownerType().equals(agentClass)) break;
            current = hop.ownerType();
        }
        Collections.reverse(hops);
        return hops;
    }

    private record NavResult(String open, String close, String boundVar) {}

    /** Walks {@code hops} from {@code selfExpr}, opening one {@code ->forAll(v : T | } per
     *  multi-valued hop (single-valued hops are plain '.' navigation) and returning the OCL
     *  expression that refers to the final occurrence, plus the matching close-paren suffix. */
    private static NavResult buildOccurrenceNav(List<ChainHop> hops, String selfExpr, String varBase,
                                                Set<String> usedNames) {
        StringBuilder open = new StringBuilder();
        int closeCount = 0;
        String current = selfExpr;
        String boundVar = current;
        for (int i = 0; i < hops.size(); i++) {
            ChainHop hop = hops.get(i);
            boolean last = i == hops.size() - 1;
            if (hop.multiValued()) {
                String var = unique(usedNames, last ? varBase : varBase + "_a" + i);
                open.append(current).append('.').append(hop.childNavRoleName())
                        .append("->forAll(").append(var).append(" : ").append(id(hop.roleType())).append(" |\n");
                closeCount++;
                current = var;
                boundVar = var;
            } else {
                current = current + "." + hop.childNavRoleName();
                boundVar = current;
            }
        }
        return new NavResult(open.toString(), ")".repeat(closeCount), boundVar);
    }

    /** Ensures that the Role play chain and Group Owner chain reach the same Group occurrence,
     * not merely compatible Group types. Type-level monotonicity is checked by
     * {@link AclSemanticValidator}; these invariants preserve it for concrete states. */
    private static boolean renderRoleOwnerScopeInvariants(StringBuilder out, AclModel model,
            Map<String, ChainHop> chainHopByRole, Map<String, List<GroupNav>> roleGroupNavByRole,
            Map<String, List<GroupNav>> groupParentNavByGroup, Set<String> names) {
        StringBuilder invariants = new StringBuilder();
        for (AclRole child : model.roles()) {
            List<GroupNav> childOwnerNavs = roleGroupNavByRole.getOrDefault(child.name(), List.of());
            if (childOwnerNavs.isEmpty()) continue;
            GroupNav childOwner = childOwnerNavs.get(0);
            String ancestorRoleExpression = "self";
            String currentRole = child.name();
            Set<String> seen = new LinkedHashSet<>();
            while (seen.add(currentRole)) {
                ChainHop hop = chainHopByRole.get(currentRole);
                if (hop == null || hop.ownerType().equals("Agent") || hop.ownerType().equals("ACLAgent")) break;
                ancestorRoleExpression += "." + hop.parentNavRoleName();
                currentRole = hop.ownerType();
                List<GroupNav> ancestorOwnerNavs = roleGroupNavByRole.getOrDefault(currentRole, List.of());
                if (ancestorOwnerNavs.isEmpty()) continue;
                GroupNav ancestorOwner = ancestorOwnerNavs.get(0);
                String childGroupExpression = ascendGroupExpression("self." + childOwner.navRoleName(),
                        childOwner.groupType(), ancestorOwner.groupType(), groupParentNavByGroup);
                if (childGroupExpression == null) continue; // validator reports the type-level error
                String parentGroupExpression = ancestorRoleExpression + "." + ancestorOwner.navRoleName();
                String invName = unique(names, "RoleOwnerScope_" + currentRole + "_" + child.name());
                invariants.append("context ").append(id(child.name())).append(" inv ")
                        .append(invName).append(":\n  ")
                        .append(parentGroupExpression).append(" = ").append(childGroupExpression)
                        .append("\n\n");
            }
        }
        if (invariants.isEmpty()) return false;
        out.append("constraints\n\n").append(invariants);
        return true;
    }

    private static String ascendGroupExpression(String expression, String startGroup,
            String ancestorGroup, Map<String, List<GroupNav>> groupParentNavByGroup) {
        String current = startGroup;
        String result = expression;
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            if (current.equals(ancestorGroup)) return result;
            List<GroupNav> parents = groupParentNavByGroup.getOrDefault(current, List.of());
            if (parents.isEmpty()) return null;
            GroupNav parent = parents.get(0);
            result += "." + parent.navRoleName();
            current = parent.groupType();
        }
        return null;
    }

    /** R12 ('inv NoConflict_<R1>_<R2>') + R13 ('inv NoSelfConflict_<R>'). Only Roles that are
     *  ever an Owner target participate -- a "profile" Role (never a Group member) has no
     *  Group-instance scope to compare, so it cannot conflict with anything. A pair where one
     *  Role is a play-chain ancestor of the other is skipped: the descendant's existence
     *  already requires the ancestor occurrence (R6), so they are never "two independent
     *  occurrences that might collide" -- forbidding them from sharing scope would reject the
     *  very relationship the chain exists to express. */
    private static void renderCompatibilityInvariants(StringBuilder out, AclModel model, String agentClass,
            Map<String, ChainHop> chainHopByRole, Map<String, List<GroupNav>> roleGroupNavByRole,
            Map<String, List<GroupNav>> groupParentNavByGroup, Set<String> ownerTargetRoles, Set<String> names,
            boolean constraintsStarted) {
        List<AclRole> concreteRoles = model.roles().stream()
                .filter(r -> ownerTargetRoles.contains(r.name())).toList();

        StringBuilder invariants = new StringBuilder();

        // R13
        for (AclRole role : concreteRoles) {
            ChainHop hop = chainHopByRole.get(role.name());
            List<GroupNav> groupNavs = roleGroupNavByRole.getOrDefault(role.name(), List.of());
            if (groupNavs.isEmpty()) continue; // defensive: an Owner-target role always has one
            String groupNav = groupNavs.get(0).navRoleName();
            String invName = unique(names, "NoSelfConflict_" + role.name());
            invariants.append("context ").append(id(hop.ownerType())).append(" inv ").append(invName).append(":\n")
                    .append("  self.").append(hop.childNavRoleName()).append("->forAll(r1, r2 : ")
                    .append(id(role.name())).append(" |\n")
                    .append("    r1 <> r2 implies r1.").append(groupNav)
                    .append(" <> r2.").append(groupNav).append(")\n\n");
        }

        // R12
        for (int i = 0; i < concreteRoles.size(); i++) {
            for (int j = i + 1; j < concreteRoles.size(); j++) {
                AclRole r1 = concreteRoles.get(i);
                AclRole r2 = concreteRoles.get(j);
                if (isRoleOrSubtype(model, r1.name(), r2.name()) || isRoleOrSubtype(model, r2.name(), r1.name())) {
                    continue; // ancestor/descendant in the play chain: never "conflicting"
                }

                List<ChainHop> hops1 = chainFromAgent(r1.name(), chainHopByRole, agentClass);
                List<ChainHop> hops2 = chainFromAgent(r2.name(), chainHopByRole, agentClass);
                int lca = 0;
                while (lca < hops1.size() && lca < hops2.size()
                        && hops1.get(lca).roleType().equals(hops2.get(lca).roleType())) {
                    lca++;
                }
                String lcaContext = lca == 0 ? agentClass : hops1.get(lca - 1).roleType();
                List<ChainHop> suffix1 = hops1.subList(lca, hops1.size());
                List<ChainHop> suffix2 = hops2.subList(lca, hops2.size());

                Set<String> usedVars = new LinkedHashSet<>();
                NavResult nav1 = buildOccurrenceNav(suffix1, "self", "r1", usedVars);
                NavResult nav2 = buildOccurrenceNav(suffix2, "self", "r2", usedVars);

                Map<String, List<String>> r1Scopes = groupScopes(nav1.boundVar(),
                        roleGroupNavByRole.getOrDefault(r1.name(), List.of()), groupParentNavByGroup);
                Map<String, List<String>> r2Scopes = groupScopes(nav2.boundVar(),
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
                    if (c.scope() == AclScope.INTRA_GROUP) {
                        allowedClauses.add(sameDirectGroupOfType(nav1.boundVar(), nav2.boundVar(),
                                roleGroupNavByRole.getOrDefault(r1.name(), List.of()),
                                roleGroupNavByRole.getOrDefault(r2.name(), List.of()), c.groupName()));
                    } else {
                        allowedClauses.add(sameScopeOfType(r1Scopes, r2Scopes, c.groupName()));
                    }
                }
                // The declaration permits every pair that shares the only possible scope;
                // emitting `not scope or scope` would be correct but useless noise.
                if (allowedClauses.contains(sharedScope)) continue;

                String invName = unique(names, "NoConflict_" + r1.name() + "_" + r2.name());
                invariants.append("context ").append(id(lcaContext)).append(" inv ").append(invName).append(":\n");
                invariants.append(nav1.open());
                invariants.append(nav2.open());
                invariants.append("      not (").append(sharedScope).append(")");
                if (!allowedClauses.isEmpty()) {
                    invariants.append(" or (").append(String.join(" or ", allowedClauses)).append(")");
                }
                invariants.append(nav2.close()).append(nav1.close()).append("\n\n");
            }
        }
        if (!invariants.isEmpty()) {
            if (!constraintsStarted) out.append("constraints\n\n");
            out.append(invariants);
        }
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

    private static String sameDirectGroupOfType(String r1Var, String r2Var, List<GroupNav> navs1,
                                                 List<GroupNav> navs2, String groupType) {
        return sameGroupInstance(r1Var, r2Var,
                navs1.stream().filter(n -> n.groupType().equals(groupType)).toList(),
                navs2.stream().filter(n -> n.groupType().equals(groupType)).toList());
    }

    private static String sameScopeOfType(Map<String, List<String>> left, Map<String, List<String>> right,
                                          String groupType) {
        List<String> clauses = new ArrayList<>();
        for (String l : left.getOrDefault(groupType, List.of())) {
            for (String r : right.getOrDefault(groupType, List.of())) clauses.add(l + " = " + r);
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
     *  practice (only parentRoles().get(0) is ever consumed anywhere in this codebase). Also
     *  used here (R12) to detect play-chain ancestor/descendant pairs, which are excluded from
     *  NoConflict generation regardless of any 'compatible' declaration. */
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
                                    List<AclAttribute> attributes) {
        out.append("class ").append(id(name));
        parent.ifPresent(value -> out.append(" < ").append(id(value)));
        List<AclAttribute> domainAttributes = attributes.stream()
                .filter(value -> !value.name().equals("id"))
                .toList();
        out.append("\n");
        if (parent.isEmpty() || !domainAttributes.isEmpty()) {
            out.append("attributes\n");
        }
        // Specialized Entity/Group classes inherit id from their USE parent;
        // every other generated class declares the technical identity itself.
        if (parent.isEmpty()) out.append("  id : Integer\n");
        // `id` is reserved by the USE/TOCL translation as the stable identity of
        // one logical object across Filmstrip snapshots.  Do not emit a second
        // source-level attribute with the same name.
        domainAttributes.forEach(value -> out.append("  ").append(id(value.name())).append(" : ")
                .append(id(value.type().sourceName())).append("\n"));
        out.append("end\n\n");
    }

    private static void relationship(StringBuilder out, String name, String kind,
                                     String sourceType, AclCardinality sourceMultiplicity, String sourceRole,
                                     String targetType, AclCardinality targetMultiplicity, String targetRole) {
        String relation = id(name);
        out.append(kind).append(' ').append(relation).append(" between\n")
                .append("  ").append(id(sourceType)).append(multiplicity(sourceMultiplicity))
                .append(" role ").append(id(sourceRole)).append("\n")
                .append("  ").append(id(targetType)).append(multiplicity(targetMultiplicity))
                .append(" role ").append(id(targetRole)).append("\n")
                .append("end\n\n");
    }

    /**
     * Splices an {@code operations} section into an already-rendered {@code class
     * ... end} block for {@code className}, right before its closing {@code end}.
     * USE has no syntax for attaching operations to a class outside its own
     * {@code class ... end} block (the {@code operations} section is part of the
     * same grammar production as {@code attributes}), so this is the only way to
     * give an already-emitted class -- e.g. one just produced by {@link #translate}
     * -- derived operations computed elsewhere (iStar goal/task guards, BPMN
     * activity/gateway guards, ...) after the fact. {@code className} must already
     * be in final USE-identifier form (see {@link #translate}'s {@code id(...)}).
     */
    public static String spliceOperations(String useText, String className, List<String> operationLines) {
        if (operationLines.isEmpty()) return useText;
        Pattern header = Pattern.compile(
                "(?m)^(?:abstract\\s+)?class\\s+" + Pattern.quote(className) + "(?:\\s*<[^\\n]*)?\\n");
        Matcher matcher = header.matcher(useText);
        if (!matcher.find()) return useText; // defensive: caller's className should always match an emitted class
        int classEnd = useText.indexOf("\nend\n", matcher.start());
        if (classEnd < 0) return useText;
        String classBlock = useText.substring(matcher.start(), classEnd);
        boolean alreadyHasOperations = Pattern.compile("(?m)^operations\\s*$")
                .matcher(classBlock).find();
        String opsBlock = (alreadyHasOperations ? "" : "operations\n")
                + String.join("\n", operationLines) + "\n";
        return useText.substring(0, classEnd + 1) + opsBlock + useText.substring(classEnd + 1);
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

    private static String lowerFirst(String value) {
        String clean = id(value);
        return Character.toLowerCase(clean.charAt(0)) + clean.substring(1);
    }

    private static String playRole(String value) {
        return "play_" + lowerFirst(value);
    }
}
