package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/**
 * Sealed hierarchy for BPMN 2.0 flow nodes in concrete syntax.
 * Mirrors the grammar alternatives in Bpmn2.g4.
 */
public sealed interface FlowNodeCS {

    /** start <id> : <eventType> */
    record StartEventCS(String id, String eventType) implements FlowNodeCS {}

    /** end <id> : <eventType> */
    record EndEventCS(String id, String eventType) implements FlowNodeCS {}

    /** intermediate <id> : <eventType> catching|throwing */
    record IntermediateEventCS(String id, String eventType, boolean catching) implements FlowNodeCS {}

    /** task <id> "<label>" */
    record TaskCS(String id, String label) implements FlowNodeCS {}

    /** gateway <id> : xor|and|or|event-based */
    record GatewayCS(String id, String gwType) implements FlowNodeCS {}

    /** subprocess <id> "<label>" { ... } */
    record SubProcessCS(
            String              id,
            String              label,
            List<FlowNodeCS>    children,
            List<SequenceFlowCS> flows
    ) implements FlowNodeCS {}
}
