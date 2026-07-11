package org.vnu.sme.goal.bpmn2.view;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Layout result: pools (with lanes/nodes) + flow edges with computed
 * coordinates. Pure data, no Swing/AWT.
 */
public final class Bpmn2Layout {

    public final List<Bpmn2Pool>        pools;
    public final Map<String, Bpmn2Node> nodes;   // flat index by id, for edge resolution
    public final List<Bpmn2Edge>        edges;
    public final int width, height;

    public Bpmn2Layout(List<Bpmn2Pool> pools, Map<String, Bpmn2Node> nodes,
                        List<Bpmn2Edge> edges, int width, int height) {
        this.pools  = List.copyOf(pools);
        this.nodes  = Collections.unmodifiableMap(new LinkedHashMap<>(nodes));
        this.edges  = List.copyOf(edges);
        this.width  = width;
        this.height = height;
    }
}
