package org.vnu.sme.goal.dsl.bpmn.view;

import java.util.ArrayList;
import java.util.List;

import org.vnu.sme.goal.dsl.bpmn.mm.EventTrigger;
import org.vnu.sme.goal.dsl.bpmn.mm.GatewayKind;

/**
 * Adapter: wraps one BPMN 2.0 FlowElement as a placeable node. Position is
 * mutable — the View (drag) and the LayoutBuilder are the only writers.
 */
public final class BpmnNode {

    public String              id;
    public String              label;
    public final BpmnNodeKind kind;
    public final EventTrigger  trigger;   // meaningful for *_EVT kinds
    public final boolean       catching;  // meaningful for INT_EVT
    public final GatewayKind   gwKind;    // meaningful for GATEWAY
    public BpmnDiagramNode.ScenarioState scenarioState = BpmnDiagramNode.ScenarioState.NONE;
    public final List<String> scenarioDetails = new ArrayList<>();

    public int x, y, w, h;

    public BpmnNode(String id, String label, BpmnNodeKind kind,
                      EventTrigger trigger, boolean catching, GatewayKind gwKind) {
        this.id = id;
        this.label = label;
        this.kind = kind;
        this.trigger = trigger;
        this.catching = catching;
        this.gwKind = gwKind;
    }
}
