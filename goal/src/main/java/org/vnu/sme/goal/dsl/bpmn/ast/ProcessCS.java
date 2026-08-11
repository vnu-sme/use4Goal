package org.vnu.sme.goal.dsl.bpmn.ast;

import java.util.List;

/**
 * Process (= 'pool' in concrete syntax) in concrete syntax: a bare lane
 * skeleton, plus the optional ACL group class it is scoped to (`for
 * <GroupClass>`). Flow elements live at {@link BpmnModelCS} level and
 * reference a lane by id, not the other way around.
 */
public record ProcessCS(
        String       id,
        String       name,       // nullable
        String       groupClass, // nullable
        List<LaneCS> lanes
) {}
