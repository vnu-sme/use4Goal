package org.vnu.sme.goal.maxbpmn.mm;

import java.util.*;

public final class BpmnProcess {

    private final String              name;
    private final List<Lane>          lanes    = new ArrayList<>();
    private final List<BpmnNode>      nodes    = new ArrayList<>();
    private final List<SequenceFlow>  flows    = new ArrayList<>();
    private final List<MessageFlow>   messages = new ArrayList<>();

    public BpmnProcess(String name) { this.name = name; }

    public String            getName()    { return name; }
    public List<Lane>        getLanes()   { return Collections.unmodifiableList(lanes); }
    public List<BpmnNode>    getNodes()   { return Collections.unmodifiableList(nodes); }
    public List<SequenceFlow> getFlows()  { return Collections.unmodifiableList(flows); }
    public List<MessageFlow>  getMessages(){ return Collections.unmodifiableList(messages); }

    public void addLane(Lane l)         { lanes.add(l); }
    public void addNode(BpmnNode n)     { nodes.add(n); }
    public void addFlow(SequenceFlow f) { flows.add(f); }
    public void addMessage(MessageFlow m){ messages.add(m); }

    public Optional<BpmnNode> findNode(String id) {
        return nodes.stream().filter(n -> n.id().equals(id)).findFirst();
    }

    public Optional<Lane> findLane(String id) {
        return lanes.stream().filter(l -> l.id().equals(id)).findFirst();
    }
}
