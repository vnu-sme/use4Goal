package org.vnu.sme.goal.dsl.bpmn.ast;

import java.util.List;

/**
 * Sealed hierarchy for BPMN 2.0 flow elements in concrete syntax. Every
 * variant is a top-level model declaration (Event-B-style): it names its
 * own lane and its own outgoing flow(s) instead of being nested inside a
 * lane block and wired up through a separate `flow A -> B` list.
 */
public sealed interface FlowElementCS {

    String id();
    String laneId();

    /** start &lt;id&gt; { lane/trigger/pre?/flow } — {@code pre} is the process-entry predicate. */
    record StartEventCS(String id, String laneId, String trigger, String pre, String flow) implements FlowElementCS {}

    /** end &lt;id&gt; { lane/trigger } — a process boundary; never has an outgoing flow. */
    record EndEventCS(String id, String laneId, String trigger) implements FlowElementCS {}

    /** event &lt;id&gt; { lane/trigger/direction/flow } — the intermediate (catching/throwing) kind. */
    record IntermediateEventCS(String id, String laneId, String trigger, String direction, String flow)
            implements FlowElementCS {}

    /** activity &lt;id&gt; { name?/type/lane/pre?/post?/flow? }; kind is task|call-activity|subprocess. */
    record ActivityCS(String id, String name, String kind, String laneId,
                      String pre, String post, String flow) implements FlowElementCS {}

    /** gateway &lt;id&gt; { lane/type/pre?/flow+ } — no name, no post: routing never mutates state. */
    record GatewayCS(String id, String laneId, String kind, String pre, List<GatewayFlowCS> flows)
            implements FlowElementCS {}
}
