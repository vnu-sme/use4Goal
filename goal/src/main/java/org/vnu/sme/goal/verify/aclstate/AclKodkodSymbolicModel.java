package org.vnu.sme.goal.verify.aclstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.vnu.sme.goal.dsl.acl.mm.AclAttribute;
import org.vnu.sme.goal.dsl.acl.mm.AclDataType;
import org.vnu.sme.goal.dsl.acl.mm.AclEndpoint;
import org.vnu.sme.goal.dsl.acl.mm.AclEntity;
import org.vnu.sme.goal.dsl.acl.mm.AclGroup;
import org.vnu.sme.goal.dsl.acl.mm.AclInvariant;
import org.vnu.sme.goal.dsl.acl.mm.AclModel;
import org.vnu.sme.goal.dsl.acl.mm.AclPrimitiveType;
import org.vnu.sme.goal.dsl.acl.mm.AclRelation;
import org.vnu.sme.goal.dsl.acl.mm.AclRole;
import org.vnu.sme.goal.dsl.aol.state.AclSystemState.Kind;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.AtPre;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Binary;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Call;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Literal;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Let;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Name;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Node;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Property;
import org.vnu.sme.goal.verify.aclstate.AclOclFormulaParser.Unary;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.instance.Bounds;
import kodkod.instance.Instance;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

/** Symbolic ACL system states and OCL formulas for one bounded Kodkod query. */
final class AclKodkodSymbolicModel {
    record ObjectAtom(String atom, String id, String concreteType, Kind kind, Relation singleton) {}
    record ScalarAtom(String atom, String type, Object value, Relation singleton) {}
    record AttributeSlot(String ownerType, AclAttribute attribute) {}
    private record ContextMember(String context, String type) {}

    final class Frame {
        private final int index;
        private final Map<String, Relation> exists = new LinkedHashMap<>();
        private final Map<AttributeSlot, Relation> attributes = new LinkedHashMap<>();
        private final Map<String, Relation> associations = new LinkedHashMap<>();
        private Relation play;
        private final Map<ContextMember, Relation> memberships = new LinkedHashMap<>();

        Frame(int index) { this.index = index; }
        int index() { return index; }
        Relation exists(String concreteType) { return exists.get(concreteType); }
        Relation attribute(AttributeSlot slot) { return attributes.get(slot); }
        Relation association(String name) { return associations.get(name); }
        Relation play() { return play; }
        Relation membership(String contextType, String memberType) {
            return memberships.get(new ContextMember(contextType, memberType));
        }
    }

    private sealed interface Value permits BoolValue, ScalarValue, ObjectsValue, TypeValue, ScalarCollectionValue {}
    private record BoolValue(Formula formula, Formula defined) implements Value {
        BoolValue(Formula formula) { this(formula, Formula.TRUE); }
    }
    private record ScalarEntry(ScalarAtom scalar, Formula member) {}
    /** A bag: two distinct objects may contribute the same scalar value. */
    private record ScalarCollectionValue(List<ScalarEntry> entries) implements Value {}
    private record ScalarValue(Map<ScalarAtom, Formula> choices) implements Value {
        ScalarValue { choices = Map.copyOf(choices); }
    }
    private record ObjectsValue(Map<ObjectAtom, Formula> members, boolean collection, Formula defined) implements Value {
        ObjectsValue(Map<ObjectAtom, Formula> members) { this(members, true, Formula.TRUE); }
        ObjectsValue { members = java.util.Collections.unmodifiableMap(new LinkedHashMap<>(members)); }
    }
    private record TypeValue(String name) implements Value {}
    private record Environment(Frame current, Frame previous, Map<String, Value> variables) {
        Environment with(String name, Value value) {
            Map<String, Value> copy = new LinkedHashMap<>(variables);
            copy.put(name, value);
            return new Environment(current, previous, Map.copyOf(copy));
        }
        Environment atPre() { return new Environment(previous == null ? current : previous, previous, variables); }
    }

    private final AclModel acl;
    private final AclBpmnBoundary boundary;
    private final List<ObjectAtom> objects = new ArrayList<>();
    private final List<ScalarAtom> scalars = new ArrayList<>();
    private final Map<String, List<ObjectAtom>> objectsByConcreteType = new LinkedHashMap<>();
    private final Map<String, ObjectAtom> objectByAtom = new HashMap<>();
    private final Map<String, ScalarAtom> scalarByAtom = new HashMap<>();
    private final Map<String, Relation> atomRelations = new LinkedHashMap<>();
    private final Map<String, List<AttributeSlot>> slotsByProperty = new LinkedHashMap<>();
    private final List<Frame> frames = new ArrayList<>();
    private final Universe universe;
    private final TupleFactory factory;
    private final Bounds bounds;
    /*
     * Keep structural clauses flat until the query is assembled. Building one
     * long left-associated BinaryFormula here overflows Kodkod's recursive
     * FOL2Bool translator for realistic multi-snapshot models.
     */
    private final List<Formula> structuralFormulas = new ArrayList<>();
    private record ExpressionKey(String source, int current, int previous, String self) {}
    private final Map<ExpressionKey, Formula> expressionCache = new HashMap<>();
    private int choiceCount;

    AclKodkodSymbolicModel(AclModel acl, AclBpmnBoundary boundary) {
        for (AclRelation relation : acl.relations())
            require(relation.endpoints().size() == 2, "symbolic backend currently requires binary associations: " + relation.name());
        this.acl = Objects.requireNonNull(acl, "acl");
        this.boundary = Objects.requireNonNull(boundary, "boundary");
        createObjectAtoms();
        createScalarAtoms();
        List<String> universeAtoms = new ArrayList<>();
        objects.forEach(value -> universeAtoms.add(value.atom()));
        scalars.forEach(value -> universeAtoms.add(value.atom()));
        universe = new Universe(universeAtoms);
        factory = universe.factory();
        bounds = new Bounds(universe);
        bindAtomRelations();
        indexAttributeSlots();
        for (int index = 0; index < boundary.snapshots(); index++) createFrame(index);
        addInitialDefaults();
        addInvariantFormulas();
    }

    Universe universe() { return universe; }
    Bounds bounds() { return bounds; }
    Formula structuralFormula() { return and(structuralFormulas); }
    Frame frame(int index) { return frames.get(index); }
    int frameCount() { return frames.size(); }

    List<ObjectAtom> processSelfCandidates(String groupType) {
        return objectsForType(groupType);
    }

    /** Potential ACL objects that instantiate an iStar actor with this classifier name. */
    List<ObjectAtom> actorCandidates(String actorType) {
        return objectsForType(actorType);
    }

    Formula exists(Frame frame, ObjectAtom object) {
        return object.singleton().in(frame.exists(object.concreteType()));
    }

    Formula expression(String source, Frame current, Frame previous, ObjectAtom self) {
        Map<String, Value> variables = new LinkedHashMap<>();
        if (self != null) variables.put("self", singleton(self));
        ExpressionKey key = new ExpressionKey(source, current.index(), previous.index(), self == null ? "" : self.id());
        return expressionCache.computeIfAbsent(key, ignored -> {
            Value value = compile(AclOclFormulaParser.parse(source),
                    new Environment(current, previous, Map.copyOf(variables)));
            return bool(value).and(defined(value));
        });
    }

    /** Frame condition for a BPMN step that declares no state-changing postcondition. */
    Formula sameState(Frame left, Frame right) {
        return frameCondition(left, right, Set.of());
    }

    /**
     * Preserves structural state and every attribute/association not named by a
     * postcondition. This gives partial OCL contracts their usual frame semantics.
     */
    Formula frameCondition(Frame left, Frame right, Set<String> changedProperties) {
        List<Formula> clauses = new ArrayList<>();
        Set<String> creatable = new LinkedHashSet<>();
        if (acl.isRevisedCore()) {
            for (ContextMember member : left.memberships.keySet())
                if (changedProperties.contains(member.type())) creatable.add(member.type());
            for (AclRelation relation : acl.relations()) {
                if (relation.target().roleName().stream().anyMatch(changedProperties::contains))
                    creatable.add(relation.target().type());
                if (relation.source().roleName().stream().anyMatch(changedProperties::contains))
                    creatable.add(relation.source().type());
            }
        }
        for (String type : left.exists.keySet()) {
            boolean creates = kind(type) == Kind.ENTITY && creatable.stream().anyMatch(t ->
                    typeConforms(type, t, Kind.ENTITY));
            clauses.add(creates ? left.exists(type).in(right.exists(type))
                    : left.exists(type).eq(right.exists(type)));
        }
        for (AttributeSlot slot : left.attributes.keySet()) {
            if (!changedProperties.contains(slot.attribute().name())
                    || acl.isRevisedCore() && !slot.attribute().mutable()) {
                for (ObjectAtom object : attributeDomain(slot))
                    clauses.add(exists(left, object).implies(object.singleton().join(left.attribute(slot))
                            .eq(object.singleton().join(right.attribute(slot)))));
            }
            if (acl.isRevisedCore() && slot.attribute().defaultValue().isPresent()) {
                String expected = normalizedDefault(slot.attribute().defaultValue().orElseThrow());
                ScalarAtom scalar = scalarsFor(slot.attribute().type()).stream()
                        .filter(v -> normalizedDefault(String.valueOf(v.value())).equals(expected)).findFirst().orElseThrow();
                for (ObjectAtom object : attributeDomain(slot)) clauses.add(
                        exists(right, object).and(exists(left, object).not()).implies(
                                object.singleton().join(right.attribute(slot)).eq(scalar.singleton())));
            }
        }
        for (AclRelation definition : acl.relations()) {
            boolean changed = changedProperties.contains(definition.name())
                    || definition.source().roleName().stream().anyMatch(changedProperties::contains)
                    || definition.target().roleName().stream().anyMatch(changedProperties::contains);
            if (!changed) {
                Expression oldPairs = typeExpression(left, definition.source().type())
                        .product(typeExpression(left, definition.target().type()));
                clauses.add(left.association(definition.name())
                        .eq(right.association(definition.name()).intersection(oldPairs)));
            }
        }
        for (var entry : left.memberships.entrySet()) {
            // Existing objects keep their owning process context and identity.
            Relation next = right.memberships.get(entry.getKey());
            clauses.add(entry.getValue().in(next));
            if (!changedProperties.contains(entry.getKey().type()))
                clauses.add(entry.getValue().eq(next));
        }
        clauses.add(left.play().eq(right.play()));
        return and(clauses);
    }

    List<String> decodePath(Solution solution, int usedFrames) {
        Instance instance = solution.instance();
        List<String> result = new ArrayList<>();
        for (int index = 0; index < usedFrames; index++) result.add(decodeFrame(instance, frame(index)));
        return List.copyOf(result);
    }

    private void createObjectAtoms() {
        acl.entities().forEach(value -> addObjects(value.name(), Kind.ENTITY));
        acl.roles().forEach(value -> addObjects(value.name(), Kind.ROLE));
        acl.groups().forEach(value -> addObjects(value.name(), Kind.GROUP));
    }

    private void addObjects(String type, Kind kind) {
        int upper = boundary.objectScopes().get(type).upper();
        List<ObjectAtom> typed = new ArrayList<>();
        for (int index = 1; index <= upper; index++) {
            String atom = "obj:" + type + ":" + index;
            ObjectAtom value = new ObjectAtom(atom, type + "_" + index, type, kind,
                    Relation.unary("atom_obj_" + safe(type) + "_" + index));
            objects.add(value);
            typed.add(value);
            objectByAtom.put(atom, value);
        }
        objectsByConcreteType.put(type, List.copyOf(typed));
    }

    private void createScalarAtoms() {
        addScalar("Boolean", Boolean.TRUE, "scalar_bool_true");
        addScalar("Boolean", Boolean.FALSE, "scalar_bool_false");
        acl.enums().forEach(enumeration -> enumeration.literals().forEach(literal ->
                addScalar(enumeration.name(), literal, "scalar_enum_" + safe(enumeration.name())
                        + "_" + safe(literal))));
        for (int value = boundary.integerMin(); value <= boundary.integerMax(); value++) {
            addScalar("Integer", Long.valueOf(value), "scalar_int_" + (value < 0 ? "m" + -value : value));
        }
        Set<String> stringValues = new LinkedHashSet<>(boundary.stringAtoms());
        Set<String> realValues = new LinkedHashSet<>(boundary.realAtoms());
        stringValues.add("");
        realValues.add("0.0");
        allAttributes().forEach(slot -> slot.attribute().defaultValue().ifPresent(raw -> {
            if (slot.attribute().type() == AclPrimitiveType.STRING) stringValues.add(unquote(raw));
            if (slot.attribute().type() == AclPrimitiveType.REAL) realValues.add(raw);
        }));
        // Uninterpreted data types have opaque atoms, not invented date/time arithmetic.
        for (var type : acl.namedDataTypes()) {
            int capacity = Math.max(1, objects.size());
            for (int n = 0; n < capacity; n++)
                addScalar(type.sourceName(), type.sourceName() + "_" + n, "scalar_opaque_" + safe(type.sourceName()) + "_" + n);
        }
        int index = 0;
        for (String value : stringValues) addScalar("String", value, "scalar_string_" + index++);
        index = 0;
        for (String value : realValues) addScalar("Real", Double.valueOf(value), "scalar_real_" + index++);
    }

    private void addScalar(String type, Object value, String relationName) {
        String atom = "value:" + type + ":" + scalars.size();
        ScalarAtom scalar = new ScalarAtom(atom, type, value, Relation.unary(relationName));
        scalars.add(scalar);
        scalarByAtom.put(atom, scalar);
    }

    private void bindAtomRelations() {
        for (ObjectAtom object : objects) bindSingleton(object.singleton(), object.atom());
        for (ScalarAtom scalar : scalars) bindSingleton(scalar.singleton(), scalar.atom());
    }

    private void bindSingleton(Relation relation, String atom) {
        bounds.boundExactly(relation, factory.setOf(atom));
        atomRelations.put(atom, relation);
    }

    private void indexAttributeSlots() {
        for (AttributeSlot slot : allAttributes()) {
            slotsByProperty.computeIfAbsent(slot.attribute().name(), ignored -> new ArrayList<>()).add(slot);
        }
    }

    private List<AttributeSlot> allAttributes() {
        List<AttributeSlot> result = new ArrayList<>();
        acl.entities().forEach(owner -> owner.attributes().forEach(attribute ->
                result.add(new AttributeSlot(owner.name(), attribute))));
        acl.roles().forEach(owner -> owner.attributes().forEach(attribute ->
                result.add(new AttributeSlot(owner.name(), attribute))));
        acl.groups().forEach(owner -> owner.attributes().forEach(attribute ->
                result.add(new AttributeSlot(owner.name(), attribute))));
        return List.copyOf(result);
    }

    private void createFrame(int index) {
        Frame frame = new Frame(index);
        frames.add(frame);
        for (var entry : objectsByConcreteType.entrySet()) {
            Relation relation = Relation.unary("exists_" + safe(entry.getKey()) + "_s" + index);
            frame.exists.put(entry.getKey(), relation);
            TupleSet upper = unary(entry.getValue().stream().map(ObjectAtom::atom).toList());
            bounds.bound(relation, upper);
            AclBpmnBoundary.Scope scope = boundary.objectScopes().get(entry.getKey());
            addStructural(relation.count().gte(IntConstant.constant(scope.lower())));
            addStructural(relation.count().lte(IntConstant.constant(scope.upper())));
        }

        for (AttributeSlot slot : allAttributes()) {
            Relation relation = Relation.binary("attr_" + safe(slot.ownerType()) + "_"
                    + safe(slot.attribute().name()) + "_s" + index);
            frame.attributes.put(slot, relation);
            List<ObjectAtom> domain = attributeDomain(slot);
            List<ScalarAtom> range = scalarsFor(slot.attribute().type());
            bounds.bound(relation, unaryAtoms(domain).product(unaryScalars(range)));
            for (ObjectAtom object : domain) {
                Formula present = exists(frame, object);
                Expression values = object.singleton().join(relation);
                addStructural(present.implies(slot.attribute().optional() ? values.lone() : values.one()));
                addStructural(present.not().implies(values.no()));
            }
        }

        for (AclRelation aclRelation : acl.relations()) {
            Relation relation = Relation.binary("assoc_" + safe(aclRelation.name()) + "_s" + index);
            frame.associations.put(aclRelation.name(), relation);
            List<ObjectAtom> sources = objectsForType(aclRelation.source().type());
            List<ObjectAtom> targets = objectsForType(aclRelation.target().type());
            bounds.bound(relation, unaryAtoms(sources).product(unaryAtoms(targets)));
            addAssociationFormula(frame, aclRelation, relation, sources, targets);
        }

        frame.play = Relation.binary("sigma_Play_s" + index);
        TupleSet playUpper = factory.noneOf(2);
        for (AclRole childType : acl.isRevisedCore() ? List.<AclRole>of() : acl.roles()) {
            for (String parentType : childType.parentRoles()) {
                playUpper.addAll(unaryAtoms(objectsByConcreteType.getOrDefault(parentType, List.of()))
                        .product(unaryAtoms(objectsByConcreteType.getOrDefault(childType.name(), List.of()))));
            }
        }
        bounds.bound(frame.play, playUpper);
        addPlayFormula(frame);
        if (acl.isRevisedCore()) createContextMemberships(frame);
    }

    /** Runtime process membership; it does not alter the declaration-only M1 model. */
    private void createContextMemberships(Frame frame) {
        for (AclGroup context : acl.orgContexts()) {
            for (var declaration : context.members()) {
                ContextMember key = new ContextMember(context.name(), declaration.type());
                Relation relation = Relation.binary("context_" + safe(key.context()) + "_" + safe(key.type()) + "_s" + frame.index());
                frame.memberships.put(key, relation);
                bounds.bound(relation, unaryAtoms(objectsForType(key.context())).product(unaryAtoms(objectsForType(key.type()))));
                addStructural(relation.in(typeExpression(frame, key.context()).product(typeExpression(frame, key.type()))));
                for (ObjectAtom member : objectsForType(key.type()))
                    addStructural(exists(frame, member).implies(relation.join(member.singleton()).one()));
            }
        }
    }

    private void addInitialDefaults() {
        Frame initial = frame(0);
        for (AttributeSlot slot : allAttributes()) {
            if (slot.attribute().defaultValue().isEmpty()) continue;
            String expected = normalizedDefault(slot.attribute().defaultValue().orElseThrow());
            ScalarAtom scalar = scalarsFor(slot.attribute().type()).stream()
                    .filter(value -> normalizedDefault(String.valueOf(value.value())).equals(expected))
                    .findFirst().orElseThrow(() -> new IllegalArgumentException(
                            "default value outside boundary for " + slot.ownerType() + "."
                                    + slot.attribute().name() + ": " + expected));
            Relation relation = initial.attribute(slot);
            for (ObjectAtom object : attributeDomain(slot)) {
                Formula hasDefault = object.singleton().join(relation).eq(scalar.singleton());
                addStructural(exists(initial, object).implies(hasDefault));
            }
        }
    }

    private static String normalizedDefault(String raw) {
        return unquote(raw).replaceFirst("^#", "").replaceFirst("^.*::", "");
    }

    private void addAssociationFormula(Frame frame, AclRelation definition, Relation relation,
                                       List<ObjectAtom> sources, List<ObjectAtom> targets) {
        for (ObjectAtom source : sources) {
            Expression linked = source.singleton().join(relation);
            addStructural(cardinality(frame, source, linked,
                    definition.target(), exists(frame, source)));
        }
        for (ObjectAtom target : targets) {
            Expression linked = relation.join(target.singleton());
            addStructural(cardinality(frame, target, linked,
                    definition.source(), exists(frame, target)));
        }
        AclBpmnBoundary.Scope scope = boundary.linkScopes().get(definition.name());
        if (scope != null) {
            addStructural(relation.count().gte(IntConstant.constant(scope.lower())));
            addStructural(relation.count().lte(IntConstant.constant(scope.upper())));
        }
    }

    private Formula cardinality(Frame frame, ObjectAtom object, Expression linked,
                                AclEndpoint endpoint, Formula present) {
        Formula allowed = linked.count().gte(IntConstant.constant(endpoint.multiplicity().min()));
        if (endpoint.multiplicity().max().isPresent()) {
            allowed = allowed.and(linked.count().lte(
                    IntConstant.constant(endpoint.multiplicity().max().getAsInt())));
        }
        return present.implies(allowed).and(present.not().implies(linked.no()));
    }

    private void addPlayFormula(Frame frame) {
        for (AclRole childType : acl.isRevisedCore() ? List.<AclRole>of() : acl.roles()) {
            for (String parentType : childType.parentRoles()) {
                for (ObjectAtom parent : objectsByConcreteType.getOrDefault(parentType, List.of())) {
                    for (ObjectAtom child : objectsByConcreteType.getOrDefault(childType.name(), List.of())) {
                        Formula linked = parent.singleton().product(child.singleton()).in(frame.play());
                        addStructural(linked.implies(
                                exists(frame, parent).and(exists(frame, child))));
                    }
                }
            }
            for (ObjectAtom child : objectsByConcreteType.getOrDefault(childType.name(), List.of())) {
                Formula childExists = exists(frame, child);
                for (String parentType : childType.parentRoles()) {
                    Expression parents = frame.play().join(child.singleton())
                            .intersection(typeExpression(frame, parentType));
                    addStructural(childExists.implies(parents.one()));
                    addStructural(childExists.not().implies(parents.no()));
                }
            }
        }
    }

    private void addInvariantFormulas() {
        for (Frame frame : frames) {
            for (AclInvariant invariant : acl.invariants()) {
                for (ObjectAtom self : objectsForType(invariant.contextType())) {
                    Formula body = expression(invariant.expression(), frame, frame, self);
                    addStructural(exists(frame, self).implies(body));
                }
            }
        }
    }

    private void addStructural(Formula formula) {
        structuralFormulas.add(formula);
    }

    private Value compile(Node node, Environment environment) {
        if (node instanceof Literal literal) return literal(literal.value());
        if (node instanceof Name name) {
            Value variable = environment.variables().get(name.value());
            if (variable != null) return variable;
            if (isClassifier(name.value())) return new TypeValue(name.value());
            return literal(name.value());
        }
        if (node instanceof Let let) return compile(let.body(),
                environment.with(let.variable(), compile(let.value(), environment)));
        if (node instanceof AtPre atPre) return compile(atPre.expression(), environment.atPre());
        if (node instanceof Unary unary) {
            if (!unary.operator().equals("not")) throw unsupported("unary operator " + unary.operator());
            Value operand = compile(unary.operand(), environment);
            return new BoolValue(bool(operand).not(), defined(operand));
        }
        if (node instanceof Binary binary) return binary(binary, environment);
        if (node instanceof Property property) {
            Value source = compile(property.source(), environment);
            if (!(source instanceof ObjectsValue objectsValue)) {
                throw unsupported("property navigation on a non-object value: " + property.name());
            }
            return property(objectsValue, property.name(), environment.current(), new LinkedHashSet<>());
        }
        if (node instanceof Call call) return call(call, environment);
        throw unsupported("unknown expression node");
    }

    private Value binary(Binary binary, Environment environment) {
        Value left = compile(binary.left(), environment);
        Value right = compile(binary.right(), environment);
        Formula valid = defined(left).and(defined(right));
        return switch (binary.operator()) {
            case "and" -> new BoolValue(bool(left).and(bool(right)), valid
                    .or(defined(left).and(bool(left).not())).or(defined(right).and(bool(right).not())));
            case "or" -> new BoolValue(bool(left).or(bool(right)), valid
                    .or(defined(left).and(bool(left))).or(defined(right).and(bool(right))));
            case "implies" -> new BoolValue(bool(left).implies(bool(right)), valid
                    .or(defined(left).and(bool(left).not())).or(defined(right).and(bool(right))));
            case "=" -> new BoolValue(equal(left, right), valid);
            case "<>" -> new BoolValue(equal(left, right).not(), valid);
            case "<", "<=", ">", ">=" -> new BoolValue(compare(left, right, binary.operator()), valid);
            case "+" -> add(left, right);
            default -> throw unsupported("binary operator " + binary.operator());
        };
    }

    private Value call(Call call, Environment environment) {
        Value source = compile(call.source(), environment);
        if (call.operation().equals("allInstances") && source instanceof TypeValue type) {
            require(call.arguments().isEmpty(), "allInstances() takes no arguments");
            return allInstances(type.name(), environment.current());
        }
        if (source instanceof ScalarCollectionValue bag) {
            require(call.arguments().isEmpty(), call.operation() + " takes no arguments");
            if (call.operation().equals("max")) {
                Map<ScalarAtom, List<Formula>> maxima = new LinkedHashMap<>();
                for (ScalarEntry entry : bag.entries()) {
                    require(entry.scalar().value() instanceof Number, "max() requires numeric elements");
                    Formula maximum = entry.member();
                    for (ScalarEntry other : bag.entries()) {
                        if (compareScalar(other.scalar().value(), entry.scalar().value(), ">"))
                            maximum = maximum.and(other.member().not());
                    }
                    maxima.computeIfAbsent(entry.scalar(), ignored -> new ArrayList<>()).add(maximum);
                }
                return new ScalarValue(combine(maxima));
            }
            throw unsupported("scalar collection operation " + call.operation());
        }
        if (!(source instanceof ObjectsValue collection)) {
            throw unsupported("collection operation " + call.operation() + " on a non-object collection");
        }
        return switch (call.operation()) {
            case "isEmpty" -> {
                require(call.arguments().isEmpty(), "isEmpty() takes no arguments");
                yield new BoolValue(or(collection.members().values()).not());
            }
            case "notEmpty" -> {
                require(call.arguments().isEmpty(), "notEmpty() takes no arguments");
                yield new BoolValue(or(collection.members().values()));
            }
            case "forAll", "exists" -> quantify(call, collection, environment);
            case "collect" -> {
                require(call.variable() != null && call.arguments().size() == 1, "collect requires iterator | body");
                List<ScalarEntry> entries = new ArrayList<>();
                for (var member : collection.members().entrySet()) {
                    Value value = compile(call.arguments().get(0), environment.with(call.variable(), singleton(member.getKey())));
                    if (!(value instanceof ScalarValue scalar)) throw unsupported("collect currently requires a scalar body");
                    scalar.choices().forEach((atom, condition) -> entries.add(new ScalarEntry(atom, member.getValue().and(condition))));
                }
                yield new ScalarCollectionValue(List.copyOf(entries));
            }
            case "any" -> {
                require(call.variable() != null && call.arguments().size() == 1, "any requires iterator | body");
                Relation choice = Relation.unary("ocl_any_" + choiceCount++);
                bounds.bound(choice, unaryAtoms(collection.members().keySet()));
                Map<ObjectAtom, Formula> selected = new LinkedHashMap<>();
                List<Formula> matches = new ArrayList<>();
                for (var member : collection.members().entrySet()) {
                    Value predicate = compile(call.arguments().get(0), environment.with(call.variable(), singleton(member.getKey())));
                    Formula match = member.getValue().and(bool(predicate)).and(defined(predicate));
                    Formula chosen = member.getKey().singleton().in(choice);
                    addStructural(chosen.implies(match));
                    matches.add(match);
                    selected.put(member.getKey(), chosen);
                }
                Formula some = or(matches);
                addStructural(some.implies(choice.one()).and(some.not().implies(choice.no())));
                yield new ObjectsValue(selected, false, some);
            }
            case "includes" -> {
                require(call.arguments().size() == 1, "includes takes one argument");
                Value item = compile(call.arguments().get(0), environment);
                if (!(item instanceof ObjectsValue itemObjects)) throw unsupported("includes of a scalar value");
                List<Formula> matches = new ArrayList<>();
                collection.members().forEach((object, member) -> {
                    Formula itemMember = itemObjects.members().get(object);
                    if (itemMember != null) matches.add(member.and(itemMember));
                });
                yield new BoolValue(or(matches));
            }
            case "size" -> {
                require(call.arguments().isEmpty(), "size() takes no arguments");
                kodkod.ast.IntExpression count = IntConstant.constant(0);
                for (Formula member : collection.members().values())
                    count = count.plus(member.thenElse(IntConstant.constant(1), IntConstant.constant(0)));
                Map<ScalarAtom, Formula> sizes = new LinkedHashMap<>();
                for (int n = 0; n <= collection.members().size(); n++)
                    sizes.put(new ScalarAtom("size:" + n, "Integer", (long) n, null), count.eq(IntConstant.constant(n)));
                yield new ScalarValue(sizes);
            }
            default -> throw unsupported("collection operation " + call.operation());
        };
    }

    private Value quantify(Call call, ObjectsValue collection, Environment environment) {
        require(call.variable() != null && call.arguments().size() == 1,
                call.operation() + " requires iterator | body");
        List<Formula> formulas = new ArrayList<>();
        List<Formula> valid = new ArrayList<>();
        for (ObjectAtom object : objects) {
            Formula membership = collection.members().get(object);
            if (membership == null) continue;
            Value body = compile(call.arguments().get(0),
                    environment.with(call.variable(), singleton(object)));
            valid.add(membership.implies(defined(body)));
            formulas.add(call.operation().equals("forAll")
                    ? membership.implies(bool(body)) : membership.and(bool(body)));
        }
        return new BoolValue(call.operation().equals("forAll") ? and(formulas) : or(formulas), and(valid));
    }

    private Value property(ObjectsValue source, String property, Frame frame, Set<String> visiting) {
        List<Formula> trueConditions = new ArrayList<>();
        Map<ScalarAtom, List<Formula>> scalarConditions = new LinkedHashMap<>();
        Map<ObjectAtom, List<Formula>> objectConditions = new LinkedHashMap<>();
        List<ScalarEntry> collected = new ArrayList<>();
        boolean navigationCollection = source.collection();
        boolean foundAttribute = false;
        boolean foundNavigation = false;

        for (var baseEntry : source.members().entrySet()) {
            ObjectAtom base = baseEntry.getKey();
            Formula baseMember = baseEntry.getValue();
            boolean baseNavigation = false;
            Optional<AttributeSlot> slot = attributeSlot(base, property);
            if (slot.isPresent()) {
                foundAttribute = true;
                Relation relation = frame.attribute(slot.get());
                for (ScalarAtom scalar : scalarsFor(slot.get().attribute().type())) {
                    Formula selected = baseMember.and(base.singleton().product(scalar.singleton()).in(relation));
                    if (slot.get().attribute().type() == AclPrimitiveType.BOOLEAN) {
                        if (Boolean.TRUE.equals(scalar.value())) trueConditions.add(selected);
                    } else {
                        scalarConditions.computeIfAbsent(scalar, ignored -> new ArrayList<>()).add(selected);
                        collected.add(new ScalarEntry(scalar, selected));
                    }
                }
                continue;
            }

            if (property.equals("playOf") && base.kind() == Kind.ROLE) {
                foundNavigation = true;
                baseNavigation = true;
                for (ObjectAtom parent : objects.stream().filter(value -> value.kind() == Kind.ROLE).toList()) {
                    objectConditions.computeIfAbsent(parent, ignored -> new ArrayList<>()).add(
                            baseMember.and(parent.singleton().product(base.singleton()).in(frame.play())));
                }
            }
            for (var entry : frame.memberships.entrySet()) {
                ContextMember key = entry.getKey();
                if (conforms(base, key.context()) && property.equals(key.type())) {
                    foundNavigation = true;
                    baseNavigation = true;
                    navigationCollection = true;
                    for (ObjectAtom member : objectsForType(key.type())) objectConditions
                            .computeIfAbsent(member, ignored -> new ArrayList<>()).add(baseMember.and(
                                    base.singleton().product(member.singleton()).in(entry.getValue())));
                }
                if (conforms(base, key.type())) {
                    if (property.equals(key.context())) {
                        foundNavigation = true;
                        baseNavigation = true;
                        for (ObjectAtom ctxObj : objectsForType(key.context())) {
                            objectConditions.computeIfAbsent(ctxObj, ignored -> new ArrayList<>()).add(
                                    baseMember.and(ctxObj.singleton().product(base.singleton()).in(entry.getValue())));
                        }
                    }
                    for (var entry2 : frame.memberships.entrySet()) {
                        ContextMember key2 = entry2.getKey();
                        if (key2.context().equals(key.context()) && property.equals(key2.type())) {
                            foundNavigation = true;
                            baseNavigation = true;
                            navigationCollection = true;
                            for (ObjectAtom ctxObj : objectsForType(key.context())) {
                                Formula isMember = baseMember.and(ctxObj.singleton().product(base.singleton()).in(entry.getValue()));
                                for (ObjectAtom targetObj : objectsForType(key2.type())) {
                                    objectConditions.computeIfAbsent(targetObj, ignored -> new ArrayList<>()).add(
                                            isMember.and(ctxObj.singleton().product(targetObj.singleton()).in(entry2.getValue())));
                                }
                            }
                        }
                    }
                }
            }
            for (AclRelation definition : acl.relations()) {
                Relation relation = frame.association(definition.name());
                if (conforms(base, definition.source().type())
                        && navigatesBy(property, definition, true)) {
                    foundNavigation = true;
                    baseNavigation = true;
                    navigationCollection |= definition.target().multiplicity().max().orElse(-1) != 1;
                    for (ObjectAtom target : objectsForType(definition.target().type())) {
                        objectConditions.computeIfAbsent(target, ignored -> new ArrayList<>()).add(
                                baseMember.and(base.singleton().product(target.singleton()).in(relation)));
                    }
                }
                if (conforms(base, definition.target().type())
                        && navigatesBy(property, definition, false)) {
                    foundNavigation = true;
                    baseNavigation = true;
                    navigationCollection |= definition.source().multiplicity().max().orElse(-1) != 1;
                    for (ObjectAtom sourceObject : objectsForType(definition.source().type())) {
                        objectConditions.computeIfAbsent(sourceObject, ignored -> new ArrayList<>()).add(
                                baseMember.and(sourceObject.singleton().product(base.singleton()).in(relation)));
                    }
                }
            }
            if (!acl.isRevisedCore() && !baseNavigation && base.kind() == Kind.ROLE
                    && !role(base.concreteType()).parentRoles().isEmpty()) {
                String key = base.concreteType() + "." + property;
                if (!visiting.add(key)) throw unsupported("cyclic inherited Role property " + property);
                Map<ObjectAtom, Formula> parents = new LinkedHashMap<>();
                for (String parentType : role(base.concreteType()).parentRoles()) {
                    for (ObjectAtom parent : objectsByConcreteType.getOrDefault(parentType, List.of())) {
                        parents.put(parent, baseMember.and(parent.singleton().product(base.singleton())
                                .in(frame.play())));
                    }
                }
                Value inherited = property(new ObjectsValue(parents), property, frame, visiting);
                visiting.remove(key);
                if (inherited instanceof BoolValue bool) trueConditions.add(bool.formula());
                else if (inherited instanceof ScalarValue scalar) scalar.choices().forEach((value, condition) ->
                        scalarConditions.computeIfAbsent(value, ignored -> new ArrayList<>()).add(condition));
                else if (inherited instanceof ObjectsValue inheritedObjects) inheritedObjects.members()
                        .forEach((value, condition) -> objectConditions
                                .computeIfAbsent(value, ignored -> new ArrayList<>()).add(condition));
            }
        }

        if (foundAttribute) {
            if (!trueConditions.isEmpty() || scalarConditions.isEmpty()) return new BoolValue(or(trueConditions), source.defined());
            if (source.collection()) return new ScalarCollectionValue(List.copyOf(collected));
            return new ScalarValue(combine(scalarConditions));
        }
        if (foundNavigation) return new ObjectsValue(combine(objectConditions), navigationCollection, source.defined());
        throw unsupported("unknown property '" + property + "'");
    }

    private Optional<AttributeSlot> attributeSlot(ObjectAtom object, String property) {
        for (AttributeSlot slot : slotsByProperty.getOrDefault(property, List.of())) {
            if (slot.ownerType().equals(object.concreteType())) return Optional.of(slot);
            if ((acl.isRevisedCore() || object.kind() != Kind.ROLE) && typeConforms(object.concreteType(), slot.ownerType(), object.kind())) {
                return Optional.of(slot);
            }
        }
        return Optional.empty();
    }

    private ObjectsValue allInstances(String type, Frame frame) {
        Map<ObjectAtom, Formula> result = new LinkedHashMap<>();
        for (ObjectAtom object : objectsForType(type)) result.put(object, exists(frame, object));
        return new ObjectsValue(result);
    }

    private Formula equal(Value left, Value right) {
        if (left instanceof BoolValue a && right instanceof BoolValue b) return a.formula().iff(b.formula());
        if (left instanceof ScalarValue a && right instanceof ScalarValue b) {
            List<Formula> matches = new ArrayList<>();
            a.choices().forEach((leftValue, leftCondition) -> b.choices().forEach((rightValue, rightCondition) -> {
                if (sameScalar(leftValue, rightValue)) matches.add(leftCondition.and(rightCondition));
            }));
            return or(matches);
        }
        if (left instanceof ObjectsValue a && right instanceof ObjectsValue b) {
            Set<ObjectAtom> keys = new LinkedHashSet<>(a.members().keySet());
            keys.addAll(b.members().keySet());
            List<Formula> matches = new ArrayList<>();
            for (ObjectAtom object : keys) matches.add(a.members().getOrDefault(object, Formula.FALSE)
                    .iff(b.members().getOrDefault(object, Formula.FALSE)));
            return and(matches);
        }
        throw unsupported("equality between incompatible values");
    }

    private Formula compare(Value left, Value right, String operator) {
        if (!(left instanceof ScalarValue a) || !(right instanceof ScalarValue b)) {
            throw unsupported("comparison on a non-scalar value");
        }
        List<Formula> matches = new ArrayList<>();
        a.choices().forEach((leftValue, leftCondition) -> b.choices().forEach((rightValue, rightCondition) -> {
            if (compareScalar(leftValue.value(), rightValue.value(), operator)) {
                matches.add(leftCondition.and(rightCondition));
            }
        }));
        return or(matches);
    }

    private Value add(Value left, Value right) {
        if (!(left instanceof ScalarValue a) || !(right instanceof ScalarValue b)) {
            throw unsupported("addition on a non-scalar value");
        }
        Map<ScalarAtom, List<Formula>> results = new LinkedHashMap<>();
        a.choices().forEach((leftValue, leftCondition) ->
                b.choices().forEach((rightValue, rightCondition) -> {
                    if (!(leftValue.value() instanceof Number leftNumber)
                            || !(rightValue.value() instanceof Number rightNumber)) return;
                    double sum = leftNumber.doubleValue() + rightNumber.doubleValue();
                    for (ScalarAtom candidate : scalars) {
                        if (candidate.value() instanceof Number number
                                && Double.compare(number.doubleValue(), sum) == 0) {
                            results.computeIfAbsent(candidate, ignored -> new ArrayList<>())
                                    .add(leftCondition.and(rightCondition));
                        }
                    }
                }));
        return new ScalarValue(combine(results));
    }

    private Value literal(Object value) {
        if (value instanceof Boolean bool) return new BoolValue(bool ? Formula.TRUE : Formula.FALSE);
        Map<ScalarAtom, Formula> choices = new LinkedHashMap<>();
        for (ScalarAtom scalar : scalars) {
            if (literalMatches(value, scalar)) choices.put(scalar, Formula.TRUE);
        }
        if (choices.isEmpty()) throw unsupported("literal outside boundary: " + value);
        return new ScalarValue(choices);
    }

    private boolean literalMatches(Object literal, ScalarAtom scalar) {
        if (literal instanceof Number number && scalar.value() instanceof Number value) {
            return Double.compare(number.doubleValue(), value.doubleValue()) == 0;
        }
        String raw = String.valueOf(literal);
        if (raw.contains("::") && !raw.substring(0, raw.indexOf("::")).equals(scalar.type())) return false;
        String expected = raw.replaceFirst("^.*::", "");
        return expected.equals(String.valueOf(scalar.value()).replaceFirst("^.*::", ""));
    }

    private Formula defined(Value value) {
        if (value instanceof BoolValue b) return b.defined();
        if (value instanceof ScalarValue s) return or(s.choices().values());
        if (value instanceof ObjectsValue o) return o.defined();
        return Formula.TRUE;
    }

    private Formula bool(Value value) {
        if (value instanceof BoolValue result) return result.formula();
        throw unsupported("expected a Boolean OCL expression");
    }

    private ObjectsValue singleton(ObjectAtom object) {
        return new ObjectsValue(Map.of(object, Formula.TRUE), false, Formula.TRUE);
    }

    private List<ObjectAtom> objectsForType(String type) {
        List<ObjectAtom> result = new ArrayList<>();
        Kind expectedKind = kind(type);
        for (ObjectAtom object : objects) {
            if (object.concreteType().equals(type)
                    || (acl.isRevisedCore() || expectedKind != Kind.ROLE) && object.kind() == expectedKind
                    && typeConforms(object.concreteType(), type, expectedKind)) result.add(object);
        }
        return List.copyOf(result);
    }

    private Expression typeExpression(Frame frame, String type) {
        List<Expression> expressions = objectsForType(type).stream()
                .map(object -> object.singleton().intersection(frame.exists(object.concreteType())))
                .map(Expression.class::cast).toList();
        return expressions.isEmpty() ? Expression.NONE : unionExpressions(expressions);
    }

    private List<ObjectAtom> attributeDomain(AttributeSlot slot) {
        Kind ownerKind = kind(slot.ownerType());
        if (ownerKind == Kind.ROLE && !acl.isRevisedCore()) return objectsByConcreteType.getOrDefault(slot.ownerType(), List.of());
        return objectsForType(slot.ownerType());
    }

    private List<ScalarAtom> scalarsFor(AclDataType type) {
        return scalars.stream().filter(value -> value.type().equals(type.sourceName())).toList();
    }

    private AclRole role(String name) {
        return acl.findRole(name).orElseThrow(() -> new IllegalArgumentException("unknown Role " + name));
    }

    private boolean isClassifier(String value) {
        return acl.findEntity(value).isPresent() || acl.findRole(value).isPresent() || acl.findGroup(value).isPresent();
    }

    private Kind kind(String type) {
        if (acl.findEntity(type).isPresent()) return Kind.ENTITY;
        if (acl.findRole(type).isPresent()) return Kind.ROLE;
        if (acl.findGroup(type).isPresent()) return Kind.GROUP;
        throw new IllegalArgumentException("unknown ACL classifier " + type);
    }

    private boolean conforms(ObjectAtom object, String expected) {
        return object.concreteType().equals(expected)
                || (acl.isRevisedCore() || object.kind() != Kind.ROLE) && kind(expected) == object.kind()
                && typeConforms(object.concreteType(), expected, object.kind());
    }

    private boolean typeConforms(String concrete, String expected, Kind kind) {
        String current = concrete;
        Set<String> seen = new LinkedHashSet<>();
        while (seen.add(current)) {
            if (current.equals(expected)) return true;
            if (kind == Kind.ENTITY) current = acl.findEntity(current)
                    .flatMap(AclEntity::specializes).orElse(null);
            else if (kind == Kind.GROUP) current = acl.findGroup(current)
                    .flatMap(AclGroup::specializes).orElse(null);
            else if (kind == Kind.ROLE && acl.isRevisedCore()) current = acl.findRole(current)
                    .flatMap(r -> r.parentRoles().stream().findFirst()).orElse(null);
            else return false;
            if (current == null) return false;
        }
        return false;
    }

    private boolean navigatesBy(String requested, AclRelation relation, boolean forward) {
        AclEndpoint endpoint = forward ? relation.target() : relation.source();
        String compatibility = (forward ? "target_" : "source_") + relation.target().type()
                + "_in_" + relation.source().type();
        return requested.equals(relation.name()) || endpoint.roleName().filter(requested::equals).isPresent()
                || requested.equals(compatibility);
    }

    private TupleSet unary(Collection<String> atoms) {
        TupleSet result = factory.noneOf(1);
        atoms.forEach(atom -> result.add(factory.tuple(atom)));
        return result;
    }

    private TupleSet unaryAtoms(Collection<ObjectAtom> atoms) {
        return unary(atoms.stream().map(ObjectAtom::atom).toList());
    }

    private TupleSet unaryScalars(Collection<ScalarAtom> atoms) {
        return unary(atoms.stream().map(ScalarAtom::atom).toList());
    }

    private String decodeFrame(Instance instance, Frame frame) {
        String aclFileName = acl.name() != null && !acl.name().isBlank() ? acl.name() + ".acl" : "model.acl";
        StringBuilder result = new StringBuilder("aol v2.0 State_")
                .append(frame.index())
                .append(" for \"").append(aclFileName).append("\" {\n");

        List<ObjectAtom> present = objects.stream().filter(object -> contains(instance, frame.exists(object.concreteType()),
                object.atom())).toList();

        List<ObjectAtom> groups = present.stream().filter(o -> o.kind() == Kind.GROUP).toList();
        List<ObjectAtom> entities = present.stream().filter(o -> o.kind() == Kind.ENTITY).toList();

        Map<String, String> roleToAgent = new LinkedHashMap<>();
        Set<String> agentIds = new LinkedHashSet<>();
        TupleSet plays = instance.tuples(frame.play());
        if (plays != null) {
            for (Tuple tuple : plays) {
                ObjectAtom source = objectByAtom.get(String.valueOf(tuple.atom(0)));
                ObjectAtom target = objectByAtom.get(String.valueOf(tuple.atom(1)));
                if (source != null && target != null) {
                    roleToAgent.put(target.id(), source.id());
                    if (source.kind() != Kind.ROLE) {
                        agentIds.add(source.id());
                    }
                }
            }
        }

        if (!agentIds.isEmpty()) {
            for (String agentId : agentIds) {
                result.append("  agent ").append(agentId).append(";\n");
            }
        }

        Map<String, List<ObjectAtom>> contextMembers = new LinkedHashMap<>();
        Set<ObjectAtom> containedObjects = new LinkedHashSet<>();

        for (var entry : frame.memberships.entrySet()) {
            TupleSet tuples = instance.tuples(entry.getValue());
            if (tuples != null) {
                for (Tuple tuple : tuples) {
                    ObjectAtom contextObj = objectByAtom.get(String.valueOf(tuple.atom(0)));
                    ObjectAtom memberObj = objectByAtom.get(String.valueOf(tuple.atom(1)));
                    if (contextObj != null && memberObj != null) {
                        contextMembers.computeIfAbsent(contextObj.id(), ignored -> new ArrayList<>()).add(memberObj);
                        containedObjects.add(memberObj);
                    }
                }
            }
        }

        for (ObjectAtom group : groups) {
            result.append("\n  group ").append(group.concreteType()).append(" as ").append(group.id()).append(" {\n");
            List<String> groupValues = getAttributeValues(instance, frame, group);
            for (String val : groupValues) {
                result.append("    ").append(val).append(";\n");
            }
            List<ObjectAtom> members = contextMembers.getOrDefault(group.id(), List.of());
            for (ObjectAtom member : members) {
                if (member.kind() == Kind.ROLE) {
                    String agentId = roleToAgent.get(member.id());
                    List<String> roleValues = getAttributeValues(instance, frame, member);
                    if (agentId != null) {
                        result.append("    play ").append(member.concreteType()).append(" as ").append(member.id())
                                .append(" by ").append(agentId);
                        if (!roleValues.isEmpty()) {
                            result.append(" { ").append(String.join("; ", roleValues)).append(" }");
                        }
                        result.append(";\n");
                    } else {
                        result.append("    role ").append(member.concreteType()).append(" as ").append(member.id());
                        if (!roleValues.isEmpty()) {
                            result.append(" { ").append(String.join("; ", roleValues)).append(" }");
                        }
                        result.append(";\n");
                    }
                } else if (member.kind() == Kind.ENTITY) {
                    List<String> entValues = getAttributeValues(instance, frame, member);
                    result.append("    entity ").append(member.concreteType()).append(" as ").append(member.id());
                    if (!entValues.isEmpty()) {
                        result.append(" { ").append(String.join("; ", entValues)).append(" }");
                    }
                    result.append(";\n");
                }
            }
            result.append("  }\n");
        }

        for (ObjectAtom entity : entities) {
            if (!containedObjects.contains(entity)) {
                List<String> entValues = getAttributeValues(instance, frame, entity);
                result.append("  entity ").append(entity.concreteType()).append(" as ").append(entity.id());
                if (!entValues.isEmpty()) {
                    result.append(" { ").append(String.join("; ", entValues)).append(" }");
                }
                result.append(";\n");
            }
        }

        for (AclRelation definition : acl.relations()) {
            TupleSet tuples = instance.tuples(frame.association(definition.name()));
            if (tuples == null) continue;
            for (Tuple tuple : tuples) {
                ObjectAtom source = objectByAtom.get(String.valueOf(tuple.atom(0)));
                ObjectAtom target = objectByAtom.get(String.valueOf(tuple.atom(1)));
                if (source != null && target != null) {
                    result.append("  link ").append(definition.name()).append(": ")
                            .append(source.id()).append(" -> ").append(target.id()).append(";\n");
                }
            }
        }

        if (plays != null) {
            for (Tuple tuple : plays) {
                ObjectAtom source = objectByAtom.get(String.valueOf(tuple.atom(0)));
                ObjectAtom target = objectByAtom.get(String.valueOf(tuple.atom(1)));
                if (source != null && target != null && source.kind() == Kind.ROLE && target.kind() == Kind.ROLE) {
                    result.append("  play ").append(source.id()).append(" -> ").append(target.id()).append(";\n");
                }
            }
        }

        result.append("}");
        return result.toString();
    }

    private List<String> getAttributeValues(Instance instance, Frame frame, ObjectAtom object) {
        List<String> values = new ArrayList<>();
        for (var entry : frame.attributes.entrySet()) {
            if (!attributeDomain(entry.getKey()).contains(object)) continue;
            ScalarAtom scalar = selectedScalar(instance, entry.getValue(), object);
            if (scalar != null) {
                values.add(entry.getKey().attribute().name() + " = " + display(scalar));
            }
        }
        return values;
    }

    private ScalarAtom selectedScalar(Instance instance, Relation relation, ObjectAtom object) {
        TupleSet tuples = instance.tuples(relation);
        if (tuples == null) return null;
        for (Tuple tuple : tuples) {
            if (String.valueOf(tuple.atom(0)).equals(object.atom())) {
                return scalarByAtom.get(String.valueOf(tuple.atom(1)));
            }
        }
        return null;
    }

    private static String display(ScalarAtom scalar) {
        if (scalar.type().equals("String")) return "\"" + scalar.value() + "\"";
        return String.valueOf(scalar.value());
    }

    private static boolean contains(Instance instance, Relation relation, String atom) {
        TupleSet tuples = instance.tuples(relation);
        if (tuples == null) return false;
        for (Tuple tuple : tuples) if (String.valueOf(tuple.atom(0)).equals(atom)) return true;
        return false;
    }

    private static boolean sameScalar(ScalarAtom left, ScalarAtom right) {
        if (left.value() instanceof Number a && right.value() instanceof Number b) {
            return Double.compare(a.doubleValue(), b.doubleValue()) == 0;
        }
        return left.type().equals(right.type()) && Objects.equals(left.value(), right.value());
    }

    private static boolean compareScalar(Object left, Object right, String operator) {
        int comparison;
        if (left instanceof Number a && right instanceof Number b) {
            comparison = Double.compare(a.doubleValue(), b.doubleValue());
        } else comparison = String.valueOf(left).compareTo(String.valueOf(right));
        return switch (operator) {
            case "<" -> comparison < 0;
            case "<=" -> comparison <= 0;
            case ">" -> comparison > 0;
            case ">=" -> comparison >= 0;
            default -> false;
        };
    }

    private static <K> Map<K, Formula> combine(Map<K, List<Formula>> values) {
        Map<K, Formula> result = new LinkedHashMap<>();
        values.forEach((key, formulas) -> result.put(key, or(formulas)));
        return Map.copyOf(result);
    }

    private static Formula and(Collection<Formula> formulas) { return Formula.and(formulas); }
    private static Formula or(Collection<Formula> formulas) { return Formula.or(formulas); }

    private static Expression unionExpressions(List<Expression> expressions) {
        return Expression.union(expressions);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw unsupported(message);
    }

    private static IllegalArgumentException unsupported(String message) {
        return new IllegalArgumentException("Unsupported symbolic ACL/OCL: " + message);
    }

    private static String safe(String value) { return value.replaceAll("[^A-Za-z0-9_]", "_"); }
    private static String unquote(String value) {
        return value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")
                ? value.substring(1, value.length() - 1) : value;
    }
}
