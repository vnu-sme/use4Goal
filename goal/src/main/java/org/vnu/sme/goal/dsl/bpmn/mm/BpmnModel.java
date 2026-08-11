package org.vnu.sme.goal.dsl.bpmn.mm;

import java.util.*;

/**
 * Root of the unified BPMN 2.0 metamodel (doc/05-bpmn2-metamodel.drawio):
 * Model owns Process/Message/MessageFlow. There is no separate
 * Collaboration/Choreography class — a Collaboration view is just this
 * same Model's processes + messageFlows rendered together.
 */
public final class BpmnModel {

    private final String name;

    private final List<Process>     processes    = new ArrayList<>();
    private final List<Message>     messages     = new ArrayList<>();
    private final List<MessageFlow> messageFlows = new ArrayList<>();

    private final Map<String, Process>     processIndex      = new LinkedHashMap<>();
    private final Map<String, Message>     messageIndex      = new LinkedHashMap<>();
    private final Map<String, FlowElement> flowElementIndex  = new LinkedHashMap<>();
    private final Map<String, String>      flowElementOwner  = new LinkedHashMap<>(); // flowElementId -> owning Process id

    public BpmnModel(String name) { this.name = name; }

    public void addProcess(Process p) {
        processes.add(p);
        processIndex.put(p.id(), p);
        indexFlowElements(p.flowElements(), p.id());
    }

    public void addMessage(Message m) {
        messages.add(m);
        messageIndex.put(m.id(), m);
    }

    public void addMessageFlow(MessageFlow mf) { messageFlows.add(mf); }

    private void indexFlowElements(List<FlowElement> elements, String ownerProcessId) {
        for (FlowElement fe : elements) {
            flowElementIndex.put(fe.id(), fe);
            flowElementOwner.put(fe.id(), ownerProcessId);
            if (fe instanceof SubProcess sp) indexFlowElements(sp.flowElements(), ownerProcessId);
        }
    }

    public String             name()         { return name; }
    public List<Process>      processes()    { return Collections.unmodifiableList(processes); }
    public List<Message>      messages()     { return Collections.unmodifiableList(messages); }
    public List<MessageFlow>  messageFlows() { return Collections.unmodifiableList(messageFlows); }
    public int                flowElementCount() { return flowElementIndex.size(); }

    public Optional<Process>     findProcess(String id)     { return Optional.ofNullable(processIndex.get(id)); }
    public Optional<Message>     findMessage(String id)     { return Optional.ofNullable(messageIndex.get(id)); }
    public Optional<FlowElement> findFlowElement(String id) { return Optional.ofNullable(flowElementIndex.get(id)); }

    /** Id of the top-level Process that owns this FlowElement (even if nested inside a SubProcess). */
    public Optional<String> ownerProcessId(String flowElementId) {
        return Optional.ofNullable(flowElementOwner.get(flowElementId));
    }
}
