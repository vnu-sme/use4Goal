package org.vnu.sme.goal.bpmn2.ocl;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.bpmn2.mm.Bpmn2Model;
import org.vnu.sme.goal.bpmn2.mm.FlowElement;
import org.vnu.sme.goal.conformance.mapping.ConformanceMapping;
import org.vnu.sme.goal.conformance.mapping.ElementMapping;
import org.vnu.sme.goal.istar.mm.GoalModel;
import org.vnu.sme.goal.istar.mm.GoalTaskElement;

/**
 * Lightweight bridge check for Option 2: if a mapped i-star/goal element has OCL,
 * the realizing BPMN node should also carry an OCL clause. This does not prove
 * achievement; it catches missing BPMN-side behavioral conditions before the
 * execution/path checker runs.
 */
public final class Bpmn2GoalOclCoverageValidator {

    private Bpmn2GoalOclCoverageValidator() {}

    public static List<String> validate(GoalModel goalModel, Bpmn2Model bpmnModel, ConformanceMapping mapping) {
        List<String> warnings = new ArrayList<>();

        for (ElementMapping m : mapping.elements()) {
            GoalTaskElement goalElement = goalModel.findElement(m.istarElementId())
                    .filter(GoalTaskElement.class::isInstance)
                    .map(GoalTaskElement.class::cast)
                    .orElse(null);
            if (goalElement == null || goalElement.oclSource() == null || goalElement.oclSource().isBlank()) {
                continue;
            }

            FlowElement bpmnElement = bpmnModel.findFlowElement(m.bpmnNodeId()).orElse(null);
            if (bpmnElement == null) continue;
            if (bpmnElement.postconditions().isEmpty()) {
                warnings.add("map: i* element '" + m.istarElementId() + "' has OCL but BPMN node '"
                        + m.bpmnNodeId() + "' has no BPMN postcondition");
            }
        }

        return warnings;
    }
}
