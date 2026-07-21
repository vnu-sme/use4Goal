package org.vnu.sme.goal.bpmn2.ast;

import java.util.List;

/**
 * Sealed hierarchy for BPMN 2.0 flow elements in concrete syntax.
 * Mirrors the {@code poolElement} alternatives in Bpmn2.g4 and the
 * Event/Activity/Gateway subclasses of the unified metamodel
 * (doc/05-bpmn2-metamodel.drawio).
 */
public sealed interface FlowElementCS {

    /** start &lt;id&gt; { name/trigger/pre/post properties } */
    record StartEventCS(String id, String name, String trigger,
                        List<ActivityConstraintCS> constraints) implements FlowElementCS {}

    /** end &lt;id&gt; { name/trigger/pre/post properties } */
    record EndEventCS(String id, String name, String trigger,
                      List<ActivityConstraintCS> constraints) implements FlowElementCS {}

    /** intermediate &lt;id&gt; { name/trigger/direction/pre/post properties } */
    record IntermediateEventCS(String id, String name, String trigger, String direction,
                               List<ActivityConstraintCS> constraints) implements FlowElementCS {}

    /** task &lt;id&gt; { optional name/pre/post properties } */
    record TaskCS(String id, String name, List<ActivityConstraintCS> constraints,
                  String effectSource) implements FlowElementCS {}

    /** call-activity &lt;id&gt; { optional name/pre/post properties } */
    record CallActivityCS(String id, String name,
                          List<ActivityConstraintCS> constraints, String effectSource) implements FlowElementCS {}

    /** gateway &lt;id&gt; { optional name, required type, optional pre/post } */
    record GatewayCS(String id, String name, String kind,
                     List<ActivityConstraintCS> constraints) implements FlowElementCS {}

    /** subprocess &lt;id&gt; { name/pre/post and nested process content } */
    record SubProcessCS(
            String                id,
            String                name,          // nullable
            List<ActivityConstraintCS> constraints,
            String                effectSource,
            List<FlowElementCS>   flowElements,
            List<SequenceFlowCS>  sequenceFlows
    ) implements FlowElementCS {}
}
