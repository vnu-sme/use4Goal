package org.vnu.sme.goal.dsl.aol.state;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclRelation;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclState;

/**
 * Canonical finite ACL system state
 * (sigma_Class, sigma_Att, sigma_Assoc, sigma_Play).
 *
 * <p>There is intentionally no Agent object, owner field, or Role subtype
 * inclusion in this representation.</p>
 */
public final class AclSystemState implements AclOclState {
    public enum Kind { ENTITY, ROLE, GROUP }

    public record ObjectValue(String id, String type, Kind kind, Map<String, Object> attributes) {
        public ObjectValue {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(kind, "kind");
            attributes = Map.copyOf(attributes);
        }
    }

    public record AssociationLink(String relationName, String sourceId, String targetId) {}
    public record PlayLink(String parentRoleId, String childRoleId) {}

    private final AclModel model;
    private final Map<String, ObjectValue> objects;
    private final List<AssociationLink> associationLinks;
    private final List<PlayLink> playLinks;
    private final Map<String, AclRelation> relations;

    AclSystemState(AclModel model, Map<String, ObjectValue> objects,
                   List<AssociationLink> associationLinks, List<PlayLink> playLinks) {
        this.model = Objects.requireNonNull(model, "model");
        this.objects = Map.copyOf(objects);
        this.associationLinks = List.copyOf(associationLinks);
        this.playLinks = List.copyOf(playLinks);
        Map<String, AclRelation> relationIndex = new LinkedHashMap<>();
        model.relations().forEach(relation -> relationIndex.put(relation.name(), relation));
        this.relations = Map.copyOf(relationIndex);
    }

    public AclModel model() { return model; }
    public ObjectValue object(String id) { return objects.get(id); }
    public int objectCount() { return objects.size(); }
    public int associationLinkCount() { return associationLinks.size(); }
    public int playLinkCount() { return playLinks.size(); }
    public int linkCount() { return associationLinkCount() + playLinkCount(); }
    public List<AssociationLink> associationLinks() { return associationLinks; }
    public List<PlayLink> playLinks() { return playLinks; }

    public List<ObjectValue> objectsOfType(String type) {
        List<ObjectValue> result = new ArrayList<>();
        for (ObjectValue object : objects.values()) {
            if (conforms(object, type)) result.add(object);
        }
        return List.copyOf(result);
    }

    @Override
    public Object property(Object base, String name) {
        if (base == null) return null;
        if (base instanceof List<?> values) {
            List<Object> result = new ArrayList<>();
            for (Object value : values) {
                Object property = property(value, name);
                if (property instanceof List<?> nested) result.addAll(nested);
                else if (property != null) result.add(property);
            }
            return List.copyOf(result);
        }
        if (!(base instanceof ObjectValue object)) return null;
        if (object.attributes().containsKey(name)) return object.attributes().get(name);
        if (name.equals("playOf")) return parentsOf(object);

        Object navigation = associationNavigation(object, name);
        if (navigation != null) return navigation;

        // Translation of an inherited Role property follows actual sigma_Play
        // links. It never reads a parent declaration from the child object.
        if (object.kind() == Kind.ROLE) {
            Object parents = parentsOf(object);
            if (parents instanceof ObjectValue parent) return property(parent, name);
            if (parents instanceof List<?> list && !list.isEmpty()) return property(list, name);
        }
        return null;
    }

    private Object parentsOf(ObjectValue child) {
        if (child.kind() != Kind.ROLE) return null;
        List<ObjectValue> parents = playLinks.stream()
                .filter(link -> link.childRoleId().equals(child.id()))
                .map(link -> objects.get(link.parentRoleId()))
                .filter(Objects::nonNull).toList();
        return scalarOrCollection(parents, parents.size() <= 1);
    }

    private Object associationNavigation(ObjectValue object, String property) {
        List<ObjectValue> result = new ArrayList<>();
        boolean scalar = true;
        boolean foundProperty = false;
        for (AssociationLink link : associationLinks) {
            AclRelation relation = relations.get(link.relationName());
            if (relation == null) continue;
            String useTargetRole = "target_" + relation.target().type()
                    + "_in_" + relation.source().type();
            String useSourceRole = "source_" + relation.target().type()
                    + "_in_" + relation.source().type();
            if (link.sourceId().equals(object.id())
                    && navigatesBy(property, relation.name(),
                            relation.target().roleName().orElse(null), useTargetRole)) {
                foundProperty = true;
                ObjectValue target = objects.get(link.targetId());
                if (target != null) result.add(target);
                scalar &= relation.target().multiplicity().max().isPresent()
                        && relation.target().multiplicity().max().getAsInt() <= 1;
            }
            if (link.targetId().equals(object.id())
                    && navigatesBy(property, relation.name(),
                            relation.source().roleName().orElse(null), useSourceRole)) {
                foundProperty = true;
                ObjectValue source = objects.get(link.sourceId());
                if (source != null) result.add(source);
                scalar &= relation.source().multiplicity().max().isPresent()
                        && relation.source().multiplicity().max().getAsInt() <= 1;
            }
        }
        return foundProperty ? scalarOrCollection(result, scalar) : null;
    }

    private static boolean navigatesBy(String requested, String relation, String roleName,
                                       String compatibilityRole) {
        return requested.equals(relation)
                || requested.equals(roleName)
                || requested.equals(compatibilityRole);
    }

    private static Object scalarOrCollection(List<ObjectValue> values, boolean scalar) {
        if (!scalar) return List.copyOf(values);
        return values.isEmpty() ? null : values.get(0);
    }

    private boolean conforms(ObjectValue object, String expected) {
        if (object.type().equals(expected)) return true;
        // Role inheritance has disjoint oid domains and therefore never uses
        // subtype inclusion. Entity and Group use their generalization chain.
        if (object.kind() == Kind.ROLE) return false;
        String current = object.type();
        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        while (seen.add(current)) {
            if (object.kind() == Kind.ENTITY) {
                var entity = model.findEntity(current).orElse(null);
                current = entity == null ? null : entity.specializes().orElse(null);
            } else {
                var group = model.findGroup(current).orElse(null);
                current = group == null ? null : group.specializes().orElse(null);
            }
            if (current == null) return false;
            if (current.equals(expected)) return true;
        }
        return false;
    }

    @Override
    public String identity(Object value) {
        return value instanceof ObjectValue object ? object.id() : String.valueOf(value);
    }
}
