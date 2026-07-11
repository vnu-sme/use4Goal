package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/** Lane (swimlane) inside a process in concrete syntax. */
public record LaneCS(
        String              id,
        String              name,   // nullable
        List<FlowElementCS> flowElements
) {}
