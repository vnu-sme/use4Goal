package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/** Root AST node for a BPMN 2.0 unified Model (CS = Concrete Syntax). */
public record Bpmn2ModelCS(
        String              name,
        List<ProcessCS>     processes,
        List<MessageCS>     messages,
        List<MessageFlowCS> messageFlows
) {}
