package org.vnu.sme.goal.bpmn2.mm;

/** Gateway — single class + kind (no relation distinguishes the 4 kinds). */
public final class Gateway implements FlowElement {

    private final String      id;
    private final GatewayKind kind;

    public Gateway(String id, GatewayKind kind) {
        this.id   = id;
        this.kind = kind;
    }

    @Override public String      id()   { return id; }
    public               GatewayKind kind() { return kind; }
}
