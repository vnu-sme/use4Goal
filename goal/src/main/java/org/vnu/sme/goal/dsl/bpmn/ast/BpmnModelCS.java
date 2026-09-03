package org.vnu.sme.goal.dsl.bpmn.ast;

import java.util.List;

/**
 * Root AST node for a BPMN 2.0 unified Model (CS = Concrete Syntax).
 * Flow elements are top-level declarations of the model (Event-B-style),
 * not nested inside a pool/lane; each one names its own lane.
 */
public record BpmnModelCS(
        String              name,
        List<ProcessCS>     processes,
        List<FlowElementCS> flowElements,
        List<MessageCS>     messages,
        List<MessageFlowCS> messageFlows
) {}
