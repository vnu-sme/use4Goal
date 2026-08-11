package org.vnu.sme.goal.dsl.dcr.view;

import java.util.List;
import java.util.Map;

public final class DcrLayout {
    public final Map<String, DcrNode> nodes;
    public final List<DcrEdge> edges;
    public final int width;
    public final int height;

    public DcrLayout(Map<String, DcrNode> nodes, List<DcrEdge> edges, int width, int height) {
        this.nodes = Map.copyOf(nodes);
        this.edges = List.copyOf(edges);
        this.width = width;
        this.height = height;
    }
}
