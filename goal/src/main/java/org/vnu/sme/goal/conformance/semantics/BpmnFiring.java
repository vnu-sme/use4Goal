package org.vnu.sme.goal.conformance.semantics;

import java.util.Set;

import org.vnu.sme.goal.bpmn2.mm.FlowElement;

/**
 * One possible firing of a FlowElement from a given marking: which arc(s) it consumes and which
 * arc(s) it produces. A Gateway(XOR/EVENT_BASED/OR) split enumerates one Firing per outgoing
 * branch — this is exactly where BPMN branching becomes LTS non-determinism (§3.3.2).
 */
public record BpmnFiring(FlowElement node, Set<String> consumed, Set<String> produced) {}
