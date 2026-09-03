package org.vnu.sme.goal.dsl.acl.ocl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vnu.sme.goal.dsl.acl.mm.AclAttribute;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclOwner;
import org.vnu.sme.goal.dsl.acl.mm.AclRole;

/**
 * Rewrites ACL's transparent inherited Role-property syntax before USE compiles OCL.
 *
 * <p>{@code self.x} is left untouched when {@code x} is local. When {@code x} is declared by a
 * play-chain ancestor Role (see {@code Acl2UseTranslator}, {@code goal/docs/ACL_OCL_SEMANTICS.md}
 * §3.4-§3.7), the value is read by walking the lower-camel owner navigation
 * one hop per {@code extends} edge between the context Role and the declaring ancestor. Every
 * hop is single-valued in that direction (the parent side of a play-chain association is
 * always {@code [1]}), so this is always a plain navigation chain -- never a search over
 * candidates, unlike the pre-play-chain design.</p>
 */
public final class AclOclPropertyResolver {
    private static final Pattern SELF_PROPERTY = Pattern.compile("\\bself\\s*\\.\\s*([A-Za-z_][A-Za-z0-9_]*)");

    private AclOclPropertyResolver() {}

    public static String rewrite(AclModel acl, String contextRole, String source) {
        return rewrite(acl, contextRole, Map.of(), source);
    }

    /**
     * Rewrites transparent properties for every contextual role variable. A Role is an OCL
     * facade: local properties stay on the role occurrence, while properties declared by a
     * play-chain ancestor are projected through the chain to that ancestor's occurrence.
     */
    public static String rewrite(AclModel acl, String contextRole,
                                 Map<String, String> contextualVariables, String source) {
        if (acl == null || source == null || source.isBlank()) return source;
        // Older BPMN/iStar examples were written when every Role was linked
        // directly to Agent.  Role `extends` is now represented by a play
        // chain, so the direct owner of a child Role is its parent Role.  Do
        // this compatibility rewrite even when the OCL context is a Group
        // (the common BPMN case), where the role-specific rewriting below is
        // intentionally not applicable.
        String rewritten = rewriteLegacyGeneratedNavigation(acl, source);
        AclRole context = acl.findRole(contextRole).orElse(null);
        if (context == null) return rewritten;

        rewritten = rewriteAclNavigationAliases(acl, context, rewritten);
        Map<String, String> variables = new LinkedHashMap<>(contextualVariables);
        variables.put("self", contextRole);
        rewritten = rewriteContextualRoleProperties(acl, variables, rewritten);
        Matcher matcher = SELF_PROPERTY.matcher(rewritten);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String property = matcher.group(1);
            Declaration declaration = declaration(acl, context, property);
            if (declaration == null || declaration.role().name().equals(context.name())) continue;
            String replacement = chainAccess(acl, "self", context, declaration.role().name(), property);
            if (replacement != null) matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /** Normalizes the verbose association-end names emitted by older ACL translators. */
    private static String rewriteLegacyGeneratedNavigation(AclModel acl, String source) {
        String result = source;
        for (AclRole role : acl.roles()) {
            Optional<String> parent = role.parentRoles().stream().findFirst();
            String owner = parent.orElse("Agent");
            String ownerNavigation = parent.map(AclOclPropertyResolver::lowerFirst).orElse("agent");
            result = replaceWord(result, "source_Agent_plays_" + id(role.name()), ownerNavigation);
            result = replaceWord(result, "source_ACLAgent_plays_" + id(role.name()), ownerNavigation);
            result = replaceWord(result, "source_" + id(owner) + "_plays_" + id(role.name()), ownerNavigation);
            result = replaceWord(result, "target_Agent_plays_" + id(role.name()), playRole(role.name()));
            result = replaceWord(result, "target_ACLAgent_plays_" + id(role.name()), playRole(role.name()));
            result = replaceWord(result, "target_" + id(owner) + "_plays_" + id(role.name()),
                    playRole(role.name()));
        }
        for (AclOwner owner : acl.owners()) {
            if (acl.findRole(owner.target()).isPresent()) {
                String association = id(owner.target()) + "_in_" + id(owner.sourceGroup());
                result = replaceWord(result, "source_" + association, lowerFirst(owner.sourceGroup()));
                result = replaceWord(result, "target_" + association, lowerFirst(owner.target()));
            } else {
                String association = "Owner_" + id(owner.sourceGroup()) + "_" + id(owner.target());
                result = replaceWord(result, "source_" + association, lowerFirst(owner.sourceGroup()));
                result = replaceWord(result, "target_" + association, lowerFirst(owner.target()));
            }
        }
        return result;
    }

    private static String replaceWord(String source, String oldName, String newName) {
        return source.replaceAll("\\b" + Pattern.quote(oldName) + "\\b", Matcher.quoteReplacement(newName));
    }

    private static String rewriteContextualRoleProperties(
            AclModel acl, Map<String, String> variables, String source) {
        String result = source;

        // Association endpoint role names declared on a Role type: navigating through them (or
        // passing a variable to their ->includes(...)) from a variable typed at a DESCENDANT of
        // that Role must first walk the play chain up to the occurrence that actually
        // participates in the association -- a descendant is a separate occurrence, not the
        // same object (R6), so it cannot stand in directly.
        Map<String, String> navigationDeclaredOn = new LinkedHashMap<>();
        for (var relation : acl.relations()) {
            if (acl.findRole(relation.source().type()).isPresent()) {
                relation.source().roleName().ifPresent(nm -> navigationDeclaredOn.putIfAbsent(nm, relation.source().type()));
            }
            if (acl.findRole(relation.target().type()).isPresent()) {
                relation.target().roleName().ifPresent(nm -> navigationDeclaredOn.putIfAbsent(nm, relation.target().type()));
            }
        }

        for (var variable : variables.entrySet()) {
            String name = variable.getKey();
            AclRole role = acl.findRole(variable.getValue()).orElse(null);
            if (role == null) continue;

            // Attributes declared by a play-chain ancestor.
            Set<String> ancestorAttrNames = new LinkedHashSet<>();
            AclRole walker = role;
            while (walker.parentRoles().stream().findFirst().isPresent()) {
                AclRole parent = acl.findRole(walker.parentRoles().get(0)).orElse(null);
                if (parent == null) break;
                parent.attributes().forEach(a -> ancestorAttrNames.add(a.name()));
                walker = parent;
            }
            for (String attrName : ancestorAttrNames) {
                Declaration declaration = declaration(acl, role, attrName);
                if (declaration == null || declaration.role().name().equals(role.name())) continue;
                String replacement = chainAccess(acl, name, role, declaration.role().name(), attrName);
                if (replacement == null) continue;
                result = result.replaceAll(
                        "\\b" + Pattern.quote(name) + "\\s*\\.\\s*" + Pattern.quote(attrName) + "\\b",
                        Matcher.quoteReplacement(replacement));
            }

            // Association navigation declared on a play-chain ancestor.
            for (var nav : navigationDeclaredOn.entrySet()) {
                String navRoleName = nav.getKey();
                String declaringType = nav.getValue();
                if (declaringType.equals(role.name()) || !isProperAncestor(acl, role, declaringType)) continue;
                String toAncestor = chainAccess(acl, name, role, declaringType, null);
                if (toAncestor == null) continue;
                result = result.replaceAll(
                        "\\b" + Pattern.quote(name) + "\\s*\\.\\s*" + Pattern.quote(navRoleName) + "\\b",
                        Matcher.quoteReplacement(toAncestor + "." + id(navRoleName)));
                result = result.replaceAll(
                        "(\\." + Pattern.quote(id(navRoleName)) + "\\s*->\\s*includes\\(\\s*)"
                                + Pattern.quote(name) + "(\\s*\\))",
                        "$1" + Matcher.quoteReplacement(toAncestor) + "$2");
            }
        }
        return result;
    }

    /**
     * Keeps generated USE association-end names out of user OCL.
     *
     * <ul>
     *   <li>{@code self.group} is the nearest ACL Group occurrence owning {@code self};</li>
     *   <li>{@code self.group.RoleName} is that Group's occurrence(s) of {@code RoleName};</li>
     *   <li>{@code self.agent} walks the whole play chain up to the Agent playing {@code self};</li>
     *   <li>{@code participant.agent} uses the Role name of a conventional lower-camel
     *       iterator variable to reach the corresponding Agent the same way.</li>
     * </ul>
     */
    private static String rewriteAclNavigationAliases(AclModel acl, AclRole context, String source) {
        String group = ownerGroup(acl, context.name());
        String result = source;
        if (group != null) {
            String groupAccess = "self." + lowerFirst(group);
            for (AclOwner owner : acl.owners()) {
                if (!owner.sourceGroup().equals(group) || acl.findRole(owner.target()).isEmpty()) continue;
                String member = owner.target();
                result = result.replaceAll(
                        "\\bself\\s*\\.\\s*group\\s*\\.\\s*" + Pattern.quote(member) + "\\b",
                        Matcher.quoteReplacement(groupAccess + "." + lowerFirst(member)));
            }
            result = result.replaceAll("\\bself\\s*\\.\\s*group\\b", Matcher.quoteReplacement(groupAccess));
        }

        result = result.replaceAll("\\bself\\s*\\.\\s*agent\\b",
                Matcher.quoteReplacement(chainAccess(acl, "self", context, null, null)));
        for (AclRole role : acl.roles()) {
            String variable = lowerFirst(role.name());
            String toAgent = chainAccess(acl, variable, role, null, null);
            result = result.replaceAll(
                    "\\b" + Pattern.quote(variable) + "\\s*\\.\\s*agent\\b",
                    Matcher.quoteReplacement(toAgent));
        }
        return result;
    }

    private static Declaration declaration(AclModel acl, AclRole context, String property) {
        String current = context.name();
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            AclRole role = acl.findRole(current).orElse(null);
            if (role == null) return null;
            for (AclAttribute attribute : role.attributes()) {
                if (attribute.name().equals(property)) return new Declaration(role, attribute);
            }
            if (role.parentRoles().isEmpty()) return null;
            current = role.parentRoles().get(0);
        }
        return null;
    }

    /**
     * Builds the play-chain navigation from {@code selfExpr} (typed {@code context}) up to the
     * occurrence of {@code stopAtRoleName} (or, if {@code null}, all the way to the Agent
     * playing {@code context}), optionally appending {@code .property}. Every hop is
     * lower-camel owner role ({@code agent} or the parent Role name) -- always
     * single-valued in this direction, since the
     * owner side of a play-chain association is always {@code [1]} -- so this never needs a
     * search or an {@code ->any(...)}.
     */
    private static String chainAccess(AclModel acl, String selfExpr, AclRole context,
                                      String stopAtRoleName, String property) {
        StringBuilder nav = new StringBuilder(selfExpr);
        AclRole current = context;
        while (stopAtRoleName == null || !current.name().equals(stopAtRoleName)) {
            Optional<String> parent = current.parentRoles().stream().findFirst();
            nav.append('.').append(parent.map(AclOclPropertyResolver::lowerFirst).orElse("agent"));
            if (parent.isEmpty()) break;
            AclRole next = acl.findRole(parent.get()).orElse(null);
            if (next == null) return null;
            current = next;
        }
        if (property != null) nav.append('.').append(id(property));
        return nav.toString();
    }

    private static boolean isProperAncestor(AclModel acl, AclRole role, String ancestorTypeName) {
        AclRole current = role;
        while (true) {
            Optional<String> parent = current.parentRoles().stream().findFirst();
            if (parent.isEmpty()) return false;
            if (parent.get().equals(ancestorTypeName)) return true;
            current = acl.findRole(parent.get()).orElse(null);
            if (current == null) return false;
        }
    }

    private static String ownerGroup(AclModel acl, String role) {
        return acl.owners().stream()
                .filter(owner -> owner.target().equals(role))
                .map(AclOwner::sourceGroup)
                .findFirst().orElse(null);
    }

    private static String id(String value) {
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

    private record Declaration(AclRole role, AclAttribute attribute) {}
}
