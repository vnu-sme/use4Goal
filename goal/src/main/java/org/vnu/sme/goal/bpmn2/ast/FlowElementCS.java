package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/**
 * Sealed hierarchy for BPMN 2.0 flow elements in concrete syntax.
 * Mirrors the {@code poolElement} alternatives in Bpmn2.g4 and the
 * Event/Activity/Gateway subclasses of the unified metamodel
 * (doc/05-bpmn2-metamodel.drawio).
 */
public sealed interface FlowElementCS {

    /** start <id> : <trigger> */
    record StartEventCS(String id, String trigger) implements FlowElementCS {}

    /** end <id> : <trigger> */
    record EndEventCS(String id, String trigger) implements FlowElementCS {}

    /** intermediate <id> : <trigger> : catching|throwing */
    record IntermediateEventCS(String id, String trigger, String direction) implements FlowElementCS {}

    /** task <id> "<name>" */
    record TaskCS(String id, String name) implements FlowElementCS {}

    /** call-activity <id> */
    record CallActivityCS(String id) implements FlowElementCS {}

    /** gateway <id> : xor|and|or|event-based */
    record GatewayCS(String id, String kind) implements FlowElementCS {}

    /** subprocess <id> "<name>" { ... } */
    record SubProcessCS(
            String                id,
            String                name,          // nullable
            List<FlowElementCS>   flowElements,
            List<SequenceFlowCS>  sequenceFlows
    ) implements FlowElementCS {}
}
