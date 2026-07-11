package org.vnu.sme.goal.bpmn2.mm;

public enum EventDirection {
    CATCHING, THROWING;

    public static EventDirection from(String s) {
        return "throwing".equalsIgnoreCase(s) ? THROWING : CATCHING;
    }
}
