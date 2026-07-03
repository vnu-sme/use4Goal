package org.vnu.sme.goal.bpmn2.mm;

import java.util.*;

/** Root model for a BPMN 2.0 Collaboration diagram (multi-pool). */
public final class Bpmn2Collaboration {

    private final String           name;
    private final List<Pool>       pools        = new ArrayList<>();
    private final List<MessageFlow> messageFlows = new ArrayList<>();

    private final Map<String, FlowNode> nodeIndex = new LinkedHashMap<>();

    public Bpmn2Collaboration(String name) { this.name = name; }

    public void addPool(Pool p) {
        pools.add(p);
        indexNodes(p.allNodes());
    }

    public void addMessageFlow(MessageFlow mf) { messageFlows.add(mf); }

    private void indexNodes(List<FlowNode> nodes) {
        for (FlowNode n : nodes) {
            nodeIndex.put(n.id(), n);
            if (n instanceof FlowNode.SubProcess sp) indexNodes(sp.elements());
        }
    }

    public String              getName()         { return name; }
    public List<Pool>          getPools()        { return Collections.unmodifiableList(pools); }
    public List<MessageFlow>   getMessageFlows() { return Collections.unmodifiableList(messageFlows); }

    public Optional<FlowNode> findNode(String id) {
        return Optional.ofNullable(nodeIndex.get(id));
    }

    public Map<String, FlowNode> allNodes() {
        return Collections.unmodifiableMap(nodeIndex);
    }
}
