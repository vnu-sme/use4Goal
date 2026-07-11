package org.vnu.sme.goal.bpmn2.mm;

/** Message flow — reference to source/target FlowElement and an optional Message. */
public final class MessageFlow {

    private final FlowElement source;
    private final FlowElement target;
    private final Message     message;   // nullable

    public MessageFlow(FlowElement source, FlowElement target, Message message) {
        this.source  = source;
        this.target  = target;
        this.message = message;
    }

    public FlowElement source()  { return source; }
    public FlowElement target()  { return target; }
    public Message     message() { return message; }
}
