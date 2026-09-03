package org.vnu.sme.goal.dsl.aol.state;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.vnu.sme.goal.dsl.acl.ast.AclSourceLocationCS;
import org.vnu.sme.goal.dsl.acl.mm.AclAttribute;
import org.vnu.sme.goal.dsl.acl.mm.AclCardinality;
import org.vnu.sme.goal.dsl.acl.mm.AclEnum;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclRelation;
import org.vnu.sme.goal.dsl.aol.ast.AolAttributeValueCS;
import org.vnu.sme.goal.dsl.aol.ast.AolModelCS;
import org.vnu.sme.goal.dsl.aol.parser.AOLBuildingVisitor;
import org.vnu.sme.goal.dsl.aol.parser.AOLLexer;
import org.vnu.sme.goal.dsl.aol.parser.AOLParser;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState.AssociationLink;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState.Kind;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState.ObjectValue;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState.PlayLink;

/** Compiles AOL v2 snapshots into the formal ACL system-state tuple. */
public final class AclSystemStateCompiler {
    public record Result(Path aclFile, AclSystemState state, List<String> diagnostics) {
        public Result { diagnostics = List.copyOf(diagnostics); }
        public boolean valid() { return state != null && diagnostics.isEmpty(); }
    }

    private AclSystemStateCompiler() {}

    public static Result compile(Path file, Path expectedAclFile, AclModel model) throws IOException {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(expectedAclFile, "expectedAclFile");
        Objects.requireNonNull(model, "model");
        Path source = file.toAbsolutePath().normalize();
        List<String> syntax = new ArrayList<>();
        AOLLexer lexer = new AOLLexer(CharStreams.fromPath(source));
        AOLParser parser = new AOLParser(new CommonTokenStream(lexer));
        BaseErrorListener listener = new BaseErrorListener() {
            @Override public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                    int line, int column, String message, RecognitionException exception) {
                syntax.add(format(source, new AclSourceLocationCS(line, column), "syntax", message));
            }
        };
        lexer.removeErrorListeners(); parser.removeErrorListeners();
        lexer.addErrorListener(listener); parser.addErrorListener(listener);
        AOLParser.ModelContext tree = parser.model();
        if (!syntax.isEmpty()) return new Result(null, null, syntax);

        AolModelCS ast = new AOLBuildingVisitor().visitModel(tree);
        Path aclFile = source.getParent().resolve(ast.aclFile()).normalize();
        if (!sameFile(expectedAclFile, aclFile)) {
            return new Result(aclFile, null, List.of("AOL references '" + aclFile
                    + "', expected ACL specification '" + expectedAclFile + "'"));
        }
        return new Builder(source, aclFile, ast, model).build();
    }

    private static final class Builder {
        private final Path source;
        private final Path aclFile;
        private final AolModelCS ast;
        private final AclModel model;
        private final List<String> diagnostics = new ArrayList<>();
        private final Map<String, ObjectValue> objects = new LinkedHashMap<>();
        private final List<AssociationLink> associations = new ArrayList<>();
        private final List<PlayLink> plays = new ArrayList<>();
        private int fatalErrors;

        Builder(Path source, Path aclFile, AolModelCS ast, AclModel model) {
            this.source = source; this.aclFile = aclFile; this.ast = ast; this.model = model;
        }

        Result build() {
            if (!ast.version().equals("v2.0")) {
                fatal(ast.location(), "AOL formal system states require version v2.0");
            }
            if (!ast.agents().isEmpty()) {
                fatal(ast.agents().get(0).location(),
                        "Agent is not part of Class = Entity union Role union Group; declare Role objects instead");
            }
            for (var group : ast.groupInstances()) {
                if (!group.subgroups().isEmpty() || !group.plays().isEmpty() || !group.entities().isEmpty()) {
                    fatal(group.location(), "AOL v2 Group blocks contain attributes only; represent containment"
                            + " with an explicit composition link");
                }
                var type = model.findGroup(group.typeName());
                if (type.isEmpty()) fatal(group.location(), "unknown Group type '" + group.typeName() + "'");
                else addObject(group.instanceId(), group.typeName(), Kind.GROUP,
                        values(attributesForGroup(group.typeName()), group.attributeValues(), group.location()),
                        group.location());
            }
            for (var entity : ast.entities()) {
                var type = model.findEntity(entity.entityType());
                if (type.isEmpty()) fatal(entity.location(), "unknown Entity type '" + entity.entityType() + "'");
                else addObject(entity.instanceId(), entity.entityType(), Kind.ENTITY,
                        values(attributesForEntity(entity.entityType()), entity.attributeValues(), entity.location()),
                        entity.location());
            }
            for (var role : ast.roles()) {
                var type = model.findRole(role.roleType());
                if (type.isEmpty()) fatal(role.location(), "unknown Role type '" + role.roleType() + "'");
                else addObject(role.instanceId(), role.roleType(), Kind.ROLE,
                        values(type.get().attributes(), role.attributeValues(), role.location()), role.location());
            }
            if (fatalErrors > 0) return new Result(aclFile, null, diagnostics);

            buildAssociationLinks();
            buildPlayLinks();
            if (fatalErrors > 0) return new Result(aclFile, null, diagnostics);
            validateAssociationMultiplicities();
            validateRequiredPlayLinks();
            AclSystemState state = new AclSystemState(model, objects, associations, plays);
            return new Result(aclFile, state, diagnostics);
        }

        private void addObject(String id, String type, Kind kind, Map<String, Object> values,
                               AclSourceLocationCS location) {
            if (objects.putIfAbsent(id, new ObjectValue(id, type, kind, values)) != null) {
                fatal(location, "duplicate object identifier '" + id + "'");
            }
        }

        private void buildAssociationLinks() {
            Map<String, AclRelation> relationIndex = new LinkedHashMap<>();
            model.relations().forEach(relation -> relationIndex.put(relation.name(), relation));
            Set<String> seen = new LinkedHashSet<>();
            for (var declaration : ast.links()) {
                AclRelation relation = relationIndex.get(declaration.relationName());
                if (relation == null) {
                    fatal(declaration.location(), "unknown Association '" + declaration.relationName() + "'");
                    continue;
                }
                ObjectValue sourceObject = objects.get(declaration.sourceInstanceId());
                if (sourceObject == null) {
                    fatal(declaration.location(), "unknown source object '" + declaration.sourceInstanceId() + "'");
                    continue;
                }
                if (!conforms(sourceObject, relation.source().type())) {
                    fatal(declaration.location(), "object '" + sourceObject.id() + "' of type '"
                            + sourceObject.type() + "' does not conform to source endpoint '"
                            + relation.source().type() + "'");
                }
                for (String targetId : declaration.targetInstanceIds()) {
                    ObjectValue targetObject = objects.get(targetId);
                    if (targetObject == null) {
                        fatal(declaration.location(), "unknown target object '" + targetId + "'");
                        continue;
                    }
                    if (!conforms(targetObject, relation.target().type())) {
                        fatal(declaration.location(), "object '" + targetObject.id() + "' of type '"
                                + targetObject.type() + "' does not conform to target endpoint '"
                                + relation.target().type() + "'");
                    }
                    String key = relation.name() + "\0" + sourceObject.id() + "\0" + targetObject.id();
                    if (!seen.add(key)) fatal(declaration.location(), "duplicate Association link '"
                            + relation.name() + "(" + sourceObject.id() + ", " + targetObject.id() + ")'");
                    else associations.add(new AssociationLink(relation.name(), sourceObject.id(), targetObject.id()));
                }
            }
        }

        private void buildPlayLinks() {
            Set<String> seen = new LinkedHashSet<>();
            for (var declaration : ast.playLinks()) {
                ObjectValue parent = objects.get(declaration.parentInstanceId());
                ObjectValue child = objects.get(declaration.childInstanceId());
                if (parent == null || child == null) {
                    fatal(declaration.location(), "sigma_Play endpoints must reference existing Role objects");
                    continue;
                }
                if (parent.kind() != Kind.ROLE || child.kind() != Kind.ROLE) {
                    fatal(declaration.location(), "sigma_Play endpoints must both be Role objects");
                    continue;
                }
                var childType = model.findRole(child.type()).orElseThrow();
                if (!childType.parentRoles().contains(parent.type())) {
                    fatal(declaration.location(), "Role '" + child.type() + "' is not a direct child of Role '"
                            + parent.type() + "'");
                    continue;
                }
                String key = parent.id() + "\0" + child.id();
                if (!seen.add(key)) fatal(declaration.location(), "duplicate sigma_Play link from '"
                        + parent.id() + "' to '" + child.id() + "'");
                else plays.add(new PlayLink(parent.id(), child.id()));
            }
        }

        private void validateRequiredPlayLinks() {
            for (ObjectValue child : objects.values()) {
                if (child.kind() != Kind.ROLE) continue;
                var role = model.findRole(child.type()).orElseThrow();
                for (String parentType : role.parentRoles()) {
                    long count = plays.stream().filter(link -> link.childRoleId().equals(child.id()))
                            .map(link -> objects.get(link.parentRoleId()))
                            .filter(parent -> parent != null && parent.type().equals(parentType)).count();
                    if (count != 1) constraint(ast.location(), "Role object '" + child.id() + "' of type '"
                            + child.type() + "' requires exactly one sigma_Play parent of type '"
                            + parentType + "', found " + count);
                }
            }
        }

        private void validateAssociationMultiplicities() {
            for (AclRelation relation : model.relations()) {
                List<ObjectValue> sources = objects.values().stream()
                        .filter(object -> conforms(object, relation.source().type())).toList();
                List<ObjectValue> targets = objects.values().stream()
                        .filter(object -> conforms(object, relation.target().type())).toList();
                for (ObjectValue sourceObject : sources) {
                    long count = associations.stream().filter(link -> link.relationName().equals(relation.name())
                            && link.sourceId().equals(sourceObject.id())).count();
                    checkMultiplicity(relation, sourceObject.id(), "target", relation.target().multiplicity(), count);
                }
                for (ObjectValue targetObject : targets) {
                    long count = associations.stream().filter(link -> link.relationName().equals(relation.name())
                            && link.targetId().equals(targetObject.id())).count();
                    checkMultiplicity(relation, targetObject.id(), "source", relation.source().multiplicity(), count);
                }
            }
        }

        private void checkMultiplicity(AclRelation relation, String objectId, String end,
                                       AclCardinality cardinality, long count) {
            boolean valid = count >= cardinality.min()
                    && (cardinality.max().isEmpty() || count <= cardinality.max().getAsInt());
            if (!valid) constraint(ast.location(), "Association '" + relation.name() + "' at object '"
                    + objectId + "' has " + count + " " + end + " link(s), expected "
                    + cardinalityText(cardinality));
        }

        private Map<String, Object> values(List<AclAttribute> declared,
                                           List<AolAttributeValueCS> supplied,
                                           AclSourceLocationCS objectLocation) {
            Map<String, AclAttribute> definitions = new LinkedHashMap<>();
            declared.forEach(attribute -> definitions.put(attribute.name(), attribute));
            Map<String, String> raw = new LinkedHashMap<>();
            for (AolAttributeValueCS value : supplied) {
                if (!definitions.containsKey(value.name())) {
                    fatal(value.location(), "unknown direct attribute '" + value.name() + "'");
                } else if (raw.putIfAbsent(value.name(), value.rawValue()) != null) {
                    fatal(value.location(), "duplicate value for attribute '" + value.name() + "'");
                }
            }
            Map<String, Object> result = new LinkedHashMap<>();
            for (AclAttribute attribute : declared) {
                String value = raw.get(attribute.name());
                if (value == null) value = attribute.defaultValue().orElse(null);
                if (value == null) {
                    if (!attribute.optional()) fatal(objectLocation, "required attribute '"
                            + attribute.name() + "' has no value");
                    continue;
                }
                Object typed = typedValue(attribute, value, objectLocation);
                if (typed != null) result.put(attribute.name(), typed);
            }
            return result;
        }

        private Object typedValue(AclAttribute attribute, String raw, AclSourceLocationCS location) {
            try {
                return switch (attribute.type().sourceName()) {
                    case "Boolean" -> {
                        if (!raw.equals("true") && !raw.equals("false")) throw new IllegalArgumentException();
                        yield Boolean.valueOf(raw);
                    }
                    case "Integer" -> Long.valueOf(raw);
                    case "Real" -> Double.valueOf(raw);
                    case "String" -> {
                        if (raw.length() < 2 || raw.charAt(0) != '"' || raw.charAt(raw.length() - 1) != '"')
                            throw new IllegalArgumentException();
                        yield raw.substring(1, raw.length() - 1);
                    }
                    default -> {
                        AclEnum enumeration = model.enums().stream()
                                .filter(value -> value.name().equals(attribute.type().sourceName()))
                                .findFirst().orElseThrow();
                        String literal = raw.replaceFirst("^.*::", "");
                        if (!enumeration.literals().contains(literal)) throw new IllegalArgumentException();
                        yield literal;
                    }
                };
            } catch (RuntimeException ex) {
                fatal(location, "value '" + raw + "' is invalid for attribute '" + attribute.name()
                        + " : " + attribute.type().sourceName() + "'");
                return null;
            }
        }

        private List<AclAttribute> attributesForEntity(String type) {
            List<AclAttribute> result = new ArrayList<>();
            String current = type;
            Set<String> seen = new LinkedHashSet<>();
            while (current != null && seen.add(current)) {
                var entity = model.findEntity(current).orElse(null);
                if (entity == null) break;
                result.addAll(entity.attributes());
                current = entity.specializes().orElse(null);
            }
            return result;
        }

        private List<AclAttribute> attributesForGroup(String type) {
            List<AclAttribute> result = new ArrayList<>();
            String current = type;
            Set<String> seen = new LinkedHashSet<>();
            while (current != null && seen.add(current)) {
                var group = model.findGroup(current).orElse(null);
                if (group == null) break;
                result.addAll(group.attributes());
                current = group.specializes().orElse(null);
            }
            return result;
        }

        private boolean conforms(ObjectValue object, String expected) {
            if (object.type().equals(expected)) return true;
            if (object.kind() == Kind.ROLE) return false;
            String current = object.type();
            Set<String> seen = new LinkedHashSet<>();
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

        private void fatal(AclSourceLocationCS location, String message) {
            fatalErrors++;
            diagnostics.add(format(source, location, "semantic", message));
        }

        private void constraint(AclSourceLocationCS location, String message) {
            diagnostics.add(format(source, location, "state", message));
        }
    }

    private static boolean sameFile(Path expected, Path actual) {
        try { return Files.isSameFile(expected, actual); }
        catch (IOException ex) { return expected.toAbsolutePath().normalize().equals(actual); }
    }

    private static String cardinalityText(AclCardinality value) {
        return "[" + value.min() + ".." + (value.max().isPresent()
                ? value.max().getAsInt() : "*") + "]";
    }

    private static String format(Path source, AclSourceLocationCS location, String phase, String message) {
        return source + ":" + location.line() + ":" + location.column() + ": " + phase + ": " + message;
    }
}
