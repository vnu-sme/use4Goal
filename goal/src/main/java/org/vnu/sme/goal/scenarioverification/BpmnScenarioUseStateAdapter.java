package org.vnu.sme.goal.scenarioverification;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.RealValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;
import org.vnu.sme.goal.bpmn2scenario.mm.NodeOccurrence;
import org.vnu.sme.goal.bpmn2scenario.mm.TokenMark;

/**
 * Materializes the USE runtime state explicitly described by a BPMN scenario
 * snapshot. This adapter does not execute BPMN and does not evaluate OCL.
 */
public final class BpmnScenarioUseStateAdapter {

    private BpmnScenarioUseStateAdapter() {}

    public record Result(ScenarioRuntimeState runtimeState, List<String> errors) {
        public Result {
            errors = List.copyOf(errors == null ? List.of() : errors);
        }

        public boolean ok() {
            return errors.isEmpty() && runtimeState != null;
        }
    }

    public static Result materialize(Bpmn2ScenarioSnapshot snapshot, MModel useModel) {
        List<String> errors = new ArrayList<>();
        if (snapshot == null) {
            errors.add("Scenario snapshot is null.");
            return new Result(null, errors);
        }
        if (useModel == null) {
            errors.add("USE model is null.");
            return new Result(null, errors);
        }

        MSystem system = new MSystem(useModel);
        Map<String, MObject> objects = materializeObjects(snapshot, useModel, system, errors);
        setAttributes(snapshot, system.state(), objects, errors);
        Map<String, MObject> selfBindings = buildSelfBindings(snapshot, objects, errors);

        if (!errors.isEmpty()) {
            return new Result(null, errors);
        }
        return new Result(new ScenarioRuntimeState(snapshot, system.state(), selfBindings), List.of());
    }

    private static Map<String, MObject> materializeObjects(
            Bpmn2ScenarioSnapshot snapshot, MModel useModel, MSystem system, List<String> errors) {
        Map<String, MObject> objects = new LinkedHashMap<>();
        for (Map.Entry<String, String> declaration : snapshot.actors().entrySet()) {
            String objectName = declaration.getKey();
            String className = declaration.getValue();
            MClass cls = useModel.getClass(className);
            if (cls == null) {
                errors.add("Unknown USE class '" + className + "' for object '" + objectName + "'.");
                continue;
            }
            try {
                objects.put(objectName, system.state().createObject(cls, objectName));
            } catch (MSystemException ex) {
                errors.add("Cannot create object '" + objectName + "' of class '" + className + "': "
                        + ex.getMessage());
            }
        }
        return objects;
    }

    private static void setAttributes(
            Bpmn2ScenarioSnapshot snapshot,
            MSystemState state,
            Map<String, MObject> objects,
            List<String> errors) {
        for (Map.Entry<String, org.vnu.sme.goal.bpmn2scenario.mm.Value> assignment : snapshot.values().entrySet()) {
            String target = assignment.getKey();
            int dot = target.lastIndexOf('.');
            if (dot <= 0 || dot == target.length() - 1) {
                errors.add("Unsupported value target '" + target + "'; expected '<object>.<attribute>'.");
                continue;
            }

            String objectName = target.substring(0, dot);
            String attributeName = target.substring(dot + 1);
            MObject object = objects.get(objectName);
            if (object == null) {
                errors.add("Unknown object '" + objectName + "' for value target '" + target + "'.");
                continue;
            }

            MAttribute attribute = object.cls().attribute(attributeName, true);
            if (attribute == null) {
                errors.add("Unknown attribute '" + attributeName + "' on object '" + objectName
                        + "' of class '" + object.cls().name() + "'.");
                continue;
            }

            Value useValue = convertValue(target, assignment.getValue(), attribute.type(), objects, errors);
            if (useValue == null) {
                continue;
            }

            try {
                object.state(state).setAttributeValue(attribute, useValue);
            } catch (IllegalArgumentException ex) {
                errors.add("Invalid value type for '" + target + "': " + ex.getMessage());
            }
        }
    }

    private static Value convertValue(String target,
            org.vnu.sme.goal.bpmn2scenario.mm.Value scenarioValue,
            Type expectedType,
            Map<String, MObject> objects,
            List<String> errors) {
        if (scenarioValue instanceof org.vnu.sme.goal.bpmn2scenario.mm.Value.ListValue) {
            errors.add("Unsupported list value for attribute '" + target + "'.");
            return null;
        }
        if (!(scenarioValue instanceof org.vnu.sme.goal.bpmn2scenario.mm.Value.Atom atom)) {
            errors.add("Unsupported value for attribute '" + target + "'.");
            return null;
        }

        String text = atom.text();
        if (expectedType.isTypeOfBoolean()) {
            if ("true".equalsIgnoreCase(text)) {
                return BooleanValue.TRUE;
            }
            if ("false".equalsIgnoreCase(text)) {
                return BooleanValue.FALSE;
            }
            errors.add("Invalid value type for '" + target + "': expected Boolean, found '" + text + "'.");
            return null;
        }
        if (expectedType.isTypeOfInteger()) {
            try {
                return IntegerValue.valueOf(Integer.parseInt(text));
            } catch (NumberFormatException ex) {
                errors.add("Invalid value type for '" + target + "': expected Integer, found '" + text + "'.");
                return null;
            }
        }
        if (expectedType.isTypeOfReal()) {
            try {
                return new RealValue(Double.parseDouble(text));
            } catch (NumberFormatException ex) {
                errors.add("Invalid value type for '" + target + "': expected Real, found '" + text + "'.");
                return null;
            }
        }
        if (expectedType.isTypeOfString()) {
            return new StringValue(unquote(text));
        }
        if (expectedType.isTypeOfEnum()) {
            EnumType enumType = (EnumType) expectedType;
            String literal = enumLiteral(text);
            if (!enumType.contains(literal)) {
                errors.add("Invalid enum literal '" + text + "' for '" + target + "'; expected one of "
                        + enumType.getLiterals() + ".");
                return null;
            }
            return new EnumValue(enumType, literal);
        }
        if (expectedType.isTypeOfClass()) {
            String objectName = unquote(text);
            MObject object = objects.get(objectName);
            if (object == null) {
                errors.add("Unknown object '" + objectName + "' for reference attribute '" + target + "'.");
                return null;
            }
            return object.value();
        }

        errors.add("Unsupported attribute type '" + expectedType + "' for '" + target + "'.");
        return null;
    }

    private static Map<String, MObject> buildSelfBindings(
            Bpmn2ScenarioSnapshot snapshot, Map<String, MObject> objects, List<String> errors) {
        Map<String, MObject> bindings = new LinkedHashMap<>();
        for (NodeOccurrence occurrence : snapshot.fired()) {
            bindNode("fired", occurrence, objects, bindings, errors);
        }
        for (NodeOccurrence occurrence : snapshot.completed()) {
            bindNode("completed", occurrence, objects, bindings, errors);
        }
        for (NodeOccurrence occurrence : snapshot.active()) {
            bindNode("active", occurrence, objects, bindings, errors);
        }
        for (TokenMark token : snapshot.tokens()) {
            bindOwner("token", token.display(), token.arcId(), token.objectId(), objects, bindings, errors);
        }
        return bindings;
    }

    private static void bindNode(String source,
            NodeOccurrence occurrence,
            Map<String, MObject> objects,
            Map<String, MObject> bindings,
            List<String> errors) {
        bindOwner(source, occurrence.display(), occurrence.elementId(), occurrence.objectId(), objects, bindings, errors);
    }

    private static void bindOwner(String source,
            String display,
            String ownerId,
            String objectId,
            Map<String, MObject> objects,
            Map<String, MObject> bindings,
            List<String> errors) {
        if (objectId == null || objectId.isBlank()) {
            errors.add("Missing self binding for BPMN owner '" + ownerId + "' in " + source
                    + " occurrence '" + display + "'.");
            return;
        }
        MObject object = objects.get(objectId);
        if (object == null) {
            errors.add("Unknown object '" + objectId + "' for BPMN owner '" + ownerId
                    + "' in " + source + " occurrence '" + display + "'.");
            return;
        }
        MObject existing = bindings.get(ownerId);
        if (existing != null && existing != object) {
            errors.add("Conflicting self binding for BPMN owner '" + ownerId + "': '"
                    + existing.name() + "' vs '" + object.name() + "'.");
            return;
        }
        bindings.put(ownerId, object);
    }

    private static String enumLiteral(String text) {
        String literal = unquote(text);
        if (literal.startsWith("#")) {
            return literal.substring(1);
        }
        int separator = literal.lastIndexOf("::");
        if (separator >= 0) {
            return literal.substring(separator + 2);
        }
        return literal;
    }

    private static String unquote(String text) {
        if (text == null || text.length() < 2) {
            return text;
        }
        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }
}
