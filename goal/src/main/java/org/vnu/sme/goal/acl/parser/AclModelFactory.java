package org.vnu.sme.goal.acl.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vnu.sme.goal.acl.ast.AclAttributeCS;
import org.vnu.sme.goal.acl.ast.AclModelCS;
import org.vnu.sme.goal.acl.mm.AclActor;
import org.vnu.sme.goal.acl.mm.AclAttribute;
import org.vnu.sme.goal.acl.mm.AclEndpoint;
import org.vnu.sme.goal.acl.mm.AclEntity;
import org.vnu.sme.goal.acl.mm.AclEnum;
import org.vnu.sme.goal.acl.mm.AclGroup;
import org.vnu.sme.goal.acl.mm.AclGroupMember;
import org.vnu.sme.goal.acl.mm.AclInvariant;
import org.vnu.sme.goal.acl.mm.AclLink;
import org.vnu.sme.goal.acl.mm.AclModel;
import org.vnu.sme.goal.acl.mm.AclRelation;

public final class AclModelFactory {
    public record Result(AclModel model, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    private static final Set<String> PRIMITIVES = Set.of("String", "Integer", "Real", "Boolean");

    private AclModelFactory() {}

    public static Result create(AclModelCS ast) {
        AclModel model = new AclModel(ast.version(), ast.name(),
                ast.enums().stream().map(e -> new AclEnum(e.name(), e.literals())).toList(),
                ast.entities().stream().map(e -> new AclEntity(e.name(), attributesToModel(e.attributes()))).toList(),
                ast.actors().stream().map(a -> new AclActor(a.kind(), a.isAbstract(), a.name(), a.specializes(),
                        attributesToModel(a.attributes()))).toList(),
                ast.relations().stream().map(r -> new AclRelation(r.kind(), r.name(),
                        r.endpoints().stream().map(e -> new AclEndpoint(e.type(), e.multiplicity(), e.roleName())).toList())).toList(),
                ast.groups().stream().map(g -> new AclGroup(g.name(), g.specializes(),
                        attributesToModel(g.attributes()),
                        g.members().stream().map(m -> new AclGroupMember(m.type(), m.multiplicity())).toList())).toList(),
                ast.links().stream().map(l -> new AclLink(l.kind(), l.sourceRole(), l.targetRole(), l.scopeKind(), l.scopeGroup())).toList(),
                ast.invariants().stream().map(i -> new AclInvariant(i.name(), i.contextType(), i.oclBody())).toList());
        List<String> errors = new ArrayList<>();
        validate(model, errors);
        return new Result(model, List.copyOf(errors));
    }

    private static List<AclAttribute> attributesToModel(List<AclAttributeCS> attributes) {
        return attributes.stream().map(a -> new AclAttribute(a.name(), a.type())).toList();
    }

    private static void validate(AclModel model, List<String> errors) {
        Map<String, String> types = new LinkedHashMap<>();
        for (AclEnum e : model.enums()) checkUnique("enum", e.name(), types, errors);
        for (AclEntity e : model.entities()) checkUnique("entity", e.name(), types, errors);
        for (AclActor a : model.actors()) checkUnique(a.isAbstract() ? "abstract " + a.kind() : a.kind(), a.name(), types, errors);
        for (AclGroup g : model.groups()) checkUnique("group", g.name(), types, errors);

        Set<String> attributeTypes = new LinkedHashSet<>(types.keySet());
        attributeTypes.addAll(PRIMITIVES);
        for (AclEntity e : model.entities()) validateAttributes(e.name(), e.attributes(), attributeTypes, errors);
        for (AclActor a : model.actors()) validateAttributes(a.name(), a.attributes(), attributeTypes, errors);
        for (AclGroup g : model.groups()) validateAttributes(g.name(), g.attributes(), attributeTypes, errors);

        Map<String, AclActor> actors = model.actors().stream()
                .collect(LinkedHashMap::new, (m, a) -> m.put(a.name(), a), LinkedHashMap::putAll);
        Set<String> endpointTypes = new LinkedHashSet<>();
        model.entities().forEach(e -> endpointTypes.add(e.name()));
        model.actors().forEach(a -> endpointTypes.add(a.name()));
        model.groups().forEach(g -> endpointTypes.add(g.name()));

        for (AclActor actor : model.actors()) {
            if (actor.specializes() == null) continue;
            AclActor parent = actors.get(actor.specializes());
            if (parent == null) errors.add("semantic: unknown actor '" + actor.specializes()
                    + "' specialized by '" + actor.name() + "'");
            else if (!parent.kind().equals(actor.kind())) errors.add("semantic: " + actor.name()
                    + " cannot specialize " + parent.kind() + " '" + parent.name() + "'");
        }

        for (AclRelation r : model.relations()) {
            if (r.endpoints().size() < 2) errors.add("semantic: " + r.kind() + " '" + r.name()
                    + "' must have at least two endpoints");
            for (AclEndpoint endpoint : r.endpoints()) validateEndpoint(r.name(), endpoint, endpointTypes, errors);
        }

        Map<String, AclGroup> groups = model.groups().stream()
                .collect(LinkedHashMap::new, (m, g) -> m.put(g.name(), g), LinkedHashMap::putAll);
        for (AclGroup group : model.groups()) {
            if (group.specializes() != null && !groups.containsKey(group.specializes())) {
                errors.add("semantic: unknown group '" + group.specializes() + "' specialized by '" + group.name() + "'");
            }
            Set<String> members = new LinkedHashSet<>();
            for (AclGroupMember member : group.members()) {
                if (!endpointTypes.contains(member.type())) errors.add("semantic: unknown group member type '"
                        + member.type() + "' in group '" + group.name() + "'");
                AclActor actor = actors.get(member.type());
                if (actor != null && actor.isAbstract()) errors.add("semantic: abstract role '" + member.type()
                        + "' cannot be instantiated directly in group '" + group.name() + "'");
                if (!members.add(member.type())) errors.add("semantic: duplicate member '" + member.type()
                        + "' in group '" + group.name() + "'");
            }
        }

        for (AclLink link : model.links()) validateLink(link, actors, groups, errors);
        for (AclInvariant invariant : model.invariants()) {
            if (!types.containsKey(invariant.contextType())) errors.add("semantic: unknown invariant context type '"
                    + invariant.contextType() + "' in invariant '" + invariant.name() + "'");
        }
    }

    private static void checkUnique(String kind, String name, Map<String, String> names, List<String> errors) {
        String old = names.putIfAbsent(name, kind);
        if (old != null) errors.add("semantic: duplicate type name '" + name + "' (" + old + " and " + kind + ")");
    }

    private static void validateAttributes(String owner, List<AclAttribute> attributes, Set<String> types,
                                           List<String> errors) {
        Set<String> names = new LinkedHashSet<>();
        for (AclAttribute a : attributes) {
            if (!names.add(a.name())) errors.add("semantic: duplicate attribute '" + owner + "." + a.name() + "'");
            if (!types.contains(a.type())) errors.add("semantic: unknown type '" + a.type()
                    + "' for attribute '" + owner + "." + a.name() + "'");
        }
    }

    private static void validateEndpoint(String owner, AclEndpoint endpoint, Set<String> types, List<String> errors) {
        if (!types.contains(endpoint.type())) {
            errors.add("semantic: unknown endpoint type '" + endpoint.type() + "' in " + owner);
        }
    }

    private static void validateLink(AclLink link, Map<String, AclActor> actors, Map<String, AclGroup> groups,
                                     List<String> errors) {
        validateRoleEndpoint("source", link.sourceRole(), link.kind(), actors, errors);
        validateRoleEndpoint("target", link.targetRole(), link.kind(), actors, errors);
        if (link.scopeKind() != null && !groups.containsKey(link.scopeGroup())) {
            errors.add("semantic: unknown group '" + link.scopeGroup() + "' in " + link.scopeKind()
                    + " link '" + link.kind() + "'");
        }
    }

    private static void validateRoleEndpoint(String position, String name, String linkKind,
                                             Map<String, AclActor> actors, List<String> errors) {
        AclActor actor = actors.get(name);
        if (actor == null) {
            errors.add("semantic: unknown " + position + " role '" + name + "' in link '" + linkKind + "'");
        } else if (!actor.kind().equals("role")) {
            errors.add("semantic: " + position + " endpoint '" + name + "' in link '" + linkKind
                    + "' must be a role");
        }
    }
}
