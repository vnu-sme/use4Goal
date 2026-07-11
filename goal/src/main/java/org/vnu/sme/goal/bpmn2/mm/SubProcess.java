package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/**
 * SubProcess — like Process, owns a nested set of FlowElement/SequenceFlow.
 * A FlowElement is owned by exactly one of {@code Process} or {@code SubProcess}
 * (xor, see doc/05-bpmn2-metamodel.drawio), never both.
 */
public final class SubProcess implements Activity {

    private final String              id;
    private final String              name;
    private final List<FlowElement>   flowElements;
    private final List<SequenceFlow>  sequenceFlows;

    public SubProcess(String id, String name,
                       List<FlowElement> flowElements,
                       List<SequenceFlow> sequenceFlows) {
        this.id            = id;
        this.name          = name;
        this.flowElements  = List.copyOf(flowElements);
        this.sequenceFlows = List.copyOf(sequenceFlows);
    }

    @Override public String             id()            { return id; }
    public               String             name()          { return name; }
    public               List<FlowElement>  flowElements()  { return flowElements; }
    public               List<SequenceFlow> sequenceFlows() { return sequenceFlows; }
}
