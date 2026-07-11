package org.vnu.sme.goal.bpmn2.mm;

public enum GatewayKind {
    XOR, AND, OR, EVENT_BASED;

    public static GatewayKind from(String s) {
        return switch (s.toLowerCase()) {
            case "and", "parallel" -> AND;
            case "or", "inclusive" -> OR;
            case "event-based"     -> EVENT_BASED;
            default                 -> XOR;
        };
    }

    public String symbol() {
        return switch (this) {
            case XOR         -> "X";
            case AND         -> "+";
            case OR          -> "O";
            case EVENT_BASED -> "⬠";
        };
    }
}
