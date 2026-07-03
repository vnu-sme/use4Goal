package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/** Root AST node for a BPMN 2.0 Collaboration (CS = Concrete Syntax). */
public record Bpmn2CollaborationCS(
        String              id,
        List<PoolCS>        pools,
        List<MessageFlowCS> messageFlows
) {}
