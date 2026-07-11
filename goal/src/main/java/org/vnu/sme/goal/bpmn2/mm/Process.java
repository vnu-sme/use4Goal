package org.vnu.sme.goal.bpmn2.mm;

import java.util.List;

/**
 * BPMN 2.0 Process (= "pool" in concrete syntax). Owns every FlowElement
 * declared inside it — both the ones inside a Lane and the ones directly
 * at process level. Lane only references a subset of them for partitioning.
 */
public final class Process {

    private final String              id;
    private final String              name;
    private final List<Lane>          lanes;
    private final List<FlowElement>   flowElements;
    private final List<SequenceFlow>  sequenceFlows;

    public Process(String id, String name,
                    List<Lane> lanes,
                    List<FlowElement> flowElements,
                    List<SequenceFlow> sequenceFlows) {
        this.id            = id;
        this.name          = name;
        this.lanes         = List.copyOf(lanes);
        this.flowElements  = List.copyOf(flowElements);
        this.sequenceFlows = List.copyOf(sequenceFlows);
    }

    public String             id()            { return id; }
    public String             name()          { return name; }
    public List<Lane>         lanes()         { return lanes; }
    public List<FlowElement>  flowElements()  { return flowElements; }
    public List<SequenceFlow> sequenceFlows() { return sequenceFlows; }
}
