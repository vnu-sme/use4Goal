package org.vnu.sme.goal.bpmn2.mm;

/** Gateway — single class + kind (no relation distinguishes the 4 kinds). */
public final class Gateway implements FlowElement {

    private final String      id;
    private final GatewayKind kind;
    private final String      oclSource;

    public Gateway(String id, GatewayKind kind) {
        this(id, kind, null);
    }

    public Gateway(String id, GatewayKind kind, String oclSource) {
        this.id   = id;
        this.kind = kind;
        this.oclSource = oclSource;
    }

    @Override public String      id()   { return id; }
    public               GatewayKind kind() { return kind; }
    @Override public String      oclSource() { return oclSource; }
}
