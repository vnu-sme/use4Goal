package org.vnu.sme.goal.dsl.bpmn.mm;

import java.util.List;

/**
 * BPMN 2.0 Process (= "pool" in concrete syntax). Owns every FlowElement
 * declared for it — both the ones inside a Lane and the ones directly
 * at process level. Lane only references a subset of them for partitioning.
 *
 * <p>{@code groupClass}, when present, names the ACL group class one
 * instance of this process is scoped to. A session runs one execution
 * engine per concrete instance of that class, each with {@code self}
 * bound to its own instance, so two instances (two groups) proceed as
 * two independent processes rather than being conflated through
 * {@code X.allInstances()} in every predicate.
 */
public final class Process {

    private final String              id;
    private final String              name;
    private final String              groupClass;
    private final List<Lane>          lanes;
    private final List<FlowElement>   flowElements;
    private final List<SequenceFlow>  sequenceFlows;

    public Process(String id, String name, String groupClass,
                    List<Lane> lanes,
                    List<FlowElement> flowElements,
                    List<SequenceFlow> sequenceFlows) {
        this.id            = id;
        this.name          = name;
        this.groupClass    = groupClass;
        this.lanes         = List.copyOf(lanes);
        this.flowElements  = List.copyOf(flowElements);
        this.sequenceFlows = List.copyOf(sequenceFlows);
    }

    public String             id()            { return id; }
    public String             name()          { return name; }
    public String             groupClass()    { return groupClass; }
    public List<Lane>         lanes()         { return lanes; }
    public List<FlowElement>  flowElements()  { return flowElements; }
    public List<SequenceFlow> sequenceFlows() { return sequenceFlows; }
}
