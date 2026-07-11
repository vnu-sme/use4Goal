package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/** Process (= 'pool' in concrete syntax) in concrete syntax. */
public record ProcessCS(
        String                id,
        String                name,          // nullable
        List<LaneCS>          lanes,
        List<FlowElementCS>   flowElements,   // elements not assigned to any lane
        List<SequenceFlowCS>  sequenceFlows
) {}
