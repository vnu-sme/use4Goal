package org.vnu.sme.goal.trace.istartrace.nativeacl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.ocl.AclOclState;
import org.vnu.sme.goal.dsl.aol.mm.AolEntityInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolGroupInstance;
import org.vnu.sme.goal.dsl.aol.mm.AolModel;
import org.vnu.sme.goal.dsl.aol.mm.AolPlay;

/** @deprecated Legacy AOL v1 Agent graph; ACL state evaluation uses {@code AclSystemState}. */
@Deprecated(forRemoval = false)
public final class AclSnapshot implements AclOclState {
    public enum Kind { AGENT, GROUP, ROLE, ENTITY }
    public record ObjectValue(String id, String type, Kind kind, Map<String, String> attributes,
                              String agentId, String groupId) {
        public ObjectValue { attributes = Map.copyOf(attributes); }
    }

    private final AclModel acl;
    private final Map<String, ObjectValue> objects = new LinkedHashMap<>();
    private final Map<String, List<ObjectValue>> byType = new LinkedHashMap<>();
    private final Map<String, List<String>> links = new LinkedHashMap<>();
    private int explicitLinkCount;

    private AclSnapshot(AclModel acl) { this.acl = acl; }

    public static AclSnapshot of(AclModel acl, AolModel model) {
        AclSnapshot out = new AclSnapshot(acl);
        for (String agent : model.agents()) {
            out.add(new ObjectValue(agent, model.agentProfileRoles().getOrDefault(agent, "Agent"), Kind.AGENT,
                    model.agentAttributeValues().getOrDefault(agent, Map.of()), null, null));
        }
        for (AolGroupInstance group : model.groupInstances()) out.addGroup(group, null);
        for (AolEntityInstance entity : model.topLevelEntities()) out.addEntity(entity, null);
        model.links().forEach(link -> link.targetInstanceIds().forEach(target -> {
            out.explicitLinkCount++;
            out.addLink(link.relationName(), link.sourceInstanceId(), target);
        }));
        return out;
    }

    private void addLink(String relationName, String source, String target) {
        // Keep the relation name as a convenient forward alias, and expose the
        // UML/ACL opposite-end role names in both navigation directions.
        putLink(relationName, source, target);
        acl.relations().stream().filter(r -> r.name().equals(relationName)).findFirst()
                .ifPresent(relation -> {
                    relation.target().roleName().ifPresent(role -> putLink(role, source, target));
                    relation.source().roleName().ifPresent(role -> putLink(role, target, source));
                });
    }

    private void putLink(String property, String source, String target) {
        links.computeIfAbsent(property + "\u0000" + source, ignored -> new ArrayList<>()).add(target);
    }

    private void addGroup(AolGroupInstance group, String parentGroup) {
        add(new ObjectValue(group.instanceId(), group.typeName(), Kind.GROUP,
                group.attributeValues(), null, parentGroup));
        for (AolPlay play : group.plays()) {
            add(new ObjectValue(play.instanceId(), play.roleType(), Kind.ROLE,
                    play.attributeValues(), play.agentId(), group.instanceId()));
        }
        for (AolEntityInstance entity : group.entities()) addEntity(entity, group.instanceId());
        for (AolGroupInstance subgroup : group.subgroups()) addGroup(subgroup, group.instanceId());
    }

    private void addEntity(AolEntityInstance entity, String group) {
        add(new ObjectValue(entity.instanceId(), entity.entityType(), Kind.ENTITY,
                entity.attributeValues(), null, group));
    }

    private void add(ObjectValue value) {
        objects.put(value.id(), value);
        byType.computeIfAbsent(value.type(), ignored -> new ArrayList<>()).add(value);
    }

    public AclModel acl() { return acl; }
    public ObjectValue object(String id) { return objects.get(id); }
    public int objectCount() { return objects.size(); }
    public int linkCount() {
        long contained = objects.values().stream().filter(x -> x.groupId() != null).count();
        return Math.toIntExact(contained + explicitLinkCount);
    }
    public List<ObjectValue> objectsOfType(String type) {
        List<ObjectValue> result = new ArrayList<>();
        for (ObjectValue value : objects.values()) {
            if (isType(value.type(), type)) result.add(value);
        }
        return List.copyOf(result);
    }
    public List<ObjectValue> roleOccurrences() {
        return objects.values().stream().filter(x -> x.kind() == Kind.ROLE).toList();
    }

    @Override
    public Object property(Object base, String name) {
        if (base == null) return null;
        if (base instanceof List<?> list) {
            List<Object> values = new ArrayList<>();
            for (Object item : list) {
                Object value = property(item, name);
                if (value instanceof List<?> nested) values.addAll(nested); else if (value != null) values.add(value);
            }
            return List.copyOf(values);
        }
        if (!(base instanceof ObjectValue object)) return null;
        if (name.equals("group")) return object(object.groupId());
        if (name.equals("agent")) return object(object.agentId());
        if (object.attributes().containsKey(name)) return literal(object.attributes().get(name));
        String defaultValue = defaultAttributeValue(object.type(), name);
        if (defaultValue != null) return literal(defaultValue);
        if (object.kind() == Kind.ROLE && object.agentId() != null) {
            ObjectValue agent = object(object.agentId());
            if (agent != null && agent.attributes().containsKey(name)) return literal(agent.attributes().get(name));
        }
        if (object.kind() == Kind.GROUP) {
            return objects.values().stream().filter(x -> x.kind() == Kind.ROLE)
                    .filter(x -> object.id().equals(x.groupId()) && isType(x.type(), name)).toList();
        }
        List<String> targets = links.get(name + "\u0000" + (object.kind() == Kind.ROLE
                && object.agentId() != null ? object.agentId() : object.id()));
        if (targets != null) return targets.stream().map(this::object).toList();
        return null;
    }

    @Override
    public String identity(Object value) {
        if (!(value instanceof ObjectValue object)) return String.valueOf(value);
        return object.agentId() == null ? object.id() : object.agentId();
    }

    private String defaultAttributeValue(String type, String attributeName) {
        String current = type;
        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        while (current != null && seen.add(current)) {
            var role = acl.findRole(current).orElse(null);
            if (role != null) {
                for (var attribute : role.attributes()) if (attribute.name().equals(attributeName))
                    return attribute.defaultValue().orElse(null);
                current = role.parentRoles().stream().findFirst().orElse(null);
                continue;
            }
            var entity = acl.findEntity(current).orElse(null);
            if (entity != null) {
                for (var attribute : entity.attributes()) if (attribute.name().equals(attributeName))
                    return attribute.defaultValue().orElse(null);
                current = entity.specializes().orElse(null);
                continue;
            }
            var group = acl.findGroup(current).orElse(null);
            if (group != null) {
                for (var attribute : group.attributes()) if (attribute.name().equals(attributeName))
                    return attribute.defaultValue().orElse(null);
                current = group.specializes().orElse(null);
                continue;
            }
            return null;
        }
        return null;
    }

    private boolean isType(String actual, String expected) {
        if (actual.equals(expected)) return true;
        String current = actual;
        java.util.Set<String> seen = new java.util.LinkedHashSet<>();
        while (seen.add(current)) {
            var role = acl.findRole(current).orElse(null);
            if (role != null) current = role.parentRoles().stream().findFirst().orElse(null);
            else {
                var entity = acl.findEntity(current).orElse(null);
                if (entity != null) current = entity.specializes().orElse(null);
                else {
                    var group = acl.findGroup(current).orElse(null);
                    current = group == null ? null : group.specializes().orElse(null);
                }
            }
            if (current == null) return false;
            if (current.equals(expected)) return true;
        }
        return false;
    }

    static Object literal(String raw) {
        if (raw == null) return null;
        String value = raw.strip();
        if (value.equalsIgnoreCase("true")) return Boolean.TRUE;
        if (value.equalsIgnoreCase("false")) return Boolean.FALSE;
        if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        int enumSeparator = value.lastIndexOf("::");
        return enumSeparator >= 0 ? value.substring(enumSeparator + 2) : value;
    }
}
