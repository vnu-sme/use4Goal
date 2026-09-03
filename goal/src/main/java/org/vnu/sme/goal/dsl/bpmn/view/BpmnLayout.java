package org.vnu.sme.goal.dsl.bpmn.view;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Layout result: pools (with lanes/nodes) + flow edges with computed
 * coordinates. Pure data, no Swing/AWT.
 */
public final class BpmnLayout {

    public final List<BpmnPool>        pools;
    public final Map<String, BpmnNode> nodes;   // flat index by id, for edge resolution
    public final List<BpmnEdge>        edges;
    public final int width, height;

    public BpmnLayout(List<BpmnPool> pools, Map<String, BpmnNode> nodes,
                        List<BpmnEdge> edges, int width, int height) {
        this.pools  = List.copyOf(pools);
        this.nodes  = Collections.unmodifiableMap(new LinkedHashMap<>(nodes));
        this.edges  = List.copyOf(edges);
        this.width  = width;
        this.height = height;
    }
}
