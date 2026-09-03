package org.vnu.sme.goal.dsl.bpmn.mm;

public enum EventTrigger {
    NONE, MESSAGE, TIMER, ERROR, SIGNAL, TERMINATE, COMPENSATION, CONDITIONAL;

    public static EventTrigger from(String s) {
        if (s == null) return NONE;
        return switch (s.toLowerCase()) {
            case "message"      -> MESSAGE;
            case "timer"        -> TIMER;
            case "error"        -> ERROR;
            case "signal"       -> SIGNAL;
            case "terminate"    -> TERMINATE;
            case "compensation" -> COMPENSATION;
            case "conditional"  -> CONDITIONAL;
            default             -> NONE;
        };
    }
}
