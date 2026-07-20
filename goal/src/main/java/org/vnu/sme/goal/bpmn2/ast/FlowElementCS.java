package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/**
 * Sealed hierarchy for BPMN 2.0 flow elements in concrete syntax.
 * Mirrors the {@code poolElement} alternatives in Bpmn2.g4 and the
 * Event/Activity/Gateway subclasses of the unified metamodel
 * (doc/05-bpmn2-metamodel.drawio).
 */
public sealed interface FlowElementCS {

    /** start <id> : <trigger> [ocl {[ ... ]}] */
    record StartEventCS(String id, String trigger, String oclSource) implements FlowElementCS {}

    /** end <id> : <trigger> [ocl {[ ... ]}] */
    record EndEventCS(String id, String trigger, String oclSource) implements FlowElementCS {}

    /** intermediate <id> : <trigger> : catching|throwing [ocl {[ ... ]}] */
    record IntermediateEventCS(String id, String trigger, String direction, String oclSource) implements FlowElementCS {}

    /** task <id> "<name>" [ocl {[ ... ]}] */
    record TaskCS(String id, String name, String oclSource) implements FlowElementCS {}

    /** call-activity <id> [ocl {[ ... ]}] */
    record CallActivityCS(String id, String oclSource) implements FlowElementCS {}

    /** gateway <id> : xor|and|or|event-based [ocl {[ ... ]}] */
    record GatewayCS(String id, String kind, String oclSource) implements FlowElementCS {}

    /** subprocess <id> "<name>" { ... } */
    record SubProcessCS(
            String                id,
            String                name,          // nullable
            List<FlowElementCS>   flowElements,
            List<SequenceFlowCS>  sequenceFlows,
            String                oclSource      // nullable
    ) implements FlowElementCS {}
}
