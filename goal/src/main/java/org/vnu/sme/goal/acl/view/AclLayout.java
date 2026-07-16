package org.vnu.sme.goal.acl.view;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AclLayout {
    public final Map<String, AclNode> nodes;
    public final List<AclEdge> edges;
    public final int width;
    public final int height;

    public AclLayout(Map<String, AclNode> nodes, List<AclEdge> edges, int width, int height) {
        this.nodes = Collections.unmodifiableMap(new LinkedHashMap<>(nodes));
        this.edges = List.copyOf(edges);
        this.width = width;
        this.height = height;
    }
}
