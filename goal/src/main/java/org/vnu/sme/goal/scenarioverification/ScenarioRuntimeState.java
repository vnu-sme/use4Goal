package org.vnu.sme.goal.scenarioverification;

import java.util.Map;

import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystemState;
import org.vnu.sme.goal.bpmn2scenario.mm.Bpmn2ScenarioSnapshot;

/**
 * Immutable container for the externally supplied scenario runtime state.
 *
 * <p>This class does not execute BPMN, materialize USE objects, resolve bindings, or
 * evaluate OCL. It only groups data prepared by earlier pipeline phases.
 */
public record ScenarioRuntimeState(
        Bpmn2ScenarioSnapshot snapshot,
        MSystemState state,
        Map<String, MObject> selfBindings) {

    public ScenarioRuntimeState {
        selfBindings = selfBindings == null ? Map.of() : Map.copyOf(selfBindings);
    }

    public MObject selfForBpmn(String ownerId) {
        return selfBindings.get(ownerId);
    }
}
