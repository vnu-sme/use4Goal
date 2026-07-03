package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/** Pool (process participant) in concrete syntax. */
public record PoolCS(
        String              id,
        String              label,
        List<LaneCS>        lanes,
        List<FlowNodeCS>    elements,      // nodes not assigned to any lane
        List<SequenceFlowCS> sequenceFlows
) {}
