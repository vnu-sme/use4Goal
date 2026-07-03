package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/** Lane (swimlane) inside a pool in concrete syntax. */
public record LaneCS(
        String           id,
        String           label,
        List<FlowNodeCS> elements
) {}
